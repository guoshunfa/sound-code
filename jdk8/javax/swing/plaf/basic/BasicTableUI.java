package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.EventObject;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.CellRendererPane;
import javax.swing.DefaultListSelectionModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.TransferHandler;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TableHeaderUI;
import javax.swing.plaf.TableUI;
import javax.swing.plaf.UIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class BasicTableUI extends TableUI {
   private static final StringBuilder BASELINE_COMPONENT_KEY = new StringBuilder("Table.baselineComponent");
   protected JTable table;
   protected CellRendererPane rendererPane;
   protected KeyListener keyListener;
   protected FocusListener focusListener;
   protected MouseInputListener mouseInputListener;
   private BasicTableUI.Handler handler;
   private boolean isFileList = false;
   private static final TransferHandler defaultTransferHandler = new BasicTableUI.TableTransferHandler();

   private boolean pointOutsidePrefSize(int var1, int var2, Point var3) {
      return !this.isFileList ? false : SwingUtilities2.pointOutsidePrefSize(this.table, var1, var2, var3);
   }

   private BasicTableUI.Handler getHandler() {
      if (this.handler == null) {
         this.handler = new BasicTableUI.Handler();
      }

      return this.handler;
   }

   protected KeyListener createKeyListener() {
      return null;
   }

   protected FocusListener createFocusListener() {
      return this.getHandler();
   }

   protected MouseInputListener createMouseInputListener() {
      return this.getHandler();
   }

   public static ComponentUI createUI(JComponent var0) {
      return new BasicTableUI();
   }

   public void installUI(JComponent var1) {
      this.table = (JTable)var1;
      this.rendererPane = new CellRendererPane();
      this.table.add(this.rendererPane);
      this.installDefaults();
      this.installDefaults2();
      this.installListeners();
      this.installKeyboardActions();
   }

   protected void installDefaults() {
      LookAndFeel.installColorsAndFont(this.table, "Table.background", "Table.foreground", "Table.font");
      LookAndFeel.installProperty(this.table, "opaque", Boolean.TRUE);
      Color var1 = this.table.getSelectionBackground();
      if (var1 == null || var1 instanceof UIResource) {
         var1 = UIManager.getColor("Table.selectionBackground");
         this.table.setSelectionBackground(var1 != null ? var1 : UIManager.getColor("textHighlight"));
      }

      Color var2 = this.table.getSelectionForeground();
      if (var2 == null || var2 instanceof UIResource) {
         var2 = UIManager.getColor("Table.selectionForeground");
         this.table.setSelectionForeground(var2 != null ? var2 : UIManager.getColor("textHighlightText"));
      }

      Color var3 = this.table.getGridColor();
      if (var3 == null || var3 instanceof UIResource) {
         var3 = UIManager.getColor("Table.gridColor");
         this.table.setGridColor(var3 != null ? var3 : Color.GRAY);
      }

      Container var4 = SwingUtilities.getUnwrappedParent(this.table);
      if (var4 != null) {
         var4 = var4.getParent();
         if (var4 != null && var4 instanceof JScrollPane) {
            LookAndFeel.installBorder((JScrollPane)var4, "Table.scrollPaneBorder");
         }
      }

      this.isFileList = Boolean.TRUE.equals(this.table.getClientProperty("Table.isFileList"));
   }

   private void installDefaults2() {
      TransferHandler var1 = this.table.getTransferHandler();
      if (var1 == null || var1 instanceof UIResource) {
         this.table.setTransferHandler(defaultTransferHandler);
         if (this.table.getDropTarget() instanceof UIResource) {
            this.table.setDropTarget((DropTarget)null);
         }
      }

   }

   protected void installListeners() {
      this.focusListener = this.createFocusListener();
      this.keyListener = this.createKeyListener();
      this.mouseInputListener = this.createMouseInputListener();
      this.table.addFocusListener(this.focusListener);
      this.table.addKeyListener(this.keyListener);
      this.table.addMouseListener(this.mouseInputListener);
      this.table.addMouseMotionListener(this.mouseInputListener);
      this.table.addPropertyChangeListener(this.getHandler());
      if (this.isFileList) {
         this.table.getSelectionModel().addListSelectionListener(this.getHandler());
      }

   }

   protected void installKeyboardActions() {
      LazyActionMap.installLazyActionMap(this.table, BasicTableUI.class, "Table.actionMap");
      InputMap var1 = this.getInputMap(1);
      SwingUtilities.replaceUIInputMap(this.table, 1, var1);
   }

   InputMap getInputMap(int var1) {
      if (var1 == 1) {
         InputMap var2 = (InputMap)DefaultLookup.get(this.table, this, "Table.ancestorInputMap");
         InputMap var3;
         if (!this.table.getComponentOrientation().isLeftToRight() && (var3 = (InputMap)DefaultLookup.get(this.table, this, "Table.ancestorInputMap.RightToLeft")) != null) {
            var3.setParent(var2);
            return var3;
         } else {
            return var2;
         }
      } else {
         return null;
      }
   }

   static void loadActionMap(LazyActionMap var0) {
      var0.put(new BasicTableUI.Actions("selectNextColumn", 1, 0, false, false));
      var0.put(new BasicTableUI.Actions("selectNextColumnChangeLead", 1, 0, false, false));
      var0.put(new BasicTableUI.Actions("selectPreviousColumn", -1, 0, false, false));
      var0.put(new BasicTableUI.Actions("selectPreviousColumnChangeLead", -1, 0, false, false));
      var0.put(new BasicTableUI.Actions("selectNextRow", 0, 1, false, false));
      var0.put(new BasicTableUI.Actions("selectNextRowChangeLead", 0, 1, false, false));
      var0.put(new BasicTableUI.Actions("selectPreviousRow", 0, -1, false, false));
      var0.put(new BasicTableUI.Actions("selectPreviousRowChangeLead", 0, -1, false, false));
      var0.put(new BasicTableUI.Actions("selectNextColumnExtendSelection", 1, 0, true, false));
      var0.put(new BasicTableUI.Actions("selectPreviousColumnExtendSelection", -1, 0, true, false));
      var0.put(new BasicTableUI.Actions("selectNextRowExtendSelection", 0, 1, true, false));
      var0.put(new BasicTableUI.Actions("selectPreviousRowExtendSelection", 0, -1, true, false));
      var0.put(new BasicTableUI.Actions("scrollUpChangeSelection", false, false, true, false));
      var0.put(new BasicTableUI.Actions("scrollDownChangeSelection", false, true, true, false));
      var0.put(new BasicTableUI.Actions("selectFirstColumn", false, false, false, true));
      var0.put(new BasicTableUI.Actions("selectLastColumn", false, true, false, true));
      var0.put(new BasicTableUI.Actions("scrollUpExtendSelection", true, false, true, false));
      var0.put(new BasicTableUI.Actions("scrollDownExtendSelection", true, true, true, false));
      var0.put(new BasicTableUI.Actions("selectFirstColumnExtendSelection", true, false, false, true));
      var0.put(new BasicTableUI.Actions("selectLastColumnExtendSelection", true, true, false, true));
      var0.put(new BasicTableUI.Actions("selectFirstRow", false, false, true, true));
      var0.put(new BasicTableUI.Actions("selectLastRow", false, true, true, true));
      var0.put(new BasicTableUI.Actions("selectFirstRowExtendSelection", true, false, true, true));
      var0.put(new BasicTableUI.Actions("selectLastRowExtendSelection", true, true, true, true));
      var0.put(new BasicTableUI.Actions("selectNextColumnCell", 1, 0, false, true));
      var0.put(new BasicTableUI.Actions("selectPreviousColumnCell", -1, 0, false, true));
      var0.put(new BasicTableUI.Actions("selectNextRowCell", 0, 1, false, true));
      var0.put(new BasicTableUI.Actions("selectPreviousRowCell", 0, -1, false, true));
      var0.put(new BasicTableUI.Actions("selectAll"));
      var0.put(new BasicTableUI.Actions("clearSelection"));
      var0.put(new BasicTableUI.Actions("cancel"));
      var0.put(new BasicTableUI.Actions("startEditing"));
      var0.put(TransferHandler.getCutAction().getValue("Name"), TransferHandler.getCutAction());
      var0.put(TransferHandler.getCopyAction().getValue("Name"), TransferHandler.getCopyAction());
      var0.put(TransferHandler.getPasteAction().getValue("Name"), TransferHandler.getPasteAction());
      var0.put(new BasicTableUI.Actions("scrollLeftChangeSelection", false, false, false, false));
      var0.put(new BasicTableUI.Actions("scrollRightChangeSelection", false, true, false, false));
      var0.put(new BasicTableUI.Actions("scrollLeftExtendSelection", true, false, false, false));
      var0.put(new BasicTableUI.Actions("scrollRightExtendSelection", true, true, false, false));
      var0.put(new BasicTableUI.Actions("addToSelection"));
      var0.put(new BasicTableUI.Actions("toggleAndAnchor"));
      var0.put(new BasicTableUI.Actions("extendTo"));
      var0.put(new BasicTableUI.Actions("moveSelectionTo"));
      var0.put(new BasicTableUI.Actions("focusHeader"));
   }

   public void uninstallUI(JComponent var1) {
      this.uninstallDefaults();
      this.uninstallListeners();
      this.uninstallKeyboardActions();
      this.table.remove(this.rendererPane);
      this.rendererPane = null;
      this.table = null;
   }

   protected void uninstallDefaults() {
      if (this.table.getTransferHandler() instanceof UIResource) {
         this.table.setTransferHandler((TransferHandler)null);
      }

   }

   protected void uninstallListeners() {
      this.table.removeFocusListener(this.focusListener);
      this.table.removeKeyListener(this.keyListener);
      this.table.removeMouseListener(this.mouseInputListener);
      this.table.removeMouseMotionListener(this.mouseInputListener);
      this.table.removePropertyChangeListener(this.getHandler());
      if (this.isFileList) {
         this.table.getSelectionModel().removeListSelectionListener(this.getHandler());
      }

      this.focusListener = null;
      this.keyListener = null;
      this.mouseInputListener = null;
      this.handler = null;
   }

   protected void uninstallKeyboardActions() {
      SwingUtilities.replaceUIInputMap(this.table, 1, (InputMap)null);
      SwingUtilities.replaceUIActionMap(this.table, (ActionMap)null);
   }

   public int getBaseline(JComponent var1, int var2, int var3) {
      super.getBaseline(var1, var2, var3);
      UIDefaults var4 = UIManager.getLookAndFeelDefaults();
      Component var5 = (Component)var4.get(BASELINE_COMPONENT_KEY);
      if (var5 == null) {
         DefaultTableCellRenderer var6 = new DefaultTableCellRenderer();
         var5 = var6.getTableCellRendererComponent(this.table, "a", false, false, -1, -1);
         var4.put(BASELINE_COMPONENT_KEY, var5);
      }

      var5.setFont(this.table.getFont());
      int var7 = this.table.getRowMargin();
      return var5.getBaseline(Integer.MAX_VALUE, this.table.getRowHeight() - var7) + var7 / 2;
   }

   public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent var1) {
      super.getBaselineResizeBehavior(var1);
      return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
   }

   private Dimension createTableSize(long var1) {
      int var3 = 0;
      int var4 = this.table.getRowCount();
      if (var4 > 0 && this.table.getColumnCount() > 0) {
         Rectangle var5 = this.table.getCellRect(var4 - 1, 0, true);
         var3 = var5.y + var5.height;
      }

      long var7 = Math.abs(var1);
      if (var7 > 2147483647L) {
         var7 = 2147483647L;
      }

      return new Dimension((int)var7, var3);
   }

   public Dimension getMinimumSize(JComponent var1) {
      long var2 = 0L;

      TableColumn var5;
      for(Enumeration var4 = this.table.getColumnModel().getColumns(); var4.hasMoreElements(); var2 += (long)var5.getMinWidth()) {
         var5 = (TableColumn)var4.nextElement();
      }

      return this.createTableSize(var2);
   }

   public Dimension getPreferredSize(JComponent var1) {
      long var2 = 0L;

      TableColumn var5;
      for(Enumeration var4 = this.table.getColumnModel().getColumns(); var4.hasMoreElements(); var2 += (long)var5.getPreferredWidth()) {
         var5 = (TableColumn)var4.nextElement();
      }

      return this.createTableSize(var2);
   }

   public Dimension getMaximumSize(JComponent var1) {
      long var2 = 0L;

      TableColumn var5;
      for(Enumeration var4 = this.table.getColumnModel().getColumns(); var4.hasMoreElements(); var2 += (long)var5.getMaxWidth()) {
         var5 = (TableColumn)var4.nextElement();
      }

      return this.createTableSize(var2);
   }

   public void paint(Graphics var1, JComponent var2) {
      Rectangle var3 = var1.getClipBounds();
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

         this.paintGrid(var1, var8, var9, var10, var11);
         this.paintCells(var1, var8, var9, var10, var11);
         this.paintDropLines(var1);
      } else {
         this.paintDropLines(var1);
      }
   }

   private void paintDropLines(Graphics var1) {
      JTable.DropLocation var2 = this.table.getDropLocation();
      if (var2 != null) {
         Color var3 = UIManager.getColor("Table.dropLineColor");
         Color var4 = UIManager.getColor("Table.dropLineShortColor");
         if (var3 != null || var4 != null) {
            Rectangle var5 = this.getHDropLineRect(var2);
            int var6;
            int var7;
            if (var5 != null) {
               var6 = var5.x;
               var7 = var5.width;
               if (var3 != null) {
                  this.extendRect(var5, true);
                  var1.setColor(var3);
                  var1.fillRect(var5.x, var5.y, var5.width, var5.height);
               }

               if (!var2.isInsertColumn() && var4 != null) {
                  var1.setColor(var4);
                  var1.fillRect(var6, var5.y, var7, var5.height);
               }
            }

            var5 = this.getVDropLineRect(var2);
            if (var5 != null) {
               var6 = var5.y;
               var7 = var5.height;
               if (var3 != null) {
                  this.extendRect(var5, false);
                  var1.setColor(var3);
                  var1.fillRect(var5.x, var5.y, var5.width, var5.height);
               }

               if (!var2.isInsertRow() && var4 != null) {
                  var1.setColor(var4);
                  var1.fillRect(var5.x, var6, var5.width, var7);
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

   private void paintGrid(Graphics var1, int var2, int var3, int var4, int var5) {
      var1.setColor(this.table.getGridColor());
      Rectangle var6 = this.table.getCellRect(var2, var4, true);
      Rectangle var7 = this.table.getCellRect(var3, var5, true);
      Rectangle var8 = var6.union(var7);
      int var10;
      int var11;
      if (this.table.getShowHorizontalLines()) {
         int var9 = var8.x + var8.width;
         var10 = var8.y;

         for(var11 = var2; var11 <= var3; ++var11) {
            var10 += this.table.getRowHeight(var11);
            var1.drawLine(var8.x, var10 - 1, var9 - 1, var10 - 1);
         }
      }

      if (this.table.getShowVerticalLines()) {
         TableColumnModel var14 = this.table.getColumnModel();
         var10 = var8.y + var8.height;
         int var12;
         int var13;
         if (this.table.getComponentOrientation().isLeftToRight()) {
            var11 = var8.x;

            for(var12 = var4; var12 <= var5; ++var12) {
               var13 = var14.getColumn(var12).getWidth();
               var11 += var13;
               var1.drawLine(var11 - 1, 0, var11 - 1, var10 - 1);
            }
         } else {
            var11 = var8.x;

            for(var12 = var5; var12 >= var4; --var12) {
               var13 = var14.getColumn(var12).getWidth();
               var11 += var13;
               var1.drawLine(var11 - 1, 0, var11 - 1, var10 - 1);
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

   private void paintCells(Graphics var1, int var2, int var3, int var4, int var5) {
      JTableHeader var6 = this.table.getTableHeader();
      TableColumn var7 = var6 == null ? null : var6.getDraggedColumn();
      TableColumnModel var8 = this.table.getColumnModel();
      int var9 = var8.getColumnMargin();
      Rectangle var10;
      TableColumn var11;
      int var12;
      int var13;
      int var14;
      if (this.table.getComponentOrientation().isLeftToRight()) {
         for(var13 = var2; var13 <= var3; ++var13) {
            var10 = this.table.getCellRect(var13, var4, false);

            for(var14 = var4; var14 <= var5; ++var14) {
               var11 = var8.getColumn(var14);
               var12 = var11.getWidth();
               var10.width = var12 - var9;
               if (var11 != var7) {
                  this.paintCell(var1, var10, var13, var14);
               }

               var10.x += var12;
            }
         }
      } else {
         for(var13 = var2; var13 <= var3; ++var13) {
            var10 = this.table.getCellRect(var13, var4, false);
            var11 = var8.getColumn(var4);
            if (var11 != var7) {
               var12 = var11.getWidth();
               var10.width = var12 - var9;
               this.paintCell(var1, var10, var13, var4);
            }

            for(var14 = var4 + 1; var14 <= var5; ++var14) {
               var11 = var8.getColumn(var14);
               var12 = var11.getWidth();
               var10.width = var12 - var9;
               var10.x -= var12;
               if (var11 != var7) {
                  this.paintCell(var1, var10, var13, var14);
               }
            }
         }
      }

      if (var7 != null) {
         this.paintDraggedArea(var1, var2, var3, var7, var6.getDraggedDistance());
      }

      this.rendererPane.removeAll();
   }

   private void paintDraggedArea(Graphics var1, int var2, int var3, TableColumn var4, int var5) {
      int var6 = this.viewIndexForColumn(var4);
      Rectangle var7 = this.table.getCellRect(var2, var6, true);
      Rectangle var8 = this.table.getCellRect(var3, var6, true);
      Rectangle var9 = var7.union(var8);
      var1.setColor(this.table.getParent().getBackground());
      var1.fillRect(var9.x, var9.y, var9.width, var9.height);
      var9.x += var5;
      var1.setColor(this.table.getBackground());
      var1.fillRect(var9.x, var9.y, var9.width, var9.height);
      int var10;
      int var13;
      if (this.table.getShowVerticalLines()) {
         var1.setColor(this.table.getGridColor());
         var10 = var9.x;
         int var11 = var9.y;
         int var12 = var10 + var9.width - 1;
         var13 = var11 + var9.height - 1;
         var1.drawLine(var10 - 1, var11, var10 - 1, var13);
         var1.drawLine(var12, var11, var12, var13);
      }

      for(var10 = var2; var10 <= var3; ++var10) {
         Rectangle var17 = this.table.getCellRect(var10, var6, false);
         var17.x += var5;
         this.paintCell(var1, var17, var10, var6);
         if (this.table.getShowHorizontalLines()) {
            var1.setColor(this.table.getGridColor());
            Rectangle var18 = this.table.getCellRect(var10, var6, true);
            var18.x += var5;
            var13 = var18.x;
            int var14 = var18.y;
            int var15 = var13 + var18.width - 1;
            int var16 = var14 + var18.height - 1;
            var1.drawLine(var13, var16, var15, var16);
         }
      }

   }

   private void paintCell(Graphics var1, Rectangle var2, int var3, int var4) {
      if (this.table.isEditing() && this.table.getEditingRow() == var3 && this.table.getEditingColumn() == var4) {
         Component var7 = this.table.getEditorComponent();
         var7.setBounds(var2);
         var7.validate();
      } else {
         TableCellRenderer var5 = this.table.getCellRenderer(var3, var4);
         Component var6 = this.table.prepareRenderer(var5, var3, var4);
         this.rendererPane.paintComponent(var1, var6, this.table, var2.x, var2.y, var2.width, var2.height, true);
      }

   }

   private static int getAdjustedLead(JTable var0, boolean var1, ListSelectionModel var2) {
      int var3 = var2.getLeadSelectionIndex();
      int var4 = var1 ? var0.getRowCount() : var0.getColumnCount();
      return var3 < var4 ? var3 : -1;
   }

   private static int getAdjustedLead(JTable var0, boolean var1) {
      return var1 ? getAdjustedLead(var0, var1, var0.getSelectionModel()) : getAdjustedLead(var0, var1, var0.getColumnModel().getSelectionModel());
   }

   static class TableTransferHandler extends TransferHandler implements UIResource {
      protected Transferable createTransferable(JComponent var1) {
         if (var1 instanceof JTable) {
            JTable var2 = (JTable)var1;
            if (!var2.getRowSelectionAllowed() && !var2.getColumnSelectionAllowed()) {
               return null;
            } else {
               int[] var3;
               int var5;
               int var6;
               if (!var2.getRowSelectionAllowed()) {
                  var5 = var2.getRowCount();
                  var3 = new int[var5];

                  for(var6 = 0; var6 < var5; var3[var6] = var6++) {
                  }
               } else {
                  var3 = var2.getSelectedRows();
               }

               int[] var4;
               if (!var2.getColumnSelectionAllowed()) {
                  var5 = var2.getColumnCount();
                  var4 = new int[var5];

                  for(var6 = 0; var6 < var5; var4[var6] = var6++) {
                  }
               } else {
                  var4 = var2.getSelectedColumns();
               }

               if (var3 != null && var4 != null && var3.length != 0 && var4.length != 0) {
                  StringBuffer var11 = new StringBuffer();
                  StringBuffer var12 = new StringBuffer();
                  var12.append("<html>\n<body>\n<table>\n");

                  for(int var7 = 0; var7 < var3.length; ++var7) {
                     var12.append("<tr>\n");

                     for(int var8 = 0; var8 < var4.length; ++var8) {
                        Object var9 = var2.getValueAt(var3[var7], var4[var8]);
                        String var10 = var9 == null ? "" : var9.toString();
                        var11.append(var10 + "\t");
                        var12.append("  <td>" + var10 + "</td>\n");
                     }

                     var11.deleteCharAt(var11.length() - 1).append("\n");
                     var12.append("</tr>\n");
                  }

                  var11.deleteCharAt(var11.length() - 1);
                  var12.append("</table>\n</body>\n</html>");
                  return new BasicTransferable(var11.toString(), var12.toString());
               } else {
                  return null;
               }
            }
         } else {
            return null;
         }
      }

      public int getSourceActions(JComponent var1) {
         return 1;
      }
   }

   private class Handler implements FocusListener, MouseInputListener, PropertyChangeListener, ListSelectionListener, ActionListener, DragRecognitionSupport.BeforeDrag {
      private Component dispatchComponent;
      private int pressedRow;
      private int pressedCol;
      private MouseEvent pressedEvent;
      private boolean dragPressDidSelection;
      private boolean dragStarted;
      private boolean shouldStartTimer;
      private boolean outsidePrefSize;
      private Timer timer;

      private Handler() {
         this.timer = null;
      }

      private void repaintLeadCell() {
         int var1 = BasicTableUI.getAdjustedLead(BasicTableUI.this.table, true);
         int var2 = BasicTableUI.getAdjustedLead(BasicTableUI.this.table, false);
         if (var1 >= 0 && var2 >= 0) {
            Rectangle var3 = BasicTableUI.this.table.getCellRect(var1, var2, false);
            BasicTableUI.this.table.repaint(var3);
         }
      }

      public void focusGained(FocusEvent var1) {
         this.repaintLeadCell();
      }

      public void focusLost(FocusEvent var1) {
         this.repaintLeadCell();
      }

      public void keyPressed(KeyEvent var1) {
      }

      public void keyReleased(KeyEvent var1) {
      }

      public void keyTyped(KeyEvent var1) {
         KeyStroke var2 = KeyStroke.getKeyStroke(var1.getKeyChar(), var1.getModifiers());
         InputMap var3 = BasicTableUI.this.table.getInputMap(0);
         if (var3 == null || var3.get(var2) == null) {
            var3 = BasicTableUI.this.table.getInputMap(1);
            if (var3 == null || var3.get(var2) == null) {
               var2 = KeyStroke.getKeyStrokeForEvent(var1);
               if (var1.getKeyChar() != '\r') {
                  int var4 = BasicTableUI.getAdjustedLead(BasicTableUI.this.table, true);
                  int var5 = BasicTableUI.getAdjustedLead(BasicTableUI.this.table, false);
                  if (var4 == -1 || var5 == -1 || BasicTableUI.this.table.isEditing() || BasicTableUI.this.table.editCellAt(var4, var5)) {
                     Component var6 = BasicTableUI.this.table.getEditorComponent();
                     if (BasicTableUI.this.table.isEditing() && var6 != null && var6 instanceof JComponent) {
                        JComponent var7 = (JComponent)var6;
                        var3 = var7.getInputMap(0);
                        Object var8 = var3 != null ? var3.get(var2) : null;
                        if (var8 == null) {
                           var3 = var7.getInputMap(1);
                           var8 = var3 != null ? var3.get(var2) : null;
                        }

                        if (var8 != null) {
                           ActionMap var9 = var7.getActionMap();
                           Action var10 = var9 != null ? var9.get(var8) : null;
                           if (var10 != null && SwingUtilities.notifyAction(var10, var2, var1, var7, var1.getModifiers())) {
                              var1.consume();
                           }
                        }
                     }

                  }
               }
            }
         }
      }

      public void mouseClicked(MouseEvent var1) {
      }

      private void setDispatchComponent(MouseEvent var1) {
         Component var2 = BasicTableUI.this.table.getEditorComponent();
         Point var3 = var1.getPoint();
         Point var4 = SwingUtilities.convertPoint(BasicTableUI.this.table, var3, var2);
         this.dispatchComponent = SwingUtilities.getDeepestComponentAt(var2, var4.x, var4.y);
         SwingUtilities2.setSkipClickCount(this.dispatchComponent, var1.getClickCount() - 1);
      }

      private boolean repostEvent(MouseEvent var1) {
         if (this.dispatchComponent != null && BasicTableUI.this.table.isEditing()) {
            MouseEvent var2 = SwingUtilities.convertMouseEvent(BasicTableUI.this.table, var1, this.dispatchComponent);
            this.dispatchComponent.dispatchEvent(var2);
            return true;
         } else {
            return false;
         }
      }

      private void setValueIsAdjusting(boolean var1) {
         BasicTableUI.this.table.getSelectionModel().setValueIsAdjusting(var1);
         BasicTableUI.this.table.getColumnModel().getSelectionModel().setValueIsAdjusting(var1);
      }

      private boolean canStartDrag() {
         if (this.pressedRow != -1 && this.pressedCol != -1) {
            if (BasicTableUI.this.isFileList) {
               return !this.outsidePrefSize;
            } else {
               return BasicTableUI.this.table.getSelectionModel().getSelectionMode() == 0 && BasicTableUI.this.table.getColumnModel().getSelectionModel().getSelectionMode() == 0 ? true : BasicTableUI.this.table.isCellSelected(this.pressedRow, this.pressedCol);
            }
         } else {
            return false;
         }
      }

      public void mousePressed(MouseEvent var1) {
         if (!SwingUtilities2.shouldIgnore(var1, BasicTableUI.this.table)) {
            if (BasicTableUI.this.table.isEditing() && !BasicTableUI.this.table.getCellEditor().stopCellEditing()) {
               Component var3 = BasicTableUI.this.table.getEditorComponent();
               if (var3 != null && !var3.hasFocus()) {
                  SwingUtilities2.compositeRequestFocus(var3);
               }

            } else {
               Point var2 = var1.getPoint();
               this.pressedRow = BasicTableUI.this.table.rowAtPoint(var2);
               this.pressedCol = BasicTableUI.this.table.columnAtPoint(var2);
               this.outsidePrefSize = BasicTableUI.this.pointOutsidePrefSize(this.pressedRow, this.pressedCol, var2);
               if (BasicTableUI.this.isFileList) {
                  this.shouldStartTimer = BasicTableUI.this.table.isCellSelected(this.pressedRow, this.pressedCol) && !var1.isShiftDown() && !BasicGraphicsUtils.isMenuShortcutKeyDown(var1) && !this.outsidePrefSize;
               }

               if (BasicTableUI.this.table.getDragEnabled()) {
                  this.mousePressedDND(var1);
               } else {
                  SwingUtilities2.adjustFocus(BasicTableUI.this.table);
                  if (!BasicTableUI.this.isFileList) {
                     this.setValueIsAdjusting(true);
                  }

                  this.adjustSelection(var1);
               }

            }
         }
      }

      private void mousePressedDND(MouseEvent var1) {
         this.pressedEvent = var1;
         boolean var2 = true;
         this.dragStarted = false;
         if (this.canStartDrag() && DragRecognitionSupport.mousePressed(var1)) {
            this.dragPressDidSelection = false;
            if (BasicGraphicsUtils.isMenuShortcutKeyDown(var1) && BasicTableUI.this.isFileList) {
               return;
            }

            if (!var1.isShiftDown() && BasicTableUI.this.table.isCellSelected(this.pressedRow, this.pressedCol)) {
               BasicTableUI.this.table.getSelectionModel().addSelectionInterval(this.pressedRow, this.pressedRow);
               BasicTableUI.this.table.getColumnModel().getSelectionModel().addSelectionInterval(this.pressedCol, this.pressedCol);
               return;
            }

            this.dragPressDidSelection = true;
            var2 = false;
         } else if (!BasicTableUI.this.isFileList) {
            this.setValueIsAdjusting(true);
         }

         if (var2) {
            SwingUtilities2.adjustFocus(BasicTableUI.this.table);
         }

         this.adjustSelection(var1);
      }

      private void adjustSelection(MouseEvent var1) {
         if (this.outsidePrefSize) {
            if (var1.getID() == 501 && (!var1.isShiftDown() || BasicTableUI.this.table.getSelectionModel().getSelectionMode() == 0)) {
               BasicTableUI.this.table.clearSelection();
               TableCellEditor var4 = BasicTableUI.this.table.getCellEditor();
               if (var4 != null) {
                  var4.stopCellEditing();
               }
            }

         } else if (this.pressedCol != -1 && this.pressedRow != -1) {
            boolean var2 = BasicTableUI.this.table.getDragEnabled();
            if (!var2 && !BasicTableUI.this.isFileList && BasicTableUI.this.table.editCellAt(this.pressedRow, this.pressedCol, var1)) {
               this.setDispatchComponent(var1);
               this.repostEvent(var1);
            }

            TableCellEditor var3 = BasicTableUI.this.table.getCellEditor();
            if (var2 || var3 == null || var3.shouldSelectCell(var1)) {
               BasicTableUI.this.table.changeSelection(this.pressedRow, this.pressedCol, BasicGraphicsUtils.isMenuShortcutKeyDown(var1), var1.isShiftDown());
            }

         }
      }

      public void valueChanged(ListSelectionEvent var1) {
         if (this.timer != null) {
            this.timer.stop();
            this.timer = null;
         }

      }

      public void actionPerformed(ActionEvent var1) {
         BasicTableUI.this.table.editCellAt(this.pressedRow, this.pressedCol, (EventObject)null);
         Component var2 = BasicTableUI.this.table.getEditorComponent();
         if (var2 != null && !var2.hasFocus()) {
            SwingUtilities2.compositeRequestFocus(var2);
         }

      }

      private void maybeStartTimer() {
         if (this.shouldStartTimer) {
            if (this.timer == null) {
               this.timer = new Timer(1200, this);
               this.timer.setRepeats(false);
            }

            this.timer.start();
         }
      }

      public void mouseReleased(MouseEvent var1) {
         if (!SwingUtilities2.shouldIgnore(var1, BasicTableUI.this.table)) {
            if (BasicTableUI.this.table.getDragEnabled()) {
               this.mouseReleasedDND(var1);
            } else if (BasicTableUI.this.isFileList) {
               this.maybeStartTimer();
            }

            this.pressedEvent = null;
            this.repostEvent(var1);
            this.dispatchComponent = null;
            this.setValueIsAdjusting(false);
         }
      }

      private void mouseReleasedDND(MouseEvent var1) {
         MouseEvent var2 = DragRecognitionSupport.mouseReleased(var1);
         if (var2 != null) {
            SwingUtilities2.adjustFocus(BasicTableUI.this.table);
            if (!this.dragPressDidSelection) {
               this.adjustSelection(var2);
            }
         }

         if (!this.dragStarted) {
            if (BasicTableUI.this.isFileList) {
               this.maybeStartTimer();
               return;
            }

            Point var3 = var1.getPoint();
            if (this.pressedEvent != null && BasicTableUI.this.table.rowAtPoint(var3) == this.pressedRow && BasicTableUI.this.table.columnAtPoint(var3) == this.pressedCol && BasicTableUI.this.table.editCellAt(this.pressedRow, this.pressedCol, this.pressedEvent)) {
               this.setDispatchComponent(this.pressedEvent);
               this.repostEvent(this.pressedEvent);
               TableCellEditor var4 = BasicTableUI.this.table.getCellEditor();
               if (var4 != null) {
                  var4.shouldSelectCell(this.pressedEvent);
               }
            }
         }

      }

      public void mouseEntered(MouseEvent var1) {
      }

      public void mouseExited(MouseEvent var1) {
      }

      public void mouseMoved(MouseEvent var1) {
      }

      public void dragStarting(MouseEvent var1) {
         this.dragStarted = true;
         if (BasicGraphicsUtils.isMenuShortcutKeyDown(var1) && BasicTableUI.this.isFileList) {
            BasicTableUI.this.table.getSelectionModel().addSelectionInterval(this.pressedRow, this.pressedRow);
            BasicTableUI.this.table.getColumnModel().getSelectionModel().addSelectionInterval(this.pressedCol, this.pressedCol);
         }

         this.pressedEvent = null;
      }

      public void mouseDragged(MouseEvent var1) {
         if (!SwingUtilities2.shouldIgnore(var1, BasicTableUI.this.table)) {
            if (!BasicTableUI.this.table.getDragEnabled() || !DragRecognitionSupport.mouseDragged(var1, this) && !this.dragStarted) {
               this.repostEvent(var1);
               if (!BasicTableUI.this.isFileList && !BasicTableUI.this.table.isEditing()) {
                  Point var2 = var1.getPoint();
                  int var3 = BasicTableUI.this.table.rowAtPoint(var2);
                  int var4 = BasicTableUI.this.table.columnAtPoint(var2);
                  if (var4 != -1 && var3 != -1) {
                     BasicTableUI.this.table.changeSelection(var3, var4, BasicGraphicsUtils.isMenuShortcutKeyDown(var1), true);
                  }
               }
            }
         }
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if ("componentOrientation" == var2) {
            InputMap var3 = BasicTableUI.this.getInputMap(1);
            SwingUtilities.replaceUIInputMap(BasicTableUI.this.table, 1, var3);
            JTableHeader var4 = BasicTableUI.this.table.getTableHeader();
            if (var4 != null) {
               var4.setComponentOrientation((ComponentOrientation)var1.getNewValue());
            }
         } else if ("dropLocation" == var2) {
            JTable.DropLocation var5 = (JTable.DropLocation)var1.getOldValue();
            this.repaintDropLocation(var5);
            this.repaintDropLocation(BasicTableUI.this.table.getDropLocation());
         } else if ("Table.isFileList" == var2) {
            BasicTableUI.this.isFileList = Boolean.TRUE.equals(BasicTableUI.this.table.getClientProperty("Table.isFileList"));
            BasicTableUI.this.table.revalidate();
            BasicTableUI.this.table.repaint();
            if (BasicTableUI.this.isFileList) {
               BasicTableUI.this.table.getSelectionModel().addListSelectionListener(BasicTableUI.this.getHandler());
            } else {
               BasicTableUI.this.table.getSelectionModel().removeListSelectionListener(BasicTableUI.this.getHandler());
               this.timer = null;
            }
         } else if ("selectionModel" == var2 && BasicTableUI.this.isFileList) {
            ListSelectionModel var6 = (ListSelectionModel)var1.getOldValue();
            var6.removeListSelectionListener(BasicTableUI.this.getHandler());
            BasicTableUI.this.table.getSelectionModel().addListSelectionListener(BasicTableUI.this.getHandler());
         }

      }

      private void repaintDropLocation(JTable.DropLocation var1) {
         if (var1 != null) {
            Rectangle var2;
            if (!var1.isInsertRow() && !var1.isInsertColumn()) {
               var2 = BasicTableUI.this.table.getCellRect(var1.getRow(), var1.getColumn(), false);
               if (var2 != null) {
                  BasicTableUI.this.table.repaint(var2);
               }

            } else {
               if (var1.isInsertRow()) {
                  var2 = BasicTableUI.this.extendRect(BasicTableUI.this.getHDropLineRect(var1), true);
                  if (var2 != null) {
                     BasicTableUI.this.table.repaint(var2);
                  }
               }

               if (var1.isInsertColumn()) {
                  var2 = BasicTableUI.this.extendRect(BasicTableUI.this.getVDropLineRect(var1), false);
                  if (var2 != null) {
                     BasicTableUI.this.table.repaint(var2);
                  }
               }

            }
         }
      }

      // $FF: synthetic method
      Handler(Object var2) {
         this();
      }
   }

   public class MouseInputHandler implements MouseInputListener {
      public void mouseClicked(MouseEvent var1) {
         BasicTableUI.this.getHandler().mouseClicked(var1);
      }

      public void mousePressed(MouseEvent var1) {
         BasicTableUI.this.getHandler().mousePressed(var1);
      }

      public void mouseReleased(MouseEvent var1) {
         BasicTableUI.this.getHandler().mouseReleased(var1);
      }

      public void mouseEntered(MouseEvent var1) {
         BasicTableUI.this.getHandler().mouseEntered(var1);
      }

      public void mouseExited(MouseEvent var1) {
         BasicTableUI.this.getHandler().mouseExited(var1);
      }

      public void mouseMoved(MouseEvent var1) {
         BasicTableUI.this.getHandler().mouseMoved(var1);
      }

      public void mouseDragged(MouseEvent var1) {
         BasicTableUI.this.getHandler().mouseDragged(var1);
      }
   }

   public class FocusHandler implements FocusListener {
      public void focusGained(FocusEvent var1) {
         BasicTableUI.this.getHandler().focusGained(var1);
      }

      public void focusLost(FocusEvent var1) {
         BasicTableUI.this.getHandler().focusLost(var1);
      }
   }

   public class KeyHandler implements KeyListener {
      public void keyPressed(KeyEvent var1) {
         BasicTableUI.this.getHandler().keyPressed(var1);
      }

      public void keyReleased(KeyEvent var1) {
         BasicTableUI.this.getHandler().keyReleased(var1);
      }

      public void keyTyped(KeyEvent var1) {
         BasicTableUI.this.getHandler().keyTyped(var1);
      }
   }

   private static class Actions extends UIAction {
      private static final String CANCEL_EDITING = "cancel";
      private static final String SELECT_ALL = "selectAll";
      private static final String CLEAR_SELECTION = "clearSelection";
      private static final String START_EDITING = "startEditing";
      private static final String NEXT_ROW = "selectNextRow";
      private static final String NEXT_ROW_CELL = "selectNextRowCell";
      private static final String NEXT_ROW_EXTEND_SELECTION = "selectNextRowExtendSelection";
      private static final String NEXT_ROW_CHANGE_LEAD = "selectNextRowChangeLead";
      private static final String PREVIOUS_ROW = "selectPreviousRow";
      private static final String PREVIOUS_ROW_CELL = "selectPreviousRowCell";
      private static final String PREVIOUS_ROW_EXTEND_SELECTION = "selectPreviousRowExtendSelection";
      private static final String PREVIOUS_ROW_CHANGE_LEAD = "selectPreviousRowChangeLead";
      private static final String NEXT_COLUMN = "selectNextColumn";
      private static final String NEXT_COLUMN_CELL = "selectNextColumnCell";
      private static final String NEXT_COLUMN_EXTEND_SELECTION = "selectNextColumnExtendSelection";
      private static final String NEXT_COLUMN_CHANGE_LEAD = "selectNextColumnChangeLead";
      private static final String PREVIOUS_COLUMN = "selectPreviousColumn";
      private static final String PREVIOUS_COLUMN_CELL = "selectPreviousColumnCell";
      private static final String PREVIOUS_COLUMN_EXTEND_SELECTION = "selectPreviousColumnExtendSelection";
      private static final String PREVIOUS_COLUMN_CHANGE_LEAD = "selectPreviousColumnChangeLead";
      private static final String SCROLL_LEFT_CHANGE_SELECTION = "scrollLeftChangeSelection";
      private static final String SCROLL_LEFT_EXTEND_SELECTION = "scrollLeftExtendSelection";
      private static final String SCROLL_RIGHT_CHANGE_SELECTION = "scrollRightChangeSelection";
      private static final String SCROLL_RIGHT_EXTEND_SELECTION = "scrollRightExtendSelection";
      private static final String SCROLL_UP_CHANGE_SELECTION = "scrollUpChangeSelection";
      private static final String SCROLL_UP_EXTEND_SELECTION = "scrollUpExtendSelection";
      private static final String SCROLL_DOWN_CHANGE_SELECTION = "scrollDownChangeSelection";
      private static final String SCROLL_DOWN_EXTEND_SELECTION = "scrollDownExtendSelection";
      private static final String FIRST_COLUMN = "selectFirstColumn";
      private static final String FIRST_COLUMN_EXTEND_SELECTION = "selectFirstColumnExtendSelection";
      private static final String LAST_COLUMN = "selectLastColumn";
      private static final String LAST_COLUMN_EXTEND_SELECTION = "selectLastColumnExtendSelection";
      private static final String FIRST_ROW = "selectFirstRow";
      private static final String FIRST_ROW_EXTEND_SELECTION = "selectFirstRowExtendSelection";
      private static final String LAST_ROW = "selectLastRow";
      private static final String LAST_ROW_EXTEND_SELECTION = "selectLastRowExtendSelection";
      private static final String ADD_TO_SELECTION = "addToSelection";
      private static final String TOGGLE_AND_ANCHOR = "toggleAndAnchor";
      private static final String EXTEND_TO = "extendTo";
      private static final String MOVE_SELECTION_TO = "moveSelectionTo";
      private static final String FOCUS_HEADER = "focusHeader";
      protected int dx;
      protected int dy;
      protected boolean extend;
      protected boolean inSelection;
      protected boolean forwards;
      protected boolean vertically;
      protected boolean toLimit;
      protected int leadRow;
      protected int leadColumn;

      Actions(String var1) {
         super(var1);
      }

      Actions(String var1, int var2, int var3, boolean var4, boolean var5) {
         super(var1);
         if (var5) {
            this.inSelection = true;
            var2 = sign(var2);
            var3 = sign(var3);

            assert (var2 == 0 || var3 == 0) && (var2 != 0 || var3 != 0);
         }

         this.dx = var2;
         this.dy = var3;
         this.extend = var4;
      }

      Actions(String var1, boolean var2, boolean var3, boolean var4, boolean var5) {
         this(var1, 0, 0, var2, false);
         this.forwards = var3;
         this.vertically = var4;
         this.toLimit = var5;
      }

      private static int clipToRange(int var0, int var1, int var2) {
         return Math.min(Math.max(var0, var1), var2 - 1);
      }

      private void moveWithinTableRange(JTable var1, int var2, int var3) {
         this.leadRow = clipToRange(this.leadRow + var3, 0, var1.getRowCount());
         this.leadColumn = clipToRange(this.leadColumn + var2, 0, var1.getColumnCount());
      }

      private static int sign(int var0) {
         return var0 < 0 ? -1 : (var0 == 0 ? 0 : 1);
      }

      private boolean moveWithinSelectedRange(JTable var1, int var2, int var3, ListSelectionModel var4, ListSelectionModel var5) {
         boolean var11 = var1.getRowSelectionAllowed();
         boolean var12 = var1.getColumnSelectionAllowed();
         int var6;
         int var7;
         int var8;
         int var9;
         int var10;
         if (var11 && var12) {
            var6 = var1.getSelectedRowCount() * var1.getSelectedColumnCount();
            var7 = var5.getMinSelectionIndex();
            var8 = var5.getMaxSelectionIndex();
            var9 = var4.getMinSelectionIndex();
            var10 = var4.getMaxSelectionIndex();
         } else if (var11) {
            var6 = var1.getSelectedRowCount();
            var7 = 0;
            var8 = var1.getColumnCount() - 1;
            var9 = var4.getMinSelectionIndex();
            var10 = var4.getMaxSelectionIndex();
         } else if (var12) {
            var6 = var1.getSelectedColumnCount();
            var7 = var5.getMinSelectionIndex();
            var8 = var5.getMaxSelectionIndex();
            var9 = 0;
            var10 = var1.getRowCount() - 1;
         } else {
            var6 = 0;
            var10 = 0;
            var9 = 0;
            var8 = 0;
            var7 = 0;
         }

         boolean var13;
         if (var6 != 0 && (var6 != 1 || !var1.isCellSelected(this.leadRow, this.leadColumn))) {
            var13 = true;
         } else {
            var13 = false;
            var8 = var1.getColumnCount() - 1;
            var10 = var1.getRowCount() - 1;
            var7 = Math.min(0, var8);
            var9 = Math.min(0, var10);
         }

         if (var3 == 1 && this.leadColumn == -1) {
            this.leadColumn = var7;
            this.leadRow = -1;
         } else if (var2 == 1 && this.leadRow == -1) {
            this.leadRow = var9;
            this.leadColumn = -1;
         } else if (var3 == -1 && this.leadColumn == -1) {
            this.leadColumn = var8;
            this.leadRow = var10 + 1;
         } else if (var2 == -1 && this.leadRow == -1) {
            this.leadRow = var10;
            this.leadColumn = var8 + 1;
         }

         this.leadRow = Math.min(Math.max(this.leadRow, var9 - 1), var10 + 1);
         this.leadColumn = Math.min(Math.max(this.leadColumn, var7 - 1), var8 + 1);

         do {
            this.calcNextPos(var2, var7, var8, var3, var9, var10);
         } while(var13 && !var1.isCellSelected(this.leadRow, this.leadColumn));

         return var13;
      }

      private void calcNextPos(int var1, int var2, int var3, int var4, int var5, int var6) {
         if (var1 != 0) {
            this.leadColumn += var1;
            if (this.leadColumn > var3) {
               this.leadColumn = var2;
               ++this.leadRow;
               if (this.leadRow > var6) {
                  this.leadRow = var5;
               }
            } else if (this.leadColumn < var2) {
               this.leadColumn = var3;
               --this.leadRow;
               if (this.leadRow < var5) {
                  this.leadRow = var6;
               }
            }
         } else {
            this.leadRow += var4;
            if (this.leadRow > var6) {
               this.leadRow = var5;
               ++this.leadColumn;
               if (this.leadColumn > var3) {
                  this.leadColumn = var2;
               }
            } else if (this.leadRow < var5) {
               this.leadRow = var6;
               --this.leadColumn;
               if (this.leadColumn < var2) {
                  this.leadColumn = var3;
               }
            }
         }

      }

      public void actionPerformed(ActionEvent var1) {
         String var2 = this.getName();
         JTable var3 = (JTable)var1.getSource();
         ListSelectionModel var4 = var3.getSelectionModel();
         this.leadRow = BasicTableUI.getAdjustedLead(var3, true, var4);
         ListSelectionModel var5 = var3.getColumnModel().getSelectionModel();
         this.leadColumn = BasicTableUI.getAdjustedLead(var3, false, var5);
         int var6;
         Rectangle var7;
         if (var2 == "scrollLeftChangeSelection" || var2 == "scrollLeftExtendSelection" || var2 == "scrollRightChangeSelection" || var2 == "scrollRightExtendSelection" || var2 == "scrollUpChangeSelection" || var2 == "scrollUpExtendSelection" || var2 == "scrollDownChangeSelection" || var2 == "scrollDownExtendSelection" || var2 == "selectFirstColumn" || var2 == "selectFirstColumnExtendSelection" || var2 == "selectFirstRow" || var2 == "selectFirstRowExtendSelection" || var2 == "selectLastColumn" || var2 == "selectLastColumnExtendSelection" || var2 == "selectLastRow" || var2 == "selectLastRowExtendSelection") {
            if (this.toLimit) {
               if (this.vertically) {
                  var6 = var3.getRowCount();
                  this.dx = 0;
                  this.dy = this.forwards ? var6 : -var6;
               } else {
                  var6 = var3.getColumnCount();
                  this.dx = this.forwards ? var6 : -var6;
                  this.dy = 0;
               }
            } else {
               if (!(SwingUtilities.getUnwrappedParent(var3).getParent() instanceof JScrollPane)) {
                  return;
               }

               Dimension var10 = var3.getParent().getSize();
               int var8;
               if (this.vertically) {
                  var7 = var3.getCellRect(this.leadRow, 0, true);
                  if (this.forwards) {
                     var7.y += Math.max(var10.height, var7.height);
                  } else {
                     var7.y -= var10.height;
                  }

                  this.dx = 0;
                  var8 = var3.rowAtPoint(var7.getLocation());
                  if (var8 == -1 && this.forwards) {
                     var8 = var3.getRowCount();
                  }

                  this.dy = var8 - this.leadRow;
               } else {
                  var7 = var3.getCellRect(0, this.leadColumn, true);
                  if (this.forwards) {
                     var7.x += Math.max(var10.width, var7.width);
                  } else {
                     var7.x -= var10.width;
                  }

                  var8 = var3.columnAtPoint(var7.getLocation());
                  if (var8 == -1) {
                     boolean var9 = var3.getComponentOrientation().isLeftToRight();
                     var8 = this.forwards ? (var9 ? var3.getColumnCount() : 0) : (var9 ? 0 : var3.getColumnCount());
                  }

                  this.dx = var8 - this.leadColumn;
                  this.dy = 0;
               }
            }
         }

         if (var2 != "selectNextRow" && var2 != "selectNextRowCell" && var2 != "selectNextRowExtendSelection" && var2 != "selectNextRowChangeLead" && var2 != "selectNextColumn" && var2 != "selectNextColumnCell" && var2 != "selectNextColumnExtendSelection" && var2 != "selectNextColumnChangeLead" && var2 != "selectPreviousRow" && var2 != "selectPreviousRowCell" && var2 != "selectPreviousRowExtendSelection" && var2 != "selectPreviousRowChangeLead" && var2 != "selectPreviousColumn" && var2 != "selectPreviousColumnCell" && var2 != "selectPreviousColumnExtendSelection" && var2 != "selectPreviousColumnChangeLead" && var2 != "scrollLeftChangeSelection" && var2 != "scrollLeftExtendSelection" && var2 != "scrollRightChangeSelection" && var2 != "scrollRightExtendSelection" && var2 != "scrollUpChangeSelection" && var2 != "scrollUpExtendSelection" && var2 != "scrollDownChangeSelection" && var2 != "scrollDownExtendSelection" && var2 != "selectFirstColumn" && var2 != "selectFirstColumnExtendSelection" && var2 != "selectFirstRow" && var2 != "selectFirstRowExtendSelection" && var2 != "selectLastColumn" && var2 != "selectLastColumnExtendSelection" && var2 != "selectLastRow" && var2 != "selectLastRowExtendSelection") {
            if (var2 == "cancel") {
               var3.removeEditor();
            } else if (var2 == "selectAll") {
               var3.selectAll();
            } else if (var2 == "clearSelection") {
               var3.clearSelection();
            } else if (var2 == "startEditing") {
               if (!var3.hasFocus()) {
                  TableCellEditor var14 = var3.getCellEditor();
                  if (var14 != null && !var14.stopCellEditing()) {
                     return;
                  }

                  var3.requestFocus();
                  return;
               }

               var3.editCellAt(this.leadRow, this.leadColumn, var1);
               Component var12 = var3.getEditorComponent();
               if (var12 != null) {
                  var12.requestFocus();
               }
            } else {
               int var13;
               if (var2 == "addToSelection") {
                  if (!var3.isCellSelected(this.leadRow, this.leadColumn)) {
                     var6 = var4.getAnchorSelectionIndex();
                     var13 = var5.getAnchorSelectionIndex();
                     var4.setValueIsAdjusting(true);
                     var5.setValueIsAdjusting(true);
                     var3.changeSelection(this.leadRow, this.leadColumn, true, false);
                     var4.setAnchorSelectionIndex(var6);
                     var5.setAnchorSelectionIndex(var13);
                     var4.setValueIsAdjusting(false);
                     var5.setValueIsAdjusting(false);
                  }
               } else if (var2 == "toggleAndAnchor") {
                  var3.changeSelection(this.leadRow, this.leadColumn, true, false);
               } else if (var2 == "extendTo") {
                  var3.changeSelection(this.leadRow, this.leadColumn, false, true);
               } else if (var2 == "moveSelectionTo") {
                  var3.changeSelection(this.leadRow, this.leadColumn, false, false);
               } else if (var2 == "focusHeader") {
                  JTableHeader var15 = var3.getTableHeader();
                  if (var15 != null) {
                     var13 = var3.getSelectedColumn();
                     if (var13 >= 0) {
                        TableHeaderUI var16 = var15.getUI();
                        if (var16 instanceof BasicTableHeaderUI) {
                           ((BasicTableHeaderUI)var16).selectColumn(var13);
                        }
                     }

                     var15.requestFocusInWindow();
                  }
               }
            }
         } else {
            if (var3.isEditing() && !var3.getCellEditor().stopCellEditing()) {
               return;
            }

            boolean var11 = false;
            if (var2 != "selectNextRowChangeLead" && var2 != "selectPreviousRowChangeLead") {
               if (var2 == "selectNextColumnChangeLead" || var2 == "selectPreviousColumnChangeLead") {
                  var11 = var5.getSelectionMode() == 2;
               }
            } else {
               var11 = var4.getSelectionMode() == 2;
            }

            if (var11) {
               this.moveWithinTableRange(var3, this.dx, this.dy);
               if (this.dy != 0) {
                  ((DefaultListSelectionModel)var4).moveLeadSelectionIndex(this.leadRow);
                  if (BasicTableUI.getAdjustedLead(var3, false, var5) == -1 && var3.getColumnCount() > 0) {
                     ((DefaultListSelectionModel)var5).moveLeadSelectionIndex(0);
                  }
               } else {
                  ((DefaultListSelectionModel)var5).moveLeadSelectionIndex(this.leadColumn);
                  if (BasicTableUI.getAdjustedLead(var3, true, var4) == -1 && var3.getRowCount() > 0) {
                     ((DefaultListSelectionModel)var4).moveLeadSelectionIndex(0);
                  }
               }

               var7 = var3.getCellRect(this.leadRow, this.leadColumn, false);
               if (var7 != null) {
                  var3.scrollRectToVisible(var7);
               }
            } else if (!this.inSelection) {
               this.moveWithinTableRange(var3, this.dx, this.dy);
               var3.changeSelection(this.leadRow, this.leadColumn, false, this.extend);
            } else {
               if (var3.getRowCount() <= 0 || var3.getColumnCount() <= 0) {
                  return;
               }

               if (this.moveWithinSelectedRange(var3, this.dx, this.dy, var4, var5)) {
                  if (var4.isSelectedIndex(this.leadRow)) {
                     var4.addSelectionInterval(this.leadRow, this.leadRow);
                  } else {
                     var4.removeSelectionInterval(this.leadRow, this.leadRow);
                  }

                  if (var5.isSelectedIndex(this.leadColumn)) {
                     var5.addSelectionInterval(this.leadColumn, this.leadColumn);
                  } else {
                     var5.removeSelectionInterval(this.leadColumn, this.leadColumn);
                  }

                  var7 = var3.getCellRect(this.leadRow, this.leadColumn, false);
                  if (var7 != null) {
                     var3.scrollRectToVisible(var7);
                  }
               } else {
                  var3.changeSelection(this.leadRow, this.leadColumn, false, false);
               }
            }
         }

      }

      public boolean isEnabled(Object var1) {
         String var2 = this.getName();
         if (!(var1 instanceof JTable) || !Boolean.TRUE.equals(((JTable)var1).getClientProperty("Table.isFileList")) || var2 != "selectNextColumn" && var2 != "selectNextColumnCell" && var2 != "selectNextColumnExtendSelection" && var2 != "selectNextColumnChangeLead" && var2 != "selectPreviousColumn" && var2 != "selectPreviousColumnCell" && var2 != "selectPreviousColumnExtendSelection" && var2 != "selectPreviousColumnChangeLead" && var2 != "scrollLeftChangeSelection" && var2 != "scrollLeftExtendSelection" && var2 != "scrollRightChangeSelection" && var2 != "scrollRightExtendSelection" && var2 != "selectFirstColumn" && var2 != "selectFirstColumnExtendSelection" && var2 != "selectLastColumn" && var2 != "selectLastColumnExtendSelection" && var2 != "selectNextRowCell" && var2 != "selectPreviousRowCell") {
            if (var2 == "cancel" && var1 instanceof JTable) {
               return ((JTable)var1).isEditing();
            } else if (var2 != "selectNextRowChangeLead" && var2 != "selectPreviousRowChangeLead") {
               if (var2 != "selectNextColumnChangeLead" && var2 != "selectPreviousColumnChangeLead") {
                  JTable var3;
                  if (var2 == "addToSelection" && var1 instanceof JTable) {
                     var3 = (JTable)var1;
                     int var4 = BasicTableUI.getAdjustedLead(var3, true);
                     int var5 = BasicTableUI.getAdjustedLead(var3, false);
                     return !var3.isEditing() && !var3.isCellSelected(var4, var5);
                  } else if (var2 == "focusHeader" && var1 instanceof JTable) {
                     var3 = (JTable)var1;
                     return var3.getTableHeader() != null;
                  } else {
                     return true;
                  }
               } else {
                  return var1 != null && ((JTable)var1).getColumnModel().getSelectionModel() instanceof DefaultListSelectionModel;
               }
            } else {
               return var1 != null && ((JTable)var1).getSelectionModel() instanceof DefaultListSelectionModel;
            }
         } else {
            return false;
         }
      }
   }
}
