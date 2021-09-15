package com.sun.org.apache.xml.internal.security.utils.resolver;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.implementations.ResolverDirectHTTP;
import com.sun.org.apache.xml.internal.security.utils.resolver.implementations.ResolverFragment;
import com.sun.org.apache.xml.internal.security.utils.resolver.implementations.ResolverLocalFilesystem;
import com.sun.org.apache.xml.internal.security.utils.resolver.implementations.ResolverXPointer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Attr;

public class ResourceResolver {
   private static Logger log = Logger.getLogger(ResourceResolver.class.getName());
   private static List<ResourceResolver> resolverList = new ArrayList();
   private final ResourceResolverSpi resolverSpi;

   public ResourceResolver(ResourceResolverSpi var1) {
      this.resolverSpi = var1;
   }

   public static final ResourceResolver getInstance(Attr var0, String var1) throws ResourceResolverException {
      return getInstance(var0, var1, false);
   }

   public static final ResourceResolver getInstance(Attr var0, String var1, boolean var2) throws ResourceResolverException {
      ResourceResolverContext var3 = new ResourceResolverContext(var0, var1, var2);
      return internalGetInstance(var3);
   }

   private static <N> ResourceResolver internalGetInstance(ResourceResolverContext var0) throws ResourceResolverException {
      synchronized(resolverList) {
         Iterator var2 = resolverList.iterator();

         while(var2.hasNext()) {
            ResourceResolver var3 = (ResourceResolver)var2.next();
            ResourceResolver var4 = var3;
            if (!var3.resolverSpi.engineIsThreadSafe()) {
               try {
                  var4 = new ResourceResolver((ResourceResolverSpi)var3.resolverSpi.getClass().newInstance());
               } catch (InstantiationException var7) {
                  throw new ResourceResolverException("", var7, var0.attr, var0.baseUri);
               } catch (IllegalAccessException var8) {
                  throw new ResourceResolverException("", var8, var0.attr, var0.baseUri);
               }
            }

            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "check resolvability by class " + var4.getClass().getName());
            }

            if (var4 != null && var4.canResolve(var0)) {
               if (!var0.secureValidation || !(var4.resolverSpi instanceof ResolverLocalFilesystem) && !(var4.resolverSpi instanceof ResolverDirectHTTP)) {
                  return var4;
               }

               Object[] var5 = new Object[]{var4.resolverSpi.getClass().getName()};
               throw new ResourceResolverException("signature.Reference.ForbiddenResolver", var5, var0.attr, var0.baseUri);
            }
         }
      }

      Object[] var1 = new Object[]{var0.uriToResolve != null ? var0.uriToResolve : "null", var0.baseUri};
      throw new ResourceResolverException("utils.resolver.noClass", var1, var0.attr, var0.baseUri);
   }

   public static ResourceResolver getInstance(Attr var0, String var1, List<ResourceResolver> var2) throws ResourceResolverException {
      return getInstance(var0, var1, var2, false);
   }

   public static ResourceResolver getInstance(Attr var0, String var1, List<ResourceResolver> var2, boolean var3) throws ResourceResolverException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "I was asked to create a ResourceResolver and got " + (var2 == null ? 0 : var2.size()));
      }

      ResourceResolverContext var4 = new ResourceResolverContext(var0, var1, var3);
      if (var2 != null) {
         for(int var5 = 0; var5 < var2.size(); ++var5) {
            ResourceResolver var6 = (ResourceResolver)var2.get(var5);
            if (var6 != null) {
               if (log.isLoggable(Level.FINE)) {
                  String var7 = var6.resolverSpi.getClass().getName();
                  log.log(Level.FINE, "check resolvability by class " + var7);
               }

               if (var6.canResolve(var4)) {
                  return var6;
               }
            }
         }
      }

      return internalGetInstance(var4);
   }

   public static void register(String var0) {
      JavaUtils.checkRegisterPermission();

      try {
         Class var1 = Class.forName(var0);
         register(var1, false);
      } catch (ClassNotFoundException var2) {
         log.log(Level.WARNING, "Error loading resolver " + var0 + " disabling it");
      }

   }

   public static void registerAtStart(String var0) {
      JavaUtils.checkRegisterPermission();

      try {
         Class var1 = Class.forName(var0);
         register(var1, true);
      } catch (ClassNotFoundException var2) {
         log.log(Level.WARNING, "Error loading resolver " + var0 + " disabling it");
      }

   }

   public static void register(Class<? extends ResourceResolverSpi> var0, boolean var1) {
      JavaUtils.checkRegisterPermission();

      try {
         ResourceResolverSpi var2 = (ResourceResolverSpi)var0.newInstance();
         register(var2, var1);
      } catch (IllegalAccessException var3) {
         log.log(Level.WARNING, "Error loading resolver " + var0 + " disabling it");
      } catch (InstantiationException var4) {
         log.log(Level.WARNING, "Error loading resolver " + var0 + " disabling it");
      }

   }

   public static void register(ResourceResolverSpi var0, boolean var1) {
      JavaUtils.checkRegisterPermission();
      synchronized(resolverList) {
         if (var1) {
            resolverList.add(0, new ResourceResolver(var0));
         } else {
            resolverList.add(new ResourceResolver(var0));
         }
      }

      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Registered resolver: " + var0.toString());
      }

   }

   public static void registerDefaultResolvers() {
      synchronized(resolverList) {
         resolverList.add(new ResourceResolver(new ResolverFragment()));
         resolverList.add(new ResourceResolver(new ResolverLocalFilesystem()));
         resolverList.add(new ResourceResolver(new ResolverXPointer()));
         resolverList.add(new ResourceResolver(new ResolverDirectHTTP()));
      }
   }

   /** @deprecated */
   @Deprecated
   public XMLSignatureInput resolve(Attr var1, String var2) throws ResourceResolverException {
      return this.resolve(var1, var2, true);
   }

   public XMLSignatureInput resolve(Attr var1, String var2, boolean var3) throws ResourceResolverException {
      ResourceResolverContext var4 = new ResourceResolverContext(var1, var2, var3);
      return this.resolverSpi.engineResolveURI(var4);
   }

   public void setProperty(String var1, String var2) {
      this.resolverSpi.engineSetProperty(var1, var2);
   }

   public String getProperty(String var1) {
      return this.resolverSpi.engineGetProperty(var1);
   }

   public void addProperties(Map<String, String> var1) {
      this.resolverSpi.engineAddProperies(var1);
   }

   public String[] getPropertyKeys() {
      return this.resolverSpi.engineGetPropertyKeys();
   }

   public boolean understandsProperty(String var1) {
      return this.resolverSpi.understandsProperty(var1);
   }

   private boolean canResolve(ResourceResolverContext var1) {
      return this.resolverSpi.engineCanResolveURI(var1);
   }
}
