package sun.text.normalizer;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class UBiDiProps {
   private static UBiDiProps gBdp = null;
   private static UBiDiProps gBdpDummy = null;
   private int[] indexes;
   private int[] mirrors;
   private byte[] jgArray;
   private CharTrie trie;
   private static final String DATA_FILE_NAME = "/sun/text/resources/ubidi.icu";
   private static final byte[] FMT = new byte[]{66, 105, 68, 105};
   private static final int IX_INDEX_TOP = 0;
   private static final int IX_MIRROR_LENGTH = 3;
   private static final int IX_JG_START = 4;
   private static final int IX_JG_LIMIT = 5;
   private static final int IX_TOP = 16;
   private static final int CLASS_MASK = 31;

   public UBiDiProps() throws IOException {
      InputStream var1 = ICUData.getStream("/sun/text/resources/ubidi.icu");
      BufferedInputStream var2 = new BufferedInputStream(var1, 4096);
      this.readData(var2);
      var2.close();
      var1.close();
   }

   private void readData(InputStream var1) throws IOException {
      DataInputStream var2 = new DataInputStream(var1);
      ICUBinary.readHeader(var2, FMT, new UBiDiProps.IsAcceptable());
      int var4 = var2.readInt();
      if (var4 < 0) {
         throw new IOException("indexes[0] too small in /sun/text/resources/ubidi.icu");
      } else {
         this.indexes = new int[var4];
         this.indexes[0] = var4;

         int var3;
         for(var3 = 1; var3 < var4; ++var3) {
            this.indexes[var3] = var2.readInt();
         }

         this.trie = new CharTrie(var2, (Trie.DataManipulate)null);
         var4 = this.indexes[3];
         if (var4 > 0) {
            this.mirrors = new int[var4];

            for(var3 = 0; var3 < var4; ++var3) {
               this.mirrors[var3] = var2.readInt();
            }
         }

         var4 = this.indexes[5] - this.indexes[4];
         this.jgArray = new byte[var4];

         for(var3 = 0; var3 < var4; ++var3) {
            this.jgArray[var3] = var2.readByte();
         }

      }
   }

   public static final synchronized UBiDiProps getSingleton() throws IOException {
      if (gBdp == null) {
         gBdp = new UBiDiProps();
      }

      return gBdp;
   }

   private UBiDiProps(boolean var1) {
      this.indexes = new int[16];
      this.indexes[0] = 16;
      this.trie = new CharTrie(0, 0, (Trie.DataManipulate)null);
   }

   public static final synchronized UBiDiProps getDummy() {
      if (gBdpDummy == null) {
         gBdpDummy = new UBiDiProps(true);
      }

      return gBdpDummy;
   }

   public final int getClass(int var1) {
      return getClassFromProps(this.trie.getCodePointValue(var1));
   }

   private static final int getClassFromProps(int var0) {
      return var0 & 31;
   }

   private final class IsAcceptable implements ICUBinary.Authenticate {
      private IsAcceptable() {
      }

      public boolean isDataVersionAcceptable(byte[] var1) {
         return var1[0] == 1 && var1[2] == 5 && var1[3] == 2;
      }

      // $FF: synthetic method
      IsAcceptable(Object var2) {
         this();
      }
   }
}
