package sun.text.normalizer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class Trie {
   protected static final int LEAD_INDEX_OFFSET_ = 320;
   protected static final int INDEX_STAGE_1_SHIFT_ = 5;
   protected static final int INDEX_STAGE_2_SHIFT_ = 2;
   protected static final int DATA_BLOCK_LENGTH = 32;
   protected static final int INDEX_STAGE_3_MASK_ = 31;
   protected static final int SURROGATE_BLOCK_BITS = 5;
   protected static final int SURROGATE_BLOCK_COUNT = 32;
   protected static final int BMP_INDEX_LENGTH = 2048;
   protected static final int SURROGATE_MASK_ = 1023;
   protected char[] m_index_;
   protected Trie.DataManipulate m_dataManipulate_;
   protected int m_dataOffset_;
   protected int m_dataLength_;
   protected static final int HEADER_OPTIONS_LATIN1_IS_LINEAR_MASK_ = 512;
   protected static final int HEADER_SIGNATURE_ = 1416784229;
   private static final int HEADER_OPTIONS_SHIFT_MASK_ = 15;
   protected static final int HEADER_OPTIONS_INDEX_SHIFT_ = 4;
   protected static final int HEADER_OPTIONS_DATA_IS_32_BIT_ = 256;
   private boolean m_isLatin1Linear_;
   private int m_options_;

   protected Trie(InputStream var1, Trie.DataManipulate var2) throws IOException {
      DataInputStream var3 = new DataInputStream(var1);
      int var4 = var3.readInt();
      this.m_options_ = var3.readInt();
      if (!this.checkHeader(var4)) {
         throw new IllegalArgumentException("ICU data file error: Trie header authentication failed, please check if you have the most updated ICU data file");
      } else {
         if (var2 != null) {
            this.m_dataManipulate_ = var2;
         } else {
            this.m_dataManipulate_ = new Trie.DefaultGetFoldingOffset();
         }

         this.m_isLatin1Linear_ = (this.m_options_ & 512) != 0;
         this.m_dataOffset_ = var3.readInt();
         this.m_dataLength_ = var3.readInt();
         this.unserialize(var1);
      }
   }

   protected Trie(char[] var1, int var2, Trie.DataManipulate var3) {
      this.m_options_ = var2;
      if (var3 != null) {
         this.m_dataManipulate_ = var3;
      } else {
         this.m_dataManipulate_ = new Trie.DefaultGetFoldingOffset();
      }

      this.m_isLatin1Linear_ = (this.m_options_ & 512) != 0;
      this.m_index_ = var1;
      this.m_dataOffset_ = this.m_index_.length;
   }

   protected abstract int getSurrogateOffset(char var1, char var2);

   protected abstract int getValue(int var1);

   protected abstract int getInitialValue();

   protected final int getRawOffset(int var1, char var2) {
      return (this.m_index_[var1 + (var2 >> 5)] << 2) + (var2 & 31);
   }

   protected final int getBMPOffset(char var1) {
      return var1 >= '\ud800' && var1 <= '\udbff' ? this.getRawOffset(320, var1) : this.getRawOffset(0, var1);
   }

   protected final int getLeadOffset(char var1) {
      return this.getRawOffset(0, var1);
   }

   protected final int getCodePointOffset(int var1) {
      if (var1 < 0) {
         return -1;
      } else if (var1 < 55296) {
         return this.getRawOffset(0, (char)var1);
      } else if (var1 < 65536) {
         return this.getBMPOffset((char)var1);
      } else {
         return var1 <= 1114111 ? this.getSurrogateOffset(UTF16.getLeadSurrogate(var1), (char)(var1 & 1023)) : -1;
      }
   }

   protected void unserialize(InputStream var1) throws IOException {
      this.m_index_ = new char[this.m_dataOffset_];
      DataInputStream var2 = new DataInputStream(var1);

      for(int var3 = 0; var3 < this.m_dataOffset_; ++var3) {
         this.m_index_[var3] = var2.readChar();
      }

   }

   protected final boolean isIntTrie() {
      return (this.m_options_ & 256) != 0;
   }

   protected final boolean isCharTrie() {
      return (this.m_options_ & 256) == 0;
   }

   private final boolean checkHeader(int var1) {
      if (var1 != 1416784229) {
         return false;
      } else {
         return (this.m_options_ & 15) == 5 && (this.m_options_ >> 4 & 15) == 2;
      }
   }

   private static class DefaultGetFoldingOffset implements Trie.DataManipulate {
      private DefaultGetFoldingOffset() {
      }

      public int getFoldingOffset(int var1) {
         return var1;
      }

      // $FF: synthetic method
      DefaultGetFoldingOffset(Object var1) {
         this();
      }
   }

   public interface DataManipulate {
      int getFoldingOffset(int var1);
   }
}
