package sun.lwawt.macosx;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleText;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import sun.awt.dnd.SunDragSourceContextPeer;
import sun.awt.dnd.SunDropTargetContextPeer;
import sun.lwawt.LWComponentPeer;
import sun.lwawt.LWWindowPeer;
import sun.lwawt.PlatformWindow;
import sun.security.action.GetPropertyAction;

public final class CDragSourceContextPeer extends SunDragSourceContextPeer {
   private static final CDragSourceContextPeer fInstance = new CDragSourceContextPeer((DragGestureEvent)null);
   private Image fDragImage;
   private CImage fDragCImage;
   private Point fDragImageOffset;
   private static Component hoveringComponent = null;
   private static double fMaxImageSize = 128.0D;

   private CDragSourceContextPeer(DragGestureEvent var1) {
      super(var1);
   }

   public static CDragSourceContextPeer createDragSourceContextPeer(DragGestureEvent var0) throws InvalidDnDOperationException {
      fInstance.setTrigger(var0);
      return fInstance;
   }

   public void startDrag(DragSourceContext var1, Cursor var2, Image var3, Point var4) throws InvalidDnDOperationException {
      this.fDragImage = var3;
      this.fDragImageOffset = var4;
      super.startDrag(var1, var2, var3, var4);
   }

   protected void startDrag(Transferable var1, long[] var2, Map var3) {
      DragGestureEvent var4 = this.getTrigger();
      InputEvent var5 = var4.getTriggerEvent();
      Point var6 = new Point(var4.getDragOrigin());
      int var7 = var5.getModifiers() | var5.getModifiersEx();
      long var8 = var5.getWhen();
      int var10 = var5 instanceof MouseEvent ? ((MouseEvent)var5).getClickCount() : 1;
      Component var11 = var4.getComponent();
      Point var12 = var11.getLocation();

      Object var13;
      for(var13 = var11; !(var13 instanceof Window); var12 = ((Component)var13).getLocation()) {
         var6.translate(var12.x, var12.y);
         var13 = ((Component)var13).getParent();
      }

      if (this.fDragImage == null) {
         this.setDefaultDragImage(var11);
      }

      Point var14;
      if (this.fDragImage != null) {
         try {
            this.fDragCImage = CImage.getCreator().createFromImageImmediately(this.fDragImage);
         } catch (Exception var21) {
            throw new InvalidDnDOperationException("Drag image can not be created.");
         }

         if (this.fDragCImage == null) {
            throw new InvalidDnDOperationException("Drag image is not ready.");
         }

         var14 = this.fDragImageOffset;
      } else {
         this.fDragCImage = null;
         var14 = new Point(0, 0);
      }

      long var16;
      try {
         PlatformWindow var15 = ((LWComponentPeer)((Component)var13).getPeer()).getPlatformWindow();
         var16 = CPlatformWindow.getNativeViewPtr(var15);
         if (var16 == 0L) {
            throw new InvalidDnDOperationException("Unsupported platform window implementation");
         }

         long var18 = this.createNativeDragSource(var11, var16, var1, var5, (int)var6.getX(), (int)var6.getY(), var7, var10, var8, this.fDragCImage, var14.x, var14.y, this.getDragSourceContext().getSourceActions(), var2, var3);
         if (var18 == 0L) {
            throw new InvalidDnDOperationException("");
         }

         this.setNativeContext(var18);
      } catch (Exception var22) {
         throw new InvalidDnDOperationException("failed to create native peer: " + var22);
      }

      SunDropTargetContextPeer.setCurrentJVMLocalSourceTransferable(var1);
      CCursorManager.getInstance().setCursor(this.getCursor());

      try {
         Thread var23 = new Thread() {
            public void run() {
               long var1 = CDragSourceContextPeer.this.getNativeContext();

               try {
                  CDragSourceContextPeer.this.doDragging(var1);
               } catch (Exception var7) {
                  var7.printStackTrace();
               } finally {
                  CDragSourceContextPeer.this.releaseNativeDragSource(var1);
                  CDragSourceContextPeer.this.fDragImage = null;
                  if (CDragSourceContextPeer.this.fDragCImage != null) {
                     CDragSourceContextPeer.this.fDragCImage.dispose();
                     CDragSourceContextPeer.this.fDragCImage = null;
                  }

               }

            }
         };
         var23.start();
      } catch (Exception var20) {
         var16 = this.getNativeContext();
         this.setNativeContext(0L);
         this.releaseNativeDragSource(var16);
         SunDropTargetContextPeer.setCurrentJVMLocalSourceTransferable((Transferable)null);
         throw new InvalidDnDOperationException("failed to start dragging thread: " + var20);
      }
   }

   private void setDefaultDragImage(Component var1) {
      boolean var2 = false;
      if (var1.isLightweight()) {
         if (var1 instanceof JTextComponent) {
            this.setDefaultDragImage((JTextComponent)var1);
            var2 = true;
         } else if (var1 instanceof JTree) {
            this.setDefaultDragImage((JTree)var1);
            var2 = true;
         } else if (var1 instanceof JTable) {
            this.setDefaultDragImage((JTable)var1);
            var2 = true;
         } else if (var1 instanceof JList) {
            this.setDefaultDragImage((JList)var1);
            var2 = true;
         }
      }

      if (!var2) {
         this.setDefaultDragImage();
      }

   }

   private void setDefaultDragImage(JTextComponent var1) {
      DragGestureEvent var2 = this.getTrigger();
      int var3 = var1.getSelectionStart();
      int var4 = var1.getSelectionEnd();
      boolean var5 = false;
      int var6 = var1.viewToModel(var2.getDragOrigin());
      if (var3 < var4 && var6 >= var3 && var6 <= var4) {
         try {
            Rectangle var7 = var1.modelToView(var3);
            Rectangle var8 = var1.modelToView(var4);
            Rectangle var9 = null;
            if (var7.y == var8.y) {
               var9 = new Rectangle(var7.x, var7.y, var8.x - var7.x + var8.width, var8.y - var7.y + var8.height);
            } else {
               AccessibleContext var10 = var1.getAccessibleContext();
               AccessibleText var11 = (AccessibleText)var10;
               var9 = var1.modelToView(var3);

               for(int var12 = var3 + 1; var12 <= var4; ++var12) {
                  Rectangle var13 = var11.getCharacterBounds(var12);
                  if (var13 != null) {
                     var9.add(var13);
                  }
               }
            }

            this.setOutlineDragImage(var9);
            var5 = true;
         } catch (BadLocationException var14) {
         }
      }

      if (!var5) {
         this.setDefaultDragImage();
      }

   }

   private void setDefaultDragImage(JTree var1) {
      Rectangle var2 = null;
      int[] var3 = var1.getSelectionRows();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         Rectangle var5 = var1.getRowBounds(var3[var4]);
         if (var2 == null) {
            var2 = var5;
         } else {
            var2.add(var5);
         }
      }

      if (var2 != null) {
         this.setOutlineDragImage(var2);
      } else {
         this.setDefaultDragImage();
      }

   }

   private void setDefaultDragImage(JTable var1) {
      Rectangle var2 = null;
      int[] var3 = var1.getSelectedRows();
      int[] var4 = var1.getSelectedColumns();

      for(int var5 = 0; var5 < var3.length; ++var5) {
         for(int var6 = 0; var6 < var4.length; ++var6) {
            Rectangle var7 = var1.getCellRect(var3[var5], var4[var6], true);
            if (var2 == null) {
               var2 = var7;
            } else {
               var2.add(var7);
            }
         }
      }

      if (var2 != null) {
         this.setOutlineDragImage(var2);
      } else {
         this.setDefaultDragImage();
      }

   }

   private void setDefaultDragImage(JList var1) {
      Rectangle var2 = null;
      int[] var3 = var1.getSelectedIndices();
      if (var3.length > 0) {
         var2 = var1.getCellBounds(var3[0], var3[var3.length - 1]);
      }

      if (var2 != null) {
         this.setOutlineDragImage(var2);
      } else {
         this.setDefaultDragImage();
      }

   }

   private void setDefaultDragImage() {
      DragGestureEvent var1 = this.getTrigger();
      Component var2 = var1.getComponent();
      this.setOutlineDragImage(new Rectangle(0, 0, var2.getWidth(), var2.getHeight()), true);
   }

   private void setOutlineDragImage(Rectangle var1) {
      this.setOutlineDragImage(var1, false);
   }

   private void setOutlineDragImage(Rectangle var1, Boolean var2) {
      int var3 = (int)var1.getWidth();
      int var4 = (int)var1.getHeight();
      double var5 = 1.0D;
      if (var2) {
         int var7 = var3 * var4;
         int var8 = (int)(fMaxImageSize * fMaxImageSize);
         if (var7 > var8) {
            var5 = (double)var7 / (double)var8;
            var3 = (int)((double)var3 / var5);
            var4 = (int)((double)var4 / var5);
         }
      }

      if (var3 <= 0) {
         var3 = 1;
      }

      if (var4 <= 0) {
         var4 = 1;
      }

      DragGestureEvent var18 = this.getTrigger();
      Component var19 = var18.getComponent();
      Point var9 = var19.getLocation();
      if (var19 instanceof JComponent) {
         Rectangle var10 = ((JComponent)var19).getVisibleRect();
         Rectangle var11 = var1.intersection(var10);
         if (!var11.isEmpty()) {
            var1 = var11;
         }

         var1.translate(var9.x, var9.y);
      }

      GraphicsConfiguration var20 = var19.getGraphicsConfiguration();
      BufferedImage var21 = var20.createCompatibleImage(var3, var4, 3);
      Color var12 = Color.gray;
      BasicStroke var13 = new BasicStroke(2.0F);
      int var14 = (int)(var13.getLineWidth() + 1.0F) / 2;
      Graphics2D var15 = (Graphics2D)var21.getGraphics();
      var15.setPaint(var12);
      var15.setStroke(var13);
      var15.drawRect(var14, var14, var3 - 2 * var14 - 1, var4 - 2 * var14 - 1);
      var15.dispose();
      this.fDragImage = var21;
      Point var16 = var18.getDragOrigin();
      Point var17 = new Point(var1.x - var16.x, var1.y - var16.y);
      if (var19 instanceof JComponent) {
         var17.translate(-var9.x, -var9.y);
      }

      if (var2) {
         var17.x = (int)((double)var17.x / var5);
         var17.y = (int)((double)var17.y / var5);
      }

      this.fDragImageOffset = var17;
   }

   private void dragMouseMoved(int var1, int var2, final int var3, final int var4) {
      try {
         Component var5 = (Component)LWCToolkit.invokeAndWait(new Callable<Component>() {
            public Component call() {
               LWWindowPeer var1 = LWWindowPeer.getWindowUnderCursor();
               if (var1 == null) {
                  return null;
               } else {
                  Component var2 = SwingUtilities.getRoot(var1.getTarget());
                  if (var2 == null) {
                     return null;
                  } else {
                     Point var3x = var2.getLocationOnScreen();
                     return CDragSourceContextPeer.getDropTargetAt(var2, var3 - var3x.x, var4 - var3x.y);
                  }
               }
            }
         }, this.getComponent());
         if (var5 != hoveringComponent) {
            if (hoveringComponent != null) {
               this.dragExit(var3, var4);
            }

            if (var5 != null) {
               this.dragEnter(var1, var2, var3, var4);
            }

            hoveringComponent = var5;
         }

         this.postDragSourceDragEvent(var1, var2, var3, var4, 6);
      } catch (Exception var6) {
         throw new InvalidDnDOperationException("Failed to handle DragMouseMoved event");
      }
   }

   private static Component getDropTargetAt(Component var0, int var1, int var2) {
      if (var0.contains(var1, var2) && var0.isEnabled() && var0.isVisible()) {
         if (var0.getDropTarget() != null && var0.getDropTarget().isActive()) {
            return var0;
         } else {
            if (var0 instanceof Container) {
               Component[] var3 = ((Container)var0).getComponents();
               int var4 = var3.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  Component var6 = var3[var5];
                  Point var7 = var6.getLocation();
                  Component var8 = getDropTargetAt(var6, var1 - var7.x, var2 - var7.y);
                  if (var8 != null) {
                     return var8;
                  }
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   private void resetHovering() {
      hoveringComponent = null;
   }

   protected void setNativeCursor(long var1, Cursor var3, int var4) {
      CCursorManager.getInstance().setCursor(var3);
   }

   private native long createNativeDragSource(Component var1, long var2, Transferable var4, InputEvent var5, int var6, int var7, int var8, int var9, long var10, CImage var12, int var13, int var14, int var15, long[] var16, Map var17);

   private native void doDragging(long var1);

   private native void releaseNativeDragSource(long var1);

   static {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("apple.awt.dnd.defaultDragImageSize")));
      if (var0 != null) {
         try {
            double var1 = Double.parseDouble(var0);
            if (var1 > 0.0D) {
               fMaxImageSize = var1;
            }
         } catch (NumberFormatException var3) {
         }
      }

   }
}
