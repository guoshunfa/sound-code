package sun.rmi.transport;

import java.io.IOException;
import java.rmi.server.UID;
import sun.rmi.server.MarshalOutputStream;

class ConnectionOutputStream extends MarshalOutputStream {
   private final Connection conn;
   private final boolean resultStream;
   private final UID ackID;
   private DGCAckHandler dgcAckHandler = null;

   ConnectionOutputStream(Connection var1, boolean var2) throws IOException {
      super(var1.getOutputStream());
      this.conn = var1;
      this.resultStream = var2;
      this.ackID = var2 ? new UID() : null;
   }

   void writeID() throws IOException {
      assert this.resultStream;

      this.ackID.write(this);
   }

   boolean isResultStream() {
      return this.resultStream;
   }

   void saveObject(Object var1) {
      if (this.dgcAckHandler == null) {
         this.dgcAckHandler = new DGCAckHandler(this.ackID);
      }

      this.dgcAckHandler.add(var1);
   }

   DGCAckHandler getDGCAckHandler() {
      return this.dgcAckHandler;
   }

   void done() {
      if (this.dgcAckHandler != null) {
         this.dgcAckHandler.startTimer();
      }

   }
}
