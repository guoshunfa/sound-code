package javax.imageio.plugins.jpeg;

import java.util.Locale;
import javax.imageio.ImageWriteParam;

public class JPEGImageWriteParam extends ImageWriteParam {
   private JPEGQTable[] qTables = null;
   private JPEGHuffmanTable[] DCHuffmanTables = null;
   private JPEGHuffmanTable[] ACHuffmanTables = null;
   private boolean optimizeHuffman = false;
   private String[] compressionNames = new String[]{"JPEG"};
   private float[] qualityVals = new float[]{0.0F, 0.3F, 0.75F, 1.0F};
   private String[] qualityDescs = new String[]{"Low quality", "Medium quality", "Visually lossless"};

   public JPEGImageWriteParam(Locale var1) {
      super(var1);
      this.canWriteProgressive = true;
      this.progressiveMode = 0;
      this.canWriteCompressed = true;
      this.compressionTypes = this.compressionNames;
      this.compressionType = this.compressionTypes[0];
      this.compressionQuality = 0.75F;
   }

   public void unsetCompression() {
      if (this.getCompressionMode() != 2) {
         throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
      } else {
         this.compressionQuality = 0.75F;
      }
   }

   public boolean isCompressionLossless() {
      if (this.getCompressionMode() != 2) {
         throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
      } else {
         return false;
      }
   }

   public String[] getCompressionQualityDescriptions() {
      if (this.getCompressionMode() != 2) {
         throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
      } else if (this.getCompressionTypes() != null && this.getCompressionType() == null) {
         throw new IllegalStateException("No compression type set!");
      } else {
         return (String[])((String[])this.qualityDescs.clone());
      }
   }

   public float[] getCompressionQualityValues() {
      if (this.getCompressionMode() != 2) {
         throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
      } else if (this.getCompressionTypes() != null && this.getCompressionType() == null) {
         throw new IllegalStateException("No compression type set!");
      } else {
         return (float[])((float[])this.qualityVals.clone());
      }
   }

   public boolean areTablesSet() {
      return this.qTables != null;
   }

   public void setEncodeTables(JPEGQTable[] var1, JPEGHuffmanTable[] var2, JPEGHuffmanTable[] var3) {
      if (var1 != null && var2 != null && var3 != null && var1.length <= 4 && var2.length <= 4 && var3.length <= 4 && var2.length == var3.length) {
         this.qTables = (JPEGQTable[])((JPEGQTable[])var1.clone());
         this.DCHuffmanTables = (JPEGHuffmanTable[])((JPEGHuffmanTable[])var2.clone());
         this.ACHuffmanTables = (JPEGHuffmanTable[])((JPEGHuffmanTable[])var3.clone());
      } else {
         throw new IllegalArgumentException("Invalid JPEG table arrays");
      }
   }

   public void unsetEncodeTables() {
      this.qTables = null;
      this.DCHuffmanTables = null;
      this.ACHuffmanTables = null;
   }

   public JPEGQTable[] getQTables() {
      return this.qTables != null ? (JPEGQTable[])((JPEGQTable[])this.qTables.clone()) : null;
   }

   public JPEGHuffmanTable[] getDCHuffmanTables() {
      return this.DCHuffmanTables != null ? (JPEGHuffmanTable[])((JPEGHuffmanTable[])this.DCHuffmanTables.clone()) : null;
   }

   public JPEGHuffmanTable[] getACHuffmanTables() {
      return this.ACHuffmanTables != null ? (JPEGHuffmanTable[])((JPEGHuffmanTable[])this.ACHuffmanTables.clone()) : null;
   }

   public void setOptimizeHuffmanTables(boolean var1) {
      this.optimizeHuffman = var1;
   }

   public boolean getOptimizeHuffmanTables() {
      return this.optimizeHuffman;
   }
}
