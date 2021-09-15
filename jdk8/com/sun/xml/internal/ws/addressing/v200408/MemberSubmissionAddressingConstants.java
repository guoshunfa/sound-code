package com.sun.xml.internal.ws.addressing.v200408;

import javax.xml.namespace.QName;

public interface MemberSubmissionAddressingConstants {
   String WSA_NAMESPACE_NAME = "http://schemas.xmlsoap.org/ws/2004/08/addressing";
   String WSA_NAMESPACE_WSDL_NAME = "http://schemas.xmlsoap.org/ws/2004/08/addressing";
   String WSA_NAMESPACE_POLICY_NAME = "http://schemas.xmlsoap.org/ws/2004/08/addressing/policy";
   QName WSA_ACTION_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "Action");
   String WSA_SERVICENAME_NAME = "ServiceName";
   String WSA_PORTTYPE_NAME = "PortType";
   String WSA_PORTNAME_NAME = "PortName";
   String WSA_ADDRESS_NAME = "Address";
   QName WSA_ADDRESS_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "Address");
   String WSA_EPR_NAME = "EndpointReference";
   QName WSA_EPR_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "EndpointReference");
   String WSA_ANONYMOUS_ADDRESS = "http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous";
   String WSA_NONE_ADDRESS = "";
   String WSA_DEFAULT_FAULT_ACTION = "http://schemas.xmlsoap.org/ws/2004/08/addressing/fault";
   QName INVALID_MAP_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "InvalidMessageInformationHeader");
   QName MAP_REQUIRED_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "MessageInformationHeaderRequired");
   QName DESTINATION_UNREACHABLE_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "DestinationUnreachable");
   QName ACTION_NOT_SUPPORTED_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "ActionNotSupported");
   QName ENDPOINT_UNAVAILABLE_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "EndpointUnavailable");
   String ACTION_NOT_SUPPORTED_TEXT = "The \"%s\" cannot be processed at the receiver.";
   String DESTINATION_UNREACHABLE_TEXT = "No route can be determined to reach the destination role defined by the WS-Addressing To.";
   String ENDPOINT_UNAVAILABLE_TEXT = "The endpoint is unable to process the message at this time.";
   String INVALID_MAP_TEXT = "A message information header is not valid and the message cannot be processed.";
   String MAP_REQUIRED_TEXT = "A required message information header, To, MessageID, or Action, is not present.";
   QName PROBLEM_ACTION_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "ProblemAction");
   QName PROBLEM_HEADER_QNAME_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "ProblemHeaderQName");
   QName FAULT_DETAIL_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "FaultDetail");
   String ANONYMOUS_EPR = "<EndpointReference xmlns=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">\n    <Address>http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous</Address>\n</EndpointReference>";
   QName MEX_METADATA = new QName("http://schemas.xmlsoap.org/ws/2004/09/mex", "Metadata", "mex");
   QName MEX_METADATA_SECTION = new QName("http://schemas.xmlsoap.org/ws/2004/09/mex", "MetadataSection", "mex");
   String MEX_METADATA_DIALECT_ATTRIBUTE = "Dialect";
   String MEX_METADATA_DIALECT_VALUE = "http://schemas.xmlsoap.org/wsdl/";
}
