package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ActionMap;
import javax.swing.CellRendererPane;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ListUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.Position;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class BasicListUI extends ListUI {
   private static final StringBuilder BASELINE_COMPONENT_KEY = new StringBuilder("List.baselineComponent");
   protected JList list = null;
   protected CellRendererPane rendererPane;
   protected FocusListener focusListener;
   protected MouseInputListener mouseInputListener;
   protected ListSelectionListener listSelectionListener;
   protected ListDataListener listDataListener;
   protected PropertyChangeListener propertyChangeListener;
   private BasicListUI.Handler handler;
   protected int[] cellHeights = null;
   protected int cellHeight = -1;
   protected int cellWidth = -1;
   protected int updateLayoutStateNeeded = 1;
   private int listHeight;
   private int listWidth;
   private int layoutOrientation;
   private int columnCount;
   private int preferredHeight;
   private int rowsPerColumn;
   private long timeFactor = 1000L;
   private boolean isFileList = false;
   private boolean isLeftToRight = true;
   protected static final int modelChanged = 1;
   protected static final int selectionModelChanged = 2;
   protected static final int fontChanged = 4;
   protected static final int fixedCellWidthChanged = 8;
   protected static final int fixedCellHeightChanged = 16;
   protected static final int prototypeCellValueChanged = 32;
   protected static final int cellRendererChanged = 64;
   private static final int layoutOrientationChanged = 128;
   private static final int heightChanged = 256;
   private static final int widthChanged = 512;
   private static final int componentOrientationChanged = 1024;
   private static final int DROP_LINE_THICKNESS = 2;
   private static final int CHANGE_LEAD = 0;
   private static final int CHANGE_SELECTION = 1;
   private static final int EXTEND_SELECTION = 2;
   private static final TransferHandler defaultTransferHandler = new BasicListUI.ListTransferHandler();

   static void loadActionMap(LazyActionMap var0) {
      var0.put(new BasicListUI.Actions("selectPreviousColumn"));
      var0.put(new BasicListUI.Actions("selectPreviousColumnExtendSelection"));
      var0.put(new BasicListUI.Actions("selectPreviousColumnChangeLead"));
      var0.put(new BasicListUI.Actions("selectNextColumn"));
      var0.put(new BasicListUI.Actions("selectNextColumnExtendSelection"));
      var0.put(new BasicListUI.Actions("selectNextColumnChangeLead"));
      var0.put(new BasicListUI.Actions("selectPreviousRow"));
      var0.put(new BasicListUI.Actions("selectPreviousRowExtendSelection"));
      var0.put(new BasicListUI.Actions("selectPreviousRowChangeLead"));
      var0.put(new BasicListUI.Actions("selectNextRow"));
      var0.put(new BasicListUI.Actions("selectNextRowExtendSelection"));
      var0.put(new BasicListUI.Actions("selectNextRowChangeLead"));
      var0.put(new BasicListUI.Actions("selectFirstRow"));
      var0.put(new BasicListUI.Actions("selectFirstRowExtendSelection"));
      var0.put(new BasicListUI.Actions("selectFirstRowChangeLead"));
      var0.put(new BasicListUI.Actions("selectLastRow"));
      var0.put(new BasicListUI.Actions("selectLastRowExtendSelection"));
      var0.put(new BasicListUI.Actions("selectLastRowChangeLead"));
      var0.put(new BasicListUI.Actions("scrollUp"));
      var0.put(new BasicListUI.Actions("scrollUpExtendSelection"));
      var0.put(new BasicListUI.Actions("scrollUpChangeLead"));
      var0.put(new BasicListUI.Actions("scrollDown"));
      var0.put(new BasicListUI.Actions("scrollDownExtendSelection"));
      var0.put(new BasicListUI.Actions("scrollDownChangeLead"));
      var0.put(new BasicListUI.Actions("selectAll"));
      var0.put(new BasicListUI.Actions("clearSelection"));
      var0.put(new BasicListUI.Actions("addToSelection"));
      var0.put(new BasicListUI.Actions("toggleAndAnchor"));
      var0.put(new BasicListUI.Actions("extendTo"));
      var0.put(new BasicListUI.Actions("moveSelectionTo"));
      var0.put(TransferHandler.getCutAction().getValue("Name"), TransferHandler.getCutAction());
      var0.put(TransferHandler.getCopyAction().getValue("Name"), TransferHandler.getCopyAction());
      var0.put(TransferHandler.getPasteAction().getValue("Name"), TransferHandler.getPasteAction());
   }

   protected void paintCell(Graphics var1, int var2, Rectangle var3, ListCellRenderer var4, ListModel var5, ListSelectionModel var6, int var7) {
      Object var8 = var5.getElementAt(var2);
      boolean var9 = this.list.hasFocus() && var2 == var7;
      boolean var10 = var6.isSelectedIndex(var2);
      Component var11 = var4.getListCellRendererComponent(this.list, var8, var2, var10, var9);
      int var12 = var3.x;
      int var13 = var3.y;
      int var14 = var3.width;
      int var15 = var3.height;
      if (this.isFileList) {
         int var16 = Math.min(var14, var11.getPreferredSize().width + 4);
         if (!this.isLeftToRight) {
            var12 += var14 - var16;
         }

         var14 = var16;
      }

      this.rendererPane.paintComponent(var1, var11, this.list, var12, var13, var14, var15, true);
   }

   public void paint(Graphics var1, JComponent var2) {
      Shape var3 = var1.getClip();
      this.paintImpl(var1, var2);
      var1.setClip(var3);
      this.paintDropLine(var1);
   }

   private void paintImpl(Graphics var1, JComponent var2) {
      switch(this.layoutOrientation) {
      case 1:
         if (this.list.getHeight() != this.listHeight) {
            this.updateLayoutStateNeeded |= 256;
            this.redrawList();
         }
         break;
      case 2:
         if (this.list.getWidth() != this.listWidth) {
            this.updateLayoutStateNeeded |= 512;
            this.redrawList();
         }
      }

      this.maybeUpdateLayoutState();
      ListCellRenderer var3 = this.list.getCellRenderer();
      ListModel var4 = this.list.getModel();
      ListSelectionModel var5 = this.list.getSelectionModel();
      int var6;
      if (var3 != null && (var6 = var4.getSize()) != 0) {
         Rectangle var7 = var1.getClipBounds();
         int var8;
         int var9;
         if (var2.getComponentOrientation().isLeftToRight()) {
            var8 = this.convertLocationToColumn(var7.x, var7.y);
            var9 = this.convertLocationToColumn(var7.x + var7.width, var7.y);
         } else {
            var8 = this.convertLocationToColumn(var7.x + var7.width, var7.y);
            var9 = this.convertLocationToColumn(var7.x, var7.y);
         }

         int var10 = var7.y + var7.height;
         int var11 = adjustIndex(this.list.getLeadSelectionIndex(), this.list);
         int var12 = this.layoutOrientation == 2 ? this.columnCount : 1;

         for(int var13 = var8; var13 <= var9; ++var13) {
            int var14 = this.convertLocationToRowInColumn(var7.y, var13);
            int var15 = this.getRowCount(var13);
            int var16 = this.getModelIndex(var13, var14);
            Rectangle var17 = this.getCellBounds(this.list, var16, var16);
            if (var17 == null) {
               return;
            }

            while(var14 < var15 && var17.y < var10 && var16 < var6) {
               var17.height = this.getHeight(var13, var14);
               var1.setClip(var17.x, var17.y, var17.width, var17.height);
               var1.clipRect(var7.x, var7.y, var7.width, var7.height);
               this.paintCell(var1, var16, var17, var3, var4, var5, var11);
               var17.y += var17.height;
               var16 += var12;
               ++var14;
            }
         }

         this.rendererPane.removeAll();
      }
   }

   private void paintDropLine(Graphics var1) {
      JList.DropLocation var2 = this.list.getDropLocation();
      if (var2 != null && var2.isInsert()) {
         Color var3 = DefaultLookup.getColor(this.list, this, "List.dropLineColor", (Color)null);
         if (var3 != null) {
            var1.setColor(var3);
            Rectangle var4 = this.getDropLineRect(var2);
            var1.fillRect(var4.x, var4.y, var4.width, var4.height);
         }

      }
   }

   private Rectangle getDropLineRect(JList.DropLocation var1) {
      int var2 = this.list.getModel().getSize();
      if (var2 == 0) {
         Insets var9 = this.list.getInsets();
         if (this.layoutOrientation == 2) {
            return this.isLeftToRight ? new Rectangle(var9.left, var9.top, 2, 20) : new Rectangle(this.list.getWidth() - 2 - var9.right, var9.top, 2, 20);
         } else {
            return new Rectangle(var9.left, var9.top, this.list.getWidth() - var9.left - var9.right, 2);
         }
      } else {
         Rectangle var3 = null;
         int var4 = var1.getIndex();
         boolean var5 = false;
         Rectangle var6;
         Rectangle var7;
         Point var8;
         if (this.layoutOrientation == 2) {
            if (var4 == var2) {
               var5 = true;
            } else if (var4 != 0 && this.convertModelToRow(var4) != this.convertModelToRow(var4 - 1)) {
               var6 = this.getCellBounds(this.list, var4 - 1);
               var7 = this.getCellBounds(this.list, var4);
               var8 = var1.getDropPoint();
               if (this.isLeftToRight) {
                  var5 = Point2D.distance((double)(var6.x + var6.width), (double)(var6.y + (int)((double)var6.height / 2.0D)), (double)var8.x, (double)var8.y) < Point2D.distance((double)var7.x, (double)(var7.y + (int)((double)var7.height / 2.0D)), (double)var8.x, (double)var8.y);
               } else {
                  var5 = Point2D.distance((double)var6.x, (double)(var6.y + (int)((double)var6.height / 2.0D)), (double)var8.x, (double)var8.y) < Point2D.distance((double)(var7.x + var7.width), (double)(var7.y + (int)((double)var6.height / 2.0D)), (double)var8.x, (double)var8.y);
               }
            }

            if (var5) {
               --var4;
               var3 = this.getCellBounds(this.list, var4);
               if (this.isLeftToRight) {
                  var3.x += var3.width;
               } else {
                  var3.x -= 2;
               }
            } else {
               var3 = this.getCellBounds(this.list, var4);
               if (!this.isLeftToRight) {
                  var3.x += var3.width - 2;
               }
            }

            if (var3.x >= this.list.getWidth()) {
               var3.x = this.list.getWidth() - 2;
            } else if (var3.x < 0) {
               var3.x = 0;
            }

            var3.width = 2;
         } else if (this.layoutOrientation == 1) {
            if (var4 == var2) {
               --var4;
               var3 = this.getCellBounds(this.list, var4);
               var3.y += var3.height;
            } else if (var4 != 0 && this.convertModelToColumn(var4) != this.convertModelToColumn(var4 - 1)) {
               var6 = this.getCellBounds(this.list, var4 - 1);
               var7 = this.getCellBounds(this.list, var4);
               var8 = var1.getDropPoint();
               if (Point2D.distance((double)(var6.x + (int)((double)var6.width / 2.0D)), (double)(var6.y + var6.height), (double)var8.x, (double)var8.y) < Point2D.distance((double)(var7.x + (int)((double)var7.width / 2.0D)), (double)var7.y, (double)var8.x, (double)var8.y)) {
                  --var4;
                  var3 = this.getCellBounds(this.list, var4);
                  var3.y += var3.height;
               } else {
                  var3 = this.getCellBounds(this.list, var4);
               }
            } else {
               var3 = this.getCellBounds(this.list, var4);
            }

            if (var3.y >= this.list.getHeight()) {
               var3.y = this.list.getHeight() - 2;
            }

            var3.height = 2;
         } else {
            if (var4 == var2) {
               --var4;
               var3 = this.getCellBounds(this.list, var4);
               var3.y += var3.height;
            } else {
               var3 = this.getCellBounds(this.list, var4);
            }

            if (var3.y >= this.list.getHeight()) {
               var3.y = this.list.getHeight() - 2;
            }

            var3.height = 2;
         }

         return var3;
      }
   }

   public int getBaseline(JComponent var1, int var2, int var3) {
      super.getBaseline(var1, var2, var3);
      int var4 = this.list.getFixedCellHeight();
      UIDefaults var5 = UIManager.getLookAndFeelDefaults();
      Component var6 = (Component)var5.get(BASELINE_COMPONENT_KEY);
      if (var6 == null) {
         Object var7 = (ListCellRenderer)UIManager.get("List.cellRenderer");
         if (var7 == null) {
            var7 = new DefaultListCellRenderer();
         }

         var6 = ((ListCellRenderer)var7).getListCellRendererComponent(this.list, "a", -1, false, false);
         var5.put(BASELINE_COMPONENT_KEY, var6);
      }

      var6.setFont(this.list.getFont());
      if (var4 == -1) {
         var4 = var6.getPreferredSize().height;
      }

      return var6.getBaseline(Integer.MAX_VALUE, var4) + this.list.getInsets().top;
   }

   public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent var1) {
      super.getBaselineResizeBehavior(var1);
      return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
   }

   public Dimension getPreferredSize(JComponent var1) {
      this.maybeUpdateLayoutState();
      int var2 = this.list.getModel().getSize() - 1;
      if (var2 < 0) {
         return new Dimension(0, 0);
      } else {
         Insets var3 = this.list.getInsets();
         int var4 = this.cellWidth * this.columnCount + var3.left + var3.right;
         int var5;
         if (this.layoutOrientation != 0) {
            var5 = this.preferredHeight;
         } else {
            Rectangle var6 = this.getCellBounds(this.list, var2);
            if (var6 != null) {
               var5 = var6.y + var6.height + var3.bottom;
            } else {
               var5 = 0;
            }
         }

         return new Dimension(var4, var5);
      }
   }

   protected void selectPreviousIndex() {
      int var1 = this.list.getSelectedIndex();
      if (var1 > 0) {
         --var1;
         this.list.setSelectedIndex(var1);
         this.list.ensureIndexIsVisible(var1);
      }

   }

   protected void selectNextIndex() {
      int var1 = this.list.getSelectedIndex();
      if (var1 + 1 < this.list.getModel().getSize()) {
         ++var1;
         this.list.setSelectedIndex(var1);
         this.list.ensureIndexIsVisible(var1);
      }

   }

   protected void installKeyboardActions() {
      InputMap var1 = this.getInputMap(0);
      SwingUtilities.replaceUIInputMap(this.list, 0, var1);
      LazyActionMap.installLazyActionMap(this.list, BasicListUI.class, "List.actionMap");
   }

   InputMap getInputMap(int var1) {
      if (var1 == 0) {
         InputMap var2 = (InputMap)DefaultLookup.get(this.list, this, "List.focusInputMap");
         InputMap var3;
         if (!this.isLeftToRight && (var3 = (InputMap)DefaultLookup.get(this.list, this, "List.focusInputMap.RightToLeft")) != null) {
            var3.setParent(var2);
            return var3;
         } else {
            return var2;
         }
      } else {
         return null;
      }
   }

   protected void uninstallKeyboardActions() {
      SwingUtilities.replaceUIActionMap(this.list, (ActionMap)null);
      SwingUtilities.replaceUIInputMap(this.list, 0, (InputMap)null);
   }

   protected void installListeners() {
      TransferHandler var1 = this.list.getTransferHandler();
      if (var1 == null || var1 instanceof UIResource) {
         this.list.setTransferHandler(defaultTransferHandler);
         if (this.list.getDropTarget() instanceof UIResource) {
            this.list.setDropTarget((DropTarget)null);
         }
      }

      this.focusListener = this.createFocusListener();
      this.mouseInputListener = this.createMouseInputListener();
      this.propertyChangeListener = this.createPropertyChangeListener();
      this.listSelectionListener = this.createListSelectionListener();
      this.listDataListener = this.createListDataListener();
      this.list.addFocusListener(this.focusListener);
      this.list.addMouseListener(this.mouseInputListener);
      this.list.addMouseMotionListener(this.mouseInputListener);
      this.list.addPropertyChangeListener(this.propertyChangeListener);
      this.list.addKeyListener(this.getHandler());
      ListModel var2 = this.list.getModel();
      if (var2 != null) {
         var2.addListDataListener(this.listDataListener);
      }

      ListSelectionModel var3 = this.list.getSelectionModel();
      if (var3 != null) {
         var3.addListSelectionListener(this.listSelectionListener);
      }

   }

   protected void uninstallListeners() {
      this.list.removeFocusListener(this.focusListener);
      this.list.removeMouseListener(this.mouseInputListener);
      this.list.removeMouseMotionListener(this.mouseInputListener);
      this.list.removePropertyChangeListener(this.propertyChangeListener);
      this.list.removeKeyListener(this.getHandler());
      ListModel var1 = this.list.getModel();
      if (var1 != null) {
         var1.removeListDataListener(this.listDataListener);
      }

      ListSelectionModel var2 = this.list.getSelectionModel();
      if (var2 != null) {
         var2.removeListSelectionListener(this.listSelectionListener);
      }

      this.focusListener = null;
      this.mouseInputListener = null;
      this.listSelectionListener = null;
      this.listDataListener = null;
      this.propertyChangeListener = null;
      this.handler = null;
   }

   protected void installDefaults() {
      this.list.setLayout((LayoutManager)null);
      LookAndFeel.installBorder(this.list, "List.border");
      LookAndFeel.installColorsAndFont(this.list, "List.background", "List.foreground", "List.font");
      LookAndFeel.installProperty(this.list, "opaque", Boolean.TRUE);
      if (this.list.getCellRenderer() == null) {
         this.list.setCellRenderer((ListCellRenderer)((ListCellRenderer)UIManager.get("List.cellRenderer")));
      }

      Color var1 = this.list.getSelectionBackground();
      if (var1 == null || var1 instanceof UIResource) {
         this.list.setSelectionBackground(UIManager.getColor("List.selectionBackground"));
      }

      Color var2 = this.list.getSelectionForeground();
      if (var2 == null || var2 instanceof UIResource) {
         this.list.setSelectionForeground(UIManager.getColor("List.selectionForeground"));
      }

      Long var3 = (Long)UIManager.get("List.timeFactor");
      this.timeFactor = var3 != null ? var3 : 1000L;
      this.updateIsFileList();
   }

   private void updateIsFileList() {
      boolean var1 = Boolean.TRUE.equals(this.list.getClientProperty("List.isFileList"));
      if (var1 != this.isFileList) {
         this.isFileList = var1;
         Font var2 = this.list.getFont();
         if (var2 == null || var2 instanceof UIResource) {
            Font var3 = UIManager.getFont(var1 ? "FileChooser.listFont" : "List.font");
            if (var3 != null && var3 != var2) {
               this.list.setFont(var3);
            }
         }
      }

   }

   protected void uninstallDefaults() {
      LookAndFeel.uninstallBorder(this.list);
      if (this.list.getFont() instanceof UIResource) {
         this.list.setFont((Font)null);
      }

      if (this.list.getForeground() instanceof UIResource) {
         this.list.setForeground((Color)null);
      }

      if (this.list.getBackground() instanceof UIResource) {
         this.list.setBackground((Color)null);
      }

      if (this.list.getSelectionBackground() instanceof UIResource) {
         this.list.setSelectionBackground((Color)null);
      }

      if (this.list.getSelectionForeground() instanceof UIResource) {
         this.list.setSelectionForeground((Color)null);
      }

      if (this.list.getCellRenderer() instanceof UIResource) {
         this.list.setCellRenderer((ListCellRenderer)null);
      }

      if (this.list.getTransferHandler() instanceof UIResource) {
         this.list.setTransferHandler((TransferHandler)null);
      }

   }

   public void installUI(JComponent var1) {
      this.list = (JList)var1;
      this.layoutOrientation = this.list.getLayoutOrientation();
      this.rendererPane = new CellRendererPane();
      this.list.add(this.rendererPane);
      this.columnCount = 1;
      this.updateLayoutStateNeeded = 1;
      this.isLeftToRight = this.list.getComponentOrientation().isLeftToRight();
      this.installDefaults();
      this.installListeners();
      this.installKeyboardActions();
   }

   public void uninstallUI(JComponent var1) {
      this.uninstallListeners();
      this.uninstallDefaults();
      this.uninstallKeyboardActions();
      this.cellWidth = this.cellHeight = -1;
      this.cellHeights = null;
      this.listWidth = this.listHeight = -1;
      this.list.remove(this.rendererPane);
      this.rendererPane = null;
      this.list = null;
   }

   public static ComponentUI createUI(JComponent var0) {
      return new BasicListUI();
   }

   public int locationToIndex(JList var1, Point var2) {
      this.maybeUpdateLayoutState();
      return this.convertLocationToModel(var2.x, var2.y);
   }

   public Point indexToLocation(JList var1, int var2) {
      this.maybeUpdateLayoutState();
      Rectangle var3 = this.getCellBounds(var1, var2, var2);
      return var3 != null ? new Point(var3.x, var3.y) : null;
   }

   public Rectangle getCellBounds(JList var1, int var2, int var3) {
      this.maybeUpdateLayoutState();
      int var4 = Math.min(var2, var3);
      int var5 = Math.max(var2, var3);
      if (var4 >= var1.getModel().getSize()) {
         return null;
      } else {
         Rectangle var6 = this.getCellBounds(var1, var4);
         if (var6 == null) {
            return null;
         } else if (var4 == var5) {
            return var6;
         } else {
            Rectangle var7 = this.getCellBounds(var1, var5);
            if (var7 != null) {
               if (this.layoutOrientation == 2) {
                  int var8 = this.convertModelToRow(var4);
                  int var9 = this.convertModelToRow(var5);
                  if (var8 != var9) {
                     var6.x = 0;
                     var6.width = var1.getWidth();
                  }
               } else if (var6.x != var7.x) {
                  var6.y = 0;
                  var6.height = var1.getHeight();
               }

               var6.add(var7);
            }

            return var6;
         }
      }
   }

   private Rectangle getCellBounds(JList var1, int var2) {
      this.maybeUpdateLayoutState();
      int var3 = this.convertModelToRow(var2);
      int var4 = this.convertModelToColumn(var2);
      if (var3 != -1 && var4 != -1) {
         Insets var5 = var1.getInsets();
         int var7 = this.cellWidth;
         int var8 = var5.top;
         int var6;
         int var9;
         switch(this.layoutOrientation) {
         case 1:
         case 2:
            if (this.isLeftToRight) {
               var6 = var5.left + var4 * this.cellWidth;
            } else {
               var6 = var1.getWidth() - var5.right - (var4 + 1) * this.cellWidth;
            }

            var8 += this.cellHeight * var3;
            var9 = this.cellHeight;
            break;
         default:
            var6 = var5.left;
            if (this.cellHeights == null) {
               var8 += this.cellHeight * var3;
            } else if (var3 >= this.cellHeights.length) {
               var8 = 0;
            } else {
               for(int var10 = 0; var10 < var3; ++var10) {
                  var8 += this.cellHeights[var10];
               }
            }

            var7 = var1.getWidth() - (var5.left + var5.right);
            var9 = this.getRowHeight(var2);
         }

         return new Rectangle(var6, var8, var7, var9);
      } else {
         return null;
      }
   }

   protected int getRowHeight(int var1) {
      return this.getHeight(0, var1);
   }

   protected int convertYToRow(int var1) {
      return this.convertLocationToRow(0, var1, false);
   }

   protected int convertRowToY(int var1) {
      if (var1 < this.getRowCount(0) && var1 >= 0) {
         Rectangle var2 = this.getCellBounds(this.list, var1, var1);
         return var2.y;
      } else {
         return -1;
      }
   }

   private int getHeight(int var1, int var2) {
      if (var1 >= 0 && var1 <= this.columnCount && var2 >= 0) {
         if (this.layoutOrientation != 0) {
            return this.cellHeight;
         } else if (var2 >= this.list.getModel().getSize()) {
            return -1;
         } else {
            return this.cellHeights == null ? this.cellHeight : (var2 < this.cellHeights.length ? this.cellHeights[var2] : -1);
         }
      } else {
         return -1;
      }
   }

   private int convertLocationToRow(int var1, int var2, boolean var3) {
      int var4 = this.list.getModel().getSize();
      if (var4 <= 0) {
         return -1;
      } else {
         Insets var5 = this.list.getInsets();
         int var6;
         if (this.cellHeights == null) {
            var6 = this.cellHeight == 0 ? 0 : (var2 - var5.top) / this.cellHeight;
            if (var3) {
               if (var6 < 0) {
                  var6 = 0;
               } else if (var6 >= var4) {
                  var6 = var4 - 1;
               }
            }

            return var6;
         } else if (var4 > this.cellHeights.length) {
            return -1;
         } else {
            var6 = var5.top;
            int var7 = 0;
            if (var3 && var2 < var6) {
               return 0;
            } else {
               int var8;
               for(var8 = 0; var8 < var4; ++var8) {
                  if (var2 >= var6 && var2 < var6 + this.cellHeights[var8]) {
                     return var7;
                  }

                  var6 += this.cellHeights[var8];
                  ++var7;
               }

               return var8 - 1;
            }
         }
      }
   }

   private int convertLocationToRowInColumn(int var1, int var2) {
      int var3 = 0;
      if (this.layoutOrientation != 0) {
         if (this.isLeftToRight) {
            var3 = var2 * this.cellWidth;
         } else {
            var3 = this.list.getWidth() - (var2 + 1) * this.cellWidth - this.list.getInsets().right;
         }
      }

      return this.convertLocationToRow(var3, var1, true);
   }

   private int convertLocationToModel(int var1, int var2) {
      int var3 = this.convertLocationToRow(var1, var2, true);
      int var4 = this.convertLocationToColumn(var1, var2);
      return var3 >= 0 && var4 >= 0 ? this.getModelIndex(var4, var3) : -1;
   }

   private int getRowCount(int var1) {
      if (var1 >= 0 && var1 < this.columnCount) {
         if (this.layoutOrientation != 0 && (var1 != 0 || this.columnCount != 1)) {
            if (var1 >= this.columnCount) {
               return -1;
            } else if (this.layoutOrientation == 1) {
               return var1 < this.columnCount - 1 ? this.rowsPerColumn : this.list.getModel().getSize() - (this.columnCount - 1) * this.rowsPerColumn;
            } else {
               int var2 = this.columnCount - (this.columnCount * this.rowsPerColumn - this.list.getModel().getSize());
               return var1 >= var2 ? Math.max(0, this.rowsPerColumn - 1) : this.rowsPerColumn;
            }
         } else {
            return this.list.getModel().getSize();
         }
      } else {
         return -1;
      }
   }

   private int getModelIndex(int var1, int var2) {
      switch(this.layoutOrientation) {
      case 1:
         return Math.min(this.list.getModel().getSize() - 1, this.rowsPerColumn * var1 + Math.min(var2, this.rowsPerColumn - 1));
      case 2:
         return Math.min(this.list.getModel().getSize() - 1, var2 * this.columnCount + var1);
      default:
         return var2;
      }
   }

   private int convertLocationToColumn(int var1, int var2) {
      if (this.cellWidth > 0) {
         if (this.layoutOrientation == 0) {
            return 0;
         } else {
            Insets var3 = this.list.getInsets();
            int var4;
            if (this.isLeftToRight) {
               var4 = (var1 - var3.left) / this.cellWidth;
            } else {
               var4 = (this.list.getWidth() - var1 - var3.right - 1) / this.cellWidth;
            }

            if (var4 < 0) {
               return 0;
            } else {
               return var4 >= this.columnCount ? this.columnCount - 1 : var4;
            }
         }
      } else {
         return 0;
      }
   }

   private int convertModelToRow(int var1) {
      int var2 = this.list.getModel().getSize();
      if (var1 >= 0 && var1 < var2) {
         if (this.layoutOrientation != 0 && this.columnCount > 1 && this.rowsPerColumn > 0) {
            return this.layoutOrientation == 1 ? var1 % this.rowsPerColumn : var1 / this.columnCount;
         } else {
            return var1;
         }
      } else {
         return -1;
      }
   }

   private int convertModelToColumn(int var1) {
      int var2 = this.list.getModel().getSize();
      if (var1 >= 0 && var1 < var2) {
         if (this.layoutOrientation != 0 && this.rowsPerColumn > 0 && this.columnCount > 1) {
            return this.layoutOrientation == 1 ? var1 / this.rowsPerColumn : var1 % this.columnCount;
         } else {
            return 0;
         }
      } else {
         return -1;
      }
   }

   protected void maybeUpdateLayoutState() {
      if (this.updateLayoutStateNeeded != 0) {
         this.updateLayoutState();
         this.updateLayoutStateNeeded = 0;
      }

   }

   protected void updateLayoutState() {
      int var1 = this.list.getFixedCellHeight();
      int var2 = this.list.getFixedCellWidth();
      this.cellWidth = var2 != -1 ? var2 : -1;
      if (var1 != -1) {
         this.cellHeight = var1;
         this.cellHeights = null;
      } else {
         this.cellHeight = -1;
         this.cellHeights = new int[this.list.getModel().getSize()];
      }

      if (var2 == -1 || var1 == -1) {
         ListModel var3 = this.list.getModel();
         int var4 = var3.getSize();
         ListCellRenderer var5 = this.list.getCellRenderer();
         int var6;
         if (var5 != null) {
            for(var6 = 0; var6 < var4; ++var6) {
               Object var7 = var3.getElementAt(var6);
               Component var8 = var5.getListCellRendererComponent(this.list, var7, var6, false, false);
               this.rendererPane.add(var8);
               Dimension var9 = var8.getPreferredSize();
               if (var2 == -1) {
                  this.cellWidth = Math.max(var9.width, this.cellWidth);
               }

               if (var1 == -1) {
                  this.cellHeights[var6] = var9.height;
               }
            }
         } else {
            if (this.cellWidth == -1) {
               this.cellWidth = 0;
            }

            if (this.cellHeights == null) {
               this.cellHeights = new int[var4];
            }

            for(var6 = 0; var6 < var4; ++var6) {
               this.cellHeights[var6] = 0;
            }
         }
      }

      this.columnCount = 1;
      if (this.layoutOrientation != 0) {
         this.updateHorizontalLayoutState(var2, var1);
      }

   }

   private void updateHorizontalLayoutState(int var1, int var2) {
      int var3 = this.list.getVisibleRowCount();
      int var4 = this.list.getModel().getSize();
      Insets var5 = this.list.getInsets();
      this.listHeight = this.list.getHeight();
      this.listWidth = this.list.getWidth();
      if (var4 == 0) {
         this.rowsPerColumn = this.columnCount = 0;
         this.preferredHeight = var5.top + var5.bottom;
      } else {
         int var6;
         if (var2 != -1) {
            var6 = var2;
         } else {
            int var7 = 0;
            if (this.cellHeights.length > 0) {
               var7 = this.cellHeights[this.cellHeights.length - 1];

               for(int var8 = this.cellHeights.length - 2; var8 >= 0; --var8) {
                  var7 = Math.max(var7, this.cellHeights[var8]);
               }
            }

            var6 = this.cellHeight = var7;
            this.cellHeights = null;
         }

         this.rowsPerColumn = var4;
         if (var3 > 0) {
            this.rowsPerColumn = var3;
            this.columnCount = Math.max(1, var4 / this.rowsPerColumn);
            if (var4 > 0 && var4 > this.rowsPerColumn && var4 % this.rowsPerColumn != 0) {
               ++this.columnCount;
            }

            if (this.layoutOrientation == 2) {
               this.rowsPerColumn = var4 / this.columnCount;
               if (var4 % this.columnCount > 0) {
                  ++this.rowsPerColumn;
               }
            }
         } else if (this.layoutOrientation == 1 && var6 != 0) {
            this.rowsPerColumn = Math.max(1, (this.listHeight - var5.top - var5.bottom) / var6);
            this.columnCount = Math.max(1, var4 / this.rowsPerColumn);
            if (var4 > 0 && var4 > this.rowsPerColumn && var4 % this.rowsPerColumn != 0) {
               ++this.columnCount;
            }
         } else if (this.layoutOrientation == 2 && this.cellWidth > 0 && this.listWidth > 0) {
            this.columnCount = Math.max(1, (this.listWidth - var5.left - var5.right) / this.cellWidth);
            this.rowsPerColumn = var4 / this.columnCount;
            if (var4 % this.columnCount > 0) {
               ++this.rowsPerColumn;
            }
         }

         this.preferredHeight = this.rowsPerColumn * this.cellHeight + var5.top + var5.bottom;
      }
   }

   private BasicListUI.Handler getHandler() {
      if (this.handler == null) {
         this.handler = new BasicListUI.Handler();
      }

      return this.handler;
   }

   protected MouseInputListener createMouseInputListener() {
      return this.getHandler();
   }

   protected FocusListener createFocusListener() {
      return this.getHandler();
   }

   protected ListSelectionListener createListSelectionListener() {
      return this.getHandler();
   }

   private void redrawList() {
      this.list.revalidate();
      this.list.repaint();
   }

   protected ListDataListener createListDataListener() {
      return this.getHandler();
   }

   protected PropertyChangeListener createPropertyChangeListener() {
      return this.getHandler();
   }

   private static int adjustIndex(int var0, JList var1) {
      return var0 < var1.getModel().getSize() ? var0 : -1;
   }

   static class ListTransferHandler extends TransferHandler implements UIResource {
      protected Transferable createTransferable(JComponent var1) {
         if (var1 instanceof JList) {
            JList var2 = (JList)var1;
            Object[] var3 = var2.getSelectedValues();
            if (var3 != null && var3.length != 0) {
               StringBuffer var4 = new StringBuffer();
               StringBuffer var5 = new StringBuffer();
               var5.append("<html>\n<body>\n<ul>\n");

               for(int var6 = 0; var6 < var3.length; ++var6) {
                  Object var7 = var3[var6];
                  String var8 = var7 == null ? "" : var7.toString();
                  var4.append(var8 + "\n");
                  var5.append("  <li>" + var8 + "\n");
               }

               var4.deleteCharAt(var4.length() - 1);
               var5.append("</ul>\n</body>\n</html>");
               return new BasicTransferable(var4.toString(), var5.toString());
            } else {
               return null;
            }
         } else {
            return null;
         }
      }

      public int getSourceActions(JComponent var1) {
         return 1;
      }
   }

   private class Handler implements FocusListener, KeyListener, ListDataListener, ListSelectionListener, MouseInputListener, PropertyChangeListener, DragRecognitionSupport.BeforeDrag {
      private String prefix;
      private String typedString;
      private long lastTime;
      private boolean dragPressDidSelection;

      private Handler() {
         this.prefix = "";
         this.typedString = "";
         this.lastTime = 0L;
      }

      public void keyTyped(KeyEvent var1) {
         JList var2 = (JList)var1.getSource();
         ListModel var3 = var2.getModel();
         if (var3.getSize() != 0 && !var1.isAltDown() && !BasicGraphicsUtils.isMenuShortcutKeyDown(var1) && !this.isNavigationKey(var1)) {
            boolean var4 = true;
            char var5 = var1.getKeyChar();
            long var6 = var1.getWhen();
            int var8 = BasicListUI.adjustIndex(var2.getLeadSelectionIndex(), BasicListUI.this.list);
            if (var6 - this.lastTime < BasicListUI.this.timeFactor) {
               this.typedString = this.typedString + var5;
               if (this.prefix.length() == 1 && var5 == this.prefix.charAt(0)) {
                  ++var8;
               } else {
                  this.prefix = this.typedString;
               }
            } else {
               ++var8;
               this.typedString = "" + var5;
               this.prefix = this.typedString;
            }

            this.lastTime = var6;
            if (var8 < 0 || var8 >= var3.getSize()) {
               var4 = false;
               var8 = 0;
            }

            int var9 = var2.getNextMatch(this.prefix, var8, Position.Bias.Forward);
            if (var9 >= 0) {
               var2.setSelectedIndex(var9);
               var2.ensureIndexIsVisible(var9);
            } else if (var4) {
               var9 = var2.getNextMatch(this.prefix, 0, Position.Bias.Forward);
               if (var9 >= 0) {
                  var2.setSelectedIndex(var9);
                  var2.ensureIndexIsVisible(var9);
               }
            }

         }
      }

      public void keyPressed(KeyEvent var1) {
         if (this.isNavigationKey(var1)) {
            this.prefix = "";
            this.typedString = "";
            this.lastTime = 0L;
         }

      }

      public void keyReleased(KeyEvent var1) {
      }

      private boolean isNavigationKey(KeyEvent var1) {
         InputMap var2 = BasicListUI.this.list.getInputMap(1);
         KeyStroke var3 = KeyStroke.getKeyStrokeForEvent(var1);
         return var2 != null && var2.get(var3) != null;
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         BasicListUI var10000;
         if (var2 == "model") {
            ListModel var3 = (ListModel)var1.getOldValue();
            ListModel var4 = (ListModel)var1.getNewValue();
            if (var3 != null) {
               var3.removeListDataListener(BasicListUI.this.listDataListener);
            }

            if (var4 != null) {
               var4.addListDataListener(BasicListUI.this.listDataListener);
            }

            var10000 = BasicListUI.this;
            var10000.updateLayoutStateNeeded |= 1;
            BasicListUI.this.redrawList();
         } else if (var2 == "selectionModel") {
            ListSelectionModel var5 = (ListSelectionModel)var1.getOldValue();
            ListSelectionModel var8 = (ListSelectionModel)var1.getNewValue();
            if (var5 != null) {
               var5.removeListSelectionListener(BasicListUI.this.listSelectionListener);
            }

            if (var8 != null) {
               var8.addListSelectionListener(BasicListUI.this.listSelectionListener);
            }

            var10000 = BasicListUI.this;
            var10000.updateLayoutStateNeeded |= 1;
            BasicListUI.this.redrawList();
         } else if (var2 == "cellRenderer") {
            var10000 = BasicListUI.this;
            var10000.updateLayoutStateNeeded |= 64;
            BasicListUI.this.redrawList();
         } else if (var2 == "font") {
            var10000 = BasicListUI.this;
            var10000.updateLayoutStateNeeded |= 4;
            BasicListUI.this.redrawList();
         } else if (var2 == "prototypeCellValue") {
            var10000 = BasicListUI.this;
            var10000.updateLayoutStateNeeded |= 32;
            BasicListUI.this.redrawList();
         } else if (var2 == "fixedCellHeight") {
            var10000 = BasicListUI.this;
            var10000.updateLayoutStateNeeded |= 16;
            BasicListUI.this.redrawList();
         } else if (var2 == "fixedCellWidth") {
            var10000 = BasicListUI.this;
            var10000.updateLayoutStateNeeded |= 8;
            BasicListUI.this.redrawList();
         } else if (var2 == "selectionForeground") {
            BasicListUI.this.list.repaint();
         } else if (var2 == "selectionBackground") {
            BasicListUI.this.list.repaint();
         } else if ("layoutOrientation" == var2) {
            var10000 = BasicListUI.this;
            var10000.updateLayoutStateNeeded |= 128;
            BasicListUI.this.layoutOrientation = BasicListUI.this.list.getLayoutOrientation();
            BasicListUI.this.redrawList();
         } else if ("visibleRowCount" == var2) {
            if (BasicListUI.this.layoutOrientation != 0) {
               var10000 = BasicListUI.this;
               var10000.updateLayoutStateNeeded |= 128;
               BasicListUI.this.redrawList();
            }
         } else if ("componentOrientation" == var2) {
            BasicListUI.this.isLeftToRight = BasicListUI.this.list.getComponentOrientation().isLeftToRight();
            var10000 = BasicListUI.this;
            var10000.updateLayoutStateNeeded |= 1024;
            BasicListUI.this.redrawList();
            InputMap var6 = BasicListUI.this.getInputMap(0);
            SwingUtilities.replaceUIInputMap(BasicListUI.this.list, 0, var6);
         } else if ("List.isFileList" == var2) {
            BasicListUI.this.updateIsFileList();
            BasicListUI.this.redrawList();
         } else if ("dropLocation" == var2) {
            JList.DropLocation var7 = (JList.DropLocation)var1.getOldValue();
            this.repaintDropLocation(var7);
            this.repaintDropLocation(BasicListUI.this.list.getDropLocation());
         }

      }

      private void repaintDropLocation(JList.DropLocation var1) {
         if (var1 != null) {
            Rectangle var2;
            if (var1.isInsert()) {
               var2 = BasicListUI.this.getDropLineRect(var1);
            } else {
               var2 = BasicListUI.this.getCellBounds(BasicListUI.this.list, var1.getIndex());
            }

            if (var2 != null) {
               BasicListUI.this.list.repaint(var2);
            }

         }
      }

      public void intervalAdded(ListDataEvent var1) {
         BasicListUI.this.updateLayoutStateNeeded = 1;
         int var2 = Math.min(var1.getIndex0(), var1.getIndex1());
         int var3 = Math.max(var1.getIndex0(), var1.getIndex1());
         ListSelectionModel var4 = BasicListUI.this.list.getSelectionModel();
         if (var4 != null) {
            var4.insertIndexInterval(var2, var3 - var2 + 1, true);
         }

         BasicListUI.this.redrawList();
      }

      public void intervalRemoved(ListDataEvent var1) {
         BasicListUI.this.updateLayoutStateNeeded = 1;
         ListSelectionModel var2 = BasicListUI.this.list.getSelectionModel();
         if (var2 != null) {
            var2.removeIndexInterval(var1.getIndex0(), var1.getIndex1());
         }

         BasicListUI.this.redrawList();
      }

      public void contentsChanged(ListDataEvent var1) {
         BasicListUI.this.updateLayoutStateNeeded = 1;
         BasicListUI.this.redrawList();
      }

      public void valueChanged(ListSelectionEvent var1) {
         BasicListUI.this.maybeUpdateLayoutState();
         int var2 = BasicListUI.this.list.getModel().getSize();
         int var3 = Math.min(var2 - 1, Math.max(var1.getFirstIndex(), 0));
         int var4 = Math.min(var2 - 1, Math.max(var1.getLastIndex(), 0));
         Rectangle var5 = BasicListUI.this.getCellBounds(BasicListUI.this.list, var3, var4);
         if (var5 != null) {
            BasicListUI.this.list.repaint(var5.x, var5.y, var5.width, var5.height);
         }

      }

      public void mouseClicked(MouseEvent var1) {
      }

      public void mouseEntered(MouseEvent var1) {
      }

      public void mouseExited(MouseEvent var1) {
      }

      public void mousePressed(MouseEvent var1) {
         if (!SwingUtilities2.shouldIgnore(var1, BasicListUI.this.list)) {
            boolean var2 = BasicListUI.this.list.getDragEnabled();
            boolean var3 = true;
            if (var2) {
               int var4 = SwingUtilities2.loc2IndexFileList(BasicListUI.this.list, var1.getPoint());
               if (var4 != -1 && DragRecognitionSupport.mousePressed(var1)) {
                  this.dragPressDidSelection = false;
                  if (BasicGraphicsUtils.isMenuShortcutKeyDown(var1)) {
                     return;
                  }

                  if (!var1.isShiftDown() && BasicListUI.this.list.isSelectedIndex(var4)) {
                     BasicListUI.this.list.addSelectionInterval(var4, var4);
                     return;
                  }

                  var3 = false;
                  this.dragPressDidSelection = true;
               }
            } else {
               BasicListUI.this.list.setValueIsAdjusting(true);
            }

            if (var3) {
               SwingUtilities2.adjustFocus(BasicListUI.this.list);
            }

            this.adjustSelection(var1);
         }
      }

      private void adjustSelection(MouseEvent var1) {
         int var2 = SwingUtilities2.loc2IndexFileList(BasicListUI.this.list, var1.getPoint());
         if (var2 < 0) {
            if (BasicListUI.this.isFileList && var1.getID() == 501 && (!var1.isShiftDown() || BasicListUI.this.list.getSelectionMode() == 0)) {
               BasicListUI.this.list.clearSelection();
            }
         } else {
            int var3 = BasicListUI.adjustIndex(BasicListUI.this.list.getAnchorSelectionIndex(), BasicListUI.this.list);
            boolean var4;
            if (var3 == -1) {
               var3 = 0;
               var4 = false;
            } else {
               var4 = BasicListUI.this.list.isSelectedIndex(var3);
            }

            if (BasicGraphicsUtils.isMenuShortcutKeyDown(var1)) {
               if (var1.isShiftDown()) {
                  if (var4) {
                     BasicListUI.this.list.addSelectionInterval(var3, var2);
                  } else {
                     BasicListUI.this.list.removeSelectionInterval(var3, var2);
                     if (BasicListUI.this.isFileList) {
                        BasicListUI.this.list.addSelectionInterval(var2, var2);
                        BasicListUI.this.list.getSelectionModel().setAnchorSelectionIndex(var3);
                     }
                  }
               } else if (BasicListUI.this.list.isSelectedIndex(var2)) {
                  BasicListUI.this.list.removeSelectionInterval(var2, var2);
               } else {
                  BasicListUI.this.list.addSelectionInterval(var2, var2);
               }
            } else if (var1.isShiftDown()) {
               BasicListUI.this.list.setSelectionInterval(var3, var2);
            } else {
               BasicListUI.this.list.setSelectionInterval(var2, var2);
            }
         }

      }

      public void dragStarting(MouseEvent var1) {
         if (BasicGraphicsUtils.isMenuShortcutKeyDown(var1)) {
            int var2 = SwingUtilities2.loc2IndexFileList(BasicListUI.this.list, var1.getPoint());
            BasicListUI.this.list.addSelectionInterval(var2, var2);
         }

      }

      public void mouseDragged(MouseEvent var1) {
         if (!SwingUtilities2.shouldIgnore(var1, BasicListUI.this.list)) {
            if (BasicListUI.this.list.getDragEnabled()) {
               DragRecognitionSupport.mouseDragged(var1, this);
            } else if (!var1.isShiftDown() && !BasicGraphicsUtils.isMenuShortcutKeyDown(var1)) {
               int var2 = BasicListUI.this.locationToIndex(BasicListUI.this.list, var1.getPoint());
               if (var2 != -1) {
                  if (BasicListUI.this.isFileList) {
                     return;
                  }

                  Rectangle var3 = BasicListUI.this.getCellBounds(BasicListUI.this.list, var2, var2);
                  if (var3 != null) {
                     BasicListUI.this.list.scrollRectToVisible(var3);
                     BasicListUI.this.list.setSelectionInterval(var2, var2);
                  }
               }

            }
         }
      }

      public void mouseMoved(MouseEvent var1) {
      }

      public void mouseReleased(MouseEvent var1) {
         if (!SwingUtilities2.shouldIgnore(var1, BasicListUI.this.list)) {
            if (BasicListUI.this.list.getDragEnabled()) {
               MouseEvent var2 = DragRecognitionSupport.mouseReleased(var1);
               if (var2 != null) {
                  SwingUtilities2.adjustFocus(BasicListUI.this.list);
                  if (!this.dragPressDidSelection) {
                     this.adjustSelection(var2);
                  }
               }
            } else {
               BasicListUI.this.list.setValueIsAdjusting(false);
            }

         }
      }

      protected void repaintCellFocus() {
         int var1 = BasicListUI.adjustIndex(BasicListUI.this.list.getLeadSelectionIndex(), BasicListUI.this.list);
         if (var1 != -1) {
            Rectangle var2 = BasicListUI.this.getCellBounds(BasicListUI.this.list, var1, var1);
            if (var2 != null) {
               BasicListUI.this.list.repaint(var2.x, var2.y, var2.width, var2.height);
            }
         }

      }

      public void focusGained(FocusEvent var1) {
         this.repaintCellFocus();
      }

      public void focusLost(FocusEvent var1) {
         this.repaintCellFocus();
      }

      // $FF: synthetic method
      Handler(Object var2) {
         this();
      }
   }

   private static class Actions extends UIAction {
      private static final String SELECT_PREVIOUS_COLUMN = "selectPreviousColumn";
      private static final String SELECT_PREVIOUS_COLUMN_EXTEND = "selectPreviousColumnExtendSelection";
      private static final String SELECT_PREVIOUS_COLUMN_CHANGE_LEAD = "selectPreviousColumnChangeLead";
      private static final String SELECT_NEXT_COLUMN = "selectNextColumn";
      private static final String SELECT_NEXT_COLUMN_EXTEND = "selectNextColumnExtendSelection";
      private static final String SELECT_NEXT_COLUMN_CHANGE_LEAD = "selectNextColumnChangeLead";
      private static final String SELECT_PREVIOUS_ROW = "selectPreviousRow";
      private static final String SELECT_PREVIOUS_ROW_EXTEND = "selectPreviousRowExtendSelection";
      private static final String SELECT_PREVIOUS_ROW_CHANGE_LEAD = "selectPreviousRowChangeLead";
      private static final String SELECT_NEXT_ROW = "selectNextRow";
      private static final String SELECT_NEXT_ROW_EXTEND = "selectNextRowExtendSelection";
      private static final String SELECT_NEXT_ROW_CHANGE_LEAD = "selectNextRowChangeLead";
      private static final String SELECT_FIRST_ROW = "selectFirstRow";
      private static final String SELECT_FIRST_ROW_EXTEND = "selectFirstRowExtendSelection";
      private static final String SELECT_FIRST_ROW_CHANGE_LEAD = "selectFirstRowChangeLead";
      private static final String SELECT_LAST_ROW = "selectLastRow";
      private static final String SELECT_LAST_ROW_EXTEND = "selectLastRowExtendSelection";
      private static final String SELECT_LAST_ROW_CHANGE_LEAD = "selectLastRowChangeLead";
      private static final String SCROLL_UP = "scrollUp";
      private static final String SCROLL_UP_EXTEND = "scrollUpExtendSelection";
      private static final String SCROLL_UP_CHANGE_LEAD = "scrollUpChangeLead";
      private static final String SCROLL_DOWN = "scrollDown";
      private static final String SCROLL_DOWN_EXTEND = "scrollDownExtendSelection";
      private static final String SCROLL_DOWN_CHANGE_LEAD = "scrollDownChangeLead";
      private static final String SELECT_ALL = "selectAll";
      private static final String CLEAR_SELECTION = "clearSelection";
      private static final String ADD_TO_SELECTION = "addToSelection";
      private static final String TOGGLE_AND_ANCHOR = "toggleAndAnchor";
      private static final String EXTEND_TO = "extendTo";
      private static final String MOVE_SELECTION_TO = "moveSelectionTo";

      Actions(String var1) {
         super(var1);
      }

      public void actionPerformed(ActionEvent var1) {
         String var2 = this.getName();
         JList var3 = (JList)var1.getSource();
         BasicListUI var4 = (BasicListUI)BasicLookAndFeel.getUIOfType(var3.getUI(), BasicListUI.class);
         if (var2 == "selectPreviousColumn") {
            this.changeSelection(var3, 1, this.getNextColumnIndex(var3, var4, -1), -1);
         } else if (var2 == "selectPreviousColumnExtendSelection") {
            this.changeSelection(var3, 2, this.getNextColumnIndex(var3, var4, -1), -1);
         } else if (var2 == "selectPreviousColumnChangeLead") {
            this.changeSelection(var3, 0, this.getNextColumnIndex(var3, var4, -1), -1);
         } else if (var2 == "selectNextColumn") {
            this.changeSelection(var3, 1, this.getNextColumnIndex(var3, var4, 1), 1);
         } else if (var2 == "selectNextColumnExtendSelection") {
            this.changeSelection(var3, 2, this.getNextColumnIndex(var3, var4, 1), 1);
         } else if (var2 == "selectNextColumnChangeLead") {
            this.changeSelection(var3, 0, this.getNextColumnIndex(var3, var4, 1), 1);
         } else if (var2 == "selectPreviousRow") {
            this.changeSelection(var3, 1, this.getNextIndex(var3, var4, -1), -1);
         } else if (var2 == "selectPreviousRowExtendSelection") {
            this.changeSelection(var3, 2, this.getNextIndex(var3, var4, -1), -1);
         } else if (var2 == "selectPreviousRowChangeLead") {
            this.changeSelection(var3, 0, this.getNextIndex(var3, var4, -1), -1);
         } else if (var2 == "selectNextRow") {
            this.changeSelection(var3, 1, this.getNextIndex(var3, var4, 1), 1);
         } else if (var2 == "selectNextRowExtendSelection") {
            this.changeSelection(var3, 2, this.getNextIndex(var3, var4, 1), 1);
         } else if (var2 == "selectNextRowChangeLead") {
            this.changeSelection(var3, 0, this.getNextIndex(var3, var4, 1), 1);
         } else if (var2 == "selectFirstRow") {
            this.changeSelection(var3, 1, 0, -1);
         } else if (var2 == "selectFirstRowExtendSelection") {
            this.changeSelection(var3, 2, 0, -1);
         } else if (var2 == "selectFirstRowChangeLead") {
            this.changeSelection(var3, 0, 0, -1);
         } else if (var2 == "selectLastRow") {
            this.changeSelection(var3, 1, var3.getModel().getSize() - 1, 1);
         } else if (var2 == "selectLastRowExtendSelection") {
            this.changeSelection(var3, 2, var3.getModel().getSize() - 1, 1);
         } else if (var2 == "selectLastRowChangeLead") {
            this.changeSelection(var3, 0, var3.getModel().getSize() - 1, 1);
         } else if (var2 == "scrollUp") {
            this.changeSelection(var3, 1, this.getNextPageIndex(var3, -1), -1);
         } else if (var2 == "scrollUpExtendSelection") {
            this.changeSelection(var3, 2, this.getNextPageIndex(var3, -1), -1);
         } else if (var2 == "scrollUpChangeLead") {
            this.changeSelection(var3, 0, this.getNextPageIndex(var3, -1), -1);
         } else if (var2 == "scrollDown") {
            this.changeSelection(var3, 1, this.getNextPageIndex(var3, 1), 1);
         } else if (var2 == "scrollDownExtendSelection") {
            this.changeSelection(var3, 2, this.getNextPageIndex(var3, 1), 1);
         } else if (var2 == "scrollDownChangeLead") {
            this.changeSelection(var3, 0, this.getNextPageIndex(var3, 1), 1);
         } else if (var2 == "selectAll") {
            this.selectAll(var3);
         } else if (var2 == "clearSelection") {
            this.clearSelection(var3);
         } else {
            int var5;
            if (var2 == "addToSelection") {
               var5 = BasicListUI.adjustIndex(var3.getSelectionModel().getLeadSelectionIndex(), var3);
               if (!var3.isSelectedIndex(var5)) {
                  int var6 = var3.getSelectionModel().getAnchorSelectionIndex();
                  var3.setValueIsAdjusting(true);
                  var3.addSelectionInterval(var5, var5);
                  var3.getSelectionModel().setAnchorSelectionIndex(var6);
                  var3.setValueIsAdjusting(false);
               }
            } else if (var2 == "toggleAndAnchor") {
               var5 = BasicListUI.adjustIndex(var3.getSelectionModel().getLeadSelectionIndex(), var3);
               if (var3.isSelectedIndex(var5)) {
                  var3.removeSelectionInterval(var5, var5);
               } else {
                  var3.addSelectionInterval(var5, var5);
               }
            } else if (var2 == "extendTo") {
               this.changeSelection(var3, 2, BasicListUI.adjustIndex(var3.getSelectionModel().getLeadSelectionIndex(), var3), 0);
            } else if (var2 == "moveSelectionTo") {
               this.changeSelection(var3, 1, BasicListUI.adjustIndex(var3.getSelectionModel().getLeadSelectionIndex(), var3), 0);
            }
         }

      }

      public boolean isEnabled(Object var1) {
         String var2 = this.getName();
         if (var2 != "selectPreviousColumnChangeLead" && var2 != "selectNextColumnChangeLead" && var2 != "selectPreviousRowChangeLead" && var2 != "selectNextRowChangeLead" && var2 != "selectFirstRowChangeLead" && var2 != "selectLastRowChangeLead" && var2 != "scrollUpChangeLead" && var2 != "scrollDownChangeLead") {
            return true;
         } else {
            return var1 != null && ((JList)var1).getSelectionModel() instanceof DefaultListSelectionModel;
         }
      }

      private void clearSelection(JList var1) {
         var1.clearSelection();
      }

      private void selectAll(JList var1) {
         int var2 = var1.getModel().getSize();
         if (var2 > 0) {
            ListSelectionModel var3 = var1.getSelectionModel();
            int var4 = BasicListUI.adjustIndex(var3.getLeadSelectionIndex(), var1);
            int var5;
            if (var3.getSelectionMode() == 0) {
               if (var4 == -1) {
                  var5 = BasicListUI.adjustIndex(var1.getMinSelectionIndex(), var1);
                  var4 = var5 == -1 ? 0 : var5;
               }

               var1.setSelectionInterval(var4, var4);
               var1.ensureIndexIsVisible(var4);
            } else {
               var1.setValueIsAdjusting(true);
               var5 = BasicListUI.adjustIndex(var3.getAnchorSelectionIndex(), var1);
               var1.setSelectionInterval(0, var2 - 1);
               SwingUtilities2.setLeadAnchorWithoutSelection(var3, var5, var4);
               var1.setValueIsAdjusting(false);
            }
         }

      }

      private int getNextPageIndex(JList var1, int var2) {
         if (var1.getModel().getSize() == 0) {
            return -1;
         } else {
            boolean var3 = true;
            Rectangle var4 = var1.getVisibleRect();
            ListSelectionModel var5 = var1.getSelectionModel();
            int var6 = BasicListUI.adjustIndex(var5.getLeadSelectionIndex(), var1);
            Rectangle var7 = var6 == -1 ? new Rectangle() : var1.getCellBounds(var6, var6);
            Point var8;
            Rectangle var9;
            int var10;
            if (var1.getLayoutOrientation() == 1 && var1.getVisibleRowCount() <= 0) {
               if (!var1.getComponentOrientation().isLeftToRight()) {
                  var2 = -var2;
               }

               if (var2 < 0) {
                  var4.x = var7.x + var7.width - var4.width;
                  var8 = new Point(var4.x - 1, var7.y);
                  var10 = var1.locationToIndex(var8);
                  var9 = var1.getCellBounds(var10, var10);
                  if (var4.intersects(var9)) {
                     var8.x = var9.x - 1;
                     var10 = var1.locationToIndex(var8);
                     var9 = var1.getCellBounds(var10, var10);
                  }

                  if (var9.y != var7.y) {
                     var8.x = var9.x + var9.width;
                     var10 = var1.locationToIndex(var8);
                  }
               } else {
                  var4.x = var7.x;
                  var8 = new Point(var4.x + var4.width, var7.y);
                  var10 = var1.locationToIndex(var8);
                  var9 = var1.getCellBounds(var10, var10);
                  if (var4.intersects(var9)) {
                     var8.x = var9.x + var9.width;
                     var10 = var1.locationToIndex(var8);
                     var9 = var1.getCellBounds(var10, var10);
                  }

                  if (var9.y != var7.y) {
                     var8.x = var9.x - 1;
                     var10 = var1.locationToIndex(var8);
                  }
               }
            } else if (var2 < 0) {
               var8 = new Point(var7.x, var4.y);
               var10 = var1.locationToIndex(var8);
               if (var6 <= var10) {
                  var4.y = var7.y + var7.height - var4.height;
                  var8.y = var4.y;
                  var10 = var1.locationToIndex(var8);
                  var9 = var1.getCellBounds(var10, var10);
                  if (var9.y < var4.y) {
                     var8.y = var9.y + var9.height;
                     var10 = var1.locationToIndex(var8);
                     var9 = var1.getCellBounds(var10, var10);
                  }

                  if (var9.y >= var7.y) {
                     var8.y = var7.y - 1;
                     var10 = var1.locationToIndex(var8);
                  }
               }
            } else {
               var8 = new Point(var7.x, var4.y + var4.height - 1);
               var10 = var1.locationToIndex(var8);
               var9 = var1.getCellBounds(var10, var10);
               if (var9.y + var9.height > var4.y + var4.height) {
                  var8.y = var9.y - 1;
                  var10 = var1.locationToIndex(var8);
                  var1.getCellBounds(var10, var10);
                  var10 = Math.max(var10, var6);
               }

               if (var6 >= var10) {
                  var4.y = var7.y;
                  var8.y = var4.y + var4.height - 1;
                  var10 = var1.locationToIndex(var8);
                  var9 = var1.getCellBounds(var10, var10);
                  if (var9.y + var9.height > var4.y + var4.height) {
                     var8.y = var9.y - 1;
                     var10 = var1.locationToIndex(var8);
                     var9 = var1.getCellBounds(var10, var10);
                  }

                  if (var9.y <= var7.y) {
                     var8.y = var7.y + var7.height;
                     var10 = var1.locationToIndex(var8);
                  }
               }
            }

            return var10;
         }
      }

      private void changeSelection(JList var1, int var2, int var3, int var4) {
         if (var3 >= 0 && var3 < var1.getModel().getSize()) {
            ListSelectionModel var5 = var1.getSelectionModel();
            if (var2 == 0 && var1.getSelectionMode() != 2) {
               var2 = 1;
            }

            this.adjustScrollPositionIfNecessary(var1, var3, var4);
            if (var2 == 2) {
               int var6 = BasicListUI.adjustIndex(var5.getAnchorSelectionIndex(), var1);
               if (var6 == -1) {
                  var6 = 0;
               }

               var1.setSelectionInterval(var6, var3);
            } else if (var2 == 1) {
               var1.setSelectedIndex(var3);
            } else {
               ((DefaultListSelectionModel)var5).moveLeadSelectionIndex(var3);
            }
         }

      }

      private void adjustScrollPositionIfNecessary(JList var1, int var2, int var3) {
         if (var3 != 0) {
            Rectangle var4 = var1.getCellBounds(var2, var2);
            Rectangle var5 = var1.getVisibleRect();
            if (var4 != null && !var5.contains(var4)) {
               int var6;
               int var7;
               Rectangle var8;
               if (var1.getLayoutOrientation() == 1 && var1.getVisibleRowCount() <= 0) {
                  if (var1.getComponentOrientation().isLeftToRight()) {
                     if (var3 > 0) {
                        var6 = Math.max(0, var4.x + var4.width - var5.width);
                        var7 = var1.locationToIndex(new Point(var6, var4.y));
                        var8 = var1.getCellBounds(var7, var7);
                        if (var8.x < var6 && var8.x < var4.x) {
                           var8.x += var8.width;
                           var7 = var1.locationToIndex(var8.getLocation());
                           var8 = var1.getCellBounds(var7, var7);
                        }

                        var4 = var8;
                     }

                     var4.width = var5.width;
                  } else if (var3 > 0) {
                     var6 = var4.x + var5.width;
                     var7 = var1.locationToIndex(new Point(var6, var4.y));
                     var8 = var1.getCellBounds(var7, var7);
                     if (var8.x + var8.width > var6 && var8.x > var4.x) {
                        var8.width = 0;
                     }

                     var4.x = Math.max(0, var8.x + var8.width - var5.width);
                     var4.width = var5.width;
                  } else {
                     var4.x += Math.max(0, var4.width - var5.width);
                     var4.width = Math.min(var4.width, var5.width);
                  }
               } else if (var3 <= 0 || var4.y >= var5.y && var4.y + var4.height <= var5.y + var5.height) {
                  var4.height = Math.min(var4.height, var5.height);
               } else {
                  var6 = Math.max(0, var4.y + var4.height - var5.height);
                  var7 = var1.locationToIndex(new Point(var4.x, var6));
                  var8 = var1.getCellBounds(var7, var7);
                  if (var8.y < var6 && var8.y < var4.y) {
                     var8.y += var8.height;
                     var7 = var1.locationToIndex(var8.getLocation());
                     var8 = var1.getCellBounds(var7, var7);
                  }

                  var4 = var8;
                  var8.height = var5.height;
               }

               var1.scrollRectToVisible(var4);
            }

         }
      }

      private int getNextColumnIndex(JList var1, BasicListUI var2, int var3) {
         if (var1.getLayoutOrientation() != 0) {
            int var4 = BasicListUI.adjustIndex(var1.getLeadSelectionIndex(), var1);
            int var5 = var1.getModel().getSize();
            if (var4 == -1) {
               return 0;
            } else if (var5 == 1) {
               return 0;
            } else if (var2 != null && var2.columnCount > 1) {
               int var6 = var2.convertModelToColumn(var4);
               int var7 = var2.convertModelToRow(var4);
               var6 += var3;
               if (var6 < var2.columnCount && var6 >= 0) {
                  int var8 = var2.getRowCount(var6);
                  return var7 >= var8 ? -1 : var2.getModelIndex(var6, var7);
               } else {
                  return -1;
               }
            } else {
               return -1;
            }
         } else {
            return -1;
         }
      }

      private int getNextIndex(JList var1, BasicListUI var2, int var3) {
         int var4 = BasicListUI.adjustIndex(var1.getLeadSelectionIndex(), var1);
         int var5 = var1.getModel().getSize();
         if (var4 == -1) {
            if (var5 > 0) {
               if (var3 > 0) {
                  var4 = 0;
               } else {
                  var4 = var5 - 1;
               }
            }
         } else if (var5 == 1) {
            var4 = 0;
         } else if (var1.getLayoutOrientation() == 2) {
            if (var2 != null) {
               var4 += var2.columnCount * var3;
            }
         } else {
            var4 += var3;
         }

         return var4;
      }
   }

   public class PropertyChangeHandler implements PropertyChangeListener {
      public void propertyChange(PropertyChangeEvent var1) {
         BasicListUI.this.getHandler().propertyChange(var1);
      }
   }

   public class ListDataHandler implements ListDataListener {
      public void intervalAdded(ListDataEvent var1) {
         BasicListUI.this.getHandler().intervalAdded(var1);
      }

      public void intervalRemoved(ListDataEvent var1) {
         BasicListUI.this.getHandler().intervalRemoved(var1);
      }

      public void contentsChanged(ListDataEvent var1) {
         BasicListUI.this.getHandler().contentsChanged(var1);
      }
   }

   public class ListSelectionHandler implements ListSelectionListener {
      public void valueChanged(ListSelectionEvent var1) {
         BasicListUI.this.getHandler().valueChanged(var1);
      }
   }

   public class FocusHandler implements FocusListener {
      protected void repaintCellFocus() {
         BasicListUI.this.getHandler().repaintCellFocus();
      }

      public void focusGained(FocusEvent var1) {
         BasicListUI.this.getHandler().focusGained(var1);
      }

      public void focusLost(FocusEvent var1) {
         BasicListUI.this.getHandler().focusLost(var1);
      }
   }

   public class MouseInputHandler implements MouseInputListener {
      public void mouseClicked(MouseEvent var1) {
         BasicListUI.this.getHandler().mouseClicked(var1);
      }

      public void mouseEntered(MouseEvent var1) {
         BasicListUI.this.getHandler().mouseEntered(var1);
      }

      public void mouseExited(MouseEvent var1) {
         BasicListUI.this.getHandler().mouseExited(var1);
      }

      public void mousePressed(MouseEvent var1) {
         BasicListUI.this.getHandler().mousePressed(var1);
      }

      public void mouseDragged(MouseEvent var1) {
         BasicListUI.this.getHandler().mouseDragged(var1);
      }

      public void mouseMoved(MouseEvent var1) {
         BasicListUI.this.getHandler().mouseMoved(var1);
      }

      public void mouseReleased(MouseEvent var1) {
         BasicListUI.this.getHandler().mouseReleased(var1);
      }
   }
}
