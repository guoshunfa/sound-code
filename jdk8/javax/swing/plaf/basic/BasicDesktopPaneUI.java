package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultDesktopManager;
import javax.swing.DesktopManager;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SortingFocusTraversalPolicy;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.DesktopPaneUI;
import javax.swing.plaf.UIResource;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicDesktopPaneUI extends DesktopPaneUI {
   private static final BasicDesktopPaneUI.Actions SHARED_ACTION = new BasicDesktopPaneUI.Actions();
   private BasicDesktopPaneUI.Handler handler;
   private PropertyChangeListener pcl;
   protected JDesktopPane desktop;
   protected DesktopManager desktopManager;
   /** @deprecated */
   @Deprecated
   protected KeyStroke minimizeKey;
   /** @deprecated */
   @Deprecated
   protected KeyStroke maximizeKey;
   /** @deprecated */
   @Deprecated
   protected KeyStroke closeKey;
   /** @deprecated */
   @Deprecated
   protected KeyStroke navigateKey;
   /** @deprecated */
   @Deprecated
   protected KeyStroke navigateKey2;

   public static ComponentUI createUI(JComponent var0) {
      return new BasicDesktopPaneUI();
   }

   public void installUI(JComponent var1) {
      this.desktop = (JDesktopPane)var1;
      this.installDefaults();
      this.installDesktopManager();
      this.installListeners();
      this.installKeyboardActions();
   }

   public void uninstallUI(JComponent var1) {
      this.uninstallKeyboardActions();
      this.uninstallListeners();
      this.uninstallDesktopManager();
      this.uninstallDefaults();
      this.desktop = null;
      this.handler = null;
   }

   protected void installDefaults() {
      if (this.desktop.getBackground() == null || this.desktop.getBackground() instanceof UIResource) {
         this.desktop.setBackground(UIManager.getColor("Desktop.background"));
      }

      LookAndFeel.installProperty(this.desktop, "opaque", Boolean.TRUE);
   }

   protected void uninstallDefaults() {
   }

   protected void installListeners() {
      this.pcl = this.createPropertyChangeListener();
      this.desktop.addPropertyChangeListener(this.pcl);
   }

   protected void uninstallListeners() {
      this.desktop.removePropertyChangeListener(this.pcl);
      this.pcl = null;
   }

   protected void installDesktopManager() {
      this.desktopManager = this.desktop.getDesktopManager();
      if (this.desktopManager == null) {
         this.desktopManager = new BasicDesktopPaneUI.BasicDesktopManager();
         this.desktop.setDesktopManager(this.desktopManager);
      }

   }

   protected void uninstallDesktopManager() {
      if (this.desktop.getDesktopManager() instanceof UIResource) {
         this.desktop.setDesktopManager((DesktopManager)null);
      }

      this.desktopManager = null;
   }

   protected void installKeyboardActions() {
      InputMap var1 = this.getInputMap(2);
      if (var1 != null) {
         SwingUtilities.replaceUIInputMap(this.desktop, 2, var1);
      }

      var1 = this.getInputMap(1);
      if (var1 != null) {
         SwingUtilities.replaceUIInputMap(this.desktop, 1, var1);
      }

      LazyActionMap.installLazyActionMap(this.desktop, BasicDesktopPaneUI.class, "DesktopPane.actionMap");
      this.registerKeyboardActions();
   }

   protected void registerKeyboardActions() {
   }

   protected void unregisterKeyboardActions() {
   }

   InputMap getInputMap(int var1) {
      if (var1 == 2) {
         return this.createInputMap(var1);
      } else {
         return var1 == 1 ? (InputMap)DefaultLookup.get(this.desktop, this, "Desktop.ancestorInputMap") : null;
      }
   }

   InputMap createInputMap(int var1) {
      if (var1 == 2) {
         Object[] var2 = (Object[])((Object[])DefaultLookup.get(this.desktop, this, "Desktop.windowBindings"));
         if (var2 != null) {
            return LookAndFeel.makeComponentInputMap(this.desktop, var2);
         }
      }

      return null;
   }

   static void loadActionMap(LazyActionMap var0) {
      var0.put(new BasicDesktopPaneUI.Actions(BasicDesktopPaneUI.Actions.RESTORE));
      var0.put(new BasicDesktopPaneUI.Actions(BasicDesktopPaneUI.Actions.CLOSE));
      var0.put(new BasicDesktopPaneUI.Actions(BasicDesktopPaneUI.Actions.MOVE));
      var0.put(new BasicDesktopPaneUI.Actions(BasicDesktopPaneUI.Actions.RESIZE));
      var0.put(new BasicDesktopPaneUI.Actions(BasicDesktopPaneUI.Actions.LEFT));
      var0.put(new BasicDesktopPaneUI.Actions(BasicDesktopPaneUI.Actions.SHRINK_LEFT));
      var0.put(new BasicDesktopPaneUI.Actions(BasicDesktopPaneUI.Actions.RIGHT));
      var0.put(new BasicDesktopPaneUI.Actions(BasicDesktopPaneUI.Actions.SHRINK_RIGHT));
      var0.put(new BasicDesktopPaneUI.Actions(BasicDesktopPaneUI.Actions.UP));
      var0.put(new BasicDesktopPaneUI.Actions(BasicDesktopPaneUI.Actions.SHRINK_UP));
      var0.put(new BasicDesktopPaneUI.Actions(BasicDesktopPaneUI.Actions.DOWN));
      var0.put(new BasicDesktopPaneUI.Actions(BasicDesktopPaneUI.Actions.SHRINK_DOWN));
      var0.put(new BasicDesktopPaneUI.Actions(BasicDesktopPaneUI.Actions.ESCAPE));
      var0.put(new BasicDesktopPaneUI.Actions(BasicDesktopPaneUI.Actions.MINIMIZE));
      var0.put(new BasicDesktopPaneUI.Actions(BasicDesktopPaneUI.Actions.MAXIMIZE));
      var0.put(new BasicDesktopPaneUI.Actions(BasicDesktopPaneUI.Actions.NEXT_FRAME));
      var0.put(new BasicDesktopPaneUI.Actions(BasicDesktopPaneUI.Actions.PREVIOUS_FRAME));
      var0.put(new BasicDesktopPaneUI.Actions(BasicDesktopPaneUI.Actions.NAVIGATE_NEXT));
      var0.put(new BasicDesktopPaneUI.Actions(BasicDesktopPaneUI.Actions.NAVIGATE_PREVIOUS));
   }

   protected void uninstallKeyboardActions() {
      this.unregisterKeyboardActions();
      SwingUtilities.replaceUIInputMap(this.desktop, 2, (InputMap)null);
      SwingUtilities.replaceUIInputMap(this.desktop, 1, (InputMap)null);
      SwingUtilities.replaceUIActionMap(this.desktop, (ActionMap)null);
   }

   public void paint(Graphics var1, JComponent var2) {
   }

   public Dimension getPreferredSize(JComponent var1) {
      return null;
   }

   public Dimension getMinimumSize(JComponent var1) {
      return new Dimension(0, 0);
   }

   public Dimension getMaximumSize(JComponent var1) {
      return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
   }

   protected PropertyChangeListener createPropertyChangeListener() {
      return this.getHandler();
   }

   private BasicDesktopPaneUI.Handler getHandler() {
      if (this.handler == null) {
         this.handler = new BasicDesktopPaneUI.Handler();
      }

      return this.handler;
   }

   protected class NavigateAction extends AbstractAction {
      public void actionPerformed(ActionEvent var1) {
         JDesktopPane var2 = (JDesktopPane)var1.getSource();
         var2.selectFrame(true);
      }

      public boolean isEnabled() {
         return true;
      }
   }

   protected class MaximizeAction extends AbstractAction {
      public void actionPerformed(ActionEvent var1) {
         JDesktopPane var2 = (JDesktopPane)var1.getSource();
         BasicDesktopPaneUI.SHARED_ACTION.setState(var2, BasicDesktopPaneUI.Actions.MAXIMIZE);
      }

      public boolean isEnabled() {
         JInternalFrame var1 = BasicDesktopPaneUI.this.desktop.getSelectedFrame();
         return var1 != null ? var1.isMaximizable() : false;
      }
   }

   protected class MinimizeAction extends AbstractAction {
      public void actionPerformed(ActionEvent var1) {
         JDesktopPane var2 = (JDesktopPane)var1.getSource();
         BasicDesktopPaneUI.SHARED_ACTION.setState(var2, BasicDesktopPaneUI.Actions.MINIMIZE);
      }

      public boolean isEnabled() {
         JInternalFrame var1 = BasicDesktopPaneUI.this.desktop.getSelectedFrame();
         return var1 != null ? var1.isIconifiable() : false;
      }
   }

   protected class CloseAction extends AbstractAction {
      public void actionPerformed(ActionEvent var1) {
         JDesktopPane var2 = (JDesktopPane)var1.getSource();
         BasicDesktopPaneUI.SHARED_ACTION.setState(var2, BasicDesktopPaneUI.Actions.CLOSE);
      }

      public boolean isEnabled() {
         JInternalFrame var1 = BasicDesktopPaneUI.this.desktop.getSelectedFrame();
         return var1 != null ? var1.isClosable() : false;
      }
   }

   protected class OpenAction extends AbstractAction {
      public void actionPerformed(ActionEvent var1) {
         JDesktopPane var2 = (JDesktopPane)var1.getSource();
         BasicDesktopPaneUI.SHARED_ACTION.setState(var2, BasicDesktopPaneUI.Actions.RESTORE);
      }

      public boolean isEnabled() {
         return true;
      }
   }

   private static class Actions extends UIAction {
      private static String CLOSE = "close";
      private static String ESCAPE = "escape";
      private static String MAXIMIZE = "maximize";
      private static String MINIMIZE = "minimize";
      private static String MOVE = "move";
      private static String RESIZE = "resize";
      private static String RESTORE = "restore";
      private static String LEFT = "left";
      private static String RIGHT = "right";
      private static String UP = "up";
      private static String DOWN = "down";
      private static String SHRINK_LEFT = "shrinkLeft";
      private static String SHRINK_RIGHT = "shrinkRight";
      private static String SHRINK_UP = "shrinkUp";
      private static String SHRINK_DOWN = "shrinkDown";
      private static String NEXT_FRAME = "selectNextFrame";
      private static String PREVIOUS_FRAME = "selectPreviousFrame";
      private static String NAVIGATE_NEXT = "navigateNext";
      private static String NAVIGATE_PREVIOUS = "navigatePrevious";
      private final int MOVE_RESIZE_INCREMENT = 10;
      private static boolean moving = false;
      private static boolean resizing = false;
      private static JInternalFrame sourceFrame = null;
      private static Component focusOwner = null;

      Actions() {
         super((String)null);
      }

      Actions(String var1) {
         super(var1);
      }

      public void actionPerformed(ActionEvent var1) {
         JDesktopPane var2 = (JDesktopPane)var1.getSource();
         String var3 = this.getName();
         if (CLOSE != var3 && MAXIMIZE != var3 && MINIMIZE != var3 && RESTORE != var3) {
            if (ESCAPE == var3) {
               if (sourceFrame == var2.getSelectedFrame() && focusOwner != null) {
                  focusOwner.requestFocus();
               }

               moving = false;
               resizing = false;
               sourceFrame = null;
               focusOwner = null;
            } else if (MOVE != var3 && RESIZE != var3) {
               if (LEFT != var3 && RIGHT != var3 && UP != var3 && DOWN != var3 && SHRINK_RIGHT != var3 && SHRINK_LEFT != var3 && SHRINK_UP != var3 && SHRINK_DOWN != var3) {
                  if (NEXT_FRAME != var3 && PREVIOUS_FRAME != var3) {
                     if (NAVIGATE_NEXT == var3 || NAVIGATE_PREVIOUS == var3) {
                        boolean var15 = true;
                        if (NAVIGATE_PREVIOUS == var3) {
                           var15 = false;
                        }

                        Container var16 = var2.getFocusCycleRootAncestor();
                        if (var16 != null) {
                           FocusTraversalPolicy var17 = var16.getFocusTraversalPolicy();
                           if (var17 != null && var17 instanceof SortingFocusTraversalPolicy) {
                              SortingFocusTraversalPolicy var18 = (SortingFocusTraversalPolicy)var17;
                              boolean var19 = var18.getImplicitDownCycleTraversal();

                              try {
                                 var18.setImplicitDownCycleTraversal(false);
                                 if (var15) {
                                    KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent(var2);
                                 } else {
                                    KeyboardFocusManager.getCurrentKeyboardFocusManager().focusPreviousComponent(var2);
                                 }
                              } finally {
                                 var18.setImplicitDownCycleTraversal(var19);
                              }
                           }
                        }
                     }
                  } else {
                     var2.selectFrame(var3 == NEXT_FRAME);
                  }
               } else {
                  JInternalFrame var4 = var2.getSelectedFrame();
                  if (sourceFrame == null || var4 != sourceFrame || KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() != sourceFrame) {
                     return;
                  }

                  Insets var5 = UIManager.getInsets("Desktop.minOnScreenInsets");
                  Dimension var6 = var4.getSize();
                  Dimension var7 = var4.getMinimumSize();
                  int var8 = var2.getWidth();
                  int var9 = var2.getHeight();
                  Point var11 = var4.getLocation();
                  if (LEFT == var3) {
                     if (moving) {
                        var4.setLocation(var11.x + var6.width - 10 < var5.right ? -var6.width + var5.right : var11.x - 10, var11.y);
                     } else if (resizing) {
                        var4.setLocation(var11.x - 10, var11.y);
                        var4.setSize(var6.width + 10, var6.height);
                     }
                  } else if (RIGHT == var3) {
                     if (moving) {
                        var4.setLocation(var11.x + 10 > var8 - var5.left ? var8 - var5.left : var11.x + 10, var11.y);
                     } else if (resizing) {
                        var4.setSize(var6.width + 10, var6.height);
                     }
                  } else if (UP == var3) {
                     if (moving) {
                        var4.setLocation(var11.x, var11.y + var6.height - 10 < var5.bottom ? -var6.height + var5.bottom : var11.y - 10);
                     } else if (resizing) {
                        var4.setLocation(var11.x, var11.y - 10);
                        var4.setSize(var6.width, var6.height + 10);
                     }
                  } else if (DOWN == var3) {
                     if (moving) {
                        var4.setLocation(var11.x, var11.y + 10 > var9 - var5.top ? var9 - var5.top : var11.y + 10);
                     } else if (resizing) {
                        var4.setSize(var6.width, var6.height + 10);
                     }
                  } else {
                     int var10;
                     if (SHRINK_LEFT == var3 && resizing) {
                        if (var7.width < var6.width - 10) {
                           var10 = 10;
                        } else {
                           var10 = var6.width - var7.width;
                        }

                        if (var11.x + var6.width - var10 < var5.left) {
                           var10 = var11.x + var6.width - var5.left;
                        }

                        var4.setSize(var6.width - var10, var6.height);
                     } else if (SHRINK_RIGHT == var3 && resizing) {
                        if (var7.width < var6.width - 10) {
                           var10 = 10;
                        } else {
                           var10 = var6.width - var7.width;
                        }

                        if (var11.x + var10 > var8 - var5.right) {
                           var10 = var8 - var5.right - var11.x;
                        }

                        var4.setLocation(var11.x + var10, var11.y);
                        var4.setSize(var6.width - var10, var6.height);
                     } else if (SHRINK_UP == var3 && resizing) {
                        if (var7.height < var6.height - 10) {
                           var10 = 10;
                        } else {
                           var10 = var6.height - var7.height;
                        }

                        if (var11.y + var6.height - var10 < var5.bottom) {
                           var10 = var11.y + var6.height - var5.bottom;
                        }

                        var4.setSize(var6.width, var6.height - var10);
                     } else if (SHRINK_DOWN == var3 && resizing) {
                        if (var7.height < var6.height - 10) {
                           var10 = 10;
                        } else {
                           var10 = var6.height - var7.height;
                        }

                        if (var11.y + var10 > var9 - var5.top) {
                           var10 = var9 - var5.top - var11.y;
                        }

                        var4.setLocation(var11.x, var11.y + var10);
                        var4.setSize(var6.width, var6.height - var10);
                     }
                  }
               }
            } else {
               sourceFrame = var2.getSelectedFrame();
               if (sourceFrame == null) {
                  return;
               }

               moving = var3 == MOVE;
               resizing = var3 == RESIZE;
               focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
               if (!SwingUtilities.isDescendingFrom(focusOwner, sourceFrame)) {
                  focusOwner = null;
               }

               sourceFrame.requestFocus();
            }
         } else {
            this.setState(var2, var3);
         }

      }

      private void setState(JDesktopPane var1, String var2) {
         JInternalFrame var3;
         if (var2 == CLOSE) {
            var3 = var1.getSelectedFrame();
            if (var3 == null) {
               return;
            }

            var3.doDefaultCloseAction();
         } else if (var2 == MAXIMIZE) {
            var3 = var1.getSelectedFrame();
            if (var3 == null) {
               return;
            }

            if (!var3.isMaximum()) {
               if (var3.isIcon()) {
                  try {
                     var3.setIcon(false);
                     var3.setMaximum(true);
                  } catch (PropertyVetoException var8) {
                  }
               } else {
                  try {
                     var3.setMaximum(true);
                  } catch (PropertyVetoException var7) {
                  }
               }
            }
         } else if (var2 == MINIMIZE) {
            var3 = var1.getSelectedFrame();
            if (var3 == null) {
               return;
            }

            if (!var3.isIcon()) {
               try {
                  var3.setIcon(true);
               } catch (PropertyVetoException var6) {
               }
            }
         } else if (var2 == RESTORE) {
            var3 = var1.getSelectedFrame();
            if (var3 == null) {
               return;
            }

            try {
               if (var3.isIcon()) {
                  var3.setIcon(false);
               } else if (var3.isMaximum()) {
                  var3.setMaximum(false);
               }

               var3.setSelected(true);
            } catch (PropertyVetoException var5) {
            }
         }

      }

      public boolean isEnabled(Object var1) {
         if (var1 instanceof JDesktopPane) {
            JDesktopPane var2 = (JDesktopPane)var1;
            String var3 = this.getName();
            if (var3 != NEXT_FRAME && var3 != PREVIOUS_FRAME) {
               JInternalFrame var4 = var2.getSelectedFrame();
               if (var4 == null) {
                  return false;
               } else if (var3 == CLOSE) {
                  return var4.isClosable();
               } else if (var3 == MINIMIZE) {
                  return var4.isIconifiable();
               } else {
                  return var3 == MAXIMIZE ? var4.isMaximizable() : true;
               }
            } else {
               return true;
            }
         } else {
            return false;
         }
      }
   }

   private class BasicDesktopManager extends DefaultDesktopManager implements UIResource {
      private BasicDesktopManager() {
      }

      // $FF: synthetic method
      BasicDesktopManager(Object var2) {
         this();
      }
   }

   private class Handler implements PropertyChangeListener {
      private Handler() {
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if ("desktopManager" == var2) {
            BasicDesktopPaneUI.this.installDesktopManager();
         }

      }

      // $FF: synthetic method
      Handler(Object var2) {
         this();
      }
   }
}
