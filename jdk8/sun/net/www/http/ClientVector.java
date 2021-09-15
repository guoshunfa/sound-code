package sun.net.www.http;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Stack;

class ClientVector extends Stack<KeepAliveEntry> {
   private static final long serialVersionUID = -8680532108106489459L;
   int nap;

   ClientVector(int var1) {
      this.nap = var1;
   }

   synchronized HttpClient get() {
      if (this.empty()) {
         return null;
      } else {
         HttpClient var1 = null;
         long var2 = System.currentTimeMillis();

         do {
            KeepAliveEntry var4 = (KeepAliveEntry)this.pop();
            if (var2 - var4.idleStartTime > (long)this.nap) {
               var4.hc.closeServer();
            } else {
               var1 = var4.hc;
            }
         } while(var1 == null && !this.empty());

         return var1;
      }
   }

   synchronized void put(HttpClient var1) {
      if (this.size() >= KeepAliveCache.getMaxConnections()) {
         var1.closeServer();
      } else {
         this.push(new KeepAliveEntry(var1, System.currentTimeMillis()));
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      throw new NotSerializableException();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      throw new NotSerializableException();
   }
}
