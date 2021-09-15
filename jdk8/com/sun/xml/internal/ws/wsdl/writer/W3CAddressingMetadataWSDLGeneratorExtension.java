package com.sun.xml.internal.ws.wsdl.writer;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.ws.addressing.W3CAddressingMetadataConstants;
import com.sun.xml.internal.ws.addressing.WsaActionUtil;
import com.sun.xml.internal.ws.api.model.CheckedException;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGenExtnContext;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGeneratorExtension;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;
import javax.xml.namespace.QName;

public class W3CAddressingMetadataWSDLGeneratorExtension extends WSDLGeneratorExtension {
   private static final Logger LOGGER = Logger.getLogger(W3CAddressingMetadataWSDLGeneratorExtension.class.getName());

   public void start(WSDLGenExtnContext ctxt) {
      TypedXmlWriter root = ctxt.getRoot();
      root._namespace("http://www.w3.org/2007/05/addressing/metadata", "wsam");
   }

   public void addOperationInputExtension(TypedXmlWriter input, JavaMethod method) {
      input._attribute((QName)W3CAddressingMetadataConstants.WSAM_ACTION_QNAME, getInputAction(method));
   }

   public void addOperationOutputExtension(TypedXmlWriter output, JavaMethod method) {
      output._attribute((QName)W3CAddressingMetadataConstants.WSAM_ACTION_QNAME, getOutputAction(method));
   }

   public void addOperationFaultExtension(TypedXmlWriter fault, JavaMethod method, CheckedException ce) {
      fault._attribute((QName)W3CAddressingMetadataConstants.WSAM_ACTION_QNAME, getFaultAction(method, ce));
   }

   private static final String getInputAction(JavaMethod method) {
      String inputaction = ((JavaMethodImpl)method).getInputAction();
      if (inputaction.equals("")) {
         inputaction = getDefaultInputAction(method);
      }

      return inputaction;
   }

   protected static final String getDefaultInputAction(JavaMethod method) {
      String tns = method.getOwner().getTargetNamespace();
      String delim = getDelimiter(tns);
      if (tns.endsWith(delim)) {
         tns = tns.substring(0, tns.length() - 1);
      }

      String name = method.getMEP().isOneWay() ? method.getOperationName() : method.getOperationName() + "Request";
      return tns + delim + method.getOwner().getPortTypeName().getLocalPart() + delim + name;
   }

   private static final String getOutputAction(JavaMethod method) {
      String outputaction = ((JavaMethodImpl)method).getOutputAction();
      if (outputaction.equals("")) {
         outputaction = getDefaultOutputAction(method);
      }

      return outputaction;
   }

   protected static final String getDefaultOutputAction(JavaMethod method) {
      String tns = method.getOwner().getTargetNamespace();
      String delim = getDelimiter(tns);
      if (tns.endsWith(delim)) {
         tns = tns.substring(0, tns.length() - 1);
      }

      String name = method.getOperationName() + "Response";
      return tns + delim + method.getOwner().getPortTypeName().getLocalPart() + delim + name;
   }

   private static final String getDelimiter(String tns) {
      String delim = "/";

      try {
         URI uri = new URI(tns);
         if (uri.getScheme() != null && uri.getScheme().equalsIgnoreCase("urn")) {
            delim = ":";
         }
      } catch (URISyntaxException var3) {
         LOGGER.warning("TargetNamespace of WebService is not a valid URI");
      }

      return delim;
   }

   private static final String getFaultAction(JavaMethod method, CheckedException ce) {
      String faultaction = ((CheckedExceptionImpl)ce).getFaultAction();
      if (faultaction.equals("")) {
         faultaction = getDefaultFaultAction(method, ce);
      }

      return faultaction;
   }

   protected static final String getDefaultFaultAction(JavaMethod method, CheckedException ce) {
      return WsaActionUtil.getDefaultFaultAction(method, ce);
   }
}
