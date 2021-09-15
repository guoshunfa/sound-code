package sun.nio.cs;

import java.lang.ref.SoftReference;
import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import sun.misc.ASCIICaseInsensitiveComparator;

public class AbstractCharsetProvider extends CharsetProvider {
   private Map<String, String> classMap;
   private Map<String, String> aliasMap;
   private Map<String, String[]> aliasNameMap;
   private Map<String, SoftReference<Charset>> cache;
   private String packagePrefix;

   protected AbstractCharsetProvider() {
      this.classMap = new TreeMap(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);
      this.aliasMap = new TreeMap(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);
      this.aliasNameMap = new TreeMap(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);
      this.cache = new TreeMap(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);
      this.packagePrefix = "sun.nio.cs";
   }

   protected AbstractCharsetProvider(String var1) {
      this.classMap = new TreeMap(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);
      this.aliasMap = new TreeMap(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);
      this.aliasNameMap = new TreeMap(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);
      this.cache = new TreeMap(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);
      this.packagePrefix = var1;
   }

   private static <K, V> void put(Map<K, V> var0, K var1, V var2) {
      if (!var0.containsKey(var1)) {
         var0.put(var1, var2);
      }

   }

   private static <K, V> void remove(Map<K, V> var0, K var1) {
      Object var2 = var0.remove(var1);

      assert var2 != null;

   }

   protected void charset(String var1, String var2, String[] var3) {
      synchronized(this) {
         put(this.classMap, var1, var2);

         for(int var5 = 0; var5 < var3.length; ++var5) {
            put(this.aliasMap, var3[var5], var1);
         }

         put(this.aliasNameMap, var1, var3);
         this.cache.clear();
      }
   }

   protected void deleteCharset(String var1, String[] var2) {
      synchronized(this) {
         remove(this.classMap, var1);

         for(int var4 = 0; var4 < var2.length; ++var4) {
            remove(this.aliasMap, var2[var4]);
         }

         remove(this.aliasNameMap, var1);
         this.cache.clear();
      }
   }

   protected void init() {
   }

   private String canonicalize(String var1) {
      String var2 = (String)this.aliasMap.get(var1);
      return var2 != null ? var2 : var1;
   }

   private Charset lookup(String var1) {
      SoftReference var2 = (SoftReference)this.cache.get(var1);
      if (var2 != null) {
         Charset var3 = (Charset)var2.get();
         if (var3 != null) {
            return var3;
         }
      }

      String var9 = (String)this.classMap.get(var1);
      if (var9 == null) {
         return null;
      } else {
         try {
            Class var4 = Class.forName(this.packagePrefix + "." + var9, true, this.getClass().getClassLoader());
            Charset var5 = (Charset)var4.newInstance();
            this.cache.put(var1, new SoftReference(var5));
            return var5;
         } catch (ClassNotFoundException var6) {
            return null;
         } catch (IllegalAccessException var7) {
            return null;
         } catch (InstantiationException var8) {
            return null;
         }
      }
   }

   public final Charset charsetForName(String var1) {
      synchronized(this) {
         this.init();
         return this.lookup(this.canonicalize(var1));
      }
   }

   public final Iterator<Charset> charsets() {
      final ArrayList var1;
      synchronized(this) {
         this.init();
         var1 = new ArrayList(this.classMap.keySet());
      }

      return new Iterator<Charset>() {
         Iterator<String> i = var1.iterator();

         public boolean hasNext() {
            return this.i.hasNext();
         }

         public Charset next() {
            String var1x = (String)this.i.next();
            synchronized(AbstractCharsetProvider.this) {
               return AbstractCharsetProvider.this.lookup(var1x);
            }
         }

         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   public final String[] aliases(String var1) {
      synchronized(this) {
         this.init();
         return (String[])this.aliasNameMap.get(var1);
      }
   }
}
