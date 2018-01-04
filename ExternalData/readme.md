ExternalData sample
===================

Demonstrates how to implement the IApexDataSource interface and use the other inner classes included with the ExternalData class to gather data from an external service for use within Nintex Document Generation for Salesforce.

Overview
--------

The sample is a single file, named ExternalData.java, that contains the following items:

* ExternalData
    The ExternalData class implements the IApexDataSource interface to interact with an external SOAP web service, represented by the ExternalDataService class, to query, retrieve, and present data for use in Drawloop. The ExternalData class retrieves information, such as table names and child relationships, from the external SOAP web service by using the SOAP client provided by the ExternalDataService class and transfers that information into appropriate inner classes, such as DataObject and FieldInfo, so that the information can be presented in a structure that can be used by Drawloop.
* ExternalDataService
    The ExternalDataService class implements a SOAP client that retrieves data from a sample SOAP web service.

Implementing the sample
-----------------------

You need to implement the ExternalDataService and **ExternalData** Apex classes in Salesforce by using the Developer Console.

To implement the sample

1. Log into your Salesforce instance, using a Developer Edition account.
1. In a text editor, open the sample file named ExternalData.java.
1. From your Salesforce instance, open the Developer Console. For more information about the Developer Console in Salesforce, see [Developer Console](https://developer.salesforce.com/page/Developer_Console).
1. From the Developer Console, create a new Apex class named ExternalDataService.
1. Select the code for the ExternalDataService class from the sample file, copy the selected code, and then paste the code into the Developer Console, overwriting the existing contents.
1. Save the new Apex class.
1. From the Developer Console, create a new Apex class named ExternalData.
1. Select the code for the ExternalData class from the sample file, copy the selected code, and then paste the code into the Developer Console, overwriting the existing contents.
1. Save the new Apex class, and then close the Developer Console.