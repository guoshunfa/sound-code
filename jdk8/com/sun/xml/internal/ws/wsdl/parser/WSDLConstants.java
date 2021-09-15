package com.sun.xml.internal.ws.wsdl.parser;

import javax.xml.namespace.QName;

public interface WSDLConstants {
   String PREFIX_NS_WSDL = "wsdl";
   String NS_XMLNS = "http://www.w3.org/2001/XMLSchema";
   String NS_WSDL = "http://schemas.xmlsoap.org/wsdl/";
   String NS_SOAP11_HTTP_BINDING = "http://schemas.xmlsoap.org/soap/http";
   QName QNAME_SCHEMA = new QName("http://www.w3.org/2001/XMLSchema", "schema");
   QName QNAME_BINDING = new QName("http://schemas.xmlsoap.org/wsdl/", "binding");
   QName QNAME_DEFINITIONS = new QName("http://schemas.xmlsoap.org/wsdl/", "definitions");
   QName QNAME_DOCUMENTATION = new QName("http://schemas.xmlsoap.org/wsdl/", "documentation");
   QName NS_SOAP_BINDING_ADDRESS = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "address");
   QName NS_SOAP_BINDING = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "binding");
   QName NS_SOAP12_BINDING = new QName("http://schemas.xmlsoap.org/wsdl/soap12/", "binding");
   QName NS_SOAP12_BINDING_ADDRESS = new QName("http://schemas.xmlsoap.org/wsdl/soap12/", "address");
   QName QNAME_IMPORT = new QName("http://schemas.xmlsoap.org/wsdl/", "import");
   QName QNAME_MESSAGE = new QName("http://schemas.xmlsoap.org/wsdl/", "message");
   QName QNAME_PART = new QName("http://schemas.xmlsoap.org/wsdl/", "part");
   QName QNAME_OPERATION = new QName("http://schemas.xmlsoap.org/wsdl/", "operation");
   QName QNAME_INPUT = new QName("http://schemas.xmlsoap.org/wsdl/", "input");
   QName QNAME_OUTPUT = new QName("http://schemas.xmlsoap.org/wsdl/", "output");
   QName QNAME_PORT = new QName("http://schemas.xmlsoap.org/wsdl/", "port");
   QName QNAME_ADDRESS = new QName("http://schemas.xmlsoap.org/wsdl/", "address");
   QName QNAME_PORT_TYPE = new QName("http://schemas.xmlsoap.org/wsdl/", "portType");
   QName QNAME_FAULT = new QName("http://schemas.xmlsoap.org/wsdl/", "fault");
   QName QNAME_SERVICE = new QName("http://schemas.xmlsoap.org/wsdl/", "service");
   QName QNAME_TYPES = new QName("http://schemas.xmlsoap.org/wsdl/", "types");
   String ATTR_TRANSPORT = "transport";
   String ATTR_LOCATION = "location";
   String ATTR_NAME = "name";
   String ATTR_TNS = "targetNamespace";
}
