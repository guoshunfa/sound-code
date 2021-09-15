package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.PolicyException;
import java.util.Collection;

public abstract class PolicyModelMarshaller {
   private static final PolicyModelMarshaller defaultXmlMarshaller = new XmlPolicyModelMarshaller(false);
   private static final PolicyModelMarshaller invisibleAssertionXmlMarshaller = new XmlPolicyModelMarshaller(true);

   PolicyModelMarshaller() {
   }

   public abstract void marshal(PolicySourceModel var1, Object var2) throws PolicyException;

   public abstract void marshal(Collection<PolicySourceModel> var1, Object var2) throws PolicyException;

   public static PolicyModelMarshaller getXmlMarshaller(boolean marshallInvisible) {
      return marshallInvisible ? invisibleAssertionXmlMarshaller : defaultXmlMarshaller;
   }
}
