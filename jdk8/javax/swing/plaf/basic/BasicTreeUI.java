package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.CellRendererPane;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.TransferHandler;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.MouseInputListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TreeUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.Position;
import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.FixedHeightLayoutCache;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.VariableHeightLayoutCache;
import sun.awt.AWTAccessor;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class BasicTreeUI extends TreeUI {
   private static final StringBuilder BASELINE_COMPONENT_KEY = new StringBuilder("Tree.baselineComponent");
   private static final BasicTreeUI.Actions SHARED_ACTION = new BasicTreeUI.Actions();
   protected transient Icon collapsedIcon;
   protected transient Icon expandedIcon;
   private Color hashColor;
   protected int leftChildIndent;
   protected int rightChildIndent;
   protected int totalChildIndent;
   protected Dimension preferredMinSize;
   protected int lastSelectedRow;
   protected JTree tree;
   protected transient TreeCellRenderer currentCellRenderer;
   protected boolean createdRenderer;
   protected transient TreeCellEditor cellEditor;
   protected boolean createdCellEditor;
   protected boolean stopEditingInCompleteEditing;
   protected CellRendererPane rendererPane;
   protected Dimension preferredSize;
   protected boolean validCachedPreferredSize;
   protected AbstractLayoutCache treeState;
   protected Hashtable<TreePath, Boolean> drawingCache;
   protected boolean largeModel;
   protected AbstractLayoutCache.NodeDimensions nodeDimensions;
   protected TreeModel treeModel;
   protected TreeSelectionModel treeSelectionModel;
   protected int depthOffset;
   protected Component editingComponent;
   protected TreePath editingPath;
   protected int editingRow;
   protected boolean editorHasDifferentSize;
   private int leadRow;
   private boolean ignoreLAChange;
   private boolean leftToRight;
   private PropertyChangeListener propertyChangeListener;
   private PropertyChangeListener selectionModelPropertyChangeListener;
   private MouseListener mouseListener;
   private FocusListener focusListener;
   private KeyListener keyListener;
   private ComponentListener componentListener;
   private CellEditorListener cellEditorListener;
   private TreeSelectionListener treeSelectionListener;
   private TreeModelListener treeModelListener;
   private TreeExpansionListener treeExpansionListener;
   private boolean paintLines = true;
   private boolean lineTypeDashed;
   private long timeFactor = 1000L;
   private BasicTreeUI.Handler handler;
   private MouseEvent releaseEvent;
   private static final TransferHandler defaultTransferHandler = new BasicTreeUI.TreeTransferHandler();

   public static ComponentUI createUI(JComponent var0) {
      return new BasicTreeUI();
   }

   static void loadActionMap(LazyActionMap var0) {
      var0.put(new BasicTreeUI.Actions("selectPrevious"));
      var0.put(new BasicTreeUI.Actions("selectPreviousChangeLead"));
      var0.put(new BasicTreeUI.Actions("selectPreviousExtendSelection"));
      var0.put(new BasicTreeUI.Actions("selectNext"));
      var0.put(new BasicTreeUI.Actions("selectNextChangeLead"));
      var0.put(new BasicTreeUI.Actions("selectNextExtendSelection"));
      var0.put(new BasicTreeUI.Actions("selectChild"));
      var0.put(new BasicTreeUI.Actions("selectChildChangeLead"));
      var0.put(new BasicTreeUI.Actions("selectParent"));
      var0.put(new BasicTreeUI.Actions("selectParentChangeLead"));
      var0.put(new BasicTreeUI.Actions("scrollUpChangeSelection"));
      var0.put(new BasicTreeUI.Actions("scrollUpChangeLead"));
      var0.put(new BasicTreeUI.Actions("scrollUpExtendSelection"));
      var0.put(new BasicTreeUI.Actions("scrollDownChangeSelection"));
      var0.put(new BasicTreeUI.Actions("scrollDownExtendSelection"));
      var0.put(new BasicTreeUI.Actions("scrollDownChangeLead"));
      var0.put(new BasicTreeUI.Actions("selectFirst"));
      var0.put(new BasicTreeUI.Actions("selectFirstChangeLead"));
      var0.put(new BasicTreeUI.Actions("selectFirstExtendSelection"));
      var0.put(new BasicTreeUI.Actions("selectLast"));
      var0.put(new BasicTreeUI.Actions("selectLastChangeLead"));
      var0.put(new BasicTreeUI.Actions("selectLastExtendSelection"));
      var0.put(new BasicTreeUI.Actions("toggle"));
      var0.put(new BasicTreeUI.Actions("cancel"));
      var0.put(new BasicTreeUI.Actions("startEditing"));
      var0.put(new BasicTreeUI.Actions("selectAll"));
      var0.put(new BasicTreeUI.Actions("clearSelection"));
      var0.put(new BasicTreeUI.Actions("scrollLeft"));
      var0.put(new BasicTreeUI.Actions("scrollRight"));
      var0.put(new BasicTreeUI.Actions("scrollLeftExtendSelection"));
      var0.put(new BasicTreeUI.Actions("scrollRightExtendSelection"));
      var0.put(new BasicTreeUI.Actions("scrollRightChangeLead"));
      var0.put(new BasicTreeUI.Actions("scrollLeftChangeLead"));
      var0.put(new BasicTreeUI.Actions("expand"));
      var0.put(new BasicTreeUI.Actions("collapse"));
      var0.put(new BasicTreeUI.Actions("moveSelectionToParent"));
      var0.put(new BasicTreeUI.Actions("addToSelection"));
      var0.put(new BasicTreeUI.Actions("toggleAndAnchor"));
      var0.put(new BasicTreeUI.Actions("extendTo"));
      var0.put(new BasicTreeUI.Actions("moveSelectionTo"));
      var0.put(TransferHandler.getCutAction());
      var0.put(TransferHandler.getCopyAction());
      var0.put(TransferHandler.getPasteAction());
   }

   protected Color getHashColor() {
      return this.hashColor;
   }

   protected void setHashColor(Color var1) {
      this.hashColor = var1;
   }

   public void setLeftChildIndent(int var1) {
      this.leftChildIndent = var1;
      this.totalChildIndent = this.leftChildIndent + this.rightChildIndent;
      if (this.treeState != null) {
         this.treeState.invalidateSizes();
      }

      this.updateSize();
   }

   public int getLeftChildIndent() {
      return this.leftChildIndent;
   }

   public void setRightChildIndent(int var1) {
      this.rightChildIndent = var1;
      this.totalChildIndent = this.leftChildIndent + this.rightChildIndent;
      if (this.treeState != null) {
         this.treeState.invalidateSizes();
      }

      this.updateSize();
   }

   public int getRightChildIndent() {
      return this.rightChildIndent;
   }

   public void setExpandedIcon(Icon var1) {
      this.expandedIcon = var1;
   }

   public Icon getExpandedIcon() {
      return this.expandedIcon;
   }

   public void setCollapsedIcon(Icon var1) {
      this.collapsedIcon = var1;
   }

   public Icon getCollapsedIcon() {
      return this.collapsedIcon;
   }

   protected void setLargeModel(boolean var1) {
      if (this.getRowHeight() < 1) {
         var1 = false;
      }

      if (this.largeModel != var1) {
         this.completeEditing();
         this.largeModel = var1;
         this.treeState = this.createLayoutCache();
         this.configureLayoutCache();
         this.updateLayoutCacheExpandedNodesIfNecessary();
         this.updateSize();
      }

   }

   protected boolean isLargeModel() {
      return this.largeModel;
   }

   protected void setRowHeight(int var1) {
      this.completeEditing();
      if (this.treeState != null) {
         this.setLargeModel(this.tree.isLargeModel());
         this.treeState.setRowHeight(var1);
         this.updateSize();
      }

   }

   protected int getRowHeight() {
      return this.tree == null ? -1 : this.tree.getRowHeight();
   }

   protected void setCellRenderer(TreeCellRenderer var1) {
      this.completeEditing();
      this.updateRenderer();
      if (this.treeState != null) {
         this.treeState.invalidateSizes();
         this.updateSize();
      }

   }

   protected TreeCellRenderer getCellRenderer() {
      return this.currentCellRenderer;
   }

   protected void setModel(TreeModel var1) {
      this.completeEditing();
      if (this.treeModel != null && this.treeModelListener != null) {
         this.treeModel.removeTreeModelListener(this.treeModelListener);
      }

      this.treeModel = var1;
      if (this.treeModel != null && this.treeModelListener != null) {
         this.treeModel.addTreeModelListener(this.treeModelListener);
      }

      if (this.treeState != null) {
         this.treeState.setModel(var1);
         this.updateLayoutCacheExpandedNodesIfNecessary();
         this.updateSize();
      }

   }

   protected TreeModel getModel() {
      return this.treeModel;
   }

   protected void setRootVisible(boolean var1) {
      this.completeEditing();
      this.updateDepthOffset();
      if (this.treeState != null) {
         this.treeState.setRootVisible(var1);
         this.treeState.invalidateSizes();
         this.updateSize();
      }

   }

   protected boolean isRootVisible() {
      return this.tree != null ? this.tree.isRootVisible() : false;
   }

   protected void setShowsRootHandles(boolean var1) {
      this.completeEditing();
      this.updateDepthOffset();
      if (this.treeState != null) {
         this.treeState.invalidateSizes();
         this.updateSize();
      }

   }

   protected boolean getShowsRootHandles() {
      return this.tree != null ? this.tree.getShowsRootHandles() : false;
   }

   protected void setCellEditor(TreeCellEditor var1) {
      this.updateCellEditor();
   }

   protected TreeCellEditor getCellEditor() {
      return this.tree != null ? this.tree.getCellEditor() : null;
   }

   protected void setEditable(boolean var1) {
      this.updateCellEditor();
   }

   protected boolean isEditable() {
      return this.tree != null ? this.tree.isEditable() : false;
   }

   protected void setSelectionModel(TreeSelectionModel var1) {
      this.completeEditing();
      if (this.selectionModelPropertyChangeListener != null && this.treeSelectionModel != null) {
         this.treeSelectionModel.removePropertyChangeListener(this.selectionModelPropertyChangeListener);
      }

      if (this.treeSelectionListener != null && this.treeSelectionModel != null) {
         this.treeSelectionModel.removeTreeSelectionListener(this.treeSelectionListener);
      }

      this.treeSelectionModel = var1;
      if (this.treeSelectionModel != null) {
         if (this.selectionModelPropertyChangeListener != null) {
            this.treeSelectionModel.addPropertyChangeListener(this.selectionModelPropertyChangeListener);
         }

         if (this.treeSelectionListener != null) {
            this.treeSelectionModel.addTreeSelectionListener(this.treeSelectionListener);
         }

         if (this.treeState != null) {
            this.treeState.setSelectionModel(this.treeSelectionModel);
         }
      } else if (this.treeState != null) {
         this.treeState.setSelectionModel((TreeSelectionModel)null);
      }

      if (this.tree != null) {
         this.tree.repaint();
      }

   }

   protected TreeSelectionModel getSelectionModel() {
      return this.treeSelectionModel;
   }

   public Rectangle getPathBounds(JTree var1, TreePath var2) {
      return var1 != null && this.treeState != null ? this.getPathBounds(var2, var1.getInsets(), new Rectangle()) : null;
   }

   private Rectangle getPathBounds(TreePath var1, Insets var2, Rectangle var3) {
      var3 = this.treeState.getBounds(var1, var3);
      if (var3 != null) {
         if (this.leftToRight) {
            var3.x += var2.left;
         } else {
            var3.x = this.tree.getWidth() - (var3.x + var3.width) - var2.right;
         }

         var3.y += var2.top;
      }

      return var3;
   }

   public TreePath getPathForRow(JTree var1, int var2) {
      return this.treeState != null ? this.treeState.getPathForRow(var2) : null;
   }

   public int getRowForPath(JTree var1, TreePath var2) {
      return this.treeState != null ? this.treeState.getRowForPath(var2) : -1;
   }

   public int getRowCount(JTree var1) {
      return this.treeState != null ? this.treeState.getRowCount() : 0;
   }

   public TreePath getClosestPathForLocation(JTree var1, int var2, int var3) {
      if (var1 != null && this.treeState != null) {
         var3 -= var1.getInsets().top;
         return this.treeState.getPathClosestTo(var2, var3);
      } else {
         return null;
      }
   }

   public boolean isEditing(JTree var1) {
      return this.editingComponent != null;
   }

   public boolean stopEditing(JTree var1) {
      if (this.editingComponent != null && this.cellEditor.stopCellEditing()) {
         this.completeEditing(false, false, true);
         return true;
      } else {
         return false;
      }
   }

   public void cancelEditing(JTree var1) {
      if (this.editingComponent != null) {
         this.completeEditing(false, true, false);
      }

   }

   public void startEditingAtPath(JTree var1, TreePath var2) {
      var1.scrollPathToVisible(var2);
      if (var2 != null && var1.isVisible(var2)) {
         this.startEditing(var2, (MouseEvent)null);
      }

   }

   public TreePath getEditingPath(JTree var1) {
      return this.editingPath;
   }

   public void installUI(JComponent var1) {
      if (var1 == null) {
         throw new NullPointerException("null component passed to BasicTreeUI.installUI()");
      } else {
         this.tree = (JTree)var1;
         this.prepareForUIInstall();
         this.installDefaults();
         this.installKeyboardActions();
         this.installComponents();
         this.installListeners();
         this.completeUIInstall();
      }
   }

   protected void prepareForUIInstall() {
      this.drawingCache = new Hashtable(7);
      this.leftToRight = BasicGraphicsUtils.isLeftToRight(this.tree);
      this.stopEditingInCompleteEditing = true;
      this.lastSelectedRow = -1;
      this.leadRow = -1;
      this.preferredSize = new Dimension();
      this.largeModel = this.tree.isLargeModel();
      if (this.getRowHeight() <= 0) {
         this.largeModel = false;
      }

      this.setModel(this.tree.getModel());
   }

   protected void completeUIInstall() {
      this.setShowsRootHandles(this.tree.getShowsRootHandles());
      this.updateRenderer();
      this.updateDepthOffset();
      this.setSelectionModel(this.tree.getSelectionModel());
      this.treeState = this.createLayoutCache();
      this.configureLayoutCache();
      this.updateSize();
   }

   protected void installDefaults() {
      if (this.tree.getBackground() == null || this.tree.getBackground() instanceof UIResource) {
         this.tree.setBackground(UIManager.getColor("Tree.background"));
      }

      if (this.getHashColor() == null || this.getHashColor() instanceof UIResource) {
         this.setHashColor(UIManager.getColor("Tree.hash"));
      }

      if (this.tree.getFont() == null || this.tree.getFont() instanceof UIResource) {
         this.tree.setFont(UIManager.getFont("Tree.font"));
      }

      this.setExpandedIcon((Icon)UIManager.get("Tree.expandedIcon"));
      this.setCollapsedIcon((Icon)UIManager.get("Tree.collapsedIcon"));
      this.setLeftChildIndent((Integer)UIManager.get("Tree.leftChildIndent"));
      this.setRightChildIndent((Integer)UIManager.get("Tree.rightChildIndent"));
      LookAndFeel.installProperty(this.tree, "rowHeight", UIManager.get("Tree.rowHeight"));
      this.largeModel = this.tree.isLargeModel() && this.tree.getRowHeight() > 0;
      Object var1 = UIManager.get("Tree.scrollsOnExpand");
      if (var1 != null) {
         LookAndFeel.installProperty(this.tree, "scrollsOnExpand", var1);
      }

      this.paintLines = UIManager.getBoolean("Tree.paintLines");
      this.lineTypeDashed = UIManager.getBoolean("Tree.lineTypeDashed");
      Long var2 = (Long)UIManager.get("Tree.timeFactor");
      this.timeFactor = var2 != null ? var2 : 1000L;
      Object var3 = UIManager.get("Tree.showsRootHandles");
      if (var3 != null) {
         LookAndFeel.installProperty(this.tree, "showsRootHandles", var3);
      }

   }

   protected void installListeners() {
      if ((this.propertyChangeListener = this.createPropertyChangeListener()) != null) {
         this.tree.addPropertyChangeListener(this.propertyChangeListener);
      }

      if ((this.mouseListener = this.createMouseListener()) != null) {
         this.tree.addMouseListener(this.mouseListener);
         if (this.mouseListener instanceof MouseMotionListener) {
            this.tree.addMouseMotionListener((MouseMotionListener)this.mouseListener);
         }
      }

      if ((this.focusListener = this.createFocusListener()) != null) {
         this.tree.addFocusListener(this.focusListener);
      }

      if ((this.keyListener = this.createKeyListener()) != null) {
         this.tree.addKeyListener(this.keyListener);
      }

      if ((this.treeExpansionListener = this.createTreeExpansionListener()) != null) {
         this.tree.addTreeExpansionListener(this.treeExpansionListener);
      }

      if ((this.treeModelListener = this.createTreeModelListener()) != null && this.treeModel != null) {
         this.treeModel.addTreeModelListener(this.treeModelListener);
      }

      if ((this.selectionModelPropertyChangeListener = this.createSelectionModelPropertyChangeListener()) != null && this.treeSelectionModel != null) {
         this.treeSelectionModel.addPropertyChangeListener(this.selectionModelPropertyChangeListener);
      }

      if ((this.treeSelectionListener = this.createTreeSelectionListener()) != null && this.treeSelectionModel != null) {
         this.treeSelectionModel.addTreeSelectionListener(this.treeSelectionListener);
      }

      TransferHandler var1 = this.tree.getTransferHandler();
      if (var1 == null || var1 instanceof UIResource) {
         this.tree.setTransferHandler(defaultTransferHandler);
         if (this.tree.getDropTarget() instanceof UIResource) {
            this.tree.setDropTarget((DropTarget)null);
         }
      }

      LookAndFeel.installProperty(this.tree, "opaque", Boolean.TRUE);
   }

   protected void installKeyboardActions() {
      InputMap var1 = this.getInputMap(1);
      SwingUtilities.replaceUIInputMap(this.tree, 1, var1);
      var1 = this.getInputMap(0);
      SwingUtilities.replaceUIInputMap(this.tree, 0, var1);
      LazyActionMap.installLazyActionMap(this.tree, BasicTreeUI.class, "Tree.actionMap");
   }

   InputMap getInputMap(int var1) {
      if (var1 == 1) {
         return (InputMap)DefaultLookup.get(this.tree, this, "Tree.ancestorInputMap");
      } else if (var1 == 0) {
         InputMap var2 = (InputMap)DefaultLookup.get(this.tree, this, "Tree.focusInputMap");
         InputMap var3;
         if (!this.tree.getComponentOrientation().isLeftToRight() && (var3 = (InputMap)DefaultLookup.get(this.tree, this, "Tree.focusInputMap.RightToLeft")) != null) {
            var3.setParent(var2);
            return var3;
         } else {
            return var2;
         }
      } else {
         return null;
      }
   }

   protected void installComponents() {
      if ((this.rendererPane = this.createCellRendererPane()) != null) {
         this.tree.add(this.rendererPane);
      }

   }

   protected AbstractLayoutCache.NodeDimensions createNodeDimensions() {
      return new BasicTreeUI.NodeDimensionsHandler();
   }

   protected PropertyChangeListener createPropertyChangeListener() {
      return this.getHandler();
   }

   private BasicTreeUI.Handler getHandler() {
      if (this.handler == null) {
         this.handler = new BasicTreeUI.Handler();
      }

      return this.handler;
   }

   protected MouseListener createMouseListener() {
      return this.getHandler();
   }

   protected FocusListener createFocusListener() {
      return this.getHandler();
   }

   protected KeyListener createKeyListener() {
      return this.getHandler();
   }

   protected PropertyChangeListener createSelectionModelPropertyChangeListener() {
      return this.getHandler();
   }

   protected TreeSelectionListener createTreeSelectionListener() {
      return this.getHandler();
   }

   protected CellEditorListener createCellEditorListener() {
      return this.getHandler();
   }

   protected ComponentListener createComponentListener() {
      return new BasicTreeUI.ComponentHandler();
   }

   protected TreeExpansionListener createTreeExpansionListener() {
      return this.getHandler();
   }

   protected AbstractLayoutCache createLayoutCache() {
      return (AbstractLayoutCache)(this.isLargeModel() && this.getRowHeight() > 0 ? new FixedHeightLayoutCache() : new VariableHeightLayoutCache());
   }

   protected CellRendererPane createCellRendererPane() {
      return new CellRendererPane();
   }

   protected TreeCellEditor createDefaultCellEditor() {
      if (this.currentCellRenderer != null && this.currentCellRenderer instanceof DefaultTreeCellRenderer) {
         DefaultTreeCellEditor var1 = new DefaultTreeCellEditor(this.tree, (DefaultTreeCellRenderer)this.currentCellRenderer);
         return var1;
      } else {
         return new DefaultTreeCellEditor(this.tree, (DefaultTreeCellRenderer)null);
      }
   }

   protected TreeCellRenderer createDefaultCellRenderer() {
      return new DefaultTreeCellRenderer();
   }

   protected TreeModelListener createTreeModelListener() {
      return this.getHandler();
   }

   public void uninstallUI(JComponent var1) {
      this.completeEditing();
      this.prepareForUIUninstall();
      this.uninstallDefaults();
      this.uninstallListeners();
      this.uninstallKeyboardActions();
      this.uninstallComponents();
      this.completeUIUninstall();
   }

   protected void prepareForUIUninstall() {
   }

   protected void completeUIUninstall() {
      if (this.createdRenderer) {
         this.tree.setCellRenderer((TreeCellRenderer)null);
      }

      if (this.createdCellEditor) {
         this.tree.setCellEditor((TreeCellEditor)null);
      }

      this.cellEditor = null;
      this.currentCellRenderer = null;
      this.rendererPane = null;
      this.componentListener = null;
      this.propertyChangeListener = null;
      this.mouseListener = null;
      this.focusListener = null;
      this.keyListener = null;
      this.setSelectionModel((TreeSelectionModel)null);
      this.treeState = null;
      this.drawingCache = null;
      this.selectionModelPropertyChangeListener = null;
      this.tree = null;
      this.treeModel = null;
      this.treeSelectionModel = null;
      this.treeSelectionListener = null;
      this.treeExpansionListener = null;
   }

   protected void uninstallDefaults() {
      if (this.tree.getTransferHandler() instanceof UIResource) {
         this.tree.setTransferHandler((TransferHandler)null);
      }

   }

   protected void uninstallListeners() {
      if (this.componentListener != null) {
         this.tree.removeComponentListener(this.componentListener);
      }

      if (this.propertyChangeListener != null) {
         this.tree.removePropertyChangeListener(this.propertyChangeListener);
      }

      if (this.mouseListener != null) {
         this.tree.removeMouseListener(this.mouseListener);
         if (this.mouseListener instanceof MouseMotionListener) {
            this.tree.removeMouseMotionListener((MouseMotionListener)this.mouseListener);
         }
      }

      if (this.focusListener != null) {
         this.tree.removeFocusListener(this.focusListener);
      }

      if (this.keyListener != null) {
         this.tree.removeKeyListener(this.keyListener);
      }

      if (this.treeExpansionListener != null) {
         this.tree.removeTreeExpansionListener(this.treeExpansionListener);
      }

      if (this.treeModel != null && this.treeModelListener != null) {
         this.treeModel.removeTreeModelListener(this.treeModelListener);
      }

      if (this.selectionModelPropertyChangeListener != null && this.treeSelectionModel != null) {
         this.treeSelectionModel.removePropertyChangeListener(this.selectionModelPropertyChangeListener);
      }

      if (this.treeSelectionListener != null && this.treeSelectionModel != null) {
         this.treeSelectionModel.removeTreeSelectionListener(this.treeSelectionListener);
      }

      this.handler = null;
   }

   protected void uninstallKeyboardActions() {
      SwingUtilities.replaceUIActionMap(this.tree, (ActionMap)null);
      SwingUtilities.replaceUIInputMap(this.tree, 1, (InputMap)null);
      SwingUtilities.replaceUIInputMap(this.tree, 0, (InputMap)null);
   }

   protected void uninstallComponents() {
      if (this.rendererPane != null) {
         this.tree.remove(this.rendererPane);
      }

   }

   private void redoTheLayout() {
      if (this.treeState != null) {
         this.treeState.invalidateSizes();
      }

   }

   public int getBaseline(JComponent var1, int var2, int var3) {
      super.getBaseline(var1, var2, var3);
      UIDefaults var4 = UIManager.getLookAndFeelDefaults();
      Component var5 = (Component)var4.get(BASELINE_COMPONENT_KEY);
      if (var5 == null) {
         TreeCellRenderer var6 = this.createDefaultCellRenderer();
         var5 = var6.getTreeCellRendererComponent(this.tree, "a", false, false, false, -1, false);
         var4.put(BASELINE_COMPONENT_KEY, var5);
      }

      int var9 = this.tree.getRowHeight();
      int var7;
      if (var9 > 0) {
         var7 = var5.getBaseline(Integer.MAX_VALUE, var9);
      } else {
         Dimension var8 = var5.getPreferredSize();
         var7 = var5.getBaseline(var8.width, var8.height);
      }

      return var7 + this.tree.getInsets().top;
   }

   public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent var1) {
      super.getBaselineResizeBehavior(var1);
      return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
   }

   public void paint(Graphics var1, JComponent var2) {
      if (this.tree != var2) {
         throw new InternalError("incorrect component");
      } else if (this.treeState != null) {
         Rectangle var3 = var1.getClipBounds();
         Insets var4 = this.tree.getInsets();
         TreePath var5 = this.getClosestPathForLocation(this.tree, 0, var3.y);
         Enumeration var6 = this.treeState.getVisiblePathsFrom(var5);
         int var7 = this.treeState.getRowForPath(var5);
         int var8 = var3.y + var3.height;
         this.drawingCache.clear();
         if (var5 != null && var6 != null) {
            TreePath var9;
            for(var9 = var5.getParentPath(); var9 != null; var9 = var9.getParentPath()) {
               this.paintVerticalPartOfLeg(var1, var3, var4, var9);
               this.drawingCache.put(var9, Boolean.TRUE);
            }

            boolean var10 = false;
            Rectangle var14 = new Rectangle();

            for(boolean var17 = this.isRootVisible(); !var10 && var6.hasMoreElements(); ++var7) {
               TreePath var16 = (TreePath)var6.nextElement();
               if (var16 != null) {
                  boolean var13 = this.treeModel.isLeaf(var16.getLastPathComponent());
                  boolean var11;
                  boolean var12;
                  if (var13) {
                     var12 = false;
                     var11 = false;
                  } else {
                     var11 = this.treeState.getExpandedState(var16);
                     var12 = this.tree.hasBeenExpanded(var16);
                  }

                  Rectangle var15 = this.getPathBounds(var16, var4, var14);
                  if (var15 == null) {
                     return;
                  }

                  var9 = var16.getParentPath();
                  if (var9 != null) {
                     if (this.drawingCache.get(var9) == null) {
                        this.paintVerticalPartOfLeg(var1, var3, var4, var9);
                        this.drawingCache.put(var9, Boolean.TRUE);
                     }

                     this.paintHorizontalPartOfLeg(var1, var3, var4, var15, var16, var7, var11, var12, var13);
                  } else if (var17 && var7 == 0) {
                     this.paintHorizontalPartOfLeg(var1, var3, var4, var15, var16, var7, var11, var12, var13);
                  }

                  if (this.shouldPaintExpandControl(var16, var7, var11, var12, var13)) {
                     this.paintExpandControl(var1, var3, var4, var15, var16, var7, var11, var12, var13);
                  }

                  this.paintRow(var1, var3, var4, var15, var16, var7, var11, var12, var13);
                  if (var15.y + var15.height >= var8) {
                     var10 = true;
                  }
               } else {
                  var10 = true;
               }
            }
         }

         this.paintDropLine(var1);
         this.rendererPane.removeAll();
         this.drawingCache.clear();
      }
   }

   protected boolean isDropLine(JTree.DropLocation var1) {
      return var1 != null && var1.getPath() != null && var1.getChildIndex() != -1;
   }

   protected void paintDropLine(Graphics var1) {
      JTree.DropLocation var2 = this.tree.getDropLocation();
      if (this.isDropLine(var2)) {
         Color var3 = UIManager.getColor("Tree.dropLineColor");
         if (var3 != null) {
            var1.setColor(var3);
            Rectangle var4 = this.getDropLineRect(var2);
            var1.fillRect(var4.x, var4.y, var4.width, var4.height);
         }

      }
   }

   protected Rectangle getDropLineRect(JTree.DropLocation var1) {
      TreePath var3 = var1.getPath();
      int var4 = var1.getChildIndex();
      boolean var5 = this.leftToRight;
      Insets var6 = this.tree.getInsets();
      Rectangle var2;
      if (this.tree.getRowCount() == 0) {
         var2 = new Rectangle(var6.left, var6.top, this.tree.getWidth() - var6.left - var6.right, 0);
      } else {
         TreeModel var7 = this.getModel();
         Object var8 = var7.getRoot();
         if (var3.getLastPathComponent() == var8 && var4 >= var7.getChildCount(var8)) {
            var2 = this.tree.getRowBounds(this.tree.getRowCount() - 1);
            var2.y += var2.height;
            Rectangle var9;
            if (!this.tree.isRootVisible()) {
               var9 = this.tree.getRowBounds(0);
            } else if (var7.getChildCount(var8) == 0) {
               var9 = this.tree.getRowBounds(0);
               var9.x += this.totalChildIndent;
               var9.width -= this.totalChildIndent + this.totalChildIndent;
            } else {
               TreePath var10 = var3.pathByAddingChild(var7.getChild(var8, var7.getChildCount(var8) - 1));
               var9 = this.tree.getPathBounds(var10);
            }

            var2.x = var9.x;
            var2.width = var9.width;
         } else {
            var2 = this.tree.getPathBounds(var3.pathByAddingChild(var7.getChild(var3.getLastPathComponent(), var4)));
         }
      }

      if (var2.y != 0) {
         --var2.y;
      }

      if (!var5) {
         var2.x = var2.x + var2.width - 100;
      }

      var2.width = 100;
      var2.height = 2;
      return var2;
   }

   protected void paintHorizontalPartOfLeg(Graphics var1, Rectangle var2, Insets var3, Rectangle var4, TreePath var5, int var6, boolean var7, boolean var8, boolean var9) {
      if (this.paintLines) {
         int var10 = var5.getPathCount() - 1;
         if (var10 != 0 && (var10 != 1 || this.isRootVisible()) || this.getShowsRootHandles()) {
            int var11 = var2.x;
            int var12 = var2.x + var2.width;
            int var13 = var2.y;
            int var14 = var2.y + var2.height;
            int var15 = var4.y + var4.height / 2;
            int var16;
            int var17;
            if (this.leftToRight) {
               var16 = var4.x - this.getRightChildIndent();
               var17 = var4.x - this.getHorizontalLegBuffer();
               if (var15 >= var13 && var15 < var14 && var17 >= var11 && var16 < var12 && var16 < var17) {
                  var1.setColor(this.getHashColor());
                  this.paintHorizontalLine(var1, this.tree, var15, var16, var17 - 1);
               }
            } else {
               var16 = var4.x + var4.width + this.getHorizontalLegBuffer();
               var17 = var4.x + var4.width + this.getRightChildIndent();
               if (var15 >= var13 && var15 < var14 && var17 >= var11 && var16 < var12 && var16 < var17) {
                  var1.setColor(this.getHashColor());
                  this.paintHorizontalLine(var1, this.tree, var15, var16, var17 - 1);
               }
            }

         }
      }
   }

   protected void paintVerticalPartOfLeg(Graphics var1, Rectangle var2, Insets var3, TreePath var4) {
      if (this.paintLines) {
         int var5 = var4.getPathCount() - 1;
         if (var5 != 0 || this.getShowsRootHandles() || this.isRootVisible()) {
            int var6 = this.getRowX(-1, var5 + 1);
            if (this.leftToRight) {
               var6 = var6 - this.getRightChildIndent() + var3.left;
            } else {
               var6 = this.tree.getWidth() - var6 - var3.right + this.getRightChildIndent() - 1;
            }

            int var7 = var2.x;
            int var8 = var2.x + (var2.width - 1);
            if (var6 >= var7 && var6 <= var8) {
               int var9 = var2.y;
               int var10 = var2.y + var2.height;
               Rectangle var11 = this.getPathBounds(this.tree, var4);
               Rectangle var12 = this.getPathBounds(this.tree, this.getLastChildPath(var4));
               if (var12 == null) {
                  return;
               }

               int var13;
               if (var11 == null) {
                  var13 = Math.max(var3.top + this.getVerticalLegBuffer(), var9);
               } else {
                  var13 = Math.max(var11.y + var11.height + this.getVerticalLegBuffer(), var9);
               }

               if (var5 == 0 && !this.isRootVisible()) {
                  TreeModel var14 = this.getModel();
                  if (var14 != null) {
                     Object var15 = var14.getRoot();
                     if (var14.getChildCount(var15) > 0) {
                        var11 = this.getPathBounds(this.tree, var4.pathByAddingChild(var14.getChild(var15, 0)));
                        if (var11 != null) {
                           var13 = Math.max(var3.top + this.getVerticalLegBuffer(), var11.y + var11.height / 2);
                        }
                     }
                  }
               }

               int var16 = Math.min(var12.y + var12.height / 2, var10);
               if (var13 <= var16) {
                  var1.setColor(this.getHashColor());
                  this.paintVerticalLine(var1, this.tree, var6, var13, var16);
               }
            }

         }
      }
   }

   protected void paintExpandControl(Graphics var1, Rectangle var2, Insets var3, Rectangle var4, TreePath var5, int var6, boolean var7, boolean var8, boolean var9) {
      Object var10 = var5.getLastPathComponent();
      if (!var9 && (!var8 || this.treeModel.getChildCount(var10) > 0)) {
         int var11;
         if (this.leftToRight) {
            var11 = var4.x - this.getRightChildIndent() + 1;
         } else {
            var11 = var4.x + var4.width + this.getRightChildIndent() - 1;
         }

         int var12 = var4.y + var4.height / 2;
         Icon var13;
         if (var7) {
            var13 = this.getExpandedIcon();
            if (var13 != null) {
               this.drawCentered(this.tree, var1, var13, var11, var12);
            }
         } else {
            var13 = this.getCollapsedIcon();
            if (var13 != null) {
               this.drawCentered(this.tree, var1, var13, var11, var12);
            }
         }
      }

   }

   protected void paintRow(Graphics var1, Rectangle var2, Insets var3, Rectangle var4, TreePath var5, int var6, boolean var7, boolean var8, boolean var9) {
      if (this.editingComponent == null || this.editingRow != var6) {
         int var10;
         if (this.tree.hasFocus()) {
            var10 = this.getLeadSelectionRow();
         } else {
            var10 = -1;
         }

         Component var11 = this.currentCellRenderer.getTreeCellRendererComponent(this.tree, var5.getLastPathComponent(), this.tree.isRowSelected(var6), var7, var9, var6, var10 == var6);
         this.rendererPane.paintComponent(var1, var11, this.tree, var4.x, var4.y, var4.width, var4.height, true);
      }
   }

   protected boolean shouldPaintExpandControl(TreePath var1, int var2, boolean var3, boolean var4, boolean var5) {
      if (var5) {
         return false;
      } else {
         int var6 = var1.getPathCount() - 1;
         return var6 != 0 && (var6 != 1 || this.isRootVisible()) || this.getShowsRootHandles();
      }
   }

   protected void paintVerticalLine(Graphics var1, JComponent var2, int var3, int var4, int var5) {
      if (this.lineTypeDashed) {
         this.drawDashedVerticalLine(var1, var3, var4, var5);
      } else {
         var1.drawLine(var3, var4, var3, var5);
      }

   }

   protected void paintHorizontalLine(Graphics var1, JComponent var2, int var3, int var4, int var5) {
      if (this.lineTypeDashed) {
         this.drawDashedHorizontalLine(var1, var3, var4, var5);
      } else {
         var1.drawLine(var4, var3, var5, var3);
      }

   }

   protected int getVerticalLegBuffer() {
      return 0;
   }

   protected int getHorizontalLegBuffer() {
      return 0;
   }

   private int findCenteredX(int var1, int var2) {
      return this.leftToRight ? var1 - (int)Math.ceil((double)var2 / 2.0D) : var1 - (int)Math.floor((double)var2 / 2.0D);
   }

   protected void drawCentered(Component var1, Graphics var2, Icon var3, int var4, int var5) {
      var3.paintIcon(var1, var2, this.findCenteredX(var4, var3.getIconWidth()), var5 - var3.getIconHeight() / 2);
   }

   protected void drawDashedHorizontalLine(Graphics var1, int var2, int var3, int var4) {
      var3 += var3 % 2;

      for(int var5 = var3; var5 <= var4; var5 += 2) {
         var1.drawLine(var5, var2, var5, var2);
      }

   }

   protected void drawDashedVerticalLine(Graphics var1, int var2, int var3, int var4) {
      var3 += var3 % 2;

      for(int var5 = var3; var5 <= var4; var5 += 2) {
         var1.drawLine(var2, var5, var2, var5);
      }

   }

   protected int getRowX(int var1, int var2) {
      return this.totalChildIndent * (var2 + this.depthOffset);
   }

   protected void updateLayoutCacheExpandedNodes() {
      if (this.treeModel != null && this.treeModel.getRoot() != null) {
         this.updateExpandedDescendants(new TreePath(this.treeModel.getRoot()));
      }

   }

   private void updateLayoutCacheExpandedNodesIfNecessary() {
      if (this.treeModel != null && this.treeModel.getRoot() != null) {
         TreePath var1 = new TreePath(this.treeModel.getRoot());
         if (this.tree.isExpanded(var1)) {
            this.updateLayoutCacheExpandedNodes();
         } else {
            this.treeState.setExpandedState(var1, false);
         }
      }

   }

   protected void updateExpandedDescendants(TreePath var1) {
      this.completeEditing();
      if (this.treeState != null) {
         this.treeState.setExpandedState(var1, true);
         Enumeration var2 = this.tree.getExpandedDescendants(var1);
         if (var2 != null) {
            while(var2.hasMoreElements()) {
               var1 = (TreePath)var2.nextElement();
               this.treeState.setExpandedState(var1, true);
            }
         }

         this.updateLeadSelectionRow();
         this.updateSize();
      }

   }

   protected TreePath getLastChildPath(TreePath var1) {
      if (this.treeModel != null) {
         int var2 = this.treeModel.getChildCount(var1.getLastPathComponent());
         if (var2 > 0) {
            return var1.pathByAddingChild(this.treeModel.getChild(var1.getLastPathComponent(), var2 - 1));
         }
      }

      return null;
   }

   protected void updateDepthOffset() {
      if (this.isRootVisible()) {
         if (this.getShowsRootHandles()) {
            this.depthOffset = 1;
         } else {
            this.depthOffset = 0;
         }
      } else if (!this.getShowsRootHandles()) {
         this.depthOffset = -1;
      } else {
         this.depthOffset = 0;
      }

   }

   protected void updateCellEditor() {
      this.completeEditing();
      TreeCellEditor var1;
      if (this.tree == null) {
         var1 = null;
      } else if (this.tree.isEditable()) {
         var1 = this.tree.getCellEditor();
         if (var1 == null) {
            var1 = this.createDefaultCellEditor();
            if (var1 != null) {
               this.tree.setCellEditor(var1);
               this.createdCellEditor = true;
            }
         }
      } else {
         var1 = null;
      }

      if (var1 != this.cellEditor) {
         if (this.cellEditor != null && this.cellEditorListener != null) {
            this.cellEditor.removeCellEditorListener(this.cellEditorListener);
         }

         this.cellEditor = var1;
         if (this.cellEditorListener == null) {
            this.cellEditorListener = this.createCellEditorListener();
         }

         if (var1 != null && this.cellEditorListener != null) {
            var1.addCellEditorListener(this.cellEditorListener);
         }

         this.createdCellEditor = false;
      }

   }

   protected void updateRenderer() {
      if (this.tree != null) {
         TreeCellRenderer var1 = this.tree.getCellRenderer();
         if (var1 == null) {
            this.tree.setCellRenderer(this.createDefaultCellRenderer());
            this.createdRenderer = true;
         } else {
            this.createdRenderer = false;
            this.currentCellRenderer = var1;
            if (this.createdCellEditor) {
               this.tree.setCellEditor((TreeCellEditor)null);
            }
         }
      } else {
         this.createdRenderer = false;
         this.currentCellRenderer = null;
      }

      this.updateCellEditor();
   }

   protected void configureLayoutCache() {
      if (this.treeState != null && this.tree != null) {
         if (this.nodeDimensions == null) {
            this.nodeDimensions = this.createNodeDimensions();
         }

         this.treeState.setNodeDimensions(this.nodeDimensions);
         this.treeState.setRootVisible(this.tree.isRootVisible());
         this.treeState.setRowHeight(this.tree.getRowHeight());
         this.treeState.setSelectionModel(this.getSelectionModel());
         if (this.treeState.getModel() != this.tree.getModel()) {
            this.treeState.setModel(this.tree.getModel());
         }

         this.updateLayoutCacheExpandedNodesIfNecessary();
         if (this.isLargeModel()) {
            if (this.componentListener == null) {
               this.componentListener = this.createComponentListener();
               if (this.componentListener != null) {
                  this.tree.addComponentListener(this.componentListener);
               }
            }
         } else if (this.componentListener != null) {
            this.tree.removeComponentListener(this.componentListener);
            this.componentListener = null;
         }
      } else if (this.componentListener != null) {
         this.tree.removeComponentListener(this.componentListener);
         this.componentListener = null;
      }

   }

   protected void updateSize() {
      this.validCachedPreferredSize = false;
      this.tree.treeDidChange();
   }

   private void updateSize0() {
      this.validCachedPreferredSize = false;
      this.tree.revalidate();
   }

   protected void updateCachedPreferredSize() {
      if (this.treeState != null) {
         Insets var1 = this.tree.getInsets();
         if (!this.isLargeModel()) {
            this.preferredSize.width = this.treeState.getPreferredWidth((Rectangle)null);
         } else {
            Rectangle var2 = this.tree.getVisibleRect();
            if (var2.x == 0 && var2.y == 0 && var2.width == 0 && var2.height == 0 && this.tree.getVisibleRowCount() > 0) {
               var2.width = 1;
               var2.height = this.tree.getRowHeight() * this.tree.getVisibleRowCount();
            } else {
               var2.x -= var1.left;
               var2.y -= var1.top;
            }

            Container var3 = SwingUtilities.getUnwrappedParent(this.tree);
            if (var3 instanceof JViewport) {
               var3 = var3.getParent();
               if (var3 instanceof JScrollPane) {
                  JScrollPane var4 = (JScrollPane)var3;
                  JScrollBar var5 = var4.getHorizontalScrollBar();
                  if (var5 != null && var5.isVisible()) {
                     int var6 = var5.getHeight();
                     var2.y -= var6;
                     var2.height += var6;
                  }
               }
            }

            this.preferredSize.width = this.treeState.getPreferredWidth(var2);
         }

         this.preferredSize.height = this.treeState.getPreferredHeight();
         Dimension var10000 = this.preferredSize;
         var10000.width += var1.left + var1.right;
         var10000 = this.preferredSize;
         var10000.height += var1.top + var1.bottom;
      }

      this.validCachedPreferredSize = true;
   }

   protected void pathWasExpanded(TreePath var1) {
      if (this.tree != null) {
         this.tree.fireTreeExpanded(var1);
      }

   }

   protected void pathWasCollapsed(TreePath var1) {
      if (this.tree != null) {
         this.tree.fireTreeCollapsed(var1);
      }

   }

   protected void ensureRowsAreVisible(int var1, int var2) {
      if (this.tree != null && var1 >= 0 && var2 < this.getRowCount(this.tree)) {
         boolean var3 = DefaultLookup.getBoolean(this.tree, this, "Tree.scrollsHorizontallyAndVertically", false);
         Rectangle var4;
         if (var1 == var2) {
            var4 = this.getPathBounds(this.tree, this.getPathForRow(this.tree, var1));
            if (var4 != null) {
               if (!var3) {
                  var4.x = this.tree.getVisibleRect().x;
                  var4.width = 1;
               }

               this.tree.scrollRectToVisible(var4);
            }
         } else {
            var4 = this.getPathBounds(this.tree, this.getPathForRow(this.tree, var1));
            if (var4 != null) {
               Rectangle var5 = this.tree.getVisibleRect();
               Rectangle var6 = var4;
               int var7 = var4.y;
               int var8 = var7 + var5.height;

               for(int var9 = var1 + 1; var9 <= var2; ++var9) {
                  var6 = this.getPathBounds(this.tree, this.getPathForRow(this.tree, var9));
                  if (var6 == null) {
                     return;
                  }

                  if (var6.y + var6.height > var8) {
                     var9 = var2;
                  }
               }

               this.tree.scrollRectToVisible(new Rectangle(var5.x, var7, 1, var6.y + var6.height - var7));
            }
         }
      }

   }

   public void setPreferredMinSize(Dimension var1) {
      this.preferredMinSize = var1;
   }

   public Dimension getPreferredMinSize() {
      return this.preferredMinSize == null ? null : new Dimension(this.preferredMinSize);
   }

   public Dimension getPreferredSize(JComponent var1) {
      return this.getPreferredSize(var1, true);
   }

   public Dimension getPreferredSize(JComponent var1, boolean var2) {
      Dimension var3 = this.getPreferredMinSize();
      if (!this.validCachedPreferredSize) {
         this.updateCachedPreferredSize();
      }

      if (this.tree != null) {
         return var3 != null ? new Dimension(Math.max(var3.width, this.preferredSize.width), Math.max(var3.height, this.preferredSize.height)) : new Dimension(this.preferredSize.width, this.preferredSize.height);
      } else {
         return var3 != null ? var3 : new Dimension(0, 0);
      }
   }

   public Dimension getMinimumSize(JComponent var1) {
      return this.getPreferredMinSize() != null ? this.getPreferredMinSize() : new Dimension(0, 0);
   }

   public Dimension getMaximumSize(JComponent var1) {
      if (this.tree != null) {
         return this.getPreferredSize(this.tree);
      } else {
         return this.getPreferredMinSize() != null ? this.getPreferredMinSize() : new Dimension(0, 0);
      }
   }

   protected void completeEditing() {
      if (this.tree.getInvokesStopCellEditing() && this.stopEditingInCompleteEditing && this.editingComponent != null) {
         this.cellEditor.stopCellEditing();
      }

      this.completeEditing(false, true, false);
   }

   protected void completeEditing(boolean var1, boolean var2, boolean var3) {
      if (this.stopEditingInCompleteEditing && this.editingComponent != null) {
         Component var4 = this.editingComponent;
         TreePath var5 = this.editingPath;
         TreeCellEditor var6 = this.cellEditor;
         Object var7 = var6.getCellEditorValue();
         Rectangle var8 = this.getPathBounds(this.tree, this.editingPath);
         boolean var9 = this.tree != null && (this.tree.hasFocus() || SwingUtilities.findFocusOwner(this.editingComponent) != null);
         this.editingComponent = null;
         this.editingPath = null;
         if (var1) {
            var6.stopCellEditing();
         } else if (var2) {
            var6.cancelCellEditing();
         }

         this.tree.remove(var4);
         if (this.editorHasDifferentSize) {
            this.treeState.invalidatePathBounds(var5);
            this.updateSize();
         } else if (var8 != null) {
            var8.x = 0;
            var8.width = this.tree.getSize().width;
            this.tree.repaint(var8);
         }

         if (var9) {
            this.tree.requestFocus();
         }

         if (var3) {
            this.treeModel.valueForPathChanged(var5, var7);
         }
      }

   }

   private boolean startEditingOnRelease(TreePath var1, MouseEvent var2, MouseEvent var3) {
      this.releaseEvent = var3;

      boolean var4;
      try {
         var4 = this.startEditing(var1, var2);
      } finally {
         this.releaseEvent = null;
      }

      return var4;
   }

   protected boolean startEditing(TreePath var1, MouseEvent var2) {
      if (this.isEditing(this.tree) && this.tree.getInvokesStopCellEditing() && !this.stopEditing(this.tree)) {
         return false;
      } else {
         this.completeEditing();
         if (this.cellEditor != null && this.tree.isPathEditable(var1)) {
            int var3 = this.getRowForPath(this.tree, var1);
            if (this.cellEditor.isCellEditable(var2)) {
               this.editingComponent = this.cellEditor.getTreeCellEditorComponent(this.tree, var1.getLastPathComponent(), this.tree.isPathSelected(var1), this.tree.isExpanded(var1), this.treeModel.isLeaf(var1.getLastPathComponent()), var3);
               Rectangle var4 = this.getPathBounds(this.tree, var1);
               if (var4 == null) {
                  return false;
               }

               this.editingRow = var3;
               Dimension var5 = this.editingComponent.getPreferredSize();
               if (var5.height != var4.height && this.getRowHeight() > 0) {
                  var5.height = this.getRowHeight();
               }

               if (var5.width == var4.width && var5.height == var4.height) {
                  this.editorHasDifferentSize = false;
               } else {
                  this.editorHasDifferentSize = true;
                  this.treeState.invalidatePathBounds(var1);
                  this.updateSize();
                  var4 = this.getPathBounds(this.tree, var1);
                  if (var4 == null) {
                     return false;
                  }
               }

               this.tree.add(this.editingComponent);
               this.editingComponent.setBounds(var4.x, var4.y, var4.width, var4.height);
               this.editingPath = var1;
               AWTAccessor.getComponentAccessor().revalidateSynchronously(this.editingComponent);
               this.editingComponent.repaint();
               if (this.cellEditor.shouldSelectCell(var2)) {
                  this.stopEditingInCompleteEditing = false;
                  this.tree.setSelectionRow(var3);
                  this.stopEditingInCompleteEditing = true;
               }

               Component var6 = SwingUtilities2.compositeRequestFocus(this.editingComponent);
               boolean var7 = true;
               if (var2 != null) {
                  Point var8 = SwingUtilities.convertPoint(this.tree, new Point(var2.getX(), var2.getY()), this.editingComponent);
                  Component var9 = SwingUtilities.getDeepestComponentAt(this.editingComponent, var8.x, var8.y);
                  if (var9 != null) {
                     BasicTreeUI.MouseInputHandler var10 = new BasicTreeUI.MouseInputHandler(this.tree, var9, var2, var6);
                     if (this.releaseEvent != null) {
                        var10.mouseReleased(this.releaseEvent);
                     }

                     var7 = false;
                  }
               }

               if (var7 && var6 instanceof JTextField) {
                  ((JTextField)var6).selectAll();
               }

               return true;
            }

            this.editingComponent = null;
         }

         return false;
      }
   }

   protected void checkForClickInExpandControl(TreePath var1, int var2, int var3) {
      if (this.isLocationInExpandControl(var1, var2, var3)) {
         this.handleExpandControlClick(var1, var2, var3);
      }

   }

   protected boolean isLocationInExpandControl(TreePath var1, int var2, int var3) {
      if (var1 != null && !this.treeModel.isLeaf(var1.getLastPathComponent())) {
         Insets var5 = this.tree.getInsets();
         int var4;
         if (this.getExpandedIcon() != null) {
            var4 = this.getExpandedIcon().getIconWidth();
         } else {
            var4 = 8;
         }

         int var6 = this.getRowX(this.tree.getRowForPath(var1), var1.getPathCount() - 1);
         if (this.leftToRight) {
            var6 = var6 + var5.left - this.getRightChildIndent() + 1;
         } else {
            var6 = this.tree.getWidth() - var6 - var5.right + this.getRightChildIndent() - 1;
         }

         var6 = this.findCenteredX(var6, var4);
         return var2 >= var6 && var2 < var6 + var4;
      } else {
         return false;
      }
   }

   protected void handleExpandControlClick(TreePath var1, int var2, int var3) {
      this.toggleExpandState(var1);
   }

   protected void toggleExpandState(TreePath var1) {
      if (!this.tree.isExpanded(var1)) {
         int var2 = this.getRowForPath(this.tree, var1);
         this.tree.expandPath(var1);
         this.updateSize();
         if (var2 != -1) {
            if (this.tree.getScrollsOnExpand()) {
               this.ensureRowsAreVisible(var2, var2 + this.treeState.getVisibleChildCount(var1));
            } else {
               this.ensureRowsAreVisible(var2, var2);
            }
         }
      } else {
         this.tree.collapsePath(var1);
         this.updateSize();
      }

   }

   protected boolean isToggleSelectionEvent(MouseEvent var1) {
      return SwingUtilities.isLeftMouseButton(var1) && BasicGraphicsUtils.isMenuShortcutKeyDown(var1);
   }

   protected boolean isMultiSelectEvent(MouseEvent var1) {
      return SwingUtilities.isLeftMouseButton(var1) && var1.isShiftDown();
   }

   protected boolean isToggleEvent(MouseEvent var1) {
      if (!SwingUtilities.isLeftMouseButton(var1)) {
         return false;
      } else {
         int var2 = this.tree.getToggleClickCount();
         if (var2 <= 0) {
            return false;
         } else {
            return var1.getClickCount() % var2 == 0;
         }
      }
   }

   protected void selectPathForEvent(TreePath var1, MouseEvent var2) {
      if (this.isMultiSelectEvent(var2)) {
         TreePath var3 = this.getAnchorSelectionPath();
         int var4 = var3 == null ? -1 : this.getRowForPath(this.tree, var3);
         if (var4 != -1 && this.tree.getSelectionModel().getSelectionMode() != 1) {
            int var5 = this.getRowForPath(this.tree, var1);
            if (this.isToggleSelectionEvent(var2)) {
               if (this.tree.isRowSelected(var4)) {
                  this.tree.addSelectionInterval(var4, var5);
               } else {
                  this.tree.removeSelectionInterval(var4, var5);
                  this.tree.addSelectionInterval(var5, var5);
               }
            } else if (var5 < var4) {
               this.tree.setSelectionInterval(var5, var4);
            } else {
               this.tree.setSelectionInterval(var4, var5);
            }

            this.lastSelectedRow = var5;
            this.setAnchorSelectionPath(var3);
            this.setLeadSelectionPath(var1);
         } else {
            this.tree.setSelectionPath(var1);
         }
      } else if (this.isToggleSelectionEvent(var2)) {
         if (this.tree.isPathSelected(var1)) {
            this.tree.removeSelectionPath(var1);
         } else {
            this.tree.addSelectionPath(var1);
         }

         this.lastSelectedRow = this.getRowForPath(this.tree, var1);
         this.setAnchorSelectionPath(var1);
         this.setLeadSelectionPath(var1);
      } else if (SwingUtilities.isLeftMouseButton(var2)) {
         this.tree.setSelectionPath(var1);
         if (this.isToggleEvent(var2)) {
            this.toggleExpandState(var1);
         }
      }

   }

   protected boolean isLeaf(int var1) {
      TreePath var2 = this.getPathForRow(this.tree, var1);
      return var2 != null ? this.treeModel.isLeaf(var2.getLastPathComponent()) : true;
   }

   private void setAnchorSelectionPath(TreePath var1) {
      this.ignoreLAChange = true;

      try {
         this.tree.setAnchorSelectionPath(var1);
      } finally {
         this.ignoreLAChange = false;
      }

   }

   private TreePath getAnchorSelectionPath() {
      return this.tree.getAnchorSelectionPath();
   }

   private void setLeadSelectionPath(TreePath var1) {
      this.setLeadSelectionPath(var1, false);
   }

   private void setLeadSelectionPath(TreePath var1, boolean var2) {
      Rectangle var3 = var2 ? this.getPathBounds(this.tree, this.getLeadSelectionPath()) : null;
      this.ignoreLAChange = true;

      try {
         this.tree.setLeadSelectionPath(var1);
      } finally {
         this.ignoreLAChange = false;
      }

      this.leadRow = this.getRowForPath(this.tree, var1);
      if (var2) {
         if (var3 != null) {
            this.tree.repaint(this.getRepaintPathBounds(var3));
         }

         var3 = this.getPathBounds(this.tree, var1);
         if (var3 != null) {
            this.tree.repaint(this.getRepaintPathBounds(var3));
         }
      }

   }

   private Rectangle getRepaintPathBounds(Rectangle var1) {
      if (UIManager.getBoolean("Tree.repaintWholeRow")) {
         var1.x = 0;
         var1.width = this.tree.getWidth();
      }

      return var1;
   }

   private TreePath getLeadSelectionPath() {
      return this.tree.getLeadSelectionPath();
   }

   protected void updateLeadSelectionRow() {
      this.leadRow = this.getRowForPath(this.tree, this.getLeadSelectionPath());
   }

   protected int getLeadSelectionRow() {
      return this.leadRow;
   }

   private void extendSelection(TreePath var1) {
      TreePath var2 = this.getAnchorSelectionPath();
      int var3 = var2 == null ? -1 : this.getRowForPath(this.tree, var2);
      int var4 = this.getRowForPath(this.tree, var1);
      if (var3 == -1) {
         this.tree.setSelectionRow(var4);
      } else {
         if (var3 < var4) {
            this.tree.setSelectionInterval(var3, var4);
         } else {
            this.tree.setSelectionInterval(var4, var3);
         }

         this.setAnchorSelectionPath(var2);
         this.setLeadSelectionPath(var1);
      }

   }

   private void repaintPath(TreePath var1) {
      if (var1 != null) {
         Rectangle var2 = this.getPathBounds(this.tree, var1);
         if (var2 != null) {
            this.tree.repaint(var2.x, var2.y, var2.width, var2.height);
         }
      }

   }

   private static class Actions extends UIAction {
      private static final String SELECT_PREVIOUS = "selectPrevious";
      private static final String SELECT_PREVIOUS_CHANGE_LEAD = "selectPreviousChangeLead";
      private static final String SELECT_PREVIOUS_EXTEND_SELECTION = "selectPreviousExtendSelection";
      private static final String SELECT_NEXT = "selectNext";
      private static final String SELECT_NEXT_CHANGE_LEAD = "selectNextChangeLead";
      private static final String SELECT_NEXT_EXTEND_SELECTION = "selectNextExtendSelection";
      private static final String SELECT_CHILD = "selectChild";
      private static final String SELECT_CHILD_CHANGE_LEAD = "selectChildChangeLead";
      private static final String SELECT_PARENT = "selectParent";
      private static final String SELECT_PARENT_CHANGE_LEAD = "selectParentChangeLead";
      private static final String SCROLL_UP_CHANGE_SELECTION = "scrollUpChangeSelection";
      private static final String SCROLL_UP_CHANGE_LEAD = "scrollUpChangeLead";
      private static final String SCROLL_UP_EXTEND_SELECTION = "scrollUpExtendSelection";
      private static final String SCROLL_DOWN_CHANGE_SELECTION = "scrollDownChangeSelection";
      private static final String SCROLL_DOWN_EXTEND_SELECTION = "scrollDownExtendSelection";
      private static final String SCROLL_DOWN_CHANGE_LEAD = "scrollDownChangeLead";
      private static final String SELECT_FIRST = "selectFirst";
      private static final String SELECT_FIRST_CHANGE_LEAD = "selectFirstChangeLead";
      private static final String SELECT_FIRST_EXTEND_SELECTION = "selectFirstExtendSelection";
      private static final String SELECT_LAST = "selectLast";
      private static final String SELECT_LAST_CHANGE_LEAD = "selectLastChangeLead";
      private static final String SELECT_LAST_EXTEND_SELECTION = "selectLastExtendSelection";
      private static final String TOGGLE = "toggle";
      private static final String CANCEL_EDITING = "cancel";
      private static final String START_EDITING = "startEditing";
      private static final String SELECT_ALL = "selectAll";
      private static final String CLEAR_SELECTION = "clearSelection";
      private static final String SCROLL_LEFT = "scrollLeft";
      private static final String SCROLL_RIGHT = "scrollRight";
      private static final String SCROLL_LEFT_EXTEND_SELECTION = "scrollLeftExtendSelection";
      private static final String SCROLL_RIGHT_EXTEND_SELECTION = "scrollRightExtendSelection";
      private static final String SCROLL_RIGHT_CHANGE_LEAD = "scrollRightChangeLead";
      private static final String SCROLL_LEFT_CHANGE_LEAD = "scrollLeftChangeLead";
      private static final String EXPAND = "expand";
      private static final String COLLAPSE = "collapse";
      private static final String MOVE_SELECTION_TO_PARENT = "moveSelectionToParent";
      private static final String ADD_TO_SELECTION = "addToSelection";
      private static final String TOGGLE_AND_ANCHOR = "toggleAndAnchor";
      private static final String EXTEND_TO = "extendTo";
      private static final String MOVE_SELECTION_TO = "moveSelectionTo";

      Actions() {
         super((String)null);
      }

      Actions(String var1) {
         super(var1);
      }

      public boolean isEnabled(Object var1) {
         return var1 instanceof JTree && this.getName() == "cancel" ? ((JTree)var1).isEditing() : true;
      }

      public void actionPerformed(ActionEvent var1) {
         JTree var2 = (JTree)var1.getSource();
         BasicTreeUI var3 = (BasicTreeUI)BasicLookAndFeel.getUIOfType(var2.getUI(), BasicTreeUI.class);
         if (var3 != null) {
            String var4 = this.getName();
            if (var4 == "selectPrevious") {
               this.increment(var2, var3, -1, false, true);
            } else if (var4 == "selectPreviousChangeLead") {
               this.increment(var2, var3, -1, false, false);
            } else if (var4 == "selectPreviousExtendSelection") {
               this.increment(var2, var3, -1, true, true);
            } else if (var4 == "selectNext") {
               this.increment(var2, var3, 1, false, true);
            } else if (var4 == "selectNextChangeLead") {
               this.increment(var2, var3, 1, false, false);
            } else if (var4 == "selectNextExtendSelection") {
               this.increment(var2, var3, 1, true, true);
            } else if (var4 == "selectChild") {
               this.traverse(var2, var3, 1, true);
            } else if (var4 == "selectChildChangeLead") {
               this.traverse(var2, var3, 1, false);
            } else if (var4 == "selectParent") {
               this.traverse(var2, var3, -1, true);
            } else if (var4 == "selectParentChangeLead") {
               this.traverse(var2, var3, -1, false);
            } else if (var4 == "scrollUpChangeSelection") {
               this.page(var2, var3, -1, false, true);
            } else if (var4 == "scrollUpChangeLead") {
               this.page(var2, var3, -1, false, false);
            } else if (var4 == "scrollUpExtendSelection") {
               this.page(var2, var3, -1, true, true);
            } else if (var4 == "scrollDownChangeSelection") {
               this.page(var2, var3, 1, false, true);
            } else if (var4 == "scrollDownExtendSelection") {
               this.page(var2, var3, 1, true, true);
            } else if (var4 == "scrollDownChangeLead") {
               this.page(var2, var3, 1, false, false);
            } else if (var4 == "selectFirst") {
               this.home(var2, var3, -1, false, true);
            } else if (var4 == "selectFirstChangeLead") {
               this.home(var2, var3, -1, false, false);
            } else if (var4 == "selectFirstExtendSelection") {
               this.home(var2, var3, -1, true, true);
            } else if (var4 == "selectLast") {
               this.home(var2, var3, 1, false, true);
            } else if (var4 == "selectLastChangeLead") {
               this.home(var2, var3, 1, false, false);
            } else if (var4 == "selectLastExtendSelection") {
               this.home(var2, var3, 1, true, true);
            } else if (var4 == "toggle") {
               this.toggle(var2, var3);
            } else if (var4 == "cancel") {
               this.cancelEditing(var2, var3);
            } else if (var4 == "startEditing") {
               this.startEditing(var2, var3);
            } else if (var4 == "selectAll") {
               this.selectAll(var2, var3, true);
            } else if (var4 == "clearSelection") {
               this.selectAll(var2, var3, false);
            } else {
               int var5;
               TreePath var6;
               if (var4 == "addToSelection") {
                  if (var3.getRowCount(var2) > 0) {
                     var5 = var3.getLeadSelectionRow();
                     if (!var2.isRowSelected(var5)) {
                        var6 = var3.getAnchorSelectionPath();
                        var2.addSelectionRow(var5);
                        var3.setAnchorSelectionPath(var6);
                     }
                  }
               } else if (var4 == "toggleAndAnchor") {
                  if (var3.getRowCount(var2) > 0) {
                     var5 = var3.getLeadSelectionRow();
                     var6 = var3.getLeadSelectionPath();
                     if (!var2.isRowSelected(var5)) {
                        var2.addSelectionRow(var5);
                     } else {
                        var2.removeSelectionRow(var5);
                        var3.setLeadSelectionPath(var6);
                     }

                     var3.setAnchorSelectionPath(var6);
                  }
               } else if (var4 == "extendTo") {
                  this.extendSelection(var2, var3);
               } else if (var4 == "moveSelectionTo") {
                  if (var3.getRowCount(var2) > 0) {
                     var5 = var3.getLeadSelectionRow();
                     var2.setSelectionInterval(var5, var5);
                  }
               } else if (var4 == "scrollLeft") {
                  this.scroll(var2, var3, 0, -10);
               } else if (var4 == "scrollRight") {
                  this.scroll(var2, var3, 0, 10);
               } else if (var4 == "scrollLeftExtendSelection") {
                  this.scrollChangeSelection(var2, var3, -1, true, true);
               } else if (var4 == "scrollRightExtendSelection") {
                  this.scrollChangeSelection(var2, var3, 1, true, true);
               } else if (var4 == "scrollRightChangeLead") {
                  this.scrollChangeSelection(var2, var3, 1, false, false);
               } else if (var4 == "scrollLeftChangeLead") {
                  this.scrollChangeSelection(var2, var3, -1, false, false);
               } else if (var4 == "expand") {
                  this.expand(var2, var3);
               } else if (var4 == "collapse") {
                  this.collapse(var2, var3);
               } else if (var4 == "moveSelectionToParent") {
                  this.moveSelectionToParent(var2, var3);
               }
            }

         }
      }

      private void scrollChangeSelection(JTree var1, BasicTreeUI var2, int var3, boolean var4, boolean var5) {
         if (var2.getRowCount(var1) > 0 && var2.treeSelectionModel != null) {
            Rectangle var8 = var1.getVisibleRect();
            TreePath var7;
            if (var3 == -1) {
               var7 = var2.getClosestPathForLocation(var1, var8.x, var8.y);
               var8.x = Math.max(0, var8.x - var8.width);
            } else {
               var8.x = Math.min(Math.max(0, var1.getWidth() - var8.width), var8.x + var8.width);
               var7 = var2.getClosestPathForLocation(var1, var8.x, var8.y + var8.height);
            }

            var1.scrollRectToVisible(var8);
            if (var4) {
               var2.extendSelection(var7);
            } else if (var5) {
               var1.setSelectionPath(var7);
            } else {
               var2.setLeadSelectionPath(var7, true);
            }
         }

      }

      private void scroll(JTree var1, BasicTreeUI var2, int var3, int var4) {
         Rectangle var5 = var1.getVisibleRect();
         Dimension var6 = var1.getSize();
         if (var3 == 0) {
            var5.x += var4;
            var5.x = Math.max(0, var5.x);
            var5.x = Math.min(Math.max(0, var6.width - var5.width), var5.x);
         } else {
            var5.y += var4;
            var5.y = Math.max(0, var5.y);
            var5.y = Math.min(Math.max(0, var6.width - var5.height), var5.y);
         }

         var1.scrollRectToVisible(var5);
      }

      private void extendSelection(JTree var1, BasicTreeUI var2) {
         if (var2.getRowCount(var1) > 0) {
            int var3 = var2.getLeadSelectionRow();
            if (var3 != -1) {
               TreePath var4 = var2.getLeadSelectionPath();
               TreePath var5 = var2.getAnchorSelectionPath();
               int var6 = var2.getRowForPath(var1, var5);
               if (var6 == -1) {
                  var6 = 0;
               }

               var1.setSelectionInterval(var6, var3);
               var2.setLeadSelectionPath(var4);
               var2.setAnchorSelectionPath(var5);
            }
         }

      }

      private void selectAll(JTree var1, BasicTreeUI var2, boolean var3) {
         int var4 = var2.getRowCount(var1);
         if (var4 > 0) {
            TreePath var5;
            TreePath var6;
            if (var3) {
               if (var1.getSelectionModel().getSelectionMode() == 1) {
                  int var7 = var2.getLeadSelectionRow();
                  if (var7 != -1) {
                     var1.setSelectionRow(var7);
                  } else if (var1.getMinSelectionRow() == -1) {
                     var1.setSelectionRow(0);
                     var2.ensureRowsAreVisible(0, 0);
                  }

                  return;
               }

               var5 = var2.getLeadSelectionPath();
               var6 = var2.getAnchorSelectionPath();
               if (var5 != null && !var1.isVisible(var5)) {
                  var5 = null;
               }

               var1.setSelectionInterval(0, var4 - 1);
               if (var5 != null) {
                  var2.setLeadSelectionPath(var5);
               }

               if (var6 != null && var1.isVisible(var6)) {
                  var2.setAnchorSelectionPath(var6);
               }
            } else {
               var5 = var2.getLeadSelectionPath();
               var6 = var2.getAnchorSelectionPath();
               var1.clearSelection();
               var2.setAnchorSelectionPath(var6);
               var2.setLeadSelectionPath(var5);
            }
         }

      }

      private void startEditing(JTree var1, BasicTreeUI var2) {
         TreePath var3 = var2.getLeadSelectionPath();
         int var4 = var3 != null ? var2.getRowForPath(var1, var3) : -1;
         if (var4 != -1) {
            var1.startEditingAtPath(var3);
         }

      }

      private void cancelEditing(JTree var1, BasicTreeUI var2) {
         var1.cancelEditing();
      }

      private void toggle(JTree var1, BasicTreeUI var2) {
         int var3 = var2.getLeadSelectionRow();
         if (var3 != -1 && !var2.isLeaf(var3)) {
            TreePath var4 = var2.getAnchorSelectionPath();
            TreePath var5 = var2.getLeadSelectionPath();
            var2.toggleExpandState(var2.getPathForRow(var1, var3));
            var2.setAnchorSelectionPath(var4);
            var2.setLeadSelectionPath(var5);
         }

      }

      private void expand(JTree var1, BasicTreeUI var2) {
         int var3 = var2.getLeadSelectionRow();
         var1.expandRow(var3);
      }

      private void collapse(JTree var1, BasicTreeUI var2) {
         int var3 = var2.getLeadSelectionRow();
         var1.collapseRow(var3);
      }

      private void increment(JTree var1, BasicTreeUI var2, int var3, boolean var4, boolean var5) {
         if (!var4 && !var5 && var1.getSelectionModel().getSelectionMode() != 4) {
            var5 = true;
         }

         int var6;
         if (var2.treeSelectionModel != null && (var6 = var1.getRowCount()) > 0) {
            int var7 = var2.getLeadSelectionRow();
            int var8;
            if (var7 == -1) {
               if (var3 == 1) {
                  var8 = 0;
               } else {
                  var8 = var6 - 1;
               }
            } else {
               var8 = Math.min(var6 - 1, Math.max(0, var7 + var3));
            }

            if (var4 && var2.treeSelectionModel.getSelectionMode() != 1) {
               var2.extendSelection(var1.getPathForRow(var8));
            } else if (var5) {
               var1.setSelectionInterval(var8, var8);
            } else {
               var2.setLeadSelectionPath(var1.getPathForRow(var8), true);
            }

            var2.ensureRowsAreVisible(var8, var8);
            var2.lastSelectedRow = var8;
         }

      }

      private void traverse(JTree var1, BasicTreeUI var2, int var3, boolean var4) {
         if (!var4 && var1.getSelectionModel().getSelectionMode() != 4) {
            var4 = true;
         }

         int var5;
         if ((var5 = var1.getRowCount()) > 0) {
            int var6 = var2.getLeadSelectionRow();
            int var7;
            if (var6 == -1) {
               var7 = 0;
            } else {
               TreePath var8;
               if (var3 == 1) {
                  var8 = var2.getPathForRow(var1, var6);
                  int var9 = var1.getModel().getChildCount(var8.getLastPathComponent());
                  var7 = -1;
                  if (!var2.isLeaf(var6)) {
                     if (!var1.isExpanded(var6)) {
                        var2.toggleExpandState(var8);
                     } else if (var9 > 0) {
                        var7 = Math.min(var6 + 1, var5 - 1);
                     }
                  }
               } else if (!var2.isLeaf(var6) && var1.isExpanded(var6)) {
                  var2.toggleExpandState(var2.getPathForRow(var1, var6));
                  var7 = -1;
               } else {
                  var8 = var2.getPathForRow(var1, var6);
                  if (var8 != null && var8.getPathCount() > 1) {
                     var7 = var2.getRowForPath(var1, var8.getParentPath());
                  } else {
                     var7 = -1;
                  }
               }
            }

            if (var7 != -1) {
               if (var4) {
                  var1.setSelectionInterval(var7, var7);
               } else {
                  var2.setLeadSelectionPath(var2.getPathForRow(var1, var7), true);
               }

               var2.ensureRowsAreVisible(var7, var7);
            }
         }

      }

      private void moveSelectionToParent(JTree var1, BasicTreeUI var2) {
         int var3 = var2.getLeadSelectionRow();
         TreePath var4 = var2.getPathForRow(var1, var3);
         if (var4 != null && var4.getPathCount() > 1) {
            int var5 = var2.getRowForPath(var1, var4.getParentPath());
            if (var5 != -1) {
               var1.setSelectionInterval(var5, var5);
               var2.ensureRowsAreVisible(var5, var5);
            }
         }

      }

      private void page(JTree var1, BasicTreeUI var2, int var3, boolean var4, boolean var5) {
         if (!var4 && !var5 && var1.getSelectionModel().getSelectionMode() != 4) {
            var5 = true;
         }

         if (var2.getRowCount(var1) > 0 && var2.treeSelectionModel != null) {
            Dimension var7 = var1.getSize();
            TreePath var8 = var2.getLeadSelectionPath();
            Rectangle var10 = var1.getVisibleRect();
            TreePath var9;
            if (var3 == -1) {
               var9 = var2.getClosestPathForLocation(var1, var10.x, var10.y);
               if (var9.equals(var8)) {
                  var10.y = Math.max(0, var10.y - var10.height);
                  var9 = var1.getClosestPathForLocation(var10.x, var10.y);
               }
            } else {
               var10.y = Math.min(var7.height, var10.y + var10.height - 1);
               var9 = var1.getClosestPathForLocation(var10.x, var10.y);
               if (var9.equals(var8)) {
                  var10.y = Math.min(var7.height, var10.y + var10.height - 1);
                  var9 = var1.getClosestPathForLocation(var10.x, var10.y);
               }
            }

            Rectangle var11 = var2.getPathBounds(var1, var9);
            if (var11 != null) {
               var11.x = var10.x;
               var11.width = var10.width;
               if (var3 == -1) {
                  var11.height = var10.height;
               } else {
                  var11.y -= var10.height - var11.height;
                  var11.height = var10.height;
               }

               if (var4) {
                  var2.extendSelection(var9);
               } else if (var5) {
                  var1.setSelectionPath(var9);
               } else {
                  var2.setLeadSelectionPath(var9, true);
               }

               var1.scrollRectToVisible(var11);
            }
         }

      }

      private void home(JTree var1, final BasicTreeUI var2, int var3, boolean var4, boolean var5) {
         if (!var4 && !var5 && var1.getSelectionModel().getSelectionMode() != 4) {
            var5 = true;
         }

         final int var6 = var2.getRowCount(var1);
         if (var6 > 0) {
            TreePath var7;
            int var8;
            if (var3 == -1) {
               var2.ensureRowsAreVisible(0, 0);
               if (var4) {
                  var7 = var2.getAnchorSelectionPath();
                  var8 = var7 == null ? -1 : var2.getRowForPath(var1, var7);
                  if (var8 == -1) {
                     var1.setSelectionInterval(0, 0);
                  } else {
                     var1.setSelectionInterval(0, var8);
                     var2.setAnchorSelectionPath(var7);
                     var2.setLeadSelectionPath(var2.getPathForRow(var1, 0));
                  }
               } else if (var5) {
                  var1.setSelectionInterval(0, 0);
               } else {
                  var2.setLeadSelectionPath(var2.getPathForRow(var1, 0), true);
               }
            } else {
               var2.ensureRowsAreVisible(var6 - 1, var6 - 1);
               if (var4) {
                  var7 = var2.getAnchorSelectionPath();
                  var8 = var7 == null ? -1 : var2.getRowForPath(var1, var7);
                  if (var8 == -1) {
                     var1.setSelectionInterval(var6 - 1, var6 - 1);
                  } else {
                     var1.setSelectionInterval(var8, var6 - 1);
                     var2.setAnchorSelectionPath(var7);
                     var2.setLeadSelectionPath(var2.getPathForRow(var1, var6 - 1));
                  }
               } else if (var5) {
                  var1.setSelectionInterval(var6 - 1, var6 - 1);
               } else {
                  var2.setLeadSelectionPath(var2.getPathForRow(var1, var6 - 1), true);
               }

               if (var2.isLargeModel()) {
                  SwingUtilities.invokeLater(new Runnable() {
                     public void run() {
                        var2.ensureRowsAreVisible(var6 - 1, var6 - 1);
                     }
                  });
               }
            }
         }

      }
   }

   private class Handler implements CellEditorListener, FocusListener, KeyListener, MouseListener, MouseMotionListener, PropertyChangeListener, TreeExpansionListener, TreeModelListener, TreeSelectionListener, DragRecognitionSupport.BeforeDrag {
      private String prefix;
      private String typedString;
      private long lastTime;
      private boolean dragPressDidSelection;
      private boolean dragStarted;
      private TreePath pressedPath;
      private MouseEvent pressedEvent;
      private boolean valueChangedOnPress;

      private Handler() {
         this.prefix = "";
         this.typedString = "";
         this.lastTime = 0L;
      }

      public void keyTyped(KeyEvent var1) {
         if (BasicTreeUI.this.tree != null && BasicTreeUI.this.tree.getRowCount() > 0 && BasicTreeUI.this.tree.hasFocus() && BasicTreeUI.this.tree.isEnabled()) {
            if (var1.isAltDown() || BasicGraphicsUtils.isMenuShortcutKeyDown(var1) || this.isNavigationKey(var1)) {
               return;
            }

            boolean var2 = true;
            char var3 = var1.getKeyChar();
            long var4 = var1.getWhen();
            int var6 = BasicTreeUI.this.tree.getLeadSelectionRow();
            if (var4 - this.lastTime < BasicTreeUI.this.timeFactor) {
               this.typedString = this.typedString + var3;
               if (this.prefix.length() == 1 && var3 == this.prefix.charAt(0)) {
                  ++var6;
               } else {
                  this.prefix = this.typedString;
               }
            } else {
               ++var6;
               this.typedString = "" + var3;
               this.prefix = this.typedString;
            }

            this.lastTime = var4;
            if (var6 < 0 || var6 >= BasicTreeUI.this.tree.getRowCount()) {
               var2 = false;
               var6 = 0;
            }

            TreePath var7 = BasicTreeUI.this.tree.getNextMatch(this.prefix, var6, Position.Bias.Forward);
            int var8;
            if (var7 != null) {
               BasicTreeUI.this.tree.setSelectionPath(var7);
               var8 = BasicTreeUI.this.getRowForPath(BasicTreeUI.this.tree, var7);
               BasicTreeUI.this.ensureRowsAreVisible(var8, var8);
            } else if (var2) {
               var7 = BasicTreeUI.this.tree.getNextMatch(this.prefix, 0, Position.Bias.Forward);
               if (var7 != null) {
                  BasicTreeUI.this.tree.setSelectionPath(var7);
                  var8 = BasicTreeUI.this.getRowForPath(BasicTreeUI.this.tree, var7);
                  BasicTreeUI.this.ensureRowsAreVisible(var8, var8);
               }
            }
         }

      }

      public void keyPressed(KeyEvent var1) {
         if (BasicTreeUI.this.tree != null && this.isNavigationKey(var1)) {
            this.prefix = "";
            this.typedString = "";
            this.lastTime = 0L;
         }

      }

      public void keyReleased(KeyEvent var1) {
      }

      private boolean isNavigationKey(KeyEvent var1) {
         InputMap var2 = BasicTreeUI.this.tree.getInputMap(1);
         KeyStroke var3 = KeyStroke.getKeyStrokeForEvent(var1);
         return var2 != null && var2.get(var3) != null;
      }

      public void propertyChange(PropertyChangeEvent var1) {
         if (var1.getSource() == BasicTreeUI.this.treeSelectionModel) {
            BasicTreeUI.this.treeSelectionModel.resetRowSelection();
         } else if (var1.getSource() == BasicTreeUI.this.tree) {
            String var2 = var1.getPropertyName();
            if (var2 == "leadSelectionPath") {
               if (!BasicTreeUI.this.ignoreLAChange) {
                  BasicTreeUI.this.updateLeadSelectionRow();
                  BasicTreeUI.this.repaintPath((TreePath)var1.getOldValue());
                  BasicTreeUI.this.repaintPath((TreePath)var1.getNewValue());
               }
            } else if (var2 == "anchorSelectionPath" && !BasicTreeUI.this.ignoreLAChange) {
               BasicTreeUI.this.repaintPath((TreePath)var1.getOldValue());
               BasicTreeUI.this.repaintPath((TreePath)var1.getNewValue());
            }

            if (var2 == "cellRenderer") {
               BasicTreeUI.this.setCellRenderer((TreeCellRenderer)var1.getNewValue());
               BasicTreeUI.this.redoTheLayout();
            } else if (var2 == "model") {
               BasicTreeUI.this.setModel((TreeModel)var1.getNewValue());
            } else if (var2 == "rootVisible") {
               BasicTreeUI.this.setRootVisible((Boolean)var1.getNewValue());
            } else if (var2 == "showsRootHandles") {
               BasicTreeUI.this.setShowsRootHandles((Boolean)var1.getNewValue());
            } else if (var2 == "rowHeight") {
               BasicTreeUI.this.setRowHeight((Integer)var1.getNewValue());
            } else if (var2 == "cellEditor") {
               BasicTreeUI.this.setCellEditor((TreeCellEditor)var1.getNewValue());
            } else if (var2 == "editable") {
               BasicTreeUI.this.setEditable((Boolean)var1.getNewValue());
            } else if (var2 == "largeModel") {
               BasicTreeUI.this.setLargeModel(BasicTreeUI.this.tree.isLargeModel());
            } else if (var2 == "selectionModel") {
               BasicTreeUI.this.setSelectionModel(BasicTreeUI.this.tree.getSelectionModel());
            } else if (var2 == "font") {
               BasicTreeUI.this.completeEditing();
               if (BasicTreeUI.this.treeState != null) {
                  BasicTreeUI.this.treeState.invalidateSizes();
               }

               BasicTreeUI.this.updateSize();
            } else if (var2 == "componentOrientation") {
               if (BasicTreeUI.this.tree != null) {
                  BasicTreeUI.this.leftToRight = BasicGraphicsUtils.isLeftToRight(BasicTreeUI.this.tree);
                  BasicTreeUI.this.redoTheLayout();
                  BasicTreeUI.this.tree.treeDidChange();
                  InputMap var3 = BasicTreeUI.this.getInputMap(0);
                  SwingUtilities.replaceUIInputMap(BasicTreeUI.this.tree, 0, var3);
               }
            } else if ("dropLocation" == var2) {
               JTree.DropLocation var4 = (JTree.DropLocation)var1.getOldValue();
               this.repaintDropLocation(var4);
               this.repaintDropLocation(BasicTreeUI.this.tree.getDropLocation());
            }
         }

      }

      private void repaintDropLocation(JTree.DropLocation var1) {
         if (var1 != null) {
            Rectangle var2;
            if (BasicTreeUI.this.isDropLine(var1)) {
               var2 = BasicTreeUI.this.getDropLineRect(var1);
            } else {
               var2 = BasicTreeUI.this.tree.getPathBounds(var1.getPath());
            }

            if (var2 != null) {
               BasicTreeUI.this.tree.repaint(var2);
            }

         }
      }

      private boolean isActualPath(TreePath var1, int var2, int var3) {
         if (var1 == null) {
            return false;
         } else {
            Rectangle var4 = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, var1);
            if (var4 != null && var3 <= var4.y + var4.height) {
               return var2 >= var4.x && var2 <= var4.x + var4.width;
            } else {
               return false;
            }
         }
      }

      public void mouseClicked(MouseEvent var1) {
      }

      public void mouseEntered(MouseEvent var1) {
      }

      public void mouseExited(MouseEvent var1) {
      }

      public void mousePressed(MouseEvent var1) {
         if (!SwingUtilities2.shouldIgnore(var1, BasicTreeUI.this.tree)) {
            if (!BasicTreeUI.this.isEditing(BasicTreeUI.this.tree) || !BasicTreeUI.this.tree.getInvokesStopCellEditing() || BasicTreeUI.this.stopEditing(BasicTreeUI.this.tree)) {
               BasicTreeUI.this.completeEditing();
               this.pressedPath = BasicTreeUI.this.getClosestPathForLocation(BasicTreeUI.this.tree, var1.getX(), var1.getY());
               if (BasicTreeUI.this.tree.getDragEnabled()) {
                  this.mousePressedDND(var1);
               } else {
                  SwingUtilities2.adjustFocus(BasicTreeUI.this.tree);
                  this.handleSelection(var1);
               }

            }
         }
      }

      private void mousePressedDND(MouseEvent var1) {
         this.pressedEvent = var1;
         boolean var2 = true;
         this.dragStarted = false;
         this.valueChangedOnPress = false;
         if (this.isActualPath(this.pressedPath, var1.getX(), var1.getY()) && DragRecognitionSupport.mousePressed(var1)) {
            this.dragPressDidSelection = false;
            if (BasicGraphicsUtils.isMenuShortcutKeyDown(var1)) {
               return;
            }

            if (!var1.isShiftDown() && BasicTreeUI.this.tree.isPathSelected(this.pressedPath)) {
               BasicTreeUI.this.setAnchorSelectionPath(this.pressedPath);
               BasicTreeUI.this.setLeadSelectionPath(this.pressedPath, true);
               return;
            }

            this.dragPressDidSelection = true;
            var2 = false;
         }

         if (var2) {
            SwingUtilities2.adjustFocus(BasicTreeUI.this.tree);
         }

         this.handleSelection(var1);
      }

      void handleSelection(MouseEvent var1) {
         if (this.pressedPath != null) {
            Rectangle var2 = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, this.pressedPath);
            if (var2 == null || var1.getY() >= var2.y + var2.height) {
               return;
            }

            if (SwingUtilities.isLeftMouseButton(var1)) {
               BasicTreeUI.this.checkForClickInExpandControl(this.pressedPath, var1.getX(), var1.getY());
            }

            int var3 = var1.getX();
            if (var3 >= var2.x && var3 < var2.x + var2.width && (BasicTreeUI.this.tree.getDragEnabled() || !BasicTreeUI.this.startEditing(this.pressedPath, var1))) {
               BasicTreeUI.this.selectPathForEvent(this.pressedPath, var1);
            }
         }

      }

      public void dragStarting(MouseEvent var1) {
         this.dragStarted = true;
         if (BasicGraphicsUtils.isMenuShortcutKeyDown(var1)) {
            BasicTreeUI.this.tree.addSelectionPath(this.pressedPath);
            BasicTreeUI.this.setAnchorSelectionPath(this.pressedPath);
            BasicTreeUI.this.setLeadSelectionPath(this.pressedPath, true);
         }

         this.pressedEvent = null;
         this.pressedPath = null;
      }

      public void mouseDragged(MouseEvent var1) {
         if (!SwingUtilities2.shouldIgnore(var1, BasicTreeUI.this.tree)) {
            if (BasicTreeUI.this.tree.getDragEnabled()) {
               DragRecognitionSupport.mouseDragged(var1, this);
            }

         }
      }

      public void mouseMoved(MouseEvent var1) {
      }

      public void mouseReleased(MouseEvent var1) {
         if (!SwingUtilities2.shouldIgnore(var1, BasicTreeUI.this.tree)) {
            if (BasicTreeUI.this.tree.getDragEnabled()) {
               this.mouseReleasedDND(var1);
            }

            this.pressedEvent = null;
            this.pressedPath = null;
         }
      }

      private void mouseReleasedDND(MouseEvent var1) {
         MouseEvent var2 = DragRecognitionSupport.mouseReleased(var1);
         if (var2 != null) {
            SwingUtilities2.adjustFocus(BasicTreeUI.this.tree);
            if (!this.dragPressDidSelection) {
               this.handleSelection(var2);
            }
         }

         if (!this.dragStarted && this.pressedPath != null && !this.valueChangedOnPress && this.isActualPath(this.pressedPath, this.pressedEvent.getX(), this.pressedEvent.getY())) {
            BasicTreeUI.this.startEditingOnRelease(this.pressedPath, this.pressedEvent, var1);
         }

      }

      public void focusGained(FocusEvent var1) {
         if (BasicTreeUI.this.tree != null) {
            Rectangle var2 = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, BasicTreeUI.this.tree.getLeadSelectionPath());
            if (var2 != null) {
               BasicTreeUI.this.tree.repaint(BasicTreeUI.this.getRepaintPathBounds(var2));
            }

            var2 = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, BasicTreeUI.this.getLeadSelectionPath());
            if (var2 != null) {
               BasicTreeUI.this.tree.repaint(BasicTreeUI.this.getRepaintPathBounds(var2));
            }
         }

      }

      public void focusLost(FocusEvent var1) {
         this.focusGained(var1);
      }

      public void editingStopped(ChangeEvent var1) {
         BasicTreeUI.this.completeEditing(false, false, true);
      }

      public void editingCanceled(ChangeEvent var1) {
         BasicTreeUI.this.completeEditing(false, false, false);
      }

      public void valueChanged(TreeSelectionEvent var1) {
         this.valueChangedOnPress = true;
         BasicTreeUI.this.completeEditing();
         if (BasicTreeUI.this.tree.getExpandsSelectedPaths() && BasicTreeUI.this.treeSelectionModel != null) {
            TreePath[] var2 = BasicTreeUI.this.treeSelectionModel.getSelectionPaths();
            if (var2 != null) {
               for(int var3 = var2.length - 1; var3 >= 0; --var3) {
                  TreePath var4 = var2[var3].getParentPath();
                  boolean var5 = true;

                  while(var4 != null) {
                     if (BasicTreeUI.this.treeModel.isLeaf(var4.getLastPathComponent())) {
                        var5 = false;
                        var4 = null;
                     } else {
                        var4 = var4.getParentPath();
                     }
                  }

                  if (var5) {
                     BasicTreeUI.this.tree.makeVisible(var2[var3]);
                  }
               }
            }
         }

         TreePath var11 = BasicTreeUI.this.getLeadSelectionPath();
         BasicTreeUI.this.lastSelectedRow = BasicTreeUI.this.tree.getMinSelectionRow();
         TreePath var12 = BasicTreeUI.this.tree.getSelectionModel().getLeadSelectionPath();
         BasicTreeUI.this.setAnchorSelectionPath(var12);
         BasicTreeUI.this.setLeadSelectionPath(var12);
         TreePath[] var13 = var1.getPaths();
         Rectangle var6 = BasicTreeUI.this.tree.getVisibleRect();
         boolean var7 = true;
         int var8 = BasicTreeUI.this.tree.getWidth();
         Rectangle var14;
         if (var13 != null) {
            int var10 = var13.length;
            if (var10 > 4) {
               BasicTreeUI.this.tree.repaint();
               var7 = false;
            } else {
               for(int var9 = 0; var9 < var10; ++var9) {
                  var14 = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, var13[var9]);
                  if (var14 != null && var6.intersects(var14)) {
                     BasicTreeUI.this.tree.repaint(0, var14.y, var8, var14.height);
                  }
               }
            }
         }

         if (var7) {
            var14 = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, var11);
            if (var14 != null && var6.intersects(var14)) {
               BasicTreeUI.this.tree.repaint(0, var14.y, var8, var14.height);
            }

            var14 = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, var12);
            if (var14 != null && var6.intersects(var14)) {
               BasicTreeUI.this.tree.repaint(0, var14.y, var8, var14.height);
            }
         }

      }

      public void treeExpanded(TreeExpansionEvent var1) {
         if (var1 != null && BasicTreeUI.this.tree != null) {
            TreePath var2 = var1.getPath();
            BasicTreeUI.this.updateExpandedDescendants(var2);
         }

      }

      public void treeCollapsed(TreeExpansionEvent var1) {
         if (var1 != null && BasicTreeUI.this.tree != null) {
            TreePath var2 = var1.getPath();
            BasicTreeUI.this.completeEditing();
            if (var2 != null && BasicTreeUI.this.tree.isVisible(var2)) {
               BasicTreeUI.this.treeState.setExpandedState(var2, false);
               BasicTreeUI.this.updateLeadSelectionRow();
               BasicTreeUI.this.updateSize();
            }
         }

      }

      public void treeNodesChanged(TreeModelEvent var1) {
         if (BasicTreeUI.this.treeState != null && var1 != null) {
            TreePath var2 = SwingUtilities2.getTreePath(var1, BasicTreeUI.this.getModel());
            int[] var3 = var1.getChildIndices();
            if (var3 != null && var3.length != 0) {
               if (!BasicTreeUI.this.treeState.isExpanded(var2)) {
                  BasicTreeUI.this.treeState.treeNodesChanged(var1);
               } else {
                  int var4 = var3[0];

                  for(int var5 = var3.length - 1; var5 > 0; --var5) {
                     var4 = Math.min(var3[var5], var4);
                  }

                  Object var9 = BasicTreeUI.this.treeModel.getChild(var2.getLastPathComponent(), var4);
                  TreePath var6 = var2.pathByAddingChild(var9);
                  Rectangle var7 = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, var6);
                  BasicTreeUI.this.treeState.treeNodesChanged(var1);
                  BasicTreeUI.this.updateSize0();
                  Rectangle var8 = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, var6);
                  if (var7 == null || var8 == null) {
                     return;
                  }

                  if (var3.length == 1 && var8.height == var7.height) {
                     BasicTreeUI.this.tree.repaint(0, var7.y, BasicTreeUI.this.tree.getWidth(), var7.height);
                  } else {
                     BasicTreeUI.this.tree.repaint(0, var7.y, BasicTreeUI.this.tree.getWidth(), BasicTreeUI.this.tree.getHeight() - var7.y);
                  }
               }
            } else {
               BasicTreeUI.this.treeState.treeNodesChanged(var1);
               BasicTreeUI.this.updateSize();
            }
         }

      }

      public void treeNodesInserted(TreeModelEvent var1) {
         if (BasicTreeUI.this.treeState != null && var1 != null) {
            BasicTreeUI.this.treeState.treeNodesInserted(var1);
            BasicTreeUI.this.updateLeadSelectionRow();
            TreePath var2 = SwingUtilities2.getTreePath(var1, BasicTreeUI.this.getModel());
            if (BasicTreeUI.this.treeState.isExpanded(var2)) {
               BasicTreeUI.this.updateSize();
            } else {
               int[] var3 = var1.getChildIndices();
               int var4 = BasicTreeUI.this.treeModel.getChildCount(var2.getLastPathComponent());
               if (var3 != null && var4 - var3.length == 0) {
                  BasicTreeUI.this.updateSize();
               }
            }
         }

      }

      public void treeNodesRemoved(TreeModelEvent var1) {
         if (BasicTreeUI.this.treeState != null && var1 != null) {
            BasicTreeUI.this.treeState.treeNodesRemoved(var1);
            BasicTreeUI.this.updateLeadSelectionRow();
            TreePath var2 = SwingUtilities2.getTreePath(var1, BasicTreeUI.this.getModel());
            if (BasicTreeUI.this.treeState.isExpanded(var2) || BasicTreeUI.this.treeModel.getChildCount(var2.getLastPathComponent()) == 0) {
               BasicTreeUI.this.updateSize();
            }
         }

      }

      public void treeStructureChanged(TreeModelEvent var1) {
         if (BasicTreeUI.this.treeState != null && var1 != null) {
            BasicTreeUI.this.treeState.treeStructureChanged(var1);
            BasicTreeUI.this.updateLeadSelectionRow();
            TreePath var2 = SwingUtilities2.getTreePath(var1, BasicTreeUI.this.getModel());
            if (var2 != null) {
               var2 = var2.getParentPath();
            }

            if (var2 == null || BasicTreeUI.this.treeState.isExpanded(var2)) {
               BasicTreeUI.this.updateSize();
            }
         }

      }

      // $FF: synthetic method
      Handler(Object var2) {
         this();
      }
   }

   static class TreeTransferHandler extends TransferHandler implements UIResource, Comparator<TreePath> {
      private JTree tree;

      protected Transferable createTransferable(JComponent var1) {
         if (!(var1 instanceof JTree)) {
            return null;
         } else {
            this.tree = (JTree)var1;
            TreePath[] var2 = this.tree.getSelectionPaths();
            if (var2 != null && var2.length != 0) {
               StringBuffer var3 = new StringBuffer();
               StringBuffer var4 = new StringBuffer();
               var4.append("<html>\n<body>\n<ul>\n");
               TreeModel var5 = this.tree.getModel();
               Object var6 = null;
               TreePath[] var7 = this.getDisplayOrderPaths(var2);
               TreePath[] var8 = var7;
               int var9 = var7.length;

               for(int var10 = 0; var10 < var9; ++var10) {
                  TreePath var11 = var8[var10];
                  Object var12 = var11.getLastPathComponent();
                  boolean var13 = var5.isLeaf(var12);
                  String var14 = this.getDisplayString(var11, true, var13);
                  var3.append(var14 + "\n");
                  var4.append("  <li>" + var14 + "\n");
               }

               var3.deleteCharAt(var3.length() - 1);
               var4.append("</ul>\n</body>\n</html>");
               this.tree = null;
               return new BasicTransferable(var3.toString(), var4.toString());
            } else {
               return null;
            }
         }
      }

      public int compare(TreePath var1, TreePath var2) {
         int var3 = this.tree.getRowForPath(var1);
         int var4 = this.tree.getRowForPath(var2);
         return var3 - var4;
      }

      String getDisplayString(TreePath var1, boolean var2, boolean var3) {
         int var4 = this.tree.getRowForPath(var1);
         boolean var5 = this.tree.getLeadSelectionRow() == var4;
         Object var6 = var1.getLastPathComponent();
         return this.tree.convertValueToText(var6, var2, this.tree.isExpanded(var4), var3, var4, var5);
      }

      TreePath[] getDisplayOrderPaths(TreePath[] var1) {
         ArrayList var2 = new ArrayList();
         TreePath[] var3 = var1;
         int var4 = var1.length;

         int var5;
         for(var5 = 0; var5 < var4; ++var5) {
            TreePath var6 = var3[var5];
            var2.add(var6);
         }

         Collections.sort(var2, this);
         int var7 = var2.size();
         TreePath[] var8 = new TreePath[var7];

         for(var5 = 0; var5 < var7; ++var5) {
            var8[var5] = (TreePath)var2.get(var5);
         }

         return var8;
      }

      public int getSourceActions(JComponent var1) {
         return 1;
      }
   }

   public class MouseInputHandler implements MouseInputListener {
      protected Component source;
      protected Component destination;
      private Component focusComponent;
      private boolean dispatchedEvent;

      public MouseInputHandler(Component var2, Component var3, MouseEvent var4) {
         this(var2, var3, var4, (Component)null);
      }

      MouseInputHandler(Component var2, Component var3, MouseEvent var4, Component var5) {
         this.source = var2;
         this.destination = var3;
         this.source.addMouseListener(this);
         this.source.addMouseMotionListener(this);
         SwingUtilities2.setSkipClickCount(var3, var4.getClickCount() - 1);
         var3.dispatchEvent(SwingUtilities.convertMouseEvent(var2, var4, var3));
         this.focusComponent = var5;
      }

      public void mouseClicked(MouseEvent var1) {
         if (this.destination != null) {
            this.dispatchedEvent = true;
            this.destination.dispatchEvent(SwingUtilities.convertMouseEvent(this.source, var1, this.destination));
         }

      }

      public void mousePressed(MouseEvent var1) {
      }

      public void mouseReleased(MouseEvent var1) {
         if (this.destination != null) {
            this.destination.dispatchEvent(SwingUtilities.convertMouseEvent(this.source, var1, this.destination));
         }

         this.removeFromSource();
      }

      public void mouseEntered(MouseEvent var1) {
         if (!SwingUtilities.isLeftMouseButton(var1)) {
            this.removeFromSource();
         }

      }

      public void mouseExited(MouseEvent var1) {
         if (!SwingUtilities.isLeftMouseButton(var1)) {
            this.removeFromSource();
         }

      }

      public void mouseDragged(MouseEvent var1) {
         if (this.destination != null) {
            this.dispatchedEvent = true;
            this.destination.dispatchEvent(SwingUtilities.convertMouseEvent(this.source, var1, this.destination));
         }

      }

      public void mouseMoved(MouseEvent var1) {
         this.removeFromSource();
      }

      protected void removeFromSource() {
         if (this.source != null) {
            this.source.removeMouseListener(this);
            this.source.removeMouseMotionListener(this);
            if (this.focusComponent != null && this.focusComponent == this.destination && !this.dispatchedEvent && this.focusComponent instanceof JTextField) {
               ((JTextField)this.focusComponent).selectAll();
            }
         }

         this.source = this.destination = null;
      }
   }

   public class TreeCancelEditingAction extends AbstractAction {
      public TreeCancelEditingAction(String var2) {
      }

      public void actionPerformed(ActionEvent var1) {
         if (BasicTreeUI.this.tree != null) {
            BasicTreeUI.SHARED_ACTION.cancelEditing(BasicTreeUI.this.tree, BasicTreeUI.this);
         }

      }

      public boolean isEnabled() {
         return BasicTreeUI.this.tree != null && BasicTreeUI.this.tree.isEnabled() && BasicTreeUI.this.isEditing(BasicTreeUI.this.tree);
      }
   }

   public class TreeToggleAction extends AbstractAction {
      public TreeToggleAction(String var2) {
      }

      public void actionPerformed(ActionEvent var1) {
         if (BasicTreeUI.this.tree != null) {
            BasicTreeUI.SHARED_ACTION.toggle(BasicTreeUI.this.tree, BasicTreeUI.this);
         }

      }

      public boolean isEnabled() {
         return BasicTreeUI.this.tree != null && BasicTreeUI.this.tree.isEnabled();
      }
   }

   public class TreeHomeAction extends AbstractAction {
      protected int direction;
      private boolean addToSelection;
      private boolean changeSelection;

      public TreeHomeAction(int var2, String var3) {
         this(var2, var3, false, true);
      }

      private TreeHomeAction(int var2, String var3, boolean var4, boolean var5) {
         this.direction = var2;
         this.changeSelection = var5;
         this.addToSelection = var4;
      }

      public void actionPerformed(ActionEvent var1) {
         if (BasicTreeUI.this.tree != null) {
            BasicTreeUI.SHARED_ACTION.home(BasicTreeUI.this.tree, BasicTreeUI.this, this.direction, this.addToSelection, this.changeSelection);
         }

      }

      public boolean isEnabled() {
         return BasicTreeUI.this.tree != null && BasicTreeUI.this.tree.isEnabled();
      }
   }

   public class TreeIncrementAction extends AbstractAction {
      protected int direction;
      private boolean addToSelection;
      private boolean changeSelection;

      public TreeIncrementAction(int var2, String var3) {
         this(var2, var3, false, true);
      }

      private TreeIncrementAction(int var2, String var3, boolean var4, boolean var5) {
         this.direction = var2;
         this.addToSelection = var4;
         this.changeSelection = var5;
      }

      public void actionPerformed(ActionEvent var1) {
         if (BasicTreeUI.this.tree != null) {
            BasicTreeUI.SHARED_ACTION.increment(BasicTreeUI.this.tree, BasicTreeUI.this, this.direction, this.addToSelection, this.changeSelection);
         }

      }

      public boolean isEnabled() {
         return BasicTreeUI.this.tree != null && BasicTreeUI.this.tree.isEnabled();
      }
   }

   public class TreePageAction extends AbstractAction {
      protected int direction;
      private boolean addToSelection;
      private boolean changeSelection;

      public TreePageAction(int var2, String var3) {
         this(var2, var3, false, true);
      }

      private TreePageAction(int var2, String var3, boolean var4, boolean var5) {
         this.direction = var2;
         this.addToSelection = var4;
         this.changeSelection = var5;
      }

      public void actionPerformed(ActionEvent var1) {
         if (BasicTreeUI.this.tree != null) {
            BasicTreeUI.SHARED_ACTION.page(BasicTreeUI.this.tree, BasicTreeUI.this, this.direction, this.addToSelection, this.changeSelection);
         }

      }

      public boolean isEnabled() {
         return BasicTreeUI.this.tree != null && BasicTreeUI.this.tree.isEnabled();
      }
   }

   public class TreeTraverseAction extends AbstractAction {
      protected int direction;
      private boolean changeSelection;

      public TreeTraverseAction(int var2, String var3) {
         this(var2, var3, true);
      }

      private TreeTraverseAction(int var2, String var3, boolean var4) {
         this.direction = var2;
         this.changeSelection = var4;
      }

      public void actionPerformed(ActionEvent var1) {
         if (BasicTreeUI.this.tree != null) {
            BasicTreeUI.SHARED_ACTION.traverse(BasicTreeUI.this.tree, BasicTreeUI.this, this.direction, this.changeSelection);
         }

      }

      public boolean isEnabled() {
         return BasicTreeUI.this.tree != null && BasicTreeUI.this.tree.isEnabled();
      }
   }

   public class SelectionModelPropertyChangeHandler implements PropertyChangeListener {
      public void propertyChange(PropertyChangeEvent var1) {
         BasicTreeUI.this.getHandler().propertyChange(var1);
      }
   }

   public class PropertyChangeHandler implements PropertyChangeListener {
      public void propertyChange(PropertyChangeEvent var1) {
         BasicTreeUI.this.getHandler().propertyChange(var1);
      }
   }

   public class MouseHandler extends MouseAdapter implements MouseMotionListener {
      public void mousePressed(MouseEvent var1) {
         BasicTreeUI.this.getHandler().mousePressed(var1);
      }

      public void mouseDragged(MouseEvent var1) {
         BasicTreeUI.this.getHandler().mouseDragged(var1);
      }

      public void mouseMoved(MouseEvent var1) {
         BasicTreeUI.this.getHandler().mouseMoved(var1);
      }

      public void mouseReleased(MouseEvent var1) {
         BasicTreeUI.this.getHandler().mouseReleased(var1);
      }
   }

   public class NodeDimensionsHandler extends AbstractLayoutCache.NodeDimensions {
      public Rectangle getNodeDimensions(Object var1, int var2, int var3, boolean var4, Rectangle var5) {
         if (BasicTreeUI.this.editingComponent != null && BasicTreeUI.this.editingRow == var2) {
            Dimension var8 = BasicTreeUI.this.editingComponent.getPreferredSize();
            int var9 = BasicTreeUI.this.getRowHeight();
            if (var9 > 0 && var9 != var8.height) {
               var8.height = var9;
            }

            if (var5 != null) {
               var5.x = this.getRowX(var2, var3);
               var5.width = var8.width;
               var5.height = var8.height;
            } else {
               var5 = new Rectangle(this.getRowX(var2, var3), 0, var8.width, var8.height);
            }

            return var5;
         } else if (BasicTreeUI.this.currentCellRenderer != null) {
            Component var6 = BasicTreeUI.this.currentCellRenderer.getTreeCellRendererComponent(BasicTreeUI.this.tree, var1, BasicTreeUI.this.tree.isRowSelected(var2), var4, BasicTreeUI.this.treeModel.isLeaf(var1), var2, false);
            if (BasicTreeUI.this.tree != null) {
               BasicTreeUI.this.rendererPane.add(var6);
               var6.validate();
            }

            Dimension var7 = var6.getPreferredSize();
            if (var5 != null) {
               var5.x = this.getRowX(var2, var3);
               var5.width = var7.width;
               var5.height = var7.height;
            } else {
               var5 = new Rectangle(this.getRowX(var2, var3), 0, var7.width, var7.height);
            }

            return var5;
         } else {
            return null;
         }
      }

      protected int getRowX(int var1, int var2) {
         return BasicTreeUI.this.getRowX(var1, var2);
      }
   }

   public class FocusHandler implements FocusListener {
      public void focusGained(FocusEvent var1) {
         BasicTreeUI.this.getHandler().focusGained(var1);
      }

      public void focusLost(FocusEvent var1) {
         BasicTreeUI.this.getHandler().focusLost(var1);
      }
   }

   public class KeyHandler extends KeyAdapter {
      protected Action repeatKeyAction;
      protected boolean isKeyDown;

      public void keyTyped(KeyEvent var1) {
         BasicTreeUI.this.getHandler().keyTyped(var1);
      }

      public void keyPressed(KeyEvent var1) {
         BasicTreeUI.this.getHandler().keyPressed(var1);
      }

      public void keyReleased(KeyEvent var1) {
         BasicTreeUI.this.getHandler().keyReleased(var1);
      }
   }

   public class CellEditorHandler implements CellEditorListener {
      public void editingStopped(ChangeEvent var1) {
         BasicTreeUI.this.getHandler().editingStopped(var1);
      }

      public void editingCanceled(ChangeEvent var1) {
         BasicTreeUI.this.getHandler().editingCanceled(var1);
      }
   }

   public class TreeSelectionHandler implements TreeSelectionListener {
      public void valueChanged(TreeSelectionEvent var1) {
         BasicTreeUI.this.getHandler().valueChanged(var1);
      }
   }

   public class TreeModelHandler implements TreeModelListener {
      public void treeNodesChanged(TreeModelEvent var1) {
         BasicTreeUI.this.getHandler().treeNodesChanged(var1);
      }

      public void treeNodesInserted(TreeModelEvent var1) {
         BasicTreeUI.this.getHandler().treeNodesInserted(var1);
      }

      public void treeNodesRemoved(TreeModelEvent var1) {
         BasicTreeUI.this.getHandler().treeNodesRemoved(var1);
      }

      public void treeStructureChanged(TreeModelEvent var1) {
         BasicTreeUI.this.getHandler().treeStructureChanged(var1);
      }
   }

   public class ComponentHandler extends ComponentAdapter implements ActionListener {
      protected Timer timer;
      protected JScrollBar scrollBar;

      public void componentMoved(ComponentEvent var1) {
         if (this.timer == null) {
            JScrollPane var2 = this.getScrollPane();
            if (var2 == null) {
               BasicTreeUI.this.updateSize();
            } else {
               this.scrollBar = var2.getVerticalScrollBar();
               if (this.scrollBar != null && this.scrollBar.getValueIsAdjusting()) {
                  this.startTimer();
               } else if ((this.scrollBar = var2.getHorizontalScrollBar()) != null && this.scrollBar.getValueIsAdjusting()) {
                  this.startTimer();
               } else {
                  BasicTreeUI.this.updateSize();
               }
            }
         }

      }

      protected void startTimer() {
         if (this.timer == null) {
            this.timer = new Timer(200, this);
            this.timer.setRepeats(true);
         }

         this.timer.start();
      }

      protected JScrollPane getScrollPane() {
         Container var1;
         for(var1 = BasicTreeUI.this.tree.getParent(); var1 != null && !(var1 instanceof JScrollPane); var1 = var1.getParent()) {
         }

         return var1 instanceof JScrollPane ? (JScrollPane)var1 : null;
      }

      public void actionPerformed(ActionEvent var1) {
         if (this.scrollBar == null || !this.scrollBar.getValueIsAdjusting()) {
            if (this.timer != null) {
               this.timer.stop();
            }

            BasicTreeUI.this.updateSize();
            this.timer = null;
            this.scrollBar = null;
         }

      }
   }

   public class TreeExpansionHandler implements TreeExpansionListener {
      public void treeExpanded(TreeExpansionEvent var1) {
         BasicTreeUI.this.getHandler().treeExpanded(var1);
      }

      public void treeCollapsed(TreeExpansionEvent var1) {
         BasicTreeUI.this.getHandler().treeCollapsed(var1);
      }
   }
}
