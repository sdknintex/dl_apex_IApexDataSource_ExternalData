OppLineItemGrouping sample
==========================

Demonstrates how to implement the IApexDataSource interface to provide an external data source that can present Salesforce objects by using a custom grouping.

Overview
--------

The sample is a single file, named OppLineItemGrouping.java, that contains an Apex class named OppLineItemGrouping. The class groups existing Opportunity objects using a custom grouping defined by the family of the product for the PricebookEntry associated with each Opportunity, where applicable.

The class uses a set of SOQL queries to retrieve opportunities, group them as described, and present two types of DataObject instances - a DataObject for each custom group, named OppLineItemGroup, and a DataObject for each opportunity within a given group, named OppLineItem.

Implementing the sample
-----------------------

You need to implement the OppLineItemGrouping Apex class in Salesforce by using the Developer Console.

To implement the sample

1. Log into your Salesforce instance, using a Developer Edition account.
1. In a text editor, open the sample file named OppLineItemGrouping.java.
1. From your Salesforce instance, open the Developer Console. For more information about the Developer Console in Salesforce, see [Developer Console](https://developer.salesforce.com/page/Developer_Console).
1. From the Developer Console, create a new Apex class named OppLineItemGrouping.
1. Paste the contents of the sample file into the Developer Console, overwriting the existing contents
1. Save the new Apex class, and then close the Developer Console.