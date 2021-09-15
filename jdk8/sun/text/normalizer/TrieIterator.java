package sun.text.normalizer;

public class TrieIterator implements RangeValueIterator {
   private static final int BMP_INDEX_LENGTH_ = 2048;
   private static final int LEAD_SURROGATE_MIN_VALUE_ = 55296;
   private static final int TRAIL_SURROGATE_MIN_VALUE_ = 56320;
   private static final int TRAIL_SURROGATE_COUNT_ = 1024;
   private static final int TRAIL_SURROGATE_INDEX_BLOCK_LENGTH_ = 32;
   private static final int DATA_BLOCK_LENGTH_ = 32;
   private Trie m_trie_;
   private int m_initialValue_;
   private int m_currentCodepoint_;
   private int m_nextCodepoint_;
   private int m_nextValue_;
   private int m_nextIndex_;
   private int m_nextBlock_;
   private int m_nextBlockIndex_;
   private int m_nextTrailIndexOffset_;

   public TrieIterator(Trie var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Argument trie cannot be null");
      } else {
         this.m_trie_ = var1;
         this.m_initialValue_ = this.extract(this.m_trie_.getInitialValue());
         this.reset();
      }
   }

   public final boolean next(RangeValueIterator.Element var1) {
      if (this.m_nextCodepoint_ > 1114111) {
         return false;
      } else if (this.m_nextCodepoint_ < 65536 && this.calculateNextBMPElement(var1)) {
         return true;
      } else {
         this.calculateNextSupplementaryElement(var1);
         return true;
      }
   }

   public final void reset() {
      this.m_currentCodepoint_ = 0;
      this.m_nextCodepoint_ = 0;
      this.m_nextIndex_ = 0;
      this.m_nextBlock_ = this.m_trie_.m_index_[0] << 2;
      if (this.m_nextBlock_ == 0) {
         this.m_nextValue_ = this.m_initialValue_;
      } else {
         this.m_nextValue_ = this.extract(this.m_trie_.getValue(this.m_nextBlock_));
      }

      this.m_nextBlockIndex_ = 0;
      this.m_nextTrailIndexOffset_ = 32;
   }

   protected int extract(int var1) {
      return var1;
   }

   private final void setResult(RangeValueIterator.Element var1, int var2, int var3, int var4) {
      var1.start = var2;
      var1.limit = var3;
      var1.value = var4;
   }

   private final boolean calculateNextBMPElement(RangeValueIterator.Element var1) {
      int var2 = this.m_nextBlock_;
      int var3 = this.m_nextValue_;
      this.m_currentCodepoint_ = this.m_nextCodepoint_++;
      ++this.m_nextBlockIndex_;
      if (!this.checkBlockDetail(var3)) {
         this.setResult(var1, this.m_currentCodepoint_, this.m_nextCodepoint_, var3);
         return true;
      } else {
         do {
            if (this.m_nextCodepoint_ >= 65536) {
               --this.m_nextCodepoint_;
               --this.m_nextBlockIndex_;
               return false;
            }

            ++this.m_nextIndex_;
            if (this.m_nextCodepoint_ == 55296) {
               this.m_nextIndex_ = 2048;
            } else if (this.m_nextCodepoint_ == 56320) {
               this.m_nextIndex_ = this.m_nextCodepoint_ >> 5;
            }

            this.m_nextBlockIndex_ = 0;
         } while(this.checkBlock(var2, var3));

         this.setResult(var1, this.m_currentCodepoint_, this.m_nextCodepoint_, var3);
         return true;
      }
   }

   private final void calculateNextSupplementaryElement(RangeValueIterator.Element var1) {
      int var2 = this.m_nextValue_;
      int var3 = this.m_nextBlock_;
      ++this.m_nextCodepoint_;
      ++this.m_nextBlockIndex_;
      if (UTF16.getTrailSurrogate(this.m_nextCodepoint_) != '\udc00') {
         if (!this.checkNullNextTrailIndex() && !this.checkBlockDetail(var2)) {
            this.setResult(var1, this.m_currentCodepoint_, this.m_nextCodepoint_, var2);
            this.m_currentCodepoint_ = this.m_nextCodepoint_;
            return;
         }

         ++this.m_nextIndex_;
         ++this.m_nextTrailIndexOffset_;
         if (!this.checkTrailBlock(var3, var2)) {
            this.setResult(var1, this.m_currentCodepoint_, this.m_nextCodepoint_, var2);
            this.m_currentCodepoint_ = this.m_nextCodepoint_;
            return;
         }
      }

      int var4 = UTF16.getLeadSurrogate(this.m_nextCodepoint_);

      while(var4 < 56320) {
         int var5 = this.m_trie_.m_index_[var4 >> 5] << 2;
         if (var5 == this.m_trie_.m_dataOffset_) {
            if (var2 != this.m_initialValue_) {
               this.m_nextValue_ = this.m_initialValue_;
               this.m_nextBlock_ = 0;
               this.m_nextBlockIndex_ = 0;
               this.setResult(var1, this.m_currentCodepoint_, this.m_nextCodepoint_, var2);
               this.m_currentCodepoint_ = this.m_nextCodepoint_;
               return;
            }

            var4 += 32;
            this.m_nextCodepoint_ = UCharacterProperty.getRawSupplementary((char)var4, '\udc00');
         } else {
            if (this.m_trie_.m_dataManipulate_ == null) {
               throw new NullPointerException("The field DataManipulate in this Trie is null");
            }

            this.m_nextIndex_ = this.m_trie_.m_dataManipulate_.getFoldingOffset(this.m_trie_.getValue(var5 + (var4 & 31)));
            if (this.m_nextIndex_ <= 0) {
               if (var2 != this.m_initialValue_) {
                  this.m_nextValue_ = this.m_initialValue_;
                  this.m_nextBlock_ = 0;
                  this.m_nextBlockIndex_ = 0;
                  this.setResult(var1, this.m_currentCodepoint_, this.m_nextCodepoint_, var2);
                  this.m_currentCodepoint_ = this.m_nextCodepoint_;
                  return;
               }

               this.m_nextCodepoint_ += 1024;
            } else {
               this.m_nextTrailIndexOffset_ = 0;
               if (!this.checkTrailBlock(var3, var2)) {
                  this.setResult(var1, this.m_currentCodepoint_, this.m_nextCodepoint_, var2);
                  this.m_currentCodepoint_ = this.m_nextCodepoint_;
                  return;
               }
            }

            ++var4;
         }
      }

      this.setResult(var1, this.m_currentCodepoint_, 1114112, var2);
   }

   private final boolean checkBlockDetail(int var1) {
      while(this.m_nextBlockIndex_ < 32) {
         this.m_nextValue_ = this.extract(this.m_trie_.getValue(this.m_nextBlock_ + this.m_nextBlockIndex_));
         if (this.m_nextValue_ != var1) {
            return false;
         }

         ++this.m_nextBlockIndex_;
         ++this.m_nextCodepoint_;
      }

      return true;
   }

   private final boolean checkBlock(int var1, int var2) {
      this.m_nextBlock_ = this.m_trie_.m_index_[this.m_nextIndex_] << 2;
      if (this.m_nextBlock_ == var1 && this.m_nextCodepoint_ - this.m_currentCodepoint_ >= 32) {
         this.m_nextCodepoint_ += 32;
      } else if (this.m_nextBlock_ == 0) {
         if (var2 != this.m_initialValue_) {
            this.m_nextValue_ = this.m_initialValue_;
            this.m_nextBlockIndex_ = 0;
            return false;
         }

         this.m_nextCodepoint_ += 32;
      } else if (!this.checkBlockDetail(var2)) {
         return false;
      }

      return true;
   }

   private final boolean checkTrailBlock(int var1, int var2) {
      while(this.m_nextTrailIndexOffset_ < 32) {
         this.m_nextBlockIndex_ = 0;
         if (!this.checkBlock(var1, var2)) {
            return false;
         }

         ++this.m_nextTrailIndexOffset_;
         ++this.m_nextIndex_;
      }

      return true;
   }

   private final boolean checkNullNextTrailIndex() {
      if (this.m_nextIndex_ <= 0) {
         this.m_nextCodepoint_ += 1023;
         char var1 = UTF16.getLeadSurrogate(this.m_nextCodepoint_);
         int var2 = this.m_trie_.m_index_[var1 >> 5] << 2;
         if (this.m_trie_.m_dataManipulate_ == null) {
            throw new NullPointerException("The field DataManipulate in this Trie is null");
         } else {
            this.m_nextIndex_ = this.m_trie_.m_dataManipulate_.getFoldingOffset(this.m_trie_.getValue(var2 + (var1 & 31)));
            --this.m_nextIndex_;
            this.m_nextBlockIndex_ = 32;
            return true;
         }
      } else {
         return false;
      }
   }
}
