package javax.management.remote;

import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.BitSet;
import java.util.StringTokenizer;

public class JMXServiceURL implements Serializable {
   private static final long serialVersionUID = 8173364409860779292L;
   private static final String INVALID_INSTANCE_MSG = "Trying to deserialize an invalid instance of JMXServiceURL";
   private static final Exception randomException = new Exception();
   private static final BitSet alphaBitSet = new BitSet(128);
   private static final BitSet numericBitSet = new BitSet(128);
   private static final BitSet alphaNumericBitSet = new BitSet(128);
   private static final BitSet protocolBitSet = new BitSet(128);
   private static final BitSet hostNameBitSet = new BitSet(128);
   private String protocol;
   private String host;
   private int port;
   private String urlPath;
   private transient String toString;
   private static final ClassLogger logger;

   public JMXServiceURL(String var1) throws MalformedURLException {
      int var2 = var1.length();

      for(int var3 = 0; var3 < var2; ++var3) {
         char var4 = var1.charAt(var3);
         if (var4 < ' ' || var4 >= 127) {
            throw new MalformedURLException("Service URL contains non-ASCII character 0x" + Integer.toHexString(var4));
         }
      }

      int var14 = "service:jmx:".length();
      if (!var1.regionMatches(true, 0, "service:jmx:", 0, var14)) {
         throw new MalformedURLException("Service URL must start with service:jmx:");
      } else {
         int var6 = indexOf(var1, ':', var14);
         this.protocol = var1.substring(var14, var6).toLowerCase();
         if (!var1.regionMatches(var6, "://", 0, 3)) {
            throw new MalformedURLException("Missing \"://\" after protocol name");
         } else {
            int var7 = var6 + 3;
            int var8;
            if (var7 < var2 && var1.charAt(var7) == '[') {
               var8 = var1.indexOf(93, var7) + 1;
               if (var8 == 0) {
                  throw new MalformedURLException("Bad host name: [ without ]");
               }

               this.host = var1.substring(var7 + 1, var8 - 1);
               if (!isNumericIPv6Address(this.host)) {
                  throw new MalformedURLException("Address inside [...] must be numeric IPv6 address");
               }
            } else {
               var8 = indexOfFirstNotInSet(var1, hostNameBitSet, var7);
               this.host = var1.substring(var7, var8);
            }

            int var9;
            if (var8 < var2 && var1.charAt(var8) == ':') {
               if (this.host.length() == 0) {
                  throw new MalformedURLException("Cannot give port number without host name");
               }

               int var10 = var8 + 1;
               var9 = indexOfFirstNotInSet(var1, numericBitSet, var10);
               String var11 = var1.substring(var10, var9);

               try {
                  this.port = Integer.parseInt(var11);
               } catch (NumberFormatException var13) {
                  throw new MalformedURLException("Bad port number: \"" + var11 + "\": " + var13);
               }
            } else {
               var9 = var8;
               this.port = 0;
            }

            if (var9 < var2) {
               this.urlPath = var1.substring(var9);
            } else {
               this.urlPath = "";
            }

            this.validate();
         }
      }
   }

   public JMXServiceURL(String var1, String var2, int var3) throws MalformedURLException {
      this(var1, var2, var3, (String)null);
   }

   public JMXServiceURL(String var1, String var2, int var3, String var4) throws MalformedURLException {
      if (var1 == null) {
         var1 = "jmxmp";
      }

      if (var2 == null) {
         InetAddress var5;
         try {
            var5 = InetAddress.getLocalHost();
         } catch (UnknownHostException var7) {
            throw new MalformedURLException("Local host name unknown: " + var7);
         }

         var2 = var5.getHostName();

         try {
            validateHost(var2, var3);
         } catch (MalformedURLException var8) {
            if (logger.fineOn()) {
               logger.fine("JMXServiceURL", "Replacing illegal local host name " + var2 + " with numeric IP address (see RFC 1034)", var8);
            }

            var2 = var5.getHostAddress();
         }
      }

      if (var2.startsWith("[")) {
         if (!var2.endsWith("]")) {
            throw new MalformedURLException("Host starts with [ but does not end with ]");
         }

         var2 = var2.substring(1, var2.length() - 1);
         if (!isNumericIPv6Address(var2)) {
            throw new MalformedURLException("Address inside [...] must be numeric IPv6 address");
         }

         if (var2.startsWith("[")) {
            throw new MalformedURLException("More than one [[...]]");
         }
      }

      this.protocol = var1.toLowerCase();
      this.host = var2;
      this.port = var3;
      if (var4 == null) {
         var4 = "";
      }

      this.urlPath = var4;
      this.validate();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField var2 = var1.readFields();
      String var3 = (String)var2.get("host", (Object)null);
      int var4 = var2.get("port", (int)-1);
      String var5 = (String)var2.get("protocol", (Object)null);
      String var6 = (String)var2.get("urlPath", (Object)null);
      if (var5 != null && var6 != null && var3 != null) {
         if (!var3.contains("[") && !var3.contains("]")) {
            try {
               this.validate(var5, var3, var4, var6);
               this.protocol = var5;
               this.host = var3;
               this.port = var4;
               this.urlPath = var6;
            } catch (MalformedURLException var9) {
               throw new InvalidObjectException("Trying to deserialize an invalid instance of JMXServiceURL: " + var9.getMessage());
            }
         } else {
            throw new InvalidObjectException("Invalid host name: " + var3);
         }
      } else {
         StringBuilder var7 = (new StringBuilder("Trying to deserialize an invalid instance of JMXServiceURL")).append('[');
         boolean var8 = true;
         if (var5 == null) {
            var7.append("protocol=null");
            var8 = false;
         }

         if (var3 == null) {
            var7.append(var8 ? "" : ",").append("host=null");
            var8 = false;
         }

         if (var6 == null) {
            var7.append(var8 ? "" : ",").append("urlPath=null");
         }

         var7.append(']');
         throw new InvalidObjectException(var7.toString());
      }
   }

   private void validate(String var1, String var2, int var3, String var4) throws MalformedURLException {
      int var5 = indexOfFirstNotInSet(var1, protocolBitSet, 0);
      if (var5 != 0 && var5 >= var1.length() && alphaBitSet.get(var1.charAt(0))) {
         validateHost(var2, var3);
         if (var3 < 0) {
            throw new MalformedURLException("Bad port: " + var3);
         } else if (var4.length() > 0 && !var4.startsWith("/") && !var4.startsWith(";")) {
            throw new MalformedURLException("Bad URL path: " + var4);
         }
      } else {
         throw new MalformedURLException("Missing or invalid protocol name: \"" + var1 + "\"");
      }
   }

   private void validate() throws MalformedURLException {
      this.validate(this.protocol, this.host, this.port, this.urlPath);
   }

   private static void validateHost(String var0, int var1) throws MalformedURLException {
      if (var0.length() == 0) {
         if (var1 != 0) {
            throw new MalformedURLException("Cannot give port number without host name");
         }
      } else {
         if (isNumericIPv6Address(var0)) {
            try {
               InetAddress.getByName(var0);
            } catch (Exception var10) {
               MalformedURLException var12 = new MalformedURLException("Bad IPv6 address: " + var0);
               EnvHelp.initCause(var12, var10);
               throw var12;
            }
         } else {
            int var2 = var0.length();
            byte var3 = 46;
            boolean var4 = false;
            char var5 = 0;

            for(int var6 = 0; var6 < var2; ++var6) {
               char var7 = var0.charAt(var6);
               boolean var8 = alphaNumericBitSet.get(var7);
               if (var3 == 46) {
                  var5 = var7;
               }

               if (var8) {
                  var3 = 97;
               } else if (var7 == '-') {
                  if (var3 == 46) {
                     break;
                  }

                  var3 = 45;
               } else {
                  if (var7 != '.') {
                     var3 = 46;
                     break;
                  }

                  var4 = true;
                  if (var3 != 97) {
                     break;
                  }

                  var3 = 46;
               }
            }

            try {
               if (var3 != 97) {
                  throw randomException;
               }

               if (var4 && !alphaBitSet.get(var5)) {
                  StringTokenizer var13 = new StringTokenizer(var0, ".", true);
                  int var14 = 0;

                  while(true) {
                     if (var14 >= 4) {
                        if (var13.hasMoreTokens()) {
                           throw randomException;
                        }
                        break;
                     }

                     String var15 = var13.nextToken();
                     int var9 = Integer.parseInt(var15);
                     if (var9 < 0 || var9 > 255) {
                        throw randomException;
                     }

                     if (var14 < 3 && !var13.nextToken().equals(".")) {
                        throw randomException;
                     }

                     ++var14;
                  }
               }
            } catch (Exception var11) {
               throw new MalformedURLException("Bad host: \"" + var0 + "\"");
            }
         }

      }
   }

   public String getProtocol() {
      return this.protocol;
   }

   public String getHost() {
      return this.host;
   }

   public int getPort() {
      return this.port;
   }

   public String getURLPath() {
      return this.urlPath;
   }

   public String toString() {
      if (this.toString != null) {
         return this.toString;
      } else {
         StringBuilder var1 = new StringBuilder("service:jmx:");
         var1.append(this.getProtocol()).append("://");
         String var2 = this.getHost();
         if (isNumericIPv6Address(var2)) {
            var1.append('[').append(var2).append(']');
         } else {
            var1.append(var2);
         }

         int var3 = this.getPort();
         if (var3 != 0) {
            var1.append(':').append(var3);
         }

         var1.append(this.getURLPath());
         this.toString = var1.toString();
         return this.toString;
      }
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof JMXServiceURL)) {
         return false;
      } else {
         JMXServiceURL var2 = (JMXServiceURL)var1;
         return var2.getProtocol().equalsIgnoreCase(this.getProtocol()) && var2.getHost().equalsIgnoreCase(this.getHost()) && var2.getPort() == this.getPort() && var2.getURLPath().equals(this.getURLPath());
      }
   }

   public int hashCode() {
      return this.toString().hashCode();
   }

   private static boolean isNumericIPv6Address(String var0) {
      return var0.indexOf(58) >= 0;
   }

   private static int indexOf(String var0, char var1, int var2) {
      int var3 = var0.indexOf(var1, var2);
      return var3 < 0 ? var0.length() : var3;
   }

   private static int indexOfFirstNotInSet(String var0, BitSet var1, int var2) {
      int var3 = var0.length();

      int var4;
      for(var4 = var2; var4 < var3; ++var4) {
         char var5 = var0.charAt(var4);
         if (var5 >= 128 || !var1.get(var5)) {
            break;
         }
      }

      return var4;
   }

   static {
      char var0;
      for(var0 = '0'; var0 <= '9'; ++var0) {
         numericBitSet.set(var0);
      }

      for(var0 = 'A'; var0 <= 'Z'; ++var0) {
         alphaBitSet.set(var0);
      }

      for(var0 = 'a'; var0 <= 'z'; ++var0) {
         alphaBitSet.set(var0);
      }

      alphaNumericBitSet.or(alphaBitSet);
      alphaNumericBitSet.or(numericBitSet);
      protocolBitSet.or(alphaNumericBitSet);
      protocolBitSet.set(43);
      protocolBitSet.set(45);
      hostNameBitSet.or(alphaNumericBitSet);
      hostNameBitSet.set(45);
      hostNameBitSet.set(46);
      logger = new ClassLogger("javax.management.remote.misc", "JMXServiceURL");
   }
}
