package java.lang.instrument;

public final class ClassDefinition {
   private final Class<?> mClass;
   private final byte[] mClassFile;

   public ClassDefinition(Class<?> var1, byte[] var2) {
      if (var1 != null && var2 != null) {
         this.mClass = var1;
         this.mClassFile = var2;
      } else {
         throw new NullPointerException();
      }
   }

   public Class<?> getDefinitionClass() {
      return this.mClass;
   }

   public byte[] getDefinitionClassFile() {
      return this.mClassFile;
   }
}
