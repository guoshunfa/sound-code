package com.sun.corba.se.internal.CosNaming;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.resolver.LocalResolver;
import com.sun.corba.se.spi.resolver.Resolver;
import com.sun.corba.se.spi.resolver.ResolverDefault;
import java.io.File;
import java.util.Properties;
import org.omg.CORBA.ORBPackage.InvalidName;

public class BootstrapServer {
   private ORB orb;

   public static final void main(String[] var0) {
      String var1 = null;
      int var2 = 900;

      for(int var3 = 0; var3 < var0.length; ++var3) {
         if (var0[var3].equals("-InitialServicesFile") && var3 < var0.length - 1) {
            var1 = var0[var3 + 1];
         }

         if (var0[var3].equals("-ORBInitialPort") && var3 < var0.length - 1) {
            var2 = Integer.parseInt(var0[var3 + 1]);
         }
      }

      if (var1 == null) {
         System.out.println(CorbaResourceUtil.getText("bootstrap.usage", "BootstrapServer"));
      } else {
         File var13 = new File(var1);
         if (var13.exists() && !var13.canRead()) {
            System.err.println(CorbaResourceUtil.getText("bootstrap.filenotreadable", var13.getAbsolutePath()));
         } else {
            System.out.println(CorbaResourceUtil.getText("bootstrap.success", Integer.toString(var2), var13.getAbsolutePath()));
            Properties var4 = new Properties();
            var4.put("com.sun.CORBA.ORBServerPort", Integer.toString(var2));
            ORB var5 = (ORB)org.omg.CORBA.ORB.init(var0, var4);
            LocalResolver var6 = var5.getLocalResolver();
            Resolver var7 = ResolverDefault.makeFileResolver(var5, var13);
            Resolver var8 = ResolverDefault.makeCompositeResolver(var7, var6);
            LocalResolver var9 = ResolverDefault.makeSplitLocalResolver(var8, var6);
            var5.setLocalResolver(var9);

            try {
               var5.resolve_initial_references("RootPOA");
            } catch (InvalidName var12) {
               RuntimeException var11 = new RuntimeException("This should not happen");
               var11.initCause(var12);
               throw var11;
            }

            var5.run();
         }
      }
   }
}
