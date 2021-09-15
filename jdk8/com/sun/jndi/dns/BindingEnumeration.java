package com.sun.jndi.dns;

import java.util.Hashtable;
import java.util.NoSuchElementException;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.spi.DirectoryManager;

final class BindingEnumeration extends BaseNameClassPairEnumeration<Binding> implements NamingEnumeration<Binding> {
   BindingEnumeration(DnsContext var1, Hashtable<String, NameNode> var2) {
      super(var1, var2);
   }

   public Binding next() throws NamingException {
      if (!this.hasMore()) {
         throw new NoSuchElementException();
      } else {
         NameNode var1 = (NameNode)this.nodes.nextElement();
         String var2 = var1.getLabel();
         Name var3 = (new DnsName()).add(var2);
         String var4 = var3.toString();
         Name var5 = (new CompositeName()).add(var4);
         String var6 = var5.toString();
         DnsName var7 = this.ctx.fullyQualify(var3);
         DnsContext var8 = new DnsContext(this.ctx, var7);

         try {
            Object var9 = DirectoryManager.getObjectInstance(var8, var5, this.ctx, var8.environment, (Attributes)null);
            Binding var12 = new Binding(var6, var9);
            var12.setNameInNamespace(this.ctx.fullyQualify(var5).toString());
            return var12;
         } catch (Exception var11) {
            NamingException var10 = new NamingException("Problem generating object using object factory");
            var10.setRootCause(var11);
            throw var10;
         }
      }
   }
}
