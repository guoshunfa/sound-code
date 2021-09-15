package sun.security.krb5.internal.ccache;

import java.io.IOException;
import java.io.OutputStream;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.internal.Ticket;
import sun.security.krb5.internal.TicketFlags;
import sun.security.krb5.internal.util.KrbDataOutputStream;

public class CCacheOutputStream extends KrbDataOutputStream implements FileCCacheConstants {
   public CCacheOutputStream(OutputStream var1) {
      super(var1);
   }

   public void writeHeader(PrincipalName var1, int var2) throws IOException {
      this.write((var2 & '\uff00') >> 8);
      this.write(var2 & 255);
      var1.writePrincipal(this);
   }

   public void addCreds(Credentials var1) throws IOException, Asn1Exception {
      var1.cname.writePrincipal(this);
      var1.sname.writePrincipal(this);
      var1.key.writeKey(this);
      this.write32((int)(var1.authtime.getTime() / 1000L));
      if (var1.starttime != null) {
         this.write32((int)(var1.starttime.getTime() / 1000L));
      } else {
         this.write32(0);
      }

      this.write32((int)(var1.endtime.getTime() / 1000L));
      if (var1.renewTill != null) {
         this.write32((int)(var1.renewTill.getTime() / 1000L));
      } else {
         this.write32(0);
      }

      if (var1.isEncInSKey) {
         this.write8(1);
      } else {
         this.write8(0);
      }

      this.writeFlags(var1.flags);
      if (var1.caddr == null) {
         this.write32(0);
      } else {
         var1.caddr.writeAddrs(this);
      }

      if (var1.authorizationData == null) {
         this.write32(0);
      } else {
         var1.authorizationData.writeAuth(this);
      }

      this.writeTicket(var1.ticket);
      this.writeTicket(var1.secondTicket);
   }

   void writeTicket(Ticket var1) throws IOException, Asn1Exception {
      if (var1 == null) {
         this.write32(0);
      } else {
         byte[] var2 = var1.asn1Encode();
         this.write32(var2.length);
         this.write(var2, 0, var2.length);
      }

   }

   void writeFlags(TicketFlags var1) throws IOException {
      int var2 = 0;
      boolean[] var3 = var1.toBooleanArray();
      if (var3[1]) {
         var2 |= 1073741824;
      }

      if (var3[2]) {
         var2 |= 536870912;
      }

      if (var3[3]) {
         var2 |= 268435456;
      }

      if (var3[4]) {
         var2 |= 134217728;
      }

      if (var3[5]) {
         var2 |= 67108864;
      }

      if (var3[6]) {
         var2 |= 33554432;
      }

      if (var3[7]) {
         var2 |= 16777216;
      }

      if (var3[8]) {
         var2 |= 8388608;
      }

      if (var3[9]) {
         var2 |= 4194304;
      }

      if (var3[10]) {
         var2 |= 2097152;
      }

      if (var3[11]) {
         var2 |= 1048576;
      }

      this.write32(var2);
   }
}
