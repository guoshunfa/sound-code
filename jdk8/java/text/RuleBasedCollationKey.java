package java.text;

final class RuleBasedCollationKey extends CollationKey {
   private String key = null;

   public int compareTo(CollationKey var1) {
      int var2 = this.key.compareTo(((RuleBasedCollationKey)((RuleBasedCollationKey)var1)).key);
      if (var2 <= -1) {
         return -1;
      } else {
         return var2 >= 1 ? 1 : 0;
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass().equals(var1.getClass())) {
         RuleBasedCollationKey var2 = (RuleBasedCollationKey)var1;
         return this.key.equals(var2.key);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.key.hashCode();
   }

   public byte[] toByteArray() {
      char[] var1 = this.key.toCharArray();
      byte[] var2 = new byte[2 * var1.length];
      int var3 = 0;

      for(int var4 = 0; var4 < var1.length; ++var4) {
         var2[var3++] = (byte)(var1[var4] >>> 8);
         var2[var3++] = (byte)(var1[var4] & 255);
      }

      return var2;
   }

   RuleBasedCollationKey(String var1, String var2) {
      super(var1);
      this.key = var2;
   }
}
