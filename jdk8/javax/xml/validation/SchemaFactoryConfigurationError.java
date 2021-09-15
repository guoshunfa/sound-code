package javax.xml.validation;

public final class SchemaFactoryConfigurationError extends Error {
   static final long serialVersionUID = 3531438703147750126L;

   public SchemaFactoryConfigurationError() {
   }

   public SchemaFactoryConfigurationError(String message) {
      super(message);
   }

   public SchemaFactoryConfigurationError(Throwable cause) {
      super(cause);
   }

   public SchemaFactoryConfigurationError(String message, Throwable cause) {
      super(message, cause);
   }
}
