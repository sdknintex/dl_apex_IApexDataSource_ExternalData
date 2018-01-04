global class ExternalData implements Loop.IApexDataSource {
    public Set<string> getGlobalDescribe() {
        
        ExternalDataService.ExternalDataServiceSoap service = new ExternalDataService.ExternalDataServiceSoap();
        ExternalDataService.ExternalDataResult externalDataResult = service.RetrieveTables();
        
        Set<string> externalObjects = new Set<string>();
        if(externalDataResult.IsSuccessful) {
            string serializedObjects = externalDataResult.Tables;
            List<List<string>> objects = (List<List<string>>)JSON.deserialize(serializedObjects, List<List<string>>.class);
            for (List<string> obj : objects) {
                string objectName = obj[0];
                System.debug('Object: ' + objectName);
                externalObjects.add(objectName);
            }
        }
        return externalObjects;
    }
    
    private String queryStart = 'SELECT * FROM ';

    public List<Loop.ExternalData.DataObject> describeObjects(List<string> objNames) {
        List<Loop.ExternalData.DataObject> output = new List<Loop.ExternalData.DataObject>();
        Set<string> allObjects = getGlobalDescribe();
        for (string objName : objNames) {
            List<Loop.ExternalData.FieldInfo> outputFields = new List<Loop.ExternalData.FieldInfo>();
            if (allObjects.contains(objName)) {
                ExternalDataService.ExternalDataServiceSoap service = new ExternalDataService.ExternalDataServiceSoap();
                ExternalDataService.ExternalDataResult externalDataResult = service.RetrieveTableDetails(objName);
                
                if(externalDataResult.IsSuccessful) {
                    string foreignKeys = externalDataResult.ForeignKeys;
                    List<List<string>> foreignKeyInfo = (List<List<string>>)JSON.deserialize(foreignKeys, List<List<string>>.class);
                    Map<string, string> foreignKeyMap = new Map<string, string>();
                    for (List<string> foreignKey: foreignKeyInfo) {
                        System.debug(objName + '.ForeignKey: ' + String.join(foreignKey, ', '));
                        foreignKeyMap.put(foreignKey[0], foreignKey[1]);
                        //Loop.externalData.FieldInfo outputInfo = new Loop.ExternalData.FieldInfo(foreignKey[0], Schema.DisplayType.REFERENCE);
                        //outputInfo.referenceTo = foreignKey[1];
                        //outputFields.add(outputInfo);
                    }
                    
                    string serializedInfoSchema = externalDataResult.InfoSchema;
                    List<List<string>> infoSchemas = (List<List<string>>)JSON.deserialize(serializedInfoSchema, List<List<string>>.class);
                    for (List<string> infoSchema: infoSchemas) {
                        System.debug(objName + '.InfoSchema: ' + String.join(infoSchema, ', '));
                        
                        Loop.ExternalData.FieldInfo outputInfo;
                        if (foreignKeyMap.containsKey(infoSchema[0])) {
                            outputInfo = new Loop.ExternalData.FieldInfo(infoSchema[0], Schema.DisplayType.REFERENCE);
                            outputInfo.referenceTo = foreignKeyMap.get(infoSchema[0]);
                        }
                        else {
                            outputInfo = new Loop.ExternalData.FieldInfo(infoSchema[0], GetDisplayType(infoSchema[1]));
                        }
                        outputFields.add(outputInfo);
                    }
                }
            }
            output.add(new Loop.ExternalData.DataObject(objName, outputFields));
        }
        return output;
    }
    
    public DisplayType GetDisplayType(string type) {
        type = type.toLowerCase();
        
        Map<string, Schema.DisplayType> displayTypeMap = new Map<string, Schema.DisplayType>{
            'int' => Schema.DisplayType.Integer,
            'anytype' => Schema.DisplayType.anytype,
            'id' => Schema.DisplayType.ID,
            'varchar' => Schema.DisplayType.String,
            'text' => Schema.DisplayType.String,
            'longtext' => Schema.DisplayType.String,
            'mediumtext' => Schema.DisplayType.String,
            'tinytext' => Schema.DisplayType.String,
            'char' => Schema.DisplayType.String
        };
        
        if (displayTypeMap.containsKey(type)) {
            return displayTypeMap.get(type);
        }
        else {
            System.debug('Error: DisplayType not included in map (' + type + ')');
            return Schema.DisplayType.anytype;
        }
    }
    
    public Set<string> getChildRelationships(string objectName) {
    
        ExternalDataService.ExternalDataServiceSoap service = new ExternalDataService.ExternalDataServiceSoap();
        ExternalDataService.ExternalDataResult externalDataResult = service.RetrieveTableChildren(objectName);
        
        Set<string> children = new Set<string>();
        
        if(externalDataResult.IsSuccessful) {
            string foreignKeys = externalDataResult.ForeignKeys;
            List<List<string>> foreignKeyInfo = (List<List<string>>)JSON.deserialize(foreignKeys, List<List<string>>.class);
            for (List<string> foreignKey: foreignKeyInfo) {
                System.debug(objectName + '.ForeignKey: ' + String.join(foreignKey, ', '));
                children.add(foreignKey[2]);
            }
        }
        return children;
    }
    
    public Loop.ExternalData.QueryResultSet query(Loop.ExternalData.QueryRequestInfo requestInfo) {
        Loop.ExternalData.QueryResultSet results = new Loop.ExternalData.QueryResultSet();
        if (string.valueOf(requestInfo.RecordId).startswith(Schema.sObjectType.Opportunity.keyprefix)) {
            Set<string> allObjects = getGlobalDescribe();
            for (string objectName : requestInfo.GetObjectNames()) {
                if (allObjects.contains(objectName)) {
                    ExternalDataService.ExternalDataServiceSoap service = new ExternalDataService.ExternalDataServiceSoap();
                    string query = queryStart + objectName;
                    ExternalDataService.ExternalDataResult externalDataResult = service.RetrieveTableData(query);
                    Loop.ExternalData.QueryResult queryResult;

                    if(externalDataResult.IsSuccessful) {
                        List<string> fields = (List<string>)JSON.deserialize(externalDataResult.Fields, List<string>.class);
                        queryResult = new Loop.ExternalData.QueryResult(objectName, fields);
                        
                        string data = externalDataResult.Data;
                        List<List<string>> rows = (List<List<string>>)JSON.deserialize(data, List<List<string>>.class);
                        for (List<string> row : rows) {
                            queryResult.rows.add(row);
                            System.debug(objectName + '.TableData: ' + String.join(row, ', '));
                        }
                    } else {
                        //this doesn't work well, do something else
                        queryResult = new Loop.ExternalData.QueryResult(objectName, new List<string> {'Error'});
                        queryResult.rows.add(new List<string> {'error'});
                    }
                    results.add(queryResult);
                }
            }
        }
        return results;
    }
}


//Generated by wsdl2apex
public class ExternalDataService {
    public class RetrieveTableDetails_element {
        public String objectName;
        private String[] objectName_type_info = new String[]{'objectName','Loop',null,'0','1','false'};
        private String[] apex_schema_type_info = new String[]{'Loop','true','false'};
        private String[] field_order_type_info = new String[]{'objectName'};
    }
    public class RetrieveTableDetailsResponse_element {
        public ExternalDataService.ExternalDataResult RetrieveTableDetailsResult;
        private String[] RetrieveTableDetailsResult_type_info = new String[]{'RetrieveTableDetailsResult','Loop',null,'0','1','false'};
        private String[] apex_schema_type_info = new String[]{'Loop','true','false'};
        private String[] field_order_type_info = new String[]{'RetrieveTableDetailsResult'};
    }
    public class RetrieveTableDataResponse_element {
        public ExternalDataService.ExternalDataResult RetrieveTableDataResult;
        private String[] RetrieveTableDataResult_type_info = new String[]{'RetrieveTableDataResult','Loop',null,'0','1','false'};
        private String[] apex_schema_type_info = new String[]{'Loop','true','false'};
        private String[] field_order_type_info = new String[]{'RetrieveTableDataResult'};
    }
    public class RetrieveTableChildrenResponse_element {
        public ExternalDataService.ExternalDataResult RetrieveTableChildrenResult;
        private String[] RetrieveTableChildrenResult_type_info = new String[]{'RetrieveTableChildrenResult','Loop',null,'0','1','false'};
        private String[] apex_schema_type_info = new String[]{'Loop','true','false'};
        private String[] field_order_type_info = new String[]{'RetrieveTableChildrenResult'};
    }
    public class RetrieveTables_element {
        private String[] apex_schema_type_info = new String[]{'Loop','true','false'};
        private String[] field_order_type_info = new String[]{};
    }
    public class RetrieveTableData_element {
        public String query;
        private String[] query_type_info = new String[]{'query','Loop',null,'0','1','false'};
        private String[] apex_schema_type_info = new String[]{'Loop','true','false'};
        private String[] field_order_type_info = new String[]{'query'};
    }
    public class ExternalDataResult {
        public Boolean IsSuccessful;
        public String Tables;
        public String Data;
        public String Fields;
        public String InfoSchema;
        public String ForeignKeys;
        private String[] IsSuccessful_type_info = new String[]{'IsSuccessful','Loop',null,'1','1','false'};
        private String[] Tables_type_info = new String[]{'Tables','Loop',null,'0','1','false'};
        private String[] Data_type_info = new String[]{'Data','Loop',null,'0','1','false'};
        private String[] Fields_type_info = new String[]{'Fields','Loop',null,'0','1','false'};
        private String[] InfoSchema_type_info = new String[]{'InfoSchema','Loop',null,'0','1','false'};
        private String[] ForeignKeys_type_info = new String[]{'ForeignKeys','Loop',null,'0','1','false'};
        private String[] apex_schema_type_info = new String[]{'Loop','true','false'};
        private String[] field_order_type_info = new String[]{'IsSuccessful','Tables','Data','Fields','InfoSchema','ForeignKeys'};
    }
    public class RetrieveTableChildren_element {
        public String objectName;
        private String[] objectName_type_info = new String[]{'objectName','Loop',null,'0','1','false'};
        private String[] apex_schema_type_info = new String[]{'Loop','true','false'};
        private String[] field_order_type_info = new String[]{'objectName'};
    }
    public class RetrieveTablesResponse_element {
        public ExternalDataService.ExternalDataResult RetrieveTablesResult;
        private String[] RetrieveTablesResult_type_info = new String[]{'RetrieveTablesResult','Loop',null,'0','1','false'};
        private String[] apex_schema_type_info = new String[]{'Loop','true','false'};
        private String[] field_order_type_info = new String[]{'RetrieveTablesResult'};
    }
    public class ExternalDataServiceSoap {
        public String endpoint_x = 'http://ec2-54-210-61-140.compute-1.amazonaws.com/ExternalDataService.asmx';
        public Map<String,String> inputHttpHeaders_x;
        public Map<String,String> outputHttpHeaders_x;
        public String clientCertName_x;
        public String clientCert_x;
        public String clientCertPasswd_x;
        public Integer timeout_x;
        private String[] ns_map_type_info = new String[]{'Loop', 'ExternalDataService'};
        public ExternalDataService.ExternalDataResult RetrieveTableChildren(String objectName) {
            ExternalDataService.RetrieveTableChildren_element request_x = new ExternalDataService.RetrieveTableChildren_element();
            request_x.objectName = objectName;
            ExternalDataService.RetrieveTableChildrenResponse_element response_x;
            Map<String, ExternalDataService.RetrieveTableChildrenResponse_element> response_map_x = new Map<String, ExternalDataService.RetrieveTableChildrenResponse_element>();
            response_map_x.put('response_x', response_x);
            WebServiceCallout.invoke(
              this,
              request_x,
              response_map_x,
              new String[]{endpoint_x,
              'Loop/RetrieveTableChildren',
              'Loop',
              'RetrieveTableChildren',
              'Loop',
              'RetrieveTableChildrenResponse',
              'ExternalDataService.RetrieveTableChildrenResponse_element'}
            );
            response_x = response_map_x.get('response_x');
            return response_x.RetrieveTableChildrenResult;
        }
        public ExternalDataService.ExternalDataResult RetrieveTableDetails(String objectName) {
            ExternalDataService.RetrieveTableDetails_element request_x = new ExternalDataService.RetrieveTableDetails_element();
            request_x.objectName = objectName;
            ExternalDataService.RetrieveTableDetailsResponse_element response_x;
            Map<String, ExternalDataService.RetrieveTableDetailsResponse_element> response_map_x = new Map<String, ExternalDataService.RetrieveTableDetailsResponse_element>();
            response_map_x.put('response_x', response_x);
            WebServiceCallout.invoke(
              this,
              request_x,
              response_map_x,
              new String[]{endpoint_x,
              'Loop/RetrieveTableDetails',
              'Loop',
              'RetrieveTableDetails',
              'Loop',
              'RetrieveTableDetailsResponse',
              'ExternalDataService.RetrieveTableDetailsResponse_element'}
            );
            response_x = response_map_x.get('response_x');
            return response_x.RetrieveTableDetailsResult;
        }
        public ExternalDataService.ExternalDataResult RetrieveTableData(String query) {
            ExternalDataService.RetrieveTableData_element request_x = new ExternalDataService.RetrieveTableData_element();
            request_x.query = query;
            ExternalDataService.RetrieveTableDataResponse_element response_x;
            Map<String, ExternalDataService.RetrieveTableDataResponse_element> response_map_x = new Map<String, ExternalDataService.RetrieveTableDataResponse_element>();
            response_map_x.put('response_x', response_x);
            WebServiceCallout.invoke(
              this,
              request_x,
              response_map_x,
              new String[]{endpoint_x,
              'Loop/RetrieveTableData',
              'Loop',
              'RetrieveTableData',
              'Loop',
              'RetrieveTableDataResponse',
              'ExternalDataService.RetrieveTableDataResponse_element'}
            );
            response_x = response_map_x.get('response_x');
            return response_x.RetrieveTableDataResult;
        }
        public ExternalDataService.ExternalDataResult RetrieveTables() {
            ExternalDataService.RetrieveTables_element request_x = new ExternalDataService.RetrieveTables_element();
            ExternalDataService.RetrieveTablesResponse_element response_x;
            Map<String, ExternalDataService.RetrieveTablesResponse_element> response_map_x = new Map<String, ExternalDataService.RetrieveTablesResponse_element>();
            response_map_x.put('response_x', response_x);
            WebServiceCallout.invoke(
              this,
              request_x,
              response_map_x,
              new String[]{endpoint_x,
              'Loop/RetrieveTables',
              'Loop',
              'RetrieveTables',
              'Loop',
              'RetrieveTablesResponse',
              'ExternalDataService.RetrieveTablesResponse_element'}
            );
            response_x = response_map_x.get('response_x');
            return response_x.RetrieveTablesResult;
        }
    }
}