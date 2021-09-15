package java.net;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.text.Normalizer;
import sun.nio.cs.ThreadLocalCoders;

public final class URI implements Comparable<URI>, Serializable {
   static final long serialVersionUID = -6052424284110960213L;
   private transient String scheme;
   private transient String fragment;
   private transient String authority;
   private transient String userInfo;
   private transient String host;
   private transient int port;
   private transient String path;
   private transient String query;
   private transient volatile String schemeSpecificPart;
   private transient volatile int hash;
   private transient volatile String decodedUserInfo;
   private transient volatile String decodedAuthority;
   private transient volatile String decodedPath;
   private transient volatile String decodedQuery;
   private transient volatile String decodedFragment;
   private transient volatile String decodedSchemeSpecificPart;
   private volatile String string;
   private static final long L_DIGIT = lowMask('0', '9');
   private static final long H_DIGIT = 0L;
   private static final long L_UPALPHA = 0L;
   private static final long H_UPALPHA = highMask('A', 'Z');
   private static final long L_LOWALPHA = 0L;
   private static final long H_LOWALPHA = highMask('a', 'z');
   private static final long L_ALPHA = 0L;
   private static final long H_ALPHA;
   private static final long L_ALPHANUM;
   private static final long H_ALPHANUM;
   private static final long L_HEX;
   private static final long H_HEX;
   private static final long L_MARK;
   private static final long H_MARK;
   private static final long L_UNRESERVED;
   private static final long H_UNRESERVED;
   private static final long L_RESERVED;
   private static final long H_RESERVED;
   private static final long L_ESCAPED = 1L;
   private static final long H_ESCAPED = 0L;
   private static final long L_URIC;
   private static final long H_URIC;
   private static final long L_PCHAR;
   private static final long H_PCHAR;
   private static final long L_PATH;
   private static final long H_PATH;
   private static final long L_DASH;
   private static final long H_DASH;
   private static final long L_DOT;
   private static final long H_DOT;
   private static final long L_USERINFO;
   private static final long H_USERINFO;
   private static final long L_REG_NAME;
   private static final long H_REG_NAME;
   private static final long L_SERVER;
   private static final long H_SERVER;
   private static final long L_SERVER_PERCENT;
   private static final long H_SERVER_PERCENT;
   private static final long L_LEFT_BRACKET;
   private static final long H_LEFT_BRACKET;
   private static final long L_SCHEME;
   private static final long H_SCHEME;
   private static final long L_URIC_NO_SLASH;
   private static final long H_URIC_NO_SLASH;
   private static final char[] hexDigits;

   private URI() {
      this.port = -1;
      this.decodedUserInfo = null;
      this.decodedAuthority = null;
      this.decodedPath = null;
      this.decodedQuery = null;
      this.decodedFragment = null;
      this.decodedSchemeSpecificPart = null;
   }

   public URI(String var1) throws URISyntaxException {
      this.port = -1;
      this.decodedUserInfo = null;
      this.decodedAuthority = null;
      this.decodedPath = null;
      this.decodedQuery = null;
      this.decodedFragment = null;
      this.decodedSchemeSpecificPart = null;
      (new URI.Parser(var1)).parse(false);
   }

   public URI(String var1, String var2, String var3, int var4, String var5, String var6, String var7) throws URISyntaxException {
      this.port = -1;
      this.decodedUserInfo = null;
      this.decodedAuthority = null;
      this.decodedPath = null;
      this.decodedQuery = null;
      this.decodedFragment = null;
      this.decodedSchemeSpecificPart = null;
      String var8 = this.toString(var1, (String)null, (String)null, var2, var3, var4, var5, var6, var7);
      checkPath(var8, var1, var5);
      (new URI.Parser(var8)).parse(true);
   }

   public URI(String var1, String var2, String var3, String var4, String var5) throws URISyntaxException {
      this.port = -1;
      this.decodedUserInfo = null;
      this.decodedAuthority = null;
      this.decodedPath = null;
      this.decodedQuery = null;
      this.decodedFragment = null;
      this.decodedSchemeSpecificPart = null;
      String var6 = this.toString(var1, (String)null, var2, (String)null, (String)null, -1, var3, var4, var5);
      checkPath(var6, var1, var3);
      (new URI.Parser(var6)).parse(false);
   }

   public URI(String var1, String var2, String var3, String var4) throws URISyntaxException {
      this(var1, (String)null, var2, -1, var3, (String)null, var4);
   }

   public URI(String var1, String var2, String var3) throws URISyntaxException {
      this.port = -1;
      this.decodedUserInfo = null;
      this.decodedAuthority = null;
      this.decodedPath = null;
      this.decodedQuery = null;
      this.decodedFragment = null;
      this.decodedSchemeSpecificPart = null;
      (new URI.Parser(this.toString(var1, var2, (String)null, (String)null, (String)null, -1, (String)null, (String)null, var3))).parse(false);
   }

   public static URI create(String var0) {
      try {
         return new URI(var0);
      } catch (URISyntaxException var2) {
         throw new IllegalArgumentException(var2.getMessage(), var2);
      }
   }

   public URI parseServerAuthority() throws URISyntaxException {
      if (this.host == null && this.authority != null) {
         this.defineString();
         (new URI.Parser(this.string)).parse(true);
         return this;
      } else {
         return this;
      }
   }

   public URI normalize() {
      return normalize(this);
   }

   public URI resolve(URI var1) {
      return resolve(this, var1);
   }

   public URI resolve(String var1) {
      return this.resolve(create(var1));
   }

   public URI relativize(URI var1) {
      return relativize(this, var1);
   }

   public URL toURL() throws MalformedURLException {
      if (!this.isAbsolute()) {
         throw new IllegalArgumentException("URI is not absolute");
      } else {
         return new URL(this.toString());
      }
   }

   public String getScheme() {
      return this.scheme;
   }

   public boolean isAbsolute() {
      return this.scheme != null;
   }

   public boolean isOpaque() {
      return this.path == null;
   }

   public String getRawSchemeSpecificPart() {
      this.defineSchemeSpecificPart();
      return this.schemeSpecificPart;
   }

   public String getSchemeSpecificPart() {
      if (this.decodedSchemeSpecificPart == null) {
         this.decodedSchemeSpecificPart = decode(this.getRawSchemeSpecificPart());
      }

      return this.decodedSchemeSpecificPart;
   }

   public String getRawAuthority() {
      return this.authority;
   }

   public String getAuthority() {
      if (this.decodedAuthority == null) {
         this.decodedAuthority = decode(this.authority);
      }

      return this.decodedAuthority;
   }

   public String getRawUserInfo() {
      return this.userInfo;
   }

   public String getUserInfo() {
      if (this.decodedUserInfo == null && this.userInfo != null) {
         this.decodedUserInfo = decode(this.userInfo);
      }

      return this.decodedUserInfo;
   }

   public String getHost() {
      return this.host;
   }

   public int getPort() {
      return this.port;
   }

   public String getRawPath() {
      return this.path;
   }

   public String getPath() {
      if (this.decodedPath == null && this.path != null) {
         this.decodedPath = decode(this.path);
      }

      return this.decodedPath;
   }

   public String getRawQuery() {
      return this.query;
   }

   public String getQuery() {
      if (this.decodedQuery == null && this.query != null) {
         this.decodedQuery = decode(this.query);
      }

      return this.decodedQuery;
   }

   public String getRawFragment() {
      return this.fragment;
   }

   public String getFragment() {
      if (this.decodedFragment == null && this.fragment != null) {
         this.decodedFragment = decode(this.fragment);
      }

      return this.decodedFragment;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof URI)) {
         return false;
      } else {
         URI var2 = (URI)var1;
         if (this.isOpaque() != var2.isOpaque()) {
            return false;
         } else if (!equalIgnoringCase(this.scheme, var2.scheme)) {
            return false;
         } else if (!equal(this.fragment, var2.fragment)) {
            return false;
         } else if (this.isOpaque()) {
            return equal(this.schemeSpecificPart, var2.schemeSpecificPart);
         } else if (!equal(this.path, var2.path)) {
            return false;
         } else if (!equal(this.query, var2.query)) {
            return false;
         } else if (this.authority == var2.authority) {
            return true;
         } else {
            if (this.host != null) {
               if (!equal(this.userInfo, var2.userInfo)) {
                  return false;
               }

               if (!equalIgnoringCase(this.host, var2.host)) {
                  return false;
               }

               if (this.port != var2.port) {
                  return false;
               }
            } else if (this.authority != null) {
               if (!equal(this.authority, var2.authority)) {
                  return false;
               }
            } else if (this.authority != var2.authority) {
               return false;
            }

            return true;
         }
      }
   }

   public int hashCode() {
      if (this.hash != 0) {
         return this.hash;
      } else {
         int var1 = hashIgnoringCase(0, this.scheme);
         var1 = hash(var1, this.fragment);
         if (this.isOpaque()) {
            var1 = hash(var1, this.schemeSpecificPart);
         } else {
            var1 = hash(var1, this.path);
            var1 = hash(var1, this.query);
            if (this.host != null) {
               var1 = hash(var1, this.userInfo);
               var1 = hashIgnoringCase(var1, this.host);
               var1 += 1949 * this.port;
            } else {
               var1 = hash(var1, this.authority);
            }
         }

         this.hash = var1;
         return var1;
      }
   }

   public int compareTo(URI var1) {
      int var2;
      if ((var2 = compareIgnoringCase(this.scheme, var1.scheme)) != 0) {
         return var2;
      } else if (this.isOpaque()) {
         if (var1.isOpaque()) {
            return (var2 = compare(this.schemeSpecificPart, var1.schemeSpecificPart)) != 0 ? var2 : compare(this.fragment, var1.fragment);
         } else {
            return 1;
         }
      } else if (var1.isOpaque()) {
         return -1;
      } else {
         if (this.host != null && var1.host != null) {
            if ((var2 = compare(this.userInfo, var1.userInfo)) != 0) {
               return var2;
            }

            if ((var2 = compareIgnoringCase(this.host, var1.host)) != 0) {
               return var2;
            }

            if ((var2 = this.port - var1.port) != 0) {
               return var2;
            }
         } else if ((var2 = compare(this.authority, var1.authority)) != 0) {
            return var2;
         }

         if ((var2 = compare(this.path, var1.path)) != 0) {
            return var2;
         } else {
            return (var2 = compare(this.query, var1.query)) != 0 ? var2 : compare(this.fragment, var1.fragment);
         }
      }
   }

   public String toString() {
      this.defineString();
      return this.string;
   }

   public String toASCIIString() {
      this.defineString();
      return encode(this.string);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      this.defineString();
      var1.defaultWriteObject();
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      this.port = -1;
      var1.defaultReadObject();

      try {
         (new URI.Parser(this.string)).parse(false);
      } catch (URISyntaxException var4) {
         InvalidObjectException var3 = new InvalidObjectException("Invalid URI");
         var3.initCause(var4);
         throw var3;
      }
   }

   private static int toLower(char var0) {
      return var0 >= 'A' && var0 <= 'Z' ? var0 + 32 : var0;
   }

   private static int toUpper(char var0) {
      return var0 >= 'a' && var0 <= 'z' ? var0 - 32 : var0;
   }

   private static boolean equal(String var0, String var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 != null && var1 != null) {
         if (var0.length() != var1.length()) {
            return false;
         } else if (var0.indexOf(37) < 0) {
            return var0.equals(var1);
         } else {
            int var2 = var0.length();
            int var3 = 0;

            while(var3 < var2) {
               char var4 = var0.charAt(var3);
               char var5 = var1.charAt(var3);
               if (var4 != '%') {
                  if (var4 != var5) {
                     return false;
                  }

                  ++var3;
               } else {
                  if (var5 != '%') {
                     return false;
                  }

                  ++var3;
                  if (toLower(var0.charAt(var3)) != toLower(var1.charAt(var3))) {
                     return false;
                  }

                  ++var3;
                  if (toLower(var0.charAt(var3)) != toLower(var1.charAt(var3))) {
                     return false;
                  }

                  ++var3;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private static boolean equalIgnoringCase(String var0, String var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 != null && var1 != null) {
         int var2 = var0.length();
         if (var1.length() != var2) {
            return false;
         } else {
            for(int var3 = 0; var3 < var2; ++var3) {
               if (toLower(var0.charAt(var3)) != toLower(var1.charAt(var3))) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private static int hash(int var0, String var1) {
      if (var1 == null) {
         return var0;
      } else {
         return var1.indexOf(37) < 0 ? var0 * 127 + var1.hashCode() : normalizedHash(var0, var1);
      }
   }

   private static int normalizedHash(int var0, String var1) {
      int var2 = 0;

      for(int var3 = 0; var3 < var1.length(); ++var3) {
         char var4 = var1.charAt(var3);
         var2 = 31 * var2 + var4;
         if (var4 == '%') {
            for(int var5 = var3 + 1; var5 < var3 + 3; ++var5) {
               var2 = 31 * var2 + toUpper(var1.charAt(var5));
            }

            var3 += 2;
         }
      }

      return var0 * 127 + var2;
   }

   private static int hashIgnoringCase(int var0, String var1) {
      if (var1 == null) {
         return var0;
      } else {
         int var2 = var0;
         int var3 = var1.length();

         for(int var4 = 0; var4 < var3; ++var4) {
            var2 = 31 * var2 + toLower(var1.charAt(var4));
         }

         return var2;
      }
   }

   private static int compare(String var0, String var1) {
      if (var0 == var1) {
         return 0;
      } else if (var0 != null) {
         return var1 != null ? var0.compareTo(var1) : 1;
      } else {
         return -1;
      }
   }

   private static int compareIgnoringCase(String var0, String var1) {
      if (var0 == var1) {
         return 0;
      } else if (var0 != null) {
         if (var1 != null) {
            int var2 = var0.length();
            int var3 = var1.length();
            int var4 = var2 < var3 ? var2 : var3;

            for(int var5 = 0; var5 < var4; ++var5) {
               int var6 = toLower(var0.charAt(var5)) - toLower(var1.charAt(var5));
               if (var6 != 0) {
                  return var6;
               }
            }

            return var2 - var3;
         } else {
            return 1;
         }
      } else {
         return -1;
      }
   }

   private static void checkPath(String var0, String var1, String var2) throws URISyntaxException {
      if (var1 != null && var2 != null && var2.length() > 0 && var2.charAt(0) != '/') {
         throw new URISyntaxException(var0, "Relative path in absolute URI");
      }
   }

   private void appendAuthority(StringBuffer var1, String var2, String var3, String var4, int var5) {
      if (var4 != null) {
         var1.append("//");
         if (var3 != null) {
            var1.append(quote(var3, L_USERINFO, H_USERINFO));
            var1.append('@');
         }

         boolean var6 = var4.indexOf(58) >= 0 && !var4.startsWith("[") && !var4.endsWith("]");
         if (var6) {
            var1.append('[');
         }

         var1.append(var4);
         if (var6) {
            var1.append(']');
         }

         if (var5 != -1) {
            var1.append(':');
            var1.append(var5);
         }
      } else if (var2 != null) {
         var1.append("//");
         if (var2.startsWith("[")) {
            int var9 = var2.indexOf("]");
            String var7 = var2;
            String var8 = "";
            if (var9 != -1 && var2.indexOf(":") != -1) {
               if (var9 == var2.length()) {
                  var8 = var2;
                  var7 = "";
               } else {
                  var8 = var2.substring(0, var9 + 1);
                  var7 = var2.substring(var9 + 1);
               }
            }

            var1.append(var8);
            var1.append(quote(var7, L_REG_NAME | L_SERVER, H_REG_NAME | H_SERVER));
         } else {
            var1.append(quote(var2, L_REG_NAME | L_SERVER, H_REG_NAME | H_SERVER));
         }
      }

   }

   private void appendSchemeSpecificPart(StringBuffer var1, String var2, String var3, String var4, String var5, int var6, String var7, String var8) {
      if (var2 != null) {
         if (var2.startsWith("//[")) {
            int var9 = var2.indexOf("]");
            if (var9 != -1 && var2.indexOf(":") != -1) {
               String var10;
               String var11;
               if (var9 == var2.length()) {
                  var11 = var2;
                  var10 = "";
               } else {
                  var11 = var2.substring(0, var9 + 1);
                  var10 = var2.substring(var9 + 1);
               }

               var1.append(var11);
               var1.append(quote(var10, L_URIC, H_URIC));
            }
         } else {
            var1.append(quote(var2, L_URIC, H_URIC));
         }
      } else {
         this.appendAuthority(var1, var3, var4, var5, var6);
         if (var7 != null) {
            var1.append(quote(var7, L_PATH, H_PATH));
         }

         if (var8 != null) {
            var1.append('?');
            var1.append(quote(var8, L_URIC, H_URIC));
         }
      }

   }

   private void appendFragment(StringBuffer var1, String var2) {
      if (var2 != null) {
         var1.append('#');
         var1.append(quote(var2, L_URIC, H_URIC));
      }

   }

   private String toString(String var1, String var2, String var3, String var4, String var5, int var6, String var7, String var8, String var9) {
      StringBuffer var10 = new StringBuffer();
      if (var1 != null) {
         var10.append(var1);
         var10.append(':');
      }

      this.appendSchemeSpecificPart(var10, var2, var3, var4, var5, var6, var7, var8);
      this.appendFragment(var10, var9);
      return var10.toString();
   }

   private void defineSchemeSpecificPart() {
      if (this.schemeSpecificPart == null) {
         StringBuffer var1 = new StringBuffer();
         this.appendSchemeSpecificPart(var1, (String)null, this.getAuthority(), this.getUserInfo(), this.host, this.port, this.getPath(), this.getQuery());
         if (var1.length() != 0) {
            this.schemeSpecificPart = var1.toString();
         }
      }
   }

   private void defineString() {
      if (this.string == null) {
         StringBuffer var1 = new StringBuffer();
         if (this.scheme != null) {
            var1.append(this.scheme);
            var1.append(':');
         }

         if (this.isOpaque()) {
            var1.append(this.schemeSpecificPart);
         } else {
            if (this.host != null) {
               var1.append("//");
               if (this.userInfo != null) {
                  var1.append(this.userInfo);
                  var1.append('@');
               }

               boolean var2 = this.host.indexOf(58) >= 0 && !this.host.startsWith("[") && !this.host.endsWith("]");
               if (var2) {
                  var1.append('[');
               }

               var1.append(this.host);
               if (var2) {
                  var1.append(']');
               }

               if (this.port != -1) {
                  var1.append(':');
                  var1.append(this.port);
               }
            } else if (this.authority != null) {
               var1.append("//");
               var1.append(this.authority);
            }

            if (this.path != null) {
               var1.append(this.path);
            }

            if (this.query != null) {
               var1.append('?');
               var1.append(this.query);
            }
         }

         if (this.fragment != null) {
            var1.append('#');
            var1.append(this.fragment);
         }

         this.string = var1.toString();
      }
   }

   private static String resolvePath(String var0, String var1, boolean var2) {
      int var3 = var0.lastIndexOf(47);
      int var4 = var1.length();
      String var5 = "";
      if (var4 == 0) {
         if (var3 >= 0) {
            var5 = var0.substring(0, var3 + 1);
         }
      } else {
         StringBuffer var6 = new StringBuffer(var0.length() + var4);
         if (var3 >= 0) {
            var6.append(var0.substring(0, var3 + 1));
         }

         var6.append(var1);
         var5 = var6.toString();
      }

      String var7 = normalize(var5);
      return var7;
   }

   private static URI resolve(URI var0, URI var1) {
      if (!var1.isOpaque() && !var0.isOpaque()) {
         URI var2;
         if (var1.scheme == null && var1.authority == null && var1.path.equals("") && var1.fragment != null && var1.query == null) {
            if (var0.fragment != null && var1.fragment.equals(var0.fragment)) {
               return var0;
            } else {
               var2 = new URI();
               var2.scheme = var0.scheme;
               var2.authority = var0.authority;
               var2.userInfo = var0.userInfo;
               var2.host = var0.host;
               var2.port = var0.port;
               var2.path = var0.path;
               var2.fragment = var1.fragment;
               var2.query = var0.query;
               return var2;
            }
         } else if (var1.scheme != null) {
            return var1;
         } else {
            var2 = new URI();
            var2.scheme = var0.scheme;
            var2.query = var1.query;
            var2.fragment = var1.fragment;
            if (var1.authority == null) {
               var2.authority = var0.authority;
               var2.host = var0.host;
               var2.userInfo = var0.userInfo;
               var2.port = var0.port;
               String var3 = var1.path == null ? "" : var1.path;
               if (var3.length() > 0 && var3.charAt(0) == '/') {
                  var2.path = var1.path;
               } else {
                  var2.path = resolvePath(var0.path, var3, var0.isAbsolute());
               }
            } else {
               var2.authority = var1.authority;
               var2.host = var1.host;
               var2.userInfo = var1.userInfo;
               var2.host = var1.host;
               var2.port = var1.port;
               var2.path = var1.path;
            }

            return var2;
         }
      } else {
         return var1;
      }
   }

   private static URI normalize(URI var0) {
      if (!var0.isOpaque() && var0.path != null && var0.path.length() != 0) {
         String var1 = normalize(var0.path);
         if (var1 == var0.path) {
            return var0;
         } else {
            URI var2 = new URI();
            var2.scheme = var0.scheme;
            var2.fragment = var0.fragment;
            var2.authority = var0.authority;
            var2.userInfo = var0.userInfo;
            var2.host = var0.host;
            var2.port = var0.port;
            var2.path = var1;
            var2.query = var0.query;
            return var2;
         }
      } else {
         return var0;
      }
   }

   private static URI relativize(URI var0, URI var1) {
      if (!var1.isOpaque() && !var0.isOpaque()) {
         if (equalIgnoringCase(var0.scheme, var1.scheme) && equal(var0.authority, var1.authority)) {
            String var2 = normalize(var0.path);
            String var3 = normalize(var1.path);
            if (!var2.equals(var3)) {
               if (!var2.endsWith("/")) {
                  var2 = var2 + "/";
               }

               if (!var3.startsWith(var2)) {
                  return var1;
               }
            }

            URI var4 = new URI();
            var4.path = var3.substring(var2.length());
            var4.query = var1.query;
            var4.fragment = var1.fragment;
            return var4;
         } else {
            return var1;
         }
      } else {
         return var1;
      }
   }

   private static int needsNormalization(String var0) {
      boolean var1 = true;
      int var2 = 0;
      int var3 = var0.length() - 1;

      int var4;
      for(var4 = 0; var4 <= var3 && var0.charAt(var4) == '/'; ++var4) {
      }

      if (var4 > 1) {
         var1 = false;
      }

      while(true) {
         while(var4 <= var3) {
            if (var0.charAt(var4) == '.' && (var4 == var3 || var0.charAt(var4 + 1) == '/' || var0.charAt(var4 + 1) == '.' && (var4 + 1 == var3 || var0.charAt(var4 + 2) == '/'))) {
               var1 = false;
            }

            ++var2;

            while(var4 <= var3) {
               if (var0.charAt(var4++) == '/') {
                  while(var4 <= var3 && var0.charAt(var4) == '/') {
                     var1 = false;
                     ++var4;
                  }
                  break;
               }
            }
         }

         return var1 ? -1 : var2;
      }
   }

   private static void split(char[] var0, int[] var1) {
      int var2 = var0.length - 1;
      int var3 = 0;

      int var4;
      for(var4 = 0; var3 <= var2 && var0[var3] == '/'; ++var3) {
         var0[var3] = 0;
      }

      while(true) {
         while(var3 <= var2) {
            var1[var4++] = var3++;

            while(var3 <= var2) {
               if (var0[var3++] == '/') {
                  for(var0[var3 - 1] = 0; var3 <= var2 && var0[var3] == '/'; var0[var3++] = 0) {
                  }
                  break;
               }
            }
         }

         if (var4 != var1.length) {
            throw new InternalError();
         }

         return;
      }
   }

   private static int join(char[] var0, int[] var1) {
      int var2 = var1.length;
      int var3 = var0.length - 1;
      int var4 = 0;
      if (var0[var4] == 0) {
         var0[var4++] = '/';
      }

      for(int var5 = 0; var5 < var2; ++var5) {
         int var6 = var1[var5];
         if (var6 != -1) {
            if (var4 == var6) {
               while(var4 <= var3 && var0[var4] != 0) {
                  ++var4;
               }

               if (var4 <= var3) {
                  var0[var4++] = '/';
               }
            } else {
               if (var4 >= var6) {
                  throw new InternalError();
               }

               while(var6 <= var3 && var0[var6] != 0) {
                  var0[var4++] = var0[var6++];
               }

               if (var6 <= var3) {
                  var0[var4++] = '/';
               }
            }
         }
      }

      return var4;
   }

   private static void removeDots(char[] var0, int[] var1) {
      int var2 = var1.length;
      int var3 = var0.length - 1;

      for(int var4 = 0; var4 < var2; ++var4) {
         byte var5 = 0;

         int var6;
         do {
            var6 = var1[var4];
            if (var0[var6] == '.') {
               if (var6 == var3) {
                  var5 = 1;
                  break;
               }

               if (var0[var6 + 1] == 0) {
                  var5 = 1;
                  break;
               }

               if (var0[var6 + 1] == '.' && (var6 + 1 == var3 || var0[var6 + 2] == 0)) {
                  var5 = 2;
                  break;
               }
            }

            ++var4;
         } while(var4 < var2);

         if (var4 > var2 || var5 == 0) {
            break;
         }

         if (var5 == 1) {
            var1[var4] = -1;
         } else {
            for(var6 = var4 - 1; var6 >= 0 && var1[var6] == -1; --var6) {
            }

            if (var6 >= 0) {
               int var7 = var1[var6];
               if (var0[var7] != '.' || var0[var7 + 1] != '.' || var0[var7 + 2] != 0) {
                  var1[var4] = -1;
                  var1[var6] = -1;
               }
            }
         }
      }

   }

   private static void maybeAddLeadingDot(char[] var0, int[] var1) {
      if (var0[0] != 0) {
         int var2 = var1.length;

         int var3;
         for(var3 = 0; var3 < var2 && var1[var3] < 0; ++var3) {
         }

         if (var3 < var2 && var3 != 0) {
            int var4;
            for(var4 = var1[var3]; var4 < var0.length && var0[var4] != ':' && var0[var4] != 0; ++var4) {
            }

            if (var4 < var0.length && var0[var4] != 0) {
               var0[0] = '.';
               var0[1] = 0;
               var1[0] = 0;
            }
         }
      }
   }

   private static String normalize(String var0) {
      int var1 = needsNormalization(var0);
      if (var1 < 0) {
         return var0;
      } else {
         char[] var2 = var0.toCharArray();
         int[] var3 = new int[var1];
         split(var2, var3);
         removeDots(var2, var3);
         maybeAddLeadingDot(var2, var3);
         String var4 = new String(var2, 0, join(var2, var3));
         return var4.equals(var0) ? var0 : var4;
      }
   }

   private static long lowMask(String var0) {
      int var1 = var0.length();
      long var2 = 0L;

      for(int var4 = 0; var4 < var1; ++var4) {
         char var5 = var0.charAt(var4);
         if (var5 < '@') {
            var2 |= 1L << var5;
         }
      }

      return var2;
   }

   private static long highMask(String var0) {
      int var1 = var0.length();
      long var2 = 0L;

      for(int var4 = 0; var4 < var1; ++var4) {
         char var5 = var0.charAt(var4);
         if (var5 >= '@' && var5 < 128) {
            var2 |= 1L << var5 - 64;
         }
      }

      return var2;
   }

   private static long lowMask(char var0, char var1) {
      long var2 = 0L;
      int var4 = Math.max(Math.min(var0, 63), 0);
      int var5 = Math.max(Math.min(var1, 63), 0);

      for(int var6 = var4; var6 <= var5; ++var6) {
         var2 |= 1L << var6;
      }

      return var2;
   }

   private static long highMask(char var0, char var1) {
      long var2 = 0L;
      int var4 = Math.max(Math.min(var0, 127), 64) - 64;
      int var5 = Math.max(Math.min(var1, 127), 64) - 64;

      for(int var6 = var4; var6 <= var5; ++var6) {
         var2 |= 1L << var6;
      }

      return var2;
   }

   private static boolean match(char var0, long var1, long var3) {
      if (var0 == 0) {
         return false;
      } else if (var0 < '@') {
         return (1L << var0 & var1) != 0L;
      } else if (var0 < 128) {
         return (1L << var0 - 64 & var3) != 0L;
      } else {
         return false;
      }
   }

   private static void appendEscape(StringBuffer var0, byte var1) {
      var0.append('%');
      var0.append(hexDigits[var1 >> 4 & 15]);
      var0.append(hexDigits[var1 >> 0 & 15]);
   }

   private static void appendEncoded(StringBuffer var0, char var1) {
      ByteBuffer var2 = null;

      try {
         var2 = ThreadLocalCoders.encoderFor("UTF-8").encode(CharBuffer.wrap((CharSequence)("" + var1)));
      } catch (CharacterCodingException var4) {
         assert false;
      }

      while(var2.hasRemaining()) {
         int var3 = var2.get() & 255;
         if (var3 >= 128) {
            appendEscape(var0, (byte)var3);
         } else {
            var0.append((char)var3);
         }
      }

   }

   private static String quote(String var0, long var1, long var3) {
      int var5 = var0.length();
      StringBuffer var6 = null;
      boolean var7 = (var1 & 1L) != 0L;

      for(int var8 = 0; var8 < var0.length(); ++var8) {
         char var9 = var0.charAt(var8);
         if (var9 < 128) {
            if (!match(var9, var1, var3)) {
               if (var6 == null) {
                  var6 = new StringBuffer();
                  var6.append(var0.substring(0, var8));
               }

               appendEscape(var6, (byte)var9);
            } else if (var6 != null) {
               var6.append(var9);
            }
         } else if (var7 && (Character.isSpaceChar(var9) || Character.isISOControl(var9))) {
            if (var6 == null) {
               var6 = new StringBuffer();
               var6.append(var0.substring(0, var8));
            }

            appendEncoded(var6, var9);
         } else if (var6 != null) {
            var6.append(var9);
         }
      }

      return var6 == null ? var0 : var6.toString();
   }

   private static String encode(String var0) {
      int var1 = var0.length();
      if (var1 == 0) {
         return var0;
      } else {
         int var2 = 0;

         while(var0.charAt(var2) < 128) {
            ++var2;
            if (var2 >= var1) {
               return var0;
            }
         }

         String var7 = Normalizer.normalize(var0, Normalizer.Form.NFC);
         ByteBuffer var3 = null;

         try {
            var3 = ThreadLocalCoders.encoderFor("UTF-8").encode(CharBuffer.wrap((CharSequence)var7));
         } catch (CharacterCodingException var6) {
            assert false;
         }

         StringBuffer var4 = new StringBuffer();

         while(var3.hasRemaining()) {
            int var5 = var3.get() & 255;
            if (var5 >= 128) {
               appendEscape(var4, (byte)var5);
            } else {
               var4.append((char)var5);
            }
         }

         return var4.toString();
      }
   }

   private static int decode(char var0) {
      if (var0 >= '0' && var0 <= '9') {
         return var0 - 48;
      } else if (var0 >= 'a' && var0 <= 'f') {
         return var0 - 97 + 10;
      } else if (var0 >= 'A' && var0 <= 'F') {
         return var0 - 65 + 10;
      } else {
         assert false;

         return -1;
      }
   }

   private static byte decode(char var0, char var1) {
      return (byte)((decode(var0) & 15) << 4 | (decode(var1) & 15) << 0);
   }

   private static String decode(String var0) {
      if (var0 == null) {
         return var0;
      } else {
         int var1 = var0.length();
         if (var1 == 0) {
            return var0;
         } else if (var0.indexOf(37) < 0) {
            return var0;
         } else {
            StringBuffer var2 = new StringBuffer(var1);
            ByteBuffer var3 = ByteBuffer.allocate(var1);
            CharBuffer var4 = CharBuffer.allocate(var1);
            CharsetDecoder var5 = ThreadLocalCoders.decoderFor("UTF-8").onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
            char var6 = var0.charAt(0);
            boolean var7 = false;
            int var8 = 0;

            while(var8 < var1) {
               assert var6 == var0.charAt(var8);

               if (var6 == '[') {
                  var7 = true;
               } else if (var7 && var6 == ']') {
                  var7 = false;
               }

               if (var6 == '%' && !var7) {
                  var3.clear();

                  do {
                     assert var1 - var8 >= 2;

                     ++var8;
                     char var10001 = var0.charAt(var8);
                     ++var8;
                     var3.put(decode(var10001, var0.charAt(var8)));
                     ++var8;
                     if (var8 >= var1) {
                        break;
                     }

                     var6 = var0.charAt(var8);
                  } while(var6 == '%');

                  var3.flip();
                  var4.clear();
                  var5.reset();
                  CoderResult var10 = var5.decode(var3, var4, true);

                  assert var10.isUnderflow();

                  var10 = var5.flush(var4);

                  assert var10.isUnderflow();

                  var2.append(var4.flip().toString());
               } else {
                  var2.append(var6);
                  ++var8;
                  if (var8 >= var1) {
                     break;
                  }

                  var6 = var0.charAt(var8);
               }
            }

            return var2.toString();
         }
      }
   }

   static {
      H_ALPHA = H_LOWALPHA | H_UPALPHA;
      L_ALPHANUM = L_DIGIT | 0L;
      H_ALPHANUM = 0L | H_ALPHA;
      L_HEX = L_DIGIT;
      H_HEX = highMask('A', 'F') | highMask('a', 'f');
      L_MARK = lowMask("-_.!~*'()");
      H_MARK = highMask("-_.!~*'()");
      L_UNRESERVED = L_ALPHANUM | L_MARK;
      H_UNRESERVED = H_ALPHANUM | H_MARK;
      L_RESERVED = lowMask(";/?:@&=+$,[]");
      H_RESERVED = highMask(";/?:@&=+$,[]");
      L_URIC = L_RESERVED | L_UNRESERVED | 1L;
      H_URIC = H_RESERVED | H_UNRESERVED | 0L;
      L_PCHAR = L_UNRESERVED | 1L | lowMask(":@&=+$,");
      H_PCHAR = H_UNRESERVED | 0L | highMask(":@&=+$,");
      L_PATH = L_PCHAR | lowMask(";/");
      H_PATH = H_PCHAR | highMask(";/");
      L_DASH = lowMask("-");
      H_DASH = highMask("-");
      L_DOT = lowMask(".");
      H_DOT = highMask(".");
      L_USERINFO = L_UNRESERVED | 1L | lowMask(";:&=+$,");
      H_USERINFO = H_UNRESERVED | 0L | highMask(";:&=+$,");
      L_REG_NAME = L_UNRESERVED | 1L | lowMask("$,;:@&=+");
      H_REG_NAME = H_UNRESERVED | 0L | highMask("$,;:@&=+");
      L_SERVER = L_USERINFO | L_ALPHANUM | L_DASH | lowMask(".:@[]");
      H_SERVER = H_USERINFO | H_ALPHANUM | H_DASH | highMask(".:@[]");
      L_SERVER_PERCENT = L_SERVER | lowMask("%");
      H_SERVER_PERCENT = H_SERVER | highMask("%");
      L_LEFT_BRACKET = lowMask("[");
      H_LEFT_BRACKET = highMask("[");
      L_SCHEME = 0L | L_DIGIT | lowMask("+-.");
      H_SCHEME = H_ALPHA | 0L | highMask("+-.");
      L_URIC_NO_SLASH = L_UNRESERVED | 1L | lowMask(";?:@&=+$,");
      H_URIC_NO_SLASH = H_UNRESERVED | 0L | highMask(";?:@&=+$,");
      hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
   }

   private class Parser {
      private String input;
      private boolean requireServerAuthority = false;
      private int ipv6byteCount = 0;

      Parser(String var2) {
         this.input = var2;
         URI.this.string = var2;
      }

      private void fail(String var1) throws URISyntaxException {
         throw new URISyntaxException(this.input, var1);
      }

      private void fail(String var1, int var2) throws URISyntaxException {
         throw new URISyntaxException(this.input, var1, var2);
      }

      private void failExpecting(String var1, int var2) throws URISyntaxException {
         this.fail("Expected " + var1, var2);
      }

      private void failExpecting(String var1, String var2, int var3) throws URISyntaxException {
         this.fail("Expected " + var1 + " following " + var2, var3);
      }

      private String substring(int var1, int var2) {
         return this.input.substring(var1, var2);
      }

      private char charAt(int var1) {
         return this.input.charAt(var1);
      }

      private boolean at(int var1, int var2, char var3) {
         return var1 < var2 && this.charAt(var1) == var3;
      }

      private boolean at(int var1, int var2, String var3) {
         int var4 = var1;
         int var5 = var3.length();
         if (var5 > var2 - var1) {
            return false;
         } else {
            int var6;
            for(var6 = 0; var6 < var5 && this.charAt(var4++) == var3.charAt(var6); ++var6) {
            }

            return var6 == var5;
         }
      }

      private int scan(int var1, int var2, char var3) {
         return var1 < var2 && this.charAt(var1) == var3 ? var1 + 1 : var1;
      }

      private int scan(int var1, int var2, String var3, String var4) {
         int var5;
         for(var5 = var1; var5 < var2; ++var5) {
            char var6 = this.charAt(var5);
            if (var3.indexOf(var6) >= 0) {
               return -1;
            }

            if (var4.indexOf(var6) >= 0) {
               break;
            }
         }

         return var5;
      }

      private int scanEscape(int var1, int var2, char var3) throws URISyntaxException {
         if (var3 == '%') {
            if (var1 + 3 <= var2 && URI.match(this.charAt(var1 + 1), URI.L_HEX, URI.H_HEX) && URI.match(this.charAt(var1 + 2), URI.L_HEX, URI.H_HEX)) {
               return var1 + 3;
            }

            this.fail("Malformed escape pair", var1);
         } else if (var3 > 128 && !Character.isSpaceChar(var3) && !Character.isISOControl(var3)) {
            return var1 + 1;
         }

         return var1;
      }

      private int scan(int var1, int var2, long var3, long var5) throws URISyntaxException {
         int var7 = var1;

         while(var7 < var2) {
            char var8 = this.charAt(var7);
            if (URI.match(var8, var3, var5)) {
               ++var7;
            } else {
               if ((var3 & 1L) == 0L) {
                  break;
               }

               int var9 = this.scanEscape(var7, var2, var8);
               if (var9 <= var7) {
                  break;
               }

               var7 = var9;
            }
         }

         return var7;
      }

      private void checkChars(int var1, int var2, long var3, long var5, String var7) throws URISyntaxException {
         int var8 = this.scan(var1, var2, var3, var5);
         if (var8 < var2) {
            this.fail("Illegal character in " + var7, var8);
         }

      }

      private void checkChar(int var1, long var2, long var4, String var6) throws URISyntaxException {
         this.checkChars(var1, var1 + 1, var2, var4, var6);
      }

      void parse(boolean var1) throws URISyntaxException {
         this.requireServerAuthority = var1;
         int var3 = this.input.length();
         int var4 = this.scan(0, var3, "/?#", ":");
         int var2;
         if (var4 >= 0 && this.at(var4, var3, ':')) {
            if (var4 == 0) {
               this.failExpecting("scheme name", 0);
            }

            this.checkChar(0, 0L, URI.H_ALPHA, "scheme name");
            this.checkChars(1, var4, URI.L_SCHEME, URI.H_SCHEME, "scheme name");
            URI.this.scheme = this.substring(0, var4);
            ++var4;
            var2 = var4;
            if (this.at(var4, var3, '/')) {
               var4 = this.parseHierarchical(var4, var3);
            } else {
               int var5 = this.scan(var4, var3, "", "#");
               if (var5 <= var4) {
                  this.failExpecting("scheme-specific part", var4);
               }

               this.checkChars(var4, var5, URI.L_URIC, URI.H_URIC, "opaque part");
               var4 = var5;
            }
         } else {
            var2 = 0;
            var4 = this.parseHierarchical(0, var3);
         }

         URI.this.schemeSpecificPart = this.substring(var2, var4);
         if (this.at(var4, var3, '#')) {
            this.checkChars(var4 + 1, var3, URI.L_URIC, URI.H_URIC, "fragment");
            URI.this.fragment = this.substring(var4 + 1, var3);
            var4 = var3;
         }

         if (var4 < var3) {
            this.fail("end of URI", var4);
         }

      }

      private int parseHierarchical(int var1, int var2) throws URISyntaxException {
         int var3 = var1;
         int var4;
         if (this.at(var1, var2, '/') && this.at(var1 + 1, var2, '/')) {
            var3 = var1 + 2;
            var4 = this.scan(var3, var2, "", "/?#");
            if (var4 > var3) {
               var3 = this.parseAuthority(var3, var4);
            } else if (var4 >= var2) {
               this.failExpecting("authority", var3);
            }
         }

         var4 = this.scan(var3, var2, "", "?#");
         this.checkChars(var3, var4, URI.L_PATH, URI.H_PATH, "path");
         URI.this.path = this.substring(var3, var4);
         var3 = var4;
         if (this.at(var4, var2, '?')) {
            var3 = var4 + 1;
            var4 = this.scan(var3, var2, "", "#");
            this.checkChars(var3, var4, URI.L_URIC, URI.H_URIC, "query");
            URI.this.query = this.substring(var3, var4);
            var3 = var4;
         }

         return var3;
      }

      private int parseAuthority(int var1, int var2) throws URISyntaxException {
         int var3 = var1;
         int var4 = var1;
         URISyntaxException var5 = null;
         boolean var6;
         if (this.scan(var1, var2, "", "]") > var1) {
            var6 = this.scan(var1, var2, URI.L_SERVER_PERCENT, URI.H_SERVER_PERCENT) == var2;
         } else {
            var6 = this.scan(var1, var2, URI.L_SERVER, URI.H_SERVER) == var2;
         }

         boolean var7 = this.scan(var1, var2, URI.L_REG_NAME, URI.H_REG_NAME) == var2;
         if (var7 && !var6) {
            URI.this.authority = this.substring(var1, var2);
            return var2;
         } else {
            if (var6) {
               try {
                  var4 = this.parseServer(var3, var2);
                  if (var4 < var2) {
                     this.failExpecting("end of authority", var4);
                  }

                  URI.this.authority = this.substring(var3, var2);
               } catch (URISyntaxException var9) {
                  URI.this.userInfo = null;
                  URI.this.host = null;
                  URI.this.port = -1;
                  if (this.requireServerAuthority) {
                     throw var9;
                  }

                  var5 = var9;
                  var4 = var1;
               }
            }

            if (var4 < var2) {
               if (var7) {
                  URI.this.authority = this.substring(var1, var2);
               } else {
                  if (var5 != null) {
                     throw var5;
                  }

                  this.fail("Illegal character in authority", var4);
               }
            }

            return var2;
         }
      }

      private int parseServer(int var1, int var2) throws URISyntaxException {
         int var3 = var1;
         int var4 = this.scan(var1, var2, "/?#", "@");
         if (var4 >= var1 && this.at(var4, var2, '@')) {
            this.checkChars(var1, var4, URI.L_USERINFO, URI.H_USERINFO, "user info");
            URI.this.userInfo = this.substring(var1, var4);
            var3 = var4 + 1;
         }

         if (this.at(var3, var2, '[')) {
            ++var3;
            var4 = this.scan(var3, var2, "/?#", "]");
            if (var4 > var3 && this.at(var4, var2, ']')) {
               int var5 = this.scan(var3, var4, "", "%");
               if (var5 > var3) {
                  this.parseIPv6Reference(var3, var5);
                  if (var5 + 1 == var4) {
                     this.fail("scope id expected");
                  }

                  this.checkChars(var5 + 1, var4, URI.L_ALPHANUM, URI.H_ALPHANUM, "scope id");
               } else {
                  this.parseIPv6Reference(var3, var4);
               }

               URI.this.host = this.substring(var3 - 1, var4 + 1);
               var3 = var4 + 1;
            } else {
               this.failExpecting("closing bracket for IPv6 address", var4);
            }
         } else {
            var4 = this.parseIPv4Address(var3, var2);
            if (var4 <= var3) {
               var4 = this.parseHostname(var3, var2);
            }

            var3 = var4;
         }

         if (this.at(var3, var2, ':')) {
            ++var3;
            var4 = this.scan(var3, var2, "", "/");
            if (var4 > var3) {
               this.checkChars(var3, var4, URI.L_DIGIT, 0L, "port number");

               try {
                  URI.this.port = Integer.parseInt(this.substring(var3, var4));
               } catch (NumberFormatException var6) {
                  this.fail("Malformed port number", var3);
               }

               var3 = var4;
            }
         }

         if (var3 < var2) {
            this.failExpecting("port number", var3);
         }

         return var3;
      }

      private int scanByte(int var1, int var2) throws URISyntaxException {
         int var4 = this.scan(var1, var2, URI.L_DIGIT, 0L);
         if (var4 <= var1) {
            return var4;
         } else {
            return Integer.parseInt(this.substring(var1, var4)) > 255 ? var1 : var4;
         }
      }

      private int scanIPv4Address(int var1, int var2, boolean var3) throws URISyntaxException {
         int var6 = this.scan(var1, var2, URI.L_DIGIT | URI.L_DOT, 0L | URI.H_DOT);
         if (var6 <= var1 || var3 && var6 != var2) {
            return -1;
         } else {
            int var5;
            if ((var5 = this.scanByte(var1, var6)) > var1) {
               int var4 = var5;
               if ((var5 = this.scan(var5, var6, '.')) > var4) {
                  var4 = var5;
                  if ((var5 = this.scanByte(var5, var6)) > var4) {
                     var4 = var5;
                     if ((var5 = this.scan(var5, var6, '.')) > var4) {
                        var4 = var5;
                        if ((var5 = this.scanByte(var5, var6)) > var4) {
                           var4 = var5;
                           if ((var5 = this.scan(var5, var6, '.')) > var4) {
                              var4 = var5;
                              if ((var5 = this.scanByte(var5, var6)) > var4 && var5 >= var6) {
                                 return var5;
                              }
                           }
                        }
                     }
                  }
               }
            }

            this.fail("Malformed IPv4 address", var5);
            return -1;
         }
      }

      private int takeIPv4Address(int var1, int var2, String var3) throws URISyntaxException {
         int var4 = this.scanIPv4Address(var1, var2, true);
         if (var4 <= var1) {
            this.failExpecting(var3, var1);
         }

         return var4;
      }

      private int parseIPv4Address(int var1, int var2) {
         int var3;
         try {
            var3 = this.scanIPv4Address(var1, var2, false);
         } catch (URISyntaxException var5) {
            return -1;
         } catch (NumberFormatException var6) {
            return -1;
         }

         if (var3 > var1 && var3 < var2 && this.charAt(var3) != ':') {
            var3 = -1;
         }

         if (var3 > var1) {
            URI.this.host = this.substring(var1, var3);
         }

         return var3;
      }

      private int parseHostname(int var1, int var2) throws URISyntaxException {
         int var3 = var1;
         int var5 = -1;

         int var4;
         do {
            var4 = this.scan(var3, var2, URI.L_ALPHANUM, URI.H_ALPHANUM);
            if (var4 <= var3) {
               break;
            }

            var5 = var3;
            if (var4 > var3) {
               var3 = var4;
               var4 = this.scan(var4, var2, URI.L_ALPHANUM | URI.L_DASH, URI.H_ALPHANUM | URI.H_DASH);
               if (var4 > var3) {
                  if (this.charAt(var4 - 1) == '-') {
                     this.fail("Illegal character in hostname", var4 - 1);
                  }

                  var3 = var4;
               }
            }

            var4 = this.scan(var3, var2, '.');
            if (var4 <= var3) {
               break;
            }

            var3 = var4;
         } while(var4 < var2);

         if (var3 < var2 && !this.at(var3, var2, ':')) {
            this.fail("Illegal character in hostname", var3);
         }

         if (var5 < 0) {
            this.failExpecting("hostname", var1);
         }

         if (var5 > var1 && !URI.match(this.charAt(var5), 0L, URI.H_ALPHA)) {
            this.fail("Illegal character in hostname", var5);
         }

         URI.this.host = this.substring(var1, var3);
         return var3;
      }

      private int parseIPv6Reference(int var1, int var2) throws URISyntaxException {
         int var3 = var1;
         boolean var5 = false;
         int var4 = this.scanHexSeq(var1, var2);
         if (var4 > var1) {
            var3 = var4;
            if (this.at(var4, var2, "::")) {
               var5 = true;
               var3 = this.scanHexPost(var4 + 2, var2);
            } else if (this.at(var4, var2, ':')) {
               var3 = this.takeIPv4Address(var4 + 1, var2, "IPv4 address");
               this.ipv6byteCount += 4;
            }
         } else if (this.at(var1, var2, "::")) {
            var5 = true;
            var3 = this.scanHexPost(var1 + 2, var2);
         }

         if (var3 < var2) {
            this.fail("Malformed IPv6 address", var1);
         }

         if (this.ipv6byteCount > 16) {
            this.fail("IPv6 address too long", var1);
         }

         if (!var5 && this.ipv6byteCount < 16) {
            this.fail("IPv6 address too short", var1);
         }

         if (var5 && this.ipv6byteCount == 16) {
            this.fail("Malformed IPv6 address", var1);
         }

         return var3;
      }

      private int scanHexPost(int var1, int var2) throws URISyntaxException {
         if (var1 == var2) {
            return var1;
         } else {
            int var4 = this.scanHexSeq(var1, var2);
            int var3;
            if (var4 > var1) {
               var3 = var4;
               if (this.at(var4, var2, ':')) {
                  var3 = var4 + 1;
                  var3 = this.takeIPv4Address(var3, var2, "hex digits or IPv4 address");
                  this.ipv6byteCount += 4;
               }
            } else {
               var3 = this.takeIPv4Address(var1, var2, "hex digits or IPv4 address");
               this.ipv6byteCount += 4;
            }

            return var3;
         }
      }

      private int scanHexSeq(int var1, int var2) throws URISyntaxException {
         int var4 = this.scan(var1, var2, URI.L_HEX, URI.H_HEX);
         if (var4 <= var1) {
            return -1;
         } else if (this.at(var4, var2, '.')) {
            return -1;
         } else {
            if (var4 > var1 + 4) {
               this.fail("IPv6 hexadecimal digit sequence too long", var1);
            }

            this.ipv6byteCount += 2;

            int var3;
            for(var3 = var4; var3 < var2 && this.at(var3, var2, ':') && !this.at(var3 + 1, var2, ':'); var3 = var4) {
               ++var3;
               var4 = this.scan(var3, var2, URI.L_HEX, URI.H_HEX);
               if (var4 <= var3) {
                  this.failExpecting("digits for an IPv6 address", var3);
               }

               if (this.at(var4, var2, '.')) {
                  --var3;
                  break;
               }

               if (var4 > var3 + 4) {
                  this.fail("IPv6 hexadecimal digit sequence too long", var3);
               }

               this.ipv6byteCount += 2;
            }

            return var3;
         }
      }
   }
}
