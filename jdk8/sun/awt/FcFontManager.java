package sun.awt;

import sun.font.FcFontConfiguration;
import sun.font.FontConfigManager;
import sun.font.SunFontManager;

public class FcFontManager extends SunFontManager {
   private FontConfigManager fcManager = null;

   public synchronized FontConfigManager getFontConfigManager() {
      if (this.fcManager == null) {
         this.fcManager = new FontConfigManager();
      }

      return this.fcManager;
   }

   protected FontConfiguration createFontConfiguration() {
      FcFontConfiguration var1 = new FcFontConfiguration(this);
      if (var1.init()) {
         return var1;
      } else {
         throw new InternalError("failed to initialize fontconfig");
      }
   }

   public FontConfiguration createFontConfiguration(boolean var1, boolean var2) {
      FcFontConfiguration var3 = new FcFontConfiguration(this, var1, var2);
      if (var3.init()) {
         return var3;
      } else {
         throw new InternalError("failed to initialize fontconfig");
      }
   }

   protected String[] getDefaultPlatformFont() {
      String[] var1 = new String[2];
      this.getFontConfigManager().initFontConfigFonts(false);
      FontConfigManager.FcCompFont[] var2 = this.getFontConfigManager().getFontConfigFonts();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if ("sans".equals(var2[var3].fcFamily) && 0 == var2[var3].style) {
            var1[0] = var2[var3].firstFont.familyName;
            var1[1] = var2[var3].firstFont.fontFile;
            break;
         }
      }

      if (var1[0] == null) {
         if (var2.length > 0 && var2[0].firstFont.fontFile != null) {
            var1[0] = var2[0].firstFont.familyName;
            var1[1] = var2[0].firstFont.fontFile;
         } else {
            var1[0] = "Dialog";
            var1[1] = "/dialog.ttf";
         }
      }

      return var1;
   }

   protected native String getFontPathNative(boolean var1, boolean var2);

   protected synchronized String getFontPath(boolean var1) {
      return this.getFontPathNative(var1, false);
   }
}
