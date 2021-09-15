package org.omg.CORBA.portable;

public class ApplicationException extends Exception {
   private String id;
   private InputStream ins;

   public ApplicationException(String var1, InputStream var2) {
      this.id = var1;
      this.ins = var2;
   }

   public String getId() {
      return this.id;
   }

   public InputStream getInputStream() {
      return this.ins;
   }
}
