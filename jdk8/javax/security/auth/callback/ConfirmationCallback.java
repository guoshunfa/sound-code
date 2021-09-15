package javax.security.auth.callback;

import java.io.Serializable;

public class ConfirmationCallback implements Callback, Serializable {
   private static final long serialVersionUID = -9095656433782481624L;
   public static final int UNSPECIFIED_OPTION = -1;
   public static final int YES_NO_OPTION = 0;
   public static final int YES_NO_CANCEL_OPTION = 1;
   public static final int OK_CANCEL_OPTION = 2;
   public static final int YES = 0;
   public static final int NO = 1;
   public static final int CANCEL = 2;
   public static final int OK = 3;
   public static final int INFORMATION = 0;
   public static final int WARNING = 1;
   public static final int ERROR = 2;
   private String prompt;
   private int messageType;
   private int optionType = -1;
   private int defaultOption;
   private String[] options;
   private int selection;

   public ConfirmationCallback(int var1, int var2, int var3) {
      if (var1 >= 0 && var1 <= 2 && var2 >= 0 && var2 <= 2) {
         switch(var2) {
         case 0:
            if (var3 != 0 && var3 != 1) {
               throw new IllegalArgumentException();
            }
            break;
         case 1:
            if (var3 != 0 && var3 != 1 && var3 != 2) {
               throw new IllegalArgumentException();
            }
            break;
         case 2:
            if (var3 != 3 && var3 != 2) {
               throw new IllegalArgumentException();
            }
         }

         this.messageType = var1;
         this.optionType = var2;
         this.defaultOption = var3;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public ConfirmationCallback(int var1, String[] var2, int var3) {
      if (var1 >= 0 && var1 <= 2 && var2 != null && var2.length != 0 && var3 >= 0 && var3 < var2.length) {
         for(int var4 = 0; var4 < var2.length; ++var4) {
            if (var2[var4] == null || var2[var4].length() == 0) {
               throw new IllegalArgumentException();
            }
         }

         this.messageType = var1;
         this.options = var2;
         this.defaultOption = var3;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public ConfirmationCallback(String var1, int var2, int var3, int var4) {
      if (var1 != null && var1.length() != 0 && var2 >= 0 && var2 <= 2 && var3 >= 0 && var3 <= 2) {
         switch(var3) {
         case 0:
            if (var4 != 0 && var4 != 1) {
               throw new IllegalArgumentException();
            }
            break;
         case 1:
            if (var4 != 0 && var4 != 1 && var4 != 2) {
               throw new IllegalArgumentException();
            }
            break;
         case 2:
            if (var4 != 3 && var4 != 2) {
               throw new IllegalArgumentException();
            }
         }

         this.prompt = var1;
         this.messageType = var2;
         this.optionType = var3;
         this.defaultOption = var4;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public ConfirmationCallback(String var1, int var2, String[] var3, int var4) {
      if (var1 != null && var1.length() != 0 && var2 >= 0 && var2 <= 2 && var3 != null && var3.length != 0 && var4 >= 0 && var4 < var3.length) {
         for(int var5 = 0; var5 < var3.length; ++var5) {
            if (var3[var5] == null || var3[var5].length() == 0) {
               throw new IllegalArgumentException();
            }
         }

         this.prompt = var1;
         this.messageType = var2;
         this.options = var3;
         this.defaultOption = var4;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public String getPrompt() {
      return this.prompt;
   }

   public int getMessageType() {
      return this.messageType;
   }

   public int getOptionType() {
      return this.optionType;
   }

   public String[] getOptions() {
      return this.options;
   }

   public int getDefaultOption() {
      return this.defaultOption;
   }

   public void setSelectedIndex(int var1) {
      this.selection = var1;
   }

   public int getSelectedIndex() {
      return this.selection;
   }
}
