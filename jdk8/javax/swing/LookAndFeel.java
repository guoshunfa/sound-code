package javax.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InputMapUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.text.JTextComponent;
import sun.awt.SunToolkit;
import sun.swing.DefaultLayoutStyle;
import sun.swing.ImageIconUIResource;
import sun.swing.SwingUtilities2;

public abstract class LookAndFeel {
   public static void installColors(JComponent var0, String var1, String var2) {
      Color var3 = var0.getBackground();
      if (var3 == null || var3 instanceof UIResource) {
         var0.setBackground(UIManager.getColor(var1));
      }

      Color var4 = var0.getForeground();
      if (var4 == null || var4 instanceof UIResource) {
         var0.setForeground(UIManager.getColor(var2));
      }

   }

   public static void installColorsAndFont(JComponent var0, String var1, String var2, String var3) {
      Font var4 = var0.getFont();
      if (var4 == null || var4 instanceof UIResource) {
         var0.setFont(UIManager.getFont(var3));
      }

      installColors(var0, var1, var2);
   }

   public static void installBorder(JComponent var0, String var1) {
      Border var2 = var0.getBorder();
      if (var2 == null || var2 instanceof UIResource) {
         var0.setBorder(UIManager.getBorder(var1));
      }

   }

   public static void uninstallBorder(JComponent var0) {
      if (var0.getBorder() instanceof UIResource) {
         var0.setBorder((Border)null);
      }

   }

   public static void installProperty(JComponent var0, String var1, Object var2) {
      if (SunToolkit.isInstanceOf((Object)var0, "javax.swing.JPasswordField")) {
         if (!((JPasswordField)var0).customSetUIProperty(var1, var2)) {
            var0.setUIProperty(var1, var2);
         }
      } else {
         var0.setUIProperty(var1, var2);
      }

   }

   public static JTextComponent.KeyBinding[] makeKeyBindings(Object[] var0) {
      JTextComponent.KeyBinding[] var1 = new JTextComponent.KeyBinding[var0.length / 2];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         Object var3 = var0[2 * var2];
         KeyStroke var4 = var3 instanceof KeyStroke ? (KeyStroke)var3 : KeyStroke.getKeyStroke((String)var3);
         String var5 = (String)var0[2 * var2 + 1];
         var1[var2] = new JTextComponent.KeyBinding(var4, var5);
      }

      return var1;
   }

   public static InputMap makeInputMap(Object[] var0) {
      InputMapUIResource var1 = new InputMapUIResource();
      loadKeyBindings(var1, var0);
      return var1;
   }

   public static ComponentInputMap makeComponentInputMap(JComponent var0, Object[] var1) {
      ComponentInputMapUIResource var2 = new ComponentInputMapUIResource(var0);
      loadKeyBindings(var2, var1);
      return var2;
   }

   public static void loadKeyBindings(InputMap var0, Object[] var1) {
      if (var1 != null) {
         int var2 = 0;

         for(int var3 = var1.length; var2 < var3; ++var2) {
            Object var4 = var1[var2++];
            KeyStroke var5 = var4 instanceof KeyStroke ? (KeyStroke)var4 : KeyStroke.getKeyStroke((String)var4);
            var0.put(var5, var1[var2]);
         }
      }

   }

   public static Object makeIcon(Class<?> var0, String var1) {
      return SwingUtilities2.makeIcon(var0, var0, var1);
   }

   public LayoutStyle getLayoutStyle() {
      return DefaultLayoutStyle.getInstance();
   }

   public void provideErrorFeedback(Component var1) {
      Toolkit var2 = null;
      if (var1 != null) {
         var2 = var1.getToolkit();
      } else {
         var2 = Toolkit.getDefaultToolkit();
      }

      var2.beep();
   }

   public static Object getDesktopPropertyValue(String var0, Object var1) {
      Object var2 = Toolkit.getDefaultToolkit().getDesktopProperty(var0);
      if (var2 == null) {
         return var1;
      } else if (var2 instanceof Color) {
         return new ColorUIResource((Color)var2);
      } else {
         return var2 instanceof Font ? new FontUIResource((Font)var2) : var2;
      }
   }

   public Icon getDisabledIcon(JComponent var1, Icon var2) {
      return var2 instanceof ImageIcon ? new ImageIconUIResource(GrayFilter.createDisabledImage(((ImageIcon)var2).getImage())) : null;
   }

   public Icon getDisabledSelectedIcon(JComponent var1, Icon var2) {
      return this.getDisabledIcon(var1, var2);
   }

   public abstract String getName();

   public abstract String getID();

   public abstract String getDescription();

   public boolean getSupportsWindowDecorations() {
      return false;
   }

   public abstract boolean isNativeLookAndFeel();

   public abstract boolean isSupportedLookAndFeel();

   public void initialize() {
   }

   public void uninitialize() {
   }

   public UIDefaults getDefaults() {
      return null;
   }

   public String toString() {
      return "[" + this.getDescription() + " - " + this.getClass().getName() + "]";
   }
}
