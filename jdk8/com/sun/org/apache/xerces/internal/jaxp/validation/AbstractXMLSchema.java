package com.sun.org.apache.xerces.internal.jaxp.validation;

import java.util.HashMap;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import javax.xml.validation.ValidatorHandler;

abstract class AbstractXMLSchema extends Schema implements XSGrammarPoolContainer {
   private final HashMap fFeatures = new HashMap();
   private final HashMap fProperties = new HashMap();

   public AbstractXMLSchema() {
   }

   public final Validator newValidator() {
      return new ValidatorImpl(this);
   }

   public final ValidatorHandler newValidatorHandler() {
      return new ValidatorHandlerImpl(this);
   }

   public final Boolean getFeature(String featureId) {
      return (Boolean)this.fFeatures.get(featureId);
   }

   public final void setFeature(String featureId, boolean state) {
      this.fFeatures.put(featureId, state ? Boolean.TRUE : Boolean.FALSE);
   }

   public final Object getProperty(String propertyId) {
      return this.fProperties.get(propertyId);
   }

   public final void setProperty(String propertyId, Object state) {
      this.fProperties.put(propertyId, state);
   }
}
