package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.spi.orbutil.proxy.CompositeInvocationHandlerImpl;
import com.sun.corba.se.spi.orbutil.proxy.DelegateInvocationHandlerImpl;
import com.sun.corba.se.spi.orbutil.proxy.InvocationHandlerFactory;
import com.sun.corba.se.spi.orbutil.proxy.LinkedInvocationHandler;
import com.sun.corba.se.spi.presentation.rmi.DynamicStub;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.omg.CORBA.Object;

public class InvocationHandlerFactoryImpl implements InvocationHandlerFactory {
   private final PresentationManager.ClassData classData;
   private final PresentationManager pm;
   private Class[] proxyInterfaces;

   public InvocationHandlerFactoryImpl(PresentationManager var1, PresentationManager.ClassData var2) {
      this.classData = var2;
      this.pm = var1;
      Class[] var3 = var2.getIDLNameTranslator().getInterfaces();
      this.proxyInterfaces = new Class[var3.length + 1];

      for(int var4 = 0; var4 < var3.length; ++var4) {
         this.proxyInterfaces[var4] = var3[var4];
      }

      this.proxyInterfaces[var3.length] = DynamicStub.class;
   }

   public InvocationHandler getInvocationHandler() {
      DynamicStubImpl var1 = new DynamicStubImpl(this.classData.getTypeIds());
      return this.getInvocationHandler(var1);
   }

   InvocationHandler getInvocationHandler(DynamicStub var1) {
      final InvocationHandler var2 = DelegateInvocationHandlerImpl.create(var1);
      StubInvocationHandlerImpl var3 = new StubInvocationHandlerImpl(this.pm, this.classData, var1);
      final InvocationHandlerFactoryImpl.CustomCompositeInvocationHandlerImpl var4 = new InvocationHandlerFactoryImpl.CustomCompositeInvocationHandlerImpl(var1);
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            var4.addInvocationHandler(DynamicStub.class, var2);
            var4.addInvocationHandler(Object.class, var2);
            var4.addInvocationHandler(java.lang.Object.class, var2);
            return null;
         }
      });
      var4.setDefaultHandler(var3);
      return var4;
   }

   public Class[] getProxyInterfaces() {
      return this.proxyInterfaces;
   }

   private class CustomCompositeInvocationHandlerImpl extends CompositeInvocationHandlerImpl implements LinkedInvocationHandler, Serializable {
      private transient DynamicStub stub;

      public void setProxy(Proxy var1) {
         ((DynamicStubImpl)this.stub).setSelf((DynamicStub)var1);
      }

      public Proxy getProxy() {
         return (Proxy)((DynamicStubImpl)this.stub).getSelf();
      }

      public CustomCompositeInvocationHandlerImpl(DynamicStub var2) {
         this.stub = var2;
      }

      public java.lang.Object writeReplace() throws ObjectStreamException {
         return this.stub;
      }
   }
}
