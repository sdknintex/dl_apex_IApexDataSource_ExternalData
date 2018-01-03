@isTest
private class SampleDrawloopApexData1SFObjectTest {
    private static testMethod void testApexData() {
        // Create data for query
        Account a = new Account(Name = '1');
        insert a;
        
        Opportunity o = new Opportunity(Name = 'test', AccountId = a.Id, StageName = 'Prospecting', CloseDate = Date.today());
        insert o;
        
        // Create apex data class instance
        SampleDrawloopApexData1SFObject apexData = new SampleDrawloopApexData1SFObject();
        
        Test.startTest();
        
        Set<string> gd = apexData.getGlobalDescribe();
        system.assertEquals(1, gd.size());
        
        system.assert(apexData.getChildRelationships('ApexOpportunity').isEmpty());
        
        List<Loop.ExternalData.DataObject> dataObjects = apexData.describeObjects(new List<string>(gd));
        system.assertEquals(1, dataObjects.size());
        for (Loop.ExternalData.DataObject dataObject : dataObjects) {
            system.assert(!dataObject.fields.isEmpty());
        }
        
        Loop.ExternalData.QueryRequestInfo requestInfo = new Loop.ExternalData.QueryRequestInfo();
        requestInfo.RecordId = o.Id;
        
        Loop.ExternalData.Relationship opps = new Loop.ExternalData.Relationship();
        opps.Name = 'ApexOpportunity';
        
        requestInfo.Relationships = new List<Loop.ExternalData.Relationship>{opps};
        
        Loop.ExternalData.QueryResultSet qrs = apexData.query(requestInfo);
        
        system.assertEquals(1, qrs.results.size());
        
        for (Loop.ExternalData.QueryResult queryResult : qrs.results) {
            system.assertEquals(1, queryResult.rows.size());
        }
        
        Test.stopTest();
    }
}