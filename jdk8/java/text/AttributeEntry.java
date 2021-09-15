package java.text;

import java.util.Map;

class AttributeEntry implements Map.Entry<AttributedCharacterIterator.Attribute, Object> {
   private AttributedCharacterIterator.Attribute key;
   private Object value;

   AttributeEntry(AttributedCharacterIterator.Attribute var1, Object var2) {
      this.key = var1;
      this.value = var2;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof AttributeEntry)) {
         return false;
      } else {
         boolean var10000;
         label31: {
            AttributeEntry var2 = (AttributeEntry)var1;
            if (var2.key.equals(this.key)) {
               if (this.value == null) {
                  if (var2.value == null) {
                     break label31;
                  }
               } else if (var2.value.equals(this.value)) {
                  break label31;
               }
            }

            var10000 = false;
            return var10000;
         }

         var10000 = true;
         return var10000;
      }
   }

   public AttributedCharacterIterator.Attribute getKey() {
      return this.key;
   }

   public Object getValue() {
      return this.value;
   }

   public Object setValue(Object var1) {
      throw new UnsupportedOperationException();
   }

   public int hashCode() {
      return this.key.hashCode() ^ (this.value == null ? 0 : this.value.hashCode());
   }

   public String toString() {
      return this.key.toString() + "=" + this.value.toString();
   }
}
