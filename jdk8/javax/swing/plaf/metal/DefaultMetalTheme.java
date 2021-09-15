package javax.swing.plaf.metal;

import java.awt.Font;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import sun.awt.AppContext;
import sun.security.action.GetPropertyAction;
import sun.swing.SwingUtilities2;

public class DefaultMetalTheme extends MetalTheme {
   private static final boolean PLAIN_FONTS;
   private static final String[] fontNames = new String[]{"Dialog", "Dialog", "Dialog", "Dialog", "Dialog", "Dialog"};
   private static final int[] fontStyles = new int[]{1, 0, 0, 1, 1, 0};
   private static final int[] fontSizes = new int[]{12, 12, 12, 12, 12, 10};
   private static final String[] defaultNames = new String[]{"swing.plaf.metal.controlFont", "swing.plaf.metal.systemFont", "swing.plaf.metal.userFont", "swing.plaf.metal.controlFont", "swing.plaf.metal.controlFont", "swing.plaf.metal.smallFont"};
   private static final ColorUIResource primary1;
   private static final ColorUIResource primary2;
   private static final ColorUIResource primary3;
   private static final ColorUIResource secondary1;
   private static final ColorUIResource secondary2;
   private static final ColorUIResource secondary3;
   private DefaultMetalTheme.FontDelegate fontDelegate;

   static String getDefaultFontName(int var0) {
      return fontNames[var0];
   }

   static int getDefaultFontSize(int var0) {
      return fontSizes[var0];
   }

   static int getDefaultFontStyle(int var0) {
      if (var0 != 4) {
         Object var1 = null;
         if (AppContext.getAppContext().get(SwingUtilities2.LAF_STATE_KEY) != null) {
            var1 = UIManager.get("swing.boldMetal");
         }

         if (var1 != null) {
            if (Boolean.FALSE.equals(var1)) {
               return 0;
            }
         } else if (PLAIN_FONTS) {
            return 0;
         }
      }

      return fontStyles[var0];
   }

   static String getDefaultPropertyName(int var0) {
      return defaultNames[var0];
   }

   public String getName() {
      return "Steel";
   }

   public DefaultMetalTheme() {
      this.install();
   }

   protected ColorUIResource getPrimary1() {
      return primary1;
   }

   protected ColorUIResource getPrimary2() {
      return primary2;
   }

   protected ColorUIResource getPrimary3() {
      return primary3;
   }

   protected ColorUIResource getSecondary1() {
      return secondary1;
   }

   protected ColorUIResource getSecondary2() {
      return secondary2;
   }

   protected ColorUIResource getSecondary3() {
      return secondary3;
   }

   public FontUIResource getControlTextFont() {
      return this.getFont(0);
   }

   public FontUIResource getSystemTextFont() {
      return this.getFont(1);
   }

   public FontUIResource getUserTextFont() {
      return this.getFont(2);
   }

   public FontUIResource getMenuTextFont() {
      return this.getFont(3);
   }

   public FontUIResource getWindowTitleFont() {
      return this.getFont(4);
   }

   public FontUIResource getSubTextFont() {
      return this.getFont(5);
   }

   private FontUIResource getFont(int var1) {
      return this.fontDelegate.getFont(var1);
   }

   void install() {
      if (MetalLookAndFeel.isWindows() && MetalLookAndFeel.useSystemFonts()) {
         this.fontDelegate = new DefaultMetalTheme.WindowsFontDelegate();
      } else {
         this.fontDelegate = new DefaultMetalTheme.FontDelegate();
      }

   }

   boolean isSystemTheme() {
      return this.getClass() == DefaultMetalTheme.class;
   }

   static {
      Object var0 = AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("swing.boldMetal")));
      if (var0 != null && "false".equals(var0)) {
         PLAIN_FONTS = true;
      } else {
         PLAIN_FONTS = false;
      }

      primary1 = new ColorUIResource(102, 102, 153);
      primary2 = new ColorUIResource(153, 153, 204);
      primary3 = new ColorUIResource(204, 204, 255);
      secondary1 = new ColorUIResource(102, 102, 102);
      secondary2 = new ColorUIResource(153, 153, 153);
      secondary3 = new ColorUIResource(204, 204, 204);
   }

   private static class WindowsFontDelegate extends DefaultMetalTheme.FontDelegate {
      private MetalFontDesktopProperty[] props = new MetalFontDesktopProperty[6];
      private boolean[] checkedPriviledged = new boolean[6];

      public WindowsFontDelegate() {
      }

      public FontUIResource getFont(int var1) {
         if (this.fonts[var1] != null) {
            return this.fonts[var1];
         } else {
            if (!this.checkedPriviledged[var1]) {
               Font var2 = this.getPrivilegedFont(var1);
               this.checkedPriviledged[var1] = true;
               if (var2 != null) {
                  this.fonts[var1] = new FontUIResource(var2);
                  return this.fonts[var1];
               }
            }

            if (this.props[var1] == null) {
               this.props[var1] = new MetalFontDesktopProperty(var1);
            }

            return (FontUIResource)this.props[var1].createValue((UIDefaults)null);
         }
      }
   }

   private static class FontDelegate {
      private static int[] defaultMapping = new int[]{0, 1, 2, 0, 0, 5};
      FontUIResource[] fonts = new FontUIResource[6];

      public FontDelegate() {
      }

      public FontUIResource getFont(int var1) {
         int var2 = defaultMapping[var1];
         if (this.fonts[var1] == null) {
            Font var3 = this.getPrivilegedFont(var2);
            if (var3 == null) {
               var3 = new Font(DefaultMetalTheme.getDefaultFontName(var1), DefaultMetalTheme.getDefaultFontStyle(var1), DefaultMetalTheme.getDefaultFontSize(var1));
            }

            this.fonts[var1] = new FontUIResource(var3);
         }

         return this.fonts[var1];
      }

      protected Font getPrivilegedFont(final int var1) {
         return (Font)AccessController.doPrivileged(new PrivilegedAction<Font>() {
            public Font run() {
               return Font.getFont(DefaultMetalTheme.getDefaultPropertyName(var1));
            }
         });
      }
   }
}
