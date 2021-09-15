package sun.nio.ch;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.channels.MembershipKey;
import java.nio.channels.MulticastChannel;
import java.util.HashSet;

class MembershipKeyImpl extends MembershipKey {
   private final MulticastChannel ch;
   private final InetAddress group;
   private final NetworkInterface interf;
   private final InetAddress source;
   private volatile boolean valid;
   private Object stateLock;
   private HashSet<InetAddress> blockedSet;

   private MembershipKeyImpl(MulticastChannel var1, InetAddress var2, NetworkInterface var3, InetAddress var4) {
      this.valid = true;
      this.stateLock = new Object();
      this.ch = var1;
      this.group = var2;
      this.interf = var3;
      this.source = var4;
   }

   public boolean isValid() {
      return this.valid;
   }

   void invalidate() {
      this.valid = false;
   }

   public void drop() {
      ((DatagramChannelImpl)this.ch).drop(this);
   }

   public MulticastChannel channel() {
      return this.ch;
   }

   public InetAddress group() {
      return this.group;
   }

   public NetworkInterface networkInterface() {
      return this.interf;
   }

   public InetAddress sourceAddress() {
      return this.source;
   }

   public MembershipKey block(InetAddress var1) throws IOException {
      if (this.source != null) {
         throw new IllegalStateException("key is source-specific");
      } else {
         synchronized(this.stateLock) {
            if (this.blockedSet != null && this.blockedSet.contains(var1)) {
               return this;
            } else {
               ((DatagramChannelImpl)this.ch).block(this, var1);
               if (this.blockedSet == null) {
                  this.blockedSet = new HashSet();
               }

               this.blockedSet.add(var1);
               return this;
            }
         }
      }
   }

   public MembershipKey unblock(InetAddress var1) {
      synchronized(this.stateLock) {
         if (this.blockedSet != null && this.blockedSet.contains(var1)) {
            ((DatagramChannelImpl)this.ch).unblock(this, var1);
            this.blockedSet.remove(var1);
            return this;
         } else {
            throw new IllegalStateException("not blocked");
         }
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(64);
      var1.append('<');
      var1.append(this.group.getHostAddress());
      var1.append(',');
      var1.append(this.interf.getName());
      if (this.source != null) {
         var1.append(',');
         var1.append(this.source.getHostAddress());
      }

      var1.append('>');
      return var1.toString();
   }

   // $FF: synthetic method
   MembershipKeyImpl(MulticastChannel var1, InetAddress var2, NetworkInterface var3, InetAddress var4, Object var5) {
      this(var1, var2, var3, var4);
   }

   static class Type6 extends MembershipKeyImpl {
      private final byte[] groupAddress;
      private final int index;
      private final byte[] sourceAddress;

      Type6(MulticastChannel var1, InetAddress var2, NetworkInterface var3, InetAddress var4, byte[] var5, int var6, byte[] var7) {
         super(var1, var2, var3, var4, null);
         this.groupAddress = var5;
         this.index = var6;
         this.sourceAddress = var7;
      }

      byte[] groupAddress() {
         return this.groupAddress;
      }

      int index() {
         return this.index;
      }

      byte[] source() {
         return this.sourceAddress;
      }
   }

   static class Type4 extends MembershipKeyImpl {
      private final int groupAddress;
      private final int interfAddress;
      private final int sourceAddress;

      Type4(MulticastChannel var1, InetAddress var2, NetworkInterface var3, InetAddress var4, int var5, int var6, int var7) {
         super(var1, var2, var3, var4, null);
         this.groupAddress = var5;
         this.interfAddress = var6;
         this.sourceAddress = var7;
      }

      int groupAddress() {
         return this.groupAddress;
      }

      int interfaceAddress() {
         return this.interfAddress;
      }

      int source() {
         return this.sourceAddress;
      }
   }
}
