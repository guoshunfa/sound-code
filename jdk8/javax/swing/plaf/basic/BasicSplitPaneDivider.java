package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import sun.swing.DefaultLookup;

public class BasicSplitPaneDivider extends Container implements PropertyChangeListener {
   protected static final int ONE_TOUCH_SIZE = 6;
   protected static final int ONE_TOUCH_OFFSET = 2;
   protected BasicSplitPaneDivider.DragController dragger;
   protected BasicSplitPaneUI splitPaneUI;
   protected int dividerSize = 0;
   protected Component hiddenDivider;
   protected JSplitPane splitPane;
   protected BasicSplitPaneDivider.MouseHandler mouseHandler;
   protected int orientation;
   protected JButton leftButton;
   protected JButton rightButton;
   private Border border;
   private boolean mouseOver;
   private int oneTouchSize;
   private int oneTouchOffset;
   private boolean centerOneTouchButtons;

   public BasicSplitPaneDivider(BasicSplitPaneUI var1) {
      this.oneTouchSize = DefaultLookup.getInt(var1.getSplitPane(), var1, "SplitPane.oneTouchButtonSize", 6);
      this.oneTouchOffset = DefaultLookup.getInt(var1.getSplitPane(), var1, "SplitPane.oneTouchButtonOffset", 2);
      this.centerOneTouchButtons = DefaultLookup.getBoolean(var1.getSplitPane(), var1, "SplitPane.centerOneTouchButtons", true);
      this.setLayout(new BasicSplitPaneDivider.DividerLayout());
      this.setBasicSplitPaneUI(var1);
      this.orientation = this.splitPane.getOrientation();
      this.setCursor(this.orientation == 1 ? Cursor.getPredefinedCursor(11) : Cursor.getPredefinedCursor(9));
      this.setBackground(UIManager.getColor("SplitPane.background"));
   }

   private void revalidateSplitPane() {
      this.invalidate();
      if (this.splitPane != null) {
         this.splitPane.revalidate();
      }

   }

   public void setBasicSplitPaneUI(BasicSplitPaneUI var1) {
      if (this.splitPane != null) {
         this.splitPane.removePropertyChangeListener(this);
         if (this.mouseHandler != null) {
            this.splitPane.removeMouseListener(this.mouseHandler);
            this.splitPane.removeMouseMotionListener(this.mouseHandler);
            this.removeMouseListener(this.mouseHandler);
            this.removeMouseMotionListener(this.mouseHandler);
            this.mouseHandler = null;
         }
      }

      this.splitPaneUI = var1;
      if (var1 != null) {
         this.splitPane = var1.getSplitPane();
         if (this.splitPane != null) {
            if (this.mouseHandler == null) {
               this.mouseHandler = new BasicSplitPaneDivider.MouseHandler();
            }

            this.splitPane.addMouseListener(this.mouseHandler);
            this.splitPane.addMouseMotionListener(this.mouseHandler);
            this.addMouseListener(this.mouseHandler);
            this.addMouseMotionListener(this.mouseHandler);
            this.splitPane.addPropertyChangeListener(this);
            if (this.splitPane.isOneTouchExpandable()) {
               this.oneTouchExpandableChanged();
            }
         }
      } else {
         this.splitPane = null;
      }

   }

   public BasicSplitPaneUI getBasicSplitPaneUI() {
      return this.splitPaneUI;
   }

   public void setDividerSize(int var1) {
      this.dividerSize = var1;
   }

   public int getDividerSize() {
      return this.dividerSize;
   }

   public void setBorder(Border var1) {
      Border var2 = this.border;
      this.border = var1;
   }

   public Border getBorder() {
      return this.border;
   }

   public Insets getInsets() {
      Border var1 = this.getBorder();
      return var1 != null ? var1.getBorderInsets(this) : super.getInsets();
   }

   protected void setMouseOver(boolean var1) {
      this.mouseOver = var1;
   }

   public boolean isMouseOver() {
      return this.mouseOver;
   }

   public Dimension getPreferredSize() {
      return this.orientation == 1 ? new Dimension(this.getDividerSize(), 1) : new Dimension(1, this.getDividerSize());
   }

   public Dimension getMinimumSize() {
      return this.getPreferredSize();
   }

   public void propertyChange(PropertyChangeEvent var1) {
      if (var1.getSource() == this.splitPane) {
         if (var1.getPropertyName() == "orientation") {
            this.orientation = this.splitPane.getOrientation();
            this.setCursor(this.orientation == 1 ? Cursor.getPredefinedCursor(11) : Cursor.getPredefinedCursor(9));
            this.revalidateSplitPane();
         } else if (var1.getPropertyName() == "oneTouchExpandable") {
            this.oneTouchExpandableChanged();
         }
      }

   }

   public void paint(Graphics var1) {
      super.paint(var1);
      Border var2 = this.getBorder();
      if (var2 != null) {
         Dimension var3 = this.getSize();
         var2.paintBorder(this, var1, 0, 0, var3.width, var3.height);
      }

   }

   protected void oneTouchExpandableChanged() {
      if (DefaultLookup.getBoolean(this.splitPane, this.splitPaneUI, "SplitPane.supportsOneTouchButtons", true)) {
         if (this.splitPane.isOneTouchExpandable() && this.leftButton == null && this.rightButton == null) {
            this.leftButton = this.createLeftOneTouchButton();
            if (this.leftButton != null) {
               this.leftButton.addActionListener(new BasicSplitPaneDivider.OneTouchActionHandler(true));
            }

            this.rightButton = this.createRightOneTouchButton();
            if (this.rightButton != null) {
               this.rightButton.addActionListener(new BasicSplitPaneDivider.OneTouchActionHandler(false));
            }

            if (this.leftButton != null && this.rightButton != null) {
               this.add(this.leftButton);
               this.add(this.rightButton);
            }
         }

         this.revalidateSplitPane();
      }
   }

   protected JButton createLeftOneTouchButton() {
      JButton var1 = new JButton() {
         public void setBorder(Border var1) {
         }

         public void paint(Graphics var1) {
            if (BasicSplitPaneDivider.this.splitPane != null) {
               int[] var2 = new int[3];
               int[] var3 = new int[3];
               var1.setColor(this.getBackground());
               var1.fillRect(0, 0, this.getWidth(), this.getHeight());
               var1.setColor(Color.black);
               int var4;
               if (BasicSplitPaneDivider.this.orientation == 0) {
                  var4 = Math.min(this.getHeight(), BasicSplitPaneDivider.this.oneTouchSize);
                  var2[0] = var4;
                  var2[1] = 0;
                  var2[2] = var4 << 1;
                  var3[0] = 0;
                  var3[1] = var3[2] = var4;
                  var1.drawPolygon(var2, var3, 3);
               } else {
                  var4 = Math.min(this.getWidth(), BasicSplitPaneDivider.this.oneTouchSize);
                  var2[0] = var2[2] = var4;
                  var2[1] = 0;
                  var3[0] = 0;
                  var3[1] = var4;
                  var3[2] = var4 << 1;
               }

               var1.fillPolygon(var2, var3, 3);
            }

         }

         public boolean isFocusTraversable() {
            return false;
         }
      };
      var1.setMinimumSize(new Dimension(this.oneTouchSize, this.oneTouchSize));
      var1.setCursor(Cursor.getPredefinedCursor(0));
      var1.setFocusPainted(false);
      var1.setBorderPainted(false);
      var1.setRequestFocusEnabled(false);
      return var1;
   }

   protected JButton createRightOneTouchButton() {
      JButton var1 = new JButton() {
         public void setBorder(Border var1) {
         }

         public void paint(Graphics var1) {
            if (BasicSplitPaneDivider.this.splitPane != null) {
               int[] var2 = new int[3];
               int[] var3 = new int[3];
               var1.setColor(this.getBackground());
               var1.fillRect(0, 0, this.getWidth(), this.getHeight());
               int var4;
               if (BasicSplitPaneDivider.this.orientation == 0) {
                  var4 = Math.min(this.getHeight(), BasicSplitPaneDivider.this.oneTouchSize);
                  var2[0] = var4;
                  var2[1] = var4 << 1;
                  var2[2] = 0;
                  var3[0] = var4;
                  var3[1] = var3[2] = 0;
               } else {
                  var4 = Math.min(this.getWidth(), BasicSplitPaneDivider.this.oneTouchSize);
                  var2[0] = var2[2] = 0;
                  var2[1] = var4;
                  var3[0] = 0;
                  var3[1] = var4;
                  var3[2] = var4 << 1;
               }

               var1.setColor(Color.black);
               var1.fillPolygon(var2, var3, 3);
            }

         }

         public boolean isFocusTraversable() {
            return false;
         }
      };
      var1.setMinimumSize(new Dimension(this.oneTouchSize, this.oneTouchSize));
      var1.setCursor(Cursor.getPredefinedCursor(0));
      var1.setFocusPainted(false);
      var1.setBorderPainted(false);
      var1.setRequestFocusEnabled(false);
      return var1;
   }

   protected void prepareForDragging() {
      this.splitPaneUI.startDragging();
   }

   protected void dragDividerTo(int var1) {
      this.splitPaneUI.dragDividerTo(var1);
   }

   protected void finishDraggingTo(int var1) {
      this.splitPaneUI.finishDraggingTo(var1);
   }

   private class OneTouchActionHandler implements ActionListener {
      private boolean toMinimum;

      OneTouchActionHandler(boolean var2) {
         this.toMinimum = var2;
      }

      public void actionPerformed(ActionEvent var1) {
         Insets var2 = BasicSplitPaneDivider.this.splitPane.getInsets();
         int var3 = BasicSplitPaneDivider.this.splitPane.getLastDividerLocation();
         int var4 = BasicSplitPaneDivider.this.splitPaneUI.getDividerLocation(BasicSplitPaneDivider.this.splitPane);
         int var5;
         int var6;
         if (this.toMinimum) {
            if (BasicSplitPaneDivider.this.orientation == 0) {
               if (var4 >= BasicSplitPaneDivider.this.splitPane.getHeight() - var2.bottom - BasicSplitPaneDivider.this.getHeight()) {
                  var6 = BasicSplitPaneDivider.this.splitPane.getMaximumDividerLocation();
                  var5 = Math.min(var3, var6);
                  BasicSplitPaneDivider.this.splitPaneUI.setKeepHidden(false);
               } else {
                  var5 = var2.top;
                  BasicSplitPaneDivider.this.splitPaneUI.setKeepHidden(true);
               }
            } else if (var4 >= BasicSplitPaneDivider.this.splitPane.getWidth() - var2.right - BasicSplitPaneDivider.this.getWidth()) {
               var6 = BasicSplitPaneDivider.this.splitPane.getMaximumDividerLocation();
               var5 = Math.min(var3, var6);
               BasicSplitPaneDivider.this.splitPaneUI.setKeepHidden(false);
            } else {
               var5 = var2.left;
               BasicSplitPaneDivider.this.splitPaneUI.setKeepHidden(true);
            }
         } else if (BasicSplitPaneDivider.this.orientation == 0) {
            if (var4 == var2.top) {
               var6 = BasicSplitPaneDivider.this.splitPane.getMaximumDividerLocation();
               var5 = Math.min(var3, var6);
               BasicSplitPaneDivider.this.splitPaneUI.setKeepHidden(false);
            } else {
               var5 = BasicSplitPaneDivider.this.splitPane.getHeight() - BasicSplitPaneDivider.this.getHeight() - var2.top;
               BasicSplitPaneDivider.this.splitPaneUI.setKeepHidden(true);
            }
         } else if (var4 == var2.left) {
            var6 = BasicSplitPaneDivider.this.splitPane.getMaximumDividerLocation();
            var5 = Math.min(var3, var6);
            BasicSplitPaneDivider.this.splitPaneUI.setKeepHidden(false);
         } else {
            var5 = BasicSplitPaneDivider.this.splitPane.getWidth() - BasicSplitPaneDivider.this.getWidth() - var2.left;
            BasicSplitPaneDivider.this.splitPaneUI.setKeepHidden(true);
         }

         if (var4 != var5) {
            BasicSplitPaneDivider.this.splitPane.setDividerLocation(var5);
            BasicSplitPaneDivider.this.splitPane.setLastDividerLocation(var4);
         }

      }
   }

   protected class DividerLayout implements LayoutManager {
      public void layoutContainer(Container var1) {
         if (BasicSplitPaneDivider.this.leftButton != null && BasicSplitPaneDivider.this.rightButton != null && var1 == BasicSplitPaneDivider.this) {
            if (BasicSplitPaneDivider.this.splitPane.isOneTouchExpandable()) {
               Insets var2 = BasicSplitPaneDivider.this.getInsets();
               int var3;
               int var4;
               int var5;
               if (BasicSplitPaneDivider.this.orientation == 0) {
                  var3 = var2 != null ? var2.left : 0;
                  var4 = BasicSplitPaneDivider.this.getHeight();
                  if (var2 != null) {
                     var4 -= var2.top + var2.bottom;
                     var4 = Math.max(var4, 0);
                  }

                  var4 = Math.min(var4, BasicSplitPaneDivider.this.oneTouchSize);
                  var5 = (var1.getSize().height - var4) / 2;
                  if (!BasicSplitPaneDivider.this.centerOneTouchButtons) {
                     var5 = var2 != null ? var2.top : 0;
                     var3 = 0;
                  }

                  BasicSplitPaneDivider.this.leftButton.setBounds(var3 + BasicSplitPaneDivider.this.oneTouchOffset, var5, var4 * 2, var4);
                  BasicSplitPaneDivider.this.rightButton.setBounds(var3 + BasicSplitPaneDivider.this.oneTouchOffset + BasicSplitPaneDivider.this.oneTouchSize * 2, var5, var4 * 2, var4);
               } else {
                  var3 = var2 != null ? var2.top : 0;
                  var4 = BasicSplitPaneDivider.this.getWidth();
                  if (var2 != null) {
                     var4 -= var2.left + var2.right;
                     var4 = Math.max(var4, 0);
                  }

                  var4 = Math.min(var4, BasicSplitPaneDivider.this.oneTouchSize);
                  var5 = (var1.getSize().width - var4) / 2;
                  if (!BasicSplitPaneDivider.this.centerOneTouchButtons) {
                     var5 = var2 != null ? var2.left : 0;
                     var3 = 0;
                  }

                  BasicSplitPaneDivider.this.leftButton.setBounds(var5, var3 + BasicSplitPaneDivider.this.oneTouchOffset, var4, var4 * 2);
                  BasicSplitPaneDivider.this.rightButton.setBounds(var5, var3 + BasicSplitPaneDivider.this.oneTouchOffset + BasicSplitPaneDivider.this.oneTouchSize * 2, var4, var4 * 2);
               }
            } else {
               BasicSplitPaneDivider.this.leftButton.setBounds(-5, -5, 1, 1);
               BasicSplitPaneDivider.this.rightButton.setBounds(-5, -5, 1, 1);
            }
         }

      }

      public Dimension minimumLayoutSize(Container var1) {
         if (var1 == BasicSplitPaneDivider.this && BasicSplitPaneDivider.this.splitPane != null) {
            Dimension var2 = null;
            if (BasicSplitPaneDivider.this.splitPane.isOneTouchExpandable() && BasicSplitPaneDivider.this.leftButton != null) {
               var2 = BasicSplitPaneDivider.this.leftButton.getMinimumSize();
            }

            Insets var3 = BasicSplitPaneDivider.this.getInsets();
            int var4 = BasicSplitPaneDivider.this.getDividerSize();
            int var5 = var4;
            int var6;
            if (BasicSplitPaneDivider.this.orientation == 0) {
               if (var2 != null) {
                  var6 = var2.height;
                  if (var3 != null) {
                     var6 += var3.top + var3.bottom;
                  }

                  var5 = Math.max(var4, var6);
               }

               var4 = 1;
            } else {
               if (var2 != null) {
                  var6 = var2.width;
                  if (var3 != null) {
                     var6 += var3.left + var3.right;
                  }

                  var4 = Math.max(var4, var6);
               }

               var5 = 1;
            }

            return new Dimension(var4, var5);
         } else {
            return new Dimension(0, 0);
         }
      }

      public Dimension preferredLayoutSize(Container var1) {
         return this.minimumLayoutSize(var1);
      }

      public void removeLayoutComponent(Component var1) {
      }

      public void addLayoutComponent(String var1, Component var2) {
      }
   }

   protected class VerticalDragController extends BasicSplitPaneDivider.DragController {
      protected VerticalDragController(MouseEvent var2) {
         super(var2);
         JSplitPane var3 = BasicSplitPaneDivider.this.splitPaneUI.getSplitPane();
         Component var4 = var3.getLeftComponent();
         Component var5 = var3.getRightComponent();
         this.initialX = BasicSplitPaneDivider.this.getLocation().y;
         if (var2.getSource() == BasicSplitPaneDivider.this) {
            this.offset = var2.getY();
         } else {
            this.offset = var2.getY() - this.initialX;
         }

         if (var4 != null && var5 != null && this.offset >= -1 && this.offset <= BasicSplitPaneDivider.this.getSize().height) {
            Insets var6 = var3.getInsets();
            if (var4.isVisible()) {
               this.minX = var4.getMinimumSize().height;
               if (var6 != null) {
                  this.minX += var6.top;
               }
            } else {
               this.minX = 0;
            }

            int var7;
            if (var5.isVisible()) {
               var7 = var6 != null ? var6.bottom : 0;
               this.maxX = Math.max(0, var3.getSize().height - (BasicSplitPaneDivider.this.getSize().height + var7) - var5.getMinimumSize().height);
            } else {
               var7 = var6 != null ? var6.bottom : 0;
               this.maxX = Math.max(0, var3.getSize().height - (BasicSplitPaneDivider.this.getSize().height + var7));
            }

            if (this.maxX < this.minX) {
               this.minX = this.maxX = 0;
            }
         } else {
            this.maxX = -1;
         }

      }

      protected int getNeededLocation(int var1, int var2) {
         int var3 = Math.min(this.maxX, Math.max(this.minX, var2 - this.offset));
         return var3;
      }

      protected int positionForMouseEvent(MouseEvent var1) {
         int var2 = var1.getSource() == BasicSplitPaneDivider.this ? var1.getY() + BasicSplitPaneDivider.this.getLocation().y : var1.getY();
         var2 = Math.min(this.maxX, Math.max(this.minX, var2 - this.offset));
         return var2;
      }
   }

   protected class DragController {
      int initialX;
      int maxX;
      int minX;
      int offset;

      protected DragController(MouseEvent var2) {
         JSplitPane var3 = BasicSplitPaneDivider.this.splitPaneUI.getSplitPane();
         Component var4 = var3.getLeftComponent();
         Component var5 = var3.getRightComponent();
         this.initialX = BasicSplitPaneDivider.this.getLocation().x;
         if (var2.getSource() == BasicSplitPaneDivider.this) {
            this.offset = var2.getX();
         } else {
            this.offset = var2.getX() - this.initialX;
         }

         if (var4 != null && var5 != null && this.offset >= -1 && this.offset < BasicSplitPaneDivider.this.getSize().width) {
            Insets var6 = var3.getInsets();
            if (var4.isVisible()) {
               this.minX = var4.getMinimumSize().width;
               if (var6 != null) {
                  this.minX += var6.left;
               }
            } else {
               this.minX = 0;
            }

            int var7;
            if (var5.isVisible()) {
               var7 = var6 != null ? var6.right : 0;
               this.maxX = Math.max(0, var3.getSize().width - (BasicSplitPaneDivider.this.getSize().width + var7) - var5.getMinimumSize().width);
            } else {
               var7 = var6 != null ? var6.right : 0;
               this.maxX = Math.max(0, var3.getSize().width - (BasicSplitPaneDivider.this.getSize().width + var7));
            }

            if (this.maxX < this.minX) {
               this.minX = this.maxX = 0;
            }
         } else {
            this.maxX = -1;
         }

      }

      protected boolean isValid() {
         return this.maxX > 0;
      }

      protected int positionForMouseEvent(MouseEvent var1) {
         int var2 = var1.getSource() == BasicSplitPaneDivider.this ? var1.getX() + BasicSplitPaneDivider.this.getLocation().x : var1.getX();
         var2 = Math.min(this.maxX, Math.max(this.minX, var2 - this.offset));
         return var2;
      }

      protected int getNeededLocation(int var1, int var2) {
         int var3 = Math.min(this.maxX, Math.max(this.minX, var1 - this.offset));
         return var3;
      }

      protected void continueDrag(int var1, int var2) {
         BasicSplitPaneDivider.this.dragDividerTo(this.getNeededLocation(var1, var2));
      }

      protected void continueDrag(MouseEvent var1) {
         BasicSplitPaneDivider.this.dragDividerTo(this.positionForMouseEvent(var1));
      }

      protected void completeDrag(int var1, int var2) {
         BasicSplitPaneDivider.this.finishDraggingTo(this.getNeededLocation(var1, var2));
      }

      protected void completeDrag(MouseEvent var1) {
         BasicSplitPaneDivider.this.finishDraggingTo(this.positionForMouseEvent(var1));
      }
   }

   protected class MouseHandler extends MouseAdapter implements MouseMotionListener {
      public void mousePressed(MouseEvent var1) {
         if ((var1.getSource() == BasicSplitPaneDivider.this || var1.getSource() == BasicSplitPaneDivider.this.splitPane) && BasicSplitPaneDivider.this.dragger == null && BasicSplitPaneDivider.this.splitPane.isEnabled()) {
            Component var2 = BasicSplitPaneDivider.this.splitPaneUI.getNonContinuousLayoutDivider();
            if (BasicSplitPaneDivider.this.hiddenDivider != var2) {
               if (BasicSplitPaneDivider.this.hiddenDivider != null) {
                  BasicSplitPaneDivider.this.hiddenDivider.removeMouseListener(this);
                  BasicSplitPaneDivider.this.hiddenDivider.removeMouseMotionListener(this);
               }

               BasicSplitPaneDivider.this.hiddenDivider = var2;
               if (BasicSplitPaneDivider.this.hiddenDivider != null) {
                  BasicSplitPaneDivider.this.hiddenDivider.addMouseMotionListener(this);
                  BasicSplitPaneDivider.this.hiddenDivider.addMouseListener(this);
               }
            }

            if (BasicSplitPaneDivider.this.splitPane.getLeftComponent() != null && BasicSplitPaneDivider.this.splitPane.getRightComponent() != null) {
               if (BasicSplitPaneDivider.this.orientation == 1) {
                  BasicSplitPaneDivider.this.dragger = BasicSplitPaneDivider.this.new DragController(var1);
               } else {
                  BasicSplitPaneDivider.this.dragger = BasicSplitPaneDivider.this.new VerticalDragController(var1);
               }

               if (!BasicSplitPaneDivider.this.dragger.isValid()) {
                  BasicSplitPaneDivider.this.dragger = null;
               } else {
                  BasicSplitPaneDivider.this.prepareForDragging();
                  BasicSplitPaneDivider.this.dragger.continueDrag(var1);
               }
            }

            var1.consume();
         }

      }

      public void mouseReleased(MouseEvent var1) {
         if (BasicSplitPaneDivider.this.dragger != null) {
            if (var1.getSource() == BasicSplitPaneDivider.this.splitPane) {
               BasicSplitPaneDivider.this.dragger.completeDrag(var1.getX(), var1.getY());
            } else {
               Point var2;
               if (var1.getSource() == BasicSplitPaneDivider.this) {
                  var2 = BasicSplitPaneDivider.this.getLocation();
                  BasicSplitPaneDivider.this.dragger.completeDrag(var1.getX() + var2.x, var1.getY() + var2.y);
               } else if (var1.getSource() == BasicSplitPaneDivider.this.hiddenDivider) {
                  var2 = BasicSplitPaneDivider.this.hiddenDivider.getLocation();
                  int var3 = var1.getX() + var2.x;
                  int var4 = var1.getY() + var2.y;
                  BasicSplitPaneDivider.this.dragger.completeDrag(var3, var4);
               }
            }

            BasicSplitPaneDivider.this.dragger = null;
            var1.consume();
         }

      }

      public void mouseDragged(MouseEvent var1) {
         if (BasicSplitPaneDivider.this.dragger != null) {
            if (var1.getSource() == BasicSplitPaneDivider.this.splitPane) {
               BasicSplitPaneDivider.this.dragger.continueDrag(var1.getX(), var1.getY());
            } else {
               Point var2;
               if (var1.getSource() == BasicSplitPaneDivider.this) {
                  var2 = BasicSplitPaneDivider.this.getLocation();
                  BasicSplitPaneDivider.this.dragger.continueDrag(var1.getX() + var2.x, var1.getY() + var2.y);
               } else if (var1.getSource() == BasicSplitPaneDivider.this.hiddenDivider) {
                  var2 = BasicSplitPaneDivider.this.hiddenDivider.getLocation();
                  int var3 = var1.getX() + var2.x;
                  int var4 = var1.getY() + var2.y;
                  BasicSplitPaneDivider.this.dragger.continueDrag(var3, var4);
               }
            }

            var1.consume();
         }

      }

      public void mouseMoved(MouseEvent var1) {
      }

      public void mouseEntered(MouseEvent var1) {
         if (var1.getSource() == BasicSplitPaneDivider.this) {
            BasicSplitPaneDivider.this.setMouseOver(true);
         }

      }

      public void mouseExited(MouseEvent var1) {
         if (var1.getSource() == BasicSplitPaneDivider.this) {
            BasicSplitPaneDivider.this.setMouseOver(false);
         }

      }
   }
}
