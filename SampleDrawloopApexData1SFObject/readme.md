SampleDrawloopApexData1SFObject sample
======================================

Demonstrates how to implement the IApexDataSource interface to provide data for a Document Package using an Apex Data relationship that returns all the fields from a single opportunity. Use this code to generate documents from records on the Opportunity object.

Overview
--------

The sample is a single file, named SampleDrawloopApexData - 1 Sf Object.java, that contains an Apex class named SampleDrawloopApexData1SFObject. The class implements the four required methods of the IApexDataSource interface to get data for a Document Package. In this sample, the data comes from all the fields associated with an opportunity record by iterating over the Opportunity object and dynamically assembling all available fields. (In other samples, the query assembles fields from a data set you create, which specifies the fields to include). This sample is hard coded to run against the Salesforce Opportunity object and add data to an Apex object called ApexOpportunity.

**Note:** There is a test class sample available that you can implement in Salesforce to test this class. Use the (SampleDrawloopApexData1SFObjectTest sample) to create a test class for SampleDrawloopApexData1SFObject.

Implementing the sample
-----------------------

Use the Developer Console to implement the SampleDrawloopApexData1SFObject Apex class in Salesforce and then creating an Apex Data relationship in a Document Package.

To create the Apex class

1. Log into your Salesforce instance.
1. From your Salesforce instance, open the Developer Console. (For more information about the Developer Console in Salesforce, see [Developer Console](https://developer.salesforce.com/page/Developer_Console)).
1. From the Developer Console, create a new Apex class named SampleDrawloopApexData1SFObject.
1. Paste the contents of the sample file (SampleDrawloopApexData - 1 Sf Object.java) into the Developer Console, overwriting the existing contents.
1. Save the new Apex class, and then close the Developer Console.

To create the Apex Data relationship

1. In Nintex Document Generation for Salesforce, from Drawloop Document Packages, create a new Document Package based on the Opportunity object.
1. From the new Document Package, in the Relationships related list, add an Apex Data relationship.
1. In the Apex Data relationship, from Apex Class, select --No Namespace-- and SampleDrawloopApexData1SFObject.
1. In Relationship (What you want), select ApexOpportunity.
1. Save the Apex Data relationship.

Using the sample
----------------

Once you implement the sample and create the relationship, you create a template with field tags from the Apex Data relationship. Load this template into your Document Package and then use a record from the Opportunity object to generate a document.

To generate documents with the sample

1. In the Field Tagger for the document package with the Apex Data relationship you implemented, select the custom Apex Object (ApexOpportunity (SampleDrawloopApexData1SFObject)) as the Main Object.
1. Use the tags to create or update your template.
1. Load the template into the Document Package.
1. Run the Document Package from a record on the Opportunity object.

**Tip:** You can use the template SampleDrawloopApexData1SFObject.docx from the repo instead of creating a new template. This template has all the tags for this sample already added.
