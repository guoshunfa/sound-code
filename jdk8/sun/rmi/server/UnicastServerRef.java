package sun.rmi.server;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectStreamClass;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.AccessException;
import java.rmi.MarshalException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.ServerError;
import java.rmi.ServerException;
import java.rmi.UnmarshalException;
import java.rmi.server.ExportException;
import java.rmi.server.RemoteCall;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteStub;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.ServerRef;
import java.rmi.server.Skeleton;
import java.rmi.server.SkeletonNotFoundException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import sun.misc.ObjectInputFilter;
import sun.rmi.runtime.Log;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.StreamRemoteCall;
import sun.rmi.transport.Target;
import sun.rmi.transport.tcp.TCPTransport;
import sun.security.action.GetBooleanAction;

public class UnicastServerRef extends UnicastRef implements ServerRef, Dispatcher {
   public static final boolean logCalls = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("java.rmi.server.logCalls")));
   public static final Log callLog;
   private static final long serialVersionUID = -7384275867073752268L;
   private static final boolean wantExceptionLog;
   private boolean forceStubUse;
   private static final boolean suppressStackTraces;
   private transient Skeleton skel;
   private final transient ObjectInputFilter filter;
   private transient Map<Long, Method> hashToMethod_Map;
   private static final WeakClassHashMap<Map<Long, Method>> hashToMethod_Maps;
   private static final Map<Class<?>, ?> withoutSkeletons;
   private final AtomicInteger methodCallIDCount;

   public UnicastServerRef() {
      this.forceStubUse = false;
      this.hashToMethod_Map = null;
      this.methodCallIDCount = new AtomicInteger(0);
      this.filter = null;
   }

   public UnicastServerRef(LiveRef var1) {
      super(var1);
      this.forceStubUse = false;
      this.hashToMethod_Map = null;
      this.methodCallIDCount = new AtomicInteger(0);
      this.filter = null;
   }

   public UnicastServerRef(LiveRef var1, ObjectInputFilter var2) {
      super(var1);
      this.forceStubUse = false;
      this.hashToMethod_Map = null;
      this.methodCallIDCount = new AtomicInteger(0);
      this.filter = var2;
   }

   public UnicastServerRef(int var1) {
      super(new LiveRef(var1));
      this.forceStubUse = false;
      this.hashToMethod_Map = null;
      this.methodCallIDCount = new AtomicInteger(0);
      this.filter = null;
   }

   public UnicastServerRef(boolean var1) {
      this(0);
      this.forceStubUse = var1;
   }

   public RemoteStub exportObject(Remote var1, Object var2) throws RemoteException {
      this.forceStubUse = true;
      return (RemoteStub)this.exportObject(var1, var2, false);
   }

   public Remote exportObject(Remote var1, Object var2, boolean var3) throws RemoteException {
      Class var4 = var1.getClass();

      Remote var5;
      try {
         var5 = Util.createProxy(var4, this.getClientRef(), this.forceStubUse);
      } catch (IllegalArgumentException var7) {
         throw new ExportException("remote object implements illegal remote interface", var7);
      }

      if (var5 instanceof RemoteStub) {
         this.setSkeleton(var1);
      }

      Target var6 = new Target(var1, this, var5, this.ref.getObjID(), var3);
      this.ref.exportObject(var6);
      this.hashToMethod_Map = (Map)hashToMethod_Maps.get(var4);
      return var5;
   }

   public String getClientHost() throws ServerNotActiveException {
      return TCPTransport.getClientHost();
   }

   public void setSkeleton(Remote var1) throws RemoteException {
      if (!withoutSkeletons.containsKey(var1.getClass())) {
         try {
            this.skel = Util.createSkeleton(var1);
         } catch (SkeletonNotFoundException var3) {
            withoutSkeletons.put(var1.getClass(), (Object)null);
         }
      }

   }

   public void dispatch(Remote var1, RemoteCall var2) throws IOException {
      try {
         int var3;
         ObjectInput var41;
         try {
            var41 = var2.getInputStream();
            var3 = var41.readInt();
         } catch (Exception var38) {
            throw new UnmarshalException("error unmarshalling call header", var38);
         }

         if (var3 >= 0) {
            if (this.skel != null) {
               this.oldDispatch(var1, var2, var3);
               return;
            }

            throw new UnmarshalException("skeleton class not found but required for client version");
         }

         long var4;
         try {
            var4 = var41.readLong();
         } catch (Exception var37) {
            throw new UnmarshalException("error unmarshalling call header", var37);
         }

         MarshalInputStream var7 = (MarshalInputStream)var41;
         var7.skipDefaultResolveClass();
         Method var42 = (Method)this.hashToMethod_Map.get(var4);
         if (var42 == null) {
            throw new UnmarshalException("unrecognized method hash: method not supported by remote object");
         }

         this.logCall(var1, var42);
         Object[] var9 = null;

         try {
            this.unmarshalCustomCallData(var41);
            var9 = this.unmarshalParameters(var1, var42, var7);
         } catch (AccessException var34) {
            ((StreamRemoteCall)var2).discardPendingRefs();
            throw var34;
         } catch (ClassNotFoundException | IOException var35) {
            ((StreamRemoteCall)var2).discardPendingRefs();
            throw new UnmarshalException("error unmarshalling arguments", var35);
         } finally {
            var2.releaseInputStream();
         }

         Object var10;
         try {
            var10 = var42.invoke(var1, var9);
         } catch (InvocationTargetException var33) {
            throw var33.getTargetException();
         }

         try {
            ObjectOutput var11 = var2.getResultStream(true);
            Class var12 = var42.getReturnType();
            if (var12 != Void.TYPE) {
               marshalValue(var12, var10, var11);
            }
         } catch (IOException var32) {
            throw new MarshalException("error marshalling return", var32);
         }
      } catch (Throwable var39) {
         Object var6 = var39;
         this.logCallException(var39);
         ObjectOutput var8 = var2.getResultStream(false);
         if (var39 instanceof Error) {
            var6 = new ServerError("Error occurred in server thread", (Error)var39);
         } else if (var39 instanceof RemoteException) {
            var6 = new ServerException("RemoteException occurred in server thread", (Exception)var39);
         }

         if (suppressStackTraces) {
            clearStackTraces((Throwable)var6);
         }

         var8.writeObject(var6);
         if (var39 instanceof AccessException) {
            throw new IOException("Connection is not reusable", var39);
         }
      } finally {
         var2.releaseInputStream();
         var2.releaseOutputStream();
      }

   }

   protected void unmarshalCustomCallData(ObjectInput var1) throws IOException, ClassNotFoundException {
      if (this.filter != null && var1 instanceof ObjectInputStream) {
         final ObjectInputStream var2 = (ObjectInputStream)var1;
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               ObjectInputFilter.Config.setObjectInputFilter(var2, UnicastServerRef.this.filter);
               return null;
            }
         });
      }

   }

   private void oldDispatch(Remote var1, RemoteCall var2, int var3) throws Exception {
      ObjectInput var6 = var2.getInputStream();

      try {
         Class var7 = Class.forName("sun.rmi.transport.DGCImpl_Skel");
         if (var7.isAssignableFrom(this.skel.getClass())) {
            ((MarshalInputStream)var6).useCodebaseOnly();
         }
      } catch (ClassNotFoundException var9) {
      }

      long var4;
      try {
         var4 = var6.readLong();
      } catch (Exception var8) {
         throw new UnmarshalException("error unmarshalling call header", var8);
      }

      this.logCall(var1, this.skel.getOperations()[var3]);
      this.unmarshalCustomCallData(var6);
      this.skel.dispatch(var1, var2, var3, var4);
   }

   public static void clearStackTraces(Throwable var0) {
      for(StackTraceElement[] var1 = new StackTraceElement[0]; var0 != null; var0 = var0.getCause()) {
         var0.setStackTrace(var1);
      }

   }

   private void logCall(Remote var1, Object var2) {
      if (callLog.isLoggable(Log.VERBOSE)) {
         String var3;
         try {
            var3 = this.getClientHost();
         } catch (ServerNotActiveException var5) {
            var3 = "(local)";
         }

         callLog.log(Log.VERBOSE, "[" + var3 + ": " + var1.getClass().getName() + this.ref.getObjID().toString() + ": " + var2 + "]");
      }

   }

   private void logCallException(Throwable var1) {
      if (callLog.isLoggable(Log.BRIEF)) {
         String var2 = "";

         try {
            var2 = "[" + this.getClientHost() + "] ";
         } catch (ServerNotActiveException var6) {
         }

         callLog.log(Log.BRIEF, var2 + "exception: ", var1);
      }

      if (wantExceptionLog) {
         PrintStream var7 = System.err;
         synchronized(var7) {
            var7.println();
            var7.println("Exception dispatching call to " + this.ref.getObjID() + " in thread \"" + Thread.currentThread().getName() + "\" at " + new Date() + ":");
            var1.printStackTrace(var7);
         }
      }

   }

   public String getRefClass(ObjectOutput var1) {
      return "UnicastServerRef";
   }

   protected RemoteRef getClientRef() {
      return new UnicastRef(this.ref);
   }

   public void writeExternal(ObjectOutput var1) throws IOException {
   }

   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      this.ref = null;
      this.skel = null;
   }

   private Object[] unmarshalParameters(Object var1, Method var2, MarshalInputStream var3) throws IOException, ClassNotFoundException {
      return var1 instanceof DeserializationChecker ? this.unmarshalParametersChecked((DeserializationChecker)var1, var2, var3) : this.unmarshalParametersUnchecked(var2, var3);
   }

   private Object[] unmarshalParametersUnchecked(Method var1, ObjectInput var2) throws IOException, ClassNotFoundException {
      Class[] var3 = var1.getParameterTypes();
      Object[] var4 = new Object[var3.length];

      for(int var5 = 0; var5 < var3.length; ++var5) {
         var4[var5] = unmarshalValue(var3[var5], var2);
      }

      return var4;
   }

   private Object[] unmarshalParametersChecked(DeserializationChecker var1, Method var2, MarshalInputStream var3) throws IOException, ClassNotFoundException {
      int var4 = this.methodCallIDCount.getAndIncrement();
      UnicastServerRef.MyChecker var5 = new UnicastServerRef.MyChecker(var1, var2, var4);
      var3.setStreamChecker(var5);

      try {
         Class[] var6 = var2.getParameterTypes();
         Object[] var7 = new Object[var6.length];

         for(int var8 = 0; var8 < var6.length; ++var8) {
            var5.setIndex(var8);
            var7[var8] = unmarshalValue(var6[var8], var3);
         }

         var5.end(var4);
         Object[] var12 = var7;
         return var12;
      } finally {
         var3.setStreamChecker((MarshalInputStream.StreamChecker)null);
      }
   }

   static {
      callLog = Log.getLog("sun.rmi.server.call", "RMI", logCalls);
      wantExceptionLog = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("sun.rmi.server.exceptionTrace")));
      suppressStackTraces = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("sun.rmi.server.suppressStackTraces")));
      hashToMethod_Maps = new UnicastServerRef.HashToMethod_Maps();
      withoutSkeletons = Collections.synchronizedMap(new WeakHashMap());
   }

   private static class MyChecker implements MarshalInputStream.StreamChecker {
      private final DeserializationChecker descriptorCheck;
      private final Method method;
      private final int callID;
      private int parameterIndex;

      MyChecker(DeserializationChecker var1, Method var2, int var3) {
         this.descriptorCheck = var1;
         this.method = var2;
         this.callID = var3;
      }

      public void validateDescriptor(ObjectStreamClass var1) {
         this.descriptorCheck.check(this.method, var1, this.parameterIndex, this.callID);
      }

      public void checkProxyInterfaceNames(String[] var1) {
         this.descriptorCheck.checkProxyClass(this.method, var1, this.parameterIndex, this.callID);
      }

      void setIndex(int var1) {
         this.parameterIndex = var1;
      }

      void end(int var1) {
         this.descriptorCheck.end(var1);
      }
   }

   private static class HashToMethod_Maps extends WeakClassHashMap<Map<Long, Method>> {
      HashToMethod_Maps() {
      }

      protected Map<Long, Method> computeValue(Class<?> var1) {
         HashMap var2 = new HashMap();

         for(Class var3 = var1; var3 != null; var3 = var3.getSuperclass()) {
            Class[] var4 = var3.getInterfaces();
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               Class var7 = var4[var6];
               if (Remote.class.isAssignableFrom(var7)) {
                  Method[] var8 = var7.getMethods();
                  int var9 = var8.length;

                  for(int var10 = 0; var10 < var9; ++var10) {
                     final Method var11 = var8[var10];
                     AccessController.doPrivileged(new PrivilegedAction<Void>() {
                        public Void run() {
                           var11.setAccessible(true);
                           return null;
                        }
                     });
                     var2.put(Util.computeMethodHash(var11), var11);
                  }
               }
            }
         }

         return var2;
      }
   }
}
