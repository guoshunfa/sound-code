package java.nio.charset;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.spi.CharsetProvider;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import sun.misc.ASCIICaseInsensitiveComparator;
import sun.misc.VM;
import sun.nio.cs.ThreadLocalCoders;
import sun.security.action.GetPropertyAction;

public abstract class Charset implements Comparable<Charset> {
   private static volatile String bugLevel = null;
   private static CharsetProvider standardProvider = new sun.nio.cs.StandardCharsets();
   private static volatile Object[] cache1 = null;
   private static volatile Object[] cache2 = null;
   private static ThreadLocal<ThreadLocal<?>> gate = new ThreadLocal();
   private static volatile Charset defaultCharset;
   private final String name;
   private final String[] aliases;
   private Set<String> aliasSet = null;

   static boolean atBugLevel(String var0) {
      String var1 = bugLevel;
      if (var1 == null) {
         if (!VM.isBooted()) {
            return false;
         }

         bugLevel = var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.nio.cs.bugLevel", "")));
      }

      return var1.equals(var0);
   }

   private static void checkName(String var0) {
      int var1 = var0.length();
      if (!atBugLevel("1.4") && var1 == 0) {
         throw new IllegalCharsetNameException(var0);
      } else {
         for(int var2 = 0; var2 < var1; ++var2) {
            char var3 = var0.charAt(var2);
            if ((var3 < 'A' || var3 > 'Z') && (var3 < 'a' || var3 > 'z') && (var3 < '0' || var3 > '9') && (var3 != '-' || var2 == 0) && (var3 != '+' || var2 == 0) && (var3 != ':' || var2 == 0) && (var3 != '_' || var2 == 0) && (var3 != '.' || var2 == 0)) {
               throw new IllegalCharsetNameException(var0);
            }
         }

      }
   }

   private static void cache(String var0, Charset var1) {
      cache2 = cache1;
      cache1 = new Object[]{var0, var1};
   }

   private static Iterator<CharsetProvider> providers() {
      return new Iterator<CharsetProvider>() {
         ClassLoader cl = ClassLoader.getSystemClassLoader();
         ServiceLoader<CharsetProvider> sl;
         Iterator<CharsetProvider> i;
         CharsetProvider next;

         {
            this.sl = ServiceLoader.load(CharsetProvider.class, this.cl);
            this.i = this.sl.iterator();
            this.next = null;
         }

         private boolean getNext() {
            while(this.next == null) {
               try {
                  if (!this.i.hasNext()) {
                     return false;
                  }

                  this.next = (CharsetProvider)this.i.next();
               } catch (ServiceConfigurationError var2) {
                  if (!(var2.getCause() instanceof SecurityException)) {
                     throw var2;
                  }
               }
            }

            return true;
         }

         public boolean hasNext() {
            return this.getNext();
         }

         public CharsetProvider next() {
            if (!this.getNext()) {
               throw new NoSuchElementException();
            } else {
               CharsetProvider var1 = this.next;
               this.next = null;
               return var1;
            }
         }

         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   private static Charset lookupViaProviders(final String var0) {
      if (!VM.isBooted()) {
         return null;
      } else if (gate.get() != null) {
         return null;
      } else {
         Charset var1;
         try {
            gate.set(gate);
            var1 = (Charset)AccessController.doPrivileged(new PrivilegedAction<Charset>() {
               public Charset run() {
                  Iterator var1 = Charset.providers();

                  Charset var3;
                  do {
                     if (!var1.hasNext()) {
                        return null;
                     }

                     CharsetProvider var2 = (CharsetProvider)var1.next();
                     var3 = var2.charsetForName(var0);
                  } while(var3 == null);

                  return var3;
               }
            });
         } finally {
            gate.set((Object)null);
         }

         return var1;
      }
   }

   private static Charset lookupExtendedCharset(String var0) {
      CharsetProvider var1 = Charset.ExtendedProviderHolder.extendedProvider;
      return var1 != null ? var1.charsetForName(var0) : null;
   }

   private static Charset lookup(String var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("Null charset name");
      } else {
         Object[] var1;
         return (var1 = cache1) != null && var0.equals(var1[0]) ? (Charset)var1[1] : lookup2(var0);
      }
   }

   private static Charset lookup2(String var0) {
      Object[] var1;
      if ((var1 = cache2) != null && var0.equals(var1[0])) {
         cache2 = cache1;
         cache1 = var1;
         return (Charset)var1[1];
      } else {
         Charset var2;
         if ((var2 = standardProvider.charsetForName(var0)) == null && (var2 = lookupExtendedCharset(var0)) == null && (var2 = lookupViaProviders(var0)) == null) {
            checkName(var0);
            return null;
         } else {
            cache(var0, var2);
            return var2;
         }
      }
   }

   public static boolean isSupported(String var0) {
      return lookup(var0) != null;
   }

   public static Charset forName(String var0) {
      Charset var1 = lookup(var0);
      if (var1 != null) {
         return var1;
      } else {
         throw new UnsupportedCharsetException(var0);
      }
   }

   private static void put(Iterator<Charset> var0, Map<String, Charset> var1) {
      while(var0.hasNext()) {
         Charset var2 = (Charset)var0.next();
         if (!var1.containsKey(var2.name())) {
            var1.put(var2.name(), var2);
         }
      }

   }

   public static SortedMap<String, Charset> availableCharsets() {
      return (SortedMap)AccessController.doPrivileged(new PrivilegedAction<SortedMap<String, Charset>>() {
         public SortedMap<String, Charset> run() {
            TreeMap var1 = new TreeMap(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);
            Charset.put(Charset.standardProvider.charsets(), var1);
            CharsetProvider var2 = Charset.ExtendedProviderHolder.extendedProvider;
            if (var2 != null) {
               Charset.put(var2.charsets(), var1);
            }

            Iterator var3 = Charset.providers();

            while(var3.hasNext()) {
               CharsetProvider var4 = (CharsetProvider)var3.next();
               Charset.put(var4.charsets(), var1);
            }

            return Collections.unmodifiableSortedMap(var1);
         }
      });
   }

   public static Charset defaultCharset() {
      if (defaultCharset == null) {
         Class var0 = Charset.class;
         synchronized(Charset.class) {
            String var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("file.encoding")));
            Charset var2 = lookup(var1);
            if (var2 != null) {
               defaultCharset = var2;
            } else {
               defaultCharset = forName("UTF-8");
            }
         }
      }

      return defaultCharset;
   }

   protected Charset(String var1, String[] var2) {
      checkName(var1);
      String[] var3 = var2 == null ? new String[0] : var2;

      for(int var4 = 0; var4 < var3.length; ++var4) {
         checkName(var3[var4]);
      }

      this.name = var1;
      this.aliases = var3;
   }

   public final String name() {
      return this.name;
   }

   public final Set<String> aliases() {
      if (this.aliasSet != null) {
         return this.aliasSet;
      } else {
         int var1 = this.aliases.length;
         HashSet var2 = new HashSet(var1);

         for(int var3 = 0; var3 < var1; ++var3) {
            var2.add(this.aliases[var3]);
         }

         this.aliasSet = Collections.unmodifiableSet(var2);
         return this.aliasSet;
      }
   }

   public String displayName() {
      return this.name;
   }

   public final boolean isRegistered() {
      return !this.name.startsWith("X-") && !this.name.startsWith("x-");
   }

   public String displayName(Locale var1) {
      return this.name;
   }

   public abstract boolean contains(Charset var1);

   public abstract CharsetDecoder newDecoder();

   public abstract CharsetEncoder newEncoder();

   public boolean canEncode() {
      return true;
   }

   public final CharBuffer decode(ByteBuffer var1) {
      try {
         return ThreadLocalCoders.decoderFor(this).onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).decode(var1);
      } catch (CharacterCodingException var3) {
         throw new Error(var3);
      }
   }

   public final ByteBuffer encode(CharBuffer var1) {
      try {
         return ThreadLocalCoders.encoderFor(this).onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).encode(var1);
      } catch (CharacterCodingException var3) {
         throw new Error(var3);
      }
   }

   public final ByteBuffer encode(String var1) {
      return this.encode(CharBuffer.wrap((CharSequence)var1));
   }

   public final int compareTo(Charset var1) {
      return this.name().compareToIgnoreCase(var1.name());
   }

   public final int hashCode() {
      return this.name().hashCode();
   }

   public final boolean equals(Object var1) {
      if (!(var1 instanceof Charset)) {
         return false;
      } else {
         return this == var1 ? true : this.name.equals(((Charset)var1).name());
      }
   }

   public final String toString() {
      return this.name();
   }

   private static class ExtendedProviderHolder {
      static final CharsetProvider extendedProvider = extendedProvider();

      private static CharsetProvider extendedProvider() {
         return (CharsetProvider)AccessController.doPrivileged(new PrivilegedAction<CharsetProvider>() {
            public CharsetProvider run() {
               try {
                  Class var1 = Class.forName("sun.nio.cs.ext.ExtendedCharsets");
                  return (CharsetProvider)var1.newInstance();
               } catch (ClassNotFoundException var2) {
                  return null;
               } catch (IllegalAccessException | InstantiationException var3) {
                  throw new Error(var3);
               }
            }
         });
      }
   }
}
