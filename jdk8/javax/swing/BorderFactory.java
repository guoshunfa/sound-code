package javax.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.StrokeBorder;
import javax.swing.border.TitledBorder;

public class BorderFactory {
   static final Border sharedRaisedBevel = new BevelBorder(0);
   static final Border sharedLoweredBevel = new BevelBorder(1);
   private static Border sharedSoftRaisedBevel;
   private static Border sharedSoftLoweredBevel;
   static final Border sharedEtchedBorder = new EtchedBorder();
   private static Border sharedRaisedEtchedBorder;
   static final Border emptyBorder = new EmptyBorder(0, 0, 0, 0);
   private static Border sharedDashedBorder;

   private BorderFactory() {
   }

   public static Border createLineBorder(Color var0) {
      return new LineBorder(var0, 1);
   }

   public static Border createLineBorder(Color var0, int var1) {
      return new LineBorder(var0, var1);
   }

   public static Border createLineBorder(Color var0, int var1, boolean var2) {
      return new LineBorder(var0, var1, var2);
   }

   public static Border createRaisedBevelBorder() {
      return createSharedBevel(0);
   }

   public static Border createLoweredBevelBorder() {
      return createSharedBevel(1);
   }

   public static Border createBevelBorder(int var0) {
      return createSharedBevel(var0);
   }

   public static Border createBevelBorder(int var0, Color var1, Color var2) {
      return new BevelBorder(var0, var1, var2);
   }

   public static Border createBevelBorder(int var0, Color var1, Color var2, Color var3, Color var4) {
      return new BevelBorder(var0, var1, var2, var3, var4);
   }

   static Border createSharedBevel(int var0) {
      if (var0 == 0) {
         return sharedRaisedBevel;
      } else {
         return var0 == 1 ? sharedLoweredBevel : null;
      }
   }

   public static Border createRaisedSoftBevelBorder() {
      if (sharedSoftRaisedBevel == null) {
         sharedSoftRaisedBevel = new SoftBevelBorder(0);
      }

      return sharedSoftRaisedBevel;
   }

   public static Border createLoweredSoftBevelBorder() {
      if (sharedSoftLoweredBevel == null) {
         sharedSoftLoweredBevel = new SoftBevelBorder(1);
      }

      return sharedSoftLoweredBevel;
   }

   public static Border createSoftBevelBorder(int var0) {
      if (var0 == 0) {
         return createRaisedSoftBevelBorder();
      } else {
         return var0 == 1 ? createLoweredSoftBevelBorder() : null;
      }
   }

   public static Border createSoftBevelBorder(int var0, Color var1, Color var2) {
      return new SoftBevelBorder(var0, var1, var2);
   }

   public static Border createSoftBevelBorder(int var0, Color var1, Color var2, Color var3, Color var4) {
      return new SoftBevelBorder(var0, var1, var2, var3, var4);
   }

   public static Border createEtchedBorder() {
      return sharedEtchedBorder;
   }

   public static Border createEtchedBorder(Color var0, Color var1) {
      return new EtchedBorder(var0, var1);
   }

   public static Border createEtchedBorder(int var0) {
      switch(var0) {
      case 0:
         if (sharedRaisedEtchedBorder == null) {
            sharedRaisedEtchedBorder = new EtchedBorder(0);
         }

         return sharedRaisedEtchedBorder;
      case 1:
         return sharedEtchedBorder;
      default:
         throw new IllegalArgumentException("type must be one of EtchedBorder.RAISED or EtchedBorder.LOWERED");
      }
   }

   public static Border createEtchedBorder(int var0, Color var1, Color var2) {
      return new EtchedBorder(var0, var1, var2);
   }

   public static TitledBorder createTitledBorder(String var0) {
      return new TitledBorder(var0);
   }

   public static TitledBorder createTitledBorder(Border var0) {
      return new TitledBorder(var0);
   }

   public static TitledBorder createTitledBorder(Border var0, String var1) {
      return new TitledBorder(var0, var1);
   }

   public static TitledBorder createTitledBorder(Border var0, String var1, int var2, int var3) {
      return new TitledBorder(var0, var1, var2, var3);
   }

   public static TitledBorder createTitledBorder(Border var0, String var1, int var2, int var3, Font var4) {
      return new TitledBorder(var0, var1, var2, var3, var4);
   }

   public static TitledBorder createTitledBorder(Border var0, String var1, int var2, int var3, Font var4, Color var5) {
      return new TitledBorder(var0, var1, var2, var3, var4, var5);
   }

   public static Border createEmptyBorder() {
      return emptyBorder;
   }

   public static Border createEmptyBorder(int var0, int var1, int var2, int var3) {
      return new EmptyBorder(var0, var1, var2, var3);
   }

   public static CompoundBorder createCompoundBorder() {
      return new CompoundBorder();
   }

   public static CompoundBorder createCompoundBorder(Border var0, Border var1) {
      return new CompoundBorder(var0, var1);
   }

   public static MatteBorder createMatteBorder(int var0, int var1, int var2, int var3, Color var4) {
      return new MatteBorder(var0, var1, var2, var3, var4);
   }

   public static MatteBorder createMatteBorder(int var0, int var1, int var2, int var3, Icon var4) {
      return new MatteBorder(var0, var1, var2, var3, var4);
   }

   public static Border createStrokeBorder(BasicStroke var0) {
      return new StrokeBorder(var0);
   }

   public static Border createStrokeBorder(BasicStroke var0, Paint var1) {
      return new StrokeBorder(var0, var1);
   }

   public static Border createDashedBorder(Paint var0) {
      return createDashedBorder(var0, 1.0F, 1.0F, 1.0F, false);
   }

   public static Border createDashedBorder(Paint var0, float var1, float var2) {
      return createDashedBorder(var0, 1.0F, var1, var2, false);
   }

   public static Border createDashedBorder(Paint var0, float var1, float var2, float var3, boolean var4) {
      boolean var5 = !var4 && var0 == null && var1 == 1.0F && var2 == 1.0F && var3 == 1.0F;
      if (var5 && sharedDashedBorder != null) {
         return sharedDashedBorder;
      } else if (var1 < 1.0F) {
         throw new IllegalArgumentException("thickness is less than 1");
      } else if (var2 < 1.0F) {
         throw new IllegalArgumentException("length is less than 1");
      } else if (var3 < 0.0F) {
         throw new IllegalArgumentException("spacing is less than 0");
      } else {
         int var6 = var4 ? 1 : 2;
         int var7 = var4 ? 1 : 0;
         float[] var8 = new float[]{var1 * (var2 - 1.0F), var1 * (var3 + 1.0F)};
         Border var9 = createStrokeBorder(new BasicStroke(var1, var6, var7, var1 * 2.0F, var8, 0.0F), var0);
         if (var5) {
            sharedDashedBorder = var9;
         }

         return var9;
      }
   }
}
