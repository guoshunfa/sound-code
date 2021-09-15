package java.text;

public class FieldPosition {
   int field;
   int endIndex;
   int beginIndex;
   private Format.Field attribute;

   public FieldPosition(int var1) {
      this.field = 0;
      this.endIndex = 0;
      this.beginIndex = 0;
      this.field = var1;
   }

   public FieldPosition(Format.Field var1) {
      this(var1, -1);
   }

   public FieldPosition(Format.Field var1, int var2) {
      this.field = 0;
      this.endIndex = 0;
      this.beginIndex = 0;
      this.attribute = var1;
      this.field = var2;
   }

   public Format.Field getFieldAttribute() {
      return this.attribute;
   }

   public int getField() {
      return this.field;
   }

   public int getBeginIndex() {
      return this.beginIndex;
   }

   public int getEndIndex() {
      return this.endIndex;
   }

   public void setBeginIndex(int var1) {
      this.beginIndex = var1;
   }

   public void setEndIndex(int var1) {
      this.endIndex = var1;
   }

   Format.FieldDelegate getFieldDelegate() {
      return new FieldPosition.Delegate();
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (!(var1 instanceof FieldPosition)) {
         return false;
      } else {
         FieldPosition var2 = (FieldPosition)var1;
         if (this.attribute == null) {
            if (var2.attribute != null) {
               return false;
            }
         } else if (!this.attribute.equals(var2.attribute)) {
            return false;
         }

         return this.beginIndex == var2.beginIndex && this.endIndex == var2.endIndex && this.field == var2.field;
      }
   }

   public int hashCode() {
      return this.field << 24 | this.beginIndex << 16 | this.endIndex;
   }

   public String toString() {
      return this.getClass().getName() + "[field=" + this.field + ",attribute=" + this.attribute + ",beginIndex=" + this.beginIndex + ",endIndex=" + this.endIndex + ']';
   }

   private boolean matchesField(Format.Field var1) {
      return this.attribute != null ? this.attribute.equals(var1) : false;
   }

   private boolean matchesField(Format.Field var1, int var2) {
      if (this.attribute != null) {
         return this.attribute.equals(var1);
      } else {
         return var2 == this.field;
      }
   }

   private class Delegate implements Format.FieldDelegate {
      private boolean encounteredField;

      private Delegate() {
      }

      public void formatted(Format.Field var1, Object var2, int var3, int var4, StringBuffer var5) {
         if (!this.encounteredField && FieldPosition.this.matchesField(var1)) {
            FieldPosition.this.setBeginIndex(var3);
            FieldPosition.this.setEndIndex(var4);
            this.encounteredField = var3 != var4;
         }

      }

      public void formatted(int var1, Format.Field var2, Object var3, int var4, int var5, StringBuffer var6) {
         if (!this.encounteredField && FieldPosition.this.matchesField(var2, var1)) {
            FieldPosition.this.setBeginIndex(var4);
            FieldPosition.this.setEndIndex(var5);
            this.encounteredField = var4 != var5;
         }

      }

      // $FF: synthetic method
      Delegate(Object var2) {
         this();
      }
   }
}
