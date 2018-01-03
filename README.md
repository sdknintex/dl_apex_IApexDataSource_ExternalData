# IApexDataSource and ExternalData samples

The following sample collection demonstrates how to implement the IApexDataSource interface to define external data sources that use the various inner classes implemented in the ExternalData class to provide external data for use in DDPs.

## SampleDrawloopApexData1SFObject

Demonstrates how to implement the IApexDataSource interface to provide data for a Document Package using an Apex Data relationship that returns all the fields from a single opportunity.

## SampleDrawloopApexData1SFObjectTest

Demonstrates how to create an Apex test class for SampleDrawloopApexData1SFObject.

## SampleDrawloopApexData1SFObjectFS

Demonstrates how to implement the IApexDataSource interface to provide data for a Document Package using an Apex Data relationship that returns a field set from a single opportunity.

## SampleDrawloopApexData2SFObjectsTo1

Demonstrates how to implement the IApexDataSource interface to provide data for a Document Package using an Apex Data relationship that queries two objects to return data for a single opportunity and any line items for that opportunity,

## SampleDrawloopApexDataQuery

Demonstrates how to implement the IApexDataSource interface to provide data for a Document Package using an Apex Data relationship that runs a SOQL query.

## SampleDrawloopApexData

Demonstrates how to implement the IApexDataSource interface to provide data for a Document Package using an Apex Data relationship that combines query methods demonstrated in the other SampleDrawloopApexData samples.

## OppLineItemGrouping

Demonstrates how to implement the IApexDataSource interface to provide an external data source that can present Salesforce objects by using a custom grouping.

## ExternalData

Demonstrates how to implement the IApexDataSource interface and use the other inner classes included with the ExternalData class to gather data from an external service for use within Nintex Document Generation for Salesforce.

For more information: 

* [Nintex Document Generation for Salesforce product assistance](https://help.nintex.com/en-US/docgen/docgen-portal.htm)
* [Apex for Nintex DocGen for Salesforce](https://help.nintex.com/en-us/docgen/docservices/Default.htm#cshid=9032)
* [ExternalData class](http://help.nintex.com/en-us/docgen/docservices/Default.htm#cshid=9057)
* [IApexDataSource interface](https://help.nintex.com/en-us/docgen/docservices/Default.htm#cshid=9058)