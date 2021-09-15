package javax.swing;

import java.applet.Applet;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.IllegalComponentStateException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleStateSet;
import javax.swing.event.MenuDragMouseEvent;
import javax.swing.plaf.UIResource;
import javax.swing.text.View;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.reflect.misc.ReflectUtil;
import sun.security.action.GetPropertyAction;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class SwingUtilities implements SwingConstants {
   private static boolean canAccessEventQueue = false;
   private static boolean eventQueueTested = false;
   private static boolean suppressDropSupport;
   private static boolean checkedSuppressDropSupport;
   private static final Object sharedOwnerFrameKey = new StringBuffer("SwingUtilities.sharedOwnerFrame");

   private static boolean getSuppressDropTarget() {
      if (!checkedSuppressDropSupport) {
         suppressDropSupport = Boolean.valueOf((String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("suppressSwingDropSupport"))));
         checkedSuppressDropSupport = true;
      }

      return suppressDropSupport;
   }

   static void installSwingDropTargetAsNecessary(Component var0, TransferHandler var1) {
      if (!getSuppressDropTarget()) {
         DropTarget var2 = var0.getDropTarget();
         if (var2 == null || var2 instanceof UIResource) {
            if (var1 == null) {
               var0.setDropTarget((DropTarget)null);
            } else if (!GraphicsEnvironment.isHeadless()) {
               var0.setDropTarget(new TransferHandler.SwingDropTarget(var0));
            }
         }
      }

   }

   public static final boolean isRectangleContainingRectangle(Rectangle var0, Rectangle var1) {
      return var1.x >= var0.x && var1.x + var1.width <= var0.x + var0.width && var1.y >= var0.y && var1.y + var1.height <= var0.y + var0.height;
   }

   public static Rectangle getLocalBounds(Component var0) {
      Rectangle var1 = new Rectangle(var0.getBounds());
      var1.x = var1.y = 0;
      return var1;
   }

   public static Window getWindowAncestor(Component var0) {
      for(Container var1 = var0.getParent(); var1 != null; var1 = var1.getParent()) {
         if (var1 instanceof Window) {
            return (Window)var1;
         }
      }

      return null;
   }

   static Point convertScreenLocationToParent(Container var0, int var1, int var2) {
      for(Container var3 = var0; var3 != null; var3 = var3.getParent()) {
         if (var3 instanceof Window) {
            Point var4 = new Point(var1, var2);
            convertPointFromScreen(var4, var0);
            return var4;
         }
      }

      throw new Error("convertScreenLocationToParent: no window ancestor");
   }

   public static Point convertPoint(Component var0, Point var1, Component var2) {
      if (var0 == null && var2 == null) {
         return var1;
      } else {
         if (var0 == null) {
            var0 = getWindowAncestor((Component)var2);
            if (var0 == null) {
               throw new Error("Source component not connected to component tree hierarchy");
            }
         }

         Point var3 = new Point(var1);
         convertPointToScreen(var3, (Component)var0);
         if (var2 == null) {
            var2 = getWindowAncestor((Component)var0);
            if (var2 == null) {
               throw new Error("Destination component not connected to component tree hierarchy");
            }
         }

         convertPointFromScreen(var3, (Component)var2);
         return var3;
      }
   }

   public static Point convertPoint(Component var0, int var1, int var2, Component var3) {
      Point var4 = new Point(var1, var2);
      return convertPoint(var0, var4, var3);
   }

   public static Rectangle convertRectangle(Component var0, Rectangle var1, Component var2) {
      Point var3 = new Point(var1.x, var1.y);
      var3 = convertPoint(var0, var3, var2);
      return new Rectangle(var3.x, var3.y, var1.width, var1.height);
   }

   public static Container getAncestorOfClass(Class<?> var0, Component var1) {
      if (var1 != null && var0 != null) {
         Container var2;
         for(var2 = var1.getParent(); var2 != null && !var0.isInstance(var2); var2 = var2.getParent()) {
         }

         return var2;
      } else {
         return null;
      }
   }

   public static Container getAncestorNamed(String var0, Component var1) {
      if (var1 != null && var0 != null) {
         Container var2;
         for(var2 = var1.getParent(); var2 != null && !var0.equals(var2.getName()); var2 = var2.getParent()) {
         }

         return var2;
      } else {
         return null;
      }
   }

   public static Component getDeepestComponentAt(Component var0, int var1, int var2) {
      if (!var0.contains(var1, var2)) {
         return null;
      } else {
         if (var0 instanceof Container) {
            Component[] var3 = ((Container)var0).getComponents();
            Component[] var4 = var3;
            int var5 = var3.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               Component var7 = var4[var6];
               if (var7 != null && var7.isVisible()) {
                  Point var8 = var7.getLocation();
                  if (var7 instanceof Container) {
                     var7 = getDeepestComponentAt(var7, var1 - var8.x, var2 - var8.y);
                  } else {
                     var7 = var7.getComponentAt(var1 - var8.x, var2 - var8.y);
                  }

                  if (var7 != null && var7.isVisible()) {
                     return var7;
                  }
               }
            }
         }

         return var0;
      }
   }

   public static MouseEvent convertMouseEvent(Component var0, MouseEvent var1, Component var2) {
      Point var3 = convertPoint(var0, new Point(var1.getX(), var1.getY()), var2);
      Component var4;
      if (var2 != null) {
         var4 = var2;
      } else {
         var4 = var0;
      }

      Object var5;
      if (var1 instanceof MouseWheelEvent) {
         MouseWheelEvent var6 = (MouseWheelEvent)var1;
         var5 = new MouseWheelEvent(var4, var6.getID(), var6.getWhen(), var6.getModifiers() | var6.getModifiersEx(), var3.x, var3.y, var6.getXOnScreen(), var6.getYOnScreen(), var6.getClickCount(), var6.isPopupTrigger(), var6.getScrollType(), var6.getScrollAmount(), var6.getWheelRotation());
      } else if (var1 instanceof MenuDragMouseEvent) {
         MenuDragMouseEvent var7 = (MenuDragMouseEvent)var1;
         var5 = new MenuDragMouseEvent(var4, var7.getID(), var7.getWhen(), var7.getModifiers() | var7.getModifiersEx(), var3.x, var3.y, var7.getXOnScreen(), var7.getYOnScreen(), var7.getClickCount(), var7.isPopupTrigger(), var7.getPath(), var7.getMenuSelectionManager());
      } else {
         var5 = new MouseEvent(var4, var1.getID(), var1.getWhen(), var1.getModifiers() | var1.getModifiersEx(), var3.x, var3.y, var1.getXOnScreen(), var1.getYOnScreen(), var1.getClickCount(), var1.isPopupTrigger(), var1.getButton());
         AWTAccessor.MouseEventAccessor var8 = AWTAccessor.getMouseEventAccessor();
         var8.setCausedByTouchEvent((MouseEvent)var5, var8.isCausedByTouchEvent(var1));
      }

      return (MouseEvent)var5;
   }

   public static void convertPointToScreen(Point var0, Component var1) {
      while(true) {
         int var3;
         int var4;
         if (var1 instanceof JComponent) {
            var3 = ((Component)var1).getX();
            var4 = ((Component)var1).getY();
         } else if (!(var1 instanceof Applet) && !(var1 instanceof Window)) {
            var3 = ((Component)var1).getX();
            var4 = ((Component)var1).getY();
         } else {
            try {
               Point var5 = ((Component)var1).getLocationOnScreen();
               var3 = var5.x;
               var4 = var5.y;
            } catch (IllegalComponentStateException var6) {
               var3 = ((Component)var1).getX();
               var4 = ((Component)var1).getY();
            }
         }

         var0.x += var3;
         var0.y += var4;
         if (!(var1 instanceof Window) && !(var1 instanceof Applet)) {
            var1 = ((Component)var1).getParent();
            if (var1 != null) {
               continue;
            }
         }

         return;
      }
   }

   public static void convertPointFromScreen(Point var0, Component var1) {
      while(true) {
         int var3;
         int var4;
         if (var1 instanceof JComponent) {
            var3 = ((Component)var1).getX();
            var4 = ((Component)var1).getY();
         } else if (!(var1 instanceof Applet) && !(var1 instanceof Window)) {
            var3 = ((Component)var1).getX();
            var4 = ((Component)var1).getY();
         } else {
            try {
               Point var5 = ((Component)var1).getLocationOnScreen();
               var3 = var5.x;
               var4 = var5.y;
            } catch (IllegalComponentStateException var6) {
               var3 = ((Component)var1).getX();
               var4 = ((Component)var1).getY();
            }
         }

         var0.x -= var3;
         var0.y -= var4;
         if (!(var1 instanceof Window) && !(var1 instanceof Applet)) {
            var1 = ((Component)var1).getParent();
            if (var1 != null) {
               continue;
            }
         }

         return;
      }
   }

   public static Window windowForComponent(Component var0) {
      return getWindowAncestor(var0);
   }

   public static boolean isDescendingFrom(Component var0, Component var1) {
      if (var0 == var1) {
         return true;
      } else {
         for(Container var2 = var0.getParent(); var2 != null; var2 = var2.getParent()) {
            if (var2 == var1) {
               return true;
            }
         }

         return false;
      }
   }

   public static Rectangle computeIntersection(int var0, int var1, int var2, int var3, Rectangle var4) {
      int var5 = var0 > var4.x ? var0 : var4.x;
      int var6 = var0 + var2 < var4.x + var4.width ? var0 + var2 : var4.x + var4.width;
      int var7 = var1 > var4.y ? var1 : var4.y;
      int var8 = var1 + var3 < var4.y + var4.height ? var1 + var3 : var4.y + var4.height;
      var4.x = var5;
      var4.y = var7;
      var4.width = var6 - var5;
      var4.height = var8 - var7;
      if (var4.width < 0 || var4.height < 0) {
         var4.x = var4.y = var4.width = var4.height = 0;
      }

      return var4;
   }

   public static Rectangle computeUnion(int var0, int var1, int var2, int var3, Rectangle var4) {
      int var5 = var0 < var4.x ? var0 : var4.x;
      int var6 = var0 + var2 > var4.x + var4.width ? var0 + var2 : var4.x + var4.width;
      int var7 = var1 < var4.y ? var1 : var4.y;
      int var8 = var1 + var3 > var4.y + var4.height ? var1 + var3 : var4.y + var4.height;
      var4.x = var5;
      var4.y = var7;
      var4.width = var6 - var5;
      var4.height = var8 - var7;
      return var4;
   }

   public static Rectangle[] computeDifference(Rectangle var0, Rectangle var1) {
      if (var1 != null && var0.intersects(var1) && !isRectangleContainingRectangle(var1, var0)) {
         Rectangle var2 = new Rectangle();
         Rectangle var3 = null;
         Rectangle var4 = null;
         Rectangle var5 = null;
         Rectangle var6 = null;
         int var8 = 0;
         if (isRectangleContainingRectangle(var0, var1)) {
            var2.x = var0.x;
            var2.y = var0.y;
            var2.width = var1.x - var0.x;
            var2.height = var0.height;
            if (var2.width > 0 && var2.height > 0) {
               var3 = new Rectangle(var2);
               ++var8;
            }

            var2.x = var1.x;
            var2.y = var0.y;
            var2.width = var1.width;
            var2.height = var1.y - var0.y;
            if (var2.width > 0 && var2.height > 0) {
               var4 = new Rectangle(var2);
               ++var8;
            }

            var2.x = var1.x;
            var2.y = var1.y + var1.height;
            var2.width = var1.width;
            var2.height = var0.y + var0.height - (var1.y + var1.height);
            if (var2.width > 0 && var2.height > 0) {
               var5 = new Rectangle(var2);
               ++var8;
            }

            var2.x = var1.x + var1.width;
            var2.y = var0.y;
            var2.width = var0.x + var0.width - (var1.x + var1.width);
            var2.height = var0.height;
            if (var2.width > 0 && var2.height > 0) {
               var6 = new Rectangle(var2);
               ++var8;
            }
         } else if (var1.x <= var0.x && var1.y <= var0.y) {
            if (var1.x + var1.width > var0.x + var0.width) {
               var2.x = var0.x;
               var2.y = var1.y + var1.height;
               var2.width = var0.width;
               var2.height = var0.y + var0.height - (var1.y + var1.height);
               if (var2.width > 0 && var2.height > 0) {
                  var3 = var2;
                  ++var8;
               }
            } else if (var1.y + var1.height > var0.y + var0.height) {
               var2.setBounds(var1.x + var1.width, var0.y, var0.x + var0.width - (var1.x + var1.width), var0.height);
               if (var2.width > 0 && var2.height > 0) {
                  var3 = var2;
                  ++var8;
               }
            } else {
               var2.setBounds(var1.x + var1.width, var0.y, var0.x + var0.width - (var1.x + var1.width), var1.y + var1.height - var0.y);
               if (var2.width > 0 && var2.height > 0) {
                  var3 = new Rectangle(var2);
                  ++var8;
               }

               var2.setBounds(var0.x, var1.y + var1.height, var0.width, var0.y + var0.height - (var1.y + var1.height));
               if (var2.width > 0 && var2.height > 0) {
                  var4 = new Rectangle(var2);
                  ++var8;
               }
            }
         } else if (var1.x <= var0.x && var1.y + var1.height >= var0.y + var0.height) {
            if (var1.x + var1.width > var0.x + var0.width) {
               var2.setBounds(var0.x, var0.y, var0.width, var1.y - var0.y);
               if (var2.width > 0 && var2.height > 0) {
                  var3 = var2;
                  ++var8;
               }
            } else {
               var2.setBounds(var0.x, var0.y, var0.width, var1.y - var0.y);
               if (var2.width > 0 && var2.height > 0) {
                  var3 = new Rectangle(var2);
                  ++var8;
               }

               var2.setBounds(var1.x + var1.width, var1.y, var0.x + var0.width - (var1.x + var1.width), var0.y + var0.height - var1.y);
               if (var2.width > 0 && var2.height > 0) {
                  var4 = new Rectangle(var2);
                  ++var8;
               }
            }
         } else if (var1.x <= var0.x) {
            if (var1.x + var1.width >= var0.x + var0.width) {
               var2.setBounds(var0.x, var0.y, var0.width, var1.y - var0.y);
               if (var2.width > 0 && var2.height > 0) {
                  var3 = new Rectangle(var2);
                  ++var8;
               }

               var2.setBounds(var0.x, var1.y + var1.height, var0.width, var0.y + var0.height - (var1.y + var1.height));
               if (var2.width > 0 && var2.height > 0) {
                  var4 = new Rectangle(var2);
                  ++var8;
               }
            } else {
               var2.setBounds(var0.x, var0.y, var0.width, var1.y - var0.y);
               if (var2.width > 0 && var2.height > 0) {
                  var3 = new Rectangle(var2);
                  ++var8;
               }

               var2.setBounds(var1.x + var1.width, var1.y, var0.x + var0.width - (var1.x + var1.width), var1.height);
               if (var2.width > 0 && var2.height > 0) {
                  var4 = new Rectangle(var2);
                  ++var8;
               }

               var2.setBounds(var0.x, var1.y + var1.height, var0.width, var0.y + var0.height - (var1.y + var1.height));
               if (var2.width > 0 && var2.height > 0) {
                  var5 = new Rectangle(var2);
                  ++var8;
               }
            }
         } else if (var1.x <= var0.x + var0.width && var1.x + var1.width > var0.x + var0.width) {
            if (var1.y <= var0.y && var1.y + var1.height > var0.y + var0.height) {
               var2.setBounds(var0.x, var0.y, var1.x - var0.x, var0.height);
               if (var2.width > 0 && var2.height > 0) {
                  var3 = var2;
                  ++var8;
               }
            } else if (var1.y <= var0.y) {
               var2.setBounds(var0.x, var0.y, var1.x - var0.x, var1.y + var1.height - var0.y);
               if (var2.width > 0 && var2.height > 0) {
                  var3 = new Rectangle(var2);
                  ++var8;
               }

               var2.setBounds(var0.x, var1.y + var1.height, var0.width, var0.y + var0.height - (var1.y + var1.height));
               if (var2.width > 0 && var2.height > 0) {
                  var4 = new Rectangle(var2);
                  ++var8;
               }
            } else if (var1.y + var1.height > var0.y + var0.height) {
               var2.setBounds(var0.x, var0.y, var0.width, var1.y - var0.y);
               if (var2.width > 0 && var2.height > 0) {
                  var3 = new Rectangle(var2);
                  ++var8;
               }

               var2.setBounds(var0.x, var1.y, var1.x - var0.x, var0.y + var0.height - var1.y);
               if (var2.width > 0 && var2.height > 0) {
                  var4 = new Rectangle(var2);
                  ++var8;
               }
            } else {
               var2.setBounds(var0.x, var0.y, var0.width, var1.y - var0.y);
               if (var2.width > 0 && var2.height > 0) {
                  var3 = new Rectangle(var2);
                  ++var8;
               }

               var2.setBounds(var0.x, var1.y, var1.x - var0.x, var1.height);
               if (var2.width > 0 && var2.height > 0) {
                  var4 = new Rectangle(var2);
                  ++var8;
               }

               var2.setBounds(var0.x, var1.y + var1.height, var0.width, var0.y + var0.height - (var1.y + var1.height));
               if (var2.width > 0 && var2.height > 0) {
                  var5 = new Rectangle(var2);
                  ++var8;
               }
            }
         } else if (var1.x >= var0.x && var1.x + var1.width <= var0.x + var0.width) {
            if (var1.y <= var0.y && var1.y + var1.height > var0.y + var0.height) {
               var2.setBounds(var0.x, var0.y, var1.x - var0.x, var0.height);
               if (var2.width > 0 && var2.height > 0) {
                  var3 = new Rectangle(var2);
                  ++var8;
               }

               var2.setBounds(var1.x + var1.width, var0.y, var0.x + var0.width - (var1.x + var1.width), var0.height);
               if (var2.width > 0 && var2.height > 0) {
                  var4 = new Rectangle(var2);
                  ++var8;
               }
            } else if (var1.y <= var0.y) {
               var2.setBounds(var0.x, var0.y, var1.x - var0.x, var0.height);
               if (var2.width > 0 && var2.height > 0) {
                  var3 = new Rectangle(var2);
                  ++var8;
               }

               var2.setBounds(var1.x, var1.y + var1.height, var1.width, var0.y + var0.height - (var1.y + var1.height));
               if (var2.width > 0 && var2.height > 0) {
                  var4 = new Rectangle(var2);
                  ++var8;
               }

               var2.setBounds(var1.x + var1.width, var0.y, var0.x + var0.width - (var1.x + var1.width), var0.height);
               if (var2.width > 0 && var2.height > 0) {
                  var5 = new Rectangle(var2);
                  ++var8;
               }
            } else {
               var2.setBounds(var0.x, var0.y, var1.x - var0.x, var0.height);
               if (var2.width > 0 && var2.height > 0) {
                  var3 = new Rectangle(var2);
                  ++var8;
               }

               var2.setBounds(var1.x, var0.y, var1.width, var1.y - var0.y);
               if (var2.width > 0 && var2.height > 0) {
                  var4 = new Rectangle(var2);
                  ++var8;
               }

               var2.setBounds(var1.x + var1.width, var0.y, var0.x + var0.width - (var1.x + var1.width), var0.height);
               if (var2.width > 0 && var2.height > 0) {
                  var5 = new Rectangle(var2);
                  ++var8;
               }
            }
         }

         Rectangle[] var7 = new Rectangle[var8];
         var8 = 0;
         if (var3 != null) {
            var7[var8++] = var3;
         }

         if (var4 != null) {
            var7[var8++] = var4;
         }

         if (var5 != null) {
            var7[var8++] = var5;
         }

         if (var6 != null) {
            var7[var8++] = var6;
         }

         return var7;
      } else {
         return new Rectangle[0];
      }
   }

   public static boolean isLeftMouseButton(MouseEvent var0) {
      return (var0.getModifiersEx() & 1024) != 0 || var0.getButton() == 1;
   }

   public static boolean isMiddleMouseButton(MouseEvent var0) {
      return (var0.getModifiersEx() & 2048) != 0 || var0.getButton() == 2;
   }

   public static boolean isRightMouseButton(MouseEvent var0) {
      return (var0.getModifiersEx() & 4096) != 0 || var0.getButton() == 3;
   }

   public static int computeStringWidth(FontMetrics var0, String var1) {
      return SwingUtilities2.stringWidth((JComponent)null, var0, var1);
   }

   public static String layoutCompoundLabel(JComponent var0, FontMetrics var1, String var2, Icon var3, int var4, int var5, int var6, int var7, Rectangle var8, Rectangle var9, Rectangle var10, int var11) {
      boolean var12 = true;
      int var13 = var5;
      int var14 = var7;
      if (var0 != null && !var0.getComponentOrientation().isLeftToRight()) {
         var12 = false;
      }

      switch(var5) {
      case 10:
         var13 = var12 ? 2 : 4;
         break;
      case 11:
         var13 = var12 ? 4 : 2;
      }

      switch(var7) {
      case 10:
         var14 = var12 ? 2 : 4;
         break;
      case 11:
         var14 = var12 ? 4 : 2;
      }

      return layoutCompoundLabelImpl(var0, var1, var2, var3, var4, var13, var6, var14, var8, var9, var10, var11);
   }

   public static String layoutCompoundLabel(FontMetrics var0, String var1, Icon var2, int var3, int var4, int var5, int var6, Rectangle var7, Rectangle var8, Rectangle var9, int var10) {
      return layoutCompoundLabelImpl((JComponent)null, var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   private static String layoutCompoundLabelImpl(JComponent var0, FontMetrics var1, String var2, Icon var3, int var4, int var5, int var6, int var7, Rectangle var8, Rectangle var9, Rectangle var10, int var11) {
      if (var3 != null) {
         var9.width = var3.getIconWidth();
         var9.height = var3.getIconHeight();
      } else {
         var9.width = var9.height = 0;
      }

      boolean var12 = var2 == null || var2.equals("");
      int var13 = 0;
      byte var14 = 0;
      int var15;
      int var17;
      if (var12) {
         var10.width = var10.height = 0;
         var2 = "";
         var15 = 0;
      } else {
         var15 = var3 == null ? 0 : var11;
         if (var7 == 0) {
            var17 = var8.width;
         } else {
            var17 = var8.width - (var9.width + var15);
         }

         View var16 = var0 != null ? (View)var0.getClientProperty("html") : null;
         if (var16 != null) {
            var10.width = Math.min(var17, (int)var16.getPreferredSpan(0));
            var10.height = (int)var16.getPreferredSpan(1);
         } else {
            var10.width = SwingUtilities2.stringWidth(var0, var1, var2);
            var13 = SwingUtilities2.getLeftSideBearing(var0, var1, var2);
            if (var13 < 0) {
               var10.width -= var13;
            }

            if (var10.width > var17) {
               var2 = SwingUtilities2.clipString(var0, var1, var2, var17);
               var10.width = SwingUtilities2.stringWidth(var0, var1, var2);
            }

            var10.height = var1.getHeight();
         }
      }

      if (var6 == 1) {
         if (var7 != 0) {
            var10.y = 0;
         } else {
            var10.y = -(var10.height + var15);
         }
      } else if (var6 == 0) {
         var10.y = var9.height / 2 - var10.height / 2;
      } else if (var7 != 0) {
         var10.y = var9.height - var10.height;
      } else {
         var10.y = var9.height + var15;
      }

      if (var7 == 2) {
         var10.x = -(var10.width + var15);
      } else if (var7 == 0) {
         var10.x = var9.width / 2 - var10.width / 2;
      } else {
         var10.x = var9.width + var15;
      }

      var17 = Math.min(var9.x, var10.x);
      int var18 = Math.max(var9.x + var9.width, var10.x + var10.width) - var17;
      int var19 = Math.min(var9.y, var10.y);
      int var20 = Math.max(var9.y + var9.height, var10.y + var10.height) - var19;
      int var22;
      if (var4 == 1) {
         var22 = var8.y - var19;
      } else if (var4 == 0) {
         var22 = var8.y + var8.height / 2 - (var19 + var20 / 2);
      } else {
         var22 = var8.y + var8.height - (var19 + var20);
      }

      int var21;
      if (var5 == 2) {
         var21 = var8.x - var17;
      } else if (var5 == 4) {
         var21 = var8.x + var8.width - (var17 + var18);
      } else {
         var21 = var8.x + var8.width / 2 - (var17 + var18 / 2);
      }

      var10.x += var21;
      var10.y += var22;
      var9.x += var21;
      var9.y += var22;
      if (var13 < 0) {
         var10.x -= var13;
         var10.width += var13;
      }

      if (var14 > 0) {
         var10.width -= var14;
      }

      return var2;
   }

   public static void paintComponent(Graphics var0, Component var1, Container var2, int var3, int var4, int var5, int var6) {
      getCellRendererPane(var1, var2).paintComponent(var0, var1, var2, var3, var4, var5, var6, false);
   }

   public static void paintComponent(Graphics var0, Component var1, Container var2, Rectangle var3) {
      paintComponent(var0, var1, var2, var3.x, var3.y, var3.width, var3.height);
   }

   private static CellRendererPane getCellRendererPane(Component var0, Container var1) {
      Object var2 = var0.getParent();
      if (var2 instanceof CellRendererPane) {
         if (((Container)var2).getParent() != var1) {
            var1.add((Component)var2);
         }
      } else {
         var2 = new CellRendererPane();
         ((Container)var2).add(var0);
         var1.add((Component)var2);
      }

      return (CellRendererPane)var2;
   }

   public static void updateComponentTreeUI(Component var0) {
      updateComponentTreeUI0(var0);
      var0.invalidate();
      var0.validate();
      var0.repaint();
   }

   private static void updateComponentTreeUI0(Component var0) {
      if (var0 instanceof JComponent) {
         JComponent var1 = (JComponent)var0;
         var1.updateUI();
         JPopupMenu var2 = var1.getComponentPopupMenu();
         if (var2 != null) {
            updateComponentTreeUI(var2);
         }
      }

      Component[] var6 = null;
      if (var0 instanceof JMenu) {
         var6 = ((JMenu)var0).getMenuComponents();
      } else if (var0 instanceof Container) {
         var6 = ((Container)var0).getComponents();
      }

      if (var6 != null) {
         Component[] var7 = var6;
         int var3 = var6.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Component var5 = var7[var4];
            updateComponentTreeUI0(var5);
         }
      }

   }

   public static void invokeLater(Runnable var0) {
      EventQueue.invokeLater(var0);
   }

   public static void invokeAndWait(Runnable var0) throws InterruptedException, InvocationTargetException {
      EventQueue.invokeAndWait(var0);
   }

   public static boolean isEventDispatchThread() {
      return EventQueue.isDispatchThread();
   }

   public static int getAccessibleIndexInParent(Component var0) {
      return var0.getAccessibleContext().getAccessibleIndexInParent();
   }

   public static Accessible getAccessibleAt(Component var0, Point var1) {
      if (var0 instanceof Container) {
         return var0.getAccessibleContext().getAccessibleComponent().getAccessibleAt(var1);
      } else if (!(var0 instanceof Accessible)) {
         return null;
      } else {
         Accessible var2 = (Accessible)var0;
         if (var2 != null) {
            AccessibleContext var3 = var2.getAccessibleContext();
            if (var3 != null) {
               int var6 = var3.getAccessibleChildrenCount();

               for(int var7 = 0; var7 < var6; ++var7) {
                  var2 = var3.getAccessibleChild(var7);
                  if (var2 != null) {
                     var3 = var2.getAccessibleContext();
                     if (var3 != null) {
                        AccessibleComponent var4 = var3.getAccessibleComponent();
                        if (var4 != null && var4.isShowing()) {
                           Point var5 = var4.getLocation();
                           Point var8 = new Point(var1.x - var5.x, var1.y - var5.y);
                           if (var4.contains(var8)) {
                              return var2;
                           }
                        }
                     }
                  }
               }
            }
         }

         return (Accessible)var0;
      }
   }

   public static AccessibleStateSet getAccessibleStateSet(Component var0) {
      return var0.getAccessibleContext().getAccessibleStateSet();
   }

   public static int getAccessibleChildrenCount(Component var0) {
      return var0.getAccessibleContext().getAccessibleChildrenCount();
   }

   public static Accessible getAccessibleChild(Component var0, int var1) {
      return var0.getAccessibleContext().getAccessibleChild(var1);
   }

   /** @deprecated */
   @Deprecated
   public static Component findFocusOwner(Component var0) {
      Component var1 = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();

      for(Object var2 = var1; var2 != null; var2 = var2 instanceof Window ? null : ((Component)var2).getParent()) {
         if (var2 == var0) {
            return var1;
         }
      }

      return null;
   }

   public static JRootPane getRootPane(Component var0) {
      if (var0 instanceof RootPaneContainer) {
         return ((RootPaneContainer)var0).getRootPane();
      } else {
         while(var0 != null) {
            if (var0 instanceof JRootPane) {
               return (JRootPane)var0;
            }

            var0 = ((Component)var0).getParent();
         }

         return null;
      }
   }

   public static Component getRoot(Component var0) {
      Object var1 = null;

      for(Object var2 = var0; var2 != null; var2 = ((Component)var2).getParent()) {
         if (var2 instanceof Window) {
            return (Component)var2;
         }

         if (var2 instanceof Applet) {
            var1 = var2;
         }
      }

      return (Component)var1;
   }

   static JComponent getPaintingOrigin(JComponent var0) {
      Object var1 = var0;

      JComponent var2;
      do {
         if (!((var1 = ((Container)var1).getParent()) instanceof JComponent)) {
            return null;
         }

         var2 = (JComponent)var1;
      } while(!var2.isPaintingOrigin());

      return var2;
   }

   public static boolean processKeyBindings(KeyEvent var0) {
      if (var0 != null) {
         if (var0.isConsumed()) {
            return false;
         }

         Object var1 = var0.getComponent();
         boolean var2 = var0.getID() == 401;
         if (!isValidKeyEventForKeyBindings(var0)) {
            return false;
         }

         while(var1 != null) {
            if (var1 instanceof JComponent) {
               return ((JComponent)var1).processKeyBindings(var0, var2);
            }

            if (var1 instanceof Applet || var1 instanceof Window) {
               return JComponent.processKeyBindingsForAllComponents(var0, (Container)var1, var2);
            }

            var1 = ((Component)var1).getParent();
         }
      }

      return false;
   }

   static boolean isValidKeyEventForKeyBindings(KeyEvent var0) {
      return true;
   }

   public static boolean notifyAction(Action var0, KeyStroke var1, KeyEvent var2, Object var3, int var4) {
      if (var0 == null) {
         return false;
      } else {
         if (var0 instanceof UIAction) {
            if (!((UIAction)var0).isEnabled(var3)) {
               return false;
            }
         } else if (!var0.isEnabled()) {
            return false;
         }

         Object var5 = var0.getValue("ActionCommandKey");
         boolean var6;
         if (var5 == null && var0 instanceof JComponent.ActionStandin) {
            var6 = true;
         } else {
            var6 = false;
         }

         String var7;
         if (var5 != null) {
            var7 = var5.toString();
         } else if (!var6 && var2.getKeyChar() != '\uffff') {
            var7 = String.valueOf(var2.getKeyChar());
         } else {
            var7 = null;
         }

         var0.actionPerformed(new ActionEvent(var3, 1001, var7, var2.getWhen(), var4));
         return true;
      }
   }

   public static void replaceUIInputMap(JComponent var0, int var1, InputMap var2) {
      InputMap var4;
      for(InputMap var3 = var0.getInputMap(var1, var2 != null); var3 != null; var3 = var4) {
         var4 = var3.getParent();
         if (var4 == null || var4 instanceof UIResource) {
            var3.setParent(var2);
            return;
         }
      }

   }

   public static void replaceUIActionMap(JComponent var0, ActionMap var1) {
      ActionMap var3;
      for(ActionMap var2 = var0.getActionMap(var1 != null); var2 != null; var2 = var3) {
         var3 = var2.getParent();
         if (var3 == null || var3 instanceof UIResource) {
            var2.setParent(var1);
            return;
         }
      }

   }

   public static InputMap getUIInputMap(JComponent var0, int var1) {
      InputMap var3;
      for(InputMap var2 = var0.getInputMap(var1, false); var2 != null; var2 = var3) {
         var3 = var2.getParent();
         if (var3 instanceof UIResource) {
            return var3;
         }
      }

      return null;
   }

   public static ActionMap getUIActionMap(JComponent var0) {
      ActionMap var2;
      for(ActionMap var1 = var0.getActionMap(false); var1 != null; var1 = var2) {
         var2 = var1.getParent();
         if (var2 instanceof UIResource) {
            return var2;
         }
      }

      return null;
   }

   static Frame getSharedOwnerFrame() throws HeadlessException {
      Object var0 = (Frame)appContextGet(sharedOwnerFrameKey);
      if (var0 == null) {
         var0 = new SwingUtilities.SharedOwnerFrame();
         appContextPut(sharedOwnerFrameKey, var0);
      }

      return (Frame)var0;
   }

   static WindowListener getSharedOwnerFrameShutdownListener() throws HeadlessException {
      Frame var0 = getSharedOwnerFrame();
      return (WindowListener)var0;
   }

   static Object appContextGet(Object var0) {
      return AppContext.getAppContext().get(var0);
   }

   static void appContextPut(Object var0, Object var1) {
      AppContext.getAppContext().put(var0, var1);
   }

   static void appContextRemove(Object var0) {
      AppContext.getAppContext().remove(var0);
   }

   static Class<?> loadSystemClass(String var0) throws ClassNotFoundException {
      ReflectUtil.checkPackageAccess(var0);
      return Class.forName(var0, true, Thread.currentThread().getContextClassLoader());
   }

   static boolean isLeftToRight(Component var0) {
      return var0.getComponentOrientation().isLeftToRight();
   }

   private SwingUtilities() {
      throw new Error("SwingUtilities is just a container for static methods");
   }

   static boolean doesIconReferenceImage(Icon var0, Image var1) {
      Image var2 = var0 != null && var0 instanceof ImageIcon ? ((ImageIcon)var0).getImage() : null;
      return var2 == var1;
   }

   static int findDisplayedMnemonicIndex(String var0, int var1) {
      if (var0 != null && var1 != 0) {
         char var2 = Character.toUpperCase((char)var1);
         char var3 = Character.toLowerCase((char)var1);
         int var4 = var0.indexOf(var2);
         int var5 = var0.indexOf(var3);
         if (var4 == -1) {
            return var5;
         } else if (var5 == -1) {
            return var4;
         } else {
            return var5 < var4 ? var5 : var4;
         }
      } else {
         return -1;
      }
   }

   public static Rectangle calculateInnerArea(JComponent var0, Rectangle var1) {
      if (var0 == null) {
         return null;
      } else {
         Rectangle var2 = var1;
         Insets var3 = var0.getInsets();
         if (var1 == null) {
            var2 = new Rectangle();
         }

         var2.x = var3.left;
         var2.y = var3.top;
         var2.width = var0.getWidth() - var3.left - var3.right;
         var2.height = var0.getHeight() - var3.top - var3.bottom;
         return var2;
      }
   }

   static void updateRendererOrEditorUI(Object var0) {
      if (var0 != null) {
         Component var1 = null;
         if (var0 instanceof Component) {
            var1 = (Component)var0;
         }

         if (var0 instanceof DefaultCellEditor) {
            var1 = ((DefaultCellEditor)var0).getComponent();
         }

         if (var1 != null) {
            updateComponentTreeUI(var1);
         }

      }
   }

   public static Container getUnwrappedParent(Component var0) {
      Container var1;
      for(var1 = var0.getParent(); var1 instanceof JLayer; var1 = var1.getParent()) {
      }

      return var1;
   }

   public static Component getUnwrappedView(JViewport var0) {
      Component var1;
      for(var1 = var0.getView(); var1 instanceof JLayer; var1 = ((JLayer)var1).getView()) {
      }

      return var1;
   }

   static Container getValidateRoot(Container var0, boolean var1) {
      Container var2 = null;

      while(true) {
         if (var0 != null) {
            if (!var0.isDisplayable() || var0 instanceof CellRendererPane) {
               return null;
            }

            if (!var0.isValidateRoot()) {
               var0 = var0.getParent();
               continue;
            }

            var2 = var0;
         }

         if (var2 == null) {
            return null;
         }

         while(var0 != null) {
            if (!var0.isDisplayable() || var1 && !var0.isVisible()) {
               return null;
            }

            if (var0 instanceof Window || var0 instanceof Applet) {
               return var2;
            }

            var0 = var0.getParent();
         }

         return null;
      }
   }

   static class SharedOwnerFrame extends Frame implements WindowListener {
      public void addNotify() {
         super.addNotify();
         this.installListeners();
      }

      void installListeners() {
         Window[] var1 = this.getOwnedWindows();
         Window[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Window var5 = var2[var4];
            if (var5 != null) {
               var5.removeWindowListener(this);
               var5.addWindowListener(this);
            }
         }

      }

      public void windowClosed(WindowEvent var1) {
         synchronized(this.getTreeLock()) {
            Window[] var3 = this.getOwnedWindows();
            Window[] var4 = var3;
            int var5 = var3.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               Window var7 = var4[var6];
               if (var7 != null) {
                  if (var7.isDisplayable()) {
                     return;
                  }

                  var7.removeWindowListener(this);
               }
            }

            this.dispose();
         }
      }

      public void windowOpened(WindowEvent var1) {
      }

      public void windowClosing(WindowEvent var1) {
      }

      public void windowIconified(WindowEvent var1) {
      }

      public void windowDeiconified(WindowEvent var1) {
      }

      public void windowActivated(WindowEvent var1) {
      }

      public void windowDeactivated(WindowEvent var1) {
      }

      public void show() {
      }

      public void dispose() {
         try {
            this.getToolkit().getSystemEventQueue();
            super.dispose();
         } catch (Exception var2) {
         }

      }
   }
}
