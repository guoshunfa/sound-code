package com.sun.jndi.dns;

import java.util.Hashtable;
import java.util.NoSuchElementException;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

final class NameClassPairEnumeration extends BaseNameClassPairEnumeration<NameClassPair> implements NamingEnumeration<NameClassPair> {
   NameClassPairEnumeration(DnsContext var1, Hashtable<String, NameNode> var2) {
      super(var1, var2);
   }

   public NameClassPair next() throws NamingException {
      if (!this.hasMore()) {
         throw new NoSuchElementException();
      } else {
         NameNode var1 = (NameNode)this.nodes.nextElement();
         String var2 = !var1.isZoneCut() && var1.getChildren() == null ? "java.lang.Object" : "javax.naming.directory.DirContext";
         String var3 = var1.getLabel();
         Name var4 = (new DnsName()).add(var3);
         Name var5 = (new CompositeName()).add(var4.toString());
         NameClassPair var6 = new NameClassPair(var5.toString(), var2);
         var6.setNameInNamespace(this.ctx.fullyQualify(var5).toString());
         return var6;
      }
   }
}
