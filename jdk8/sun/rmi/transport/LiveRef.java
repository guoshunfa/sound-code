package sun.rmi.transport;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ObjID;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.util.Arrays;
import sun.rmi.transport.tcp.TCPEndpoint;

public class LiveRef implements Cloneable {
   private final Endpoint ep;
   private final ObjID id;
   private transient Channel ch;
   private final boolean isLocal;

   public LiveRef(ObjID var1, Endpoint var2, boolean var3) {
      this.ep = var2;
      this.id = var1;
      this.isLocal = var3;
   }

   public LiveRef(int var1) {
      this(new ObjID(), var1);
   }

   public LiveRef(int var1, RMIClientSocketFactory var2, RMIServerSocketFactory var3) {
      this(new ObjID(), var1, var2, var3);
   }

   public LiveRef(ObjID var1, int var2) {
      this(var1, TCPEndpoint.getLocalEndpoint(var2), true);
   }

   public LiveRef(ObjID var1, int var2, RMIClientSocketFactory var3, RMIServerSocketFactory var4) {
      this(var1, TCPEndpoint.getLocalEndpoint(var2, var3, var4), true);
   }

   public Object clone() {
      try {
         LiveRef var1 = (LiveRef)super.clone();
         return var1;
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2.toString(), var2);
      }
   }

   public int getPort() {
      return ((TCPEndpoint)this.ep).getPort();
   }

   public RMIClientSocketFactory getClientSocketFactory() {
      return ((TCPEndpoint)this.ep).getClientSocketFactory();
   }

   public RMIServerSocketFactory getServerSocketFactory() {
      return ((TCPEndpoint)this.ep).getServerSocketFactory();
   }

   public void exportObject(Target var1) throws RemoteException {
      this.ep.exportObject(var1);
   }

   public Channel getChannel() throws RemoteException {
      if (this.ch == null) {
         this.ch = this.ep.getChannel();
      }

      return this.ch;
   }

   public ObjID getObjID() {
      return this.id;
   }

   Endpoint getEndpoint() {
      return this.ep;
   }

   public String toString() {
      String var1;
      if (this.isLocal) {
         var1 = "local";
      } else {
         var1 = "remote";
      }

      return "[endpoint:" + this.ep + "(" + var1 + "),objID:" + this.id + "]";
   }

   public int hashCode() {
      return this.id.hashCode();
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof LiveRef) {
         LiveRef var2 = (LiveRef)var1;
         return this.ep.equals(var2.ep) && this.id.equals(var2.id) && this.isLocal == var2.isLocal;
      } else {
         return false;
      }
   }

   public boolean remoteEquals(Object var1) {
      if (var1 != null && var1 instanceof LiveRef) {
         LiveRef var2 = (LiveRef)var1;
         TCPEndpoint var3 = (TCPEndpoint)this.ep;
         TCPEndpoint var4 = (TCPEndpoint)var2.ep;
         RMIClientSocketFactory var5 = var3.getClientSocketFactory();
         RMIClientSocketFactory var6 = var4.getClientSocketFactory();
         if (var3.getPort() == var4.getPort() && var3.getHost().equals(var4.getHost())) {
            if (var5 == null ^ var6 == null) {
               return false;
            } else {
               return var5 == null || var5.getClass() == var6.getClass() && var5.equals(var6) ? this.id.equals(var2.id) : false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void write(ObjectOutput var1, boolean var2) throws IOException {
      boolean var3 = false;
      if (var1 instanceof ConnectionOutputStream) {
         ConnectionOutputStream var4 = (ConnectionOutputStream)var1;
         var3 = var4.isResultStream();
         if (this.isLocal) {
            ObjectEndpoint var5 = new ObjectEndpoint(this.id, this.ep.getInboundTransport());
            Target var6 = ObjectTable.getTarget(var5);
            if (var6 != null) {
               Remote var7 = var6.getImpl();
               if (var7 != null) {
                  var4.saveObject(var7);
               }
            }
         } else {
            var4.saveObject(this);
         }
      }

      if (var2) {
         ((TCPEndpoint)this.ep).write(var1);
      } else {
         ((TCPEndpoint)this.ep).writeHostPortFormat(var1);
      }

      this.id.write(var1);
      var1.writeBoolean(var3);
   }

   public static LiveRef read(ObjectInput var0, boolean var1) throws IOException, ClassNotFoundException {
      TCPEndpoint var2;
      if (var1) {
         var2 = TCPEndpoint.read(var0);
      } else {
         var2 = TCPEndpoint.readHostPortFormat(var0);
      }

      ObjID var3 = ObjID.read(var0);
      boolean var4 = var0.readBoolean();
      LiveRef var5 = new LiveRef(var3, var2, false);
      if (var0 instanceof ConnectionInputStream) {
         ConnectionInputStream var6 = (ConnectionInputStream)var0;
         var6.saveRef(var5);
         if (var4) {
            var6.setAckNeeded();
         }
      } else {
         DGCClient.registerRefs(var2, Arrays.asList(var5));
      }

      return var5;
   }
}
