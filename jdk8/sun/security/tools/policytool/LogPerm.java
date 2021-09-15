package sun.security.tools.policytool;

class LogPerm extends Perm {
   public LogPerm() {
      super("LoggingPermission", "java.util.logging.LoggingPermission", new String[]{"control"}, (String[])null);
   }
}
