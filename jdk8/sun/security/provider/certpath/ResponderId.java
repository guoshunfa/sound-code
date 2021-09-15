package sun.security.provider.certpath;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Arrays;
import javax.security.auth.x500.X500Principal;
import sun.security.util.DerValue;
import sun.security.x509.KeyIdentifier;

public final class ResponderId {
   private ResponderId.Type type;
   private X500Principal responderName;
   private KeyIdentifier responderKeyId;
   private byte[] encodedRid;

   public ResponderId(X500Principal var1) throws IOException {
      this.responderName = var1;
      this.responderKeyId = null;
      this.encodedRid = this.principalToBytes();
      this.type = ResponderId.Type.BY_NAME;
   }

   public ResponderId(PublicKey var1) throws IOException {
      this.responderKeyId = new KeyIdentifier(var1);
      this.responderName = null;
      this.encodedRid = this.keyIdToBytes();
      this.type = ResponderId.Type.BY_KEY;
   }

   public ResponderId(byte[] var1) throws IOException {
      DerValue var2 = new DerValue(var1);
      if (var2.isContextSpecific((byte)ResponderId.Type.BY_NAME.value()) && var2.isConstructed()) {
         this.responderName = new X500Principal(var2.getDataBytes());
         this.encodedRid = this.principalToBytes();
         this.type = ResponderId.Type.BY_NAME;
      } else {
         if (!var2.isContextSpecific((byte)ResponderId.Type.BY_KEY.value()) || !var2.isConstructed()) {
            throw new IOException("Invalid ResponderId content");
         }

         this.responderKeyId = new KeyIdentifier(new DerValue(var2.getDataBytes()));
         this.encodedRid = this.keyIdToBytes();
         this.type = ResponderId.Type.BY_KEY;
      }

   }

   public byte[] getEncoded() {
      return (byte[])this.encodedRid.clone();
   }

   public ResponderId.Type getType() {
      return this.type;
   }

   public int length() {
      return this.encodedRid.length;
   }

   public X500Principal getResponderName() {
      return this.responderName;
   }

   public KeyIdentifier getKeyIdentifier() {
      return this.responderKeyId;
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (this == var1) {
         return true;
      } else if (var1 instanceof ResponderId) {
         ResponderId var2 = (ResponderId)var1;
         return Arrays.equals(this.encodedRid, var2.getEncoded());
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.encodedRid);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      switch(this.type) {
      case BY_NAME:
         var1.append((Object)this.type).append(": ").append((Object)this.responderName);
         break;
      case BY_KEY:
         var1.append((Object)this.type).append(": ");
         byte[] var2 = this.responderKeyId.getIdentifier();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            byte var5 = var2[var4];
            var1.append(String.format("%02X", var5));
         }

         return var1.toString();
      default:
         var1.append("Unknown ResponderId Type: ").append((Object)this.type);
      }

      return var1.toString();
   }

   private byte[] principalToBytes() throws IOException {
      DerValue var1 = new DerValue(DerValue.createTag((byte)-128, true, (byte)ResponderId.Type.BY_NAME.value()), this.responderName.getEncoded());
      return var1.toByteArray();
   }

   private byte[] keyIdToBytes() throws IOException {
      DerValue var1 = new DerValue((byte)4, this.responderKeyId.getIdentifier());
      DerValue var2 = new DerValue(DerValue.createTag((byte)-128, true, (byte)ResponderId.Type.BY_KEY.value()), var1.toByteArray());
      return var2.toByteArray();
   }

   public static enum Type {
      BY_NAME(1, "byName"),
      BY_KEY(2, "byKey");

      private final int tagNumber;
      private final String ridTypeName;

      private Type(int var3, String var4) {
         this.tagNumber = var3;
         this.ridTypeName = var4;
      }

      public int value() {
         return this.tagNumber;
      }

      public String toString() {
         return this.ridTypeName;
      }
   }
}
