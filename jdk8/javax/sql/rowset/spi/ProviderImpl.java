package javax.sql.rowset.spi;

import javax.sql.RowSetReader;
import javax.sql.RowSetWriter;

class ProviderImpl extends SyncProvider {
   private String className = null;
   private String vendorName = null;
   private String ver = null;
   private int index;

   public void setClassname(String var1) {
      this.className = var1;
   }

   public String getClassname() {
      return this.className;
   }

   public void setVendor(String var1) {
      this.vendorName = var1;
   }

   public String getVendor() {
      return this.vendorName;
   }

   public void setVersion(String var1) {
      this.ver = var1;
   }

   public String getVersion() {
      return this.ver;
   }

   public void setIndex(int var1) {
      this.index = var1;
   }

   public int getIndex() {
      return this.index;
   }

   public int getDataSourceLock() throws SyncProviderException {
      boolean var1 = false;

      try {
         int var4 = SyncFactory.getInstance(this.className).getDataSourceLock();
         return var4;
      } catch (SyncFactoryException var3) {
         throw new SyncProviderException(var3.getMessage());
      }
   }

   public int getProviderGrade() {
      int var1 = 0;

      try {
         var1 = SyncFactory.getInstance(this.className).getProviderGrade();
      } catch (SyncFactoryException var3) {
      }

      return var1;
   }

   public String getProviderID() {
      return this.className;
   }

   public RowSetReader getRowSetReader() {
      RowSetReader var1 = null;

      try {
         var1 = SyncFactory.getInstance(this.className).getRowSetReader();
      } catch (SyncFactoryException var3) {
      }

      return var1;
   }

   public RowSetWriter getRowSetWriter() {
      RowSetWriter var1 = null;

      try {
         var1 = SyncFactory.getInstance(this.className).getRowSetWriter();
      } catch (SyncFactoryException var3) {
      }

      return var1;
   }

   public void setDataSourceLock(int var1) throws SyncProviderException {
      try {
         SyncFactory.getInstance(this.className).setDataSourceLock(var1);
      } catch (SyncFactoryException var3) {
         throw new SyncProviderException(var3.getMessage());
      }
   }

   public int supportsUpdatableView() {
      int var1 = 0;

      try {
         var1 = SyncFactory.getInstance(this.className).supportsUpdatableView();
      } catch (SyncFactoryException var3) {
      }

      return var1;
   }
}
