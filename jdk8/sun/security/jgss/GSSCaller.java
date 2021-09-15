package sun.security.jgss;

public class GSSCaller {
   public static final GSSCaller CALLER_UNKNOWN = new GSSCaller("UNKNOWN");
   public static final GSSCaller CALLER_INITIATE = new GSSCaller("INITIATE");
   public static final GSSCaller CALLER_ACCEPT = new GSSCaller("ACCEPT");
   public static final GSSCaller CALLER_SSL_CLIENT = new GSSCaller("SSL_CLIENT");
   public static final GSSCaller CALLER_SSL_SERVER = new GSSCaller("SSL_SERVER");
   private String name;

   GSSCaller(String var1) {
      this.name = var1;
   }

   public String toString() {
      return "GSSCaller{" + this.name + '}';
   }
}
