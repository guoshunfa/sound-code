package com.sun.xml.internal.ws.api.model.soap;

import com.sun.xml.internal.ws.api.SOAPVersion;

public abstract class SOAPBinding {
   protected javax.jws.soap.SOAPBinding.Use use;
   protected javax.jws.soap.SOAPBinding.Style style;
   protected SOAPVersion soapVersion;
   protected String soapAction;

   public SOAPBinding() {
      this.use = javax.jws.soap.SOAPBinding.Use.LITERAL;
      this.style = javax.jws.soap.SOAPBinding.Style.DOCUMENT;
      this.soapVersion = SOAPVersion.SOAP_11;
      this.soapAction = "";
   }

   public javax.jws.soap.SOAPBinding.Use getUse() {
      return this.use;
   }

   public javax.jws.soap.SOAPBinding.Style getStyle() {
      return this.style;
   }

   public SOAPVersion getSOAPVersion() {
      return this.soapVersion;
   }

   public boolean isDocLit() {
      return this.style == javax.jws.soap.SOAPBinding.Style.DOCUMENT && this.use == javax.jws.soap.SOAPBinding.Use.LITERAL;
   }

   public boolean isRpcLit() {
      return this.style == javax.jws.soap.SOAPBinding.Style.RPC && this.use == javax.jws.soap.SOAPBinding.Use.LITERAL;
   }

   public String getSOAPAction() {
      return this.soapAction;
   }
}
