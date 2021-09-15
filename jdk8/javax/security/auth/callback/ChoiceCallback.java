package javax.security.auth.callback;

import java.io.Serializable;

public class ChoiceCallback implements Callback, Serializable {
   private static final long serialVersionUID = -3975664071579892167L;
   private String prompt;
   private String[] choices;
   private int defaultChoice;
   private boolean multipleSelectionsAllowed;
   private int[] selections;

   public ChoiceCallback(String var1, String[] var2, int var3, boolean var4) {
      if (var1 != null && var1.length() != 0 && var2 != null && var2.length != 0 && var3 >= 0 && var3 < var2.length) {
         for(int var5 = 0; var5 < var2.length; ++var5) {
            if (var2[var5] == null || var2[var5].length() == 0) {
               throw new IllegalArgumentException();
            }
         }

         this.prompt = var1;
         this.choices = var2;
         this.defaultChoice = var3;
         this.multipleSelectionsAllowed = var4;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public String getPrompt() {
      return this.prompt;
   }

   public String[] getChoices() {
      return this.choices;
   }

   public int getDefaultChoice() {
      return this.defaultChoice;
   }

   public boolean allowMultipleSelections() {
      return this.multipleSelectionsAllowed;
   }

   public void setSelectedIndex(int var1) {
      this.selections = new int[1];
      this.selections[0] = var1;
   }

   public void setSelectedIndexes(int[] var1) {
      if (!this.multipleSelectionsAllowed) {
         throw new UnsupportedOperationException();
      } else {
         this.selections = var1;
      }
   }

   public int[] getSelectedIndexes() {
      return this.selections;
   }
}
