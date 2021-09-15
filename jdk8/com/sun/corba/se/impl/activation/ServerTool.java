package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.Repository;
import com.sun.corba.se.spi.activation.RepositoryHelper;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import org.omg.CORBA.ORB;

public class ServerTool {
   static final String helpCommand = "help";
   static final String toolName = "servertool";
   static final String commandArg = "-cmd";
   private static final boolean debug = false;
   ORB orb = null;
   static Vector handlers = new Vector();
   static int maxNameLen;

   static int getServerIdForAlias(ORB var0, String var1) throws ServerNotRegistered {
      try {
         Repository var2 = RepositoryHelper.narrow(var0.resolve_initial_references("ServerRepository"));
         var2.getServerID(var1);
         return var2.getServerID(var1);
      } catch (Exception var4) {
         throw new ServerNotRegistered();
      }
   }

   void run(String[] var1) {
      String[] var2 = null;

      label44:
      for(int var3 = 0; var3 < var1.length; ++var3) {
         if (var1[var3].equals("-cmd")) {
            int var4 = var1.length - var3 - 1;
            var2 = new String[var4];
            int var5 = 0;

            while(true) {
               if (var5 >= var4) {
                  break label44;
               }

               ++var3;
               var2[var5] = var1[var3];
               ++var5;
            }
         }
      }

      try {
         Properties var7 = System.getProperties();
         var7.put("org.omg.CORBA.ORBClass", "com.sun.corba.se.impl.orb.ORBImpl");
         this.orb = ORB.init(var1, var7);
         if (var2 == null) {
            BufferedReader var8 = new BufferedReader(new InputStreamReader(System.in));
            System.out.println(CorbaResourceUtil.getText("servertool.banner"));

            while(true) {
               while(true) {
                  var2 = this.readCommand(var8);
                  if (var2 != null) {
                     this.executeCommand(var2);
                  } else {
                     this.printAvailableCommands();
                  }
               }
            }
         }

         this.executeCommand(var2);
      } catch (Exception var6) {
         System.out.println(CorbaResourceUtil.getText("servertool.usage", "servertool"));
         System.out.println();
         var6.printStackTrace();
      }

   }

   public static void main(String[] var0) {
      ServerTool var1 = new ServerTool();
      var1.run(var0);
   }

   String[] readCommand(BufferedReader var1) {
      System.out.print("servertool > ");

      try {
         int var2 = 0;
         String[] var3 = null;
         String var4 = var1.readLine();
         if (var4 != null) {
            StringTokenizer var5 = new StringTokenizer(var4);
            if (var5.countTokens() != 0) {
               for(var3 = new String[var5.countTokens()]; var5.hasMoreTokens(); var3[var2++] = var5.nextToken()) {
               }
            }
         }

         return var3;
      } catch (Exception var6) {
         System.out.println(CorbaResourceUtil.getText("servertool.usage", "servertool"));
         System.out.println();
         var6.printStackTrace();
         return null;
      }
   }

   void printAvailableCommands() {
      System.out.println(CorbaResourceUtil.getText("servertool.shorthelp"));

      for(int var2 = 0; var2 < handlers.size(); ++var2) {
         CommandHandler var1 = (CommandHandler)handlers.elementAt(var2);
         System.out.print("\t" + var1.getCommandName());

         for(int var3 = var1.getCommandName().length(); var3 < maxNameLen; ++var3) {
            System.out.print(" ");
         }

         System.out.print(" - ");
         var1.printCommandHelp(System.out, true);
      }

      System.out.println();
   }

   void executeCommand(String[] var1) {
      CommandHandler var3;
      int var4;
      if (var1[0].equals("help")) {
         if (var1.length == 1) {
            this.printAvailableCommands();
         } else {
            for(var4 = 0; var4 < handlers.size(); ++var4) {
               var3 = (CommandHandler)handlers.elementAt(var4);
               if (var3.getCommandName().equals(var1[1])) {
                  var3.printCommandHelp(System.out, false);
               }
            }
         }

      } else {
         for(var4 = 0; var4 < handlers.size(); ++var4) {
            var3 = (CommandHandler)handlers.elementAt(var4);
            if (var3.getCommandName().equals(var1[0])) {
               String[] var5 = new String[var1.length - 1];

               for(int var6 = 0; var6 < var5.length; ++var6) {
                  var5[var6] = var1[var6 + 1];
               }

               try {
                  System.out.println();
                  boolean var2 = var3.processCommand(var5, this.orb, System.out);
                  if (var2) {
                     var3.printCommandHelp(System.out, false);
                  }

                  System.out.println();
               } catch (Exception var7) {
               }

               return;
            }
         }

         this.printAvailableCommands();
      }
   }

   static {
      handlers.addElement(new RegisterServer());
      handlers.addElement(new UnRegisterServer());
      handlers.addElement(new GetServerID());
      handlers.addElement(new ListServers());
      handlers.addElement(new ListAliases());
      handlers.addElement(new ListActiveServers());
      handlers.addElement(new LocateServer());
      handlers.addElement(new LocateServerForORB());
      handlers.addElement(new ListORBs());
      handlers.addElement(new ShutdownServer());
      handlers.addElement(new StartServer());
      handlers.addElement(new Help());
      handlers.addElement(new Quit());
      maxNameLen = 0;

      for(int var1 = 0; var1 < handlers.size(); ++var1) {
         CommandHandler var2 = (CommandHandler)handlers.elementAt(var1);
         int var0 = var2.getCommandName().length();
         if (var0 > maxNameLen) {
            maxNameLen = var0;
         }
      }

   }
}
