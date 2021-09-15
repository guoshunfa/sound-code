package sun.rmi.transport;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import sun.rmi.runtime.Log;
import sun.rmi.server.MarshalInputStream;

class ConnectionInputStream extends MarshalInputStream {
   private boolean dgcAckNeeded = false;
   private Map<Endpoint, List<LiveRef>> incomingRefTable = new HashMap(5);
   private UID ackID;

   ConnectionInputStream(InputStream var1) throws IOException {
      super(var1);
   }

   void readID() throws IOException {
      this.ackID = UID.read(this);
   }

   void saveRef(LiveRef var1) {
      Endpoint var2 = var1.getEndpoint();
      Object var3 = (List)this.incomingRefTable.get(var2);
      if (var3 == null) {
         var3 = new ArrayList();
         this.incomingRefTable.put(var2, var3);
      }

      ((List)var3).add(var1);
   }

   void discardRefs() {
      this.incomingRefTable.clear();
   }

   void registerRefs() throws IOException {
      if (!this.incomingRefTable.isEmpty()) {
         Iterator var1 = this.incomingRefTable.entrySet().iterator();

         while(var1.hasNext()) {
            Map.Entry var2 = (Map.Entry)var1.next();
            DGCClient.registerRefs((Endpoint)var2.getKey(), (List)var2.getValue());
         }
      }

   }

   void setAckNeeded() {
      this.dgcAckNeeded = true;
   }

   void done(Connection var1) {
      if (this.dgcAckNeeded) {
         Connection var2 = null;
         Channel var3 = null;
         boolean var4 = true;
         DGCImpl.dgcLog.log(Log.VERBOSE, "send ack");

         try {
            var3 = var1.getChannel();
            var2 = var3.newConnection();
            DataOutputStream var5 = new DataOutputStream(var2.getOutputStream());
            var5.writeByte(84);
            if (this.ackID == null) {
               this.ackID = new UID();
            }

            this.ackID.write(var5);
            var2.releaseOutputStream();
            var2.getInputStream().available();
            var2.releaseInputStream();
         } catch (RemoteException var7) {
            var4 = false;
         } catch (IOException var8) {
            var4 = false;
         }

         try {
            if (var2 != null) {
               var3.free(var2, var4);
            }
         } catch (RemoteException var6) {
         }
      }

   }
}
