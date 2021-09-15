package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;
import sun.security.util.BitArray;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class NetscapeCertTypeExtension extends Extension implements CertAttrSet<String> {
   public static final String IDENT = "x509.info.extensions.NetscapeCertType";
   public static final String NAME = "NetscapeCertType";
   public static final String SSL_CLIENT = "ssl_client";
   public static final String SSL_SERVER = "ssl_server";
   public static final String S_MIME = "s_mime";
   public static final String OBJECT_SIGNING = "object_signing";
   public static final String SSL_CA = "ssl_ca";
   public static final String S_MIME_CA = "s_mime_ca";
   public static final String OBJECT_SIGNING_CA = "object_signing_ca";
   private static final int[] CertType_data = new int[]{2, 16, 840, 1, 113730, 1, 1};
   public static ObjectIdentifier NetscapeCertType_Id;
   private boolean[] bitString;
   private static NetscapeCertTypeExtension.MapEntry[] mMapData;
   private static final Vector<String> mAttributeNames;

   private static int getPosition(String var0) throws IOException {
      for(int var1 = 0; var1 < mMapData.length; ++var1) {
         if (var0.equalsIgnoreCase(mMapData[var1].mName)) {
            return mMapData[var1].mPosition;
         }
      }

      throw new IOException("Attribute name [" + var0 + "] not recognized by CertAttrSet:NetscapeCertType.");
   }

   private void encodeThis() throws IOException {
      DerOutputStream var1 = new DerOutputStream();
      var1.putTruncatedUnalignedBitString(new BitArray(this.bitString));
      this.extensionValue = var1.toByteArray();
   }

   private boolean isSet(int var1) {
      return var1 < this.bitString.length && this.bitString[var1];
   }

   private void set(int var1, boolean var2) {
      if (var1 >= this.bitString.length) {
         boolean[] var3 = new boolean[var1 + 1];
         System.arraycopy(this.bitString, 0, var3, 0, this.bitString.length);
         this.bitString = var3;
      }

      this.bitString[var1] = var2;
   }

   public NetscapeCertTypeExtension(byte[] var1) throws IOException {
      this.bitString = (new BitArray(var1.length * 8, var1)).toBooleanArray();
      this.extensionId = NetscapeCertType_Id;
      this.critical = true;
      this.encodeThis();
   }

   public NetscapeCertTypeExtension(boolean[] var1) throws IOException {
      this.bitString = var1;
      this.extensionId = NetscapeCertType_Id;
      this.critical = true;
      this.encodeThis();
   }

   public NetscapeCertTypeExtension(Boolean var1, Object var2) throws IOException {
      this.extensionId = NetscapeCertType_Id;
      this.critical = var1;
      this.extensionValue = (byte[])((byte[])var2);
      DerValue var3 = new DerValue(this.extensionValue);
      this.bitString = var3.getUnalignedBitString().toBooleanArray();
   }

   public NetscapeCertTypeExtension() {
      this.extensionId = NetscapeCertType_Id;
      this.critical = true;
      this.bitString = new boolean[0];
   }

   public void set(String var1, Object var2) throws IOException {
      if (!(var2 instanceof Boolean)) {
         throw new IOException("Attribute must be of type Boolean.");
      } else {
         boolean var3 = (Boolean)var2;
         this.set(getPosition(var1), var3);
         this.encodeThis();
      }
   }

   public Boolean get(String var1) throws IOException {
      return this.isSet(getPosition(var1));
   }

   public void delete(String var1) throws IOException {
      this.set(getPosition(var1), false);
      this.encodeThis();
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(super.toString());
      var1.append("NetscapeCertType [\n");
      if (this.isSet(0)) {
         var1.append("   SSL client\n");
      }

      if (this.isSet(1)) {
         var1.append("   SSL server\n");
      }

      if (this.isSet(2)) {
         var1.append("   S/MIME\n");
      }

      if (this.isSet(3)) {
         var1.append("   Object Signing\n");
      }

      if (this.isSet(5)) {
         var1.append("   SSL CA\n");
      }

      if (this.isSet(6)) {
         var1.append("   S/MIME CA\n");
      }

      if (this.isSet(7)) {
         var1.append("   Object Signing CA");
      }

      var1.append("]\n");
      return var1.toString();
   }

   public void encode(OutputStream var1) throws IOException {
      DerOutputStream var2 = new DerOutputStream();
      if (this.extensionValue == null) {
         this.extensionId = NetscapeCertType_Id;
         this.critical = true;
         this.encodeThis();
      }

      super.encode(var2);
      var1.write(var2.toByteArray());
   }

   public Enumeration<String> getElements() {
      return mAttributeNames.elements();
   }

   public String getName() {
      return "NetscapeCertType";
   }

   public boolean[] getKeyUsageMappedBits() {
      KeyUsageExtension var1 = new KeyUsageExtension();
      Boolean var2 = Boolean.TRUE;

      try {
         if (this.isSet(getPosition("ssl_client")) || this.isSet(getPosition("s_mime")) || this.isSet(getPosition("object_signing"))) {
            var1.set("digital_signature", var2);
         }

         if (this.isSet(getPosition("ssl_server"))) {
            var1.set("key_encipherment", var2);
         }

         if (this.isSet(getPosition("ssl_ca")) || this.isSet(getPosition("s_mime_ca")) || this.isSet(getPosition("object_signing_ca"))) {
            var1.set("key_certsign", var2);
         }
      } catch (IOException var4) {
      }

      return var1.getBits();
   }

   static {
      try {
         NetscapeCertType_Id = new ObjectIdentifier(CertType_data);
      } catch (IOException var4) {
      }

      mMapData = new NetscapeCertTypeExtension.MapEntry[]{new NetscapeCertTypeExtension.MapEntry("ssl_client", 0), new NetscapeCertTypeExtension.MapEntry("ssl_server", 1), new NetscapeCertTypeExtension.MapEntry("s_mime", 2), new NetscapeCertTypeExtension.MapEntry("object_signing", 3), new NetscapeCertTypeExtension.MapEntry("ssl_ca", 5), new NetscapeCertTypeExtension.MapEntry("s_mime_ca", 6), new NetscapeCertTypeExtension.MapEntry("object_signing_ca", 7)};
      mAttributeNames = new Vector();
      NetscapeCertTypeExtension.MapEntry[] var0 = mMapData;
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         NetscapeCertTypeExtension.MapEntry var3 = var0[var2];
         mAttributeNames.add(var3.mName);
      }

   }

   private static class MapEntry {
      String mName;
      int mPosition;

      MapEntry(String var1, int var2) {
         this.mName = var1;
         this.mPosition = var2;
      }
   }
}
