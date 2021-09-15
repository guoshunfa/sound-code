package com.sun.security.auth.callback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.ConfirmationCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import jdk.Exported;
import sun.security.util.Password;

@Exported
public class TextCallbackHandler implements CallbackHandler {
   public void handle(Callback[] var1) throws IOException, UnsupportedCallbackException {
      ConfirmationCallback var2 = null;

      for(int var3 = 0; var3 < var1.length; ++var3) {
         String var5;
         if (var1[var3] instanceof TextOutputCallback) {
            TextOutputCallback var4 = (TextOutputCallback)var1[var3];
            switch(var4.getMessageType()) {
            case 0:
               var5 = "";
               break;
            case 1:
               var5 = "Warning: ";
               break;
            case 2:
               var5 = "Error: ";
               break;
            default:
               throw new UnsupportedCallbackException(var1[var3], "Unrecognized message type");
            }

            String var6 = var4.getMessage();
            if (var6 != null) {
               var5 = var5 + var6;
            }

            if (var5 != null) {
               System.err.println(var5);
            }
         } else if (var1[var3] instanceof NameCallback) {
            NameCallback var7 = (NameCallback)var1[var3];
            if (var7.getDefaultName() == null) {
               System.err.print(var7.getPrompt());
            } else {
               System.err.print(var7.getPrompt() + " [" + var7.getDefaultName() + "] ");
            }

            System.err.flush();
            var5 = this.readLine();
            if (var5.equals("")) {
               var5 = var7.getDefaultName();
            }

            var7.setName(var5);
         } else if (var1[var3] instanceof PasswordCallback) {
            PasswordCallback var8 = (PasswordCallback)var1[var3];
            System.err.print(var8.getPrompt());
            System.err.flush();
            var8.setPassword(Password.readPassword(System.in, var8.isEchoOn()));
         } else {
            if (!(var1[var3] instanceof ConfirmationCallback)) {
               throw new UnsupportedCallbackException(var1[var3], "Unrecognized Callback");
            }

            var2 = (ConfirmationCallback)var1[var3];
         }
      }

      if (var2 != null) {
         this.doConfirmation(var2);
      }

   }

   private String readLine() throws IOException {
      String var1 = (new BufferedReader(new InputStreamReader(System.in))).readLine();
      if (var1 == null) {
         throw new IOException("Cannot read from System.in");
      } else {
         return var1;
      }
   }

   private void doConfirmation(ConfirmationCallback var1) throws IOException, UnsupportedCallbackException {
      int var3 = var1.getMessageType();
      String var2;
      switch(var3) {
      case 0:
         var2 = "";
         break;
      case 1:
         var2 = "Warning: ";
         break;
      case 2:
         var2 = "Error: ";
         break;
      default:
         throw new UnsupportedCallbackException(var1, "Unrecognized message type: " + var3);
      }

      class OptionInfo {
         String name;
         int value;

         OptionInfo(String var2, int var3) {
            this.name = var2;
            this.value = var3;
         }
      }

      OptionInfo[] var4;
      int var5;
      var5 = var1.getOptionType();
      label74:
      switch(var5) {
      case -1:
         String[] var6 = var1.getOptions();
         var4 = new OptionInfo[var6.length];
         int var7 = 0;

         while(true) {
            if (var7 >= var4.length) {
               break label74;
            }

            var4[var7] = new OptionInfo(var6[var7], var7);
            ++var7;
         }
      case 0:
         var4 = new OptionInfo[]{new OptionInfo("Yes", 0), new OptionInfo("No", 1)};
         break;
      case 1:
         var4 = new OptionInfo[]{new OptionInfo("Yes", 0), new OptionInfo("No", 1), new OptionInfo("Cancel", 2)};
         break;
      case 2:
         var4 = new OptionInfo[]{new OptionInfo("OK", 3), new OptionInfo("Cancel", 2)};
         break;
      default:
         throw new UnsupportedCallbackException(var1, "Unrecognized option type: " + var5);
      }

      int var11 = var1.getDefaultOption();
      String var12 = var1.getPrompt();
      if (var12 == null) {
         var12 = "";
      }

      var12 = var2 + var12;
      if (!var12.equals("")) {
         System.err.println(var12);
      }

      int var8;
      for(var8 = 0; var8 < var4.length; ++var8) {
         if (var5 == -1) {
            System.err.println(var8 + ". " + var4[var8].name + (var8 == var11 ? " [default]" : ""));
         } else {
            System.err.println(var8 + ". " + var4[var8].name + (var4[var8].value == var11 ? " [default]" : ""));
         }
      }

      System.err.print("Enter a number: ");
      System.err.flush();

      try {
         var8 = Integer.parseInt(this.readLine());
         if (var8 < 0 || var8 > var4.length - 1) {
            var8 = var11;
         }

         var8 = var4[var8].value;
      } catch (NumberFormatException var10) {
         var8 = var11;
      }

      var1.setSelectedIndex(var8);
   }
}
