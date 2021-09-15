package sun.font;

public final class FontRunIterator {
   CompositeFont font;
   char[] text;
   int start;
   int limit;
   CompositeGlyphMapper mapper;
   int slot = -1;
   int pos;
   static final int SURROGATE_START = 65536;
   static final int LEAD_START = 55296;
   static final int LEAD_LIMIT = 56320;
   static final int TAIL_START = 56320;
   static final int TAIL_LIMIT = 57344;
   static final int LEAD_SURROGATE_SHIFT = 10;
   static final int SURROGATE_OFFSET = -56613888;
   static final int DONE = -1;

   public void init(CompositeFont var1, char[] var2, int var3, int var4) {
      if (var1 != null && var2 != null && var3 >= 0 && var4 >= var3 && var4 <= var2.length) {
         this.font = var1;
         this.text = var2;
         this.start = var3;
         this.limit = var4;
         this.mapper = (CompositeGlyphMapper)var1.getMapper();
         this.slot = -1;
         this.pos = var3;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public PhysicalFont getFont() {
      return this.slot == -1 ? null : this.font.getSlotFont(this.slot);
   }

   public int getGlyphMask() {
      return this.slot << 24;
   }

   public int getPos() {
      return this.pos;
   }

   public boolean next(int var1, int var2) {
      if (this.pos == var2) {
         return false;
      } else {
         int var3 = this.nextCodePoint(var2);
         int var4 = this.mapper.charToGlyph(var3) & -16777216;
         this.slot = var4 >>> 24;

         while((var3 = this.nextCodePoint(var2)) != -1 && (this.mapper.charToGlyph(var3) & -16777216) == var4) {
         }

         this.pushback(var3);
         return true;
      }
   }

   public boolean next() {
      return this.next(0, this.limit);
   }

   final int nextCodePoint() {
      return this.nextCodePoint(this.limit);
   }

   final int nextCodePoint(int var1) {
      if (this.pos >= var1) {
         return -1;
      } else {
         int var2 = this.text[this.pos++];
         if (var2 >= 55296 && var2 < 56320 && this.pos < var1) {
            char var3 = this.text[this.pos];
            if (var3 >= '\udc00' && var3 < '\ue000') {
               ++this.pos;
               var2 = (var2 << 10) + var3 + -56613888;
            }
         }

         return var2;
      }
   }

   final void pushback(int var1) {
      if (var1 >= 0) {
         if (var1 >= 65536) {
            this.pos -= 2;
         } else {
            --this.pos;
         }
      }

   }
}
