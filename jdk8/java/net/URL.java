package java.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;
import java.util.StringTokenizer;
import sun.net.ApplicationProxy;
import sun.security.action.GetPropertyAction;
import sun.security.util.SecurityConstants;

public final class URL implements Serializable {
   static final String BUILTIN_HANDLERS_PREFIX = "sun.net.www.protocol";
   static final long serialVersionUID = -7627629688361524110L;
   private static final String protocolPathProp = "java.protocol.handler.pkgs";
   private String protocol;
   private String host;
   private int port;
   private String file;
   private transient String query;
   private String authority;
   private transient String path;
   private transient String userInfo;
   private String ref;
   transient InetAddress hostAddress;
   transient URLStreamHandler handler;
   private int hashCode;
   private transient UrlDeserializedState tempState;
   static URLStreamHandlerFactory factory;
   static Hashtable<String, URLStreamHandler> handlers = new Hashtable();
   private static Object streamHandlerLock = new Object();
   private static final ObjectStreamField[] serialPersistentFields;

   public URL(String var1, String var2, int var3, String var4) throws MalformedURLException {
      this(var1, var2, var3, var4, (URLStreamHandler)null);
   }

   public URL(String var1, String var2, String var3) throws MalformedURLException {
      this(var1, var2, -1, var3);
   }

   public URL(String var1, String var2, int var3, String var4, URLStreamHandler var5) throws MalformedURLException {
      this.port = -1;
      this.hashCode = -1;
      if (var5 != null) {
         SecurityManager var6 = System.getSecurityManager();
         if (var6 != null) {
            this.checkSpecifyHandler(var6);
         }
      }

      var1 = var1.toLowerCase();
      this.protocol = var1;
      if (var2 != null) {
         if (var2.indexOf(58) >= 0 && !var2.startsWith("[")) {
            var2 = "[" + var2 + "]";
         }

         this.host = var2;
         if (var3 < -1) {
            throw new MalformedURLException("Invalid port number :" + var3);
         }

         this.port = var3;
         this.authority = var3 == -1 ? var2 : var2 + ":" + var3;
      }

      Parts var7 = new Parts(var4);
      this.path = var7.getPath();
      this.query = var7.getQuery();
      if (this.query != null) {
         this.file = this.path + "?" + this.query;
      } else {
         this.file = this.path;
      }

      this.ref = var7.getRef();
      if (var5 == null && (var5 = getURLStreamHandler(var1)) == null) {
         throw new MalformedURLException("unknown protocol: " + var1);
      } else {
         this.handler = var5;
      }
   }

   public URL(String var1) throws MalformedURLException {
      this((URL)null, var1);
   }

   public URL(URL var1, String var2) throws MalformedURLException {
      this((URL)var1, var2, (URLStreamHandler)null);
   }

   public URL(URL var1, String var2, URLStreamHandler var3) throws MalformedURLException {
      this.port = -1;
      this.hashCode = -1;
      String var4 = var2;
      int var8 = 0;
      String var9 = null;
      boolean var10 = false;
      boolean var11 = false;
      if (var3 != null) {
         SecurityManager var12 = System.getSecurityManager();
         if (var12 != null) {
            this.checkSpecifyHandler(var12);
         }
      }

      try {
         int var6;
         for(var6 = var2.length(); var6 > 0 && var2.charAt(var6 - 1) <= ' '; --var6) {
         }

         while(var8 < var6 && var2.charAt(var8) <= ' ') {
            ++var8;
         }

         if (var2.regionMatches(true, var8, "url:", 0, 4)) {
            var8 += 4;
         }

         if (var8 < var2.length() && var2.charAt(var8) == '#') {
            var10 = true;
         }

         int var5;
         char var7;
         for(var5 = var8; !var10 && var5 < var6 && (var7 = var2.charAt(var5)) != '/'; ++var5) {
            if (var7 == ':') {
               String var16 = var2.substring(var8, var5).toLowerCase();
               if (this.isValidProtocol(var16)) {
                  var9 = var16;
                  var8 = var5 + 1;
               }
               break;
            }
         }

         this.protocol = var9;
         if (var1 != null && (var9 == null || var9.equalsIgnoreCase(var1.protocol))) {
            if (var3 == null) {
               var3 = var1.handler;
            }

            if (var1.path != null && var1.path.startsWith("/")) {
               var9 = null;
            }

            if (var9 == null) {
               this.protocol = var1.protocol;
               this.authority = var1.authority;
               this.userInfo = var1.userInfo;
               this.host = var1.host;
               this.port = var1.port;
               this.file = var1.file;
               this.path = var1.path;
               var11 = true;
            }
         }

         if (this.protocol == null) {
            throw new MalformedURLException("no protocol: " + var4);
         } else if (var3 == null && (var3 = getURLStreamHandler(this.protocol)) == null) {
            throw new MalformedURLException("unknown protocol: " + this.protocol);
         } else {
            this.handler = var3;
            var5 = var2.indexOf(35, var8);
            if (var5 >= 0) {
               this.ref = var2.substring(var5 + 1, var6);
               var6 = var5;
            }

            if (var11 && var8 == var6) {
               this.query = var1.query;
               if (this.ref == null) {
                  this.ref = var1.ref;
               }
            }

            var3.parseURL(this, var2, var8, var6);
         }
      } catch (MalformedURLException var14) {
         throw var14;
      } catch (Exception var15) {
         MalformedURLException var13 = new MalformedURLException(var15.getMessage());
         var13.initCause(var15);
         throw var13;
      }
   }

   private boolean isValidProtocol(String var1) {
      int var2 = var1.length();
      if (var2 < 1) {
         return false;
      } else {
         char var3 = var1.charAt(0);
         if (!Character.isLetter(var3)) {
            return false;
         } else {
            for(int var4 = 1; var4 < var2; ++var4) {
               var3 = var1.charAt(var4);
               if (!Character.isLetterOrDigit(var3) && var3 != '.' && var3 != '+' && var3 != '-') {
                  return false;
               }
            }

            return true;
         }
      }
   }

   private void checkSpecifyHandler(SecurityManager var1) {
      var1.checkPermission(SecurityConstants.SPECIFY_HANDLER_PERMISSION);
   }

   void set(String var1, String var2, int var3, String var4, String var5) {
      synchronized(this) {
         this.protocol = var1;
         this.host = var2;
         this.authority = var3 == -1 ? var2 : var2 + ":" + var3;
         this.port = var3;
         this.file = var4;
         this.ref = var5;
         this.hashCode = -1;
         this.hostAddress = null;
         int var7 = var4.lastIndexOf(63);
         if (var7 != -1) {
            this.query = var4.substring(var7 + 1);
            this.path = var4.substring(0, var7);
         } else {
            this.path = var4;
         }

      }
   }

   void set(String var1, String var2, int var3, String var4, String var5, String var6, String var7, String var8) {
      synchronized(this) {
         this.protocol = var1;
         this.host = var2;
         this.port = var3;
         this.file = var7 == null ? var6 : var6 + "?" + var7;
         this.userInfo = var5;
         this.path = var6;
         this.ref = var8;
         this.hashCode = -1;
         this.hostAddress = null;
         this.query = var7;
         this.authority = var4;
      }
   }

   public String getQuery() {
      return this.query;
   }

   public String getPath() {
      return this.path;
   }

   public String getUserInfo() {
      return this.userInfo;
   }

   public String getAuthority() {
      return this.authority;
   }

   public int getPort() {
      return this.port;
   }

   public int getDefaultPort() {
      return this.handler.getDefaultPort();
   }

   public String getProtocol() {
      return this.protocol;
   }

   public String getHost() {
      return this.host;
   }

   public String getFile() {
      return this.file;
   }

   public String getRef() {
      return this.ref;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof URL)) {
         return false;
      } else {
         URL var2 = (URL)var1;
         return this.handler.equals(this, var2);
      }
   }

   public synchronized int hashCode() {
      if (this.hashCode != -1) {
         return this.hashCode;
      } else {
         this.hashCode = this.handler.hashCode(this);
         return this.hashCode;
      }
   }

   public boolean sameFile(URL var1) {
      return this.handler.sameFile(this, var1);
   }

   public String toString() {
      return this.toExternalForm();
   }

   public String toExternalForm() {
      return this.handler.toExternalForm(this);
   }

   public URI toURI() throws URISyntaxException {
      return new URI(this.toString());
   }

   public URLConnection openConnection() throws IOException {
      return this.handler.openConnection(this);
   }

   public URLConnection openConnection(Proxy var1) throws IOException {
      if (var1 == null) {
         throw new IllegalArgumentException("proxy can not be null");
      } else {
         Object var2 = var1 == Proxy.NO_PROXY ? Proxy.NO_PROXY : ApplicationProxy.create(var1);
         SecurityManager var3 = System.getSecurityManager();
         if (((Proxy)var2).type() != Proxy.Type.DIRECT && var3 != null) {
            InetSocketAddress var4 = (InetSocketAddress)((Proxy)var2).address();
            if (var4.isUnresolved()) {
               var3.checkConnect(var4.getHostName(), var4.getPort());
            } else {
               var3.checkConnect(var4.getAddress().getHostAddress(), var4.getPort());
            }
         }

         return this.handler.openConnection(this, (Proxy)var2);
      }
   }

   public final InputStream openStream() throws IOException {
      return this.openConnection().getInputStream();
   }

   public final Object getContent() throws IOException {
      return this.openConnection().getContent();
   }

   public final Object getContent(Class[] var1) throws IOException {
      return this.openConnection().getContent(var1);
   }

   public static void setURLStreamHandlerFactory(URLStreamHandlerFactory var0) {
      synchronized(streamHandlerLock) {
         if (factory != null) {
            throw new Error("factory already defined");
         } else {
            SecurityManager var2 = System.getSecurityManager();
            if (var2 != null) {
               var2.checkSetFactory();
            }

            handlers.clear();
            factory = var0;
         }
      }
   }

   static URLStreamHandler getURLStreamHandler(String var0) {
      URLStreamHandler var1 = (URLStreamHandler)handlers.get(var0);
      if (var1 == null) {
         boolean var2 = false;
         if (factory != null) {
            var1 = factory.createURLStreamHandler(var0);
            var2 = true;
         }

         StringTokenizer var4;
         if (var1 == null) {
            String var3 = null;
            var3 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("java.protocol.handler.pkgs", "")));
            if (var3 != "") {
               var3 = var3 + "|";
            }

            var3 = var3 + "sun.net.www.protocol";
            var4 = new StringTokenizer(var3, "|");

            while(var1 == null && var4.hasMoreTokens()) {
               String var5 = var4.nextToken().trim();

               try {
                  String var6 = var5 + "." + var0 + ".Handler";
                  Class var7 = null;

                  try {
                     var7 = Class.forName(var6);
                  } catch (ClassNotFoundException var12) {
                     ClassLoader var9 = ClassLoader.getSystemClassLoader();
                     if (var9 != null) {
                        var7 = var9.loadClass(var6);
                     }
                  }

                  if (var7 != null) {
                     var1 = (URLStreamHandler)var7.newInstance();
                  }
               } catch (Exception var13) {
               }
            }
         }

         synchronized(streamHandlerLock) {
            var4 = null;
            URLStreamHandler var14 = (URLStreamHandler)handlers.get(var0);
            if (var14 != null) {
               return var14;
            }

            if (!var2 && factory != null) {
               var14 = factory.createURLStreamHandler(var0);
            }

            if (var14 != null) {
               var1 = var14;
            }

            if (var1 != null) {
               handlers.put(var0, var1);
            }
         }
      }

      return var1;
   }

   private synchronized void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
   }

   private synchronized void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField var2 = var1.readFields();
      String var3 = (String)var2.get("protocol", (Object)null);
      if (getURLStreamHandler(var3) == null) {
         throw new IOException("unknown protocol: " + var3);
      } else {
         String var4 = (String)var2.get("host", (Object)null);
         int var5 = var2.get("port", (int)-1);
         String var6 = (String)var2.get("authority", (Object)null);
         String var7 = (String)var2.get("file", (Object)null);
         String var8 = (String)var2.get("ref", (Object)null);
         int var9 = var2.get("hashCode", (int)-1);
         if (var6 == null && (var4 != null && var4.length() > 0 || var5 != -1)) {
            if (var4 == null) {
               var4 = "";
            }

            var6 = var5 == -1 ? var4 : var4 + ":" + var5;
         }

         this.tempState = new UrlDeserializedState(var3, var4, var5, var6, var7, var8, var9);
      }
   }

   private Object readResolve() throws ObjectStreamException {
      URLStreamHandler var1 = null;
      var1 = getURLStreamHandler(this.tempState.getProtocol());
      URL var2 = null;
      if (this.isBuiltinStreamHandler(var1.getClass().getName())) {
         var2 = this.fabricateNewURL();
      } else {
         var2 = this.setDeserializedFields(var1);
      }

      return var2;
   }

   private URL setDeserializedFields(URLStreamHandler var1) {
      String var3 = null;
      String var4 = this.tempState.getProtocol();
      String var5 = this.tempState.getHost();
      int var6 = this.tempState.getPort();
      String var7 = this.tempState.getAuthority();
      String var8 = this.tempState.getFile();
      String var9 = this.tempState.getRef();
      int var10 = this.tempState.getHashCode();
      int var11;
      if (var7 != null || (var5 == null || var5.length() <= 0) && var6 == -1) {
         if (var7 != null) {
            var11 = var7.indexOf(64);
            if (var11 != -1) {
               var3 = var7.substring(0, var11);
            }
         }
      } else {
         if (var5 == null) {
            var5 = "";
         }

         var7 = var6 == -1 ? var5 : var5 + ":" + var6;
         var11 = var5.lastIndexOf(64);
         if (var11 != -1) {
            var3 = var5.substring(0, var11);
            var5 = var5.substring(var11 + 1);
         }
      }

      String var14 = null;
      String var12 = null;
      if (var8 != null) {
         int var13 = var8.lastIndexOf(63);
         if (var13 != -1) {
            var12 = var8.substring(var13 + 1);
            var14 = var8.substring(0, var13);
         } else {
            var14 = var8;
         }
      }

      this.protocol = var4;
      this.host = var5;
      this.port = var6;
      this.file = var8;
      this.authority = var7;
      this.ref = var9;
      this.hashCode = var10;
      this.handler = var1;
      this.query = var12;
      this.path = var14;
      this.userInfo = var3;
      return this;
   }

   private URL fabricateNewURL() throws InvalidObjectException {
      URL var1 = null;
      String var2 = this.tempState.reconstituteUrlString();

      try {
         var1 = new URL(var2);
      } catch (MalformedURLException var5) {
         this.resetState();
         InvalidObjectException var4 = new InvalidObjectException("Malformed URL: " + var2);
         var4.initCause(var5);
         throw var4;
      }

      var1.setSerializedHashCode(this.tempState.getHashCode());
      this.resetState();
      return var1;
   }

   private boolean isBuiltinStreamHandler(String var1) {
      return var1.startsWith("sun.net.www.protocol");
   }

   private void resetState() {
      this.protocol = null;
      this.host = null;
      this.port = -1;
      this.file = null;
      this.authority = null;
      this.ref = null;
      this.hashCode = -1;
      this.handler = null;
      this.query = null;
      this.path = null;
      this.userInfo = null;
      this.tempState = null;
   }

   private void setSerializedHashCode(int var1) {
      this.hashCode = var1;
   }

   static {
      serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("protocol", String.class), new ObjectStreamField("host", String.class), new ObjectStreamField("port", Integer.TYPE), new ObjectStreamField("authority", String.class), new ObjectStreamField("file", String.class), new ObjectStreamField("ref", String.class), new ObjectStreamField("hashCode", Integer.TYPE)};
   }
}
