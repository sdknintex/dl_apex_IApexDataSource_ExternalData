// Sample Apex class leveraging Nintex Drawloop feature Apex Data
// Class must be global for Nintex Drawloop to access it
// Class must implement the Loop.IApexDataSource Apex interface to use it in a Document Package Relationship
global class SampleDrawloopApexData1SFObjectFS implements Loop.IApexDataSource {
    
    // Loop.IApexDataSource interface requires four public methods with the following signatures:
    // Set<string> getGlobalDescribe()
    // Set<string> getChildRelationships(string objectName)
    // List<ExternalData.DataObject> describeObjects(List<string> objectNames)
    // Loop.ExternalData.QueryResultSet query(Loop.ExternalData.QueryRequestInfo requestInfo)
    
    // optional, but good practice: use variables to store object names to avoid mistakes from typos
    private string opportunityObjectName = 'ApexOpportunity';
    
    public Set<string> getGlobalDescribe() {
        // Return a set of object names that this Apex class will provide data for.
        // In this example, we will provide data for a single Opportunity.
        return new Set<string>{
            opportunityObjectName
        };
    }
    
    public Set<string> getChildRelationships(string objectName) {
        // Return a set of object names corresponding to the children for the objectName specified
        // In this example, there are no child objects.
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
            
            if (objectName == opportunityObjectName) {
                // Describe the fields for this object.
                // Loop through fields in the appropriate field set, store the info, and add to fields
                Schema.FieldSet fieldSet = Schema.SObjectType.Opportunity.FieldSets.SampleApexDataFieldSet;
                List<Schema.FieldSetMember> fieldSetMembers = fieldSet.getFields();
                for (Schema.FieldSetMember fsm : fieldSetMembers) {
                    
                    // Store the field info for this field in the field set
                    Loop.ExternalData.FieldInfo fieldInfo = new Loop.ExternalData.FieldInfo(fsm.getFieldPath(), fsm.getType());
                    fieldInfo.label = fsm.getLabel();
                    
                    // optional (not shown): Pull the field's scale and referenceTo from the Salesforce field describe info
                    
                    fields.add(fieldInfo);
                }
            }
            
            // Declare variable to add to results list using fields described above
            Loop.ExternalData.DataObject describeObjectResult = new Loop.ExternalData.DataObject(objectName, fields);
            
            // Add to results list
            describeObjectResults.add(describeObjectResult);
        }
        
        return describeObjectResults;
    }
    
    public Loop.ExternalData.QueryResultSet query(Loop.ExternalData.QueryRequestInfo requestInfo) {
        // Provide data for each object in requestInfo.GetObjectNames()
        
        // Assume that the Document Package is run from the Opportunity
        Id opportunityId = requestInfo.RecordId;
        
        // Declare the variable to be returned.
        Loop.ExternalData.QueryResultSet queryResultSet = new Loop.ExternalData.QueryResultSet();
        
        // Loop through all objects requested. The QueryResultSet instance returned needs to contain a QueryResult instance for each object requested.
        for (string objectName : requestInfo.GetObjectNames()) {
            // Declare fields list for QueryResult instance
            List<string> fields = new List<string>();
            
            // Declare query result to add to QueryResultSet instance
            Loop.ExternalData.QueryResult queryResult = new Loop.ExternalData.QueryResult(objectName, fields);
            
            // set up fields list and query to get data for QueryResult instance
            if (objectName == opportunityObjectName) {
                // Get list of fields from appropriate field set (compact version. for expanded version, see above)
                for (Schema.FieldSetMember fsm : Schema.SObjectType.Opportunity.FieldSets.SampleApexDataFieldSet.getFields()) {
                    fields.add(fsm.getFieldPath());
                }
                
                // Since we added fields to the list, update the QueryResult instance
                queryResult = new Loop.ExternalData.QueryResult(objectName, fields);
                
                string query = string.format(
                    'SELECT {0} FROM Opportunity WHERE Id = :opportunityId',
                    new List<string>{ string.join(fields, ',') }
                );
                
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
    
    // Helper method
    
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
}