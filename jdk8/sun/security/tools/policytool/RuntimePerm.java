package sun.security.tools.policytool;

class RuntimePerm extends Perm {
   public RuntimePerm() {
      super("RuntimePermission", "java.lang.RuntimePermission", new String[]{"createClassLoader", "getClassLoader", "setContextClassLoader", "enableContextClassLoaderOverride", "setSecurityManager", "createSecurityManager", "getenv.<" + PolicyTool.getMessage("environment.variable.name") + ">", "exitVM", "shutdownHooks", "setFactory", "setIO", "modifyThread", "stopThread", "modifyThreadGroup", "getProtectionDomain", "readFileDescriptor", "writeFileDescriptor", "loadLibrary.<" + PolicyTool.getMessage("library.name") + ">", "accessClassInPackage.<" + PolicyTool.getMessage("package.name") + ">", "defineClassInPackage.<" + PolicyTool.getMessage("package.name") + ">", "accessDeclaredMembers", "queuePrintJob", "getStackTrace", "setDefaultUncaughtExceptionHandler", "preferences", "usePolicy"}, (String[])null);
   }
}
