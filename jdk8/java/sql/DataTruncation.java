package java.sql;

public class DataTruncation extends SQLWarning {
   private int index;
   private boolean parameter;
   private boolean read;
   private int dataSize;
   private int transferSize;
   private static final long serialVersionUID = 6464298989504059473L;

   public DataTruncation(int var1, boolean var2, boolean var3, int var4, int var5) {
      super("Data truncation", var3 ? "01004" : "22001");
      this.index = var1;
      this.parameter = var2;
      this.read = var3;
      this.dataSize = var4;
      this.transferSize = var5;
   }

   public DataTruncation(int var1, boolean var2, boolean var3, int var4, int var5, Throwable var6) {
      super("Data truncation", var3 ? "01004" : "22001", var6);
      this.index = var1;
      this.parameter = var2;
      this.read = var3;
      this.dataSize = var4;
      this.transferSize = var5;
   }

   public int getIndex() {
      return this.index;
   }

   public boolean getParameter() {
      return this.parameter;
   }

   public boolean getRead() {
      return this.read;
   }

   public int getDataSize() {
      return this.dataSize;
   }

   public int getTransferSize() {
      return this.transferSize;
   }
}
