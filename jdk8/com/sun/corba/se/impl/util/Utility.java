package com.sun.corba.se.impl.util;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.UtilSystemException;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import java.rmi.Remote;
import java.rmi.RemoteException;
import javax.rmi.PortableRemoteObject;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ValueFactory;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import org.omg.stub.java.rmi._Remote_Stub;

public final class Utility {
   public static final String STUB_PREFIX = "_";
   public static final String RMI_STUB_SUFFIX = "_Stub";
   public static final String DYNAMIC_STUB_SUFFIX = "_DynamicStub";
   public static final String IDL_STUB_SUFFIX = "Stub";
   public static final String TIE_SUFIX = "_Tie";
   private static IdentityHashtable tieCache = new IdentityHashtable();
   private static IdentityHashtable tieToStubCache = new IdentityHashtable();
   private static IdentityHashtable stubToTieCache = new IdentityHashtable();
   private static Object CACHE_MISS = new Object();
   private static UtilSystemException wrapper = UtilSystemException.get("util");
   private static OMGSystemException omgWrapper = OMGSystemException.get("util");

   public static Object autoConnect(Object var0, ORB var1, boolean var2) {
      if (var0 == null) {
         return var0;
      } else if (StubAdapter.isStub(var0)) {
         try {
            StubAdapter.getDelegate(var0);
         } catch (BAD_OPERATION var7) {
            try {
               StubAdapter.connect(var0, var1);
            } catch (RemoteException var6) {
               throw wrapper.objectNotConnected((Throwable)var6, var0.getClass().getName());
            }
         }

         return var0;
      } else if (var0 instanceof Remote) {
         Remote var3 = (Remote)var0;
         Tie var4 = Util.getTie(var3);
         if (var4 != null) {
            try {
               var4.orb();
            } catch (SystemException var8) {
               var4.orb(var1);
            }

            if (var2) {
               Remote var5 = loadStub(var4, (PresentationManager.StubFactory)null, (String)null, true);
               if (var5 != null) {
                  return var5;
               } else {
                  throw wrapper.couldNotLoadStub(var0.getClass().getName());
               }
            } else {
               return StubAdapter.activateTie(var4);
            }
         } else {
            throw wrapper.objectNotExported(var0.getClass().getName());
         }
      } else {
         return var0;
      }
   }

   public static Tie loadTie(Remote var0) {
      Tie var1 = null;
      Class var2 = var0.getClass();
      synchronized(tieCache) {
         Object var4 = tieCache.get(var0);
         if (var4 == null) {
            try {
               for(var1 = loadTie(var2); var1 == null && (var2 = var2.getSuperclass()) != null && var2 != PortableRemoteObject.class && var2 != Object.class; var1 = loadTie(var2)) {
               }
            } catch (Exception var8) {
               wrapper.loadTieFailed((Throwable)var8, var2.getName());
            }

            if (var1 == null) {
               tieCache.put(var0, CACHE_MISS);
            } else {
               tieCache.put(var0, var1);
            }
         } else if (var4 != CACHE_MISS) {
            try {
               var1 = (Tie)var4.getClass().newInstance();
            } catch (Exception var7) {
            }
         }

         return var1;
      }
   }

   private static Tie loadTie(Class var0) {
      return com.sun.corba.se.spi.orb.ORB.getStubFactoryFactory().getTie(var0);
   }

   public static void clearCaches() {
      synchronized(tieToStubCache) {
         tieToStubCache.clear();
      }

      synchronized(tieCache) {
         tieCache.clear();
      }

      synchronized(stubToTieCache) {
         stubToTieCache.clear();
      }
   }

   static Class loadClassOfType(String var0, String var1, ClassLoader var2, Class var3, ClassLoader var4) throws ClassNotFoundException {
      Class var5 = null;

      try {
         try {
            if (!PackagePrefixChecker.hasOffendingPrefix(PackagePrefixChecker.withoutPackagePrefix(var0))) {
               var5 = Util.loadClass(PackagePrefixChecker.withoutPackagePrefix(var0), var1, var2);
            } else {
               var5 = Util.loadClass(var0, var1, var2);
            }
         } catch (ClassNotFoundException var7) {
            var5 = Util.loadClass(var0, var1, var2);
         }

         if (var3 == null) {
            return var5;
         }
      } catch (ClassNotFoundException var8) {
         if (var3 == null) {
            throw var8;
         }
      }

      if (var5 == null || !var3.isAssignableFrom(var5)) {
         if (var3.getClassLoader() != var4) {
            throw new IllegalArgumentException("expectedTypeClassLoader not class loader of expected Type.");
         }

         if (var4 != null) {
            var5 = var4.loadClass(var0);
         } else {
            ClassLoader var6 = Thread.currentThread().getContextClassLoader();
            if (var6 == null) {
               var6 = ClassLoader.getSystemClassLoader();
            }

            var5 = var6.loadClass(var0);
         }
      }

      return var5;
   }

   public static Class loadClassForClass(String var0, String var1, ClassLoader var2, Class var3, ClassLoader var4) throws ClassNotFoundException {
      if (var3 == null) {
         return Util.loadClass(var0, var1, var2);
      } else {
         Class var5 = null;

         try {
            var5 = Util.loadClass(var0, var1, var2);
         } catch (ClassNotFoundException var7) {
            if (var3.getClassLoader() == null) {
               throw var7;
            }
         }

         if (var5 == null || var5.getClassLoader() != null && var5.getClassLoader().loadClass(var3.getName()) != var3) {
            if (var3.getClassLoader() != var4) {
               throw new IllegalArgumentException("relatedTypeClassLoader not class loader of relatedType.");
            }

            if (var4 != null) {
               var5 = var4.loadClass(var0);
            }
         }

         return var5;
      }
   }

   public static BoxedValueHelper getHelper(Class var0, String var1, String var2) {
      String var3 = null;
      if (var0 != null) {
         var3 = var0.getName();
         if (var1 == null) {
            var1 = Util.getCodebase(var0);
         }
      } else {
         if (var2 != null) {
            var3 = RepositoryId.cache.getId(var2).getClassName();
         }

         if (var3 == null) {
            throw wrapper.unableLocateValueHelper(CompletionStatus.COMPLETED_MAYBE);
         }
      }

      try {
         ClassLoader var4 = var0 == null ? null : var0.getClassLoader();
         Class var5 = loadClassForClass(var3 + "Helper", var1, var4, var0, var4);
         return (BoxedValueHelper)var5.newInstance();
      } catch (ClassNotFoundException var6) {
         throw wrapper.unableLocateValueHelper(CompletionStatus.COMPLETED_MAYBE, var6);
      } catch (IllegalAccessException var7) {
         throw wrapper.unableLocateValueHelper(CompletionStatus.COMPLETED_MAYBE, var7);
      } catch (InstantiationException var8) {
         throw wrapper.unableLocateValueHelper(CompletionStatus.COMPLETED_MAYBE, var8);
      } catch (ClassCastException var9) {
         throw wrapper.unableLocateValueHelper(CompletionStatus.COMPLETED_MAYBE, var9);
      }
   }

   public static ValueFactory getFactory(Class var0, String var1, ORB var2, String var3) {
      ValueFactory var4 = null;
      if (var2 != null && var3 != null) {
         try {
            var4 = ((org.omg.CORBA_2_3.ORB)var2).lookup_value_factory(var3);
         } catch (BAD_PARAM var12) {
         }
      }

      String var5 = null;
      if (var0 != null) {
         var5 = var0.getName();
         if (var1 == null) {
            var1 = Util.getCodebase(var0);
         }
      } else {
         if (var3 != null) {
            var5 = RepositoryId.cache.getId(var3).getClassName();
         }

         if (var5 == null) {
            throw omgWrapper.unableLocateValueFactory(CompletionStatus.COMPLETED_MAYBE);
         }
      }

      if (var4 == null || var4.getClass().getName().equals(var5 + "DefaultFactory") && (var0 != null || var1 != null)) {
         try {
            ClassLoader var6 = var0 == null ? null : var0.getClassLoader();
            Class var7 = loadClassForClass(var5 + "DefaultFactory", var1, var6, var0, var6);
            return (ValueFactory)var7.newInstance();
         } catch (ClassNotFoundException var8) {
            throw omgWrapper.unableLocateValueFactory(CompletionStatus.COMPLETED_MAYBE, var8);
         } catch (IllegalAccessException var9) {
            throw omgWrapper.unableLocateValueFactory(CompletionStatus.COMPLETED_MAYBE, var9);
         } catch (InstantiationException var10) {
            throw omgWrapper.unableLocateValueFactory(CompletionStatus.COMPLETED_MAYBE, var10);
         } catch (ClassCastException var11) {
            throw omgWrapper.unableLocateValueFactory(CompletionStatus.COMPLETED_MAYBE, var11);
         }
      } else {
         return var4;
      }
   }

   public static Remote loadStub(Tie var0, PresentationManager.StubFactory var1, String var2, boolean var3) {
      StubEntry var4 = null;
      synchronized(tieToStubCache) {
         Object var6 = tieToStubCache.get(var0);
         if (var6 == null) {
            var4 = loadStubAndUpdateCache(var0, var1, var2, var3);
         } else if (var6 != CACHE_MISS) {
            var4 = (StubEntry)var6;
            if (!var4.mostDerived && var3) {
               var4 = loadStubAndUpdateCache(var0, (PresentationManager.StubFactory)null, var2, true);
            } else if (var1 != null && !StubAdapter.getTypeIds(var4.stub)[0].equals(var1.getTypeIds()[0])) {
               var4 = loadStubAndUpdateCache(var0, (PresentationManager.StubFactory)null, var2, true);
               if (var4 == null) {
                  var4 = loadStubAndUpdateCache(var0, var1, var2, var3);
               }
            } else {
               try {
                  Delegate var7 = StubAdapter.getDelegate(var4.stub);
               } catch (Exception var11) {
                  try {
                     Delegate var8 = StubAdapter.getDelegate(var0);
                     StubAdapter.setDelegate(var4.stub, var8);
                  } catch (Exception var10) {
                  }
               }
            }
         }
      }

      return var4 != null ? (Remote)var4.stub : null;
   }

   private static StubEntry loadStubAndUpdateCache(Tie var0, PresentationManager.StubFactory var1, String var2, boolean var3) {
      Object var4 = null;
      StubEntry var5 = null;
      boolean var6 = StubAdapter.isStub(var0);
      if (var1 != null) {
         try {
            var4 = var1.makeStub();
         } catch (Throwable var21) {
            wrapper.stubFactoryCouldNotMakeStub(var21);
            if (var21 instanceof ThreadDeath) {
               throw (ThreadDeath)var21;
            }
         }
      } else {
         String[] var7 = null;
         if (var6) {
            var7 = StubAdapter.getTypeIds(var0);
         } else {
            var7 = ((Servant)var0)._all_interfaces((POA)null, (byte[])null);
         }

         if (var2 == null) {
            var2 = Util.getCodebase(var0.getClass());
         }

         if (var7.length == 0) {
            var4 = new _Remote_Stub();
         } else {
            int var8 = 0;

            while(var8 < var7.length) {
               if (var7[var8].length() == 0) {
                  var4 = new _Remote_Stub();
                  break;
               }

               try {
                  PresentationManager.StubFactoryFactory var9 = com.sun.corba.se.spi.orb.ORB.getStubFactoryFactory();
                  RepositoryId var10 = RepositoryId.cache.getId(var7[var8]);
                  String var11 = var10.getClassName();
                  boolean var12 = var10.isIDLType();
                  var1 = var9.createStubFactory(var11, var12, var2, (Class)null, var0.getClass().getClassLoader());
                  var4 = var1.makeStub();
                  break;
               } catch (Exception var20) {
                  wrapper.errorInMakeStubFromRepositoryId((Throwable)var20);
                  if (var3) {
                     break;
                  }

                  ++var8;
               }
            }
         }
      }

      if (var4 == null) {
         tieToStubCache.put(var0, CACHE_MISS);
      } else {
         Delegate var22;
         if (var6) {
            try {
               var22 = StubAdapter.getDelegate(var0);
               StubAdapter.setDelegate(var4, var22);
            } catch (Exception var19) {
               synchronized(stubToTieCache) {
                  stubToTieCache.put(var4, var0);
               }
            }
         } else {
            try {
               var22 = StubAdapter.getDelegate(var0);
               StubAdapter.setDelegate(var4, var22);
            } catch (BAD_INV_ORDER var16) {
               synchronized(stubToTieCache) {
                  stubToTieCache.put(var4, var0);
               }
            } catch (Exception var17) {
               throw wrapper.noPoa((Throwable)var17);
            }
         }

         var5 = new StubEntry((org.omg.CORBA.Object)var4, var3);
         tieToStubCache.put(var0, var5);
      }

      return var5;
   }

   public static Tie getAndForgetTie(org.omg.CORBA.Object var0) {
      synchronized(stubToTieCache) {
         return (Tie)stubToTieCache.remove(var0);
      }
   }

   public static void purgeStubForTie(Tie var0) {
      StubEntry var1;
      synchronized(tieToStubCache) {
         var1 = (StubEntry)tieToStubCache.remove(var0);
      }

      if (var1 != null) {
         synchronized(stubToTieCache) {
            stubToTieCache.remove(var1.stub);
         }
      }

   }

   public static void purgeTieAndServant(Tie var0) {
      synchronized(tieCache) {
         Remote var2 = var0.getTarget();
         if (var2 != null) {
            tieCache.remove(var2);
         }

      }
   }

   public static String stubNameFromRepID(String var0) {
      RepositoryId var1 = RepositoryId.cache.getId(var0);
      String var2 = var1.getClassName();
      if (var1.isIDLType()) {
         var2 = idlStubName(var2);
      } else {
         var2 = stubName(var2);
      }

      return var2;
   }

   public static Remote loadStub(org.omg.CORBA.Object var0, Class var1) {
      Remote var2 = null;

      try {
         String var3 = null;

         try {
            Delegate var4 = StubAdapter.getDelegate(var0);
            var3 = ((org.omg.CORBA_2_3.portable.Delegate)var4).get_codebase(var0);
         } catch (ClassCastException var6) {
            wrapper.classCastExceptionInLoadStub((Throwable)var6);
         }

         PresentationManager.StubFactoryFactory var8 = com.sun.corba.se.spi.orb.ORB.getStubFactoryFactory();
         PresentationManager.StubFactory var5 = var8.createStubFactory(var1.getName(), false, var3, var1, var1.getClassLoader());
         var2 = (Remote)var5.makeStub();
         StubAdapter.setDelegate(var2, StubAdapter.getDelegate(var0));
      } catch (Exception var7) {
         wrapper.exceptionInLoadStub((Throwable)var7);
      }

      return var2;
   }

   public static Class loadStubClass(String var0, String var1, Class var2) throws ClassNotFoundException {
      if (var0.length() == 0) {
         throw new ClassNotFoundException();
      } else {
         String var3 = stubNameFromRepID(var0);
         ClassLoader var4 = var2 == null ? null : var2.getClassLoader();

         try {
            return loadClassOfType(var3, var1, var4, var2, var4);
         } catch (ClassNotFoundException var6) {
            return loadClassOfType(PackagePrefixChecker.packagePrefix() + var3, var1, var4, var2, var4);
         }
      }
   }

   public static String stubName(String var0) {
      return stubName(var0, false);
   }

   public static String dynamicStubName(String var0) {
      return stubName(var0, true);
   }

   private static String stubName(String var0, boolean var1) {
      String var2 = stubNameForCompiler(var0, var1);
      if (PackagePrefixChecker.hasOffendingPrefix(var2)) {
         var2 = PackagePrefixChecker.packagePrefix() + var2;
      }

      return var2;
   }

   public static String stubNameForCompiler(String var0) {
      return stubNameForCompiler(var0, false);
   }

   private static String stubNameForCompiler(String var0, boolean var1) {
      int var2 = var0.indexOf(36);
      if (var2 < 0) {
         var2 = var0.lastIndexOf(46);
      }

      String var3 = var1 ? "_DynamicStub" : "_Stub";
      return var2 > 0 ? var0.substring(0, var2 + 1) + "_" + var0.substring(var2 + 1) + var3 : "_" + var0 + var3;
   }

   public static String tieName(String var0) {
      return PackagePrefixChecker.hasOffendingPrefix(tieNameForCompiler(var0)) ? PackagePrefixChecker.packagePrefix() + tieNameForCompiler(var0) : tieNameForCompiler(var0);
   }

   public static String tieNameForCompiler(String var0) {
      int var1 = var0.indexOf(36);
      if (var1 < 0) {
         var1 = var0.lastIndexOf(46);
      }

      return var1 > 0 ? var0.substring(0, var1 + 1) + "_" + var0.substring(var1 + 1) + "_Tie" : "_" + var0 + "_Tie";
   }

   public static void throwNotSerializableForCorba(String var0) {
      throw omgWrapper.notSerializable((CompletionStatus)CompletionStatus.COMPLETED_MAYBE, var0);
   }

   public static String idlStubName(String var0) {
      String var1 = null;
      int var2 = var0.lastIndexOf(46);
      if (var2 > 0) {
         var1 = var0.substring(0, var2 + 1) + "_" + var0.substring(var2 + 1) + "Stub";
      } else {
         var1 = "_" + var0 + "Stub";
      }

      return var1;
   }

   public static void printStackTrace() {
      Throwable var0 = new Throwable("Printing stack trace:");
      var0.fillInStackTrace();
      var0.printStackTrace();
   }

   public static Object readObjectAndNarrow(InputStream var0, Class var1) throws ClassCastException {
      org.omg.CORBA.Object var2 = var0.read_Object();
      return var2 != null ? PortableRemoteObject.narrow(var2, var1) : null;
   }

   public static Object readAbstractAndNarrow(org.omg.CORBA_2_3.portable.InputStream var0, Class var1) throws ClassCastException {
      Object var2 = var0.read_abstract_interface();
      return var2 != null ? PortableRemoteObject.narrow(var2, var1) : null;
   }

   static int hexOf(char var0) {
      int var1 = var0 - 48;
      if (var1 >= 0 && var1 <= 9) {
         return var1;
      } else {
         var1 = var0 - 97 + 10;
         if (var1 >= 10 && var1 <= 15) {
            return var1;
         } else {
            var1 = var0 - 65 + 10;
            if (var1 >= 10 && var1 <= 15) {
               return var1;
            } else {
               throw wrapper.badHexDigit();
            }
         }
      }
   }
}
