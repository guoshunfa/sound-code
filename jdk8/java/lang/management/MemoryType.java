package java.lang.management;

public enum MemoryType {
   HEAP("Heap memory"),
   NON_HEAP("Non-heap memory");

   private final String description;
   private static final long serialVersionUID = 6992337162326171013L;

   private MemoryType(String var3) {
      this.description = var3;
   }

   public String toString() {
      return this.description;
   }
}
