package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.PolicyException;

public abstract class PolicyModelUnmarshaller {
   private static final PolicyModelUnmarshaller xmlUnmarshaller = new XmlPolicyModelUnmarshaller();

   PolicyModelUnmarshaller() {
   }

   public abstract PolicySourceModel unmarshalModel(Object var1) throws PolicyException;

   public static PolicyModelUnmarshaller getXmlUnmarshaller() {
      return xmlUnmarshaller;
   }
}
