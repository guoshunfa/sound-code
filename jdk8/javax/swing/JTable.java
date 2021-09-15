package javax.swing;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterAbortException;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleExtendedTable;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleTable;
import javax.accessibility.AccessibleTableModelChange;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleValue;
import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.TableUI;
import javax.swing.plaf.UIResource;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import sun.awt.AWTAccessor;
import sun.reflect.misc.ReflectUtil;
import sun.swing.PrintingStatus;
import sun.swing.SwingUtilities2;

public class JTable extends JComponent implements TableModelListener, Scrollable, TableColumnModelListener, ListSelectionListener, CellEditorListener, Accessible, RowSorterListener {
   private static final String uiClassID = "TableUI";
   public static final int AUTO_RESIZE_OFF = 0;
   public static final int AUTO_RESIZE_NEXT_COLUMN = 1;
   public static final int AUTO_RESIZE_SUBSEQUENT_COLUMNS = 2;
   public static final int AUTO_RESIZE_LAST_COLUMN = 3;
   public static final int AUTO_RESIZE_ALL_COLUMNS = 4;
   protected TableModel dataModel;
   protected TableColumnModel columnModel;
   protected ListSelectionModel selectionModel;
   protected JTableHeader tableHeader;
   protected int rowHeight;
   protected int rowMargin;
   protected Color gridColor;
   protected boolean showHorizontalLines;
   protected boolean showVerticalLines;
   protected int autoResizeMode;
   protected boolean autoCreateColumnsFromModel;
   protected Dimension preferredViewportSize;
   protected boolean rowSelectionAllowed;
   protected boolean cellSelectionEnabled;
   protected transient Component editorComp;
   protected transient TableCellEditor cellEditor;
   protected transient int editingColumn;
   protected transient int editingRow;
   protected transient Hashtable defaultRenderersByColumnClass;
   protected transient Hashtable defaultEditorsByColumnClass;
   protected Color selectionForeground;
   protected Color selectionBackground;
   private SizeSequence rowModel;
   private boolean dragEnabled;
   private boolean surrendersFocusOnKeystroke;
   private PropertyChangeListener editorRemover;
   private boolean columnSelectionAdjusting;
   private boolean rowSelectionAdjusting;
   private Throwable printError;
   private boolean isRowHeightSet;
   private boolean updateSelectionOnSort;
   private transient JTable.SortManager sortManager;
   private boolean ignoreSortChange;
   private boolean sorterChanged;
   private boolean autoCreateRowSorter;
   private boolean fillsViewportHeight;
   private DropMode dropMode;
   private transient JTable.DropLocation dropLocation;

   public JTable() {
      this((TableModel)null, (TableColumnModel)null, (ListSelectionModel)null);
   }

   public JTable(TableModel var1) {
      this(var1, (TableColumnModel)null, (ListSelectionModel)null);
   }

   public JTable(TableModel var1, TableColumnModel var2) {
      this(var1, var2, (ListSelectionModel)null);
   }

   public JTable(TableModel var1, TableColumnModel var2, ListSelectionModel var3) {
      this.editorRemover = null;
      this.dropMode = DropMode.USE_SELECTION;
      this.setLayout((LayoutManager)null);
      this.setFocusTraversalKeys(0, JComponent.getManagingFocusForwardTraversalKeys());
      this.setFocusTraversalKeys(1, JComponent.getManagingFocusBackwardTraversalKeys());
      if (var2 == null) {
         var2 = this.createDefaultColumnModel();
         this.autoCreateColumnsFromModel = true;
      }

      this.setColumnModel(var2);
      if (var3 == null) {
         var3 = this.createDefaultSelectionModel();
      }

      this.setSelectionModel(var3);
      if (var1 == null) {
         var1 = this.createDefaultDataModel();
      }

      this.setModel(var1);
      this.initializeLocalVars();
      this.updateUI();
   }

   public JTable(int var1, int var2) {
      this(new DefaultTableModel(var1, var2));
   }

   public JTable(Vector var1, Vector var2) {
      this(new DefaultTableModel(var1, var2));
   }

   public JTable(final Object[][] var1, final Object[] var2) {
      this(new AbstractTableModel() {
         public String getColumnName(int var1x) {
            return var2[var1x].toString();
         }

         public int getRowCount() {
            return var1.length;
         }

         public int getColumnCount() {
            return var2.length;
         }

         public Object getValueAt(int var1x, int var2x) {
            return var1[var1x][var2x];
         }

         public boolean isCellEditable(int var1x, int var2x) {
            return true;
         }

         public void setValueAt(Object var1x, int var2x, int var3) {
            var1[var2x][var3] = var1x;
            this.fireTableCellUpdated(var2x, var3);
         }
      });
   }

   public void addNotify() {
      super.addNotify();
      this.configureEnclosingScrollPane();
   }

   protected void configureEnclosingScrollPane() {
      Container var1 = SwingUtilities.getUnwrappedParent(this);
      if (var1 instanceof JViewport) {
         JViewport var2 = (JViewport)var1;
         Container var3 = var2.getParent();
         if (var3 instanceof JScrollPane) {
            JScrollPane var4 = (JScrollPane)var3;
            JViewport var5 = var4.getViewport();
            if (var5 == null || SwingUtilities.getUnwrappedView(var5) != this) {
               return;
            }

            var4.setColumnHeaderView(this.getTableHeader());
            this.configureEnclosingScrollPaneUI();
         }
      }

   }

   private void configureEnclosingScrollPaneUI() {
      Container var1 = SwingUtilities.getUnwrappedParent(this);
      if (var1 instanceof JViewport) {
         JViewport var2 = (JViewport)var1;
         Container var3 = var2.getParent();
         if (var3 instanceof JScrollPane) {
            JScrollPane var4 = (JScrollPane)var3;
            JViewport var5 = var4.getViewport();
            if (var5 == null || SwingUtilities.getUnwrappedView(var5) != this) {
               return;
            }

            Border var6 = var4.getBorder();
            if (var6 == null || var6 instanceof UIResource) {
               Border var7 = UIManager.getBorder("Table.scrollPaneBorder");
               if (var7 != null) {
                  var4.setBorder(var7);
               }
            }

            Component var10 = var4.getCorner("UPPER_TRAILING_CORNER");
            if (var10 == null || var10 instanceof UIResource) {
               var10 = null;

               try {
                  var10 = (Component)UIManager.get("Table.scrollPaneCornerComponent");
               } catch (Exception var9) {
               }

               var4.setCorner("UPPER_TRAILING_CORNER", var10);
            }
         }
      }

   }

   public void removeNotify() {
      KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener("permanentFocusOwner", this.editorRemover);
      this.editorRemover = null;
      this.unconfigureEnclosingScrollPane();
      super.removeNotify();
   }

   protected void unconfigureEnclosingScrollPane() {
      Container var1 = SwingUtilities.getUnwrappedParent(this);
      if (var1 instanceof JViewport) {
         JViewport var2 = (JViewport)var1;
         Container var3 = var2.getParent();
         if (var3 instanceof JScrollPane) {
            JScrollPane var4 = (JScrollPane)var3;
            JViewport var5 = var4.getViewport();
            if (var5 == null || SwingUtilities.getUnwrappedView(var5) != this) {
               return;
            }

            var4.setColumnHeaderView((Component)null);
            Component var6 = var4.getCorner("UPPER_TRAILING_CORNER");
            if (var6 instanceof UIResource) {
               var4.setCorner("UPPER_TRAILING_CORNER", (Component)null);
            }
         }
      }

   }

   void setUIProperty(String var1, Object var2) {
      if (var1 == "rowHeight") {
         if (!this.isRowHeightSet) {
            this.setRowHeight(((Number)var2).intValue());
            this.isRowHeightSet = false;
         }

      } else {
         super.setUIProperty(var1, var2);
      }
   }

   /** @deprecated */
   @Deprecated
   public static JScrollPane createScrollPaneForTable(JTable var0) {
      return new JScrollPane(var0);
   }

   public void setTableHeader(JTableHeader var1) {
      if (this.tableHeader != var1) {
         JTableHeader var2 = this.tableHeader;
         if (var2 != null) {
            var2.setTable((JTable)null);
         }

         this.tableHeader = var1;
         if (var1 != null) {
            var1.setTable(this);
         }

         this.firePropertyChange("tableHeader", var2, var1);
      }

   }

   public JTableHeader getTableHeader() {
      return this.tableHeader;
   }

   public void setRowHeight(int var1) {
      if (var1 <= 0) {
         throw new IllegalArgumentException("New row height less than 1");
      } else {
         int var2 = this.rowHeight;
         this.rowHeight = var1;
         this.rowModel = null;
         if (this.sortManager != null) {
            this.sortManager.modelRowSizes = null;
         }

         this.isRowHeightSet = true;
         this.resizeAndRepaint();
         this.firePropertyChange("rowHeight", var2, var1);
      }
   }

   public int getRowHeight() {
      return this.rowHeight;
   }

   private SizeSequence getRowModel() {
      if (this.rowModel == null) {
         this.rowModel = new SizeSequence(this.getRowCount(), this.getRowHeight());
      }

      return this.rowModel;
   }

   public void setRowHeight(int var1, int var2) {
      if (var2 <= 0) {
         throw new IllegalArgumentException("New row height less than 1");
      } else {
         this.getRowModel().setSize(var1, var2);
         if (this.sortManager != null) {
            this.sortManager.setViewRowHeight(var1, var2);
         }

         this.resizeAndRepaint();
      }
   }

   public int getRowHeight(int var1) {
      return this.rowModel == null ? this.getRowHeight() : this.rowModel.getSize(var1);
   }

   public void setRowMargin(int var1) {
      int var2 = this.rowMargin;
      this.rowMargin = var1;
      this.resizeAndRepaint();
      this.firePropertyChange("rowMargin", var2, var1);
   }

   public int getRowMargin() {
      return this.rowMargin;
   }

   public void setIntercellSpacing(Dimension var1) {
      this.setRowMargin(var1.height);
      this.getColumnModel().setColumnMargin(var1.width);
      this.resizeAndRepaint();
   }

   public Dimension getIntercellSpacing() {
      return new Dimension(this.getColumnModel().getColumnMargin(), this.rowMargin);
   }

   public void setGridColor(Color var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("New color is null");
      } else {
         Color var2 = this.gridColor;
         this.gridColor = var1;
         this.firePropertyChange("gridColor", var2, var1);
         this.repaint();
      }
   }

   public Color getGridColor() {
      return this.gridColor;
   }

   public void setShowGrid(boolean var1) {
      this.setShowHorizontalLines(var1);
      this.setShowVerticalLines(var1);
      this.repaint();
   }

   public void setShowHorizontalLines(boolean var1) {
      boolean var2 = this.showHorizontalLines;
      this.showHorizontalLines = var1;
      this.firePropertyChange("showHorizontalLines", var2, var1);
      this.repaint();
   }

   public void setShowVerticalLines(boolean var1) {
      boolean var2 = this.showVerticalLines;
      this.showVerticalLines = var1;
      this.firePropertyChange("showVerticalLines", var2, var1);
      this.repaint();
   }

   public boolean getShowHorizontalLines() {
      return this.showHorizontalLines;
   }

   public boolean getShowVerticalLines() {
      return this.showVerticalLines;
   }

   public void setAutoResizeMode(int var1) {
      if (var1 == 0 || var1 == 1 || var1 == 2 || var1 == 3 || var1 == 4) {
         int var2 = this.autoResizeMode;
         this.autoResizeMode = var1;
         this.resizeAndRepaint();
         if (this.tableHeader != null) {
            this.tableHeader.resizeAndRepaint();
         }

         this.firePropertyChange("autoResizeMode", var2, this.autoResizeMode);
      }

   }

   public int getAutoResizeMode() {
      return this.autoResizeMode;
   }

   public void setAutoCreateColumnsFromModel(boolean var1) {
      if (this.autoCreateColumnsFromModel != var1) {
         boolean var2 = this.autoCreateColumnsFromModel;
         this.autoCreateColumnsFromModel = var1;
         if (var1) {
            this.createDefaultColumnsFromModel();
         }

         this.firePropertyChange("autoCreateColumnsFromModel", var2, var1);
      }

   }

   public boolean getAutoCreateColumnsFromModel() {
      return this.autoCreateColumnsFromModel;
   }

   public void createDefaultColumnsFromModel() {
      TableModel var1 = this.getModel();
      if (var1 != null) {
         TableColumnModel var2 = this.getColumnModel();

         while(var2.getColumnCount() > 0) {
            var2.removeColumn(var2.getColumn(0));
         }

         for(int var3 = 0; var3 < var1.getColumnCount(); ++var3) {
            TableColumn var4 = new TableColumn(var3);
            this.addColumn(var4);
         }
      }

   }

   public void setDefaultRenderer(Class<?> var1, TableCellRenderer var2) {
      if (var2 != null) {
         this.defaultRenderersByColumnClass.put(var1, var2);
      } else {
         this.defaultRenderersByColumnClass.remove(var1);
      }

   }

   public TableCellRenderer getDefaultRenderer(Class<?> var1) {
      if (var1 == null) {
         return null;
      } else {
         Object var2 = this.defaultRenderersByColumnClass.get(var1);
         if (var2 != null) {
            return (TableCellRenderer)var2;
         } else {
            Class var3 = var1.getSuperclass();
            if (var3 == null && var1 != Object.class) {
               var3 = Object.class;
            }

            return this.getDefaultRenderer(var3);
         }
      }
   }

   public void setDefaultEditor(Class<?> var1, TableCellEditor var2) {
      if (var2 != null) {
         this.defaultEditorsByColumnClass.put(var1, var2);
      } else {
         this.defaultEditorsByColumnClass.remove(var1);
      }

   }

   public TableCellEditor getDefaultEditor(Class<?> var1) {
      if (var1 == null) {
         return null;
      } else {
         Object var2 = this.defaultEditorsByColumnClass.get(var1);
         return var2 != null ? (TableCellEditor)var2 : this.getDefaultEditor(var1.getSuperclass());
      }
   }

   public void setDragEnabled(boolean var1) {
      if (var1 && GraphicsEnvironment.isHeadless()) {
         throw new HeadlessException();
      } else {
         this.dragEnabled = var1;
      }
   }

   public boolean getDragEnabled() {
      return this.dragEnabled;
   }

   public final void setDropMode(DropMode var1) {
      if (var1 != null) {
         switch(var1) {
         case USE_SELECTION:
         case ON:
         case INSERT:
         case INSERT_ROWS:
         case INSERT_COLS:
         case ON_OR_INSERT:
         case ON_OR_INSERT_ROWS:
         case ON_OR_INSERT_COLS:
            this.dropMode = var1;
            return;
         }
      }

      throw new IllegalArgumentException(var1 + ": Unsupported drop mode for table");
   }

   public final DropMode getDropMode() {
      return this.dropMode;
   }

   JTable.DropLocation dropLocationForPoint(Point var1) {
      JTable.DropLocation var2 = null;
      int var3 = this.rowAtPoint(var1);
      int var4 = this.columnAtPoint(var1);
      boolean var5 = Boolean.TRUE == this.getClientProperty("Table.isFileList") && SwingUtilities2.pointOutsidePrefSize(this, var3, var4, var1);
      Rectangle var6 = this.getCellRect(var3, var4, true);
      boolean var9 = false;
      boolean var10 = this.getComponentOrientation().isLeftToRight();
      SwingUtilities2.Section var7;
      SwingUtilities2.Section var8;
      switch(this.dropMode) {
      case USE_SELECTION:
      case ON:
         if (var3 != -1 && var4 != -1 && !var5) {
            var2 = new JTable.DropLocation(var1, var3, var4, false, false);
         } else {
            var2 = new JTable.DropLocation(var1, -1, -1, false, false);
         }
         break;
      case INSERT:
         if (var3 == -1 && var4 == -1) {
            var2 = new JTable.DropLocation(var1, 0, 0, true, true);
         } else {
            var7 = SwingUtilities2.liesInHorizontal(var6, var1, var10, true);
            if (var3 == -1) {
               if (var7 == SwingUtilities2.Section.LEADING) {
                  var2 = new JTable.DropLocation(var1, this.getRowCount(), var4, true, true);
               } else if (var7 == SwingUtilities2.Section.TRAILING) {
                  var2 = new JTable.DropLocation(var1, this.getRowCount(), var4 + 1, true, true);
               } else {
                  var2 = new JTable.DropLocation(var1, this.getRowCount(), var4, true, false);
               }
            } else if (var7 != SwingUtilities2.Section.LEADING && var7 != SwingUtilities2.Section.TRAILING) {
               if (SwingUtilities2.liesInVertical(var6, var1, false) == SwingUtilities2.Section.TRAILING) {
                  ++var3;
               }

               var2 = new JTable.DropLocation(var1, var3, var4, true, false);
            } else {
               var8 = SwingUtilities2.liesInVertical(var6, var1, true);
               if (var8 == SwingUtilities2.Section.LEADING) {
                  var9 = true;
               } else if (var8 == SwingUtilities2.Section.TRAILING) {
                  ++var3;
                  var9 = true;
               }

               var2 = new JTable.DropLocation(var1, var3, var7 == SwingUtilities2.Section.TRAILING ? var4 + 1 : var4, var9, true);
            }
         }
         break;
      case INSERT_ROWS:
         if (var3 == -1 && var4 == -1) {
            var2 = new JTable.DropLocation(var1, -1, -1, false, false);
         } else if (var3 == -1) {
            var2 = new JTable.DropLocation(var1, this.getRowCount(), var4, true, false);
         } else {
            if (SwingUtilities2.liesInVertical(var6, var1, false) == SwingUtilities2.Section.TRAILING) {
               ++var3;
            }

            var2 = new JTable.DropLocation(var1, var3, var4, true, false);
         }
         break;
      case INSERT_COLS:
         if (var3 == -1) {
            var2 = new JTable.DropLocation(var1, -1, -1, false, false);
         } else if (var4 == -1) {
            var2 = new JTable.DropLocation(var1, this.getColumnCount(), var4, false, true);
         } else {
            if (SwingUtilities2.liesInHorizontal(var6, var1, var10, false) == SwingUtilities2.Section.TRAILING) {
               ++var4;
            }

            var2 = new JTable.DropLocation(var1, var3, var4, false, true);
         }
         break;
      case ON_OR_INSERT:
         if (var3 == -1 && var4 == -1) {
            var2 = new JTable.DropLocation(var1, 0, 0, true, true);
         } else {
            var7 = SwingUtilities2.liesInHorizontal(var6, var1, var10, true);
            if (var3 == -1) {
               if (var7 == SwingUtilities2.Section.LEADING) {
                  var2 = new JTable.DropLocation(var1, this.getRowCount(), var4, true, true);
               } else if (var7 == SwingUtilities2.Section.TRAILING) {
                  var2 = new JTable.DropLocation(var1, this.getRowCount(), var4 + 1, true, true);
               } else {
                  var2 = new JTable.DropLocation(var1, this.getRowCount(), var4, true, false);
               }
            } else {
               var8 = SwingUtilities2.liesInVertical(var6, var1, true);
               if (var8 == SwingUtilities2.Section.LEADING) {
                  var9 = true;
               } else if (var8 == SwingUtilities2.Section.TRAILING) {
                  ++var3;
                  var9 = true;
               }

               var2 = new JTable.DropLocation(var1, var3, var7 == SwingUtilities2.Section.TRAILING ? var4 + 1 : var4, var9, var7 != SwingUtilities2.Section.MIDDLE);
            }
         }
         break;
      case ON_OR_INSERT_ROWS:
         if (var3 == -1 && var4 == -1) {
            var2 = new JTable.DropLocation(var1, -1, -1, false, false);
         } else if (var3 == -1) {
            var2 = new JTable.DropLocation(var1, this.getRowCount(), var4, true, false);
         } else {
            var8 = SwingUtilities2.liesInVertical(var6, var1, true);
            if (var8 == SwingUtilities2.Section.LEADING) {
               var9 = true;
            } else if (var8 == SwingUtilities2.Section.TRAILING) {
               ++var3;
               var9 = true;
            }

            var2 = new JTable.DropLocation(var1, var3, var4, var9, false);
         }
         break;
      case ON_OR_INSERT_COLS:
         if (var3 == -1) {
            var2 = new JTable.DropLocation(var1, -1, -1, false, false);
         } else if (var4 == -1) {
            var2 = new JTable.DropLocation(var1, var3, this.getColumnCount(), false, true);
         } else {
            var7 = SwingUtilities2.liesInHorizontal(var6, var1, var10, true);
            if (var7 == SwingUtilities2.Section.LEADING) {
               var9 = true;
            } else if (var7 == SwingUtilities2.Section.TRAILING) {
               ++var4;
               var9 = true;
            }

            var2 = new JTable.DropLocation(var1, var3, var4, false, var9);
         }
         break;
      default:
         assert false : "Unexpected drop mode";
      }

      return var2;
   }

   Object setDropLocation(TransferHandler.DropLocation var1, Object var2, boolean var3) {
      Object var4 = null;
      JTable.DropLocation var5 = (JTable.DropLocation)var1;
      if (this.dropMode == DropMode.USE_SELECTION) {
         if (var5 == null) {
            if (!var3 && var2 != null) {
               this.clearSelection();
               int[] var6 = ((int[][])((int[][])var2))[0];
               int[] var7 = ((int[][])((int[][])var2))[1];
               int[] var8 = ((int[][])((int[][])var2))[2];
               int[] var9 = var6;
               int var10 = var6.length;

               int var11;
               int var12;
               for(var11 = 0; var11 < var10; ++var11) {
                  var12 = var9[var11];
                  this.addRowSelectionInterval(var12, var12);
               }

               var9 = var7;
               var10 = var7.length;

               for(var11 = 0; var11 < var10; ++var11) {
                  var12 = var9[var11];
                  this.addColumnSelectionInterval(var12, var12);
               }

               SwingUtilities2.setLeadAnchorWithoutSelection(this.getSelectionModel(), var8[1], var8[0]);
               SwingUtilities2.setLeadAnchorWithoutSelection(this.getColumnModel().getSelectionModel(), var8[3], var8[2]);
            }
         } else {
            if (this.dropLocation == null) {
               var4 = new int[][]{this.getSelectedRows(), this.getSelectedColumns(), {this.getAdjustedIndex(this.getSelectionModel().getAnchorSelectionIndex(), true), this.getAdjustedIndex(this.getSelectionModel().getLeadSelectionIndex(), true), this.getAdjustedIndex(this.getColumnModel().getSelectionModel().getAnchorSelectionIndex(), false), this.getAdjustedIndex(this.getColumnModel().getSelectionModel().getLeadSelectionIndex(), false)}};
            } else {
               var4 = var2;
            }

            if (var5.getRow() == -1) {
               this.clearSelectionAndLeadAnchor();
            } else {
               this.setRowSelectionInterval(var5.getRow(), var5.getRow());
               this.setColumnSelectionInterval(var5.getColumn(), var5.getColumn());
            }
         }
      }

      JTable.DropLocation var13 = this.dropLocation;
      this.dropLocation = var5;
      this.firePropertyChange("dropLocation", var13, this.dropLocation);
      return var4;
   }

   public final JTable.DropLocation getDropLocation() {
      return this.dropLocation;
   }

   public void setAutoCreateRowSorter(boolean var1) {
      boolean var2 = this.autoCreateRowSorter;
      this.autoCreateRowSorter = var1;
      if (var1) {
         this.setRowSorter(new TableRowSorter(this.getModel()));
      }

      this.firePropertyChange("autoCreateRowSorter", var2, var1);
   }

   public boolean getAutoCreateRowSorter() {
      return this.autoCreateRowSorter;
   }

   public void setUpdateSelectionOnSort(boolean var1) {
      if (this.updateSelectionOnSort != var1) {
         this.updateSelectionOnSort = var1;
         this.firePropertyChange("updateSelectionOnSort", !var1, var1);
      }

   }

   public boolean getUpdateSelectionOnSort() {
      return this.updateSelectionOnSort;
   }

   public void setRowSorter(RowSorter<? extends TableModel> var1) {
      RowSorter var2 = null;
      if (this.sortManager != null) {
         var2 = this.sortManager.sorter;
         this.sortManager.dispose();
         this.sortManager = null;
      }

      this.rowModel = null;
      this.clearSelectionAndLeadAnchor();
      if (var1 != null) {
         this.sortManager = new JTable.SortManager(var1);
      }

      this.resizeAndRepaint();
      this.firePropertyChange("rowSorter", var2, var1);
      this.firePropertyChange("sorter", var2, var1);
   }

   public RowSorter<? extends TableModel> getRowSorter() {
      return this.sortManager != null ? this.sortManager.sorter : null;
   }

   public void setSelectionMode(int var1) {
      this.clearSelection();
      this.getSelectionModel().setSelectionMode(var1);
      this.getColumnModel().getSelectionModel().setSelectionMode(var1);
   }

   public void setRowSelectionAllowed(boolean var1) {
      boolean var2 = this.rowSelectionAllowed;
      this.rowSelectionAllowed = var1;
      if (var2 != var1) {
         this.repaint();
      }

      this.firePropertyChange("rowSelectionAllowed", var2, var1);
   }

   public boolean getRowSelectionAllowed() {
      return this.rowSelectionAllowed;
   }

   public void setColumnSelectionAllowed(boolean var1) {
      boolean var2 = this.columnModel.getColumnSelectionAllowed();
      this.columnModel.setColumnSelectionAllowed(var1);
      if (var2 != var1) {
         this.repaint();
      }

      this.firePropertyChange("columnSelectionAllowed", var2, var1);
   }

   public boolean getColumnSelectionAllowed() {
      return this.columnModel.getColumnSelectionAllowed();
   }

   public void setCellSelectionEnabled(boolean var1) {
      this.setRowSelectionAllowed(var1);
      this.setColumnSelectionAllowed(var1);
      boolean var2 = this.cellSelectionEnabled;
      this.cellSelectionEnabled = var1;
      this.firePropertyChange("cellSelectionEnabled", var2, var1);
   }

   public boolean getCellSelectionEnabled() {
      return this.getRowSelectionAllowed() && this.getColumnSelectionAllowed();
   }

   public void selectAll() {
      if (this.isEditing()) {
         this.removeEditor();
      }

      if (this.getRowCount() > 0 && this.getColumnCount() > 0) {
         ListSelectionModel var3 = this.selectionModel;
         var3.setValueIsAdjusting(true);
         int var1 = this.getAdjustedIndex(var3.getLeadSelectionIndex(), true);
         int var2 = this.getAdjustedIndex(var3.getAnchorSelectionIndex(), true);
         this.setRowSelectionInterval(0, this.getRowCount() - 1);
         SwingUtilities2.setLeadAnchorWithoutSelection(var3, var1, var2);
         var3.setValueIsAdjusting(false);
         var3 = this.columnModel.getSelectionModel();
         var3.setValueIsAdjusting(true);
         var1 = this.getAdjustedIndex(var3.getLeadSelectionIndex(), false);
         var2 = this.getAdjustedIndex(var3.getAnchorSelectionIndex(), false);
         this.setColumnSelectionInterval(0, this.getColumnCount() - 1);
         SwingUtilities2.setLeadAnchorWithoutSelection(var3, var1, var2);
         var3.setValueIsAdjusting(false);
      }

   }

   public void clearSelection() {
      this.selectionModel.clearSelection();
      this.columnModel.getSelectionModel().clearSelection();
   }

   private void clearSelectionAndLeadAnchor() {
      this.selectionModel.setValueIsAdjusting(true);
      this.columnModel.getSelectionModel().setValueIsAdjusting(true);
      this.clearSelection();
      this.selectionModel.setAnchorSelectionIndex(-1);
      this.selectionModel.setLeadSelectionIndex(-1);
      this.columnModel.getSelectionModel().setAnchorSelectionIndex(-1);
      this.columnModel.getSelectionModel().setLeadSelectionIndex(-1);
      this.selectionModel.setValueIsAdjusting(false);
      this.columnModel.getSelectionModel().setValueIsAdjusting(false);
   }

   private int getAdjustedIndex(int var1, boolean var2) {
      int var3 = var2 ? this.getRowCount() : this.getColumnCount();
      return var1 < var3 ? var1 : -1;
   }

   private int boundRow(int var1) throws IllegalArgumentException {
      if (var1 >= 0 && var1 < this.getRowCount()) {
         return var1;
      } else {
         throw new IllegalArgumentException("Row index out of range");
      }
   }

   private int boundColumn(int var1) {
      if (var1 >= 0 && var1 < this.getColumnCount()) {
         return var1;
      } else {
         throw new IllegalArgumentException("Column index out of range");
      }
   }

   public void setRowSelectionInterval(int var1, int var2) {
      this.selectionModel.setSelectionInterval(this.boundRow(var1), this.boundRow(var2));
   }

   public void setColumnSelectionInterval(int var1, int var2) {
      this.columnModel.getSelectionModel().setSelectionInterval(this.boundColumn(var1), this.boundColumn(var2));
   }

   public void addRowSelectionInterval(int var1, int var2) {
      this.selectionModel.addSelectionInterval(this.boundRow(var1), this.boundRow(var2));
   }

   public void addColumnSelectionInterval(int var1, int var2) {
      this.columnModel.getSelectionModel().addSelectionInterval(this.boundColumn(var1), this.boundColumn(var2));
   }

   public void removeRowSelectionInterval(int var1, int var2) {
      this.selectionModel.removeSelectionInterval(this.boundRow(var1), this.boundRow(var2));
   }

   public void removeColumnSelectionInterval(int var1, int var2) {
      this.columnModel.getSelectionModel().removeSelectionInterval(this.boundColumn(var1), this.boundColumn(var2));
   }

   public int getSelectedRow() {
      return this.selectionModel.getMinSelectionIndex();
   }

   public int getSelectedColumn() {
      return this.columnModel.getSelectionModel().getMinSelectionIndex();
   }

   public int[] getSelectedRows() {
      int var1 = this.selectionModel.getMinSelectionIndex();
      int var2 = this.selectionModel.getMaxSelectionIndex();
      if (var1 != -1 && var2 != -1) {
         int[] var3 = new int[1 + (var2 - var1)];
         int var4 = 0;

         for(int var5 = var1; var5 <= var2; ++var5) {
            if (this.selectionModel.isSelectedIndex(var5)) {
               var3[var4++] = var5;
            }
         }

         int[] var6 = new int[var4];
         System.arraycopy(var3, 0, var6, 0, var4);
         return var6;
      } else {
         return new int[0];
      }
   }

   public int[] getSelectedColumns() {
      return this.columnModel.getSelectedColumns();
   }

   public int getSelectedRowCount() {
      int var1 = this.selectionModel.getMinSelectionIndex();
      int var2 = this.selectionModel.getMaxSelectionIndex();
      int var3 = 0;

      for(int var4 = var1; var4 <= var2; ++var4) {
         if (this.selectionModel.isSelectedIndex(var4)) {
            ++var3;
         }
      }

      return var3;
   }

   public int getSelectedColumnCount() {
      return this.columnModel.getSelectedColumnCount();
   }

   public boolean isRowSelected(int var1) {
      return this.selectionModel.isSelectedIndex(var1);
   }

   public boolean isColumnSelected(int var1) {
      return this.columnModel.getSelectionModel().isSelectedIndex(var1);
   }

   public boolean isCellSelected(int var1, int var2) {
      if (!this.getRowSelectionAllowed() && !this.getColumnSelectionAllowed()) {
         return false;
      } else {
         return (!this.getRowSelectionAllowed() || this.isRowSelected(var1)) && (!this.getColumnSelectionAllowed() || this.isColumnSelected(var2));
      }
   }

   private void changeSelectionModel(ListSelectionModel var1, int var2, boolean var3, boolean var4, boolean var5, int var6, boolean var7) {
      if (var4) {
         if (var3) {
            if (var7) {
               var1.addSelectionInterval(var6, var2);
            } else {
               var1.removeSelectionInterval(var6, var2);
               if (Boolean.TRUE == this.getClientProperty("Table.isFileList")) {
                  var1.addSelectionInterval(var2, var2);
                  var1.setAnchorSelectionIndex(var6);
               }
            }
         } else {
            var1.setSelectionInterval(var6, var2);
         }
      } else if (var3) {
         if (var5) {
            var1.removeSelectionInterval(var2, var2);
         } else {
            var1.addSelectionInterval(var2, var2);
         }
      } else {
         var1.setSelectionInterval(var2, var2);
      }

   }

   public void changeSelection(int var1, int var2, boolean var3, boolean var4) {
      ListSelectionModel var5 = this.getSelectionModel();
      ListSelectionModel var6 = this.getColumnModel().getSelectionModel();
      int var7 = this.getAdjustedIndex(var5.getAnchorSelectionIndex(), true);
      int var8 = this.getAdjustedIndex(var6.getAnchorSelectionIndex(), false);
      boolean var9 = true;
      if (var7 == -1) {
         if (this.getRowCount() > 0) {
            var7 = 0;
         }

         var9 = false;
      }

      if (var8 == -1) {
         if (this.getColumnCount() > 0) {
            var8 = 0;
         }

         var9 = false;
      }

      boolean var10 = this.isCellSelected(var1, var2);
      var9 = var9 && this.isCellSelected(var7, var8);
      this.changeSelectionModel(var6, var2, var3, var4, var10, var8, var9);
      this.changeSelectionModel(var5, var1, var3, var4, var10, var7, var9);
      if (this.getAutoscrolls()) {
         Rectangle var11 = this.getCellRect(var1, var2, false);
         if (var11 != null) {
            this.scrollRectToVisible(var11);
         }
      }

   }

   public Color getSelectionForeground() {
      return this.selectionForeground;
   }

   public void setSelectionForeground(Color var1) {
      Color var2 = this.selectionForeground;
      this.selectionForeground = var1;
      this.firePropertyChange("selectionForeground", var2, var1);
      this.repaint();
   }

   public Color getSelectionBackground() {
      return this.selectionBackground;
   }

   public void setSelectionBackground(Color var1) {
      Color var2 = this.selectionBackground;
      this.selectionBackground = var1;
      this.firePropertyChange("selectionBackground", var2, var1);
      this.repaint();
   }

   public TableColumn getColumn(Object var1) {
      TableColumnModel var2 = this.getColumnModel();
      int var3 = var2.getColumnIndex(var1);
      return var2.getColumn(var3);
   }

   public int convertColumnIndexToModel(int var1) {
      return SwingUtilities2.convertColumnIndexToModel(this.getColumnModel(), var1);
   }

   public int convertColumnIndexToView(int var1) {
      return SwingUtilities2.convertColumnIndexToView(this.getColumnModel(), var1);
   }

   public int convertRowIndexToView(int var1) {
      RowSorter var2 = this.getRowSorter();
      return var2 != null ? var2.convertRowIndexToView(var1) : var1;
   }

   public int convertRowIndexToModel(int var1) {
      RowSorter var2 = this.getRowSorter();
      return var2 != null ? var2.convertRowIndexToModel(var1) : var1;
   }

   public int getRowCount() {
      RowSorter var1 = this.getRowSorter();
      return var1 != null ? var1.getViewRowCount() : this.getModel().getRowCount();
   }

   public int getColumnCount() {
      return this.getColumnModel().getColumnCount();
   }

   public String getColumnName(int var1) {
      return this.getModel().getColumnName(this.convertColumnIndexToModel(var1));
   }

   public Class<?> getColumnClass(int var1) {
      return this.getModel().getColumnClass(this.convertColumnIndexToModel(var1));
   }

   public Object getValueAt(int var1, int var2) {
      return this.getModel().getValueAt(this.convertRowIndexToModel(var1), this.convertColumnIndexToModel(var2));
   }

   public void setValueAt(Object var1, int var2, int var3) {
      this.getModel().setValueAt(var1, this.convertRowIndexToModel(var2), this.convertColumnIndexToModel(var3));
   }

   public boolean isCellEditable(int var1, int var2) {
      return this.getModel().isCellEditable(this.convertRowIndexToModel(var1), this.convertColumnIndexToModel(var2));
   }

   public void addColumn(TableColumn var1) {
      if (var1.getHeaderValue() == null) {
         int var2 = var1.getModelIndex();
         String var3 = this.getModel().getColumnName(var2);
         var1.setHeaderValue(var3);
      }

      this.getColumnModel().addColumn(var1);
   }

   public void removeColumn(TableColumn var1) {
      this.getColumnModel().removeColumn(var1);
   }

   public void moveColumn(int var1, int var2) {
      this.getColumnModel().moveColumn(var1, var2);
   }

   public int columnAtPoint(Point var1) {
      int var2 = var1.x;
      if (!this.getComponentOrientation().isLeftToRight()) {
         var2 = this.getWidth() - var2 - 1;
      }

      return this.getColumnModel().getColumnIndexAtX(var2);
   }

   public int rowAtPoint(Point var1) {
      int var2 = var1.y;
      int var3 = this.rowModel == null ? var2 / this.getRowHeight() : this.rowModel.getIndex(var2);
      if (var3 < 0) {
         return -1;
      } else {
         return var3 >= this.getRowCount() ? -1 : var3;
      }
   }

   public Rectangle getCellRect(int var1, int var2, boolean var3) {
      Rectangle var4 = new Rectangle();
      boolean var5 = true;
      if (var1 < 0) {
         var5 = false;
      } else if (var1 >= this.getRowCount()) {
         var4.y = this.getHeight();
         var5 = false;
      } else {
         var4.height = this.getRowHeight(var1);
         var4.y = this.rowModel == null ? var1 * var4.height : this.rowModel.getPosition(var1);
      }

      int var7;
      if (var2 < 0) {
         if (!this.getComponentOrientation().isLeftToRight()) {
            var4.x = this.getWidth();
         }

         var5 = false;
      } else if (var2 >= this.getColumnCount()) {
         if (this.getComponentOrientation().isLeftToRight()) {
            var4.x = this.getWidth();
         }

         var5 = false;
      } else {
         TableColumnModel var6 = this.getColumnModel();
         if (this.getComponentOrientation().isLeftToRight()) {
            for(var7 = 0; var7 < var2; ++var7) {
               var4.x += var6.getColumn(var7).getWidth();
            }
         } else {
            for(var7 = var6.getColumnCount() - 1; var7 > var2; --var7) {
               var4.x += var6.getColumn(var7).getWidth();
            }
         }

         var4.width = var6.getColumn(var2).getWidth();
      }

      if (var5 && !var3) {
         int var8 = Math.min(this.getRowMargin(), var4.height);
         var7 = Math.min(this.getColumnModel().getColumnMargin(), var4.width);
         var4.setBounds(var4.x + var7 / 2, var4.y + var8 / 2, var4.width - var7, var4.height - var8);
      }

      return var4;
   }

   private int viewIndexForColumn(TableColumn var1) {
      TableColumnModel var2 = this.getColumnModel();

      for(int var3 = 0; var3 < var2.getColumnCount(); ++var3) {
         if (var2.getColumn(var3) == var1) {
            return var3;
         }
      }

      return -1;
   }

   public void doLayout() {
      TableColumn var1 = this.getResizingColumn();
      if (var1 == null) {
         this.setWidthsFromPreferredWidths(false);
      } else {
         int var2 = this.viewIndexForColumn(var1);
         int var3 = this.getWidth() - this.getColumnModel().getTotalColumnWidth();
         this.accommodateDelta(var2, var3);
         var3 = this.getWidth() - this.getColumnModel().getTotalColumnWidth();
         if (var3 != 0) {
            var1.setWidth(var1.getWidth() + var3);
         }

         this.setWidthsFromPreferredWidths(true);
      }

      super.doLayout();
   }

   private TableColumn getResizingColumn() {
      return this.tableHeader == null ? null : this.tableHeader.getResizingColumn();
   }

   /** @deprecated */
   @Deprecated
   public void sizeColumnsToFit(boolean var1) {
      int var2 = this.autoResizeMode;
      this.setAutoResizeMode(var1 ? 3 : 4);
      this.sizeColumnsToFit(-1);
      this.setAutoResizeMode(var2);
   }

   public void sizeColumnsToFit(int var1) {
      if (var1 == -1) {
         this.setWidthsFromPreferredWidths(false);
      } else if (this.autoResizeMode == 0) {
         TableColumn var2 = this.getColumnModel().getColumn(var1);
         var2.setPreferredWidth(var2.getWidth());
      } else {
         int var3 = this.getWidth() - this.getColumnModel().getTotalColumnWidth();
         this.accommodateDelta(var1, var3);
         this.setWidthsFromPreferredWidths(true);
      }

   }

   private void setWidthsFromPreferredWidths(final boolean var1) {
      int var2 = this.getWidth();
      int var3 = this.getPreferredSize().width;
      int var4 = !var1 ? var2 : var3;
      final TableColumnModel var5 = this.columnModel;
      JTable.Resizable3 var6 = new JTable.Resizable3() {
         public int getElementCount() {
            return var5.getColumnCount();
         }

         public int getLowerBoundAt(int var1x) {
            return var5.getColumn(var1x).getMinWidth();
         }

         public int getUpperBoundAt(int var1x) {
            return var5.getColumn(var1x).getMaxWidth();
         }

         public int getMidPointAt(int var1x) {
            return !var1 ? var5.getColumn(var1x).getPreferredWidth() : var5.getColumn(var1x).getWidth();
         }

         public void setSizeAt(int var1x, int var2) {
            if (!var1) {
               var5.getColumn(var2).setWidth(var1x);
            } else {
               var5.getColumn(var2).setPreferredWidth(var1x);
            }

         }
      };
      this.adjustSizes((long)var4, var6, var1);
   }

   private void accommodateDelta(int var1, int var2) {
      int var3 = this.getColumnCount();
      final int var4;
      final int var5;
      switch(this.autoResizeMode) {
      case 1:
         var4 = var1 + 1;
         var5 = Math.min(var4 + 1, var3);
         break;
      case 2:
         var4 = var1 + 1;
         var5 = var3;
         break;
      case 3:
         var4 = var3 - 1;
         var5 = var4 + 1;
         break;
      case 4:
         var4 = 0;
         var5 = var3;
         break;
      default:
         return;
      }

      final TableColumnModel var8 = this.columnModel;
      JTable.Resizable3 var9 = new JTable.Resizable3() {
         public int getElementCount() {
            return var5 - var4;
         }

         public int getLowerBoundAt(int var1) {
            return var8.getColumn(var1 + var4).getMinWidth();
         }

         public int getUpperBoundAt(int var1) {
            return var8.getColumn(var1 + var4).getMaxWidth();
         }

         public int getMidPointAt(int var1) {
            return var8.getColumn(var1 + var4).getWidth();
         }

         public void setSizeAt(int var1, int var2) {
            var8.getColumn(var2 + var4).setWidth(var1);
         }
      };
      int var10 = 0;

      for(int var11 = var4; var11 < var5; ++var11) {
         TableColumn var12 = this.columnModel.getColumn(var11);
         int var13 = var12.getWidth();
         var10 += var13;
      }

      this.adjustSizes((long)(var10 + var2), var9, false);
   }

   private void adjustSizes(long var1, final JTable.Resizable3 var3, boolean var4) {
      int var5 = var3.getElementCount();
      long var6 = 0L;

      for(int var8 = 0; var8 < var5; ++var8) {
         var6 += (long)var3.getMidPointAt(var8);
      }

      JTable.Resizable2 var9;
      if (var1 < var6 == !var4) {
         var9 = new JTable.Resizable2() {
            public int getElementCount() {
               return var3.getElementCount();
            }

            public int getLowerBoundAt(int var1) {
               return var3.getLowerBoundAt(var1);
            }

            public int getUpperBoundAt(int var1) {
               return var3.getMidPointAt(var1);
            }

            public void setSizeAt(int var1, int var2) {
               var3.setSizeAt(var1, var2);
            }
         };
      } else {
         var9 = new JTable.Resizable2() {
            public int getElementCount() {
               return var3.getElementCount();
            }

            public int getLowerBoundAt(int var1) {
               return var3.getMidPointAt(var1);
            }

            public int getUpperBoundAt(int var1) {
               return var3.getUpperBoundAt(var1);
            }

            public void setSizeAt(int var1, int var2) {
               var3.setSizeAt(var1, var2);
            }
         };
      }

      this.adjustSizes(var1, var9, !var4);
   }

   private void adjustSizes(long var1, JTable.Resizable2 var3, boolean var4) {
      long var5 = 0L;
      long var7 = 0L;

      int var9;
      for(var9 = 0; var9 < var3.getElementCount(); ++var9) {
         var5 += (long)var3.getLowerBoundAt(var9);
         var7 += (long)var3.getUpperBoundAt(var9);
      }

      if (var4) {
         var1 = Math.min(Math.max(var5, var1), var7);
      }

      for(var9 = 0; var9 < var3.getElementCount(); ++var9) {
         int var10 = var3.getLowerBoundAt(var9);
         int var11 = var3.getUpperBoundAt(var9);
         int var12;
         if (var5 == var7) {
            var12 = var10;
         } else {
            double var13 = (double)(var1 - var5) / (double)(var7 - var5);
            var12 = (int)Math.round((double)var10 + var13 * (double)(var11 - var10));
         }

         var3.setSizeAt(var12, var9);
         var1 -= (long)var12;
         var5 -= (long)var10;
         var7 -= (long)var11;
      }

   }

   public String getToolTipText(MouseEvent var1) {
      String var2 = null;
      Point var3 = var1.getPoint();
      int var4 = this.columnAtPoint(var3);
      int var5 = this.rowAtPoint(var3);
      if (var4 != -1 && var5 != -1) {
         TableCellRenderer var6 = this.getCellRenderer(var5, var4);
         Component var7 = this.prepareRenderer(var6, var5, var4);
         if (var7 instanceof JComponent) {
            Rectangle var8 = this.getCellRect(var5, var4, false);
            var3.translate(-var8.x, -var8.y);
            MouseEvent var9 = new MouseEvent(var7, var1.getID(), var1.getWhen(), var1.getModifiers(), var3.x, var3.y, var1.getXOnScreen(), var1.getYOnScreen(), var1.getClickCount(), var1.isPopupTrigger(), 0);
            AWTAccessor.MouseEventAccessor var10 = AWTAccessor.getMouseEventAccessor();
            var10.setCausedByTouchEvent(var9, var10.isCausedByTouchEvent(var1));
            var2 = ((JComponent)var7).getToolTipText(var9);
         }
      }

      if (var2 == null) {
         var2 = this.getToolTipText();
      }

      return var2;
   }

   public void setSurrendersFocusOnKeystroke(boolean var1) {
      this.surrendersFocusOnKeystroke = var1;
   }

   public boolean getSurrendersFocusOnKeystroke() {
      return this.surrendersFocusOnKeystroke;
   }

   public boolean editCellAt(int var1, int var2) {
      return this.editCellAt(var1, var2, (EventObject)null);
   }

   public boolean editCellAt(int var1, int var2, EventObject var3) {
      if (this.cellEditor != null && !this.cellEditor.stopCellEditing()) {
         return false;
      } else if (var1 >= 0 && var1 < this.getRowCount() && var2 >= 0 && var2 < this.getColumnCount()) {
         if (!this.isCellEditable(var1, var2)) {
            return false;
         } else {
            if (this.editorRemover == null) {
               KeyboardFocusManager var4 = KeyboardFocusManager.getCurrentKeyboardFocusManager();
               this.editorRemover = new JTable.CellEditorRemover(var4);
               var4.addPropertyChangeListener("permanentFocusOwner", this.editorRemover);
            }

            TableCellEditor var5 = this.getCellEditor(var1, var2);
            if (var5 != null && var5.isCellEditable(var3)) {
               this.editorComp = this.prepareEditor(var5, var1, var2);
               if (this.editorComp == null) {
                  this.removeEditor();
                  return false;
               } else {
                  this.editorComp.setBounds(this.getCellRect(var1, var2, false));
                  this.add(this.editorComp);
                  this.editorComp.validate();
                  this.editorComp.repaint();
                  this.setCellEditor(var5);
                  this.setEditingRow(var1);
                  this.setEditingColumn(var2);
                  var5.addCellEditorListener(this);
                  return true;
               }
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public boolean isEditing() {
      return this.cellEditor != null;
   }

   public Component getEditorComponent() {
      return this.editorComp;
   }

   public int getEditingColumn() {
      return this.editingColumn;
   }

   public int getEditingRow() {
      return this.editingRow;
   }

   public TableUI getUI() {
      return (TableUI)this.ui;
   }

   public void setUI(TableUI var1) {
      if (this.ui != var1) {
         super.setUI(var1);
         this.repaint();
      }

   }

   public void updateUI() {
      TableColumnModel var1 = this.getColumnModel();

      for(int var2 = 0; var2 < var1.getColumnCount(); ++var2) {
         TableColumn var3 = var1.getColumn(var2);
         SwingUtilities.updateRendererOrEditorUI(var3.getCellRenderer());
         SwingUtilities.updateRendererOrEditorUI(var3.getCellEditor());
         SwingUtilities.updateRendererOrEditorUI(var3.getHeaderRenderer());
      }

      Enumeration var4 = this.defaultRenderersByColumnClass.elements();

      while(var4.hasMoreElements()) {
         SwingUtilities.updateRendererOrEditorUI(var4.nextElement());
      }

      Enumeration var5 = this.defaultEditorsByColumnClass.elements();

      while(var5.hasMoreElements()) {
         SwingUtilities.updateRendererOrEditorUI(var5.nextElement());
      }

      if (this.tableHeader != null && this.tableHeader.getParent() == null) {
         this.tableHeader.updateUI();
      }

      this.configureEnclosingScrollPaneUI();
      this.setUI((TableUI)UIManager.getUI(this));
   }

   public String getUIClassID() {
      return "TableUI";
   }

   public void setModel(TableModel var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Cannot set a null TableModel");
      } else {
         if (this.dataModel != var1) {
            TableModel var2 = this.dataModel;
            if (var2 != null) {
               var2.removeTableModelListener(this);
            }

            this.dataModel = var1;
            var1.addTableModelListener(this);
            this.tableChanged(new TableModelEvent(var1, -1));
            this.firePropertyChange("model", var2, var1);
            if (this.getAutoCreateRowSorter()) {
               this.setRowSorter(new TableRowSorter(var1));
            }
         }

      }
   }

   public TableModel getModel() {
      return this.dataModel;
   }

   public void setColumnModel(TableColumnModel var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Cannot set a null ColumnModel");
      } else {
         TableColumnModel var2 = this.columnModel;
         if (var1 != var2) {
            if (var2 != null) {
               var2.removeColumnModelListener(this);
            }

            this.columnModel = var1;
            var1.addColumnModelListener(this);
            if (this.tableHeader != null) {
               this.tableHeader.setColumnModel(var1);
            }

            this.firePropertyChange("columnModel", var2, var1);
            this.resizeAndRepaint();
         }

      }
   }

   public TableColumnModel getColumnModel() {
      return this.columnModel;
   }

   public void setSelectionModel(ListSelectionModel var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Cannot set a null SelectionModel");
      } else {
         ListSelectionModel var2 = this.selectionModel;
         if (var1 != var2) {
            if (var2 != null) {
               var2.removeListSelectionListener(this);
            }

            this.selectionModel = var1;
            var1.addListSelectionListener(this);
            this.firePropertyChange("selectionModel", var2, var1);
            this.repaint();
         }

      }
   }

   public ListSelectionModel getSelectionModel() {
      return this.selectionModel;
   }

   public void sorterChanged(RowSorterEvent var1) {
      if (var1.getType() == RowSorterEvent.Type.SORT_ORDER_CHANGED) {
         JTableHeader var2 = this.getTableHeader();
         if (var2 != null) {
            var2.repaint();
         }
      } else if (var1.getType() == RowSorterEvent.Type.SORTED) {
         this.sorterChanged = true;
         if (!this.ignoreSortChange) {
            this.sortedTableChanged(var1, (TableModelEvent)null);
         }
      }

   }

   private void sortedTableChanged(RowSorterEvent var1, TableModelEvent var2) {
      int var3 = -1;
      JTable.ModelChange var4 = var2 != null ? new JTable.ModelChange(var2) : null;
      if ((var4 == null || !var4.allRowsChanged) && this.editingRow != -1) {
         var3 = this.convertRowIndexToModel(var1, this.editingRow);
      }

      this.sortManager.prepareForChange(var1, var4);
      if (var2 != null) {
         if (var4.type == 0) {
            this.repaintSortedRows(var4);
         }

         this.notifySorter(var4);
         if (var4.type != 0) {
            this.sorterChanged = true;
         }
      } else {
         this.sorterChanged = true;
      }

      this.sortManager.processChange(var1, var4, this.sorterChanged);
      if (this.sorterChanged) {
         if (this.editingRow != -1) {
            int var5 = var3 == -1 ? -1 : this.convertRowIndexToView(var3, var4);
            this.restoreSortingEditingRow(var5);
         }

         if (var2 == null || var4.type != 0) {
            this.resizeAndRepaint();
         }
      }

      if (var4 != null && var4.allRowsChanged) {
         this.clearSelectionAndLeadAnchor();
         this.resizeAndRepaint();
      }

   }

   private void repaintSortedRows(JTable.ModelChange var1) {
      if (var1.startModelIndex <= var1.endModelIndex && var1.startModelIndex + 10 >= var1.endModelIndex) {
         int var2 = var1.event.getColumn();
         int var3;
         if (var2 == -1) {
            var3 = 0;
         } else {
            var3 = this.convertColumnIndexToView(var2);
            if (var3 == -1) {
               return;
            }
         }

         int var4 = var1.startModelIndex;

         while(var4 <= var1.endModelIndex) {
            int var5 = this.convertRowIndexToView(var4++);
            if (var5 != -1) {
               Rectangle var6 = this.getCellRect(var5, var3, false);
               int var7 = var6.x;
               int var8 = var6.width;
               if (var2 == -1) {
                  var7 = 0;
                  var8 = this.getWidth();
               }

               this.repaint(var7, var6.y, var8, var6.height);
            }
         }

      } else {
         this.repaint();
      }
   }

   private void restoreSortingSelection(int[] var1, int var2, JTable.ModelChange var3) {
      int var4;
      for(var4 = var1.length - 1; var4 >= 0; --var4) {
         var1[var4] = this.convertRowIndexToView(var1[var4], var3);
      }

      var2 = this.convertRowIndexToView(var2, var3);
      if (var1.length != 0 && (var1.length != 1 || var1[0] != this.getSelectedRow())) {
         this.selectionModel.setValueIsAdjusting(true);
         this.selectionModel.clearSelection();

         for(var4 = var1.length - 1; var4 >= 0; --var4) {
            if (var1[var4] != -1) {
               this.selectionModel.addSelectionInterval(var1[var4], var1[var4]);
            }
         }

         SwingUtilities2.setLeadAnchorWithoutSelection(this.selectionModel, var2, var2);
         this.selectionModel.setValueIsAdjusting(false);
      }
   }

   private void restoreSortingEditingRow(int var1) {
      if (var1 == -1) {
         TableCellEditor var2 = this.getCellEditor();
         if (var2 != null) {
            var2.cancelCellEditing();
            if (this.getCellEditor() != null) {
               this.removeEditor();
            }
         }
      } else {
         this.editingRow = var1;
         this.repaint();
      }

   }

   private void notifySorter(JTable.ModelChange var1) {
      try {
         this.ignoreSortChange = true;
         this.sorterChanged = false;
         switch(var1.type) {
         case -1:
            this.sortManager.sorter.rowsDeleted(var1.startModelIndex, var1.endModelIndex);
            break;
         case 0:
            if (var1.event.getLastRow() == Integer.MAX_VALUE) {
               this.sortManager.sorter.allRowsChanged();
            } else if (var1.event.getColumn() == -1) {
               this.sortManager.sorter.rowsUpdated(var1.startModelIndex, var1.endModelIndex);
            } else {
               this.sortManager.sorter.rowsUpdated(var1.startModelIndex, var1.endModelIndex, var1.event.getColumn());
            }
            break;
         case 1:
            this.sortManager.sorter.rowsInserted(var1.startModelIndex, var1.endModelIndex);
         }
      } finally {
         this.ignoreSortChange = false;
      }

   }

   private int convertRowIndexToView(int var1, JTable.ModelChange var2) {
      if (var1 < 0) {
         return -1;
      } else {
         if (var2 != null && var1 >= var2.startModelIndex) {
            if (var2.type == 1) {
               if (var1 + var2.length >= var2.modelRowCount) {
                  return -1;
               }

               return this.sortManager.sorter.convertRowIndexToView(var1 + var2.length);
            }

            if (var2.type == -1) {
               if (var1 <= var2.endModelIndex) {
                  return -1;
               }

               if (var1 - var2.length >= var2.modelRowCount) {
                  return -1;
               }

               return this.sortManager.sorter.convertRowIndexToView(var1 - var2.length);
            }
         }

         return var1 >= this.getModel().getRowCount() ? -1 : this.sortManager.sorter.convertRowIndexToView(var1);
      }
   }

   private int[] convertSelectionToModel(RowSorterEvent var1) {
      int[] var2 = this.getSelectedRows();

      for(int var3 = var2.length - 1; var3 >= 0; --var3) {
         var2[var3] = this.convertRowIndexToModel(var1, var2[var3]);
      }

      return var2;
   }

   private int convertRowIndexToModel(RowSorterEvent var1, int var2) {
      if (var1 != null) {
         return var1.getPreviousRowCount() == 0 ? var2 : var1.convertPreviousRowIndexToModel(var2);
      } else {
         return var2 >= 0 && var2 < this.getRowCount() ? this.convertRowIndexToModel(var2) : -1;
      }
   }

   public void tableChanged(TableModelEvent var1) {
      if (var1 != null && var1.getFirstRow() != -1) {
         if (this.sortManager != null) {
            this.sortedTableChanged((RowSorterEvent)null, var1);
         } else {
            if (this.rowModel != null) {
               this.repaint();
            }

            if (var1.getType() == 1) {
               this.tableRowsInserted(var1);
            } else if (var1.getType() == -1) {
               this.tableRowsDeleted(var1);
            } else {
               int var2 = var1.getColumn();
               int var3 = var1.getFirstRow();
               int var4 = var1.getLastRow();
               Rectangle var5;
               if (var2 == -1) {
                  var5 = new Rectangle(0, var3 * this.getRowHeight(), this.getColumnModel().getTotalColumnWidth(), 0);
               } else {
                  int var6 = this.convertColumnIndexToView(var2);
                  var5 = this.getCellRect(var3, var6, false);
               }

               if (var4 != Integer.MAX_VALUE) {
                  var5.height = (var4 - var3 + 1) * this.getRowHeight();
                  this.repaint(var5.x, var5.y, var5.width, var5.height);
               } else {
                  this.clearSelectionAndLeadAnchor();
                  this.resizeAndRepaint();
                  this.rowModel = null;
               }

            }
         }
      } else {
         this.clearSelectionAndLeadAnchor();
         this.rowModel = null;
         if (this.sortManager != null) {
            try {
               this.ignoreSortChange = true;
               this.sortManager.sorter.modelStructureChanged();
            } finally {
               this.ignoreSortChange = false;
            }

            this.sortManager.allChanged();
         }

         if (this.getAutoCreateColumnsFromModel()) {
            this.createDefaultColumnsFromModel();
         } else {
            this.resizeAndRepaint();
         }
      }
   }

   private void tableRowsInserted(TableModelEvent var1) {
      int var2 = var1.getFirstRow();
      int var3 = var1.getLastRow();
      if (var2 < 0) {
         var2 = 0;
      }

      if (var3 < 0) {
         var3 = this.getRowCount() - 1;
      }

      int var4 = var3 - var2 + 1;
      this.selectionModel.insertIndexInterval(var2, var4, true);
      if (this.rowModel != null) {
         this.rowModel.insertEntries(var2, var4, this.getRowHeight());
      }

      int var5 = this.getRowHeight();
      Rectangle var6 = new Rectangle(0, var2 * var5, this.getColumnModel().getTotalColumnWidth(), (this.getRowCount() - var2) * var5);
      this.revalidate();
      this.repaint(var6);
   }

   private void tableRowsDeleted(TableModelEvent var1) {
      int var2 = var1.getFirstRow();
      int var3 = var1.getLastRow();
      if (var2 < 0) {
         var2 = 0;
      }

      if (var3 < 0) {
         var3 = this.getRowCount() - 1;
      }

      int var4 = var3 - var2 + 1;
      int var5 = this.getRowCount() + var4;
      this.selectionModel.removeIndexInterval(var2, var3);
      if (this.rowModel != null) {
         this.rowModel.removeEntries(var2, var4);
      }

      int var6 = this.getRowHeight();
      Rectangle var7 = new Rectangle(0, var2 * var6, this.getColumnModel().getTotalColumnWidth(), (var5 - var2) * var6);
      this.revalidate();
      this.repaint(var7);
   }

   public void columnAdded(TableColumnModelEvent var1) {
      if (this.isEditing()) {
         this.removeEditor();
      }

      this.resizeAndRepaint();
   }

   public void columnRemoved(TableColumnModelEvent var1) {
      if (this.isEditing()) {
         this.removeEditor();
      }

      this.resizeAndRepaint();
   }

   public void columnMoved(TableColumnModelEvent var1) {
      if (this.isEditing() && !this.getCellEditor().stopCellEditing()) {
         this.getCellEditor().cancelCellEditing();
      }

      this.repaint();
   }

   public void columnMarginChanged(ChangeEvent var1) {
      if (this.isEditing() && !this.getCellEditor().stopCellEditing()) {
         this.getCellEditor().cancelCellEditing();
      }

      TableColumn var2 = this.getResizingColumn();
      if (var2 != null && this.autoResizeMode == 0) {
         var2.setPreferredWidth(var2.getWidth());
      }

      this.resizeAndRepaint();
   }

   private int limit(int var1, int var2, int var3) {
      return Math.min(var3, Math.max(var1, var2));
   }

   public void columnSelectionChanged(ListSelectionEvent var1) {
      boolean var2 = var1.getValueIsAdjusting();
      if (this.columnSelectionAdjusting && !var2) {
         this.columnSelectionAdjusting = false;
      } else {
         this.columnSelectionAdjusting = var2;
         if (this.getRowCount() > 0 && this.getColumnCount() > 0) {
            int var3 = this.limit(var1.getFirstIndex(), 0, this.getColumnCount() - 1);
            int var4 = this.limit(var1.getLastIndex(), 0, this.getColumnCount() - 1);
            int var5 = 0;
            int var6 = this.getRowCount() - 1;
            if (this.getRowSelectionAllowed()) {
               var5 = this.selectionModel.getMinSelectionIndex();
               var6 = this.selectionModel.getMaxSelectionIndex();
               int var7 = this.getAdjustedIndex(this.selectionModel.getLeadSelectionIndex(), true);
               if (var5 != -1 && var6 != -1) {
                  if (var7 != -1) {
                     var5 = Math.min(var5, var7);
                     var6 = Math.max(var6, var7);
                  }
               } else {
                  if (var7 == -1) {
                     return;
                  }

                  var6 = var7;
                  var5 = var7;
               }
            }

            Rectangle var10 = this.getCellRect(var5, var3, false);
            Rectangle var8 = this.getCellRect(var6, var4, false);
            Rectangle var9 = var10.union(var8);
            this.repaint(var9);
         }
      }
   }

   public void valueChanged(ListSelectionEvent var1) {
      if (this.sortManager != null) {
         this.sortManager.viewSelectionChanged(var1);
      }

      boolean var2 = var1.getValueIsAdjusting();
      if (this.rowSelectionAdjusting && !var2) {
         this.rowSelectionAdjusting = false;
      } else {
         this.rowSelectionAdjusting = var2;
         if (this.getRowCount() > 0 && this.getColumnCount() > 0) {
            int var3 = this.limit(var1.getFirstIndex(), 0, this.getRowCount() - 1);
            int var4 = this.limit(var1.getLastIndex(), 0, this.getRowCount() - 1);
            Rectangle var5 = this.getCellRect(var3, 0, false);
            Rectangle var6 = this.getCellRect(var4, this.getColumnCount() - 1, false);
            Rectangle var7 = var5.union(var6);
            this.repaint(var7);
         }
      }
   }

   public void editingStopped(ChangeEvent var1) {
      TableCellEditor var2 = this.getCellEditor();
      if (var2 != null) {
         Object var3 = var2.getCellEditorValue();
         this.setValueAt(var3, this.editingRow, this.editingColumn);
         this.removeEditor();
      }

   }

   public void editingCanceled(ChangeEvent var1) {
      this.removeEditor();
   }

   public void setPreferredScrollableViewportSize(Dimension var1) {
      this.preferredViewportSize = var1;
   }

   public Dimension getPreferredScrollableViewportSize() {
      return this.preferredViewportSize;
   }

   public int getScrollableUnitIncrement(Rectangle var1, int var2, int var3) {
      int var4 = this.getLeadingRow(var1);
      int var5 = this.getLeadingCol(var1);
      if (var2 == 1 && var4 < 0) {
         return this.getRowHeight();
      } else if (var2 == 0 && var5 < 0) {
         return 100;
      } else {
         Rectangle var6 = this.getCellRect(var4, var5, true);
         int var7 = this.leadingEdge(var1, var2);
         int var8 = this.leadingEdge(var6, var2);
         int var9;
         if (var2 == 1) {
            var9 = var6.height;
         } else {
            var9 = var6.width;
         }

         int var10;
         if (var7 == var8) {
            if (var3 >= 0) {
               return var9;
            } else {
               var10 = 0;
               if (var2 == 1) {
                  do {
                     --var4;
                     if (var4 < 0) {
                        break;
                     }

                     var10 = this.getRowHeight(var4);
                  } while(var10 == 0);
               } else {
                  do {
                     --var5;
                     if (var5 < 0) {
                        break;
                     }

                     var10 = this.getCellRect(var4, var5, true).width;
                  } while(var10 == 0);
               }

               return var10;
            }
         } else {
            var10 = Math.abs(var7 - var8);
            int var11 = var9 - var10;
            return var3 > 0 ? var11 : var10;
         }
      }
   }

   public int getScrollableBlockIncrement(Rectangle var1, int var2, int var3) {
      int var4;
      if (this.getRowCount() == 0) {
         if (1 == var2) {
            var4 = this.getRowHeight();
            return var4 > 0 ? Math.max(var4, var1.height / var4 * var4) : var1.height;
         } else {
            return var1.width;
         }
      } else {
         if (null == this.rowModel && 1 == var2) {
            var4 = this.rowAtPoint(var1.getLocation());

            assert var4 != -1;

            int var5 = this.columnAtPoint(var1.getLocation());
            Rectangle var6 = this.getCellRect(var4, var5, true);
            if (var6.y == var1.y) {
               int var7 = this.getRowHeight();

               assert var7 > 0;

               return Math.max(var7, var1.height / var7 * var7);
            }
         }

         return var3 < 0 ? this.getPreviousBlockIncrement(var1, var2) : this.getNextBlockIncrement(var1, var2);
      }
   }

   private int getPreviousBlockIncrement(Rectangle var1, int var2) {
      int var7 = this.leadingEdge(var1, var2);
      boolean var8 = this.getComponentOrientation().isLeftToRight();
      int var5;
      Point var6;
      if (var2 == 1) {
         var5 = var7 - var1.height;
         int var10 = var1.x + (var8 ? 0 : var1.width);
         var6 = new Point(var10, var5);
      } else if (var8) {
         var5 = var7 - var1.width;
         var6 = new Point(var5, var1.y);
      } else {
         var5 = var7 + var1.width;
         var6 = new Point(var5 - 1, var1.y);
      }

      int var3 = this.rowAtPoint(var6);
      int var4 = this.columnAtPoint(var6);
      int var9;
      if (var2 == 1 & var3 < 0) {
         var9 = 0;
      } else if (var2 == 0 & var4 < 0) {
         if (var8) {
            var9 = 0;
         } else {
            var9 = this.getWidth();
         }
      } else {
         Rectangle var13 = this.getCellRect(var3, var4, true);
         int var11 = this.leadingEdge(var13, var2);
         int var12 = this.trailingEdge(var13, var2);
         if ((var2 == 1 || var8) && var12 >= var7) {
            var9 = var11;
         } else if (var2 == 0 && !var8 && var12 <= var7) {
            var9 = var11;
         } else if (var5 == var11) {
            var9 = var11;
         } else {
            var9 = var12;
         }
      }

      return Math.abs(var7 - var9);
   }

   private int getNextBlockIncrement(Rectangle var1, int var2) {
      int var3 = this.getTrailingRow(var1);
      int var4 = this.getTrailingCol(var1);
      int var10 = this.leadingEdge(var1, var2);
      if (var2 == 1 && var3 < 0) {
         return var1.height;
      } else if (var2 == 0 && var4 < 0) {
         return var1.width;
      } else {
         Rectangle var5 = this.getCellRect(var3, var4, true);
         int var7 = this.leadingEdge(var5, var2);
         int var8 = this.trailingEdge(var5, var2);
         boolean var6;
         if (var2 != 1 && !this.getComponentOrientation().isLeftToRight()) {
            var6 = var7 >= var10;
         } else {
            var6 = var7 <= var10;
         }

         int var9;
         if (var6) {
            var9 = var8;
         } else if (var8 == this.trailingEdge(var1, var2)) {
            var9 = var8;
         } else {
            var9 = var7;
         }

         return Math.abs(var9 - var10);
      }
   }

   private int getLeadingRow(Rectangle var1) {
      Point var2;
      if (this.getComponentOrientation().isLeftToRight()) {
         var2 = new Point(var1.x, var1.y);
      } else {
         var2 = new Point(var1.x + var1.width - 1, var1.y);
      }

      return this.rowAtPoint(var2);
   }

   private int getLeadingCol(Rectangle var1) {
      Point var2;
      if (this.getComponentOrientation().isLeftToRight()) {
         var2 = new Point(var1.x, var1.y);
      } else {
         var2 = new Point(var1.x + var1.width - 1, var1.y);
      }

      return this.columnAtPoint(var2);
   }

   private int getTrailingRow(Rectangle var1) {
      Point var2;
      if (this.getComponentOrientation().isLeftToRight()) {
         var2 = new Point(var1.x, var1.y + var1.height - 1);
      } else {
         var2 = new Point(var1.x + var1.width - 1, var1.y + var1.height - 1);
      }

      return this.rowAtPoint(var2);
   }

   private int getTrailingCol(Rectangle var1) {
      Point var2;
      if (this.getComponentOrientation().isLeftToRight()) {
         var2 = new Point(var1.x + var1.width - 1, var1.y);
      } else {
         var2 = new Point(var1.x, var1.y);
      }

      return this.columnAtPoint(var2);
   }

   private int leadingEdge(Rectangle var1, int var2) {
      if (var2 == 1) {
         return var1.y;
      } else {
         return this.getComponentOrientation().isLeftToRight() ? var1.x : var1.x + var1.width;
      }
   }

   private int trailingEdge(Rectangle var1, int var2) {
      if (var2 == 1) {
         return var1.y + var1.height;
      } else {
         return this.getComponentOrientation().isLeftToRight() ? var1.x + var1.width : var1.x;
      }
   }

   public boolean getScrollableTracksViewportWidth() {
      return this.autoResizeMode != 0;
   }

   public boolean getScrollableTracksViewportHeight() {
      Container var1 = SwingUtilities.getUnwrappedParent(this);
      return this.getFillsViewportHeight() && var1 instanceof JViewport && var1.getHeight() > this.getPreferredSize().height;
   }

   public void setFillsViewportHeight(boolean var1) {
      boolean var2 = this.fillsViewportHeight;
      this.fillsViewportHeight = var1;
      this.resizeAndRepaint();
      this.firePropertyChange("fillsViewportHeight", var2, var1);
   }

   public boolean getFillsViewportHeight() {
      return this.fillsViewportHeight;
   }

   protected boolean processKeyBinding(KeyStroke var1, KeyEvent var2, int var3, boolean var4) {
      boolean var5 = super.processKeyBinding(var1, var2, var3, var4);
      if (!var5 && var3 == 1 && this.isFocusOwner() && !Boolean.FALSE.equals(this.getClientProperty("JTable.autoStartsEdit"))) {
         Component var6 = this.getEditorComponent();
         if (var6 == null) {
            if (var2 == null || var2.getID() != 401) {
               return false;
            }

            int var7 = var2.getKeyCode();
            if (var7 == 16 || var7 == 17 || var7 == 18) {
               return false;
            }

            int var8 = this.getSelectionModel().getLeadSelectionIndex();
            int var9 = this.getColumnModel().getSelectionModel().getLeadSelectionIndex();
            if (var8 != -1 && var9 != -1 && !this.isEditing() && !this.editCellAt(var8, var9, var2)) {
               return false;
            }

            var6 = this.getEditorComponent();
            if (var6 == null) {
               return false;
            }
         }

         if (var6 instanceof JComponent) {
            var5 = ((JComponent)var6).processKeyBinding(var1, var2, 0, var4);
            if (this.getSurrendersFocusOnKeystroke()) {
               var6.requestFocus();
            }
         }
      }

      return var5;
   }

   protected void createDefaultRenderers() {
      this.defaultRenderersByColumnClass = new UIDefaults(8, 0.75F);
      this.defaultRenderersByColumnClass.put(Object.class, (var0) -> {
         return new DefaultTableCellRenderer.UIResource();
      });
      this.defaultRenderersByColumnClass.put(Number.class, (var0) -> {
         return new JTable.NumberRenderer();
      });
      this.defaultRenderersByColumnClass.put(Float.class, (var0) -> {
         return new JTable.DoubleRenderer();
      });
      this.defaultRenderersByColumnClass.put(Double.class, (var0) -> {
         return new JTable.DoubleRenderer();
      });
      this.defaultRenderersByColumnClass.put(Date.class, (var0) -> {
         return new JTable.DateRenderer();
      });
      this.defaultRenderersByColumnClass.put(Icon.class, (var0) -> {
         return new JTable.IconRenderer();
      });
      this.defaultRenderersByColumnClass.put(ImageIcon.class, (var0) -> {
         return new JTable.IconRenderer();
      });
      this.defaultRenderersByColumnClass.put(Boolean.class, (var0) -> {
         return new JTable.BooleanRenderer();
      });
   }

   protected void createDefaultEditors() {
      this.defaultEditorsByColumnClass = new UIDefaults(3, 0.75F);
      this.defaultEditorsByColumnClass.put(Object.class, (var0) -> {
         return new JTable.GenericEditor();
      });
      this.defaultEditorsByColumnClass.put(Number.class, (var0) -> {
         return new JTable.NumberEditor();
      });
      this.defaultEditorsByColumnClass.put(Boolean.class, (var0) -> {
         return new JTable.BooleanEditor();
      });
   }

   protected void initializeLocalVars() {
      this.updateSelectionOnSort = true;
      this.setOpaque(true);
      this.createDefaultRenderers();
      this.createDefaultEditors();
      this.setTableHeader(this.createDefaultTableHeader());
      this.setShowGrid(true);
      this.setAutoResizeMode(2);
      this.setRowHeight(16);
      this.isRowHeightSet = false;
      this.setRowMargin(1);
      this.setRowSelectionAllowed(true);
      this.setCellEditor((TableCellEditor)null);
      this.setEditingColumn(-1);
      this.setEditingRow(-1);
      this.setSurrendersFocusOnKeystroke(false);
      this.setPreferredScrollableViewportSize(new Dimension(450, 400));
      ToolTipManager var1 = ToolTipManager.sharedInstance();
      var1.registerComponent(this);
      this.setAutoscrolls(true);
   }

   protected TableModel createDefaultDataModel() {
      return new DefaultTableModel();
   }

   protected TableColumnModel createDefaultColumnModel() {
      return new DefaultTableColumnModel();
   }

   protected ListSelectionModel createDefaultSelectionModel() {
      return new DefaultListSelectionModel();
   }

   protected JTableHeader createDefaultTableHeader() {
      return new JTableHeader(this.columnModel);
   }

   protected void resizeAndRepaint() {
      this.revalidate();
      this.repaint();
   }

   public TableCellEditor getCellEditor() {
      return this.cellEditor;
   }

   public void setCellEditor(TableCellEditor var1) {
      TableCellEditor var2 = this.cellEditor;
      this.cellEditor = var1;
      this.firePropertyChange("tableCellEditor", var2, var1);
   }

   public void setEditingColumn(int var1) {
      this.editingColumn = var1;
   }

   public void setEditingRow(int var1) {
      this.editingRow = var1;
   }

   public TableCellRenderer getCellRenderer(int var1, int var2) {
      TableColumn var3 = this.getColumnModel().getColumn(var2);
      TableCellRenderer var4 = var3.getCellRenderer();
      if (var4 == null) {
         var4 = this.getDefaultRenderer(this.getColumnClass(var2));
      }

      return var4;
   }

   public Component prepareRenderer(TableCellRenderer var1, int var2, int var3) {
      Object var4 = this.getValueAt(var2, var3);
      boolean var5 = false;
      boolean var6 = false;
      if (!this.isPaintingForPrint()) {
         var5 = this.isCellSelected(var2, var3);
         boolean var7 = this.selectionModel.getLeadSelectionIndex() == var2;
         boolean var8 = this.columnModel.getSelectionModel().getLeadSelectionIndex() == var3;
         var6 = var7 && var8 && this.isFocusOwner();
      }

      return var1.getTableCellRendererComponent(this, var4, var5, var6, var2, var3);
   }

   public TableCellEditor getCellEditor(int var1, int var2) {
      TableColumn var3 = this.getColumnModel().getColumn(var2);
      TableCellEditor var4 = var3.getCellEditor();
      if (var4 == null) {
         var4 = this.getDefaultEditor(this.getColumnClass(var2));
      }

      return var4;
   }

   public Component prepareEditor(TableCellEditor var1, int var2, int var3) {
      Object var4 = this.getValueAt(var2, var3);
      boolean var5 = this.isCellSelected(var2, var3);
      Component var6 = var1.getTableCellEditorComponent(this, var4, var5, var2, var3);
      if (var6 instanceof JComponent) {
         JComponent var7 = (JComponent)var6;
         if (var7.getNextFocusableComponent() == null) {
            var7.setNextFocusableComponent(this);
         }
      }

      return var6;
   }

   public void removeEditor() {
      KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener("permanentFocusOwner", this.editorRemover);
      this.editorRemover = null;
      TableCellEditor var1 = this.getCellEditor();
      if (var1 != null) {
         var1.removeCellEditorListener(this);
         if (this.editorComp != null) {
            Component var2 = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            boolean var3 = var2 != null ? SwingUtilities.isDescendingFrom(var2, this) : false;
            this.remove(this.editorComp);
            if (var3) {
               this.requestFocusInWindow();
            }
         }

         Rectangle var4 = this.getCellRect(this.editingRow, this.editingColumn, false);
         this.setCellEditor((TableCellEditor)null);
         this.setEditingColumn(-1);
         this.setEditingRow(-1);
         this.editorComp = null;
         this.repaint(var4);
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("TableUI")) {
         byte var2 = JComponent.getWriteObjCounter(this);
         --var2;
         JComponent.setWriteObjCounter(this, var2);
         if (var2 == 0 && this.ui != null) {
            this.ui.installUI(this);
         }
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      if (this.ui != null && this.getUIClassID().equals("TableUI")) {
         this.ui.installUI(this);
      }

      this.createDefaultRenderers();
      this.createDefaultEditors();
      if (this.getToolTipText() == null) {
         ToolTipManager.sharedInstance().registerComponent(this);
      }

   }

   void compWriteObjectNotify() {
      super.compWriteObjectNotify();
      if (this.getToolTipText() == null) {
         ToolTipManager.sharedInstance().unregisterComponent(this);
      }

   }

   protected String paramString() {
      String var1 = this.gridColor != null ? this.gridColor.toString() : "";
      String var2 = this.showHorizontalLines ? "true" : "false";
      String var3 = this.showVerticalLines ? "true" : "false";
      String var4;
      if (this.autoResizeMode == 0) {
         var4 = "AUTO_RESIZE_OFF";
      } else if (this.autoResizeMode == 1) {
         var4 = "AUTO_RESIZE_NEXT_COLUMN";
      } else if (this.autoResizeMode == 2) {
         var4 = "AUTO_RESIZE_SUBSEQUENT_COLUMNS";
      } else if (this.autoResizeMode == 3) {
         var4 = "AUTO_RESIZE_LAST_COLUMN";
      } else if (this.autoResizeMode == 4) {
         var4 = "AUTO_RESIZE_ALL_COLUMNS";
      } else {
         var4 = "";
      }

      String var5 = this.autoCreateColumnsFromModel ? "true" : "false";
      String var6 = this.preferredViewportSize != null ? this.preferredViewportSize.toString() : "";
      String var7 = this.rowSelectionAllowed ? "true" : "false";
      String var8 = this.cellSelectionEnabled ? "true" : "false";
      String var9 = this.selectionForeground != null ? this.selectionForeground.toString() : "";
      String var10 = this.selectionBackground != null ? this.selectionBackground.toString() : "";
      return super.paramString() + ",autoCreateColumnsFromModel=" + var5 + ",autoResizeMode=" + var4 + ",cellSelectionEnabled=" + var8 + ",editingColumn=" + this.editingColumn + ",editingRow=" + this.editingRow + ",gridColor=" + var1 + ",preferredViewportSize=" + var6 + ",rowHeight=" + this.rowHeight + ",rowMargin=" + this.rowMargin + ",rowSelectionAllowed=" + var7 + ",selectionBackground=" + var10 + ",selectionForeground=" + var9 + ",showHorizontalLines=" + var2 + ",showVerticalLines=" + var3;
   }

   public boolean print() throws PrinterException {
      return this.print(JTable.PrintMode.FIT_WIDTH);
   }

   public boolean print(JTable.PrintMode var1) throws PrinterException {
      return this.print(var1, (MessageFormat)null, (MessageFormat)null);
   }

   public boolean print(JTable.PrintMode var1, MessageFormat var2, MessageFormat var3) throws PrinterException {
      boolean var4 = !GraphicsEnvironment.isHeadless();
      return this.print(var1, var2, var3, var4, (PrintRequestAttributeSet)null, var4);
   }

   public boolean print(JTable.PrintMode var1, MessageFormat var2, MessageFormat var3, boolean var4, PrintRequestAttributeSet var5, boolean var6) throws PrinterException, HeadlessException {
      return this.print(var1, var2, var3, var4, var5, var6, (PrintService)null);
   }

   public boolean print(JTable.PrintMode var1, MessageFormat var2, MessageFormat var3, boolean var4, final PrintRequestAttributeSet var5, boolean var6, PrintService var7) throws PrinterException, HeadlessException {
      boolean var8 = GraphicsEnvironment.isHeadless();
      if (var8) {
         if (var4) {
            throw new HeadlessException("Can't show print dialog.");
         }

         if (var6) {
            throw new HeadlessException("Can't run interactively.");
         }
      }

      final PrinterJob var9 = PrinterJob.getPrinterJob();
      if (this.isEditing() && !this.getCellEditor().stopCellEditing()) {
         this.getCellEditor().cancelCellEditing();
      }

      if (var5 == null) {
         var5 = new HashPrintRequestAttributeSet();
      }

      Printable var11 = this.getPrintable(var1, var2, var3);
      final PrintingStatus var10;
      if (var6) {
         JTable.ThreadSafePrintable var20 = new JTable.ThreadSafePrintable(var11);
         var10 = PrintingStatus.createPrintingStatus(this, var9);
         var11 = var10.createNotificationPrintable(var20);
      } else {
         var10 = null;
      }

      var9.setPrintable(var11);
      if (var7 != null) {
         var9.setPrintService(var7);
      }

      if (var4 && !var9.printDialog((PrintRequestAttributeSet)var5)) {
         return false;
      } else if (!var6) {
         var9.print((PrintRequestAttributeSet)var5);
         return true;
      } else {
         this.printError = null;
         final Object var12 = new Object();
         Runnable var14 = new Runnable() {
            public void run() {
               try {
                  var9.print((PrintRequestAttributeSet)var5);
               } catch (Throwable var9x) {
                  Throwable var1 = var9x;
                  synchronized(var12) {
                     JTable.this.printError = var1;
                  }
               } finally {
                  var10.dispose();
               }

            }
         };
         Thread var15 = new Thread(var14);
         var15.start();
         var10.showModal(true);
         Throwable var16;
         synchronized(var12) {
            var16 = this.printError;
            this.printError = null;
         }

         if (var16 != null) {
            if (var16 instanceof PrinterAbortException) {
               return false;
            } else if (var16 instanceof PrinterException) {
               throw (PrinterException)var16;
            } else if (var16 instanceof RuntimeException) {
               throw (RuntimeException)var16;
            } else if (var16 instanceof Error) {
               throw (Error)var16;
            } else {
               throw new AssertionError(var16);
            }
         } else {
            return true;
         }
      }
   }

   public Printable getPrintable(JTable.PrintMode var1, MessageFormat var2, MessageFormat var3) {
      return new TablePrintable(this, var1, var2, var3);
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JTable.AccessibleJTable();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJTable extends JComponent.AccessibleJComponent implements AccessibleSelection, ListSelectionListener, TableModelListener, TableColumnModelListener, CellEditorListener, PropertyChangeListener, AccessibleExtendedTable {
      int previousFocusedRow;
      int previousFocusedCol;
      private Accessible caption;
      private Accessible summary;
      private Accessible[] rowDescription;
      private Accessible[] columnDescription;

      protected AccessibleJTable() {
         super();
         JTable.this.addPropertyChangeListener(this);
         JTable.this.getSelectionModel().addListSelectionListener(this);
         TableColumnModel var2 = JTable.this.getColumnModel();
         var2.addColumnModelListener(this);
         var2.getSelectionModel().addListSelectionListener(this);
         JTable.this.getModel().addTableModelListener(this);
         this.previousFocusedRow = JTable.this.getSelectionModel().getLeadSelectionIndex();
         this.previousFocusedCol = JTable.this.getColumnModel().getSelectionModel().getLeadSelectionIndex();
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         Object var3 = var1.getOldValue();
         Object var4 = var1.getNewValue();
         if (var2.compareTo("model") == 0) {
            if (var3 != null && var3 instanceof TableModel) {
               ((TableModel)var3).removeTableModelListener(this);
            }

            if (var4 != null && var4 instanceof TableModel) {
               ((TableModel)var4).addTableModelListener(this);
            }
         } else if (var2.compareTo("selectionModel") == 0) {
            Object var5 = var1.getSource();
            if (var5 == JTable.this) {
               if (var3 != null && var3 instanceof ListSelectionModel) {
                  ((ListSelectionModel)var3).removeListSelectionListener(this);
               }

               if (var4 != null && var4 instanceof ListSelectionModel) {
                  ((ListSelectionModel)var4).addListSelectionListener(this);
               }
            } else if (var5 == JTable.this.getColumnModel()) {
               if (var3 != null && var3 instanceof ListSelectionModel) {
                  ((ListSelectionModel)var3).removeListSelectionListener(this);
               }

               if (var4 != null && var4 instanceof ListSelectionModel) {
                  ((ListSelectionModel)var4).addListSelectionListener(this);
               }
            }
         } else if (var2.compareTo("columnModel") == 0) {
            TableColumnModel var6;
            if (var3 != null && var3 instanceof TableColumnModel) {
               var6 = (TableColumnModel)var3;
               var6.removeColumnModelListener(this);
               var6.getSelectionModel().removeListSelectionListener(this);
            }

            if (var4 != null && var4 instanceof TableColumnModel) {
               var6 = (TableColumnModel)var4;
               var6.addColumnModelListener(this);
               var6.getSelectionModel().addListSelectionListener(this);
            }
         } else if (var2.compareTo("tableCellEditor") == 0) {
            if (var3 != null && var3 instanceof TableCellEditor) {
               ((TableCellEditor)var3).removeCellEditorListener(this);
            }

            if (var4 != null && var4 instanceof TableCellEditor) {
               ((TableCellEditor)var4).addCellEditorListener(this);
            }
         }

      }

      public void tableChanged(TableModelEvent var1) {
         this.firePropertyChange("AccessibleVisibleData", (Object)null, (Object)null);
         if (var1 != null) {
            int var2 = var1.getColumn();
            int var3 = var1.getColumn();
            if (var2 == -1) {
               var2 = 0;
               var3 = JTable.this.getColumnCount() - 1;
            }

            JTable.AccessibleJTable.AccessibleJTableModelChange var4 = new JTable.AccessibleJTable.AccessibleJTableModelChange(var1.getType(), var1.getFirstRow(), var1.getLastRow(), var2, var3);
            this.firePropertyChange("accessibleTableModelChanged", (Object)null, var4);
         }

      }

      public void tableRowsInserted(TableModelEvent var1) {
         this.firePropertyChange("AccessibleVisibleData", (Object)null, (Object)null);
         int var2 = var1.getColumn();
         int var3 = var1.getColumn();
         if (var2 == -1) {
            var2 = 0;
            var3 = JTable.this.getColumnCount() - 1;
         }

         JTable.AccessibleJTable.AccessibleJTableModelChange var4 = new JTable.AccessibleJTable.AccessibleJTableModelChange(var1.getType(), var1.getFirstRow(), var1.getLastRow(), var2, var3);
         this.firePropertyChange("accessibleTableModelChanged", (Object)null, var4);
      }

      public void tableRowsDeleted(TableModelEvent var1) {
         this.firePropertyChange("AccessibleVisibleData", (Object)null, (Object)null);
         int var2 = var1.getColumn();
         int var3 = var1.getColumn();
         if (var2 == -1) {
            var2 = 0;
            var3 = JTable.this.getColumnCount() - 1;
         }

         JTable.AccessibleJTable.AccessibleJTableModelChange var4 = new JTable.AccessibleJTable.AccessibleJTableModelChange(var1.getType(), var1.getFirstRow(), var1.getLastRow(), var2, var3);
         this.firePropertyChange("accessibleTableModelChanged", (Object)null, var4);
      }

      public void columnAdded(TableColumnModelEvent var1) {
         this.firePropertyChange("AccessibleVisibleData", (Object)null, (Object)null);
         byte var2 = 1;
         JTable.AccessibleJTable.AccessibleJTableModelChange var3 = new JTable.AccessibleJTable.AccessibleJTableModelChange(var2, 0, 0, var1.getFromIndex(), var1.getToIndex());
         this.firePropertyChange("accessibleTableModelChanged", (Object)null, var3);
      }

      public void columnRemoved(TableColumnModelEvent var1) {
         this.firePropertyChange("AccessibleVisibleData", (Object)null, (Object)null);
         byte var2 = -1;
         JTable.AccessibleJTable.AccessibleJTableModelChange var3 = new JTable.AccessibleJTable.AccessibleJTableModelChange(var2, 0, 0, var1.getFromIndex(), var1.getToIndex());
         this.firePropertyChange("accessibleTableModelChanged", (Object)null, var3);
      }

      public void columnMoved(TableColumnModelEvent var1) {
         this.firePropertyChange("AccessibleVisibleData", (Object)null, (Object)null);
         byte var2 = -1;
         JTable.AccessibleJTable.AccessibleJTableModelChange var3 = new JTable.AccessibleJTable.AccessibleJTableModelChange(var2, 0, 0, var1.getFromIndex(), var1.getFromIndex());
         this.firePropertyChange("accessibleTableModelChanged", (Object)null, var3);
         byte var4 = 1;
         JTable.AccessibleJTable.AccessibleJTableModelChange var5 = new JTable.AccessibleJTable.AccessibleJTableModelChange(var4, 0, 0, var1.getToIndex(), var1.getToIndex());
         this.firePropertyChange("accessibleTableModelChanged", (Object)null, var5);
      }

      public void columnMarginChanged(ChangeEvent var1) {
         this.firePropertyChange("AccessibleVisibleData", (Object)null, (Object)null);
      }

      public void columnSelectionChanged(ListSelectionEvent var1) {
      }

      public void editingStopped(ChangeEvent var1) {
         this.firePropertyChange("AccessibleVisibleData", (Object)null, (Object)null);
      }

      public void editingCanceled(ChangeEvent var1) {
      }

      public void valueChanged(ListSelectionEvent var1) {
         this.firePropertyChange("AccessibleSelection", false, true);
         int var2 = JTable.this.getSelectionModel().getLeadSelectionIndex();
         int var3 = JTable.this.getColumnModel().getSelectionModel().getLeadSelectionIndex();
         if (var2 != this.previousFocusedRow || var3 != this.previousFocusedCol) {
            Accessible var4 = this.getAccessibleAt(this.previousFocusedRow, this.previousFocusedCol);
            Accessible var5 = this.getAccessibleAt(var2, var3);
            this.firePropertyChange("AccessibleActiveDescendant", var4, var5);
            this.previousFocusedRow = var2;
            this.previousFocusedCol = var3;
         }

      }

      public AccessibleSelection getAccessibleSelection() {
         return this;
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.TABLE;
      }

      public Accessible getAccessibleAt(Point var1) {
         int var2 = JTable.this.columnAtPoint(var1);
         int var3 = JTable.this.rowAtPoint(var1);
         if (var2 != -1 && var3 != -1) {
            TableColumn var4 = JTable.this.getColumnModel().getColumn(var2);
            TableCellRenderer var5 = var4.getCellRenderer();
            if (var5 == null) {
               Class var6 = JTable.this.getColumnClass(var2);
               var5 = JTable.this.getDefaultRenderer(var6);
            }

            var5.getTableCellRendererComponent(JTable.this, (Object)null, false, false, var3, var2);
            return new JTable.AccessibleJTable.AccessibleJTableCell(JTable.this, var3, var2, this.getAccessibleIndexAt(var3, var2));
         } else {
            return null;
         }
      }

      public int getAccessibleChildrenCount() {
         return JTable.this.getColumnCount() * JTable.this.getRowCount();
      }

      public Accessible getAccessibleChild(int var1) {
         if (var1 >= 0 && var1 < this.getAccessibleChildrenCount()) {
            int var2 = this.getAccessibleColumnAtIndex(var1);
            int var3 = this.getAccessibleRowAtIndex(var1);
            TableColumn var4 = JTable.this.getColumnModel().getColumn(var2);
            TableCellRenderer var5 = var4.getCellRenderer();
            if (var5 == null) {
               Class var6 = JTable.this.getColumnClass(var2);
               var5 = JTable.this.getDefaultRenderer(var6);
            }

            var5.getTableCellRendererComponent(JTable.this, (Object)null, false, false, var3, var2);
            return new JTable.AccessibleJTable.AccessibleJTableCell(JTable.this, var3, var2, this.getAccessibleIndexAt(var3, var2));
         } else {
            return null;
         }
      }

      public int getAccessibleSelectionCount() {
         int var1 = JTable.this.getSelectedRowCount();
         int var2 = JTable.this.getSelectedColumnCount();
         if (JTable.this.cellSelectionEnabled) {
            return var1 * var2;
         } else if (JTable.this.getRowSelectionAllowed() && JTable.this.getColumnSelectionAllowed()) {
            return var1 * JTable.this.getColumnCount() + var2 * JTable.this.getRowCount() - var1 * var2;
         } else if (JTable.this.getRowSelectionAllowed()) {
            return var1 * JTable.this.getColumnCount();
         } else {
            return JTable.this.getColumnSelectionAllowed() ? var2 * JTable.this.getRowCount() : 0;
         }
      }

      public Accessible getAccessibleSelection(int var1) {
         if (var1 >= 0 && var1 <= this.getAccessibleSelectionCount()) {
            int var2 = JTable.this.getSelectedRowCount();
            int var3 = JTable.this.getSelectedColumnCount();
            int[] var4 = JTable.this.getSelectedRows();
            int[] var5 = JTable.this.getSelectedColumns();
            int var6 = JTable.this.getColumnCount();
            int var7 = JTable.this.getRowCount();
            int var8;
            int var9;
            if (JTable.this.cellSelectionEnabled) {
               var8 = var4[var1 / var3];
               var9 = var5[var1 % var3];
               return this.getAccessibleChild(var8 * var6 + var9);
            } else {
               if (JTable.this.getRowSelectionAllowed() && JTable.this.getColumnSelectionAllowed()) {
                  int var10 = var1;
                  int var13 = var4[0] == 0 ? 0 : 1;
                  int var14 = 0;
                  int var15 = -1;

                  while(true) {
                     while(var14 < var4.length) {
                        switch(var13) {
                        case 0:
                           if (var10 < var6) {
                              var9 = var10 % var6;
                              var8 = var4[var14];
                              return this.getAccessibleChild(var8 * var6 + var9);
                           }

                           var10 -= var6;
                           if (var14 + 1 == var4.length || var4[var14] != var4[var14 + 1] - 1) {
                              var13 = 1;
                              var15 = var4[var14];
                           }

                           ++var14;
                           break;
                        case 1:
                           if (var10 < var3 * (var4[var14] - (var15 == -1 ? 0 : var15 + 1))) {
                              var9 = var5[var10 % var3];
                              var8 = (var14 > 0 ? var4[var14 - 1] + 1 : 0) + var10 / var3;
                              return this.getAccessibleChild(var8 * var6 + var9);
                           }

                           var10 -= var3 * (var4[var14] - (var15 == -1 ? 0 : var15 + 1));
                           var13 = 0;
                        }
                     }

                     if (var10 < var3 * (var7 - (var15 == -1 ? 0 : var15 + 1))) {
                        var9 = var5[var10 % var3];
                        var8 = var4[var14 - 1] + var10 / var3 + 1;
                        return this.getAccessibleChild(var8 * var6 + var9);
                     }
                     break;
                  }
               } else {
                  if (JTable.this.getRowSelectionAllowed()) {
                     var9 = var1 % var6;
                     var8 = var4[var1 / var6];
                     return this.getAccessibleChild(var8 * var6 + var9);
                  }

                  if (JTable.this.getColumnSelectionAllowed()) {
                     var9 = var5[var1 % var3];
                     var8 = var1 / var3;
                     return this.getAccessibleChild(var8 * var6 + var9);
                  }
               }

               return null;
            }
         } else {
            return null;
         }
      }

      public boolean isAccessibleChildSelected(int var1) {
         int var2 = this.getAccessibleColumnAtIndex(var1);
         int var3 = this.getAccessibleRowAtIndex(var1);
         return JTable.this.isCellSelected(var3, var2);
      }

      public void addAccessibleSelection(int var1) {
         int var2 = this.getAccessibleColumnAtIndex(var1);
         int var3 = this.getAccessibleRowAtIndex(var1);
         JTable.this.changeSelection(var3, var2, true, false);
      }

      public void removeAccessibleSelection(int var1) {
         if (JTable.this.cellSelectionEnabled) {
            int var2 = this.getAccessibleColumnAtIndex(var1);
            int var3 = this.getAccessibleRowAtIndex(var1);
            JTable.this.removeRowSelectionInterval(var3, var3);
            JTable.this.removeColumnSelectionInterval(var2, var2);
         }

      }

      public void clearAccessibleSelection() {
         JTable.this.clearSelection();
      }

      public void selectAllAccessibleSelection() {
         if (JTable.this.cellSelectionEnabled) {
            JTable.this.selectAll();
         }

      }

      public int getAccessibleRow(int var1) {
         return this.getAccessibleRowAtIndex(var1);
      }

      public int getAccessibleColumn(int var1) {
         return this.getAccessibleColumnAtIndex(var1);
      }

      public int getAccessibleIndex(int var1, int var2) {
         return this.getAccessibleIndexAt(var1, var2);
      }

      public AccessibleTable getAccessibleTable() {
         return this;
      }

      public Accessible getAccessibleCaption() {
         return this.caption;
      }

      public void setAccessibleCaption(Accessible var1) {
         Accessible var2 = this.caption;
         this.caption = var1;
         this.firePropertyChange("accessibleTableCaptionChanged", var2, this.caption);
      }

      public Accessible getAccessibleSummary() {
         return this.summary;
      }

      public void setAccessibleSummary(Accessible var1) {
         Accessible var2 = this.summary;
         this.summary = var1;
         this.firePropertyChange("accessibleTableSummaryChanged", var2, this.summary);
      }

      public int getAccessibleRowCount() {
         return JTable.this.getRowCount();
      }

      public int getAccessibleColumnCount() {
         return JTable.this.getColumnCount();
      }

      public Accessible getAccessibleAt(int var1, int var2) {
         return this.getAccessibleChild(var1 * this.getAccessibleColumnCount() + var2);
      }

      public int getAccessibleRowExtentAt(int var1, int var2) {
         return 1;
      }

      public int getAccessibleColumnExtentAt(int var1, int var2) {
         return 1;
      }

      public AccessibleTable getAccessibleRowHeader() {
         return null;
      }

      public void setAccessibleRowHeader(AccessibleTable var1) {
      }

      public AccessibleTable getAccessibleColumnHeader() {
         JTableHeader var1 = JTable.this.getTableHeader();
         return var1 == null ? null : new JTable.AccessibleJTable.AccessibleTableHeader(var1);
      }

      public void setAccessibleColumnHeader(AccessibleTable var1) {
      }

      public Accessible getAccessibleRowDescription(int var1) {
         if (var1 >= 0 && var1 < this.getAccessibleRowCount()) {
            return this.rowDescription == null ? null : this.rowDescription[var1];
         } else {
            throw new IllegalArgumentException(Integer.toString(var1));
         }
      }

      public void setAccessibleRowDescription(int var1, Accessible var2) {
         if (var1 >= 0 && var1 < this.getAccessibleRowCount()) {
            if (this.rowDescription == null) {
               int var3 = this.getAccessibleRowCount();
               this.rowDescription = new Accessible[var3];
            }

            this.rowDescription[var1] = var2;
         } else {
            throw new IllegalArgumentException(Integer.toString(var1));
         }
      }

      public Accessible getAccessibleColumnDescription(int var1) {
         if (var1 >= 0 && var1 < this.getAccessibleColumnCount()) {
            return this.columnDescription == null ? null : this.columnDescription[var1];
         } else {
            throw new IllegalArgumentException(Integer.toString(var1));
         }
      }

      public void setAccessibleColumnDescription(int var1, Accessible var2) {
         if (var1 >= 0 && var1 < this.getAccessibleColumnCount()) {
            if (this.columnDescription == null) {
               int var3 = this.getAccessibleColumnCount();
               this.columnDescription = new Accessible[var3];
            }

            this.columnDescription[var1] = var2;
         } else {
            throw new IllegalArgumentException(Integer.toString(var1));
         }
      }

      public boolean isAccessibleSelected(int var1, int var2) {
         return JTable.this.isCellSelected(var1, var2);
      }

      public boolean isAccessibleRowSelected(int var1) {
         return JTable.this.isRowSelected(var1);
      }

      public boolean isAccessibleColumnSelected(int var1) {
         return JTable.this.isColumnSelected(var1);
      }

      public int[] getSelectedAccessibleRows() {
         return JTable.this.getSelectedRows();
      }

      public int[] getSelectedAccessibleColumns() {
         return JTable.this.getSelectedColumns();
      }

      public int getAccessibleRowAtIndex(int var1) {
         int var2 = this.getAccessibleColumnCount();
         return var2 == 0 ? -1 : var1 / var2;
      }

      public int getAccessibleColumnAtIndex(int var1) {
         int var2 = this.getAccessibleColumnCount();
         return var2 == 0 ? -1 : var1 % var2;
      }

      public int getAccessibleIndexAt(int var1, int var2) {
         return var1 * this.getAccessibleColumnCount() + var2;
      }

      private class AccessibleJTableHeaderCell extends AccessibleContext implements Accessible, AccessibleComponent {
         private int row;
         private int column;
         private JTableHeader parent;
         private Component rendererComponent;

         public AccessibleJTableHeaderCell(int var2, int var3, JTableHeader var4, Component var5) {
            this.row = var2;
            this.column = var3;
            this.parent = var4;
            this.rendererComponent = var5;
            this.setAccessibleParent(var4);
         }

         public AccessibleContext getAccessibleContext() {
            return this;
         }

         private AccessibleContext getCurrentAccessibleContext() {
            return this.rendererComponent.getAccessibleContext();
         }

         private Component getCurrentComponent() {
            return this.rendererComponent;
         }

         public String getAccessibleName() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 != null) {
               String var2 = var1.getAccessibleName();
               if (var2 != null && var2 != "") {
                  return var1.getAccessibleName();
               }
            }

            return this.accessibleName != null && this.accessibleName != "" ? this.accessibleName : null;
         }

         public void setAccessibleName(String var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 != null) {
               var2.setAccessibleName(var1);
            } else {
               super.setAccessibleName(var1);
            }

         }

         public String getAccessibleDescription() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            return var1 != null ? var1.getAccessibleDescription() : super.getAccessibleDescription();
         }

         public void setAccessibleDescription(String var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 != null) {
               var2.setAccessibleDescription(var1);
            } else {
               super.setAccessibleDescription(var1);
            }

         }

         public AccessibleRole getAccessibleRole() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            return var1 != null ? var1.getAccessibleRole() : AccessibleRole.UNKNOWN;
         }

         public AccessibleStateSet getAccessibleStateSet() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            AccessibleStateSet var2 = null;
            if (var1 != null) {
               var2 = var1.getAccessibleStateSet();
            }

            if (var2 == null) {
               var2 = new AccessibleStateSet();
            }

            Rectangle var3 = JTable.this.getVisibleRect();
            Rectangle var4 = JTable.this.getCellRect(this.row, this.column, false);
            if (var3.intersects(var4)) {
               var2.add(AccessibleState.SHOWING);
            } else if (var2.contains(AccessibleState.SHOWING)) {
               var2.remove(AccessibleState.SHOWING);
            }

            if (JTable.this.isCellSelected(this.row, this.column)) {
               var2.add(AccessibleState.SELECTED);
            } else if (var2.contains(AccessibleState.SELECTED)) {
               var2.remove(AccessibleState.SELECTED);
            }

            if (this.row == JTable.this.getSelectedRow() && this.column == JTable.this.getSelectedColumn()) {
               var2.add(AccessibleState.ACTIVE);
            }

            var2.add(AccessibleState.TRANSIENT);
            return var2;
         }

         public Accessible getAccessibleParent() {
            return this.parent;
         }

         public int getAccessibleIndexInParent() {
            return this.column;
         }

         public int getAccessibleChildrenCount() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            return var1 != null ? var1.getAccessibleChildrenCount() : 0;
         }

         public Accessible getAccessibleChild(int var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 != null) {
               Accessible var3 = var2.getAccessibleChild(var1);
               var2.setAccessibleParent(this);
               return var3;
            } else {
               return null;
            }
         }

         public Locale getLocale() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            return var1 != null ? var1.getLocale() : null;
         }

         public void addPropertyChangeListener(PropertyChangeListener var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 != null) {
               var2.addPropertyChangeListener(var1);
            } else {
               super.addPropertyChangeListener(var1);
            }

         }

         public void removePropertyChangeListener(PropertyChangeListener var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 != null) {
               var2.removePropertyChangeListener(var1);
            } else {
               super.removePropertyChangeListener(var1);
            }

         }

         public AccessibleAction getAccessibleAction() {
            return this.getCurrentAccessibleContext().getAccessibleAction();
         }

         public AccessibleComponent getAccessibleComponent() {
            return this;
         }

         public AccessibleSelection getAccessibleSelection() {
            return this.getCurrentAccessibleContext().getAccessibleSelection();
         }

         public AccessibleText getAccessibleText() {
            return this.getCurrentAccessibleContext().getAccessibleText();
         }

         public AccessibleValue getAccessibleValue() {
            return this.getCurrentAccessibleContext().getAccessibleValue();
         }

         public Color getBackground() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 instanceof AccessibleComponent) {
               return ((AccessibleComponent)var1).getBackground();
            } else {
               Component var2 = this.getCurrentComponent();
               return var2 != null ? var2.getBackground() : null;
            }
         }

         public void setBackground(Color var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               ((AccessibleComponent)var2).setBackground(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  var3.setBackground(var1);
               }
            }

         }

         public Color getForeground() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 instanceof AccessibleComponent) {
               return ((AccessibleComponent)var1).getForeground();
            } else {
               Component var2 = this.getCurrentComponent();
               return var2 != null ? var2.getForeground() : null;
            }
         }

         public void setForeground(Color var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               ((AccessibleComponent)var2).setForeground(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  var3.setForeground(var1);
               }
            }

         }

         public Cursor getCursor() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 instanceof AccessibleComponent) {
               return ((AccessibleComponent)var1).getCursor();
            } else {
               Component var2 = this.getCurrentComponent();
               if (var2 != null) {
                  return var2.getCursor();
               } else {
                  Accessible var3 = this.getAccessibleParent();
                  return var3 instanceof AccessibleComponent ? ((AccessibleComponent)var3).getCursor() : null;
               }
            }
         }

         public void setCursor(Cursor var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               ((AccessibleComponent)var2).setCursor(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  var3.setCursor(var1);
               }
            }

         }

         public Font getFont() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 instanceof AccessibleComponent) {
               return ((AccessibleComponent)var1).getFont();
            } else {
               Component var2 = this.getCurrentComponent();
               return var2 != null ? var2.getFont() : null;
            }
         }

         public void setFont(Font var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               ((AccessibleComponent)var2).setFont(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  var3.setFont(var1);
               }
            }

         }

         public FontMetrics getFontMetrics(Font var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               return ((AccessibleComponent)var2).getFontMetrics(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               return var3 != null ? var3.getFontMetrics(var1) : null;
            }
         }

         public boolean isEnabled() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 instanceof AccessibleComponent) {
               return ((AccessibleComponent)var1).isEnabled();
            } else {
               Component var2 = this.getCurrentComponent();
               return var2 != null ? var2.isEnabled() : false;
            }
         }

         public void setEnabled(boolean var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               ((AccessibleComponent)var2).setEnabled(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  var3.setEnabled(var1);
               }
            }

         }

         public boolean isVisible() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 instanceof AccessibleComponent) {
               return ((AccessibleComponent)var1).isVisible();
            } else {
               Component var2 = this.getCurrentComponent();
               return var2 != null ? var2.isVisible() : false;
            }
         }

         public void setVisible(boolean var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               ((AccessibleComponent)var2).setVisible(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  var3.setVisible(var1);
               }
            }

         }

         public boolean isShowing() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 instanceof AccessibleComponent) {
               return var1.getAccessibleParent() != null ? ((AccessibleComponent)var1).isShowing() : this.isVisible();
            } else {
               Component var2 = this.getCurrentComponent();
               return var2 != null ? var2.isShowing() : false;
            }
         }

         public boolean contains(Point var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               Rectangle var5 = ((AccessibleComponent)var2).getBounds();
               return var5.contains(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  Rectangle var4 = var3.getBounds();
                  return var4.contains(var1);
               } else {
                  return this.getBounds().contains(var1);
               }
            }
         }

         public Point getLocationOnScreen() {
            if (this.parent != null && this.parent.isShowing()) {
               Point var1 = this.parent.getLocationOnScreen();
               Point var2 = this.getLocation();
               var2.translate(var1.x, var1.y);
               return var2;
            } else {
               return null;
            }
         }

         public Point getLocation() {
            if (this.parent != null) {
               Rectangle var1 = this.parent.getHeaderRect(this.column);
               if (var1 != null) {
                  return var1.getLocation();
               }
            }

            return null;
         }

         public void setLocation(Point var1) {
         }

         public Rectangle getBounds() {
            return this.parent != null ? this.parent.getHeaderRect(this.column) : null;
         }

         public void setBounds(Rectangle var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               ((AccessibleComponent)var2).setBounds(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  var3.setBounds(var1);
               }
            }

         }

         public Dimension getSize() {
            if (this.parent != null) {
               Rectangle var1 = this.parent.getHeaderRect(this.column);
               if (var1 != null) {
                  return var1.getSize();
               }
            }

            return null;
         }

         public void setSize(Dimension var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               ((AccessibleComponent)var2).setSize(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  var3.setSize(var1);
               }
            }

         }

         public Accessible getAccessibleAt(Point var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            return var2 instanceof AccessibleComponent ? ((AccessibleComponent)var2).getAccessibleAt(var1) : null;
         }

         public boolean isFocusTraversable() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 instanceof AccessibleComponent) {
               return ((AccessibleComponent)var1).isFocusTraversable();
            } else {
               Component var2 = this.getCurrentComponent();
               return var2 != null ? var2.isFocusTraversable() : false;
            }
         }

         public void requestFocus() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 instanceof AccessibleComponent) {
               ((AccessibleComponent)var1).requestFocus();
            } else {
               Component var2 = this.getCurrentComponent();
               if (var2 != null) {
                  var2.requestFocus();
               }
            }

         }

         public void addFocusListener(FocusListener var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               ((AccessibleComponent)var2).addFocusListener(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  var3.addFocusListener(var1);
               }
            }

         }

         public void removeFocusListener(FocusListener var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               ((AccessibleComponent)var2).removeFocusListener(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  var3.removeFocusListener(var1);
               }
            }

         }
      }

      protected class AccessibleJTableCell extends AccessibleContext implements Accessible, AccessibleComponent {
         private JTable parent;
         private int row;
         private int column;
         private int index;

         public AccessibleJTableCell(JTable var2, int var3, int var4, int var5) {
            this.parent = var2;
            this.row = var3;
            this.column = var4;
            this.index = var5;
            this.setAccessibleParent(this.parent);
         }

         public AccessibleContext getAccessibleContext() {
            return this;
         }

         protected AccessibleContext getCurrentAccessibleContext() {
            TableColumn var1 = JTable.this.getColumnModel().getColumn(this.column);
            TableCellRenderer var2 = var1.getCellRenderer();
            if (var2 == null) {
               Class var3 = JTable.this.getColumnClass(this.column);
               var2 = JTable.this.getDefaultRenderer(var3);
            }

            Component var4 = var2.getTableCellRendererComponent(JTable.this, JTable.this.getValueAt(this.row, this.column), false, false, this.row, this.column);
            return var4 instanceof Accessible ? var4.getAccessibleContext() : null;
         }

         protected Component getCurrentComponent() {
            TableColumn var1 = JTable.this.getColumnModel().getColumn(this.column);
            TableCellRenderer var2 = var1.getCellRenderer();
            if (var2 == null) {
               Class var3 = JTable.this.getColumnClass(this.column);
               var2 = JTable.this.getDefaultRenderer(var3);
            }

            return var2.getTableCellRendererComponent(JTable.this, (Object)null, false, false, this.row, this.column);
         }

         public String getAccessibleName() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 != null) {
               String var2 = var1.getAccessibleName();
               if (var2 != null && var2 != "") {
                  return var2;
               }
            }

            return this.accessibleName != null && this.accessibleName != "" ? this.accessibleName : (String)JTable.this.getClientProperty("AccessibleName");
         }

         public void setAccessibleName(String var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 != null) {
               var2.setAccessibleName(var1);
            } else {
               super.setAccessibleName(var1);
            }

         }

         public String getAccessibleDescription() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            return var1 != null ? var1.getAccessibleDescription() : super.getAccessibleDescription();
         }

         public void setAccessibleDescription(String var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 != null) {
               var2.setAccessibleDescription(var1);
            } else {
               super.setAccessibleDescription(var1);
            }

         }

         public AccessibleRole getAccessibleRole() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            return var1 != null ? var1.getAccessibleRole() : AccessibleRole.UNKNOWN;
         }

         public AccessibleStateSet getAccessibleStateSet() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            AccessibleStateSet var2 = null;
            if (var1 != null) {
               var2 = var1.getAccessibleStateSet();
            }

            if (var2 == null) {
               var2 = new AccessibleStateSet();
            }

            Rectangle var3 = JTable.this.getVisibleRect();
            Rectangle var4 = JTable.this.getCellRect(this.row, this.column, false);
            if (var3.intersects(var4)) {
               var2.add(AccessibleState.SHOWING);
            } else if (var2.contains(AccessibleState.SHOWING)) {
               var2.remove(AccessibleState.SHOWING);
            }

            if (this.parent.isCellSelected(this.row, this.column)) {
               var2.add(AccessibleState.SELECTED);
            } else if (var2.contains(AccessibleState.SELECTED)) {
               var2.remove(AccessibleState.SELECTED);
            }

            if (this.row == JTable.this.getSelectedRow() && this.column == JTable.this.getSelectedColumn()) {
               var2.add(AccessibleState.ACTIVE);
            }

            var2.add(AccessibleState.TRANSIENT);
            return var2;
         }

         public Accessible getAccessibleParent() {
            return this.parent;
         }

         public int getAccessibleIndexInParent() {
            return this.index;
         }

         public int getAccessibleChildrenCount() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            return var1 != null ? var1.getAccessibleChildrenCount() : 0;
         }

         public Accessible getAccessibleChild(int var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 != null) {
               Accessible var3 = var2.getAccessibleChild(var1);
               var2.setAccessibleParent(this);
               return var3;
            } else {
               return null;
            }
         }

         public Locale getLocale() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            return var1 != null ? var1.getLocale() : null;
         }

         public void addPropertyChangeListener(PropertyChangeListener var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 != null) {
               var2.addPropertyChangeListener(var1);
            } else {
               super.addPropertyChangeListener(var1);
            }

         }

         public void removePropertyChangeListener(PropertyChangeListener var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 != null) {
               var2.removePropertyChangeListener(var1);
            } else {
               super.removePropertyChangeListener(var1);
            }

         }

         public AccessibleAction getAccessibleAction() {
            return this.getCurrentAccessibleContext().getAccessibleAction();
         }

         public AccessibleComponent getAccessibleComponent() {
            return this;
         }

         public AccessibleSelection getAccessibleSelection() {
            return this.getCurrentAccessibleContext().getAccessibleSelection();
         }

         public AccessibleText getAccessibleText() {
            return this.getCurrentAccessibleContext().getAccessibleText();
         }

         public AccessibleValue getAccessibleValue() {
            return this.getCurrentAccessibleContext().getAccessibleValue();
         }

         public Color getBackground() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 instanceof AccessibleComponent) {
               return ((AccessibleComponent)var1).getBackground();
            } else {
               Component var2 = this.getCurrentComponent();
               return var2 != null ? var2.getBackground() : null;
            }
         }

         public void setBackground(Color var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               ((AccessibleComponent)var2).setBackground(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  var3.setBackground(var1);
               }
            }

         }

         public Color getForeground() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 instanceof AccessibleComponent) {
               return ((AccessibleComponent)var1).getForeground();
            } else {
               Component var2 = this.getCurrentComponent();
               return var2 != null ? var2.getForeground() : null;
            }
         }

         public void setForeground(Color var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               ((AccessibleComponent)var2).setForeground(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  var3.setForeground(var1);
               }
            }

         }

         public Cursor getCursor() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 instanceof AccessibleComponent) {
               return ((AccessibleComponent)var1).getCursor();
            } else {
               Component var2 = this.getCurrentComponent();
               if (var2 != null) {
                  return var2.getCursor();
               } else {
                  Accessible var3 = this.getAccessibleParent();
                  return var3 instanceof AccessibleComponent ? ((AccessibleComponent)var3).getCursor() : null;
               }
            }
         }

         public void setCursor(Cursor var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               ((AccessibleComponent)var2).setCursor(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  var3.setCursor(var1);
               }
            }

         }

         public Font getFont() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 instanceof AccessibleComponent) {
               return ((AccessibleComponent)var1).getFont();
            } else {
               Component var2 = this.getCurrentComponent();
               return var2 != null ? var2.getFont() : null;
            }
         }

         public void setFont(Font var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               ((AccessibleComponent)var2).setFont(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  var3.setFont(var1);
               }
            }

         }

         public FontMetrics getFontMetrics(Font var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               return ((AccessibleComponent)var2).getFontMetrics(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               return var3 != null ? var3.getFontMetrics(var1) : null;
            }
         }

         public boolean isEnabled() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 instanceof AccessibleComponent) {
               return ((AccessibleComponent)var1).isEnabled();
            } else {
               Component var2 = this.getCurrentComponent();
               return var2 != null ? var2.isEnabled() : false;
            }
         }

         public void setEnabled(boolean var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               ((AccessibleComponent)var2).setEnabled(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  var3.setEnabled(var1);
               }
            }

         }

         public boolean isVisible() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 instanceof AccessibleComponent) {
               return ((AccessibleComponent)var1).isVisible();
            } else {
               Component var2 = this.getCurrentComponent();
               return var2 != null ? var2.isVisible() : false;
            }
         }

         public void setVisible(boolean var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               ((AccessibleComponent)var2).setVisible(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  var3.setVisible(var1);
               }
            }

         }

         public boolean isShowing() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 instanceof AccessibleComponent) {
               return var1.getAccessibleParent() != null ? ((AccessibleComponent)var1).isShowing() : this.isVisible();
            } else {
               Component var2 = this.getCurrentComponent();
               return var2 != null ? var2.isShowing() : false;
            }
         }

         public boolean contains(Point var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               Rectangle var5 = ((AccessibleComponent)var2).getBounds();
               return var5.contains(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  Rectangle var4 = var3.getBounds();
                  return var4.contains(var1);
               } else {
                  return this.getBounds().contains(var1);
               }
            }
         }

         public Point getLocationOnScreen() {
            if (this.parent != null && this.parent.isShowing()) {
               Point var1 = this.parent.getLocationOnScreen();
               Point var2 = this.getLocation();
               var2.translate(var1.x, var1.y);
               return var2;
            } else {
               return null;
            }
         }

         public Point getLocation() {
            if (this.parent != null) {
               Rectangle var1 = this.parent.getCellRect(this.row, this.column, false);
               if (var1 != null) {
                  return var1.getLocation();
               }
            }

            return null;
         }

         public void setLocation(Point var1) {
         }

         public Rectangle getBounds() {
            return this.parent != null ? this.parent.getCellRect(this.row, this.column, false) : null;
         }

         public void setBounds(Rectangle var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               ((AccessibleComponent)var2).setBounds(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  var3.setBounds(var1);
               }
            }

         }

         public Dimension getSize() {
            if (this.parent != null) {
               Rectangle var1 = this.parent.getCellRect(this.row, this.column, false);
               if (var1 != null) {
                  return var1.getSize();
               }
            }

            return null;
         }

         public void setSize(Dimension var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               ((AccessibleComponent)var2).setSize(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  var3.setSize(var1);
               }
            }

         }

         public Accessible getAccessibleAt(Point var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            return var2 instanceof AccessibleComponent ? ((AccessibleComponent)var2).getAccessibleAt(var1) : null;
         }

         public boolean isFocusTraversable() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 instanceof AccessibleComponent) {
               return ((AccessibleComponent)var1).isFocusTraversable();
            } else {
               Component var2 = this.getCurrentComponent();
               return var2 != null ? var2.isFocusTraversable() : false;
            }
         }

         public void requestFocus() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 instanceof AccessibleComponent) {
               ((AccessibleComponent)var1).requestFocus();
            } else {
               Component var2 = this.getCurrentComponent();
               if (var2 != null) {
                  var2.requestFocus();
               }
            }

         }

         public void addFocusListener(FocusListener var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               ((AccessibleComponent)var2).addFocusListener(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  var3.addFocusListener(var1);
               }
            }

         }

         public void removeFocusListener(FocusListener var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               ((AccessibleComponent)var2).removeFocusListener(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  var3.removeFocusListener(var1);
               }
            }

         }
      }

      private class AccessibleTableHeader implements AccessibleTable {
         private JTableHeader header;
         private TableColumnModel headerModel;

         AccessibleTableHeader(JTableHeader var2) {
            this.header = var2;
            this.headerModel = var2.getColumnModel();
         }

         public Accessible getAccessibleCaption() {
            return null;
         }

         public void setAccessibleCaption(Accessible var1) {
         }

         public Accessible getAccessibleSummary() {
            return null;
         }

         public void setAccessibleSummary(Accessible var1) {
         }

         public int getAccessibleRowCount() {
            return 1;
         }

         public int getAccessibleColumnCount() {
            return this.headerModel.getColumnCount();
         }

         public Accessible getAccessibleAt(int var1, int var2) {
            TableColumn var3 = this.headerModel.getColumn(var2);
            TableCellRenderer var4 = var3.getHeaderRenderer();
            if (var4 == null) {
               var4 = this.header.getDefaultRenderer();
            }

            Component var5 = var4.getTableCellRendererComponent(this.header.getTable(), var3.getHeaderValue(), false, false, -1, var2);
            return AccessibleJTable.this.new AccessibleJTableHeaderCell(var1, var2, JTable.this.getTableHeader(), var5);
         }

         public int getAccessibleRowExtentAt(int var1, int var2) {
            return 1;
         }

         public int getAccessibleColumnExtentAt(int var1, int var2) {
            return 1;
         }

         public AccessibleTable getAccessibleRowHeader() {
            return null;
         }

         public void setAccessibleRowHeader(AccessibleTable var1) {
         }

         public AccessibleTable getAccessibleColumnHeader() {
            return null;
         }

         public void setAccessibleColumnHeader(AccessibleTable var1) {
         }

         public Accessible getAccessibleRowDescription(int var1) {
            return null;
         }

         public void setAccessibleRowDescription(int var1, Accessible var2) {
         }

         public Accessible getAccessibleColumnDescription(int var1) {
            return null;
         }

         public void setAccessibleColumnDescription(int var1, Accessible var2) {
         }

         public boolean isAccessibleSelected(int var1, int var2) {
            return false;
         }

         public boolean isAccessibleRowSelected(int var1) {
            return false;
         }

         public boolean isAccessibleColumnSelected(int var1) {
            return false;
         }

         public int[] getSelectedAccessibleRows() {
            return new int[0];
         }

         public int[] getSelectedAccessibleColumns() {
            return new int[0];
         }
      }

      protected class AccessibleJTableModelChange implements AccessibleTableModelChange {
         protected int type;
         protected int firstRow;
         protected int lastRow;
         protected int firstColumn;
         protected int lastColumn;

         protected AccessibleJTableModelChange(int var2, int var3, int var4, int var5, int var6) {
            this.type = var2;
            this.firstRow = var3;
            this.lastRow = var4;
            this.firstColumn = var5;
            this.lastColumn = var6;
         }

         public int getType() {
            return this.type;
         }

         public int getFirstRow() {
            return this.firstRow;
         }

         public int getLastRow() {
            return this.lastRow;
         }

         public int getFirstColumn() {
            return this.firstColumn;
         }

         public int getLastColumn() {
            return this.lastColumn;
         }
      }
   }

   private class ThreadSafePrintable implements Printable {
      private Printable printDelegate;
      private int retVal;
      private Throwable retThrowable;

      public ThreadSafePrintable(Printable var2) {
         this.printDelegate = var2;
      }

      public int print(final Graphics var1, final PageFormat var2, final int var3) throws PrinterException {
         Runnable var4 = new Runnable() {
            public synchronized void run() {
               try {
                  ThreadSafePrintable.this.retVal = ThreadSafePrintable.this.printDelegate.print(var1, var2, var3);
               } catch (Throwable var5) {
                  ThreadSafePrintable.this.retThrowable = var5;
               } finally {
                  this.notifyAll();
               }

            }
         };
         synchronized(var4) {
            this.retVal = -1;
            this.retThrowable = null;
            SwingUtilities.invokeLater(var4);

            while(this.retVal == -1 && this.retThrowable == null) {
               try {
                  var4.wait();
               } catch (InterruptedException var8) {
               }
            }

            if (this.retThrowable != null) {
               if (this.retThrowable instanceof PrinterException) {
                  throw (PrinterException)this.retThrowable;
               } else if (this.retThrowable instanceof RuntimeException) {
                  throw (RuntimeException)this.retThrowable;
               } else if (this.retThrowable instanceof Error) {
                  throw (Error)this.retThrowable;
               } else {
                  throw new AssertionError(this.retThrowable);
               }
            } else {
               return this.retVal;
            }
         }
      }
   }

   class CellEditorRemover implements PropertyChangeListener {
      KeyboardFocusManager focusManager;

      public CellEditorRemover(KeyboardFocusManager var2) {
         this.focusManager = var2;
      }

      public void propertyChange(PropertyChangeEvent var1) {
         if (JTable.this.isEditing() && JTable.this.getClientProperty("terminateEditOnFocusLost") == Boolean.TRUE) {
            for(Object var2 = this.focusManager.getPermanentFocusOwner(); var2 != null; var2 = ((Component)var2).getParent()) {
               if (var2 == JTable.this) {
                  return;
               }

               if (var2 instanceof Window || var2 instanceof Applet && ((Component)var2).getParent() == null) {
                  if (var2 == SwingUtilities.getRoot(JTable.this) && !JTable.this.getCellEditor().stopCellEditing()) {
                     JTable.this.getCellEditor().cancelCellEditing();
                  }
                  break;
               }
            }

         }
      }
   }

   static class BooleanEditor extends DefaultCellEditor {
      public BooleanEditor() {
         super(new JCheckBox());
         JCheckBox var1 = (JCheckBox)this.getComponent();
         var1.setHorizontalAlignment(0);
      }
   }

   static class NumberEditor extends JTable.GenericEditor {
      public NumberEditor() {
         ((JTextField)this.getComponent()).setHorizontalAlignment(4);
      }
   }

   static class GenericEditor extends DefaultCellEditor {
      Class[] argTypes = new Class[]{String.class};
      Constructor constructor;
      Object value;

      public GenericEditor() {
         super(new JTextField());
         this.getComponent().setName("Table.editor");
      }

      public boolean stopCellEditing() {
         String var1 = (String)super.getCellEditorValue();

         try {
            if ("".equals(var1)) {
               if (this.constructor.getDeclaringClass() == String.class) {
                  this.value = var1;
               }

               return super.stopCellEditing();
            }

            SwingUtilities2.checkAccess(this.constructor.getModifiers());
            this.value = this.constructor.newInstance(var1);
         } catch (Exception var3) {
            ((JComponent)this.getComponent()).setBorder(new LineBorder(Color.red));
            return false;
         }

         return super.stopCellEditing();
      }

      public Component getTableCellEditorComponent(JTable var1, Object var2, boolean var3, int var4, int var5) {
         this.value = null;
         ((JComponent)this.getComponent()).setBorder(new LineBorder(Color.black));

         try {
            Class var6 = var1.getColumnClass(var5);
            if (var6 == Object.class) {
               var6 = String.class;
            }

            ReflectUtil.checkPackageAccess(var6);
            SwingUtilities2.checkAccess(var6.getModifiers());
            this.constructor = var6.getConstructor(this.argTypes);
         } catch (Exception var7) {
            return null;
         }

         return super.getTableCellEditorComponent(var1, var2, var3, var4, var5);
      }

      public Object getCellEditorValue() {
         return this.value;
      }
   }

   static class BooleanRenderer extends JCheckBox implements TableCellRenderer, UIResource {
      private static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

      public BooleanRenderer() {
         this.setHorizontalAlignment(0);
         this.setBorderPainted(true);
      }

      public Component getTableCellRendererComponent(JTable var1, Object var2, boolean var3, boolean var4, int var5, int var6) {
         if (var3) {
            this.setForeground(var1.getSelectionForeground());
            super.setBackground(var1.getSelectionBackground());
         } else {
            this.setForeground(var1.getForeground());
            this.setBackground(var1.getBackground());
         }

         this.setSelected(var2 != null && (Boolean)var2);
         if (var4) {
            this.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
         } else {
            this.setBorder(noFocusBorder);
         }

         return this;
      }
   }

   static class IconRenderer extends DefaultTableCellRenderer.UIResource {
      public IconRenderer() {
         this.setHorizontalAlignment(0);
      }

      public void setValue(Object var1) {
         this.setIcon(var1 instanceof Icon ? (Icon)var1 : null);
      }
   }

   static class DateRenderer extends DefaultTableCellRenderer.UIResource {
      DateFormat formatter;

      public DateRenderer() {
      }

      public void setValue(Object var1) {
         if (this.formatter == null) {
            this.formatter = DateFormat.getDateInstance();
         }

         this.setText(var1 == null ? "" : this.formatter.format(var1));
      }
   }

   static class DoubleRenderer extends JTable.NumberRenderer {
      NumberFormat formatter;

      public DoubleRenderer() {
      }

      public void setValue(Object var1) {
         if (this.formatter == null) {
            this.formatter = NumberFormat.getInstance();
         }

         this.setText(var1 == null ? "" : this.formatter.format(var1));
      }
   }

   static class NumberRenderer extends DefaultTableCellRenderer.UIResource {
      public NumberRenderer() {
         this.setHorizontalAlignment(4);
      }
   }

   private final class ModelChange {
      int startModelIndex;
      int endModelIndex;
      int type;
      int modelRowCount;
      TableModelEvent event;
      int length;
      boolean allRowsChanged;

      ModelChange(TableModelEvent var2) {
         this.startModelIndex = Math.max(0, var2.getFirstRow());
         this.endModelIndex = var2.getLastRow();
         this.modelRowCount = JTable.this.getModel().getRowCount();
         if (this.endModelIndex < 0) {
            this.endModelIndex = Math.max(0, this.modelRowCount - 1);
         }

         this.length = this.endModelIndex - this.startModelIndex + 1;
         this.type = var2.getType();
         this.event = var2;
         this.allRowsChanged = var2.getLastRow() == Integer.MAX_VALUE;
      }
   }

   private final class SortManager {
      RowSorter<? extends TableModel> sorter;
      private ListSelectionModel modelSelection;
      private int modelLeadIndex;
      private boolean syncingSelection;
      private int[] lastModelSelection;
      private SizeSequence modelRowSizes;

      SortManager(RowSorter<? extends TableModel> var2) {
         this.sorter = var2;
         var2.addRowSorterListener(JTable.this);
      }

      public void dispose() {
         if (this.sorter != null) {
            this.sorter.removeRowSorterListener(JTable.this);
         }

      }

      public void setViewRowHeight(int var1, int var2) {
         if (this.modelRowSizes == null) {
            this.modelRowSizes = new SizeSequence(JTable.this.getModel().getRowCount(), JTable.this.getRowHeight());
         }

         this.modelRowSizes.setSize(JTable.this.convertRowIndexToModel(var1), var2);
      }

      public void allChanged() {
         this.modelLeadIndex = -1;
         this.modelSelection = null;
         this.modelRowSizes = null;
      }

      public void viewSelectionChanged(ListSelectionEvent var1) {
         if (!this.syncingSelection && this.modelSelection != null) {
            this.modelSelection = null;
         }

      }

      public void prepareForChange(RowSorterEvent var1, JTable.ModelChange var2) {
         if (JTable.this.getUpdateSelectionOnSort()) {
            this.cacheSelection(var1, var2);
         }

      }

      private void cacheSelection(RowSorterEvent var1, JTable.ModelChange var2) {
         if (var1 != null) {
            if (this.modelSelection == null && this.sorter.getViewRowCount() != JTable.this.getModel().getRowCount()) {
               this.modelSelection = new DefaultListSelectionModel();
               ListSelectionModel var3 = JTable.this.getSelectionModel();
               int var4 = var3.getMinSelectionIndex();
               int var5 = var3.getMaxSelectionIndex();

               int var6;
               for(int var7 = var4; var7 <= var5; ++var7) {
                  if (var3.isSelectedIndex(var7)) {
                     var6 = JTable.this.convertRowIndexToModel(var1, var7);
                     if (var6 != -1) {
                        this.modelSelection.addSelectionInterval(var6, var6);
                     }
                  }
               }

               var6 = JTable.this.convertRowIndexToModel(var1, var3.getLeadSelectionIndex());
               SwingUtilities2.setLeadAnchorWithoutSelection(this.modelSelection, var6, var6);
            } else if (this.modelSelection == null) {
               this.cacheModelSelection(var1);
            }
         } else if (var2.allRowsChanged) {
            this.modelSelection = null;
         } else if (this.modelSelection != null) {
            switch(var2.type) {
            case -1:
               this.modelSelection.removeIndexInterval(var2.startModelIndex, var2.endModelIndex);
               break;
            case 1:
               this.modelSelection.insertIndexInterval(var2.startModelIndex, var2.length, true);
            }
         } else {
            this.cacheModelSelection((RowSorterEvent)null);
         }

      }

      private void cacheModelSelection(RowSorterEvent var1) {
         this.lastModelSelection = JTable.this.convertSelectionToModel(var1);
         this.modelLeadIndex = JTable.this.convertRowIndexToModel(var1, JTable.this.selectionModel.getLeadSelectionIndex());
      }

      public void processChange(RowSorterEvent var1, JTable.ModelChange var2, boolean var3) {
         if (var2 != null) {
            if (var2.allRowsChanged) {
               this.modelRowSizes = null;
               JTable.this.rowModel = null;
            } else if (this.modelRowSizes != null) {
               if (var2.type == 1) {
                  this.modelRowSizes.insertEntries(var2.startModelIndex, var2.endModelIndex - var2.startModelIndex + 1, JTable.this.getRowHeight());
               } else if (var2.type == -1) {
                  this.modelRowSizes.removeEntries(var2.startModelIndex, var2.endModelIndex - var2.startModelIndex + 1);
               }
            }
         }

         if (var3) {
            this.setViewRowHeightsFromModel();
            this.restoreSelection(var2);
         }

      }

      private void setViewRowHeightsFromModel() {
         if (this.modelRowSizes != null) {
            JTable.this.rowModel.setSizes(JTable.this.getRowCount(), JTable.this.getRowHeight());

            for(int var1 = JTable.this.getRowCount() - 1; var1 >= 0; --var1) {
               int var2 = JTable.this.convertRowIndexToModel(var1);
               JTable.this.rowModel.setSize(var1, this.modelRowSizes.getSize(var2));
            }
         }

      }

      private void restoreSelection(JTable.ModelChange var1) {
         this.syncingSelection = true;
         if (this.lastModelSelection != null) {
            JTable.this.restoreSortingSelection(this.lastModelSelection, this.modelLeadIndex, var1);
            this.lastModelSelection = null;
         } else if (this.modelSelection != null) {
            ListSelectionModel var2 = JTable.this.getSelectionModel();
            var2.setValueIsAdjusting(true);
            var2.clearSelection();
            int var3 = this.modelSelection.getMinSelectionIndex();
            int var4 = this.modelSelection.getMaxSelectionIndex();

            int var6;
            for(var6 = var3; var6 <= var4; ++var6) {
               if (this.modelSelection.isSelectedIndex(var6)) {
                  int var5 = JTable.this.convertRowIndexToView(var6);
                  if (var5 != -1) {
                     var2.addSelectionInterval(var5, var5);
                  }
               }
            }

            var6 = this.modelSelection.getLeadSelectionIndex();
            if (var6 != -1 && !this.modelSelection.isSelectionEmpty()) {
               var6 = JTable.this.convertRowIndexToView(var6);
            }

            SwingUtilities2.setLeadAnchorWithoutSelection(var2, var6, var6);
            var2.setValueIsAdjusting(false);
         }

         this.syncingSelection = false;
      }
   }

   private interface Resizable3 extends JTable.Resizable2 {
      int getMidPointAt(int var1);
   }

   private interface Resizable2 {
      int getElementCount();

      int getLowerBoundAt(int var1);

      int getUpperBoundAt(int var1);

      void setSizeAt(int var1, int var2);
   }

   public static final class DropLocation extends TransferHandler.DropLocation {
      private final int row;
      private final int col;
      private final boolean isInsertRow;
      private final boolean isInsertCol;

      private DropLocation(Point var1, int var2, int var3, boolean var4, boolean var5) {
         super(var1);
         this.row = var2;
         this.col = var3;
         this.isInsertRow = var4;
         this.isInsertCol = var5;
      }

      public int getRow() {
         return this.row;
      }

      public int getColumn() {
         return this.col;
      }

      public boolean isInsertRow() {
         return this.isInsertRow;
      }

      public boolean isInsertColumn() {
         return this.isInsertCol;
      }

      public String toString() {
         return this.getClass().getName() + "[dropPoint=" + this.getDropPoint() + ",row=" + this.row + ",column=" + this.col + ",insertRow=" + this.isInsertRow + ",insertColumn=" + this.isInsertCol + "]";
      }

      // $FF: synthetic method
      DropLocation(Point var1, int var2, int var3, boolean var4, boolean var5, Object var6) {
         this(var1, var2, var3, var4, var5);
      }
   }

   public static enum PrintMode {
      NORMAL,
      FIT_WIDTH;
   }
}
