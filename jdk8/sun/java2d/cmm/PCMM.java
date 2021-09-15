package sun.java2d.cmm;

import java.awt.color.ICC_Profile;

public interface PCMM {
   Profile loadProfile(byte[] var1);

   void freeProfile(Profile var1);

   int getProfileSize(Profile var1);

   void getProfileData(Profile var1, byte[] var2);

   void getTagData(Profile var1, int var2, byte[] var3);

   int getTagSize(Profile var1, int var2);

   void setTagData(Profile var1, int var2, byte[] var3);

   ColorTransform createTransform(ICC_Profile var1, int var2, int var3);

   ColorTransform createTransform(ColorTransform[] var1);
}
