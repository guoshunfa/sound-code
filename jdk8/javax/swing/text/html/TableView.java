package javax.swing.text.html;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Vector;
import javax.swing.SizeRequirements;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BoxView;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

class TableView extends BoxView implements ViewFactory {
   private AttributeSet attr;
   private StyleSheet.BoxPainter painter;
   private int cellSpacing;
   private int borderWidth;
   private int captionIndex = -1;
   private boolean relativeCells;
   private boolean multiRowCells;
   int[] columnSpans;
   int[] columnOffsets;
   SizeRequirements totalColumnRequirements = new SizeRequirements();
   SizeRequirements[] columnRequirements;
   TableView.RowIterator rowIterator = new TableView.RowIterator();
   TableView.ColumnIterator colIterator = new TableView.ColumnIterator();
   Vector<TableView.RowView> rows = new Vector();
   boolean skipComments = false;
   boolean gridValid = false;
   private static final BitSet EMPTY = new BitSet();

   public TableView(Element var1) {
      super(var1, 1);
   }

   protected TableView.RowView createTableRow(Element var1) {
      Object var2 = var1.getAttributes().getAttribute(StyleConstants.NameAttribute);
      return var2 == HTML.Tag.TR ? new TableView.RowView(var1) : null;
   }

   public int getColumnCount() {
      return this.columnSpans.length;
   }

   public int getColumnSpan(int var1) {
      return var1 < this.columnSpans.length ? this.columnSpans[var1] : 0;
   }

   public int getRowCount() {
      return this.rows.size();
   }

   public int getMultiRowSpan(int var1, int var2) {
      TableView.RowView var3 = this.getRow(var1);
      TableView.RowView var4 = this.getRow(var2);
      if (var3 != null && var4 != null) {
         int var5 = var3.viewIndex;
         int var6 = var4.viewIndex;
         int var7 = this.getOffset(1, var6) - this.getOffset(1, var5) + this.getSpan(1, var6);
         return var7;
      } else {
         return 0;
      }
   }

   public int getRowSpan(int var1) {
      TableView.RowView var2 = this.getRow(var1);
      return var2 != null ? this.getSpan(1, var2.viewIndex) : 0;
   }

   TableView.RowView getRow(int var1) {
      return var1 < this.rows.size() ? (TableView.RowView)this.rows.elementAt(var1) : null;
   }

   protected View getViewAtPoint(int var1, int var2, Rectangle var3) {
      int var4 = this.getViewCount();
      Rectangle var6 = new Rectangle();

      for(int var7 = 0; var7 < var4; ++var7) {
         var6.setBounds(var3);
         this.childAllocation(var7, var6);
         View var5 = this.getView(var7);
         if (var5 instanceof TableView.RowView) {
            var5 = ((TableView.RowView)var5).findViewAtPoint(var1, var2, var6);
            if (var5 != null) {
               var3.setBounds(var6);
               return var5;
            }
         }
      }

      return super.getViewAtPoint(var1, var2, var3);
   }

   protected int getColumnsOccupied(View var1) {
      AttributeSet var2 = var1.getElement().getAttributes();
      if (var2.isDefined(HTML.Attribute.COLSPAN)) {
         String var3 = (String)var2.getAttribute(HTML.Attribute.COLSPAN);
         if (var3 != null) {
            try {
               return Integer.parseInt(var3);
            } catch (NumberFormatException var5) {
            }
         }
      }

      return 1;
   }

   protected int getRowsOccupied(View var1) {
      AttributeSet var2 = var1.getElement().getAttributes();
      if (var2.isDefined(HTML.Attribute.ROWSPAN)) {
         String var3 = (String)var2.getAttribute(HTML.Attribute.ROWSPAN);
         if (var3 != null) {
            try {
               return Integer.parseInt(var3);
            } catch (NumberFormatException var5) {
            }
         }
      }

      return 1;
   }

   protected void invalidateGrid() {
      this.gridValid = false;
   }

   protected StyleSheet getStyleSheet() {
      HTMLDocument var1 = (HTMLDocument)this.getDocument();
      return var1.getStyleSheet();
   }

   void updateInsets() {
      short var1 = (short)((int)this.painter.getInset(1, this));
      short var2 = (short)((int)this.painter.getInset(3, this));
      if (this.captionIndex != -1) {
         View var3 = this.getView(this.captionIndex);
         short var4 = (short)((int)var3.getPreferredSpan(1));
         AttributeSet var5 = var3.getAttributes();
         Object var6 = var5.getAttribute(CSS.Attribute.CAPTION_SIDE);
         if (var6 != null && var6.equals("bottom")) {
            var2 += var4;
         } else {
            var1 += var4;
         }
      }

      this.setInsets(var1, (short)((int)this.painter.getInset(2, this)), var2, (short)((int)this.painter.getInset(4, this)));
   }

   protected void setPropertiesFromAttributes() {
      StyleSheet var1 = this.getStyleSheet();
      this.attr = var1.getViewAttributes(this);
      this.painter = var1.getBoxPainter(this.attr);
      if (this.attr != null) {
         this.setInsets((short)((int)this.painter.getInset(1, this)), (short)((int)this.painter.getInset(2, this)), (short)((int)this.painter.getInset(3, this)), (short)((int)this.painter.getInset(4, this)));
         CSS.LengthValue var2 = (CSS.LengthValue)this.attr.getAttribute(CSS.Attribute.BORDER_SPACING);
         if (var2 != null) {
            this.cellSpacing = (int)var2.getValue();
         } else {
            this.cellSpacing = 2;
         }

         var2 = (CSS.LengthValue)this.attr.getAttribute(CSS.Attribute.BORDER_TOP_WIDTH);
         if (var2 != null) {
            this.borderWidth = (int)var2.getValue();
         } else {
            this.borderWidth = 0;
         }
      }

   }

   void updateGrid() {
      if (!this.gridValid) {
         this.relativeCells = false;
         this.multiRowCells = false;
         this.captionIndex = -1;
         this.rows.removeAllElements();
         int var1 = this.getViewCount();

         int var2;
         for(var2 = 0; var2 < var1; ++var2) {
            View var3 = this.getView(var2);
            if (var3 instanceof TableView.RowView) {
               this.rows.addElement((TableView.RowView)var3);
               TableView.RowView var4 = (TableView.RowView)var3;
               var4.clearFilledColumns();
               var4.rowIndex = this.rows.size() - 1;
               var4.viewIndex = var2;
            } else {
               Object var16 = var3.getElement().getAttributes().getAttribute(StyleConstants.NameAttribute);
               if (var16 instanceof HTML.Tag) {
                  HTML.Tag var5 = (HTML.Tag)var16;
                  if (var5 == HTML.Tag.CAPTION) {
                     this.captionIndex = var2;
                  }
               }
            }
         }

         var2 = 0;
         int var15 = this.rows.size();

         int var17;
         for(var17 = 0; var17 < var15; ++var17) {
            TableView.RowView var18 = this.getRow(var17);
            int var6 = 0;

            for(int var7 = 0; var7 < var18.getViewCount(); ++var6) {
               View var8 = var18.getView(var7);
               if (!this.relativeCells) {
                  AttributeSet var9 = var8.getAttributes();
                  CSS.LengthValue var10 = (CSS.LengthValue)var9.getAttribute(CSS.Attribute.WIDTH);
                  if (var10 != null && var10.isPercentage()) {
                     this.relativeCells = true;
                  }
               }

               while(var18.isFilled(var6)) {
                  ++var6;
               }

               int var19 = this.getRowsOccupied(var8);
               if (var19 > 1) {
                  this.multiRowCells = true;
               }

               int var20 = this.getColumnsOccupied(var8);
               if (var20 > 1 || var19 > 1) {
                  int var11 = var17 + var19;
                  int var12 = var6 + var20;
                  int var13 = var17;

                  while(true) {
                     if (var13 >= var11) {
                        if (var20 > 1) {
                           var6 += var20 - 1;
                        }
                        break;
                     }

                     for(int var14 = var6; var14 < var12; ++var14) {
                        if (var13 != var17 || var14 != var6) {
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

         for(var17 = 0; var17 < var2; ++var17) {
            this.columnRequirements[var17] = new SizeRequirements();
            this.columnRequirements[var17].maximum = Integer.MAX_VALUE;
         }

         this.gridValid = true;
      }

   }

   void addFill(int var1, int var2) {
      TableView.RowView var3 = this.getRow(var1);
      if (var3 != null) {
         var3.fillColumn(var2);
      }

   }

   protected void layoutColumns(int var1, int[] var2, int[] var3, SizeRequirements[] var4) {
      Arrays.fill((int[])var2, (int)0);
      Arrays.fill((int[])var3, (int)0);
      this.colIterator.setLayoutArrays(var2, var3, var1);
      CSS.calculateTiledLayout(this.colIterator, var1);
   }

   void calculateColumnRequirements(int var1) {
      SizeRequirements[] var2 = this.columnRequirements;
      int var3 = var2.length;

      int var4;
      for(var4 = 0; var4 < var3; ++var4) {
         SizeRequirements var5 = var2[var4];
         var5.minimum = 0;
         var5.preferred = 0;
         var5.maximum = Integer.MAX_VALUE;
      }

      Container var13 = this.getContainer();
      if (var13 != null) {
         if (var13 instanceof JTextComponent) {
            this.skipComments = !((JTextComponent)var13).isEditable();
         } else {
            this.skipComments = true;
         }
      }

      boolean var14 = false;
      var4 = this.getRowCount();

      TableView.RowView var6;
      int var7;
      int var8;
      int var9;
      View var10;
      int var15;
      for(var15 = 0; var15 < var4; ++var15) {
         var6 = this.getRow(var15);
         var7 = 0;
         var8 = var6.getViewCount();

         for(var9 = 0; var9 < var8; ++var9) {
            var10 = var6.getView(var9);
            if (!this.skipComments || var10 instanceof TableView.CellView) {
               while(var6.isFilled(var7)) {
                  ++var7;
               }

               this.getRowsOccupied(var10);
               int var12 = this.getColumnsOccupied(var10);
               if (var12 == 1) {
                  this.checkSingleColumnCell(var1, var7, var10);
               } else {
                  var14 = true;
                  var7 += var12 - 1;
               }

               ++var7;
            }
         }
      }

      if (var14) {
         for(var15 = 0; var15 < var4; ++var15) {
            var6 = this.getRow(var15);
            var7 = 0;
            var8 = var6.getViewCount();

            for(var9 = 0; var9 < var8; ++var9) {
               var10 = var6.getView(var9);
               if (!this.skipComments || var10 instanceof TableView.CellView) {
                  while(var6.isFilled(var7)) {
                     ++var7;
                  }

                  int var11 = this.getColumnsOccupied(var10);
                  if (var11 > 1) {
                     this.checkMultiColumnCell(var1, var7, var11, var10);
                     var7 += var11 - 1;
                  }

                  ++var7;
               }
            }
         }
      }

   }

   void checkSingleColumnCell(int var1, int var2, View var3) {
      SizeRequirements var4 = this.columnRequirements[var2];
      var4.minimum = Math.max((int)var3.getMinimumSpan(var1), var4.minimum);
      var4.preferred = Math.max((int)var3.getPreferredSpan(var1), var4.preferred);
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
      int[] var14;
      if ((long)var11 > var5) {
         SizeRequirements[] var18 = new SizeRequirements[var3];

         for(int var13 = 0; var13 < var3; ++var13) {
            var18[var13] = this.columnRequirements[var2 + var13];
         }

         int[] var20 = new int[var3];
         var14 = new int[var3];
         SizeRequirements.calculateTiledPositions(var11, (SizeRequirements)null, var18, var14, var20);

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

         for(int var22 = 0; var22 < var3; ++var22) {
            var21[var22] = this.columnRequirements[var2 + var22];
         }

         var14 = new int[var3];
         int[] var23 = new int[var3];
         SizeRequirements.calculateTiledPositions(var19, (SizeRequirements)null, var21, var23, var14);

         for(int var24 = 0; var24 < var3; ++var24) {
            SizeRequirements var17 = var21[var24];
            var17.preferred = Math.max(var14[var24], var17.preferred);
            var17.maximum = Math.max(var17.preferred, var17.maximum);
         }
      }

   }

   protected SizeRequirements calculateMinorAxisRequirements(int var1, SizeRequirements var2) {
      this.updateGrid();
      this.calculateColumnRequirements(var1);
      if (var2 == null) {
         var2 = new SizeRequirements();
      }

      long var3 = 0L;
      long var5 = 0L;
      int var7 = this.columnRequirements.length;

      int var8;
      for(var8 = 0; var8 < var7; ++var8) {
         SizeRequirements var9 = this.columnRequirements[var8];
         var3 += (long)var9.minimum;
         var5 += (long)var9.preferred;
      }

      var8 = (var7 + 1) * this.cellSpacing + 2 * this.borderWidth;
      var3 += (long)var8;
      var5 += (long)var8;
      var2.minimum = (int)var3;
      var2.preferred = (int)var5;
      var2.maximum = (int)var5;
      AttributeSet var13 = this.getAttributes();
      CSS.LengthValue var10 = (CSS.LengthValue)var13.getAttribute(CSS.Attribute.WIDTH);
      if (BlockView.spanSetFromAttributes(var1, var2, var10, (CSS.LengthValue)null) && var2.minimum < (int)var3) {
         var2.maximum = var2.minimum = var2.preferred = (int)var3;
      }

      this.totalColumnRequirements.minimum = var2.minimum;
      this.totalColumnRequirements.preferred = var2.preferred;
      this.totalColumnRequirements.maximum = var2.maximum;
      Object var11 = var13.getAttribute(CSS.Attribute.TEXT_ALIGN);
      if (var11 != null) {
         String var12 = var11.toString();
         if (var12.equals("left")) {
            var2.alignment = 0.0F;
         } else if (var12.equals("center")) {
            var2.alignment = 0.5F;
         } else if (var12.equals("right")) {
            var2.alignment = 1.0F;
         } else {
            var2.alignment = 0.0F;
         }
      } else {
         var2.alignment = 0.0F;
      }

      return var2;
   }

   protected SizeRequirements calculateMajorAxisRequirements(int var1, SizeRequirements var2) {
      this.updateInsets();
      this.rowIterator.updateAdjustments();
      var2 = CSS.calculateTiledRequirements(this.rowIterator, var2);
      var2.maximum = var2.preferred;
      return var2;
   }

   protected void layoutMinorAxis(int var1, int var2, int[] var3, int[] var4) {
      this.updateGrid();
      int var5 = this.getRowCount();

      for(int var6 = 0; var6 < var5; ++var6) {
         TableView.RowView var7 = this.getRow(var6);
         var7.layoutChanged(var2);
      }

      this.layoutColumns(var1, this.columnOffsets, this.columnSpans, this.columnRequirements);
      super.layoutMinorAxis(var1, var2, var3, var4);
   }

   protected void layoutMajorAxis(int var1, int var2, int[] var3, int[] var4) {
      this.rowIterator.setLayoutArrays(var3, var4);
      CSS.calculateTiledLayout(this.rowIterator, var1);
      if (this.captionIndex != -1) {
         View var5 = this.getView(this.captionIndex);
         int var6 = (int)var5.getPreferredSpan(1);
         var4[this.captionIndex] = var6;
         short var7 = (short)((int)this.painter.getInset(3, this));
         if (var7 != this.getBottomInset()) {
            var3[this.captionIndex] = var1 + var7;
         } else {
            var3[this.captionIndex] = -this.getTopInset();
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

   public AttributeSet getAttributes() {
      if (this.attr == null) {
         StyleSheet var1 = this.getStyleSheet();
         this.attr = var1.getViewAttributes(this);
      }

      return this.attr;
   }

   public void paint(Graphics var1, Shape var2) {
      Rectangle var3 = var2.getBounds();
      this.setSize((float)var3.width, (float)var3.height);
      if (this.captionIndex != -1) {
         short var4 = (short)((int)this.painter.getInset(1, this));
         short var5 = (short)((int)this.painter.getInset(3, this));
         if (var4 != this.getTopInset()) {
            int var6 = this.getTopInset() - var4;
            var3.y += var6;
            var3.height -= var6;
         } else {
            var3.height -= this.getBottomInset() - var5;
         }
      }

      this.painter.paint(var1, (float)var3.x, (float)var3.y, (float)var3.width, (float)var3.height, this);
      int var7 = this.getViewCount();

      for(int var8 = 0; var8 < var7; ++var8) {
         View var9 = this.getView(var8);
         var9.paint(var1, this.getChildAllocation(var8, var2));
      }

   }

   public void setParent(View var1) {
      super.setParent(var1);
      if (var1 != null) {
         this.setPropertiesFromAttributes();
      }

   }

   public ViewFactory getViewFactory() {
      return this;
   }

   public void insertUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
      super.insertUpdate(var1, var2, this);
   }

   public void removeUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
      super.removeUpdate(var1, var2, this);
   }

   public void changedUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
      super.changedUpdate(var1, var2, this);
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

   public View create(Element var1) {
      Object var2 = var1.getAttributes().getAttribute(StyleConstants.NameAttribute);
      if (var2 instanceof HTML.Tag) {
         HTML.Tag var3 = (HTML.Tag)var2;
         if (var3 == HTML.Tag.TR) {
            return this.createTableRow(var1);
         }

         if (var3 == HTML.Tag.TD || var3 == HTML.Tag.TH) {
            return new TableView.CellView(var1);
         }

         if (var3 == HTML.Tag.CAPTION) {
            return new ParagraphView(var1);
         }
      }

      View var5 = this.getParent();
      if (var5 != null) {
         ViewFactory var4 = var5.getViewFactory();
         if (var4 != null) {
            return var4.create(var1);
         }
      }

      return null;
   }

   class CellView extends BlockView {
      public CellView(Element var2) {
         super(var2, 1);
      }

      protected void layoutMajorAxis(int var1, int var2, int[] var3, int[] var4) {
         super.layoutMajorAxis(var1, var2, var3, var4);
         int var5 = 0;
         int var6 = var4.length;

         int var7;
         for(var7 = 0; var7 < var6; ++var7) {
            var5 += var4[var7];
         }

         var7 = 0;
         if (var5 < var1) {
            String var8 = (String)this.getElement().getAttributes().getAttribute(HTML.Attribute.VALIGN);
            if (var8 == null) {
               AttributeSet var9 = this.getElement().getParentElement().getAttributes();
               var8 = (String)var9.getAttribute(HTML.Attribute.VALIGN);
            }

            if (var8 != null && !var8.equals("middle")) {
               if (var8.equals("bottom")) {
                  var7 = var1 - var5;
               }
            } else {
               var7 = (var1 - var5) / 2;
            }
         }

         if (var7 != 0) {
            for(int var10 = 0; var10 < var6; ++var10) {
               var3[var10] += var7;
            }
         }

      }

      protected SizeRequirements calculateMajorAxisRequirements(int var1, SizeRequirements var2) {
         SizeRequirements var3 = super.calculateMajorAxisRequirements(var1, var2);
         var3.maximum = Integer.MAX_VALUE;
         return var3;
      }

      protected SizeRequirements calculateMinorAxisRequirements(int var1, SizeRequirements var2) {
         SizeRequirements var3 = super.calculateMinorAxisRequirements(var1, var2);
         int var4 = this.getViewCount();
         int var5 = 0;

         for(int var6 = 0; var6 < var4; ++var6) {
            View var7 = this.getView(var6);
            var5 = Math.max((int)var7.getMinimumSpan(var1), var5);
         }

         var3.minimum = Math.min(var3.minimum, var5);
         return var3;
      }
   }

   public class RowView extends BoxView {
      private StyleSheet.BoxPainter painter;
      private AttributeSet attr;
      BitSet fillColumns = new BitSet();
      int rowIndex;
      int viewIndex;
      boolean multiRowCells;

      public RowView(Element var2) {
         super(var2, 0);
         this.setPropertiesFromAttributes();
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

      public AttributeSet getAttributes() {
         return this.attr;
      }

      View findViewAtPoint(int var1, int var2, Rectangle var3) {
         int var4 = this.getViewCount();

         for(int var5 = 0; var5 < var4; ++var5) {
            if (this.getChildAllocation(var5, var3).contains((double)var1, (double)var2)) {
               this.childAllocation(var5, var3);
               return this.getView(var5);
            }
         }

         return null;
      }

      protected StyleSheet getStyleSheet() {
         HTMLDocument var1 = (HTMLDocument)this.getDocument();
         return var1.getStyleSheet();
      }

      public void preferenceChanged(View var1, boolean var2, boolean var3) {
         super.preferenceChanged(var1, var2, var3);
         if (TableView.this.multiRowCells && var3) {
            for(int var4 = this.rowIndex - 1; var4 >= 0; --var4) {
               TableView.RowView var5 = TableView.this.getRow(var4);
               if (var5.multiRowCells) {
                  var5.preferenceChanged((View)null, false, true);
                  break;
               }
            }
         }

      }

      protected SizeRequirements calculateMajorAxisRequirements(int var1, SizeRequirements var2) {
         SizeRequirements var3 = new SizeRequirements();
         var3.minimum = TableView.this.totalColumnRequirements.minimum;
         var3.maximum = TableView.this.totalColumnRequirements.maximum;
         var3.preferred = TableView.this.totalColumnRequirements.preferred;
         var3.alignment = 0.0F;
         return var3;
      }

      public float getMinimumSpan(int var1) {
         float var2;
         if (var1 == 0) {
            var2 = (float)(TableView.this.totalColumnRequirements.minimum + this.getLeftInset() + this.getRightInset());
         } else {
            var2 = super.getMinimumSpan(var1);
         }

         return var2;
      }

      public float getMaximumSpan(int var1) {
         float var2;
         if (var1 == 0) {
            var2 = 2.14748365E9F;
         } else {
            var2 = super.getMaximumSpan(var1);
         }

         return var2;
      }

      public float getPreferredSpan(int var1) {
         float var2;
         if (var1 == 0) {
            var2 = (float)(TableView.this.totalColumnRequirements.preferred + this.getLeftInset() + this.getRightInset());
         } else {
            var2 = super.getPreferredSpan(var1);
         }

         return var2;
      }

      public void changedUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
         super.changedUpdate(var1, var2, var3);
         int var4 = var1.getOffset();
         if (var4 <= this.getStartOffset() && var4 + var1.getLength() >= this.getEndOffset()) {
            this.setPropertiesFromAttributes();
         }

      }

      public void paint(Graphics var1, Shape var2) {
         Rectangle var3 = (Rectangle)var2;
         this.painter.paint(var1, (float)var3.x, (float)var3.y, (float)var3.width, (float)var3.height, this);
         super.paint(var1, var3);
      }

      public void replace(int var1, int var2, View[] var3) {
         super.replace(var1, var2, var3);
         TableView.this.invalidateGrid();
      }

      protected SizeRequirements calculateMinorAxisRequirements(int var1, SizeRequirements var2) {
         long var3 = 0L;
         long var5 = 0L;
         long var7 = 0L;
         this.multiRowCells = false;
         int var9 = this.getViewCount();

         for(int var10 = 0; var10 < var9; ++var10) {
            View var11 = this.getView(var10);
            if (TableView.this.getRowsOccupied(var11) > 1) {
               this.multiRowCells = true;
               var7 = Math.max((long)((int)var11.getMaximumSpan(var1)), var7);
            } else {
               var3 = Math.max((long)((int)var11.getMinimumSpan(var1)), var3);
               var5 = Math.max((long)((int)var11.getPreferredSpan(var1)), var5);
               var7 = Math.max((long)((int)var11.getMaximumSpan(var1)), var7);
            }
         }

         if (var2 == null) {
            var2 = new SizeRequirements();
            var2.alignment = 0.5F;
         }

         var2.preferred = (int)var5;
         var2.minimum = (int)var3;
         var2.maximum = (int)var7;
         return var2;
      }

      protected void layoutMajorAxis(int var1, int var2, int[] var3, int[] var4) {
         int var5 = 0;
         int var6 = this.getViewCount();

         for(int var7 = 0; var7 < var6; ++var7) {
            View var8 = this.getView(var7);
            if (!TableView.this.skipComments || var8 instanceof TableView.CellView) {
               while(this.isFilled(var5)) {
                  ++var5;
               }

               int var9 = TableView.this.getColumnsOccupied(var8);
               var4[var7] = TableView.this.columnSpans[var5];
               var3[var7] = TableView.this.columnOffsets[var5];
               if (var9 > 1) {
                  int var10 = TableView.this.columnSpans.length;

                  for(int var11 = 1; var11 < var9; ++var11) {
                     if (var5 + var11 < var10) {
                        var4[var7] += TableView.this.columnSpans[var5 + var11];
                        var4[var7] += TableView.this.cellSpacing;
                     }
                  }

                  var5 += var9 - 1;
               }

               ++var5;
            }
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
               int var11 = this.rowIndex;
               int var12 = Math.min(this.rowIndex + var10 - 1, TableView.this.getRowCount() - 1);
               var4[var7] = TableView.this.getMultiRowSpan(var11, var12);
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

      void setPropertiesFromAttributes() {
         StyleSheet var1 = this.getStyleSheet();
         this.attr = var1.getViewAttributes(this);
         this.painter = var1.getBoxPainter(this.attr);
      }
   }

   class RowIterator implements CSS.LayoutIterator {
      private int row;
      private int[] adjustments;
      private int[] offsets;
      private int[] spans;

      void updateAdjustments() {
         byte var1 = 1;
         if (TableView.this.multiRowCells) {
            int var2 = TableView.this.getRowCount();
            this.adjustments = new int[var2];

            for(int var3 = 0; var3 < var2; ++var3) {
               TableView.RowView var4 = TableView.this.getRow(var3);
               if (var4.multiRowCells) {
                  int var5 = var4.getViewCount();

                  for(int var6 = 0; var6 < var5; ++var6) {
                     View var7 = var4.getView(var6);
                     int var8 = TableView.this.getRowsOccupied(var7);
                     if (var8 > 1) {
                        int var9 = (int)var7.getPreferredSpan(var1);
                        this.adjustMultiRowSpan(var9, var8, var3);
                     }
                  }
               }
            }
         } else {
            this.adjustments = null;
         }

      }

      void adjustMultiRowSpan(int var1, int var2, int var3) {
         if (var3 + var2 > this.getCount()) {
            var2 = this.getCount() - var3;
            if (var2 < 1) {
               return;
            }
         }

         int var4 = 0;

         int var5;
         for(var5 = 0; var5 < var2; ++var5) {
            TableView.RowView var6 = TableView.this.getRow(var3 + var5);
            var4 = (int)((float)var4 + var6.getPreferredSpan(1));
         }

         if (var1 > var4) {
            var5 = var1 - var4;
            int var10 = var5 / var2;
            int var7 = var10 + (var5 - var10 * var2);
            TableView.this.getRow(var3);
            this.adjustments[var3] = Math.max(this.adjustments[var3], var7);

            for(int var9 = 1; var9 < var2; ++var9) {
               this.adjustments[var3 + var9] = Math.max(this.adjustments[var3 + var9], var10);
            }
         }

      }

      void setLayoutArrays(int[] var1, int[] var2) {
         this.offsets = var1;
         this.spans = var2;
      }

      public void setOffset(int var1) {
         TableView.RowView var2 = TableView.this.getRow(this.row);
         if (var2 != null) {
            this.offsets[var2.viewIndex] = var1;
         }

      }

      public int getOffset() {
         TableView.RowView var1 = TableView.this.getRow(this.row);
         return var1 != null ? this.offsets[var1.viewIndex] : 0;
      }

      public void setSpan(int var1) {
         TableView.RowView var2 = TableView.this.getRow(this.row);
         if (var2 != null) {
            this.spans[var2.viewIndex] = var1;
         }

      }

      public int getSpan() {
         TableView.RowView var1 = TableView.this.getRow(this.row);
         return var1 != null ? this.spans[var1.viewIndex] : 0;
      }

      public int getCount() {
         return TableView.this.rows.size();
      }

      public void setIndex(int var1) {
         this.row = var1;
      }

      public float getMinimumSpan(float var1) {
         return this.getPreferredSpan(var1);
      }

      public float getPreferredSpan(float var1) {
         TableView.RowView var2 = TableView.this.getRow(this.row);
         if (var2 != null) {
            int var3 = this.adjustments != null ? this.adjustments[this.row] : 0;
            return var2.getPreferredSpan(TableView.this.getAxis()) + (float)var3;
         } else {
            return 0.0F;
         }
      }

      public float getMaximumSpan(float var1) {
         return this.getPreferredSpan(var1);
      }

      public float getBorderWidth() {
         return (float)TableView.this.borderWidth;
      }

      public float getLeadingCollapseSpan() {
         return (float)TableView.this.cellSpacing;
      }

      public float getTrailingCollapseSpan() {
         return (float)TableView.this.cellSpacing;
      }

      public int getAdjustmentWeight() {
         return 0;
      }
   }

   class ColumnIterator implements CSS.LayoutIterator {
      private int col;
      private int[] percentages;
      private int[] adjustmentWeights;
      private int[] offsets;
      private int[] spans;

      void disablePercentages() {
         this.percentages = null;
      }

      private void updatePercentagesAndAdjustmentWeights(int var1) {
         this.adjustmentWeights = new int[TableView.this.columnRequirements.length];

         int var2;
         for(var2 = 0; var2 < TableView.this.columnRequirements.length; ++var2) {
            this.adjustmentWeights[var2] = 0;
         }

         if (TableView.this.relativeCells) {
            this.percentages = new int[TableView.this.columnRequirements.length];
         } else {
            this.percentages = null;
         }

         var2 = TableView.this.getRowCount();

         for(int var3 = 0; var3 < var2; ++var3) {
            TableView.RowView var4 = TableView.this.getRow(var3);
            int var5 = 0;
            int var6 = var4.getViewCount();

            for(int var7 = 0; var7 < var6; ++var5) {
               View var8;
               for(var8 = var4.getView(var7); var4.isFilled(var5); ++var5) {
               }

               TableView.this.getRowsOccupied(var8);
               int var10 = TableView.this.getColumnsOccupied(var8);
               AttributeSet var11 = var8.getAttributes();
               CSS.LengthValue var12 = (CSS.LengthValue)var11.getAttribute(CSS.Attribute.WIDTH);
               if (var12 != null) {
                  int var13 = (int)(var12.getValue((float)var1) / (float)var10 + 0.5F);

                  for(int var14 = 0; var14 < var10; ++var14) {
                     if (var12.isPercentage()) {
                        this.percentages[var5 + var14] = Math.max(this.percentages[var5 + var14], var13);
                        this.adjustmentWeights[var5 + var14] = Math.max(this.adjustmentWeights[var5 + var14], 2);
                     } else {
                        this.adjustmentWeights[var5 + var14] = Math.max(this.adjustmentWeights[var5 + var14], 1);
                     }
                  }
               }

               var5 += var10 - 1;
               ++var7;
            }
         }

      }

      public void setLayoutArrays(int[] var1, int[] var2, int var3) {
         this.offsets = var1;
         this.spans = var2;
         this.updatePercentagesAndAdjustmentWeights(var3);
      }

      public int getCount() {
         return TableView.this.columnRequirements.length;
      }

      public void setIndex(int var1) {
         this.col = var1;
      }

      public void setOffset(int var1) {
         this.offsets[this.col] = var1;
      }

      public int getOffset() {
         return this.offsets[this.col];
      }

      public void setSpan(int var1) {
         this.spans[this.col] = var1;
      }

      public int getSpan() {
         return this.spans[this.col];
      }

      public float getMinimumSpan(float var1) {
         return (float)TableView.this.columnRequirements[this.col].minimum;
      }

      public float getPreferredSpan(float var1) {
         return this.percentages != null && this.percentages[this.col] != 0 ? (float)Math.max(this.percentages[this.col], TableView.this.columnRequirements[this.col].minimum) : (float)TableView.this.columnRequirements[this.col].preferred;
      }

      public float getMaximumSpan(float var1) {
         return (float)TableView.this.columnRequirements[this.col].maximum;
      }

      public float getBorderWidth() {
         return (float)TableView.this.borderWidth;
      }

      public float getLeadingCollapseSpan() {
         return (float)TableView.this.cellSpacing;
      }

      public float getTrailingCollapseSpan() {
         return (float)TableView.this.cellSpacing;
      }

      public int getAdjustmentWeight() {
         return this.adjustmentWeights[this.col];
      }
   }
}
