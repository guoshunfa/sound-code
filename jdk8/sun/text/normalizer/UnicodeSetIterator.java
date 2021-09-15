package sun.text.normalizer;

import java.util.Iterator;

public class UnicodeSetIterator {
   public static int IS_STRING = -1;
   public int codepoint;
   public int codepointEnd;
   public String string;
   private UnicodeSet set;
   private int endRange = 0;
   private int range = 0;
   protected int endElement;
   protected int nextElement;
   private Iterator<String> stringIterator = null;

   public UnicodeSetIterator(UnicodeSet var1) {
      this.reset(var1);
   }

   public boolean nextRange() {
      if (this.nextElement <= this.endElement) {
         this.codepointEnd = this.endElement;
         this.codepoint = this.nextElement;
         this.nextElement = this.endElement + 1;
         return true;
      } else if (this.range < this.endRange) {
         this.loadRange(++this.range);
         this.codepointEnd = this.endElement;
         this.codepoint = this.nextElement;
         this.nextElement = this.endElement + 1;
         return true;
      } else if (this.stringIterator == null) {
         return false;
      } else {
         this.codepoint = IS_STRING;
         this.string = (String)this.stringIterator.next();
         if (!this.stringIterator.hasNext()) {
            this.stringIterator = null;
         }

         return true;
      }
   }

   public void reset(UnicodeSet var1) {
      this.set = var1;
      this.reset();
   }

   public void reset() {
      this.endRange = this.set.getRangeCount() - 1;
      this.range = 0;
      this.endElement = -1;
      this.nextElement = 0;
      if (this.endRange >= 0) {
         this.loadRange(this.range);
      }

      this.stringIterator = null;
      if (this.set.strings != null) {
         this.stringIterator = this.set.strings.iterator();
         if (!this.stringIterator.hasNext()) {
            this.stringIterator = null;
         }
      }

   }

   protected void loadRange(int var1) {
      this.nextElement = this.set.getRangeStart(var1);
      this.endElement = this.set.getRangeEnd(var1);
   }
}
