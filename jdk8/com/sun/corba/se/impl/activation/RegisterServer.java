package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.Activator;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import com.sun.corba.se.spi.activation.BadServerDefinition;
import com.sun.corba.se.spi.activation.Repository;
import com.sun.corba.se.spi.activation.RepositoryHelper;
import com.sun.corba.se.spi.activation.ServerAlreadyActive;
import com.sun.corba.se.spi.activation.ServerAlreadyRegistered;
import com.sun.corba.se.spi.activation.ServerHeldDown;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import java.io.PrintStream;
import org.omg.CORBA.ORB;

class RegisterServer implements CommandHandler {
   public String getCommandName() {
      return "register";
   }

   public void printCommandHelp(PrintStream var1, boolean var2) {
      if (!var2) {
         var1.println(CorbaResourceUtil.getText("servertool.register"));
      } else {
         var1.println(CorbaResourceUtil.getText("servertool.register1"));
      }

   }

   public boolean processCommand(String[] var1, ORB var2, PrintStream var3) {
      int var4 = 0;
      String var5 = "";
      String var6 = "";
      String var7 = "";
      String var8 = "";
      String var9 = "";
      byte var10 = 0;

      while(true) {
         while(var4 < var1.length) {
            String var11 = var1[var4++];
            if (var11.equals("-server")) {
               if (var4 >= var1.length) {
                  return true;
               }

               var6 = var1[var4++];
            } else if (var11.equals("-applicationName")) {
               if (var4 >= var1.length) {
                  return true;
               }

               var5 = var1[var4++];
            } else if (var11.equals("-classpath")) {
               if (var4 >= var1.length) {
                  return true;
               }

               var7 = var1[var4++];
            } else if (var11.equals("-args")) {
               while(var4 < var1.length && !var1[var4].equals("-vmargs")) {
                  var8 = var8.equals("") ? var1[var4] : var8 + " " + var1[var4];
                  ++var4;
               }

               if (var8.equals("")) {
                  return true;
               }
            } else {
               if (!var11.equals("-vmargs")) {
                  return true;
               }

               while(var4 < var1.length && !var1[var4].equals("-args")) {
                  var9 = var9.equals("") ? var1[var4] : var9 + " " + var1[var4];
                  ++var4;
               }

               if (var9.equals("")) {
                  return true;
               }
            }
         }

         if (var6.equals("")) {
            return true;
         }

         try {
            Repository var12 = RepositoryHelper.narrow(var2.resolve_initial_references("ServerRepository"));
            ServerDef var13 = new ServerDef(var5, var6, var7, var8, var9);
            int var21 = var12.registerServer(var13);
            Activator var14 = ActivatorHelper.narrow(var2.resolve_initial_references("ServerActivator"));
            var14.activate(var21);
            var14.install(var21);
            var3.println(CorbaResourceUtil.getText("servertool.register2", var21));
         } catch (ServerNotRegistered var15) {
         } catch (ServerAlreadyActive var16) {
         } catch (ServerHeldDown var17) {
            var3.println(CorbaResourceUtil.getText("servertool.register3", var10));
         } catch (ServerAlreadyRegistered var18) {
            var3.println(CorbaResourceUtil.getText("servertool.register4", var10));
         } catch (BadServerDefinition var19) {
            var3.println(CorbaResourceUtil.getText("servertool.baddef", var19.reason));
         } catch (Exception var20) {
            var20.printStackTrace();
         }

         return false;
      }
   }
}
