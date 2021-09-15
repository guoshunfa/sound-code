package com.sun.org.apache.xml.internal.security.utils.resolver.implementations;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverContext;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverException;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;
import java.io.FileInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResolverLocalFilesystem extends ResourceResolverSpi {
   private static final int FILE_URI_LENGTH = "file:/".length();
   private static Logger log = Logger.getLogger(ResolverLocalFilesystem.class.getName());

   public boolean engineIsThreadSafe() {
      return true;
   }

   public XMLSignatureInput engineResolveURI(ResourceResolverContext var1) throws ResourceResolverException {
      try {
         URI var2 = getNewURI(var1.uriToResolve, var1.baseUri);
         String var3 = translateUriToFilename(var2.toString());
         FileInputStream var4 = new FileInputStream(var3);
         XMLSignatureInput var5 = new XMLSignatureInput(var4);
         var5.setSourceURI(var2.toString());
         return var5;
      } catch (Exception var6) {
         throw new ResourceResolverException("generic.EmptyMessage", var6, var1.attr, var1.baseUri);
      }
   }

   private static String translateUriToFilename(String var0) {
      String var1 = var0.substring(FILE_URI_LENGTH);
      if (var1.indexOf("%20") > -1) {
         int var2 = 0;
         boolean var3 = false;
         StringBuilder var4 = new StringBuilder(var1.length());

         int var5;
         do {
            var5 = var1.indexOf("%20", var2);
            if (var5 == -1) {
               var4.append(var1.substring(var2));
            } else {
               var4.append(var1.substring(var2, var5));
               var4.append(' ');
               var2 = var5 + 3;
            }
         } while(var5 != -1);

         var1 = var4.toString();
      }

      return var1.charAt(1) == ':' ? var1 : "/" + var1;
   }

   public boolean engineCanResolveURI(ResourceResolverContext var1) {
      if (var1.uriToResolve == null) {
         return false;
      } else if (!var1.uriToResolve.equals("") && var1.uriToResolve.charAt(0) != '#' && !var1.uriToResolve.startsWith("http:")) {
         try {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "I was asked whether I can resolve " + var1.uriToResolve);
            }

            if (var1.uriToResolve.startsWith("file:") || var1.baseUri.startsWith("file:")) {
               if (log.isLoggable(Level.FINE)) {
                  log.log(Level.FINE, "I state that I can resolve " + var1.uriToResolve);
               }

               return true;
            }
         } catch (Exception var3) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, (String)var3.getMessage(), (Throwable)var3);
            }
         }

         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "But I can't");
         }

         return false;
      } else {
         return false;
      }
   }

   private static URI getNewURI(String var0, String var1) throws URISyntaxException {
      URI var2 = null;
      if (var1 != null && !"".equals(var1)) {
         var2 = (new URI(var1)).resolve(var0);
      } else {
         var2 = new URI(var0);
      }

      if (var2.getFragment() != null) {
         URI var3 = new URI(var2.getScheme(), var2.getSchemeSpecificPart(), (String)null);
         return var3;
      } else {
         return var2;
      }
   }
}
