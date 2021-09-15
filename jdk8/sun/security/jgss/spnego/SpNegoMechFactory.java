package sun.security.jgss.spnego;

import java.security.Provider;
import java.util.Vector;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import sun.security.jgss.GSSCaller;
import sun.security.jgss.GSSManagerImpl;
import sun.security.jgss.GSSUtil;
import sun.security.jgss.ProviderList;
import sun.security.jgss.SunProvider;
import sun.security.jgss.krb5.Krb5AcceptCredential;
import sun.security.jgss.krb5.Krb5InitCredential;
import sun.security.jgss.krb5.Krb5MechFactory;
import sun.security.jgss.krb5.Krb5NameElement;
import sun.security.jgss.spi.GSSContextSpi;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.jgss.spi.MechanismFactory;

public final class SpNegoMechFactory implements MechanismFactory {
   static final Provider PROVIDER = new SunProvider();
   static final Oid GSS_SPNEGO_MECH_OID = GSSUtil.createOid("1.3.6.1.5.5.2");
   private static Oid[] nameTypes;
   private static final Oid DEFAULT_SPNEGO_MECH_OID;
   final GSSManagerImpl manager;
   final Oid[] availableMechs;

   private static SpNegoCredElement getCredFromSubject(GSSNameSpi var0, boolean var1) throws GSSException {
      Vector var2 = GSSUtil.searchSubject(var0, GSS_SPNEGO_MECH_OID, var1, SpNegoCredElement.class);
      SpNegoCredElement var3 = var2 != null && !var2.isEmpty() ? (SpNegoCredElement)var2.firstElement() : null;
      if (var3 != null) {
         GSSCredentialSpi var4 = var3.getInternalCred();
         if (GSSUtil.isKerberosMech(var4.getMechanism())) {
            if (var1) {
               Krb5InitCredential var5 = (Krb5InitCredential)var4;
               Krb5MechFactory.checkInitCredPermission((Krb5NameElement)var5.getName());
            } else {
               Krb5AcceptCredential var6 = (Krb5AcceptCredential)var4;
               Krb5MechFactory.checkAcceptCredPermission((Krb5NameElement)var6.getName(), var0);
            }
         }
      }

      return var3;
   }

   public SpNegoMechFactory(GSSCaller var1) {
      this.manager = new GSSManagerImpl(var1, false);
      Oid[] var2 = this.manager.getMechs();
      this.availableMechs = new Oid[var2.length - 1];
      int var3 = 0;

      for(int var4 = 0; var3 < var2.length; ++var3) {
         if (!var2[var3].equals(GSS_SPNEGO_MECH_OID)) {
            this.availableMechs[var4++] = var2[var3];
         }
      }

      for(var3 = 0; var3 < this.availableMechs.length; ++var3) {
         if (this.availableMechs[var3].equals(DEFAULT_SPNEGO_MECH_OID)) {
            if (var3 != 0) {
               this.availableMechs[var3] = this.availableMechs[0];
               this.availableMechs[0] = DEFAULT_SPNEGO_MECH_OID;
            }
            break;
         }
      }

   }

   public GSSNameSpi getNameElement(String var1, Oid var2) throws GSSException {
      return this.manager.getNameElement(var1, var2, DEFAULT_SPNEGO_MECH_OID);
   }

   public GSSNameSpi getNameElement(byte[] var1, Oid var2) throws GSSException {
      return this.manager.getNameElement(var1, var2, DEFAULT_SPNEGO_MECH_OID);
   }

   public GSSCredentialSpi getCredentialElement(GSSNameSpi var1, int var2, int var3, int var4) throws GSSException {
      SpNegoCredElement var5 = getCredFromSubject(var1, var4 != 2);
      if (var5 == null) {
         var5 = new SpNegoCredElement(this.manager.getCredentialElement(var1, var2, var3, (Oid)null, var4));
      }

      return var5;
   }

   public GSSContextSpi getMechanismContext(GSSNameSpi var1, GSSCredentialSpi var2, int var3) throws GSSException {
      if (var2 == null) {
         var2 = getCredFromSubject((GSSNameSpi)null, true);
      } else if (!(var2 instanceof SpNegoCredElement)) {
         SpNegoCredElement var4 = new SpNegoCredElement((GSSCredentialSpi)var2);
         return new SpNegoContext(this, var1, var4, var3);
      }

      return new SpNegoContext(this, var1, (GSSCredentialSpi)var2, var3);
   }

   public GSSContextSpi getMechanismContext(GSSCredentialSpi var1) throws GSSException {
      if (var1 == null) {
         var1 = getCredFromSubject((GSSNameSpi)null, false);
      } else if (!(var1 instanceof SpNegoCredElement)) {
         SpNegoCredElement var2 = new SpNegoCredElement((GSSCredentialSpi)var1);
         return new SpNegoContext(this, var2);
      }

      return new SpNegoContext(this, (GSSCredentialSpi)var1);
   }

   public GSSContextSpi getMechanismContext(byte[] var1) throws GSSException {
      return new SpNegoContext(this, var1);
   }

   public final Oid getMechanismOid() {
      return GSS_SPNEGO_MECH_OID;
   }

   public Provider getProvider() {
      return PROVIDER;
   }

   public Oid[] getNameTypes() {
      return nameTypes;
   }

   static {
      nameTypes = new Oid[]{GSSName.NT_USER_NAME, GSSName.NT_HOSTBASED_SERVICE, GSSName.NT_EXPORT_NAME};
      DEFAULT_SPNEGO_MECH_OID = ProviderList.DEFAULT_MECH_OID.equals(GSS_SPNEGO_MECH_OID) ? GSSUtil.GSS_KRB5_MECH_OID : ProviderList.DEFAULT_MECH_OID;
   }
}
