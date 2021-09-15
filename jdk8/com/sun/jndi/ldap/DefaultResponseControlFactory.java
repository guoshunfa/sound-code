package com.sun.jndi.ldap;

import java.io.IOException;
import javax.naming.NamingException;
import javax.naming.ldap.Control;
import javax.naming.ldap.ControlFactory;
import javax.naming.ldap.PagedResultsResponseControl;
import javax.naming.ldap.SortResponseControl;

public class DefaultResponseControlFactory extends ControlFactory {
   public Control getControlInstance(Control var1) throws NamingException {
      String var2 = var1.getID();

      try {
         if (var2.equals("1.2.840.113556.1.4.474")) {
            return new SortResponseControl(var2, var1.isCritical(), var1.getEncodedValue());
         } else if (var2.equals("1.2.840.113556.1.4.319")) {
            return new PagedResultsResponseControl(var2, var1.isCritical(), var1.getEncodedValue());
         } else {
            return var2.equals("2.16.840.1.113730.3.4.7") ? new EntryChangeResponseControl(var2, var1.isCritical(), var1.getEncodedValue()) : null;
         }
      } catch (IOException var5) {
         NamingException var4 = new NamingException();
         var4.setRootCause(var5);
         throw var4;
      }
   }
}
