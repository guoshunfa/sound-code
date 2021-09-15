package sun.text.normalizer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class IntTrie extends Trie {
   private int m_initialValue_;
   private int[] m_data_;

   public IntTrie(InputStream var1, Trie.DataManipulate var2) throws IOException {
      super(var1, var2);
      if (!this.isIntTrie()) {
         throw new IllegalArgumentException("Data given does not belong to a int trie.");
      }
   }

   public final int getCodePointValue(int var1) {
      int var2 = this.getCodePointOffset(var1);
      return var2 >= 0 ? this.m_data_[var2] : this.m_initialValue_;
   }

   public final int getLeadValue(char var1) {
      return this.m_data_[this.getLeadOffset(var1)];
   }

   public final int getTrailValue(int var1, char var2) {
      if (this.m_dataManipulate_ == null) {
         throw new NullPointerException("The field DataManipulate in this Trie is null");
      } else {
         int var3 = this.m_dataManipulate_.getFoldingOffset(var1);
         return var3 > 0 ? this.m_data_[this.getRawOffset(var3, (char)(var2 & 1023))] : this.m_initialValue_;
      }
   }

   protected final void unserialize(InputStream var1) throws IOException {
      super.unserialize(var1);
      this.m_data_ = new int[this.m_dataLength_];
      DataInputStream var2 = new DataInputStream(var1);

      for(int var3 = 0; var3 < this.m_dataLength_; ++var3) {
         this.m_data_[var3] = var2.readInt();
      }

      this.m_initialValue_ = this.m_data_[0];
   }

   protected final int getSurrogateOffset(char var1, char var2) {
      if (this.m_dataManipulate_ == null) {
         throw new NullPointerException("The field DataManipulate in this Trie is null");
      } else {
         int var3 = this.m_dataManipulate_.getFoldingOffset(this.getLeadValue(var1));
         return var3 > 0 ? this.getRawOffset(var3, (char)(var2 & 1023)) : -1;
      }
   }

   protected final int getValue(int var1) {
      return this.m_data_[var1];
   }

   protected final int getInitialValue() {
      return this.m_initialValue_;
   }

   IntTrie(char[] var1, int[] var2, int var3, int var4, Trie.DataManipulate var5) {
      super(var1, var4, var5);
      this.m_data_ = var2;
      this.m_dataLength_ = this.m_data_.length;
      this.m_initialValue_ = var3;
   }
}
