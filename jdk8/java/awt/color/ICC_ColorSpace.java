package java.awt.color;

import java.io.IOException;
import java.io.ObjectInputStream;
import sun.java2d.cmm.CMSManager;
import sun.java2d.cmm.ColorTransform;
import sun.java2d.cmm.PCMM;

public class ICC_ColorSpace extends ColorSpace {
   static final long serialVersionUID = 3455889114070431483L;
   private ICC_Profile thisProfile;
   private float[] minVal;
   private float[] maxVal;
   private float[] diffMinMax;
   private float[] invDiffMinMax;
   private boolean needScaleInit = true;
   private transient ColorTransform this2srgb;
   private transient ColorTransform srgb2this;
   private transient ColorTransform this2xyz;
   private transient ColorTransform xyz2this;

   public ICC_ColorSpace(ICC_Profile var1) {
      super(var1.getColorSpaceType(), var1.getNumComponents());
      int var2 = var1.getProfileClass();
      if (var2 != 0 && var2 != 1 && var2 != 2 && var2 != 4 && var2 != 6 && var2 != 5) {
         throw new IllegalArgumentException("Invalid profile type");
      } else {
         this.thisProfile = var1;
         this.setMinMax();
      }
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      var1.defaultReadObject();
      if (this.thisProfile == null) {
         this.thisProfile = ICC_Profile.getInstance(1000);
      }

   }

   public ICC_Profile getProfile() {
      return this.thisProfile;
   }

   public float[] toRGB(float[] var1) {
      if (this.this2srgb == null) {
         ColorTransform[] var2 = new ColorTransform[2];
         ICC_ColorSpace var3 = (ICC_ColorSpace)ColorSpace.getInstance(1000);
         PCMM var4 = CMSManager.getModule();
         var2[0] = var4.createTransform(this.thisProfile, -1, 1);
         var2[1] = var4.createTransform(var3.getProfile(), -1, 2);
         this.this2srgb = var4.createTransform(var2);
         if (this.needScaleInit) {
            this.setComponentScaling();
         }
      }

      int var6 = this.getNumComponents();
      short[] var7 = new short[var6];

      for(int var8 = 0; var8 < var6; ++var8) {
         var7[var8] = (short)((int)((var1[var8] - this.minVal[var8]) * this.invDiffMinMax[var8] + 0.5F));
      }

      var7 = this.this2srgb.colorConvert((short[])var7, (short[])null);
      float[] var9 = new float[3];

      for(int var5 = 0; var5 < 3; ++var5) {
         var9[var5] = (float)(var7[var5] & '\uffff') / 65535.0F;
      }

      return var9;
   }

   public float[] fromRGB(float[] var1) {
      if (this.srgb2this == null) {
         ColorTransform[] var2 = new ColorTransform[2];
         ICC_ColorSpace var3 = (ICC_ColorSpace)ColorSpace.getInstance(1000);
         PCMM var4 = CMSManager.getModule();
         var2[0] = var4.createTransform(var3.getProfile(), -1, 1);
         var2[1] = var4.createTransform(this.thisProfile, -1, 2);
         this.srgb2this = var4.createTransform(var2);
         if (this.needScaleInit) {
            this.setComponentScaling();
         }
      }

      short[] var6 = new short[3];

      int var7;
      for(var7 = 0; var7 < 3; ++var7) {
         var6[var7] = (short)((int)(var1[var7] * 65535.0F + 0.5F));
      }

      var6 = this.srgb2this.colorConvert((short[])var6, (short[])null);
      var7 = this.getNumComponents();
      float[] var8 = new float[var7];

      for(int var5 = 0; var5 < var7; ++var5) {
         var8[var5] = (float)(var6[var5] & '\uffff') / 65535.0F * this.diffMinMax[var5] + this.minVal[var5];
      }

      return var8;
   }

   public float[] toCIEXYZ(float[] var1) {
      if (this.this2xyz == null) {
         ColorTransform[] var2 = new ColorTransform[2];
         ICC_ColorSpace var3 = (ICC_ColorSpace)ColorSpace.getInstance(1001);
         PCMM var4 = CMSManager.getModule();

         try {
            var2[0] = var4.createTransform(this.thisProfile, 1, 1);
         } catch (CMMException var7) {
            var2[0] = var4.createTransform(this.thisProfile, -1, 1);
         }

         var2[1] = var4.createTransform(var3.getProfile(), -1, 2);
         this.this2xyz = var4.createTransform(var2);
         if (this.needScaleInit) {
            this.setComponentScaling();
         }
      }

      int var8 = this.getNumComponents();
      short[] var9 = new short[var8];

      for(int var10 = 0; var10 < var8; ++var10) {
         var9[var10] = (short)((int)((var1[var10] - this.minVal[var10]) * this.invDiffMinMax[var10] + 0.5F));
      }

      var9 = this.this2xyz.colorConvert((short[])var9, (short[])null);
      float var11 = 1.9999695F;
      float[] var5 = new float[3];

      for(int var6 = 0; var6 < 3; ++var6) {
         var5[var6] = (float)(var9[var6] & '\uffff') / 65535.0F * var11;
      }

      return var5;
   }

   public float[] fromCIEXYZ(float[] var1) {
      if (this.xyz2this == null) {
         ColorTransform[] var2 = new ColorTransform[2];
         ICC_ColorSpace var3 = (ICC_ColorSpace)ColorSpace.getInstance(1001);
         PCMM var4 = CMSManager.getModule();
         var2[0] = var4.createTransform(var3.getProfile(), -1, 1);

         try {
            var2[1] = var4.createTransform(this.thisProfile, 1, 2);
         } catch (CMMException var8) {
            var2[1] = CMSManager.getModule().createTransform(this.thisProfile, -1, 2);
         }

         this.xyz2this = var4.createTransform(var2);
         if (this.needScaleInit) {
            this.setComponentScaling();
         }
      }

      short[] var9 = new short[3];
      float var10 = 1.9999695F;
      float var11 = 65535.0F / var10;

      int var5;
      for(var5 = 0; var5 < 3; ++var5) {
         var9[var5] = (short)((int)(var1[var5] * var11 + 0.5F));
      }

      var9 = this.xyz2this.colorConvert((short[])var9, (short[])null);
      var5 = this.getNumComponents();
      float[] var6 = new float[var5];

      for(int var7 = 0; var7 < var5; ++var7) {
         var6[var7] = (float)(var9[var7] & '\uffff') / 65535.0F * this.diffMinMax[var7] + this.minVal[var7];
      }

      return var6;
   }

   public float getMinValue(int var1) {
      if (var1 >= 0 && var1 <= this.getNumComponents() - 1) {
         return this.minVal[var1];
      } else {
         throw new IllegalArgumentException("Component index out of range: + component");
      }
   }

   public float getMaxValue(int var1) {
      if (var1 >= 0 && var1 <= this.getNumComponents() - 1) {
         return this.maxVal[var1];
      } else {
         throw new IllegalArgumentException("Component index out of range: + component");
      }
   }

   private void setMinMax() {
      int var1 = this.getNumComponents();
      int var2 = this.getType();
      this.minVal = new float[var1];
      this.maxVal = new float[var1];
      if (var2 == 1) {
         this.minVal[0] = 0.0F;
         this.maxVal[0] = 100.0F;
         this.minVal[1] = -128.0F;
         this.maxVal[1] = 127.0F;
         this.minVal[2] = -128.0F;
         this.maxVal[2] = 127.0F;
      } else if (var2 == 0) {
         this.minVal[0] = this.minVal[1] = this.minVal[2] = 0.0F;
         this.maxVal[0] = this.maxVal[1] = this.maxVal[2] = 1.9999695F;
      } else {
         for(int var3 = 0; var3 < var1; ++var3) {
            this.minVal[var3] = 0.0F;
            this.maxVal[var3] = 1.0F;
         }
      }

   }

   private void setComponentScaling() {
      int var1 = this.getNumComponents();
      this.diffMinMax = new float[var1];
      this.invDiffMinMax = new float[var1];

      for(int var2 = 0; var2 < var1; ++var2) {
         this.minVal[var2] = this.getMinValue(var2);
         this.maxVal[var2] = this.getMaxValue(var2);
         this.diffMinMax[var2] = this.maxVal[var2] - this.minVal[var2];
         this.invDiffMinMax[var2] = 65535.0F / this.diffMinMax[var2];
      }

      this.needScaleInit = false;
   }
}
