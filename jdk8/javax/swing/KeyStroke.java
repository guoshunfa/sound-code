package javax.swing;

import java.awt.AWTKeyStroke;
import java.awt.event.KeyEvent;

public class KeyStroke extends AWTKeyStroke {
   private static final long serialVersionUID = -9060180771037902530L;

   private KeyStroke() {
   }

   private KeyStroke(char var1, int var2, int var3, boolean var4) {
      super(var1, var2, var3, var4);
   }

   public static KeyStroke getKeyStroke(char var0) {
      Class var1 = AWTKeyStroke.class;
      synchronized(AWTKeyStroke.class) {
         registerSubclass(KeyStroke.class);
         return (KeyStroke)getAWTKeyStroke(var0);
      }
   }

   /** @deprecated */
   @Deprecated
   public static KeyStroke getKeyStroke(char var0, boolean var1) {
      return new KeyStroke(var0, 0, 0, var1);
   }

   public static KeyStroke getKeyStroke(Character var0, int var1) {
      Class var2 = AWTKeyStroke.class;
      synchronized(AWTKeyStroke.class) {
         registerSubclass(KeyStroke.class);
         return (KeyStroke)getAWTKeyStroke(var0, var1);
      }
   }

   public static KeyStroke getKeyStroke(int var0, int var1, boolean var2) {
      Class var3 = AWTKeyStroke.class;
      synchronized(AWTKeyStroke.class) {
         registerSubclass(KeyStroke.class);
         return (KeyStroke)getAWTKeyStroke(var0, var1, var2);
      }
   }

   public static KeyStroke getKeyStroke(int var0, int var1) {
      Class var2 = AWTKeyStroke.class;
      synchronized(AWTKeyStroke.class) {
         registerSubclass(KeyStroke.class);
         return (KeyStroke)getAWTKeyStroke(var0, var1);
      }
   }

   public static KeyStroke getKeyStrokeForEvent(KeyEvent var0) {
      Class var1 = AWTKeyStroke.class;
      synchronized(AWTKeyStroke.class) {
         registerSubclass(KeyStroke.class);
         return (KeyStroke)getAWTKeyStrokeForEvent(var0);
      }
   }

   public static KeyStroke getKeyStroke(String var0) {
      if (var0 != null && var0.length() != 0) {
         Class var1 = AWTKeyStroke.class;
         synchronized(AWTKeyStroke.class) {
            registerSubclass(KeyStroke.class);

            KeyStroke var10000;
            try {
               var10000 = (KeyStroke)getAWTKeyStroke(var0);
            } catch (IllegalArgumentException var4) {
               return null;
            }

            return var10000;
         }
      } else {
         return null;
      }
   }
}
