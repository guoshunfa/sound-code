package javax.xml.bind;

import java.security.PrivilegedAction;

final class GetPropertyAction implements PrivilegedAction<String> {
   private final String propertyName;

   public GetPropertyAction(String propertyName) {
      this.propertyName = propertyName;
   }

   public String run() {
      return System.getProperty(this.propertyName);
   }
}
