package com.sun.xml.internal.ws.developer;

import com.sun.org.glassfish.gmbal.ManagedAttribute;
import com.sun.org.glassfish.gmbal.ManagedData;
import com.sun.xml.internal.ws.api.FeatureConstructor;
import javax.xml.ws.WebServiceFeature;

@ManagedData
public class MemberSubmissionAddressingFeature extends WebServiceFeature {
   public static final String ID = "http://java.sun.com/xml/ns/jaxws/2004/08/addressing";
   public static final String IS_REQUIRED = "ADDRESSING_IS_REQUIRED";
   private boolean required;
   private MemberSubmissionAddressing.Validation validation;

   public MemberSubmissionAddressingFeature() {
      this.validation = MemberSubmissionAddressing.Validation.LAX;
      this.enabled = true;
   }

   public MemberSubmissionAddressingFeature(boolean enabled) {
      this.validation = MemberSubmissionAddressing.Validation.LAX;
      this.enabled = enabled;
   }

   public MemberSubmissionAddressingFeature(boolean enabled, boolean required) {
      this.validation = MemberSubmissionAddressing.Validation.LAX;
      this.enabled = enabled;
      this.required = required;
   }

   @FeatureConstructor({"enabled", "required", "validation"})
   public MemberSubmissionAddressingFeature(boolean enabled, boolean required, MemberSubmissionAddressing.Validation validation) {
      this.validation = MemberSubmissionAddressing.Validation.LAX;
      this.enabled = enabled;
      this.required = required;
      this.validation = validation;
   }

   @ManagedAttribute
   public String getID() {
      return "http://java.sun.com/xml/ns/jaxws/2004/08/addressing";
   }

   @ManagedAttribute
   public boolean isRequired() {
      return this.required;
   }

   public void setRequired(boolean required) {
      this.required = required;
   }

   public void setValidation(MemberSubmissionAddressing.Validation validation) {
      this.validation = validation;
   }

   @ManagedAttribute
   public MemberSubmissionAddressing.Validation getValidation() {
      return this.validation;
   }
}
