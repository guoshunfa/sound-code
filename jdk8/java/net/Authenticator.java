package java.net;

public abstract class Authenticator {
   private static Authenticator theAuthenticator;
   private String requestingHost;
   private InetAddress requestingSite;
   private int requestingPort;
   private String requestingProtocol;
   private String requestingPrompt;
   private String requestingScheme;
   private URL requestingURL;
   private Authenticator.RequestorType requestingAuthType;

   private void reset() {
      this.requestingHost = null;
      this.requestingSite = null;
      this.requestingPort = -1;
      this.requestingProtocol = null;
      this.requestingPrompt = null;
      this.requestingScheme = null;
      this.requestingURL = null;
      this.requestingAuthType = Authenticator.RequestorType.SERVER;
   }

   public static synchronized void setDefault(Authenticator var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         NetPermission var2 = new NetPermission("setDefaultAuthenticator");
         var1.checkPermission(var2);
      }

      theAuthenticator = var0;
   }

   public static PasswordAuthentication requestPasswordAuthentication(InetAddress var0, int var1, String var2, String var3, String var4) {
      SecurityManager var5 = System.getSecurityManager();
      if (var5 != null) {
         NetPermission var6 = new NetPermission("requestPasswordAuthentication");
         var5.checkPermission(var6);
      }

      Authenticator var10 = theAuthenticator;
      if (var10 == null) {
         return null;
      } else {
         synchronized(var10) {
            var10.reset();
            var10.requestingSite = var0;
            var10.requestingPort = var1;
            var10.requestingProtocol = var2;
            var10.requestingPrompt = var3;
            var10.requestingScheme = var4;
            return var10.getPasswordAuthentication();
         }
      }
   }

   public static PasswordAuthentication requestPasswordAuthentication(String var0, InetAddress var1, int var2, String var3, String var4, String var5) {
      SecurityManager var6 = System.getSecurityManager();
      if (var6 != null) {
         NetPermission var7 = new NetPermission("requestPasswordAuthentication");
         var6.checkPermission(var7);
      }

      Authenticator var11 = theAuthenticator;
      if (var11 == null) {
         return null;
      } else {
         synchronized(var11) {
            var11.reset();
            var11.requestingHost = var0;
            var11.requestingSite = var1;
            var11.requestingPort = var2;
            var11.requestingProtocol = var3;
            var11.requestingPrompt = var4;
            var11.requestingScheme = var5;
            return var11.getPasswordAuthentication();
         }
      }
   }

   public static PasswordAuthentication requestPasswordAuthentication(String var0, InetAddress var1, int var2, String var3, String var4, String var5, URL var6, Authenticator.RequestorType var7) {
      SecurityManager var8 = System.getSecurityManager();
      if (var8 != null) {
         NetPermission var9 = new NetPermission("requestPasswordAuthentication");
         var8.checkPermission(var9);
      }

      Authenticator var13 = theAuthenticator;
      if (var13 == null) {
         return null;
      } else {
         synchronized(var13) {
            var13.reset();
            var13.requestingHost = var0;
            var13.requestingSite = var1;
            var13.requestingPort = var2;
            var13.requestingProtocol = var3;
            var13.requestingPrompt = var4;
            var13.requestingScheme = var5;
            var13.requestingURL = var6;
            var13.requestingAuthType = var7;
            return var13.getPasswordAuthentication();
         }
      }
   }

   protected final String getRequestingHost() {
      return this.requestingHost;
   }

   protected final InetAddress getRequestingSite() {
      return this.requestingSite;
   }

   protected final int getRequestingPort() {
      return this.requestingPort;
   }

   protected final String getRequestingProtocol() {
      return this.requestingProtocol;
   }

   protected final String getRequestingPrompt() {
      return this.requestingPrompt;
   }

   protected final String getRequestingScheme() {
      return this.requestingScheme;
   }

   protected PasswordAuthentication getPasswordAuthentication() {
      return null;
   }

   protected URL getRequestingURL() {
      return this.requestingURL;
   }

   protected Authenticator.RequestorType getRequestorType() {
      return this.requestingAuthType;
   }

   public static enum RequestorType {
      PROXY,
      SERVER;
   }
}
