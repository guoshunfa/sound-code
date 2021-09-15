package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.Activator;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import com.sun.corba.se.spi.activation.ServerAlreadyActive;
import com.sun.corba.se.spi.activation.ServerHeldDown;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import java.io.PrintStream;
import org.omg.CORBA.ORB;

class StartServer implements CommandHandler {
   static final int illegalServerId = -1;

   public String getCommandName() {
      return "startup";
   }

   public void printCommandHelp(PrintStream var1, boolean var2) {
      if (!var2) {
         var1.println(CorbaResourceUtil.getText("servertool.startserver"));
      } else {
         var1.println(CorbaResourceUtil.getText("servertool.startserver1"));
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
         var5.activate(var4);
         var3.println(CorbaResourceUtil.getText("servertool.startserver2"));
      } catch (ServerNotRegistered var6) {
         var3.println(CorbaResourceUtil.getText("servertool.nosuchserver"));
      } catch (ServerAlreadyActive var7) {
         var3.println(CorbaResourceUtil.getText("servertool.serverup"));
      } catch (ServerHeldDown var8) {
         var3.println(CorbaResourceUtil.getText("servertool.helddown"));
      } catch (Exception var9) {
         var9.printStackTrace();
      }

      return false;
   }
}
