package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import java.io.SerializablePermission;
import org.omg.CORBA.Object;

public abstract class StubFactoryDynamicBase extends StubFactoryBase {
   protected final ClassLoader loader;

   private static Void checkPermission() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(new SerializablePermission("enableSubclassImplementation"));
      }

      return null;
   }

   private StubFactoryDynamicBase(Void var1, PresentationManager.ClassData var2, ClassLoader var3) {
      super(var2);
      if (var3 == null) {
         ClassLoader var4 = Thread.currentThread().getContextClassLoader();
         if (var4 == null) {
            var4 = ClassLoader.getSystemClassLoader();
         }

         this.loader = var4;
      } else {
         this.loader = var3;
      }

   }

   public StubFactoryDynamicBase(PresentationManager.ClassData var1, ClassLoader var2) {
      this(checkPermission(), var1, var2);
   }

   public abstract Object makeStub();
}
