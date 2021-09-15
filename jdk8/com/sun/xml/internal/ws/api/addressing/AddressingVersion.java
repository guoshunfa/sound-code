package com.sun.xml.internal.ws.api.addressing;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.addressing.WsaTubeHelper;
import com.sun.xml.internal.ws.addressing.WsaTubeHelperImpl;
import com.sun.xml.internal.ws.addressing.v200408.MemberSubmissionAddressingConstants;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressingFeature;
import com.sun.xml.internal.ws.developer.MemberSubmissionEndpointReference;
import com.sun.xml.internal.ws.message.stream.OutboundStreamHeader;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.AddressingFeature;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

public enum AddressingVersion {
   W3C("http://www.w3.org/2005/08/addressing", "wsa", "<EndpointReference xmlns=\"http://www.w3.org/2005/08/addressing\">\n    <Address>http://www.w3.org/2005/08/addressing/anonymous</Address>\n</EndpointReference>", "http://www.w3.org/2006/05/addressing/wsdl", "http://www.w3.org/2006/05/addressing/wsdl", "http://www.w3.org/2005/08/addressing/anonymous", "http://www.w3.org/2005/08/addressing/none", new AddressingVersion.EPR(W3CEndpointReference.class, "Address", "ServiceName", "EndpointName", "InterfaceName", new QName("http://www.w3.org/2005/08/addressing", "Metadata", "wsa"), "ReferenceParameters", (String)null)) {
      String getActionMismatchLocalName() {
         return "ActionMismatch";
      }

      public boolean isReferenceParameter(String localName) {
         return localName.equals("ReferenceParameters");
      }

      public WsaTubeHelper getWsaHelper(WSDLPort wsdlPort, SEIModel seiModel, WSBinding binding) {
         return new WsaTubeHelperImpl(wsdlPort, seiModel, binding);
      }

      String getMapRequiredLocalName() {
         return "MessageAddressingHeaderRequired";
      }

      public String getMapRequiredText() {
         return "A required header representing a Message Addressing Property is not present";
      }

      String getInvalidAddressLocalName() {
         return "InvalidAddress";
      }

      String getInvalidMapLocalName() {
         return "InvalidAddressingHeader";
      }

      public String getInvalidMapText() {
         return "A header representing a Message Addressing Property is not valid and the message cannot be processed";
      }

      String getInvalidCardinalityLocalName() {
         return "InvalidCardinality";
      }

      Header createReferenceParameterHeader(XMLStreamBuffer mark, String nsUri, String localName) {
         return new OutboundReferenceParameterHeader(mark, nsUri, localName);
      }

      String getIsReferenceParameterLocalName() {
         return "IsReferenceParameter";
      }

      String getWsdlAnonymousLocalName() {
         return "Anonymous";
      }

      public String getPrefix() {
         return "wsa";
      }

      public String getWsdlPrefix() {
         return "wsaw";
      }

      public Class<? extends WebServiceFeature> getFeatureClass() {
         return AddressingFeature.class;
      }
   },
   MEMBER("http://schemas.xmlsoap.org/ws/2004/08/addressing", "wsa", "<EndpointReference xmlns=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">\n    <Address>http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous</Address>\n</EndpointReference>", "http://schemas.xmlsoap.org/ws/2004/08/addressing", "http://schemas.xmlsoap.org/ws/2004/08/addressing/policy", "http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous", "", new AddressingVersion.EPR(MemberSubmissionEndpointReference.class, "Address", "ServiceName", "PortName", "PortType", MemberSubmissionAddressingConstants.MEX_METADATA, "ReferenceParameters", "ReferenceProperties")) {
      String getActionMismatchLocalName() {
         return "InvalidMessageInformationHeader";
      }

      public boolean isReferenceParameter(String localName) {
         return localName.equals("ReferenceParameters") || localName.equals("ReferenceProperties");
      }

      public WsaTubeHelper getWsaHelper(WSDLPort wsdlPort, SEIModel seiModel, WSBinding binding) {
         return new com.sun.xml.internal.ws.addressing.v200408.WsaTubeHelperImpl(wsdlPort, seiModel, binding);
      }

      String getMapRequiredLocalName() {
         return "MessageInformationHeaderRequired";
      }

      public String getMapRequiredText() {
         return "A required message information header, To, MessageID, or Action, is not present.";
      }

      String getInvalidAddressLocalName() {
         return this.getInvalidMapLocalName();
      }

      String getInvalidMapLocalName() {
         return "InvalidMessageInformationHeader";
      }

      public String getInvalidMapText() {
         return "A message information header is not valid and the message cannot be processed.";
      }

      String getInvalidCardinalityLocalName() {
         return this.getInvalidMapLocalName();
      }

      Header createReferenceParameterHeader(XMLStreamBuffer mark, String nsUri, String localName) {
         return new OutboundStreamHeader(mark, nsUri, localName);
      }

      String getIsReferenceParameterLocalName() {
         return "";
      }

      String getWsdlAnonymousLocalName() {
         return "";
      }

      public String getPrefix() {
         return "wsa";
      }

      public String getWsdlPrefix() {
         return "wsaw";
      }

      public Class<? extends WebServiceFeature> getFeatureClass() {
         return MemberSubmissionAddressingFeature.class;
      }
   };

   public final String nsUri;
   public final String wsdlNsUri;
   public final AddressingVersion.EPR eprType;
   public final String policyNsUri;
   @NotNull
   public final String anonymousUri;
   @NotNull
   public final String noneUri;
   public final WSEndpointReference anonymousEpr;
   public final QName toTag;
   public final QName fromTag;
   public final QName replyToTag;
   public final QName faultToTag;
   public final QName actionTag;
   public final QName messageIDTag;
   public final QName relatesToTag;
   public final QName mapRequiredTag;
   public final QName actionMismatchTag;
   public final QName actionNotSupportedTag;
   public final String actionNotSupportedText;
   public final QName invalidMapTag;
   public final QName invalidCardinalityTag;
   public final QName invalidAddressTag;
   public final QName problemHeaderQNameTag;
   public final QName problemActionTag;
   public final QName faultDetailTag;
   public final QName fault_missingAddressInEpr;
   public final QName wsdlActionTag;
   public final QName wsdlExtensionTag;
   public final QName wsdlAnonymousTag;
   public final QName isReferenceParameterTag;
   private static final String EXTENDED_FAULT_NAMESPACE = "http://jax-ws.dev.java.net/addressing/fault";
   public static final String UNSET_OUTPUT_ACTION = "http://jax-ws.dev.java.net/addressing/output-action-not-set";
   public static final String UNSET_INPUT_ACTION = "http://jax-ws.dev.java.net/addressing/input-action-not-set";
   public static final QName fault_duplicateAddressInEpr = new QName("http://jax-ws.dev.java.net/addressing/fault", "DuplicateAddressInEpr", "wsa");

   private AddressingVersion(String nsUri, String prefix, String anonymousEprString, String wsdlNsUri, String policyNsUri, String anonymousUri, String noneUri, AddressingVersion.EPR eprType) {
      this.nsUri = nsUri;
      this.wsdlNsUri = wsdlNsUri;
      this.policyNsUri = policyNsUri;
      this.anonymousUri = anonymousUri;
      this.noneUri = noneUri;
      this.toTag = new QName(nsUri, "To", prefix);
      this.fromTag = new QName(nsUri, "From", prefix);
      this.replyToTag = new QName(nsUri, "ReplyTo", prefix);
      this.faultToTag = new QName(nsUri, "FaultTo", prefix);
      this.actionTag = new QName(nsUri, "Action", prefix);
      this.messageIDTag = new QName(nsUri, "MessageID", prefix);
      this.relatesToTag = new QName(nsUri, "RelatesTo", prefix);
      this.mapRequiredTag = new QName(nsUri, this.getMapRequiredLocalName(), prefix);
      this.actionMismatchTag = new QName(nsUri, this.getActionMismatchLocalName(), prefix);
      this.actionNotSupportedTag = new QName(nsUri, "ActionNotSupported", prefix);
      this.actionNotSupportedText = "The \"%s\" cannot be processed at the receiver";
      this.invalidMapTag = new QName(nsUri, this.getInvalidMapLocalName(), prefix);
      this.invalidAddressTag = new QName(nsUri, this.getInvalidAddressLocalName(), prefix);
      this.invalidCardinalityTag = new QName(nsUri, this.getInvalidCardinalityLocalName(), prefix);
      this.faultDetailTag = new QName(nsUri, "FaultDetail", prefix);
      this.problemHeaderQNameTag = new QName(nsUri, "ProblemHeaderQName", prefix);
      this.problemActionTag = new QName(nsUri, "ProblemAction", prefix);
      this.fault_missingAddressInEpr = new QName(nsUri, "MissingAddressInEPR", prefix);
      this.isReferenceParameterTag = new QName(nsUri, this.getIsReferenceParameterLocalName(), prefix);
      this.wsdlActionTag = new QName(wsdlNsUri, "Action", prefix);
      this.wsdlExtensionTag = new QName(wsdlNsUri, "UsingAddressing", prefix);
      this.wsdlAnonymousTag = new QName(wsdlNsUri, this.getWsdlAnonymousLocalName(), prefix);

      try {
         this.anonymousEpr = new WSEndpointReference(new ByteArrayInputStream(anonymousEprString.getBytes("UTF-8")), this);
      } catch (XMLStreamException var12) {
         throw new Error(var12);
      } catch (UnsupportedEncodingException var13) {
         throw new Error(var13);
      }

      this.eprType = eprType;
   }

   abstract String getActionMismatchLocalName();

   public static AddressingVersion fromNsUri(String nsUri) {
      if (nsUri.equals(W3C.nsUri)) {
         return W3C;
      } else {
         return nsUri.equals(MEMBER.nsUri) ? MEMBER : null;
      }
   }

   @Nullable
   public static AddressingVersion fromBinding(WSBinding binding) {
      if (binding.isFeatureEnabled(AddressingFeature.class)) {
         return W3C;
      } else {
         return binding.isFeatureEnabled(MemberSubmissionAddressingFeature.class) ? MEMBER : null;
      }
   }

   public static AddressingVersion fromPort(WSDLPort port) {
      if (port == null) {
         return null;
      } else {
         WebServiceFeature wsf = port.getFeature(AddressingFeature.class);
         if (wsf == null) {
            wsf = port.getFeature(MemberSubmissionAddressingFeature.class);
         }

         return wsf == null ? null : fromFeature(wsf);
      }
   }

   /** @deprecated */
   public String getNsUri() {
      return this.nsUri;
   }

   public abstract boolean isReferenceParameter(String var1);

   /** @deprecated */
   public abstract WsaTubeHelper getWsaHelper(WSDLPort var1, SEIModel var2, WSBinding var3);

   /** @deprecated */
   public final String getNoneUri() {
      return this.noneUri;
   }

   /** @deprecated */
   public final String getAnonymousUri() {
      return this.anonymousUri;
   }

   public String getDefaultFaultAction() {
      return this.nsUri + "/fault";
   }

   abstract String getMapRequiredLocalName();

   public abstract String getMapRequiredText();

   abstract String getInvalidAddressLocalName();

   abstract String getInvalidMapLocalName();

   public abstract String getInvalidMapText();

   abstract String getInvalidCardinalityLocalName();

   abstract String getWsdlAnonymousLocalName();

   public abstract String getPrefix();

   public abstract String getWsdlPrefix();

   public abstract Class<? extends WebServiceFeature> getFeatureClass();

   abstract Header createReferenceParameterHeader(XMLStreamBuffer var1, String var2, String var3);

   abstract String getIsReferenceParameterLocalName();

   public static AddressingVersion fromFeature(WebServiceFeature af) {
      if (af.getID().equals("http://www.w3.org/2005/08/addressing/module")) {
         return W3C;
      } else {
         return af.getID().equals("http://java.sun.com/xml/ns/jaxws/2004/08/addressing") ? MEMBER : null;
      }
   }

   @NotNull
   public static WebServiceFeature getFeature(String nsUri, boolean enabled, boolean required) {
      if (nsUri.equals(W3C.policyNsUri)) {
         return new AddressingFeature(enabled, required);
      } else if (nsUri.equals(MEMBER.policyNsUri)) {
         return new MemberSubmissionAddressingFeature(enabled, required);
      } else {
         throw new WebServiceException("Unsupported namespace URI: " + nsUri);
      }
   }

   @NotNull
   public static AddressingVersion fromSpecClass(Class<? extends EndpointReference> eprClass) {
      if (eprClass == W3CEndpointReference.class) {
         return W3C;
      } else if (eprClass == MemberSubmissionEndpointReference.class) {
         return MEMBER;
      } else {
         throw new WebServiceException("Unsupported EPR type: " + eprClass);
      }
   }

   public static boolean isRequired(WebServiceFeature wsf) {
      if (wsf.getID().equals("http://www.w3.org/2005/08/addressing/module")) {
         return ((AddressingFeature)wsf).isRequired();
      } else if (wsf.getID().equals("http://java.sun.com/xml/ns/jaxws/2004/08/addressing")) {
         return ((MemberSubmissionAddressingFeature)wsf).isRequired();
      } else {
         throw new WebServiceException("WebServiceFeature not an Addressing feature: " + wsf.getID());
      }
   }

   public static boolean isRequired(WSBinding binding) {
      AddressingFeature af = (AddressingFeature)binding.getFeature(AddressingFeature.class);
      if (af != null) {
         return af.isRequired();
      } else {
         MemberSubmissionAddressingFeature msaf = (MemberSubmissionAddressingFeature)binding.getFeature(MemberSubmissionAddressingFeature.class);
         return msaf != null ? msaf.isRequired() : false;
      }
   }

   public static boolean isEnabled(WSBinding binding) {
      return binding.isFeatureEnabled(MemberSubmissionAddressingFeature.class) || binding.isFeatureEnabled(AddressingFeature.class);
   }

   // $FF: synthetic method
   AddressingVersion(String x2, String x3, String x4, String x5, String x6, String x7, String x8, AddressingVersion.EPR x9, Object x10) {
      this(x2, x3, x4, x5, x6, x7, x8, x9);
   }

   public static final class EPR {
      public final Class<? extends EndpointReference> eprClass;
      public final String address;
      public final String serviceName;
      public final String portName;
      public final String portTypeName;
      public final String referenceParameters;
      public final QName wsdlMetadata;
      public final String referenceProperties;

      public EPR(Class<? extends EndpointReference> eprClass, String address, String serviceName, String portName, String portTypeName, QName wsdlMetadata, String referenceParameters, String referenceProperties) {
         this.eprClass = eprClass;
         this.address = address;
         this.serviceName = serviceName;
         this.portName = portName;
         this.portTypeName = portTypeName;
         this.referenceParameters = referenceParameters;
         this.referenceProperties = referenceProperties;
         this.wsdlMetadata = wsdlMetadata;
      }
   }
}
