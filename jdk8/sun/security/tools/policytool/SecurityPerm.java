package sun.security.tools.policytool;

class SecurityPerm extends Perm {
   public SecurityPerm() {
      super("SecurityPermission", "java.security.SecurityPermission", new String[]{"createAccessControlContext", "getDomainCombiner", "getPolicy", "setPolicy", "createPolicy.<" + PolicyTool.getMessage("policy.type") + ">", "getProperty.<" + PolicyTool.getMessage("property.name") + ">", "setProperty.<" + PolicyTool.getMessage("property.name") + ">", "insertProvider.<" + PolicyTool.getMessage("provider.name") + ">", "removeProvider.<" + PolicyTool.getMessage("provider.name") + ">", "clearProviderProperties.<" + PolicyTool.getMessage("provider.name") + ">", "putProviderProperty.<" + PolicyTool.getMessage("provider.name") + ">", "removeProviderProperty.<" + PolicyTool.getMessage("provider.name") + ">"}, (String[])null);
   }
}
