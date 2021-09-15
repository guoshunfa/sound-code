package java.awt.event;

import java.awt.Component;

public class MouseWheelEvent extends MouseEvent {
   public static final int WHEEL_UNIT_SCROLL = 0;
   public static final int WHEEL_BLOCK_SCROLL = 1;
   int scrollType;
   int scrollAmount;
   int wheelRotation;
   double preciseWheelRotation;
   private static final long serialVersionUID = 6459879390515399677L;

   public MouseWheelEvent(Component var1, int var2, long var3, int var5, int var6, int var7, int var8, boolean var9, int var10, int var11, int var12) {
      this(var1, var2, var3, var5, var6, var7, 0, 0, var8, var9, var10, var11, var12);
   }

   public MouseWheelEvent(Component var1, int var2, long var3, int var5, int var6, int var7, int var8, int var9, int var10, boolean var11, int var12, int var13, int var14) {
      this(var1, var2, var3, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, (double)var14);
   }

   public MouseWheelEvent(Component var1, int var2, long var3, int var5, int var6, int var7, int var8, int var9, int var10, boolean var11, int var12, int var13, int var14, double var15) {
      super(var1, var2, var3, var5, var6, var7, var8, var9, var10, var11, 0);
      this.scrollType = var12;
      this.scrollAmount = var13;
      this.wheelRotation = var14;
      this.preciseWheelRotation = var15;
   }

   public int getScrollType() {
      return this.scrollType;
   }

   public int getScrollAmount() {
      return this.scrollAmount;
   }

   public int getWheelRotation() {
      return this.wheelRotation;
   }

   public double getPreciseWheelRotation() {
      return this.preciseWheelRotation;
   }

   public int getUnitsToScroll() {
      return this.scrollAmount * this.wheelRotation;
   }

   public String paramString() {
      String var1 = null;
      if (this.getScrollType() == 0) {
         var1 = "WHEEL_UNIT_SCROLL";
      } else if (this.getScrollType() == 1) {
         var1 = "WHEEL_BLOCK_SCROLL";
      } else {
         var1 = "unknown scroll type";
      }

      return super.paramString() + ",scrollType=" + var1 + ",scrollAmount=" + this.getScrollAmount() + ",wheelRotation=" + this.getWheelRotation() + ",preciseWheelRotation=" + this.getPreciseWheelRotation();
   }
}
