package com.sun.jndi.ldap;

import java.util.Vector;
import javax.naming.directory.Attributes;
import javax.naming.ldap.Control;

final class LdapEntry {
   String DN;
   Attributes attributes;
   Vector<Control> respCtls = null;

   LdapEntry(String var1, Attributes var2) {
      this.DN = var1;
      this.attributes = var2;
   }

   LdapEntry(String var1, Attributes var2, Vector<Control> var3) {
      this.DN = var1;
      this.attributes = var2;
      this.respCtls = var3;
   }
}
