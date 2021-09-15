package sun.awt;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.nio.cs.HistoricallyNamedCharset;
import sun.security.action.GetPropertyAction;

public class FontDescriptor implements Cloneable {
   String nativeName;
   public CharsetEncoder encoder;
   String charsetName;
   private int[] exclusionRanges;
   public CharsetEncoder unicodeEncoder;
   boolean useUnicode;
   static boolean isLE;

   public FontDescriptor(String var1, CharsetEncoder var2, int[] var3) {
      this.nativeName = var1;
      this.encoder = var2;
      this.exclusionRanges = var3;
      this.useUnicode = false;
      Charset var4 = var2.charset();
      if (var4 instanceof HistoricallyNamedCharset) {
         this.charsetName = ((HistoricallyNamedCharset)var4).historicalName();
      } else {
         this.charsetName = var4.name();
      }

   }

   public String getNativeName() {
      return this.nativeName;
   }

   public CharsetEncoder getFontCharsetEncoder() {
      return this.encoder;
   }

   public String getFontCharsetName() {
      return this.charsetName;
   }

   public int[] getExclusionRanges() {
      return this.exclusionRanges;
   }

   public boolean isExcluded(char var1) {
      int var2 = 0;

      int var3;
      int var4;
      do {
         if (var2 >= this.exclusionRanges.length) {
            return false;
         }

         var3 = this.exclusionRanges[var2++];
         var4 = this.exclusionRanges[var2++];
      } while(var1 < var3 || var1 > var4);

      return true;
   }

   public String toString() {
      return super.toString() + " [" + this.nativeName + "|" + this.encoder + "]";
   }

   private static native void initIDs();

   public boolean useUnicode() {
      if (this.useUnicode && this.unicodeEncoder == null) {
         try {
            this.unicodeEncoder = isLE ? StandardCharsets.UTF_16LE.newEncoder() : StandardCharsets.UTF_16BE.newEncoder();
         } catch (IllegalArgumentException var2) {
         }
      }

      return this.useUnicode;
   }

   static {
      NativeLibLoader.loadLibraries();
      initIDs();
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.io.unicode.encoding", "UnicodeBig")));
      isLE = !"UnicodeBig".equals(var0);
   }
}
