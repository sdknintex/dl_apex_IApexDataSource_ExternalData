// Sample Apex class leveraging Nintex Drawloop feature Apex Data
// Class must be global for Nintex Drawloop to access it
// Class must implement the Loop.IApexDataSource Apex interface to use it in a Document Package Relationship
global class SampleDrawloopApexData implements Loop.IApexDataSource {
    
    // Loop.IApexDataSource interface requires four public methods with the following signatures:
    // Set<string> getGlobalDescribe()
    // Set<string> getChildRelationships(string objectName)
    // List<ExternalData.DataObject> describeObjects(List<string> objectNames)
    // Loop.ExternalData.QueryResultSet query(Loop.ExternalData.QueryRequestInfo requestInfo)
    
    // optional, but good practice: use variables to store object names to avoid mistakes from typos
    private string opportunityObjectName = 'ApexOpportunity',
        accountObjectName = 'ApexAccount',
        oppLineItemObjectName = 'ApexOppLineItem',
        sampleSoqlObjectName = 'ApexSoql';
    
    // query used for the 'ApexSoql' object
    private string sampleSoqlQuery = 'SELECT Name, Title FROM Contact WHERE Title LIKE \'C%\' AND AccountId = :accountId';
    
    public Set<string> getGlobalDescribe() {
        // Return a set of object names that this Apex class will provide data for.
        // In this example, we will provide data for a single Opportunity, its Line Items, its Account, and an arbitrary SOQL query.
        return new Set<string>{
            opportunityObjectName, accountObjectName, oppLineItemObjectName, sampleSoqlObjectName
        };
    }
    
    public Set<string> getChildRelationships(string objectName) {
        // Return a set of object names corresponding to the children for the objectName specified
        // In this example, only the Opp Line Item object is a child object.
        if (objectName == opportunityObjectName) {
            return new Set<string> { oppLineItemObjectName };
        }
        return new Set<string>();
    }
    
    public List<Loop.ExternalData.DataObject> describeObjects(List<string> objectNames) {
        // Describe each object in objectNames.
        // In this example, we will use a field set for each Salesforce object being used.
        
        // Declare the variable to be returned.
        List<Loop.ExternalData.DataObject> describeObjectResults = new List<Loop.ExternalData.DataObject>();
        
        // Loop through each object in objectNames and add to the result
        for (string objectName : objectNames) {
            // Declare variable to store field data for the object
            List<Loop.ExternalData.FieldInfo> fields = new List<Loop.ExternalData.FieldInfo>();
            
            if (objectName == sampleSoqlObjectName) {
                // Use hard coded list of fields based on SOQL desired
                fields.add(new Loop.ExternalData.FieldInfo('Name', Schema.DisplayType.STRING));
                fields.add(new Loop.ExternalData.FieldInfo('Title', Schema.DisplayType.STRING));
            }
            else if (fieldSetsByObjectName.containsKey(objectName) && fieldSetsByObjectName.get(objectName) != null) {
                // Keep track of unique field paths in field set so that we can add the opp-opp line item relationship field if necessary.
                Set<string> fieldPaths = new Set<string>();
                
                // Loop through fields in the appropriate field set, store the info, and add to fields
                Schema.FieldSet fieldSet = fieldSetsByObjectName.get(objectName);
                List<Schema.FieldSetMember> fieldSetMembers = fieldSet.getFields();
                for (Schema.FieldSetMember fsm : fieldSetMembers) {
                    fieldPaths.add(fsm.getFieldPath());
                    
                    // Store the field info for this field in the field set
                    Loop.ExternalData.FieldInfo fieldInfo = new Loop.ExternalData.FieldInfo(fsm.getFieldPath(), fsm.getType());
                    fieldInfo.label = fsm.getLabel();
                    
                    // Pull the field's scale and referenceTo from the Salesforce field describe info
                    Schema.SObjectField sobjectField = fieldMapsByObjectName.get(objectName).get(fsm.getFieldPath());
                    if (sobjectField != null) {
                        Schema.DescribeFieldResult sobjectFieldDescribe = sobjectField.getDescribe();
                        fieldInfo.scale = sobjectFieldDescribe.getScale();
                        if (!sobjectFieldDescribe.getReferenceTo().isEmpty()) {
                            // referenceTo is needed to define a relationship between parent and child objects
                            if (objectName == oppLineItemObjectName && sobjectFieldDescribe.getReferenceTo()[0] == Schema.Opportunity.SObjectType) {
                                // Connect the Apex Opp object to the Apex Opp Line Item object
                                fieldInfo.referenceTo = opportunityObjectName;
                            }
                            else {
                                fieldInfo.referenceTo = string.valueOf(sobjectFieldDescribe.getReferenceTo()[0]);
                            }
                        }
                    }
                    
                    fields.add(fieldInfo);
                }
                
                // Add the Id field if missing
                if (fieldPaths.add('Id')) {
                    fields.add(new Loop.ExternalData.FieldInfo('Id', Schema.DisplayType.ID));
                }
                
                // Add the opp-opp line item relationship field if necessary
                if (objectName == oppLineItemObjectName && fieldPaths.add('OpportunityId')) {
                    Loop.ExternalData.FieldInfo fieldInfo = new Loop.ExternalData.FieldInfo('OpportunityId', Schema.DisplayType.REFERENCE);
                    fieldInfo.label = Schema.SObjectType.OpportunityLineItem.fields.OpportunityId.getLabel();
                    fieldInfo.referenceTo = opportunityObjectName;
                    fields.add(fieldInfo);
                }
            }
            
            // Declare variable to add to results list
            Loop.ExternalData.DataObject describeObjectResult = new Loop.ExternalData.DataObject(objectName, fields);
            
            // Add to results list
            describeObjectResults.add(describeObjectResult);
        }
        
        return describeObjectResults;
    }
    
    public Loop.ExternalData.QueryResultSet query(Loop.ExternalData.QueryRequestInfo requestInfo) {
        // Provide data for each object in requestInfo.GetObjectNames()
        
        // Get top level record Ids for queries below
        Id accountId;
        Id opportunityId;
        
        Set<Id> topLevelIds = requestInfo.TopLevelIds;
        topLevelIds.add(requestInfo.RecordId); // not sure if requestInfo.TopLevelIds already contains requestInfo.RecordId, but it's a Set, so this doesn't hurt
        
        for (Id recordId : topLevelIds) {
            if (recordId != null) {
                if (recordId.getSObjectType() == Schema.Opportunity.SObjectType) {
                    opportunityId = recordId;
                } else if (recordId.getSObjectType() == Schema.Account.SObjectType) {
                    accountId = recordId;
                }
            }
        }
        
        // Declare the variable to be returned.
        Loop.ExternalData.QueryResultSet queryResultSet = new Loop.ExternalData.QueryResultSet();
        
        for (string objectName : requestInfo.GetObjectNames()) {
            string query;
            List<string> fields = new List<string>();
            Set<string> fieldsSet = new Set<string>();
            
            // set up fields list and query to get data for QueryResult instance
            if (objectName == sampleSoqlObjectName) {
                fields = new List<string>{ 'Name', 'Title' };
                query = sampleSoqlQuery;
            }
            else if (fieldSetsByObjectName.containsKey(objectName) && fieldSetsByObjectName.get(objectName) != null) {
                // Get list of fields from appropriate field set (compact version. for expanded version, see above)
                for (Schema.FieldSetMember fsm : fieldSetsByObjectName.get(objectName).getFields()) {
                    fields.add(fsm.getFieldPath());
                    fieldsSet.add(fsm.getFieldPath());
                }
                if (fieldsSet.add('Id')) {
                    fields.add('Id');
                }
                if (objectName == oppLineItemObjectName && fieldsSet.add('OpportunityId')) {
                    fields.add('OpportunityId');
                }
                query = queryCreator(fields, queryPartsByObjectName.get(objectName));
            }
            // Declare query result to add to QueryResultSet instance
            Loop.ExternalData.QueryResult queryResult = new Loop.ExternalData.QueryResult(objectName, fields);
            
            if (!string.isBlank(query)) {
                // for each row of data returned by the query
                for (SObject record : Database.query(query)) {
                    // Store the values (as strings) from the record in the same order of the fields defined in the QueryResult instance
                    List<string> recordValues = new List<string>();
                    for (string field : fields) {
                        recordValues.add(getFieldValue(record, field));
                    }
                    
                    // Add the values to the QueryResult instance rows
                    queryResult.rows.add(recordValues);
                }
            }
            
            // Add the QueryResult instance to the QueryResultSet instance
            // This needs to be done for every object specified in requestInfo.GetObjectNames(), regardless of whether data is required for the request.
            queryResultSet.add(queryResult);
        }
        
        return queryResultSet;
    }
    
    // Helper methods / variables
    
    // Use hard references to field sets (instead of soft references, e.g. Schema.SObjectType.Account.fieldSets.getMap().get('field_set_name')) to ensure they do not get deleted or renamed
    private Map<string, Schema.FieldSet> fieldSetsByObjectName = new Map<string, Schema.FieldSet> {
        opportunityObjectName => Schema.SObjectType.Opportunity.FieldSets.SampleApexDataFieldSet,
        accountObjectName => Schema.SObjectType.Account.FieldSets.SampleApexDataFieldSet,
        oppLineItemObjectName => Schema.SObjectType.OpportunityLineItem.FieldSets.SampleApexDataFieldSet
    };
    
    private Map<string, Map<string, Schema.SObjectField>> fieldMapsByObjectName = new Map<string, Map<string, Schema.SObjectField>> {
        opportunityObjectName => Schema.SObjectType.Opportunity.Fields.getMap(),
        accountObjectName => Schema.SObjectType.Account.Fields.getMap(),
        oppLineItemObjectName => Schema.SObjectType.OpportunityLineItem.Fields.getMap()
    };
    
    private Map<string, string> queryPartsByObjectName = new Map<string, string> {
        opportunityObjectName => 'Opportunity WHERE Id = :opportunityId',
        accountObjectName => 'Account WHERE Id = :accountId',
        oppLineItemObjectName => 'OpportunityLineItem WHERE OpportunityId = :opportunityId ORDER BY SortOrder, CreatedDate'
    };
    
    // This method properly retrieves a field value from an SObject for fields used through lookup relationships, e.g. PricebookEntry.Product2.ProductCode on the OpportunityLineItem object.
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
    
    private string queryCreator(List<string> fields, string queryPart) {
        return string.format(
            'SELECT {0} FROM {1}',
            new List<string>{
                string.join(fields, ','),
                queryPart
            }
        );
    }
}