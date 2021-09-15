package com.sun.xml.internal.ws.api;

import com.sun.istack.internal.Nullable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import javax.xml.ws.WebServiceException;

public final class EndpointAddress {
   @Nullable
   private URL url;
   private final URI uri;
   private final String stringForm;
   private volatile boolean dontUseProxyMethod;
   private Proxy proxy;

   public EndpointAddress(URI uri) {
      this.uri = uri;
      this.stringForm = uri.toString();

      try {
         this.initURL();
         this.proxy = this.chooseProxy();
      } catch (MalformedURLException var3) {
      }

   }

   public EndpointAddress(String url) throws URISyntaxException {
      this.uri = new URI(url);
      this.stringForm = url;

      try {
         this.initURL();
         this.proxy = this.chooseProxy();
      } catch (MalformedURLException var3) {
      }

   }

   private void initURL() throws MalformedURLException {
      String scheme = this.uri.getScheme();
      if (scheme == null) {
         this.url = new URL(this.uri.toString());
      } else {
         scheme = scheme.toLowerCase();
         if (!"http".equals(scheme) && !"https".equals(scheme)) {
            this.url = this.uri.toURL();
         } else {
            this.url = new URL(this.uri.toASCIIString());
         }

      }
   }

   public static EndpointAddress create(String url) {
      try {
         return new EndpointAddress(url);
      } catch (URISyntaxException var2) {
         throw new WebServiceException("Illegal endpoint address: " + url, var2);
      }
   }

   private Proxy chooseProxy() {
      ProxySelector sel = (ProxySelector)AccessController.doPrivileged(new PrivilegedAction<ProxySelector>() {
         public ProxySelector run() {
            return ProxySelector.getDefault();
         }
      });
      if (sel == null) {
         return Proxy.NO_PROXY;
      } else if (!sel.getClass().getName().equals("sun.net.spi.DefaultProxySelector")) {
         return null;
      } else {
         Iterator<Proxy> it = sel.select(this.uri).iterator();
         return it.hasNext() ? (Proxy)it.next() : Proxy.NO_PROXY;
      }
   }

   public URL getURL() {
      return this.url;
   }

   public URI getURI() {
      return this.uri;
   }

   public URLConnection openConnection() throws IOException {
      if (this.url == null) {
         throw new WebServiceException("URI=" + this.uri + " doesn't have the corresponding URL");
      } else {
         if (this.proxy != null && !this.dontUseProxyMethod) {
            try {
               return this.url.openConnection(this.proxy);
            } catch (UnsupportedOperationException var2) {
               this.dontUseProxyMethod = true;
            }
         }

         return this.url.openConnection();
      }
   }

   public String toString() {
      return this.stringForm;
   }
}
