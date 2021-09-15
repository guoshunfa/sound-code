package com.sun.xml.internal.ws.addressing;

import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import javax.xml.namespace.QName;

public interface W3CAddressingConstants {
   String WSA_NAMESPACE_NAME = "http://www.w3.org/2005/08/addressing";
   String WSA_NAMESPACE_WSDL_NAME = "http://www.w3.org/2006/05/addressing/wsdl";
   String WSAW_SERVICENAME_NAME = "ServiceName";
   String WSAW_INTERFACENAME_NAME = "InterfaceName";
   String WSAW_ENDPOINTNAME_NAME = "EndpointName";
   String WSA_REFERENCEPROPERTIES_NAME = "ReferenceParameters";
   QName WSA_REFERENCEPROPERTIES_QNAME = new QName("http://www.w3.org/2005/08/addressing", "ReferenceParameters");
   String WSA_REFERENCEPARAMETERS_NAME = "ReferenceParameters";
   QName WSA_REFERENCEPARAMETERS_QNAME = new QName("http://www.w3.org/2005/08/addressing", "ReferenceParameters");
   String WSA_METADATA_NAME = "Metadata";
   QName WSA_METADATA_QNAME = new QName("http://www.w3.org/2005/08/addressing", "Metadata");
   String WSA_ADDRESS_NAME = "Address";
   QName WSA_ADDRESS_QNAME = new QName("http://www.w3.org/2005/08/addressing", "Address");
   String WSA_ANONYMOUS_ADDRESS = "http://www.w3.org/2005/08/addressing/anonymous";
   String WSA_NONE_ADDRESS = "http://www.w3.org/2005/08/addressing/none";
   String WSA_DEFAULT_FAULT_ACTION = "http://www.w3.org/2005/08/addressing/fault";
   String WSA_EPR_NAME = "EndpointReference";
   QName WSA_EPR_QNAME = new QName("http://www.w3.org/2005/08/addressing", "EndpointReference");
   String WSAW_USING_ADDRESSING_NAME = "UsingAddressing";
   QName WSAW_USING_ADDRESSING_QNAME = new QName("http://www.w3.org/2006/05/addressing/wsdl", "UsingAddressing");
   QName INVALID_MAP_QNAME = new QName("http://www.w3.org/2005/08/addressing", "InvalidAddressingHeader");
   QName MAP_REQUIRED_QNAME = new QName("http://www.w3.org/2005/08/addressing", "MessageAddressingHeaderRequired");
   QName DESTINATION_UNREACHABLE_QNAME = new QName("http://www.w3.org/2005/08/addressing", "DestinationUnreachable");
   QName ACTION_NOT_SUPPORTED_QNAME = new QName("http://www.w3.org/2005/08/addressing", "ActionNotSupported");
   QName ENDPOINT_UNAVAILABLE_QNAME = new QName("http://www.w3.org/2005/08/addressing", "EndpointUnavailable");
   String ACTION_NOT_SUPPORTED_TEXT = "The \"%s\" cannot be processed at the receiver";
   String DESTINATION_UNREACHABLE_TEXT = "No route can be determined to reach %s";
   String ENDPOINT_UNAVAILABLE_TEXT = "The endpoint is unable to process the message at this time";
   String INVALID_MAP_TEXT = "A header representing a Message Addressing Property is not valid and the message cannot be processed";
   String MAP_REQUIRED_TEXT = "A required header representing a Message Addressing Property is not present";
   QName PROBLEM_ACTION_QNAME = new QName("http://www.w3.org/2005/08/addressing", "ProblemAction");
   QName PROBLEM_HEADER_QNAME_QNAME = new QName("http://www.w3.org/2005/08/addressing", "ProblemHeaderQName");
   QName FAULT_DETAIL_QNAME = new QName("http://www.w3.org/2005/08/addressing", "FaultDetail");
   QName INVALID_ADDRESS_SUBCODE = new QName("http://www.w3.org/2005/08/addressing", "InvalidAddress", AddressingVersion.W3C.getPrefix());
   QName INVALID_EPR = new QName("http://www.w3.org/2005/08/addressing", "InvalidEPR", AddressingVersion.W3C.getPrefix());
   QName INVALID_CARDINALITY = new QName("http://www.w3.org/2005/08/addressing", "InvalidCardinality", AddressingVersion.W3C.getPrefix());
   QName MISSING_ADDRESS_IN_EPR = new QName("http://www.w3.org/2005/08/addressing", "MissingAddressInEPR", AddressingVersion.W3C.getPrefix());
   QName DUPLICATE_MESSAGEID = new QName("http://www.w3.org/2005/08/addressing", "DuplicateMessageID", AddressingVersion.W3C.getPrefix());
   QName ACTION_MISMATCH = new QName("http://www.w3.org/2005/08/addressing", "ActionMismatch", AddressingVersion.W3C.getPrefix());
   QName ONLY_ANONYMOUS_ADDRESS_SUPPORTED = new QName("http://www.w3.org/2005/08/addressing", "OnlyAnonymousAddressSupported", AddressingVersion.W3C.getPrefix());
   QName ONLY_NON_ANONYMOUS_ADDRESS_SUPPORTED = new QName("http://www.w3.org/2005/08/addressing", "OnlyNonAnonymousAddressSupported", AddressingVersion.W3C.getPrefix());
   String ANONYMOUS_EPR = "<EndpointReference xmlns=\"http://www.w3.org/2005/08/addressing\">\n    <Address>http://www.w3.org/2005/08/addressing/anonymous</Address>\n</EndpointReference>";
}
