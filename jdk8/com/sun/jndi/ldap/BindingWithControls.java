package com.sun.jndi.ldap;

import javax.naming.Binding;
import javax.naming.NamingException;
import javax.naming.ldap.Control;
import javax.naming.ldap.HasControls;

class BindingWithControls extends Binding implements HasControls {
   private Control[] controls;
   private static final long serialVersionUID = 9117274533692320040L;

   public BindingWithControls(String var1, Object var2, Control[] var3) {
      super(var1, var2);
      this.controls = var3;
   }

   public Control[] getControls() throws NamingException {
      return this.controls;
   }
}
