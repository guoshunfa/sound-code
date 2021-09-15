package java.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.jar.JarEntry;
import java.util.spi.ResourceBundleControlProvider;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.util.locale.BaseLocale;
import sun.util.locale.LocaleExtensions;
import sun.util.locale.LocaleObjectCache;

public abstract class ResourceBundle {
   private static final int INITIAL_CACHE_SIZE = 32;
   private static final ResourceBundle NONEXISTENT_BUNDLE = new ResourceBundle() {
      public Enumeration<String> getKeys() {
         return null;
      }

      protected Object handleGetObject(String var1) {
         return null;
      }

      public String toString() {
         return "NONEXISTENT_BUNDLE";
      }
   };
   private static final ConcurrentMap<ResourceBundle.CacheKey, ResourceBundle.BundleReference> cacheList = new ConcurrentHashMap(32);
   private static final ReferenceQueue<Object> referenceQueue = new ReferenceQueue();
   protected ResourceBundle parent = null;
   private Locale locale = null;
   private String name;
   private volatile boolean expired;
   private volatile ResourceBundle.CacheKey cacheKey;
   private volatile Set<String> keySet;
   private static final List<ResourceBundleControlProvider> providers;

   public String getBaseBundleName() {
      return this.name;
   }

   public final String getString(String var1) {
      return (String)this.getObject(var1);
   }

   public final String[] getStringArray(String var1) {
      return (String[])((String[])this.getObject(var1));
   }

   public final Object getObject(String var1) {
      Object var2 = this.handleGetObject(var1);
      if (var2 == null) {
         if (this.parent != null) {
            var2 = this.parent.getObject(var1);
         }

         if (var2 == null) {
            throw new MissingResourceException("Can't find resource for bundle " + this.getClass().getName() + ", key " + var1, this.getClass().getName(), var1);
         }
      }

      return var2;
   }

   public Locale getLocale() {
      return this.locale;
   }

   private static ClassLoader getLoader(Class<?> var0) {
      Object var1 = var0 == null ? null : var0.getClassLoader();
      if (var1 == null) {
         var1 = ResourceBundle.RBClassLoader.INSTANCE;
      }

      return (ClassLoader)var1;
   }

   protected void setParent(ResourceBundle var1) {
      assert var1 != NONEXISTENT_BUNDLE;

      this.parent = var1;
   }

   @CallerSensitive
   public static final ResourceBundle getBundle(String var0) {
      return getBundleImpl(var0, Locale.getDefault(), getLoader(Reflection.getCallerClass()), getDefaultControl(var0));
   }

   @CallerSensitive
   public static final ResourceBundle getBundle(String var0, ResourceBundle.Control var1) {
      return getBundleImpl(var0, Locale.getDefault(), getLoader(Reflection.getCallerClass()), var1);
   }

   @CallerSensitive
   public static final ResourceBundle getBundle(String var0, Locale var1) {
      return getBundleImpl(var0, var1, getLoader(Reflection.getCallerClass()), getDefaultControl(var0));
   }

   @CallerSensitive
   public static final ResourceBundle getBundle(String var0, Locale var1, ResourceBundle.Control var2) {
      return getBundleImpl(var0, var1, getLoader(Reflection.getCallerClass()), var2);
   }

   public static ResourceBundle getBundle(String var0, Locale var1, ClassLoader var2) {
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         return getBundleImpl(var0, var1, var2, getDefaultControl(var0));
      }
   }

   public static ResourceBundle getBundle(String var0, Locale var1, ClassLoader var2, ResourceBundle.Control var3) {
      if (var2 != null && var3 != null) {
         return getBundleImpl(var0, var1, var2, var3);
      } else {
         throw new NullPointerException();
      }
   }

   private static ResourceBundle.Control getDefaultControl(String var0) {
      if (providers != null) {
         Iterator var1 = providers.iterator();

         while(var1.hasNext()) {
            ResourceBundleControlProvider var2 = (ResourceBundleControlProvider)var1.next();
            ResourceBundle.Control var3 = var2.getControl(var0);
            if (var3 != null) {
               return var3;
            }
         }
      }

      return ResourceBundle.Control.INSTANCE;
   }

   private static ResourceBundle getBundleImpl(String var0, Locale var1, ClassLoader var2, ResourceBundle.Control var3) {
      if (var1 != null && var3 != null) {
         ResourceBundle.CacheKey var4 = new ResourceBundle.CacheKey(var0, var1, var2);
         ResourceBundle var5 = null;
         ResourceBundle.BundleReference var6 = (ResourceBundle.BundleReference)cacheList.get(var4);
         if (var6 != null) {
            var5 = (ResourceBundle)var6.get();
            var6 = null;
         }

         if (isValidBundle(var5) && hasValidParentChain(var5)) {
            return var5;
         } else {
            boolean var7 = var3 == ResourceBundle.Control.INSTANCE || var3 instanceof ResourceBundle.SingleFormatControl;
            List var8 = var3.getFormats(var0);
            if (!var7 && !checkList(var8)) {
               throw new IllegalArgumentException("Invalid Control: getFormats");
            } else {
               ResourceBundle var9 = null;

               for(Locale var10 = var1; var10 != null; var10 = var3.getFallbackLocale(var0, var10)) {
                  List var11 = var3.getCandidateLocales(var0, var10);
                  if (!var7 && !checkList(var11)) {
                     throw new IllegalArgumentException("Invalid Control: getCandidateLocales");
                  }

                  var5 = findBundle(var4, var11, var8, 0, var3, var9);
                  if (isValidBundle(var5)) {
                     boolean var12 = Locale.ROOT.equals(var5.locale);
                     if (!var12 || var5.locale.equals(var1) || var11.size() == 1 && var5.locale.equals(var11.get(0))) {
                        break;
                     }

                     if (var12 && var9 == null) {
                        var9 = var5;
                     }
                  }
               }

               if (var5 == null) {
                  if (var9 == null) {
                     throwMissingResourceException(var0, var1, var4.getCause());
                  }

                  var5 = var9;
               }

               return var5;
            }
         }
      } else {
         throw new NullPointerException();
      }
   }

   private static boolean checkList(List<?> var0) {
      boolean var1 = var0 != null && !var0.isEmpty();
      if (var1) {
         int var2 = var0.size();

         for(int var3 = 0; var1 && var3 < var2; ++var3) {
            var1 = var0.get(var3) != null;
         }
      }

      return var1;
   }

   private static ResourceBundle findBundle(ResourceBundle.CacheKey var0, List<Locale> var1, List<String> var2, int var3, ResourceBundle.Control var4, ResourceBundle var5) {
      Locale var6 = (Locale)var1.get(var3);
      ResourceBundle var7 = null;
      if (var3 != var1.size() - 1) {
         var7 = findBundle(var0, var1, var2, var3 + 1, var4, var5);
      } else if (var5 != null && Locale.ROOT.equals(var6)) {
         return var5;
      }

      Reference var8;
      while((var8 = referenceQueue.poll()) != null) {
         cacheList.remove(((ResourceBundle.CacheKeyReference)var8).getCacheKey());
      }

      boolean var9 = false;
      var0.setLocale(var6);
      ResourceBundle var10 = findBundleInCache(var0, var4);
      if (isValidBundle(var10)) {
         var9 = var10.expired;
         if (!var9) {
            if (var10.parent == var7) {
               return var10;
            }

            ResourceBundle.BundleReference var11 = (ResourceBundle.BundleReference)cacheList.get(var0);
            if (var11 != null && var11.get() == var10) {
               cacheList.remove(var0, var11);
            }
         }
      }

      if (var10 != NONEXISTENT_BUNDLE) {
         ResourceBundle.CacheKey var16 = (ResourceBundle.CacheKey)var0.clone();

         ResourceBundle var12;
         try {
            var10 = loadBundle(var0, var2, var4, var9);
            if (var10 == null) {
               putBundleInCache(var0, NONEXISTENT_BUNDLE, var4);
               return var7;
            }

            if (var10.parent == null) {
               var10.setParent(var7);
            }

            var10.locale = var6;
            var10 = putBundleInCache(var0, var10, var4);
            var12 = var10;
         } finally {
            if (var16.getCause() instanceof InterruptedException) {
               Thread.currentThread().interrupt();
            }

         }

         return var12;
      } else {
         return var7;
      }
   }

   private static ResourceBundle loadBundle(ResourceBundle.CacheKey var0, List<String> var1, ResourceBundle.Control var2, boolean var3) {
      Locale var4 = var0.getLocale();
      ResourceBundle var5 = null;
      int var6 = var1.size();

      for(int var7 = 0; var7 < var6; ++var7) {
         String var8 = (String)var1.get(var7);

         try {
            var5 = var2.newBundle(var0.getName(), var4, var8, var0.getLoader(), var3);
         } catch (LinkageError var10) {
            var0.setCause(var10);
         } catch (Exception var11) {
            var0.setCause(var11);
         }

         if (var5 != null) {
            var0.setFormat(var8);
            var5.name = var0.getName();
            var5.locale = var4;
            var5.expired = false;
            break;
         }
      }

      return var5;
   }

   private static boolean isValidBundle(ResourceBundle var0) {
      return var0 != null && var0 != NONEXISTENT_BUNDLE;
   }

   private static boolean hasValidParentChain(ResourceBundle var0) {
      for(long var1 = System.currentTimeMillis(); var0 != null; var0 = var0.parent) {
         if (var0.expired) {
            return false;
         }

         ResourceBundle.CacheKey var3 = var0.cacheKey;
         if (var3 != null) {
            long var4 = var3.expirationTime;
            if (var4 >= 0L && var4 <= var1) {
               return false;
            }
         }
      }

      return true;
   }

   private static void throwMissingResourceException(String var0, Locale var1, Throwable var2) {
      if (var2 instanceof MissingResourceException) {
         var2 = null;
      }

      throw new MissingResourceException("Can't find bundle for base name " + var0 + ", locale " + var1, var0 + "_" + var1, "", var2);
   }

   private static ResourceBundle findBundleInCache(ResourceBundle.CacheKey var0, ResourceBundle.Control var1) {
      ResourceBundle.BundleReference var2 = (ResourceBundle.BundleReference)cacheList.get(var0);
      if (var2 == null) {
         return null;
      } else {
         ResourceBundle var3 = (ResourceBundle)var2.get();
         if (var3 == null) {
            return null;
         } else {
            ResourceBundle var4 = var3.parent;

            assert var4 != NONEXISTENT_BUNDLE;

            if (var4 != null && var4.expired) {
               assert var3 != NONEXISTENT_BUNDLE;

               var3.expired = true;
               var3.cacheKey = null;
               cacheList.remove(var0, var2);
               var3 = null;
            } else {
               ResourceBundle.CacheKey var5 = var2.getCacheKey();
               long var6 = var5.expirationTime;
               if (!var3.expired && var6 >= 0L && var6 <= System.currentTimeMillis()) {
                  if (var3 != NONEXISTENT_BUNDLE) {
                     synchronized(var3) {
                        var6 = var5.expirationTime;
                        if (!var3.expired && var6 >= 0L && var6 <= System.currentTimeMillis()) {
                           try {
                              var3.expired = var1.needsReload(var5.getName(), var5.getLocale(), var5.getFormat(), var5.getLoader(), var3, var5.loadTime);
                           } catch (Exception var11) {
                              var0.setCause(var11);
                           }

                           if (var3.expired) {
                              var3.cacheKey = null;
                              cacheList.remove(var0, var2);
                           } else {
                              setExpirationTime(var5, var1);
                           }
                        }
                     }
                  } else {
                     cacheList.remove(var0, var2);
                     var3 = null;
                  }
               }
            }

            return var3;
         }
      }
   }

   private static ResourceBundle putBundleInCache(ResourceBundle.CacheKey var0, ResourceBundle var1, ResourceBundle.Control var2) {
      setExpirationTime(var0, var2);
      if (var0.expirationTime != -1L) {
         ResourceBundle.CacheKey var3 = (ResourceBundle.CacheKey)var0.clone();
         ResourceBundle.BundleReference var4 = new ResourceBundle.BundleReference(var1, referenceQueue, var3);
         var1.cacheKey = var3;
         ResourceBundle.BundleReference var5 = (ResourceBundle.BundleReference)cacheList.putIfAbsent(var3, var4);
         if (var5 != null) {
            ResourceBundle var6 = (ResourceBundle)var5.get();
            if (var6 != null && !var6.expired) {
               var1.cacheKey = null;
               var1 = var6;
               var4.clear();
            } else {
               cacheList.put(var3, var4);
            }
         }
      }

      return var1;
   }

   private static void setExpirationTime(ResourceBundle.CacheKey var0, ResourceBundle.Control var1) {
      long var2 = var1.getTimeToLive(var0.getName(), var0.getLocale());
      if (var2 >= 0L) {
         long var4 = System.currentTimeMillis();
         var0.loadTime = var4;
         var0.expirationTime = var4 + var2;
      } else {
         if (var2 < -2L) {
            throw new IllegalArgumentException("Invalid Control: TTL=" + var2);
         }

         var0.expirationTime = var2;
      }

   }

   @CallerSensitive
   public static final void clearCache() {
      clearCache(getLoader(Reflection.getCallerClass()));
   }

   public static final void clearCache(ClassLoader var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         Set var1 = cacheList.keySet();
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            ResourceBundle.CacheKey var3 = (ResourceBundle.CacheKey)var2.next();
            if (var3.getLoader() == var0) {
               var1.remove(var3);
            }
         }

      }
   }

   protected abstract Object handleGetObject(String var1);

   public abstract Enumeration<String> getKeys();

   public boolean containsKey(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         for(ResourceBundle var2 = this; var2 != null; var2 = var2.parent) {
            if (var2.handleKeySet().contains(var1)) {
               return true;
            }
         }

         return false;
      }
   }

   public Set<String> keySet() {
      HashSet var1 = new HashSet();

      for(ResourceBundle var2 = this; var2 != null; var2 = var2.parent) {
         var1.addAll(var2.handleKeySet());
      }

      return var1;
   }

   protected Set<String> handleKeySet() {
      if (this.keySet == null) {
         synchronized(this) {
            if (this.keySet == null) {
               HashSet var2 = new HashSet();
               Enumeration var3 = this.getKeys();

               while(var3.hasMoreElements()) {
                  String var4 = (String)var3.nextElement();
                  if (this.handleGetObject(var4) != null) {
                     var2.add(var4);
                  }
               }

               this.keySet = var2;
            }
         }
      }

      return this.keySet;
   }

   static {
      ArrayList var0 = null;
      ServiceLoader var1 = ServiceLoader.loadInstalled(ResourceBundleControlProvider.class);

      ResourceBundleControlProvider var3;
      for(Iterator var2 = var1.iterator(); var2.hasNext(); var0.add(var3)) {
         var3 = (ResourceBundleControlProvider)var2.next();
         if (var0 == null) {
            var0 = new ArrayList();
         }
      }

      providers = var0;
   }

   private static final class NoFallbackControl extends ResourceBundle.SingleFormatControl {
      private static final ResourceBundle.Control NO_FALLBACK;
      private static final ResourceBundle.Control PROPERTIES_ONLY_NO_FALLBACK;
      private static final ResourceBundle.Control CLASS_ONLY_NO_FALLBACK;

      protected NoFallbackControl(List<String> var1) {
         super(var1);
      }

      public Locale getFallbackLocale(String var1, Locale var2) {
         if (var1 != null && var2 != null) {
            return null;
         } else {
            throw new NullPointerException();
         }
      }

      static {
         NO_FALLBACK = new ResourceBundle.NoFallbackControl(FORMAT_DEFAULT);
         PROPERTIES_ONLY_NO_FALLBACK = new ResourceBundle.NoFallbackControl(FORMAT_PROPERTIES);
         CLASS_ONLY_NO_FALLBACK = new ResourceBundle.NoFallbackControl(FORMAT_CLASS);
      }
   }

   private static class SingleFormatControl extends ResourceBundle.Control {
      private static final ResourceBundle.Control PROPERTIES_ONLY;
      private static final ResourceBundle.Control CLASS_ONLY;
      private final List<String> formats;

      protected SingleFormatControl(List<String> var1) {
         this.formats = var1;
      }

      public List<String> getFormats(String var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            return this.formats;
         }
      }

      static {
         PROPERTIES_ONLY = new ResourceBundle.SingleFormatControl(FORMAT_PROPERTIES);
         CLASS_ONLY = new ResourceBundle.SingleFormatControl(FORMAT_CLASS);
      }
   }

   public static class Control {
      public static final List<String> FORMAT_DEFAULT = Collections.unmodifiableList(Arrays.asList("java.class", "java.properties"));
      public static final List<String> FORMAT_CLASS = Collections.unmodifiableList(Arrays.asList("java.class"));
      public static final List<String> FORMAT_PROPERTIES = Collections.unmodifiableList(Arrays.asList("java.properties"));
      public static final long TTL_DONT_CACHE = -1L;
      public static final long TTL_NO_EXPIRATION_CONTROL = -2L;
      private static final ResourceBundle.Control INSTANCE = new ResourceBundle.Control();
      private static final ResourceBundle.Control.CandidateListCache CANDIDATES_CACHE = new ResourceBundle.Control.CandidateListCache();

      protected Control() {
      }

      public static final ResourceBundle.Control getControl(List<String> var0) {
         if (var0.equals(FORMAT_PROPERTIES)) {
            return ResourceBundle.SingleFormatControl.PROPERTIES_ONLY;
         } else if (var0.equals(FORMAT_CLASS)) {
            return ResourceBundle.SingleFormatControl.CLASS_ONLY;
         } else if (var0.equals(FORMAT_DEFAULT)) {
            return INSTANCE;
         } else {
            throw new IllegalArgumentException();
         }
      }

      public static final ResourceBundle.Control getNoFallbackControl(List<String> var0) {
         if (var0.equals(FORMAT_DEFAULT)) {
            return ResourceBundle.NoFallbackControl.NO_FALLBACK;
         } else if (var0.equals(FORMAT_PROPERTIES)) {
            return ResourceBundle.NoFallbackControl.PROPERTIES_ONLY_NO_FALLBACK;
         } else if (var0.equals(FORMAT_CLASS)) {
            return ResourceBundle.NoFallbackControl.CLASS_ONLY_NO_FALLBACK;
         } else {
            throw new IllegalArgumentException();
         }
      }

      public List<String> getFormats(String var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            return FORMAT_DEFAULT;
         }
      }

      public List<Locale> getCandidateLocales(String var1, Locale var2) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            return new ArrayList((Collection)CANDIDATES_CACHE.get(var2.getBaseLocale()));
         }
      }

      public Locale getFallbackLocale(String var1, Locale var2) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            Locale var3 = Locale.getDefault();
            return var2.equals(var3) ? null : var3;
         }
      }

      public ResourceBundle newBundle(String var1, Locale var2, String var3, ClassLoader var4, boolean var5) throws IllegalAccessException, InstantiationException, IOException {
         String var6 = this.toBundleName(var1, var2);
         Object var7 = null;
         if (var3.equals("java.class")) {
            try {
               Class var8 = var4.loadClass(var6);
               if (!ResourceBundle.class.isAssignableFrom(var8)) {
                  throw new ClassCastException(var8.getName() + " cannot be cast to ResourceBundle");
               }

               var7 = (ResourceBundle)var8.newInstance();
            } catch (ClassNotFoundException var19) {
            }
         } else {
            if (!var3.equals("java.properties")) {
               throw new IllegalArgumentException("unknown format: " + var3);
            }

            final String var20 = this.toResourceName0(var6, "properties");
            if (var20 == null) {
               return (ResourceBundle)var7;
            }

            final ClassLoader var9 = var4;
            final boolean var10 = var5;
            InputStream var11 = null;

            try {
               var11 = (InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction<InputStream>() {
                  public InputStream run() throws IOException {
                     InputStream var1 = null;
                     if (var10) {
                        URL var2 = var9.getResource(var20);
                        if (var2 != null) {
                           URLConnection var3 = var2.openConnection();
                           if (var3 != null) {
                              var3.setUseCaches(false);
                              var1 = var3.getInputStream();
                           }
                        }
                     } else {
                        var1 = var9.getResourceAsStream(var20);
                     }

                     return var1;
                  }
               });
            } catch (PrivilegedActionException var18) {
               throw (IOException)var18.getException();
            }

            if (var11 != null) {
               try {
                  var7 = new PropertyResourceBundle(var11);
               } finally {
                  var11.close();
               }
            }
         }

         return (ResourceBundle)var7;
      }

      public long getTimeToLive(String var1, Locale var2) {
         if (var1 != null && var2 != null) {
            return -2L;
         } else {
            throw new NullPointerException();
         }
      }

      public boolean needsReload(String var1, Locale var2, String var3, ClassLoader var4, ResourceBundle var5, long var6) {
         if (var5 == null) {
            throw new NullPointerException();
         } else {
            if (var3.equals("java.class") || var3.equals("java.properties")) {
               var3 = var3.substring(5);
            }

            boolean var8 = false;

            try {
               String var9 = this.toResourceName0(this.toBundleName(var1, var2), var3);
               if (var9 == null) {
                  return var8;
               }

               URL var10 = var4.getResource(var9);
               if (var10 != null) {
                  long var11 = 0L;
                  URLConnection var13 = var10.openConnection();
                  if (var13 != null) {
                     var13.setUseCaches(false);
                     if (var13 instanceof JarURLConnection) {
                        JarEntry var14 = ((JarURLConnection)var13).getJarEntry();
                        if (var14 != null) {
                           var11 = var14.getTime();
                           if (var11 == -1L) {
                              var11 = 0L;
                           }
                        }
                     } else {
                        var11 = var13.getLastModified();
                     }
                  }

                  var8 = var11 >= var6;
               }
            } catch (NullPointerException var15) {
               throw var15;
            } catch (Exception var16) {
            }

            return var8;
         }
      }

      public String toBundleName(String var1, Locale var2) {
         if (var2 == Locale.ROOT) {
            return var1;
         } else {
            String var3 = var2.getLanguage();
            String var4 = var2.getScript();
            String var5 = var2.getCountry();
            String var6 = var2.getVariant();
            if (var3 == "" && var5 == "" && var6 == "") {
               return var1;
            } else {
               StringBuilder var7 = new StringBuilder(var1);
               var7.append('_');
               if (var4 != "") {
                  if (var6 != "") {
                     var7.append(var3).append('_').append(var4).append('_').append(var5).append('_').append(var6);
                  } else if (var5 != "") {
                     var7.append(var3).append('_').append(var4).append('_').append(var5);
                  } else {
                     var7.append(var3).append('_').append(var4);
                  }
               } else if (var6 != "") {
                  var7.append(var3).append('_').append(var5).append('_').append(var6);
               } else if (var5 != "") {
                  var7.append(var3).append('_').append(var5);
               } else {
                  var7.append(var3);
               }

               return var7.toString();
            }
         }
      }

      public final String toResourceName(String var1, String var2) {
         StringBuilder var3 = new StringBuilder(var1.length() + 1 + var2.length());
         var3.append(var1.replace('.', '/')).append('.').append(var2);
         return var3.toString();
      }

      private String toResourceName0(String var1, String var2) {
         return var1.contains("://") ? null : this.toResourceName(var1, var2);
      }

      private static class CandidateListCache extends LocaleObjectCache<BaseLocale, List<Locale>> {
         private CandidateListCache() {
         }

         protected List<Locale> createObject(BaseLocale var1) {
            String var2 = var1.getLanguage();
            String var3 = var1.getScript();
            String var4 = var1.getRegion();
            String var5 = var1.getVariant();
            boolean var6 = false;
            boolean var7 = false;
            if (var2.equals("no")) {
               if (var4.equals("NO") && var5.equals("NY")) {
                  var5 = "";
                  var7 = true;
               } else {
                  var6 = true;
               }
            }

            List var8;
            if (!var2.equals("nb") && !var6) {
               if (!var2.equals("nn") && !var7) {
                  if (var2.equals("zh")) {
                     byte var13;
                     if (var3.length() == 0 && var4.length() > 0) {
                        var13 = -1;
                        switch(var4.hashCode()) {
                        case 2155:
                           if (var4.equals("CN")) {
                              var13 = 3;
                           }
                           break;
                        case 2307:
                           if (var4.equals("HK")) {
                              var13 = 1;
                           }
                           break;
                        case 2466:
                           if (var4.equals("MO")) {
                              var13 = 2;
                           }
                           break;
                        case 2644:
                           if (var4.equals("SG")) {
                              var13 = 4;
                           }
                           break;
                        case 2691:
                           if (var4.equals("TW")) {
                              var13 = 0;
                           }
                        }

                        switch(var13) {
                        case 0:
                        case 1:
                        case 2:
                           var3 = "Hant";
                           break;
                        case 3:
                        case 4:
                           var3 = "Hans";
                        }
                     } else if (var3.length() > 0 && var4.length() == 0) {
                        var13 = -1;
                        switch(var3.hashCode()) {
                        case 2241694:
                           if (var3.equals("Hans")) {
                              var13 = 0;
                           }
                           break;
                        case 2241695:
                           if (var3.equals("Hant")) {
                              var13 = 1;
                           }
                        }

                        switch(var13) {
                        case 0:
                           var4 = "CN";
                           break;
                        case 1:
                           var4 = "TW";
                        }
                     }
                  }

                  return getDefaultList(var2, var3, var4, var5);
               } else {
                  var8 = getDefaultList("nn", var3, var4, var5);
                  int var12 = var8.size() - 1;
                  var8.add(var12++, Locale.getInstance("no", "NO", "NY"));
                  var8.add(var12++, Locale.getInstance("no", "NO", ""));
                  var8.add(var12++, Locale.getInstance("no", "", ""));
                  return var8;
               }
            } else {
               var8 = getDefaultList("nb", var3, var4, var5);
               LinkedList var9 = new LinkedList();
               Iterator var10 = var8.iterator();

               while(var10.hasNext()) {
                  Locale var11 = (Locale)var10.next();
                  var9.add(var11);
                  if (var11.getLanguage().length() == 0) {
                     break;
                  }

                  var9.add(Locale.getInstance("no", var11.getScript(), var11.getCountry(), var11.getVariant(), (LocaleExtensions)null));
               }

               return var9;
            }
         }

         private static List<Locale> getDefaultList(String var0, String var1, String var2, String var3) {
            LinkedList var4 = null;
            if (var3.length() > 0) {
               var4 = new LinkedList();

               for(int var5 = var3.length(); var5 != -1; var5 = var3.lastIndexOf(95, var5)) {
                  var4.add(var3.substring(0, var5));
                  --var5;
               }
            }

            LinkedList var8 = new LinkedList();
            Iterator var6;
            String var7;
            if (var4 != null) {
               var6 = var4.iterator();

               while(var6.hasNext()) {
                  var7 = (String)var6.next();
                  var8.add(Locale.getInstance(var0, var1, var2, var7, (LocaleExtensions)null));
               }
            }

            if (var2.length() > 0) {
               var8.add(Locale.getInstance(var0, var1, var2, "", (LocaleExtensions)null));
            }

            if (var1.length() > 0) {
               var8.add(Locale.getInstance(var0, var1, "", "", (LocaleExtensions)null));
               if (var4 != null) {
                  var6 = var4.iterator();

                  while(var6.hasNext()) {
                     var7 = (String)var6.next();
                     var8.add(Locale.getInstance(var0, "", var2, var7, (LocaleExtensions)null));
                  }
               }

               if (var2.length() > 0) {
                  var8.add(Locale.getInstance(var0, "", var2, "", (LocaleExtensions)null));
               }
            }

            if (var0.length() > 0) {
               var8.add(Locale.getInstance(var0, "", "", "", (LocaleExtensions)null));
            }

            var8.add(Locale.ROOT);
            return var8;
         }

         // $FF: synthetic method
         CandidateListCache(Object var1) {
            this();
         }
      }
   }

   private static class BundleReference extends SoftReference<ResourceBundle> implements ResourceBundle.CacheKeyReference {
      private ResourceBundle.CacheKey cacheKey;

      BundleReference(ResourceBundle var1, ReferenceQueue<Object> var2, ResourceBundle.CacheKey var3) {
         super(var1, var2);
         this.cacheKey = var3;
      }

      public ResourceBundle.CacheKey getCacheKey() {
         return this.cacheKey;
      }
   }

   private static class LoaderReference extends WeakReference<ClassLoader> implements ResourceBundle.CacheKeyReference {
      private ResourceBundle.CacheKey cacheKey;

      LoaderReference(ClassLoader var1, ReferenceQueue<Object> var2, ResourceBundle.CacheKey var3) {
         super(var1, var2);
         this.cacheKey = var3;
      }

      public ResourceBundle.CacheKey getCacheKey() {
         return this.cacheKey;
      }
   }

   private interface CacheKeyReference {
      ResourceBundle.CacheKey getCacheKey();
   }

   private static class CacheKey implements Cloneable {
      private String name;
      private Locale locale;
      private ResourceBundle.LoaderReference loaderRef;
      private String format;
      private volatile long loadTime;
      private volatile long expirationTime;
      private Throwable cause;
      private int hashCodeCache;

      CacheKey(String var1, Locale var2, ClassLoader var3) {
         this.name = var1;
         this.locale = var2;
         if (var3 == null) {
            this.loaderRef = null;
         } else {
            this.loaderRef = new ResourceBundle.LoaderReference(var3, ResourceBundle.referenceQueue, this);
         }

         this.calculateHashCode();
      }

      String getName() {
         return this.name;
      }

      ResourceBundle.CacheKey setName(String var1) {
         if (!this.name.equals(var1)) {
            this.name = var1;
            this.calculateHashCode();
         }

         return this;
      }

      Locale getLocale() {
         return this.locale;
      }

      ResourceBundle.CacheKey setLocale(Locale var1) {
         if (!this.locale.equals(var1)) {
            this.locale = var1;
            this.calculateHashCode();
         }

         return this;
      }

      ClassLoader getLoader() {
         return this.loaderRef != null ? (ClassLoader)this.loaderRef.get() : null;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            try {
               ResourceBundle.CacheKey var2 = (ResourceBundle.CacheKey)var1;
               if (this.hashCodeCache != var2.hashCodeCache) {
                  return false;
               } else if (!this.name.equals(var2.name)) {
                  return false;
               } else if (!this.locale.equals(var2.locale)) {
                  return false;
               } else if (this.loaderRef == null) {
                  return var2.loaderRef == null;
               } else {
                  ClassLoader var3 = (ClassLoader)this.loaderRef.get();
                  return var2.loaderRef != null && var3 != null && var3 == var2.loaderRef.get();
               }
            } catch (ClassCastException | NullPointerException var4) {
               return false;
            }
         }
      }

      public int hashCode() {
         return this.hashCodeCache;
      }

      private void calculateHashCode() {
         this.hashCodeCache = this.name.hashCode() << 3;
         this.hashCodeCache ^= this.locale.hashCode();
         ClassLoader var1 = this.getLoader();
         if (var1 != null) {
            this.hashCodeCache ^= var1.hashCode();
         }

      }

      public Object clone() {
         try {
            ResourceBundle.CacheKey var1 = (ResourceBundle.CacheKey)super.clone();
            if (this.loaderRef != null) {
               var1.loaderRef = new ResourceBundle.LoaderReference((ClassLoader)this.loaderRef.get(), ResourceBundle.referenceQueue, var1);
            }

            var1.cause = null;
            return var1;
         } catch (CloneNotSupportedException var2) {
            throw new InternalError(var2);
         }
      }

      String getFormat() {
         return this.format;
      }

      void setFormat(String var1) {
         this.format = var1;
      }

      private void setCause(Throwable var1) {
         if (this.cause == null) {
            this.cause = var1;
         } else if (this.cause instanceof ClassNotFoundException) {
            this.cause = var1;
         }

      }

      private Throwable getCause() {
         return this.cause;
      }

      public String toString() {
         String var1 = this.locale.toString();
         if (var1.length() == 0) {
            if (this.locale.getVariant().length() != 0) {
               var1 = "__" + this.locale.getVariant();
            } else {
               var1 = "\"\"";
            }
         }

         return "CacheKey[" + this.name + ", lc=" + var1 + ", ldr=" + this.getLoader() + "(format=" + this.format + ")]";
      }
   }

   private static class RBClassLoader extends ClassLoader {
      private static final ResourceBundle.RBClassLoader INSTANCE = (ResourceBundle.RBClassLoader)AccessController.doPrivileged(new PrivilegedAction<ResourceBundle.RBClassLoader>() {
         public ResourceBundle.RBClassLoader run() {
            return new ResourceBundle.RBClassLoader();
         }
      });
      private static final ClassLoader loader;

      private RBClassLoader() {
      }

      public Class<?> loadClass(String var1) throws ClassNotFoundException {
         return loader != null ? loader.loadClass(var1) : Class.forName(var1);
      }

      public URL getResource(String var1) {
         return loader != null ? loader.getResource(var1) : ClassLoader.getSystemResource(var1);
      }

      public InputStream getResourceAsStream(String var1) {
         return loader != null ? loader.getResourceAsStream(var1) : ClassLoader.getSystemResourceAsStream(var1);
      }

      // $FF: synthetic method
      RBClassLoader(Object var1) {
         this();
      }

      static {
         ClassLoader var0;
         ClassLoader var1;
         for(var0 = ClassLoader.getSystemClassLoader(); (var1 = var0.getParent()) != null; var0 = var1) {
         }

         loader = var0;
      }
   }
}
