package sun.awt;

final class MostRecentKeyValue {
   Object key;
   Object value;

   MostRecentKeyValue(Object var1, Object var2) {
      this.key = var1;
      this.value = var2;
   }

   void setPair(Object var1, Object var2) {
      this.key = var1;
      this.value = var2;
   }
}
