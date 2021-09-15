package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import javax.swing.ActionMap;
import javax.swing.CellRendererPane;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.LookAndFeel;
import javax.swing.RowSorter;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TableHeaderUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class BasicTableHeaderUI extends TableHeaderUI {
   private static Cursor resizeCursor = Cursor.getPredefinedCursor(11);
   protected JTableHeader header;
   protected CellRendererPane rendererPane;
   protected MouseInputListener mouseInputListener;
   private int rolloverColumn = -1;
   private int selectedColumnIndex = 0;
   private static FocusListener focusListener = new FocusListener() {
      public void focusGained(FocusEvent var1) {
         this.repaintHeader(var1.getSource());
      }

      public void focusLost(FocusEvent var1) {
         this.repaintHeader(var1.getSource());
      }

      private void repaintHeader(Object var1) {
         if (var1 instanceof JTableHeader) {
            JTableHeader var2 = (JTableHeader)var1;
            BasicTableHeaderUI var3 = (BasicTableHeaderUI)BasicLookAndFeel.getUIOfType(var2.getUI(), BasicTableHeaderUI.class);
            if (var3 == null) {
               return;
            }

            var2.repaint(var2.getHeaderRect(var3.getSelectedColumnIndex()));
         }

      }
   };

   protected MouseInputListener createMouseInputListener() {
      return new BasicTableHeaderUI.MouseInputHandler();
   }

   public static ComponentUI createUI(JComponent var0) {
      return new BasicTableHeaderUI();
   }

   public void installUI(JComponent var1) {
      this.header = (JTableHeader)var1;
      this.rendererPane = new CellRendererPane();
      this.header.add(this.rendererPane);
      this.installDefaults();
      this.installListeners();
      this.installKeyboardActions();
   }

   protected void installDefaults() {
      LookAndFeel.installColorsAndFont(this.header, "TableHeader.background", "TableHeader.foreground", "TableHeader.font");
      LookAndFeel.installProperty(this.header, "opaque", Boolean.TRUE);
   }

   protected void installListeners() {
      this.mouseInputListener = this.createMouseInputListener();
      this.header.addMouseListener(this.mouseInputListener);
      this.header.addMouseMotionListener(this.mouseInputListener);
      this.header.addFocusListener(focusListener);
   }

   protected void installKeyboardActions() {
      InputMap var1 = (InputMap)DefaultLookup.get(this.header, this, "TableHeader.ancestorInputMap");
      SwingUtilities.replaceUIInputMap(this.header, 1, var1);
      LazyActionMap.installLazyActionMap(this.header, BasicTableHeaderUI.class, "TableHeader.actionMap");
   }

   public void uninstallUI(JComponent var1) {
      this.uninstallDefaults();
      this.uninstallListeners();
      this.uninstallKeyboardActions();
      this.header.remove(this.rendererPane);
      this.rendererPane = null;
      this.header = null;
   }

   protected void uninstallDefaults() {
   }

   protected void uninstallListeners() {
      this.header.removeMouseListener(this.mouseInputListener);
      this.header.removeMouseMotionListener(this.mouseInputListener);
      this.mouseInputListener = null;
   }

   protected void uninstallKeyboardActions() {
      SwingUtilities.replaceUIInputMap(this.header, 0, (InputMap)null);
      SwingUtilities.replaceUIActionMap(this.header, (ActionMap)null);
   }

   static void loadActionMap(LazyActionMap var0) {
      var0.put(new BasicTableHeaderUI.Actions("toggleSortOrder"));
      var0.put(new BasicTableHeaderUI.Actions("selectColumnToLeft"));
      var0.put(new BasicTableHeaderUI.Actions("selectColumnToRight"));
      var0.put(new BasicTableHeaderUI.Actions("moveColumnLeft"));
      var0.put(new BasicTableHeaderUI.Actions("moveColumnRight"));
      var0.put(new BasicTableHeaderUI.Actions("resizeLeft"));
      var0.put(new BasicTableHeaderUI.Actions("resizeRight"));
      var0.put(new BasicTableHeaderUI.Actions("focusTable"));
   }

   protected int getRolloverColumn() {
      return this.rolloverColumn;
   }

   protected void rolloverColumnUpdated(int var1, int var2) {
   }

   private void updateRolloverColumn(MouseEvent var1) {
      if (this.header.getDraggedColumn() == null && this.header.contains(var1.getPoint())) {
         int var2 = this.header.columnAtPoint(var1.getPoint());
         if (var2 != this.rolloverColumn) {
            int var3 = this.rolloverColumn;
            this.rolloverColumn = var2;
            this.rolloverColumnUpdated(var3, this.rolloverColumn);
         }
      }

   }

   private int selectNextColumn(boolean var1) {
      int var2 = this.getSelectedColumnIndex();
      if (var2 < this.header.getColumnModel().getColumnCount() - 1) {
         ++var2;
         if (var1) {
            this.selectColumn(var2);
         }
      }

      return var2;
   }

   private int selectPreviousColumn(boolean var1) {
      int var2 = this.getSelectedColumnIndex();
      if (var2 > 0) {
         --var2;
         if (var1) {
            this.selectColumn(var2);
         }
      }

      return var2;
   }

   void selectColumn(int var1) {
      this.selectColumn(var1, true);
   }

   void selectColumn(int var1, boolean var2) {
      Rectangle var3 = this.header.getHeaderRect(this.selectedColumnIndex);
      this.header.repaint(var3);
      this.selectedColumnIndex = var1;
      var3 = this.header.getHeaderRect(var1);
      this.header.repaint(var3);
      if (var2) {
         this.scrollToColumn(var1);
      }

   }

   private void scrollToColumn(int var1) {
      Container var2;
      JTable var3;
      if (this.header.getParent() != null && (var2 = this.header.getParent().getParent()) != null && var2 instanceof JScrollPane && (var3 = this.header.getTable()) != null) {
         Rectangle var4 = var3.getVisibleRect();
         Rectangle var5 = var3.getCellRect(0, var1, true);
         var4.x = var5.x;
         var4.width = var5.width;
         var3.scrollRectToVisible(var4);
      }
   }

   private int getSelectedColumnIndex() {
      int var1 = this.header.getColumnModel().getColumnCount();
      if (this.selectedColumnIndex >= var1 && var1 > 0) {
         this.selectedColumnIndex = var1 - 1;
      }

      return this.selectedColumnIndex;
   }

   private static boolean canResize(TableColumn var0, JTableHeader var1) {
      return var0 != null && var1.getResizingAllowed() && var0.getResizable();
   }

   private int changeColumnWidth(TableColumn var1, JTableHeader var2, int var3, int var4) {
      var1.setWidth(var4);
      Container var5;
      JTable var6;
      if (var2.getParent() != null && (var5 = var2.getParent().getParent()) != null && var5 instanceof JScrollPane && (var6 = var2.getTable()) != null) {
         if (!var5.getComponentOrientation().isLeftToRight() && !var2.getComponentOrientation().isLeftToRight()) {
            JViewport var7 = ((JScrollPane)var5).getViewport();
            int var8 = var7.getWidth();
            int var9 = var4 - var3;
            int var10 = var6.getWidth() + var9;
            Dimension var11 = var6.getSize();
            var11.width += var9;
            var6.setSize(var11);
            if (var10 >= var8 && var6.getAutoResizeMode() == 0) {
               Point var12 = var7.getViewPosition();
               var12.x = Math.max(0, Math.min(var10 - var8, var12.x + var9));
               var7.setViewPosition(var12);
               return var9;
            }
         }

         return 0;
      } else {
         return 0;
      }
   }

   public int getBaseline(JComponent var1, int var2, int var3) {
      super.getBaseline(var1, var2, var3);
      int var4 = -1;
      TableColumnModel var5 = this.header.getColumnModel();

      for(int var6 = 0; var6 < var5.getColumnCount(); ++var6) {
         var5.getColumn(var6);
         Component var8 = this.getHeaderRenderer(var6);
         Dimension var9 = var8.getPreferredSize();
         int var10 = var8.getBaseline(var9.width, var3);
         if (var10 >= 0) {
            if (var4 == -1) {
               var4 = var10;
            } else if (var4 != var10) {
               var4 = -1;
               break;
            }
         }
      }

      return var4;
   }

   public void paint(Graphics var1, JComponent var2) {
      if (this.header.getColumnModel().getColumnCount() > 0) {
         boolean var3 = this.header.getComponentOrientation().isLeftToRight();
         Rectangle var4 = var1.getClipBounds();
         Point var5 = var4.getLocation();
         Point var6 = new Point(var4.x + var4.width - 1, var4.y);
         TableColumnModel var7 = this.header.getColumnModel();
         int var8 = this.header.columnAtPoint(var3 ? var5 : var6);
         int var9 = this.header.columnAtPoint(var3 ? var6 : var5);
         if (var8 == -1) {
            var8 = 0;
         }

         if (var9 == -1) {
            var9 = var7.getColumnCount() - 1;
         }

         TableColumn var10 = this.header.getDraggedColumn();
         Rectangle var12 = this.header.getHeaderRect(var3 ? var8 : var9);
         int var11;
         TableColumn var13;
         int var14;
         if (var3) {
            for(var14 = var8; var14 <= var9; ++var14) {
               var13 = var7.getColumn(var14);
               var11 = var13.getWidth();
               var12.width = var11;
               if (var13 != var10) {
                  this.paintCell(var1, var12, var14);
               }

               var12.x += var11;
            }
         } else {
            for(var14 = var9; var14 >= var8; --var14) {
               var13 = var7.getColumn(var14);
               var11 = var13.getWidth();
               var12.width = var11;
               if (var13 != var10) {
                  this.paintCell(var1, var12, var14);
               }

               var12.x += var11;
            }
         }

         if (var10 != null) {
            var14 = this.viewIndexForColumn(var10);
            Rectangle var15 = this.header.getHeaderRect(var14);
            var1.setColor(this.header.getParent().getBackground());
            var1.fillRect(var15.x, var15.y, var15.width, var15.height);
            var15.x += this.header.getDraggedDistance();
            var1.setColor(this.header.getBackground());
            var1.fillRect(var15.x, var15.y, var15.width, var15.height);
            this.paintCell(var1, var15, var14);
         }

         this.rendererPane.removeAll();
      }
   }

   private Component getHeaderRenderer(int var1) {
      TableColumn var2 = this.header.getColumnModel().getColumn(var1);
      TableCellRenderer var3 = var2.getHeaderRenderer();
      if (var3 == null) {
         var3 = this.header.getDefaultRenderer();
      }

      boolean var4 = !this.header.isPaintingForPrint() && var1 == this.getSelectedColumnIndex() && this.header.hasFocus();
      return var3.getTableCellRendererComponent(this.header.getTable(), var2.getHeaderValue(), false, var4, -1, var1);
   }

   private void paintCell(Graphics var1, Rectangle var2, int var3) {
      Component var4 = this.getHeaderRenderer(var3);
      this.rendererPane.paintComponent(var1, var4, this.header, var2.x, var2.y, var2.width, var2.height, true);
   }

   private int viewIndexForColumn(TableColumn var1) {
      TableColumnModel var2 = this.header.getColumnModel();

      for(int var3 = 0; var3 < var2.getColumnCount(); ++var3) {
         if (var2.getColumn(var3) == var1) {
            return var3;
         }
      }

      return -1;
   }

   private int getHeaderHeight() {
      int var1 = 0;
      boolean var2 = false;
      TableColumnModel var3 = this.header.getColumnModel();

      for(int var4 = 0; var4 < var3.getColumnCount(); ++var4) {
         TableColumn var5 = var3.getColumn(var4);
         boolean var6 = var5.getHeaderRenderer() == null;
         if (!var6 || !var2) {
            Component var7 = this.getHeaderRenderer(var4);
            int var8 = var7.getPreferredSize().height;
            var1 = Math.max(var1, var8);
            if (var6 && var8 > 0) {
               Object var9 = var5.getHeaderValue();
               if (var9 != null) {
                  String var10 = var9.toString();
                  if (var10 != null && !var10.equals("")) {
                     var2 = true;
                  }
               }
            }
         }
      }

      return var1;
   }

   private Dimension createHeaderSize(long var1) {
      if (var1 > 2147483647L) {
         var1 = 2147483647L;
      }

      return new Dimension((int)var1, this.getHeaderHeight());
   }

   public Dimension getMinimumSize(JComponent var1) {
      long var2 = 0L;

      TableColumn var5;
      for(Enumeration var4 = this.header.getColumnModel().getColumns(); var4.hasMoreElements(); var2 += (long)var5.getMinWidth()) {
         var5 = (TableColumn)var4.nextElement();
      }

      return this.createHeaderSize(var2);
   }

   public Dimension getPreferredSize(JComponent var1) {
      long var2 = 0L;

      TableColumn var5;
      for(Enumeration var4 = this.header.getColumnModel().getColumns(); var4.hasMoreElements(); var2 += (long)var5.getPreferredWidth()) {
         var5 = (TableColumn)var4.nextElement();
      }

      return this.createHeaderSize(var2);
   }

   public Dimension getMaximumSize(JComponent var1) {
      long var2 = 0L;

      TableColumn var5;
      for(Enumeration var4 = this.header.getColumnModel().getColumns(); var4.hasMoreElements(); var2 += (long)var5.getMaxWidth()) {
         var5 = (TableColumn)var4.nextElement();
      }

      return this.createHeaderSize(var2);
   }

   private static class Actions extends UIAction {
      public static final String TOGGLE_SORT_ORDER = "toggleSortOrder";
      public static final String SELECT_COLUMN_TO_LEFT = "selectColumnToLeft";
      public static final String SELECT_COLUMN_TO_RIGHT = "selectColumnToRight";
      public static final String MOVE_COLUMN_LEFT = "moveColumnLeft";
      public static final String MOVE_COLUMN_RIGHT = "moveColumnRight";
      public static final String RESIZE_LEFT = "resizeLeft";
      public static final String RESIZE_RIGHT = "resizeRight";
      public static final String FOCUS_TABLE = "focusTable";

      public Actions(String var1) {
         super(var1);
      }

      public boolean isEnabled(Object var1) {
         if (var1 instanceof JTableHeader) {
            JTableHeader var2 = (JTableHeader)var1;
            TableColumnModel var3 = var2.getColumnModel();
            if (var3.getColumnCount() <= 0) {
               return false;
            }

            String var4 = this.getName();
            BasicTableHeaderUI var5 = (BasicTableHeaderUI)BasicLookAndFeel.getUIOfType(var2.getUI(), BasicTableHeaderUI.class);
            if (var5 != null) {
               if (var4 == "moveColumnLeft") {
                  return var2.getReorderingAllowed() && this.maybeMoveColumn(true, var2, var5, false);
               }

               if (var4 == "moveColumnRight") {
                  return var2.getReorderingAllowed() && this.maybeMoveColumn(false, var2, var5, false);
               }

               if (var4 == "resizeLeft" || var4 == "resizeRight") {
                  return BasicTableHeaderUI.canResize(var3.getColumn(var5.getSelectedColumnIndex()), var2);
               }

               if (var4 == "focusTable") {
                  return var2.getTable() != null;
               }
            }
         }

         return true;
      }

      public void actionPerformed(ActionEvent var1) {
         JTableHeader var2 = (JTableHeader)var1.getSource();
         BasicTableHeaderUI var3 = (BasicTableHeaderUI)BasicLookAndFeel.getUIOfType(var2.getUI(), BasicTableHeaderUI.class);
         if (var3 != null) {
            String var4 = this.getName();
            JTable var5;
            if ("toggleSortOrder" == var4) {
               var5 = var2.getTable();
               RowSorter var6 = var5 == null ? null : var5.getRowSorter();
               if (var6 != null) {
                  int var7 = var3.getSelectedColumnIndex();
                  var7 = var5.convertColumnIndexToModel(var7);
                  var6.toggleSortOrder(var7);
               }
            } else if ("selectColumnToLeft" == var4) {
               if (var2.getComponentOrientation().isLeftToRight()) {
                  var3.selectPreviousColumn(true);
               } else {
                  var3.selectNextColumn(true);
               }
            } else if ("selectColumnToRight" == var4) {
               if (var2.getComponentOrientation().isLeftToRight()) {
                  var3.selectNextColumn(true);
               } else {
                  var3.selectPreviousColumn(true);
               }
            } else if ("moveColumnLeft" == var4) {
               this.moveColumn(true, var2, var3);
            } else if ("moveColumnRight" == var4) {
               this.moveColumn(false, var2, var3);
            } else if ("resizeLeft" == var4) {
               this.resize(true, var2, var3);
            } else if ("resizeRight" == var4) {
               this.resize(false, var2, var3);
            } else if ("focusTable" == var4) {
               var5 = var2.getTable();
               if (var5 != null) {
                  var5.requestFocusInWindow();
               }
            }

         }
      }

      private void moveColumn(boolean var1, JTableHeader var2, BasicTableHeaderUI var3) {
         this.maybeMoveColumn(var1, var2, var3, true);
      }

      private boolean maybeMoveColumn(boolean var1, JTableHeader var2, BasicTableHeaderUI var3, boolean var4) {
         int var5 = var3.getSelectedColumnIndex();
         int var6;
         if (var2.getComponentOrientation().isLeftToRight()) {
            var6 = var1 ? var3.selectPreviousColumn(var4) : var3.selectNextColumn(var4);
         } else {
            var6 = var1 ? var3.selectNextColumn(var4) : var3.selectPreviousColumn(var4);
         }

         if (var6 != var5) {
            if (!var4) {
               return true;
            }

            var2.getColumnModel().moveColumn(var5, var6);
         }

         return false;
      }

      private void resize(boolean var1, JTableHeader var2, BasicTableHeaderUI var3) {
         int var4 = var3.getSelectedColumnIndex();
         TableColumn var5 = var2.getColumnModel().getColumn(var4);
         var2.setResizingColumn(var5);
         int var6 = var5.getWidth();
         int var7;
         if (var2.getComponentOrientation().isLeftToRight()) {
            var7 = var6 + (var1 ? -1 : 1);
         } else {
            var7 = var6 + (var1 ? 1 : -1);
         }

         var3.changeColumnWidth(var5, var2, var6, var7);
      }
   }

   public class MouseInputHandler implements MouseInputListener {
      private int mouseXOffset;
      private Cursor otherCursor;

      public MouseInputHandler() {
         this.otherCursor = BasicTableHeaderUI.resizeCursor;
      }

      public void mouseClicked(MouseEvent var1) {
         if (BasicTableHeaderUI.this.header.isEnabled()) {
            if (var1.getClickCount() % 2 == 1 && SwingUtilities.isLeftMouseButton(var1)) {
               JTable var2 = BasicTableHeaderUI.this.header.getTable();
               RowSorter var3;
               if (var2 != null && (var3 = var2.getRowSorter()) != null) {
                  int var4 = BasicTableHeaderUI.this.header.columnAtPoint(var1.getPoint());
                  if (var4 != -1) {
                     var4 = var2.convertColumnIndexToModel(var4);
                     var3.toggleSortOrder(var4);
                  }
               }
            }

         }
      }

      private TableColumn getResizingColumn(Point var1) {
         return this.getResizingColumn(var1, BasicTableHeaderUI.this.header.columnAtPoint(var1));
      }

      private TableColumn getResizingColumn(Point var1, int var2) {
         if (var2 == -1) {
            return null;
         } else {
            Rectangle var3 = BasicTableHeaderUI.this.header.getHeaderRect(var2);
            var3.grow(-3, 0);
            if (var3.contains(var1)) {
               return null;
            } else {
               int var4 = var3.x + var3.width / 2;
               int var5;
               if (BasicTableHeaderUI.this.header.getComponentOrientation().isLeftToRight()) {
                  var5 = var1.x < var4 ? var2 - 1 : var2;
               } else {
                  var5 = var1.x < var4 ? var2 : var2 - 1;
               }

               return var5 == -1 ? null : BasicTableHeaderUI.this.header.getColumnModel().getColumn(var5);
            }
         }
      }

      public void mousePressed(MouseEvent var1) {
         if (BasicTableHeaderUI.this.header.isEnabled()) {
            BasicTableHeaderUI.this.header.setDraggedColumn((TableColumn)null);
            BasicTableHeaderUI.this.header.setResizingColumn((TableColumn)null);
            BasicTableHeaderUI.this.header.setDraggedDistance(0);
            Point var2 = var1.getPoint();
            TableColumnModel var3 = BasicTableHeaderUI.this.header.getColumnModel();
            int var4 = BasicTableHeaderUI.this.header.columnAtPoint(var2);
            if (var4 != -1) {
               TableColumn var5 = this.getResizingColumn(var2, var4);
               if (BasicTableHeaderUI.canResize(var5, BasicTableHeaderUI.this.header)) {
                  BasicTableHeaderUI.this.header.setResizingColumn(var5);
                  if (BasicTableHeaderUI.this.header.getComponentOrientation().isLeftToRight()) {
                     this.mouseXOffset = var2.x - var5.getWidth();
                  } else {
                     this.mouseXOffset = var2.x + var5.getWidth();
                  }
               } else if (BasicTableHeaderUI.this.header.getReorderingAllowed()) {
                  TableColumn var6 = var3.getColumn(var4);
                  BasicTableHeaderUI.this.header.setDraggedColumn(var6);
                  this.mouseXOffset = var2.x;
               }
            }

            if (BasicTableHeaderUI.this.header.getReorderingAllowed()) {
               int var7 = BasicTableHeaderUI.this.rolloverColumn;
               BasicTableHeaderUI.this.rolloverColumn = -1;
               BasicTableHeaderUI.this.rolloverColumnUpdated(var7, BasicTableHeaderUI.this.rolloverColumn);
            }

         }
      }

      private void swapCursor() {
         Cursor var1 = BasicTableHeaderUI.this.header.getCursor();
         BasicTableHeaderUI.this.header.setCursor(this.otherCursor);
         this.otherCursor = var1;
      }

      public void mouseMoved(MouseEvent var1) {
         if (BasicTableHeaderUI.this.header.isEnabled()) {
            if (BasicTableHeaderUI.canResize(this.getResizingColumn(var1.getPoint()), BasicTableHeaderUI.this.header) != (BasicTableHeaderUI.this.header.getCursor() == BasicTableHeaderUI.resizeCursor)) {
               this.swapCursor();
            }

            BasicTableHeaderUI.this.updateRolloverColumn(var1);
         }
      }

      public void mouseDragged(MouseEvent var1) {
         if (BasicTableHeaderUI.this.header.isEnabled()) {
            int var2 = var1.getX();
            TableColumn var3 = BasicTableHeaderUI.this.header.getResizingColumn();
            TableColumn var4 = BasicTableHeaderUI.this.header.getDraggedColumn();
            boolean var5 = BasicTableHeaderUI.this.header.getComponentOrientation().isLeftToRight();
            int var7;
            if (var3 != null) {
               int var6 = var3.getWidth();
               if (var5) {
                  var7 = var2 - this.mouseXOffset;
               } else {
                  var7 = this.mouseXOffset - var2;
               }

               this.mouseXOffset += BasicTableHeaderUI.this.changeColumnWidth(var3, BasicTableHeaderUI.this.header, var6, var7);
            } else if (var4 != null) {
               TableColumnModel var13 = BasicTableHeaderUI.this.header.getColumnModel();
               var7 = var2 - this.mouseXOffset;
               int var8 = var7 < 0 ? -1 : 1;
               int var9 = BasicTableHeaderUI.this.viewIndexForColumn(var4);
               int var10 = var9 + (var5 ? var8 : -var8);
               if (0 <= var10 && var10 < var13.getColumnCount()) {
                  int var11 = var13.getColumn(var10).getWidth();
                  if (Math.abs(var7) > var11 / 2) {
                     this.mouseXOffset += var8 * var11;
                     BasicTableHeaderUI.this.header.setDraggedDistance(var7 - var8 * var11);
                     int var12 = SwingUtilities2.convertColumnIndexToModel(BasicTableHeaderUI.this.header.getColumnModel(), BasicTableHeaderUI.this.getSelectedColumnIndex());
                     var13.moveColumn(var9, var10);
                     BasicTableHeaderUI.this.selectColumn(SwingUtilities2.convertColumnIndexToView(BasicTableHeaderUI.this.header.getColumnModel(), var12), false);
                     return;
                  }
               }

               this.setDraggedDistance(var7, var9);
            }

            BasicTableHeaderUI.this.updateRolloverColumn(var1);
         }
      }

      public void mouseReleased(MouseEvent var1) {
         if (BasicTableHeaderUI.this.header.isEnabled()) {
            this.setDraggedDistance(0, BasicTableHeaderUI.this.viewIndexForColumn(BasicTableHeaderUI.this.header.getDraggedColumn()));
            BasicTableHeaderUI.this.header.setResizingColumn((TableColumn)null);
            BasicTableHeaderUI.this.header.setDraggedColumn((TableColumn)null);
            BasicTableHeaderUI.this.updateRolloverColumn(var1);
         }
      }

      public void mouseEntered(MouseEvent var1) {
         if (BasicTableHeaderUI.this.header.isEnabled()) {
            BasicTableHeaderUI.this.updateRolloverColumn(var1);
         }
      }

      public void mouseExited(MouseEvent var1) {
         if (BasicTableHeaderUI.this.header.isEnabled()) {
            int var2 = BasicTableHeaderUI.this.rolloverColumn;
            BasicTableHeaderUI.this.rolloverColumn = -1;
            BasicTableHeaderUI.this.rolloverColumnUpdated(var2, BasicTableHeaderUI.this.rolloverColumn);
         }
      }

      private void setDraggedDistance(int var1, int var2) {
         BasicTableHeaderUI.this.header.setDraggedDistance(var1);
         if (var2 != -1) {
            BasicTableHeaderUI.this.header.getColumnModel().moveColumn(var2, var2);
         }

      }
   }
}
