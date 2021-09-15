package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import java.io.PrintStream;
import org.omg.CORBA.ORB;

class Help implements CommandHandler {
   public String getCommandName() {
      return "help";
   }

   public void printCommandHelp(PrintStream var1, boolean var2) {
      if (!var2) {
         var1.println(CorbaResourceUtil.getText("servertool.help"));
      } else {
         var1.println(CorbaResourceUtil.getText("servertool.help1"));
      }

   }

   public boolean processCommand(String[] var1, ORB var2, PrintStream var3) {
      return false;
   }
}
