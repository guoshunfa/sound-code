package com.sun.jndi.ldap;

import com.sun.jndi.toolkit.ctx.Continuation;
import java.util.Vector;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.ldap.Control;

final class LdapNamingEnumeration extends AbstractLdapNamingEnumeration<NameClassPair> {
   private static final String defaultClassName = DirContext.class.getName();

   LdapNamingEnumeration(LdapCtx var1, LdapResult var2, Name var3, Continuation var4) throws NamingException {
      super(var1, var2, var3, var4);
   }

   protected NameClassPair createItem(String var1, Attributes var2, Vector<Control> var3) throws NamingException {
      String var5 = null;
      Attribute var4;
      if ((var4 = var2.get(Obj.JAVA_ATTRIBUTES[2])) != null) {
         var5 = (String)var4.get();
      } else {
         var5 = defaultClassName;
      }

      CompositeName var6 = new CompositeName();
      var6.add(this.getAtom(var1));
      Object var7;
      if (var3 != null) {
         var7 = new NameClassPairWithControls(var6.toString(), var5, this.homeCtx.convertControls(var3));
      } else {
         var7 = new NameClassPair(var6.toString(), var5);
      }

      ((NameClassPair)var7).setNameInNamespace(var1);
      return (NameClassPair)var7;
   }

   protected AbstractLdapNamingEnumeration<? extends NameClassPair> getReferredResults(LdapReferralContext var1) throws NamingException {
      return (AbstractLdapNamingEnumeration)var1.list(this.listArg);
   }
}
