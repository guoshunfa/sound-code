package com.sun.jndi.ldap;

import com.sun.jndi.url.ldap.ldapURLContextFactory;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import javax.naming.AuthenticationException;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.ldap.Control;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.ObjectFactory;

public final class LdapCtxFactory implements ObjectFactory, InitialContextFactory {
   public static final String ADDRESS_TYPE = "URL";

   public Object getObjectInstance(Object var1, Name var2, Context var3, Hashtable<?, ?> var4) throws Exception {
      if (!isLdapRef(var1)) {
         return null;
      } else {
         ldapURLContextFactory var5 = new ldapURLContextFactory();
         String[] var6 = getURLs((Reference)var1);
         return var5.getObjectInstance(var6, var2, var3, var4);
      }
   }

   public Context getInitialContext(Hashtable<?, ?> var1) throws NamingException {
      try {
         String var2 = var1 != null ? (String)var1.get("java.naming.provider.url") : null;
         if (var2 == null) {
            return new LdapCtx("", "localhost", 389, var1, false);
         } else {
            String[] var5 = LdapURL.fromList(var2);
            if (var5.length == 0) {
               throw new ConfigurationException("java.naming.provider.url property does not contain a URL");
            } else {
               return getLdapCtxInstance(var5, var1);
            }
         }
      } catch (LdapReferralException var4) {
         if (var1 != null && "throw".equals(var1.get("java.naming.referral"))) {
            throw var4;
         } else {
            Control[] var3 = var1 != null ? (Control[])((Control[])var1.get("java.naming.ldap.control.connect")) : null;
            return (LdapCtx)var4.getReferralContext(var1, var3);
         }
      }
   }

   private static boolean isLdapRef(Object var0) {
      if (!(var0 instanceof Reference)) {
         return false;
      } else {
         String var1 = LdapCtxFactory.class.getName();
         Reference var2 = (Reference)var0;
         return var1.equals(var2.getFactoryClassName());
      }
   }

   private static String[] getURLs(Reference var0) throws NamingException {
      int var1 = 0;
      String[] var2 = new String[var0.size()];
      Enumeration var3 = var0.getAll();

      while(var3.hasMoreElements()) {
         RefAddr var4 = (RefAddr)var3.nextElement();
         if (var4 instanceof StringRefAddr && var4.getType().equals("URL")) {
            var2[var1++] = (String)var4.getContent();
         }
      }

      if (var1 == 0) {
         throw new ConfigurationException("Reference contains no valid addresses");
      } else if (var1 == var0.size()) {
         return var2;
      } else {
         String[] var5 = new String[var1];
         System.arraycopy(var2, 0, var5, 0, var1);
         return var5;
      }
   }

   public static DirContext getLdapCtxInstance(Object var0, Hashtable<?, ?> var1) throws NamingException {
      if (var0 instanceof String) {
         return getUsingURL((String)var0, var1);
      } else if (var0 instanceof String[]) {
         return getUsingURLs((String[])((String[])var0), var1);
      } else {
         throw new IllegalArgumentException("argument must be an LDAP URL String or array of them");
      }
   }

   private static DirContext getUsingURL(String var0, Hashtable<?, ?> var1) throws NamingException {
      Object var2 = null;
      LdapURL var3 = new LdapURL(var0);
      String var4 = var3.getDN();
      String var5 = var3.getHost();
      int var6 = var3.getPort();
      String var8 = null;
      String[] var7;
      if (var5 == null && var6 == -1 && var4 != null && (var8 = ServiceLocator.mapDnToDomainName(var4)) != null && (var7 = ServiceLocator.getLdapService(var8, var1)) != null) {
         String var9 = var3.getScheme() + "://";
         String[] var10 = new String[var7.length];
         String var11 = var3.getQuery();
         String var12 = var3.getPath() + (var11 != null ? var11 : "");

         for(int var13 = 0; var13 < var7.length; ++var13) {
            var10[var13] = var9 + var7[var13] + var12;
         }

         var2 = getUsingURLs(var10, var1);
         ((LdapCtx)var2).setDomainName(var8);
      } else {
         var2 = new LdapCtx(var4, var5, var6, var1, var3.useSsl());
         ((LdapCtx)var2).setProviderUrl(var0);
      }

      return (DirContext)var2;
   }

   private static DirContext getUsingURLs(String[] var0, Hashtable<?, ?> var1) throws NamingException {
      NamingException var2 = null;
      Object var3 = null;
      int var4 = 0;

      while(var4 < var0.length) {
         try {
            return getUsingURL(var0[var4], var1);
         } catch (AuthenticationException var6) {
            throw var6;
         } catch (NamingException var7) {
            var2 = var7;
            ++var4;
         }
      }

      throw var2;
   }

   public static Attribute createTypeNameAttr(Class<?> var0) {
      Vector var1 = new Vector(10);
      String[] var2 = getTypeNames(var0, var1);
      if (var2.length <= 0) {
         return null;
      } else {
         BasicAttribute var3 = new BasicAttribute(Obj.JAVA_ATTRIBUTES[6]);

         for(int var4 = 0; var4 < var2.length; ++var4) {
            var3.add(var2[var4]);
         }

         return var3;
      }
   }

   private static String[] getTypeNames(Class<?> var0, Vector<String> var1) {
      getClassesAux(var0, var1);
      Class[] var2 = var0.getInterfaces();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         getClassesAux(var2[var3], var1);
      }

      String[] var7 = new String[var1.size()];
      int var4 = 0;

      String var6;
      for(Iterator var5 = var1.iterator(); var5.hasNext(); var7[var4++] = var6) {
         var6 = (String)var5.next();
      }

      return var7;
   }

   private static void getClassesAux(Class<?> var0, Vector<String> var1) {
      if (!var1.contains(var0.getName())) {
         var1.addElement(var0.getName());
      }

      for(var0 = var0.getSuperclass(); var0 != null; var0 = var0.getSuperclass()) {
         getTypeNames(var0, var1);
      }

   }
}
