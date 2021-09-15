package sun.security.tools.policytool;

class FilePerm extends Perm {
   public FilePerm() {
      super("FilePermission", "java.io.FilePermission", new String[]{"<<ALL FILES>>"}, new String[]{"read", "write", "delete", "execute"});
   }
}
