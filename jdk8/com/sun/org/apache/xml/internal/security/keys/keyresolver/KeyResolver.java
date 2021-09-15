package com.sun.org.apache.xml.internal.security.keys.keyresolver;

import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.DEREncodedKeyValueResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.DSAKeyValueResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.KeyInfoReferenceResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.RSAKeyValueResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.RetrievalMethodResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.X509CertificateResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.X509DigestResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.X509IssuerSerialResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.X509SKIResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.X509SubjectNameResolver;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Element;

public class KeyResolver {
   private static Logger log = Logger.getLogger(KeyResolver.class.getName());
   private static List<KeyResolver> resolverVector = new CopyOnWriteArrayList();
   private final KeyResolverSpi resolverSpi;

   private KeyResolver(KeyResolverSpi var1) {
      this.resolverSpi = var1;
   }

   public static int length() {
      return resolverVector.size();
   }

   public static final X509Certificate getX509Certificate(Element var0, String var1, StorageResolver var2) throws KeyResolverException {
      Iterator var3 = resolverVector.iterator();

      while(var3.hasNext()) {
         KeyResolver var4 = (KeyResolver)var3.next();
         if (var4 == null) {
            Object[] var7 = new Object[]{var0 != null && var0.getNodeType() == 1 ? var0.getTagName() : "null"};
            throw new KeyResolverException("utils.resolver.noClass", var7);
         }

         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "check resolvability by class " + var4.getClass());
         }

         X509Certificate var5 = var4.resolveX509Certificate(var0, var1, var2);
         if (var5 != null) {
            return var5;
         }
      }

      Object[] var6 = new Object[]{var0 != null && var0.getNodeType() == 1 ? var0.getTagName() : "null"};
      throw new KeyResolverException("utils.resolver.noClass", var6);
   }

   public static final PublicKey getPublicKey(Element var0, String var1, StorageResolver var2) throws KeyResolverException {
      Iterator var3 = resolverVector.iterator();

      while(var3.hasNext()) {
         KeyResolver var4 = (KeyResolver)var3.next();
         if (var4 == null) {
            Object[] var7 = new Object[]{var0 != null && var0.getNodeType() == 1 ? var0.getTagName() : "null"};
            throw new KeyResolverException("utils.resolver.noClass", var7);
         }

         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "check resolvability by class " + var4.getClass());
         }

         PublicKey var5 = var4.resolvePublicKey(var0, var1, var2);
         if (var5 != null) {
            return var5;
         }
      }

      Object[] var6 = new Object[]{var0 != null && var0.getNodeType() == 1 ? var0.getTagName() : "null"};
      throw new KeyResolverException("utils.resolver.noClass", var6);
   }

   public static void register(String var0, boolean var1) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
      JavaUtils.checkRegisterPermission();
      KeyResolverSpi var2 = (KeyResolverSpi)Class.forName(var0).newInstance();
      var2.setGlobalResolver(var1);
      register(var2, false);
   }

   public static void registerAtStart(String var0, boolean var1) {
      JavaUtils.checkRegisterPermission();
      KeyResolverSpi var2 = null;
      Object var3 = null;

      try {
         var2 = (KeyResolverSpi)Class.forName(var0).newInstance();
      } catch (ClassNotFoundException var5) {
         var3 = var5;
      } catch (IllegalAccessException var6) {
         var3 = var6;
      } catch (InstantiationException var7) {
         var3 = var7;
      }

      if (var3 != null) {
         throw (IllegalArgumentException)(new IllegalArgumentException("Invalid KeyResolver class name")).initCause((Throwable)var3);
      } else {
         var2.setGlobalResolver(var1);
         register(var2, true);
      }
   }

   public static void register(KeyResolverSpi var0, boolean var1) {
      JavaUtils.checkRegisterPermission();
      KeyResolver var2 = new KeyResolver(var0);
      if (var1) {
         resolverVector.add(0, var2);
      } else {
         resolverVector.add(var2);
      }

   }

   public static void registerClassNames(List<String> var0) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
      JavaUtils.checkRegisterPermission();
      ArrayList var1 = new ArrayList(var0.size());
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         KeyResolverSpi var4 = (KeyResolverSpi)Class.forName(var3).newInstance();
         var4.setGlobalResolver(false);
         var1.add(new KeyResolver(var4));
      }

      resolverVector.addAll(var1);
   }

   public static void registerDefaultResolvers() {
      ArrayList var0 = new ArrayList();
      var0.add(new KeyResolver(new RSAKeyValueResolver()));
      var0.add(new KeyResolver(new DSAKeyValueResolver()));
      var0.add(new KeyResolver(new X509CertificateResolver()));
      var0.add(new KeyResolver(new X509SKIResolver()));
      var0.add(new KeyResolver(new RetrievalMethodResolver()));
      var0.add(new KeyResolver(new X509SubjectNameResolver()));
      var0.add(new KeyResolver(new X509IssuerSerialResolver()));
      var0.add(new KeyResolver(new DEREncodedKeyValueResolver()));
      var0.add(new KeyResolver(new KeyInfoReferenceResolver()));
      var0.add(new KeyResolver(new X509DigestResolver()));
      resolverVector.addAll(var0);
   }

   public PublicKey resolvePublicKey(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      return this.resolverSpi.engineLookupAndResolvePublicKey(var1, var2, var3);
   }

   public X509Certificate resolveX509Certificate(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      return this.resolverSpi.engineLookupResolveX509Certificate(var1, var2, var3);
   }

   public SecretKey resolveSecretKey(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      return this.resolverSpi.engineLookupAndResolveSecretKey(var1, var2, var3);
   }

   public void setProperty(String var1, String var2) {
      this.resolverSpi.engineSetProperty(var1, var2);
   }

   public String getProperty(String var1) {
      return this.resolverSpi.engineGetProperty(var1);
   }

   public boolean understandsProperty(String var1) {
      return this.resolverSpi.understandsProperty(var1);
   }

   public String resolverClassName() {
      return this.resolverSpi.getClass().getName();
   }

   public static Iterator<KeyResolverSpi> iterator() {
      return new KeyResolver.ResolverIterator(resolverVector);
   }

   static class ResolverIterator implements Iterator<KeyResolverSpi> {
      List<KeyResolver> res;
      Iterator<KeyResolver> it;

      public ResolverIterator(List<KeyResolver> var1) {
         this.res = var1;
         this.it = this.res.iterator();
      }

      public boolean hasNext() {
         return this.it.hasNext();
      }

      public KeyResolverSpi next() {
         KeyResolver var1 = (KeyResolver)this.it.next();
         if (var1 == null) {
            throw new RuntimeException("utils.resolver.noClass");
         } else {
            return var1.resolverSpi;
         }
      }

      public void remove() {
         throw new UnsupportedOperationException("Can't remove resolvers using the iterator");
      }
   }
}
