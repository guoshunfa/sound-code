package sun.security.tools.policytool;

class AuthPerm extends Perm {
   public AuthPerm() {
      super("AuthPermission", "javax.security.auth.AuthPermission", new String[]{"doAs", "doAsPrivileged", "getSubject", "getSubjectFromDomainCombiner", "setReadOnly", "modifyPrincipals", "modifyPublicCredentials", "modifyPrivateCredentials", "refreshCredential", "destroyCredential", "createLoginContext.<" + PolicyTool.getMessage("name") + ">", "getLoginConfiguration", "setLoginConfiguration", "createLoginConfiguration.<" + PolicyTool.getMessage("configuration.type") + ">", "refreshLoginConfiguration"}, (String[])null);
   }
}
