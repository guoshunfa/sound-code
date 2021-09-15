package sun.rmi.transport;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.rmi.MarshalException;
import java.rmi.Remote;
import java.rmi.UnmarshalException;
import java.rmi.dgc.Lease;
import java.rmi.dgc.VMID;
import java.rmi.server.ObjID;
import java.rmi.server.Operation;
import java.rmi.server.RemoteCall;
import java.rmi.server.Skeleton;
import java.rmi.server.SkeletonMismatchException;

public final class DGCImpl_Skel implements Skeleton {
   private static final Operation[] operations = new Operation[]{new Operation("void clean(java.rmi.server.ObjID[], long, java.rmi.dgc.VMID, boolean)"), new Operation("java.rmi.dgc.Lease dirty(java.rmi.server.ObjID[], long, java.rmi.dgc.Lease)")};
   private static final long interfaceHash = -669196253586618813L;

   public Operation[] getOperations() {
      return (Operation[])operations.clone();
   }

   public void dispatch(Remote var1, RemoteCall var2, int var3, long var4) throws Exception {
      if (var4 != -669196253586618813L) {
         throw new SkeletonMismatchException("interface hash mismatch");
      } else {
         DGCImpl var6 = (DGCImpl)var1;
         ObjID[] var7;
         long var8;
         switch(var3) {
         case 0:
            VMID var39;
            boolean var41;
            try {
               ObjectInput var42 = var2.getInputStream();
               var7 = (ObjID[])((ObjID[])var42.readObject());
               var8 = var42.readLong();
               var39 = (VMID)var42.readObject();
               var41 = var42.readBoolean();
            } catch (IOException var36) {
               throw new UnmarshalException("error unmarshalling arguments", var36);
            } catch (ClassNotFoundException var37) {
               throw new UnmarshalException("error unmarshalling arguments", var37);
            } finally {
               var2.releaseInputStream();
            }

            var6.clean(var7, var8, var39, var41);

            try {
               var2.getResultStream(true);
               break;
            } catch (IOException var35) {
               throw new MarshalException("error marshalling return", var35);
            }
         case 1:
            Lease var10;
            try {
               ObjectInput var11 = var2.getInputStream();
               var7 = (ObjID[])((ObjID[])var11.readObject());
               var8 = var11.readLong();
               var10 = (Lease)var11.readObject();
            } catch (IOException var32) {
               throw new UnmarshalException("error unmarshalling arguments", var32);
            } catch (ClassNotFoundException var33) {
               throw new UnmarshalException("error unmarshalling arguments", var33);
            } finally {
               var2.releaseInputStream();
            }

            Lease var40 = var6.dirty(var7, var8, var10);

            try {
               ObjectOutput var12 = var2.getResultStream(true);
               var12.writeObject(var40);
               break;
            } catch (IOException var31) {
               throw new MarshalException("error marshalling return", var31);
            }
         default:
            throw new UnmarshalException("invalid method number");
         }

      }
   }
}
