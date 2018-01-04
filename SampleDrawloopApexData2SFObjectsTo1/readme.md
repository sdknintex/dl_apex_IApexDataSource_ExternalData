SampleDrawloopApexData2SFObjectsTo1 sample
==========================================

Demonstrates how to implement the IApexDataSource interface to provide data for a DocGen Package using an Apex Data relationship that queries two objects to return data for a single opportunity and any line items for that opportunity. Use this code to generate documents from records on the Opportunity object.

Overview
--------

The sample is a single file, named SampleDrawloopApexData - 2 Sf Objects to 1.java, that contains an Apex class named SampleDrawloopApexData2SFObjectsTo1. The class implements the four required methods of the IApexDataSource interface to get data for a DocGen Package. In this sample, the data comes from iterating over two objects, the Opportunities and Opportunity Products objects, and then dynamically assembling all available fields. This sample is hard coded to run against the Salesforce Opportunity object and add data to an Apex object called ApexOpportunityProduct.

Implementing the sample
-----------------------

Use the Developer Console to implement the SampleDrawloopApexData2SFObjectsTo1 Apex class in Salesforce and then create an Apex Data relationship in a DocGen Package.

To create the Apex class

1. Log into your Salesforce instance.
1. From your Salesforce instance, open the Developer Console. For more information about the Developer Console in Salesforce, see [Developer Console](https://developer.salesforce.com/page/Developer_Console).
1. From the Developer Console, create a new Apex class named SampleDrawloopApexData2SFObjectsTo1.
1. Paste the contents of the sample file (SampleDrawloopApexData - 2 Sf Objects to 1.java) into the Developer Console, overwriting the existing contents.
1. Save the new Apex class, and then close the Developer Console.

To create the Apex Data relationship

1. In Nintex Document Generation for Salesforce, from DocGen Packages, create a new DocGen Package based on the Opportunity object.
1. From the new DocGen Package, in the Relationships related list, add an Apex Data relationship.
1. In the Apex Data relationship, from Apex Class, select --No Namespace-- and SampleDrawloopApexData2SFObjectsTo1.
1. In Relationship (What you want), select ApexOpportunityProduct. 
1. Save the Apex Data relationship.

Using the sample
----------------

Once you implement the sample and create the relationship, you create a template with field tags from the Apex Data relationship. Load this template into your DocGen Package and then use a record from the Opportunity object to generate a document.

This sample combines data from two objects, with data from each object being stored in a variable. The variable names help you to differentiate between similarly named fields from both objects when you're creating your template. In the Field Tagger, you will select the variable name for fields from the Opportunities object as the main object (ApexOpportunityProduct), and the variable name for the Opportunity Products becomes a prefix (ApexProduct) to allow you to differentiate between similarly named fields.

![](https://help.nintex.com/en-us/docgen/docservices/docgen-sfdc/Services/images/apexdata001.png)

To generate documents with the sample

1. In the Field Tagger for the DocGen Package with the Apex Data relationship you implemented, select the custom Apex Object ApexOpportunityProduct (SampleDrawloopApexData2SFObjectsTo1) as the Main Object.
1. Use the tags to create or update your template.

    You can differentiate between tags for fields on the Opportunities object and tags for fields on the Opportunity Products object by inspecting the tags. The tag from the Opportunity Products object includes a prefix named _ApexProduct, which was the variable name used to collect the data in the query.

    For example, compare the tags for the similarly named fields from either object in the table below.

    Object | Field Label | Tag
    ---| --- | ---
    Opportunities | Created By ID | <<ApexOpportunityProduct_createdbyid>>
    Opportunity Product | Created By ID |     <<ApexOpportunityProduct_ApexProductcreatedbyid>>
    Opportunities | Created Date | <<ApexOpportunityProduct_createddate>>
    Opportunity Product | Created Date | <<ApexOpportunityProduct_ApexProductcreateddate>>

    So, if you want Create By ID from the Opportunity Product object in your template, you would use the <<ApexOpportunityProduct_ApexProductcreatedbyid>> tag because that tag includes the _ApexProduct prefix.

1. Load the template into the DocGen Package.
1. Run the DocGen Package from a record on the Opportunity object.