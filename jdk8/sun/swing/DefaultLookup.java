package sun.swing;

import java.awt.Color;
import java.awt.Insets;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import sun.awt.AppContext;

public class DefaultLookup {
   private static final Object DEFAULT_LOOKUP_KEY = new StringBuffer("DefaultLookup");
   private static Thread currentDefaultThread;
   private static DefaultLookup currentDefaultLookup;
   private static boolean isLookupSet;

   public static void setDefaultLookup(DefaultLookup var0) {
      Class var1 = DefaultLookup.class;
      synchronized(DefaultLookup.class) {
         if (isLookupSet || var0 != null) {
            if (var0 == null) {
               var0 = new DefaultLookup();
            }

            isLookupSet = true;
            AppContext.getAppContext().put(DEFAULT_LOOKUP_KEY, var0);
            currentDefaultThread = Thread.currentThread();
            currentDefaultLookup = var0;
         }
      }
   }

   public static Object get(JComponent var0, ComponentUI var1, String var2) {
      Class var4 = DefaultLookup.class;
      boolean var3;
      synchronized(DefaultLookup.class) {
         var3 = isLookupSet;
      }

      if (!var3) {
         return UIManager.get(var2, var0.getLocale());
      } else {
         Thread var10 = Thread.currentThread();
         Class var6 = DefaultLookup.class;
         DefaultLookup var5;
         synchronized(DefaultLookup.class) {
            if (var10 == currentDefaultThread) {
               var5 = currentDefaultLookup;
            } else {
               var5 = (DefaultLookup)AppContext.getAppContext().get(DEFAULT_LOOKUP_KEY);
               if (var5 == null) {
                  var5 = new DefaultLookup();
                  AppContext.getAppContext().put(DEFAULT_LOOKUP_KEY, var5);
               }

               currentDefaultThread = var10;
               currentDefaultLookup = var5;
            }
         }

         return var5.getDefault(var0, var1, var2);
      }
   }

   public static int getInt(JComponent var0, ComponentUI var1, String var2, int var3) {
      Object var4 = get(var0, var1, var2);
      return var4 != null && var4 instanceof Number ? ((Number)var4).intValue() : var3;
   }

   public static int getInt(JComponent var0, ComponentUI var1, String var2) {
      return getInt(var0, var1, var2, -1);
   }

   public static Insets getInsets(JComponent var0, ComponentUI var1, String var2, Insets var3) {
      Object var4 = get(var0, var1, var2);
      return var4 != null && var4 instanceof Insets ? (Insets)var4 : var3;
   }

   public static Insets getInsets(JComponent var0, ComponentUI var1, String var2) {
      return getInsets(var0, var1, var2, (Insets)null);
   }

   public static boolean getBoolean(JComponent var0, ComponentUI var1, String var2, boolean var3) {
      Object var4 = get(var0, var1, var2);
      return var4 != null && var4 instanceof Boolean ? (Boolean)var4 : var3;
   }

   public static boolean getBoolean(JComponent var0, ComponentUI var1, String var2) {
      return getBoolean(var0, var1, var2, false);
   }

   public static Color getColor(JComponent var0, ComponentUI var1, String var2, Color var3) {
      Object var4 = get(var0, var1, var2);
      return var4 != null && var4 instanceof Color ? (Color)var4 : var3;
   }

   public static Color getColor(JComponent var0, ComponentUI var1, String var2) {
      return getColor(var0, var1, var2, (Color)null);
   }

   public static Icon getIcon(JComponent var0, ComponentUI var1, String var2, Icon var3) {
      Object var4 = get(var0, var1, var2);
      return var4 != null && var4 instanceof Icon ? (Icon)var4 : var3;
   }

   public static Icon getIcon(JComponent var0, ComponentUI var1, String var2) {
      return getIcon(var0, var1, var2, (Icon)null);
   }

   public static Border getBorder(JComponent var0, ComponentUI var1, String var2, Border var3) {
      Object var4 = get(var0, var1, var2);
      return var4 != null && var4 instanceof Border ? (Border)var4 : var3;
   }

   public static Border getBorder(JComponent var0, ComponentUI var1, String var2) {
      return getBorder(var0, var1, var2, (Border)null);
   }

   public Object getDefault(JComponent var1, ComponentUI var2, String var3) {
      return UIManager.get(var3, var1.getLocale());
   }
}
