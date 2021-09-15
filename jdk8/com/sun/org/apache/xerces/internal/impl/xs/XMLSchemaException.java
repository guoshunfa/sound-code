package com.sun.org.apache.xerces.internal.impl.xs;

public class XMLSchemaException extends Exception {
   static final long serialVersionUID = -9096984648537046218L;
   String key;
   Object[] args;

   public XMLSchemaException(String key, Object[] args) {
      this.key = key;
      this.args = args;
   }

   public String getKey() {
      return this.key;
   }

   public Object[] getArgs() {
      return this.args;
   }
}
