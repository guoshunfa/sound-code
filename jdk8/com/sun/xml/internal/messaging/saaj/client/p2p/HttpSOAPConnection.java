package com.sun.xml.internal.messaging.saaj.client.p2p;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.util.Base64;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ParseUtil;
import com.sun.xml.internal.messaging.saaj.util.SAAJUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.Provider;
import java.security.Security;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

class HttpSOAPConnection extends SOAPConnection {
   public static final String vmVendor = SAAJUtil.getSystemProperty("java.vendor.url");
   private static final String sunVmVendor = "http://java.sun.com/";
   private static final String ibmVmVendor = "http://www.ibm.com/";
   private static final boolean isSunVM;
   private static final boolean isIBMVM;
   private static final String JAXM_URLENDPOINT = "javax.xml.messaging.URLEndpoint";
   protected static final Logger log;
   MessageFactory messageFactory = null;
   boolean closed = false;
   private static final String SSL_PKG;
   private static final String SSL_PROVIDER;
   private static final int dL = 0;

   public HttpSOAPConnection() throws SOAPException {
      try {
         this.messageFactory = MessageFactory.newInstance("Dynamic Protocol");
      } catch (NoSuchMethodError var2) {
         this.messageFactory = MessageFactory.newInstance();
      } catch (Exception var3) {
         log.log(Level.SEVERE, (String)"SAAJ0001.p2p.cannot.create.msg.factory", (Throwable)var3);
         throw new SOAPExceptionImpl("Unable to create message factory", var3);
      }

   }

   public void close() throws SOAPException {
      if (this.closed) {
         log.severe("SAAJ0002.p2p.close.already.closed.conn");
         throw new SOAPExceptionImpl("Connection already closed");
      } else {
         this.messageFactory = null;
         this.closed = true;
      }
   }

   public SOAPMessage call(SOAPMessage message, Object endPoint) throws SOAPException {
      if (this.closed) {
         log.severe("SAAJ0003.p2p.call.already.closed.conn");
         throw new SOAPExceptionImpl("Connection is closed");
      } else {
         Class urlEndpointClass = null;
         ClassLoader loader = Thread.currentThread().getContextClassLoader();

         try {
            if (loader != null) {
               urlEndpointClass = loader.loadClass("javax.xml.messaging.URLEndpoint");
            } else {
               urlEndpointClass = Class.forName("javax.xml.messaging.URLEndpoint");
            }
         } catch (ClassNotFoundException var11) {
            if (log.isLoggable(Level.FINEST)) {
               log.finest("SAAJ0090.p2p.endpoint.available.only.for.JAXM");
            }
         }

         if (urlEndpointClass != null && urlEndpointClass.isInstance(endPoint)) {
            String url = null;

            try {
               Method m = urlEndpointClass.getMethod("getURL", (Class[])null);
               url = (String)m.invoke(endPoint, (Object[])null);
            } catch (Exception var10) {
               log.log(Level.SEVERE, (String)"SAAJ0004.p2p.internal.err", (Throwable)var10);
               throw new SOAPExceptionImpl("Internal error: " + var10.getMessage());
            }

            try {
               endPoint = new URL(url);
            } catch (MalformedURLException var9) {
               log.log(Level.SEVERE, (String)"SAAJ0005.p2p.", (Throwable)var9);
               throw new SOAPExceptionImpl("Bad URL: " + var9.getMessage());
            }
         }

         if (endPoint instanceof String) {
            try {
               endPoint = new URL((String)endPoint);
            } catch (MalformedURLException var8) {
               log.log(Level.SEVERE, (String)"SAAJ0006.p2p.bad.URL", (Throwable)var8);
               throw new SOAPExceptionImpl("Bad URL: " + var8.getMessage());
            }
         }

         if (endPoint instanceof URL) {
            try {
               SOAPMessage response = this.post(message, (URL)endPoint);
               return response;
            } catch (Exception var7) {
               throw new SOAPExceptionImpl(var7);
            }
         } else {
            log.severe("SAAJ0007.p2p.bad.endPoint.type");
            throw new SOAPExceptionImpl("Bad endPoint type " + endPoint);
         }
      }
   }

   SOAPMessage post(SOAPMessage message, URL endPoint) throws SOAPException, IOException {
      boolean isFailure = false;
      URL url = null;
      HttpURLConnection httpConnection = null;
      boolean var6 = false;

      MimeHeaders headers;
      int responseCode;
      try {
         if (endPoint.getProtocol().equals("https")) {
            this.initHttps();
         }

         URI uri = new URI(endPoint.toString());
         String userInfo = uri.getRawUserInfo();
         if (!endPoint.getProtocol().equalsIgnoreCase("http") && !endPoint.getProtocol().equalsIgnoreCase("https")) {
            log.severe("SAAJ0052.p2p.protocol.mustbe.http.or.https");
            throw new IllegalArgumentException("Protocol " + endPoint.getProtocol() + " not supported in URL " + endPoint);
         }

         httpConnection = this.createConnection(endPoint);
         httpConnection.setRequestMethod("POST");
         httpConnection.setDoOutput(true);
         httpConnection.setDoInput(true);
         httpConnection.setUseCaches(false);
         httpConnection.setInstanceFollowRedirects(true);
         if (message.saveRequired()) {
            message.saveChanges();
         }

         headers = message.getMimeHeaders();
         Iterator it = headers.getAllHeaders();
         boolean hasAuth = false;

         while(it.hasNext()) {
            MimeHeader header = (MimeHeader)it.next();
            String[] values = headers.getHeader(header.getName());
            if (values.length == 1) {
               httpConnection.setRequestProperty(header.getName(), header.getValue());
            } else {
               StringBuffer concat = new StringBuffer();

               for(int i = 0; i < values.length; ++i) {
                  if (i != 0) {
                     concat.append(',');
                  }

                  concat.append(values[i]);
               }

               httpConnection.setRequestProperty(header.getName(), concat.toString());
            }

            if ("Authorization".equals(header.getName())) {
               hasAuth = true;
               if (log.isLoggable(Level.FINE)) {
                  log.fine("SAAJ0091.p2p.https.auth.in.POST.true");
               }
            }
         }

         if (!hasAuth && userInfo != null) {
            this.initAuthUserInfo(httpConnection, userInfo);
         }

         OutputStream out = httpConnection.getOutputStream();

         try {
            message.writeTo(out);
            out.flush();
         } finally {
            out.close();
         }

         httpConnection.connect();

         try {
            responseCode = httpConnection.getResponseCode();
            if (responseCode == 500) {
               isFailure = true;
            } else if (responseCode / 100 != 2) {
               log.log(Level.SEVERE, (String)"SAAJ0008.p2p.bad.response", (Object[])(new String[]{httpConnection.getResponseMessage()}));
               throw new SOAPExceptionImpl("Bad response: (" + responseCode + httpConnection.getResponseMessage());
            }
         } catch (IOException var36) {
            responseCode = httpConnection.getResponseCode();
            if (responseCode != 500) {
               throw var36;
            }

            isFailure = true;
         }
      } catch (SOAPException var37) {
         throw var37;
      } catch (Exception var38) {
         log.severe("SAAJ0009.p2p.msg.send.failed");
         throw new SOAPExceptionImpl("Message send failed", var38);
      }

      SOAPMessage response = null;
      InputStream httpIn = null;
      if (responseCode == 200 || isFailure) {
         try {
            headers = new MimeHeaders();
            int i = 1;

            while(true) {
               String key = httpConnection.getHeaderFieldKey(i);
               String value = httpConnection.getHeaderField(i);
               if (key == null && value == null) {
                  httpIn = isFailure ? httpConnection.getErrorStream() : httpConnection.getInputStream();
                  byte[] bytes = this.readFully(httpIn);
                  int length = httpConnection.getContentLength() == -1 ? bytes.length : httpConnection.getContentLength();
                  if (length == 0) {
                     response = null;
                     log.warning("SAAJ0014.p2p.content.zero");
                  } else {
                     ByteInputStream in = new ByteInputStream(bytes, length);
                     response = this.messageFactory.createMessage(headers, in);
                  }
                  break;
               }

               if (key != null) {
                  StringTokenizer values = new StringTokenizer(value, ",");

                  while(values.hasMoreTokens()) {
                     headers.addHeader(key, values.nextToken().trim());
                  }
               }

               ++i;
            }
         } catch (SOAPException var33) {
            throw var33;
         } catch (Exception var34) {
            log.log(Level.SEVERE, (String)"SAAJ0010.p2p.cannot.read.resp", (Throwable)var34);
            throw new SOAPExceptionImpl("Unable to read response: " + var34.getMessage());
         } finally {
            if (httpIn != null) {
               httpIn.close();
            }

            httpConnection.disconnect();
         }
      }

      return response;
   }

   public SOAPMessage get(Object endPoint) throws SOAPException {
      if (this.closed) {
         log.severe("SAAJ0011.p2p.get.already.closed.conn");
         throw new SOAPExceptionImpl("Connection is closed");
      } else {
         Class urlEndpointClass = null;

         try {
            urlEndpointClass = Class.forName("javax.xml.messaging.URLEndpoint");
         } catch (Exception var9) {
         }

         if (urlEndpointClass != null && urlEndpointClass.isInstance(endPoint)) {
            String url = null;

            try {
               Method m = urlEndpointClass.getMethod("getURL", (Class[])null);
               url = (String)m.invoke(endPoint, (Object[])null);
            } catch (Exception var8) {
               log.severe("SAAJ0004.p2p.internal.err");
               throw new SOAPExceptionImpl("Internal error: " + var8.getMessage());
            }

            try {
               endPoint = new URL(url);
            } catch (MalformedURLException var7) {
               log.severe("SAAJ0005.p2p.");
               throw new SOAPExceptionImpl("Bad URL: " + var7.getMessage());
            }
         }

         if (endPoint instanceof String) {
            try {
               endPoint = new URL((String)endPoint);
            } catch (MalformedURLException var6) {
               log.severe("SAAJ0006.p2p.bad.URL");
               throw new SOAPExceptionImpl("Bad URL: " + var6.getMessage());
            }
         }

         if (endPoint instanceof URL) {
            try {
               SOAPMessage response = this.doGet((URL)endPoint);
               return response;
            } catch (Exception var5) {
               throw new SOAPExceptionImpl(var5);
            }
         } else {
            throw new SOAPExceptionImpl("Bad endPoint type " + endPoint);
         }
      }
   }

   SOAPMessage doGet(URL endPoint) throws SOAPException, IOException {
      boolean isFailure = false;
      URL url = null;
      HttpURLConnection httpConnection = null;
      boolean var5 = false;

      int responseCode;
      try {
         if (endPoint.getProtocol().equals("https")) {
            this.initHttps();
         }

         URI uri = new URI(endPoint.toString());
         String userInfo = uri.getRawUserInfo();
         if (!endPoint.getProtocol().equalsIgnoreCase("http") && !endPoint.getProtocol().equalsIgnoreCase("https")) {
            log.severe("SAAJ0052.p2p.protocol.mustbe.http.or.https");
            throw new IllegalArgumentException("Protocol " + endPoint.getProtocol() + " not supported in URL " + endPoint);
         }

         httpConnection = this.createConnection(endPoint);
         httpConnection.setRequestMethod("GET");
         httpConnection.setDoOutput(true);
         httpConnection.setDoInput(true);
         httpConnection.setUseCaches(false);
         HttpURLConnection.setFollowRedirects(true);
         httpConnection.connect();

         try {
            responseCode = httpConnection.getResponseCode();
            if (responseCode == 500) {
               isFailure = true;
            } else if (responseCode / 100 != 2) {
               log.log(Level.SEVERE, (String)"SAAJ0008.p2p.bad.response", (Object[])(new String[]{httpConnection.getResponseMessage()}));
               throw new SOAPExceptionImpl("Bad response: (" + responseCode + httpConnection.getResponseMessage());
            }
         } catch (IOException var23) {
            responseCode = httpConnection.getResponseCode();
            if (responseCode != 500) {
               throw var23;
            }

            isFailure = true;
         }
      } catch (SOAPException var24) {
         throw var24;
      } catch (Exception var25) {
         log.severe("SAAJ0012.p2p.get.failed");
         throw new SOAPExceptionImpl("Get failed", var25);
      }

      SOAPMessage response = null;
      InputStream httpIn = null;
      if (responseCode == 200 || isFailure) {
         try {
            MimeHeaders headers = new MimeHeaders();
            int i = 1;

            while(true) {
               String key = httpConnection.getHeaderFieldKey(i);
               String value = httpConnection.getHeaderField(i);
               if (key == null && value == null) {
                  httpIn = isFailure ? httpConnection.getErrorStream() : httpConnection.getInputStream();
                  if (httpIn != null && httpConnection.getContentLength() != 0 && httpIn.available() != 0) {
                     response = this.messageFactory.createMessage(headers, httpIn);
                  } else {
                     response = null;
                     log.warning("SAAJ0014.p2p.content.zero");
                  }
                  break;
               }

               if (key != null) {
                  StringTokenizer values = new StringTokenizer(value, ",");

                  while(values.hasMoreTokens()) {
                     headers.addHeader(key, values.nextToken().trim());
                  }
               }

               ++i;
            }
         } catch (SOAPException var20) {
            throw var20;
         } catch (Exception var21) {
            log.log(Level.SEVERE, (String)"SAAJ0010.p2p.cannot.read.resp", (Throwable)var21);
            throw new SOAPExceptionImpl("Unable to read response: " + var21.getMessage());
         } finally {
            if (httpIn != null) {
               httpIn.close();
            }

            httpConnection.disconnect();
         }
      }

      return response;
   }

   private byte[] readFully(InputStream istream) throws IOException {
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      byte[] buf = new byte[1024];
      boolean var4 = false;

      int num;
      while((num = istream.read(buf)) != -1) {
         bout.write(buf, 0, num);
      }

      byte[] ret = bout.toByteArray();
      return ret;
   }

   private void initHttps() {
      String pkgs = SAAJUtil.getSystemProperty("java.protocol.handler.pkgs");
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, (String)"SAAJ0053.p2p.providers", (Object[])(new String[]{pkgs}));
      }

      if (pkgs == null || pkgs.indexOf(SSL_PKG) < 0) {
         if (pkgs == null) {
            pkgs = SSL_PKG;
         } else {
            pkgs = pkgs + "|" + SSL_PKG;
         }

         System.setProperty("java.protocol.handler.pkgs", pkgs);
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, (String)"SAAJ0054.p2p.set.providers", (Object[])(new String[]{pkgs}));
         }

         try {
            Class c = Class.forName(SSL_PROVIDER);
            Provider p = (Provider)c.newInstance();
            Security.addProvider(p);
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, (String)"SAAJ0055.p2p.added.ssl.provider", (Object[])(new String[]{SSL_PROVIDER}));
            }
         } catch (Exception var4) {
         }
      }

   }

   private void initAuthUserInfo(HttpURLConnection conn, String userInfo) {
      if (userInfo != null) {
         int delimiter = userInfo.indexOf(58);
         String user;
         String password;
         if (delimiter == -1) {
            user = ParseUtil.decode(userInfo);
            password = null;
         } else {
            user = ParseUtil.decode(userInfo.substring(0, delimiter++));
            password = ParseUtil.decode(userInfo.substring(delimiter));
         }

         String plain = user + ":";
         byte[] nameBytes = plain.getBytes();
         byte[] passwdBytes = password.getBytes();
         byte[] concat = new byte[nameBytes.length + passwdBytes.length];
         System.arraycopy(nameBytes, 0, concat, 0, nameBytes.length);
         System.arraycopy(passwdBytes, 0, concat, nameBytes.length, passwdBytes.length);
         String auth = "Basic " + new String(Base64.encode(concat));
         conn.setRequestProperty("Authorization", auth);
      }

   }

   private void d(String s) {
      log.log(Level.SEVERE, (String)"SAAJ0013.p2p.HttpSOAPConnection", (Object[])(new String[]{s}));
      System.err.println("HttpSOAPConnection: " + s);
   }

   private HttpURLConnection createConnection(URL endpoint) throws IOException {
      return (HttpURLConnection)endpoint.openConnection();
   }

   static {
      isSunVM = "http://java.sun.com/".equals(vmVendor);
      isIBMVM = "http://www.ibm.com/".equals(vmVendor);
      log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.client.p2p", "com.sun.xml.internal.messaging.saaj.client.p2p.LocalStrings");
      if (isIBMVM) {
         SSL_PKG = "com.ibm.net.ssl.internal.www.protocol";
         SSL_PROVIDER = "com.ibm.net.ssl.internal.ssl.Provider";
      } else {
         SSL_PKG = "com.sun.net.ssl.internal.www.protocol";
         SSL_PROVIDER = "com.sun.net.ssl.internal.ssl.Provider";
      }

   }
}
