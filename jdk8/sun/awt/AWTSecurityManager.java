package sun.awt;

public class AWTSecurityManager extends SecurityManager {
   public AppContext getAppContext() {
      return null;
   }
}
