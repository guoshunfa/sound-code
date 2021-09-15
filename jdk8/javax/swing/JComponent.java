package javax.swing;

import java.applet.Applet;
import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.peer.LightweightPeer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.Transient;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleExtendedComponent;
import javax.accessibility.AccessibleKeyBinding;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.AncestorListener;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.ComponentUI;
import javax.swing.table.JTableHeader;
import sun.awt.CausedFocusEvent;
import sun.awt.RequestFocusController;
import sun.awt.SunToolkit;
import sun.swing.SwingUtilities2;
import sun.swing.UIClientPropertyKey;

public abstract class JComponent extends Container implements Serializable, TransferHandler.HasGetTransferHandler {
   private static final String uiClassID = "ComponentUI";
   private static final Hashtable<ObjectInputStream, JComponent.ReadObjectCallback> readObjectCallbacks = new Hashtable(1);
   private static Set<KeyStroke> managingFocusForwardTraversalKeys;
   private static Set<KeyStroke> managingFocusBackwardTraversalKeys;
   private static final int NOT_OBSCURED = 0;
   private static final int PARTIALLY_OBSCURED = 1;
   private static final int COMPLETELY_OBSCURED = 2;
   static boolean DEBUG_GRAPHICS_LOADED;
   private static final Object INPUT_VERIFIER_SOURCE_KEY = new StringBuilder("InputVerifierSourceKey");
   private boolean isAlignmentXSet;
   private float alignmentX;
   private boolean isAlignmentYSet;
   private float alignmentY;
   protected transient ComponentUI ui;
   protected EventListenerList listenerList = new EventListenerList();
   private transient ArrayTable clientProperties;
   private VetoableChangeSupport vetoableChangeSupport;
   private boolean autoscrolls;
   private Border border;
   private int flags;
   private InputVerifier inputVerifier = null;
   private boolean verifyInputWhenFocusTarget = true;
   transient Component paintingChild;
   public static final int WHEN_FOCUSED = 0;
   public static final int WHEN_ANCESTOR_OF_FOCUSED_COMPONENT = 1;
   public static final int WHEN_IN_FOCUSED_WINDOW = 2;
   public static final int UNDEFINED_CONDITION = -1;
   private static final String KEYBOARD_BINDINGS_KEY = "_KeyboardBindings";
   private static final String WHEN_IN_FOCUSED_WINDOW_BINDINGS = "_WhenInFocusedWindow";
   public static final String TOOL_TIP_TEXT_KEY = "ToolTipText";
   private static final String NEXT_FOCUS = "nextFocus";
   private JPopupMenu popupMenu;
   private static final int IS_DOUBLE_BUFFERED = 0;
   private static final int ANCESTOR_USING_BUFFER = 1;
   private static final int IS_PAINTING_TILE = 2;
   private static final int IS_OPAQUE = 3;
   private static final int KEY_EVENTS_ENABLED = 4;
   private static final int FOCUS_INPUTMAP_CREATED = 5;
   private static final int ANCESTOR_INPUTMAP_CREATED = 6;
   private static final int WIF_INPUTMAP_CREATED = 7;
   private static final int ACTIONMAP_CREATED = 8;
   private static final int CREATED_DOUBLE_BUFFER = 9;
   private static final int IS_PRINTING = 11;
   private static final int IS_PRINTING_ALL = 12;
   private static final int IS_REPAINTING = 13;
   private static final int WRITE_OBJ_COUNTER_FIRST = 14;
   private static final int RESERVED_1 = 15;
   private static final int RESERVED_2 = 16;
   private static final int RESERVED_3 = 17;
   private static final int RESERVED_4 = 18;
   private static final int RESERVED_5 = 19;
   private static final int RESERVED_6 = 20;
   private static final int WRITE_OBJ_COUNTER_LAST = 21;
   private static final int REQUEST_FOCUS_DISABLED = 22;
   private static final int INHERITS_POPUP_MENU = 23;
   private static final int OPAQUE_SET = 24;
   private static final int AUTOSCROLLS_SET = 25;
   private static final int FOCUS_TRAVERSAL_KEYS_FORWARD_SET = 26;
   private static final int FOCUS_TRAVERSAL_KEYS_BACKWARD_SET = 27;
   private transient AtomicBoolean revalidateRunnableScheduled = new AtomicBoolean(false);
   private static List<Rectangle> tempRectangles = new ArrayList(11);
   private InputMap focusInputMap;
   private InputMap ancestorInputMap;
   private ComponentInputMap windowInputMap;
   private ActionMap actionMap;
   private static final String defaultLocale = "JComponent.defaultLocale";
   private static Component componentObtainingGraphicsFrom;
   private static Object componentObtainingGraphicsFromLock = new StringBuilder("componentObtainingGraphicsFrom");
   private transient Object aaTextInfo;
   static final RequestFocusController focusController = new RequestFocusController() {
      public boolean acceptRequestFocus(Component var1, Component var2, boolean var3, boolean var4, CausedFocusEvent.Cause var5) {
         if (var2 != null && var2 instanceof JComponent) {
            if (var1 != null && var1 instanceof JComponent) {
               JComponent var6 = (JComponent)var2;
               if (!var6.getVerifyInputWhenFocusTarget()) {
                  return true;
               } else {
                  JComponent var7 = (JComponent)var1;
                  InputVerifier var8 = var7.getInputVerifier();
                  if (var8 == null) {
                     return true;
                  } else {
                     Object var9 = SwingUtilities.appContextGet(JComponent.INPUT_VERIFIER_SOURCE_KEY);
                     if (var9 == var7) {
                        return true;
                     } else {
                        SwingUtilities.appContextPut(JComponent.INPUT_VERIFIER_SOURCE_KEY, var7);

                        boolean var10;
                        try {
                           var10 = var8.shouldYieldFocus(var7);
                        } finally {
                           if (var9 != null) {
                              SwingUtilities.appContextPut(JComponent.INPUT_VERIFIER_SOURCE_KEY, var9);
                           } else {
                              SwingUtilities.appContextRemove(JComponent.INPUT_VERIFIER_SOURCE_KEY);
                           }

                        }

                        return var10;
                     }
                  }
               }
            } else {
               return true;
            }
         } else {
            return true;
         }
      }
   };

   static Graphics safelyGetGraphics(Component var0) {
      return safelyGetGraphics(var0, SwingUtilities.getRoot(var0));
   }

   static Graphics safelyGetGraphics(Component var0, Component var1) {
      synchronized(componentObtainingGraphicsFromLock) {
         componentObtainingGraphicsFrom = var1;
         Graphics var3 = var0.getGraphics();
         componentObtainingGraphicsFrom = null;
         return var3;
      }
   }

   static void getGraphicsInvoked(Component var0) {
      if (!isComponentObtainingGraphicsFrom(var0)) {
         JRootPane var1 = ((RootPaneContainer)var0).getRootPane();
         if (var1 != null) {
            var1.disableTrueDoubleBuffering();
         }
      }

   }

   private static boolean isComponentObtainingGraphicsFrom(Component var0) {
      synchronized(componentObtainingGraphicsFromLock) {
         return componentObtainingGraphicsFrom == var0;
      }
   }

   static Set<KeyStroke> getManagingFocusForwardTraversalKeys() {
      Class var0 = JComponent.class;
      synchronized(JComponent.class) {
         if (managingFocusForwardTraversalKeys == null) {
            managingFocusForwardTraversalKeys = new HashSet(1);
            managingFocusForwardTraversalKeys.add(KeyStroke.getKeyStroke(9, 2));
         }
      }

      return managingFocusForwardTraversalKeys;
   }

   static Set<KeyStroke> getManagingFocusBackwardTraversalKeys() {
      Class var0 = JComponent.class;
      synchronized(JComponent.class) {
         if (managingFocusBackwardTraversalKeys == null) {
            managingFocusBackwardTraversalKeys = new HashSet(1);
            managingFocusBackwardTraversalKeys.add(KeyStroke.getKeyStroke(9, 3));
         }
      }

      return managingFocusBackwardTraversalKeys;
   }

   private static Rectangle fetchRectangle() {
      synchronized(tempRectangles) {
         int var2 = tempRectangles.size();
         Rectangle var1;
         if (var2 > 0) {
            var1 = (Rectangle)tempRectangles.remove(var2 - 1);
         } else {
            var1 = new Rectangle(0, 0, 0, 0);
         }

         return var1;
      }
   }

   private static void recycleRectangle(Rectangle var0) {
      synchronized(tempRectangles) {
         tempRectangles.add(var0);
      }
   }

   public void setInheritsPopupMenu(boolean var1) {
      boolean var2 = this.getFlag(23);
      this.setFlag(23, var1);
      this.firePropertyChange("inheritsPopupMenu", var2, var1);
   }

   public boolean getInheritsPopupMenu() {
      return this.getFlag(23);
   }

   public void setComponentPopupMenu(JPopupMenu var1) {
      if (var1 != null) {
         this.enableEvents(16L);
      }

      JPopupMenu var2 = this.popupMenu;
      this.popupMenu = var1;
      this.firePropertyChange("componentPopupMenu", var2, var1);
   }

   public JPopupMenu getComponentPopupMenu() {
      if (!this.getInheritsPopupMenu()) {
         return this.popupMenu;
      } else if (this.popupMenu != null) {
         return this.popupMenu;
      } else {
         for(Container var1 = this.getParent(); var1 != null; var1 = var1.getParent()) {
            if (var1 instanceof JComponent) {
               return ((JComponent)var1).getComponentPopupMenu();
            }

            if (var1 instanceof Window || var1 instanceof Applet) {
               break;
            }
         }

         return null;
      }
   }

   public JComponent() {
      this.enableEvents(8L);
      if (this.isManagingFocus()) {
         LookAndFeel.installProperty(this, "focusTraversalKeysForward", getManagingFocusForwardTraversalKeys());
         LookAndFeel.installProperty(this, "focusTraversalKeysBackward", getManagingFocusBackwardTraversalKeys());
      }

      super.setLocale(getDefaultLocale());
   }

   public void updateUI() {
   }

   protected void setUI(ComponentUI var1) {
      this.uninstallUIAndProperties();
      this.aaTextInfo = UIManager.getDefaults().get(SwingUtilities2.AA_TEXT_PROPERTY_KEY);
      ComponentUI var2 = this.ui;
      this.ui = var1;
      if (this.ui != null) {
         this.ui.installUI(this);
      }

      this.firePropertyChange("UI", var2, var1);
      this.revalidate();
      this.repaint();
   }

   private void uninstallUIAndProperties() {
      if (this.ui != null) {
         this.ui.uninstallUI(this);
         if (this.clientProperties != null) {
            synchronized(this.clientProperties) {
               Object[] var2 = this.clientProperties.getKeys((Object[])null);
               if (var2 != null) {
                  Object[] var3 = var2;
                  int var4 = var2.length;

                  for(int var5 = 0; var5 < var4; ++var5) {
                     Object var6 = var3[var5];
                     if (var6 instanceof UIClientPropertyKey) {
                        this.putClientProperty(var6, (Object)null);
                     }
                  }
               }
            }
         }
      }

   }

   public String getUIClassID() {
      return "ComponentUI";
   }

   protected Graphics getComponentGraphics(Graphics var1) {
      Object var2 = var1;
      if (this.ui != null && DEBUG_GRAPHICS_LOADED && DebugGraphics.debugComponentCount() != 0 && this.shouldDebugGraphics() != 0 && !(var1 instanceof DebugGraphics)) {
         var2 = new DebugGraphics(var1, this);
      }

      ((Graphics)var2).setColor(this.getForeground());
      ((Graphics)var2).setFont(this.getFont());
      return (Graphics)var2;
   }

   protected void paintComponent(Graphics var1) {
      if (this.ui != null) {
         Graphics var2 = var1 == null ? null : var1.create();

         try {
            this.ui.update(var2, this);
         } finally {
            var2.dispose();
         }
      }

   }

   protected void paintChildren(Graphics var1) {
      Graphics var2 = var1;
      synchronized(this.getTreeLock()) {
         int var4 = this.getComponentCount() - 1;
         if (var4 >= 0) {
            if (this.paintingChild != null && this.paintingChild instanceof JComponent && this.paintingChild.isOpaque()) {
               while(var4 >= 0 && this.getComponent(var4) != this.paintingChild) {
                  --var4;
               }
            }

            Rectangle var5 = fetchRectangle();
            boolean var6 = !this.isOptimizedDrawingEnabled() && this.checkIfChildObscuredBySibling();
            Rectangle var7 = null;
            if (var6) {
               var7 = var2.getClipBounds();
               if (var7 == null) {
                  var7 = new Rectangle(0, 0, this.getWidth(), this.getHeight());
               }
            }

            boolean var8 = this.getFlag(11);
            Window var9 = SwingUtilities.getWindowAncestor(this);

            for(boolean var10 = var9 == null || var9.isOpaque(); var4 >= 0; --var4) {
               Component var11 = this.getComponent(var4);
               if (var11 != null) {
                  boolean var12 = var11 instanceof JComponent;
                  if ((!var10 || var12 || isLightweightComponent(var11)) && var11.isVisible()) {
                     Rectangle var13 = var11.getBounds(var5);
                     boolean var14 = var1.hitClip(var13.x, var13.y, var13.width, var13.height);
                     if (var14) {
                        if (var6 && var4 > 0) {
                           int var15 = var13.x;
                           int var16 = var13.y;
                           int var17 = var13.width;
                           int var18 = var13.height;
                           SwingUtilities.computeIntersection(var7.x, var7.y, var7.width, var7.height, var13);
                           if (this.getObscuredState(var4, var13.x, var13.y, var13.width, var13.height) == 2) {
                              continue;
                           }

                           var13.x = var15;
                           var13.y = var16;
                           var13.width = var17;
                           var13.height = var18;
                        }

                        Graphics var25 = var2.create(var13.x, var13.y, var13.width, var13.height);
                        var25.setColor(var11.getForeground());
                        var25.setFont(var11.getFont());
                        boolean var26 = false;

                        try {
                           if (var12) {
                              if (this.getFlag(1)) {
                                 ((JComponent)var11).setFlag(1, true);
                                 var26 = true;
                              }

                              if (this.getFlag(2)) {
                                 ((JComponent)var11).setFlag(2, true);
                                 var26 = true;
                              }

                              if (!var8) {
                                 var11.paint(var25);
                              } else if (!this.getFlag(12)) {
                                 var11.print(var25);
                              } else {
                                 var11.printAll(var25);
                              }
                           } else if (!var8) {
                              var11.paint(var25);
                           } else if (!this.getFlag(12)) {
                              var11.print(var25);
                           } else {
                              var11.printAll(var25);
                           }
                        } finally {
                           var25.dispose();
                           if (var26) {
                              ((JComponent)var11).setFlag(1, false);
                              ((JComponent)var11).setFlag(2, false);
                           }

                        }
                     }
                  }
               }
            }

            recycleRectangle(var5);
         }
      }
   }

   protected void paintBorder(Graphics var1) {
      Border var2 = this.getBorder();
      if (var2 != null) {
         var2.paintBorder(this, var1, 0, 0, this.getWidth(), this.getHeight());
      }

   }

   public void update(Graphics var1) {
      this.paint(var1);
   }

   public void paint(Graphics var1) {
      boolean var2 = false;
      if (this.getWidth() > 0 && this.getHeight() > 0) {
         Graphics var3 = this.getComponentGraphics(var1);
         Graphics var4 = var3.create();

         try {
            RepaintManager var5 = RepaintManager.currentManager(this);
            Rectangle var6 = var4.getClipBounds();
            int var7;
            int var8;
            int var9;
            int var10;
            if (var6 == null) {
               var8 = 0;
               var7 = 0;
               var9 = this.getWidth();
               var10 = this.getHeight();
            } else {
               var7 = var6.x;
               var8 = var6.y;
               var9 = var6.width;
               var10 = var6.height;
            }

            if (var9 > this.getWidth()) {
               var9 = this.getWidth();
            }

            if (var10 > this.getHeight()) {
               var10 = this.getHeight();
            }

            if (this.getParent() != null && !(this.getParent() instanceof JComponent)) {
               this.adjustPaintFlags();
               var2 = true;
            }

            boolean var13 = this.getFlag(11);
            if (!var13 && var5.isDoubleBufferingEnabled() && !this.getFlag(1) && this.isDoubleBuffered() && (this.getFlag(13) || var5.isPainting())) {
               var5.beginPaint();

               try {
                  var5.paint(this, this, var4, var7, var8, var9, var10);
               } finally {
                  var5.endPaint();
               }
            } else {
               if (var6 == null) {
                  var4.setClip(var7, var8, var9, var10);
               }

               if (!this.rectangleIsObscured(var7, var8, var9, var10)) {
                  if (!var13) {
                     this.paintComponent(var4);
                     this.paintBorder(var4);
                  } else {
                     this.printComponent(var4);
                     this.printBorder(var4);
                  }
               }

               if (!var13) {
                  this.paintChildren(var4);
               } else {
                  this.printChildren(var4);
               }
            }
         } finally {
            var4.dispose();
            if (var2) {
               this.setFlag(1, false);
               this.setFlag(2, false);
               this.setFlag(11, false);
               this.setFlag(12, false);
            }

         }

      }
   }

   void paintForceDoubleBuffered(Graphics var1) {
      RepaintManager var2 = RepaintManager.currentManager(this);
      Rectangle var3 = var1.getClipBounds();
      var2.beginPaint();
      this.setFlag(13, true);

      try {
         var2.paint(this, this, var1, var3.x, var3.y, var3.width, var3.height);
      } finally {
         var2.endPaint();
         this.setFlag(13, false);
      }

   }

   boolean isPainting() {
      for(Object var1 = this; var1 != null; var1 = ((Container)var1).getParent()) {
         if (var1 instanceof JComponent && ((JComponent)var1).getFlag(1)) {
            return true;
         }
      }

      return false;
   }

   private void adjustPaintFlags() {
      for(Container var2 = this.getParent(); var2 != null; var2 = var2.getParent()) {
         if (var2 instanceof JComponent) {
            JComponent var1 = (JComponent)var2;
            if (var1.getFlag(1)) {
               this.setFlag(1, true);
            }

            if (var1.getFlag(2)) {
               this.setFlag(2, true);
            }

            if (var1.getFlag(11)) {
               this.setFlag(11, true);
            }

            if (var1.getFlag(12)) {
               this.setFlag(12, true);
            }
            break;
         }
      }

   }

   public void printAll(Graphics var1) {
      this.setFlag(12, true);

      try {
         this.print(var1);
      } finally {
         this.setFlag(12, false);
      }

   }

   public void print(Graphics var1) {
      this.setFlag(11, true);
      this.firePropertyChange("paintingForPrint", false, true);

      try {
         this.paint(var1);
      } finally {
         this.setFlag(11, false);
         this.firePropertyChange("paintingForPrint", true, false);
      }

   }

   protected void printComponent(Graphics var1) {
      this.paintComponent(var1);
   }

   protected void printChildren(Graphics var1) {
      this.paintChildren(var1);
   }

   protected void printBorder(Graphics var1) {
      this.paintBorder(var1);
   }

   public boolean isPaintingTile() {
      return this.getFlag(2);
   }

   public final boolean isPaintingForPrint() {
      return this.getFlag(11);
   }

   /** @deprecated */
   @Deprecated
   public boolean isManagingFocus() {
      return false;
   }

   private void registerNextFocusableComponent() {
      this.registerNextFocusableComponent(this.getNextFocusableComponent());
   }

   private void registerNextFocusableComponent(Component var1) {
      if (var1 != null) {
         Object var2 = this.isFocusCycleRoot() ? this : this.getFocusCycleRootAncestor();
         Object var3 = ((Container)var2).getFocusTraversalPolicy();
         if (!(var3 instanceof LegacyGlueFocusTraversalPolicy)) {
            var3 = new LegacyGlueFocusTraversalPolicy((FocusTraversalPolicy)var3);
            ((Container)var2).setFocusTraversalPolicy((FocusTraversalPolicy)var3);
         }

         ((LegacyGlueFocusTraversalPolicy)var3).setNextFocusableComponent(this, var1);
      }
   }

   private void deregisterNextFocusableComponent() {
      Component var1 = this.getNextFocusableComponent();
      if (var1 != null) {
         Object var2 = this.isFocusCycleRoot() ? this : this.getFocusCycleRootAncestor();
         if (var2 != null) {
            FocusTraversalPolicy var3 = ((Container)var2).getFocusTraversalPolicy();
            if (var3 instanceof LegacyGlueFocusTraversalPolicy) {
               ((LegacyGlueFocusTraversalPolicy)var3).unsetNextFocusableComponent(this, var1);
            }

         }
      }
   }

   /** @deprecated */
   @Deprecated
   public void setNextFocusableComponent(Component var1) {
      boolean var2 = this.isDisplayable();
      if (var2) {
         this.deregisterNextFocusableComponent();
      }

      this.putClientProperty("nextFocus", var1);
      if (var2) {
         this.registerNextFocusableComponent(var1);
      }

   }

   /** @deprecated */
   @Deprecated
   public Component getNextFocusableComponent() {
      return (Component)this.getClientProperty("nextFocus");
   }

   public void setRequestFocusEnabled(boolean var1) {
      this.setFlag(22, !var1);
   }

   public boolean isRequestFocusEnabled() {
      return !this.getFlag(22);
   }

   public void requestFocus() {
      super.requestFocus();
   }

   public boolean requestFocus(boolean var1) {
      return super.requestFocus(var1);
   }

   public boolean requestFocusInWindow() {
      return super.requestFocusInWindow();
   }

   protected boolean requestFocusInWindow(boolean var1) {
      return super.requestFocusInWindow(var1);
   }

   public void grabFocus() {
      this.requestFocus();
   }

   public void setVerifyInputWhenFocusTarget(boolean var1) {
      boolean var2 = this.verifyInputWhenFocusTarget;
      this.verifyInputWhenFocusTarget = var1;
      this.firePropertyChange("verifyInputWhenFocusTarget", var2, var1);
   }

   public boolean getVerifyInputWhenFocusTarget() {
      return this.verifyInputWhenFocusTarget;
   }

   public FontMetrics getFontMetrics(Font var1) {
      return SwingUtilities2.getFontMetrics(this, var1);
   }

   public void setPreferredSize(Dimension var1) {
      super.setPreferredSize(var1);
   }

   @Transient
   public Dimension getPreferredSize() {
      if (this.isPreferredSizeSet()) {
         return super.getPreferredSize();
      } else {
         Dimension var1 = null;
         if (this.ui != null) {
            var1 = this.ui.getPreferredSize(this);
         }

         return var1 != null ? var1 : super.getPreferredSize();
      }
   }

   public void setMaximumSize(Dimension var1) {
      super.setMaximumSize(var1);
   }

   @Transient
   public Dimension getMaximumSize() {
      if (this.isMaximumSizeSet()) {
         return super.getMaximumSize();
      } else {
         Dimension var1 = null;
         if (this.ui != null) {
            var1 = this.ui.getMaximumSize(this);
         }

         return var1 != null ? var1 : super.getMaximumSize();
      }
   }

   public void setMinimumSize(Dimension var1) {
      super.setMinimumSize(var1);
   }

   @Transient
   public Dimension getMinimumSize() {
      if (this.isMinimumSizeSet()) {
         return super.getMinimumSize();
      } else {
         Dimension var1 = null;
         if (this.ui != null) {
            var1 = this.ui.getMinimumSize(this);
         }

         return var1 != null ? var1 : super.getMinimumSize();
      }
   }

   public boolean contains(int var1, int var2) {
      return this.ui != null ? this.ui.contains(this, var1, var2) : super.contains(var1, var2);
   }

   public void setBorder(Border var1) {
      Border var2 = this.border;
      this.border = var1;
      this.firePropertyChange("border", var2, var1);
      if (var1 != var2) {
         if (var1 == null || var2 == null || !var1.getBorderInsets(this).equals(var2.getBorderInsets(this))) {
            this.revalidate();
         }

         this.repaint();
      }

   }

   public Border getBorder() {
      return this.border;
   }

   public Insets getInsets() {
      return this.border != null ? this.border.getBorderInsets(this) : super.getInsets();
   }

   public Insets getInsets(Insets var1) {
      if (var1 == null) {
         var1 = new Insets(0, 0, 0, 0);
      }

      if (this.border != null) {
         return this.border instanceof AbstractBorder ? ((AbstractBorder)this.border).getBorderInsets(this, var1) : this.border.getBorderInsets(this);
      } else {
         var1.left = var1.top = var1.right = var1.bottom = 0;
         return var1;
      }
   }

   public float getAlignmentY() {
      return this.isAlignmentYSet ? this.alignmentY : super.getAlignmentY();
   }

   public void setAlignmentY(float var1) {
      this.alignmentY = var1 > 1.0F ? 1.0F : (var1 < 0.0F ? 0.0F : var1);
      this.isAlignmentYSet = true;
   }

   public float getAlignmentX() {
      return this.isAlignmentXSet ? this.alignmentX : super.getAlignmentX();
   }

   public void setAlignmentX(float var1) {
      this.alignmentX = var1 > 1.0F ? 1.0F : (var1 < 0.0F ? 0.0F : var1);
      this.isAlignmentXSet = true;
   }

   public void setInputVerifier(InputVerifier var1) {
      InputVerifier var2 = (InputVerifier)this.getClientProperty(ClientPropertyKey.JComponent_INPUT_VERIFIER);
      this.putClientProperty(ClientPropertyKey.JComponent_INPUT_VERIFIER, var1);
      this.firePropertyChange("inputVerifier", var2, var1);
   }

   public InputVerifier getInputVerifier() {
      return (InputVerifier)this.getClientProperty(ClientPropertyKey.JComponent_INPUT_VERIFIER);
   }

   public Graphics getGraphics() {
      if (DEBUG_GRAPHICS_LOADED && this.shouldDebugGraphics() != 0) {
         DebugGraphics var1 = new DebugGraphics(super.getGraphics(), this);
         return var1;
      } else {
         return super.getGraphics();
      }
   }

   public void setDebugGraphicsOptions(int var1) {
      DebugGraphics.setDebugOptions(this, var1);
   }

   public int getDebugGraphicsOptions() {
      return DebugGraphics.getDebugOptions(this);
   }

   int shouldDebugGraphics() {
      return DebugGraphics.shouldComponentDebug(this);
   }

   public void registerKeyboardAction(ActionListener var1, String var2, KeyStroke var3, int var4) {
      InputMap var5 = this.getInputMap(var4, true);
      if (var5 != null) {
         ActionMap var6 = this.getActionMap(true);
         JComponent.ActionStandin var7 = new JComponent.ActionStandin(var1, var2);
         var5.put(var3, var7);
         if (var6 != null) {
            var6.put(var7, var7);
         }
      }

   }

   private void registerWithKeyboardManager(boolean var1) {
      InputMap var2 = this.getInputMap(2, false);
      Hashtable var4 = (Hashtable)this.getClientProperty("_WhenInFocusedWindow");
      KeyStroke[] var3;
      int var5;
      if (var2 != null) {
         var3 = var2.allKeys();
         if (var3 != null) {
            for(var5 = var3.length - 1; var5 >= 0; --var5) {
               if (!var1 || var4 == null || var4.get(var3[var5]) == null) {
                  this.registerWithKeyboardManager(var3[var5]);
               }

               if (var4 != null) {
                  var4.remove(var3[var5]);
               }
            }
         }
      } else {
         var3 = null;
      }

      if (var4 != null && var4.size() > 0) {
         Enumeration var7 = var4.keys();

         while(var7.hasMoreElements()) {
            KeyStroke var6 = (KeyStroke)var7.nextElement();
            this.unregisterWithKeyboardManager(var6);
         }

         var4.clear();
      }

      if (var3 != null && var3.length > 0) {
         if (var4 == null) {
            var4 = new Hashtable(var3.length);
            this.putClientProperty("_WhenInFocusedWindow", var4);
         }

         for(var5 = var3.length - 1; var5 >= 0; --var5) {
            var4.put(var3[var5], var3[var5]);
         }
      } else {
         this.putClientProperty("_WhenInFocusedWindow", (Object)null);
      }

   }

   private void unregisterWithKeyboardManager() {
      Hashtable var1 = (Hashtable)this.getClientProperty("_WhenInFocusedWindow");
      if (var1 != null && var1.size() > 0) {
         Enumeration var2 = var1.keys();

         while(var2.hasMoreElements()) {
            KeyStroke var3 = (KeyStroke)var2.nextElement();
            this.unregisterWithKeyboardManager(var3);
         }
      }

      this.putClientProperty("_WhenInFocusedWindow", (Object)null);
   }

   void componentInputMapChanged(ComponentInputMap var1) {
      InputMap var2;
      for(var2 = this.getInputMap(2, false); var2 != var1 && var2 != null; var2 = var2.getParent()) {
      }

      if (var2 != null) {
         this.registerWithKeyboardManager(false);
      }

   }

   private void registerWithKeyboardManager(KeyStroke var1) {
      KeyboardManager.getCurrentManager().registerKeyStroke(var1, this);
   }

   private void unregisterWithKeyboardManager(KeyStroke var1) {
      KeyboardManager.getCurrentManager().unregisterKeyStroke(var1, this);
   }

   public void registerKeyboardAction(ActionListener var1, KeyStroke var2, int var3) {
      this.registerKeyboardAction(var1, (String)null, var2, var3);
   }

   public void unregisterKeyboardAction(KeyStroke var1) {
      ActionMap var2 = this.getActionMap(false);

      for(int var3 = 0; var3 < 3; ++var3) {
         InputMap var4 = this.getInputMap(var3, false);
         if (var4 != null) {
            Object var5 = var4.get(var1);
            if (var2 != null && var5 != null) {
               var2.remove(var5);
            }

            var4.remove(var1);
         }
      }

   }

   public KeyStroke[] getRegisteredKeyStrokes() {
      int[] var1 = new int[3];
      KeyStroke[][] var2 = new KeyStroke[3][];

      for(int var3 = 0; var3 < 3; ++var3) {
         InputMap var4 = this.getInputMap(var3, false);
         var2[var3] = var4 != null ? var4.allKeys() : null;
         var1[var3] = var2[var3] != null ? var2[var3].length : 0;
      }

      KeyStroke[] var6 = new KeyStroke[var1[0] + var1[1] + var1[2]];
      int var7 = 0;

      for(int var5 = 0; var7 < 3; ++var7) {
         if (var1[var7] > 0) {
            System.arraycopy(var2[var7], 0, var6, var5, var1[var7]);
            var5 += var1[var7];
         }
      }

      return var6;
   }

   public int getConditionForKeyStroke(KeyStroke var1) {
      for(int var2 = 0; var2 < 3; ++var2) {
         InputMap var3 = this.getInputMap(var2, false);
         if (var3 != null && var3.get(var1) != null) {
            return var2;
         }
      }

      return -1;
   }

   public ActionListener getActionForKeyStroke(KeyStroke var1) {
      ActionMap var2 = this.getActionMap(false);
      if (var2 == null) {
         return null;
      } else {
         for(int var3 = 0; var3 < 3; ++var3) {
            InputMap var4 = this.getInputMap(var3, false);
            if (var4 != null) {
               Object var5 = var4.get(var1);
               if (var5 != null) {
                  Action var6 = var2.get(var5);
                  if (var6 instanceof JComponent.ActionStandin) {
                     return ((JComponent.ActionStandin)var6).actionListener;
                  }

                  return var6;
               }
            }
         }

         return null;
      }
   }

   public void resetKeyboardActions() {
      for(int var1 = 0; var1 < 3; ++var1) {
         InputMap var2 = this.getInputMap(var1, false);
         if (var2 != null) {
            var2.clear();
         }
      }

      ActionMap var3 = this.getActionMap(false);
      if (var3 != null) {
         var3.clear();
      }

   }

   public final void setInputMap(int var1, InputMap var2) {
      switch(var1) {
      case 0:
         this.focusInputMap = var2;
         this.setFlag(5, true);
         break;
      case 1:
         this.ancestorInputMap = var2;
         this.setFlag(6, true);
         break;
      case 2:
         if (var2 != null && !(var2 instanceof ComponentInputMap)) {
            throw new IllegalArgumentException("WHEN_IN_FOCUSED_WINDOW InputMaps must be of type ComponentInputMap");
         }

         this.windowInputMap = (ComponentInputMap)var2;
         this.setFlag(7, true);
         this.registerWithKeyboardManager(false);
         break;
      default:
         throw new IllegalArgumentException("condition must be one of JComponent.WHEN_IN_FOCUSED_WINDOW, JComponent.WHEN_FOCUSED or JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT");
      }

   }

   public final InputMap getInputMap(int var1) {
      return this.getInputMap(var1, true);
   }

   public final InputMap getInputMap() {
      return this.getInputMap(0, true);
   }

   public final void setActionMap(ActionMap var1) {
      this.actionMap = var1;
      this.setFlag(8, true);
   }

   public final ActionMap getActionMap() {
      return this.getActionMap(true);
   }

   final InputMap getInputMap(int var1, boolean var2) {
      InputMap var4;
      switch(var1) {
      case 0:
         if (this.getFlag(5)) {
            return this.focusInputMap;
         }

         if (var2) {
            var4 = new InputMap();
            this.setInputMap(var1, var4);
            return var4;
         }
         break;
      case 1:
         if (this.getFlag(6)) {
            return this.ancestorInputMap;
         }

         if (var2) {
            var4 = new InputMap();
            this.setInputMap(var1, var4);
            return var4;
         }
         break;
      case 2:
         if (this.getFlag(7)) {
            return this.windowInputMap;
         }

         if (var2) {
            ComponentInputMap var3 = new ComponentInputMap(this);
            this.setInputMap(var1, var3);
            return var3;
         }
         break;
      default:
         throw new IllegalArgumentException("condition must be one of JComponent.WHEN_IN_FOCUSED_WINDOW, JComponent.WHEN_FOCUSED or JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT");
      }

      return null;
   }

   final ActionMap getActionMap(boolean var1) {
      if (this.getFlag(8)) {
         return this.actionMap;
      } else if (var1) {
         ActionMap var2 = new ActionMap();
         this.setActionMap(var2);
         return var2;
      } else {
         return null;
      }
   }

   public int getBaseline(int var1, int var2) {
      super.getBaseline(var1, var2);
      return this.ui != null ? this.ui.getBaseline(this, var1, var2) : -1;
   }

   public Component.BaselineResizeBehavior getBaselineResizeBehavior() {
      return this.ui != null ? this.ui.getBaselineResizeBehavior(this) : Component.BaselineResizeBehavior.OTHER;
   }

   /** @deprecated */
   @Deprecated
   public boolean requestDefaultFocus() {
      Object var1 = this.isFocusCycleRoot() ? this : this.getFocusCycleRootAncestor();
      if (var1 == null) {
         return false;
      } else {
         Component var2 = ((Container)var1).getFocusTraversalPolicy().getDefaultComponent((Container)var1);
         if (var2 != null) {
            var2.requestFocus();
            return true;
         } else {
            return false;
         }
      }
   }

   public void setVisible(boolean var1) {
      if (var1 != this.isVisible()) {
         super.setVisible(var1);
         if (var1) {
            Container var2 = this.getParent();
            if (var2 != null) {
               Rectangle var3 = this.getBounds();
               var2.repaint(var3.x, var3.y, var3.width, var3.height);
            }

            this.revalidate();
         }
      }

   }

   public void setEnabled(boolean var1) {
      boolean var2 = this.isEnabled();
      super.setEnabled(var1);
      this.firePropertyChange("enabled", var2, var1);
      if (var1 != var2) {
         this.repaint();
      }

   }

   public void setForeground(Color var1) {
      Color var2 = this.getForeground();
      super.setForeground(var1);
      if (var2 != null) {
         if (var2.equals(var1)) {
            return;
         }
      } else if (var1 == null || var1.equals(var2)) {
         return;
      }

      this.repaint();
   }

   public void setBackground(Color var1) {
      Color var2 = this.getBackground();
      super.setBackground(var1);
      if (var2 != null) {
         if (var2.equals(var1)) {
            return;
         }
      } else if (var1 == null || var1.equals(var2)) {
         return;
      }

      this.repaint();
   }

   public void setFont(Font var1) {
      Font var2 = this.getFont();
      super.setFont(var1);
      if (var1 != var2) {
         this.revalidate();
         this.repaint();
      }

   }

   public static Locale getDefaultLocale() {
      Locale var0 = (Locale)SwingUtilities.appContextGet("JComponent.defaultLocale");
      if (var0 == null) {
         var0 = Locale.getDefault();
         setDefaultLocale(var0);
      }

      return var0;
   }

   public static void setDefaultLocale(Locale var0) {
      SwingUtilities.appContextPut("JComponent.defaultLocale", var0);
   }

   protected void processComponentKeyEvent(KeyEvent var1) {
   }

   protected void processKeyEvent(KeyEvent var1) {
      super.processKeyEvent(var1);
      if (!var1.isConsumed()) {
         this.processComponentKeyEvent(var1);
      }

      boolean var3 = JComponent.KeyboardState.shouldProcess(var1);
      if (!var1.isConsumed()) {
         if (var3 && this.processKeyBindings(var1, var1.getID() == 401)) {
            var1.consume();
         }

      }
   }

   protected boolean processKeyBinding(KeyStroke var1, KeyEvent var2, int var3, boolean var4) {
      InputMap var5 = this.getInputMap(var3, false);
      ActionMap var6 = this.getActionMap(false);
      if (var5 != null && var6 != null && this.isEnabled()) {
         Object var7 = var5.get(var1);
         Action var8 = var7 == null ? null : var6.get(var7);
         if (var8 != null) {
            return SwingUtilities.notifyAction(var8, var1, var2, this, var2.getModifiers());
         }
      }

      return false;
   }

   boolean processKeyBindings(KeyEvent var1, boolean var2) {
      if (!SwingUtilities.isValidKeyEventForKeyBindings(var1)) {
         return false;
      } else {
         KeyStroke var4 = null;
         KeyStroke var3;
         if (var1.getID() == 400) {
            var3 = KeyStroke.getKeyStroke(var1.getKeyChar());
         } else {
            var3 = KeyStroke.getKeyStroke(var1.getKeyCode(), var1.getModifiers(), !var2);
            if (var1.getKeyCode() != var1.getExtendedKeyCode()) {
               var4 = KeyStroke.getKeyStroke(var1.getExtendedKeyCode(), var1.getModifiers(), !var2);
            }
         }

         if (var4 != null && this.processKeyBinding(var4, var1, 0, var2)) {
            return true;
         } else if (this.processKeyBinding(var3, var1, 0, var2)) {
            return true;
         } else {
            Object var5;
            for(var5 = this; var5 != null && !(var5 instanceof Window) && !(var5 instanceof Applet); var5 = ((Container)var5).getParent()) {
               if (var5 instanceof JComponent) {
                  if (var4 != null && ((JComponent)var5).processKeyBinding(var4, var1, 1, var2)) {
                     return true;
                  }

                  if (((JComponent)var5).processKeyBinding(var3, var1, 1, var2)) {
                     return true;
                  }
               }

               if (var5 instanceof JInternalFrame && processKeyBindingsForAllComponents(var1, (Container)var5, var2)) {
                  return true;
               }
            }

            return var5 != null ? processKeyBindingsForAllComponents(var1, (Container)var5, var2) : false;
         }
      }
   }

   static boolean processKeyBindingsForAllComponents(KeyEvent var0, Container var1, boolean var2) {
      while(!KeyboardManager.getCurrentManager().fireKeyboardAction(var0, var2, (Container)var1)) {
         if (!(var1 instanceof Popup.HeavyWeightWindow)) {
            return false;
         }

         var1 = ((Window)var1).getOwner();
      }

      return true;
   }

   public void setToolTipText(String var1) {
      String var2 = this.getToolTipText();
      this.putClientProperty("ToolTipText", var1);
      ToolTipManager var3 = ToolTipManager.sharedInstance();
      if (var1 != null) {
         if (var2 == null) {
            var3.registerComponent(this);
         }
      } else {
         var3.unregisterComponent(this);
      }

   }

   public String getToolTipText() {
      return (String)this.getClientProperty("ToolTipText");
   }

   public String getToolTipText(MouseEvent var1) {
      return this.getToolTipText();
   }

   public Point getToolTipLocation(MouseEvent var1) {
      return null;
   }

   public Point getPopupLocation(MouseEvent var1) {
      return null;
   }

   public JToolTip createToolTip() {
      JToolTip var1 = new JToolTip();
      var1.setComponent(this);
      return var1;
   }

   public void scrollRectToVisible(Rectangle var1) {
      int var3 = this.getX();
      int var4 = this.getY();

      Container var2;
      for(var2 = this.getParent(); var2 != null && !(var2 instanceof JComponent) && !(var2 instanceof CellRendererPane); var2 = var2.getParent()) {
         Rectangle var5 = var2.getBounds();
         var3 += var5.x;
         var4 += var5.y;
      }

      if (var2 != null && !(var2 instanceof CellRendererPane)) {
         var1.x += var3;
         var1.y += var4;
         ((JComponent)var2).scrollRectToVisible(var1);
         var1.x -= var3;
         var1.y -= var4;
      }

   }

   public void setAutoscrolls(boolean var1) {
      this.setFlag(25, true);
      if (this.autoscrolls != var1) {
         this.autoscrolls = var1;
         if (var1) {
            this.enableEvents(16L);
            this.enableEvents(32L);
         } else {
            Autoscroller.stop(this);
         }
      }

   }

   public boolean getAutoscrolls() {
      return this.autoscrolls;
   }

   public void setTransferHandler(TransferHandler var1) {
      TransferHandler var2 = (TransferHandler)this.getClientProperty(ClientPropertyKey.JComponent_TRANSFER_HANDLER);
      this.putClientProperty(ClientPropertyKey.JComponent_TRANSFER_HANDLER, var1);
      SwingUtilities.installSwingDropTargetAsNecessary(this, var1);
      this.firePropertyChange("transferHandler", var2, var1);
   }

   public TransferHandler getTransferHandler() {
      return (TransferHandler)this.getClientProperty(ClientPropertyKey.JComponent_TRANSFER_HANDLER);
   }

   TransferHandler.DropLocation dropLocationForPoint(Point var1) {
      return null;
   }

   Object setDropLocation(TransferHandler.DropLocation var1, Object var2, boolean var3) {
      return null;
   }

   void dndDone() {
   }

   protected void processMouseEvent(MouseEvent var1) {
      if (this.autoscrolls && var1.getID() == 502) {
         Autoscroller.stop(this);
      }

      super.processMouseEvent(var1);
   }

   protected void processMouseMotionEvent(MouseEvent var1) {
      boolean var2 = true;
      if (this.autoscrolls && var1.getID() == 506) {
         var2 = !Autoscroller.isRunning(this);
         Autoscroller.processMouseDragged(var1);
      }

      if (var2) {
         super.processMouseMotionEvent(var1);
      }

   }

   void superProcessMouseMotionEvent(MouseEvent var1) {
      super.processMouseMotionEvent(var1);
   }

   void setCreatedDoubleBuffer(boolean var1) {
      this.setFlag(9, var1);
   }

   boolean getCreatedDoubleBuffer() {
      return this.getFlag(9);
   }

   /** @deprecated */
   @Deprecated
   public void enable() {
      if (!this.isEnabled()) {
         super.enable();
         if (this.accessibleContext != null) {
            this.accessibleContext.firePropertyChange("AccessibleState", (Object)null, AccessibleState.ENABLED);
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public void disable() {
      if (this.isEnabled()) {
         super.disable();
         if (this.accessibleContext != null) {
            this.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.ENABLED, (Object)null);
         }
      }

   }

   private ArrayTable getClientProperties() {
      if (this.clientProperties == null) {
         this.clientProperties = new ArrayTable();
      }

      return this.clientProperties;
   }

   public final Object getClientProperty(Object var1) {
      if (var1 == SwingUtilities2.AA_TEXT_PROPERTY_KEY) {
         return this.aaTextInfo;
      } else if (var1 == SwingUtilities2.COMPONENT_UI_PROPERTY_KEY) {
         return this.ui;
      } else if (this.clientProperties == null) {
         return null;
      } else {
         synchronized(this.clientProperties) {
            return this.clientProperties.get(var1);
         }
      }
   }

   public final void putClientProperty(Object var1, Object var2) {
      if (var1 == SwingUtilities2.AA_TEXT_PROPERTY_KEY) {
         this.aaTextInfo = var2;
      } else if (var2 != null || this.clientProperties != null) {
         ArrayTable var3 = this.getClientProperties();
         Object var4;
         synchronized(var3) {
            var4 = var3.get(var1);
            if (var2 != null) {
               var3.put(var1, var2);
            } else {
               if (var4 == null) {
                  return;
               }

               var3.remove(var1);
            }
         }

         this.clientPropertyChanged(var1, var4, var2);
         this.firePropertyChange(var1.toString(), var4, var2);
      }
   }

   void clientPropertyChanged(Object var1, Object var2, Object var3) {
   }

   void setUIProperty(String var1, Object var2) {
      if (var1 == "opaque") {
         if (!this.getFlag(24)) {
            this.setOpaque((Boolean)var2);
            this.setFlag(24, false);
         }
      } else if (var1 == "autoscrolls") {
         if (!this.getFlag(25)) {
            this.setAutoscrolls((Boolean)var2);
            this.setFlag(25, false);
         }
      } else if (var1 == "focusTraversalKeysForward") {
         if (!this.getFlag(26)) {
            super.setFocusTraversalKeys(0, (Set)var2);
         }
      } else {
         if (var1 != "focusTraversalKeysBackward") {
            throw new IllegalArgumentException("property \"" + var1 + "\" cannot be set using this method");
         }

         if (!this.getFlag(27)) {
            super.setFocusTraversalKeys(1, (Set)var2);
         }
      }

   }

   public void setFocusTraversalKeys(int var1, Set<? extends AWTKeyStroke> var2) {
      if (var1 == 0) {
         this.setFlag(26, true);
      } else if (var1 == 1) {
         this.setFlag(27, true);
      }

      super.setFocusTraversalKeys(var1, var2);
   }

   public static boolean isLightweightComponent(Component var0) {
      return var0.getPeer() instanceof LightweightPeer;
   }

   /** @deprecated */
   @Deprecated
   public void reshape(int var1, int var2, int var3, int var4) {
      super.reshape(var1, var2, var3, var4);
   }

   public Rectangle getBounds(Rectangle var1) {
      if (var1 == null) {
         return new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
      } else {
         var1.setBounds(this.getX(), this.getY(), this.getWidth(), this.getHeight());
         return var1;
      }
   }

   public Dimension getSize(Dimension var1) {
      if (var1 == null) {
         return new Dimension(this.getWidth(), this.getHeight());
      } else {
         var1.setSize(this.getWidth(), this.getHeight());
         return var1;
      }
   }

   public Point getLocation(Point var1) {
      if (var1 == null) {
         return new Point(this.getX(), this.getY());
      } else {
         var1.setLocation(this.getX(), this.getY());
         return var1;
      }
   }

   public int getX() {
      return super.getX();
   }

   public int getY() {
      return super.getY();
   }

   public int getWidth() {
      return super.getWidth();
   }

   public int getHeight() {
      return super.getHeight();
   }

   public boolean isOpaque() {
      return this.getFlag(3);
   }

   public void setOpaque(boolean var1) {
      boolean var2 = this.getFlag(3);
      this.setFlag(3, var1);
      this.setFlag(24, true);
      this.firePropertyChange("opaque", var2, var1);
   }

   boolean rectangleIsObscured(int var1, int var2, int var3, int var4) {
      int var5 = this.getComponentCount();

      for(int var6 = 0; var6 < var5; ++var6) {
         Component var7 = this.getComponent(var6);
         int var8 = var7.getX();
         int var9 = var7.getY();
         int var10 = var7.getWidth();
         int var11 = var7.getHeight();
         if (var1 >= var8 && var1 + var3 <= var8 + var10 && var2 >= var9 && var2 + var4 <= var9 + var11 && var7.isVisible()) {
            if (var7 instanceof JComponent) {
               return var7.isOpaque();
            }

            return false;
         }
      }

      return false;
   }

   static final void computeVisibleRect(Component var0, Rectangle var1) {
      Container var2 = var0.getParent();
      Rectangle var3 = var0.getBounds();
      if (var2 != null && !(var2 instanceof Window) && !(var2 instanceof Applet)) {
         computeVisibleRect(var2, var1);
         var1.x -= var3.x;
         var1.y -= var3.y;
         SwingUtilities.computeIntersection(0, 0, var3.width, var3.height, var1);
      } else {
         var1.setBounds(0, 0, var3.width, var3.height);
      }

   }

   public void computeVisibleRect(Rectangle var1) {
      computeVisibleRect(this, var1);
   }

   public Rectangle getVisibleRect() {
      Rectangle var1 = new Rectangle();
      this.computeVisibleRect(var1);
      return var1;
   }

   public void firePropertyChange(String var1, boolean var2, boolean var3) {
      super.firePropertyChange(var1, var2, var3);
   }

   public void firePropertyChange(String var1, int var2, int var3) {
      super.firePropertyChange(var1, var2, var3);
   }

   public void firePropertyChange(String var1, char var2, char var3) {
      super.firePropertyChange(var1, var2, var3);
   }

   protected void fireVetoableChange(String var1, Object var2, Object var3) throws PropertyVetoException {
      if (this.vetoableChangeSupport != null) {
         this.vetoableChangeSupport.fireVetoableChange(var1, var2, var3);
      }
   }

   public synchronized void addVetoableChangeListener(VetoableChangeListener var1) {
      if (this.vetoableChangeSupport == null) {
         this.vetoableChangeSupport = new VetoableChangeSupport(this);
      }

      this.vetoableChangeSupport.addVetoableChangeListener(var1);
   }

   public synchronized void removeVetoableChangeListener(VetoableChangeListener var1) {
      if (this.vetoableChangeSupport != null) {
         this.vetoableChangeSupport.removeVetoableChangeListener(var1);
      }
   }

   public synchronized VetoableChangeListener[] getVetoableChangeListeners() {
      return this.vetoableChangeSupport == null ? new VetoableChangeListener[0] : this.vetoableChangeSupport.getVetoableChangeListeners();
   }

   public Container getTopLevelAncestor() {
      for(Object var1 = this; var1 != null; var1 = ((Container)var1).getParent()) {
         if (var1 instanceof Window || var1 instanceof Applet) {
            return (Container)var1;
         }
      }

      return null;
   }

   private AncestorNotifier getAncestorNotifier() {
      return (AncestorNotifier)this.getClientProperty(ClientPropertyKey.JComponent_ANCESTOR_NOTIFIER);
   }

   public void addAncestorListener(AncestorListener var1) {
      AncestorNotifier var2 = this.getAncestorNotifier();
      if (var2 == null) {
         var2 = new AncestorNotifier(this);
         this.putClientProperty(ClientPropertyKey.JComponent_ANCESTOR_NOTIFIER, var2);
      }

      var2.addAncestorListener(var1);
   }

   public void removeAncestorListener(AncestorListener var1) {
      AncestorNotifier var2 = this.getAncestorNotifier();
      if (var2 != null) {
         var2.removeAncestorListener(var1);
         if (var2.listenerList.getListenerList().length == 0) {
            var2.removeAllListeners();
            this.putClientProperty(ClientPropertyKey.JComponent_ANCESTOR_NOTIFIER, (Object)null);
         }

      }
   }

   public AncestorListener[] getAncestorListeners() {
      AncestorNotifier var1 = this.getAncestorNotifier();
      return var1 == null ? new AncestorListener[0] : var1.getAncestorListeners();
   }

   public <T extends EventListener> T[] getListeners(Class<T> var1) {
      EventListener[] var2;
      if (var1 == AncestorListener.class) {
         var2 = (EventListener[])this.getAncestorListeners();
      } else if (var1 == VetoableChangeListener.class) {
         var2 = (EventListener[])this.getVetoableChangeListeners();
      } else if (var1 == PropertyChangeListener.class) {
         var2 = (EventListener[])this.getPropertyChangeListeners();
      } else {
         var2 = this.listenerList.getListeners(var1);
      }

      return var2.length == 0 ? super.getListeners(var1) : var2;
   }

   public void addNotify() {
      super.addNotify();
      this.firePropertyChange("ancestor", (Object)null, this.getParent());
      this.registerWithKeyboardManager(false);
      this.registerNextFocusableComponent();
   }

   public void removeNotify() {
      super.removeNotify();
      this.firePropertyChange("ancestor", this.getParent(), (Object)null);
      this.unregisterWithKeyboardManager();
      this.deregisterNextFocusableComponent();
      if (this.getCreatedDoubleBuffer()) {
         RepaintManager.currentManager(this).resetDoubleBuffer();
         this.setCreatedDoubleBuffer(false);
      }

      if (this.autoscrolls) {
         Autoscroller.stop(this);
      }

   }

   public void repaint(long var1, int var3, int var4, int var5, int var6) {
      RepaintManager.currentManager(SunToolkit.targetToAppContext(this)).addDirtyRegion(this, var3, var4, var5, var6);
   }

   public void repaint(Rectangle var1) {
      this.repaint(0L, var1.x, var1.y, var1.width, var1.height);
   }

   public void revalidate() {
      if (this.getParent() != null) {
         if (SunToolkit.isDispatchThreadForAppContext(this)) {
            this.invalidate();
            RepaintManager.currentManager(this).addInvalidComponent(this);
         } else {
            if (this.revalidateRunnableScheduled.getAndSet(true)) {
               return;
            }

            SunToolkit.executeOnEventHandlerThread(this, () -> {
               this.revalidateRunnableScheduled.set(false);
               this.revalidate();
            });
         }

      }
   }

   public boolean isValidateRoot() {
      return false;
   }

   public boolean isOptimizedDrawingEnabled() {
      return true;
   }

   protected boolean isPaintingOrigin() {
      return false;
   }

   public void paintImmediately(int var1, int var2, int var3, int var4) {
      Object var5 = this;
      if (this.isShowing()) {
         JComponent var7 = SwingUtilities.getPaintingOrigin(this);
         if (var7 != null) {
            Rectangle var8 = SwingUtilities.convertRectangle(this, new Rectangle(var1, var2, var3, var4), var7);
            var7.paintImmediately(var8.x, var8.y, var8.width, var8.height);
         } else {
            while(!((Component)var5).isOpaque()) {
               Container var6 = ((Component)var5).getParent();
               if (var6 == null) {
                  break;
               }

               var1 += ((Component)var5).getX();
               var2 += ((Component)var5).getY();
               var5 = var6;
               if (!(var6 instanceof JComponent)) {
                  break;
               }
            }

            if (var5 instanceof JComponent) {
               ((JComponent)var5)._paintImmediately(var1, var2, var3, var4);
            } else {
               ((Component)var5).repaint(var1, var2, var3, var4);
            }

         }
      }
   }

   public void paintImmediately(Rectangle var1) {
      this.paintImmediately(var1.x, var1.y, var1.width, var1.height);
   }

   boolean alwaysOnTop() {
      return false;
   }

   void setPaintingChild(Component var1) {
      this.paintingChild = var1;
   }

   void _paintImmediately(int var1, int var2, int var3, int var4) {
      int var12 = 0;
      int var13 = 0;
      boolean var14 = false;
      JComponent var15 = null;
      JComponent var16 = this;
      RepaintManager var17 = RepaintManager.currentManager(this);
      ArrayList var18 = new ArrayList(7);
      int var19 = -1;
      int var20 = 0;
      boolean var11 = false;
      boolean var10 = false;
      byte var9 = 0;
      byte var8 = 0;
      Rectangle var21 = fetchRectangle();
      var21.x = var1;
      var21.y = var2;
      var21.width = var3;
      var21.height = var4;
      boolean var22 = this.alwaysOnTop() && this.isOpaque();
      if (var22) {
         SwingUtilities.computeIntersection(0, 0, this.getWidth(), this.getHeight(), var21);
         if (var21.width == 0) {
            recycleRectangle(var21);
            return;
         }
      }

      Object var6 = this;

      int var51;
      for(Object var23 = null; var6 != null && !(var6 instanceof Window) && !(var6 instanceof Applet); var6 = ((Container)var6).getParent()) {
         JComponent var24 = var6 instanceof JComponent ? (JComponent)var6 : null;
         var18.add(var6);
         if (!var22 && var24 != null && !var24.isOptimizedDrawingEnabled()) {
            boolean var25;
            if (var6 == this) {
               var25 = false;
            } else if (var24.isPaintingOrigin()) {
               var25 = true;
            } else {
               Component[] var26 = ((Container)var6).getComponents();

               int var27;
               for(var27 = 0; var27 < var26.length && var26[var27] != var23; ++var27) {
               }

               switch(var24.getObscuredState(var27, var21.x, var21.y, var21.width, var21.height)) {
               case 0:
                  var25 = false;
                  break;
               case 2:
                  recycleRectangle(var21);
                  return;
               default:
                  var25 = true;
               }
            }

            if (var25) {
               var16 = var24;
               var19 = var20;
               var13 = 0;
               var12 = 0;
               var14 = false;
            }
         }

         ++var20;
         if (var17.isDoubleBufferingEnabled() && var24 != null && var24.isDoubleBuffered()) {
            var14 = true;
            var15 = var24;
         }

         if (!var22) {
            var51 = ((Container)var6).getX();
            int var50 = ((Container)var6).getY();
            int var46 = ((Container)var6).getWidth();
            int var47 = ((Container)var6).getHeight();
            SwingUtilities.computeIntersection(var8, var9, var46, var47, var21);
            var21.x += var51;
            var21.y += var50;
            var12 += var51;
            var13 += var50;
         }

         var23 = var6;
      }

      if (var6 != null && ((Container)var6).getPeer() != null && var21.width > 0 && var21.height > 0) {
         var16.setFlag(13, true);
         var21.x -= var12;
         var21.y -= var13;
         Component var48;
         if (var16 != this) {
            for(var51 = var19; var51 > 0; --var51) {
               var48 = (Component)var18.get(var51);
               if (var48 instanceof JComponent) {
                  ((JComponent)var48).setPaintingChild((Component)var18.get(var51 - 1));
               }
            }
         }

         boolean var36 = false;

         try {
            var36 = true;
            Graphics var5;
            if ((var5 = safelyGetGraphics(var16, (Component)var6)) != null) {
               try {
                  if (var14) {
                     RepaintManager var49 = RepaintManager.currentManager(var15);
                     var49.beginPaint();

                     try {
                        var49.paint(var16, var15, var5, var21.x, var21.y, var21.width, var21.height);
                     } finally {
                        var49.endPaint();
                     }
                  } else {
                     var5.setClip(var21.x, var21.y, var21.width, var21.height);
                     var16.paint(var5);
                  }
               } finally {
                  var5.dispose();
               }

               var36 = false;
            } else {
               var36 = false;
            }
         } finally {
            if (var36) {
               if (var16 != this) {
                  for(int var32 = var19; var32 > 0; --var32) {
                     Component var31 = (Component)var18.get(var32);
                     if (var31 instanceof JComponent) {
                        ((JComponent)var31).setPaintingChild((Component)null);
                     }
                  }
               }

               var16.setFlag(13, false);
            }
         }

         if (var16 != this) {
            for(var51 = var19; var51 > 0; --var51) {
               var48 = (Component)var18.get(var51);
               if (var48 instanceof JComponent) {
                  ((JComponent)var48).setPaintingChild((Component)null);
               }
            }
         }

         var16.setFlag(13, false);
         recycleRectangle(var21);
      } else {
         recycleRectangle(var21);
      }
   }

   void paintToOffscreen(Graphics var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      try {
         this.setFlag(1, true);
         if (var3 + var5 < var7 || var2 + var4 < var6) {
            this.setFlag(2, true);
         }

         if (this.getFlag(13)) {
            this.paint(var1);
         } else {
            if (!this.rectangleIsObscured(var2, var3, var4, var5)) {
               this.paintComponent(var1);
               this.paintBorder(var1);
            }

            this.paintChildren(var1);
         }
      } finally {
         this.setFlag(1, false);
         this.setFlag(2, false);
      }

   }

   private int getObscuredState(int var1, int var2, int var3, int var4, int var5) {
      byte var6 = 0;
      Rectangle var7 = fetchRectangle();

      for(int var8 = var1 - 1; var8 >= 0; --var8) {
         Component var9 = this.getComponent(var8);
         if (var9.isVisible()) {
            boolean var11;
            if (var9 instanceof JComponent) {
               var11 = var9.isOpaque();
               if (!var11 && var6 == 1) {
                  continue;
               }
            } else {
               var11 = true;
            }

            Rectangle var10 = var9.getBounds(var7);
            if (var11 && var2 >= var10.x && var2 + var4 <= var10.x + var10.width && var3 >= var10.y && var3 + var5 <= var10.y + var10.height) {
               recycleRectangle(var7);
               return 2;
            }

            if (var6 == 0 && var2 + var4 > var10.x && var3 + var5 > var10.y && var2 < var10.x + var10.width && var3 < var10.y + var10.height) {
               var6 = 1;
            }
         }
      }

      recycleRectangle(var7);
      return var6;
   }

   boolean checkIfChildObscuredBySibling() {
      return true;
   }

   private void setFlag(int var1, boolean var2) {
      if (var2) {
         this.flags |= 1 << var1;
      } else {
         this.flags &= ~(1 << var1);
      }

   }

   private boolean getFlag(int var1) {
      int var2 = 1 << var1;
      return (this.flags & var2) == var2;
   }

   static void setWriteObjCounter(JComponent var0, byte var1) {
      var0.flags = var0.flags & -4177921 | var1 << 14;
   }

   static byte getWriteObjCounter(JComponent var0) {
      return (byte)(var0.flags >> 14 & 255);
   }

   public void setDoubleBuffered(boolean var1) {
      this.setFlag(0, var1);
   }

   public boolean isDoubleBuffered() {
      return this.getFlag(0);
   }

   public JRootPane getRootPane() {
      return SwingUtilities.getRootPane(this);
   }

   void compWriteObjectNotify() {
      byte var1 = getWriteObjCounter(this);
      setWriteObjCounter(this, (byte)(var1 + 1));
      if (var1 == 0) {
         this.uninstallUIAndProperties();
         if (this.getToolTipText() != null || this instanceof JTableHeader) {
            ToolTipManager.sharedInstance().unregisterComponent(this);
         }

      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      JComponent.ReadObjectCallback var2 = (JComponent.ReadObjectCallback)readObjectCallbacks.get(var1);
      if (var2 == null) {
         try {
            readObjectCallbacks.put(var1, var2 = new JComponent.ReadObjectCallback(var1));
         } catch (Exception var5) {
            throw new IOException(var5.toString());
         }
      }

      var2.registerComponent(this);
      int var3 = var1.readInt();
      if (var3 > 0) {
         this.clientProperties = new ArrayTable();

         for(int var4 = 0; var4 < var3; ++var4) {
            this.clientProperties.put(var1.readObject(), var1.readObject());
         }
      }

      if (this.getToolTipText() != null) {
         ToolTipManager.sharedInstance().registerComponent(this);
      }

      setWriteObjCounter(this, (byte)0);
      this.revalidateRunnableScheduled = new AtomicBoolean(false);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("ComponentUI")) {
         byte var2 = getWriteObjCounter(this);
         --var2;
         setWriteObjCounter(this, var2);
         if (var2 == 0 && this.ui != null) {
            this.ui.installUI(this);
         }
      }

      ArrayTable.writeArrayTable(var1, this.clientProperties);
   }

   protected String paramString() {
      String var1 = this.isPreferredSizeSet() ? this.getPreferredSize().toString() : "";
      String var2 = this.isMinimumSizeSet() ? this.getMinimumSize().toString() : "";
      String var3 = this.isMaximumSizeSet() ? this.getMaximumSize().toString() : "";
      String var4 = this.border == null ? "" : (this.border == this ? "this" : this.border.toString());
      return super.paramString() + ",alignmentX=" + this.alignmentX + ",alignmentY=" + this.alignmentY + ",border=" + var4 + ",flags=" + this.flags + ",maximumSize=" + var3 + ",minimumSize=" + var2 + ",preferredSize=" + var1;
   }

   /** @deprecated */
   @Deprecated
   public void hide() {
      boolean var1 = this.isShowing();
      super.hide();
      if (var1) {
         Container var2 = this.getParent();
         if (var2 != null) {
            Rectangle var3 = this.getBounds();
            var2.repaint(var3.x, var3.y, var3.width, var3.height);
         }

         this.revalidate();
      }

   }

   private class ReadObjectCallback implements ObjectInputValidation {
      private final Vector<JComponent> roots = new Vector(1);
      private final ObjectInputStream inputStream;

      ReadObjectCallback(ObjectInputStream var2) throws Exception {
         this.inputStream = var2;
         var2.registerValidation(this, 0);
      }

      public void validateObject() throws InvalidObjectException {
         try {
            Iterator var1 = this.roots.iterator();

            while(var1.hasNext()) {
               JComponent var2 = (JComponent)var1.next();
               SwingUtilities.updateComponentTreeUI(var2);
            }
         } finally {
            JComponent.readObjectCallbacks.remove(this.inputStream);
         }

      }

      private void registerComponent(JComponent var1) {
         Iterator var2 = this.roots.iterator();

         JComponent var3;
         while(var2.hasNext()) {
            var3 = (JComponent)var2.next();

            for(Object var4 = var1; var4 != null; var4 = ((Component)var4).getParent()) {
               if (var4 == var3) {
                  return;
               }
            }
         }

         for(int var5 = 0; var5 < this.roots.size(); ++var5) {
            var3 = (JComponent)this.roots.elementAt(var5);

            for(Container var6 = var3.getParent(); var6 != null; var6 = var6.getParent()) {
               if (var6 == var1) {
                  this.roots.removeElementAt(var5--);
                  break;
               }
            }
         }

         this.roots.addElement(var1);
      }
   }

   public abstract class AccessibleJComponent extends Container.AccessibleAWTContainer implements AccessibleExtendedComponent {
      private transient volatile int propertyListenersCount = 0;
      /** @deprecated */
      @Deprecated
      protected FocusListener accessibleFocusHandler = null;

      protected AccessibleJComponent() {
         super();
      }

      public void addPropertyChangeListener(PropertyChangeListener var1) {
         super.addPropertyChangeListener(var1);
      }

      public void removePropertyChangeListener(PropertyChangeListener var1) {
         super.removePropertyChangeListener(var1);
      }

      protected String getBorderTitle(Border var1) {
         if (var1 instanceof TitledBorder) {
            return ((TitledBorder)var1).getTitle();
         } else if (var1 instanceof CompoundBorder) {
            String var2 = this.getBorderTitle(((CompoundBorder)var1).getInsideBorder());
            if (var2 == null) {
               var2 = this.getBorderTitle(((CompoundBorder)var1).getOutsideBorder());
            }

            return var2;
         } else {
            return null;
         }
      }

      public String getAccessibleName() {
         String var1 = this.accessibleName;
         if (var1 == null) {
            var1 = (String)JComponent.this.getClientProperty("AccessibleName");
         }

         if (var1 == null) {
            var1 = this.getBorderTitle(JComponent.this.getBorder());
         }

         if (var1 == null) {
            Object var2 = JComponent.this.getClientProperty("labeledBy");
            if (var2 instanceof Accessible) {
               AccessibleContext var3 = ((Accessible)var2).getAccessibleContext();
               if (var3 != null) {
                  var1 = var3.getAccessibleName();
               }
            }
         }

         return var1;
      }

      public String getAccessibleDescription() {
         String var1 = this.accessibleDescription;
         if (var1 == null) {
            var1 = (String)JComponent.this.getClientProperty("AccessibleDescription");
         }

         if (var1 == null) {
            try {
               var1 = this.getToolTipText();
            } catch (Exception var4) {
            }
         }

         if (var1 == null) {
            Object var2 = JComponent.this.getClientProperty("labeledBy");
            if (var2 instanceof Accessible) {
               AccessibleContext var3 = ((Accessible)var2).getAccessibleContext();
               if (var3 != null) {
                  var1 = var3.getAccessibleDescription();
               }
            }
         }

         return var1;
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.SWING_COMPONENT;
      }

      public AccessibleStateSet getAccessibleStateSet() {
         AccessibleStateSet var1 = super.getAccessibleStateSet();
         if (JComponent.this.isOpaque()) {
            var1.add(AccessibleState.OPAQUE);
         }

         return var1;
      }

      public int getAccessibleChildrenCount() {
         return super.getAccessibleChildrenCount();
      }

      public Accessible getAccessibleChild(int var1) {
         return super.getAccessibleChild(var1);
      }

      AccessibleExtendedComponent getAccessibleExtendedComponent() {
         return this;
      }

      public String getToolTipText() {
         return JComponent.this.getToolTipText();
      }

      public String getTitledBorderText() {
         Border var1 = JComponent.this.getBorder();
         return var1 instanceof TitledBorder ? ((TitledBorder)var1).getTitle() : null;
      }

      public AccessibleKeyBinding getAccessibleKeyBinding() {
         Object var1 = JComponent.this.getClientProperty("labeledBy");
         if (var1 instanceof Accessible) {
            AccessibleContext var2 = ((Accessible)var1).getAccessibleContext();
            if (var2 != null) {
               AccessibleComponent var3 = var2.getAccessibleComponent();
               if (!(var3 instanceof AccessibleExtendedComponent)) {
                  return null;
               }

               return ((AccessibleExtendedComponent)var3).getAccessibleKeyBinding();
            }
         }

         return null;
      }

      protected class AccessibleFocusHandler implements FocusListener {
         public void focusGained(FocusEvent var1) {
            if (JComponent.this.accessibleContext != null) {
               JComponent.this.accessibleContext.firePropertyChange("AccessibleState", (Object)null, AccessibleState.FOCUSED);
            }

         }

         public void focusLost(FocusEvent var1) {
            if (JComponent.this.accessibleContext != null) {
               JComponent.this.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.FOCUSED, (Object)null);
            }

         }
      }

      protected class AccessibleContainerHandler implements ContainerListener {
         public void componentAdded(ContainerEvent var1) {
            Component var2 = var1.getChild();
            if (var2 != null && var2 instanceof Accessible) {
               AccessibleJComponent.this.firePropertyChange("AccessibleChild", (Object)null, var2.getAccessibleContext());
            }

         }

         public void componentRemoved(ContainerEvent var1) {
            Component var2 = var1.getChild();
            if (var2 != null && var2 instanceof Accessible) {
               AccessibleJComponent.this.firePropertyChange("AccessibleChild", var2.getAccessibleContext(), (Object)null);
            }

         }
      }
   }

   static class KeyboardState implements Serializable {
      private static final Object keyCodesKey = JComponent.KeyboardState.class;

      static JComponent.IntVector getKeyCodeArray() {
         JComponent.IntVector var0 = (JComponent.IntVector)SwingUtilities.appContextGet(keyCodesKey);
         if (var0 == null) {
            var0 = new JComponent.IntVector();
            SwingUtilities.appContextPut(keyCodesKey, var0);
         }

         return var0;
      }

      static void registerKeyPressed(int var0) {
         JComponent.IntVector var1 = getKeyCodeArray();
         int var2 = var1.size();

         for(int var3 = 0; var3 < var2; ++var3) {
            if (var1.elementAt(var3) == -1) {
               var1.setElementAt(var0, var3);
               return;
            }
         }

         var1.addElement(var0);
      }

      static void registerKeyReleased(int var0) {
         JComponent.IntVector var1 = getKeyCodeArray();
         int var2 = var1.size();

         for(int var3 = 0; var3 < var2; ++var3) {
            if (var1.elementAt(var3) == var0) {
               var1.setElementAt(-1, var3);
               return;
            }
         }

      }

      static boolean keyIsPressed(int var0) {
         JComponent.IntVector var1 = getKeyCodeArray();
         int var2 = var1.size();

         for(int var3 = 0; var3 < var2; ++var3) {
            if (var1.elementAt(var3) == var0) {
               return true;
            }
         }

         return false;
      }

      static boolean shouldProcess(KeyEvent var0) {
         switch(var0.getID()) {
         case 400:
            return true;
         case 401:
            if (!keyIsPressed(var0.getKeyCode())) {
               registerKeyPressed(var0.getKeyCode());
            }

            return true;
         case 402:
            if (!keyIsPressed(var0.getKeyCode()) && var0.getKeyCode() != 154) {
               return false;
            }

            registerKeyReleased(var0.getKeyCode());
            return true;
         default:
            return false;
         }
      }
   }

   static final class IntVector {
      int[] array = null;
      int count = 0;
      int capacity = 0;

      int size() {
         return this.count;
      }

      int elementAt(int var1) {
         return this.array[var1];
      }

      void addElement(int var1) {
         if (this.count == this.capacity) {
            this.capacity = (this.capacity + 2) * 2;
            int[] var2 = new int[this.capacity];
            if (this.count > 0) {
               System.arraycopy(this.array, 0, var2, 0, this.count);
            }

            this.array = var2;
         }

         this.array[this.count++] = var1;
      }

      void setElementAt(int var1, int var2) {
         this.array[var2] = var1;
      }
   }

   final class ActionStandin implements Action {
      private final ActionListener actionListener;
      private final String command;
      private final Action action;

      ActionStandin(ActionListener var2, String var3) {
         this.actionListener = var2;
         if (var2 instanceof Action) {
            this.action = (Action)var2;
         } else {
            this.action = null;
         }

         this.command = var3;
      }

      public Object getValue(String var1) {
         if (var1 != null) {
            if (var1.equals("ActionCommandKey")) {
               return this.command;
            }

            if (this.action != null) {
               return this.action.getValue(var1);
            }

            if (var1.equals("Name")) {
               return "ActionStandin";
            }
         }

         return null;
      }

      public boolean isEnabled() {
         if (this.actionListener == null) {
            return false;
         } else {
            return this.action == null ? true : this.action.isEnabled();
         }
      }

      public void actionPerformed(ActionEvent var1) {
         if (this.actionListener != null) {
            this.actionListener.actionPerformed(var1);
         }

      }

      public void putValue(String var1, Object var2) {
      }

      public void setEnabled(boolean var1) {
      }

      public void addPropertyChangeListener(PropertyChangeListener var1) {
      }

      public void removePropertyChangeListener(PropertyChangeListener var1) {
      }
   }
}
