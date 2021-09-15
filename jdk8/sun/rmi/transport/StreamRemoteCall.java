package sun.rmi.transport;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.StreamCorruptedException;
import java.rmi.MarshalException;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;
import java.rmi.server.ObjID;
import java.rmi.server.RemoteCall;
import sun.rmi.runtime.Log;
import sun.rmi.server.UnicastRef;
import sun.rmi.transport.tcp.TCPEndpoint;

public class StreamRemoteCall implements RemoteCall {
   private ConnectionInputStream in = null;
   private ConnectionOutputStream out = null;
   private Connection conn;
   private boolean resultStarted = false;
   private Exception serverException = null;

   public StreamRemoteCall(Connection var1) {
      this.conn = var1;
   }

   public StreamRemoteCall(Connection var1, ObjID var2, int var3, long var4) throws RemoteException {
      try {
         this.conn = var1;
         Transport.transportLog.log(Log.VERBOSE, "write remote call header...");
         this.conn.getOutputStream().write(80);
         this.getOutputStream();
         var2.write(this.out);
         this.out.writeInt(var3);
         this.out.writeLong(var4);
      } catch (IOException var7) {
         throw new MarshalException("Error marshaling call header", var7);
      }
   }

   public Connection getConnection() {
      return this.conn;
   }

   public ObjectOutput getOutputStream() throws IOException {
      return this.getOutputStream(false);
   }

   private ObjectOutput getOutputStream(boolean var1) throws IOException {
      if (this.out == null) {
         Transport.transportLog.log(Log.VERBOSE, "getting output stream");
         this.out = new ConnectionOutputStream(this.conn, var1);
      }

      return this.out;
   }

   public void releaseOutputStream() throws IOException {
      try {
         if (this.out != null) {
            try {
               this.out.flush();
            } finally {
               this.out.done();
            }
         }

         this.conn.releaseOutputStream();
      } finally {
         this.out = null;
      }

   }

   public ObjectInput getInputStream() throws IOException {
      if (this.in == null) {
         Transport.transportLog.log(Log.VERBOSE, "getting input stream");
         this.in = new ConnectionInputStream(this.conn.getInputStream());
      }

      return this.in;
   }

   public void releaseInputStream() throws IOException {
      try {
         if (this.in != null) {
            try {
               this.in.done();
            } catch (RuntimeException var5) {
            }

            this.in.registerRefs();
            this.in.done(this.conn);
         }

         this.conn.releaseInputStream();
      } finally {
         this.in = null;
      }

   }

   public void discardPendingRefs() {
      this.in.discardRefs();
   }

   public ObjectOutput getResultStream(boolean var1) throws IOException {
      if (this.resultStarted) {
         throw new StreamCorruptedException("result already in progress");
      } else {
         this.resultStarted = true;
         DataOutputStream var2 = new DataOutputStream(this.conn.getOutputStream());
         var2.writeByte(81);
         this.getOutputStream(true);
         if (var1) {
            this.out.writeByte(1);
         } else {
            this.out.writeByte(2);
         }

         this.out.writeID();
         return this.out;
      }
   }

   public void executeCall() throws Exception {
      DGCAckHandler var2 = null;

      byte var1;
      try {
         if (this.out != null) {
            var2 = this.out.getDGCAckHandler();
         }

         this.releaseOutputStream();
         DataInputStream var3 = new DataInputStream(this.conn.getInputStream());
         byte var4 = var3.readByte();
         if (var4 != 81) {
            if (Transport.transportLog.isLoggable(Log.BRIEF)) {
               Transport.transportLog.log(Log.BRIEF, "transport return code invalid: " + var4);
            }

            throw new UnmarshalException("Transport return code invalid");
         }

         this.getInputStream();
         var1 = this.in.readByte();
         this.in.readID();
      } catch (UnmarshalException var11) {
         throw var11;
      } catch (IOException var12) {
         throw new UnmarshalException("Error unmarshaling return header", var12);
      } finally {
         if (var2 != null) {
            var2.release();
         }

      }

      switch(var1) {
      case 1:
         return;
      case 2:
         Object var14;
         try {
            var14 = this.in.readObject();
         } catch (Exception var10) {
            throw new UnmarshalException("Error unmarshaling return", var10);
         }

         if (!(var14 instanceof Exception)) {
            throw new UnmarshalException("Return type not Exception");
         } else {
            this.exceptionReceivedFromServer((Exception)var14);
         }
      default:
         if (Transport.transportLog.isLoggable(Log.BRIEF)) {
            Transport.transportLog.log(Log.BRIEF, "return code invalid: " + var1);
         }

         throw new UnmarshalException("Return code invalid");
      }
   }

   protected void exceptionReceivedFromServer(Exception var1) throws Exception {
      this.serverException = var1;
      StackTraceElement[] var2 = var1.getStackTrace();
      StackTraceElement[] var3 = (new Throwable()).getStackTrace();
      StackTraceElement[] var4 = new StackTraceElement[var2.length + var3.length];
      System.arraycopy(var2, 0, var4, 0, var2.length);
      System.arraycopy(var3, 0, var4, var2.length, var3.length);
      var1.setStackTrace(var4);
      if (UnicastRef.clientCallLog.isLoggable(Log.BRIEF)) {
         TCPEndpoint var5 = (TCPEndpoint)this.conn.getChannel().getEndpoint();
         UnicastRef.clientCallLog.log(Log.BRIEF, "outbound call received exception: [" + var5.getHost() + ":" + var5.getPort() + "] exception: ", var1);
      }

      throw var1;
   }

   public Exception getServerException() {
      return this.serverException;
   }

   public void done() throws IOException {
      this.releaseInputStream();
   }
}
