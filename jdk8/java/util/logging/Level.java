package java.util.logging;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class Level implements Serializable {
   private static final String defaultBundle = "sun.util.logging.resources.logging";
   private final String name;
   private final int value;
   private final String resourceBundleName;
   private transient String localizedLevelName;
   private transient Locale cachedLocale;
   public static final Level OFF = new Level("OFF", Integer.MAX_VALUE, "sun.util.logging.resources.logging");
   public static final Level SEVERE = new Level("SEVERE", 1000, "sun.util.logging.resources.logging");
   public static final Level WARNING = new Level("WARNING", 900, "sun.util.logging.resources.logging");
   public static final Level INFO = new Level("INFO", 800, "sun.util.logging.resources.logging");
   public static final Level CONFIG = new Level("CONFIG", 700, "sun.util.logging.resources.logging");
   public static final Level FINE = new Level("FINE", 500, "sun.util.logging.resources.logging");
   public static final Level FINER = new Level("FINER", 400, "sun.util.logging.resources.logging");
   public static final Level FINEST = new Level("FINEST", 300, "sun.util.logging.resources.logging");
   public static final Level ALL = new Level("ALL", Integer.MIN_VALUE, "sun.util.logging.resources.logging");
   private static final long serialVersionUID = -8176160795706313070L;

   protected Level(String var1, int var2) {
      this(var1, var2, (String)null);
   }

   protected Level(String var1, int var2, String var3) {
      this(var1, var2, var3, true);
   }

   private Level(String var1, int var2, String var3, boolean var4) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.name = var1;
         this.value = var2;
         this.resourceBundleName = var3;
         this.localizedLevelName = var3 == null ? var1 : null;
         this.cachedLocale = null;
         if (var4) {
            Level.KnownLevel.add(this);
         }

      }
   }

   public String getResourceBundleName() {
      return this.resourceBundleName;
   }

   public String getName() {
      return this.name;
   }

   public String getLocalizedName() {
      return this.getLocalizedLevelName();
   }

   final String getLevelName() {
      return this.name;
   }

   private String computeLocalizedLevelName(Locale var1) {
      if (!"sun.util.logging.resources.logging".equals(this.resourceBundleName)) {
         return ResourceBundle.getBundle(this.resourceBundleName, var1, ClassLoader.getSystemClassLoader()).getString(this.name);
      } else {
         ResourceBundle var2 = ResourceBundle.getBundle("sun.util.logging.resources.logging", var1);
         String var3 = var2.getString(this.name);
         Locale var4 = var2.getLocale();
         Locale var5 = !Locale.ROOT.equals(var4) && !this.name.equals(var3.toUpperCase(Locale.ROOT)) ? var4 : Locale.ROOT;
         return Locale.ROOT.equals(var5) ? this.name : var3.toUpperCase(var5);
      }
   }

   final String getCachedLocalizedLevelName() {
      if (this.localizedLevelName != null && this.cachedLocale != null && this.cachedLocale.equals(Locale.getDefault())) {
         return this.localizedLevelName;
      } else {
         return this.resourceBundleName == null ? this.name : null;
      }
   }

   final synchronized String getLocalizedLevelName() {
      String var1 = this.getCachedLocalizedLevelName();
      if (var1 != null) {
         return var1;
      } else {
         Locale var2 = Locale.getDefault();

         try {
            this.localizedLevelName = this.computeLocalizedLevelName(var2);
         } catch (Exception var4) {
            this.localizedLevelName = this.name;
         }

         this.cachedLocale = var2;
         return this.localizedLevelName;
      }
   }

   static Level findLevel(String var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         Level.KnownLevel var1 = Level.KnownLevel.findByName(var0);
         if (var1 != null) {
            return var1.mirroredLevel;
         } else {
            try {
               int var2 = Integer.parseInt(var0);
               var1 = Level.KnownLevel.findByValue(var2);
               if (var1 == null) {
                  new Level(var0, var2);
                  var1 = Level.KnownLevel.findByValue(var2);
               }

               return var1.mirroredLevel;
            } catch (NumberFormatException var4) {
               var1 = Level.KnownLevel.findByLocalizedLevelName(var0);
               return var1 != null ? var1.mirroredLevel : null;
            }
         }
      }
   }

   public final String toString() {
      return this.name;
   }

   public final int intValue() {
      return this.value;
   }

   private Object readResolve() {
      Level.KnownLevel var1 = Level.KnownLevel.matches(this);
      if (var1 != null) {
         return var1.levelObject;
      } else {
         Level var2 = new Level(this.name, this.value, this.resourceBundleName);
         return var2;
      }
   }

   public static synchronized Level parse(String var0) throws IllegalArgumentException {
      var0.length();
      Level.KnownLevel var1 = Level.KnownLevel.findByName(var0);
      if (var1 != null) {
         return var1.levelObject;
      } else {
         try {
            int var2 = Integer.parseInt(var0);
            var1 = Level.KnownLevel.findByValue(var2);
            if (var1 == null) {
               new Level(var0, var2);
               var1 = Level.KnownLevel.findByValue(var2);
            }

            return var1.levelObject;
         } catch (NumberFormatException var4) {
            var1 = Level.KnownLevel.findByLocalizedLevelName(var0);
            if (var1 != null) {
               return var1.levelObject;
            } else {
               throw new IllegalArgumentException("Bad level \"" + var0 + "\"");
            }
         }
      }
   }

   public boolean equals(Object var1) {
      try {
         Level var2 = (Level)var1;
         return var2.value == this.value;
      } catch (Exception var3) {
         return false;
      }
   }

   public int hashCode() {
      return this.value;
   }

   // $FF: synthetic method
   Level(String var1, int var2, String var3, boolean var4, Object var5) {
      this(var1, var2, var3, var4);
   }

   static final class KnownLevel {
      private static Map<String, List<Level.KnownLevel>> nameToLevels = new HashMap();
      private static Map<Integer, List<Level.KnownLevel>> intToLevels = new HashMap();
      final Level levelObject;
      final Level mirroredLevel;

      KnownLevel(Level var1) {
         this.levelObject = var1;
         if (var1.getClass() == Level.class) {
            this.mirroredLevel = var1;
         } else {
            this.mirroredLevel = new Level(var1.name, var1.value, var1.resourceBundleName, false);
         }

      }

      static synchronized void add(Level var0) {
         Level.KnownLevel var1 = new Level.KnownLevel(var0);
         Object var2 = (List)nameToLevels.get(var0.name);
         if (var2 == null) {
            var2 = new ArrayList();
            nameToLevels.put(var0.name, var2);
         }

         ((List)var2).add(var1);
         var2 = (List)intToLevels.get(var0.value);
         if (var2 == null) {
            var2 = new ArrayList();
            intToLevels.put(var0.value, var2);
         }

         ((List)var2).add(var1);
      }

      static synchronized Level.KnownLevel findByName(String var0) {
         List var1 = (List)nameToLevels.get(var0);
         return var1 != null ? (Level.KnownLevel)var1.get(0) : null;
      }

      static synchronized Level.KnownLevel findByValue(int var0) {
         List var1 = (List)intToLevels.get(var0);
         return var1 != null ? (Level.KnownLevel)var1.get(0) : null;
      }

      static synchronized Level.KnownLevel findByLocalizedLevelName(String var0) {
         Iterator var1 = nameToLevels.values().iterator();

         while(var1.hasNext()) {
            List var2 = (List)var1.next();
            Iterator var3 = var2.iterator();

            while(var3.hasNext()) {
               Level.KnownLevel var4 = (Level.KnownLevel)var3.next();
               String var5 = var4.levelObject.getLocalizedLevelName();
               if (var0.equals(var5)) {
                  return var4;
               }
            }
         }

         return null;
      }

      static synchronized Level.KnownLevel matches(Level var0) {
         List var1 = (List)nameToLevels.get(var0.name);
         if (var1 != null) {
            Iterator var2 = var1.iterator();

            Level.KnownLevel var3;
            Level var4;
            Class var5;
            do {
               do {
                  do {
                     if (!var2.hasNext()) {
                        return null;
                     }

                     var3 = (Level.KnownLevel)var2.next();
                     var4 = var3.mirroredLevel;
                     var5 = var3.levelObject.getClass();
                  } while(var0.value != var4.value);
               } while(var0.resourceBundleName != var4.resourceBundleName && (var0.resourceBundleName == null || !var0.resourceBundleName.equals(var4.resourceBundleName)));
            } while(var5 != var0.getClass());

            return var3;
         } else {
            return null;
         }
      }
   }
}
