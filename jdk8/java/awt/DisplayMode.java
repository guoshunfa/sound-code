package java.awt;

public final class DisplayMode {
   private Dimension size;
   private int bitDepth;
   private int refreshRate;
   public static final int BIT_DEPTH_MULTI = -1;
   public static final int REFRESH_RATE_UNKNOWN = 0;

   public DisplayMode(int var1, int var2, int var3, int var4) {
      this.size = new Dimension(var1, var2);
      this.bitDepth = var3;
      this.refreshRate = var4;
   }

   public int getHeight() {
      return this.size.height;
   }

   public int getWidth() {
      return this.size.width;
   }

   public int getBitDepth() {
      return this.bitDepth;
   }

   public int getRefreshRate() {
      return this.refreshRate;
   }

   public boolean equals(DisplayMode var1) {
      if (var1 == null) {
         return false;
      } else {
         return this.getHeight() == var1.getHeight() && this.getWidth() == var1.getWidth() && this.getBitDepth() == var1.getBitDepth() && this.getRefreshRate() == var1.getRefreshRate();
      }
   }

   public boolean equals(Object var1) {
      return var1 instanceof DisplayMode ? this.equals((DisplayMode)var1) : false;
   }

   public int hashCode() {
      return this.getWidth() + this.getHeight() + this.getBitDepth() * 7 + this.getRefreshRate() * 13;
   }
}
