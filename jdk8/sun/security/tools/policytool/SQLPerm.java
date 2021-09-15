package sun.security.tools.policytool;

class SQLPerm extends Perm {
   public SQLPerm() {
      super("SQLPermission", "java.sql.SQLPermission", new String[]{"setLog", "callAbort", "setSyncFactory", "setNetworkTimeout"}, (String[])null);
   }
}
