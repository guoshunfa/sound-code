package java.text;

final class EntryPair {
   public String entryName;
   public int value;
   public boolean fwd;

   public EntryPair(String var1, int var2) {
      this(var1, var2, true);
   }

   public EntryPair(String var1, int var2, boolean var3) {
      this.entryName = var1;
      this.value = var2;
      this.fwd = var3;
   }
}
