package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.Repository;
import com.sun.corba.se.spi.activation.RepositoryHelper;
import java.io.PrintStream;
import org.omg.CORBA.ORB;

class ListAliases implements CommandHandler {
   public String getCommandName() {
      return "listappnames";
   }

   public void printCommandHelp(PrintStream var1, boolean var2) {
      if (!var2) {
         var1.println(CorbaResourceUtil.getText("servertool.listappnames"));
      } else {
         var1.println(CorbaResourceUtil.getText("servertool.listappnames1"));
      }

   }

   public boolean processCommand(String[] var1, ORB var2, PrintStream var3) {
      try {
         Repository var4 = RepositoryHelper.narrow(var2.resolve_initial_references("ServerRepository"));
         String[] var5 = var4.getApplicationNames();
         var3.println(CorbaResourceUtil.getText("servertool.listappnames2"));
         var3.println();

         for(int var6 = 0; var6 < var5.length; ++var6) {
            var3.println("\t" + var5[var6]);
         }
      } catch (Exception var7) {
         var7.printStackTrace();
      }

      return false;
   }
}
