package sun.security.krb5.internal.rcache;

import java.util.Objects;

public class AuthTimeWithHash extends AuthTime implements Comparable<AuthTimeWithHash> {
   final String hash;

   public AuthTimeWithHash(String var1, String var2, int var3, int var4, String var5) {
      super(var1, var2, var3, var4);
      this.hash = var5;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof AuthTimeWithHash)) {
         return false;
      } else {
         AuthTimeWithHash var2 = (AuthTimeWithHash)var1;
         return Objects.equals(this.hash, var2.hash) && Objects.equals(this.client, var2.client) && Objects.equals(this.server, var2.server) && this.ctime == var2.ctime && this.cusec == var2.cusec;
      }
   }

   public int hashCode() {
      return Objects.hash(this.hash);
   }

   public String toString() {
      return String.format("%d/%06d/%s/%s", this.ctime, this.cusec, this.hash, this.client);
   }

   public int compareTo(AuthTimeWithHash var1) {
      boolean var2 = false;
      int var3;
      if (this.ctime != var1.ctime) {
         var3 = Integer.compare(this.ctime, var1.ctime);
      } else if (this.cusec != var1.cusec) {
         var3 = Integer.compare(this.cusec, var1.cusec);
      } else {
         var3 = this.hash.compareTo(var1.hash);
      }

      return var3;
   }

   public boolean isSameIgnoresHash(AuthTime var1) {
      return this.client.equals(var1.client) && this.server.equals(var1.server) && this.ctime == var1.ctime && this.cusec == var1.cusec;
   }

   public byte[] encode(boolean var1) {
      String var2;
      String var3;
      if (var1) {
         var2 = "";
         var3 = String.format("HASH:%s %d:%s %d:%s", this.hash, this.client.length(), this.client, this.server.length(), this.server);
      } else {
         var2 = this.client;
         var3 = this.server;
      }

      return this.encode0(var2, var3);
   }
}
