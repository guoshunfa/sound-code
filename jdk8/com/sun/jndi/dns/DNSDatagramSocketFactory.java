package com.sun.jndi.dns;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ProtocolFamily;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.DatagramChannel;
import java.util.Objects;
import java.util.Random;
import sun.net.PortConfig;

class DNSDatagramSocketFactory {
   static final int DEVIATION = 3;
   static final int THRESHOLD = 6;
   static final int BIT_DEVIATION = 2;
   static final int HISTORY = 32;
   static final int MAX_RANDOM_TRIES = 5;
   int lastport;
   int suitablePortCount;
   int unsuitablePortCount;
   final ProtocolFamily family;
   final int thresholdCount;
   final int deviation;
   final Random random;
   final DNSDatagramSocketFactory.PortHistory history;

   DNSDatagramSocketFactory() {
      this(new Random());
   }

   DNSDatagramSocketFactory(Random var1) {
      this((Random)Objects.requireNonNull(var1), (ProtocolFamily)null, 3, 6);
   }

   DNSDatagramSocketFactory(Random var1, ProtocolFamily var2, int var3, int var4) {
      this.lastport = 0;
      this.random = (Random)Objects.requireNonNull(var1);
      this.history = new DNSDatagramSocketFactory.PortHistory(32, var1);
      this.family = var2;
      this.deviation = Math.max(1, var3);
      this.thresholdCount = Math.max(2, var4);
   }

   public synchronized DatagramSocket open() throws SocketException {
      int var1 = this.lastport;
      boolean var3 = this.unsuitablePortCount > this.thresholdCount;
      DatagramSocket var2;
      if (var3) {
         var2 = this.openRandom();
         if (var2 != null) {
            return var2;
         }

         this.unsuitablePortCount = 0;
         this.suitablePortCount = 0;
         var1 = 0;
      }

      var2 = this.openDefault();
      this.lastport = var2.getLocalPort();
      if (var1 == 0) {
         this.history.offer(this.lastport);
         return var2;
      } else {
         var3 = this.suitablePortCount > this.thresholdCount;
         boolean var4 = Integer.bitCount(var1 ^ this.lastport) > 2 && Math.abs(this.lastport - var1) > this.deviation;
         boolean var5 = this.history.contains(this.lastport);
         boolean var6 = var3 || var4 && !var5;
         if (var6 && !var5) {
            this.history.add(this.lastport);
         }

         if (!var6) {
            assert !var3;

            DatagramSocket var7 = this.openRandom();
            if (var7 == null) {
               return var2;
            } else {
               ++this.unsuitablePortCount;
               var2.close();
               return var7;
            }
         } else {
            if (!var3) {
               ++this.suitablePortCount;
            } else if (!var4 || var5) {
               this.unsuitablePortCount = 1;
               this.suitablePortCount = this.thresholdCount / 2;
            }

            return var2;
         }
      }
   }

   private DatagramSocket openDefault() throws SocketException {
      if (this.family != null) {
         try {
            DatagramChannel var1 = DatagramChannel.open(this.family);

            try {
               DatagramSocket var6 = var1.socket();
               var6.bind((SocketAddress)null);
               return var6;
            } catch (Throwable var3) {
               var1.close();
               throw var3;
            }
         } catch (SocketException var4) {
            throw var4;
         } catch (IOException var5) {
            SocketException var2 = new SocketException(var5.getMessage());
            var2.initCause(var5);
            throw var2;
         }
      } else {
         return new DatagramSocket();
      }
   }

   synchronized boolean isUsingNativePortRandomization() {
      return this.unsuitablePortCount <= this.thresholdCount && this.suitablePortCount > this.thresholdCount;
   }

   synchronized boolean isUsingJavaPortRandomization() {
      return this.unsuitablePortCount > this.thresholdCount;
   }

   synchronized boolean isUndecided() {
      return !this.isUsingJavaPortRandomization() && !this.isUsingNativePortRandomization();
   }

   private DatagramSocket openRandom() {
      int var1 = 5;

      while(var1-- > 0) {
         int var2 = DNSDatagramSocketFactory.EphemeralPortRange.LOWER + this.random.nextInt(DNSDatagramSocketFactory.EphemeralPortRange.RANGE);

         try {
            if (this.family != null) {
               DatagramChannel var3 = DatagramChannel.open(this.family);

               try {
                  DatagramSocket var4 = var3.socket();
                  var4.bind(new InetSocketAddress(var2));
                  return var4;
               } catch (Throwable var5) {
                  var3.close();
                  throw var5;
               }
            }

            return new DatagramSocket(var2);
         } catch (IOException var6) {
         }
      }

      return null;
   }

   static final class PortHistory {
      final int capacity;
      final int[] ports;
      final Random random;
      int index;

      PortHistory(int var1, Random var2) {
         this.random = var2;
         this.capacity = var1;
         this.ports = new int[var1];
      }

      public boolean contains(int var1) {
         int var2 = 0;

         for(int var3 = 0; var3 < this.capacity && (var2 = this.ports[var3]) != 0 && var2 != var1; ++var3) {
         }

         return var2 == var1;
      }

      public boolean add(int var1) {
         if (this.ports[this.index] != 0) {
            this.ports[this.random.nextInt(this.capacity)] = var1;
         } else {
            this.ports[this.index] = var1;
         }

         if (++this.index == this.capacity) {
            this.index = 0;
         }

         return true;
      }

      public boolean offer(int var1) {
         return this.contains(var1) ? false : this.add(var1);
      }
   }

   static final class EphemeralPortRange {
      static final int LOWER = PortConfig.getLower();
      static final int UPPER = PortConfig.getUpper();
      static final int RANGE;

      private EphemeralPortRange() {
      }

      static {
         RANGE = UPPER - LOWER + 1;
      }
   }
}
