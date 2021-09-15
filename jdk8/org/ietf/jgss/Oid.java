package org.ietf.jgss;

import java.io.IOException;
import java.io.InputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class Oid {
   private ObjectIdentifier oid;
   private byte[] derEncoding;

   public Oid(String var1) throws GSSException {
      try {
         this.oid = new ObjectIdentifier(var1);
         this.derEncoding = null;
      } catch (Exception var3) {
         throw new GSSException(11, "Improperly formatted Object Identifier String - " + var1);
      }
   }

   public Oid(InputStream var1) throws GSSException {
      try {
         DerValue var2 = new DerValue(var1);
         this.derEncoding = var2.toByteArray();
         this.oid = var2.getOID();
      } catch (IOException var3) {
         throw new GSSException(11, "Improperly formatted ASN.1 DER encoding for Oid");
      }
   }

   public Oid(byte[] var1) throws GSSException {
      try {
         DerValue var2 = new DerValue(var1);
         this.derEncoding = var2.toByteArray();
         this.oid = var2.getOID();
      } catch (IOException var3) {
         throw new GSSException(11, "Improperly formatted ASN.1 DER encoding for Oid");
      }
   }

   static Oid getInstance(String var0) {
      Oid var1 = null;

      try {
         var1 = new Oid(var0);
      } catch (GSSException var3) {
      }

      return var1;
   }

   public String toString() {
      return this.oid.toString();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof Oid) {
         return this.oid.equals((Object)((Oid)var1).oid);
      } else {
         return var1 instanceof ObjectIdentifier ? this.oid.equals(var1) : false;
      }
   }

   public byte[] getDER() throws GSSException {
      if (this.derEncoding == null) {
         DerOutputStream var1 = new DerOutputStream();

         try {
            var1.putOID(this.oid);
         } catch (IOException var3) {
            throw new GSSException(11, var3.getMessage());
         }

         this.derEncoding = var1.toByteArray();
      }

      return (byte[])this.derEncoding.clone();
   }

   public boolean containedIn(Oid[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         if (var1[var2].equals(this)) {
            return true;
         }
      }

      return false;
   }

   public int hashCode() {
      return this.oid.hashCode();
   }
}
