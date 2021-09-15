package com.sun.corba.se.impl.javax.rmi.CORBA;

import com.sun.corba.se.impl.corba.AnyImpl;
import com.sun.corba.se.impl.io.ValueHandlerImpl;
import com.sun.corba.se.impl.logging.UtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.util.IdentityHashtable;
import com.sun.corba.se.impl.util.JDKBridge;
import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.pept.transport.ContactInfoList;
import com.sun.corba.se.spi.copyobject.CopierManager;
import com.sun.corba.se.spi.copyobject.ObjectCopier;
import com.sun.corba.se.spi.copyobject.ReflectiveCopyException;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.protocol.CorbaClientDelegate;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import java.io.NotSerializableException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.rmi.AccessException;
import java.rmi.MarshalException;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.ServerError;
import java.rmi.ServerException;
import java.rmi.UnexpectedException;
import java.rmi.server.RMIClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.EmptyStackException;
import java.util.Enumeration;
import javax.rmi.CORBA.Stub;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.UtilDelegate;
import javax.rmi.CORBA.ValueHandler;
import javax.transaction.InvalidTransactionException;
import javax.transaction.TransactionRequiredException;
import javax.transaction.TransactionRolledbackException;
import org.omg.CORBA.ACTIVITY_COMPLETED;
import org.omg.CORBA.ACTIVITY_REQUIRED;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.INVALID_ACTIVITY;
import org.omg.CORBA.INVALID_TRANSACTION;
import org.omg.CORBA.INV_OBJREF;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.NO_PERMISSION;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.OBJ_ADAPTER;
import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TRANSACTION_REQUIRED;
import org.omg.CORBA.TRANSACTION_ROLLEDBACK;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.UnknownException;
import sun.corba.SharedSecrets;

public class Util implements UtilDelegate {
   private static KeepAlive keepAlive = null;
   private static IdentityHashtable exportedServants = new IdentityHashtable();
   private static final ValueHandlerImpl valueHandlerSingleton = SharedSecrets.getJavaCorbaAccess().newValueHandlerImpl();
   private UtilSystemException utilWrapper = UtilSystemException.get("rpc.encoding");
   private static Util instance = null;

   public Util() {
      setInstance(this);
   }

   private static void setInstance(Util var0) {
      assert instance == null : "Instance already defined";

      instance = var0;
   }

   public static Util getInstance() {
      return instance;
   }

   public static boolean isInstanceDefined() {
      return instance != null;
   }

   public void unregisterTargetsForORB(ORB var1) {
      Enumeration var2 = exportedServants.keys();

      while(var2.hasMoreElements()) {
         Object var3 = var2.nextElement();
         Remote var4 = (Remote)((Remote)(var3 instanceof Tie ? ((Tie)var3).getTarget() : var3));

         try {
            if (var1 == this.getTie(var4).orb()) {
               try {
                  this.unexportObject(var4);
               } catch (NoSuchObjectException var6) {
               }
            }
         } catch (BAD_OPERATION var7) {
         }
      }

   }

   public RemoteException mapSystemException(SystemException var1) {
      if (var1 instanceof UnknownException) {
         Throwable var2 = ((UnknownException)var1).originalEx;
         if (var2 instanceof Error) {
            return new ServerError("Error occurred in server thread", (Error)var2);
         }

         if (var2 instanceof RemoteException) {
            return new ServerException("RemoteException occurred in server thread", (Exception)var2);
         }

         if (var2 instanceof RuntimeException) {
            throw (RuntimeException)var2;
         }
      }

      String var13 = var1.getClass().getName();
      String var3 = var13.substring(var13.lastIndexOf(46) + 1);
      String var4;
      switch(var1.completed.value()) {
      case 0:
         var4 = "Yes";
         break;
      case 1:
         var4 = "No";
         break;
      case 2:
      default:
         var4 = "Maybe";
      }

      String var5 = "CORBA " + var3 + " " + var1.minor + " " + var4;
      if (var1 instanceof COMM_FAILURE) {
         return new MarshalException(var5, var1);
      } else {
         NoSuchObjectException var18;
         if (var1 instanceof INV_OBJREF) {
            var18 = new NoSuchObjectException(var5);
            var18.detail = var1;
            return var18;
         } else if (var1 instanceof NO_PERMISSION) {
            return new AccessException(var5, var1);
         } else if (var1 instanceof MARSHAL) {
            return new MarshalException(var5, var1);
         } else if (var1 instanceof OBJECT_NOT_EXIST) {
            var18 = new NoSuchObjectException(var5);
            var18.detail = var1;
            return var18;
         } else if (var1 instanceof TRANSACTION_REQUIRED) {
            TransactionRequiredException var17 = new TransactionRequiredException(var5);
            var17.detail = var1;
            return var17;
         } else if (var1 instanceof TRANSACTION_ROLLEDBACK) {
            TransactionRolledbackException var16 = new TransactionRolledbackException(var5);
            var16.detail = var1;
            return var16;
         } else if (var1 instanceof INVALID_TRANSACTION) {
            InvalidTransactionException var15 = new InvalidTransactionException(var5);
            var15.detail = var1;
            return var15;
         } else if (var1 instanceof BAD_PARAM) {
            Object var14 = var1;
            if (var1.minor == 1398079489 || var1.minor == 1330446342) {
               if (var1.getMessage() != null) {
                  var14 = new NotSerializableException(var1.getMessage());
               } else {
                  var14 = new NotSerializableException();
               }

               ((Exception)var14).initCause(var1);
            }

            return new MarshalException(var5, (Exception)var14);
         } else {
            Class var6;
            Class[] var7;
            Constructor var8;
            Object[] var9;
            if (var1 instanceof ACTIVITY_REQUIRED) {
               try {
                  var6 = SharedSecrets.getJavaCorbaAccess().loadClass("javax.activity.ActivityRequiredException");
                  var7 = new Class[]{String.class, Throwable.class};
                  var8 = var6.getConstructor(var7);
                  var9 = new Object[]{var5, var1};
                  return (RemoteException)var8.newInstance(var9);
               } catch (Throwable var12) {
                  this.utilWrapper.classNotFound((Throwable)var12, "javax.activity.ActivityRequiredException");
               }
            } else if (var1 instanceof ACTIVITY_COMPLETED) {
               try {
                  var6 = SharedSecrets.getJavaCorbaAccess().loadClass("javax.activity.ActivityCompletedException");
                  var7 = new Class[]{String.class, Throwable.class};
                  var8 = var6.getConstructor(var7);
                  var9 = new Object[]{var5, var1};
                  return (RemoteException)var8.newInstance(var9);
               } catch (Throwable var11) {
                  this.utilWrapper.classNotFound((Throwable)var11, "javax.activity.ActivityCompletedException");
               }
            } else if (var1 instanceof INVALID_ACTIVITY) {
               try {
                  var6 = SharedSecrets.getJavaCorbaAccess().loadClass("javax.activity.InvalidActivityException");
                  var7 = new Class[]{String.class, Throwable.class};
                  var8 = var6.getConstructor(var7);
                  var9 = new Object[]{var5, var1};
                  return (RemoteException)var8.newInstance(var9);
               } catch (Throwable var10) {
                  this.utilWrapper.classNotFound((Throwable)var10, "javax.activity.InvalidActivityException");
               }
            }

            return new RemoteException(var5, var1);
         }
      }
   }

   public void writeAny(OutputStream var1, Object var2) {
      ORB var3 = var1.orb();
      Any var4 = var3.create_any();
      Object var5 = Utility.autoConnect(var2, var3, false);
      if (var5 instanceof org.omg.CORBA.Object) {
         var4.insert_Object((org.omg.CORBA.Object)var5);
      } else if (var5 == null) {
         var4.insert_Value((Serializable)null, this.createTypeCodeForNull(var3));
      } else if (var5 instanceof Serializable) {
         TypeCode var6 = this.createTypeCode((Serializable)var5, var4, var3);
         if (var6 == null) {
            var4.insert_Value((Serializable)var5);
         } else {
            var4.insert_Value((Serializable)var5, var6);
         }
      } else if (var5 instanceof Remote) {
         ORBUtility.throwNotSerializableForCorba(var5.getClass().getName());
      } else {
         ORBUtility.throwNotSerializableForCorba(var5.getClass().getName());
      }

      var1.write_any(var4);
   }

   private TypeCode createTypeCode(Serializable var1, Any var2, ORB var3) {
      if (var2 instanceof AnyImpl && var3 instanceof com.sun.corba.se.spi.orb.ORB) {
         AnyImpl var4 = (AnyImpl)var2;
         com.sun.corba.se.spi.orb.ORB var5 = (com.sun.corba.se.spi.orb.ORB)var3;
         return var4.createTypeCodeForClass(var1.getClass(), var5);
      } else {
         return null;
      }
   }

   private TypeCode createTypeCodeForNull(ORB var1) {
      if (var1 instanceof com.sun.corba.se.spi.orb.ORB) {
         com.sun.corba.se.spi.orb.ORB var2 = (com.sun.corba.se.spi.orb.ORB)var1;
         if (!ORBVersionFactory.getFOREIGN().equals(var2.getORBVersion()) && ORBVersionFactory.getNEWER().compareTo(var2.getORBVersion()) > 0) {
            return var1.get_primitive_tc(TCKind.tk_value);
         }
      }

      String var3 = "IDL:omg.org/CORBA/AbstractBase:1.0";
      return var1.create_abstract_interface_tc(var3, "");
   }

   public Object readAny(InputStream var1) {
      Any var2 = var1.read_any();
      return var2.type().kind().value() == 14 ? var2.extract_Object() : var2.extract_Value();
   }

   public void writeRemoteObject(OutputStream var1, Object var2) {
      Object var3 = Utility.autoConnect(var2, var1.orb(), false);
      var1.write_Object((org.omg.CORBA.Object)var3);
   }

   public void writeAbstractObject(OutputStream var1, Object var2) {
      Object var3 = Utility.autoConnect(var2, var1.orb(), false);
      ((org.omg.CORBA_2_3.portable.OutputStream)var1).write_abstract_interface(var3);
   }

   public void registerTarget(Tie var1, Remote var2) {
      synchronized(exportedServants) {
         if (lookupTie(var2) == null) {
            exportedServants.put(var2, var1);
            var1.setTarget(var2);
            if (keepAlive == null) {
               keepAlive = (KeepAlive)AccessController.doPrivileged(new PrivilegedAction() {
                  public Object run() {
                     return new KeepAlive();
                  }
               });
               keepAlive.start();
            }
         }

      }
   }

   public void unexportObject(Remote var1) throws NoSuchObjectException {
      synchronized(exportedServants) {
         Tie var3 = lookupTie(var1);
         if (var3 != null) {
            exportedServants.remove(var1);
            Utility.purgeStubForTie(var3);
            Utility.purgeTieAndServant(var3);

            try {
               this.cleanUpTie(var3);
            } catch (BAD_OPERATION var6) {
            } catch (OBJ_ADAPTER var7) {
            }

            if (exportedServants.isEmpty()) {
               keepAlive.quit();
               keepAlive = null;
            }

         } else {
            throw new NoSuchObjectException("Tie not found");
         }
      }
   }

   protected void cleanUpTie(Tie var1) throws NoSuchObjectException {
      var1.setTarget((Remote)null);
      var1.deactivate();
   }

   public Tie getTie(Remote var1) {
      synchronized(exportedServants) {
         return lookupTie(var1);
      }
   }

   private static Tie lookupTie(Remote var0) {
      Tie var1 = (Tie)exportedServants.get(var0);
      if (var1 == null && var0 instanceof Tie && exportedServants.contains(var0)) {
         var1 = (Tie)var0;
      }

      return var1;
   }

   public ValueHandler createValueHandler() {
      return valueHandlerSingleton;
   }

   public String getCodebase(Class var1) {
      return RMIClassLoader.getClassAnnotation(var1);
   }

   public Class loadClass(String var1, String var2, ClassLoader var3) throws ClassNotFoundException {
      return JDKBridge.loadClass(var1, var2, var3);
   }

   public boolean isLocal(Stub var1) throws RemoteException {
      boolean var2 = false;

      try {
         Delegate var3 = var1._get_delegate();
         if (var3 instanceof CorbaClientDelegate) {
            CorbaClientDelegate var4 = (CorbaClientDelegate)var3;
            ContactInfoList var5 = var4.getContactInfoList();
            if (var5 instanceof CorbaContactInfoList) {
               CorbaContactInfoList var6 = (CorbaContactInfoList)var5;
               LocalClientRequestDispatcher var7 = var6.getLocalClientRequestDispatcher();
               var2 = var7.useLocalInvocation((org.omg.CORBA.Object)null);
            }
         } else {
            var2 = var3.is_local(var1);
         }

         return var2;
      } catch (SystemException var8) {
         throw javax.rmi.CORBA.Util.mapSystemException(var8);
      }
   }

   public RemoteException wrapException(Throwable var1) {
      if (var1 instanceof SystemException) {
         return this.mapSystemException((SystemException)var1);
      } else if (var1 instanceof Error) {
         return new ServerError("Error occurred in server thread", (Error)var1);
      } else if (var1 instanceof RemoteException) {
         return new ServerException("RemoteException occurred in server thread", (Exception)var1);
      } else if (var1 instanceof RuntimeException) {
         throw (RuntimeException)var1;
      } else {
         return var1 instanceof Exception ? new UnexpectedException(var1.toString(), (Exception)var1) : new UnexpectedException(var1.toString());
      }
   }

   public Object[] copyObjects(Object[] var1, ORB var2) throws RemoteException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         Class var3 = var1.getClass().getComponentType();
         if (Remote.class.isAssignableFrom(var3) && !var3.isInterface()) {
            Remote[] var4 = new Remote[var1.length];
            System.arraycopy(var1, 0, var4, 0, var1.length);
            return (Object[])((Object[])this.copyObject(var4, var2));
         } else {
            return (Object[])((Object[])this.copyObject(var1, var2));
         }
      }
   }

   public Object copyObject(Object var1, ORB var2) throws RemoteException {
      if (var2 instanceof com.sun.corba.se.spi.orb.ORB) {
         com.sun.corba.se.spi.orb.ORB var10 = (com.sun.corba.se.spi.orb.ORB)var2;

         try {
            try {
               return var10.peekInvocationInfo().getCopierFactory().make().copy(var1);
            } catch (EmptyStackException var7) {
               CopierManager var9 = var10.getCopierManager();
               ObjectCopier var6 = var9.getDefaultObjectCopierFactory().make();
               return var6.copy(var1);
            }
         } catch (ReflectiveCopyException var8) {
            RemoteException var5 = new RemoteException();
            var5.initCause(var8);
            throw var5;
         }
      } else {
         org.omg.CORBA_2_3.portable.OutputStream var3 = (org.omg.CORBA_2_3.portable.OutputStream)var2.create_output_stream();
         var3.write_value((Serializable)var1);
         org.omg.CORBA_2_3.portable.InputStream var4 = (org.omg.CORBA_2_3.portable.InputStream)var3.create_input_stream();
         return var4.read_value();
      }
   }
}
