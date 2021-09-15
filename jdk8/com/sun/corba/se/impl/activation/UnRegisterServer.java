package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.Activator;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import com.sun.corba.se.spi.activation.Repository;
import com.sun.corba.se.spi.activation.RepositoryHelper;
import com.sun.corba.se.spi.activation.ServerHeldDown;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import java.io.PrintStream;
import org.omg.CORBA.ORB;

class UnRegisterServer implements CommandHandler {
   static final int illegalServerId = -1;

   public String getCommandName() {
      return "unregister";
   }

   public void printCommandHelp(PrintStream var1, boolean var2) {
      if (!var2) {
         var1.println(CorbaResourceUtil.getText("servertool.unregister"));
      } else {
         var1.println(CorbaResourceUtil.getText("servertool.unregister1"));
      }

   }

   public boolean processCommand(String[] var1, ORB var2, PrintStream var3) {
      int var4 = -1;

      try {
         if (var1.length == 2) {
            if (var1[0].equals("-serverid")) {
               var4 = Integer.valueOf(var1[1]);
            } else if (var1[0].equals("-applicationName")) {
               var4 = ServerTool.getServerIdForAlias(var2, var1[1]);
            }
         }

         if (var4 == -1) {
            return true;
         }

         try {
            Activator var5 = ActivatorHelper.narrow(var2.resolve_initial_references("ServerActivator"));
            var5.uninstall(var4);
         } catch (ServerHeldDown var6) {
         }

         Repository var9 = RepositoryHelper.narrow(var2.resolve_initial_references("ServerRepository"));
         var9.unregisterServer(var4);
         var3.println(CorbaResourceUtil.getText("servertool.unregister2"));
      } catch (ServerNotRegistered var7) {
         var3.println(CorbaResourceUtil.getText("servertool.nosuchserver"));
      } catch (Exception var8) {
         var8.printStackTrace();
      }

      return false;
   }
}
