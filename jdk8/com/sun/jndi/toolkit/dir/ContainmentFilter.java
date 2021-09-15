package com.sun.jndi.toolkit.dir;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

public class ContainmentFilter implements AttrFilter {
   private Attributes matchingAttrs;

   public ContainmentFilter(Attributes var1) {
      this.matchingAttrs = var1;
   }

   public boolean check(Attributes var1) throws NamingException {
      return this.matchingAttrs == null || this.matchingAttrs.size() == 0 || contains(var1, this.matchingAttrs);
   }

   public static boolean contains(Attributes var0, Attributes var1) throws NamingException {
      if (var1 == null) {
         return true;
      } else {
         NamingEnumeration var2 = var1.getAll();

         while(var2.hasMore()) {
            if (var0 == null) {
               return false;
            }

            Attribute var3 = (Attribute)var2.next();
            Attribute var4 = var0.get(var3.getID());
            if (var4 == null) {
               return false;
            }

            if (var3.size() > 0) {
               NamingEnumeration var5 = var3.getAll();

               while(var5.hasMore()) {
                  if (!var4.contains(var5.next())) {
                     return false;
                  }
               }
            }
         }

         return true;
      }
   }
}
