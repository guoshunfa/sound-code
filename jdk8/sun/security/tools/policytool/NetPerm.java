package sun.security.tools.policytool;

class NetPerm extends Perm {
   public NetPerm() {
      super("NetPermission", "java.net.NetPermission", new String[]{"setDefaultAuthenticator", "requestPasswordAuthentication", "specifyStreamHandler", "setProxySelector", "getProxySelector", "setCookieHandler", "getCookieHandler", "setResponseCache", "getResponseCache"}, (String[])null);
   }
}
