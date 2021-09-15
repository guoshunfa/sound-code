package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.presentation.rmi.DynamicMethodMarshaller;
import java.io.Externalizable;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import javax.rmi.PortableRemoteObject;
import javax.rmi.CORBA.Util;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.IDLEntity;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class DynamicMethodMarshallerImpl implements DynamicMethodMarshaller {
   Method method;
   ExceptionHandler ehandler;
   boolean hasArguments = true;
   boolean hasVoidResult = true;
   boolean needsArgumentCopy;
   boolean needsResultCopy;
   DynamicMethodMarshallerImpl.ReaderWriter[] argRWs = null;
   DynamicMethodMarshallerImpl.ReaderWriter resultRW = null;
   private static DynamicMethodMarshallerImpl.ReaderWriter booleanRW = new DynamicMethodMarshallerImpl.ReaderWriterBase("boolean") {
      public Object read(InputStream var1) {
         boolean var2 = var1.read_boolean();
         return new Boolean(var2);
      }

      public void write(OutputStream var1, Object var2) {
         Boolean var3 = (Boolean)var2;
         var1.write_boolean(var3);
      }
   };
   private static DynamicMethodMarshallerImpl.ReaderWriter byteRW = new DynamicMethodMarshallerImpl.ReaderWriterBase("byte") {
      public Object read(InputStream var1) {
         byte var2 = var1.read_octet();
         return new Byte(var2);
      }

      public void write(OutputStream var1, Object var2) {
         Byte var3 = (Byte)var2;
         var1.write_octet(var3);
      }
   };
   private static DynamicMethodMarshallerImpl.ReaderWriter charRW = new DynamicMethodMarshallerImpl.ReaderWriterBase("char") {
      public Object read(InputStream var1) {
         char var2 = var1.read_wchar();
         return new Character(var2);
      }

      public void write(OutputStream var1, Object var2) {
         Character var3 = (Character)var2;
         var1.write_wchar(var3);
      }
   };
   private static DynamicMethodMarshallerImpl.ReaderWriter shortRW = new DynamicMethodMarshallerImpl.ReaderWriterBase("short") {
      public Object read(InputStream var1) {
         short var2 = var1.read_short();
         return new Short(var2);
      }

      public void write(OutputStream var1, Object var2) {
         Short var3 = (Short)var2;
         var1.write_short(var3);
      }
   };
   private static DynamicMethodMarshallerImpl.ReaderWriter intRW = new DynamicMethodMarshallerImpl.ReaderWriterBase("int") {
      public Object read(InputStream var1) {
         int var2 = var1.read_long();
         return new Integer(var2);
      }

      public void write(OutputStream var1, Object var2) {
         Integer var3 = (Integer)var2;
         var1.write_long(var3);
      }
   };
   private static DynamicMethodMarshallerImpl.ReaderWriter longRW = new DynamicMethodMarshallerImpl.ReaderWriterBase("long") {
      public Object read(InputStream var1) {
         long var2 = var1.read_longlong();
         return new Long(var2);
      }

      public void write(OutputStream var1, Object var2) {
         Long var3 = (Long)var2;
         var1.write_longlong(var3);
      }
   };
   private static DynamicMethodMarshallerImpl.ReaderWriter floatRW = new DynamicMethodMarshallerImpl.ReaderWriterBase("float") {
      public Object read(InputStream var1) {
         float var2 = var1.read_float();
         return new Float(var2);
      }

      public void write(OutputStream var1, Object var2) {
         Float var3 = (Float)var2;
         var1.write_float(var3);
      }
   };
   private static DynamicMethodMarshallerImpl.ReaderWriter doubleRW = new DynamicMethodMarshallerImpl.ReaderWriterBase("double") {
      public Object read(InputStream var1) {
         double var2 = var1.read_double();
         return new Double(var2);
      }

      public void write(OutputStream var1, Object var2) {
         Double var3 = (Double)var2;
         var1.write_double(var3);
      }
   };
   private static DynamicMethodMarshallerImpl.ReaderWriter corbaObjectRW = new DynamicMethodMarshallerImpl.ReaderWriterBase("org.omg.CORBA.Object") {
      public Object read(InputStream var1) {
         return var1.read_Object();
      }

      public void write(OutputStream var1, Object var2) {
         var1.write_Object((org.omg.CORBA.Object)var2);
      }
   };
   private static DynamicMethodMarshallerImpl.ReaderWriter anyRW = new DynamicMethodMarshallerImpl.ReaderWriterBase("any") {
      public Object read(InputStream var1) {
         return Util.readAny(var1);
      }

      public void write(OutputStream var1, Object var2) {
         Util.writeAny(var1, var2);
      }
   };
   private static DynamicMethodMarshallerImpl.ReaderWriter abstractInterfaceRW = new DynamicMethodMarshallerImpl.ReaderWriterBase("abstract_interface") {
      public Object read(InputStream var1) {
         return var1.read_abstract_interface();
      }

      public void write(OutputStream var1, Object var2) {
         Util.writeAbstractObject(var1, var2);
      }
   };

   private static boolean isAnyClass(Class var0) {
      return var0.equals(Object.class) || var0.equals(Serializable.class) || var0.equals(Externalizable.class);
   }

   private static boolean isAbstractInterface(Class var0) {
      if (IDLEntity.class.isAssignableFrom(var0)) {
         return var0.isInterface();
      } else {
         return var0.isInterface() && allMethodsThrowRemoteException(var0);
      }
   }

   private static boolean allMethodsThrowRemoteException(Class var0) {
      Method[] var1 = var0.getMethods();

      for(int var2 = 0; var2 < var1.length; ++var2) {
         Method var3 = var1[var2];
         if (var3.getDeclaringClass() != Object.class && !throwsRemote(var3)) {
            return false;
         }
      }

      return true;
   }

   private static boolean throwsRemote(Method var0) {
      Class[] var1 = var0.getExceptionTypes();

      for(int var2 = 0; var2 < var1.length; ++var2) {
         Class var3 = var1[var2];
         if (RemoteException.class.isAssignableFrom(var3)) {
            return true;
         }
      }

      return false;
   }

   public static DynamicMethodMarshallerImpl.ReaderWriter makeReaderWriter(final Class var0) {
      if (var0.equals(Boolean.TYPE)) {
         return booleanRW;
      } else if (var0.equals(Byte.TYPE)) {
         return byteRW;
      } else if (var0.equals(Character.TYPE)) {
         return charRW;
      } else if (var0.equals(Short.TYPE)) {
         return shortRW;
      } else if (var0.equals(Integer.TYPE)) {
         return intRW;
      } else if (var0.equals(Long.TYPE)) {
         return longRW;
      } else if (var0.equals(Float.TYPE)) {
         return floatRW;
      } else if (var0.equals(Double.TYPE)) {
         return doubleRW;
      } else if (Remote.class.isAssignableFrom(var0)) {
         return new DynamicMethodMarshallerImpl.ReaderWriterBase("remote(" + var0.getName() + ")") {
            public Object read(InputStream var1) {
               return PortableRemoteObject.narrow(var1.read_Object(), var0);
            }

            public void write(OutputStream var1, Object var2) {
               Util.writeRemoteObject(var1, var2);
            }
         };
      } else if (var0.equals(org.omg.CORBA.Object.class)) {
         return corbaObjectRW;
      } else if (org.omg.CORBA.Object.class.isAssignableFrom(var0)) {
         return new DynamicMethodMarshallerImpl.ReaderWriterBase("org.omg.CORBA.Object(" + var0.getName() + ")") {
            public Object read(InputStream var1) {
               return var1.read_Object(var0);
            }

            public void write(OutputStream var1, Object var2) {
               var1.write_Object((org.omg.CORBA.Object)var2);
            }
         };
      } else if (isAnyClass(var0)) {
         return anyRW;
      } else {
         return (DynamicMethodMarshallerImpl.ReaderWriter)(isAbstractInterface(var0) ? abstractInterfaceRW : new DynamicMethodMarshallerImpl.ReaderWriterBase("value(" + var0.getName() + ")") {
            public Object read(InputStream var1) {
               return var1.read_value(var0);
            }

            public void write(OutputStream var1, Object var2) {
               var1.write_value((Serializable)var2, var0);
            }
         });
      }
   }

   public DynamicMethodMarshallerImpl(Method var1) {
      this.method = var1;
      this.ehandler = new ExceptionHandlerImpl(var1.getExceptionTypes());
      this.needsArgumentCopy = false;
      Class[] var2 = var1.getParameterTypes();
      this.hasArguments = var2.length > 0;
      if (this.hasArguments) {
         this.argRWs = new DynamicMethodMarshallerImpl.ReaderWriter[var2.length];

         for(int var3 = 0; var3 < var2.length; ++var3) {
            if (!var2[var3].isPrimitive()) {
               this.needsArgumentCopy = true;
            }

            this.argRWs[var3] = makeReaderWriter(var2[var3]);
         }
      }

      Class var4 = var1.getReturnType();
      this.needsResultCopy = false;
      this.hasVoidResult = var4.equals(Void.TYPE);
      if (!this.hasVoidResult) {
         this.needsResultCopy = !var4.isPrimitive();
         this.resultRW = makeReaderWriter(var4);
      }

   }

   public Method getMethod() {
      return this.method;
   }

   public Object[] copyArguments(Object[] var1, ORB var2) throws RemoteException {
      return this.needsArgumentCopy ? Util.copyObjects(var1, var2) : var1;
   }

   public Object[] readArguments(InputStream var1) {
      Object[] var2 = null;
      if (this.hasArguments) {
         var2 = new Object[this.argRWs.length];

         for(int var3 = 0; var3 < this.argRWs.length; ++var3) {
            var2[var3] = this.argRWs[var3].read(var1);
         }
      }

      return var2;
   }

   public void writeArguments(OutputStream var1, Object[] var2) {
      if (this.hasArguments) {
         if (var2.length != this.argRWs.length) {
            throw new IllegalArgumentException("Expected " + this.argRWs.length + " arguments, but got " + var2.length + " arguments.");
         }

         for(int var3 = 0; var3 < this.argRWs.length; ++var3) {
            this.argRWs[var3].write(var1, var2[var3]);
         }
      }

   }

   public Object copyResult(Object var1, ORB var2) throws RemoteException {
      return this.needsResultCopy ? Util.copyObject(var1, var2) : var1;
   }

   public Object readResult(InputStream var1) {
      return this.hasVoidResult ? null : this.resultRW.read(var1);
   }

   public void writeResult(OutputStream var1, Object var2) {
      if (!this.hasVoidResult) {
         this.resultRW.write(var1, var2);
      }

   }

   public boolean isDeclaredException(Throwable var1) {
      return this.ehandler.isDeclaredException(var1.getClass());
   }

   public void writeException(OutputStream var1, Exception var2) {
      this.ehandler.writeException(var1, var2);
   }

   public Exception readException(ApplicationException var1) {
      return this.ehandler.readException(var1);
   }

   abstract static class ReaderWriterBase implements DynamicMethodMarshallerImpl.ReaderWriter {
      String name;

      public ReaderWriterBase(String var1) {
         this.name = var1;
      }

      public String toString() {
         return "ReaderWriter[" + this.name + "]";
      }
   }

   public interface ReaderWriter {
      Object read(InputStream var1);

      void write(OutputStream var1, Object var2);
   }
}
