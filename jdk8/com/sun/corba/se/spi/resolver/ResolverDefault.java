package com.sun.corba.se.spi.resolver;

import com.sun.corba.se.impl.resolver.BootstrapResolverImpl;
import com.sun.corba.se.impl.resolver.CompositeResolverImpl;
import com.sun.corba.se.impl.resolver.FileResolverImpl;
import com.sun.corba.se.impl.resolver.INSURLOperationImpl;
import com.sun.corba.se.impl.resolver.LocalResolverImpl;
import com.sun.corba.se.impl.resolver.ORBDefaultInitRefResolverImpl;
import com.sun.corba.se.impl.resolver.ORBInitRefResolverImpl;
import com.sun.corba.se.impl.resolver.SplitLocalResolverImpl;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.orb.StringPair;
import java.io.File;

public class ResolverDefault {
   public static LocalResolver makeLocalResolver() {
      return new LocalResolverImpl();
   }

   public static Resolver makeORBInitRefResolver(Operation var0, StringPair[] var1) {
      return new ORBInitRefResolverImpl(var0, var1);
   }

   public static Resolver makeORBDefaultInitRefResolver(Operation var0, String var1) {
      return new ORBDefaultInitRefResolverImpl(var0, var1);
   }

   public static Resolver makeBootstrapResolver(ORB var0, String var1, int var2) {
      return new BootstrapResolverImpl(var0, var1, var2);
   }

   public static Resolver makeCompositeResolver(Resolver var0, Resolver var1) {
      return new CompositeResolverImpl(var0, var1);
   }

   public static Operation makeINSURLOperation(ORB var0, Resolver var1) {
      return new INSURLOperationImpl(var0, var1);
   }

   public static LocalResolver makeSplitLocalResolver(Resolver var0, LocalResolver var1) {
      return new SplitLocalResolverImpl(var0, var1);
   }

   public static Resolver makeFileResolver(ORB var0, File var1) {
      return new FileResolverImpl(var0, var1);
   }
}
