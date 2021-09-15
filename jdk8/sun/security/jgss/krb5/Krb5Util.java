package sun.security.jgss.krb5;

import java.io.IOException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.login.LoginException;
import sun.security.action.GetBooleanAction;
import sun.security.jgss.GSSCaller;
import sun.security.jgss.GSSUtil;
import sun.security.krb5.Credentials;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KerberosSecrets;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.internal.ktab.KeyTab;

public class Krb5Util {
   static final boolean DEBUG = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("sun.security.krb5.debug")));

   private Krb5Util() {
   }

   public static KerberosTicket getTicketFromSubjectAndTgs(GSSCaller var0, String var1, String var2, String var3, AccessControlContext var4) throws LoginException, KrbException, IOException {
      Subject var5 = Subject.getSubject(var4);
      KerberosTicket var6 = (KerberosTicket)SubjectComber.find(var5, var2, var1, KerberosTicket.class);
      if (var6 != null) {
         return var6;
      } else {
         Subject var7 = null;
         if (!GSSUtil.useSubjectCredsOnly(var0)) {
            try {
               var7 = GSSUtil.login(var0, GSSUtil.GSS_KRB5_MECH_OID);
               var6 = (KerberosTicket)SubjectComber.find(var7, var2, var1, KerberosTicket.class);
               if (var6 != null) {
                  return var6;
               }
            } catch (LoginException var12) {
            }
         }

         KerberosTicket var8 = (KerberosTicket)SubjectComber.find(var5, var3, var1, KerberosTicket.class);
         boolean var9;
         if (var8 == null && var7 != null) {
            var8 = (KerberosTicket)SubjectComber.find(var7, var3, var1, KerberosTicket.class);
            var9 = false;
         } else {
            var9 = true;
         }

         if (var8 != null) {
            Credentials var10 = ticketToCreds(var8);
            Credentials var11 = Credentials.acquireServiceCreds(var2, var10);
            if (var11 != null) {
               var6 = credsToTicket(var11);
               if (var9 && var5 != null && !var5.isReadOnly()) {
                  var5.getPrivateCredentials().add(var6);
               }
            }
         }

         return var6;
      }
   }

   static KerberosTicket getTicket(GSSCaller var0, String var1, String var2, AccessControlContext var3) throws LoginException {
      Subject var4 = Subject.getSubject(var3);
      KerberosTicket var5 = (KerberosTicket)SubjectComber.find(var4, var2, var1, KerberosTicket.class);
      if (var5 == null && !GSSUtil.useSubjectCredsOnly(var0)) {
         Subject var6 = GSSUtil.login(var0, GSSUtil.GSS_KRB5_MECH_OID);
         var5 = (KerberosTicket)SubjectComber.find(var6, var2, var1, KerberosTicket.class);
      }

      return var5;
   }

   public static Subject getSubject(GSSCaller var0, AccessControlContext var1) throws LoginException {
      Subject var2 = Subject.getSubject(var1);
      if (var2 == null && !GSSUtil.useSubjectCredsOnly(var0)) {
         var2 = GSSUtil.login(var0, GSSUtil.GSS_KRB5_MECH_OID);
      }

      return var2;
   }

   public static ServiceCreds getServiceCreds(GSSCaller var0, String var1, AccessControlContext var2) throws LoginException {
      Subject var3 = Subject.getSubject(var2);
      ServiceCreds var4 = null;
      if (var3 != null) {
         var4 = ServiceCreds.getInstance(var3, var1);
      }

      if (var4 == null && !GSSUtil.useSubjectCredsOnly(var0)) {
         Subject var5 = GSSUtil.login(var0, GSSUtil.GSS_KRB5_MECH_OID);
         var4 = ServiceCreds.getInstance(var5, var1);
      }

      return var4;
   }

   public static KerberosTicket credsToTicket(Credentials var0) {
      EncryptionKey var1 = var0.getSessionKey();
      return new KerberosTicket(var0.getEncoded(), new KerberosPrincipal(var0.getClient().getName()), new KerberosPrincipal(var0.getServer().getName(), 2), var1.getBytes(), var1.getEType(), var0.getFlags(), var0.getAuthTime(), var0.getStartTime(), var0.getEndTime(), var0.getRenewTill(), var0.getClientAddresses());
   }

   public static Credentials ticketToCreds(KerberosTicket var0) throws KrbException, IOException {
      return new Credentials(var0.getEncoded(), var0.getClient().getName(), var0.getServer().getName(), var0.getSessionKey().getEncoded(), var0.getSessionKeyType(), var0.getFlags(), var0.getAuthTime(), var0.getStartTime(), var0.getEndTime(), var0.getRenewTill(), var0.getClientAddresses());
   }

   public static KeyTab snapshotFromJavaxKeyTab(javax.security.auth.kerberos.KeyTab var0) {
      return KerberosSecrets.getJavaxSecurityAuthKerberosAccess().keyTabTakeSnapshot(var0);
   }

   public static EncryptionKey[] keysFromJavaxKeyTab(javax.security.auth.kerberos.KeyTab var0, PrincipalName var1) {
      return snapshotFromJavaxKeyTab(var0).readServiceKeys(var1);
   }
}
