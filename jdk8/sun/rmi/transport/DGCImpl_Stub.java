package sun.rmi.transport;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.rmi.MarshalException;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import java.rmi.UnmarshalException;
import java.rmi.dgc.DGC;
import java.rmi.dgc.Lease;
import java.rmi.dgc.VMID;
import java.rmi.server.ObjID;
import java.rmi.server.Operation;
import java.rmi.server.RemoteCall;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteStub;
import java.rmi.server.UID;
import java.security.AccessController;
import sun.misc.ObjectInputFilter;
import sun.rmi.transport.tcp.TCPConnection;

public final class DGCImpl_Stub extends RemoteStub implements DGC {
   private static final Operation[] operations = new Operation[]{new Operation("void clean(java.rmi.server.ObjID[], long, java.rmi.dgc.VMID, boolean)"), new Operation("java.rmi.dgc.Lease dirty(java.rmi.server.ObjID[], long, java.rmi.dgc.Lease)")};
   private static final long interfaceHash = -669196253586618813L;
   private static int DGCCLIENT_MAX_DEPTH = 6;
   private static int DGCCLIENT_MAX_ARRAY_SIZE = 10000;

   public DGCImpl_Stub() {
   }

   public DGCImpl_Stub(RemoteRef var1) {
      super(var1);
   }

   public void clean(ObjID[] var1, long var2, VMID var4, boolean var5) throws RemoteException {
      try {
         RemoteCall var6 = this.ref.newCall(this, operations, 0, -669196253586618813L);

         try {
            ObjectOutput var7 = var6.getOutputStream();
            var7.writeObject(var1);
            var7.writeLong(var2);
            var7.writeObject(var4);
            var7.writeBoolean(var5);
         } catch (IOException var8) {
            throw new MarshalException("error marshalling arguments", var8);
         }

         this.ref.invoke(var6);
         this.ref.done(var6);
      } catch (RuntimeException var9) {
         throw var9;
      } catch (RemoteException var10) {
         throw var10;
      } catch (Exception var11) {
         throw new UnexpectedException("undeclared checked exception", var11);
      }
   }

   public Lease dirty(ObjID[] var1, long var2, Lease var4) throws RemoteException {
      try {
         RemoteCall var5 = this.ref.newCall(this, operations, 1, -669196253586618813L);

         try {
            ObjectOutput var6 = var5.getOutputStream();
            var6.writeObject(var1);
            var6.writeLong(var2);
            var6.writeObject(var4);
         } catch (IOException var17) {
            throw new MarshalException("error marshalling arguments", var17);
         }

         this.ref.invoke(var5);
         Connection var7 = ((StreamRemoteCall)var5).getConnection();

         Lease var23;
         try {
            ObjectInput var8 = var5.getInputStream();
            if (var8 instanceof ObjectInputStream) {
               ObjectInputStream var9 = (ObjectInputStream)var8;
               AccessController.doPrivileged(() -> {
                  ObjectInputFilter.Config.setObjectInputFilter(var9, DGCImpl_Stub::leaseFilter);
                  return null;
               });
            }

            var23 = (Lease)var8.readObject();
         } catch (ClassNotFoundException | IOException var18) {
            if (var7 instanceof TCPConnection) {
               ((TCPConnection)var7).getChannel().free(var7, false);
            }

            throw new UnmarshalException("error unmarshalling return", var18);
         } finally {
            this.ref.done(var5);
         }

         return var23;
      } catch (RuntimeException var20) {
         throw var20;
      } catch (RemoteException var21) {
         throw var21;
      } catch (Exception var22) {
         throw new UnexpectedException("undeclared checked exception", var22);
      }
   }

   private static ObjectInputFilter.Status leaseFilter(ObjectInputFilter.FilterInfo var0) {
      if (var0.depth() > (long)DGCCLIENT_MAX_DEPTH) {
         return ObjectInputFilter.Status.REJECTED;
      } else {
         Class var1 = var0.serialClass();
         if (var1 == null) {
            return ObjectInputFilter.Status.UNDECIDED;
         } else {
            while(var1.isArray()) {
               if (var0.arrayLength() >= 0L && var0.arrayLength() > (long)DGCCLIENT_MAX_ARRAY_SIZE) {
                  return ObjectInputFilter.Status.REJECTED;
               }

               var1 = var1.getComponentType();
            }

            if (var1.isPrimitive()) {
               return ObjectInputFilter.Status.ALLOWED;
            } else {
               return var1 != UID.class && var1 != VMID.class && var1 != Lease.class ? ObjectInputFilter.Status.REJECTED : ObjectInputFilter.Status.ALLOWED;
            }
         }
      }
   }
}
