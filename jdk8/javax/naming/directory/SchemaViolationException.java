package javax.naming.directory;

import javax.naming.NamingException;

public class SchemaViolationException extends NamingException {
   private static final long serialVersionUID = -3041762429525049663L;

   public SchemaViolationException() {
   }

   public SchemaViolationException(String var1) {
      super(var1);
   }
}
