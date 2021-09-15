package java.util.jar;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import sun.misc.ASCIICaseInsensitiveComparator;
import sun.util.logging.PlatformLogger;

public class Attributes implements Map<Object, Object>, Cloneable {
   protected Map<Object, Object> map;

   public Attributes() {
      this(11);
   }

   public Attributes(int var1) {
      this.map = new HashMap(var1);
   }

   public Attributes(Attributes var1) {
      this.map = new HashMap(var1);
   }

   public Object get(Object var1) {
      return this.map.get(var1);
   }

   public String getValue(String var1) {
      return (String)this.get(new Attributes.Name(var1));
   }

   public String getValue(Attributes.Name var1) {
      return (String)this.get(var1);
   }

   public Object put(Object var1, Object var2) {
      return this.map.put((Attributes.Name)var1, (String)var2);
   }

   public String putValue(String var1, String var2) {
      return (String)this.put(new Attributes.Name(var1), var2);
   }

   public Object remove(Object var1) {
      return this.map.remove(var1);
   }

   public boolean containsValue(Object var1) {
      return this.map.containsValue(var1);
   }

   public boolean containsKey(Object var1) {
      return this.map.containsKey(var1);
   }

   public void putAll(Map<?, ?> var1) {
      if (!Attributes.class.isInstance(var1)) {
         throw new ClassCastException();
      } else {
         Iterator var2 = var1.entrySet().iterator();

         while(var2.hasNext()) {
            Map.Entry var3 = (Map.Entry)var2.next();
            this.put(var3.getKey(), var3.getValue());
         }

      }
   }

   public void clear() {
      this.map.clear();
   }

   public int size() {
      return this.map.size();
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   public Set<Object> keySet() {
      return this.map.keySet();
   }

   public Collection<Object> values() {
      return this.map.values();
   }

   public Set<Map.Entry<Object, Object>> entrySet() {
      return this.map.entrySet();
   }

   public boolean equals(Object var1) {
      return this.map.equals(var1);
   }

   public int hashCode() {
      return this.map.hashCode();
   }

   public Object clone() {
      return new Attributes(this);
   }

   void write(DataOutputStream var1) throws IOException {
      Iterator var2 = this.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         StringBuffer var4 = new StringBuffer(((Attributes.Name)var3.getKey()).toString());
         var4.append(": ");
         String var5 = (String)var3.getValue();
         if (var5 != null) {
            byte[] var6 = var5.getBytes("UTF8");
            var5 = new String(var6, 0, 0, var6.length);
         }

         var4.append(var5);
         var4.append("\r\n");
         Manifest.make72Safe(var4);
         var1.writeBytes(var4.toString());
      }

      var1.writeBytes("\r\n");
   }

   void writeMain(DataOutputStream var1) throws IOException {
      String var2 = Attributes.Name.MANIFEST_VERSION.toString();
      String var3 = this.getValue(var2);
      if (var3 == null) {
         var2 = Attributes.Name.SIGNATURE_VERSION.toString();
         var3 = this.getValue(var2);
      }

      if (var3 != null) {
         var1.writeBytes(var2 + ": " + var3 + "\r\n");
      }

      Iterator var4 = this.entrySet().iterator();

      while(var4.hasNext()) {
         Map.Entry var5 = (Map.Entry)var4.next();
         String var6 = ((Attributes.Name)var5.getKey()).toString();
         if (var3 != null && !var6.equalsIgnoreCase(var2)) {
            StringBuffer var7 = new StringBuffer(var6);
            var7.append(": ");
            String var8 = (String)var5.getValue();
            if (var8 != null) {
               byte[] var9 = var8.getBytes("UTF8");
               var8 = new String(var9, 0, 0, var9.length);
            }

            var7.append(var8);
            var7.append("\r\n");
            Manifest.make72Safe(var7);
            var1.writeBytes(var7.toString());
         }
      }

      var1.writeBytes("\r\n");
   }

   void read(Manifest.FastInputStream var1, byte[] var2) throws IOException {
      String var3 = null;
      String var4 = null;
      byte[] var5 = null;

      while(true) {
         int var6;
         if ((var6 = var1.readLine(var2)) != -1) {
            boolean var7 = false;
            --var6;
            if (var2[var6] != 10) {
               throw new IOException("line too long");
            }

            if (var6 > 0 && var2[var6 - 1] == 13) {
               --var6;
            }

            if (var6 != 0) {
               int var8 = 0;
               if (var2[0] == 32) {
                  if (var3 == null) {
                     throw new IOException("misplaced continuation line");
                  }

                  var7 = true;
                  byte[] var9 = new byte[var5.length + var6 - 1];
                  System.arraycopy(var5, 0, var9, 0, var5.length);
                  System.arraycopy(var2, 1, var9, var5.length, var6 - 1);
                  if (var1.peek() == 32) {
                     var5 = var9;
                     continue;
                  }

                  var4 = new String(var9, 0, var9.length, "UTF8");
                  var5 = null;
               } else {
                  while(var2[var8++] != 58) {
                     if (var8 >= var6) {
                        throw new IOException("invalid header field");
                     }
                  }

                  if (var2[var8++] != 32) {
                     throw new IOException("invalid header field");
                  }

                  var3 = new String(var2, 0, 0, var8 - 2);
                  if (var1.peek() == 32) {
                     var5 = new byte[var6 - var8];
                     System.arraycopy(var2, var8, var5, 0, var6 - var8);
                     continue;
                  }

                  var4 = new String(var2, var8, var6 - var8, "UTF8");
               }

               try {
                  if (this.putValue(var3, var4) != null && !var7) {
                     PlatformLogger.getLogger("java.util.jar").warning("Duplicate name in Manifest: " + var3 + ".\nEnsure that the manifest does not have duplicate entries, and\nthat blank lines separate individual sections in both your\nmanifest and in the META-INF/MANIFEST.MF entry in the jar file.");
                  }
                  continue;
               } catch (IllegalArgumentException var10) {
                  throw new IOException("invalid header field name: " + var3);
               }
            }
         }

         return;
      }
   }

   public static class Name {
      private String name;
      private int hashCode = -1;
      public static final Attributes.Name MANIFEST_VERSION = new Attributes.Name("Manifest-Version");
      public static final Attributes.Name SIGNATURE_VERSION = new Attributes.Name("Signature-Version");
      public static final Attributes.Name CONTENT_TYPE = new Attributes.Name("Content-Type");
      public static final Attributes.Name CLASS_PATH = new Attributes.Name("Class-Path");
      public static final Attributes.Name MAIN_CLASS = new Attributes.Name("Main-Class");
      public static final Attributes.Name SEALED = new Attributes.Name("Sealed");
      public static final Attributes.Name EXTENSION_LIST = new Attributes.Name("Extension-List");
      public static final Attributes.Name EXTENSION_NAME = new Attributes.Name("Extension-Name");
      /** @deprecated */
      @Deprecated
      public static final Attributes.Name EXTENSION_INSTALLATION = new Attributes.Name("Extension-Installation");
      public static final Attributes.Name IMPLEMENTATION_TITLE = new Attributes.Name("Implementation-Title");
      public static final Attributes.Name IMPLEMENTATION_VERSION = new Attributes.Name("Implementation-Version");
      public static final Attributes.Name IMPLEMENTATION_VENDOR = new Attributes.Name("Implementation-Vendor");
      /** @deprecated */
      @Deprecated
      public static final Attributes.Name IMPLEMENTATION_VENDOR_ID = new Attributes.Name("Implementation-Vendor-Id");
      /** @deprecated */
      @Deprecated
      public static final Attributes.Name IMPLEMENTATION_URL = new Attributes.Name("Implementation-URL");
      public static final Attributes.Name SPECIFICATION_TITLE = new Attributes.Name("Specification-Title");
      public static final Attributes.Name SPECIFICATION_VERSION = new Attributes.Name("Specification-Version");
      public static final Attributes.Name SPECIFICATION_VENDOR = new Attributes.Name("Specification-Vendor");

      public Name(String var1) {
         if (var1 == null) {
            throw new NullPointerException("name");
         } else if (!isValid(var1)) {
            throw new IllegalArgumentException(var1);
         } else {
            this.name = var1.intern();
         }
      }

      private static boolean isValid(String var0) {
         int var1 = var0.length();
         if (var1 <= 70 && var1 != 0) {
            for(int var2 = 0; var2 < var1; ++var2) {
               if (!isValid(var0.charAt(var2))) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      }

      private static boolean isValid(char var0) {
         return isAlpha(var0) || isDigit(var0) || var0 == '_' || var0 == '-';
      }

      private static boolean isAlpha(char var0) {
         return var0 >= 'a' && var0 <= 'z' || var0 >= 'A' && var0 <= 'Z';
      }

      private static boolean isDigit(char var0) {
         return var0 >= '0' && var0 <= '9';
      }

      public boolean equals(Object var1) {
         if (var1 instanceof Attributes.Name) {
            Comparator var2 = ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER;
            return var2.compare(this.name, ((Attributes.Name)var1).name) == 0;
         } else {
            return false;
         }
      }

      public int hashCode() {
         if (this.hashCode == -1) {
            this.hashCode = ASCIICaseInsensitiveComparator.lowerCaseHashCode(this.name);
         }

         return this.hashCode;
      }

      public String toString() {
         return this.name;
      }
   }
}
