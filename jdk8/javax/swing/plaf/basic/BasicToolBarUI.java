package javax.swing.plaf.basic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.IllegalComponentStateException;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Hashtable;
import javax.swing.AbstractButton;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.RootPaneContainer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ToolBarUI;
import javax.swing.plaf.UIResource;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicToolBarUI extends ToolBarUI implements SwingConstants {
   protected JToolBar toolBar;
   private boolean floating;
   private int floatingX;
   private int floatingY;
   private JFrame floatingFrame;
   private RootPaneContainer floatingToolBar;
   protected BasicToolBarUI.DragWindow dragWindow;
   private Container dockingSource;
   private int dockingSensitivity = 0;
   protected int focusedCompIndex = -1;
   protected Color dockingColor = null;
   protected Color floatingColor = null;
   protected Color dockingBorderColor = null;
   protected Color floatingBorderColor = null;
   protected MouseInputListener dockingListener;
   protected PropertyChangeListener propertyListener;
   protected ContainerListener toolBarContListener;
   protected FocusListener toolBarFocusListener;
   private BasicToolBarUI.Handler handler;
   protected String constraintBeforeFloating = "North";
   private static String IS_ROLLOVER = "JToolBar.isRollover";
   private static Border rolloverBorder;
   private static Border nonRolloverBorder;
   private static Border nonRolloverToggleBorder;
   private boolean rolloverBorders = false;
   private HashMap<AbstractButton, Border> borderTable = new HashMap();
   private Hashtable<AbstractButton, Boolean> rolloverTable = new Hashtable();
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
   private static String FOCUSED_COMP_INDEX = "JToolBar.focusedCompIndex";

   public static ComponentUI createUI(JComponent var0) {
      return new BasicToolBarUI();
   }

   public void installUI(JComponent var1) {
      this.toolBar = (JToolBar)var1;
      this.installDefaults();
      this.installComponents();
      this.installListeners();
      this.installKeyboardActions();
      this.dockingSensitivity = 0;
      this.floating = false;
      this.floatingX = this.floatingY = 0;
      this.floatingToolBar = null;
      this.setOrientation(this.toolBar.getOrientation());
      LookAndFeel.installProperty(var1, "opaque", Boolean.TRUE);
      if (var1.getClientProperty(FOCUSED_COMP_INDEX) != null) {
         this.focusedCompIndex = (Integer)((Integer)var1.getClientProperty(FOCUSED_COMP_INDEX));
      }

   }

   public void uninstallUI(JComponent var1) {
      this.uninstallDefaults();
      this.uninstallComponents();
      this.uninstallListeners();
      this.uninstallKeyboardActions();
      if (this.isFloating()) {
         this.setFloating(false, (Point)null);
      }

      this.floatingToolBar = null;
      this.dragWindow = null;
      this.dockingSource = null;
      var1.putClientProperty(FOCUSED_COMP_INDEX, this.focusedCompIndex);
   }

   protected void installDefaults() {
      LookAndFeel.installBorder(this.toolBar, "ToolBar.border");
      LookAndFeel.installColorsAndFont(this.toolBar, "ToolBar.background", "ToolBar.foreground", "ToolBar.font");
      if (this.dockingColor == null || this.dockingColor instanceof UIResource) {
         this.dockingColor = UIManager.getColor("ToolBar.dockingBackground");
      }

      if (this.floatingColor == null || this.floatingColor instanceof UIResource) {
         this.floatingColor = UIManager.getColor("ToolBar.floatingBackground");
      }

      if (this.dockingBorderColor == null || this.dockingBorderColor instanceof UIResource) {
         this.dockingBorderColor = UIManager.getColor("ToolBar.dockingForeground");
      }

      if (this.floatingBorderColor == null || this.floatingBorderColor instanceof UIResource) {
         this.floatingBorderColor = UIManager.getColor("ToolBar.floatingForeground");
      }

      Object var1 = this.toolBar.getClientProperty(IS_ROLLOVER);
      if (var1 == null) {
         var1 = UIManager.get("ToolBar.isRollover");
      }

      if (var1 != null) {
         this.rolloverBorders = (Boolean)var1;
      }

      if (rolloverBorder == null) {
         rolloverBorder = this.createRolloverBorder();
      }

      if (nonRolloverBorder == null) {
         nonRolloverBorder = this.createNonRolloverBorder();
      }

      if (nonRolloverToggleBorder == null) {
         nonRolloverToggleBorder = this.createNonRolloverToggleBorder();
      }

      this.setRolloverBorders(this.isRolloverBorders());
   }

   protected void uninstallDefaults() {
      LookAndFeel.uninstallBorder(this.toolBar);
      this.dockingColor = null;
      this.floatingColor = null;
      this.dockingBorderColor = null;
      this.floatingBorderColor = null;
      this.installNormalBorders(this.toolBar);
      rolloverBorder = null;
      nonRolloverBorder = null;
      nonRolloverToggleBorder = null;
   }

   protected void installComponents() {
   }

   protected void uninstallComponents() {
   }

   protected void installListeners() {
      this.dockingListener = this.createDockingListener();
      if (this.dockingListener != null) {
         this.toolBar.addMouseMotionListener(this.dockingListener);
         this.toolBar.addMouseListener(this.dockingListener);
      }

      this.propertyListener = this.createPropertyListener();
      if (this.propertyListener != null) {
         this.toolBar.addPropertyChangeListener(this.propertyListener);
      }

      this.toolBarContListener = this.createToolBarContListener();
      if (this.toolBarContListener != null) {
         this.toolBar.addContainerListener(this.toolBarContListener);
      }

      this.toolBarFocusListener = this.createToolBarFocusListener();
      if (this.toolBarFocusListener != null) {
         Component[] var1 = this.toolBar.getComponents();
         Component[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Component var5 = var2[var4];
            var5.addFocusListener(this.toolBarFocusListener);
         }
      }

   }

   protected void uninstallListeners() {
      if (this.dockingListener != null) {
         this.toolBar.removeMouseMotionListener(this.dockingListener);
         this.toolBar.removeMouseListener(this.dockingListener);
         this.dockingListener = null;
      }

      if (this.propertyListener != null) {
         this.toolBar.removePropertyChangeListener(this.propertyListener);
         this.propertyListener = null;
      }

      if (this.toolBarContListener != null) {
         this.toolBar.removeContainerListener(this.toolBarContListener);
         this.toolBarContListener = null;
      }

      if (this.toolBarFocusListener != null) {
         Component[] var1 = this.toolBar.getComponents();
         Component[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Component var5 = var2[var4];
            var5.removeFocusListener(this.toolBarFocusListener);
         }

         this.toolBarFocusListener = null;
      }

      this.handler = null;
   }

   protected void installKeyboardActions() {
      InputMap var1 = this.getInputMap(1);
      SwingUtilities.replaceUIInputMap(this.toolBar, 1, var1);
      LazyActionMap.installLazyActionMap(this.toolBar, BasicToolBarUI.class, "ToolBar.actionMap");
   }

   InputMap getInputMap(int var1) {
      return var1 == 1 ? (InputMap)DefaultLookup.get(this.toolBar, this, "ToolBar.ancestorInputMap") : null;
   }

   static void loadActionMap(LazyActionMap var0) {
      var0.put(new BasicToolBarUI.Actions("navigateRight"));
      var0.put(new BasicToolBarUI.Actions("navigateLeft"));
      var0.put(new BasicToolBarUI.Actions("navigateUp"));
      var0.put(new BasicToolBarUI.Actions("navigateDown"));
   }

   protected void uninstallKeyboardActions() {
      SwingUtilities.replaceUIActionMap(this.toolBar, (ActionMap)null);
      SwingUtilities.replaceUIInputMap(this.toolBar, 1, (InputMap)null);
   }

   protected void navigateFocusedComp(int var1) {
      int var2 = this.toolBar.getComponentCount();
      int var3;
      Component var4;
      switch(var1) {
      case 1:
      case 7:
         if (this.focusedCompIndex >= 0 && this.focusedCompIndex < var2) {
            var3 = this.focusedCompIndex - 1;

            while(var3 != this.focusedCompIndex) {
               if (var3 < 0) {
                  var3 = var2 - 1;
               }

               var4 = this.toolBar.getComponentAtIndex(var3--);
               if (var4 != null && var4.isFocusTraversable() && var4.isEnabled()) {
                  var4.requestFocus();
                  break;
               }
            }
         }
      case 2:
      case 4:
      case 6:
      default:
         break;
      case 3:
      case 5:
         if (this.focusedCompIndex >= 0 && this.focusedCompIndex < var2) {
            var3 = this.focusedCompIndex + 1;

            while(var3 != this.focusedCompIndex) {
               if (var3 >= var2) {
                  var3 = 0;
               }

               var4 = this.toolBar.getComponentAtIndex(var3++);
               if (var4 != null && var4.isFocusTraversable() && var4.isEnabled()) {
                  var4.requestFocus();
                  break;
               }
            }
         }
      }

   }

   protected Border createRolloverBorder() {
      Object var1 = UIManager.get("ToolBar.rolloverBorder");
      if (var1 != null) {
         return (Border)var1;
      } else {
         UIDefaults var2 = UIManager.getLookAndFeelDefaults();
         return new CompoundBorder(new BasicBorders.RolloverButtonBorder(var2.getColor("controlShadow"), var2.getColor("controlDkShadow"), var2.getColor("controlHighlight"), var2.getColor("controlLtHighlight")), new BasicBorders.RolloverMarginBorder());
      }
   }

   protected Border createNonRolloverBorder() {
      Object var1 = UIManager.get("ToolBar.nonrolloverBorder");
      if (var1 != null) {
         return (Border)var1;
      } else {
         UIDefaults var2 = UIManager.getLookAndFeelDefaults();
         return new CompoundBorder(new BasicBorders.ButtonBorder(var2.getColor("Button.shadow"), var2.getColor("Button.darkShadow"), var2.getColor("Button.light"), var2.getColor("Button.highlight")), new BasicBorders.RolloverMarginBorder());
      }
   }

   private Border createNonRolloverToggleBorder() {
      UIDefaults var1 = UIManager.getLookAndFeelDefaults();
      return new CompoundBorder(new BasicBorders.RadioButtonBorder(var1.getColor("ToggleButton.shadow"), var1.getColor("ToggleButton.darkShadow"), var1.getColor("ToggleButton.light"), var1.getColor("ToggleButton.highlight")), new BasicBorders.RolloverMarginBorder());
   }

   protected JFrame createFloatingFrame(JToolBar var1) {
      Window var2 = SwingUtilities.getWindowAncestor(var1);
      JFrame var3 = new JFrame(var1.getName(), var2 != null ? var2.getGraphicsConfiguration() : null) {
         protected JRootPane createRootPane() {
            JRootPane var1 = new JRootPane() {
               private boolean packing = false;

               public void validate() {
                  super.validate();
                  if (!this.packing) {
                     this.packing = true;
                     pack();
                     this.packing = false;
                  }

               }
            };
            var1.setOpaque(true);
            return var1;
         }
      };
      var3.getRootPane().setName("ToolBar.FloatingFrame");
      var3.setResizable(false);
      WindowListener var4 = this.createFrameListener();
      var3.addWindowListener(var4);
      return var3;
   }

   protected RootPaneContainer createFloatingWindow(JToolBar var1) {
      Window var3 = SwingUtilities.getWindowAncestor(var1);

      class ToolBarDialog extends JDialog {
         public ToolBarDialog(Frame var2, String var3, boolean var4) {
            super(var2, var3, var4);
         }

         public ToolBarDialog(Dialog var2, String var3, boolean var4) {
            super(var2, var3, var4);
         }

         protected JRootPane createRootPane() {
            JRootPane var1 = new JRootPane() {
               private boolean packing = false;

               public void validate() {
                  super.validate();
                  if (!this.packing) {
                     this.packing = true;
                     ToolBarDialog.this.pack();
                     this.packing = false;
                  }

               }
            };
            var1.setOpaque(true);
            return var1;
         }
      }

      ToolBarDialog var2;
      if (var3 instanceof Frame) {
         var2 = new ToolBarDialog((Frame)var3, var1.getName(), false);
      } else if (var3 instanceof Dialog) {
         var2 = new ToolBarDialog((Dialog)var3, var1.getName(), false);
      } else {
         var2 = new ToolBarDialog((Frame)null, var1.getName(), false);
      }

      var2.getRootPane().setName("ToolBar.FloatingWindow");
      var2.setTitle(var1.getName());
      var2.setResizable(false);
      WindowListener var4 = this.createFrameListener();
      var2.addWindowListener(var4);
      return var2;
   }

   protected BasicToolBarUI.DragWindow createDragWindow(JToolBar var1) {
      Window var2 = null;
      if (this.toolBar != null) {
         Container var3;
         for(var3 = this.toolBar.getParent(); var3 != null && !(var3 instanceof Window); var3 = var3.getParent()) {
         }

         if (var3 != null && var3 instanceof Window) {
            var2 = (Window)var3;
         }
      }

      if (this.floatingToolBar == null) {
         this.floatingToolBar = this.createFloatingWindow(this.toolBar);
      }

      if (this.floatingToolBar instanceof Window) {
         var2 = (Window)this.floatingToolBar;
      }

      BasicToolBarUI.DragWindow var4 = new BasicToolBarUI.DragWindow(var2);
      return var4;
   }

   public boolean isRolloverBorders() {
      return this.rolloverBorders;
   }

   public void setRolloverBorders(boolean var1) {
      this.rolloverBorders = var1;
      if (this.rolloverBorders) {
         this.installRolloverBorders(this.toolBar);
      } else {
         this.installNonRolloverBorders(this.toolBar);
      }

   }

   protected void installRolloverBorders(JComponent var1) {
      Component[] var2 = var1.getComponents();
      Component[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Component var6 = var3[var5];
         if (var6 instanceof JComponent) {
            ((JComponent)var6).updateUI();
            this.setBorderToRollover(var6);
         }
      }

   }

   protected void installNonRolloverBorders(JComponent var1) {
      Component[] var2 = var1.getComponents();
      Component[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Component var6 = var3[var5];
         if (var6 instanceof JComponent) {
            ((JComponent)var6).updateUI();
            this.setBorderToNonRollover(var6);
         }
      }

   }

   protected void installNormalBorders(JComponent var1) {
      Component[] var2 = var1.getComponents();
      Component[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Component var6 = var3[var5];
         this.setBorderToNormal(var6);
      }

   }

   protected void setBorderToRollover(Component var1) {
      if (var1 instanceof AbstractButton) {
         AbstractButton var2 = (AbstractButton)var1;
         Border var3 = (Border)this.borderTable.get(var2);
         if (var3 == null || var3 instanceof UIResource) {
            this.borderTable.put(var2, var2.getBorder());
         }

         if (var2.getBorder() instanceof UIResource) {
            var2.setBorder(this.getRolloverBorder(var2));
         }

         this.rolloverTable.put(var2, var2.isRolloverEnabled() ? Boolean.TRUE : Boolean.FALSE);
         var2.setRolloverEnabled(true);
      }

   }

   protected Border getRolloverBorder(AbstractButton var1) {
      return rolloverBorder;
   }

   protected void setBorderToNonRollover(Component var1) {
      if (var1 instanceof AbstractButton) {
         AbstractButton var2 = (AbstractButton)var1;
         Border var3 = (Border)this.borderTable.get(var2);
         if (var3 == null || var3 instanceof UIResource) {
            this.borderTable.put(var2, var2.getBorder());
         }

         if (var2.getBorder() instanceof UIResource) {
            var2.setBorder(this.getNonRolloverBorder(var2));
         }

         this.rolloverTable.put(var2, var2.isRolloverEnabled() ? Boolean.TRUE : Boolean.FALSE);
         var2.setRolloverEnabled(false);
      }

   }

   protected Border getNonRolloverBorder(AbstractButton var1) {
      return var1 instanceof JToggleButton ? nonRolloverToggleBorder : nonRolloverBorder;
   }

   protected void setBorderToNormal(Component var1) {
      if (var1 instanceof AbstractButton) {
         AbstractButton var2 = (AbstractButton)var1;
         Border var3 = (Border)this.borderTable.remove(var2);
         var2.setBorder(var3);
         Boolean var4 = (Boolean)this.rolloverTable.remove(var2);
         if (var4 != null) {
            var2.setRolloverEnabled(var4);
         }
      }

   }

   public void setFloatingLocation(int var1, int var2) {
      this.floatingX = var1;
      this.floatingY = var2;
   }

   public boolean isFloating() {
      return this.floating;
   }

   public void setFloating(boolean var1, Point var2) {
      if (this.toolBar.isFloatable()) {
         boolean var3 = false;
         Window var4 = SwingUtilities.getWindowAncestor(this.toolBar);
         if (var4 != null) {
            var3 = var4.isVisible();
         }

         if (this.dragWindow != null) {
            this.dragWindow.setVisible(false);
         }

         this.floating = var1;
         if (this.floatingToolBar == null) {
            this.floatingToolBar = this.createFloatingWindow(this.toolBar);
         }

         if (var1) {
            if (this.dockingSource == null) {
               this.dockingSource = this.toolBar.getParent();
               this.dockingSource.remove(this.toolBar);
            }

            this.constraintBeforeFloating = this.calculateConstraint();
            if (this.propertyListener != null) {
               UIManager.addPropertyChangeListener(this.propertyListener);
            }

            this.floatingToolBar.getContentPane().add((Component)this.toolBar, (Object)"Center");
            if (this.floatingToolBar instanceof Window) {
               ((Window)this.floatingToolBar).pack();
               ((Window)this.floatingToolBar).setLocation(this.floatingX, this.floatingY);
               if (var3) {
                  ((Window)this.floatingToolBar).show();
               } else {
                  var4.addWindowListener(new WindowAdapter() {
                     public void windowOpened(WindowEvent var1) {
                        ((Window)BasicToolBarUI.this.floatingToolBar).show();
                     }
                  });
               }
            }
         } else {
            if (this.floatingToolBar == null) {
               this.floatingToolBar = this.createFloatingWindow(this.toolBar);
            }

            if (this.floatingToolBar instanceof Window) {
               ((Window)this.floatingToolBar).setVisible(false);
            }

            this.floatingToolBar.getContentPane().remove(this.toolBar);
            String var5 = this.getDockingConstraint(this.dockingSource, var2);
            if (var5 == null) {
               var5 = "North";
            }

            int var6 = this.mapConstraintToOrientation(var5);
            this.setOrientation(var6);
            if (this.dockingSource == null) {
               this.dockingSource = this.toolBar.getParent();
            }

            if (this.propertyListener != null) {
               UIManager.removePropertyChangeListener(this.propertyListener);
            }

            this.dockingSource.add((String)var5, (Component)this.toolBar);
         }

         this.dockingSource.invalidate();
         Container var7 = this.dockingSource.getParent();
         if (var7 != null) {
            var7.validate();
         }

         this.dockingSource.repaint();
      }

   }

   private int mapConstraintToOrientation(String var1) {
      int var2 = this.toolBar.getOrientation();
      if (var1 != null) {
         if (!var1.equals("East") && !var1.equals("West")) {
            if (var1.equals("North") || var1.equals("South")) {
               var2 = 0;
            }
         } else {
            var2 = 1;
         }
      }

      return var2;
   }

   public void setOrientation(int var1) {
      this.toolBar.setOrientation(var1);
      if (this.dragWindow != null) {
         this.dragWindow.setOrientation(var1);
      }

   }

   public Color getDockingColor() {
      return this.dockingColor;
   }

   public void setDockingColor(Color var1) {
      this.dockingColor = var1;
   }

   public Color getFloatingColor() {
      return this.floatingColor;
   }

   public void setFloatingColor(Color var1) {
      this.floatingColor = var1;
   }

   private boolean isBlocked(Component var1, Object var2) {
      if (var1 instanceof Container) {
         Container var3 = (Container)var1;
         LayoutManager var4 = var3.getLayout();
         if (var4 instanceof BorderLayout) {
            BorderLayout var5 = (BorderLayout)var4;
            Component var6 = var5.getLayoutComponent(var3, var2);
            return var6 != null && var6 != this.toolBar;
         }
      }

      return false;
   }

   public boolean canDock(Component var1, Point var2) {
      return var2 != null && this.getDockingConstraint(var1, var2) != null;
   }

   private String calculateConstraint() {
      String var1 = null;
      LayoutManager var2 = this.dockingSource.getLayout();
      if (var2 instanceof BorderLayout) {
         var1 = (String)((BorderLayout)var2).getConstraints(this.toolBar);
      }

      return var1 != null ? var1 : this.constraintBeforeFloating;
   }

   private String getDockingConstraint(Component var1, Point var2) {
      if (var2 == null) {
         return this.constraintBeforeFloating;
      } else {
         if (var1.contains(var2)) {
            this.dockingSensitivity = this.toolBar.getOrientation() == 0 ? this.toolBar.getSize().height : this.toolBar.getSize().width;
            if (var2.y < this.dockingSensitivity && !this.isBlocked(var1, "North")) {
               return "North";
            }

            if (var2.x >= var1.getWidth() - this.dockingSensitivity && !this.isBlocked(var1, "East")) {
               return "East";
            }

            if (var2.x < this.dockingSensitivity && !this.isBlocked(var1, "West")) {
               return "West";
            }

            if (var2.y >= var1.getHeight() - this.dockingSensitivity && !this.isBlocked(var1, "South")) {
               return "South";
            }
         }

         return null;
      }
   }

   protected void dragTo(Point var1, Point var2) {
      if (this.toolBar.isFloatable()) {
         try {
            if (this.dragWindow == null) {
               this.dragWindow = this.createDragWindow(this.toolBar);
            }

            Point var3 = this.dragWindow.getOffset();
            if (var3 == null) {
               Dimension var4 = this.toolBar.getPreferredSize();
               var3 = new Point(var4.width / 2, var4.height / 2);
               this.dragWindow.setOffset(var3);
            }

            Point var11 = new Point(var2.x + var1.x, var2.y + var1.y);
            Point var5 = new Point(var11.x - var3.x, var11.y - var3.y);
            if (this.dockingSource == null) {
               this.dockingSource = this.toolBar.getParent();
            }

            this.constraintBeforeFloating = this.calculateConstraint();
            Point var6 = this.dockingSource.getLocationOnScreen();
            Point var7 = new Point(var11.x - var6.x, var11.y - var6.y);
            if (this.canDock(this.dockingSource, var7)) {
               this.dragWindow.setBackground(this.getDockingColor());
               String var8 = this.getDockingConstraint(this.dockingSource, var7);
               int var9 = this.mapConstraintToOrientation(var8);
               this.dragWindow.setOrientation(var9);
               this.dragWindow.setBorderColor(this.dockingBorderColor);
            } else {
               this.dragWindow.setBackground(this.getFloatingColor());
               this.dragWindow.setBorderColor(this.floatingBorderColor);
               this.dragWindow.setOrientation(this.toolBar.getOrientation());
            }

            this.dragWindow.setLocation(var5.x, var5.y);
            if (!this.dragWindow.isVisible()) {
               Dimension var12 = this.toolBar.getPreferredSize();
               this.dragWindow.setSize(var12.width, var12.height);
               this.dragWindow.show();
            }
         } catch (IllegalComponentStateException var10) {
         }
      }

   }

   protected void floatAt(Point var1, Point var2) {
      if (this.toolBar.isFloatable()) {
         try {
            Point var3 = this.dragWindow.getOffset();
            if (var3 == null) {
               var3 = var1;
               this.dragWindow.setOffset(var1);
            }

            Point var4 = new Point(var2.x + var1.x, var2.y + var1.y);
            this.setFloatingLocation(var4.x - var3.x, var4.y - var3.y);
            if (this.dockingSource != null) {
               Point var5 = this.dockingSource.getLocationOnScreen();
               Point var6 = new Point(var4.x - var5.x, var4.y - var5.y);
               if (this.canDock(this.dockingSource, var6)) {
                  this.setFloating(false, var6);
               } else {
                  this.setFloating(true, (Point)null);
               }
            } else {
               this.setFloating(true, (Point)null);
            }

            this.dragWindow.setOffset((Point)null);
         } catch (IllegalComponentStateException var7) {
         }
      }

   }

   private BasicToolBarUI.Handler getHandler() {
      if (this.handler == null) {
         this.handler = new BasicToolBarUI.Handler();
      }

      return this.handler;
   }

   protected ContainerListener createToolBarContListener() {
      return this.getHandler();
   }

   protected FocusListener createToolBarFocusListener() {
      return this.getHandler();
   }

   protected PropertyChangeListener createPropertyListener() {
      return this.getHandler();
   }

   protected MouseInputListener createDockingListener() {
      this.getHandler().tb = this.toolBar;
      return this.getHandler();
   }

   protected WindowListener createFrameListener() {
      return new BasicToolBarUI.FrameListener();
   }

   protected void paintDragWindow(Graphics var1) {
      var1.setColor(this.dragWindow.getBackground());
      int var2 = this.dragWindow.getWidth();
      int var3 = this.dragWindow.getHeight();
      var1.fillRect(0, 0, var2, var3);
      var1.setColor(this.dragWindow.getBorderColor());
      var1.drawRect(0, 0, var2 - 1, var3 - 1);
   }

   protected class DragWindow extends Window {
      Color borderColor;
      int orientation;
      Point offset;

      DragWindow(Window var2) {
         super(var2);
         this.borderColor = Color.gray;
         this.orientation = BasicToolBarUI.this.toolBar.getOrientation();
      }

      public int getOrientation() {
         return this.orientation;
      }

      public void setOrientation(int var1) {
         if (this.isShowing()) {
            if (var1 == this.orientation) {
               return;
            }

            this.orientation = var1;
            Dimension var2 = this.getSize();
            this.setSize(new Dimension(var2.height, var2.width));
            if (this.offset != null) {
               if (BasicGraphicsUtils.isLeftToRight(BasicToolBarUI.this.toolBar)) {
                  this.setOffset(new Point(this.offset.y, this.offset.x));
               } else if (var1 == 0) {
                  this.setOffset(new Point(var2.height - this.offset.y, this.offset.x));
               } else {
                  this.setOffset(new Point(this.offset.y, var2.width - this.offset.x));
               }
            }

            this.repaint();
         }

      }

      public Point getOffset() {
         return this.offset;
      }

      public void setOffset(Point var1) {
         this.offset = var1;
      }

      public void setBorderColor(Color var1) {
         if (this.borderColor != var1) {
            this.borderColor = var1;
            this.repaint();
         }
      }

      public Color getBorderColor() {
         return this.borderColor;
      }

      public void paint(Graphics var1) {
         BasicToolBarUI.this.paintDragWindow(var1);
         super.paint(var1);
      }

      public Insets getInsets() {
         return new Insets(1, 1, 1, 1);
      }
   }

   public class DockingListener implements MouseInputListener {
      protected JToolBar toolBar;
      protected boolean isDragging = false;
      protected Point origin = null;

      public DockingListener(JToolBar var2) {
         this.toolBar = var2;
         BasicToolBarUI.this.getHandler().tb = var2;
      }

      public void mouseClicked(MouseEvent var1) {
         BasicToolBarUI.this.getHandler().mouseClicked(var1);
      }

      public void mousePressed(MouseEvent var1) {
         BasicToolBarUI.this.getHandler().tb = this.toolBar;
         BasicToolBarUI.this.getHandler().mousePressed(var1);
         this.isDragging = BasicToolBarUI.this.getHandler().isDragging;
      }

      public void mouseReleased(MouseEvent var1) {
         BasicToolBarUI.this.getHandler().tb = this.toolBar;
         BasicToolBarUI.this.getHandler().isDragging = this.isDragging;
         BasicToolBarUI.this.getHandler().origin = this.origin;
         BasicToolBarUI.this.getHandler().mouseReleased(var1);
         this.isDragging = BasicToolBarUI.this.getHandler().isDragging;
         this.origin = BasicToolBarUI.this.getHandler().origin;
      }

      public void mouseEntered(MouseEvent var1) {
         BasicToolBarUI.this.getHandler().mouseEntered(var1);
      }

      public void mouseExited(MouseEvent var1) {
         BasicToolBarUI.this.getHandler().mouseExited(var1);
      }

      public void mouseDragged(MouseEvent var1) {
         BasicToolBarUI.this.getHandler().tb = this.toolBar;
         BasicToolBarUI.this.getHandler().origin = this.origin;
         BasicToolBarUI.this.getHandler().mouseDragged(var1);
         this.isDragging = BasicToolBarUI.this.getHandler().isDragging;
         this.origin = BasicToolBarUI.this.getHandler().origin;
      }

      public void mouseMoved(MouseEvent var1) {
         BasicToolBarUI.this.getHandler().mouseMoved(var1);
      }
   }

   protected class PropertyListener implements PropertyChangeListener {
      public void propertyChange(PropertyChangeEvent var1) {
         BasicToolBarUI.this.getHandler().propertyChange(var1);
      }
   }

   protected class ToolBarFocusListener implements FocusListener {
      public void focusGained(FocusEvent var1) {
         BasicToolBarUI.this.getHandler().focusGained(var1);
      }

      public void focusLost(FocusEvent var1) {
         BasicToolBarUI.this.getHandler().focusLost(var1);
      }
   }

   protected class ToolBarContListener implements ContainerListener {
      public void componentAdded(ContainerEvent var1) {
         BasicToolBarUI.this.getHandler().componentAdded(var1);
      }

      public void componentRemoved(ContainerEvent var1) {
         BasicToolBarUI.this.getHandler().componentRemoved(var1);
      }
   }

   protected class FrameListener extends WindowAdapter {
      public void windowClosing(WindowEvent var1) {
         if (BasicToolBarUI.this.toolBar.isFloatable()) {
            if (BasicToolBarUI.this.dragWindow != null) {
               BasicToolBarUI.this.dragWindow.setVisible(false);
            }

            BasicToolBarUI.this.floating = false;
            if (BasicToolBarUI.this.floatingToolBar == null) {
               BasicToolBarUI.this.floatingToolBar = BasicToolBarUI.this.createFloatingWindow(BasicToolBarUI.this.toolBar);
            }

            if (BasicToolBarUI.this.floatingToolBar instanceof Window) {
               ((Window)BasicToolBarUI.this.floatingToolBar).setVisible(false);
            }

            BasicToolBarUI.this.floatingToolBar.getContentPane().remove(BasicToolBarUI.this.toolBar);
            String var2 = BasicToolBarUI.this.constraintBeforeFloating;
            if (BasicToolBarUI.this.toolBar.getOrientation() == 0) {
               if (var2 == "West" || var2 == "East") {
                  var2 = "North";
               }
            } else if (var2 == "North" || var2 == "South") {
               var2 = "West";
            }

            if (BasicToolBarUI.this.dockingSource == null) {
               BasicToolBarUI.this.dockingSource = BasicToolBarUI.this.toolBar.getParent();
            }

            if (BasicToolBarUI.this.propertyListener != null) {
               UIManager.removePropertyChangeListener(BasicToolBarUI.this.propertyListener);
            }

            BasicToolBarUI.this.dockingSource.add((Component)BasicToolBarUI.this.toolBar, (Object)var2);
            BasicToolBarUI.this.dockingSource.invalidate();
            Container var3 = BasicToolBarUI.this.dockingSource.getParent();
            if (var3 != null) {
               var3.validate();
            }

            BasicToolBarUI.this.dockingSource.repaint();
         }

      }
   }

   private class Handler implements ContainerListener, FocusListener, MouseInputListener, PropertyChangeListener {
      JToolBar tb;
      boolean isDragging;
      Point origin;

      private Handler() {
         this.isDragging = false;
         this.origin = null;
      }

      public void componentAdded(ContainerEvent var1) {
         Component var2 = var1.getChild();
         if (BasicToolBarUI.this.toolBarFocusListener != null) {
            var2.addFocusListener(BasicToolBarUI.this.toolBarFocusListener);
         }

         if (BasicToolBarUI.this.isRolloverBorders()) {
            BasicToolBarUI.this.setBorderToRollover(var2);
         } else {
            BasicToolBarUI.this.setBorderToNonRollover(var2);
         }

      }

      public void componentRemoved(ContainerEvent var1) {
         Component var2 = var1.getChild();
         if (BasicToolBarUI.this.toolBarFocusListener != null) {
            var2.removeFocusListener(BasicToolBarUI.this.toolBarFocusListener);
         }

         BasicToolBarUI.this.setBorderToNormal(var2);
      }

      public void focusGained(FocusEvent var1) {
         Component var2 = var1.getComponent();
         BasicToolBarUI.this.focusedCompIndex = BasicToolBarUI.this.toolBar.getComponentIndex(var2);
      }

      public void focusLost(FocusEvent var1) {
      }

      public void mousePressed(MouseEvent var1) {
         if (this.tb.isEnabled()) {
            this.isDragging = false;
         }
      }

      public void mouseReleased(MouseEvent var1) {
         if (this.tb.isEnabled()) {
            if (this.isDragging) {
               Point var2 = var1.getPoint();
               if (this.origin == null) {
                  this.origin = var1.getComponent().getLocationOnScreen();
               }

               BasicToolBarUI.this.floatAt(var2, this.origin);
            }

            this.origin = null;
            this.isDragging = false;
         }
      }

      public void mouseDragged(MouseEvent var1) {
         if (this.tb.isEnabled()) {
            this.isDragging = true;
            Point var2 = var1.getPoint();
            if (this.origin == null) {
               this.origin = var1.getComponent().getLocationOnScreen();
            }

            BasicToolBarUI.this.dragTo(var2, this.origin);
         }
      }

      public void mouseClicked(MouseEvent var1) {
      }

      public void mouseEntered(MouseEvent var1) {
      }

      public void mouseExited(MouseEvent var1) {
      }

      public void mouseMoved(MouseEvent var1) {
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if (var2 == "lookAndFeel") {
            BasicToolBarUI.this.toolBar.updateUI();
         } else if (var2 == "orientation") {
            Component[] var3 = BasicToolBarUI.this.toolBar.getComponents();
            int var4 = (Integer)var1.getNewValue();

            for(int var6 = 0; var6 < var3.length; ++var6) {
               if (var3[var6] instanceof JToolBar.Separator) {
                  JToolBar.Separator var5 = (JToolBar.Separator)var3[var6];
                  if (var4 == 0) {
                     var5.setOrientation(1);
                  } else {
                     var5.setOrientation(0);
                  }

                  Dimension var7 = var5.getSeparatorSize();
                  if (var7 != null && var7.width != var7.height) {
                     Dimension var8 = new Dimension(var7.height, var7.width);
                     var5.setSeparatorSize(var8);
                  }
               }
            }
         } else if (var2 == BasicToolBarUI.IS_ROLLOVER) {
            BasicToolBarUI.this.installNormalBorders(BasicToolBarUI.this.toolBar);
            BasicToolBarUI.this.setRolloverBorders((Boolean)var1.getNewValue());
         }

      }

      // $FF: synthetic method
      Handler(Object var2) {
         this();
      }
   }

   private static class Actions extends UIAction {
      private static final String NAVIGATE_RIGHT = "navigateRight";
      private static final String NAVIGATE_LEFT = "navigateLeft";
      private static final String NAVIGATE_UP = "navigateUp";
      private static final String NAVIGATE_DOWN = "navigateDown";

      public Actions(String var1) {
         super(var1);
      }

      public void actionPerformed(ActionEvent var1) {
         String var2 = this.getName();
         JToolBar var3 = (JToolBar)var1.getSource();
         BasicToolBarUI var4 = (BasicToolBarUI)BasicLookAndFeel.getUIOfType(var3.getUI(), BasicToolBarUI.class);
         if ("navigateRight" == var2) {
            var4.navigateFocusedComp(3);
         } else if ("navigateLeft" == var2) {
            var4.navigateFocusedComp(7);
         } else if ("navigateUp" == var2) {
            var4.navigateFocusedComp(1);
         } else if ("navigateDown" == var2) {
            var4.navigateFocusedComp(5);
         }

      }
   }
}
