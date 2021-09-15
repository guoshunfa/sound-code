package javax.xml.soap;

import javax.xml.namespace.QName;

public interface SOAPConstants {
   String DYNAMIC_SOAP_PROTOCOL = "Dynamic Protocol";
   String SOAP_1_1_PROTOCOL = "SOAP 1.1 Protocol";
   String SOAP_1_2_PROTOCOL = "SOAP 1.2 Protocol";
   String DEFAULT_SOAP_PROTOCOL = "SOAP 1.1 Protocol";
   String URI_NS_SOAP_1_1_ENVELOPE = "http://schemas.xmlsoap.org/soap/envelope/";
   String URI_NS_SOAP_1_2_ENVELOPE = "http://www.w3.org/2003/05/soap-envelope";
   String URI_NS_SOAP_ENVELOPE = "http://schemas.xmlsoap.org/soap/envelope/";
   String URI_NS_SOAP_ENCODING = "http://schemas.xmlsoap.org/soap/encoding/";
   String URI_NS_SOAP_1_2_ENCODING = "http://www.w3.org/2003/05/soap-encoding";
   String SOAP_1_1_CONTENT_TYPE = "text/xml";
   String SOAP_1_2_CONTENT_TYPE = "application/soap+xml";
   String URI_SOAP_ACTOR_NEXT = "http://schemas.xmlsoap.org/soap/actor/next";
   String URI_SOAP_1_2_ROLE_NEXT = "http://www.w3.org/2003/05/soap-envelope/role/next";
   String URI_SOAP_1_2_ROLE_NONE = "http://www.w3.org/2003/05/soap-envelope/role/none";
   String URI_SOAP_1_2_ROLE_ULTIMATE_RECEIVER = "http://www.w3.org/2003/05/soap-envelope/role/ultimateReceiver";
   String SOAP_ENV_PREFIX = "env";
   QName SOAP_VERSIONMISMATCH_FAULT = new QName("http://www.w3.org/2003/05/soap-envelope", "VersionMismatch", "env");
   QName SOAP_MUSTUNDERSTAND_FAULT = new QName("http://www.w3.org/2003/05/soap-envelope", "MustUnderstand", "env");
   QName SOAP_DATAENCODINGUNKNOWN_FAULT = new QName("http://www.w3.org/2003/05/soap-envelope", "DataEncodingUnknown", "env");
   QName SOAP_SENDER_FAULT = new QName("http://www.w3.org/2003/05/soap-envelope", "Sender", "env");
   QName SOAP_RECEIVER_FAULT = new QName("http://www.w3.org/2003/05/soap-envelope", "Receiver", "env");
}
