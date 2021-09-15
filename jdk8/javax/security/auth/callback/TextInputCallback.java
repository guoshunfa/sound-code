package javax.security.auth.callback;

import java.io.Serializable;

public class TextInputCallback implements Callback, Serializable {
   private static final long serialVersionUID = -8064222478852811804L;
   private String prompt;
   private String defaultText;
   private String inputText;

   public TextInputCallback(String var1) {
      if (var1 != null && var1.length() != 0) {
         this.prompt = var1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public TextInputCallback(String var1, String var2) {
      if (var1 != null && var1.length() != 0 && var2 != null && var2.length() != 0) {
         this.prompt = var1;
         this.defaultText = var2;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public String getPrompt() {
      return this.prompt;
   }

   public String getDefaultText() {
      return this.defaultText;
   }

   public void setText(String var1) {
      this.inputText = var1;
   }

   public String getText() {
      return this.inputText;
   }
}
