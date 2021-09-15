package sun.font;

import java.nio.charset.Charset;
import java.util.HashMap;
import sun.awt.FontConfiguration;

class CFontConfiguration extends FontConfiguration {
   private static CompositeFontDescriptor[] emptyDescriptors = new CompositeFontDescriptor[0];
   private static String[] emptyStrings = new String[0];

   public CFontConfiguration(SunFontManager var1) {
      super(var1);
   }

   public CFontConfiguration(SunFontManager var1, boolean var2, boolean var3) {
      super(var1, var2, var3);
   }

   public int getNumberCoreFonts() {
      return 0;
   }

   public String[] getPlatformFontNames() {
      return emptyStrings;
   }

   public CompositeFontDescriptor[] get2DCompositeFontInfo() {
      return emptyDescriptors;
   }

   protected String mapFileName(String var1) {
      return "";
   }

   protected Charset getDefaultFontCharset(String var1) {
      return Charset.forName("ISO8859_1");
   }

   protected String getEncoding(String var1, String var2) {
      return "default";
   }

   protected String getFaceNameFromComponentFontName(String var1) {
      return var1;
   }

   protected String getFileNameFromComponentFontName(String var1) {
      return var1;
   }

   public String getFallbackFamilyName(String var1, String var2) {
      return var2;
   }

   protected void initReorderMap() {
      this.reorderMap = new HashMap();
   }
}
