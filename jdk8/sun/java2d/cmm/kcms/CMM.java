package sun.java2d.cmm.kcms;

import java.awt.color.CMMException;
import java.awt.color.ICC_Profile;
import java.awt.color.ProfileDataException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.java2d.cmm.ColorTransform;
import sun.java2d.cmm.PCMM;
import sun.java2d.cmm.Profile;

public class CMM implements PCMM {
   private static long ID = 0L;
   static final int cmmStatSuccess = 0;
   static final int cmmStatBadProfile = 503;
   static final int cmmStatBadTagData = 504;
   static final int cmmStatBadTagType = 505;
   static final int cmmStatBadTagId = 506;
   static final int cmmStatBadXform = 507;
   static final int cmmStatXformNotActive = 508;
   static final int cmmStatOutOfRange = 518;
   static final int cmmStatTagNotFound = 519;
   private static CMM theKcms = null;

   static native int cmmLoadProfile(byte[] var0, long[] var1);

   static native int cmmFreeProfile(long var0);

   static native int cmmGetProfileSize(long var0, int[] var2);

   static native int cmmGetProfileData(long var0, byte[] var2);

   static native int cmmGetTagSize(long var0, int var2, int[] var3);

   static native int cmmGetTagData(long var0, int var2, byte[] var3);

   static native int cmmSetTagData(long var0, int var2, byte[] var3);

   static native int cmmGetTransform(ICC_Profile var0, int var1, int var2, ICC_Transform var3);

   static native int cmmCombineTransforms(ICC_Transform[] var0, ICC_Transform var1);

   static native int cmmFreeTransform(long var0);

   static native int cmmGetNumComponents(long var0, int[] var2);

   static native int cmmColorConvert(long var0, CMMImageLayout var2, CMMImageLayout var3);

   private CMM() {
   }

   private long getKcmsPtr(Profile var1) {
      if (var1 instanceof CMM.KcmsProfile) {
         return ((CMM.KcmsProfile)var1).getKcmsPtr();
      } else {
         throw new CMMException("Invalid profile");
      }
   }

   public Profile loadProfile(byte[] var1) {
      long[] var2 = new long[1];
      checkStatus(cmmLoadProfile(var1, var2));
      return var2[0] != 0L ? new CMM.KcmsProfile(var2[0]) : null;
   }

   public void freeProfile(Profile var1) {
      checkStatus(cmmFreeProfile(this.getKcmsPtr(var1)));
   }

   public int getProfileSize(Profile var1) {
      int[] var2 = new int[1];
      checkStatus(cmmGetProfileSize(this.getKcmsPtr(var1), var2));
      return var2[0];
   }

   public void getProfileData(Profile var1, byte[] var2) {
      checkStatus(cmmGetProfileData(this.getKcmsPtr(var1), var2));
   }

   public int getTagSize(Profile var1, int var2) {
      int[] var3 = new int[1];
      checkStatus(cmmGetTagSize(this.getKcmsPtr(var1), var2, var3));
      return var3[0];
   }

   public void getTagData(Profile var1, int var2, byte[] var3) {
      checkStatus(cmmGetTagData(this.getKcmsPtr(var1), var2, var3));
   }

   public void setTagData(Profile var1, int var2, byte[] var3) {
      int var4 = cmmSetTagData(this.getKcmsPtr(var1), var2, var3);
      switch(var4) {
      case 504:
      case 505:
      case 519:
         throw new IllegalArgumentException("Can not write tag data.");
      default:
         checkStatus(var4);
      }
   }

   public ColorTransform createTransform(ICC_Profile var1, int var2, int var3) {
      ICC_Transform var4 = new ICC_Transform();
      checkStatus(cmmGetTransform(var1, var2, var3, var4));
      return var4;
   }

   public ColorTransform createTransform(ColorTransform[] var1) {
      ICC_Transform var2 = new ICC_Transform();
      ICC_Transform[] var3 = new ICC_Transform[var1.length];

      int var4;
      for(var4 = 0; var4 < var1.length; ++var4) {
         var3[var4] = (ICC_Transform)var1[var4];
      }

      var4 = cmmCombineTransforms(var3, var2);
      if (var4 == 0 && var2.getID() != 0L) {
         return var2;
      } else {
         throw new ProfileDataException("Invalid profile sequence");
      }
   }

   static native int cmmInit();

   static native int cmmTerminate();

   static synchronized PCMM getModule() {
      if (theKcms != null) {
         return theKcms;
      } else {
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               System.loadLibrary("kcms");
               return null;
            }
         });
         int var0 = cmmInit();
         checkStatus(var0);
         theKcms = new CMM();
         return theKcms;
      }
   }

   protected void finalize() {
      checkStatus(cmmTerminate());
   }

   public static void checkStatus(int var0) {
      if (var0 != 0) {
         throw new CMMException(errorString(var0));
      }
   }

   static String errorString(int var0) {
      switch(var0) {
      case 0:
         return "Success";
      case 503:
         return "Invalid profile data";
      case 504:
         return "Invalid tag data";
      case 505:
         return "Invalid tag type";
      case 506:
         return "Invalid tag signature";
      case 507:
         return "Invlaid transform";
      case 508:
         return "Transform is not active";
      case 518:
         return "Invalid image format";
      case 519:
         return "No such tag";
      default:
         return "General CMM error" + var0;
      }
   }

   final class KcmsProfile extends Profile {
      KcmsProfile(long var2) {
         super(var2);
      }

      long getKcmsPtr() {
         return this.getNativePtr();
      }
   }
}
