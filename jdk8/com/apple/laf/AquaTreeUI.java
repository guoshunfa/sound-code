package com.apple.laf;

import apple.laf.JRSUIConstants;
import apple.laf.JRSUIState;
import apple.laf.JRSUIStateFactory;
import apple.laf.JRSUIUtils;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreePath;

public class AquaTreeUI extends BasicTreeUI {
   private static final String LINE_STYLE = "JTree.lineStyle";
   private static final String LEG_LINE_STYLE_STRING = "Angled";
   private static final String HORIZ_STYLE_STRING = "Horizontal";
   private static final String NO_STYLE_STRING = "None";
   private static final int LEG_LINE_STYLE = 2;
   private static final int HORIZ_LINE_STYLE = 1;
   private static final int NO_LINE_STYLE = 0;
   private int lineStyle = 1;
   private final PropertyChangeListener lineStyleListener = new AquaTreeUI.LineListener();
   protected TreePath fTrackingPath;
   protected boolean fIsPressed = false;
   protected boolean fIsInBounds = false;
   protected int fAnimationFrame = -1;
   protected AquaTreeUI.TreeArrowMouseInputHandler fMouseHandler;
   protected final AquaPainter<JRSUIState.AnimationFrameState> painter = AquaPainter.create(JRSUIStateFactory.getDisclosureTriangle());

   public static ComponentUI createUI(JComponent var0) {
      return new AquaTreeUI();
   }

   public void installUI(JComponent var1) {
      super.installUI(var1);
      Object var2 = var1.getClientProperty("JTree.lineStyle");
      this.decodeLineStyle(var2);
      var1.addPropertyChangeListener(this.lineStyleListener);
   }

   public void uninstallUI(JComponent var1) {
      var1.removePropertyChangeListener(this.lineStyleListener);
      super.uninstallUI(var1);
   }

   protected FocusListener createFocusListener() {
      return new AquaTreeUI.FocusHandler();
   }

   protected void decodeLineStyle(Object var1) {
      if (var1 != null && !"None".equals(var1)) {
         if ("Angled".equals(var1)) {
            this.lineStyle = 2;
         } else if ("Horizontal".equals(var1)) {
            this.lineStyle = 1;
         }

      } else {
         this.lineStyle = 0;
      }
   }

   public TreePath getClosestPathForLocation(JTree var1, int var2, int var3) {
      if (var1 != null && this.treeState != null) {
         Insets var4 = var1.getInsets();
         if (var4 == null) {
            var4 = new Insets(0, 0, 0, 0);
         }

         return this.treeState.getPathClosestTo(var2 - var4.left, var3 - var4.top);
      } else {
         return null;
      }
   }

   public void paint(Graphics var1, JComponent var2) {
      super.paint(var1, var2);
      if (this.lineStyle == 1 && !this.largeModel) {
         this.paintHorizontalSeparators(var1, var2);
      }

   }

   protected void paintHorizontalSeparators(Graphics var1, JComponent var2) {
      var1.setColor(UIManager.getColor("Tree.line"));
      Rectangle var3 = var1.getClipBounds();
      int var4 = this.getRowForPath(this.tree, this.getClosestPathForLocation(this.tree, 0, var3.y));
      int var5 = this.getRowForPath(this.tree, this.getClosestPathForLocation(this.tree, 0, var3.y + var3.height - 1));
      if (var4 > -1 && var5 > -1) {
         for(int var6 = var4; var6 <= var5; ++var6) {
            TreePath var7 = this.getPathForRow(this.tree, var6);
            if (var7 != null && var7.getPathCount() == 2) {
               Rectangle var8 = this.getPathBounds(this.tree, this.getPathForRow(this.tree, var6));
               if (var8 != null) {
                  var1.drawLine(var3.x, var8.y, var3.x + var3.width, var8.y);
               }
            }
         }

      }
   }

   protected void paintVerticalPartOfLeg(Graphics var1, Rectangle var2, Insets var3, TreePath var4) {
      if (this.lineStyle == 2) {
         super.paintVerticalPartOfLeg(var1, var2, var3, var4);
      }

   }

   protected void paintHorizontalPartOfLeg(Graphics var1, Rectangle var2, Insets var3, Rectangle var4, TreePath var5, int var6, boolean var7, boolean var8, boolean var9) {
      if (this.lineStyle == 2) {
         super.paintHorizontalPartOfLeg(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      }

   }

   protected void paintExpandControl(Graphics var1, Rectangle var2, Insets var3, Rectangle var4, TreePath var5, int var6, boolean var7, boolean var8, boolean var9) {
      Object var10 = var5.getLastPathComponent();
      if (!var9 && (!var8 || this.treeModel.getChildCount(var10) > 0)) {
         boolean var11 = AquaUtils.isLeftToRight(this.tree);
         JRSUIConstants.State var12 = this.getState(var5);
         if (this.fAnimationFrame == -1 && var12 != JRSUIConstants.State.PRESSED) {
            super.paintExpandControl(var1, var2, var3, var4, var5, var6, var7, var8, var9);
         } else {
            Icon var13 = var7 ? this.getExpandedIcon() : this.getCollapsedIcon();
            if (!(var13 instanceof UIResource)) {
               super.paintExpandControl(var1, var2, var3, var4, var5, var6, var7, var8, var9);
            } else {
               int var14;
               if (var11) {
                  var14 = var4.x - (this.getRightChildIndent() - 1);
               } else {
                  var14 = var2.x + var2.width / 2;
               }

               int var15 = var4.y + var4.height / 2;
               int var16 = var14 - var13.getIconWidth() / 2;
               int var17 = var15 - var13.getIconHeight() / 2;
               int var18 = var13.getIconHeight();
               this.setupPainter(var12, var7, var11);
               this.painter.paint(var1, this.tree, var16, var17, 20, var18);
            }
         }
      }
   }

   public Icon getCollapsedIcon() {
      Icon var1 = super.getCollapsedIcon();
      if (AquaUtils.isLeftToRight(this.tree)) {
         return var1;
      } else {
         return !(var1 instanceof UIResource) ? var1 : UIManager.getIcon("Tree.rightToLeftCollapsedIcon");
      }
   }

   protected void setupPainter(JRSUIConstants.State var1, boolean var2, boolean var3) {
      if (!this.fIsInBounds && var1 == JRSUIConstants.State.PRESSED) {
         var1 = JRSUIConstants.State.ACTIVE;
      }

      ((JRSUIState.AnimationFrameState)this.painter.state).set(var1);
      if (JRSUIUtils.Tree.useLegacyTreeKnobs()) {
         if (this.fAnimationFrame == -1) {
            ((JRSUIState.AnimationFrameState)this.painter.state).set(var2 ? JRSUIConstants.Direction.DOWN : JRSUIConstants.Direction.RIGHT);
         } else {
            ((JRSUIState.AnimationFrameState)this.painter.state).set(JRSUIConstants.Direction.NONE);
            ((JRSUIState.AnimationFrameState)this.painter.state).setAnimationFrame(this.fAnimationFrame - 1);
         }
      } else {
         ((JRSUIState.AnimationFrameState)this.painter.state).set(this.getDirection(var2, var3));
         ((JRSUIState.AnimationFrameState)this.painter.state).setAnimationFrame(this.fAnimationFrame);
      }

   }

   protected JRSUIConstants.Direction getDirection(boolean var1, boolean var2) {
      if (var1 && this.fAnimationFrame == -1) {
         return JRSUIConstants.Direction.DOWN;
      } else {
         return var2 ? JRSUIConstants.Direction.RIGHT : JRSUIConstants.Direction.LEFT;
      }
   }

   protected JRSUIConstants.State getState(TreePath var1) {
      if (!this.tree.isEnabled()) {
         return JRSUIConstants.State.DISABLED;
      } else {
         return this.fIsPressed && this.fTrackingPath.equals(var1) ? JRSUIConstants.State.PRESSED : JRSUIConstants.State.ACTIVE;
      }
   }

   protected void handleExpandControlClick(TreePath var1, int var2, int var3) {
      this.fMouseHandler = new AquaTreeUI.TreeArrowMouseInputHandler(var1);
   }

   protected boolean isToggleSelectionEvent(MouseEvent var1) {
      return SwingUtilities.isLeftMouseButton(var1) && var1.isMetaDown();
   }

   protected PropertyChangeListener createPropertyChangeListener() {
      return new AquaTreeUI.MacPropertyChangeHandler();
   }

   protected int getRowForPath(TreePath var1) {
      return this.treeState.getRowForPath(var1);
   }

   protected Rectangle getPathArrowBounds(TreePath var1) {
      Rectangle var2 = this.getPathBounds(this.tree, var1);
      Insets var3 = this.tree.getInsets();
      if (this.getExpandedIcon() != null) {
         var2.width = this.getExpandedIcon().getIconWidth();
      } else {
         var2.width = 8;
      }

      int var4 = var3 != null ? var3.left : 0;
      if (AquaUtils.isLeftToRight(this.tree)) {
         var4 += (var1.getPathCount() + this.depthOffset - 2) * this.totalChildIndent + this.getLeftChildIndent() - var2.width / 2;
      } else {
         var4 += this.tree.getWidth() - 1 - (var1.getPathCount() - 2 + this.depthOffset) * this.totalChildIndent - this.getLeftChildIndent() - var2.width / 2;
      }

      var2.x = var4;
      return var2;
   }

   protected void installKeyboardActions() {
      super.installKeyboardActions();
      this.tree.getActionMap().put("aquaExpandNode", new AquaTreeUI.KeyboardExpandCollapseAction(true, false));
      this.tree.getActionMap().put("aquaCollapseNode", new AquaTreeUI.KeyboardExpandCollapseAction(false, false));
      this.tree.getActionMap().put("aquaFullyExpandNode", new AquaTreeUI.KeyboardExpandCollapseAction(true, true));
      this.tree.getActionMap().put("aquaFullyCollapseNode", new AquaTreeUI.KeyboardExpandCollapseAction(false, true));
   }

   void expandNode(int var1, boolean var2) {
      TreePath var3 = this.getPathForRow(this.tree, var1);
      if (var3 != null) {
         this.tree.expandPath(var3);
         if (var2) {
            this.expandAllNodes(var3, var1 + 1);
         }
      }
   }

   void expandAllNodes(TreePath var1, int var2) {
      int var3 = var2;

      while(true) {
         TreePath var4 = this.getPathForRow(this.tree, var3);
         if (!var1.isDescendant(var4)) {
            return;
         }

         this.tree.expandPath(var4);
         ++var3;
      }
   }

   void collapseNode(int var1, boolean var2) {
      TreePath var3 = this.getPathForRow(this.tree, var1);
      if (var3 != null) {
         if (var2) {
            this.collapseAllNodes(var3, var1 + 1);
         }

         this.tree.collapsePath(var3);
      }
   }

   void collapseAllNodes(TreePath var1, int var2) {
      int var3 = -1;

      int var4;
      TreePath var5;
      for(var4 = var2; var3 == -1; ++var4) {
         var5 = this.getPathForRow(this.tree, var4);
         if (!var1.isDescendant(var5)) {
            var3 = var4 - 1;
         }
      }

      for(var4 = var3; var4 >= var2; --var4) {
         var5 = this.getPathForRow(this.tree, var4);
         this.tree.collapsePath(var5);
      }

   }

   class KeyboardExpandCollapseAction extends AbstractAction {
      final boolean expand;
      final boolean recursive;

      public KeyboardExpandCollapseAction(boolean var2, boolean var3) {
         this.expand = var2;
         this.recursive = var3;
      }

      public void actionPerformed(ActionEvent var1) {
         if (AquaTreeUI.this.tree != null && 0 <= AquaTreeUI.this.getRowCount(AquaTreeUI.this.tree)) {
            TreePath[] var2 = AquaTreeUI.this.tree.getSelectionPaths();
            if (var2 != null) {
               for(int var3 = var2.length - 1; var3 >= 0; --var3) {
                  TreePath var4 = var2[var3];
                  if (this.expand) {
                     AquaTreeUI.this.expandNode(AquaTreeUI.this.tree.getRowForPath(var4), this.recursive);
                  } else if (var2.length == 1 && AquaTreeUI.this.tree.isCollapsed(var4)) {
                     TreePath var5 = var4.getParentPath();
                     if (var5 != null && (var5.getParentPath() != null || AquaTreeUI.this.tree.isRootVisible())) {
                        AquaTreeUI.this.tree.scrollPathToVisible(var5);
                        AquaTreeUI.this.tree.setSelectionPath(var5);
                     }
                  } else {
                     AquaTreeUI.this.collapseNode(AquaTreeUI.this.tree.getRowForPath(var4), this.recursive);
                  }
               }

            }
         }
      }

      public boolean isEnabled() {
         return AquaTreeUI.this.tree != null && AquaTreeUI.this.tree.isEnabled();
      }
   }

   class TreeArrowMouseInputHandler extends MouseInputAdapter {
      protected Rectangle fPathBounds = new Rectangle();
      protected boolean fIsLeaf;
      protected boolean fIsExpanded;
      protected boolean fHasBeenExpanded;
      protected Rectangle fBounds;
      protected Rectangle fVisibleRect;
      int fTrackingRow;
      Insets fInsets;
      Color fBackground;

      TreeArrowMouseInputHandler(TreePath var2) {
         AquaTreeUI.this.fTrackingPath = var2;
         AquaTreeUI.this.fIsPressed = true;
         AquaTreeUI.this.fIsInBounds = true;
         this.fPathBounds = AquaTreeUI.this.getPathArrowBounds(var2);
         AquaTreeUI.this.tree.addMouseListener(this);
         AquaTreeUI.this.tree.addMouseMotionListener(this);
         this.fBackground = AquaTreeUI.this.tree.getBackground();
         if (!AquaTreeUI.this.tree.isOpaque()) {
            Container var3 = AquaTreeUI.this.tree.getParent();
            if (var3 != null) {
               this.fBackground = var3.getBackground();
            }
         }

         this.fVisibleRect = AquaTreeUI.this.tree.getVisibleRect();
         this.fInsets = AquaTreeUI.this.tree.getInsets();
         if (this.fInsets == null) {
            this.fInsets = new Insets(0, 0, 0, 0);
         }

         this.fIsLeaf = AquaTreeUI.this.treeModel.isLeaf(var2.getLastPathComponent());
         if (this.fIsLeaf) {
            this.fIsExpanded = this.fHasBeenExpanded = false;
         } else {
            this.fIsExpanded = AquaTreeUI.this.treeState.getExpandedState(var2);
            this.fHasBeenExpanded = AquaTreeUI.this.tree.hasBeenExpanded(var2);
         }

         Rectangle var4 = new Rectangle();
         this.fBounds = AquaTreeUI.this.treeState.getBounds(AquaTreeUI.this.fTrackingPath, var4);
         Rectangle var10000 = this.fBounds;
         var10000.x += this.fInsets.left;
         var10000 = this.fBounds;
         var10000.y += this.fInsets.top;
         this.fTrackingRow = AquaTreeUI.this.getRowForPath(AquaTreeUI.this.fTrackingPath);
         this.paintOneControl();
      }

      public void mouseDragged(MouseEvent var1) {
         AquaTreeUI.this.fIsInBounds = this.fPathBounds.contains(var1.getX(), var1.getY());
         this.paintOneControl();
      }

      public void mouseExited(MouseEvent var1) {
         AquaTreeUI.this.fIsInBounds = this.fPathBounds.contains(var1.getX(), var1.getY());
         this.paintOneControl();
      }

      public void mouseReleased(MouseEvent var1) {
         if (AquaTreeUI.this.tree != null) {
            if (AquaTreeUI.this.fIsPressed) {
               boolean var2 = AquaTreeUI.this.fIsInBounds;
               AquaTreeUI.this.fIsPressed = false;
               AquaTreeUI.this.fIsInBounds = false;
               if (var2) {
                  this.fIsExpanded = !this.fIsExpanded;
                  this.paintAnimation(this.fIsExpanded);
                  if (var1.isAltDown()) {
                     if (this.fIsExpanded) {
                        AquaTreeUI.this.expandNode(this.fTrackingRow, true);
                     } else {
                        AquaTreeUI.this.collapseNode(this.fTrackingRow, true);
                     }
                  } else {
                     AquaTreeUI.this.toggleExpandState(AquaTreeUI.this.fTrackingPath);
                  }
               }
            }

            AquaTreeUI.this.fTrackingPath = null;
            this.removeFromSource();
         }
      }

      protected void paintAnimation(boolean var1) {
         if (var1) {
            this.paintAnimationFrame(1);
            this.paintAnimationFrame(2);
            this.paintAnimationFrame(3);
         } else {
            this.paintAnimationFrame(3);
            this.paintAnimationFrame(2);
            this.paintAnimationFrame(1);
         }

         AquaTreeUI.this.fAnimationFrame = -1;
      }

      protected void paintAnimationFrame(int var1) {
         AquaTreeUI.this.fAnimationFrame = var1;
         this.paintOneControl();

         try {
            Thread.sleep(20L);
         } catch (InterruptedException var3) {
         }

      }

      void paintOneControl() {
         if (AquaTreeUI.this.tree != null) {
            Graphics var1 = AquaTreeUI.this.tree.getGraphics();
            if (var1 != null) {
               try {
                  var1.setClip(this.fVisibleRect);
                  var1.setColor(this.fBackground);
                  var1.fillRect(this.fPathBounds.x, this.fPathBounds.y, this.fPathBounds.width, this.fPathBounds.height);
                  if (AquaTreeUI.this.fTrackingPath != null) {
                     TreePath var2 = AquaTreeUI.this.fTrackingPath.getParentPath();
                     if (var2 != null) {
                        AquaTreeUI.this.paintVerticalPartOfLeg(var1, this.fPathBounds, this.fInsets, var2);
                        AquaTreeUI.this.paintHorizontalPartOfLeg(var1, this.fPathBounds, this.fInsets, this.fBounds, AquaTreeUI.this.fTrackingPath, this.fTrackingRow, this.fIsExpanded, this.fHasBeenExpanded, this.fIsLeaf);
                     } else if (AquaTreeUI.this.isRootVisible() && this.fTrackingRow == 0) {
                        AquaTreeUI.this.paintHorizontalPartOfLeg(var1, this.fPathBounds, this.fInsets, this.fBounds, AquaTreeUI.this.fTrackingPath, this.fTrackingRow, this.fIsExpanded, this.fHasBeenExpanded, this.fIsLeaf);
                     }

                     AquaTreeUI.this.paintExpandControl(var1, this.fPathBounds, this.fInsets, this.fBounds, AquaTreeUI.this.fTrackingPath, this.fTrackingRow, this.fIsExpanded, this.fHasBeenExpanded, this.fIsLeaf);
                     return;
                  }
               } finally {
                  var1.dispose();
               }

            }
         }
      }

      protected void removeFromSource() {
         AquaTreeUI.this.tree.removeMouseListener(this);
         AquaTreeUI.this.tree.removeMouseMotionListener(this);
      }
   }

   public class MacPropertyChangeHandler extends BasicTreeUI.PropertyChangeHandler {
      public MacPropertyChangeHandler() {
         super();
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if (var2.equals("Frame.active")) {
            AquaBorder.repaintBorder(AquaTreeUI.this.tree);
            AquaFocusHandler.swapSelectionColors("Tree", AquaTreeUI.this.tree, var1.getNewValue());
         } else {
            super.propertyChange(var1);
         }

      }
   }

   class FocusHandler extends BasicTreeUI.FocusHandler {
      FocusHandler() {
         super();
      }

      public void focusGained(FocusEvent var1) {
         super.focusGained(var1);
         AquaBorder.repaintBorder(AquaTreeUI.this.tree);
      }

      public void focusLost(FocusEvent var1) {
         super.focusLost(var1);
         AquaBorder.repaintBorder(AquaTreeUI.this.tree);
      }
   }

   class LineListener implements PropertyChangeListener {
      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if (var2.equals("JTree.lineStyle")) {
            AquaTreeUI.this.decodeLineStyle(var1.getNewValue());
         }

      }
   }
}
