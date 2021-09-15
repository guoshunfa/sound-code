package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ActionMap;
import javax.swing.BoundedRangeModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.LookAndFeel;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ScrollPaneUI;
import javax.swing.plaf.UIResource;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicScrollPaneUI extends ScrollPaneUI implements ScrollPaneConstants {
   protected JScrollPane scrollpane;
   protected ChangeListener vsbChangeListener;
   protected ChangeListener hsbChangeListener;
   protected ChangeListener viewportChangeListener;
   protected PropertyChangeListener spPropertyChangeListener;
   private MouseWheelListener mouseScrollListener;
   private int oldExtent = Integer.MIN_VALUE;
   private PropertyChangeListener vsbPropertyChangeListener;
   private PropertyChangeListener hsbPropertyChangeListener;
   private BasicScrollPaneUI.Handler handler;
   private boolean setValueCalled = false;

   public static ComponentUI createUI(JComponent var0) {
      return new BasicScrollPaneUI();
   }

   static void loadActionMap(LazyActionMap var0) {
      var0.put(new BasicScrollPaneUI.Actions("scrollUp"));
      var0.put(new BasicScrollPaneUI.Actions("scrollDown"));
      var0.put(new BasicScrollPaneUI.Actions("scrollHome"));
      var0.put(new BasicScrollPaneUI.Actions("scrollEnd"));
      var0.put(new BasicScrollPaneUI.Actions("unitScrollUp"));
      var0.put(new BasicScrollPaneUI.Actions("unitScrollDown"));
      var0.put(new BasicScrollPaneUI.Actions("scrollLeft"));
      var0.put(new BasicScrollPaneUI.Actions("scrollRight"));
      var0.put(new BasicScrollPaneUI.Actions("unitScrollRight"));
      var0.put(new BasicScrollPaneUI.Actions("unitScrollLeft"));
   }

   public void paint(Graphics var1, JComponent var2) {
      Border var3 = this.scrollpane.getViewportBorder();
      if (var3 != null) {
         Rectangle var4 = this.scrollpane.getViewportBorderBounds();
         var3.paintBorder(this.scrollpane, var1, var4.x, var4.y, var4.width, var4.height);
      }

   }

   public Dimension getMaximumSize(JComponent var1) {
      return new Dimension(32767, 32767);
   }

   protected void installDefaults(JScrollPane var1) {
      LookAndFeel.installBorder(var1, "ScrollPane.border");
      LookAndFeel.installColorsAndFont(var1, "ScrollPane.background", "ScrollPane.foreground", "ScrollPane.font");
      Border var2 = var1.getViewportBorder();
      if (var2 == null || var2 instanceof UIResource) {
         var2 = UIManager.getBorder("ScrollPane.viewportBorder");
         var1.setViewportBorder(var2);
      }

      LookAndFeel.installProperty(var1, "opaque", Boolean.TRUE);
   }

   protected void installListeners(JScrollPane var1) {
      this.vsbChangeListener = this.createVSBChangeListener();
      this.vsbPropertyChangeListener = this.createVSBPropertyChangeListener();
      this.hsbChangeListener = this.createHSBChangeListener();
      this.hsbPropertyChangeListener = this.createHSBPropertyChangeListener();
      this.viewportChangeListener = this.createViewportChangeListener();
      this.spPropertyChangeListener = this.createPropertyChangeListener();
      JViewport var2 = this.scrollpane.getViewport();
      JScrollBar var3 = this.scrollpane.getVerticalScrollBar();
      JScrollBar var4 = this.scrollpane.getHorizontalScrollBar();
      if (var2 != null) {
         var2.addChangeListener(this.viewportChangeListener);
      }

      if (var3 != null) {
         var3.getModel().addChangeListener(this.vsbChangeListener);
         var3.addPropertyChangeListener(this.vsbPropertyChangeListener);
      }

      if (var4 != null) {
         var4.getModel().addChangeListener(this.hsbChangeListener);
         var4.addPropertyChangeListener(this.hsbPropertyChangeListener);
      }

      this.scrollpane.addPropertyChangeListener(this.spPropertyChangeListener);
      this.mouseScrollListener = this.createMouseWheelListener();
      this.scrollpane.addMouseWheelListener(this.mouseScrollListener);
   }

   protected void installKeyboardActions(JScrollPane var1) {
      InputMap var2 = this.getInputMap(1);
      SwingUtilities.replaceUIInputMap(var1, 1, var2);
      LazyActionMap.installLazyActionMap(var1, BasicScrollPaneUI.class, "ScrollPane.actionMap");
   }

   InputMap getInputMap(int var1) {
      if (var1 == 1) {
         InputMap var2 = (InputMap)DefaultLookup.get(this.scrollpane, this, "ScrollPane.ancestorInputMap");
         InputMap var3;
         if (!this.scrollpane.getComponentOrientation().isLeftToRight() && (var3 = (InputMap)DefaultLookup.get(this.scrollpane, this, "ScrollPane.ancestorInputMap.RightToLeft")) != null) {
            var3.setParent(var2);
            return var3;
         } else {
            return var2;
         }
      } else {
         return null;
      }
   }

   public void installUI(JComponent var1) {
      this.scrollpane = (JScrollPane)var1;
      this.installDefaults(this.scrollpane);
      this.installListeners(this.scrollpane);
      this.installKeyboardActions(this.scrollpane);
   }

   protected void uninstallDefaults(JScrollPane var1) {
      LookAndFeel.uninstallBorder(this.scrollpane);
      if (this.scrollpane.getViewportBorder() instanceof UIResource) {
         this.scrollpane.setViewportBorder((Border)null);
      }

   }

   protected void uninstallListeners(JComponent var1) {
      JViewport var2 = this.scrollpane.getViewport();
      JScrollBar var3 = this.scrollpane.getVerticalScrollBar();
      JScrollBar var4 = this.scrollpane.getHorizontalScrollBar();
      if (var2 != null) {
         var2.removeChangeListener(this.viewportChangeListener);
      }

      if (var3 != null) {
         var3.getModel().removeChangeListener(this.vsbChangeListener);
         var3.removePropertyChangeListener(this.vsbPropertyChangeListener);
      }

      if (var4 != null) {
         var4.getModel().removeChangeListener(this.hsbChangeListener);
         var4.removePropertyChangeListener(this.hsbPropertyChangeListener);
      }

      this.scrollpane.removePropertyChangeListener(this.spPropertyChangeListener);
      if (this.mouseScrollListener != null) {
         this.scrollpane.removeMouseWheelListener(this.mouseScrollListener);
      }

      this.vsbChangeListener = null;
      this.hsbChangeListener = null;
      this.viewportChangeListener = null;
      this.spPropertyChangeListener = null;
      this.mouseScrollListener = null;
      this.handler = null;
   }

   protected void uninstallKeyboardActions(JScrollPane var1) {
      SwingUtilities.replaceUIActionMap(var1, (ActionMap)null);
      SwingUtilities.replaceUIInputMap(var1, 1, (InputMap)null);
   }

   public void uninstallUI(JComponent var1) {
      this.uninstallDefaults(this.scrollpane);
      this.uninstallListeners(this.scrollpane);
      this.uninstallKeyboardActions(this.scrollpane);
      this.scrollpane = null;
   }

   private BasicScrollPaneUI.Handler getHandler() {
      if (this.handler == null) {
         this.handler = new BasicScrollPaneUI.Handler();
      }

      return this.handler;
   }

   protected void syncScrollPaneWithViewport() {
      JViewport var1 = this.scrollpane.getViewport();
      JScrollBar var2 = this.scrollpane.getVerticalScrollBar();
      JScrollBar var3 = this.scrollpane.getHorizontalScrollBar();
      JViewport var4 = this.scrollpane.getRowHeader();
      JViewport var5 = this.scrollpane.getColumnHeader();
      boolean var6 = this.scrollpane.getComponentOrientation().isLeftToRight();
      if (var1 != null) {
         Dimension var7 = var1.getExtentSize();
         Dimension var8 = var1.getViewSize();
         Point var9 = var1.getViewPosition();
         int var10;
         int var11;
         int var12;
         if (var2 != null) {
            var10 = var7.height;
            var11 = var8.height;
            var12 = Math.max(0, Math.min(var9.y, var11 - var10));
            var2.setValues(var12, var10, 0, var11);
         }

         if (var3 != null) {
            var10 = var7.width;
            var11 = var8.width;
            if (var6) {
               var12 = Math.max(0, Math.min(var9.x, var11 - var10));
            } else {
               int var13 = var3.getValue();
               if (this.setValueCalled && var11 - var13 == var9.x) {
                  var12 = Math.max(0, Math.min(var11 - var10, var13));
                  if (var10 != 0) {
                     this.setValueCalled = false;
                  }
               } else if (var10 > var11) {
                  var9.x = var11 - var10;
                  var1.setViewPosition(var9);
                  var12 = 0;
               } else {
                  var12 = Math.max(0, Math.min(var11 - var10, var11 - var10 - var9.x));
                  if (this.oldExtent > var10) {
                     var12 -= this.oldExtent - var10;
                  }
               }
            }

            this.oldExtent = var10;
            var3.setValues(var12, var10, 0, var11);
         }

         Point var14;
         if (var4 != null) {
            var14 = var4.getViewPosition();
            var14.y = var1.getViewPosition().y;
            var14.x = 0;
            var4.setViewPosition(var14);
         }

         if (var5 != null) {
            var14 = var5.getViewPosition();
            if (var6) {
               var14.x = var1.getViewPosition().x;
            } else {
               var14.x = Math.max(0, var1.getViewPosition().x);
            }

            var14.y = 0;
            var5.setViewPosition(var14);
         }
      }

   }

   public int getBaseline(JComponent var1, int var2, int var3) {
      if (var1 == null) {
         throw new NullPointerException("Component must be non-null");
      } else if (var2 >= 0 && var3 >= 0) {
         JViewport var4 = this.scrollpane.getViewport();
         Insets var5 = this.scrollpane.getInsets();
         int var6 = var5.top;
         var3 = var3 - var5.top - var5.bottom;
         var2 = var2 - var5.left - var5.right;
         JViewport var7 = this.scrollpane.getColumnHeader();
         Component var8;
         int var10;
         if (var7 != null && var7.isVisible()) {
            var8 = var7.getView();
            Dimension var9;
            if (var8 != null && var8.isVisible()) {
               var9 = var8.getPreferredSize();
               var10 = var8.getBaseline(var9.width, var9.height);
               if (var10 >= 0) {
                  return var6 + var10;
               }
            }

            var9 = var7.getPreferredSize();
            var3 -= var9.height;
            var6 += var9.height;
         }

         var8 = var4 == null ? null : var4.getView();
         if (var8 != null && var8.isVisible() && var8.getBaselineResizeBehavior() == Component.BaselineResizeBehavior.CONSTANT_ASCENT) {
            Border var11 = this.scrollpane.getViewportBorder();
            if (var11 != null) {
               Insets var12 = var11.getBorderInsets(this.scrollpane);
               var6 += var12.top;
               var3 = var3 - var12.top - var12.bottom;
               var2 = var2 - var12.left - var12.right;
            }

            if (var8.getWidth() > 0 && var8.getHeight() > 0) {
               Dimension var13 = var8.getMinimumSize();
               var2 = Math.max(var13.width, var8.getWidth());
               var3 = Math.max(var13.height, var8.getHeight());
            }

            if (var2 > 0 && var3 > 0) {
               var10 = var8.getBaseline(var2, var3);
               if (var10 > 0) {
                  return var6 + var10;
               }
            }
         }

         return -1;
      } else {
         throw new IllegalArgumentException("Width and height must be >= 0");
      }
   }

   public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent var1) {
      super.getBaselineResizeBehavior(var1);
      return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
   }

   protected ChangeListener createViewportChangeListener() {
      return this.getHandler();
   }

   private PropertyChangeListener createHSBPropertyChangeListener() {
      return this.getHandler();
   }

   protected ChangeListener createHSBChangeListener() {
      return this.getHandler();
   }

   private PropertyChangeListener createVSBPropertyChangeListener() {
      return this.getHandler();
   }

   protected ChangeListener createVSBChangeListener() {
      return this.getHandler();
   }

   protected MouseWheelListener createMouseWheelListener() {
      return this.getHandler();
   }

   protected void updateScrollBarDisplayPolicy(PropertyChangeEvent var1) {
      this.scrollpane.revalidate();
      this.scrollpane.repaint();
   }

   protected void updateViewport(PropertyChangeEvent var1) {
      JViewport var2 = (JViewport)((JViewport)var1.getOldValue());
      JViewport var3 = (JViewport)((JViewport)var1.getNewValue());
      if (var2 != null) {
         var2.removeChangeListener(this.viewportChangeListener);
      }

      if (var3 != null) {
         Point var4 = var3.getViewPosition();
         if (this.scrollpane.getComponentOrientation().isLeftToRight()) {
            var4.x = Math.max(var4.x, 0);
         } else {
            int var5 = var3.getViewSize().width;
            int var6 = var3.getExtentSize().width;
            if (var6 > var5) {
               var4.x = var5 - var6;
            } else {
               var4.x = Math.max(0, Math.min(var5 - var6, var4.x));
            }
         }

         var4.y = Math.max(var4.y, 0);
         var3.setViewPosition(var4);
         var3.addChangeListener(this.viewportChangeListener);
      }

   }

   protected void updateRowHeader(PropertyChangeEvent var1) {
      JViewport var2 = (JViewport)((JViewport)var1.getNewValue());
      if (var2 != null) {
         JViewport var3 = this.scrollpane.getViewport();
         Point var4 = var2.getViewPosition();
         var4.y = var3 != null ? var3.getViewPosition().y : 0;
         var2.setViewPosition(var4);
      }

   }

   protected void updateColumnHeader(PropertyChangeEvent var1) {
      JViewport var2 = (JViewport)((JViewport)var1.getNewValue());
      if (var2 != null) {
         JViewport var3 = this.scrollpane.getViewport();
         Point var4 = var2.getViewPosition();
         if (var3 == null) {
            var4.x = 0;
         } else if (this.scrollpane.getComponentOrientation().isLeftToRight()) {
            var4.x = var3.getViewPosition().x;
         } else {
            var4.x = Math.max(0, var3.getViewPosition().x);
         }

         var2.setViewPosition(var4);
         this.scrollpane.add(var2, "COLUMN_HEADER");
      }

   }

   private void updateHorizontalScrollBar(PropertyChangeEvent var1) {
      this.updateScrollBar(var1, this.hsbChangeListener, this.hsbPropertyChangeListener);
   }

   private void updateVerticalScrollBar(PropertyChangeEvent var1) {
      this.updateScrollBar(var1, this.vsbChangeListener, this.vsbPropertyChangeListener);
   }

   private void updateScrollBar(PropertyChangeEvent var1, ChangeListener var2, PropertyChangeListener var3) {
      JScrollBar var4 = (JScrollBar)var1.getOldValue();
      if (var4 != null) {
         if (var2 != null) {
            var4.getModel().removeChangeListener(var2);
         }

         if (var3 != null) {
            var4.removePropertyChangeListener(var3);
         }
      }

      var4 = (JScrollBar)var1.getNewValue();
      if (var4 != null) {
         if (var2 != null) {
            var4.getModel().addChangeListener(var2);
         }

         if (var3 != null) {
            var4.addPropertyChangeListener(var3);
         }
      }

   }

   protected PropertyChangeListener createPropertyChangeListener() {
      return this.getHandler();
   }

   class Handler implements ChangeListener, PropertyChangeListener, MouseWheelListener {
      public void mouseWheelMoved(MouseWheelEvent var1) {
         if (BasicScrollPaneUI.this.scrollpane.isWheelScrollingEnabled() && var1.getWheelRotation() != 0) {
            JScrollBar var2 = BasicScrollPaneUI.this.scrollpane.getVerticalScrollBar();
            int var3 = var1.getWheelRotation() < 0 ? -1 : 1;
            byte var4 = 1;
            if (var2 == null || !var2.isVisible() || var1.isShiftDown()) {
               var2 = BasicScrollPaneUI.this.scrollpane.getHorizontalScrollBar();
               if (var2 == null || !var2.isVisible()) {
                  return;
               }

               var4 = 0;
            }

            var1.consume();
            if (var1.getScrollType() == 0) {
               JViewport var5 = BasicScrollPaneUI.this.scrollpane.getViewport();
               if (var5 == null) {
                  return;
               }

               Component var6 = var5.getView();
               int var7 = Math.abs(var1.getUnitsToScroll());
               boolean var8 = Math.abs(var1.getWheelRotation()) == 1;
               Object var9 = var2.getClientProperty("JScrollBar.fastWheelScrolling");
               if (Boolean.TRUE == var9 && var6 instanceof Scrollable) {
                  Scrollable var10 = (Scrollable)var6;
                  Rectangle var11 = var5.getViewRect();
                  int var12 = var11.x;
                  boolean var13 = var6.getComponentOrientation().isLeftToRight();
                  int var14 = var2.getMinimum();
                  int var15 = var2.getMaximum() - var2.getModel().getExtent();
                  int var16;
                  if (var8) {
                     var16 = var10.getScrollableBlockIncrement(var11, var4, var3);
                     if (var3 < 0) {
                        var14 = Math.max(var14, var2.getValue() - var16);
                     } else {
                        var15 = Math.min(var15, var2.getValue() + var16);
                     }
                  }

                  for(var16 = 0; var16 < var7; ++var16) {
                     int var17 = var10.getScrollableUnitIncrement(var11, var4, var3);
                     if (var4 == 1) {
                        if (var3 < 0) {
                           var11.y -= var17;
                           if (var11.y <= var14) {
                              var11.y = var14;
                              break;
                           }
                        } else {
                           var11.y += var17;
                           if (var11.y >= var15) {
                              var11.y = var15;
                              break;
                           }
                        }
                     } else if (var13 && var3 < 0 || !var13 && var3 > 0) {
                        var11.x -= var17;
                        if (var13 && var11.x < var14) {
                           var11.x = var14;
                           break;
                        }
                     } else if ((!var13 || var3 <= 0) && (var13 || var3 >= 0)) {
                        assert false : "Non-sensical ComponentOrientation / scroll direction";
                     } else {
                        var11.x += var17;
                        if (var13 && var11.x > var15) {
                           var11.x = var15;
                           break;
                        }
                     }
                  }

                  if (var4 == 1) {
                     var2.setValue(var11.y);
                  } else if (var13) {
                     var2.setValue(var11.x);
                  } else {
                     var16 = var2.getValue() - (var11.x - var12);
                     if (var16 < var14) {
                        var16 = var14;
                     } else if (var16 > var15) {
                        var16 = var15;
                     }

                     var2.setValue(var16);
                  }
               } else {
                  BasicScrollBarUI.scrollByUnits(var2, var3, var7, var8);
               }
            } else if (var1.getScrollType() == 1) {
               BasicScrollBarUI.scrollByBlock(var2, var3);
            }
         }

      }

      public void stateChanged(ChangeEvent var1) {
         JViewport var2 = BasicScrollPaneUI.this.scrollpane.getViewport();
         if (var2 != null) {
            if (var1.getSource() == var2) {
               BasicScrollPaneUI.this.syncScrollPaneWithViewport();
            } else {
               JScrollBar var3 = BasicScrollPaneUI.this.scrollpane.getHorizontalScrollBar();
               if (var3 != null && var1.getSource() == var3.getModel()) {
                  this.hsbStateChanged(var2, var1);
               } else {
                  JScrollBar var4 = BasicScrollPaneUI.this.scrollpane.getVerticalScrollBar();
                  if (var4 != null && var1.getSource() == var4.getModel()) {
                     this.vsbStateChanged(var2, var1);
                  }
               }
            }
         }

      }

      private void vsbStateChanged(JViewport var1, ChangeEvent var2) {
         BoundedRangeModel var3 = (BoundedRangeModel)((BoundedRangeModel)var2.getSource());
         Point var4 = var1.getViewPosition();
         var4.y = var3.getValue();
         var1.setViewPosition(var4);
      }

      private void hsbStateChanged(JViewport var1, ChangeEvent var2) {
         BoundedRangeModel var3 = (BoundedRangeModel)((BoundedRangeModel)var2.getSource());
         Point var4 = var1.getViewPosition();
         int var5 = var3.getValue();
         if (BasicScrollPaneUI.this.scrollpane.getComponentOrientation().isLeftToRight()) {
            var4.x = var5;
         } else {
            int var6 = var1.getViewSize().width;
            int var7 = var1.getExtentSize().width;
            int var8 = var4.x;
            var4.x = var6 - var7 - var5;
            if (var7 == 0 && var5 != 0 && var8 == var6) {
               BasicScrollPaneUI.this.setValueCalled = true;
            } else if (var7 != 0 && var8 < 0 && var4.x == 0) {
               var4.x += var5;
            }
         }

         var1.setViewPosition(var4);
      }

      public void propertyChange(PropertyChangeEvent var1) {
         if (var1.getSource() == BasicScrollPaneUI.this.scrollpane) {
            this.scrollPanePropertyChange(var1);
         } else {
            this.sbPropertyChange(var1);
         }

      }

      private void scrollPanePropertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if (var2 == "verticalScrollBarDisplayPolicy") {
            BasicScrollPaneUI.this.updateScrollBarDisplayPolicy(var1);
         } else if (var2 == "horizontalScrollBarDisplayPolicy") {
            BasicScrollPaneUI.this.updateScrollBarDisplayPolicy(var1);
         } else if (var2 == "viewport") {
            BasicScrollPaneUI.this.updateViewport(var1);
         } else if (var2 == "rowHeader") {
            BasicScrollPaneUI.this.updateRowHeader(var1);
         } else if (var2 == "columnHeader") {
            BasicScrollPaneUI.this.updateColumnHeader(var1);
         } else if (var2 == "verticalScrollBar") {
            BasicScrollPaneUI.this.updateVerticalScrollBar(var1);
         } else if (var2 == "horizontalScrollBar") {
            BasicScrollPaneUI.this.updateHorizontalScrollBar(var1);
         } else if (var2 == "componentOrientation") {
            BasicScrollPaneUI.this.scrollpane.revalidate();
            BasicScrollPaneUI.this.scrollpane.repaint();
         }

      }

      private void sbPropertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         Object var3 = var1.getSource();
         JScrollBar var4;
         if ("model" == var2) {
            var4 = BasicScrollPaneUI.this.scrollpane.getVerticalScrollBar();
            BoundedRangeModel var5 = (BoundedRangeModel)var1.getOldValue();
            ChangeListener var6 = null;
            if (var3 == var4) {
               var6 = BasicScrollPaneUI.this.vsbChangeListener;
            } else if (var3 == BasicScrollPaneUI.this.scrollpane.getHorizontalScrollBar()) {
               var4 = BasicScrollPaneUI.this.scrollpane.getHorizontalScrollBar();
               var6 = BasicScrollPaneUI.this.hsbChangeListener;
            }

            if (var6 != null) {
               if (var5 != null) {
                  var5.removeChangeListener(var6);
               }

               if (var4.getModel() != null) {
                  var4.getModel().addChangeListener(var6);
               }
            }
         } else if ("componentOrientation" == var2 && var3 == BasicScrollPaneUI.this.scrollpane.getHorizontalScrollBar()) {
            var4 = BasicScrollPaneUI.this.scrollpane.getHorizontalScrollBar();
            JViewport var7 = BasicScrollPaneUI.this.scrollpane.getViewport();
            Point var8 = var7.getViewPosition();
            if (BasicScrollPaneUI.this.scrollpane.getComponentOrientation().isLeftToRight()) {
               var8.x = var4.getValue();
            } else {
               var8.x = var7.getViewSize().width - var7.getExtentSize().width - var4.getValue();
            }

            var7.setViewPosition(var8);
         }

      }
   }

   private static class Actions extends UIAction {
      private static final String SCROLL_UP = "scrollUp";
      private static final String SCROLL_DOWN = "scrollDown";
      private static final String SCROLL_HOME = "scrollHome";
      private static final String SCROLL_END = "scrollEnd";
      private static final String UNIT_SCROLL_UP = "unitScrollUp";
      private static final String UNIT_SCROLL_DOWN = "unitScrollDown";
      private static final String SCROLL_LEFT = "scrollLeft";
      private static final String SCROLL_RIGHT = "scrollRight";
      private static final String UNIT_SCROLL_LEFT = "unitScrollLeft";
      private static final String UNIT_SCROLL_RIGHT = "unitScrollRight";

      Actions(String var1) {
         super(var1);
      }

      public void actionPerformed(ActionEvent var1) {
         JScrollPane var2 = (JScrollPane)var1.getSource();
         boolean var3 = var2.getComponentOrientation().isLeftToRight();
         String var4 = this.getName();
         if (var4 == "scrollUp") {
            this.scroll(var2, 1, -1, true);
         } else if (var4 == "scrollDown") {
            this.scroll(var2, 1, 1, true);
         } else if (var4 == "scrollHome") {
            this.scrollHome(var2);
         } else if (var4 == "scrollEnd") {
            this.scrollEnd(var2);
         } else if (var4 == "unitScrollUp") {
            this.scroll(var2, 1, -1, false);
         } else if (var4 == "unitScrollDown") {
            this.scroll(var2, 1, 1, false);
         } else if (var4 == "scrollLeft") {
            this.scroll(var2, 0, var3 ? -1 : 1, true);
         } else if (var4 == "scrollRight") {
            this.scroll(var2, 0, var3 ? 1 : -1, true);
         } else if (var4 == "unitScrollLeft") {
            this.scroll(var2, 0, var3 ? -1 : 1, false);
         } else if (var4 == "unitScrollRight") {
            this.scroll(var2, 0, var3 ? 1 : -1, false);
         }

      }

      private void scrollEnd(JScrollPane var1) {
         JViewport var2 = var1.getViewport();
         Component var3;
         if (var2 != null && (var3 = var2.getView()) != null) {
            Rectangle var4 = var2.getViewRect();
            Rectangle var5 = var3.getBounds();
            if (var1.getComponentOrientation().isLeftToRight()) {
               var2.setViewPosition(new Point(var5.width - var4.width, var5.height - var4.height));
            } else {
               var2.setViewPosition(new Point(0, var5.height - var4.height));
            }
         }

      }

      private void scrollHome(JScrollPane var1) {
         JViewport var2 = var1.getViewport();
         Component var3;
         if (var2 != null && (var3 = var2.getView()) != null) {
            if (var1.getComponentOrientation().isLeftToRight()) {
               var2.setViewPosition(new Point(0, 0));
            } else {
               Rectangle var4 = var2.getViewRect();
               Rectangle var5 = var3.getBounds();
               var2.setViewPosition(new Point(var5.width - var4.width, 0));
            }
         }

      }

      private void scroll(JScrollPane var1, int var2, int var3, boolean var4) {
         JViewport var5 = var1.getViewport();
         Component var6;
         if (var5 != null && (var6 = var5.getView()) != null) {
            Rectangle var7 = var5.getViewRect();
            Dimension var8 = var6.getSize();
            int var9;
            if (var6 instanceof Scrollable) {
               if (var4) {
                  var9 = ((Scrollable)var6).getScrollableBlockIncrement(var7, var2, var3);
               } else {
                  var9 = ((Scrollable)var6).getScrollableUnitIncrement(var7, var2, var3);
               }
            } else if (var4) {
               if (var2 == 1) {
                  var9 = var7.height;
               } else {
                  var9 = var7.width;
               }
            } else {
               var9 = 10;
            }

            if (var2 == 1) {
               var7.y += var9 * var3;
               if (var7.y + var7.height > var8.height) {
                  var7.y = Math.max(0, var8.height - var7.height);
               } else if (var7.y < 0) {
                  var7.y = 0;
               }
            } else if (var1.getComponentOrientation().isLeftToRight()) {
               var7.x += var9 * var3;
               if (var7.x + var7.width > var8.width) {
                  var7.x = Math.max(0, var8.width - var7.width);
               } else if (var7.x < 0) {
                  var7.x = 0;
               }
            } else {
               var7.x -= var9 * var3;
               if (var7.width > var8.width) {
                  var7.x = var8.width - var7.width;
               } else {
                  var7.x = Math.max(0, Math.min(var8.width - var7.width, var7.x));
               }
            }

            var5.setViewPosition(var7.getLocation());
         }

      }
   }

   public class PropertyChangeHandler implements PropertyChangeListener {
      public void propertyChange(PropertyChangeEvent var1) {
         BasicScrollPaneUI.this.getHandler().propertyChange(var1);
      }
   }

   protected class MouseWheelHandler implements MouseWheelListener {
      public void mouseWheelMoved(MouseWheelEvent var1) {
         BasicScrollPaneUI.this.getHandler().mouseWheelMoved(var1);
      }
   }

   public class VSBChangeListener implements ChangeListener {
      public void stateChanged(ChangeEvent var1) {
         BasicScrollPaneUI.this.getHandler().stateChanged(var1);
      }
   }

   public class HSBChangeListener implements ChangeListener {
      public void stateChanged(ChangeEvent var1) {
         BasicScrollPaneUI.this.getHandler().stateChanged(var1);
      }
   }

   public class ViewportChangeHandler implements ChangeListener {
      public void stateChanged(ChangeEvent var1) {
         BasicScrollPaneUI.this.getHandler().stateChanged(var1);
      }
   }
}
