package com.sun.xml.internal.ws.api.databinding;

import com.oracle.webservices.internal.api.databinding.WSDLResolver;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGeneratorExtension;

public class WSDLGenInfo {
   WSDLResolver wsdlResolver;
   Container container;
   boolean inlineSchemas;
   boolean secureXmlProcessingDisabled;
   WSDLGeneratorExtension[] extensions;

   public WSDLResolver getWsdlResolver() {
      return this.wsdlResolver;
   }

   public void setWsdlResolver(WSDLResolver wsdlResolver) {
      this.wsdlResolver = wsdlResolver;
   }

   public Container getContainer() {
      return this.container;
   }

   public void setContainer(Container container) {
      this.container = container;
   }

   public boolean isInlineSchemas() {
      return this.inlineSchemas;
   }

   public void setInlineSchemas(boolean inlineSchemas) {
      this.inlineSchemas = inlineSchemas;
   }

   public WSDLGeneratorExtension[] getExtensions() {
      return this.extensions == null ? new WSDLGeneratorExtension[0] : this.extensions;
   }

   public void setExtensions(WSDLGeneratorExtension[] extensions) {
      this.extensions = extensions;
   }

   public void setSecureXmlProcessingDisabled(boolean secureXmlProcessingDisabled) {
      this.secureXmlProcessingDisabled = secureXmlProcessingDisabled;
   }

   public boolean isSecureXmlProcessingDisabled() {
      return this.secureXmlProcessingDisabled;
   }
}
