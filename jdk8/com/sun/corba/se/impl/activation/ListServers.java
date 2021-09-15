package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.Repository;
import com.sun.corba.se.spi.activation.RepositoryHelper;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import java.io.PrintStream;
import org.omg.CORBA.ORB;

class ListServers implements CommandHandler {
   static final int illegalServerId = -1;

   public String getCommandName() {
      return "list";
   }

   public void printCommandHelp(PrintStream var1, boolean var2) {
      if (!var2) {
         var1.println(CorbaResourceUtil.getText("servertool.list"));
      } else {
         var1.println(CorbaResourceUtil.getText("servertool.list1"));
      }

   }

   public boolean processCommand(String[] var1, ORB var2, PrintStream var3) {
      int var4 = -1;
      boolean var5 = false;
      var5 = var1.length != 0;
      if (var1.length == 2 && var1[0].equals("-serverid")) {
         var4 = Integer.valueOf(var1[1]);
      }

      if (var4 == -1 && var5) {
         return true;
      } else {
         try {
            Repository var7 = RepositoryHelper.narrow(var2.resolve_initial_references("ServerRepository"));
            ServerDef var6;
            if (var5) {
               try {
                  var6 = var7.getServer(var4);
                  var3.println();
                  printServerDef(var6, var4, var3);
                  var3.println();
               } catch (ServerNotRegistered var12) {
                  var3.println(CorbaResourceUtil.getText("servertool.nosuchserver"));
               }
            } else {
               int[] var8 = var7.listRegisteredServers();
               var3.println(CorbaResourceUtil.getText("servertool.list2"));
               sortServers(var8);

               for(int var9 = 0; var9 < var8.length; ++var9) {
                  try {
                     var6 = var7.getServer(var8[var9]);
                     var3.println("\t   " + var8[var9] + "\t\t" + var6.serverName + "\t\t" + var6.applicationName);
                  } catch (ServerNotRegistered var11) {
                  }
               }
            }
         } catch (Exception var13) {
            var13.printStackTrace();
         }

         return false;
      }
   }

   static void printServerDef(ServerDef var0, int var1, PrintStream var2) {
      var2.println(CorbaResourceUtil.getText("servertool.appname", var0.applicationName));
      var2.println(CorbaResourceUtil.getText("servertool.name", var0.serverName));
      var2.println(CorbaResourceUtil.getText("servertool.classpath", var0.serverClassPath));
      var2.println(CorbaResourceUtil.getText("servertool.args", var0.serverArgs));
      var2.println(CorbaResourceUtil.getText("servertool.vmargs", var0.serverVmArgs));
      var2.println(CorbaResourceUtil.getText("servertool.serverid", var1));
   }

   static void sortServers(int[] var0) {
      int var1 = var0.length;

      for(int var3 = 0; var3 < var1; ++var3) {
         int var2 = var3;

         int var4;
         for(var4 = var3 + 1; var4 < var1; ++var4) {
            if (var0[var4] < var0[var2]) {
               var2 = var4;
            }
         }

         if (var2 != var3) {
            var4 = var0[var3];
            var0[var3] = var0[var2];
            var0[var2] = var4;
         }
      }

   }
}
