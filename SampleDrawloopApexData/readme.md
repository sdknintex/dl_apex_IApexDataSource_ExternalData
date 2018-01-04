SampleDrawloopApexData sample
=============================

Demonstrates how to implement the **IApexDataSource** interface to provide data for a DocGen Package using an Apex Data relationship that queries a single opportunity, any line items for that opportunity, the account for that opportunity, and an arbitrary SOQL query. Use this code to generate documents from records on the Opportunity object.

Overview
--------

The sample is a single file, named SampleDrawloopApexData.java, that contains an Apex class named **SampleDrawloopApexData**. The class implements the four required methods of the **IApexDataSource** interface to get data for a DocGen Package. In this sample, the data comes from three field sets that you create before you implement the class and a SQQL query that looks for contacts on an account with the letter "C" in their title. This sample is hard coded to run against the Salesforce Opportunity object and add data to four Apex objects, **ApexAccount**, **ApexOppLineItem**, **ApexOpportunity** and **ApexSoql**.

Implementing the sample
-----------------------

First create three field sets on the **Accounts**, **Opportunity** and **Opportunity Products** objects, then use the Developer Console to implement the **SampleDrawloopApexData** Apex class in Salesforce. With the class implemented, create an Apex Data relationship in a DocGen Package.

To create the field sets

1. From **Setup**, enter Field Sets in the **Quick Find** box, then under Accounts, click Field Sets to display a list of the field sets on the Account object in your organization.
1. Click **New**.
1. In **Field Set Label**, type: *SampleApexDataFieldSet*
1. In **Field Set Name**, use *SampleApexDataFieldSet* (the same name).
1. In **Where is this used?**, type: *This field set is for the SampleDrawloopApexData class.*
1. Click **Save**.
1. In the field set editor, add the fields from this object for the data that you want to make available in your DocGen Package. For more information, see [Creating and Editing Field Sets](https://help.salesforce.com/articleView?err=1&id=fields_editing_field_sets.htm&siteLang=en_US&type=0).
1. Repeat steps 1 through 7 to create field sets on **Opportunity** and **Opportunity Products** objects. Use the same values in **Field Set Label**, **Field Set Name**, and **Where is this used?**. Specify whatever fields seem useful to your organization in the field sets themselves.

To create the Apex class

1. Log into your Salesforce instance.
1. From your Salesforce instance, open the Developer Console. For more information about the Developer Console in Salesforce, see [Developer Console](https://developer.salesforce.com/page/Developer_Console).
1. From the Developer Console, create a new Apex class named **SampleDrawloopApexData**.
1. Paste the contents of the sample file (SampleDrawloopApexData.java) into the Developer Console, overwriting the existing contents.
1. Save the new Apex class, and then close the Developer Console.

To create the Apex Data relationship

1. In Nintex Document Generation for Salesforce, from **DocGen Packages**, create a new DocGen Package based on the Opportunity object.
1. From the new DocGen Package, in the Relationships related list, add an Apex Data relationship.
1. In the Apex Data relationship, from Apex Class, select **--No Namespace--** and **SampleDrawloopApexData**.
1. In Relationship (What you want), select one of the following:
    Available relationships | Description
    --- | ---
    **ApexAccount** | Fields in the *SampleApexDataFieldSet* field set on the **Account** object.
    **ApexOppLineItem** | Fields in the *SampleApexDataFieldSet* field set  on the **Opportunity Products** object.
    **ApexOpportunity** | Fields in the *SampleApexDataFieldSet* field set on the **Opportunity** object.
    **ApexSoql** | Contacts associated with the account with the letter "C" in their title.

1. Save the Apex Data relationship.

Using the sample
----------------

Once you implement the sample and create the relationship, you create a template with field tags from the Apex Data relationship. Load this template into your DocGen Package and then use a record from the Opportunity object to generate a document.

To generate documents with the sample

1. In the Field Tagger for the DocGen package with the Apex Data relationship you implemented, select the custom Apex Object as the **Main Object**. The custom object you select here will match the item that you chose in the Apex Data relationship (from step 4, **Relationship (What you want)**). For example, if you chose **ApexAccount** in the relationship, then in field tagger you would specify **ApexAccount (SampleDrawloopApexData)**.
1. Use the tags to create or update your template.
1. Load the template into the DocGen Package.
1. Run the DocGen Package from a record on the Opportunity object.