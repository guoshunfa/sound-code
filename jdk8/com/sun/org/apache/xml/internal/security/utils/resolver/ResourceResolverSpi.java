package com.sun.org.apache.xml.internal.security.utils.resolver;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Attr;

public abstract class ResourceResolverSpi {
   private static Logger log = Logger.getLogger(ResourceResolverSpi.class.getName());
   protected Map<String, String> properties = null;
   /** @deprecated */
   @Deprecated
   protected final boolean secureValidation = true;

   /** @deprecated */
   @Deprecated
   public XMLSignatureInput engineResolve(Attr var1, String var2) throws ResourceResolverException {
      throw new UnsupportedOperationException();
   }

   public XMLSignatureInput engineResolveURI(ResourceResolverContext var1) throws ResourceResolverException {
      return this.engineResolve(var1.attr, var1.baseUri);
   }

   public void engineSetProperty(String var1, String var2) {
      if (this.properties == null) {
         this.properties = new HashMap();
      }

      this.properties.put(var1, var2);
   }

   public String engineGetProperty(String var1) {
      return this.properties == null ? null : (String)this.properties.get(var1);
   }

   public void engineAddProperies(Map<String, String> var1) {
      if (var1 != null && !var1.isEmpty()) {
         if (this.properties == null) {
            this.properties = new HashMap();
         }

         this.properties.putAll(var1);
      }

   }

   public boolean engineIsThreadSafe() {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public boolean engineCanResolve(Attr var1, String var2) {
      throw new UnsupportedOperationException();
   }

   public boolean engineCanResolveURI(ResourceResolverContext var1) {
      return this.engineCanResolve(var1.attr, var1.baseUri);
   }

   public String[] engineGetPropertyKeys() {
      return new String[0];
   }

   public boolean understandsProperty(String var1) {
      String[] var2 = this.engineGetPropertyKeys();
      if (var2 != null) {
         for(int var3 = 0; var3 < var2.length; ++var3) {
            if (var2[var3].equals(var1)) {
               return true;
            }
         }
      }

      return false;
   }

   public static String fixURI(String var0) {
      var0 = var0.replace(File.separatorChar, '/');
      char var1;
      char var2;
      if (var0.length() >= 4) {
         var1 = Character.toUpperCase(var0.charAt(0));
         var2 = var0.charAt(1);
         char var3 = var0.charAt(2);
         char var4 = var0.charAt(3);
         boolean var5 = 'A' <= var1 && var1 <= 'Z' && var2 == ':' && var3 == '/' && var4 != '/';
         if (var5 && log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Found DOS filename: " + var0);
         }
      }

      if (var0.length() >= 2) {
         var1 = var0.charAt(1);
         if (var1 == ':') {
            var2 = Character.toUpperCase(var0.charAt(0));
            if ('A' <= var2 && var2 <= 'Z') {
               var0 = "/" + var0;
            }
         }
      }

      return var0;
   }
}
