package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class BadServerDefinition extends UserException {
   public String reason = null;

   public BadServerDefinition() {
      super(BadServerDefinitionHelper.id());
   }

   public BadServerDefinition(String var1) {
      super(BadServerDefinitionHelper.id());
      this.reason = var1;
   }

   public BadServerDefinition(String var1, String var2) {
      super(BadServerDefinitionHelper.id() + "  " + var1);
      this.reason = var2;
   }
}
