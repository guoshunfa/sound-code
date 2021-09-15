package sun.security.krb5.internal;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Vector;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.internal.ccache.CCacheOutputStream;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class HostAddresses implements Cloneable {
   private static boolean DEBUG;
   private HostAddress[] addresses = null;
   private volatile int hashCode = 0;

   public HostAddresses(HostAddress[] var1) throws IOException {
      if (var1 != null) {
         this.addresses = new HostAddress[var1.length];

         for(int var2 = 0; var2 < var1.length; ++var2) {
            if (var1[var2] == null) {
               throw new IOException("Cannot create a HostAddress");
            }

            this.addresses[var2] = (HostAddress)var1[var2].clone();
         }
      }

   }

   public HostAddresses() throws UnknownHostException {
      this.addresses = new HostAddress[1];
      this.addresses[0] = new HostAddress();
   }

   private HostAddresses(int var1) {
   }

   public HostAddresses(PrincipalName var1) throws UnknownHostException, KrbException {
      String[] var2 = var1.getNameStrings();
      if (var1.getNameType() == 3 && var2.length >= 2) {
         String var3 = var2[1];
         InetAddress[] var4 = InetAddress.getAllByName(var3);
         HostAddress[] var5 = new HostAddress[var4.length];

         for(int var6 = 0; var6 < var4.length; ++var6) {
            var5[var6] = new HostAddress(var4[var6]);
         }

         this.addresses = var5;
      } else {
         throw new KrbException(60, "Bad name");
      }
   }

   public Object clone() {
      HostAddresses var1 = new HostAddresses(0);
      if (this.addresses != null) {
         var1.addresses = new HostAddress[this.addresses.length];

         for(int var2 = 0; var2 < this.addresses.length; ++var2) {
            var1.addresses[var2] = (HostAddress)this.addresses[var2].clone();
         }
      }

      return var1;
   }

   public boolean inList(HostAddress var1) {
      if (this.addresses != null) {
         for(int var2 = 0; var2 < this.addresses.length; ++var2) {
            if (this.addresses[var2].equals(var1)) {
               return true;
            }
         }
      }

      return false;
   }

   public int hashCode() {
      if (this.hashCode == 0) {
         int var1 = 17;
         if (this.addresses != null) {
            for(int var2 = 0; var2 < this.addresses.length; ++var2) {
               var1 = 37 * var1 + this.addresses[var2].hashCode();
            }
         }

         this.hashCode = var1;
      }

      return this.hashCode;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof HostAddresses)) {
         return false;
      } else {
         HostAddresses var2 = (HostAddresses)var1;
         if (this.addresses == null && var2.addresses != null || this.addresses != null && var2.addresses == null) {
            return false;
         } else {
            if (this.addresses != null && var2.addresses != null) {
               if (this.addresses.length != var2.addresses.length) {
                  return false;
               }

               for(int var3 = 0; var3 < this.addresses.length; ++var3) {
                  if (!this.addresses[var3].equals(var2.addresses[var3])) {
                     return false;
                  }
               }
            }

            return true;
         }
      }
   }

   public HostAddresses(DerValue var1) throws Asn1Exception, IOException {
      Vector var2 = new Vector();
      DerValue var3 = null;

      while(var1.getData().available() > 0) {
         var3 = var1.getData().getDerValue();
         var2.addElement(new HostAddress(var3));
      }

      if (var2.size() > 0) {
         this.addresses = new HostAddress[var2.size()];
         var2.copyInto(this.addresses);
      }

   }

   public byte[] asn1Encode() throws Asn1Exception, IOException {
      DerOutputStream var1 = new DerOutputStream();
      DerOutputStream var2 = new DerOutputStream();
      if (this.addresses != null && this.addresses.length > 0) {
         for(int var3 = 0; var3 < this.addresses.length; ++var3) {
            var1.write(this.addresses[var3].asn1Encode());
         }
      }

      var2.write((byte)48, (DerOutputStream)var1);
      return var2.toByteArray();
   }

   public static HostAddresses parse(DerInputStream var0, byte var1, boolean var2) throws Asn1Exception, IOException {
      if (var2 && ((byte)var0.peekByte() & 31) != var1) {
         return null;
      } else {
         DerValue var3 = var0.getDerValue();
         if (var1 != (var3.getTag() & 31)) {
            throw new Asn1Exception(906);
         } else {
            DerValue var4 = var3.getData().getDerValue();
            return new HostAddresses(var4);
         }
      }
   }

   public void writeAddrs(CCacheOutputStream var1) throws IOException {
      var1.write32(this.addresses.length);

      for(int var2 = 0; var2 < this.addresses.length; ++var2) {
         var1.write16(this.addresses[var2].addrType);
         var1.write32(this.addresses[var2].address.length);
         var1.write(this.addresses[var2].address, 0, this.addresses[var2].address.length);
      }

   }

   public InetAddress[] getInetAddresses() {
      if (this.addresses != null && this.addresses.length != 0) {
         ArrayList var1 = new ArrayList(this.addresses.length);

         for(int var2 = 0; var2 < this.addresses.length; ++var2) {
            try {
               if (this.addresses[var2].addrType == 2 || this.addresses[var2].addrType == 24) {
                  var1.add(this.addresses[var2].getInetAddress());
               }
            } catch (UnknownHostException var4) {
               return null;
            }
         }

         InetAddress[] var5 = new InetAddress[var1.size()];
         return (InetAddress[])var1.toArray(var5);
      } else {
         return null;
      }
   }

   public static HostAddresses getLocalAddresses() throws IOException {
      String var0 = null;
      InetAddress[] var1 = null;

      try {
         InetAddress var2 = InetAddress.getLocalHost();
         var0 = var2.getHostName();
         var1 = InetAddress.getAllByName(var0);
         HostAddress[] var3 = new HostAddress[var1.length];

         int var4;
         for(var4 = 0; var4 < var1.length; ++var4) {
            var3[var4] = new HostAddress(var1[var4]);
         }

         if (DEBUG) {
            System.out.println(">>> KrbKdcReq local addresses for " + var0 + " are: ");

            for(var4 = 0; var4 < var1.length; ++var4) {
               System.out.println("\n\t" + var1[var4]);
               if (var1[var4] instanceof Inet4Address) {
                  System.out.println("IPv4 address");
               }

               if (var1[var4] instanceof Inet6Address) {
                  System.out.println("IPv6 address");
               }
            }
         }

         return new HostAddresses(var3);
      } catch (Exception var5) {
         throw new IOException(var5.toString());
      }
   }

   public HostAddresses(InetAddress[] var1) {
      if (var1 == null) {
         this.addresses = null;
      } else {
         this.addresses = new HostAddress[var1.length];

         for(int var2 = 0; var2 < var1.length; ++var2) {
            this.addresses[var2] = new HostAddress(var1[var2]);
         }

      }
   }

   static {
      DEBUG = Krb5.DEBUG;
   }
}
