package com.sun.jndi.ldap;

import com.sun.jndi.toolkit.ctx.Continuation;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Vector;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.ldap.Control;
import javax.naming.spi.DirectoryManager;

final class LdapBindingEnumeration extends AbstractLdapNamingEnumeration<Binding> {
   private final AccessControlContext acc = AccessController.getContext();

   LdapBindingEnumeration(LdapCtx var1, LdapResult var2, Name var3, Continuation var4) throws NamingException {
      super(var1, var2, var3, var4);
   }

   protected Binding createItem(String var1, final Attributes var2, Vector<Control> var3) throws NamingException {
      Object var4 = null;
      String var5 = this.getAtom(var1);
      if (var2.get(Obj.JAVA_ATTRIBUTES[2]) != null) {
         try {
            var4 = AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
               public Object run() throws NamingException {
                  return Obj.decodeObject(var2);
               }
            }, this.acc);
         } catch (PrivilegedActionException var11) {
            throw (NamingException)var11.getException();
         }
      }

      if (var4 == null) {
         var4 = new LdapCtx(this.homeCtx, var1);
      }

      CompositeName var6 = new CompositeName();
      var6.add(var5);

      try {
         var4 = DirectoryManager.getObjectInstance(var4, var6, this.homeCtx, this.homeCtx.envprops, var2);
      } catch (NamingException var9) {
         throw var9;
      } catch (Exception var10) {
         NamingException var8 = new NamingException("problem generating object using object factory");
         var8.setRootCause(var10);
         throw var8;
      }

      Object var7;
      if (var3 != null) {
         var7 = new BindingWithControls(var6.toString(), var4, this.homeCtx.convertControls(var3));
      } else {
         var7 = new Binding(var6.toString(), var4);
      }

      ((Binding)var7).setNameInNamespace(var1);
      return (Binding)var7;
   }

   protected AbstractLdapNamingEnumeration<? extends NameClassPair> getReferredResults(LdapReferralContext var1) throws NamingException {
      return (AbstractLdapNamingEnumeration)var1.listBindings(this.listArg);
   }
}
