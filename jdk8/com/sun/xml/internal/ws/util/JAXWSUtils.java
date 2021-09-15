package com.sun.xml.internal.ws.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.xml.namespace.QName;

public final class JAXWSUtils {
   public static String getUUID() {
      return UUID.randomUUID().toString();
   }

   public static String getFileOrURLName(String fileOrURL) {
      try {
         try {
            return escapeSpace((new URL(fileOrURL)).toExternalForm());
         } catch (MalformedURLException var2) {
            return (new File(fileOrURL)).getCanonicalFile().toURL().toExternalForm();
         }
      } catch (Exception var3) {
         return fileOrURL;
      }
   }

   public static URL getFileOrURL(String fileOrURL) throws IOException {
      try {
         URL url = new URL(fileOrURL);
         String scheme = String.valueOf((Object)url.getProtocol()).toLowerCase();
         return !scheme.equals("http") && !scheme.equals("https") ? url : new URL(url.toURI().toASCIIString());
      } catch (URISyntaxException var3) {
         return (new File(fileOrURL)).toURL();
      } catch (MalformedURLException var4) {
         return (new File(fileOrURL)).toURL();
      }
   }

   public static URL getEncodedURL(String urlStr) throws MalformedURLException {
      URL url = new URL(urlStr);
      String scheme = String.valueOf((Object)url.getProtocol()).toLowerCase();
      if (!scheme.equals("http") && !scheme.equals("https")) {
         return url;
      } else {
         try {
            return new URL(url.toURI().toASCIIString());
         } catch (URISyntaxException var5) {
            MalformedURLException malformedURLException = new MalformedURLException(var5.getMessage());
            malformedURLException.initCause(var5);
            throw malformedURLException;
         }
      }
   }

   private static String escapeSpace(String url) {
      StringBuilder buf = new StringBuilder();

      for(int i = 0; i < url.length(); ++i) {
         if (url.charAt(i) == ' ') {
            buf.append("%20");
         } else {
            buf.append(url.charAt(i));
         }
      }

      return buf.toString();
   }

   public static String absolutize(String name) {
      try {
         URL baseURL = (new File(".")).getCanonicalFile().toURL();
         return (new URL(baseURL, name)).toExternalForm();
      } catch (IOException var2) {
         return name;
      }
   }

   public static void checkAbsoluteness(String systemId) {
      try {
         new URL(systemId);
      } catch (MalformedURLException var4) {
         try {
            new URI(systemId);
         } catch (URISyntaxException var3) {
            throw new IllegalArgumentException("system ID '" + systemId + "' isn't absolute", var3);
         }
      }

   }

   public static boolean matchQNames(QName target, QName pattern) {
      if (target != null && pattern != null) {
         if (pattern.getNamespaceURI().equals(target.getNamespaceURI())) {
            String regex = pattern.getLocalPart().replaceAll("\\*", ".*");
            return Pattern.matches(regex, target.getLocalPart());
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
