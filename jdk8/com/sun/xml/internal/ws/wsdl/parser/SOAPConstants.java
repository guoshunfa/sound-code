package com.sun.xml.internal.ws.wsdl.parser;

import javax.xml.namespace.QName;

public interface SOAPConstants {
   String URI_ENVELOPE = "http://schemas.xmlsoap.org/soap/envelope/";
   String URI_ENVELOPE12 = "http://www.w3.org/2003/05/soap-envelope";
   String NS_WSDL_SOAP = "http://schemas.xmlsoap.org/wsdl/soap/";
   String NS_WSDL_SOAP12 = "http://schemas.xmlsoap.org/wsdl/soap12/";
   String NS_SOAP_ENCODING = "http://schemas.xmlsoap.org/soap/encoding/";
   String URI_SOAP_TRANSPORT_HTTP = "http://schemas.xmlsoap.org/soap/http";
   QName QNAME_ADDRESS = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "address");
   QName QNAME_SOAP12ADDRESS = new QName("http://schemas.xmlsoap.org/wsdl/soap12/", "address");
   QName QNAME_BINDING = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "binding");
   QName QNAME_BODY = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "body");
   QName QNAME_SOAP12BODY = new QName("http://schemas.xmlsoap.org/wsdl/soap12/", "body");
   QName QNAME_FAULT = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "fault");
   QName QNAME_HEADER = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "header");
   QName QNAME_SOAP12HEADER = new QName("http://schemas.xmlsoap.org/wsdl/soap12/", "header");
   QName QNAME_HEADERFAULT = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "headerfault");
   QName QNAME_OPERATION = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "operation");
   QName QNAME_SOAP12OPERATION = new QName("http://schemas.xmlsoap.org/wsdl/soap12/", "operation");
   QName QNAME_MUSTUNDERSTAND = new QName("http://schemas.xmlsoap.org/soap/envelope/", "mustUnderstand");
}
