package java.text;

import java.util.ArrayList;

class CharacterIteratorFieldDelegate implements Format.FieldDelegate {
   private ArrayList<AttributedString> attributedStrings = new ArrayList();
   private int size;

   public void formatted(Format.Field var1, Object var2, int var3, int var4, StringBuffer var5) {
      if (var3 != var4) {
         int var6;
         if (var3 < this.size) {
            var6 = this.size;

            int var9;
            for(int var7 = this.attributedStrings.size() - 1; var3 < var6; var6 = var9) {
               AttributedString var8 = (AttributedString)this.attributedStrings.get(var7--);
               var9 = var6 - var8.length();
               int var10 = Math.max(0, var3 - var9);
               var8.addAttribute(var1, var2, var10, Math.min(var4 - var3, var8.length() - var10) + var10);
            }
         }

         if (this.size < var3) {
            this.attributedStrings.add(new AttributedString(var5.substring(this.size, var3)));
            this.size = var3;
         }

         if (this.size < var4) {
            var6 = Math.max(var3, this.size);
            AttributedString var11 = new AttributedString(var5.substring(var6, var4));
            var11.addAttribute(var1, var2);
            this.attributedStrings.add(var11);
            this.size = var4;
         }
      }

   }

   public void formatted(int var1, Format.Field var2, Object var3, int var4, int var5, StringBuffer var6) {
      this.formatted(var2, var3, var4, var5, var6);
   }

   public AttributedCharacterIterator getIterator(String var1) {
      if (var1.length() > this.size) {
         this.attributedStrings.add(new AttributedString(var1.substring(this.size)));
         this.size = var1.length();
      }

      int var2 = this.attributedStrings.size();
      AttributedCharacterIterator[] var3 = new AttributedCharacterIterator[var2];

      for(int var4 = 0; var4 < var2; ++var4) {
         var3[var4] = ((AttributedString)this.attributedStrings.get(var4)).getIterator();
      }

      return (new AttributedString(var3)).getIterator();
   }
}
