package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.Activator;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import com.sun.corba.se.spi.activation.ServerNotActive;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import java.io.PrintStream;
import org.omg.CORBA.ORB;

class ShutdownServer implements CommandHandler {
   static final int illegalServerId = -1;

   public String getCommandName() {
      return "shutdown";
   }

   public void printCommandHelp(PrintStream var1, boolean var2) {
      if (!var2) {
         var1.println(CorbaResourceUtil.getText("servertool.shutdown"));
      } else {
         var1.println(CorbaResourceUtil.getText("servertool.shutdown1"));
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

         Activator var5 = ActivatorHelper.narrow(var2.resolve_initial_references("ServerActivator"));
         var5.shutdown(var4);
         var3.println(CorbaResourceUtil.getText("servertool.shutdown2"));
      } catch (ServerNotActive var6) {
         var3.println(CorbaResourceUtil.getText("servertool.servernotrunning"));
      } catch (ServerNotRegistered var7) {
         var3.println(CorbaResourceUtil.getText("servertool.nosuchserver"));
      } catch (Exception var8) {
         var8.printStackTrace();
      }

      return false;
   }
}
