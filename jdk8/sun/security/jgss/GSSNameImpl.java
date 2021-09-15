package sun.security.jgss;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.ObjectIdentifier;

public class GSSNameImpl implements GSSName {
   static final Oid oldHostbasedServiceName;
   private GSSManagerImpl gssManager;
   private String appNameStr;
   private byte[] appNameBytes;
   private Oid appNameType;
   private String printableName;
   private Oid printableNameType;
   private HashMap<Oid, GSSNameSpi> elements;
   private GSSNameSpi mechElement;

   static GSSNameImpl wrapElement(GSSManagerImpl var0, GSSNameSpi var1) throws GSSException {
      return var1 == null ? null : new GSSNameImpl(var0, var1);
   }

   GSSNameImpl(GSSManagerImpl var1, GSSNameSpi var2) {
      this.gssManager = null;
      this.appNameStr = null;
      this.appNameBytes = null;
      this.appNameType = null;
      this.printableName = null;
      this.printableNameType = null;
      this.elements = null;
      this.mechElement = null;
      this.gssManager = var1;
      this.appNameStr = this.printableName = var2.toString();
      this.appNameType = this.printableNameType = var2.getStringNameType();
      this.mechElement = var2;
      this.elements = new HashMap(1);
      this.elements.put(var2.getMechanism(), this.mechElement);
   }

   GSSNameImpl(GSSManagerImpl var1, Object var2, Oid var3) throws GSSException {
      this(var1, var2, var3, (Oid)null);
   }

   GSSNameImpl(GSSManagerImpl var1, Object var2, Oid var3, Oid var4) throws GSSException {
      this.gssManager = null;
      this.appNameStr = null;
      this.appNameBytes = null;
      this.appNameType = null;
      this.printableName = null;
      this.printableNameType = null;
      this.elements = null;
      this.mechElement = null;
      if (oldHostbasedServiceName.equals(var3)) {
         var3 = GSSName.NT_HOSTBASED_SERVICE;
      }

      if (var2 == null) {
         throw new GSSExceptionImpl(3, "Cannot import null name");
      } else {
         if (var4 == null) {
            var4 = ProviderList.DEFAULT_MECH_OID;
         }

         if (NT_EXPORT_NAME.equals(var3)) {
            this.importName(var1, var2);
         } else {
            this.init(var1, var2, var3, var4);
         }

      }
   }

   private void init(GSSManagerImpl var1, Object var2, Oid var3, Oid var4) throws GSSException {
      this.gssManager = var1;
      this.elements = new HashMap(var1.getMechs().length);
      if (var2 instanceof String) {
         this.appNameStr = (String)var2;
         if (var3 != null) {
            this.printableName = this.appNameStr;
            this.printableNameType = var3;
         }
      } else {
         this.appNameBytes = (byte[])((byte[])var2);
      }

      this.appNameType = var3;
      this.mechElement = this.getElement(var4);
      if (this.printableName == null) {
         this.printableName = this.mechElement.toString();
         this.printableNameType = this.mechElement.getStringNameType();
      }

   }

   private void importName(GSSManagerImpl var1, Object var2) throws GSSException {
      byte var3 = 0;
      byte[] var4 = null;
      if (var2 instanceof String) {
         try {
            var4 = ((String)var2).getBytes("UTF-8");
         } catch (UnsupportedEncodingException var11) {
         }
      } else {
         var4 = (byte[])((byte[])var2);
      }

      int var12 = var3 + 1;
      if (var4[var3] == 4 && var4[var12++] == 1) {
         int var5 = (255 & var4[var12++]) << 8 | 255 & var4[var12++];
         ObjectIdentifier var6 = null;

         try {
            DerInputStream var7 = new DerInputStream(var4, var12, var5);
            var6 = new ObjectIdentifier(var7);
         } catch (IOException var10) {
            throw new GSSExceptionImpl(3, "Exported name Object identifier is corrupted!");
         }

         Oid var13 = new Oid(var6.toString());
         var12 += var5;
         int var8 = (255 & var4[var12++]) << 24 | (255 & var4[var12++]) << 16 | (255 & var4[var12++]) << 8 | 255 & var4[var12++];
         if (var8 >= 0 && var12 <= var4.length - var8) {
            byte[] var9 = new byte[var8];
            System.arraycopy(var4, var12, var9, 0, var8);
            this.init(var1, var9, NT_EXPORT_NAME, var13);
         } else {
            throw new GSSExceptionImpl(3, "Exported name mech name is corrupted!");
         }
      } else {
         throw new GSSExceptionImpl(3, "Exported name token id is corrupted!");
      }
   }

   public GSSName canonicalize(Oid var1) throws GSSException {
      if (var1 == null) {
         var1 = ProviderList.DEFAULT_MECH_OID;
      }

      return wrapElement(this.gssManager, this.getElement(var1));
   }

   public boolean equals(GSSName var1) throws GSSException {
      if (!this.isAnonymous() && !var1.isAnonymous()) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof GSSNameImpl)) {
            return this.equals(this.gssManager.createName(var1.toString(), var1.getStringNameType()));
         } else {
            GSSNameImpl var2 = (GSSNameImpl)var1;
            GSSNameSpi var3 = this.mechElement;
            GSSNameSpi var4 = var2.mechElement;
            if (var3 == null && var4 != null) {
               var3 = this.getElement(var4.getMechanism());
            } else if (var3 != null && var4 == null) {
               var4 = var2.getElement(var3.getMechanism());
            }

            if (var3 != null && var4 != null) {
               return var3.equals(var4);
            } else if (this.appNameType != null && var2.appNameType != null) {
               if (!this.appNameType.equals(var2.appNameType)) {
                  return false;
               } else {
                  byte[] var5 = null;
                  byte[] var6 = null;

                  try {
                     var5 = this.appNameStr != null ? this.appNameStr.getBytes("UTF-8") : this.appNameBytes;
                     var6 = var2.appNameStr != null ? var2.appNameStr.getBytes("UTF-8") : var2.appNameBytes;
                  } catch (UnsupportedEncodingException var8) {
                  }

                  return Arrays.equals(var5, var6);
               }
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      return 1;
   }

   public boolean equals(Object var1) {
      try {
         if (var1 instanceof GSSName) {
            return this.equals((GSSName)var1);
         }
      } catch (GSSException var3) {
      }

      return false;
   }

   public byte[] export() throws GSSException {
      if (this.mechElement == null) {
         this.mechElement = this.getElement(ProviderList.DEFAULT_MECH_OID);
      }

      byte[] var1 = this.mechElement.export();
      Object var2 = null;
      ObjectIdentifier var3 = null;

      try {
         var3 = new ObjectIdentifier(this.mechElement.getMechanism().toString());
      } catch (IOException var8) {
         throw new GSSExceptionImpl(11, "Invalid OID String ");
      }

      DerOutputStream var4 = new DerOutputStream();

      try {
         var4.putOID(var3);
      } catch (IOException var7) {
         throw new GSSExceptionImpl(11, "Could not ASN.1 Encode " + var3.toString());
      }

      byte[] var9 = var4.toByteArray();
      byte[] var5 = new byte[4 + var9.length + 4 + var1.length];
      byte var6 = 0;
      int var10 = var6 + 1;
      var5[var6] = 4;
      var5[var10++] = 1;
      var5[var10++] = (byte)(var9.length >>> 8);
      var5[var10++] = (byte)var9.length;
      System.arraycopy(var9, 0, var5, var10, var9.length);
      var10 += var9.length;
      var5[var10++] = (byte)(var1.length >>> 24);
      var5[var10++] = (byte)(var1.length >>> 16);
      var5[var10++] = (byte)(var1.length >>> 8);
      var5[var10++] = (byte)var1.length;
      System.arraycopy(var1, 0, var5, var10, var1.length);
      return var5;
   }

   public String toString() {
      return this.printableName;
   }

   public Oid getStringNameType() throws GSSException {
      return this.printableNameType;
   }

   public boolean isAnonymous() {
      return this.printableNameType == null ? false : GSSName.NT_ANONYMOUS.equals(this.printableNameType);
   }

   public boolean isMN() {
      return true;
   }

   public synchronized GSSNameSpi getElement(Oid var1) throws GSSException {
      GSSNameSpi var2 = (GSSNameSpi)this.elements.get(var1);
      if (var2 == null) {
         if (this.appNameStr != null) {
            var2 = this.gssManager.getNameElement(this.appNameStr, this.appNameType, var1);
         } else {
            var2 = this.gssManager.getNameElement(this.appNameBytes, this.appNameType, var1);
         }

         this.elements.put(var1, var2);
      }

      return var2;
   }

   Set<GSSNameSpi> getElements() {
      return new HashSet(this.elements.values());
   }

   private static String getNameTypeStr(Oid var0) {
      if (var0 == null) {
         return "(NT is null)";
      } else if (var0.equals(NT_USER_NAME)) {
         return "NT_USER_NAME";
      } else if (var0.equals(NT_HOSTBASED_SERVICE)) {
         return "NT_HOSTBASED_SERVICE";
      } else if (var0.equals(NT_EXPORT_NAME)) {
         return "NT_EXPORT_NAME";
      } else {
         return var0.equals(GSSUtil.NT_GSS_KRB5_PRINCIPAL) ? "NT_GSS_KRB5_PRINCIPAL" : "Unknown";
      }
   }

   static {
      Oid var0 = null;

      try {
         var0 = new Oid("1.3.6.1.5.6.2");
      } catch (Exception var2) {
      }

      oldHostbasedServiceName = var0;
   }
}
