package java.sql;

import java.util.Map;

public class SQLClientInfoException extends SQLException {
   private Map<String, ClientInfoStatus> failedProperties;
   private static final long serialVersionUID = -4319604256824655880L;

   public SQLClientInfoException() {
      this.failedProperties = null;
   }

   public SQLClientInfoException(Map<String, ClientInfoStatus> var1) {
      this.failedProperties = var1;
   }

   public SQLClientInfoException(Map<String, ClientInfoStatus> var1, Throwable var2) {
      super(var2 != null ? var2.toString() : null);
      this.initCause(var2);
      this.failedProperties = var1;
   }

   public SQLClientInfoException(String var1, Map<String, ClientInfoStatus> var2) {
      super(var1);
      this.failedProperties = var2;
   }

   public SQLClientInfoException(String var1, Map<String, ClientInfoStatus> var2, Throwable var3) {
      super(var1);
      this.initCause(var3);
      this.failedProperties = var2;
   }

   public SQLClientInfoException(String var1, String var2, Map<String, ClientInfoStatus> var3) {
      super(var1, var2);
      this.failedProperties = var3;
   }

   public SQLClientInfoException(String var1, String var2, Map<String, ClientInfoStatus> var3, Throwable var4) {
      super(var1, var2);
      this.initCause(var4);
      this.failedProperties = var3;
   }

   public SQLClientInfoException(String var1, String var2, int var3, Map<String, ClientInfoStatus> var4) {
      super(var1, var2, var3);
      this.failedProperties = var4;
   }

   public SQLClientInfoException(String var1, String var2, int var3, Map<String, ClientInfoStatus> var4, Throwable var5) {
      super(var1, var2, var3);
      this.initCause(var5);
      this.failedProperties = var4;
   }

   public Map<String, ClientInfoStatus> getFailedProperties() {
      return this.failedProperties;
   }
}
