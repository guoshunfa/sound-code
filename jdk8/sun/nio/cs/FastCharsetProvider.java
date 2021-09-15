package sun.nio.cs;

import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.util.Iterator;
import java.util.Map;

public class FastCharsetProvider extends CharsetProvider {
   private Map<String, String> classMap;
   private Map<String, String> aliasMap;
   private Map<String, Charset> cache;
   private String packagePrefix;

   protected FastCharsetProvider(String var1, Map<String, String> var2, Map<String, String> var3, Map<String, Charset> var4) {
      this.packagePrefix = var1;
      this.aliasMap = var2;
      this.classMap = var3;
      this.cache = var4;
   }

   private String canonicalize(String var1) {
      String var2 = (String)this.aliasMap.get(var1);
      return var2 != null ? var2 : var1;
   }

   private static String toLower(String var0) {
      int var1 = var0.length();
      boolean var2 = true;

      for(int var3 = 0; var3 < var1; ++var3) {
         char var4 = var0.charAt(var3);
         if ((var4 - 65 | 90 - var4) >= 0) {
            var2 = false;
            break;
         }
      }

      if (var2) {
         return var0;
      } else {
         char[] var6 = new char[var1];

         for(int var7 = 0; var7 < var1; ++var7) {
            char var5 = var0.charAt(var7);
            if ((var5 - 65 | 90 - var5) >= 0) {
               var6[var7] = (char)(var5 + 32);
            } else {
               var6[var7] = (char)var5;
            }
         }

         return new String(var6);
      }
   }

   private Charset lookup(String var1) {
      String var2 = this.canonicalize(toLower(var1));
      Charset var3 = (Charset)this.cache.get(var2);
      if (var3 != null) {
         return var3;
      } else {
         String var4 = (String)this.classMap.get(var2);
         if (var4 == null) {
            return null;
         } else if (var4.equals("US_ASCII")) {
            US_ASCII var7 = new US_ASCII();
            this.cache.put(var2, var7);
            return var7;
         } else {
            try {
               Class var5 = Class.forName(this.packagePrefix + "." + var4, true, this.getClass().getClassLoader());
               var3 = (Charset)var5.newInstance();
               this.cache.put(var2, var3);
               return var3;
            } catch (IllegalAccessException | InstantiationException | ClassNotFoundException var6) {
               return null;
            }
         }
      }
   }

   public final Charset charsetForName(String var1) {
      synchronized(this) {
         return this.lookup(this.canonicalize(var1));
      }
   }

   public final Iterator<Charset> charsets() {
      return new Iterator<Charset>() {
         Iterator<String> i;

         {
            this.i = FastCharsetProvider.this.classMap.keySet().iterator();
         }

         public boolean hasNext() {
            return this.i.hasNext();
         }

         public Charset next() {
            String var1 = (String)this.i.next();
            return FastCharsetProvider.this.lookup(var1);
         }

         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }
}
