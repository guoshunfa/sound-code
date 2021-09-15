package java.awt;

import java.awt.event.KeyEvent;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import sun.awt.AppContext;

public class AWTKeyStroke implements Serializable {
   static final long serialVersionUID = -6430539691155161871L;
   private static Map<String, Integer> modifierKeywords;
   private static VKCollection vks;
   private static Object APP_CONTEXT_CACHE_KEY = new Object();
   private static AWTKeyStroke APP_CONTEXT_KEYSTROKE_KEY = new AWTKeyStroke();
   private char keyChar = '\uffff';
   private int keyCode = 0;
   private int modifiers;
   private boolean onKeyRelease;

   private static Class<AWTKeyStroke> getAWTKeyStrokeClass() {
      Class var0 = (Class)AppContext.getAppContext().get(AWTKeyStroke.class);
      if (var0 == null) {
         var0 = AWTKeyStroke.class;
         AppContext.getAppContext().put(AWTKeyStroke.class, AWTKeyStroke.class);
      }

      return var0;
   }

   protected AWTKeyStroke() {
   }

   protected AWTKeyStroke(char var1, int var2, int var3, boolean var4) {
      this.keyChar = var1;
      this.keyCode = var2;
      this.modifiers = var3;
      this.onKeyRelease = var4;
   }

   protected static void registerSubclass(Class<?> var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("subclass cannot be null");
      } else {
         Class var1 = AWTKeyStroke.class;
         synchronized(AWTKeyStroke.class) {
            Class var2 = (Class)AppContext.getAppContext().get(AWTKeyStroke.class);
            if (var2 != null && var2.equals(var0)) {
               return;
            }
         }

         if (!AWTKeyStroke.class.isAssignableFrom(var0)) {
            throw new ClassCastException("subclass is not derived from AWTKeyStroke");
         } else {
            Constructor var12 = getCtor(var0);
            String var13 = "subclass could not be instantiated";
            if (var12 == null) {
               throw new IllegalArgumentException(var13);
            } else {
               try {
                  AWTKeyStroke var3 = (AWTKeyStroke)var12.newInstance((Object[])null);
                  if (var3 == null) {
                     throw new IllegalArgumentException(var13);
                  }
               } catch (NoSuchMethodError var6) {
                  throw new IllegalArgumentException(var13);
               } catch (ExceptionInInitializerError var7) {
                  throw new IllegalArgumentException(var13);
               } catch (InstantiationException var8) {
                  throw new IllegalArgumentException(var13);
               } catch (IllegalAccessException var9) {
                  throw new IllegalArgumentException(var13);
               } catch (InvocationTargetException var10) {
                  throw new IllegalArgumentException(var13);
               }

               Class var14 = AWTKeyStroke.class;
               synchronized(AWTKeyStroke.class) {
                  AppContext.getAppContext().put(AWTKeyStroke.class, var0);
                  AppContext.getAppContext().remove(APP_CONTEXT_CACHE_KEY);
                  AppContext.getAppContext().remove(APP_CONTEXT_KEYSTROKE_KEY);
               }
            }
         }
      }
   }

   private static Constructor getCtor(final Class var0) {
      Constructor var1 = (Constructor)AccessController.doPrivileged(new PrivilegedAction<Constructor>() {
         public Constructor run() {
            try {
               Constructor var1 = var0.getDeclaredConstructor((Class[])null);
               if (var1 != null) {
                  var1.setAccessible(true);
               }

               return var1;
            } catch (SecurityException var2) {
            } catch (NoSuchMethodException var3) {
            }

            return null;
         }
      });
      return var1;
   }

   private static synchronized AWTKeyStroke getCachedStroke(char var0, int var1, int var2, boolean var3) {
      Object var4 = (Map)AppContext.getAppContext().get(APP_CONTEXT_CACHE_KEY);
      AWTKeyStroke var5 = (AWTKeyStroke)AppContext.getAppContext().get(APP_CONTEXT_KEYSTROKE_KEY);
      if (var4 == null) {
         var4 = new HashMap();
         AppContext.getAppContext().put(APP_CONTEXT_CACHE_KEY, var4);
      }

      if (var5 == null) {
         try {
            Class var6 = getAWTKeyStrokeClass();
            var5 = (AWTKeyStroke)getCtor(var6).newInstance((Object[])null);
            AppContext.getAppContext().put(APP_CONTEXT_KEYSTROKE_KEY, var5);
         } catch (InstantiationException var7) {
            assert false;
         } catch (IllegalAccessException var8) {
            assert false;
         } catch (InvocationTargetException var9) {
            assert false;
         }
      }

      var5.keyChar = var0;
      var5.keyCode = var1;
      var5.modifiers = mapNewModifiers(mapOldModifiers(var2));
      var5.onKeyRelease = var3;
      AWTKeyStroke var10 = (AWTKeyStroke)((Map)var4).get(var5);
      if (var10 == null) {
         var10 = var5;
         ((Map)var4).put(var5, var5);
         AppContext.getAppContext().remove(APP_CONTEXT_KEYSTROKE_KEY);
      }

      return var10;
   }

   public static AWTKeyStroke getAWTKeyStroke(char var0) {
      return getCachedStroke(var0, 0, 0, false);
   }

   public static AWTKeyStroke getAWTKeyStroke(Character var0, int var1) {
      if (var0 == null) {
         throw new IllegalArgumentException("keyChar cannot be null");
      } else {
         return getCachedStroke(var0, 0, var1, false);
      }
   }

   public static AWTKeyStroke getAWTKeyStroke(int var0, int var1, boolean var2) {
      return getCachedStroke('\uffff', var0, var1, var2);
   }

   public static AWTKeyStroke getAWTKeyStroke(int var0, int var1) {
      return getCachedStroke('\uffff', var0, var1, false);
   }

   public static AWTKeyStroke getAWTKeyStrokeForEvent(KeyEvent var0) {
      int var1 = var0.getID();
      switch(var1) {
      case 400:
         return getCachedStroke(var0.getKeyChar(), 0, var0.getModifiers(), false);
      case 401:
      case 402:
         return getCachedStroke('\uffff', var0.getKeyCode(), var0.getModifiers(), var1 == 402);
      default:
         return null;
      }
   }

   public static AWTKeyStroke getAWTKeyStroke(String var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("String cannot be null");
      } else {
         StringTokenizer var2 = new StringTokenizer(var0, " ");
         int var3 = 0;
         boolean var4 = false;
         boolean var5 = false;
         boolean var6 = false;
         Class var7 = AWTKeyStroke.class;
         synchronized(AWTKeyStroke.class) {
            if (modifierKeywords == null) {
               HashMap var8 = new HashMap(8, 1.0F);
               var8.put("shift", 65);
               var8.put("control", 130);
               var8.put("ctrl", 130);
               var8.put("meta", 260);
               var8.put("alt", 520);
               var8.put("altGraph", 8224);
               var8.put("button1", 1024);
               var8.put("button2", 2048);
               var8.put("button3", 4096);
               modifierKeywords = Collections.synchronizedMap(var8);
            }
         }

         int var13 = var2.countTokens();

         for(int var14 = 1; var14 <= var13; ++var14) {
            String var9 = var2.nextToken();
            if (var5) {
               if (var9.length() == 1 && var14 == var13) {
                  return getCachedStroke(var9.charAt(0), 0, var3, false);
               }

               throw new IllegalArgumentException("String formatted incorrectly");
            }

            if (var6 || var4 || var14 == var13) {
               if (var14 != var13) {
                  throw new IllegalArgumentException("String formatted incorrectly");
               } else {
                  String var15 = "VK_" + var9;
                  int var11 = getVKValue(var15);
                  return getCachedStroke('\uffff', var11, var3, var4);
               }
            }

            if (var9.equals("released")) {
               var4 = true;
            } else if (var9.equals("pressed")) {
               var6 = true;
            } else if (var9.equals("typed")) {
               var5 = true;
            } else {
               Integer var10 = (Integer)modifierKeywords.get(var9);
               if (var10 == null) {
                  throw new IllegalArgumentException("String formatted incorrectly");
               }

               var3 |= var10;
            }
         }

         throw new IllegalArgumentException("String formatted incorrectly");
      }
   }

   private static VKCollection getVKCollection() {
      if (vks == null) {
         vks = new VKCollection();
      }

      return vks;
   }

   private static int getVKValue(String var0) {
      VKCollection var1 = getVKCollection();
      Integer var2 = var1.findCode(var0);
      if (var2 == null) {
         boolean var3 = false;

         int var8;
         try {
            var8 = KeyEvent.class.getField(var0).getInt(KeyEvent.class);
         } catch (NoSuchFieldException var6) {
            throw new IllegalArgumentException("String formatted incorrectly");
         } catch (IllegalAccessException var7) {
            throw new IllegalArgumentException("String formatted incorrectly");
         }

         var2 = var8;
         var1.put(var0, var2);
      }

      return var2;
   }

   public final char getKeyChar() {
      return this.keyChar;
   }

   public final int getKeyCode() {
      return this.keyCode;
   }

   public final int getModifiers() {
      return this.modifiers;
   }

   public final boolean isOnKeyRelease() {
      return this.onKeyRelease;
   }

   public final int getKeyEventType() {
      if (this.keyCode == 0) {
         return 400;
      } else {
         return this.onKeyRelease ? 402 : 401;
      }
   }

   public int hashCode() {
      return (this.keyChar + 1) * 2 * (this.keyCode + 1) * (this.modifiers + 1) + (this.onKeyRelease ? 1 : 2);
   }

   public final boolean equals(Object var1) {
      if (!(var1 instanceof AWTKeyStroke)) {
         return false;
      } else {
         AWTKeyStroke var2 = (AWTKeyStroke)var1;
         return var2.keyChar == this.keyChar && var2.keyCode == this.keyCode && var2.onKeyRelease == this.onKeyRelease && var2.modifiers == this.modifiers;
      }
   }

   public String toString() {
      return this.keyCode == 0 ? getModifiersText(this.modifiers) + "typed " + this.keyChar : getModifiersText(this.modifiers) + (this.onKeyRelease ? "released" : "pressed") + " " + getVKText(this.keyCode);
   }

   static String getModifiersText(int var0) {
      StringBuilder var1 = new StringBuilder();
      if ((var0 & 64) != 0) {
         var1.append("shift ");
      }

      if ((var0 & 128) != 0) {
         var1.append("ctrl ");
      }

      if ((var0 & 256) != 0) {
         var1.append("meta ");
      }

      if ((var0 & 512) != 0) {
         var1.append("alt ");
      }

      if ((var0 & 8192) != 0) {
         var1.append("altGraph ");
      }

      if ((var0 & 1024) != 0) {
         var1.append("button1 ");
      }

      if ((var0 & 2048) != 0) {
         var1.append("button2 ");
      }

      if ((var0 & 4096) != 0) {
         var1.append("button3 ");
      }

      return var1.toString();
   }

   static String getVKText(int var0) {
      VKCollection var1 = getVKCollection();
      Integer var2 = var0;
      String var3 = var1.findName(var2);
      if (var3 != null) {
         return var3.substring(3);
      } else {
         byte var4 = 25;
         Field[] var5 = KeyEvent.class.getDeclaredFields();

         for(int var6 = 0; var6 < var5.length; ++var6) {
            try {
               if (var5[var6].getModifiers() == var4 && var5[var6].getType() == Integer.TYPE && var5[var6].getName().startsWith("VK_") && var5[var6].getInt(KeyEvent.class) == var0) {
                  var3 = var5[var6].getName();
                  var1.put(var3, var2);
                  return var3.substring(3);
               }
            } catch (IllegalAccessException var8) {
               assert false;
            }
         }

         return "UNKNOWN";
      }
   }

   protected Object readResolve() throws ObjectStreamException {
      Class var1 = AWTKeyStroke.class;
      synchronized(AWTKeyStroke.class) {
         return this.getClass().equals(getAWTKeyStrokeClass()) ? getCachedStroke(this.keyChar, this.keyCode, this.modifiers, this.onKeyRelease) : this;
      }
   }

   private static int mapOldModifiers(int var0) {
      if ((var0 & 1) != 0) {
         var0 |= 64;
      }

      if ((var0 & 8) != 0) {
         var0 |= 512;
      }

      if ((var0 & 32) != 0) {
         var0 |= 8192;
      }

      if ((var0 & 2) != 0) {
         var0 |= 128;
      }

      if ((var0 & 4) != 0) {
         var0 |= 256;
      }

      var0 &= 16320;
      return var0;
   }

   private static int mapNewModifiers(int var0) {
      if ((var0 & 64) != 0) {
         var0 |= 1;
      }

      if ((var0 & 512) != 0) {
         var0 |= 8;
      }

      if ((var0 & 8192) != 0) {
         var0 |= 32;
      }

      if ((var0 & 128) != 0) {
         var0 |= 2;
      }

      if ((var0 & 256) != 0) {
         var0 |= 4;
      }

      return var0;
   }

   static {
      Toolkit.loadLibraries();
   }
}
