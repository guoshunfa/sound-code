package javax.xml.bind;

import java.security.BasicPermission;

public final class JAXBPermission extends BasicPermission {
   private static final long serialVersionUID = 1L;

   public JAXBPermission(String name) {
      super(name);
   }
}
