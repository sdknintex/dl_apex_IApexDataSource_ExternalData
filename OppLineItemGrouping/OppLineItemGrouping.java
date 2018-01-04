global class OppLineItemGrouping implements Loop.IApexDataSource {
    public Set<string> getGlobalDescribe() {
        return new Set<String>{
            'OppLineItemGroup', 'OppLineItem'
        };
    }
    
    public List<Loop.ExternalData.DataObject> describeObjects(List<string> objNames) {
        List<Loop.ExternalData.DataObject> output = new List<Loop.ExternalData.DataObject>();
        for (string objName : objNames) {
            List<Loop.ExternalData.FieldInfo> outputFields = new List<Loop.ExternalData.FieldInfo>();
            if (objName == 'OppLineItemGroup') {
                outputFields.add(new Loop.ExternalData.FieldInfo('Id', Schema.DisplayType.ID));
                outputFields.add(new Loop.ExternalData.FieldInfo('GroupName', Schema.DisplayType.STRING));
            }
            else if (objName == 'OppLineItem') {
                Map<string, Schema.SObjectField> fields = Schema.sObjectType.OpportunityLineItem.fields.getMap();
                for (string key : fields.keySet()) {
                    Schema.DescribeFieldResult info = fields.get(key).getDescribe();
                    Loop.ExternalData.FieldInfo outputInfo = new Loop.ExternalData.FieldInfo(info.getName(), info.getType());
                    outputInfo.referenceTo = info.getReferenceTo().size() > 0 ? string.valueOf(info.getReferenceTo()[0]) : null;
                    outputInfo.scale = info.getScale();
                    outputInfo.label = info.getLabel();
                    outputFields.add(outputInfo);
                }
                Loop.ExternalData.FieldInfo outputInfo = new Loop.ExternalData.FieldInfo('Group', Schema.DisplayType.REFERENCE);
                outputInfo.referenceTo = 'OppLineItemGroup';
                outputFields.add(outputInfo);
            }
            output.add(new Loop.ExternalData.DataObject(objName, outputFields));
        }
        return output;
    }
    
    public Set<string> getChildRelationships(string objectName) {
        Set<string> childObjectNames = new Set<string>();
        if (objectName == 'OppLineItemGroup')
            childObjectNames.add('OppLineItem');
        return childObjectNames;
    }
    
    public Loop.ExternalData.QueryResultSet query(Loop.ExternalData.QueryRequestInfo requestInfo) {
        Loop.ExternalData.QueryResultSet results = new Loop.ExternalData.QueryResultSet();
        
        Loop.ExternalData.QueryResult lineItemGroups = new Loop.ExternalData.QueryResult('OppLineItemGroup', new List<string> { 'Id', 'GroupName' });
        Loop.ExternalData.QueryResult lineItems = new Loop.ExternalData.QueryResult('OppLineItem', new List<string>());
        
        if (string.valueOf(requestInfo.RecordId).startswith('006')) {
            Set<string> lineItemFieldsSet = Schema.sObjectType.OpportunityLineItem.fields.getMap().keySet();
            List<string> lineItemFields = new List<string>(lineItemFieldsSet);
            if (lineItemFieldsSet.contains('pricebookentryid')) {
                for (string productField : Schema.sObjectType.Product2.fields.getMap().keySet())
                    lineItemFields.add('pricebookentry.product2.' + productField);
            }
            
            Map<string, List<OpportunityLineItem>> groupedLineItems = new Map<string, List<OpportunityLineItem>>();
            for (OpportunityLineItem oli : (List<OpportunityLineItem>)queryCreator(
                    lineItemFields, 'OpportunityLineItem', 'OpportunityId', requestInfo.RecordId, '=', 'SortOrder, CreatedDate DESC'
                )
            ) {
                string family = (string)((Product2)oli.getSObject('PricebookEntry').getSObject('Product2')).get('Family');
                if (string.isBlank(family))
                    family = 'nullx';
                if (!groupedLineItems.containsKey(family))
                    groupedLineItems.put(family, new List<OpportunityLineItem>());
                groupedLineItems.get(family).add(oli);
            }
            
            List<string> qrLineItemFields = new List<string>(lineItemFields);
            qrLineItemFields.add('Group');
            
            lineItems = new Loop.ExternalData.QueryResult('OppLineItem', qrLineItemFields);
            
            for (string productFamily : groupedLineItems.keySet()) {
                system.debug(productFamily);
                lineItemGroups.rows.add(new List<string>{ productFamily, productFamily });
                for (OpportunityLineItem oli : groupedLineItems.get(productFamily)) {
                    List<string> oliFieldValues = new List<string>();
                    for (string field : lineItemFields) {
                        oliFieldValues.add(getFieldValue(oli, field));
                    }
                    oliFieldValues.add(productFamily);
                    lineItems.rows.add(oliFieldValues);
                }
            }
        }
        
        for (string objectName : requestInfo.GetObjectNames()) {
            if (objectName == 'OppLineItemGroup')
                results.add(lineItemGroups);
            else if (objectName == 'OppLineItem')
                results.add(lineItems);
        }
        
        return results;
    }
    
    private string getFieldValue(sObject obj, string field) {
        if (obj == null) return '';
        string[] fieldParts = field.split('\\.');
        if (fieldParts.size() == 3) {
            sObject sobj = obj.getSObject(fieldParts[0]);
            if (sobj == null) return '';
            sobj = sobj.getSObject(fieldParts[1]);
            if (sobj == null) return '';
            return string.valueOf(sobj.get(fieldParts[2]));
        }
        else if (fieldParts.size() == 2) {
            sObject sobj = obj.getSObject(fieldParts[0]);
            if (sobj == null) return '';
            return string.valueOf(sobj.get(fieldParts[1]));
        }
        return string.valueOf(obj.get(field));
    }
    
    private List<sObject> queryCreator(List<string> fields, string objectName, string idName, string recordId, string inOrEquals, string orderBy) {
        string soql = string.format(
            'SELECT {0} FROM {1} WHERE {2} {3} {4} {5}',
            new List<string>{
                string.join(fields, ','),
                objectName,
                idName,
                inOrEquals,
                '\''+recordId+'\'',
                string.isBlank(orderBy) ? '' : 'ORDER BY '+orderBy
            }
        );
        return Database.query(soql);
    }
}