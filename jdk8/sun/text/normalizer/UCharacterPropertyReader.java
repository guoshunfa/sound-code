package sun.text.normalizer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

final class UCharacterPropertyReader implements ICUBinary.Authenticate {
   private static final int INDEX_SIZE_ = 16;
   private DataInputStream m_dataInputStream_;
   private int m_propertyOffset_;
   private int m_exceptionOffset_;
   private int m_caseOffset_;
   private int m_additionalOffset_;
   private int m_additionalVectorsOffset_;
   private int m_additionalColumnsCount_;
   private int m_reservedOffset_;
   private byte[] m_unicodeVersion_;
   private static final byte[] DATA_FORMAT_ID_ = new byte[]{85, 80, 114, 111};
   private static final byte[] DATA_FORMAT_VERSION_ = new byte[]{5, 0, 5, 2};

   public boolean isDataVersionAcceptable(byte[] var1) {
      return var1[0] == DATA_FORMAT_VERSION_[0] && var1[2] == DATA_FORMAT_VERSION_[2] && var1[3] == DATA_FORMAT_VERSION_[3];
   }

   protected UCharacterPropertyReader(InputStream var1) throws IOException {
      this.m_unicodeVersion_ = ICUBinary.readHeader(var1, DATA_FORMAT_ID_, this);
      this.m_dataInputStream_ = new DataInputStream(var1);
   }

   protected void read(UCharacterProperty var1) throws IOException {
      byte var2 = 16;
      this.m_propertyOffset_ = this.m_dataInputStream_.readInt();
      int var5 = var2 - 1;
      this.m_exceptionOffset_ = this.m_dataInputStream_.readInt();
      --var5;
      this.m_caseOffset_ = this.m_dataInputStream_.readInt();
      --var5;
      this.m_additionalOffset_ = this.m_dataInputStream_.readInt();
      --var5;
      this.m_additionalVectorsOffset_ = this.m_dataInputStream_.readInt();
      --var5;
      this.m_additionalColumnsCount_ = this.m_dataInputStream_.readInt();
      --var5;
      this.m_reservedOffset_ = this.m_dataInputStream_.readInt();
      --var5;
      this.m_dataInputStream_.skipBytes(12);
      var5 -= 3;
      var1.m_maxBlockScriptValue_ = this.m_dataInputStream_.readInt();
      --var5;
      var1.m_maxJTGValue_ = this.m_dataInputStream_.readInt();
      --var5;
      this.m_dataInputStream_.skipBytes(var5 << 2);
      var1.m_trie_ = new CharTrie(this.m_dataInputStream_, (Trie.DataManipulate)null);
      int var3 = this.m_exceptionOffset_ - this.m_propertyOffset_;
      this.m_dataInputStream_.skipBytes(var3 * 4);
      var3 = this.m_caseOffset_ - this.m_exceptionOffset_;
      this.m_dataInputStream_.skipBytes(var3 * 4);
      var3 = this.m_additionalOffset_ - this.m_caseOffset_ << 1;
      this.m_dataInputStream_.skipBytes(var3 * 2);
      if (this.m_additionalColumnsCount_ > 0) {
         var1.m_additionalTrie_ = new CharTrie(this.m_dataInputStream_, (Trie.DataManipulate)null);
         var3 = this.m_reservedOffset_ - this.m_additionalVectorsOffset_;
         var1.m_additionalVectors_ = new int[var3];

         for(int var4 = 0; var4 < var3; ++var4) {
            var1.m_additionalVectors_[var4] = this.m_dataInputStream_.readInt();
         }
      }

      this.m_dataInputStream_.close();
      var1.m_additionalColumnsCount_ = this.m_additionalColumnsCount_;
      var1.m_unicodeVersion_ = VersionInfo.getInstance(this.m_unicodeVersion_[0], this.m_unicodeVersion_[1], this.m_unicodeVersion_[2], this.m_unicodeVersion_[3]);
   }
}
