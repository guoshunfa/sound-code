package sun.security.krb5.internal;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class HostAddress implements Cloneable {
   int addrType;
   byte[] address = null;
   private static InetAddress localInetAddress;
   private static final boolean DEBUG;
   private volatile int hashCode = 0;

   private HostAddress(int var1) {
   }

   public Object clone() {
      HostAddress var1 = new HostAddress(0);
      var1.addrType = this.addrType;
      if (this.address != null) {
         var1.address = (byte[])this.address.clone();
      }

      return var1;
   }

   public int hashCode() {
      if (this.hashCode == 0) {
         byte var1 = 17;
         int var3 = 37 * var1 + this.addrType;
         if (this.address != null) {
            for(int var2 = 0; var2 < this.address.length; ++var2) {
               var3 = 37 * var3 + this.address[var2];
            }
         }

         this.hashCode = var3;
      }

      return this.hashCode;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof HostAddress)) {
         return false;
      } else {
         HostAddress var2 = (HostAddress)var1;
         if (this.addrType != var2.addrType || this.address != null && var2.address == null || this.address == null && var2.address != null) {
            return false;
         } else {
            if (this.address != null && var2.address != null) {
               if (this.address.length != var2.address.length) {
                  return false;
               }

               for(int var3 = 0; var3 < this.address.length; ++var3) {
                  if (this.address[var3] != var2.address[var3]) {
                     return false;
                  }
               }
            }

            return true;
         }
      }
   }

   private static synchronized InetAddress getLocalInetAddress() throws UnknownHostException {
      if (localInetAddress == null) {
         localInetAddress = InetAddress.getLocalHost();
      }

      if (localInetAddress == null) {
         throw new UnknownHostException();
      } else {
         return localInetAddress;
      }
   }

   public InetAddress getInetAddress() throws UnknownHostException {
      return this.addrType != 2 && this.addrType != 24 ? null : InetAddress.getByAddress(this.address);
   }

   private int getAddrType(InetAddress var1) {
      byte var2 = 0;
      if (var1 instanceof Inet4Address) {
         var2 = 2;
      } else if (var1 instanceof Inet6Address) {
         var2 = 24;
      }

      return var2;
   }

   public HostAddress() throws UnknownHostException {
      InetAddress var1 = getLocalInetAddress();
      this.addrType = this.getAddrType(var1);
      this.address = var1.getAddress();
   }

   public HostAddress(int var1, byte[] var2) throws KrbApErrException, UnknownHostException {
      switch(var1) {
      case 2:
         if (var2.length != 4) {
            throw new KrbApErrException(0, "Invalid Internet address");
         }
      case 3:
      case 4:
      case 7:
      case 8:
      case 9:
      case 10:
      case 11:
      case 13:
      case 14:
      case 15:
      case 17:
      case 18:
      case 19:
      case 20:
      case 21:
      case 22:
      case 23:
      default:
         break;
      case 5:
         if (var2.length != 2) {
            throw new KrbApErrException(0, "Invalid CHAOSnet address");
         }
         break;
      case 6:
         if (var2.length != 6) {
            throw new KrbApErrException(0, "Invalid XNS address");
         }
         break;
      case 12:
         if (var2.length != 2) {
            throw new KrbApErrException(0, "Invalid DECnet Phase IV address");
         }
         break;
      case 16:
         if (var2.length != 3) {
            throw new KrbApErrException(0, "Invalid DDP address");
         }
         break;
      case 24:
         if (var2.length != 16) {
            throw new KrbApErrException(0, "Invalid Internet IPv6 address");
         }
      }

      this.addrType = var1;
      if (var2 != null) {
         this.address = (byte[])var2.clone();
      }

      if (DEBUG && (this.addrType == 2 || this.addrType == 24)) {
         System.out.println("Host address is " + InetAddress.getByAddress(this.address));
      }

   }

   public HostAddress(InetAddress var1) {
      this.addrType = this.getAddrType(var1);
      this.address = var1.getAddress();
   }

   public HostAddress(DerValue var1) throws Asn1Exception, IOException {
      DerValue var2 = var1.getData().getDerValue();
      if ((var2.getTag() & 31) == 0) {
         this.addrType = var2.getData().getBigInteger().intValue();
         var2 = var1.getData().getDerValue();
         if ((var2.getTag() & 31) == 1) {
            this.address = var2.getData().getOctetString();
            if (var1.getData().available() > 0) {
               throw new Asn1Exception(906);
            }
         } else {
            throw new Asn1Exception(906);
         }
      } else {
         throw new Asn1Exception(906);
      }
   }

   public byte[] asn1Encode() throws Asn1Exception, IOException {
      DerOutputStream var1 = new DerOutputStream();
      DerOutputStream var2 = new DerOutputStream();
      var2.putInteger(this.addrType);
      var1.write(DerValue.createTag((byte)-128, true, (byte)0), var2);
      var2 = new DerOutputStream();
      var2.putOctetString(this.address);
      var1.write(DerValue.createTag((byte)-128, true, (byte)1), var2);
      var2 = new DerOutputStream();
      var2.write((byte)48, (DerOutputStream)var1);
      return var2.toByteArray();
   }

   public static HostAddress parse(DerInputStream var0, byte var1, boolean var2) throws Asn1Exception, IOException {
      if (var2 && ((byte)var0.peekByte() & 31) != var1) {
         return null;
      } else {
         DerValue var3 = var0.getDerValue();
         if (var1 != (var3.getTag() & 31)) {
            throw new Asn1Exception(906);
         } else {
            DerValue var4 = var3.getData().getDerValue();
            return new HostAddress(var4);
         }
      }
   }

   static {
      DEBUG = Krb5.DEBUG;
   }
}
