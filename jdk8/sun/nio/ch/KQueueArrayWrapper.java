package sun.nio.ch;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.LinkedList;
import sun.security.action.GetPropertyAction;

class KQueueArrayWrapper {
   static short EVFILT_READ;
   static short EVFILT_WRITE;
   static short SIZEOF_KEVENT;
   static short FD_OFFSET;
   static short FILTER_OFFSET;
   static final int NUM_KEVENTS = 128;
   static boolean is64bit = false;
   private AllocatedNativeObject keventArray = null;
   private long keventArrayAddress;
   private int kq = -1;
   private int outgoingInterruptFD;
   private int incomingInterruptFD;
   private LinkedList<KQueueArrayWrapper.Update> updateList = new LinkedList();

   KQueueArrayWrapper() {
      int var1 = SIZEOF_KEVENT * 128;
      this.keventArray = new AllocatedNativeObject(var1, true);
      this.keventArrayAddress = this.keventArray.address();
      this.kq = this.init();
   }

   void initInterrupt(int var1, int var2) {
      this.outgoingInterruptFD = var2;
      this.incomingInterruptFD = var1;
      this.register0(this.kq, var1, 1, 0);
   }

   int getReventOps(int var1) {
      int var2 = 0;
      int var3 = SIZEOF_KEVENT * var1 + FILTER_OFFSET;
      short var4 = this.keventArray.getShort(var3);
      if (var4 == EVFILT_READ) {
         var2 |= Net.POLLIN;
      } else if (var4 == EVFILT_WRITE) {
         var2 |= Net.POLLOUT;
      }

      return var2;
   }

   int getDescriptor(int var1) {
      int var2 = SIZEOF_KEVENT * var1 + FD_OFFSET;
      if (is64bit) {
         long var3 = this.keventArray.getLong(var2);

         assert var3 <= 2147483647L;

         return (int)var3;
      } else {
         return this.keventArray.getInt(var2);
      }
   }

   void setInterest(SelChImpl var1, int var2) {
      synchronized(this.updateList) {
         this.updateList.add(new KQueueArrayWrapper.Update(var1, var2));
      }
   }

   void release(SelChImpl var1) {
      synchronized(this.updateList) {
         Iterator var3 = this.updateList.iterator();

         while(var3.hasNext()) {
            if (((KQueueArrayWrapper.Update)var3.next()).channel == var1) {
               var3.remove();
            }
         }

         this.register0(this.kq, var1.getFDVal(), 0, 0);
      }
   }

   void updateRegistrations() {
      synchronized(this.updateList) {
         KQueueArrayWrapper.Update var2 = null;

         while((var2 = (KQueueArrayWrapper.Update)this.updateList.poll()) != null) {
            SelChImpl var3 = var2.channel;
            if (var3.isOpen()) {
               this.register0(this.kq, var3.getFDVal(), var2.events & Net.POLLIN, var2.events & Net.POLLOUT);
            }
         }

      }
   }

   void close() throws IOException {
      if (this.keventArray != null) {
         this.keventArray.free();
         this.keventArray = null;
      }

      if (this.kq >= 0) {
         FileDispatcherImpl.closeIntFD(this.kq);
         this.kq = -1;
      }

   }

   int poll(long var1) {
      this.updateRegistrations();
      int var3 = this.kevent0(this.kq, this.keventArrayAddress, 128, var1);
      return var3;
   }

   void interrupt() {
      interrupt(this.outgoingInterruptFD);
   }

   private native int init();

   private static native void initStructSizes();

   private native void register0(int var1, int var2, int var3, int var4);

   private native int kevent0(int var1, long var2, int var4, long var5);

   private static native void interrupt(int var0);

   static {
      IOUtil.load();
      initStructSizes();
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.arch.data.model")));
      is64bit = var0.equals("64");
   }

   private static class Update {
      SelChImpl channel;
      int events;

      Update(SelChImpl var1, int var2) {
         this.channel = var1;
         this.events = var2;
      }
   }
}
