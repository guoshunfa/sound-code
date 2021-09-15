package com.sun.xml.internal.ws.addressing.v200408;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(
   name = "ProblemAction",
   namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing"
)
public class ProblemAction {
   @XmlElement(
      name = "Action",
      namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing"
   )
   private String action;
   @XmlElement(
      name = "SoapAction",
      namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing"
   )
   private String soapAction;

   public ProblemAction() {
   }

   public ProblemAction(String action) {
      this.action = action;
   }

   public ProblemAction(String action, String soapAction) {
      this.action = action;
      this.soapAction = soapAction;
   }

   public String getAction() {
      return this.action;
   }

   public String getSoapAction() {
      return this.soapAction;
   }
}
