package com.sun.jndi.ldap;

import javax.naming.NameClassPair;
import javax.naming.NamingException;
import javax.naming.ldap.Control;
import javax.naming.ldap.HasControls;

class NameClassPairWithControls extends NameClassPair implements HasControls {
   private Control[] controls;
   private static final long serialVersionUID = 2010738921219112944L;

   public NameClassPairWithControls(String var1, String var2, Control[] var3) {
      super(var1, var2);
      this.controls = var3;
   }

   public Control[] getControls() throws NamingException {
      return this.controls;
   }
}
