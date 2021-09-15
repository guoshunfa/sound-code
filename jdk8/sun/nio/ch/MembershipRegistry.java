package sun.nio.ch;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.channels.MembershipKey;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class MembershipRegistry {
   private Map<InetAddress, List<MembershipKeyImpl>> groups = null;

   MembershipKey checkMembership(InetAddress var1, NetworkInterface var2, InetAddress var3) {
      if (this.groups != null) {
         List var4 = (List)this.groups.get(var1);
         if (var4 != null) {
            Iterator var5 = var4.iterator();

            while(var5.hasNext()) {
               MembershipKeyImpl var6 = (MembershipKeyImpl)var5.next();
               if (var6.networkInterface().equals(var2)) {
                  if (var3 == null) {
                     if (var6.sourceAddress() == null) {
                        return var6;
                     }

                     throw new IllegalStateException("Already a member to receive all packets");
                  }

                  if (var6.sourceAddress() == null) {
                     throw new IllegalStateException("Already have source-specific membership");
                  }

                  if (var3.equals(var6.sourceAddress())) {
                     return var6;
                  }
               }
            }
         }
      }

      return null;
   }

   void add(MembershipKeyImpl var1) {
      InetAddress var2 = var1.group();
      Object var3;
      if (this.groups == null) {
         this.groups = new HashMap();
         var3 = null;
      } else {
         var3 = (List)this.groups.get(var2);
      }

      if (var3 == null) {
         var3 = new LinkedList();
         this.groups.put(var2, var3);
      }

      ((List)var3).add(var1);
   }

   void remove(MembershipKeyImpl var1) {
      InetAddress var2 = var1.group();
      List var3 = (List)this.groups.get(var2);
      if (var3 != null) {
         Iterator var4 = var3.iterator();

         while(var4.hasNext()) {
            if (var4.next() == var1) {
               var4.remove();
               break;
            }
         }

         if (var3.isEmpty()) {
            this.groups.remove(var2);
         }
      }

   }

   void invalidateAll() {
      if (this.groups != null) {
         Iterator var1 = this.groups.keySet().iterator();

         while(var1.hasNext()) {
            InetAddress var2 = (InetAddress)var1.next();
            Iterator var3 = ((List)this.groups.get(var2)).iterator();

            while(var3.hasNext()) {
               MembershipKeyImpl var4 = (MembershipKeyImpl)var3.next();
               var4.invalidate();
            }
         }
      }

   }
}
