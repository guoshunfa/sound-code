package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import org.omg.CORBA.UserException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class ExceptionHandlerImpl implements ExceptionHandler {
   private ExceptionHandlerImpl.ExceptionRW[] rws;
   private final ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.presentation");

   public ExceptionHandlerImpl(Class[] var1) {
      int var2 = 0;

      int var3;
      for(var3 = 0; var3 < var1.length; ++var3) {
         Class var4 = var1[var3];
         if (!RemoteException.class.isAssignableFrom(var4)) {
            ++var2;
         }
      }

      this.rws = new ExceptionHandlerImpl.ExceptionRW[var2];
      var3 = 0;

      for(int var7 = 0; var7 < var1.length; ++var7) {
         Class var5 = var1[var7];
         if (!RemoteException.class.isAssignableFrom(var5)) {
            Object var6 = null;
            if (UserException.class.isAssignableFrom(var5)) {
               var6 = new ExceptionHandlerImpl.ExceptionRWIDLImpl(var5);
            } else {
               var6 = new ExceptionHandlerImpl.ExceptionRWRMIImpl(var5);
            }

            this.rws[var3++] = (ExceptionHandlerImpl.ExceptionRW)var6;
         }
      }

   }

   private int findDeclaredException(Class var1) {
      for(int var2 = 0; var2 < this.rws.length; ++var2) {
         Class var3 = this.rws[var2].getExceptionClass();
         if (var3.isAssignableFrom(var1)) {
            return var2;
         }
      }

      return -1;
   }

   private int findDeclaredException(String var1) {
      for(int var2 = 0; var2 < this.rws.length; ++var2) {
         if (this.rws[var2] == null) {
            return -1;
         }

         String var3 = this.rws[var2].getId();
         if (var1.equals(var3)) {
            return var2;
         }
      }

      return -1;
   }

   public boolean isDeclaredException(Class var1) {
      return this.findDeclaredException(var1) >= 0;
   }

   public void writeException(OutputStream var1, Exception var2) {
      int var3 = this.findDeclaredException(var2.getClass());
      if (var3 < 0) {
         throw this.wrapper.writeUndeclaredException((Throwable)var2, var2.getClass().getName());
      } else {
         this.rws[var3].write(var1, var2);
      }
   }

   public Exception readException(ApplicationException var1) {
      InputStream var2 = (InputStream)var1.getInputStream();
      String var3 = var1.getId();
      int var4 = this.findDeclaredException(var3);
      if (var4 < 0) {
         var3 = var2.read_string();
         UnexpectedException var5 = new UnexpectedException(var3);
         var5.initCause(var1);
         return var5;
      } else {
         return this.rws[var4].read(var2);
      }
   }

   public ExceptionHandlerImpl.ExceptionRW getRMIExceptionRW(Class var1) {
      return new ExceptionHandlerImpl.ExceptionRWRMIImpl(var1);
   }

   public class ExceptionRWRMIImpl extends ExceptionHandlerImpl.ExceptionRWBase {
      public ExceptionRWRMIImpl(Class var2) {
         super(var2);
         this.setId(IDLNameTranslatorImpl.getExceptionId(var2));
      }

      public void write(OutputStream var1, Exception var2) {
         var1.write_string(this.getId());
         var1.write_value(var2, (Class)this.getExceptionClass());
      }

      public Exception read(InputStream var1) {
         var1.read_string();
         return (Exception)var1.read_value(this.getExceptionClass());
      }
   }

   public class ExceptionRWIDLImpl extends ExceptionHandlerImpl.ExceptionRWBase {
      private Method readMethod;
      private Method writeMethod;

      public ExceptionRWIDLImpl(Class var2) {
         super(var2);
         String var3 = var2.getName() + "Helper";
         ClassLoader var4 = var2.getClassLoader();

         Class var5;
         try {
            var5 = Class.forName(var3, true, var4);
            Method var6 = var5.getDeclaredMethod("id", (Class[])null);
            this.setId((String)var6.invoke((Object)null, (Object[])null));
         } catch (Exception var9) {
            throw ExceptionHandlerImpl.this.wrapper.badHelperIdMethod((Throwable)var9, var3);
         }

         Class[] var10;
         try {
            var10 = new Class[]{org.omg.CORBA.portable.OutputStream.class, var2};
            this.writeMethod = var5.getDeclaredMethod("write", var10);
         } catch (Exception var8) {
            throw ExceptionHandlerImpl.this.wrapper.badHelperWriteMethod((Throwable)var8, var3);
         }

         try {
            var10 = new Class[]{org.omg.CORBA.portable.InputStream.class};
            this.readMethod = var5.getDeclaredMethod("read", var10);
         } catch (Exception var7) {
            throw ExceptionHandlerImpl.this.wrapper.badHelperReadMethod((Throwable)var7, var3);
         }
      }

      public void write(OutputStream var1, Exception var2) {
         try {
            Object[] var3 = new Object[]{var1, var2};
            this.writeMethod.invoke((Object)null, var3);
         } catch (Exception var4) {
            throw ExceptionHandlerImpl.this.wrapper.badHelperWriteMethod((Throwable)var4, this.writeMethod.getDeclaringClass().getName());
         }
      }

      public Exception read(InputStream var1) {
         try {
            Object[] var2 = new Object[]{var1};
            return (Exception)this.readMethod.invoke((Object)null, var2);
         } catch (Exception var3) {
            throw ExceptionHandlerImpl.this.wrapper.badHelperReadMethod((Throwable)var3, this.readMethod.getDeclaringClass().getName());
         }
      }
   }

   public abstract class ExceptionRWBase implements ExceptionHandlerImpl.ExceptionRW {
      private Class cls;
      private String id;

      public ExceptionRWBase(Class var2) {
         this.cls = var2;
      }

      public Class getExceptionClass() {
         return this.cls;
      }

      public String getId() {
         return this.id;
      }

      void setId(String var1) {
         this.id = var1;
      }
   }

   public interface ExceptionRW {
      Class getExceptionClass();

      String getId();

      void write(OutputStream var1, Exception var2);

      Exception read(InputStream var1);
   }
}
