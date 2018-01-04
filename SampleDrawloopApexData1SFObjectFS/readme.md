SampleDrawloopApexData1SFObjectFS sample
========================================

Demonstrates how to implement the IApexDataSource interface to provide data for a DocGen Package using an Apex Data relationship that returns a field set from a single opportunity. Use this code to generate documents from records on the Opportunity object.

Overview
--------

The sample is a single file, named SampleDrawloopApexData - 1 Sf Object using field set.java, that contains an Apex class named SampleDrawloopApexData1SFObjectFS. The class implements the four required methods of the IApexDataSource interface to get data for a DocGen Package. In this sample, the data comes from a field set that you create before you implement the class. This sample is hard coded to run against the Salesforce Opportunity object and add data to an Apex object called ApexOpportunity.

Implementing the sample
-----------------------

First create a field set on the Opportunities object, then use the Developer Console to implement the SampleDrawloopApexData1SFObjectFS Apex class in Salesforce. With the class implemented, create an Apex Data relationship in a DocGen Package.

To create the field set

1. From Setup, enter Field Sets in the Quick Find box, then under Opportunities, click Field Sets to display a list of the field sets on the Opportunities object in your organization.
1. Click New.
1. In Field Set Label, type SampleApexDataFieldSet
1. In Field Set Name, use SampleApexDataFieldSet (the same name).
1. In Where is this used?, type This field set is for the SampleDrawloopApexData1SFObejectFS class.
1. Click Save.
1. In the field set editor, add the fields from this object for the data that you want to make available in your DocGen Package.

**Tip:** If you want to use the sample template (SampleDrawloopApexData1SFObjectFS.docx), include these fields): Name, Type, Probability and Stage Name. You are not required to use the sample template or these fields. For more information, see [Creating and Editing Field Sets](https://help.salesforce.com/articleView?err=1&id=fields_editing_field_sets.htm&siteLang=en_US&type=0).

To create the Apex class

1. Log into your Salesforce instance.
1. From your Salesforce instance, open the Developer Console. For more information about the Developer Console in Salesforce, see [Developer Console](https://developer.salesforce.com/page/Developer_Console).
1. From the Developer Console, create a new Apex class named SampleDrawloopApexData1SFObjectFS.
1. Paste the contents of the sample file (SampleDrawloopApexData - 1 Sf Object using field set.java) into the Developer Console, overwriting the existing contents.
1. Save the new Apex class, and then close the Developer Console.

To create the Apex Data relationship

1. In Nintex Document Generation for Salesforce, from DocGen Packages, create a new DocGen Package based on the Opportunity object.
1. From the new DocGen Package, in the Relationships related list, add an Apex Data relationship.
1. In the Apex Data relationship, from Apex Class, select --No Namespace-- and SampleDrawloopApexData1SFObjectFS.
1. In Relationship (What you want), select ApexOpportunity.
1. Save the Apex Data relationship.

Using the sample
----------------

Once you implement the sample and create the relationship, you create a template with field tags from the Apex Data relationship. Load this template into your DocGen Package and then use a record from the Opportunity object to generate a document.

To generate documents with the sample

1. In the Field Tagger for the docGen package with the Apex Data relationship you implemented, select the custom Apex Object (ApexOpportunity (SampleDrawloopApexData1SFObjectFS)) as the Main Object.
1. Use the tags to create or update your template.
1. Load the template into the DocGen Package.
1. Run the DocGen Package from a record on the Opportunity object.

**Tip:** You can use the template SampleDrawloopApexData1SFObjectFS.docx from the repo instead of creating a new template. This template has all the tags for this sample already added.