package sun.security.tools.policytool;

class URLPerm extends Perm {
   public URLPerm() {
      super("URLPermission", "java.net.URLPermission", new String[]{"<" + PolicyTool.getMessage("url") + ">"}, new String[]{"<" + PolicyTool.getMessage("method.list") + ">:<" + PolicyTool.getMessage("request.headers.list") + ">"});
   }
}
