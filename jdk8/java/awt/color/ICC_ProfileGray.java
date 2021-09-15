package java.awt.color;

import sun.java2d.cmm.Profile;
import sun.java2d.cmm.ProfileDeferralInfo;

public class ICC_ProfileGray extends ICC_Profile {
   static final long serialVersionUID = -1124721290732002649L;

   ICC_ProfileGray(Profile var1) {
      super(var1);
   }

   ICC_ProfileGray(ProfileDeferralInfo var1) {
      super(var1);
   }

   public float[] getMediaWhitePoint() {
      return super.getMediaWhitePoint();
   }

   public float getGamma() {
      float var1 = super.getGamma(1800688195);
      return var1;
   }

   public short[] getTRC() {
      short[] var1 = super.getTRC(1800688195);
      return var1;
   }
}
