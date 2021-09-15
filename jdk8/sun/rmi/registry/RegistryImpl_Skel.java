package sun.rmi.registry;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.rmi.MarshalException;
import java.rmi.Remote;
import java.rmi.UnmarshalException;
import java.rmi.server.Operation;
import java.rmi.server.RemoteCall;
import java.rmi.server.Skeleton;
import java.rmi.server.SkeletonMismatchException;

public final class RegistryImpl_Skel implements Skeleton {
   private static final Operation[] operations = new Operation[]{new Operation("void bind(java.lang.String, java.rmi.Remote)"), new Operation("java.lang.String list()[]"), new Operation("java.rmi.Remote lookup(java.lang.String)"), new Operation("void rebind(java.lang.String, java.rmi.Remote)"), new Operation("void unbind(java.lang.String)")};
   private static final long interfaceHash = 4905912898345647071L;

   public Operation[] getOperations() {
      return (Operation[])operations.clone();
   }

   public void dispatch(Remote var1, RemoteCall var2, int var3, long var4) throws Exception {
      if (var4 != 4905912898345647071L) {
         throw new SkeletonMismatchException("interface hash mismatch");
      } else {
         RegistryImpl var6 = (RegistryImpl)var1;
         String var7;
         ObjectInput var8;
         ObjectInput var9;
         Remote var80;
         switch(var3) {
         case 0:
            RegistryImpl.checkAccess("Registry.bind");

            try {
               var9 = var2.getInputStream();
               var7 = (String)var9.readObject();
               var80 = (Remote)var9.readObject();
            } catch (ClassNotFoundException | IOException var77) {
               throw new UnmarshalException("error unmarshalling arguments", var77);
            } finally {
               var2.releaseInputStream();
            }

            var6.bind(var7, var80);

            try {
               var2.getResultStream(true);
               break;
            } catch (IOException var76) {
               throw new MarshalException("error marshalling return", var76);
            }
         case 1:
            var2.releaseInputStream();
            String[] var79 = var6.list();

            try {
               ObjectOutput var81 = var2.getResultStream(true);
               var81.writeObject(var79);
               break;
            } catch (IOException var75) {
               throw new MarshalException("error marshalling return", var75);
            }
         case 2:
            try {
               var8 = var2.getInputStream();
               var7 = (String)var8.readObject();
            } catch (ClassNotFoundException | IOException var73) {
               throw new UnmarshalException("error unmarshalling arguments", var73);
            } finally {
               var2.releaseInputStream();
            }

            var80 = var6.lookup(var7);

            try {
               ObjectOutput var82 = var2.getResultStream(true);
               var82.writeObject(var80);
               break;
            } catch (IOException var72) {
               throw new MarshalException("error marshalling return", var72);
            }
         case 3:
            RegistryImpl.checkAccess("Registry.rebind");

            try {
               var9 = var2.getInputStream();
               var7 = (String)var9.readObject();
               var80 = (Remote)var9.readObject();
            } catch (ClassNotFoundException | IOException var70) {
               throw new UnmarshalException("error unmarshalling arguments", var70);
            } finally {
               var2.releaseInputStream();
            }

            var6.rebind(var7, var80);

            try {
               var2.getResultStream(true);
               break;
            } catch (IOException var69) {
               throw new MarshalException("error marshalling return", var69);
            }
         case 4:
            RegistryImpl.checkAccess("Registry.unbind");

            try {
               var8 = var2.getInputStream();
               var7 = (String)var8.readObject();
            } catch (ClassNotFoundException | IOException var67) {
               throw new UnmarshalException("error unmarshalling arguments", var67);
            } finally {
               var2.releaseInputStream();
            }

            var6.unbind(var7);

            try {
               var2.getResultStream(true);
               break;
            } catch (IOException var66) {
               throw new MarshalException("error marshalling return", var66);
            }
         default:
            throw new UnmarshalException("invalid method number");
         }

      }
   }
}
