package java.awt.color;

import sun.java2d.cmm.Profile;
import sun.java2d.cmm.ProfileDeferralInfo;

public class ICC_ProfileRGB extends ICC_Profile {
   static final long serialVersionUID = 8505067385152579334L;
   public static final int REDCOMPONENT = 0;
   public static final int GREENCOMPONENT = 1;
   public static final int BLUECOMPONENT = 2;

   ICC_ProfileRGB(Profile var1) {
      super(var1);
   }

   ICC_ProfileRGB(ProfileDeferralInfo var1) {
      super(var1);
   }

   public float[] getMediaWhitePoint() {
      return super.getMediaWhitePoint();
   }

   public float[][] getMatrix() {
      float[][] var1 = new float[3][3];
      float[] var2 = this.getXYZTag(1918392666);
      var1[0][0] = var2[0];
      var1[1][0] = var2[1];
      var1[2][0] = var2[2];
      var2 = this.getXYZTag(1733843290);
      var1[0][1] = var2[0];
      var1[1][1] = var2[1];
      var1[2][1] = var2[2];
      var2 = this.getXYZTag(1649957210);
      var1[0][2] = var2[0];
      var1[1][2] = var2[1];
      var1[2][2] = var2[2];
      return var1;
   }

   public float getGamma(int var1) {
      int var3;
      switch(var1) {
      case 0:
         var3 = 1918128707;
         break;
      case 1:
         var3 = 1733579331;
         break;
      case 2:
         var3 = 1649693251;
         break;
      default:
         throw new IllegalArgumentException("Must be Red, Green, or Blue");
      }

      float var2 = super.getGamma(var3);
      return var2;
   }

   public short[] getTRC(int var1) {
      int var3;
      switch(var1) {
      case 0:
         var3 = 1918128707;
         break;
      case 1:
         var3 = 1733579331;
         break;
      case 2:
         var3 = 1649693251;
         break;
      default:
         throw new IllegalArgumentException("Must be Red, Green, or Blue");
      }

      short[] var2 = super.getTRC(var3);
      return var2;
   }
}
