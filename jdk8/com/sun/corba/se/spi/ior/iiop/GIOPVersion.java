package com.sun.corba.se.spi.ior.iiop;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public class GIOPVersion {
   public static final GIOPVersion V1_0 = new GIOPVersion((byte)1, (byte)0);
   public static final GIOPVersion V1_1 = new GIOPVersion((byte)1, (byte)1);
   public static final GIOPVersion V1_2 = new GIOPVersion((byte)1, (byte)2);
   public static final GIOPVersion V1_3 = new GIOPVersion((byte)1, (byte)3);
   public static final GIOPVersion V13_XX = new GIOPVersion((byte)13, (byte)1);
   public static final GIOPVersion DEFAULT_VERSION;
   public static final int VERSION_1_0 = 256;
   public static final int VERSION_1_1 = 257;
   public static final int VERSION_1_2 = 258;
   public static final int VERSION_1_3 = 259;
   public static final int VERSION_13_XX = 3329;
   private byte major = 0;
   private byte minor = 0;

   public GIOPVersion() {
   }

   public GIOPVersion(byte var1, byte var2) {
      this.major = var1;
      this.minor = var2;
   }

   public GIOPVersion(int var1, int var2) {
      this.major = (byte)var1;
      this.minor = (byte)var2;
   }

   public byte getMajor() {
      return this.major;
   }

   public byte getMinor() {
      return this.minor;
   }

   public boolean equals(GIOPVersion var1) {
      return var1.major == this.major && var1.minor == this.minor;
   }

   public boolean equals(Object var1) {
      return var1 != null && var1 instanceof GIOPVersion ? this.equals((GIOPVersion)var1) : false;
   }

   public int hashCode() {
      return 37 * this.major + this.minor;
   }

   public boolean lessThan(GIOPVersion var1) {
      if (this.major < var1.major) {
         return true;
      } else {
         return this.major == var1.major && this.minor < var1.minor;
      }
   }

   public int intValue() {
      return this.major << 8 | this.minor;
   }

   public String toString() {
      return this.major + "." + this.minor;
   }

   public static GIOPVersion getInstance(byte var0, byte var1) {
      switch(var0 << 8 | var1) {
      case 256:
         return V1_0;
      case 257:
         return V1_1;
      case 258:
         return V1_2;
      case 259:
         return V1_3;
      case 3329:
         return V13_XX;
      default:
         return new GIOPVersion(var0, (byte)var1);
      }
   }

   public static GIOPVersion parseVersion(String var0) {
      int var1 = var0.indexOf(46);
      if (var1 >= 1 && var1 != var0.length() - 1) {
         int var2 = Integer.parseInt(var0.substring(0, var1));
         int var3 = Integer.parseInt(var0.substring(var1 + 1, var0.length()));
         return getInstance((byte)var2, (byte)var3);
      } else {
         throw new NumberFormatException("GIOP major, minor, and decimal point required: " + var0);
      }
   }

   public static GIOPVersion chooseRequestVersion(ORB var0, IOR var1) {
      GIOPVersion var2 = var0.getORBData().getGIOPVersion();
      IIOPProfile var3 = var1.getProfile();
      GIOPVersion var4 = var3.getGIOPVersion();
      ORBVersion var5 = var3.getORBVersion();
      if (!var5.equals(ORBVersionFactory.getFOREIGN()) && var5.lessThan(ORBVersionFactory.getNEWER())) {
         return V1_0;
      } else {
         byte var6 = var4.getMajor();
         byte var7 = var4.getMinor();
         byte var8 = var2.getMajor();
         byte var9 = var2.getMinor();
         if (var8 < var6) {
            return var2;
         } else if (var8 > var6) {
            return var4;
         } else {
            return var9 <= var7 ? var2 : var4;
         }
      }
   }

   public boolean supportsIORIIOPProfileComponents() {
      return this.getMinor() > 0 || this.getMajor() > 1;
   }

   public void read(InputStream var1) {
      this.major = var1.read_octet();
      this.minor = var1.read_octet();
   }

   public void write(OutputStream var1) {
      var1.write_octet(this.major);
      var1.write_octet(this.minor);
   }

   static {
      DEFAULT_VERSION = V1_2;
   }
}
