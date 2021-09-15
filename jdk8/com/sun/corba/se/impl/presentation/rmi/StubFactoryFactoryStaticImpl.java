package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.util.PackagePrefixChecker;
import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import org.omg.CORBA.CompletionStatus;

public class StubFactoryFactoryStaticImpl extends StubFactoryFactoryBase {
   private ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.presentation");

   public PresentationManager.StubFactory createStubFactory(String var1, boolean var2, String var3, Class var4, ClassLoader var5) {
      String var6 = null;
      if (var2) {
         var6 = Utility.idlStubName(var1);
      } else {
         var6 = Utility.stubNameForCompiler(var1);
      }

      ClassLoader var7 = var4 == null ? var5 : var4.getClassLoader();
      String var8 = var6;
      String var9 = var6;
      if (PackagePrefixChecker.hasOffendingPrefix(var6)) {
         var8 = PackagePrefixChecker.packagePrefix() + var6;
      } else {
         var9 = PackagePrefixChecker.packagePrefix() + var6;
      }

      Class var10 = null;

      try {
         var10 = Util.loadClass(var8, var3, var7);
      } catch (ClassNotFoundException var15) {
         this.wrapper.classNotFound1(CompletionStatus.COMPLETED_MAYBE, var15, var8);

         try {
            var10 = Util.loadClass(var9, var3, var7);
         } catch (ClassNotFoundException var14) {
            throw this.wrapper.classNotFound2(CompletionStatus.COMPLETED_MAYBE, var14, var9);
         }
      }

      if (var10 == null || var4 != null && !var4.isAssignableFrom(var10)) {
         try {
            ClassLoader var11 = Thread.currentThread().getContextClassLoader();
            if (var11 == null) {
               var11 = ClassLoader.getSystemClassLoader();
            }

            var10 = var11.loadClass(var1);
         } catch (Exception var13) {
            IllegalStateException var12 = new IllegalStateException("Could not load class " + var6);
            var12.initCause(var13);
            throw var12;
         }
      }

      return new StubFactoryStaticImpl(var10);
   }

   public Tie getTie(Class var1) {
      Class var2 = null;
      String var3 = Utility.tieName(var1.getName());

      try {
         try {
            var2 = Utility.loadClassForClass(var3, Util.getCodebase(var1), (ClassLoader)null, var1, var1.getClassLoader());
            return (Tie)var2.newInstance();
         } catch (Exception var5) {
            var2 = Utility.loadClassForClass(PackagePrefixChecker.packagePrefix() + var3, Util.getCodebase(var1), (ClassLoader)null, var1, var1.getClassLoader());
            return (Tie)var2.newInstance();
         }
      } catch (Exception var6) {
         return null;
      }
   }

   public boolean createsDynamicStubs() {
      return false;
   }
}
