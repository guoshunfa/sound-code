package sun.security.krb5.internal.ccache;

import sun.security.krb5.EncryptionKey;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.RealmException;
import sun.security.krb5.internal.AuthorizationData;
import sun.security.krb5.internal.HostAddresses;
import sun.security.krb5.internal.KDCRep;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.Ticket;
import sun.security.krb5.internal.TicketFlags;

public class Credentials {
   PrincipalName cname;
   PrincipalName sname;
   EncryptionKey key;
   KerberosTime authtime;
   KerberosTime starttime;
   KerberosTime endtime;
   KerberosTime renewTill;
   HostAddresses caddr;
   AuthorizationData authorizationData;
   public boolean isEncInSKey;
   TicketFlags flags;
   Ticket ticket;
   Ticket secondTicket;
   private boolean DEBUG;

   public Credentials(PrincipalName var1, PrincipalName var2, EncryptionKey var3, KerberosTime var4, KerberosTime var5, KerberosTime var6, KerberosTime var7, boolean var8, TicketFlags var9, HostAddresses var10, AuthorizationData var11, Ticket var12, Ticket var13) {
      this.DEBUG = Krb5.DEBUG;
      this.cname = (PrincipalName)var1.clone();
      this.sname = (PrincipalName)var2.clone();
      this.key = (EncryptionKey)var3.clone();
      this.authtime = var4;
      this.starttime = var5;
      this.endtime = var6;
      this.renewTill = var7;
      if (var10 != null) {
         this.caddr = (HostAddresses)var10.clone();
      }

      if (var11 != null) {
         this.authorizationData = (AuthorizationData)var11.clone();
      }

      this.isEncInSKey = var8;
      this.flags = (TicketFlags)var9.clone();
      this.ticket = (Ticket)((Ticket)var12.clone());
      if (var13 != null) {
         this.secondTicket = (Ticket)var13.clone();
      }

   }

   public Credentials(KDCRep var1, Ticket var2, AuthorizationData var3, boolean var4) {
      this.DEBUG = Krb5.DEBUG;
      if (var1.encKDCRepPart != null) {
         this.cname = (PrincipalName)var1.cname.clone();
         this.ticket = (Ticket)var1.ticket.clone();
         this.key = (EncryptionKey)var1.encKDCRepPart.key.clone();
         this.flags = (TicketFlags)var1.encKDCRepPart.flags.clone();
         this.authtime = var1.encKDCRepPart.authtime;
         this.starttime = var1.encKDCRepPart.starttime;
         this.endtime = var1.encKDCRepPart.endtime;
         this.renewTill = var1.encKDCRepPart.renewTill;
         this.sname = (PrincipalName)var1.encKDCRepPart.sname.clone();
         this.caddr = (HostAddresses)var1.encKDCRepPart.caddr.clone();
         this.secondTicket = (Ticket)var2.clone();
         this.authorizationData = (AuthorizationData)var3.clone();
         this.isEncInSKey = var4;
      }
   }

   public Credentials(KDCRep var1) {
      this(var1, (Ticket)null);
   }

   public Credentials(KDCRep var1, Ticket var2) {
      this.DEBUG = Krb5.DEBUG;
      this.sname = (PrincipalName)var1.encKDCRepPart.sname.clone();
      this.cname = (PrincipalName)var1.cname.clone();
      this.key = (EncryptionKey)var1.encKDCRepPart.key.clone();
      this.authtime = var1.encKDCRepPart.authtime;
      this.starttime = var1.encKDCRepPart.starttime;
      this.endtime = var1.encKDCRepPart.endtime;
      this.renewTill = var1.encKDCRepPart.renewTill;
      this.flags = var1.encKDCRepPart.flags;
      if (var1.encKDCRepPart.caddr != null) {
         this.caddr = (HostAddresses)var1.encKDCRepPart.caddr.clone();
      } else {
         this.caddr = null;
      }

      this.ticket = (Ticket)var1.ticket.clone();
      if (var2 != null) {
         this.secondTicket = (Ticket)var2.clone();
         this.isEncInSKey = true;
      } else {
         this.secondTicket = null;
         this.isEncInSKey = false;
      }

   }

   public boolean isValid() {
      boolean var1 = true;
      if (this.endtime.getTime() < System.currentTimeMillis()) {
         var1 = false;
      } else if (this.starttime != null) {
         if (this.starttime.getTime() > System.currentTimeMillis()) {
            var1 = false;
         }
      } else if (this.authtime.getTime() > System.currentTimeMillis()) {
         var1 = false;
      }

      return var1;
   }

   public PrincipalName getServicePrincipal() throws RealmException {
      return this.sname;
   }

   public sun.security.krb5.Credentials setKrbCreds() {
      return new sun.security.krb5.Credentials(this.ticket, this.cname, this.sname, this.key, this.flags, this.authtime, this.starttime, this.endtime, this.renewTill, this.caddr);
   }

   public KerberosTime getStartTime() {
      return this.starttime;
   }

   public KerberosTime getAuthTime() {
      return this.authtime;
   }

   public KerberosTime getEndTime() {
      return this.endtime;
   }

   public KerberosTime getRenewTill() {
      return this.renewTill;
   }

   public TicketFlags getTicketFlags() {
      return this.flags;
   }

   public int getEType() {
      return this.key.getEType();
   }

   public int getTktEType() {
      return this.ticket.encPart.getEType();
   }
}
