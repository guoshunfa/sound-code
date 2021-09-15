package com.sun.jndi.ldap;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.HasControls;

class SearchResultWithControls extends SearchResult implements HasControls {
   private Control[] controls;
   private static final long serialVersionUID = 8476983938747908202L;

   public SearchResultWithControls(String var1, Object var2, Attributes var3, boolean var4, Control[] var5) {
      super(var1, var2, var3, var4);
      this.controls = var5;
   }

   public Control[] getControls() throws NamingException {
      return this.controls;
   }
}
