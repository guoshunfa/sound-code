package java.awt.color;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.StringTokenizer;
import sun.java2d.cmm.CMSManager;
import sun.java2d.cmm.PCMM;
import sun.java2d.cmm.Profile;
import sun.java2d.cmm.ProfileActivator;
import sun.java2d.cmm.ProfileDataVerifier;
import sun.java2d.cmm.ProfileDeferralInfo;
import sun.java2d.cmm.ProfileDeferralMgr;

public class ICC_Profile implements Serializable {
   private static final long serialVersionUID = -3938515861990936766L;
   private transient Profile cmmProfile;
   private transient ProfileDeferralInfo deferralInfo;
   private transient ProfileActivator profileActivator;
   private static ICC_Profile sRGBprofile;
   private static ICC_Profile XYZprofile;
   private static ICC_Profile PYCCprofile;
   private static ICC_Profile GRAYprofile;
   private static ICC_Profile LINEAR_RGBprofile;
   public static final int CLASS_INPUT = 0;
   public static final int CLASS_DISPLAY = 1;
   public static final int CLASS_OUTPUT = 2;
   public static final int CLASS_DEVICELINK = 3;
   public static final int CLASS_COLORSPACECONVERSION = 4;
   public static final int CLASS_ABSTRACT = 5;
   public static final int CLASS_NAMEDCOLOR = 6;
   public static final int icSigXYZData = 1482250784;
   public static final int icSigLabData = 1281450528;
   public static final int icSigLuvData = 1282766368;
   public static final int icSigYCbCrData = 1497588338;
   public static final int icSigYxyData = 1501067552;
   public static final int icSigRgbData = 1380401696;
   public static final int icSigGrayData = 1196573017;
   public static final int icSigHsvData = 1213421088;
   public static final int icSigHlsData = 1212961568;
   public static final int icSigCmykData = 1129142603;
   public static final int icSigCmyData = 1129142560;
   public static final int icSigSpace2CLR = 843271250;
   public static final int icSigSpace3CLR = 860048466;
   public static final int icSigSpace4CLR = 876825682;
   public static final int icSigSpace5CLR = 893602898;
   public static final int icSigSpace6CLR = 910380114;
   public static final int icSigSpace7CLR = 927157330;
   public static final int icSigSpace8CLR = 943934546;
   public static final int icSigSpace9CLR = 960711762;
   public static final int icSigSpaceACLR = 1094929490;
   public static final int icSigSpaceBCLR = 1111706706;
   public static final int icSigSpaceCCLR = 1128483922;
   public static final int icSigSpaceDCLR = 1145261138;
   public static final int icSigSpaceECLR = 1162038354;
   public static final int icSigSpaceFCLR = 1178815570;
   public static final int icSigInputClass = 1935896178;
   public static final int icSigDisplayClass = 1835955314;
   public static final int icSigOutputClass = 1886549106;
   public static final int icSigLinkClass = 1818848875;
   public static final int icSigAbstractClass = 1633842036;
   public static final int icSigColorSpaceClass = 1936744803;
   public static final int icSigNamedColorClass = 1852662636;
   public static final int icPerceptual = 0;
   public static final int icRelativeColorimetric = 1;
   public static final int icMediaRelativeColorimetric = 1;
   public static final int icSaturation = 2;
   public static final int icAbsoluteColorimetric = 3;
   public static final int icICCAbsoluteColorimetric = 3;
   public static final int icSigHead = 1751474532;
   public static final int icSigAToB0Tag = 1093812784;
   public static final int icSigAToB1Tag = 1093812785;
   public static final int icSigAToB2Tag = 1093812786;
   public static final int icSigBlueColorantTag = 1649957210;
   public static final int icSigBlueMatrixColumnTag = 1649957210;
   public static final int icSigBlueTRCTag = 1649693251;
   public static final int icSigBToA0Tag = 1110589744;
   public static final int icSigBToA1Tag = 1110589745;
   public static final int icSigBToA2Tag = 1110589746;
   public static final int icSigCalibrationDateTimeTag = 1667329140;
   public static final int icSigCharTargetTag = 1952543335;
   public static final int icSigCopyrightTag = 1668313716;
   public static final int icSigCrdInfoTag = 1668441193;
   public static final int icSigDeviceMfgDescTag = 1684893284;
   public static final int icSigDeviceModelDescTag = 1684890724;
   public static final int icSigDeviceSettingsTag = 1684371059;
   public static final int icSigGamutTag = 1734438260;
   public static final int icSigGrayTRCTag = 1800688195;
   public static final int icSigGreenColorantTag = 1733843290;
   public static final int icSigGreenMatrixColumnTag = 1733843290;
   public static final int icSigGreenTRCTag = 1733579331;
   public static final int icSigLuminanceTag = 1819635049;
   public static final int icSigMeasurementTag = 1835360627;
   public static final int icSigMediaBlackPointTag = 1651208308;
   public static final int icSigMediaWhitePointTag = 2004119668;
   public static final int icSigNamedColor2Tag = 1852009522;
   public static final int icSigOutputResponseTag = 1919251312;
   public static final int icSigPreview0Tag = 1886545200;
   public static final int icSigPreview1Tag = 1886545201;
   public static final int icSigPreview2Tag = 1886545202;
   public static final int icSigProfileDescriptionTag = 1684370275;
   public static final int icSigProfileSequenceDescTag = 1886610801;
   public static final int icSigPs2CRD0Tag = 1886610480;
   public static final int icSigPs2CRD1Tag = 1886610481;
   public static final int icSigPs2CRD2Tag = 1886610482;
   public static final int icSigPs2CRD3Tag = 1886610483;
   public static final int icSigPs2CSATag = 1886597747;
   public static final int icSigPs2RenderingIntentTag = 1886597737;
   public static final int icSigRedColorantTag = 1918392666;
   public static final int icSigRedMatrixColumnTag = 1918392666;
   public static final int icSigRedTRCTag = 1918128707;
   public static final int icSigScreeningDescTag = 1935897188;
   public static final int icSigScreeningTag = 1935897198;
   public static final int icSigTechnologyTag = 1952801640;
   public static final int icSigUcrBgTag = 1650877472;
   public static final int icSigViewingCondDescTag = 1987405156;
   public static final int icSigViewingConditionsTag = 1986618743;
   public static final int icSigChromaticityTag = 1667789421;
   public static final int icSigChromaticAdaptationTag = 1667785060;
   public static final int icSigColorantOrderTag = 1668051567;
   public static final int icSigColorantTableTag = 1668051572;
   public static final int icHdrSize = 0;
   public static final int icHdrCmmId = 4;
   public static final int icHdrVersion = 8;
   public static final int icHdrDeviceClass = 12;
   public static final int icHdrColorSpace = 16;
   public static final int icHdrPcs = 20;
   public static final int icHdrDate = 24;
   public static final int icHdrMagic = 36;
   public static final int icHdrPlatform = 40;
   public static final int icHdrFlags = 44;
   public static final int icHdrManufacturer = 48;
   public static final int icHdrModel = 52;
   public static final int icHdrAttributes = 56;
   public static final int icHdrRenderingIntent = 64;
   public static final int icHdrIlluminant = 68;
   public static final int icHdrCreator = 80;
   public static final int icHdrProfileID = 84;
   public static final int icTagType = 0;
   public static final int icTagReserved = 4;
   public static final int icCurveCount = 8;
   public static final int icCurveData = 12;
   public static final int icXYZNumberX = 8;
   private int iccProfileSerializedDataVersion = 1;
   private transient ICC_Profile resolvedDeserializedProfile;

   ICC_Profile(Profile var1) {
      this.cmmProfile = var1;
   }

   ICC_Profile(ProfileDeferralInfo var1) {
      this.deferralInfo = var1;
      this.profileActivator = new ProfileActivator() {
         public void activate() throws ProfileDataException {
            ICC_Profile.this.activateDeferredProfile();
         }
      };
      ProfileDeferralMgr.registerDeferral(this.profileActivator);
   }

   protected void finalize() {
      if (this.cmmProfile != null) {
         CMSManager.getModule().freeProfile(this.cmmProfile);
      } else if (this.profileActivator != null) {
         ProfileDeferralMgr.unregisterDeferral(this.profileActivator);
      }

   }

   public static ICC_Profile getInstance(byte[] var0) {
      Profile var2 = null;
      if (ProfileDeferralMgr.deferring) {
         ProfileDeferralMgr.activateProfiles();
      }

      ProfileDataVerifier.verify(var0);

      try {
         var2 = CMSManager.getModule().loadProfile(var0);
      } catch (CMMException var4) {
         throw new IllegalArgumentException("Invalid ICC Profile Data");
      }

      Object var1;
      try {
         if (getColorSpaceType(var2) == 6 && getData(var2, 2004119668) != null && getData(var2, 1800688195) != null) {
            var1 = new ICC_ProfileGray(var2);
         } else if (getColorSpaceType(var2) == 5 && getData(var2, 2004119668) != null && getData(var2, 1918392666) != null && getData(var2, 1733843290) != null && getData(var2, 1649957210) != null && getData(var2, 1918128707) != null && getData(var2, 1733579331) != null && getData(var2, 1649693251) != null) {
            var1 = new ICC_ProfileRGB(var2);
         } else {
            var1 = new ICC_Profile(var2);
         }
      } catch (CMMException var5) {
         var1 = new ICC_Profile(var2);
      }

      return (ICC_Profile)var1;
   }

   public static ICC_Profile getInstance(int var0) {
      ICC_Profile var1 = null;
      Class var3;
      ProfileDeferralInfo var4;
      switch(var0) {
      case 1000:
         var3 = ICC_Profile.class;
         synchronized(ICC_Profile.class) {
            if (sRGBprofile == null) {
               var4 = new ProfileDeferralInfo("sRGB.pf", 5, 3, 1);
               sRGBprofile = getDeferredInstance(var4);
            }

            var1 = sRGBprofile;
            break;
         }
      case 1001:
         var3 = ICC_Profile.class;
         synchronized(ICC_Profile.class) {
            if (XYZprofile == null) {
               var4 = new ProfileDeferralInfo("CIEXYZ.pf", 0, 3, 1);
               XYZprofile = getDeferredInstance(var4);
            }

            var1 = XYZprofile;
            break;
         }
      case 1002:
         var3 = ICC_Profile.class;
         synchronized(ICC_Profile.class) {
            if (PYCCprofile == null) {
               if (!standardProfileExists("PYCC.pf")) {
                  throw new IllegalArgumentException("Can't load standard profile: PYCC.pf");
               }

               var4 = new ProfileDeferralInfo("PYCC.pf", 13, 3, 1);
               PYCCprofile = getDeferredInstance(var4);
            }

            var1 = PYCCprofile;
            break;
         }
      case 1003:
         var3 = ICC_Profile.class;
         synchronized(ICC_Profile.class) {
            if (GRAYprofile == null) {
               var4 = new ProfileDeferralInfo("GRAY.pf", 6, 1, 1);
               GRAYprofile = getDeferredInstance(var4);
            }

            var1 = GRAYprofile;
            break;
         }
      case 1004:
         var3 = ICC_Profile.class;
         synchronized(ICC_Profile.class) {
            if (LINEAR_RGBprofile == null) {
               var4 = new ProfileDeferralInfo("LINEAR_RGB.pf", 5, 3, 1);
               LINEAR_RGBprofile = getDeferredInstance(var4);
            }

            var1 = LINEAR_RGBprofile;
            break;
         }
      default:
         throw new IllegalArgumentException("Unknown color space");
      }

      return var1;
   }

   private static ICC_Profile getStandardProfile(final String var0) {
      return (ICC_Profile)AccessController.doPrivileged(new PrivilegedAction<ICC_Profile>() {
         public ICC_Profile run() {
            ICC_Profile var1 = null;

            try {
               var1 = ICC_Profile.getInstance(var0);
               return var1;
            } catch (IOException var3) {
               throw new IllegalArgumentException("Can't load standard profile: " + var0);
            }
         }
      });
   }

   public static ICC_Profile getInstance(String var0) throws IOException {
      FileInputStream var2 = null;
      File var3 = getProfileFile(var0);
      if (var3 != null) {
         var2 = new FileInputStream(var3);
      }

      if (var2 == null) {
         throw new IOException("Cannot open file " + var0);
      } else {
         ICC_Profile var1 = getInstance((InputStream)var2);
         var2.close();
         return var1;
      }
   }

   public static ICC_Profile getInstance(InputStream var0) throws IOException {
      if (var0 instanceof ProfileDeferralInfo) {
         return getDeferredInstance((ProfileDeferralInfo)var0);
      } else {
         byte[] var1;
         if ((var1 = getProfileDataFromStream(var0)) == null) {
            throw new IllegalArgumentException("Invalid ICC Profile Data");
         } else {
            return getInstance(var1);
         }
      }
   }

   static byte[] getProfileDataFromStream(InputStream var0) throws IOException {
      byte[] var3 = new byte[128];
      int var4 = 128;

      int var5;
      int var6;
      for(var5 = 0; var4 != 0; var4 -= var6) {
         if ((var6 = var0.read(var3, var5, var4)) < 0) {
            return null;
         }

         var5 += var6;
      }

      if (var3[36] == 97 && var3[37] == 99 && var3[38] == 115 && var3[39] == 112) {
         int var2 = (var3[0] & 255) << 24 | (var3[1] & 255) << 16 | (var3[2] & 255) << 8 | var3[3] & 255;
         byte[] var1 = new byte[var2];
         System.arraycopy(var3, 0, var1, 0, 128);
         var4 = var2 - 128;

         for(var5 = 128; var4 != 0; var4 -= var6) {
            if ((var6 = var0.read(var1, var5, var4)) < 0) {
               return null;
            }

            var5 += var6;
         }

         return var1;
      } else {
         return null;
      }
   }

   static ICC_Profile getDeferredInstance(ProfileDeferralInfo var0) {
      if (!ProfileDeferralMgr.deferring) {
         return getStandardProfile(var0.filename);
      } else if (var0.colorSpaceType == 5) {
         return new ICC_ProfileRGB(var0);
      } else {
         return (ICC_Profile)(var0.colorSpaceType == 6 ? new ICC_ProfileGray(var0) : new ICC_Profile(var0));
      }
   }

   void activateDeferredProfile() throws ProfileDataException {
      final String var3 = this.deferralInfo.filename;
      this.profileActivator = null;
      this.deferralInfo = null;
      PrivilegedAction var4 = new PrivilegedAction<FileInputStream>() {
         public FileInputStream run() {
            File var1 = ICC_Profile.getStandardProfileFile(var3);
            if (var1 != null) {
               try {
                  return new FileInputStream(var1);
               } catch (FileNotFoundException var3x) {
               }
            }

            return null;
         }
      };
      FileInputStream var2;
      if ((var2 = (FileInputStream)AccessController.doPrivileged(var4)) == null) {
         throw new ProfileDataException("Cannot open file " + var3);
      } else {
         byte[] var1;
         ProfileDataException var6;
         try {
            var1 = getProfileDataFromStream(var2);
            var2.close();
         } catch (IOException var8) {
            var6 = new ProfileDataException("Invalid ICC Profile Data" + var3);
            var6.initCause(var8);
            throw var6;
         }

         if (var1 == null) {
            throw new ProfileDataException("Invalid ICC Profile Data" + var3);
         } else {
            try {
               this.cmmProfile = CMSManager.getModule().loadProfile(var1);
            } catch (CMMException var7) {
               var6 = new ProfileDataException("Invalid ICC Profile Data" + var3);
               var6.initCause(var7);
               throw var6;
            }
         }
      }
   }

   public int getMajorVersion() {
      byte[] var1 = this.getData(1751474532);
      return var1[8];
   }

   public int getMinorVersion() {
      byte[] var1 = this.getData(1751474532);
      return var1[9];
   }

   public int getProfileClass() {
      if (this.deferralInfo != null) {
         return this.deferralInfo.profileClass;
      } else {
         byte[] var1 = this.getData(1751474532);
         int var2 = intFromBigEndian(var1, 12);
         byte var3;
         switch(var2) {
         case 1633842036:
            var3 = 5;
            break;
         case 1818848875:
            var3 = 3;
            break;
         case 1835955314:
            var3 = 1;
            break;
         case 1852662636:
            var3 = 6;
            break;
         case 1886549106:
            var3 = 2;
            break;
         case 1935896178:
            var3 = 0;
            break;
         case 1936744803:
            var3 = 4;
            break;
         default:
            throw new IllegalArgumentException("Unknown profile class");
         }

         return var3;
      }
   }

   public int getColorSpaceType() {
      return this.deferralInfo != null ? this.deferralInfo.colorSpaceType : getColorSpaceType(this.cmmProfile);
   }

   static int getColorSpaceType(Profile var0) {
      byte[] var1 = getData(var0, 1751474532);
      int var2 = intFromBigEndian(var1, 16);
      int var3 = iccCStoJCS(var2);
      return var3;
   }

   public int getPCSType() {
      if (ProfileDeferralMgr.deferring) {
         ProfileDeferralMgr.activateProfiles();
      }

      return getPCSType(this.cmmProfile);
   }

   static int getPCSType(Profile var0) {
      byte[] var1 = getData(var0, 1751474532);
      int var2 = intFromBigEndian(var1, 20);
      int var3 = iccCStoJCS(var2);
      return var3;
   }

   public void write(String var1) throws IOException {
      byte[] var3 = this.getData();
      FileOutputStream var2 = new FileOutputStream(var1);
      var2.write(var3);
      var2.close();
   }

   public void write(OutputStream var1) throws IOException {
      byte[] var2 = this.getData();
      var1.write(var2);
   }

   public byte[] getData() {
      if (ProfileDeferralMgr.deferring) {
         ProfileDeferralMgr.activateProfiles();
      }

      PCMM var3 = CMSManager.getModule();
      int var1 = var3.getProfileSize(this.cmmProfile);
      byte[] var2 = new byte[var1];
      var3.getProfileData(this.cmmProfile, var2);
      return var2;
   }

   public byte[] getData(int var1) {
      if (ProfileDeferralMgr.deferring) {
         ProfileDeferralMgr.activateProfiles();
      }

      return getData(this.cmmProfile, var1);
   }

   static byte[] getData(Profile var0, int var1) {
      byte[] var3;
      try {
         PCMM var4 = CMSManager.getModule();
         int var2 = var4.getTagSize(var0, var1);
         var3 = new byte[var2];
         var4.getTagData(var0, var1, var3);
      } catch (CMMException var5) {
         var3 = null;
      }

      return var3;
   }

   public void setData(int var1, byte[] var2) {
      if (ProfileDeferralMgr.deferring) {
         ProfileDeferralMgr.activateProfiles();
      }

      CMSManager.getModule().setTagData(this.cmmProfile, var1, var2);
   }

   void setRenderingIntent(int var1) {
      byte[] var2 = this.getData(1751474532);
      intToBigEndian(var1, var2, 64);
      this.setData(1751474532, var2);
   }

   int getRenderingIntent() {
      byte[] var1 = this.getData(1751474532);
      int var2 = intFromBigEndian(var1, 64);
      return '\uffff' & var2;
   }

   public int getNumComponents() {
      if (this.deferralInfo != null) {
         return this.deferralInfo.numComponents;
      } else {
         byte[] var1 = this.getData(1751474532);
         int var2 = intFromBigEndian(var1, 16);
         byte var3;
         switch(var2) {
         case 843271250:
            var3 = 2;
            break;
         case 860048466:
         case 1129142560:
         case 1212961568:
         case 1213421088:
         case 1281450528:
         case 1282766368:
         case 1380401696:
         case 1482250784:
         case 1497588338:
         case 1501067552:
            var3 = 3;
            break;
         case 876825682:
         case 1129142603:
            var3 = 4;
            break;
         case 893602898:
            var3 = 5;
            break;
         case 910380114:
            var3 = 6;
            break;
         case 927157330:
            var3 = 7;
            break;
         case 943934546:
            var3 = 8;
            break;
         case 960711762:
            var3 = 9;
            break;
         case 1094929490:
            var3 = 10;
            break;
         case 1111706706:
            var3 = 11;
            break;
         case 1128483922:
            var3 = 12;
            break;
         case 1145261138:
            var3 = 13;
            break;
         case 1162038354:
            var3 = 14;
            break;
         case 1178815570:
            var3 = 15;
            break;
         case 1196573017:
            var3 = 1;
            break;
         default:
            throw new ProfileDataException("invalid ICC color space");
         }

         return var3;
      }
   }

   float[] getMediaWhitePoint() {
      return this.getXYZTag(2004119668);
   }

   float[] getXYZTag(int var1) {
      byte[] var2 = this.getData(var1);
      float[] var3 = new float[3];
      int var4 = 0;

      for(int var5 = 8; var4 < 3; var5 += 4) {
         int var6 = intFromBigEndian(var2, var5);
         var3[var4] = (float)var6 / 65536.0F;
         ++var4;
      }

      return var3;
   }

   float getGamma(int var1) {
      byte[] var2 = this.getData(var1);
      if (intFromBigEndian(var2, 8) != 1) {
         throw new ProfileDataException("TRC is not a gamma");
      } else {
         int var4 = shortFromBigEndian(var2, 12) & '\uffff';
         float var3 = (float)var4 / 256.0F;
         return var3;
      }
   }

   short[] getTRC(int var1) {
      byte[] var2 = this.getData(var1);
      int var6 = intFromBigEndian(var2, 8);
      if (var6 == 1) {
         throw new ProfileDataException("TRC is not a table");
      } else {
         short[] var3 = new short[var6];
         int var4 = 0;

         for(int var5 = 12; var4 < var6; var5 += 2) {
            var3[var4] = shortFromBigEndian(var2, var5);
            ++var4;
         }

         return var3;
      }
   }

   static int iccCStoJCS(int var0) {
      byte var1;
      switch(var0) {
      case 843271250:
         var1 = 12;
         break;
      case 860048466:
         var1 = 13;
         break;
      case 876825682:
         var1 = 14;
         break;
      case 893602898:
         var1 = 15;
         break;
      case 910380114:
         var1 = 16;
         break;
      case 927157330:
         var1 = 17;
         break;
      case 943934546:
         var1 = 18;
         break;
      case 960711762:
         var1 = 19;
         break;
      case 1094929490:
         var1 = 20;
         break;
      case 1111706706:
         var1 = 21;
         break;
      case 1128483922:
         var1 = 22;
         break;
      case 1129142560:
         var1 = 11;
         break;
      case 1129142603:
         var1 = 9;
         break;
      case 1145261138:
         var1 = 23;
         break;
      case 1162038354:
         var1 = 24;
         break;
      case 1178815570:
         var1 = 25;
         break;
      case 1196573017:
         var1 = 6;
         break;
      case 1212961568:
         var1 = 8;
         break;
      case 1213421088:
         var1 = 7;
         break;
      case 1281450528:
         var1 = 1;
         break;
      case 1282766368:
         var1 = 2;
         break;
      case 1380401696:
         var1 = 5;
         break;
      case 1482250784:
         var1 = 0;
         break;
      case 1497588338:
         var1 = 3;
         break;
      case 1501067552:
         var1 = 4;
         break;
      default:
         throw new IllegalArgumentException("Unknown color space");
      }

      return var1;
   }

   static int intFromBigEndian(byte[] var0, int var1) {
      return (var0[var1] & 255) << 24 | (var0[var1 + 1] & 255) << 16 | (var0[var1 + 2] & 255) << 8 | var0[var1 + 3] & 255;
   }

   static void intToBigEndian(int var0, byte[] var1, int var2) {
      var1[var2] = (byte)(var0 >> 24);
      var1[var2 + 1] = (byte)(var0 >> 16);
      var1[var2 + 2] = (byte)(var0 >> 8);
      var1[var2 + 3] = (byte)var0;
   }

   static short shortFromBigEndian(byte[] var0, int var1) {
      return (short)((var0[var1] & 255) << 8 | var0[var1 + 1] & 255);
   }

   static void shortToBigEndian(short var0, byte[] var1, int var2) {
      var1[var2] = (byte)(var0 >> 8);
      var1[var2 + 1] = (byte)var0;
   }

   private static File getProfileFile(String var0) {
      File var4 = new File(var0);
      if (var4.isAbsolute()) {
         return var4.isFile() ? var4 : null;
      } else {
         String var1;
         String var2;
         String var3;
         StringTokenizer var5;
         if (!var4.isFile() && (var1 = System.getProperty("java.iccprofile.path")) != null) {
            var5 = new StringTokenizer(var1, File.pathSeparator);

            while(var5.hasMoreTokens() && (var4 == null || !var4.isFile())) {
               var2 = var5.nextToken();
               var3 = var2 + File.separatorChar + var0;
               var4 = new File(var3);
               if (!isChildOf(var4, var2)) {
                  var4 = null;
               }
            }
         }

         if ((var4 == null || !var4.isFile()) && (var1 = System.getProperty("java.class.path")) != null) {
            for(var5 = new StringTokenizer(var1, File.pathSeparator); var5.hasMoreTokens() && (var4 == null || !var4.isFile()); var4 = new File(var3)) {
               var2 = var5.nextToken();
               var3 = var2 + File.separatorChar + var0;
            }
         }

         if (var4 == null || !var4.isFile()) {
            var4 = getStandardProfileFile(var0);
         }

         return var4 != null && var4.isFile() ? var4 : null;
      }
   }

   private static File getStandardProfileFile(String var0) {
      String var1 = System.getProperty("java.home") + File.separatorChar + "lib" + File.separatorChar + "cmm";
      String var2 = var1 + File.separatorChar + var0;
      File var3 = new File(var2);
      return var3.isFile() && isChildOf(var3, var1) ? var3 : null;
   }

   private static boolean isChildOf(File var0, String var1) {
      try {
         File var2 = new File(var1);
         String var3 = var2.getCanonicalPath();
         if (!var3.endsWith(File.separator)) {
            var3 = var3 + File.separator;
         }

         String var4 = var0.getCanonicalPath();
         return var4.startsWith(var3);
      } catch (IOException var5) {
         return false;
      }
   }

   private static boolean standardProfileExists(final String var0) {
      return (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
         public Boolean run() {
            return ICC_Profile.getStandardProfileFile(var0) != null;
         }
      });
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      String var2 = null;
      if (this == sRGBprofile) {
         var2 = "CS_sRGB";
      } else if (this == XYZprofile) {
         var2 = "CS_CIEXYZ";
      } else if (this == PYCCprofile) {
         var2 = "CS_PYCC";
      } else if (this == GRAYprofile) {
         var2 = "CS_GRAY";
      } else if (this == LINEAR_RGBprofile) {
         var2 = "CS_LINEAR_RGB";
      }

      byte[] var3 = null;
      if (var2 == null) {
         var3 = this.getData();
      }

      var1.writeObject(var2);
      var1.writeObject(var3);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      String var2 = (String)var1.readObject();
      byte[] var3 = (byte[])((byte[])var1.readObject());
      short var4 = 0;
      boolean var5 = false;
      if (var2 != null) {
         var5 = true;
         if (var2.equals("CS_sRGB")) {
            var4 = 1000;
         } else if (var2.equals("CS_CIEXYZ")) {
            var4 = 1001;
         } else if (var2.equals("CS_PYCC")) {
            var4 = 1002;
         } else if (var2.equals("CS_GRAY")) {
            var4 = 1003;
         } else if (var2.equals("CS_LINEAR_RGB")) {
            var4 = 1004;
         } else {
            var5 = false;
         }
      }

      if (var5) {
         this.resolvedDeserializedProfile = getInstance(var4);
      } else {
         this.resolvedDeserializedProfile = getInstance(var3);
      }

   }

   protected Object readResolve() throws ObjectStreamException {
      return this.resolvedDeserializedProfile;
   }
}
