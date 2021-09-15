package com.sun.jndi.ldap;

import com.sun.jndi.toolkit.ctx.Continuation;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Vector;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.spi.DirectoryManager;

final class LdapSearchEnumeration extends AbstractLdapNamingEnumeration<SearchResult> {
   private Name startName;
   private LdapCtx.SearchArgs searchArgs = null;
   private final AccessControlContext acc = AccessController.getContext();

   LdapSearchEnumeration(LdapCtx var1, LdapResult var2, String var3, LdapCtx.SearchArgs var4, Continuation var5) throws NamingException {
      super(var1, var2, var4.name, var5);
      this.startName = new javax.naming.ldap.LdapName(var3);
      this.searchArgs = var4;
   }

   protected SearchResult createItem(String var1, final Attributes var2, Vector<Control> var3) throws NamingException {
      Object var4 = null;
      boolean var7 = true;

      String var5;
      String var6;
      try {
         javax.naming.ldap.LdapName var8 = new javax.naming.ldap.LdapName(var1);
         if (this.startName != null && var8.startsWith(this.startName)) {
            var5 = var8.getSuffix(this.startName.size()).toString();
            var6 = var8.getSuffix(this.homeCtx.currentParsedDN.size()).toString();
         } else {
            var7 = false;
            var6 = var5 = LdapURL.toUrlString(this.homeCtx.hostname, this.homeCtx.port_number, var1, this.homeCtx.hasLdapsScheme);
         }
      } catch (NamingException var16) {
         var7 = false;
         var6 = var5 = LdapURL.toUrlString(this.homeCtx.hostname, this.homeCtx.port_number, var1, this.homeCtx.hasLdapsScheme);
      }

      CompositeName var17 = new CompositeName();
      if (!var5.equals("")) {
         var17.add(var5);
      }

      CompositeName var9 = new CompositeName();
      if (!var6.equals("")) {
         var9.add(var6);
      }

      this.homeCtx.setParents(var2, var9);
      if (this.searchArgs.cons.getReturningObjFlag()) {
         if (var2.get(Obj.JAVA_ATTRIBUTES[2]) != null) {
            try {
               var4 = AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                  public Object run() throws NamingException {
                     return Obj.decodeObject(var2);
                  }
               }, this.acc);
            } catch (PrivilegedActionException var15) {
               throw (NamingException)var15.getException();
            }
         }

         if (var4 == null) {
            var4 = new LdapCtx(this.homeCtx, var1);
         }

         try {
            var4 = DirectoryManager.getObjectInstance(var4, var9, var7 ? this.homeCtx : null, this.homeCtx.envprops, var2);
         } catch (NamingException var13) {
            throw var13;
         } catch (Exception var14) {
            NamingException var11 = new NamingException("problem generating object using object factory");
            var11.setRootCause(var14);
            throw var11;
         }

         String[] var10;
         if ((var10 = this.searchArgs.reqAttrs) != null) {
            BasicAttributes var19 = new BasicAttributes(true);

            int var12;
            for(var12 = 0; var12 < var10.length; ++var12) {
               var19.put(var10[var12], (Object)null);
            }

            for(var12 = 0; var12 < Obj.JAVA_ATTRIBUTES.length; ++var12) {
               if (var19.get(Obj.JAVA_ATTRIBUTES[var12]) == null) {
                  var2.remove(Obj.JAVA_ATTRIBUTES[var12]);
               }
            }
         }
      }

      Object var18;
      if (var3 != null) {
         var18 = new SearchResultWithControls(var7 ? var17.toString() : var5, var4, var2, var7, this.homeCtx.convertControls(var3));
      } else {
         var18 = new SearchResult(var7 ? var17.toString() : var5, var4, var2, var7);
      }

      ((SearchResult)var18).setNameInNamespace(var1);
      return (SearchResult)var18;
   }

   public void appendUnprocessedReferrals(LdapReferralException var1) {
      this.startName = null;
      super.appendUnprocessedReferrals(var1);
   }

   protected AbstractLdapNamingEnumeration<? extends NameClassPair> getReferredResults(LdapReferralContext var1) throws NamingException {
      return (AbstractLdapNamingEnumeration)var1.search(this.searchArgs.name, this.searchArgs.filter, this.searchArgs.cons);
   }

   protected void update(AbstractLdapNamingEnumeration<? extends NameClassPair> var1) {
      super.update(var1);
      LdapSearchEnumeration var2 = (LdapSearchEnumeration)var1;
      this.startName = var2.startName;
   }

   void setStartName(Name var1) {
      this.startName = var1;
   }
}
