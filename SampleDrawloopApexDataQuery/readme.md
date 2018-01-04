SampleDrawloopApexDataQuery sample
==================================

Demonstrates how to implement the IApexDataSource interface to provide data for a DocGen Package using an Apex Data relationship that runs a SOQL query. Use this code to generate documents from records on the Account object.

Overview
--------

The sample is a single file, named SampleDrawloopApexData - 1 Sf Query.java, that contains an Apex class named SampleDrawloopApexDataQuery. The class implements the four required methods of the IApexDataSource interface to get data for a DocGen Package. In this sample, the data comes from a single SQQL query that looks for contacts on an account with the letter "C" in their title. The query is hard coded to run against the Salesforce Account object, and add data to an Apex object called ApexSoql.

Implementing the sample
-----------------------

Use the Developer Console to implement the SampleDrawloopApexDataQuery Apex class in Salesforce and then creating an Apex Data relationship in a DocGen Package.

To create the Apex class

1. Log into your Salesforce instance.
1. From your Salesforce instance, open the Developer Console. For more information about the Developer Console in Salesforce, see [Developer Console](https://developer.salesforce.com/page/Developer_Console).
1. From the Developer Console, create a new Apex class named SampleDrawloopApexDataQuery.
1. Paste the contents of the sample file ([SampleDrawloopApexData - 1 Sf Query.java) into the Developer Console, overwriting the existing contents.
1. Save the new Apex class, and then close the Developer Console.

To create the Apex Data relationship

1. In Nintex Document Generation for Salesforce, from DocGen Packages, create a new DocGen Package based on the Account object.
1. From the new DocGen Package, in the Relationships related list, add an Apex Data relationship.
1. In the Apex Data relationship, from Apex Class, select --No Namespace-- and SampleDrawloopApexDataQuery.
1. In Relationship (What you want), select ApexSoql.
1. Save the Apex Data relationship.

Using the sample
----------------

Once you implement the sample and create the relationship, you create a template with field tags from the Apex Data relationship. Load this template into your DocGen Package and then use a record from the Opportunity object to generate a document.

To generate documents with the sample

1. In the Field Tagger for the DocGen Package with the Apex Data relationship you implemented, select the custom Apex Object (ApexSoql (SampleDrawloopApexDataQuery)) as the Main Object.
1. Use the tags to create or update your template.
1. Load the template into the DocGen Package.
1. Run the DocGen Package from a record on the Opportunity object.

**Tip:** You can use the template SampleDrawloopApexData1SFQuery.docx from the repo instead of creating a new template. This template has all the tags for this sample already added.