package sun.util.locale.provider;

import java.io.IOException;
import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.Stack;

class DictionaryBasedBreakIterator extends RuleBasedBreakIterator {
   private BreakDictionary dictionary;
   private boolean[] categoryFlags;
   private int dictionaryCharCount;
   private int[] cachedBreakPositions;
   private int positionInCache;

   DictionaryBasedBreakIterator(String var1, String var2) throws IOException {
      super(var1);
      byte[] var3 = super.getAdditionalData();
      if (var3 != null) {
         this.prepareCategoryFlags(var3);
         super.setAdditionalData((byte[])null);
      }

      this.dictionary = new BreakDictionary(var2);
   }

   private void prepareCategoryFlags(byte[] var1) {
      this.categoryFlags = new boolean[var1.length];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         this.categoryFlags[var2] = var1[var2] == 1;
      }

   }

   public void setText(CharacterIterator var1) {
      super.setText(var1);
      this.cachedBreakPositions = null;
      this.dictionaryCharCount = 0;
      this.positionInCache = 0;
   }

   public int first() {
      this.cachedBreakPositions = null;
      this.dictionaryCharCount = 0;
      this.positionInCache = 0;
      return super.first();
   }

   public int last() {
      this.cachedBreakPositions = null;
      this.dictionaryCharCount = 0;
      this.positionInCache = 0;
      return super.last();
   }

   public int previous() {
      CharacterIterator var1 = this.getText();
      if (this.cachedBreakPositions != null && this.positionInCache > 0) {
         --this.positionInCache;
         var1.setIndex(this.cachedBreakPositions[this.positionInCache]);
         return this.cachedBreakPositions[this.positionInCache];
      } else {
         this.cachedBreakPositions = null;
         int var2 = super.previous();
         if (this.cachedBreakPositions != null) {
            this.positionInCache = this.cachedBreakPositions.length - 2;
         }

         return var2;
      }
   }

   public int preceding(int var1) {
      CharacterIterator var2 = this.getText();
      checkOffset(var1, var2);
      if (this.cachedBreakPositions != null && var1 > this.cachedBreakPositions[0] && var1 <= this.cachedBreakPositions[this.cachedBreakPositions.length - 1]) {
         for(this.positionInCache = 0; this.positionInCache < this.cachedBreakPositions.length && var1 > this.cachedBreakPositions[this.positionInCache]; ++this.positionInCache) {
         }

         --this.positionInCache;
         var2.setIndex(this.cachedBreakPositions[this.positionInCache]);
         return var2.getIndex();
      } else {
         this.cachedBreakPositions = null;
         return super.preceding(var1);
      }
   }

   public int following(int var1) {
      CharacterIterator var2 = this.getText();
      checkOffset(var1, var2);
      if (this.cachedBreakPositions != null && var1 >= this.cachedBreakPositions[0] && var1 < this.cachedBreakPositions[this.cachedBreakPositions.length - 1]) {
         for(this.positionInCache = 0; this.positionInCache < this.cachedBreakPositions.length && var1 >= this.cachedBreakPositions[this.positionInCache]; ++this.positionInCache) {
         }

         var2.setIndex(this.cachedBreakPositions[this.positionInCache]);
         return var2.getIndex();
      } else {
         this.cachedBreakPositions = null;
         return super.following(var1);
      }
   }

   protected int handleNext() {
      CharacterIterator var1 = this.getText();
      if (this.cachedBreakPositions == null || this.positionInCache == this.cachedBreakPositions.length - 1) {
         int var2 = var1.getIndex();
         this.dictionaryCharCount = 0;
         int var3 = super.handleNext();
         if (this.dictionaryCharCount <= 1 || var3 - var2 <= 1) {
            this.cachedBreakPositions = null;
            return var3;
         }

         this.divideUpDictionaryRange(var2, var3);
      }

      if (this.cachedBreakPositions != null) {
         ++this.positionInCache;
         var1.setIndex(this.cachedBreakPositions[this.positionInCache]);
         return this.cachedBreakPositions[this.positionInCache];
      } else {
         return -9999;
      }
   }

   protected int lookupCategory(int var1) {
      int var2 = super.lookupCategory(var1);
      if (var2 != -1 && this.categoryFlags[var2]) {
         ++this.dictionaryCharCount;
      }

      return var2;
   }

   private void divideUpDictionaryRange(int var1, int var2) {
      CharacterIterator var3 = this.getText();
      var3.setIndex(var1);
      int var4 = this.getCurrent();

      for(int var5 = this.lookupCategory(var4); var5 == -1 || !this.categoryFlags[var5]; var5 = this.lookupCategory(var4)) {
         var4 = this.getNext();
      }

      Stack var6 = new Stack();
      Stack var7 = new Stack();
      ArrayList var8 = new ArrayList();
      short var9 = 0;
      int var10 = var3.getIndex();
      Stack var11 = null;
      var4 = this.getCurrent();

      while(true) {
         if (this.dictionary.getNextState(var9, 0) == -1) {
            var7.push(var3.getIndex());
         }

         var9 = this.dictionary.getNextStateFromCharacter(var9, var4);
         if (var9 == -1) {
            var6.push(var3.getIndex());
            break;
         }

         if (var9 != 0 && var3.getIndex() < var2) {
            var4 = this.getNext();
         } else {
            if (var3.getIndex() > var10) {
               var10 = var3.getIndex();
               Stack var12 = (Stack)var6.clone();
               var11 = var12;
            }

            while(!var7.isEmpty() && var8.contains(var7.peek())) {
               var7.pop();
            }

            if (var7.isEmpty()) {
               if (var11 != null) {
                  var6 = var11;
                  if (var10 >= var2) {
                     break;
                  }

                  var3.setIndex(var10 + 1);
               } else {
                  if ((var6.size() == 0 || (Integer)var6.peek() != var3.getIndex()) && var3.getIndex() != var1) {
                     var6.push(new Integer(var3.getIndex()));
                  }

                  this.getNext();
                  var6.push(new Integer(var3.getIndex()));
               }
            } else {
               Integer var14 = (Integer)var7.pop();
               Integer var13 = null;

               while(!var6.isEmpty() && var14 < (Integer)var6.peek()) {
                  var13 = (Integer)var6.pop();
                  var8.add(var13);
               }

               var6.push(var14);
               var3.setIndex((Integer)var6.peek());
            }

            var4 = this.getCurrent();
            if (var3.getIndex() >= var2) {
               break;
            }
         }
      }

      if (!var6.isEmpty()) {
         var6.pop();
      }

      var6.push(var2);
      this.cachedBreakPositions = new int[var6.size() + 1];
      this.cachedBreakPositions[0] = var1;

      for(int var15 = 0; var15 < var6.size(); ++var15) {
         this.cachedBreakPositions[var15 + 1] = (Integer)var6.elementAt(var15);
      }

      this.positionInCache = 0;
   }
}
