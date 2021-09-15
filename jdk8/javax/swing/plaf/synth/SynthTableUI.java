package javax.swing.plaf.synth;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Date;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.LookAndFeel;
import javax.swing.TransferHandler;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class SynthTableUI extends BasicTableUI implements SynthUI, PropertyChangeListener {
   private SynthStyle style;
   private boolean useTableColors;
   private boolean useUIBorder;
   private Color alternateColor;
   private TableCellRenderer dateRenderer;
   private TableCellRenderer numberRenderer;
   private TableCellRenderer doubleRender;
   private TableCellRenderer floatRenderer;
   private TableCellRenderer iconRenderer;
   private TableCellRenderer imageIconRenderer;
   private TableCellRenderer booleanRenderer;
   private TableCellRenderer objectRenderer;

   public static ComponentUI createUI(JComponent var0) {
      return new SynthTableUI();
   }

   protected void installDefaults() {
      this.dateRenderer = this.installRendererIfPossible(Date.class, (TableCellRenderer)null);
      this.numberRenderer = this.installRendererIfPossible(Number.class, (TableCellRenderer)null);
      this.doubleRender = this.installRendererIfPossible(Double.class, (TableCellRenderer)null);
      this.floatRenderer = this.installRendererIfPossible(Float.class, (TableCellRenderer)null);
      this.iconRenderer = this.installRendererIfPossible(Icon.class, (TableCellRenderer)null);
      this.imageIconRenderer = this.installRendererIfPossible(ImageIcon.class, (TableCellRenderer)null);
      this.booleanRenderer = this.installRendererIfPossible(Boolean.class, new SynthTableUI.SynthBooleanTableCellRenderer());
      this.objectRenderer = this.installRendererIfPossible(Object.class, new SynthTableUI.SynthTableCellRenderer());
      this.updateStyle(this.table);
   }

   private TableCellRenderer installRendererIfPossible(Class var1, TableCellRenderer var2) {
      TableCellRenderer var3 = this.table.getDefaultRenderer(var1);
      if (var3 instanceof UIResource) {
         this.table.setDefaultRenderer(var1, var2);
      }

      return var3;
   }

   private void updateStyle(JTable var1) {
      SynthContext var2 = this.getContext(var1, 1);
      SynthStyle var3 = this.style;
      this.style = SynthLookAndFeel.updateStyle(var2, this);
      if (this.style != var3) {
         var2.setComponentState(513);
         Color var4 = this.table.getSelectionBackground();
         if (var4 == null || var4 instanceof UIResource) {
            this.table.setSelectionBackground(this.style.getColor(var2, ColorType.TEXT_BACKGROUND));
         }

         Color var5 = this.table.getSelectionForeground();
         if (var5 == null || var5 instanceof UIResource) {
            this.table.setSelectionForeground(this.style.getColor(var2, ColorType.TEXT_FOREGROUND));
         }

         var2.setComponentState(1);
         Color var6 = this.table.getGridColor();
         if (var6 == null || var6 instanceof UIResource) {
            var6 = (Color)this.style.get(var2, "Table.gridColor");
            if (var6 == null) {
               var6 = this.style.getColor(var2, ColorType.FOREGROUND);
            }

            this.table.setGridColor((Color)(var6 == null ? new ColorUIResource(Color.GRAY) : var6));
         }

         this.useTableColors = this.style.getBoolean(var2, "Table.rendererUseTableColors", true);
         this.useUIBorder = this.style.getBoolean(var2, "Table.rendererUseUIBorder", true);
         Object var7 = this.style.get(var2, "Table.rowHeight");
         if (var7 != null) {
            LookAndFeel.installProperty(this.table, "rowHeight", var7);
         }

         boolean var8 = this.style.getBoolean(var2, "Table.showGrid", true);
         if (!var8) {
            this.table.setShowGrid(false);
         }

         Dimension var9 = this.table.getIntercellSpacing();
         if (var9 != null) {
            var9 = (Dimension)this.style.get(var2, "Table.intercellSpacing");
         }

         this.alternateColor = (Color)this.style.get(var2, "Table.alternateRowColor");
         if (var9 != null) {
            this.table.setIntercellSpacing(var9);
         }

         if (var3 != null) {
            this.uninstallKeyboardActions();
            this.installKeyboardActions();
         }
      }

      var2.dispose();
   }

   protected void installListeners() {
      super.installListeners();
      this.table.addPropertyChangeListener(this);
   }

   protected void uninstallDefaults() {
      this.table.setDefaultRenderer(Date.class, this.dateRenderer);
      this.table.setDefaultRenderer(Number.class, this.numberRenderer);
      this.table.setDefaultRenderer(Double.class, this.doubleRender);
      this.table.setDefaultRenderer(Float.class, this.floatRenderer);
      this.table.setDefaultRenderer(Icon.class, this.iconRenderer);
      this.table.setDefaultRenderer(ImageIcon.class, this.imageIconRenderer);
      this.table.setDefaultRenderer(Boolean.class, this.booleanRenderer);
      this.table.setDefaultRenderer(Object.class, this.objectRenderer);
      if (this.table.getTransferHandler() instanceof UIResource) {
         this.table.setTransferHandler((TransferHandler)null);
      }

      SynthContext var1 = this.getContext(this.table, 1);
      this.style.uninstallDefaults(var1);
      var1.dispose();
      this.style = null;
   }

   protected void uninstallListeners() {
      this.table.removePropertyChangeListener(this);
      super.uninstallListeners();
   }

   public SynthContext getContext(JComponent var1) {
      return this.getContext(var1, SynthLookAndFeel.getComponentState(var1));
   }

   private SynthContext getContext(JComponent var1, int var2) {
      return SynthContext.getContext(var1, this.style, var2);
   }

   public void update(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      SynthLookAndFeel.update(var3, var1);
      var3.getPainter().paintTableBackground(var3, var1, 0, 0, var2.getWidth(), var2.getHeight());
      this.paint(var3, var1);
      var3.dispose();
   }

   public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      var1.getPainter().paintTableBorder(var1, var2, var3, var4, var5, var6);
   }

   public void paint(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      this.paint(var3, var1);
      var3.dispose();
   }

   protected void paint(SynthContext var1, Graphics var2) {
      Rectangle var3 = var2.getClipBounds();
      Rectangle var4 = this.table.getBounds();
      var4.x = var4.y = 0;
      if (this.table.getRowCount() > 0 && this.table.getColumnCount() > 0 && var4.intersects(var3)) {
         boolean var5 = this.table.getComponentOrientation().isLeftToRight();
         Point var6 = var3.getLocation();
         Point var7 = new Point(var3.x + var3.width - 1, var3.y + var3.height - 1);
         int var8 = this.table.rowAtPoint(var6);
         int var9 = this.table.rowAtPoint(var7);
         if (var8 == -1) {
            var8 = 0;
         }

         if (var9 == -1) {
            var9 = this.table.getRowCount() - 1;
         }

         int var10 = this.table.columnAtPoint(var5 ? var6 : var7);
         int var11 = this.table.columnAtPoint(var5 ? var7 : var6);
         if (var10 == -1) {
            var10 = 0;
         }

         if (var11 == -1) {
            var11 = this.table.getColumnCount() - 1;
         }

         this.paintCells(var1, var2, var8, var9, var10, var11);
         this.paintGrid(var1, var2, var8, var9, var10, var11);
         this.paintDropLines(var1, var2);
      } else {
         this.paintDropLines(var1, var2);
      }
   }

   private void paintDropLines(SynthContext var1, Graphics var2) {
      JTable.DropLocation var3 = this.table.getDropLocation();
      if (var3 != null) {
         Color var4 = (Color)this.style.get(var1, "Table.dropLineColor");
         Color var5 = (Color)this.style.get(var1, "Table.dropLineShortColor");
         if (var4 != null || var5 != null) {
            Rectangle var6 = this.getHDropLineRect(var3);
            int var7;
            int var8;
            if (var6 != null) {
               var7 = var6.x;
               var8 = var6.width;
               if (var4 != null) {
                  this.extendRect(var6, true);
                  var2.setColor(var4);
                  var2.fillRect(var6.x, var6.y, var6.width, var6.height);
               }

               if (!var3.isInsertColumn() && var5 != null) {
                  var2.setColor(var5);
                  var2.fillRect(var7, var6.y, var8, var6.height);
               }
            }

            var6 = this.getVDropLineRect(var3);
            if (var6 != null) {
               var7 = var6.y;
               var8 = var6.height;
               if (var4 != null) {
                  this.extendRect(var6, false);
                  var2.setColor(var4);
                  var2.fillRect(var6.x, var6.y, var6.width, var6.height);
               }

               if (!var3.isInsertRow() && var5 != null) {
                  var2.setColor(var5);
                  var2.fillRect(var6.x, var7, var6.width, var8);
               }
            }

         }
      }
   }

   private Rectangle getHDropLineRect(JTable.DropLocation var1) {
      if (!var1.isInsertRow()) {
         return null;
      } else {
         int var2 = var1.getRow();
         int var3 = var1.getColumn();
         if (var3 >= this.table.getColumnCount()) {
            --var3;
         }

         Rectangle var4 = this.table.getCellRect(var2, var3, true);
         if (var2 >= this.table.getRowCount()) {
            --var2;
            Rectangle var5 = this.table.getCellRect(var2, var3, true);
            var4.y = var5.y + var5.height;
         }

         if (var4.y == 0) {
            var4.y = -1;
         } else {
            var4.y -= 2;
         }

         var4.height = 3;
         return var4;
      }
   }

   private Rectangle getVDropLineRect(JTable.DropLocation var1) {
      if (!var1.isInsertColumn()) {
         return null;
      } else {
         boolean var2 = this.table.getComponentOrientation().isLeftToRight();
         int var3 = var1.getColumn();
         Rectangle var4 = this.table.getCellRect(var1.getRow(), var3, true);
         if (var3 >= this.table.getColumnCount()) {
            --var3;
            var4 = this.table.getCellRect(var1.getRow(), var3, true);
            if (var2) {
               var4.x += var4.width;
            }
         } else if (!var2) {
            var4.x += var4.width;
         }

         if (var4.x == 0) {
            var4.x = -1;
         } else {
            var4.x -= 2;
         }

         var4.width = 3;
         return var4;
      }
   }

   private Rectangle extendRect(Rectangle var1, boolean var2) {
      if (var1 == null) {
         return var1;
      } else {
         if (var2) {
            var1.x = 0;
            var1.width = this.table.getWidth();
         } else {
            var1.y = 0;
            if (this.table.getRowCount() != 0) {
               Rectangle var3 = this.table.getCellRect(this.table.getRowCount() - 1, 0, true);
               var1.height = var3.y + var3.height;
            } else {
               var1.height = this.table.getHeight();
            }
         }

         return var1;
      }
   }

   private void paintGrid(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      var2.setColor(this.table.getGridColor());
      Rectangle var7 = this.table.getCellRect(var3, var5, true);
      Rectangle var8 = this.table.getCellRect(var4, var6, true);
      Rectangle var9 = var7.union(var8);
      SynthGraphicsUtils var10 = var1.getStyle().getGraphicsUtils(var1);
      int var12;
      int var13;
      if (this.table.getShowHorizontalLines()) {
         int var11 = var9.x + var9.width;
         var12 = var9.y;

         for(var13 = var3; var13 <= var4; ++var13) {
            var12 += this.table.getRowHeight(var13);
            var10.drawLine(var1, "Table.grid", var2, var9.x, var12 - 1, var11 - 1, var12 - 1);
         }
      }

      if (this.table.getShowVerticalLines()) {
         TableColumnModel var16 = this.table.getColumnModel();
         var12 = var9.y + var9.height;
         int var14;
         int var15;
         if (this.table.getComponentOrientation().isLeftToRight()) {
            var13 = var9.x;

            for(var14 = var5; var14 <= var6; ++var14) {
               var15 = var16.getColumn(var14).getWidth();
               var13 += var15;
               var10.drawLine(var1, "Table.grid", var2, var13 - 1, 0, var13 - 1, var12 - 1);
            }
         } else {
            var13 = var9.x;

            for(var14 = var6; var14 >= var5; --var14) {
               var15 = var16.getColumn(var14).getWidth();
               var13 += var15;
               var10.drawLine(var1, "Table.grid", var2, var13 - 1, 0, var13 - 1, var12 - 1);
            }
         }
      }

   }

   private int viewIndexForColumn(TableColumn var1) {
      TableColumnModel var2 = this.table.getColumnModel();

      for(int var3 = 0; var3 < var2.getColumnCount(); ++var3) {
         if (var2.getColumn(var3) == var1) {
            return var3;
         }
      }

      return -1;
   }

   private void paintCells(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      JTableHeader var7 = this.table.getTableHeader();
      TableColumn var8 = var7 == null ? null : var7.getDraggedColumn();
      TableColumnModel var9 = this.table.getColumnModel();
      int var10 = var9.getColumnMargin();
      Rectangle var11;
      TableColumn var12;
      int var13;
      int var14;
      int var15;
      if (this.table.getComponentOrientation().isLeftToRight()) {
         for(var14 = var3; var14 <= var4; ++var14) {
            var11 = this.table.getCellRect(var14, var5, false);

            for(var15 = var5; var15 <= var6; ++var15) {
               var12 = var9.getColumn(var15);
               var13 = var12.getWidth();
               var11.width = var13 - var10;
               if (var12 != var8) {
                  this.paintCell(var1, var2, var11, var14, var15);
               }

               var11.x += var13;
            }
         }
      } else {
         for(var14 = var3; var14 <= var4; ++var14) {
            var11 = this.table.getCellRect(var14, var5, false);
            var12 = var9.getColumn(var5);
            if (var12 != var8) {
               var13 = var12.getWidth();
               var11.width = var13 - var10;
               this.paintCell(var1, var2, var11, var14, var5);
            }

            for(var15 = var5 + 1; var15 <= var6; ++var15) {
               var12 = var9.getColumn(var15);
               var13 = var12.getWidth();
               var11.width = var13 - var10;
               var11.x -= var13;
               if (var12 != var8) {
                  this.paintCell(var1, var2, var11, var14, var15);
               }
            }
         }
      }

      if (var8 != null) {
         this.paintDraggedArea(var1, var2, var3, var4, var8, var7.getDraggedDistance());
      }

      this.rendererPane.removeAll();
   }

   private void paintDraggedArea(SynthContext var1, Graphics var2, int var3, int var4, TableColumn var5, int var6) {
      int var7 = this.viewIndexForColumn(var5);
      Rectangle var8 = this.table.getCellRect(var3, var7, true);
      Rectangle var9 = this.table.getCellRect(var4, var7, true);
      Rectangle var10 = var8.union(var9);
      var2.setColor(this.table.getParent().getBackground());
      var2.fillRect(var10.x, var10.y, var10.width, var10.height);
      var10.x += var6;
      var2.setColor(var1.getStyle().getColor(var1, ColorType.BACKGROUND));
      var2.fillRect(var10.x, var10.y, var10.width, var10.height);
      SynthGraphicsUtils var11 = var1.getStyle().getGraphicsUtils(var1);
      int var12;
      int var15;
      if (this.table.getShowVerticalLines()) {
         var2.setColor(this.table.getGridColor());
         var12 = var10.x;
         int var13 = var10.y;
         int var14 = var12 + var10.width - 1;
         var15 = var13 + var10.height - 1;
         var11.drawLine(var1, "Table.grid", var2, var12 - 1, var13, var12 - 1, var15);
         var11.drawLine(var1, "Table.grid", var2, var14, var13, var14, var15);
      }

      for(var12 = var3; var12 <= var4; ++var12) {
         Rectangle var19 = this.table.getCellRect(var12, var7, false);
         var19.x += var6;
         this.paintCell(var1, var2, var19, var12, var7);
         if (this.table.getShowHorizontalLines()) {
            var2.setColor(this.table.getGridColor());
            Rectangle var20 = this.table.getCellRect(var12, var7, true);
            var20.x += var6;
            var15 = var20.x;
            int var16 = var20.y;
            int var17 = var15 + var20.width - 1;
            int var18 = var16 + var20.height - 1;
            var11.drawLine(var1, "Table.grid", var2, var15, var18, var17, var18);
         }
      }

   }

   private void paintCell(SynthContext var1, Graphics var2, Rectangle var3, int var4, int var5) {
      if (this.table.isEditing() && this.table.getEditingRow() == var4 && this.table.getEditingColumn() == var5) {
         Component var9 = this.table.getEditorComponent();
         var9.setBounds(var3);
         var9.validate();
      } else {
         TableCellRenderer var6 = this.table.getCellRenderer(var4, var5);
         Component var7 = this.table.prepareRenderer(var6, var4, var5);
         Color var8 = var7.getBackground();
         if ((var8 == null || var8 instanceof UIResource || var7 instanceof SynthTableUI.SynthBooleanTableCellRenderer) && !this.table.isCellSelected(var4, var5) && this.alternateColor != null && var4 % 2 != 0) {
            var7.setBackground(this.alternateColor);
         }

         this.rendererPane.paintComponent(var2, var7, this.table, var3.x, var3.y, var3.width, var3.height, true);
      }

   }

   public void propertyChange(PropertyChangeEvent var1) {
      if (SynthLookAndFeel.shouldUpdateStyle(var1)) {
         this.updateStyle((JTable)var1.getSource());
      }

   }

   private class SynthTableCellRenderer extends DefaultTableCellRenderer {
      private Object numberFormat;
      private Object dateFormat;
      private boolean opaque;

      private SynthTableCellRenderer() {
      }

      public void setOpaque(boolean var1) {
         this.opaque = var1;
      }

      public boolean isOpaque() {
         return this.opaque;
      }

      public String getName() {
         String var1 = super.getName();
         return var1 == null ? "Table.cellRenderer" : var1;
      }

      public void setBorder(Border var1) {
         if (SynthTableUI.this.useUIBorder || var1 instanceof SynthBorder) {
            super.setBorder(var1);
         }

      }

      public Component getTableCellRendererComponent(JTable var1, Object var2, boolean var3, boolean var4, int var5, int var6) {
         if (SynthTableUI.this.useTableColors || !var3 && !var4) {
            SynthLookAndFeel.resetSelectedUI();
         } else {
            SynthLookAndFeel.setSelectedUI((SynthLabelUI)SynthLookAndFeel.getUIOfType(this.getUI(), SynthLabelUI.class), var3, var4, var1.isEnabled(), false);
         }

         super.getTableCellRendererComponent(var1, var2, var3, var4, var5, var6);
         this.setIcon((Icon)null);
         if (var1 != null) {
            this.configureValue(var2, var1.getColumnClass(var6));
         }

         return this;
      }

      private void configureValue(Object var1, Class var2) {
         if (var2 != Object.class && var2 != null) {
            if (var2 != Float.class && var2 != Double.class) {
               if (var2 == Number.class) {
                  this.setHorizontalAlignment(11);
               } else if (var2 != Icon.class && var2 != ImageIcon.class) {
                  if (var2 == Date.class) {
                     if (this.dateFormat == null) {
                        this.dateFormat = DateFormat.getDateInstance();
                     }

                     this.setHorizontalAlignment(10);
                     this.setText(var1 == null ? "" : ((Format)this.dateFormat).format(var1));
                  } else {
                     this.configureValue(var1, var2.getSuperclass());
                  }
               } else {
                  this.setHorizontalAlignment(0);
                  this.setIcon(var1 instanceof Icon ? (Icon)var1 : null);
                  this.setText("");
               }
            } else {
               if (this.numberFormat == null) {
                  this.numberFormat = NumberFormat.getInstance();
               }

               this.setHorizontalAlignment(11);
               this.setText(var1 == null ? "" : ((NumberFormat)this.numberFormat).format(var1));
            }
         } else {
            this.setHorizontalAlignment(10);
         }

      }

      public void paint(Graphics var1) {
         super.paint(var1);
         SynthLookAndFeel.resetSelectedUI();
      }

      // $FF: synthetic method
      SynthTableCellRenderer(Object var2) {
         this();
      }
   }

   private class SynthBooleanTableCellRenderer extends JCheckBox implements TableCellRenderer {
      private boolean isRowSelected;

      public SynthBooleanTableCellRenderer() {
         this.setHorizontalAlignment(0);
         this.setName("Table.cellRenderer");
      }

      public Component getTableCellRendererComponent(JTable var1, Object var2, boolean var3, boolean var4, int var5, int var6) {
         this.isRowSelected = var3;
         if (var3) {
            this.setForeground(this.unwrap(var1.getSelectionForeground()));
            this.setBackground(this.unwrap(var1.getSelectionBackground()));
         } else {
            this.setForeground(this.unwrap(var1.getForeground()));
            this.setBackground(this.unwrap(var1.getBackground()));
         }

         this.setSelected(var2 != null && (Boolean)var2);
         return this;
      }

      private Color unwrap(Color var1) {
         return var1 instanceof UIResource ? new Color(var1.getRGB()) : var1;
      }

      public boolean isOpaque() {
         return this.isRowSelected ? true : super.isOpaque();
      }
   }
}
