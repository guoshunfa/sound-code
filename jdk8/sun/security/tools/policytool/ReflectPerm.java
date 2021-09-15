package sun.security.tools.policytool;

class ReflectPerm extends Perm {
   public ReflectPerm() {
      super("ReflectPermission", "java.lang.reflect.ReflectPermission", new String[]{"suppressAccessChecks"}, (String[])null);
   }
}
