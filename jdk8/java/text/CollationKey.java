package java.text;

public abstract class CollationKey implements Comparable<CollationKey> {
   private final String source;

   public abstract int compareTo(CollationKey var1);

   public String getSourceString() {
      return this.source;
   }

   public abstract byte[] toByteArray();

   protected CollationKey(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.source = var1;
      }
   }
}
