package javax.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.beans.ConstructorProperties;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleValue;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.plaf.TreeUI;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.RowMapper;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import sun.awt.AWTAccessor;
import sun.swing.SwingUtilities2;

public class JTree extends JComponent implements Scrollable, Accessible {
   private static final String uiClassID = "TreeUI";
   protected transient TreeModel treeModel;
   protected transient TreeSelectionModel selectionModel;
   protected boolean rootVisible;
   protected transient TreeCellRenderer cellRenderer;
   protected int rowHeight;
   private boolean rowHeightSet;
   private transient Hashtable<TreePath, Boolean> expandedState;
   protected boolean showsRootHandles;
   private boolean showsRootHandlesSet;
   protected transient JTree.TreeSelectionRedirector selectionRedirector;
   protected transient TreeCellEditor cellEditor;
   protected boolean editable;
   protected boolean largeModel;
   protected int visibleRowCount;
   protected boolean invokesStopCellEditing;
   protected boolean scrollsOnExpand;
   private boolean scrollsOnExpandSet;
   protected int toggleClickCount;
   protected transient TreeModelListener treeModelListener;
   private transient Stack<Stack<TreePath>> expandedStack;
   private TreePath leadPath;
   private TreePath anchorPath;
   private boolean expandsSelectedPaths;
   private boolean settingUI;
   private boolean dragEnabled;
   private DropMode dropMode;
   private transient JTree.DropLocation dropLocation;
   private int expandRow;
   private JTree.TreeTimer dropTimer;
   private transient TreeExpansionListener uiTreeExpansionListener;
   private static int TEMP_STACK_SIZE = 11;
   public static final String CELL_RENDERER_PROPERTY = "cellRenderer";
   public static final String TREE_MODEL_PROPERTY = "model";
   public static final String ROOT_VISIBLE_PROPERTY = "rootVisible";
   public static final String SHOWS_ROOT_HANDLES_PROPERTY = "showsRootHandles";
   public static final String ROW_HEIGHT_PROPERTY = "rowHeight";
   public static final String CELL_EDITOR_PROPERTY = "cellEditor";
   public static final String EDITABLE_PROPERTY = "editable";
   public static final String LARGE_MODEL_PROPERTY = "largeModel";
   public static final String SELECTION_MODEL_PROPERTY = "selectionModel";
   public static final String VISIBLE_ROW_COUNT_PROPERTY = "visibleRowCount";
   public static final String INVOKES_STOP_CELL_EDITING_PROPERTY = "invokesStopCellEditing";
   public static final String SCROLLS_ON_EXPAND_PROPERTY = "scrollsOnExpand";
   public static final String TOGGLE_CLICK_COUNT_PROPERTY = "toggleClickCount";
   public static final String LEAD_SELECTION_PATH_PROPERTY = "leadSelectionPath";
   public static final String ANCHOR_SELECTION_PATH_PROPERTY = "anchorSelectionPath";
   public static final String EXPANDS_SELECTED_PATHS_PROPERTY = "expandsSelectedPaths";

   protected static TreeModel getDefaultTreeModel() {
      DefaultMutableTreeNode var0 = new DefaultMutableTreeNode("JTree");
      DefaultMutableTreeNode var1 = new DefaultMutableTreeNode("colors");
      var0.add(var1);
      var1.add(new DefaultMutableTreeNode("blue"));
      var1.add(new DefaultMutableTreeNode("violet"));
      var1.add(new DefaultMutableTreeNode("red"));
      var1.add(new DefaultMutableTreeNode("yellow"));
      var1 = new DefaultMutableTreeNode("sports");
      var0.add(var1);
      var1.add(new DefaultMutableTreeNode("basketball"));
      var1.add(new DefaultMutableTreeNode("soccer"));
      var1.add(new DefaultMutableTreeNode("football"));
      var1.add(new DefaultMutableTreeNode("hockey"));
      var1 = new DefaultMutableTreeNode("food");
      var0.add(var1);
      var1.add(new DefaultMutableTreeNode("hot dogs"));
      var1.add(new DefaultMutableTreeNode("pizza"));
      var1.add(new DefaultMutableTreeNode("ravioli"));
      var1.add(new DefaultMutableTreeNode("bananas"));
      return new DefaultTreeModel(var0);
   }

   protected static TreeModel createTreeModel(Object var0) {
      Object var1;
      if (!(var0 instanceof Object[]) && !(var0 instanceof Hashtable) && !(var0 instanceof Vector)) {
         var1 = new JTree.DynamicUtilTreeNode("root", var0);
      } else {
         var1 = new DefaultMutableTreeNode("root");
         JTree.DynamicUtilTreeNode.createChildren((DefaultMutableTreeNode)var1, var0);
      }

      return new DefaultTreeModel((TreeNode)var1, false);
   }

   public JTree() {
      this(getDefaultTreeModel());
   }

   public JTree(Object[] var1) {
      this(createTreeModel(var1));
      this.setRootVisible(false);
      this.setShowsRootHandles(true);
      this.expandRoot();
   }

   public JTree(Vector<?> var1) {
      this(createTreeModel(var1));
      this.setRootVisible(false);
      this.setShowsRootHandles(true);
      this.expandRoot();
   }

   public JTree(Hashtable<?, ?> var1) {
      this(createTreeModel(var1));
      this.setRootVisible(false);
      this.setShowsRootHandles(true);
      this.expandRoot();
   }

   public JTree(TreeNode var1) {
      this(var1, false);
   }

   public JTree(TreeNode var1, boolean var2) {
      this((TreeModel)(new DefaultTreeModel(var1, var2)));
   }

   @ConstructorProperties({"model"})
   public JTree(TreeModel var1) {
      this.rowHeightSet = false;
      this.showsRootHandlesSet = false;
      this.scrollsOnExpandSet = false;
      this.dropMode = DropMode.USE_SELECTION;
      this.expandRow = -1;
      this.expandedStack = new Stack();
      this.toggleClickCount = 2;
      this.expandedState = new Hashtable();
      this.setLayout((LayoutManager)null);
      this.rowHeight = 16;
      this.visibleRowCount = 20;
      this.rootVisible = true;
      this.selectionModel = new DefaultTreeSelectionModel();
      this.cellRenderer = null;
      this.scrollsOnExpand = true;
      this.setOpaque(true);
      this.expandsSelectedPaths = true;
      this.updateUI();
      this.setModel(var1);
   }

   public TreeUI getUI() {
      return (TreeUI)this.ui;
   }

   public void setUI(TreeUI var1) {
      if (this.ui != var1) {
         this.settingUI = true;
         this.uiTreeExpansionListener = null;

         try {
            super.setUI(var1);
         } finally {
            this.settingUI = false;
         }
      }

   }

   public void updateUI() {
      this.setUI((TreeUI)UIManager.getUI(this));
      SwingUtilities.updateRendererOrEditorUI(this.getCellRenderer());
      SwingUtilities.updateRendererOrEditorUI(this.getCellEditor());
   }

   public String getUIClassID() {
      return "TreeUI";
   }

   public TreeCellRenderer getCellRenderer() {
      return this.cellRenderer;
   }

   public void setCellRenderer(TreeCellRenderer var1) {
      TreeCellRenderer var2 = this.cellRenderer;
      this.cellRenderer = var1;
      this.firePropertyChange("cellRenderer", var2, this.cellRenderer);
      this.invalidate();
   }

   public void setEditable(boolean var1) {
      boolean var2 = this.editable;
      this.editable = var1;
      this.firePropertyChange("editable", var2, var1);
      if (this.accessibleContext != null) {
         this.accessibleContext.firePropertyChange("AccessibleState", var2 ? AccessibleState.EDITABLE : null, var1 ? AccessibleState.EDITABLE : null);
      }

   }

   public boolean isEditable() {
      return this.editable;
   }

   public void setCellEditor(TreeCellEditor var1) {
      TreeCellEditor var2 = this.cellEditor;
      this.cellEditor = var1;
      this.firePropertyChange("cellEditor", var2, var1);
      this.invalidate();
   }

   public TreeCellEditor getCellEditor() {
      return this.cellEditor;
   }

   public TreeModel getModel() {
      return this.treeModel;
   }

   public void setModel(TreeModel var1) {
      this.clearSelection();
      TreeModel var2 = this.treeModel;
      if (this.treeModel != null && this.treeModelListener != null) {
         this.treeModel.removeTreeModelListener(this.treeModelListener);
      }

      if (this.accessibleContext != null) {
         if (this.treeModel != null) {
            this.treeModel.removeTreeModelListener((TreeModelListener)this.accessibleContext);
         }

         if (var1 != null) {
            var1.addTreeModelListener((TreeModelListener)this.accessibleContext);
         }
      }

      this.treeModel = var1;
      this.clearToggledPaths();
      if (this.treeModel != null) {
         if (this.treeModelListener == null) {
            this.treeModelListener = this.createTreeModelListener();
         }

         if (this.treeModelListener != null) {
            this.treeModel.addTreeModelListener(this.treeModelListener);
         }

         Object var3 = this.treeModel.getRoot();
         if (var3 != null && !this.treeModel.isLeaf(var3)) {
            this.expandedState.put(new TreePath(var3), Boolean.TRUE);
         }
      }

      this.firePropertyChange("model", var2, this.treeModel);
      this.invalidate();
   }

   public boolean isRootVisible() {
      return this.rootVisible;
   }

   public void setRootVisible(boolean var1) {
      boolean var2 = this.rootVisible;
      this.rootVisible = var1;
      this.firePropertyChange("rootVisible", var2, this.rootVisible);
      if (this.accessibleContext != null) {
         ((JTree.AccessibleJTree)this.accessibleContext).fireVisibleDataPropertyChange();
      }

   }

   public void setShowsRootHandles(boolean var1) {
      boolean var2 = this.showsRootHandles;
      TreeModel var3 = this.getModel();
      this.showsRootHandles = var1;
      this.showsRootHandlesSet = true;
      this.firePropertyChange("showsRootHandles", var2, this.showsRootHandles);
      if (this.accessibleContext != null) {
         ((JTree.AccessibleJTree)this.accessibleContext).fireVisibleDataPropertyChange();
      }

      this.invalidate();
   }

   public boolean getShowsRootHandles() {
      return this.showsRootHandles;
   }

   public void setRowHeight(int var1) {
      int var2 = this.rowHeight;
      this.rowHeight = var1;
      this.rowHeightSet = true;
      this.firePropertyChange("rowHeight", var2, this.rowHeight);
      this.invalidate();
   }

   public int getRowHeight() {
      return this.rowHeight;
   }

   public boolean isFixedRowHeight() {
      return this.rowHeight > 0;
   }

   public void setLargeModel(boolean var1) {
      boolean var2 = this.largeModel;
      this.largeModel = var1;
      this.firePropertyChange("largeModel", var2, var1);
   }

   public boolean isLargeModel() {
      return this.largeModel;
   }

   public void setInvokesStopCellEditing(boolean var1) {
      boolean var2 = this.invokesStopCellEditing;
      this.invokesStopCellEditing = var1;
      this.firePropertyChange("invokesStopCellEditing", var2, var1);
   }

   public boolean getInvokesStopCellEditing() {
      return this.invokesStopCellEditing;
   }

   public void setScrollsOnExpand(boolean var1) {
      boolean var2 = this.scrollsOnExpand;
      this.scrollsOnExpand = var1;
      this.scrollsOnExpandSet = true;
      this.firePropertyChange("scrollsOnExpand", var2, var1);
   }

   public boolean getScrollsOnExpand() {
      return this.scrollsOnExpand;
   }

   public void setToggleClickCount(int var1) {
      int var2 = this.toggleClickCount;
      this.toggleClickCount = var1;
      this.firePropertyChange("toggleClickCount", var2, var1);
   }

   public int getToggleClickCount() {
      return this.toggleClickCount;
   }

   public void setExpandsSelectedPaths(boolean var1) {
      boolean var2 = this.expandsSelectedPaths;
      this.expandsSelectedPaths = var1;
      this.firePropertyChange("expandsSelectedPaths", var2, var1);
   }

   public boolean getExpandsSelectedPaths() {
      return this.expandsSelectedPaths;
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
         case ON_OR_INSERT:
            this.dropMode = var1;
            return;
         }
      }

      throw new IllegalArgumentException(var1 + ": Unsupported drop mode for tree");
   }

   public final DropMode getDropMode() {
      return this.dropMode;
   }

   JTree.DropLocation dropLocationForPoint(Point var1) {
      JTree.DropLocation var2 = null;
      int var3 = this.getClosestRowForLocation(var1.x, var1.y);
      Rectangle var4 = this.getRowBounds(var3);
      TreeModel var5 = this.getModel();
      Object var6 = var5 == null ? null : var5.getRoot();
      TreePath var7 = var6 == null ? null : new TreePath(var6);
      boolean var10 = var3 == -1 || var1.y < var4.y || var1.y >= var4.y + var4.height;
      switch(this.dropMode) {
      case USE_SELECTION:
      case ON:
         if (var10) {
            var2 = new JTree.DropLocation(var1, (TreePath)null, -1);
         } else {
            var2 = new JTree.DropLocation(var1, this.getPathForRow(var3), -1);
         }
         break;
      case INSERT:
      case ON_OR_INSERT:
         if (var3 == -1) {
            if (var6 != null && !var5.isLeaf(var6) && this.isExpanded(var7)) {
               var2 = new JTree.DropLocation(var1, var7, 0);
            } else {
               var2 = new JTree.DropLocation(var1, (TreePath)null, -1);
            }
         } else {
            boolean var11 = this.dropMode == DropMode.ON_OR_INSERT || !var5.isLeaf(this.getPathForRow(var3).getLastPathComponent());
            SwingUtilities2.Section var12 = SwingUtilities2.liesInVertical(var4, var1, var11);
            TreePath var8;
            TreePath var9;
            if (var12 == SwingUtilities2.Section.LEADING) {
               var8 = this.getPathForRow(var3);
               var9 = var8.getParentPath();
            } else {
               if (var12 != SwingUtilities2.Section.TRAILING) {
                  assert var11;

                  var2 = new JTree.DropLocation(var1, this.getPathForRow(var3), -1);
                  break;
               }

               int var13 = var3 + 1;
               if (var13 >= this.getRowCount()) {
                  if (!var5.isLeaf(var6) && this.isExpanded(var7)) {
                     var13 = var5.getChildCount(var6);
                     var2 = new JTree.DropLocation(var1, var7, var13);
                  } else {
                     var2 = new JTree.DropLocation(var1, (TreePath)null, -1);
                  }
                  break;
               }

               var8 = this.getPathForRow(var13);
               var9 = var8.getParentPath();
            }

            if (var9 != null) {
               var2 = new JTree.DropLocation(var1, var9, var5.getIndexOfChild(var9.getLastPathComponent(), var8.getLastPathComponent()));
            } else if (!var11 && var5.isLeaf(var6)) {
               var2 = new JTree.DropLocation(var1, (TreePath)null, -1);
            } else {
               var2 = new JTree.DropLocation(var1, var7, -1);
            }
         }
         break;
      default:
         assert false : "Unexpected drop mode";
      }

      if (var10 || var3 != this.expandRow) {
         this.cancelDropTimer();
      }

      if (!var10 && var3 != this.expandRow && this.isCollapsed(var3)) {
         this.expandRow = var3;
         this.startDropTimer();
      }

      return var2;
   }

   Object setDropLocation(TransferHandler.DropLocation var1, Object var2, boolean var3) {
      Object var4 = null;
      JTree.DropLocation var5 = (JTree.DropLocation)var1;
      if (this.dropMode == DropMode.USE_SELECTION) {
         if (var5 == null) {
            if (!var3 && var2 != null) {
               this.setSelectionPaths(((TreePath[][])((TreePath[][])var2))[0]);
               this.setAnchorSelectionPath(((TreePath[][])((TreePath[][])var2))[1][0]);
               this.setLeadSelectionPath(((TreePath[][])((TreePath[][])var2))[1][1]);
            }
         } else {
            if (this.dropLocation == null) {
               TreePath[] var6 = this.getSelectionPaths();
               if (var6 == null) {
                  var6 = new TreePath[0];
               }

               var4 = new TreePath[][]{var6, {this.getAnchorSelectionPath(), this.getLeadSelectionPath()}};
            } else {
               var4 = var2;
            }

            this.setSelectionPath(var5.getPath());
         }
      }

      JTree.DropLocation var7 = this.dropLocation;
      this.dropLocation = var5;
      this.firePropertyChange("dropLocation", var7, this.dropLocation);
      return var4;
   }

   void dndDone() {
      this.cancelDropTimer();
      this.dropTimer = null;
   }

   public final JTree.DropLocation getDropLocation() {
      return this.dropLocation;
   }

   private void startDropTimer() {
      if (this.dropTimer == null) {
         this.dropTimer = new JTree.TreeTimer();
      }

      this.dropTimer.start();
   }

   private void cancelDropTimer() {
      if (this.dropTimer != null && this.dropTimer.isRunning()) {
         this.expandRow = -1;
         this.dropTimer.stop();
      }

   }

   public boolean isPathEditable(TreePath var1) {
      return this.isEditable();
   }

   public String getToolTipText(MouseEvent var1) {
      String var2 = null;
      if (var1 != null) {
         Point var3 = var1.getPoint();
         int var4 = this.getRowForLocation(var3.x, var3.y);
         TreeCellRenderer var5 = this.getCellRenderer();
         if (var4 != -1 && var5 != null) {
            TreePath var6 = this.getPathForRow(var4);
            Object var7 = var6.getLastPathComponent();
            Component var8 = var5.getTreeCellRendererComponent(this, var7, this.isRowSelected(var4), this.isExpanded(var4), this.getModel().isLeaf(var7), var4, true);
            if (var8 instanceof JComponent) {
               Rectangle var10 = this.getPathBounds(var6);
               var3.translate(-var10.x, -var10.y);
               MouseEvent var9 = new MouseEvent(var8, var1.getID(), var1.getWhen(), var1.getModifiers(), var3.x, var3.y, var1.getXOnScreen(), var1.getYOnScreen(), var1.getClickCount(), var1.isPopupTrigger(), 0);
               AWTAccessor.MouseEventAccessor var11 = AWTAccessor.getMouseEventAccessor();
               var11.setCausedByTouchEvent(var9, var11.isCausedByTouchEvent(var1));
               var2 = ((JComponent)var8).getToolTipText(var9);
            }
         }
      }

      if (var2 == null) {
         var2 = this.getToolTipText();
      }

      return var2;
   }

   public String convertValueToText(Object var1, boolean var2, boolean var3, boolean var4, int var5, boolean var6) {
      if (var1 != null) {
         String var7 = var1.toString();
         if (var7 != null) {
            return var7;
         }
      }

      return "";
   }

   public int getRowCount() {
      TreeUI var1 = this.getUI();
      return var1 != null ? var1.getRowCount(this) : 0;
   }

   public void setSelectionPath(TreePath var1) {
      this.getSelectionModel().setSelectionPath(var1);
   }

   public void setSelectionPaths(TreePath[] var1) {
      this.getSelectionModel().setSelectionPaths(var1);
   }

   public void setLeadSelectionPath(TreePath var1) {
      TreePath var2 = this.leadPath;
      this.leadPath = var1;
      this.firePropertyChange("leadSelectionPath", var2, var1);
      if (this.accessibleContext != null) {
         ((JTree.AccessibleJTree)this.accessibleContext).fireActiveDescendantPropertyChange(var2, var1);
      }

   }

   public void setAnchorSelectionPath(TreePath var1) {
      TreePath var2 = this.anchorPath;
      this.anchorPath = var1;
      this.firePropertyChange("anchorSelectionPath", var2, var1);
   }

   public void setSelectionRow(int var1) {
      int[] var2 = new int[]{var1};
      this.setSelectionRows(var2);
   }

   public void setSelectionRows(int[] var1) {
      TreeUI var2 = this.getUI();
      if (var2 != null && var1 != null) {
         int var3 = var1.length;
         TreePath[] var4 = new TreePath[var3];

         for(int var5 = 0; var5 < var3; ++var5) {
            var4[var5] = var2.getPathForRow(this, var1[var5]);
         }

         this.setSelectionPaths(var4);
      }

   }

   public void addSelectionPath(TreePath var1) {
      this.getSelectionModel().addSelectionPath(var1);
   }

   public void addSelectionPaths(TreePath[] var1) {
      this.getSelectionModel().addSelectionPaths(var1);
   }

   public void addSelectionRow(int var1) {
      int[] var2 = new int[]{var1};
      this.addSelectionRows(var2);
   }

   public void addSelectionRows(int[] var1) {
      TreeUI var2 = this.getUI();
      if (var2 != null && var1 != null) {
         int var3 = var1.length;
         TreePath[] var4 = new TreePath[var3];

         for(int var5 = 0; var5 < var3; ++var5) {
            var4[var5] = var2.getPathForRow(this, var1[var5]);
         }

         this.addSelectionPaths(var4);
      }

   }

   public Object getLastSelectedPathComponent() {
      TreePath var1 = this.getSelectionModel().getSelectionPath();
      return var1 != null ? var1.getLastPathComponent() : null;
   }

   public TreePath getLeadSelectionPath() {
      return this.leadPath;
   }

   public TreePath getAnchorSelectionPath() {
      return this.anchorPath;
   }

   public TreePath getSelectionPath() {
      return this.getSelectionModel().getSelectionPath();
   }

   public TreePath[] getSelectionPaths() {
      TreePath[] var1 = this.getSelectionModel().getSelectionPaths();
      return var1 != null && var1.length > 0 ? var1 : null;
   }

   public int[] getSelectionRows() {
      return this.getSelectionModel().getSelectionRows();
   }

   public int getSelectionCount() {
      return this.selectionModel.getSelectionCount();
   }

   public int getMinSelectionRow() {
      return this.getSelectionModel().getMinSelectionRow();
   }

   public int getMaxSelectionRow() {
      return this.getSelectionModel().getMaxSelectionRow();
   }

   public int getLeadSelectionRow() {
      TreePath var1 = this.getLeadSelectionPath();
      return var1 != null ? this.getRowForPath(var1) : -1;
   }

   public boolean isPathSelected(TreePath var1) {
      return this.getSelectionModel().isPathSelected(var1);
   }

   public boolean isRowSelected(int var1) {
      return this.getSelectionModel().isRowSelected(var1);
   }

   public Enumeration<TreePath> getExpandedDescendants(TreePath var1) {
      if (!this.isExpanded(var1)) {
         return null;
      } else {
         Enumeration var2 = this.expandedState.keys();
         Vector var3 = null;
         if (var2 != null) {
            while(var2.hasMoreElements()) {
               TreePath var4 = (TreePath)var2.nextElement();
               Object var5 = this.expandedState.get(var4);
               if (var4 != var1 && var5 != null && (Boolean)var5 && var1.isDescendant(var4) && this.isVisible(var4)) {
                  if (var3 == null) {
                     var3 = new Vector();
                  }

                  var3.addElement(var4);
               }
            }
         }

         if (var3 == null) {
            Set var6 = Collections.emptySet();
            return Collections.enumeration(var6);
         } else {
            return var3.elements();
         }
      }
   }

   public boolean hasBeenExpanded(TreePath var1) {
      return var1 != null && this.expandedState.get(var1) != null;
   }

   public boolean isExpanded(TreePath var1) {
      if (var1 == null) {
         return false;
      } else {
         do {
            Object var2 = this.expandedState.get(var1);
            if (var2 == null || !(Boolean)var2) {
               return false;
            }
         } while((var1 = var1.getParentPath()) != null);

         return true;
      }
   }

   public boolean isExpanded(int var1) {
      TreeUI var2 = this.getUI();
      if (var2 != null) {
         TreePath var3 = var2.getPathForRow(this, var1);
         if (var3 != null) {
            Boolean var4 = (Boolean)this.expandedState.get(var3);
            return var4 != null && var4;
         }
      }

      return false;
   }

   public boolean isCollapsed(TreePath var1) {
      return !this.isExpanded(var1);
   }

   public boolean isCollapsed(int var1) {
      return !this.isExpanded(var1);
   }

   public void makeVisible(TreePath var1) {
      if (var1 != null) {
         TreePath var2 = var1.getParentPath();
         if (var2 != null) {
            this.expandPath(var2);
         }
      }

   }

   public boolean isVisible(TreePath var1) {
      if (var1 != null) {
         TreePath var2 = var1.getParentPath();
         return var2 != null ? this.isExpanded(var2) : true;
      } else {
         return false;
      }
   }

   public Rectangle getPathBounds(TreePath var1) {
      TreeUI var2 = this.getUI();
      return var2 != null ? var2.getPathBounds(this, var1) : null;
   }

   public Rectangle getRowBounds(int var1) {
      return this.getPathBounds(this.getPathForRow(var1));
   }

   public void scrollPathToVisible(TreePath var1) {
      if (var1 != null) {
         this.makeVisible(var1);
         Rectangle var2 = this.getPathBounds(var1);
         if (var2 != null) {
            this.scrollRectToVisible(var2);
            if (this.accessibleContext != null) {
               ((JTree.AccessibleJTree)this.accessibleContext).fireVisibleDataPropertyChange();
            }
         }
      }

   }

   public void scrollRowToVisible(int var1) {
      this.scrollPathToVisible(this.getPathForRow(var1));
   }

   public TreePath getPathForRow(int var1) {
      TreeUI var2 = this.getUI();
      return var2 != null ? var2.getPathForRow(this, var1) : null;
   }

   public int getRowForPath(TreePath var1) {
      TreeUI var2 = this.getUI();
      return var2 != null ? var2.getRowForPath(this, var1) : -1;
   }

   public void expandPath(TreePath var1) {
      TreeModel var2 = this.getModel();
      if (var1 != null && var2 != null && !var2.isLeaf(var1.getLastPathComponent())) {
         this.setExpandedState(var1, true);
      }

   }

   public void expandRow(int var1) {
      this.expandPath(this.getPathForRow(var1));
   }

   public void collapsePath(TreePath var1) {
      this.setExpandedState(var1, false);
   }

   public void collapseRow(int var1) {
      this.collapsePath(this.getPathForRow(var1));
   }

   public TreePath getPathForLocation(int var1, int var2) {
      TreePath var3 = this.getClosestPathForLocation(var1, var2);
      if (var3 != null) {
         Rectangle var4 = this.getPathBounds(var3);
         if (var4 != null && var1 >= var4.x && var1 < var4.x + var4.width && var2 >= var4.y && var2 < var4.y + var4.height) {
            return var3;
         }
      }

      return null;
   }

   public int getRowForLocation(int var1, int var2) {
      return this.getRowForPath(this.getPathForLocation(var1, var2));
   }

   public TreePath getClosestPathForLocation(int var1, int var2) {
      TreeUI var3 = this.getUI();
      return var3 != null ? var3.getClosestPathForLocation(this, var1, var2) : null;
   }

   public int getClosestRowForLocation(int var1, int var2) {
      return this.getRowForPath(this.getClosestPathForLocation(var1, var2));
   }

   public boolean isEditing() {
      TreeUI var1 = this.getUI();
      return var1 != null ? var1.isEditing(this) : false;
   }

   public boolean stopEditing() {
      TreeUI var1 = this.getUI();
      return var1 != null ? var1.stopEditing(this) : false;
   }

   public void cancelEditing() {
      TreeUI var1 = this.getUI();
      if (var1 != null) {
         var1.cancelEditing(this);
      }

   }

   public void startEditingAtPath(TreePath var1) {
      TreeUI var2 = this.getUI();
      if (var2 != null) {
         var2.startEditingAtPath(this, var1);
      }

   }

   public TreePath getEditingPath() {
      TreeUI var1 = this.getUI();
      return var1 != null ? var1.getEditingPath(this) : null;
   }

   public void setSelectionModel(TreeSelectionModel var1) {
      if (var1 == null) {
         var1 = JTree.EmptySelectionModel.sharedInstance();
      }

      TreeSelectionModel var2 = this.selectionModel;
      if (this.selectionModel != null && this.selectionRedirector != null) {
         this.selectionModel.removeTreeSelectionListener(this.selectionRedirector);
      }

      if (this.accessibleContext != null) {
         this.selectionModel.removeTreeSelectionListener((TreeSelectionListener)this.accessibleContext);
         ((TreeSelectionModel)var1).addTreeSelectionListener((TreeSelectionListener)this.accessibleContext);
      }

      this.selectionModel = (TreeSelectionModel)var1;
      if (this.selectionRedirector != null) {
         this.selectionModel.addTreeSelectionListener(this.selectionRedirector);
      }

      this.firePropertyChange("selectionModel", var2, this.selectionModel);
      if (this.accessibleContext != null) {
         this.accessibleContext.firePropertyChange("AccessibleSelection", false, true);
      }

   }

   public TreeSelectionModel getSelectionModel() {
      return this.selectionModel;
   }

   protected TreePath[] getPathBetweenRows(int var1, int var2) {
      TreeUI var3 = this.getUI();
      if (var3 != null) {
         int var4 = this.getRowCount();
         if (var4 > 0 && (var1 >= 0 || var2 >= 0) && (var1 < var4 || var2 < var4)) {
            var1 = Math.min(var4 - 1, Math.max(var1, 0));
            var2 = Math.min(var4 - 1, Math.max(var2, 0));
            int var5 = Math.min(var1, var2);
            int var6 = Math.max(var1, var2);
            TreePath[] var7 = new TreePath[var6 - var5 + 1];

            for(int var8 = var5; var8 <= var6; ++var8) {
               var7[var8 - var5] = var3.getPathForRow(this, var8);
            }

            return var7;
         }
      }

      return new TreePath[0];
   }

   public void setSelectionInterval(int var1, int var2) {
      TreePath[] var3 = this.getPathBetweenRows(var1, var2);
      this.getSelectionModel().setSelectionPaths(var3);
   }

   public void addSelectionInterval(int var1, int var2) {
      TreePath[] var3 = this.getPathBetweenRows(var1, var2);
      if (var3 != null && var3.length > 0) {
         this.getSelectionModel().addSelectionPaths(var3);
      }

   }

   public void removeSelectionInterval(int var1, int var2) {
      TreePath[] var3 = this.getPathBetweenRows(var1, var2);
      if (var3 != null && var3.length > 0) {
         this.getSelectionModel().removeSelectionPaths(var3);
      }

   }

   public void removeSelectionPath(TreePath var1) {
      this.getSelectionModel().removeSelectionPath(var1);
   }

   public void removeSelectionPaths(TreePath[] var1) {
      this.getSelectionModel().removeSelectionPaths(var1);
   }

   public void removeSelectionRow(int var1) {
      int[] var2 = new int[]{var1};
      this.removeSelectionRows(var2);
   }

   public void removeSelectionRows(int[] var1) {
      TreeUI var2 = this.getUI();
      if (var2 != null && var1 != null) {
         int var3 = var1.length;
         TreePath[] var4 = new TreePath[var3];

         for(int var5 = 0; var5 < var3; ++var5) {
            var4[var5] = var2.getPathForRow(this, var1[var5]);
         }

         this.removeSelectionPaths(var4);
      }

   }

   public void clearSelection() {
      this.getSelectionModel().clearSelection();
   }

   public boolean isSelectionEmpty() {
      return this.getSelectionModel().isSelectionEmpty();
   }

   public void addTreeExpansionListener(TreeExpansionListener var1) {
      if (this.settingUI) {
         this.uiTreeExpansionListener = var1;
      }

      this.listenerList.add(TreeExpansionListener.class, var1);
   }

   public void removeTreeExpansionListener(TreeExpansionListener var1) {
      this.listenerList.remove(TreeExpansionListener.class, var1);
      if (this.uiTreeExpansionListener == var1) {
         this.uiTreeExpansionListener = null;
      }

   }

   public TreeExpansionListener[] getTreeExpansionListeners() {
      return (TreeExpansionListener[])this.listenerList.getListeners(TreeExpansionListener.class);
   }

   public void addTreeWillExpandListener(TreeWillExpandListener var1) {
      this.listenerList.add(TreeWillExpandListener.class, var1);
   }

   public void removeTreeWillExpandListener(TreeWillExpandListener var1) {
      this.listenerList.remove(TreeWillExpandListener.class, var1);
   }

   public TreeWillExpandListener[] getTreeWillExpandListeners() {
      return (TreeWillExpandListener[])this.listenerList.getListeners(TreeWillExpandListener.class);
   }

   public void fireTreeExpanded(TreePath var1) {
      Object[] var2 = this.listenerList.getListenerList();
      TreeExpansionEvent var3 = null;
      if (this.uiTreeExpansionListener != null) {
         var3 = new TreeExpansionEvent(this, var1);
         this.uiTreeExpansionListener.treeExpanded(var3);
      }

      for(int var4 = var2.length - 2; var4 >= 0; var4 -= 2) {
         if (var2[var4] == TreeExpansionListener.class && var2[var4 + 1] != this.uiTreeExpansionListener) {
            if (var3 == null) {
               var3 = new TreeExpansionEvent(this, var1);
            }

            ((TreeExpansionListener)var2[var4 + 1]).treeExpanded(var3);
         }
      }

   }

   public void fireTreeCollapsed(TreePath var1) {
      Object[] var2 = this.listenerList.getListenerList();
      TreeExpansionEvent var3 = null;
      if (this.uiTreeExpansionListener != null) {
         var3 = new TreeExpansionEvent(this, var1);
         this.uiTreeExpansionListener.treeCollapsed(var3);
      }

      for(int var4 = var2.length - 2; var4 >= 0; var4 -= 2) {
         if (var2[var4] == TreeExpansionListener.class && var2[var4 + 1] != this.uiTreeExpansionListener) {
            if (var3 == null) {
               var3 = new TreeExpansionEvent(this, var1);
            }

            ((TreeExpansionListener)var2[var4 + 1]).treeCollapsed(var3);
         }
      }

   }

   public void fireTreeWillExpand(TreePath var1) throws ExpandVetoException {
      Object[] var2 = this.listenerList.getListenerList();
      TreeExpansionEvent var3 = null;

      for(int var4 = var2.length - 2; var4 >= 0; var4 -= 2) {
         if (var2[var4] == TreeWillExpandListener.class) {
            if (var3 == null) {
               var3 = new TreeExpansionEvent(this, var1);
            }

            ((TreeWillExpandListener)var2[var4 + 1]).treeWillExpand(var3);
         }
      }

   }

   public void fireTreeWillCollapse(TreePath var1) throws ExpandVetoException {
      Object[] var2 = this.listenerList.getListenerList();
      TreeExpansionEvent var3 = null;

      for(int var4 = var2.length - 2; var4 >= 0; var4 -= 2) {
         if (var2[var4] == TreeWillExpandListener.class) {
            if (var3 == null) {
               var3 = new TreeExpansionEvent(this, var1);
            }

            ((TreeWillExpandListener)var2[var4 + 1]).treeWillCollapse(var3);
         }
      }

   }

   public void addTreeSelectionListener(TreeSelectionListener var1) {
      this.listenerList.add(TreeSelectionListener.class, var1);
      if (this.listenerList.getListenerCount(TreeSelectionListener.class) != 0 && this.selectionRedirector == null) {
         this.selectionRedirector = new JTree.TreeSelectionRedirector();
         this.selectionModel.addTreeSelectionListener(this.selectionRedirector);
      }

   }

   public void removeTreeSelectionListener(TreeSelectionListener var1) {
      this.listenerList.remove(TreeSelectionListener.class, var1);
      if (this.listenerList.getListenerCount(TreeSelectionListener.class) == 0 && this.selectionRedirector != null) {
         this.selectionModel.removeTreeSelectionListener(this.selectionRedirector);
         this.selectionRedirector = null;
      }

   }

   public TreeSelectionListener[] getTreeSelectionListeners() {
      return (TreeSelectionListener[])this.listenerList.getListeners(TreeSelectionListener.class);
   }

   protected void fireValueChanged(TreeSelectionEvent var1) {
      Object[] var2 = this.listenerList.getListenerList();

      for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
         if (var2[var3] == TreeSelectionListener.class) {
            ((TreeSelectionListener)var2[var3 + 1]).valueChanged(var1);
         }
      }

   }

   public void treeDidChange() {
      this.revalidate();
      this.repaint();
   }

   public void setVisibleRowCount(int var1) {
      int var2 = this.visibleRowCount;
      this.visibleRowCount = var1;
      this.firePropertyChange("visibleRowCount", var2, this.visibleRowCount);
      this.invalidate();
      if (this.accessibleContext != null) {
         ((JTree.AccessibleJTree)this.accessibleContext).fireVisibleDataPropertyChange();
      }

   }

   public int getVisibleRowCount() {
      return this.visibleRowCount;
   }

   private void expandRoot() {
      TreeModel var1 = this.getModel();
      if (var1 != null && var1.getRoot() != null) {
         this.expandPath(new TreePath(var1.getRoot()));
      }

   }

   public TreePath getNextMatch(String var1, int var2, Position.Bias var3) {
      int var4 = this.getRowCount();
      if (var1 == null) {
         throw new IllegalArgumentException();
      } else if (var2 >= 0 && var2 < var4) {
         var1 = var1.toUpperCase();
         int var5 = var3 == Position.Bias.Forward ? 1 : -1;
         int var6 = var2;

         do {
            TreePath var7 = this.getPathForRow(var6);
            String var8 = this.convertValueToText(var7.getLastPathComponent(), this.isRowSelected(var6), this.isExpanded(var6), true, var6, false);
            if (var8.toUpperCase().startsWith(var1)) {
               return var7;
            }

            var6 = (var6 + var5 + var4) % var4;
         } while(var6 != var2);

         return null;
      } else {
         throw new IllegalArgumentException();
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      Vector var2 = new Vector();
      var1.defaultWriteObject();
      if (this.cellRenderer != null && this.cellRenderer instanceof Serializable) {
         var2.addElement("cellRenderer");
         var2.addElement(this.cellRenderer);
      }

      if (this.cellEditor != null && this.cellEditor instanceof Serializable) {
         var2.addElement("cellEditor");
         var2.addElement(this.cellEditor);
      }

      if (this.treeModel != null && this.treeModel instanceof Serializable) {
         var2.addElement("treeModel");
         var2.addElement(this.treeModel);
      }

      if (this.selectionModel != null && this.selectionModel instanceof Serializable) {
         var2.addElement("selectionModel");
         var2.addElement(this.selectionModel);
      }

      Object var3 = this.getArchivableExpandedState();
      if (var3 != null) {
         var2.addElement("expandedState");
         var2.addElement(var3);
      }

      var1.writeObject(var2);
      if (this.getUIClassID().equals("TreeUI")) {
         byte var4 = JComponent.getWriteObjCounter(this);
         --var4;
         JComponent.setWriteObjCounter(this, var4);
         if (var4 == 0 && this.ui != null) {
            this.ui.installUI(this);
         }
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.expandedState = new Hashtable();
      this.expandedStack = new Stack();
      Vector var2 = (Vector)var1.readObject();
      int var3 = 0;
      int var4 = var2.size();
      if (var3 < var4 && var2.elementAt(var3).equals("cellRenderer")) {
         ++var3;
         this.cellRenderer = (TreeCellRenderer)var2.elementAt(var3);
         ++var3;
      }

      if (var3 < var4 && var2.elementAt(var3).equals("cellEditor")) {
         ++var3;
         this.cellEditor = (TreeCellEditor)var2.elementAt(var3);
         ++var3;
      }

      if (var3 < var4 && var2.elementAt(var3).equals("treeModel")) {
         ++var3;
         this.treeModel = (TreeModel)var2.elementAt(var3);
         ++var3;
      }

      if (var3 < var4 && var2.elementAt(var3).equals("selectionModel")) {
         ++var3;
         this.selectionModel = (TreeSelectionModel)var2.elementAt(var3);
         ++var3;
      }

      if (var3 < var4 && var2.elementAt(var3).equals("expandedState")) {
         ++var3;
         this.unarchiveExpandedState(var2.elementAt(var3));
         ++var3;
      }

      if (this.listenerList.getListenerCount(TreeSelectionListener.class) != 0) {
         this.selectionRedirector = new JTree.TreeSelectionRedirector();
         this.selectionModel.addTreeSelectionListener(this.selectionRedirector);
      }

      if (this.treeModel != null) {
         this.treeModelListener = this.createTreeModelListener();
         if (this.treeModelListener != null) {
            this.treeModel.addTreeModelListener(this.treeModelListener);
         }
      }

   }

   private Object getArchivableExpandedState() {
      TreeModel var1 = this.getModel();
      if (var1 != null) {
         Enumeration var2 = this.expandedState.keys();
         if (var2 != null) {
            Vector var3 = new Vector();

            while(var2.hasMoreElements()) {
               TreePath var4 = (TreePath)var2.nextElement();

               int[] var5;
               try {
                  var5 = this.getModelIndexsForPath(var4);
               } catch (Error var7) {
                  var5 = null;
               }

               if (var5 != null) {
                  var3.addElement(var5);
                  var3.addElement(this.expandedState.get(var4));
               }
            }

            return var3;
         }
      }

      return null;
   }

   private void unarchiveExpandedState(Object var1) {
      if (var1 instanceof Vector) {
         Vector var2 = (Vector)var1;

         for(int var3 = var2.size() - 1; var3 >= 0; --var3) {
            Boolean var4 = (Boolean)var2.elementAt(var3--);

            try {
               TreePath var5 = this.getPathForIndexs((int[])((int[])var2.elementAt(var3)));
               if (var5 != null) {
                  this.expandedState.put(var5, var4);
               }
            } catch (Error var7) {
            }
         }
      }

   }

   private int[] getModelIndexsForPath(TreePath var1) {
      if (var1 != null) {
         TreeModel var2 = this.getModel();
         int var3 = var1.getPathCount();
         int[] var4 = new int[var3 - 1];
         Object var5 = var2.getRoot();

         for(int var6 = 1; var6 < var3; ++var6) {
            var4[var6 - 1] = var2.getIndexOfChild(var5, var1.getPathComponent(var6));
            var5 = var1.getPathComponent(var6);
            if (var4[var6 - 1] < 0) {
               return null;
            }
         }

         return var4;
      } else {
         return null;
      }
   }

   private TreePath getPathForIndexs(int[] var1) {
      if (var1 == null) {
         return null;
      } else {
         TreeModel var2 = this.getModel();
         if (var2 == null) {
            return null;
         } else {
            int var3 = var1.length;
            Object var4 = var2.getRoot();
            if (var4 == null) {
               return null;
            } else {
               TreePath var5 = new TreePath(var4);

               for(int var6 = 0; var6 < var3; ++var6) {
                  var4 = var2.getChild(var4, var1[var6]);
                  if (var4 == null) {
                     return null;
                  }

                  var5 = var5.pathByAddingChild(var4);
               }

               return var5;
            }
         }
      }
   }

   public Dimension getPreferredScrollableViewportSize() {
      int var1 = this.getPreferredSize().width;
      int var2 = this.getVisibleRowCount();
      int var3 = -1;
      if (this.isFixedRowHeight()) {
         var3 = var2 * this.getRowHeight();
      } else {
         TreeUI var4 = this.getUI();
         if (var4 != null && var2 > 0) {
            int var5 = var4.getRowCount(this);
            Rectangle var6;
            if (var5 >= var2) {
               var6 = this.getRowBounds(var2 - 1);
               if (var6 != null) {
                  var3 = var6.y + var6.height;
               }
            } else if (var5 > 0) {
               var6 = this.getRowBounds(0);
               if (var6 != null) {
                  var3 = var6.height * var2;
               }
            }
         }

         if (var3 == -1) {
            var3 = 16 * var2;
         }
      }

      return new Dimension(var1, var3);
   }

   public int getScrollableUnitIncrement(Rectangle var1, int var2, int var3) {
      if (var2 == 1) {
         int var5 = this.getClosestRowForLocation(0, var1.y);
         if (var5 != -1) {
            Rectangle var4 = this.getRowBounds(var5);
            if (var4.y != var1.y) {
               if (var3 < 0) {
                  return Math.max(0, var1.y - var4.y);
               }

               return var4.y + var4.height - var1.y;
            }

            if (var3 >= 0) {
               return var4.height;
            }

            if (var5 != 0) {
               var4 = this.getRowBounds(var5 - 1);
               return var4.height;
            }
         }

         return 0;
      } else {
         return 4;
      }
   }

   public int getScrollableBlockIncrement(Rectangle var1, int var2, int var3) {
      return var2 == 1 ? var1.height : var1.width;
   }

   public boolean getScrollableTracksViewportWidth() {
      Container var1 = SwingUtilities.getUnwrappedParent(this);
      if (var1 instanceof JViewport) {
         return var1.getWidth() > this.getPreferredSize().width;
      } else {
         return false;
      }
   }

   public boolean getScrollableTracksViewportHeight() {
      Container var1 = SwingUtilities.getUnwrappedParent(this);
      if (var1 instanceof JViewport) {
         return var1.getHeight() > this.getPreferredSize().height;
      } else {
         return false;
      }
   }

   protected void setExpandedState(TreePath var1, boolean var2) {
      if (var1 != null) {
         TreePath var4 = var1.getParentPath();
         Stack var3;
         if (this.expandedStack.size() == 0) {
            var3 = new Stack();
         } else {
            var3 = (Stack)this.expandedStack.pop();
         }

         try {
            label190:
            while(true) {
               if (var4 == null) {
                  int var5 = var3.size() - 1;

                  while(true) {
                     if (var5 < 0) {
                        break label190;
                     }

                     var4 = (TreePath)var3.pop();
                     if (!this.isExpanded(var4)) {
                        try {
                           this.fireTreeWillExpand(var4);
                        } catch (ExpandVetoException var14) {
                           return;
                        }

                        this.expandedState.put(var4, Boolean.TRUE);
                        this.fireTreeExpanded(var4);
                        if (this.accessibleContext != null) {
                           ((JTree.AccessibleJTree)this.accessibleContext).fireVisibleDataPropertyChange();
                        }
                     }

                     --var5;
                  }
               }

               if (this.isExpanded(var4)) {
                  var4 = null;
               } else {
                  var3.push(var4);
                  var4 = var4.getParentPath();
               }
            }
         } finally {
            if (this.expandedStack.size() < TEMP_STACK_SIZE) {
               var3.removeAllElements();
               this.expandedStack.push(var3);
            }

         }

         Object var16;
         if (!var2) {
            var16 = this.expandedState.get(var1);
            if (var16 != null && (Boolean)var16) {
               try {
                  this.fireTreeWillCollapse(var1);
               } catch (ExpandVetoException var13) {
                  return;
               }

               this.expandedState.put(var1, Boolean.FALSE);
               this.fireTreeCollapsed(var1);
               if (this.removeDescendantSelectedPaths(var1, false) && !this.isPathSelected(var1)) {
                  this.addSelectionPath(var1);
               }

               if (this.accessibleContext != null) {
                  ((JTree.AccessibleJTree)this.accessibleContext).fireVisibleDataPropertyChange();
               }
            }
         } else {
            var16 = this.expandedState.get(var1);
            if (var16 == null || !(Boolean)var16) {
               try {
                  this.fireTreeWillExpand(var1);
               } catch (ExpandVetoException var12) {
                  return;
               }

               this.expandedState.put(var1, Boolean.TRUE);
               this.fireTreeExpanded(var1);
               if (this.accessibleContext != null) {
                  ((JTree.AccessibleJTree)this.accessibleContext).fireVisibleDataPropertyChange();
               }
            }
         }
      }

   }

   protected Enumeration<TreePath> getDescendantToggledPaths(TreePath var1) {
      if (var1 == null) {
         return null;
      } else {
         Vector var2 = new Vector();
         Enumeration var3 = this.expandedState.keys();

         while(var3.hasMoreElements()) {
            TreePath var4 = (TreePath)var3.nextElement();
            if (var1.isDescendant(var4)) {
               var2.addElement(var4);
            }
         }

         return var2.elements();
      }
   }

   protected void removeDescendantToggledPaths(Enumeration<TreePath> var1) {
      if (var1 != null) {
         while(true) {
            Enumeration var2;
            do {
               if (!var1.hasMoreElements()) {
                  return;
               }

               var2 = this.getDescendantToggledPaths((TreePath)var1.nextElement());
            } while(var2 == null);

            while(var2.hasMoreElements()) {
               this.expandedState.remove(var2.nextElement());
            }
         }
      }
   }

   protected void clearToggledPaths() {
      this.expandedState.clear();
   }

   protected TreeModelListener createTreeModelListener() {
      return new JTree.TreeModelHandler();
   }

   protected boolean removeDescendantSelectedPaths(TreePath var1, boolean var2) {
      TreePath[] var3 = this.getDescendantSelectedPaths(var1, var2);
      if (var3 != null) {
         this.getSelectionModel().removeSelectionPaths(var3);
         return true;
      } else {
         return false;
      }
   }

   private TreePath[] getDescendantSelectedPaths(TreePath var1, boolean var2) {
      TreeSelectionModel var3 = this.getSelectionModel();
      TreePath[] var4 = var3 != null ? var3.getSelectionPaths() : null;
      if (var4 == null) {
         return null;
      } else {
         boolean var5 = false;

         for(int var6 = var4.length - 1; var6 >= 0; --var6) {
            if (var4[var6] == null || !var1.isDescendant(var4[var6]) || var1.equals(var4[var6]) && !var2) {
               var4[var6] = null;
            } else {
               var5 = true;
            }
         }

         if (!var5) {
            var4 = null;
         }

         return var4;
      }
   }

   void removeDescendantSelectedPaths(TreeModelEvent var1) {
      TreePath var2 = SwingUtilities2.getTreePath(var1, this.getModel());
      Object[] var3 = var1.getChildren();
      TreeSelectionModel var4 = this.getSelectionModel();
      if (var4 != null && var2 != null && var3 != null && var3.length > 0) {
         for(int var5 = var3.length - 1; var5 >= 0; --var5) {
            this.removeDescendantSelectedPaths(var2.pathByAddingChild(var3[var5]), true);
         }
      }

   }

   void setUIProperty(String var1, Object var2) {
      if (var1 == "rowHeight") {
         if (!this.rowHeightSet) {
            this.setRowHeight(((Number)var2).intValue());
            this.rowHeightSet = false;
         }
      } else if (var1 == "scrollsOnExpand") {
         if (!this.scrollsOnExpandSet) {
            this.setScrollsOnExpand((Boolean)var2);
            this.scrollsOnExpandSet = false;
         }
      } else if (var1 == "showsRootHandles") {
         if (!this.showsRootHandlesSet) {
            this.setShowsRootHandles((Boolean)var2);
            this.showsRootHandlesSet = false;
         }
      } else {
         super.setUIProperty(var1, var2);
      }

   }

   protected String paramString() {
      String var1 = this.rootVisible ? "true" : "false";
      String var2 = this.showsRootHandles ? "true" : "false";
      String var3 = this.editable ? "true" : "false";
      String var4 = this.largeModel ? "true" : "false";
      String var5 = this.invokesStopCellEditing ? "true" : "false";
      String var6 = this.scrollsOnExpand ? "true" : "false";
      return super.paramString() + ",editable=" + var3 + ",invokesStopCellEditing=" + var5 + ",largeModel=" + var4 + ",rootVisible=" + var1 + ",rowHeight=" + this.rowHeight + ",scrollsOnExpand=" + var6 + ",showsRootHandles=" + var2 + ",toggleClickCount=" + this.toggleClickCount + ",visibleRowCount=" + this.visibleRowCount;
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JTree.AccessibleJTree();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJTree extends JComponent.AccessibleJComponent implements AccessibleSelection, TreeSelectionListener, TreeModelListener, TreeExpansionListener {
      TreePath leadSelectionPath;
      Accessible leadSelectionAccessible;

      public AccessibleJTree() {
         super();
         TreeModel var2 = JTree.this.getModel();
         if (var2 != null) {
            var2.addTreeModelListener(this);
         }

         JTree.this.addTreeExpansionListener(this);
         JTree.this.addTreeSelectionListener(this);
         this.leadSelectionPath = JTree.this.getLeadSelectionPath();
         this.leadSelectionAccessible = this.leadSelectionPath != null ? new JTree.AccessibleJTree.AccessibleJTreeNode(JTree.this, this.leadSelectionPath, JTree.this) : null;
      }

      public void valueChanged(TreeSelectionEvent var1) {
         this.firePropertyChange("AccessibleSelection", false, true);
      }

      public void fireVisibleDataPropertyChange() {
         this.firePropertyChange("AccessibleVisibleData", false, true);
      }

      public void treeNodesChanged(TreeModelEvent var1) {
         this.fireVisibleDataPropertyChange();
      }

      public void treeNodesInserted(TreeModelEvent var1) {
         this.fireVisibleDataPropertyChange();
      }

      public void treeNodesRemoved(TreeModelEvent var1) {
         this.fireVisibleDataPropertyChange();
      }

      public void treeStructureChanged(TreeModelEvent var1) {
         this.fireVisibleDataPropertyChange();
      }

      public void treeCollapsed(TreeExpansionEvent var1) {
         this.fireVisibleDataPropertyChange();
         TreePath var2 = var1.getPath();
         if (var2 != null) {
            JTree.AccessibleJTree.AccessibleJTreeNode var3 = new JTree.AccessibleJTree.AccessibleJTreeNode(JTree.this, var2, (Accessible)null);
            PropertyChangeEvent var4 = new PropertyChangeEvent(var3, "AccessibleState", AccessibleState.EXPANDED, AccessibleState.COLLAPSED);
            this.firePropertyChange("AccessibleState", (Object)null, var4);
         }

      }

      public void treeExpanded(TreeExpansionEvent var1) {
         this.fireVisibleDataPropertyChange();
         TreePath var2 = var1.getPath();
         if (var2 != null) {
            JTree.AccessibleJTree.AccessibleJTreeNode var3 = new JTree.AccessibleJTree.AccessibleJTreeNode(JTree.this, var2, (Accessible)null);
            PropertyChangeEvent var4 = new PropertyChangeEvent(var3, "AccessibleState", AccessibleState.COLLAPSED, AccessibleState.EXPANDED);
            this.firePropertyChange("AccessibleState", (Object)null, var4);
         }

      }

      void fireActiveDescendantPropertyChange(TreePath var1, TreePath var2) {
         if (var1 != var2) {
            JTree.AccessibleJTree.AccessibleJTreeNode var3 = var1 != null ? new JTree.AccessibleJTree.AccessibleJTreeNode(JTree.this, var1, (Accessible)null) : null;
            JTree.AccessibleJTree.AccessibleJTreeNode var4 = var2 != null ? new JTree.AccessibleJTree.AccessibleJTreeNode(JTree.this, var2, (Accessible)null) : null;
            this.firePropertyChange("AccessibleActiveDescendant", var3, var4);
         }

      }

      private AccessibleContext getCurrentAccessibleContext() {
         Component var1 = this.getCurrentComponent();
         return var1 instanceof Accessible ? var1.getAccessibleContext() : null;
      }

      private Component getCurrentComponent() {
         TreeModel var1 = JTree.this.getModel();
         if (var1 == null) {
            return null;
         } else {
            Object var2 = var1.getRoot();
            if (var2 == null) {
               return null;
            } else {
               TreePath var3 = new TreePath(var2);
               if (JTree.this.isVisible(var3)) {
                  TreeCellRenderer var4 = JTree.this.getCellRenderer();
                  TreeUI var5 = JTree.this.getUI();
                  if (var5 != null) {
                     int var6 = var5.getRowForPath(JTree.this, var3);
                     int var7 = JTree.this.getLeadSelectionRow();
                     boolean var8 = JTree.this.isFocusOwner() && var7 == var6;
                     boolean var9 = JTree.this.isPathSelected(var3);
                     boolean var10 = JTree.this.isExpanded(var3);
                     return var4.getTreeCellRendererComponent(JTree.this, var2, var9, var10, var1.isLeaf(var2), var6, var8);
                  }
               }

               return null;
            }
         }
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.TREE;
      }

      public Accessible getAccessibleAt(Point var1) {
         TreePath var2 = JTree.this.getClosestPathForLocation(var1.x, var1.y);
         return var2 != null ? new JTree.AccessibleJTree.AccessibleJTreeNode(JTree.this, var2, (Accessible)null) : null;
      }

      public int getAccessibleChildrenCount() {
         TreeModel var1 = JTree.this.getModel();
         if (var1 == null) {
            return 0;
         } else if (JTree.this.isRootVisible()) {
            return 1;
         } else {
            Object var2 = var1.getRoot();
            return var2 == null ? 0 : var1.getChildCount(var2);
         }
      }

      public Accessible getAccessibleChild(int var1) {
         TreeModel var2 = JTree.this.getModel();
         if (var2 == null) {
            return null;
         } else {
            Object var3 = var2.getRoot();
            if (var3 == null) {
               return null;
            } else if (JTree.this.isRootVisible()) {
               if (var1 == 0) {
                  Object[] var8 = new Object[]{var3};
                  if (var8[0] == null) {
                     return null;
                  } else {
                     TreePath var9 = new TreePath(var8);
                     return new JTree.AccessibleJTree.AccessibleJTreeNode(JTree.this, var9, JTree.this);
                  }
               } else {
                  return null;
               }
            } else {
               int var4 = var2.getChildCount(var3);
               if (var1 >= 0 && var1 < var4) {
                  Object var5 = var2.getChild(var3, var1);
                  if (var5 == null) {
                     return null;
                  } else {
                     Object[] var6 = new Object[]{var3, var5};
                     TreePath var7 = new TreePath(var6);
                     return new JTree.AccessibleJTree.AccessibleJTreeNode(JTree.this, var7, JTree.this);
                  }
               } else {
                  return null;
               }
            }
         }
      }

      public int getAccessibleIndexInParent() {
         return super.getAccessibleIndexInParent();
      }

      public AccessibleSelection getAccessibleSelection() {
         return this;
      }

      public int getAccessibleSelectionCount() {
         Object[] var1 = new Object[]{JTree.this.treeModel.getRoot()};
         if (var1[0] == null) {
            return 0;
         } else {
            TreePath var2 = new TreePath(var1);
            return JTree.this.isPathSelected(var2) ? 1 : 0;
         }
      }

      public Accessible getAccessibleSelection(int var1) {
         if (var1 == 0) {
            Object[] var2 = new Object[]{JTree.this.treeModel.getRoot()};
            if (var2[0] == null) {
               return null;
            }

            TreePath var3 = new TreePath(var2);
            if (JTree.this.isPathSelected(var3)) {
               return new JTree.AccessibleJTree.AccessibleJTreeNode(JTree.this, var3, JTree.this);
            }
         }

         return null;
      }

      public boolean isAccessibleChildSelected(int var1) {
         if (var1 == 0) {
            Object[] var2 = new Object[]{JTree.this.treeModel.getRoot()};
            if (var2[0] == null) {
               return false;
            } else {
               TreePath var3 = new TreePath(var2);
               return JTree.this.isPathSelected(var3);
            }
         } else {
            return false;
         }
      }

      public void addAccessibleSelection(int var1) {
         TreeModel var2 = JTree.this.getModel();
         if (var2 != null && var1 == 0) {
            Object[] var3 = new Object[]{var2.getRoot()};
            if (var3[0] == null) {
               return;
            }

            TreePath var4 = new TreePath(var3);
            JTree.this.addSelectionPath(var4);
         }

      }

      public void removeAccessibleSelection(int var1) {
         TreeModel var2 = JTree.this.getModel();
         if (var2 != null && var1 == 0) {
            Object[] var3 = new Object[]{var2.getRoot()};
            if (var3[0] == null) {
               return;
            }

            TreePath var4 = new TreePath(var3);
            JTree.this.removeSelectionPath(var4);
         }

      }

      public void clearAccessibleSelection() {
         int var1 = this.getAccessibleChildrenCount();

         for(int var2 = 0; var2 < var1; ++var2) {
            this.removeAccessibleSelection(var2);
         }

      }

      public void selectAllAccessibleSelection() {
         TreeModel var1 = JTree.this.getModel();
         if (var1 != null) {
            Object[] var2 = new Object[]{var1.getRoot()};
            if (var2[0] == null) {
               return;
            }

            TreePath var3 = new TreePath(var2);
            JTree.this.addSelectionPath(var3);
         }

      }

      protected class AccessibleJTreeNode extends AccessibleContext implements Accessible, AccessibleComponent, AccessibleSelection, AccessibleAction {
         private JTree tree = null;
         private TreeModel treeModel = null;
         private Object obj = null;
         private TreePath path = null;
         private Accessible accessibleParent = null;
         private int index = 0;
         private boolean isLeaf = false;

         public AccessibleJTreeNode(JTree var2, TreePath var3, Accessible var4) {
            this.tree = var2;
            this.path = var3;
            this.accessibleParent = var4;
            this.treeModel = var2.getModel();
            this.obj = var3.getLastPathComponent();
            if (this.treeModel != null) {
               this.isLeaf = this.treeModel.isLeaf(this.obj);
            }

         }

         private TreePath getChildTreePath(int var1) {
            if (var1 >= 0 && var1 < this.getAccessibleChildrenCount()) {
               Object var2 = this.treeModel.getChild(this.obj, var1);
               Object[] var3 = this.path.getPath();
               Object[] var4 = new Object[var3.length + 1];
               System.arraycopy(var3, 0, var4, 0, var3.length);
               var4[var4.length - 1] = var2;
               return new TreePath(var4);
            } else {
               return null;
            }
         }

         public AccessibleContext getAccessibleContext() {
            return this;
         }

         private AccessibleContext getCurrentAccessibleContext() {
            Component var1 = this.getCurrentComponent();
            return var1 instanceof Accessible ? var1.getAccessibleContext() : null;
         }

         private Component getCurrentComponent() {
            if (this.tree.isVisible(this.path)) {
               TreeCellRenderer var1 = this.tree.getCellRenderer();
               if (var1 == null) {
                  return null;
               }

               TreeUI var2 = this.tree.getUI();
               if (var2 != null) {
                  int var3 = var2.getRowForPath(JTree.this, this.path);
                  boolean var4 = this.tree.isPathSelected(this.path);
                  boolean var5 = this.tree.isExpanded(this.path);
                  boolean var6 = false;
                  return var1.getTreeCellRendererComponent(this.tree, this.obj, var4, var5, this.isLeaf, var3, var6);
               }
            }

            return null;
         }

         public String getAccessibleName() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 != null) {
               String var2 = var1.getAccessibleName();
               return var2 != null && var2 != "" ? var1.getAccessibleName() : null;
            } else {
               return this.accessibleName != null && this.accessibleName != "" ? this.accessibleName : (String)JTree.this.getClientProperty("AccessibleName");
            }
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
            AccessibleStateSet var2;
            if (var1 != null) {
               var2 = var1.getAccessibleStateSet();
            } else {
               var2 = new AccessibleStateSet();
            }

            if (this.isShowing()) {
               var2.add(AccessibleState.SHOWING);
            } else if (var2.contains(AccessibleState.SHOWING)) {
               var2.remove(AccessibleState.SHOWING);
            }

            if (this.isVisible()) {
               var2.add(AccessibleState.VISIBLE);
            } else if (var2.contains(AccessibleState.VISIBLE)) {
               var2.remove(AccessibleState.VISIBLE);
            }

            if (this.tree.isPathSelected(this.path)) {
               var2.add(AccessibleState.SELECTED);
            }

            if (this.path == JTree.this.getLeadSelectionPath()) {
               var2.add(AccessibleState.ACTIVE);
            }

            if (!this.isLeaf) {
               var2.add(AccessibleState.EXPANDABLE);
            }

            if (this.tree.isExpanded(this.path)) {
               var2.add(AccessibleState.EXPANDED);
            } else {
               var2.add(AccessibleState.COLLAPSED);
            }

            if (this.tree.isEditable()) {
               var2.add(AccessibleState.EDITABLE);
            }

            return var2;
         }

         public Accessible getAccessibleParent() {
            if (this.accessibleParent == null) {
               Object[] var1 = this.path.getPath();
               if (var1.length > 1) {
                  Object var2 = var1[var1.length - 2];
                  if (this.treeModel != null) {
                     this.index = this.treeModel.getIndexOfChild(var2, this.obj);
                  }

                  Object[] var3 = new Object[var1.length - 1];
                  System.arraycopy(var1, 0, var3, 0, var1.length - 1);
                  TreePath var4 = new TreePath(var3);
                  this.accessibleParent = AccessibleJTree.this.new AccessibleJTreeNode(this.tree, var4, (Accessible)null);
                  this.setAccessibleParent(this.accessibleParent);
               } else if (this.treeModel != null) {
                  this.accessibleParent = this.tree;
                  this.index = 0;
                  this.setAccessibleParent(this.accessibleParent);
               }
            }

            return this.accessibleParent;
         }

         public int getAccessibleIndexInParent() {
            if (this.accessibleParent == null) {
               this.getAccessibleParent();
            }

            Object[] var1 = this.path.getPath();
            if (var1.length > 1) {
               Object var2 = var1[var1.length - 2];
               if (this.treeModel != null) {
                  this.index = this.treeModel.getIndexOfChild(var2, this.obj);
               }
            }

            return this.index;
         }

         public int getAccessibleChildrenCount() {
            return this.treeModel.getChildCount(this.obj);
         }

         public Accessible getAccessibleChild(int var1) {
            if (var1 >= 0 && var1 < this.getAccessibleChildrenCount()) {
               Object var2 = this.treeModel.getChild(this.obj, var1);
               Object[] var3 = this.path.getPath();
               Object[] var4 = new Object[var3.length + 1];
               System.arraycopy(var3, 0, var4, 0, var3.length);
               var4[var4.length - 1] = var2;
               TreePath var5 = new TreePath(var4);
               return AccessibleJTree.this.new AccessibleJTreeNode(JTree.this, var5, this);
            } else {
               return null;
            }
         }

         public Locale getLocale() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            return var1 != null ? var1.getLocale() : this.tree.getLocale();
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
            return this;
         }

         public AccessibleComponent getAccessibleComponent() {
            return this;
         }

         public AccessibleSelection getAccessibleSelection() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            return (AccessibleSelection)(var1 != null && this.isLeaf ? this.getCurrentAccessibleContext().getAccessibleSelection() : this);
         }

         public AccessibleText getAccessibleText() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            return var1 != null ? this.getCurrentAccessibleContext().getAccessibleText() : null;
         }

         public AccessibleValue getAccessibleValue() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            return var1 != null ? this.getCurrentAccessibleContext().getAccessibleValue() : null;
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
            Rectangle var1 = this.tree.getPathBounds(this.path);
            Rectangle var2 = this.tree.getVisibleRect();
            return var1 != null && var2 != null && var2.intersects(var1);
         }

         public void setVisible(boolean var1) {
         }

         public boolean isShowing() {
            return this.tree.isShowing() && this.isVisible();
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
            if (this.tree != null) {
               Point var1 = this.tree.getLocationOnScreen();
               Rectangle var2 = this.tree.getPathBounds(this.path);
               if (var1 != null && var2 != null) {
                  Point var3 = new Point(var2.x, var2.y);
                  var3.translate(var1.x, var1.y);
                  return var3;
               } else {
                  return null;
               }
            } else {
               return null;
            }
         }

         protected Point getLocationInJTree() {
            Rectangle var1 = this.tree.getPathBounds(this.path);
            return var1 != null ? var1.getLocation() : null;
         }

         public Point getLocation() {
            Rectangle var1 = this.getBounds();
            return var1 != null ? var1.getLocation() : null;
         }

         public void setLocation(Point var1) {
         }

         public Rectangle getBounds() {
            Rectangle var1 = this.tree.getPathBounds(this.path);
            Accessible var2 = this.getAccessibleParent();
            if (var2 != null && var2 instanceof JTree.AccessibleJTree.AccessibleJTreeNode) {
               Point var3 = ((JTree.AccessibleJTree.AccessibleJTreeNode)var2).getLocationInJTree();
               if (var3 == null || var1 == null) {
                  return null;
               }

               var1.translate(-var3.x, -var3.y);
            }

            return var1;
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
            return this.getBounds().getSize();
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

         public int getAccessibleSelectionCount() {
            int var1 = 0;
            int var2 = this.getAccessibleChildrenCount();

            for(int var3 = 0; var3 < var2; ++var3) {
               TreePath var4 = this.getChildTreePath(var3);
               if (this.tree.isPathSelected(var4)) {
                  ++var1;
               }
            }

            return var1;
         }

         public Accessible getAccessibleSelection(int var1) {
            int var2 = this.getAccessibleChildrenCount();
            if (var1 >= 0 && var1 < var2) {
               int var3 = 0;

               for(int var4 = 0; var4 < var2 && var1 >= var3; ++var4) {
                  TreePath var5 = this.getChildTreePath(var4);
                  if (this.tree.isPathSelected(var5)) {
                     if (var3 == var1) {
                        return AccessibleJTree.this.new AccessibleJTreeNode(this.tree, var5, this);
                     }

                     ++var3;
                  }
               }

               return null;
            } else {
               return null;
            }
         }

         public boolean isAccessibleChildSelected(int var1) {
            int var2 = this.getAccessibleChildrenCount();
            if (var1 >= 0 && var1 < var2) {
               TreePath var3 = this.getChildTreePath(var1);
               return this.tree.isPathSelected(var3);
            } else {
               return false;
            }
         }

         public void addAccessibleSelection(int var1) {
            TreeModel var2 = JTree.this.getModel();
            if (var2 != null && var1 >= 0 && var1 < this.getAccessibleChildrenCount()) {
               TreePath var3 = this.getChildTreePath(var1);
               JTree.this.addSelectionPath(var3);
            }

         }

         public void removeAccessibleSelection(int var1) {
            TreeModel var2 = JTree.this.getModel();
            if (var2 != null && var1 >= 0 && var1 < this.getAccessibleChildrenCount()) {
               TreePath var3 = this.getChildTreePath(var1);
               JTree.this.removeSelectionPath(var3);
            }

         }

         public void clearAccessibleSelection() {
            int var1 = this.getAccessibleChildrenCount();

            for(int var2 = 0; var2 < var1; ++var2) {
               this.removeAccessibleSelection(var2);
            }

         }

         public void selectAllAccessibleSelection() {
            TreeModel var1 = JTree.this.getModel();
            if (var1 != null) {
               int var2 = this.getAccessibleChildrenCount();

               for(int var4 = 0; var4 < var2; ++var4) {
                  TreePath var3 = this.getChildTreePath(var4);
                  JTree.this.addSelectionPath(var3);
               }
            }

         }

         public int getAccessibleActionCount() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 != null) {
               AccessibleAction var2 = var1.getAccessibleAction();
               if (var2 != null) {
                  return var2.getAccessibleActionCount() + (this.isLeaf ? 0 : 1);
               }
            }

            return this.isLeaf ? 0 : 1;
         }

         public String getAccessibleActionDescription(int var1) {
            if (var1 >= 0 && var1 < this.getAccessibleActionCount()) {
               AccessibleContext var2 = this.getCurrentAccessibleContext();
               if (var1 == 0) {
                  return AccessibleAction.TOGGLE_EXPAND;
               } else {
                  if (var2 != null) {
                     AccessibleAction var3 = var2.getAccessibleAction();
                     if (var3 != null) {
                        return var3.getAccessibleActionDescription(var1 - 1);
                     }
                  }

                  return null;
               }
            } else {
               return null;
            }
         }

         public boolean doAccessibleAction(int var1) {
            if (var1 >= 0 && var1 < this.getAccessibleActionCount()) {
               AccessibleContext var2 = this.getCurrentAccessibleContext();
               if (var1 == 0) {
                  if (JTree.this.isExpanded(this.path)) {
                     JTree.this.collapsePath(this.path);
                  } else {
                     JTree.this.expandPath(this.path);
                  }

                  return true;
               } else {
                  if (var2 != null) {
                     AccessibleAction var3 = var2.getAccessibleAction();
                     if (var3 != null) {
                        return var3.doAccessibleAction(var1 - 1);
                     }
                  }

                  return false;
               }
            } else {
               return false;
            }
         }
      }
   }

   public static class DynamicUtilTreeNode extends DefaultMutableTreeNode {
      protected boolean hasChildren;
      protected Object childValue;
      protected boolean loadedChildren = false;

      public static void createChildren(DefaultMutableTreeNode var0, Object var1) {
         int var3;
         int var4;
         if (var1 instanceof Vector) {
            Vector var2 = (Vector)var1;
            var3 = 0;

            for(var4 = var2.size(); var3 < var4; ++var3) {
               var0.add(new JTree.DynamicUtilTreeNode(var2.elementAt(var3), var2.elementAt(var3)));
            }
         } else if (var1 instanceof Hashtable) {
            Hashtable var5 = (Hashtable)var1;
            Enumeration var7 = var5.keys();

            while(var7.hasMoreElements()) {
               Object var8 = var7.nextElement();
               var0.add(new JTree.DynamicUtilTreeNode(var8, var5.get(var8)));
            }
         } else if (var1 instanceof Object[]) {
            Object[] var6 = (Object[])((Object[])var1);
            var3 = 0;

            for(var4 = var6.length; var3 < var4; ++var3) {
               var0.add(new JTree.DynamicUtilTreeNode(var6[var3], var6[var3]));
            }
         }

      }

      public DynamicUtilTreeNode(Object var1, Object var2) {
         super(var1);
         this.childValue = var2;
         if (var2 != null) {
            if (var2 instanceof Vector) {
               this.setAllowsChildren(true);
            } else if (var2 instanceof Hashtable) {
               this.setAllowsChildren(true);
            } else if (var2 instanceof Object[]) {
               this.setAllowsChildren(true);
            } else {
               this.setAllowsChildren(false);
            }
         } else {
            this.setAllowsChildren(false);
         }

      }

      public boolean isLeaf() {
         return !this.getAllowsChildren();
      }

      public int getChildCount() {
         if (!this.loadedChildren) {
            this.loadChildren();
         }

         return super.getChildCount();
      }

      protected void loadChildren() {
         this.loadedChildren = true;
         createChildren(this, this.childValue);
      }

      public TreeNode getChildAt(int var1) {
         if (!this.loadedChildren) {
            this.loadChildren();
         }

         return super.getChildAt(var1);
      }

      public Enumeration children() {
         if (!this.loadedChildren) {
            this.loadChildren();
         }

         return super.children();
      }
   }

   protected class TreeModelHandler implements TreeModelListener {
      public void treeNodesChanged(TreeModelEvent var1) {
      }

      public void treeNodesInserted(TreeModelEvent var1) {
      }

      public void treeStructureChanged(TreeModelEvent var1) {
         if (var1 != null) {
            TreePath var2 = SwingUtilities2.getTreePath(var1, JTree.this.getModel());
            if (var2 != null) {
               if (var2.getPathCount() == 1) {
                  JTree.this.clearToggledPaths();
                  Object var3 = JTree.this.treeModel.getRoot();
                  if (var3 != null && !JTree.this.treeModel.isLeaf(var3)) {
                     JTree.this.expandedState.put(var2, Boolean.TRUE);
                  }
               } else if (JTree.this.expandedState.get(var2) != null) {
                  Vector var6 = new Vector(1);
                  boolean var4 = JTree.this.isExpanded(var2);
                  var6.addElement(var2);
                  JTree.this.removeDescendantToggledPaths(var6.elements());
                  if (var4) {
                     TreeModel var5 = JTree.this.getModel();
                     if (var5 != null && !var5.isLeaf(var2.getLastPathComponent())) {
                        JTree.this.expandedState.put(var2, Boolean.TRUE);
                     } else {
                        JTree.this.collapsePath(var2);
                     }
                  }
               }

               JTree.this.removeDescendantSelectedPaths(var2, false);
            }
         }
      }

      public void treeNodesRemoved(TreeModelEvent var1) {
         if (var1 != null) {
            TreePath var2 = SwingUtilities2.getTreePath(var1, JTree.this.getModel());
            Object[] var3 = var1.getChildren();
            if (var3 != null) {
               Vector var5 = new Vector(Math.max(1, var3.length));

               for(int var6 = var3.length - 1; var6 >= 0; --var6) {
                  TreePath var4 = var2.pathByAddingChild(var3[var6]);
                  if (JTree.this.expandedState.get(var4) != null) {
                     var5.addElement(var4);
                  }
               }

               if (var5.size() > 0) {
                  JTree.this.removeDescendantToggledPaths(var5.elements());
               }

               TreeModel var7 = JTree.this.getModel();
               if (var7 == null || var7.isLeaf(var2.getLastPathComponent())) {
                  JTree.this.expandedState.remove(var2);
               }

               JTree.this.removeDescendantSelectedPaths(var1);
            }
         }
      }
   }

   protected class TreeSelectionRedirector implements Serializable, TreeSelectionListener {
      public void valueChanged(TreeSelectionEvent var1) {
         TreeSelectionEvent var2 = (TreeSelectionEvent)var1.cloneWithSource(JTree.this);
         JTree.this.fireValueChanged(var2);
      }
   }

   protected static class EmptySelectionModel extends DefaultTreeSelectionModel {
      protected static final JTree.EmptySelectionModel sharedInstance = new JTree.EmptySelectionModel();

      public static JTree.EmptySelectionModel sharedInstance() {
         return sharedInstance;
      }

      public void setSelectionPaths(TreePath[] var1) {
      }

      public void addSelectionPaths(TreePath[] var1) {
      }

      public void removeSelectionPaths(TreePath[] var1) {
      }

      public void setSelectionMode(int var1) {
      }

      public void setRowMapper(RowMapper var1) {
      }

      public void addTreeSelectionListener(TreeSelectionListener var1) {
      }

      public void removeTreeSelectionListener(TreeSelectionListener var1) {
      }

      public void addPropertyChangeListener(PropertyChangeListener var1) {
      }

      public void removePropertyChangeListener(PropertyChangeListener var1) {
      }
   }

   private class TreeTimer extends Timer {
      public TreeTimer() {
         super(2000, (ActionListener)null);
         this.setRepeats(false);
      }

      public void fireActionPerformed(ActionEvent var1) {
         JTree.this.expandRow(JTree.this.expandRow);
      }
   }

   public static final class DropLocation extends TransferHandler.DropLocation {
      private final TreePath path;
      private final int index;

      private DropLocation(Point var1, TreePath var2, int var3) {
         super(var1);
         this.path = var2;
         this.index = var3;
      }

      public int getChildIndex() {
         return this.index;
      }

      public TreePath getPath() {
         return this.path;
      }

      public String toString() {
         return this.getClass().getName() + "[dropPoint=" + this.getDropPoint() + ",path=" + this.path + ",childIndex=" + this.index + "]";
      }

      // $FF: synthetic method
      DropLocation(Point var1, TreePath var2, int var3, Object var4) {
         this(var1, var2, var3);
      }
   }
}
