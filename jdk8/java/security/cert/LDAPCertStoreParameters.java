package java.security.cert;

public class LDAPCertStoreParameters implements CertStoreParameters {
   private static final int LDAP_DEFAULT_PORT = 389;
   private int port;
   private String serverName;

   public LDAPCertStoreParameters(String var1, int var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.serverName = var1;
         this.port = var2;
      }
   }

   public LDAPCertStoreParameters(String var1) {
      this(var1, 389);
   }

   public LDAPCertStoreParameters() {
      this("localhost", 389);
   }

   public String getServerName() {
      return this.serverName;
   }

   public int getPort() {
      return this.port;
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2.toString(), var2);
      }
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append("LDAPCertStoreParameters: [\n");
      var1.append("  serverName: " + this.serverName + "\n");
      var1.append("  port: " + this.port + "\n");
      var1.append("]");
      return var1.toString();
   }
}
