package sun.java2d.pipe.hw;

public class ContextCapabilities {
   public static final int CAPS_EMPTY = 0;
   public static final int CAPS_RT_PLAIN_ALPHA = 2;
   public static final int CAPS_RT_TEXTURE_ALPHA = 4;
   public static final int CAPS_RT_TEXTURE_OPAQUE = 8;
   public static final int CAPS_MULTITEXTURE = 16;
   public static final int CAPS_TEXNONPOW2 = 32;
   public static final int CAPS_TEXNONSQUARE = 64;
   public static final int CAPS_PS20 = 128;
   public static final int CAPS_PS30 = 256;
   protected static final int FIRST_PRIVATE_CAP = 65536;
   protected final int caps;
   protected final String adapterId;

   protected ContextCapabilities(int var1, String var2) {
      this.caps = var1;
      this.adapterId = var2 != null ? var2 : "unknown adapter";
   }

   public String getAdapterId() {
      return this.adapterId;
   }

   public int getCaps() {
      return this.caps;
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer("ContextCapabilities: adapter=" + this.adapterId + ", caps=");
      if (this.caps == 0) {
         var1.append("CAPS_EMPTY");
      } else {
         if ((this.caps & 2) != 0) {
            var1.append("CAPS_RT_PLAIN_ALPHA|");
         }

         if ((this.caps & 4) != 0) {
            var1.append("CAPS_RT_TEXTURE_ALPHA|");
         }

         if ((this.caps & 8) != 0) {
            var1.append("CAPS_RT_TEXTURE_OPAQUE|");
         }

         if ((this.caps & 16) != 0) {
            var1.append("CAPS_MULTITEXTURE|");
         }

         if ((this.caps & 32) != 0) {
            var1.append("CAPS_TEXNONPOW2|");
         }

         if ((this.caps & 64) != 0) {
            var1.append("CAPS_TEXNONSQUARE|");
         }

         if ((this.caps & 128) != 0) {
            var1.append("CAPS_PS20|");
         }

         if ((this.caps & 256) != 0) {
            var1.append("CAPS_PS30|");
         }
      }

      return var1.toString();
   }
}
