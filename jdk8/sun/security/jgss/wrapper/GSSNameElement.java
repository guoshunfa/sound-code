package sun.security.jgss.wrapper;

import java.io.IOException;
import java.security.Provider;
import javax.security.auth.kerberos.ServicePermission;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import sun.security.jgss.GSSExceptionImpl;
import sun.security.jgss.GSSUtil;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.krb5.Realm;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.ObjectIdentifier;

public class GSSNameElement implements GSSNameSpi {
   long pName = 0L;
   private String printableName;
   private Oid printableType;
   private GSSLibStub cStub;
   static final GSSNameElement DEF_ACCEPTOR = new GSSNameElement();

   private static Oid getNativeNameType(Oid var0, GSSLibStub var1) {
      if (GSSUtil.NT_GSS_KRB5_PRINCIPAL.equals(var0)) {
         Oid[] var2 = null;

         try {
            var2 = var1.inquireNamesForMech();
         } catch (GSSException var6) {
            if (var6.getMajor() == 2 && GSSUtil.isSpNegoMech(var1.getMech())) {
               try {
                  var1 = GSSLibStub.getInstance(GSSUtil.GSS_KRB5_MECH_OID);
                  var2 = var1.inquireNamesForMech();
               } catch (GSSException var5) {
                  SunNativeProvider.debug("Name type list unavailable: " + var5.getMajorString());
               }
            } else {
               SunNativeProvider.debug("Name type list unavailable: " + var6.getMajorString());
            }
         }

         if (var2 != null) {
            for(int var3 = 0; var3 < var2.length; ++var3) {
               if (var2[var3].equals(var0)) {
                  return var0;
               }
            }

            SunNativeProvider.debug("Override " + var0 + " with mechanism default(null)");
            return null;
         }
      }

      return var0;
   }

   private GSSNameElement() {
      this.printableName = "<DEFAULT ACCEPTOR>";
   }

   GSSNameElement(long var1, GSSLibStub var3) throws GSSException {
      assert var3 != null;

      if (var1 == 0L) {
         throw new GSSException(3);
      } else {
         this.pName = var1;
         this.cStub = var3;
         this.setPrintables();
      }
   }

   GSSNameElement(byte[] var1, Oid var2, GSSLibStub var3) throws GSSException {
      assert var3 != null;

      if (var1 == null) {
         throw new GSSException(3);
      } else {
         this.cStub = var3;
         byte[] var4 = var1;
         SecurityManager var5;
         if (var2 != null) {
            var2 = getNativeNameType(var2, var3);
            if (GSSName.NT_EXPORT_NAME.equals(var2)) {
               var5 = null;
               DerOutputStream var6 = new DerOutputStream();
               Oid var7 = this.cStub.getMech();

               try {
                  var6.putOID(new ObjectIdentifier(var7.toString()));
               } catch (IOException var11) {
                  throw new GSSExceptionImpl(11, var11);
               }

               byte[] var12 = var6.toByteArray();
               var4 = new byte[4 + var12.length + 4 + var1.length];
               byte var8 = 0;
               int var15 = var8 + 1;
               var4[var8] = 4;
               var4[var15++] = 1;
               var4[var15++] = (byte)(var12.length >>> 8);
               var4[var15++] = (byte)var12.length;
               System.arraycopy(var12, 0, var4, var15, var12.length);
               var15 += var12.length;
               var4[var15++] = (byte)(var1.length >>> 24);
               var4[var15++] = (byte)(var1.length >>> 16);
               var4[var15++] = (byte)(var1.length >>> 8);
               var4[var15++] = (byte)var1.length;
               System.arraycopy(var1, 0, var4, var15, var1.length);
            }
         }

         this.pName = this.cStub.importName(var4, var2);
         this.setPrintables();
         var5 = System.getSecurityManager();
         if (var5 != null && !Realm.AUTODEDUCEREALM) {
            String var13 = this.getKrbName();
            int var14 = var13.lastIndexOf(64);
            if (var14 != -1) {
               String var16 = var13.substring(var14);
               if (var2 != null && !var2.equals(GSSUtil.NT_GSS_KRB5_PRINCIPAL) || !(new String(var1)).endsWith(var16)) {
                  try {
                     var5.checkPermission(new ServicePermission(var16, "-"));
                  } catch (SecurityException var10) {
                     throw new GSSException(11);
                  }
               }
            }
         }

         SunNativeProvider.debug("Imported " + this.printableName + " w/ type " + this.printableType);
      }
   }

   private void setPrintables() throws GSSException {
      Object[] var1 = null;
      var1 = this.cStub.displayName(this.pName);

      assert var1 != null && var1.length == 2;

      this.printableName = (String)var1[0];

      assert this.printableName != null;

      this.printableType = (Oid)var1[1];
      if (this.printableType == null) {
         this.printableType = GSSName.NT_USER_NAME;
      }

   }

   public String getKrbName() throws GSSException {
      long var1 = 0L;
      GSSLibStub var3 = this.cStub;
      if (!GSSUtil.isKerberosMech(this.cStub.getMech())) {
         var3 = GSSLibStub.getInstance(GSSUtil.GSS_KRB5_MECH_OID);
      }

      var1 = var3.canonicalizeName(this.pName);
      Object[] var4 = var3.displayName(var1);
      var3.releaseName(var1);
      SunNativeProvider.debug("Got kerberized name: " + var4[0]);
      return (String)var4[0];
   }

   public Provider getProvider() {
      return SunNativeProvider.INSTANCE;
   }

   public boolean equals(GSSNameSpi var1) throws GSSException {
      return !(var1 instanceof GSSNameElement) ? false : this.cStub.compareName(this.pName, ((GSSNameElement)var1).pName);
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof GSSNameElement)) {
         return false;
      } else {
         try {
            return this.equals((GSSNameSpi)((GSSNameElement)var1));
         } catch (GSSException var3) {
            return false;
         }
      }
   }

   public int hashCode() {
      return (new Long(this.pName)).hashCode();
   }

   public byte[] export() throws GSSException {
      byte[] var1 = this.cStub.exportName(this.pName);
      byte var2 = 0;
      int var9 = var2 + 1;
      if (var1[var2] == 4 && var1[var9++] == 1) {
         int var3 = (255 & var1[var9++]) << 8 | 255 & var1[var9++];
         ObjectIdentifier var4 = null;

         try {
            DerInputStream var5 = new DerInputStream(var1, var9, var3);
            var4 = new ObjectIdentifier(var5);
         } catch (IOException var8) {
            throw new GSSExceptionImpl(3, var8);
         }

         Oid var10 = new Oid(var4.toString());

         assert var10.equals(this.getMechanism());

         var9 += var3;
         int var6 = (255 & var1[var9++]) << 24 | (255 & var1[var9++]) << 16 | (255 & var1[var9++]) << 8 | 255 & var1[var9++];
         if (var6 < 0) {
            throw new GSSException(3);
         } else {
            byte[] var7 = new byte[var6];
            System.arraycopy(var1, var9, var7, 0, var6);
            return var7;
         }
      } else {
         throw new GSSException(3);
      }
   }

   public Oid getMechanism() {
      return this.cStub.getMech();
   }

   public String toString() {
      return this.printableName;
   }

   public Oid getStringNameType() {
      return this.printableType;
   }

   public boolean isAnonymousName() {
      return GSSName.NT_ANONYMOUS.equals(this.printableType);
   }

   public void dispose() {
      if (this.pName != 0L) {
         this.cStub.releaseName(this.pName);
         this.pName = 0L;
      }

   }

   protected void finalize() throws Throwable {
      this.dispose();
   }
}
