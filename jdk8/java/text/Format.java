package java.text;

import java.io.Serializable;

public abstract class Format implements Serializable, Cloneable {
   private static final long serialVersionUID = -299282585814624189L;

   protected Format() {
   }

   public final String format(Object var1) {
      return this.format(var1, new StringBuffer(), new FieldPosition(0)).toString();
   }

   public abstract StringBuffer format(Object var1, StringBuffer var2, FieldPosition var3);

   public AttributedCharacterIterator formatToCharacterIterator(Object var1) {
      return this.createAttributedCharacterIterator(this.format(var1));
   }

   public abstract Object parseObject(String var1, ParsePosition var2);

   public Object parseObject(String var1) throws ParseException {
      ParsePosition var2 = new ParsePosition(0);
      Object var3 = this.parseObject(var1, var2);
      if (var2.index == 0) {
         throw new ParseException("Format.parseObject(String) failed", var2.errorIndex);
      } else {
         return var3;
      }
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2);
      }
   }

   AttributedCharacterIterator createAttributedCharacterIterator(String var1) {
      AttributedString var2 = new AttributedString(var1);
      return var2.getIterator();
   }

   AttributedCharacterIterator createAttributedCharacterIterator(AttributedCharacterIterator[] var1) {
      AttributedString var2 = new AttributedString(var1);
      return var2.getIterator();
   }

   AttributedCharacterIterator createAttributedCharacterIterator(String var1, AttributedCharacterIterator.Attribute var2, Object var3) {
      AttributedString var4 = new AttributedString(var1);
      var4.addAttribute(var2, var3);
      return var4.getIterator();
   }

   AttributedCharacterIterator createAttributedCharacterIterator(AttributedCharacterIterator var1, AttributedCharacterIterator.Attribute var2, Object var3) {
      AttributedString var4 = new AttributedString(var1);
      var4.addAttribute(var2, var3);
      return var4.getIterator();
   }

   interface FieldDelegate {
      void formatted(Format.Field var1, Object var2, int var3, int var4, StringBuffer var5);

      void formatted(int var1, Format.Field var2, Object var3, int var4, int var5, StringBuffer var6);
   }

   public static class Field extends AttributedCharacterIterator.Attribute {
      private static final long serialVersionUID = 276966692217360283L;

      protected Field(String var1) {
         super(var1);
      }
   }
}
