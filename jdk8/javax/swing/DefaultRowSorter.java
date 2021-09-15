package javax.swing;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public abstract class DefaultRowSorter<M, I> extends RowSorter<M> {
   private boolean sortsOnUpdates;
   private DefaultRowSorter.Row[] viewToModel;
   private int[] modelToView;
   private Comparator[] comparators;
   private boolean[] isSortable;
   private RowSorter.SortKey[] cachedSortKeys;
   private Comparator[] sortComparators;
   private RowFilter<? super M, ? super I> filter;
   private DefaultRowSorter<M, I>.FilterEntry filterEntry;
   private List<RowSorter.SortKey> sortKeys = Collections.emptyList();
   private boolean[] useToString;
   private boolean sorted;
   private int maxSortKeys = 3;
   private DefaultRowSorter.ModelWrapper<M, I> modelWrapper;
   private int modelRowCount;

   protected final void setModelWrapper(DefaultRowSorter.ModelWrapper<M, I> var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("modelWrapper most be non-null");
      } else {
         DefaultRowSorter.ModelWrapper var2 = this.modelWrapper;
         this.modelWrapper = var1;
         if (var2 != null) {
            this.modelStructureChanged();
         } else {
            this.modelRowCount = this.getModelWrapper().getRowCount();
         }

      }
   }

   protected final DefaultRowSorter.ModelWrapper<M, I> getModelWrapper() {
      return this.modelWrapper;
   }

   public final M getModel() {
      return this.getModelWrapper().getModel();
   }

   public void setSortable(int var1, boolean var2) {
      this.checkColumn(var1);
      if (this.isSortable == null) {
         this.isSortable = new boolean[this.getModelWrapper().getColumnCount()];

         for(int var3 = this.isSortable.length - 1; var3 >= 0; --var3) {
            this.isSortable[var3] = true;
         }
      }

      this.isSortable[var1] = var2;
   }

   public boolean isSortable(int var1) {
      this.checkColumn(var1);
      return this.isSortable == null ? true : this.isSortable[var1];
   }

   public void setSortKeys(List<? extends RowSorter.SortKey> var1) {
      List var2 = this.sortKeys;
      if (var1 != null && var1.size() > 0) {
         label40: {
            int var3 = this.getModelWrapper().getColumnCount();
            Iterator var4 = var1.iterator();

            RowSorter.SortKey var5;
            do {
               if (!var4.hasNext()) {
                  this.sortKeys = Collections.unmodifiableList(new ArrayList(var1));
                  break label40;
               }

               var5 = (RowSorter.SortKey)var4.next();
            } while(var5 != null && var5.getColumn() >= 0 && var5.getColumn() < var3);

            throw new IllegalArgumentException("Invalid SortKey");
         }
      } else {
         this.sortKeys = Collections.emptyList();
      }

      if (!this.sortKeys.equals(var2)) {
         this.fireSortOrderChanged();
         if (this.viewToModel == null) {
            this.sort();
         } else {
            this.sortExistingData();
         }
      }

   }

   public List<? extends RowSorter.SortKey> getSortKeys() {
      return this.sortKeys;
   }

   public void setMaxSortKeys(int var1) {
      if (var1 < 1) {
         throw new IllegalArgumentException("Invalid max");
      } else {
         this.maxSortKeys = var1;
      }
   }

   public int getMaxSortKeys() {
      return this.maxSortKeys;
   }

   public void setSortsOnUpdates(boolean var1) {
      this.sortsOnUpdates = var1;
   }

   public boolean getSortsOnUpdates() {
      return this.sortsOnUpdates;
   }

   public void setRowFilter(RowFilter<? super M, ? super I> var1) {
      this.filter = var1;
      this.sort();
   }

   public RowFilter<? super M, ? super I> getRowFilter() {
      return this.filter;
   }

   public void toggleSortOrder(int var1) {
      this.checkColumn(var1);
      if (this.isSortable(var1)) {
         Object var2 = new ArrayList(this.getSortKeys());

         int var4;
         for(var4 = ((List)var2).size() - 1; var4 >= 0 && ((RowSorter.SortKey)((List)var2).get(var4)).getColumn() != var1; --var4) {
         }

         if (var4 == -1) {
            RowSorter.SortKey var3 = new RowSorter.SortKey(var1, SortOrder.ASCENDING);
            ((List)var2).add(0, var3);
         } else if (var4 == 0) {
            ((List)var2).set(0, this.toggle((RowSorter.SortKey)((List)var2).get(0)));
         } else {
            ((List)var2).remove(var4);
            ((List)var2).add(0, new RowSorter.SortKey(var1, SortOrder.ASCENDING));
         }

         if (((List)var2).size() > this.getMaxSortKeys()) {
            var2 = ((List)var2).subList(0, this.getMaxSortKeys());
         }

         this.setSortKeys((List)var2);
      }

   }

   private RowSorter.SortKey toggle(RowSorter.SortKey var1) {
      return var1.getSortOrder() == SortOrder.ASCENDING ? new RowSorter.SortKey(var1.getColumn(), SortOrder.DESCENDING) : new RowSorter.SortKey(var1.getColumn(), SortOrder.ASCENDING);
   }

   public int convertRowIndexToView(int var1) {
      if (this.modelToView == null) {
         if (var1 >= 0 && var1 < this.getModelWrapper().getRowCount()) {
            return var1;
         } else {
            throw new IndexOutOfBoundsException("Invalid index");
         }
      } else {
         return this.modelToView[var1];
      }
   }

   public int convertRowIndexToModel(int var1) {
      if (this.viewToModel == null) {
         if (var1 >= 0 && var1 < this.getModelWrapper().getRowCount()) {
            return var1;
         } else {
            throw new IndexOutOfBoundsException("Invalid index");
         }
      } else {
         return this.viewToModel[var1].modelIndex;
      }
   }

   private boolean isUnsorted() {
      List var1 = this.getSortKeys();
      int var2 = var1.size();
      return var2 == 0 || ((RowSorter.SortKey)var1.get(0)).getSortOrder() == SortOrder.UNSORTED;
   }

   private void sortExistingData() {
      int[] var1 = this.getViewToModelAsInts(this.viewToModel);
      this.updateUseToString();
      this.cacheSortKeys(this.getSortKeys());
      if (this.isUnsorted()) {
         if (this.getRowFilter() == null) {
            this.viewToModel = null;
            this.modelToView = null;
         } else {
            int var2 = 0;

            for(int var3 = 0; var3 < this.modelToView.length; ++var3) {
               if (this.modelToView[var3] != -1) {
                  this.viewToModel[var2].modelIndex = var3;
                  this.modelToView[var3] = var2++;
               }
            }
         }
      } else {
         Arrays.sort((Object[])this.viewToModel);
         this.setModelToViewFromViewToModel(false);
      }

      this.fireRowSorterChanged(var1);
   }

   public void sort() {
      this.sorted = true;
      int[] var1 = this.getViewToModelAsInts(this.viewToModel);
      this.updateUseToString();
      if (this.isUnsorted()) {
         this.cachedSortKeys = new RowSorter.SortKey[0];
         if (this.getRowFilter() == null) {
            if (this.viewToModel == null) {
               return;
            }

            this.viewToModel = null;
            this.modelToView = null;
         } else {
            this.initializeFilteredMapping();
         }
      } else {
         this.cacheSortKeys(this.getSortKeys());
         if (this.getRowFilter() != null) {
            this.initializeFilteredMapping();
         } else {
            this.createModelToView(this.getModelWrapper().getRowCount());
            this.createViewToModel(this.getModelWrapper().getRowCount());
         }

         Arrays.sort((Object[])this.viewToModel);
         this.setModelToViewFromViewToModel(false);
      }

      this.fireRowSorterChanged(var1);
   }

   private void updateUseToString() {
      int var1 = this.getModelWrapper().getColumnCount();
      if (this.useToString == null || this.useToString.length != var1) {
         this.useToString = new boolean[var1];
      }

      --var1;

      while(var1 >= 0) {
         this.useToString[var1] = this.useToString(var1);
         --var1;
      }

   }

   private void initializeFilteredMapping() {
      int var1 = this.getModelWrapper().getRowCount();
      int var4 = 0;
      this.createModelToView(var1);

      int var2;
      for(var2 = 0; var2 < var1; ++var2) {
         if (this.include(var2)) {
            this.modelToView[var2] = var2 - var4;
         } else {
            this.modelToView[var2] = -1;
            ++var4;
         }
      }

      this.createViewToModel(var1 - var4);
      var2 = 0;

      for(int var3 = 0; var2 < var1; ++var2) {
         if (this.modelToView[var2] != -1) {
            this.viewToModel[var3++].modelIndex = var2;
         }
      }

   }

   private void createModelToView(int var1) {
      if (this.modelToView == null || this.modelToView.length != var1) {
         this.modelToView = new int[var1];
      }

   }

   private void createViewToModel(int var1) {
      int var2 = 0;
      if (this.viewToModel != null) {
         var2 = Math.min(var1, this.viewToModel.length);
         if (this.viewToModel.length != var1) {
            DefaultRowSorter.Row[] var3 = this.viewToModel;
            this.viewToModel = new DefaultRowSorter.Row[var1];
            System.arraycopy(var3, 0, this.viewToModel, 0, var2);
         }
      } else {
         this.viewToModel = new DefaultRowSorter.Row[var1];
      }

      int var4;
      for(var4 = 0; var4 < var2; this.viewToModel[var4].modelIndex = var4++) {
      }

      for(var4 = var2; var4 < var1; ++var4) {
         this.viewToModel[var4] = new DefaultRowSorter.Row(this, var4);
      }

   }

   private void cacheSortKeys(List<? extends RowSorter.SortKey> var1) {
      int var2 = var1.size();
      this.sortComparators = new Comparator[var2];

      for(int var3 = 0; var3 < var2; ++var3) {
         this.sortComparators[var3] = this.getComparator0(((RowSorter.SortKey)var1.get(var3)).getColumn());
      }

      this.cachedSortKeys = (RowSorter.SortKey[])var1.toArray(new RowSorter.SortKey[var2]);
   }

   protected boolean useToString(int var1) {
      return this.getComparator(var1) == null;
   }

   private void setModelToViewFromViewToModel(boolean var1) {
      int var2;
      if (var1) {
         for(var2 = this.modelToView.length - 1; var2 >= 0; --var2) {
            this.modelToView[var2] = -1;
         }
      }

      for(var2 = this.viewToModel.length - 1; var2 >= 0; this.modelToView[this.viewToModel[var2].modelIndex] = var2--) {
      }

   }

   private int[] getViewToModelAsInts(DefaultRowSorter.Row[] var1) {
      if (var1 == null) {
         return new int[0];
      } else {
         int[] var2 = new int[var1.length];

         for(int var3 = var1.length - 1; var3 >= 0; --var3) {
            var2[var3] = var1[var3].modelIndex;
         }

         return var2;
      }
   }

   public void setComparator(int var1, Comparator<?> var2) {
      this.checkColumn(var1);
      if (this.comparators == null) {
         this.comparators = new Comparator[this.getModelWrapper().getColumnCount()];
      }

      this.comparators[var1] = var2;
   }

   public Comparator<?> getComparator(int var1) {
      this.checkColumn(var1);
      return this.comparators != null ? this.comparators[var1] : null;
   }

   private Comparator getComparator0(int var1) {
      Comparator var2 = this.getComparator(var1);
      return (Comparator)(var2 != null ? var2 : Collator.getInstance());
   }

   private RowFilter.Entry<M, I> getFilterEntry(int var1) {
      if (this.filterEntry == null) {
         this.filterEntry = new DefaultRowSorter.FilterEntry();
      }

      this.filterEntry.modelIndex = var1;
      return this.filterEntry;
   }

   public int getViewRowCount() {
      return this.viewToModel != null ? this.viewToModel.length : this.getModelWrapper().getRowCount();
   }

   public int getModelRowCount() {
      return this.getModelWrapper().getRowCount();
   }

   private void allChanged() {
      this.modelToView = null;
      this.viewToModel = null;
      this.comparators = null;
      this.isSortable = null;
      if (this.isUnsorted()) {
         this.sort();
      } else {
         this.setSortKeys((List)null);
      }

   }

   public void modelStructureChanged() {
      this.allChanged();
      this.modelRowCount = this.getModelWrapper().getRowCount();
   }

   public void allRowsChanged() {
      this.modelRowCount = this.getModelWrapper().getRowCount();
      this.sort();
   }

   public void rowsInserted(int var1, int var2) {
      this.checkAgainstModel(var1, var2);
      int var3 = this.getModelWrapper().getRowCount();
      if (var2 >= var3) {
         throw new IndexOutOfBoundsException("Invalid range");
      } else {
         this.modelRowCount = var3;
         if (this.shouldOptimizeChange(var1, var2)) {
            this.rowsInserted0(var1, var2);
         }

      }
   }

   public void rowsDeleted(int var1, int var2) {
      this.checkAgainstModel(var1, var2);
      if (var1 < this.modelRowCount && var2 < this.modelRowCount) {
         this.modelRowCount = this.getModelWrapper().getRowCount();
         if (this.shouldOptimizeChange(var1, var2)) {
            this.rowsDeleted0(var1, var2);
         }

      } else {
         throw new IndexOutOfBoundsException("Invalid range");
      }
   }

   public void rowsUpdated(int var1, int var2) {
      this.checkAgainstModel(var1, var2);
      if (var1 < this.modelRowCount && var2 < this.modelRowCount) {
         if (this.getSortsOnUpdates()) {
            if (this.shouldOptimizeChange(var1, var2)) {
               this.rowsUpdated0(var1, var2);
            }
         } else {
            this.sorted = false;
         }

      } else {
         throw new IndexOutOfBoundsException("Invalid range");
      }
   }

   public void rowsUpdated(int var1, int var2, int var3) {
      this.checkColumn(var3);
      this.rowsUpdated(var1, var2);
   }

   private void checkAgainstModel(int var1, int var2) {
      if (var1 > var2 || var1 < 0 || var2 < 0 || var1 > this.modelRowCount) {
         throw new IndexOutOfBoundsException("Invalid range");
      }
   }

   private boolean include(int var1) {
      RowFilter var2 = this.getRowFilter();
      return var2 != null ? var2.include(this.getFilterEntry(var1)) : true;
   }

   private int compare(int var1, int var2) {
      for(int var8 = 0; var8 < this.cachedSortKeys.length; ++var8) {
         int var3 = this.cachedSortKeys[var8].getColumn();
         SortOrder var4 = this.cachedSortKeys[var8].getSortOrder();
         int var7;
         if (var4 == SortOrder.UNSORTED) {
            var7 = var1 - var2;
         } else {
            Object var5;
            Object var6;
            if (this.useToString[var3]) {
               var5 = this.getModelWrapper().getStringValueAt(var1, var3);
               var6 = this.getModelWrapper().getStringValueAt(var2, var3);
            } else {
               var5 = this.getModelWrapper().getValueAt(var1, var3);
               var6 = this.getModelWrapper().getValueAt(var2, var3);
            }

            if (var5 == null) {
               if (var6 == null) {
                  var7 = 0;
               } else {
                  var7 = -1;
               }
            } else if (var6 == null) {
               var7 = 1;
            } else {
               var7 = this.sortComparators[var8].compare(var5, var6);
            }

            if (var4 == SortOrder.DESCENDING) {
               var7 *= -1;
            }
         }

         if (var7 != 0) {
            return var7;
         }
      }

      return var1 - var2;
   }

   private boolean isTransformed() {
      return this.viewToModel != null;
   }

   private void insertInOrder(List<DefaultRowSorter.Row> var1, DefaultRowSorter.Row[] var2) {
      int var3 = 0;
      int var5 = var1.size();

      for(int var6 = 0; var6 < var5; ++var6) {
         int var4 = Arrays.binarySearch(var2, var1.get(var6));
         if (var4 < 0) {
            var4 = -1 - var4;
         }

         System.arraycopy(var2, var3, this.viewToModel, var3 + var6, var4 - var3);
         this.viewToModel[var4 + var6] = (DefaultRowSorter.Row)var1.get(var6);
         var3 = var4;
      }

      System.arraycopy(var2, var3, this.viewToModel, var3 + var5, var2.length - var3);
   }

   private boolean shouldOptimizeChange(int var1, int var2) {
      if (!this.isTransformed()) {
         return false;
      } else if (this.sorted && var2 - var1 <= this.viewToModel.length / 10) {
         return true;
      } else {
         this.sort();
         return false;
      }
   }

   private void rowsInserted0(int var1, int var2) {
      int[] var3 = this.getViewToModelAsInts(this.viewToModel);
      int var5 = var2 - var1 + 1;
      ArrayList var6 = new ArrayList(var5);

      int var4;
      for(var4 = var1; var4 <= var2; ++var4) {
         if (this.include(var4)) {
            var6.add(new DefaultRowSorter.Row(this, var4));
         }
      }

      for(var4 = this.modelToView.length - 1; var4 >= var1; --var4) {
         int var7 = this.modelToView[var4];
         if (var7 != -1) {
            DefaultRowSorter.Row var10000 = this.viewToModel[var7];
            var10000.modelIndex += var5;
         }
      }

      if (var6.size() > 0) {
         Collections.sort(var6);
         DefaultRowSorter.Row[] var8 = this.viewToModel;
         this.viewToModel = new DefaultRowSorter.Row[this.viewToModel.length + var6.size()];
         this.insertInOrder(var6, var8);
      }

      this.createModelToView(this.getModelWrapper().getRowCount());
      this.setModelToViewFromViewToModel(true);
      this.fireRowSorterChanged(var3);
   }

   private void rowsDeleted0(int var1, int var2) {
      int[] var3 = this.getViewToModelAsInts(this.viewToModel);
      int var4 = 0;

      int var5;
      int var6;
      for(var5 = var1; var5 <= var2; ++var5) {
         var6 = this.modelToView[var5];
         if (var6 != -1) {
            ++var4;
            this.viewToModel[var6] = null;
         }
      }

      int var7 = var2 - var1 + 1;

      for(var5 = this.modelToView.length - 1; var5 > var2; --var5) {
         var6 = this.modelToView[var5];
         if (var6 != -1) {
            DefaultRowSorter.Row var10000 = this.viewToModel[var6];
            var10000.modelIndex -= var7;
         }
      }

      if (var4 > 0) {
         DefaultRowSorter.Row[] var8 = new DefaultRowSorter.Row[this.viewToModel.length - var4];
         int var9 = 0;
         int var10 = 0;

         for(var5 = 0; var5 < this.viewToModel.length; ++var5) {
            if (this.viewToModel[var5] == null) {
               System.arraycopy(this.viewToModel, var10, var8, var9, var5 - var10);
               var9 += var5 - var10;
               var10 = var5 + 1;
            }
         }

         System.arraycopy(this.viewToModel, var10, var8, var9, this.viewToModel.length - var10);
         this.viewToModel = var8;
      }

      this.createModelToView(this.getModelWrapper().getRowCount());
      this.setModelToViewFromViewToModel(true);
      this.fireRowSorterChanged(var3);
   }

   private void rowsUpdated0(int var1, int var2) {
      int[] var3 = this.getViewToModelAsInts(this.viewToModel);
      int var6 = var2 - var1 + 1;
      int var4;
      int var5;
      int var7;
      if (this.getRowFilter() == null) {
         DefaultRowSorter.Row[] var10 = new DefaultRowSorter.Row[var6];
         var5 = 0;

         for(var4 = var1; var4 <= var2; ++var5) {
            var10[var5] = this.viewToModel[this.modelToView[var4]];
            ++var4;
         }

         Arrays.sort((Object[])var10);
         DefaultRowSorter.Row[] var11 = new DefaultRowSorter.Row[this.viewToModel.length - var6];
         var4 = 0;

         for(var5 = 0; var4 < this.viewToModel.length; ++var4) {
            var7 = this.viewToModel[var4].modelIndex;
            if (var7 < var1 || var7 > var2) {
               var11[var5++] = this.viewToModel[var4];
            }
         }

         this.insertInOrder(Arrays.asList(var10), var11);
         this.setModelToViewFromViewToModel(false);
      } else {
         ArrayList var15 = new ArrayList(var6);
         int var16 = 0;
         int var12 = 0;
         int var13 = 0;

         for(var4 = var1; var4 <= var2; ++var4) {
            if (this.modelToView[var4] == -1) {
               if (this.include(var4)) {
                  var15.add(new DefaultRowSorter.Row(this, var4));
                  ++var16;
               }
            } else {
               if (!this.include(var4)) {
                  ++var12;
               } else {
                  var15.add(this.viewToModel[this.modelToView[var4]]);
               }

               this.modelToView[var4] = -2;
               ++var13;
            }
         }

         Collections.sort(var15);
         DefaultRowSorter.Row[] var14 = new DefaultRowSorter.Row[this.viewToModel.length - var13];
         var4 = 0;

         for(var5 = 0; var4 < this.viewToModel.length; ++var4) {
            var7 = this.viewToModel[var4].modelIndex;
            if (this.modelToView[var7] != -2) {
               var14[var5++] = this.viewToModel[var4];
            }
         }

         if (var16 != var12) {
            this.viewToModel = new DefaultRowSorter.Row[this.viewToModel.length + var16 - var12];
         }

         this.insertInOrder(var15, var14);
         this.setModelToViewFromViewToModel(true);
      }

      this.fireRowSorterChanged(var3);
   }

   private void checkColumn(int var1) {
      if (var1 < 0 || var1 >= this.getModelWrapper().getColumnCount()) {
         throw new IndexOutOfBoundsException("column beyond range of TableModel");
      }
   }

   private static class Row implements Comparable<DefaultRowSorter.Row> {
      private DefaultRowSorter sorter;
      int modelIndex;

      public Row(DefaultRowSorter var1, int var2) {
         this.sorter = var1;
         this.modelIndex = var2;
      }

      public int compareTo(DefaultRowSorter.Row var1) {
         return this.sorter.compare(this.modelIndex, var1.modelIndex);
      }
   }

   private class FilterEntry extends RowFilter.Entry<M, I> {
      int modelIndex;

      private FilterEntry() {
      }

      public M getModel() {
         return DefaultRowSorter.this.getModelWrapper().getModel();
      }

      public int getValueCount() {
         return DefaultRowSorter.this.getModelWrapper().getColumnCount();
      }

      public Object getValue(int var1) {
         return DefaultRowSorter.this.getModelWrapper().getValueAt(this.modelIndex, var1);
      }

      public String getStringValue(int var1) {
         return DefaultRowSorter.this.getModelWrapper().getStringValueAt(this.modelIndex, var1);
      }

      public I getIdentifier() {
         return DefaultRowSorter.this.getModelWrapper().getIdentifier(this.modelIndex);
      }

      // $FF: synthetic method
      FilterEntry(Object var2) {
         this();
      }
   }

   protected abstract static class ModelWrapper<M, I> {
      public abstract M getModel();

      public abstract int getColumnCount();

      public abstract int getRowCount();

      public abstract Object getValueAt(int var1, int var2);

      public String getStringValueAt(int var1, int var2) {
         Object var3 = this.getValueAt(var1, var2);
         if (var3 == null) {
            return "";
         } else {
            String var4 = var3.toString();
            return var4 == null ? "" : var4;
         }
      }

      public abstract I getIdentifier(int var1);
   }
}
