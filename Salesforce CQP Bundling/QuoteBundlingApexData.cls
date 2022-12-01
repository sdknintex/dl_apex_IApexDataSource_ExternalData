global class QuoteBundlingApexData implements Loop.IApexDataSource {
    string parentObject = 'Bundle';
    string childObject = 'BundleLineItem';
    Map<string, Schema.SObjectField> quoteFields = Schema.sObjectType.SBQQ__QuoteLine__c.fields.getMap();
    
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
                //Add all the fields from the Quote Line object for the bundle and non-bundle products.
               for (string fieldName : quoteFields.keySet()) {

                    Schema.DescribeFieldResult info = quoteFields.get(fieldName).getDescribe();

                    Loop.ExternalData.FieldInfo outputInfo = new Loop.ExternalData.FieldInfo(info.getName(), info.getType());
                    outputInfo.referenceTo = info.getReferenceTo().size() > 0 ? string.valueOf(info.getReferenceTo()[0]) : null;
                    outputInfo.scale = info.getScale();
                    outputInfo.label = info.getLabel();
                    
                    fields.add(outputInfo);
                }
            } else if (objectName == childObject) {
                //Add all the fields from the Quote Line object for the bundle line items.
                for (string fieldName : quoteFields.keySet()) {

                    Schema.DescribeFieldResult info = quoteFields.get(fieldName).getDescribe();

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
        //Retrieve current Quote Id.
        Id recordId = requestInfo.RecordId;
        
        Loop.ExternalData.QueryResultSet queryResultSet = new Loop.ExternalData.QueryResultSet();

        List<string> lineItemFieldList = new List<string>(quoteFields.keySet());

        //Query for all the fields for every Quote Line both bundled and non-bundled.
        String queryString = 'SELECT ' + string.join(lineItemFieldList,',') + ' FROM SBQQ__QuoteLine__c WHERE SBQQ__Quote__c = \'' + recordId + '\'';
        List<SBQQ__QuoteLine__c> lineItems = Database.query(queryString);
        
        for (string objectName : requestInfo.GetObjectNames()) {
            List<string> fields = new List<string>();
            Loop.ExternalData.QueryResult queryResult = new Loop.ExternalData.QueryResult(objectName, fields);


            //Map for bundles and non-bundle items.
            Map<Id, SBQQ__QuoteLine__c> bundleMap = new Map<Id, SBQQ__QuoteLine__c>();
            //Map for bundle line items. Key is the Bundle Id.
            Map<Id, List<SBQQ__QuoteLine__c>> bundleLineMap = new Map<Id, List<SBQQ__QuoteLine__c>>();
            
            //Sort the line items into bundles/non-bundles and bundle items.
            for (SBQQ__QuoteLine__c lineItem : lineItems) {
                if (lineItem.SBQQ__RequiredBy__c == null) {
                    bundleMap.put(lineItem.Id, lineItem);
                } else {
                    if (bundleLineMap.get(lineItem.SBQQ__RequiredBy__c) == null) {
                        bundleLineMap.put(lineItem.SBQQ__RequiredBy__c, new List<SBQQ__QuoteLine__c>());
                    }
                    bundleLineMap.get(lineItem.SBQQ__RequiredBy__c).add(lineItem);
                }
            }
            
            if (objectName == parentObject) {
                //Create columns by adding list of field names. (Same fields as listed in describeObjects() method)
                fields = new List<string>(lineItemFieldList);

                //Add object and list of field names.
                queryResult = new Loop.ExternalData.QueryResult(objectName, fields);

                //Set rows of values for each bundle and non-bundle item.
                for (Id bundleId : bundleMap.keySet()) {
                    SBQQ__QuoteLine__c lineItem = bundleMap.get(bundleId);
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
                    for (SBQQ__QuoteLine__c lineItem : bundleLineMap.get(bundleId)) {
                        List<string> recordValues = new List<string>();
                        for (string field : lineItemFieldList) {
                            recordValues.add(string.valueOf(lineItem.get(field)));
                        }
                        //Add parent Bundle Id to ParentId field.
                        recordValues.add(bundleId);
                        queryResult.rows.add(recordValues);
                    }
                }
            }
            queryResultSet.add(queryResult);
        }
        return queryResultSet;
    }
}