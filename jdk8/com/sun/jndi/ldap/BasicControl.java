package com.sun.jndi.ldap;

import javax.naming.ldap.Control;

public class BasicControl implements Control {
   protected String id;
   protected boolean criticality = false;
   protected byte[] value = null;
   private static final long serialVersionUID = -5914033725246428413L;

   public BasicControl(String var1) {
      this.id = var1;
   }

   public BasicControl(String var1, boolean var2, byte[] var3) {
      this.id = var1;
      this.criticality = var2;
      if (var3 != null) {
         this.value = (byte[])var3.clone();
      }

   }

   public String getID() {
      return this.id;
   }

   public boolean isCritical() {
      return this.criticality;
   }

   public byte[] getEncodedValue() {
      return this.value == null ? null : (byte[])this.value.clone();
   }
}
