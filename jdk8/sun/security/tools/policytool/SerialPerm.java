package sun.security.tools.policytool;

class SerialPerm extends Perm {
   public SerialPerm() {
      super("SerializablePermission", "java.io.SerializablePermission", new String[]{"enableSubclassImplementation", "enableSubstitution"}, (String[])null);
   }
}
