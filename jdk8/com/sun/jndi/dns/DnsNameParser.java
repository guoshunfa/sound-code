package com.sun.jndi.dns;

import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;

class DnsNameParser implements NameParser {
   public Name parse(String var1) throws NamingException {
      return new DnsName(var1);
   }

   public boolean equals(Object var1) {
      return var1 instanceof DnsNameParser;
   }

   public int hashCode() {
      return DnsNameParser.class.hashCode() + 1;
   }
}
