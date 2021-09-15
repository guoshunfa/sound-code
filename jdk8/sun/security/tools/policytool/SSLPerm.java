package sun.security.tools.policytool;

class SSLPerm extends Perm {
   public SSLPerm() {
      super("SSLPermission", "javax.net.ssl.SSLPermission", new String[]{"setHostnameVerifier", "getSSLSessionContext"}, (String[])null);
   }
}
