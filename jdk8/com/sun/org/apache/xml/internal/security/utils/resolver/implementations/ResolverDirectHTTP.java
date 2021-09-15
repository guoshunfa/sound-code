package com.sun.org.apache.xml.internal.security.utils.resolver.implementations;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverContext;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverException;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResolverDirectHTTP extends ResourceResolverSpi {
   private static Logger log = Logger.getLogger(ResolverDirectHTTP.class.getName());
   private static final String[] properties = new String[]{"http.proxy.host", "http.proxy.port", "http.proxy.username", "http.proxy.password", "http.basic.username", "http.basic.password"};
   private static final int HttpProxyHost = 0;
   private static final int HttpProxyPort = 1;
   private static final int HttpProxyUser = 2;
   private static final int HttpProxyPass = 3;
   private static final int HttpBasicUser = 4;
   private static final int HttpBasicPass = 5;

   public boolean engineIsThreadSafe() {
      return true;
   }

   public XMLSignatureInput engineResolveURI(ResourceResolverContext var1) throws ResourceResolverException {
      try {
         URI var2 = getNewURI(var1.uriToResolve, var1.baseUri);
         URL var3 = var2.toURL();
         URLConnection var4 = this.openConnection(var3);
         String var5 = var4.getHeaderField("WWW-Authenticate");
         String var6;
         if (var5 != null && var5.startsWith("Basic")) {
            var6 = this.engineGetProperty(properties[4]);
            String var7 = this.engineGetProperty(properties[5]);
            if (var6 != null && var7 != null) {
               var4 = this.openConnection(var3);
               String var8 = var6 + ":" + var7;
               String var9 = Base64.encode(var8.getBytes("ISO-8859-1"));
               var4.setRequestProperty("Authorization", "Basic " + var9);
            }
         }

         var6 = var4.getHeaderField("Content-Type");
         InputStream var17 = var4.getInputStream();
         ByteArrayOutputStream var18 = new ByteArrayOutputStream();
         byte[] var19 = new byte[4096];
         boolean var10 = false;

         int var11;
         int var20;
         for(var11 = 0; (var20 = var17.read(var19)) >= 0; var11 += var20) {
            var18.write(var19, 0, var20);
         }

         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Fetched " + var11 + " bytes from URI " + var2.toString());
         }

         XMLSignatureInput var12 = new XMLSignatureInput(var18.toByteArray());
         var12.setSourceURI(var2.toString());
         var12.setMIMEType(var6);
         return var12;
      } catch (URISyntaxException var13) {
         throw new ResourceResolverException("generic.EmptyMessage", var13, var1.attr, var1.baseUri);
      } catch (MalformedURLException var14) {
         throw new ResourceResolverException("generic.EmptyMessage", var14, var1.attr, var1.baseUri);
      } catch (IOException var15) {
         throw new ResourceResolverException("generic.EmptyMessage", var15, var1.attr, var1.baseUri);
      } catch (IllegalArgumentException var16) {
         throw new ResourceResolverException("generic.EmptyMessage", var16, var1.attr, var1.baseUri);
      }
   }

   private URLConnection openConnection(URL var1) throws IOException {
      String var2 = this.engineGetProperty(properties[0]);
      String var3 = this.engineGetProperty(properties[1]);
      String var4 = this.engineGetProperty(properties[2]);
      String var5 = this.engineGetProperty(properties[3]);
      Proxy var6 = null;
      if (var2 != null && var3 != null) {
         int var7 = Integer.parseInt(var3);
         var6 = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(var2, var7));
      }

      URLConnection var10;
      if (var6 != null) {
         var10 = var1.openConnection(var6);
         if (var4 != null && var5 != null) {
            String var8 = var4 + ":" + var5;
            String var9 = "Basic " + Base64.encode(var8.getBytes("ISO-8859-1"));
            var10.setRequestProperty("Proxy-Authorization", var9);
         }
      } else {
         var10 = var1.openConnection();
      }

      return var10;
   }

   public boolean engineCanResolveURI(ResourceResolverContext var1) {
      if (var1.uriToResolve == null) {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "quick fail, uri == null");
         }

         return false;
      } else if (!var1.uriToResolve.equals("") && var1.uriToResolve.charAt(0) != '#') {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "I was asked whether I can resolve " + var1.uriToResolve);
         }

         if (var1.uriToResolve.startsWith("http:") || var1.baseUri != null && var1.baseUri.startsWith("http:")) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "I state that I can resolve " + var1.uriToResolve);
            }

            return true;
         } else {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "I state that I can't resolve " + var1.uriToResolve);
            }

            return false;
         }
      } else {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "quick fail for empty URIs and local ones");
         }

         return false;
      }
   }

   public String[] engineGetPropertyKeys() {
      return (String[])properties.clone();
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
