package sun.security.jgss.krb5;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosKey;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.kerberos.KeyTab;
import sun.security.krb5.Credentials;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;

public final class ServiceCreds {
   private KerberosPrincipal kp;
   private Set<KerberosPrincipal> allPrincs;
   private List<KeyTab> ktabs;
   private List<KerberosKey> kk;
   private KerberosTicket tgt;
   private boolean destroyed;

   private ServiceCreds() {
   }

   public static ServiceCreds getInstance(Subject var0, String var1) {
      ServiceCreds var2 = new ServiceCreds();
      var2.allPrincs = var0.getPrincipals(KerberosPrincipal.class);
      Iterator var3 = SubjectComber.findMany(var0, var1, (String)null, KerberosKey.class).iterator();

      while(var3.hasNext()) {
         KerberosKey var4 = (KerberosKey)var3.next();
         var2.allPrincs.add(var4.getPrincipal());
      }

      if (var1 != null) {
         var2.kp = new KerberosPrincipal(var1);
      } else if (var2.allPrincs.size() == 1) {
         boolean var6 = false;
         Iterator var7 = SubjectComber.findMany(var0, (String)null, (String)null, KeyTab.class).iterator();

         while(var7.hasNext()) {
            KeyTab var5 = (KeyTab)var7.next();
            if (!var5.isBound()) {
               var6 = true;
               break;
            }
         }

         if (!var6) {
            var2.kp = (KerberosPrincipal)var2.allPrincs.iterator().next();
            var1 = var2.kp.getName();
         }
      }

      var2.ktabs = SubjectComber.findMany(var0, var1, (String)null, KeyTab.class);
      var2.kk = SubjectComber.findMany(var0, var1, (String)null, KerberosKey.class);
      var2.tgt = (KerberosTicket)SubjectComber.find(var0, (String)null, var1, KerberosTicket.class);
      if (var2.ktabs.isEmpty() && var2.kk.isEmpty() && var2.tgt == null) {
         return null;
      } else {
         var2.destroyed = false;
         return var2;
      }
   }

   public String getName() {
      if (this.destroyed) {
         throw new IllegalStateException("This object is destroyed");
      } else {
         return this.kp == null ? null : this.kp.getName();
      }
   }

   public KerberosKey[] getKKeys() {
      if (this.destroyed) {
         throw new IllegalStateException("This object is destroyed");
      } else {
         KerberosPrincipal var1 = this.kp;
         if (var1 == null && !this.allPrincs.isEmpty()) {
            var1 = (KerberosPrincipal)this.allPrincs.iterator().next();
         }

         if (var1 == null) {
            Iterator var2 = this.ktabs.iterator();

            while(var2.hasNext()) {
               KeyTab var3 = (KeyTab)var2.next();
               PrincipalName var4 = Krb5Util.snapshotFromJavaxKeyTab(var3).getOneName();
               if (var4 != null) {
                  var1 = new KerberosPrincipal(var4.getName());
                  break;
               }
            }
         }

         return var1 != null ? this.getKKeys(var1) : new KerberosKey[0];
      }
   }

   public KerberosKey[] getKKeys(KerberosPrincipal var1) {
      if (this.destroyed) {
         throw new IllegalStateException("This object is destroyed");
      } else {
         ArrayList var2 = new ArrayList();
         if (this.kp != null && !var1.equals(this.kp)) {
            return new KerberosKey[0];
         } else {
            Iterator var3 = this.kk.iterator();

            while(var3.hasNext()) {
               KerberosKey var4 = (KerberosKey)var3.next();
               if (var4.getPrincipal().equals(var1)) {
                  var2.add(var4);
               }
            }

            var3 = this.ktabs.iterator();

            while(true) {
               KeyTab var9;
               do {
                  if (!var3.hasNext()) {
                     return (KerberosKey[])var2.toArray(new KerberosKey[var2.size()]);
                  }

                  var9 = (KeyTab)var3.next();
               } while(var9.getPrincipal() == null && var9.isBound() && !this.allPrincs.contains(var1));

               KerberosKey[] var5 = var9.getKeys(var1);
               int var6 = var5.length;

               for(int var7 = 0; var7 < var6; ++var7) {
                  KerberosKey var8 = var5[var7];
                  var2.add(var8);
               }
            }
         }
      }
   }

   public EncryptionKey[] getEKeys(PrincipalName var1) {
      if (this.destroyed) {
         throw new IllegalStateException("This object is destroyed");
      } else {
         KerberosKey[] var2 = this.getKKeys(new KerberosPrincipal(var1.getName()));
         if (var2.length == 0) {
            var2 = this.getKKeys();
         }

         EncryptionKey[] var3 = new EncryptionKey[var2.length];

         for(int var4 = 0; var4 < var3.length; ++var4) {
            var3[var4] = new EncryptionKey(var2[var4].getEncoded(), var2[var4].getKeyType(), new Integer(var2[var4].getVersionNumber()));
         }

         return var3;
      }
   }

   public Credentials getInitCred() {
      if (this.destroyed) {
         throw new IllegalStateException("This object is destroyed");
      } else if (this.tgt == null) {
         return null;
      } else {
         try {
            return Krb5Util.ticketToCreds(this.tgt);
         } catch (IOException | KrbException var2) {
            return null;
         }
      }
   }

   public void destroy() {
      this.destroyed = true;
      this.kp = null;
      this.ktabs.clear();
      this.kk.clear();
      this.tgt = null;
   }
}
