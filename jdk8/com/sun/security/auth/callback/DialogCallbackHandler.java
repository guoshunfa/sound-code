package com.sun.security.auth.callback;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.ConfirmationCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import jdk.Exported;

/** @deprecated */
@Exported(false)
@Deprecated
public class DialogCallbackHandler implements CallbackHandler {
   private Component parentComponent;
   private static final int JPasswordFieldLen = 8;
   private static final int JTextFieldLen = 8;

   public DialogCallbackHandler() {
   }

   public DialogCallbackHandler(Component var1) {
      this.parentComponent = var1;
   }

   public void handle(Callback[] var1) throws UnsupportedCallbackException {
      ArrayList var2 = new ArrayList(3);
      ArrayList var3 = new ArrayList(2);
      DialogCallbackHandler.ConfirmationInfo var4 = new DialogCallbackHandler.ConfirmationInfo();

      int var5;
      for(var5 = 0; var5 < var1.length; ++var5) {
         if (var1[var5] instanceof TextOutputCallback) {
            TextOutputCallback var6 = (TextOutputCallback)var1[var5];
            switch(var6.getMessageType()) {
            case 0:
               var4.messageType = 1;
               break;
            case 1:
               var4.messageType = 2;
               break;
            case 2:
               var4.messageType = 0;
               break;
            default:
               throw new UnsupportedCallbackException(var1[var5], "Unrecognized message type");
            }

            var2.add(var6.getMessage());
         } else {
            JLabel var7;
            if (var1[var5] instanceof NameCallback) {
               final NameCallback var11 = (NameCallback)var1[var5];
               var7 = new JLabel(var11.getPrompt());
               final JTextField var8 = new JTextField(8);
               String var9 = var11.getDefaultName();
               if (var9 != null) {
                  var8.setText(var9);
               }

               Box var10 = Box.createHorizontalBox();
               var10.add(var7);
               var10.add(var8);
               var2.add(var10);
               var3.add(new DialogCallbackHandler.Action() {
                  public void perform() {
                     var11.setName(var8.getText());
                  }
               });
            } else if (var1[var5] instanceof PasswordCallback) {
               final PasswordCallback var12 = (PasswordCallback)var1[var5];
               var7 = new JLabel(var12.getPrompt());
               final JPasswordField var15 = new JPasswordField(8);
               if (!var12.isEchoOn()) {
                  var15.setEchoChar('*');
               }

               Box var16 = Box.createHorizontalBox();
               var16.add(var7);
               var16.add(var15);
               var2.add(var16);
               var3.add(new DialogCallbackHandler.Action() {
                  public void perform() {
                     var12.setPassword(var15.getPassword());
                  }
               });
            } else {
               if (!(var1[var5] instanceof ConfirmationCallback)) {
                  throw new UnsupportedCallbackException(var1[var5], "Unrecognized Callback");
               }

               ConfirmationCallback var13 = (ConfirmationCallback)var1[var5];
               var4.setCallback(var13);
               if (var13.getPrompt() != null) {
                  var2.add(var13.getPrompt());
               }
            }
         }
      }

      var5 = JOptionPane.showOptionDialog(this.parentComponent, var2.toArray(), "Confirmation", var4.optionType, var4.messageType, (Icon)null, var4.options, var4.initialValue);
      if (var5 == 0 || var5 == 0) {
         Iterator var14 = var3.iterator();

         while(var14.hasNext()) {
            ((DialogCallbackHandler.Action)var14.next()).perform();
         }
      }

      var4.handleResult(var5);
   }

   private static class ConfirmationInfo {
      private int[] translations;
      int optionType;
      Object[] options;
      Object initialValue;
      int messageType;
      private ConfirmationCallback callback;

      private ConfirmationInfo() {
         this.optionType = 2;
         this.options = null;
         this.initialValue = null;
         this.messageType = 3;
      }

      void setCallback(ConfirmationCallback var1) throws UnsupportedCallbackException {
         this.callback = var1;
         int var2 = var1.getOptionType();
         switch(var2) {
         case -1:
            this.options = var1.getOptions();
            this.translations = new int[]{-1, var1.getDefaultOption()};
            break;
         case 0:
            this.optionType = 0;
            this.translations = new int[]{0, 0, 1, 1, -1, 1};
            break;
         case 1:
            this.optionType = 1;
            this.translations = new int[]{0, 0, 1, 1, 2, 2, -1, 2};
            break;
         case 2:
            this.optionType = 2;
            this.translations = new int[]{0, 3, 2, 2, -1, 2};
            break;
         default:
            throw new UnsupportedCallbackException(var1, "Unrecognized option type: " + var2);
         }

         int var3 = var1.getMessageType();
         switch(var3) {
         case 0:
            this.messageType = 1;
            break;
         case 1:
            this.messageType = 2;
            break;
         case 2:
            this.messageType = 0;
            break;
         default:
            throw new UnsupportedCallbackException(var1, "Unrecognized message type: " + var3);
         }

      }

      void handleResult(int var1) {
         if (this.callback != null) {
            for(int var2 = 0; var2 < this.translations.length; var2 += 2) {
               if (this.translations[var2] == var1) {
                  var1 = this.translations[var2 + 1];
                  break;
               }
            }

            this.callback.setSelectedIndex(var1);
         }
      }

      // $FF: synthetic method
      ConfirmationInfo(Object var1) {
         this();
      }
   }

   private interface Action {
      void perform();
   }
}
