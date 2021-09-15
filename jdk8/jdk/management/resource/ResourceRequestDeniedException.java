package jdk.management.resource;

public class ResourceRequestDeniedException extends RuntimeException {
   private static final long serialVersionUID = 4861402271690587669L;

   public ResourceRequestDeniedException() {
   }

   public ResourceRequestDeniedException(String var1) {
      super(var1);
   }

   public ResourceRequestDeniedException(String var1, Throwable var2) {
      super(var1, var2);
   }
}
