package javax.swing.plaf.basic;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.peer.ComponentPeer;
import java.awt.peer.LightweightPeer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.UIResource;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class BasicSplitPaneUI extends SplitPaneUI {
   protected static final String NON_CONTINUOUS_DIVIDER = "nonContinuousDivider";
   protected static int KEYBOARD_DIVIDER_MOVE_OFFSET = 3;
   protected JSplitPane splitPane;
   protected BasicSplitPaneUI.BasicHorizontalLayoutManager layoutManager;
   protected BasicSplitPaneDivider divider;
   protected PropertyChangeListener propertyChangeListener;
   protected FocusListener focusListener;
   private BasicSplitPaneUI.Handler handler;
   private Set<KeyStroke> managingFocusForwardTraversalKeys;
   private Set<KeyStroke> managingFocusBackwardTraversalKeys;
   protected int dividerSize;
   protected Component nonContinuousLayoutDivider;
   protected boolean draggingHW;
   protected int beginDragDividerLocation;
   /** @deprecated */
   @Deprecated
   protected KeyStroke upKey;
   /** @deprecated */
   @Deprecated
   protected KeyStroke downKey;
   /** @deprecated */
   @Deprecated
   protected KeyStroke leftKey;
   /** @deprecated */
   @Deprecated
   protected KeyStroke rightKey;
   /** @deprecated */
   @Deprecated
   protected KeyStroke homeKey;
   /** @deprecated */
   @Deprecated
   protected KeyStroke endKey;
   /** @deprecated */
   @Deprecated
   protected KeyStroke dividerResizeToggleKey;
   /** @deprecated */
   @Deprecated
   protected ActionListener keyboardUpLeftListener;
   /** @deprecated */
   @Deprecated
   protected ActionListener keyboardDownRightListener;
   /** @deprecated */
   @Deprecated
   protected ActionListener keyboardHomeListener;
   /** @deprecated */
   @Deprecated
   protected ActionListener keyboardEndListener;
   /** @deprecated */
   @Deprecated
   protected ActionListener keyboardResizeToggleListener;
   private int orientation;
   private int lastDragLocation;
   private boolean continuousLayout;
   private boolean dividerKeyboardResize;
   private boolean dividerLocationIsSet;
   private Color dividerDraggingColor;
   private boolean rememberPaneSizes;
   private boolean keepHidden = false;
   boolean painted;
   boolean ignoreDividerLocationChange;

   public static ComponentUI createUI(JComponent var0) {
      return new BasicSplitPaneUI();
   }

   static void loadActionMap(LazyActionMap var0) {
      var0.put(new BasicSplitPaneUI.Actions("negativeIncrement"));
      var0.put(new BasicSplitPaneUI.Actions("positiveIncrement"));
      var0.put(new BasicSplitPaneUI.Actions("selectMin"));
      var0.put(new BasicSplitPaneUI.Actions("selectMax"));
      var0.put(new BasicSplitPaneUI.Actions("startResize"));
      var0.put(new BasicSplitPaneUI.Actions("toggleFocus"));
      var0.put(new BasicSplitPaneUI.Actions("focusOutForward"));
      var0.put(new BasicSplitPaneUI.Actions("focusOutBackward"));
   }

   public void installUI(JComponent var1) {
      this.splitPane = (JSplitPane)var1;
      this.dividerLocationIsSet = false;
      this.dividerKeyboardResize = false;
      this.keepHidden = false;
      this.installDefaults();
      this.installListeners();
      this.installKeyboardActions();
      this.setLastDragLocation(-1);
   }

   protected void installDefaults() {
      LookAndFeel.installBorder(this.splitPane, "SplitPane.border");
      LookAndFeel.installColors(this.splitPane, "SplitPane.background", "SplitPane.foreground");
      LookAndFeel.installProperty(this.splitPane, "opaque", Boolean.TRUE);
      if (this.divider == null) {
         this.divider = this.createDefaultDivider();
      }

      this.divider.setBasicSplitPaneUI(this);
      Border var1 = this.divider.getBorder();
      if (var1 == null || !(var1 instanceof UIResource)) {
         this.divider.setBorder(UIManager.getBorder("SplitPaneDivider.border"));
      }

      this.dividerDraggingColor = UIManager.getColor("SplitPaneDivider.draggingColor");
      this.setOrientation(this.splitPane.getOrientation());
      Integer var2 = (Integer)UIManager.get("SplitPane.dividerSize");
      LookAndFeel.installProperty(this.splitPane, "dividerSize", var2 == null ? 10 : var2);
      this.divider.setDividerSize(this.splitPane.getDividerSize());
      this.dividerSize = this.divider.getDividerSize();
      this.splitPane.add(this.divider, "divider");
      this.setContinuousLayout(this.splitPane.isContinuousLayout());
      this.resetLayoutManager();
      if (this.nonContinuousLayoutDivider == null) {
         this.setNonContinuousLayoutDivider(this.createDefaultNonContinuousLayoutDivider(), true);
      } else {
         this.setNonContinuousLayoutDivider(this.nonContinuousLayoutDivider, true);
      }

      if (this.managingFocusForwardTraversalKeys == null) {
         this.managingFocusForwardTraversalKeys = new HashSet();
         this.managingFocusForwardTraversalKeys.add(KeyStroke.getKeyStroke(9, 0));
      }

      this.splitPane.setFocusTraversalKeys(0, this.managingFocusForwardTraversalKeys);
      if (this.managingFocusBackwardTraversalKeys == null) {
         this.managingFocusBackwardTraversalKeys = new HashSet();
         this.managingFocusBackwardTraversalKeys.add(KeyStroke.getKeyStroke(9, 1));
      }

      this.splitPane.setFocusTraversalKeys(1, this.managingFocusBackwardTraversalKeys);
   }

   protected void installListeners() {
      if ((this.propertyChangeListener = this.createPropertyChangeListener()) != null) {
         this.splitPane.addPropertyChangeListener(this.propertyChangeListener);
      }

      if ((this.focusListener = this.createFocusListener()) != null) {
         this.splitPane.addFocusListener(this.focusListener);
      }

   }

   protected void installKeyboardActions() {
      InputMap var1 = this.getInputMap(1);
      SwingUtilities.replaceUIInputMap(this.splitPane, 1, var1);
      LazyActionMap.installLazyActionMap(this.splitPane, BasicSplitPaneUI.class, "SplitPane.actionMap");
   }

   InputMap getInputMap(int var1) {
      return var1 == 1 ? (InputMap)DefaultLookup.get(this.splitPane, this, "SplitPane.ancestorInputMap") : null;
   }

   public void uninstallUI(JComponent var1) {
      this.uninstallKeyboardActions();
      this.uninstallListeners();
      this.uninstallDefaults();
      this.dividerLocationIsSet = false;
      this.dividerKeyboardResize = false;
      this.splitPane = null;
   }

   protected void uninstallDefaults() {
      if (this.splitPane.getLayout() == this.layoutManager) {
         this.splitPane.setLayout((LayoutManager)null);
      }

      if (this.nonContinuousLayoutDivider != null) {
         this.splitPane.remove(this.nonContinuousLayoutDivider);
      }

      LookAndFeel.uninstallBorder(this.splitPane);
      Border var1 = this.divider.getBorder();
      if (var1 instanceof UIResource) {
         this.divider.setBorder((Border)null);
      }

      this.splitPane.remove(this.divider);
      this.divider.setBasicSplitPaneUI((BasicSplitPaneUI)null);
      this.layoutManager = null;
      this.divider = null;
      this.nonContinuousLayoutDivider = null;
      this.setNonContinuousLayoutDivider((Component)null);
      this.splitPane.setFocusTraversalKeys(0, (Set)null);
      this.splitPane.setFocusTraversalKeys(1, (Set)null);
   }

   protected void uninstallListeners() {
      if (this.propertyChangeListener != null) {
         this.splitPane.removePropertyChangeListener(this.propertyChangeListener);
         this.propertyChangeListener = null;
      }

      if (this.focusListener != null) {
         this.splitPane.removeFocusListener(this.focusListener);
         this.focusListener = null;
      }

      this.keyboardUpLeftListener = null;
      this.keyboardDownRightListener = null;
      this.keyboardHomeListener = null;
      this.keyboardEndListener = null;
      this.keyboardResizeToggleListener = null;
      this.handler = null;
   }

   protected void uninstallKeyboardActions() {
      SwingUtilities.replaceUIActionMap(this.splitPane, (ActionMap)null);
      SwingUtilities.replaceUIInputMap(this.splitPane, 1, (InputMap)null);
   }

   protected PropertyChangeListener createPropertyChangeListener() {
      return this.getHandler();
   }

   private BasicSplitPaneUI.Handler getHandler() {
      if (this.handler == null) {
         this.handler = new BasicSplitPaneUI.Handler();
      }

      return this.handler;
   }

   protected FocusListener createFocusListener() {
      return this.getHandler();
   }

   /** @deprecated */
   @Deprecated
   protected ActionListener createKeyboardUpLeftListener() {
      return new BasicSplitPaneUI.KeyboardUpLeftHandler();
   }

   /** @deprecated */
   @Deprecated
   protected ActionListener createKeyboardDownRightListener() {
      return new BasicSplitPaneUI.KeyboardDownRightHandler();
   }

   /** @deprecated */
   @Deprecated
   protected ActionListener createKeyboardHomeListener() {
      return new BasicSplitPaneUI.KeyboardHomeHandler();
   }

   /** @deprecated */
   @Deprecated
   protected ActionListener createKeyboardEndListener() {
      return new BasicSplitPaneUI.KeyboardEndHandler();
   }

   /** @deprecated */
   @Deprecated
   protected ActionListener createKeyboardResizeToggleListener() {
      return new BasicSplitPaneUI.KeyboardResizeToggleHandler();
   }

   public int getOrientation() {
      return this.orientation;
   }

   public void setOrientation(int var1) {
      this.orientation = var1;
   }

   public boolean isContinuousLayout() {
      return this.continuousLayout;
   }

   public void setContinuousLayout(boolean var1) {
      this.continuousLayout = var1;
   }

   public int getLastDragLocation() {
      return this.lastDragLocation;
   }

   public void setLastDragLocation(int var1) {
      this.lastDragLocation = var1;
   }

   int getKeyboardMoveIncrement() {
      return 3;
   }

   public BasicSplitPaneDivider getDivider() {
      return this.divider;
   }

   protected Component createDefaultNonContinuousLayoutDivider() {
      return new Canvas() {
         public void paint(Graphics var1) {
            if (!BasicSplitPaneUI.this.isContinuousLayout() && BasicSplitPaneUI.this.getLastDragLocation() != -1) {
               Dimension var2 = BasicSplitPaneUI.this.splitPane.getSize();
               var1.setColor(BasicSplitPaneUI.this.dividerDraggingColor);
               if (BasicSplitPaneUI.this.orientation == 1) {
                  var1.fillRect(0, 0, BasicSplitPaneUI.this.dividerSize - 1, var2.height - 1);
               } else {
                  var1.fillRect(0, 0, var2.width - 1, BasicSplitPaneUI.this.dividerSize - 1);
               }
            }

         }
      };
   }

   protected void setNonContinuousLayoutDivider(Component var1) {
      this.setNonContinuousLayoutDivider(var1, true);
   }

   protected void setNonContinuousLayoutDivider(Component var1, boolean var2) {
      this.rememberPaneSizes = var2;
      if (this.nonContinuousLayoutDivider != null && this.splitPane != null) {
         this.splitPane.remove(this.nonContinuousLayoutDivider);
      }

      this.nonContinuousLayoutDivider = var1;
   }

   private void addHeavyweightDivider() {
      if (this.nonContinuousLayoutDivider != null && this.splitPane != null) {
         Component var1 = this.splitPane.getLeftComponent();
         Component var2 = this.splitPane.getRightComponent();
         int var3 = this.splitPane.getDividerLocation();
         if (var1 != null) {
            this.splitPane.setLeftComponent((Component)null);
         }

         if (var2 != null) {
            this.splitPane.setRightComponent((Component)null);
         }

         this.splitPane.remove(this.divider);
         this.splitPane.add(this.nonContinuousLayoutDivider, "nonContinuousDivider", this.splitPane.getComponentCount());
         this.splitPane.setLeftComponent(var1);
         this.splitPane.setRightComponent(var2);
         this.splitPane.add(this.divider, "divider");
         if (this.rememberPaneSizes) {
            this.splitPane.setDividerLocation(var3);
         }
      }

   }

   public Component getNonContinuousLayoutDivider() {
      return this.nonContinuousLayoutDivider;
   }

   public JSplitPane getSplitPane() {
      return this.splitPane;
   }

   public BasicSplitPaneDivider createDefaultDivider() {
      return new BasicSplitPaneDivider(this);
   }

   public void resetToPreferredSizes(JSplitPane var1) {
      if (this.splitPane != null) {
         this.layoutManager.resetToPreferredSizes();
         this.splitPane.revalidate();
         this.splitPane.repaint();
      }

   }

   public void setDividerLocation(JSplitPane var1, int var2) {
      if (!this.ignoreDividerLocationChange) {
         this.dividerLocationIsSet = true;
         this.splitPane.revalidate();
         this.splitPane.repaint();
         if (this.keepHidden) {
            Insets var3 = this.splitPane.getInsets();
            int var4 = this.splitPane.getOrientation();
            if (var4 == 0 && var2 != var3.top && var2 != this.splitPane.getHeight() - this.divider.getHeight() - var3.top || var4 == 1 && var2 != var3.left && var2 != this.splitPane.getWidth() - this.divider.getWidth() - var3.left) {
               this.setKeepHidden(false);
            }
         }
      } else {
         this.ignoreDividerLocationChange = false;
      }

   }

   public int getDividerLocation(JSplitPane var1) {
      return this.orientation == 1 ? this.divider.getLocation().x : this.divider.getLocation().y;
   }

   public int getMinimumDividerLocation(JSplitPane var1) {
      int var2 = 0;
      Component var3 = this.splitPane.getLeftComponent();
      if (var3 != null && var3.isVisible()) {
         Insets var4 = this.splitPane.getInsets();
         Dimension var5 = var3.getMinimumSize();
         if (this.orientation == 1) {
            var2 = var5.width;
         } else {
            var2 = var5.height;
         }

         if (var4 != null) {
            if (this.orientation == 1) {
               var2 += var4.left;
            } else {
               var2 += var4.top;
            }
         }
      }

      return var2;
   }

   public int getMaximumDividerLocation(JSplitPane var1) {
      Dimension var2 = this.splitPane.getSize();
      int var3 = 0;
      Component var4 = this.splitPane.getRightComponent();
      if (var4 != null) {
         Insets var5 = this.splitPane.getInsets();
         Dimension var6 = new Dimension(0, 0);
         if (var4.isVisible()) {
            var6 = var4.getMinimumSize();
         }

         if (this.orientation == 1) {
            var3 = var2.width - var6.width;
         } else {
            var3 = var2.height - var6.height;
         }

         var3 -= this.dividerSize;
         if (var5 != null) {
            if (this.orientation == 1) {
               var3 -= var5.right;
            } else {
               var3 -= var5.top;
            }
         }
      }

      return Math.max(this.getMinimumDividerLocation(this.splitPane), var3);
   }

   public void finishedPaintingChildren(JSplitPane var1, Graphics var2) {
      if (var1 == this.splitPane && this.getLastDragLocation() != -1 && !this.isContinuousLayout() && !this.draggingHW) {
         Dimension var3 = this.splitPane.getSize();
         var2.setColor(this.dividerDraggingColor);
         if (this.orientation == 1) {
            var2.fillRect(this.getLastDragLocation(), 0, this.dividerSize - 1, var3.height - 1);
         } else {
            var2.fillRect(0, this.lastDragLocation, var3.width - 1, this.dividerSize - 1);
         }
      }

   }

   public void paint(Graphics var1, JComponent var2) {
      if (!this.painted && this.splitPane.getDividerLocation() < 0) {
         this.ignoreDividerLocationChange = true;
         this.splitPane.setDividerLocation(this.getDividerLocation(this.splitPane));
      }

      this.painted = true;
   }

   public Dimension getPreferredSize(JComponent var1) {
      return this.splitPane != null ? this.layoutManager.preferredLayoutSize(this.splitPane) : new Dimension(0, 0);
   }

   public Dimension getMinimumSize(JComponent var1) {
      return this.splitPane != null ? this.layoutManager.minimumLayoutSize(this.splitPane) : new Dimension(0, 0);
   }

   public Dimension getMaximumSize(JComponent var1) {
      return this.splitPane != null ? this.layoutManager.maximumLayoutSize(this.splitPane) : new Dimension(0, 0);
   }

   public Insets getInsets(JComponent var1) {
      return null;
   }

   protected void resetLayoutManager() {
      if (this.orientation == 1) {
         this.layoutManager = new BasicSplitPaneUI.BasicHorizontalLayoutManager(0);
      } else {
         this.layoutManager = new BasicSplitPaneUI.BasicHorizontalLayoutManager(1);
      }

      this.splitPane.setLayout(this.layoutManager);
      this.layoutManager.updateComponents();
      this.splitPane.revalidate();
      this.splitPane.repaint();
   }

   void setKeepHidden(boolean var1) {
      this.keepHidden = var1;
   }

   private boolean getKeepHidden() {
      return this.keepHidden;
   }

   protected void startDragging() {
      Component var1 = this.splitPane.getLeftComponent();
      Component var2 = this.splitPane.getRightComponent();
      this.beginDragDividerLocation = this.getDividerLocation(this.splitPane);
      this.draggingHW = false;
      ComponentPeer var3;
      if (var1 != null && (var3 = var1.getPeer()) != null && !(var3 instanceof LightweightPeer)) {
         this.draggingHW = true;
      } else if (var2 != null && (var3 = var2.getPeer()) != null && !(var3 instanceof LightweightPeer)) {
         this.draggingHW = true;
      }

      if (this.orientation == 1) {
         this.setLastDragLocation(this.divider.getBounds().x);
         this.dividerSize = this.divider.getSize().width;
         if (!this.isContinuousLayout() && this.draggingHW) {
            this.nonContinuousLayoutDivider.setBounds(this.getLastDragLocation(), 0, this.dividerSize, this.splitPane.getHeight());
            this.addHeavyweightDivider();
         }
      } else {
         this.setLastDragLocation(this.divider.getBounds().y);
         this.dividerSize = this.divider.getSize().height;
         if (!this.isContinuousLayout() && this.draggingHW) {
            this.nonContinuousLayoutDivider.setBounds(0, this.getLastDragLocation(), this.splitPane.getWidth(), this.dividerSize);
            this.addHeavyweightDivider();
         }
      }

   }

   protected void dragDividerTo(int var1) {
      if (this.getLastDragLocation() != var1) {
         if (this.isContinuousLayout()) {
            this.splitPane.setDividerLocation(var1);
            this.setLastDragLocation(var1);
         } else {
            int var2 = this.getLastDragLocation();
            this.setLastDragLocation(var1);
            int var3;
            if (this.orientation == 1) {
               if (this.draggingHW) {
                  this.nonContinuousLayoutDivider.setLocation(this.getLastDragLocation(), 0);
               } else {
                  var3 = this.splitPane.getHeight();
                  this.splitPane.repaint(var2, 0, this.dividerSize, var3);
                  this.splitPane.repaint(var1, 0, this.dividerSize, var3);
               }
            } else if (this.draggingHW) {
               this.nonContinuousLayoutDivider.setLocation(0, this.getLastDragLocation());
            } else {
               var3 = this.splitPane.getWidth();
               this.splitPane.repaint(0, var2, var3, this.dividerSize);
               this.splitPane.repaint(0, var1, var3, this.dividerSize);
            }
         }
      }

   }

   protected void finishDraggingTo(int var1) {
      this.dragDividerTo(var1);
      this.setLastDragLocation(-1);
      if (!this.isContinuousLayout()) {
         Component var2 = this.splitPane.getLeftComponent();
         Rectangle var3 = var2.getBounds();
         if (this.draggingHW) {
            if (this.orientation == 1) {
               this.nonContinuousLayoutDivider.setLocation(-this.dividerSize, 0);
            } else {
               this.nonContinuousLayoutDivider.setLocation(0, -this.dividerSize);
            }

            this.splitPane.remove(this.nonContinuousLayoutDivider);
         }

         this.splitPane.setDividerLocation(var1);
      }

   }

   /** @deprecated */
   @Deprecated
   protected int getDividerBorderSize() {
      return 1;
   }

   private static class Actions extends UIAction {
      private static final String NEGATIVE_INCREMENT = "negativeIncrement";
      private static final String POSITIVE_INCREMENT = "positiveIncrement";
      private static final String SELECT_MIN = "selectMin";
      private static final String SELECT_MAX = "selectMax";
      private static final String START_RESIZE = "startResize";
      private static final String TOGGLE_FOCUS = "toggleFocus";
      private static final String FOCUS_OUT_FORWARD = "focusOutForward";
      private static final String FOCUS_OUT_BACKWARD = "focusOutBackward";

      Actions(String var1) {
         super(var1);
      }

      public void actionPerformed(ActionEvent var1) {
         JSplitPane var2 = (JSplitPane)var1.getSource();
         BasicSplitPaneUI var3 = (BasicSplitPaneUI)BasicLookAndFeel.getUIOfType(var2.getUI(), BasicSplitPaneUI.class);
         if (var3 != null) {
            String var4 = this.getName();
            if (var4 == "negativeIncrement") {
               if (var3.dividerKeyboardResize) {
                  var2.setDividerLocation(Math.max(0, var3.getDividerLocation(var2) - var3.getKeyboardMoveIncrement()));
               }
            } else if (var4 == "positiveIncrement") {
               if (var3.dividerKeyboardResize) {
                  var2.setDividerLocation(var3.getDividerLocation(var2) + var3.getKeyboardMoveIncrement());
               }
            } else if (var4 == "selectMin") {
               if (var3.dividerKeyboardResize) {
                  var2.setDividerLocation(0);
               }
            } else if (var4 == "selectMax") {
               if (var3.dividerKeyboardResize) {
                  Insets var5 = var2.getInsets();
                  int var6 = var5 != null ? var5.bottom : 0;
                  int var7 = var5 != null ? var5.right : 0;
                  if (var3.orientation == 0) {
                     var2.setDividerLocation(var2.getHeight() - var6);
                  } else {
                     var2.setDividerLocation(var2.getWidth() - var7);
                  }
               }
            } else if (var4 == "startResize") {
               if (!var3.dividerKeyboardResize) {
                  var2.requestFocus();
               } else {
                  JSplitPane var8 = (JSplitPane)SwingUtilities.getAncestorOfClass(JSplitPane.class, var2);
                  if (var8 != null) {
                     var8.requestFocus();
                  }
               }
            } else if (var4 == "toggleFocus") {
               this.toggleFocus(var2);
            } else if (var4 == "focusOutForward") {
               this.moveFocus(var2, 1);
            } else if (var4 == "focusOutBackward") {
               this.moveFocus(var2, -1);
            }

         }
      }

      private void moveFocus(JSplitPane var1, int var2) {
         Container var3 = var1.getFocusCycleRootAncestor();
         FocusTraversalPolicy var4 = var3.getFocusTraversalPolicy();
         Component var5 = var2 > 0 ? var4.getComponentAfter(var3, var1) : var4.getComponentBefore(var3, var1);
         HashSet var6 = new HashSet();
         if (var1.isAncestorOf(var5)) {
            do {
               var6.add(var5);
               var3 = var5.getFocusCycleRootAncestor();
               var4 = var3.getFocusTraversalPolicy();
               var5 = var2 > 0 ? var4.getComponentAfter(var3, var5) : var4.getComponentBefore(var3, var5);
            } while(var1.isAncestorOf(var5) && !var6.contains(var5));
         }

         if (var5 != null && !var1.isAncestorOf(var5)) {
            var5.requestFocus();
         }

      }

      private void toggleFocus(JSplitPane var1) {
         Component var2 = var1.getLeftComponent();
         Component var3 = var1.getRightComponent();
         KeyboardFocusManager var4 = KeyboardFocusManager.getCurrentKeyboardFocusManager();
         Component var5 = var4.getFocusOwner();
         Component var6 = this.getNextSide(var1, var5);
         if (var6 != null) {
            if (var5 != null && (SwingUtilities.isDescendingFrom(var5, var2) && SwingUtilities.isDescendingFrom(var6, var2) || SwingUtilities.isDescendingFrom(var5, var3) && SwingUtilities.isDescendingFrom(var6, var3))) {
               return;
            }

            SwingUtilities2.compositeRequestFocus(var6);
         }

      }

      private Component getNextSide(JSplitPane var1, Component var2) {
         Component var3 = var1.getLeftComponent();
         Component var4 = var1.getRightComponent();
         Component var5;
         if (var2 != null && SwingUtilities.isDescendingFrom(var2, var3) && var4 != null) {
            var5 = this.getFirstAvailableComponent(var4);
            if (var5 != null) {
               return var5;
            }
         }

         JSplitPane var6 = (JSplitPane)SwingUtilities.getAncestorOfClass(JSplitPane.class, var1);
         if (var6 != null) {
            var5 = this.getNextSide(var6, var2);
         } else {
            var5 = this.getFirstAvailableComponent(var3);
            if (var5 == null) {
               var5 = this.getFirstAvailableComponent(var4);
            }
         }

         return var5;
      }

      private Component getFirstAvailableComponent(Component var1) {
         if (var1 != null && var1 instanceof JSplitPane) {
            JSplitPane var2 = (JSplitPane)var1;
            Component var3 = this.getFirstAvailableComponent(var2.getLeftComponent());
            if (var3 != null) {
               var1 = var3;
            } else {
               var1 = this.getFirstAvailableComponent(var2.getRightComponent());
            }
         }

         return var1;
      }
   }

   private class Handler implements FocusListener, PropertyChangeListener {
      private Handler() {
      }

      public void propertyChange(PropertyChangeEvent var1) {
         if (var1.getSource() == BasicSplitPaneUI.this.splitPane) {
            String var2 = var1.getPropertyName();
            if (var2 == "orientation") {
               BasicSplitPaneUI.this.orientation = BasicSplitPaneUI.this.splitPane.getOrientation();
               BasicSplitPaneUI.this.resetLayoutManager();
            } else if (var2 == "continuousLayout") {
               BasicSplitPaneUI.this.setContinuousLayout(BasicSplitPaneUI.this.splitPane.isContinuousLayout());
               if (!BasicSplitPaneUI.this.isContinuousLayout()) {
                  if (BasicSplitPaneUI.this.nonContinuousLayoutDivider == null) {
                     BasicSplitPaneUI.this.setNonContinuousLayoutDivider(BasicSplitPaneUI.this.createDefaultNonContinuousLayoutDivider(), true);
                  } else if (BasicSplitPaneUI.this.nonContinuousLayoutDivider.getParent() == null) {
                     BasicSplitPaneUI.this.setNonContinuousLayoutDivider(BasicSplitPaneUI.this.nonContinuousLayoutDivider, true);
                  }
               }
            } else if (var2 == "dividerSize") {
               BasicSplitPaneUI.this.divider.setDividerSize(BasicSplitPaneUI.this.splitPane.getDividerSize());
               BasicSplitPaneUI.this.dividerSize = BasicSplitPaneUI.this.divider.getDividerSize();
               BasicSplitPaneUI.this.splitPane.revalidate();
               BasicSplitPaneUI.this.splitPane.repaint();
            }
         }

      }

      public void focusGained(FocusEvent var1) {
         BasicSplitPaneUI.this.dividerKeyboardResize = true;
         BasicSplitPaneUI.this.splitPane.repaint();
      }

      public void focusLost(FocusEvent var1) {
         BasicSplitPaneUI.this.dividerKeyboardResize = false;
         BasicSplitPaneUI.this.splitPane.repaint();
      }

      // $FF: synthetic method
      Handler(Object var2) {
         this();
      }
   }

   public class BasicVerticalLayoutManager extends BasicSplitPaneUI.BasicHorizontalLayoutManager {
      public BasicVerticalLayoutManager() {
         super(1);
      }
   }

   public class BasicHorizontalLayoutManager implements LayoutManager2 {
      protected int[] sizes;
      protected Component[] components;
      private int lastSplitPaneSize;
      private boolean doReset;
      private int axis;

      BasicHorizontalLayoutManager() {
         this(0);
      }

      BasicHorizontalLayoutManager(int var2) {
         this.axis = var2;
         this.components = new Component[3];
         this.components[0] = this.components[1] = this.components[2] = null;
         this.sizes = new int[3];
      }

      public void layoutContainer(Container var1) {
         Dimension var2 = var1.getSize();
         if (var2.height > 0 && var2.width > 0) {
            int var3 = BasicSplitPaneUI.this.splitPane.getDividerLocation();
            Insets var4 = BasicSplitPaneUI.this.splitPane.getInsets();
            int var5 = this.getAvailableSize(var2, var4);
            this.getSizeForPrimaryAxis(var2);
            int var7 = BasicSplitPaneUI.this.getDividerLocation(BasicSplitPaneUI.this.splitPane);
            int var8 = this.getSizeForPrimaryAxis(var4, true);
            Dimension var9 = this.components[2] == null ? null : this.components[2].getPreferredSize();
            if ((!this.doReset || BasicSplitPaneUI.this.dividerLocationIsSet) && var3 >= 0) {
               if (this.lastSplitPaneSize <= 0 || var5 == this.lastSplitPaneSize || !BasicSplitPaneUI.this.painted || var9 != null && this.getSizeForPrimaryAxis(var9) != this.sizes[2]) {
                  if (var9 != null) {
                     this.sizes[2] = this.getSizeForPrimaryAxis(var9);
                  } else {
                     this.sizes[2] = 0;
                  }

                  this.setDividerLocation(var3 - var8, var5);
                  BasicSplitPaneUI.this.dividerLocationIsSet = false;
               } else if (var5 != this.lastSplitPaneSize) {
                  this.distributeSpace(var5 - this.lastSplitPaneSize, BasicSplitPaneUI.this.getKeepHidden());
               }
            } else {
               this.resetToPreferredSizes(var5);
            }

            this.doReset = false;
            BasicSplitPaneUI.this.dividerLocationIsSet = false;
            this.lastSplitPaneSize = var5;
            int var10 = this.getInitialLocation(var4);
            byte var11 = 0;

            while(var11 < 3) {
               if (this.components[var11] != null && this.components[var11].isVisible()) {
                  this.setComponentToSize(this.components[var11], this.sizes[var11], var10, var4, var2);
                  var10 += this.sizes[var11];
               }

               switch(var11) {
               case 0:
                  var11 = 2;
                  break;
               case 1:
                  var11 = 3;
                  break;
               case 2:
                  var11 = 1;
               }
            }

            if (BasicSplitPaneUI.this.painted) {
               int var12 = BasicSplitPaneUI.this.getDividerLocation(BasicSplitPaneUI.this.splitPane);
               if (var12 != var3 - var8) {
                  int var13 = BasicSplitPaneUI.this.splitPane.getLastDividerLocation();
                  BasicSplitPaneUI.this.ignoreDividerLocationChange = true;

                  try {
                     BasicSplitPaneUI.this.splitPane.setDividerLocation(var12);
                     BasicSplitPaneUI.this.splitPane.setLastDividerLocation(var13);
                  } finally {
                     BasicSplitPaneUI.this.ignoreDividerLocationChange = false;
                  }
               }
            }

         } else {
            this.lastSplitPaneSize = 0;
         }
      }

      public void addLayoutComponent(String var1, Component var2) {
         boolean var3 = true;
         if (var1 != null) {
            if (var1.equals("divider")) {
               this.components[2] = var2;
               this.sizes[2] = this.getSizeForPrimaryAxis(var2.getPreferredSize());
            } else if (!var1.equals("left") && !var1.equals("top")) {
               if (!var1.equals("right") && !var1.equals("bottom")) {
                  if (!var1.equals("nonContinuousDivider")) {
                     var3 = false;
                  }
               } else {
                  this.components[1] = var2;
                  this.sizes[1] = 0;
               }
            } else {
               this.components[0] = var2;
               this.sizes[0] = 0;
            }
         } else {
            var3 = false;
         }

         if (!var3) {
            throw new IllegalArgumentException("cannot add to layout: unknown constraint: " + var1);
         } else {
            this.doReset = true;
         }
      }

      public Dimension minimumLayoutSize(Container var1) {
         int var2 = 0;
         int var3 = 0;
         Insets var4 = BasicSplitPaneUI.this.splitPane.getInsets();

         for(int var5 = 0; var5 < 3; ++var5) {
            if (this.components[var5] != null) {
               Dimension var6 = this.components[var5].getMinimumSize();
               int var7 = this.getSizeForSecondaryAxis(var6);
               var2 += this.getSizeForPrimaryAxis(var6);
               if (var7 > var3) {
                  var3 = var7;
               }
            }
         }

         if (var4 != null) {
            var2 += this.getSizeForPrimaryAxis(var4, true) + this.getSizeForPrimaryAxis(var4, false);
            var3 += this.getSizeForSecondaryAxis(var4, true) + this.getSizeForSecondaryAxis(var4, false);
         }

         if (this.axis == 0) {
            return new Dimension(var2, var3);
         } else {
            return new Dimension(var3, var2);
         }
      }

      public Dimension preferredLayoutSize(Container var1) {
         int var2 = 0;
         int var3 = 0;
         Insets var4 = BasicSplitPaneUI.this.splitPane.getInsets();

         for(int var5 = 0; var5 < 3; ++var5) {
            if (this.components[var5] != null) {
               Dimension var6 = this.components[var5].getPreferredSize();
               int var7 = this.getSizeForSecondaryAxis(var6);
               var2 += this.getSizeForPrimaryAxis(var6);
               if (var7 > var3) {
                  var3 = var7;
               }
            }
         }

         if (var4 != null) {
            var2 += this.getSizeForPrimaryAxis(var4, true) + this.getSizeForPrimaryAxis(var4, false);
            var3 += this.getSizeForSecondaryAxis(var4, true) + this.getSizeForSecondaryAxis(var4, false);
         }

         if (this.axis == 0) {
            return new Dimension(var2, var3);
         } else {
            return new Dimension(var3, var2);
         }
      }

      public void removeLayoutComponent(Component var1) {
         for(int var2 = 0; var2 < 3; ++var2) {
            if (this.components[var2] == var1) {
               this.components[var2] = null;
               this.sizes[var2] = 0;
               this.doReset = true;
            }
         }

      }

      public void addLayoutComponent(Component var1, Object var2) {
         if (var2 != null && !(var2 instanceof String)) {
            throw new IllegalArgumentException("cannot add to layout: constraint must be a string (or null)");
         } else {
            this.addLayoutComponent((String)var2, var1);
         }
      }

      public float getLayoutAlignmentX(Container var1) {
         return 0.0F;
      }

      public float getLayoutAlignmentY(Container var1) {
         return 0.0F;
      }

      public void invalidateLayout(Container var1) {
      }

      public Dimension maximumLayoutSize(Container var1) {
         return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
      }

      public void resetToPreferredSizes() {
         this.doReset = true;
      }

      protected void resetSizeAt(int var1) {
         this.sizes[var1] = 0;
         this.doReset = true;
      }

      protected void setSizes(int[] var1) {
         System.arraycopy(var1, 0, this.sizes, 0, 3);
      }

      protected int[] getSizes() {
         int[] var1 = new int[3];
         System.arraycopy(this.sizes, 0, var1, 0, 3);
         return var1;
      }

      protected int getPreferredSizeOfComponent(Component var1) {
         return this.getSizeForPrimaryAxis(var1.getPreferredSize());
      }

      int getMinimumSizeOfComponent(Component var1) {
         return this.getSizeForPrimaryAxis(var1.getMinimumSize());
      }

      protected int getSizeOfComponent(Component var1) {
         return this.getSizeForPrimaryAxis(var1.getSize());
      }

      protected int getAvailableSize(Dimension var1, Insets var2) {
         return var2 == null ? this.getSizeForPrimaryAxis(var1) : this.getSizeForPrimaryAxis(var1) - (this.getSizeForPrimaryAxis(var2, true) + this.getSizeForPrimaryAxis(var2, false));
      }

      protected int getInitialLocation(Insets var1) {
         return var1 != null ? this.getSizeForPrimaryAxis(var1, true) : 0;
      }

      protected void setComponentToSize(Component var1, int var2, int var3, Insets var4, Dimension var5) {
         if (var4 != null) {
            if (this.axis == 0) {
               var1.setBounds(var3, var4.top, var2, var5.height - (var4.top + var4.bottom));
            } else {
               var1.setBounds(var4.left, var3, var5.width - (var4.left + var4.right), var2);
            }
         } else if (this.axis == 0) {
            var1.setBounds(var3, 0, var2, var5.height);
         } else {
            var1.setBounds(0, var3, var5.width, var2);
         }

      }

      int getSizeForPrimaryAxis(Dimension var1) {
         return this.axis == 0 ? var1.width : var1.height;
      }

      int getSizeForSecondaryAxis(Dimension var1) {
         return this.axis == 0 ? var1.height : var1.width;
      }

      int getSizeForPrimaryAxis(Insets var1, boolean var2) {
         if (this.axis == 0) {
            return var2 ? var1.left : var1.right;
         } else {
            return var2 ? var1.top : var1.bottom;
         }
      }

      int getSizeForSecondaryAxis(Insets var1, boolean var2) {
         if (this.axis == 0) {
            return var2 ? var1.top : var1.bottom;
         } else {
            return var2 ? var1.left : var1.right;
         }
      }

      protected void updateComponents() {
         Component var1 = BasicSplitPaneUI.this.splitPane.getLeftComponent();
         if (this.components[0] != var1) {
            this.components[0] = var1;
            if (var1 == null) {
               this.sizes[0] = 0;
            } else {
               this.sizes[0] = -1;
            }
         }

         var1 = BasicSplitPaneUI.this.splitPane.getRightComponent();
         if (this.components[1] != var1) {
            this.components[1] = var1;
            if (var1 == null) {
               this.sizes[1] = 0;
            } else {
               this.sizes[1] = -1;
            }
         }

         Component[] var2 = BasicSplitPaneUI.this.splitPane.getComponents();
         Component var3 = this.components[2];
         this.components[2] = null;

         for(int var4 = var2.length - 1; var4 >= 0; --var4) {
            if (var2[var4] != this.components[0] && var2[var4] != this.components[1] && var2[var4] != BasicSplitPaneUI.this.nonContinuousLayoutDivider) {
               if (var3 != var2[var4]) {
                  this.components[2] = var2[var4];
               } else {
                  this.components[2] = var3;
               }
               break;
            }
         }

         if (this.components[2] == null) {
            this.sizes[2] = 0;
         } else {
            this.sizes[2] = this.getSizeForPrimaryAxis(this.components[2].getPreferredSize());
         }

      }

      void setDividerLocation(int var1, int var2) {
         boolean var3 = this.components[0] != null && this.components[0].isVisible();
         boolean var4 = this.components[1] != null && this.components[1].isVisible();
         boolean var5 = this.components[2] != null && this.components[2].isVisible();
         int var6 = var2;
         if (var5) {
            var6 = var2 - this.sizes[2];
         }

         var1 = Math.max(0, Math.min(var1, var6));
         if (var3) {
            if (var4) {
               this.sizes[0] = var1;
               this.sizes[1] = var6 - var1;
            } else {
               this.sizes[0] = var6;
               this.sizes[1] = 0;
            }
         } else if (var4) {
            this.sizes[1] = var6;
            this.sizes[0] = 0;
         }

      }

      int[] getPreferredSizes() {
         int[] var1 = new int[3];

         for(int var2 = 0; var2 < 3; ++var2) {
            if (this.components[var2] != null && this.components[var2].isVisible()) {
               var1[var2] = this.getPreferredSizeOfComponent(this.components[var2]);
            } else {
               var1[var2] = -1;
            }
         }

         return var1;
      }

      int[] getMinimumSizes() {
         int[] var1 = new int[3];

         for(int var2 = 0; var2 < 2; ++var2) {
            if (this.components[var2] != null && this.components[var2].isVisible()) {
               var1[var2] = this.getMinimumSizeOfComponent(this.components[var2]);
            } else {
               var1[var2] = -1;
            }
         }

         var1[2] = this.components[2] != null ? this.getMinimumSizeOfComponent(this.components[2]) : -1;
         return var1;
      }

      void resetToPreferredSizes(int var1) {
         int[] var2 = this.getPreferredSizes();
         int var3 = 0;

         int var4;
         for(var4 = 0; var4 < 3; ++var4) {
            if (var2[var4] != -1) {
               var3 += var2[var4];
            }
         }

         if (var3 > var1) {
            var2 = this.getMinimumSizes();
            var3 = 0;

            for(var4 = 0; var4 < 3; ++var4) {
               if (var2[var4] != -1) {
                  var3 += var2[var4];
               }
            }
         }

         this.setSizes(var2);
         this.distributeSpace(var1 - var3, false);
      }

      void distributeSpace(int var1, boolean var2) {
         boolean var3 = this.components[0] != null && this.components[0].isVisible();
         boolean var4 = this.components[1] != null && this.components[1].isVisible();
         if (var2) {
            if (var3 && this.getSizeForPrimaryAxis(this.components[0].getSize()) == 0) {
               var3 = false;
               if (var4 && this.getSizeForPrimaryAxis(this.components[1].getSize()) == 0) {
                  var3 = true;
               }
            } else if (var4 && this.getSizeForPrimaryAxis(this.components[1].getSize()) == 0) {
               var4 = false;
            }
         }

         if (var3 && var4) {
            double var5 = BasicSplitPaneUI.this.splitPane.getResizeWeight();
            int var7 = (int)(var5 * (double)var1);
            int var8 = var1 - var7;
            int[] var10000 = this.sizes;
            var10000[0] += var7;
            var10000 = this.sizes;
            var10000[1] += var8;
            int var9 = this.getMinimumSizeOfComponent(this.components[0]);
            int var10 = this.getMinimumSizeOfComponent(this.components[1]);
            boolean var11 = this.sizes[0] >= var9;
            boolean var12 = this.sizes[1] >= var10;
            if (!var11 && !var12) {
               if (this.sizes[0] < 0) {
                  var10000 = this.sizes;
                  var10000[1] += this.sizes[0];
                  this.sizes[0] = 0;
               } else if (this.sizes[1] < 0) {
                  var10000 = this.sizes;
                  var10000[0] += this.sizes[1];
                  this.sizes[1] = 0;
               }
            } else if (!var11) {
               if (this.sizes[1] - (var9 - this.sizes[0]) < var10) {
                  if (this.sizes[0] < 0) {
                     var10000 = this.sizes;
                     var10000[1] += this.sizes[0];
                     this.sizes[0] = 0;
                  }
               } else {
                  var10000 = this.sizes;
                  var10000[1] -= var9 - this.sizes[0];
                  this.sizes[0] = var9;
               }
            } else if (!var12) {
               if (this.sizes[0] - (var10 - this.sizes[1]) < var9) {
                  if (this.sizes[1] < 0) {
                     var10000 = this.sizes;
                     var10000[0] += this.sizes[1];
                     this.sizes[1] = 0;
                  }
               } else {
                  var10000 = this.sizes;
                  var10000[0] -= var10 - this.sizes[1];
                  this.sizes[1] = var10;
               }
            }

            if (this.sizes[0] < 0) {
               this.sizes[0] = 0;
            }

            if (this.sizes[1] < 0) {
               this.sizes[1] = 0;
            }
         } else if (var3) {
            this.sizes[0] = Math.max(0, this.sizes[0] + var1);
         } else if (var4) {
            this.sizes[1] = Math.max(0, this.sizes[1] + var1);
         }

      }
   }

   public class KeyboardResizeToggleHandler implements ActionListener {
      public void actionPerformed(ActionEvent var1) {
         if (!BasicSplitPaneUI.this.dividerKeyboardResize) {
            BasicSplitPaneUI.this.splitPane.requestFocus();
         }

      }
   }

   public class KeyboardEndHandler implements ActionListener {
      public void actionPerformed(ActionEvent var1) {
         if (BasicSplitPaneUI.this.dividerKeyboardResize) {
            Insets var2 = BasicSplitPaneUI.this.splitPane.getInsets();
            int var3 = var2 != null ? var2.bottom : 0;
            int var4 = var2 != null ? var2.right : 0;
            if (BasicSplitPaneUI.this.orientation == 0) {
               BasicSplitPaneUI.this.splitPane.setDividerLocation(BasicSplitPaneUI.this.splitPane.getHeight() - var3);
            } else {
               BasicSplitPaneUI.this.splitPane.setDividerLocation(BasicSplitPaneUI.this.splitPane.getWidth() - var4);
            }
         }

      }
   }

   public class KeyboardHomeHandler implements ActionListener {
      public void actionPerformed(ActionEvent var1) {
         if (BasicSplitPaneUI.this.dividerKeyboardResize) {
            BasicSplitPaneUI.this.splitPane.setDividerLocation(0);
         }

      }
   }

   public class KeyboardDownRightHandler implements ActionListener {
      public void actionPerformed(ActionEvent var1) {
         if (BasicSplitPaneUI.this.dividerKeyboardResize) {
            BasicSplitPaneUI.this.splitPane.setDividerLocation(BasicSplitPaneUI.this.getDividerLocation(BasicSplitPaneUI.this.splitPane) + BasicSplitPaneUI.this.getKeyboardMoveIncrement());
         }

      }
   }

   public class KeyboardUpLeftHandler implements ActionListener {
      public void actionPerformed(ActionEvent var1) {
         if (BasicSplitPaneUI.this.dividerKeyboardResize) {
            BasicSplitPaneUI.this.splitPane.setDividerLocation(Math.max(0, BasicSplitPaneUI.this.getDividerLocation(BasicSplitPaneUI.this.splitPane) - BasicSplitPaneUI.this.getKeyboardMoveIncrement()));
         }

      }
   }

   public class FocusHandler extends FocusAdapter {
      public void focusGained(FocusEvent var1) {
         BasicSplitPaneUI.this.getHandler().focusGained(var1);
      }

      public void focusLost(FocusEvent var1) {
         BasicSplitPaneUI.this.getHandler().focusLost(var1);
      }
   }

   public class PropertyHandler implements PropertyChangeListener {
      public void propertyChange(PropertyChangeEvent var1) {
         BasicSplitPaneUI.this.getHandler().propertyChange(var1);
      }
   }
}
