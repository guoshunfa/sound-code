package javax.swing.plaf.metal;

import com.sun.java.swing.plaf.windows.DesktopProperty;
import java.awt.Font;

class MetalFontDesktopProperty extends DesktopProperty {
   private static final String[] propertyMapping = new String[]{"win.ansiVar.font.height", "win.tooltip.font.height", "win.ansiVar.font.height", "win.menu.font.height", "win.frame.captionFont.height", "win.menu.font.height"};
   private int type;

   MetalFontDesktopProperty(int var1) {
      this(propertyMapping[var1], var1);
   }

   MetalFontDesktopProperty(String var1, int var2) {
      super(var1, (Object)null);
      this.type = var2;
   }

   protected Object configureValue(Object var1) {
      if (var1 instanceof Integer) {
         var1 = new Font(DefaultMetalTheme.getDefaultFontName(this.type), DefaultMetalTheme.getDefaultFontStyle(this.type), (Integer)var1);
      }

      return super.configureValue(var1);
   }

   protected Object getDefaultValue() {
      return new Font(DefaultMetalTheme.getDefaultFontName(this.type), DefaultMetalTheme.getDefaultFontStyle(this.type), DefaultMetalTheme.getDefaultFontSize(this.type));
   }
}
