package com.sun.jndi.toolkit.dir;

import java.util.Properties;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;

final class HierarchicalNameParser implements NameParser {
   static final Properties mySyntax = new Properties();

   public Name parse(String var1) throws NamingException {
      return new HierarchicalName(var1, mySyntax);
   }

   static {
      mySyntax.put("jndi.syntax.direction", "left_to_right");
      mySyntax.put("jndi.syntax.separator", "/");
      mySyntax.put("jndi.syntax.ignorecase", "true");
      mySyntax.put("jndi.syntax.escape", "\\");
      mySyntax.put("jndi.syntax.beginquote", "\"");
      mySyntax.put("jndi.syntax.trimblanks", "false");
   }
}
