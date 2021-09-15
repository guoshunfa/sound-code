package sun.java2d.cmm.lcms;

import java.awt.color.CMMException;
import java.awt.color.ICC_Profile;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.java2d.cmm.ColorTransform;
import sun.java2d.cmm.PCMM;
import sun.java2d.cmm.Profile;

public class LCMS implements PCMM {
   private static LCMS theLcms = null;

   public Profile loadProfile(byte[] var1) {
      Object var2 = new Object();
      long var3 = this.loadProfileNative(var1, var2);
      return var3 != 0L ? new LCMSProfile(var3, var2) : null;
   }

   private native long loadProfileNative(byte[] var1, Object var2);

   private LCMSProfile getLcmsProfile(Profile var1) {
      if (var1 instanceof LCMSProfile) {
         return (LCMSProfile)var1;
      } else {
         throw new CMMException("Invalid profile: " + var1);
      }
   }

   public void freeProfile(Profile var1) {
   }

   public int getProfileSize(Profile var1) {
      synchronized(var1) {
         return this.getProfileSizeNative(this.getLcmsProfile(var1).getLcmsPtr());
      }
   }

   private native int getProfileSizeNative(long var1);

   public void getProfileData(Profile var1, byte[] var2) {
      synchronized(var1) {
         this.getProfileDataNative(this.getLcmsProfile(var1).getLcmsPtr(), var2);
      }
   }

   private native void getProfileDataNative(long var1, byte[] var3);

   public int getTagSize(Profile var1, int var2) {
      LCMSProfile var3 = this.getLcmsProfile(var1);
      synchronized(var3) {
         LCMSProfile.TagData var5 = var3.getTag(var2);
         return var5 == null ? 0 : var5.getSize();
      }
   }

   static native byte[] getTagNative(long var0, int var2);

   public void getTagData(Profile var1, int var2, byte[] var3) {
      LCMSProfile var4 = this.getLcmsProfile(var1);
      synchronized(var4) {
         LCMSProfile.TagData var6 = var4.getTag(var2);
         if (var6 != null) {
            var6.copyDataTo(var3);
         }

      }
   }

   public synchronized void setTagData(Profile var1, int var2, byte[] var3) {
      LCMSProfile var4 = this.getLcmsProfile(var1);
      synchronized(var4) {
         var4.clearTagCache();
         this.setTagDataNative(var4.getLcmsPtr(), var2, var3);
      }
   }

   private native void setTagDataNative(long var1, int var3, byte[] var4);

   public static synchronized native LCMSProfile getProfileID(ICC_Profile var0);

   static long createTransform(LCMSProfile[] var0, int var1, int var2, boolean var3, int var4, boolean var5, Object var6) {
      long[] var7 = new long[var0.length];

      for(int var8 = 0; var8 < var0.length; ++var8) {
         if (var0[var8] == null) {
            throw new CMMException("Unknown profile ID");
         }

         var7[var8] = var0[var8].getLcmsPtr();
      }

      return createNativeTransform(var7, var1, var2, var3, var4, var5, var6);
   }

   private static native long createNativeTransform(long[] var0, int var1, int var2, boolean var3, int var4, boolean var5, Object var6);

   public ColorTransform createTransform(ICC_Profile var1, int var2, int var3) {
      return new LCMSTransform(var1, var2, var2);
   }

   public synchronized ColorTransform createTransform(ColorTransform[] var1) {
      return new LCMSTransform(var1);
   }

   public static native void colorConvert(LCMSTransform var0, LCMSImageLayout var1, LCMSImageLayout var2);

   public static native void freeTransform(long var0);

   public static native void initLCMS(Class var0, Class var1, Class var2);

   private LCMS() {
   }

   static synchronized PCMM getModule() {
      if (theLcms != null) {
         return theLcms;
      } else {
         AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
               System.loadLibrary("awt");
               System.loadLibrary("lcms");
               return null;
            }
         });
         initLCMS(LCMSTransform.class, LCMSImageLayout.class, ICC_Profile.class);
         theLcms = new LCMS();
         return theLcms;
      }
   }
}
