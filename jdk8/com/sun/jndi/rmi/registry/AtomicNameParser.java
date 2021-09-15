package com.sun.jndi.rmi.registry;

import java.util.Properties;
import javax.naming.CompoundName;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;

class AtomicNameParser implements NameParser {
   private static final Properties syntax = new Properties();

   public Name parse(String var1) throws NamingException {
      return new CompoundName(var1, syntax);
   }
}
