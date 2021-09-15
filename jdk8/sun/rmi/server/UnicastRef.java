package sun.rmi.server;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.rmi.MarshalException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;
import java.rmi.server.Operation;
import java.rmi.server.RemoteCall;
import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteRef;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.rmi.runtime.Log;
import sun.rmi.transport.Connection;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.StreamRemoteCall;
import sun.security.action.GetBooleanAction;

public class UnicastRef implements RemoteRef {
   public static final Log clientRefLog;
   public static final Log clientCallLog;
   private static final long serialVersionUID = 8258372400816541186L;
   protected LiveRef ref;

   public UnicastRef() {
   }

   public UnicastRef(LiveRef var1) {
      this.ref = var1;
   }

   public LiveRef getLiveRef() {
      return this.ref;
   }

   public Object invoke(Remote var1, Method var2, Object[] var3, long var4) throws Exception {
      if (clientRefLog.isLoggable(Log.VERBOSE)) {
         clientRefLog.log(Log.VERBOSE, "method: " + var2);
      }

      if (clientCallLog.isLoggable(Log.VERBOSE)) {
         this.logClientCall(var1, var2);
      }

      Connection var6 = this.ref.getChannel().newConnection();
      StreamRemoteCall var7 = null;
      boolean var8 = true;
      boolean var9 = false;

      Object var13;
      try {
         if (clientRefLog.isLoggable(Log.VERBOSE)) {
            clientRefLog.log(Log.VERBOSE, "opnum = " + var4);
         }

         var7 = new StreamRemoteCall(var6, this.ref.getObjID(), -1, var4);

         Object var11;
         try {
            ObjectOutput var10 = var7.getOutputStream();
            this.marshalCustomCallData(var10);
            var11 = var2.getParameterTypes();

            for(int var12 = 0; var12 < ((Object[])var11).length; ++var12) {
               marshalValue((Class)((Object[])var11)[var12], var3[var12], var10);
            }
         } catch (IOException var39) {
            clientRefLog.log(Log.BRIEF, "IOException marshalling arguments: ", var39);
            throw new MarshalException("error marshalling arguments", var39);
         }

         var7.executeCall();

         try {
            Class var46 = var2.getReturnType();
            if (var46 == Void.TYPE) {
               var11 = null;
               return var11;
            }

            var11 = var7.getInputStream();
            Object var47 = unmarshalValue(var46, (ObjectInput)var11);
            var9 = true;
            clientRefLog.log(Log.BRIEF, "free connection (reuse = true)");
            this.ref.getChannel().free(var6, true);
            var13 = var47;
         } catch (ClassNotFoundException | IOException var40) {
            ((StreamRemoteCall)var7).discardPendingRefs();
            clientRefLog.log(Log.BRIEF, var40.getClass().getName() + " unmarshalling return: ", var40);
            throw new UnmarshalException("error unmarshalling return", var40);
         } finally {
            try {
               var7.done();
            } catch (IOException var38) {
               var8 = false;
            }

         }
      } catch (RuntimeException var42) {
         if (var7 == null || ((StreamRemoteCall)var7).getServerException() != var42) {
            var8 = false;
         }

         throw var42;
      } catch (RemoteException var43) {
         var8 = false;
         throw var43;
      } catch (Error var44) {
         var8 = false;
         throw var44;
      } finally {
         if (!var9) {
            if (clientRefLog.isLoggable(Log.BRIEF)) {
               clientRefLog.log(Log.BRIEF, "free connection (reuse = " + var8 + ")");
            }

            this.ref.getChannel().free(var6, var8);
         }

      }

      return var13;
   }

   protected void marshalCustomCallData(ObjectOutput var1) throws IOException {
   }

   protected static void marshalValue(Class<?> var0, Object var1, ObjectOutput var2) throws IOException {
      if (var0.isPrimitive()) {
         if (var0 == Integer.TYPE) {
            var2.writeInt((Integer)var1);
         } else if (var0 == Boolean.TYPE) {
            var2.writeBoolean((Boolean)var1);
         } else if (var0 == Byte.TYPE) {
            var2.writeByte((Byte)var1);
         } else if (var0 == Character.TYPE) {
            var2.writeChar((Character)var1);
         } else if (var0 == Short.TYPE) {
            var2.writeShort((Short)var1);
         } else if (var0 == Long.TYPE) {
            var2.writeLong((Long)var1);
         } else if (var0 == Float.TYPE) {
            var2.writeFloat((Float)var1);
         } else {
            if (var0 != Double.TYPE) {
               throw new Error("Unrecognized primitive type: " + var0);
            }

            var2.writeDouble((Double)var1);
         }
      } else {
         var2.writeObject(var1);
      }

   }

   protected static Object unmarshalValue(Class<?> var0, ObjectInput var1) throws IOException, ClassNotFoundException {
      if (var0.isPrimitive()) {
         if (var0 == Integer.TYPE) {
            return var1.readInt();
         } else if (var0 == Boolean.TYPE) {
            return var1.readBoolean();
         } else if (var0 == Byte.TYPE) {
            return var1.readByte();
         } else if (var0 == Character.TYPE) {
            return var1.readChar();
         } else if (var0 == Short.TYPE) {
            return var1.readShort();
         } else if (var0 == Long.TYPE) {
            return var1.readLong();
         } else if (var0 == Float.TYPE) {
            return var1.readFloat();
         } else if (var0 == Double.TYPE) {
            return var1.readDouble();
         } else {
            throw new Error("Unrecognized primitive type: " + var0);
         }
      } else {
         return var1.readObject();
      }
   }

   public RemoteCall newCall(RemoteObject var1, Operation[] var2, int var3, long var4) throws RemoteException {
      clientRefLog.log(Log.BRIEF, "get connection");
      Connection var6 = this.ref.getChannel().newConnection();

      try {
         clientRefLog.log(Log.VERBOSE, "create call context");
         if (clientCallLog.isLoggable(Log.VERBOSE)) {
            this.logClientCall(var1, var2[var3]);
         }

         StreamRemoteCall var7 = new StreamRemoteCall(var6, this.ref.getObjID(), var3, var4);

         try {
            this.marshalCustomCallData(var7.getOutputStream());
         } catch (IOException var9) {
            throw new MarshalException("error marshaling custom call data");
         }

         return var7;
      } catch (RemoteException var10) {
         this.ref.getChannel().free(var6, false);
         throw var10;
      }
   }

   public void invoke(RemoteCall var1) throws Exception {
      try {
         clientRefLog.log(Log.VERBOSE, "execute call");
         var1.executeCall();
      } catch (RemoteException var3) {
         clientRefLog.log(Log.BRIEF, "exception: ", var3);
         this.free(var1, false);
         throw var3;
      } catch (Error var4) {
         clientRefLog.log(Log.BRIEF, "error: ", var4);
         this.free(var1, false);
         throw var4;
      } catch (RuntimeException var5) {
         clientRefLog.log(Log.BRIEF, "exception: ", var5);
         this.free(var1, false);
         throw var5;
      } catch (Exception var6) {
         clientRefLog.log(Log.BRIEF, "exception: ", var6);
         this.free(var1, true);
         throw var6;
      }
   }

   private void free(RemoteCall var1, boolean var2) throws RemoteException {
      Connection var3 = ((StreamRemoteCall)var1).getConnection();
      this.ref.getChannel().free(var3, var2);
   }

   public void done(RemoteCall var1) throws RemoteException {
      clientRefLog.log(Log.BRIEF, "free connection (reuse = true)");
      this.free(var1, true);

      try {
         var1.done();
      } catch (IOException var3) {
      }

   }

   void logClientCall(Object var1, Object var2) {
      clientCallLog.log(Log.VERBOSE, "outbound call: " + this.ref + " : " + var1.getClass().getName() + this.ref.getObjID().toString() + ": " + var2);
   }

   public String getRefClass(ObjectOutput var1) {
      return "UnicastRef";
   }

   public void writeExternal(ObjectOutput var1) throws IOException {
      this.ref.write(var1, false);
   }

   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      this.ref = LiveRef.read(var1, false);
   }

   public String remoteToString() {
      return Util.getUnqualifiedName(this.getClass()) + " [liveRef: " + this.ref + "]";
   }

   public int remoteHashCode() {
      return this.ref.hashCode();
   }

   public boolean remoteEquals(RemoteRef var1) {
      return var1 instanceof UnicastRef ? this.ref.remoteEquals(((UnicastRef)var1).ref) : false;
   }

   static {
      clientRefLog = Log.getLog("sun.rmi.client.ref", "transport", Util.logLevel);
      clientCallLog = Log.getLog("sun.rmi.client.call", "RMI", (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("sun.rmi.client.logCalls"))));
   }
}
