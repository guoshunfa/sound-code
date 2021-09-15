package java.awt.color;

import java.io.Serializable;
import sun.java2d.cmm.CMSManager;

public abstract class ColorSpace implements Serializable {
   static final long serialVersionUID = -409452704308689724L;
   private int type;
   private int numComponents;
   private transient String[] compName = null;
   private static ColorSpace sRGBspace;
   private static ColorSpace XYZspace;
   private static ColorSpace PYCCspace;
   private static ColorSpace GRAYspace;
   private static ColorSpace LINEAR_RGBspace;
   public static final int TYPE_XYZ = 0;
   public static final int TYPE_Lab = 1;
   public static final int TYPE_Luv = 2;
   public static final int TYPE_YCbCr = 3;
   public static final int TYPE_Yxy = 4;
   public static final int TYPE_RGB = 5;
   public static final int TYPE_GRAY = 6;
   public static final int TYPE_HSV = 7;
   public static final int TYPE_HLS = 8;
   public static final int TYPE_CMYK = 9;
   public static final int TYPE_CMY = 11;
   public static final int TYPE_2CLR = 12;
   public static final int TYPE_3CLR = 13;
   public static final int TYPE_4CLR = 14;
   public static final int TYPE_5CLR = 15;
   public static final int TYPE_6CLR = 16;
   public static final int TYPE_7CLR = 17;
   public static final int TYPE_8CLR = 18;
   public static final int TYPE_9CLR = 19;
   public static final int TYPE_ACLR = 20;
   public static final int TYPE_BCLR = 21;
   public static final int TYPE_CCLR = 22;
   public static final int TYPE_DCLR = 23;
   public static final int TYPE_ECLR = 24;
   public static final int TYPE_FCLR = 25;
   public static final int CS_sRGB = 1000;
   public static final int CS_LINEAR_RGB = 1004;
   public static final int CS_CIEXYZ = 1001;
   public static final int CS_PYCC = 1002;
   public static final int CS_GRAY = 1003;

   protected ColorSpace(int var1, int var2) {
      this.type = var1;
      this.numComponents = var2;
   }

   public static ColorSpace getInstance(int var0) {
      ColorSpace var1;
      Class var2;
      ICC_Profile var3;
      switch(var0) {
      case 1000:
         var2 = ColorSpace.class;
         synchronized(ColorSpace.class) {
            if (sRGBspace == null) {
               var3 = ICC_Profile.getInstance(1000);
               sRGBspace = new ICC_ColorSpace(var3);
            }

            var1 = sRGBspace;
            break;
         }
      case 1001:
         var2 = ColorSpace.class;
         synchronized(ColorSpace.class) {
            if (XYZspace == null) {
               var3 = ICC_Profile.getInstance(1001);
               XYZspace = new ICC_ColorSpace(var3);
            }

            var1 = XYZspace;
            break;
         }
      case 1002:
         var2 = ColorSpace.class;
         synchronized(ColorSpace.class) {
            if (PYCCspace == null) {
               var3 = ICC_Profile.getInstance(1002);
               PYCCspace = new ICC_ColorSpace(var3);
            }

            var1 = PYCCspace;
            break;
         }
      case 1003:
         var2 = ColorSpace.class;
         synchronized(ColorSpace.class) {
            if (GRAYspace == null) {
               var3 = ICC_Profile.getInstance(1003);
               GRAYspace = new ICC_ColorSpace(var3);
               CMSManager.GRAYspace = GRAYspace;
            }

            var1 = GRAYspace;
            break;
         }
      case 1004:
         var2 = ColorSpace.class;
         synchronized(ColorSpace.class) {
            if (LINEAR_RGBspace == null) {
               var3 = ICC_Profile.getInstance(1004);
               LINEAR_RGBspace = new ICC_ColorSpace(var3);
               CMSManager.LINEAR_RGBspace = LINEAR_RGBspace;
            }

            var1 = LINEAR_RGBspace;
            break;
         }
      default:
         throw new IllegalArgumentException("Unknown color space");
      }

      return var1;
   }

   public boolean isCS_sRGB() {
      return this == sRGBspace;
   }

   public abstract float[] toRGB(float[] var1);

   public abstract float[] fromRGB(float[] var1);

   public abstract float[] toCIEXYZ(float[] var1);

   public abstract float[] fromCIEXYZ(float[] var1);

   public int getType() {
      return this.type;
   }

   public int getNumComponents() {
      return this.numComponents;
   }

   public String getName(int var1) {
      if (var1 >= 0 && var1 <= this.numComponents - 1) {
         if (this.compName == null) {
            switch(this.type) {
            case 0:
               this.compName = new String[]{"X", "Y", "Z"};
               break;
            case 1:
               this.compName = new String[]{"L", "a", "b"};
               break;
            case 2:
               this.compName = new String[]{"L", "u", "v"};
               break;
            case 3:
               this.compName = new String[]{"Y", "Cb", "Cr"};
               break;
            case 4:
               this.compName = new String[]{"Y", "x", "y"};
               break;
            case 5:
               this.compName = new String[]{"Red", "Green", "Blue"};
               break;
            case 6:
               this.compName = new String[]{"Gray"};
               break;
            case 7:
               this.compName = new String[]{"Hue", "Saturation", "Value"};
               break;
            case 8:
               this.compName = new String[]{"Hue", "Lightness", "Saturation"};
               break;
            case 9:
               this.compName = new String[]{"Cyan", "Magenta", "Yellow", "Black"};
               break;
            case 10:
            default:
               String[] var2 = new String[this.numComponents];

               for(int var3 = 0; var3 < var2.length; ++var3) {
                  var2[var3] = "Unnamed color component(" + var3 + ")";
               }

               this.compName = var2;
               break;
            case 11:
               this.compName = new String[]{"Cyan", "Magenta", "Yellow"};
            }
         }

         return this.compName[var1];
      } else {
         throw new IllegalArgumentException("Component index out of range: " + var1);
      }
   }

   public float getMinValue(int var1) {
      if (var1 >= 0 && var1 <= this.numComponents - 1) {
         return 0.0F;
      } else {
         throw new IllegalArgumentException("Component index out of range: " + var1);
      }
   }

   public float getMaxValue(int var1) {
      if (var1 >= 0 && var1 <= this.numComponents - 1) {
         return 1.0F;
      } else {
         throw new IllegalArgumentException("Component index out of range: " + var1);
      }
   }

   static boolean isCS_CIEXYZ(ColorSpace var0) {
      return var0 == XYZspace;
   }
}
