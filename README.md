# dl_apex_IApexDataSource_ExternalData
The following sample collection demonstrates how to implement the IApexDataSource interface to define external data sources that use the various inner classes implemented in the ExternalData class to provide external data for use in DDPs.

Each sample in this collection is a single, standalone file, meant to be implmented from a Developer Console in Salesforce. 

## ExternalData sample

The sample is a single file, named ExternalData.java. 

Demonstrates how to implement the IApexDataSource interface and use the other inner classes included with the ExternalData class to gather data from an external service for use within Drawloop.

### Implementing ExternalData sample

1. Log into your Salesforce instance, using a Developer Edition account.
2. In a text editor, open the sample file named ExternalData.java.
3. From your Salesforce instance, open the Developer Console. 
4. From the Developer Console, create a new Apex class named ExternalDataService.
5. Select the code for the ExternalDataService class from the sample file, copy the selected code, and then paste the code into the Developer Console, overwriting the existing contents.
6. Save the new Apex class.
7. From the Developer Console, create a new Apex class named ExternalData.
8. Select the code for the ExternalData class from the sample file, copy the selected code, and then paste the code into the Developer Console, overwriting the existing contents.
9. Save the new Apex class, and then close the Developer Console.

## OppLineItemGrouping sample

The sample is a single file, named OppLineItemGrouping.java.

Demonstrates how to implement the IApexDataSource interface to provide an external data source that can present Salesforce objects by using a custom grouping.

### Implmenting OppLineItemGrouping sample

1. Log into your Salesforce instance, using a Developer Edition account.
2. In a text editor, open the sample file named OppLineItemGrouping.java.
3. From your Salesforce instance, open the Developer Console.
4. From the Developer Console, create a new Apex class named OppLineItemGrouping.
5. Paste the contents of the sample file into the Developer Console, overwriting the existing contents
6. Save the new Apex class, and then close the Developer Console.

## SampleDrawloopApexData1SFObject sample

The sample is a single file, named SampleDrawloopApexData - 1 Sf Object.java.

Demonstrates how to implement the IApexDataSource interface to provide data from a single Salesforce object for an Apex Data relationship.

### Implmenting SampleDrawloopApexData1SFObject sample

1. Log into your Salesforce instance, using a Developer Edition account.
2. In a text editor, open the sample file named SampleDrawloopApexData - 1 Sf Object.java.
3. From your Salesforce instance, open the Developer Console.
4. From the Developer Console, create a new Apex class named **SampleDrawloopApexData1SFObject**.
5. Paste the contents of the sample file into the Developer Console, overwriting the existing contents
6. Save the new Apex class, and then close the Developer Console.
7. In the app, from Drawloop Document Packages, create a new Document Package based on the Opportunity object.
8. From the new Document Package, in the Relationships related list, add an Apex Data relationship.
9. In the Apex Data relationship, from Apex Class, select **--No Namespace--** and **SampleDrawloopApexData1SFObject**.
10. In **Relationship (What you want)**, select **ApexOpportunity**.
11. Save the Apex Data relationship.