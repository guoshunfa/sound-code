package java.awt.event;

import java.awt.AWTEvent;
import java.awt.Adjustable;

public class AdjustmentEvent extends AWTEvent {
   public static final int ADJUSTMENT_FIRST = 601;
   public static final int ADJUSTMENT_LAST = 601;
   public static final int ADJUSTMENT_VALUE_CHANGED = 601;
   public static final int UNIT_INCREMENT = 1;
   public static final int UNIT_DECREMENT = 2;
   public static final int BLOCK_DECREMENT = 3;
   public static final int BLOCK_INCREMENT = 4;
   public static final int TRACK = 5;
   Adjustable adjustable;
   int value;
   int adjustmentType;
   boolean isAdjusting;
   private static final long serialVersionUID = 5700290645205279921L;

   public AdjustmentEvent(Adjustable var1, int var2, int var3, int var4) {
      this(var1, var2, var3, var4, false);
   }

   public AdjustmentEvent(Adjustable var1, int var2, int var3, int var4, boolean var5) {
      super(var1, var2);
      this.adjustable = var1;
      this.adjustmentType = var3;
      this.value = var4;
      this.isAdjusting = var5;
   }

   public Adjustable getAdjustable() {
      return this.adjustable;
   }

   public int getValue() {
      return this.value;
   }

   public int getAdjustmentType() {
      return this.adjustmentType;
   }

   public boolean getValueIsAdjusting() {
      return this.isAdjusting;
   }

   public String paramString() {
      String var1;
      switch(this.id) {
      case 601:
         var1 = "ADJUSTMENT_VALUE_CHANGED";
         break;
      default:
         var1 = "unknown type";
      }

      String var2;
      switch(this.adjustmentType) {
      case 1:
         var2 = "UNIT_INCREMENT";
         break;
      case 2:
         var2 = "UNIT_DECREMENT";
         break;
      case 3:
         var2 = "BLOCK_DECREMENT";
         break;
      case 4:
         var2 = "BLOCK_INCREMENT";
         break;
      case 5:
         var2 = "TRACK";
         break;
      default:
         var2 = "unknown type";
      }

      return var1 + ",adjType=" + var2 + ",value=" + this.value + ",isAdjusting=" + this.isAdjusting;
   }
}
