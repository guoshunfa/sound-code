package com.sun.jndi.ldap;

import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;

class LdapNameParser implements NameParser {
   public LdapNameParser() {
   }

   public Name parse(String var1) throws NamingException {
      return new javax.naming.ldap.LdapName(var1);
   }
}
