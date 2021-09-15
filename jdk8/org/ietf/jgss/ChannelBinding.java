package org.ietf.jgss;

import java.net.InetAddress;
import java.util.Arrays;

public class ChannelBinding {
   private InetAddress initiator;
   private InetAddress acceptor;
   private byte[] appData;

   public ChannelBinding(InetAddress var1, InetAddress var2, byte[] var3) {
      this.initiator = var1;
      this.acceptor = var2;
      if (var3 != null) {
         this.appData = new byte[var3.length];
         System.arraycopy(var3, 0, this.appData, 0, var3.length);
      }

   }

   public ChannelBinding(byte[] var1) {
      this((InetAddress)null, (InetAddress)null, var1);
   }

   public InetAddress getInitiatorAddress() {
      return this.initiator;
   }

   public InetAddress getAcceptorAddress() {
      return this.acceptor;
   }

   public byte[] getApplicationData() {
      if (this.appData == null) {
         return null;
      } else {
         byte[] var1 = new byte[this.appData.length];
         System.arraycopy(this.appData, 0, var1, 0, this.appData.length);
         return var1;
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ChannelBinding)) {
         return false;
      } else {
         ChannelBinding var2 = (ChannelBinding)var1;
         if (this.initiator != null && var2.initiator == null || this.initiator == null && var2.initiator != null) {
            return false;
         } else if (this.initiator != null && !this.initiator.equals(var2.initiator)) {
            return false;
         } else if ((this.acceptor == null || var2.acceptor != null) && (this.acceptor != null || var2.acceptor == null)) {
            return this.acceptor != null && !this.acceptor.equals(var2.acceptor) ? false : Arrays.equals(this.appData, var2.appData);
         } else {
            return false;
         }
      }
   }

   public int hashCode() {
      if (this.initiator != null) {
         return this.initiator.hashCode();
      } else if (this.acceptor != null) {
         return this.acceptor.hashCode();
      } else {
         return this.appData != null ? (new String(this.appData)).hashCode() : 1;
      }
   }
}
