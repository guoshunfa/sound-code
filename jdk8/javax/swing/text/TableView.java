package javax.swing.text;

import java.awt.Container;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.BitSet;
import java.util.Vector;
import javax.swing.SizeRequirements;
import javax.swing.event.DocumentEvent;
import javax.swing.text.html.HTML;

public abstract class TableView extends BoxView {
   int[] columnSpans;
   int[] columnOffsets;
   SizeRequirements[] columnRequirements;
   Vector<TableView.TableRow> rows = new Vector();
   boolean gridValid = false;
   private static final BitSet EMPTY = new BitSet();

   public TableView(Element var1) {
      super(var1, 1);
   }

   protected TableView.TableRow createTableRow(Element var1) {
      return new TableView.TableRow(var1);
   }

   /** @deprecated */
   @Deprecated
   protected TableView.TableCell createTableCell(Element var1) {
      return new TableView.TableCell(var1);
   }

   int getColumnCount() {
      return this.columnSpans.length;
   }

   int getColumnSpan(int var1) {
      return this.columnSpans[var1];
   }

   int getRowCount() {
      return this.rows.size();
   }

   int getRowSpan(int var1) {
      TableView.TableRow var2 = this.getRow(var1);
      return var2 != null ? (int)var2.getPreferredSpan(1) : 0;
   }

   TableView.TableRow getRow(int var1) {
      return var1 < this.rows.size() ? (TableView.TableRow)this.rows.elementAt(var1) : null;
   }

   int getColumnsOccupied(View var1) {
      AttributeSet var2 = var1.getElement().getAttributes();
      String var3 = (String)var2.getAttribute(HTML.Attribute.COLSPAN);
      if (var3 != null) {
         try {
            return Integer.parseInt(var3);
         } catch (NumberFormatException var5) {
         }
      }

      return 1;
   }

   int getRowsOccupied(View var1) {
      AttributeSet var2 = var1.getElement().getAttributes();
      String var3 = (String)var2.getAttribute(HTML.Attribute.ROWSPAN);
      if (var3 != null) {
         try {
            return Integer.parseInt(var3);
         } catch (NumberFormatException var5) {
         }
      }

      return 1;
   }

   void invalidateGrid() {
      this.gridValid = false;
   }

   protected void forwardUpdate(DocumentEvent.ElementChange var1, DocumentEvent var2, Shape var3, ViewFactory var4) {
      super.forwardUpdate(var1, var2, var3, var4);
      if (var3 != null) {
         Container var5 = this.getContainer();
         if (var5 != null) {
            Rectangle var6 = var3 instanceof Rectangle ? (Rectangle)var3 : var3.getBounds();
            var5.repaint(var6.x, var6.y, var6.width, var6.height);
         }
      }

   }

   public void replace(int var1, int var2, View[] var3) {
      super.replace(var1, var2, var3);
      this.invalidateGrid();
   }

   void updateGrid() {
      if (!this.gridValid) {
         this.rows.removeAllElements();
         int var1 = this.getViewCount();

         int var2;
         for(var2 = 0; var2 < var1; ++var2) {
            View var3 = this.getView(var2);
            if (var3 instanceof TableView.TableRow) {
               this.rows.addElement((TableView.TableRow)var3);
               TableView.TableRow var4 = (TableView.TableRow)var3;
               var4.clearFilledColumns();
               var4.setRow(var2);
            }
         }

         var2 = 0;
         int var15 = this.rows.size();

         int var16;
         for(var16 = 0; var16 < var15; ++var16) {
            TableView.TableRow var5 = this.getRow(var16);
            int var6 = 0;

            for(int var7 = 0; var7 < var5.getViewCount(); ++var6) {
               View var8;
               for(var8 = var5.getView(var7); var5.isFilled(var6); ++var6) {
               }

               int var9 = this.getRowsOccupied(var8);
               int var10 = this.getColumnsOccupied(var8);
               if (var10 > 1 || var9 > 1) {
                  int var11 = var16 + var9;
                  int var12 = var6 + var10;
                  int var13 = var16;

                  while(true) {
                     if (var13 >= var11) {
                        if (var10 > 1) {
                           var6 += var10 - 1;
                        }
                        break;
                     }

                     for(int var14 = var6; var14 < var12; ++var14) {
                        if (var13 != var16 || var14 != var6) {
                           this.addFill(var13, var14);
                        }
                     }

                     ++var13;
                  }
               }

               ++var7;
            }

            var2 = Math.max(var2, var6);
         }

         this.columnSpans = new int[var2];
         this.columnOffsets = new int[var2];
         this.columnRequirements = new SizeRequirements[var2];

         for(var16 = 0; var16 < var2; ++var16) {
            this.columnRequirements[var16] = new SizeRequirements();
         }

         this.gridValid = true;
      }

   }

   void addFill(int var1, int var2) {
      TableView.TableRow var3 = this.getRow(var1);
      if (var3 != null) {
         var3.fillColumn(var2);
      }

   }

   protected void layoutColumns(int var1, int[] var2, int[] var3, SizeRequirements[] var4) {
      SizeRequirements.calculateTiledPositions(var1, (SizeRequirements)null, var4, var2, var3);
   }

   protected void layoutMinorAxis(int var1, int var2, int[] var3, int[] var4) {
      this.updateGrid();
      int var5 = this.getRowCount();

      for(int var6 = 0; var6 < var5; ++var6) {
         TableView.TableRow var7 = this.getRow(var6);
         var7.layoutChanged(var2);
      }

      this.layoutColumns(var1, this.columnOffsets, this.columnSpans, this.columnRequirements);
      super.layoutMinorAxis(var1, var2, var3, var4);
   }

   protected SizeRequirements calculateMinorAxisRequirements(int var1, SizeRequirements var2) {
      this.updateGrid();
      this.calculateColumnRequirements(var1);
      if (var2 == null) {
         var2 = new SizeRequirements();
      }

      long var3 = 0L;
      long var5 = 0L;
      long var7 = 0L;
      SizeRequirements[] var9 = this.columnRequirements;
      int var10 = var9.length;

      for(int var11 = 0; var11 < var10; ++var11) {
         SizeRequirements var12 = var9[var11];
         var3 += (long)var12.minimum;
         var5 += (long)var12.preferred;
         var7 += (long)var12.maximum;
      }

      var2.minimum = (int)var3;
      var2.preferred = (int)var5;
      var2.maximum = (int)var7;
      var2.alignment = 0.0F;
      return var2;
   }

   void calculateColumnRequirements(int var1) {
      boolean var2 = false;
      int var3 = this.getRowCount();

      int var4;
      TableView.TableRow var5;
      int var6;
      int var7;
      int var8;
      View var9;
      for(var4 = 0; var4 < var3; ++var4) {
         var5 = this.getRow(var4);
         var6 = 0;
         var7 = var5.getViewCount();

         for(var8 = 0; var8 < var7; ++var6) {
            for(var9 = var5.getView(var8); var5.isFilled(var6); ++var6) {
            }

            this.getRowsOccupied(var9);
            int var11 = this.getColumnsOccupied(var9);
            if (var11 == 1) {
               this.checkSingleColumnCell(var1, var6, var9);
            } else {
               var2 = true;
               var6 += var11 - 1;
            }

            ++var8;
         }
      }

      if (var2) {
         for(var4 = 0; var4 < var3; ++var4) {
            var5 = this.getRow(var4);
            var6 = 0;
            var7 = var5.getViewCount();

            for(var8 = 0; var8 < var7; ++var6) {
               for(var9 = var5.getView(var8); var5.isFilled(var6); ++var6) {
               }

               int var10 = this.getColumnsOccupied(var9);
               if (var10 > 1) {
                  this.checkMultiColumnCell(var1, var6, var10, var9);
                  var6 += var10 - 1;
               }

               ++var8;
            }
         }
      }

   }

   void checkSingleColumnCell(int var1, int var2, View var3) {
      SizeRequirements var4 = this.columnRequirements[var2];
      var4.minimum = Math.max((int)var3.getMinimumSpan(var1), var4.minimum);
      var4.preferred = Math.max((int)var3.getPreferredSpan(var1), var4.preferred);
      var4.maximum = Math.max((int)var3.getMaximumSpan(var1), var4.maximum);
   }

   void checkMultiColumnCell(int var1, int var2, int var3, View var4) {
      long var5 = 0L;
      long var7 = 0L;
      long var9 = 0L;

      int var11;
      for(var11 = 0; var11 < var3; ++var11) {
         SizeRequirements var12 = this.columnRequirements[var2 + var11];
         var5 += (long)var12.minimum;
         var7 += (long)var12.preferred;
         var9 += (long)var12.maximum;
      }

      var11 = (int)var4.getMinimumSpan(var1);
      int[] var22;
      if ((long)var11 > var5) {
         SizeRequirements[] var18 = new SizeRequirements[var3];

         for(int var13 = 0; var13 < var3; ++var13) {
            SizeRequirements var14 = var18[var13] = this.columnRequirements[var2 + var13];
            var14.maximum = Math.max(var14.maximum, (int)var4.getMaximumSpan(var1));
         }

         int[] var20 = new int[var3];
         var22 = new int[var3];
         SizeRequirements.calculateTiledPositions(var11, (SizeRequirements)null, var18, var22, var20);

         for(int var15 = 0; var15 < var3; ++var15) {
            SizeRequirements var16 = var18[var15];
            var16.minimum = Math.max(var20[var15], var16.minimum);
            var16.preferred = Math.max(var16.minimum, var16.preferred);
            var16.maximum = Math.max(var16.preferred, var16.maximum);
         }
      }

      int var19 = (int)var4.getPreferredSpan(var1);
      if ((long)var19 > var7) {
         SizeRequirements[] var21 = new SizeRequirements[var3];

         for(int var23 = 0; var23 < var3; ++var23) {
            SizeRequirements var24 = var21[var23] = this.columnRequirements[var2 + var23];
         }

         var22 = new int[var3];
         int[] var25 = new int[var3];
         SizeRequirements.calculateTiledPositions(var19, (SizeRequirements)null, var21, var25, var22);

         for(int var26 = 0; var26 < var3; ++var26) {
            SizeRequirements var17 = var21[var26];
            var17.preferred = Math.max(var22[var26], var17.preferred);
            var17.maximum = Math.max(var17.preferred, var17.maximum);
         }
      }

   }

   protected View getViewAtPosition(int var1, Rectangle var2) {
      int var3 = this.getViewCount();

      for(int var4 = 0; var4 < var3; ++var4) {
         View var5 = this.getView(var4);
         int var6 = var5.getStartOffset();
         int var7 = var5.getEndOffset();
         if (var1 >= var6 && var1 < var7) {
            if (var2 != null) {
               this.childAllocation(var4, var2);
            }

            return var5;
         }
      }

      if (var1 == this.getEndOffset()) {
         View var8 = this.getView(var3 - 1);
         if (var2 != null) {
            this.childAllocation(var3 - 1, var2);
         }

         return var8;
      } else {
         return null;
      }
   }

   interface GridCell {
      void setGridLocation(int var1, int var2);

      int getGridRow();

      int getGridColumn();

      int getColumnCount();

      int getRowCount();
   }

   /** @deprecated */
   @Deprecated
   public class TableCell extends BoxView implements TableView.GridCell {
      int row;
      int col;

      public TableCell(Element var2) {
         super(var2, 1);
      }

      public int getColumnCount() {
         return 1;
      }

      public int getRowCount() {
         return 1;
      }

      public void setGridLocation(int var1, int var2) {
         this.row = var1;
         this.col = var2;
      }

      public int getGridRow() {
         return this.row;
      }

      public int getGridColumn() {
         return this.col;
      }
   }

   public class TableRow extends BoxView {
      BitSet fillColumns = new BitSet();
      int row;

      public TableRow(Element var2) {
         super(var2, 0);
      }

      void clearFilledColumns() {
         this.fillColumns.and(TableView.EMPTY);
      }

      void fillColumn(int var1) {
         this.fillColumns.set(var1);
      }

      boolean isFilled(int var1) {
         return this.fillColumns.get(var1);
      }

      int getRow() {
         return this.row;
      }

      void setRow(int var1) {
         this.row = var1;
      }

      int getColumnCount() {
         int var1 = 0;
         int var2 = this.fillColumns.size();

         for(int var3 = 0; var3 < var2; ++var3) {
            if (this.fillColumns.get(var3)) {
               ++var1;
            }
         }

         return this.getViewCount() + var1;
      }

      public void replace(int var1, int var2, View[] var3) {
         super.replace(var1, var2, var3);
         TableView.this.invalidateGrid();
      }

      protected void layoutMajorAxis(int var1, int var2, int[] var3, int[] var4) {
         int var5 = 0;
         int var6 = this.getViewCount();

         for(int var7 = 0; var7 < var6; ++var5) {
            View var8;
            for(var8 = this.getView(var7); this.isFilled(var5); ++var5) {
            }

            int var9 = TableView.this.getColumnsOccupied(var8);
            var4[var7] = TableView.this.columnSpans[var5];
            var3[var7] = TableView.this.columnOffsets[var5];
            if (var9 > 1) {
               int var10 = TableView.this.columnSpans.length;

               for(int var11 = 1; var11 < var9; ++var11) {
                  if (var5 + var11 < var10) {
                     var4[var7] += TableView.this.columnSpans[var5 + var11];
                  }
               }

               var5 += var9 - 1;
            }

            ++var7;
         }

      }

      protected void layoutMinorAxis(int var1, int var2, int[] var3, int[] var4) {
         super.layoutMinorAxis(var1, var2, var3, var4);
         int var5 = 0;
         int var6 = this.getViewCount();

         for(int var7 = 0; var7 < var6; ++var5) {
            View var8;
            for(var8 = this.getView(var7); this.isFilled(var5); ++var5) {
            }

            int var9 = TableView.this.getColumnsOccupied(var8);
            int var10 = TableView.this.getRowsOccupied(var8);
            if (var10 > 1) {
               for(int var11 = 1; var11 < var10; ++var11) {
                  int var12 = this.getRow() + var11;
                  if (var12 < TableView.this.getViewCount()) {
                     int var13 = TableView.this.getSpan(1, this.getRow() + var11);
                     var4[var7] += var13;
                  }
               }
            }

            if (var9 > 1) {
               var5 += var9 - 1;
            }

            ++var7;
         }

      }

      public int getResizeWeight(int var1) {
         return 1;
      }

      protected View getViewAtPosition(int var1, Rectangle var2) {
         int var3 = this.getViewCount();

         for(int var4 = 0; var4 < var3; ++var4) {
            View var5 = this.getView(var4);
            int var6 = var5.getStartOffset();
            int var7 = var5.getEndOffset();
            if (var1 >= var6 && var1 < var7) {
               if (var2 != null) {
                  this.childAllocation(var4, var2);
               }

               return var5;
            }
         }

         if (var1 == this.getEndOffset()) {
            View var8 = this.getView(var3 - 1);
            if (var2 != null) {
               this.childAllocation(var3 - 1, var2);
            }

            return var8;
         } else {
            return null;
         }
      }
   }
}
