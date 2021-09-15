package java.text;

import java.lang.ref.SoftReference;
import java.text.spi.CollatorProvider;
import java.util.Comparator;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleServiceProviderPool;

public abstract class Collator implements Comparator<Object>, Cloneable {
   public static final int PRIMARY = 0;
   public static final int SECONDARY = 1;
   public static final int TERTIARY = 2;
   public static final int IDENTICAL = 3;
   public static final int NO_DECOMPOSITION = 0;
   public static final int CANONICAL_DECOMPOSITION = 1;
   public static final int FULL_DECOMPOSITION = 2;
   private int strength = 0;
   private int decmp = 0;
   private static final ConcurrentMap<Locale, SoftReference<Collator>> cache = new ConcurrentHashMap();
   static final int LESS = -1;
   static final int EQUAL = 0;
   static final int GREATER = 1;

   public static synchronized Collator getInstance() {
      return getInstance(Locale.getDefault());
   }

   public static Collator getInstance(Locale var0) {
      SoftReference var1 = (SoftReference)cache.get(var0);
      Collator var2 = var1 != null ? (Collator)var1.get() : null;
      if (var2 == null) {
         LocaleProviderAdapter var3 = LocaleProviderAdapter.getAdapter(CollatorProvider.class, var0);
         CollatorProvider var4 = var3.getCollatorProvider();
         var2 = var4.getInstance(var0);
         if (var2 == null) {
            var2 = LocaleProviderAdapter.forJRE().getCollatorProvider().getInstance(var0);
         }

         while(true) {
            if (var1 != null) {
               cache.remove(var0, var1);
            }

            var1 = (SoftReference)cache.putIfAbsent(var0, new SoftReference(var2));
            if (var1 == null) {
               break;
            }

            Collator var5 = (Collator)var1.get();
            if (var5 != null) {
               var2 = var5;
               break;
            }
         }
      }

      return (Collator)var2.clone();
   }

   public abstract int compare(String var1, String var2);

   public int compare(Object var1, Object var2) {
      return this.compare((String)var1, (String)var2);
   }

   public abstract CollationKey getCollationKey(String var1);

   public boolean equals(String var1, String var2) {
      return this.compare(var1, var2) == 0;
   }

   public synchronized int getStrength() {
      return this.strength;
   }

   public synchronized void setStrength(int var1) {
      if (var1 != 0 && var1 != 1 && var1 != 2 && var1 != 3) {
         throw new IllegalArgumentException("Incorrect comparison level.");
      } else {
         this.strength = var1;
      }
   }

   public synchronized int getDecomposition() {
      return this.decmp;
   }

   public synchronized void setDecomposition(int var1) {
      if (var1 != 0 && var1 != 1 && var1 != 2) {
         throw new IllegalArgumentException("Wrong decomposition mode.");
      } else {
         this.decmp = var1;
      }
   }

   public static synchronized Locale[] getAvailableLocales() {
      LocaleServiceProviderPool var0 = LocaleServiceProviderPool.getPool(CollatorProvider.class);
      return var0.getAvailableLocales();
   }

   public Object clone() {
      try {
         return (Collator)super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2);
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         Collator var2 = (Collator)var1;
         return this.strength == var2.strength && this.decmp == var2.decmp;
      }
   }

   public abstract int hashCode();

   protected Collator() {
      this.strength = 2;
      this.decmp = 1;
   }
}
