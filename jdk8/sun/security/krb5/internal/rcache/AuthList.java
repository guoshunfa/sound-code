package sun.security.krb5.internal.rcache;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.KrbApErrException;

public class AuthList {
   private final LinkedList<AuthTimeWithHash> entries;
   private final int lifespan;

   public AuthList(int var1) {
      this.lifespan = var1;
      this.entries = new LinkedList();
   }

   public void put(AuthTimeWithHash var1, KerberosTime var2) throws KrbApErrException {
      ListIterator var5;
      if (this.entries.isEmpty()) {
         this.entries.addFirst(var1);
      } else {
         AuthTimeWithHash var3 = (AuthTimeWithHash)this.entries.getFirst();
         int var4 = var3.compareTo(var1);
         if (var4 < 0) {
            this.entries.addFirst(var1);
         } else {
            if (var4 == 0) {
               throw new KrbApErrException(34);
            }

            var5 = this.entries.listIterator(1);
            boolean var6 = false;

            while(var5.hasNext()) {
               var3 = (AuthTimeWithHash)var5.next();
               var4 = var3.compareTo(var1);
               if (var4 < 0) {
                  this.entries.add(this.entries.indexOf(var3), var1);
                  var6 = true;
                  break;
               }

               if (var4 == 0) {
                  throw new KrbApErrException(34);
               }
            }

            if (!var6) {
               this.entries.addLast(var1);
            }
         }
      }

      long var8 = (long)(var2.getSeconds() - this.lifespan);
      var5 = this.entries.listIterator(0);
      AuthTimeWithHash var9 = null;
      int var7 = -1;

      while(var5.hasNext()) {
         var9 = (AuthTimeWithHash)var5.next();
         if ((long)var9.ctime < var8) {
            var7 = this.entries.indexOf(var9);
            break;
         }
      }

      if (var7 > -1) {
         do {
            this.entries.removeLast();
         } while(this.entries.size() > var7);
      }

   }

   public boolean isEmpty() {
      return this.entries.isEmpty();
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      Iterator var2 = this.entries.descendingIterator();
      int var3 = this.entries.size();

      while(var2.hasNext()) {
         AuthTimeWithHash var4 = (AuthTimeWithHash)var2.next();
         var1.append('#').append(var3--).append(": ").append(var4.toString()).append('\n');
      }

      return var1.toString();
   }
}
