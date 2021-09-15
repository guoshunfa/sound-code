package javax.naming.directory;

import javax.naming.NamingException;

public class AttributeInUseException extends NamingException {
   private static final long serialVersionUID = 4437710305529322564L;

   public AttributeInUseException(String var1) {
      super(var1);
   }

   public AttributeInUseException() {
   }
}
