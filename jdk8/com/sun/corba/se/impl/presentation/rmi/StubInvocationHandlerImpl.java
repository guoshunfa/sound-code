package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.pept.transport.ContactInfoList;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orbutil.proxy.LinkedInvocationHandler;
import com.sun.corba.se.spi.presentation.rmi.DynamicMethodMarshaller;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import com.sun.corba.se.spi.protocol.CorbaClientDelegate;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.rmi.CORBA.Util;
import org.omg.CORBA.Object;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ServantObject;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public final class StubInvocationHandlerImpl implements LinkedInvocationHandler {
   private transient PresentationManager.ClassData classData;
   private transient PresentationManager pm;
   private transient Object stub;
   private transient Proxy self;

   public void setProxy(Proxy var1) {
      this.self = var1;
   }

   public Proxy getProxy() {
      return this.self;
   }

   public StubInvocationHandlerImpl(PresentationManager var1, PresentationManager.ClassData var2, Object var3) {
      SecurityManager var4 = System.getSecurityManager();
      if (var4 != null) {
         var4.checkPermission(new DynamicAccessPermission("access"));
      }

      this.classData = var2;
      this.pm = var1;
      this.stub = var3;
   }

   private boolean isLocal() {
      boolean var1 = false;
      Delegate var2 = StubAdapter.getDelegate(this.stub);
      if (var2 instanceof CorbaClientDelegate) {
         CorbaClientDelegate var3 = (CorbaClientDelegate)var2;
         ContactInfoList var4 = var3.getContactInfoList();
         if (var4 instanceof CorbaContactInfoList) {
            CorbaContactInfoList var5 = (CorbaContactInfoList)var4;
            LocalClientRequestDispatcher var6 = var5.getLocalClientRequestDispatcher();
            var1 = var6.useLocalInvocation((Object)null);
         }
      }

      return var1;
   }

   public java.lang.Object invoke(java.lang.Object var1, final Method var2, java.lang.Object[] var3) throws Throwable {
      String var4 = this.classData.getIDLNameTranslator().getIDLName(var2);
      DynamicMethodMarshaller var5 = this.pm.getDynamicMethodMarshaller(var2);
      Delegate var6 = null;

      try {
         var6 = StubAdapter.getDelegate(this.stub);
      } catch (SystemException var29) {
         throw Util.mapSystemException(var29);
      }

      if (!this.isLocal()) {
         try {
            InputStream var37 = null;

            java.lang.Object var39;
            try {
               OutputStream var38 = (OutputStream)var6.request(this.stub, var4, true);
               var5.writeArguments(var38, var3);
               var37 = (InputStream)var6.invoke(this.stub, var38);
               var39 = var5.readResult(var37);
               return var39;
            } catch (ApplicationException var30) {
               throw var5.readException(var30);
            } catch (RemarshalException var31) {
               var39 = this.invoke(var1, var2, var3);
            } finally {
               var6.releaseReply(this.stub, var37);
            }

            return var39;
         } catch (SystemException var33) {
            throw Util.mapSystemException(var33);
         }
      } else {
         ORB var7 = (ORB)var6.orb(this.stub);
         ServantObject var8 = var6.servant_preinvoke(this.stub, var4, var2.getDeclaringClass());
         if (var8 == null) {
            return this.invoke(this.stub, var2, var3);
         } else {
            java.lang.Object var41;
            try {
               java.lang.Object[] var9 = var5.copyArguments(var3, var7);
               if (!var2.isAccessible()) {
                  AccessController.doPrivileged(new PrivilegedAction() {
                     public java.lang.Object run() {
                        var2.setAccessible(true);
                        return null;
                     }
                  });
               }

               java.lang.Object var40 = var2.invoke(var8.servant, var9);
               var41 = var5.copyResult(var40, var7);
            } catch (InvocationTargetException var34) {
               Throwable var10 = var34.getCause();
               Throwable var11 = (Throwable)Util.copyObject(var10, var7);
               if (var5.isDeclaredException(var11)) {
                  throw var11;
               }

               throw Util.wrapException(var11);
            } catch (Throwable var35) {
               if (var35 instanceof ThreadDeath) {
                  throw (ThreadDeath)var35;
               }

               throw Util.wrapException(var35);
            } finally {
               var6.servant_postinvoke(this.stub, var8);
            }

            return var41;
         }
      }
   }
}
