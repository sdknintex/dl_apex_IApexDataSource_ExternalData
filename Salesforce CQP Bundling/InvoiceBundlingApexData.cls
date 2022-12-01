global class InvoiceBundlingApexData implements Loop.IApexDataSource {
    string parentObject = 'Bundle';
    string childObject = 'BundleLineItem';
    Map<string, Schema.SObjectField> invoiceFields = Schema.sObjectType.blng__InvoiceLine__c.fields.getMap();

    public Set<string> getGlobalDescribe() {
        //Create the objects
        return new Set<string>{
            parentObject, childObject
        };
    }

    public Set<string> getChildRelationships(string objectName) {
        //Establish the parent/child structure between the two objects.
        Set<string> childObjectNames = new Set<string>();
        if (objectName == parentObject) {
            childObjectNames.add(childObject);
        }
        return childObjectNames;
    }

    public List<Loop.ExternalData.DataObject> describeObjects(List<string> objectNames) {
        List<Loop.ExternalData.DataObject> describeObjectResults = new List<Loop.ExternalData.DataObject>();
        
        for (string objectName : objectNames) {
            List<Loop.ExternalData.FieldInfo> fields = new List<Loop.ExternalData.FieldInfo>();
            if (objectName == parentObject) {
                //Add all the fields from the Invoice Line object for the bundle and non-bundle items.
                for (string fieldName : invoiceFields.keySet()) {

                    Schema.DescribeFieldResult info = invoiceFields.get(fieldName).getDescribe();

                    Loop.ExternalData.FieldInfo outputInfo = new Loop.ExternalData.FieldInfo(info.getName(), info.getType());
                    outputInfo.referenceTo = info.getReferenceTo().size() > 0 ? string.valueOf(info.getReferenceTo()[0]) : null;
                    outputInfo.scale = info.getScale();
                    outputInfo.label = info.getLabel();
                    
                    fields.add(outputInfo);
                }
            } else if (objectName == childObject) {
                //Add all the fields from the Invoice Line object for the bundle line items.
                for (string fieldName : invoiceFields.keySet()) {

                    Schema.DescribeFieldResult info = invoiceFields.get(fieldName).getDescribe();

                    Loop.ExternalData.FieldInfo outputInfo = new Loop.ExternalData.FieldInfo(info.getName(), info.getType());
                    outputInfo.referenceTo = info.getReferenceTo().size() > 0 ? string.valueOf(info.getReferenceTo()[0]) : null;
                    outputInfo.scale = info.getScale();
                    outputInfo.label = info.getLabel();
                    
                    fields.add(outputInfo);
                }
                //This creates the parent/child relationship between the ParentObject and the ChildObject. The parent MUST contain a field called Id.
                Loop.ExternalData.FieldInfo fieldInfo = new Loop.ExternalData.FieldInfo('ParentId', Schema.DisplayType.REFERENCE);
                fieldInfo.referenceTo = parentObject;
                fields.add(fieldInfo);
            }
            Loop.ExternalData.DataObject describeObjectResult = new Loop.ExternalData.DataObject(objectName, fields);
            describeObjectResults.add(describeObjectResult);
        }
        return describeObjectResults;
    }

    public Loop.ExternalData.QueryResultSet query(Loop.ExternalData.QueryRequestInfo requestInfo) {
        //Retrieve current Invoice Id.
        Id recordId = requestInfo.RecordId;
        
        Loop.ExternalData.QueryResultSet queryResultSet = new Loop.ExternalData.QueryResultSet();
        List<string> lineItemFieldList = new List<string>(invoiceFields.keySet());

        //Query for all the fields for every Invoice Line both bundled and non-bundled. RequiredBy__c field on OrderProduct__c object contains Id of parent bundle.
        String queryString = 'SELECT ' + string.join(lineItemFieldList,',') + ',blng__OrderProduct__r.SBQQ__RequiredBy__c FROM blng__InvoiceLine__c WHERE blng__Invoice__c = \'' + recordId + '\'';
        List<blng__InvoiceLine__c> lineItems = Database.query(queryString);
        
        //Map for bundles and non-bundle items.
        Map<Id, blng__InvoiceLine__c> bundleMap = new Map<Id, blng__InvoiceLine__c>();
        //Map for bundle line items. Key is the Order Product Id.
        Map<Id, List<blng__InvoiceLine__c>> bundleLineMap = new Map<Id, List<blng__InvoiceLine__c>>();
        //Map for matching Order Product Id to Invoice Line Id for bundles.
        Map<Id, Id> bundleIdMap = new Map<Id, Id>();
        
        //Sort Invoice Line Items by bundle/non-bundle and bundle line items.
        for (blng__InvoiceLine__c lineItem : lineItems) {
            if (lineItem.blng__OrderProduct__r.SBQQ__RequiredBy__c == null) {
                bundleMap.put(lineItem.Id, lineItem);
                bundleIdMap.put(lineItem.blng__OrderProduct__c, lineItem.Id);
            } else {
                if (bundleLineMap.get(lineItem.blng__OrderProduct__r.SBQQ__RequiredBy__c) == null) {
                    bundleLineMap.put(lineItem.blng__OrderProduct__r.SBQQ__RequiredBy__c, new List<blng__InvoiceLine__c>());
                }
                bundleLineMap.get(lineItem.blng__OrderProduct__r.SBQQ__RequiredBy__c).add(lineItem);
            }
        }
        
        for (string objectName : requestInfo.GetObjectNames()) {
            List<string> fields = new List<string>();
            Loop.ExternalData.QueryResult queryResult = new Loop.ExternalData.QueryResult(objectName, fields);
            
            if (objectName == parentObject) {       
                //Create columns by adding list of field names. (Same fields as listed in describeObjects() method)       
                fields = new List<string>(lineItemFieldList);

                //Add object and list of field names.
                queryResult = new Loop.ExternalData.QueryResult(objectName, fields);

                //Set rows of values for each bundle and non-bundle item.
                for (Id bundleId : bundleMap.keySet()) {
                    blng__InvoiceLine__c lineItem = bundleMap.get(bundleId);
                    List<string> recordValues = new List<string>();
                    for (string field : lineItemFieldList) {
                        recordValues.add(string.valueOf(lineItem.get(field)));
                    }
                    queryResult.rows.add(recordValues);
                }
            } else if (objectName == childObject) {
                //Create columns by adding list of field names. (Same fields as listed in describeObjects() method)
                fields = new List<string>(lineItemFieldList);
                //Additionally add ParentId field to establish relationship between child and parent objects.
                fields.add('ParentId');

                //Add object and list of field names.
                queryResult = new Loop.ExternalData.QueryResult(objectName, fields);

                //Set rows of values for each bundle line item.
                for (Id bundleId : bundleLineMap.keySet()) {
                    for (blng__InvoiceLine__c lineItem : bundleLineMap.get(bundleId)) {
                        List<string> recordValues = new List<string>();
                        for (string field : lineItemFieldList) {
                            recordValues.add(string.valueOf(lineItem.get(field)));
                        }
                        //Add parent Bundle Id to ParentId field.
                        recordValues.add(bundleIdMap.get(bundleId));
                        queryResult.rows.add(recordValues);
                    }
                }
            }
            queryResultSet.add(queryResult);
        }
        return queryResultSet;
    }
}