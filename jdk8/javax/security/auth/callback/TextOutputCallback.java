package javax.security.auth.callback;

import java.io.Serializable;

public class TextOutputCallback implements Callback, Serializable {
   private static final long serialVersionUID = 1689502495511663102L;
   public static final int INFORMATION = 0;
   public static final int WARNING = 1;
   public static final int ERROR = 2;
   private int messageType;
   private String message;

   public TextOutputCallback(int var1, String var2) {
      if ((var1 == 0 || var1 == 1 || var1 == 2) && var2 != null && var2.length() != 0) {
         this.messageType = var1;
         this.message = var2;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int getMessageType() {
      return this.messageType;
   }

   public String getMessage() {
      return this.message;
   }
}
