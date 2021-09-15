package java.net;

import java.io.IOException;

public class HttpRetryException extends IOException {
   private static final long serialVersionUID = -9186022286469111381L;
   private int responseCode;
   private String location;

   public HttpRetryException(String var1, int var2) {
      super(var1);
      this.responseCode = var2;
   }

   public HttpRetryException(String var1, int var2, String var3) {
      super(var1);
      this.responseCode = var2;
      this.location = var3;
   }

   public int responseCode() {
      return this.responseCode;
   }

   public String getReason() {
      return super.getMessage();
   }

   public String getLocation() {
      return this.location;
   }
}
