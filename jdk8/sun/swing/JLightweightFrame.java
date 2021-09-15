package sun.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.peer.ComponentPeer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.LayoutFocusTraversalPolicy;
import javax.swing.RepaintManager;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import sun.awt.AWTAccessor;
import sun.awt.DisplayChangedListener;
import sun.awt.LightweightFrame;
import sun.awt.OverrideNativeWindowHandle;
import sun.security.action.GetPropertyAction;

public final class JLightweightFrame extends LightweightFrame implements RootPaneContainer {
   private final JRootPane rootPane = new JRootPane();
   private LightweightContent content;
   private Component component;
   private JPanel contentPane;
   private BufferedImage bbImage;
   private volatile int scaleFactor = 1;
   private static boolean copyBufferEnabled;
   private int[] copyBuffer;
   private PropertyChangeListener layoutSizeListener;
   private SwingUtilities2.RepaintListener repaintListener;

   public JLightweightFrame() {
      copyBufferEnabled = "true".equals(AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("swing.jlf.copyBufferEnabled", "true"))));
      this.add(this.rootPane, "Center");
      this.setFocusTraversalPolicy(new LayoutFocusTraversalPolicy());
      if (this.getGraphicsConfiguration().isTranslucencyCapable()) {
         this.setBackground(new Color(0, 0, 0, 0));
      }

      this.layoutSizeListener = new PropertyChangeListener() {
         public void propertyChange(PropertyChangeEvent var1) {
            Dimension var2 = (Dimension)var1.getNewValue();
            if ("preferredSize".equals(var1.getPropertyName())) {
               JLightweightFrame.this.content.preferredSizeChanged(var2.width, var2.height);
            } else if ("maximumSize".equals(var1.getPropertyName())) {
               JLightweightFrame.this.content.maximumSizeChanged(var2.width, var2.height);
            } else if ("minimumSize".equals(var1.getPropertyName())) {
               JLightweightFrame.this.content.minimumSizeChanged(var2.width, var2.height);
            }

         }
      };
      this.repaintListener = (var1, var2, var3, var4, var5) -> {
         Window var6 = SwingUtilities.getWindowAncestor(var1);
         if (var6 == this) {
            Point var7 = SwingUtilities.convertPoint(var1, var2, var3, var6);
            Rectangle var8 = (new Rectangle(var7.x, var7.y, var4, var5)).intersection(new Rectangle(0, 0, this.bbImage.getWidth() / this.scaleFactor, this.bbImage.getHeight() / this.scaleFactor));
            if (!var8.isEmpty()) {
               this.notifyImageUpdated(var8.x, var8.y, var8.width, var8.height);
            }

         }
      };
      SwingAccessor.getRepaintManagerAccessor().addRepaintListener(RepaintManager.currentManager((Component)this), this.repaintListener);
   }

   public void dispose() {
      SwingAccessor.getRepaintManagerAccessor().removeRepaintListener(RepaintManager.currentManager((Component)this), this.repaintListener);
      super.dispose();
   }

   public void setContent(LightweightContent var1) {
      if (var1 == null) {
         System.err.println("JLightweightFrame.setContent: content may not be null!");
      } else {
         this.content = var1;
         this.component = var1.getComponent();
         Dimension var2 = this.component.getPreferredSize();
         var1.preferredSizeChanged(var2.width, var2.height);
         var2 = this.component.getMaximumSize();
         var1.maximumSizeChanged(var2.width, var2.height);
         var2 = this.component.getMinimumSize();
         var1.minimumSizeChanged(var2.width, var2.height);
         this.initInterior();
      }
   }

   public Graphics getGraphics() {
      if (this.bbImage == null) {
         return null;
      } else {
         Graphics2D var1 = this.bbImage.createGraphics();
         var1.setBackground(this.getBackground());
         var1.setColor(this.getForeground());
         var1.setFont(this.getFont());
         var1.scale((double)this.scaleFactor, (double)this.scaleFactor);
         return var1;
      }
   }

   public void grabFocus() {
      if (this.content != null) {
         this.content.focusGrabbed();
      }

   }

   public void ungrabFocus() {
      if (this.content != null) {
         this.content.focusUngrabbed();
      }

   }

   public int getScaleFactor() {
      return this.scaleFactor;
   }

   public void notifyDisplayChanged(int var1) {
      if (var1 != this.scaleFactor) {
         if (!copyBufferEnabled) {
            this.content.paintLock();
         }

         try {
            if (this.bbImage != null) {
               this.resizeBuffer(this.getWidth(), this.getHeight(), var1);
            }
         } finally {
            if (!copyBufferEnabled) {
               this.content.paintUnlock();
            }

         }

         this.scaleFactor = var1;
      }

      if (this.getPeer() instanceof DisplayChangedListener) {
         ((DisplayChangedListener)this.getPeer()).displayChanged();
      }

      this.repaint();
   }

   public void addNotify() {
      super.addNotify();
      if (this.getPeer() instanceof DisplayChangedListener) {
         ((DisplayChangedListener)this.getPeer()).displayChanged();
      }

   }

   private void syncCopyBuffer(boolean var1, int var2, int var3, int var4, int var5, int var6) {
      this.content.paintLock();

      try {
         int[] var7 = ((DataBufferInt)this.bbImage.getRaster().getDataBuffer()).getData();
         if (var1) {
            this.copyBuffer = new int[var7.length];
         }

         int var8 = this.bbImage.getWidth();
         var2 *= var6;
         var3 *= var6;
         var4 *= var6;
         var5 *= var6;

         for(int var9 = 0; var9 < var5; ++var9) {
            int var10 = (var3 + var9) * var8 + var2;
            System.arraycopy(var7, var10, this.copyBuffer, var10, var4);
         }
      } finally {
         this.content.paintUnlock();
      }

   }

   private void notifyImageUpdated(int var1, int var2, int var3, int var4) {
      if (copyBufferEnabled) {
         this.syncCopyBuffer(false, var1, var2, var3, var4, this.scaleFactor);
      }

      this.content.imageUpdated(var1, var2, var3, var4);
   }

   private void initInterior() {
      this.contentPane = new JPanel() {
         public void paint(Graphics var1) {
            if (!JLightweightFrame.copyBufferEnabled) {
               JLightweightFrame.this.content.paintLock();
            }

            try {
               super.paint(var1);
               final Rectangle var2 = var1.getClipBounds() != null ? var1.getClipBounds() : new Rectangle(0, 0, JLightweightFrame.this.contentPane.getWidth(), JLightweightFrame.this.contentPane.getHeight());
               var2.x = Math.max(0, var2.x);
               var2.y = Math.max(0, var2.y);
               var2.width = Math.min(JLightweightFrame.this.contentPane.getWidth(), var2.width);
               var2.height = Math.min(JLightweightFrame.this.contentPane.getHeight(), var2.height);
               EventQueue.invokeLater(new Runnable() {
                  public void run() {
                     Rectangle var1 = JLightweightFrame.this.contentPane.getBounds().intersection(var2);
                     JLightweightFrame.this.notifyImageUpdated(var1.x, var1.y, var1.width, var1.height);
                  }
               });
            } finally {
               if (!JLightweightFrame.copyBufferEnabled) {
                  JLightweightFrame.this.content.paintUnlock();
               }

            }

         }

         protected boolean isPaintingOrigin() {
            return true;
         }
      };
      this.contentPane.setLayout(new BorderLayout());
      this.contentPane.add(this.component);
      if ("true".equals(AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("swing.jlf.contentPaneTransparent", "false"))))) {
         this.contentPane.setOpaque(false);
      }

      this.setContentPane(this.contentPane);
      this.contentPane.addContainerListener(new ContainerListener() {
         public void componentAdded(ContainerEvent var1) {
            Component var2 = JLightweightFrame.this.component;
            if (var1.getChild() == var2) {
               var2.addPropertyChangeListener("preferredSize", JLightweightFrame.this.layoutSizeListener);
               var2.addPropertyChangeListener("maximumSize", JLightweightFrame.this.layoutSizeListener);
               var2.addPropertyChangeListener("minimumSize", JLightweightFrame.this.layoutSizeListener);
            }

         }

         public void componentRemoved(ContainerEvent var1) {
            Component var2 = JLightweightFrame.this.component;
            if (var1.getChild() == var2) {
               var2.removePropertyChangeListener(JLightweightFrame.this.layoutSizeListener);
            }

         }
      });
   }

   public void reshape(int var1, int var2, int var3, int var4) {
      super.reshape(var1, var2, var3, var4);
      if (var3 != 0 && var4 != 0) {
         if (!copyBufferEnabled) {
            this.content.paintLock();
         }

         try {
            boolean var5 = this.bbImage == null;
            int var6 = var3;
            int var7 = var4;
            if (this.bbImage != null) {
               int var8 = this.bbImage.getWidth() / this.scaleFactor;
               int var9 = this.bbImage.getHeight() / this.scaleFactor;
               if (var3 != var8 || var4 != var9) {
                  var5 = true;
                  if (this.bbImage != null) {
                     if (var8 >= var3 && var9 >= var4) {
                        var5 = false;
                     } else {
                        if (var8 >= var3) {
                           var6 = var8;
                        } else {
                           var6 = Math.max((int)((double)var8 * 1.2D), var3);
                        }

                        if (var9 >= var4) {
                           var7 = var9;
                        } else {
                           var7 = Math.max((int)((double)var9 * 1.2D), var4);
                        }
                     }
                  }
               }
            }

            if (var5) {
               this.resizeBuffer(var6, var7, this.scaleFactor);
               return;
            }

            this.content.imageReshaped(0, 0, var3, var4);
         } finally {
            if (!copyBufferEnabled) {
               this.content.paintUnlock();
            }

         }

      }
   }

   private void resizeBuffer(int var1, int var2, int var3) {
      this.bbImage = new BufferedImage(var1 * var3, var2 * var3, 3);
      int[] var4 = ((DataBufferInt)this.bbImage.getRaster().getDataBuffer()).getData();
      if (copyBufferEnabled) {
         this.syncCopyBuffer(true, 0, 0, var1, var2, var3);
         var4 = this.copyBuffer;
      }

      this.content.imageBufferReset(var4, 0, 0, var1, var2, var1 * var3, var3);
   }

   public JRootPane getRootPane() {
      return this.rootPane;
   }

   public void setContentPane(Container var1) {
      this.getRootPane().setContentPane(var1);
   }

   public Container getContentPane() {
      return this.getRootPane().getContentPane();
   }

   public void setLayeredPane(JLayeredPane var1) {
      this.getRootPane().setLayeredPane(var1);
   }

   public JLayeredPane getLayeredPane() {
      return this.getRootPane().getLayeredPane();
   }

   public void setGlassPane(Component var1) {
      this.getRootPane().setGlassPane(var1);
   }

   public Component getGlassPane() {
      return this.getRootPane().getGlassPane();
   }

   private void updateClientCursor() {
      Point var1 = MouseInfo.getPointerInfo().getLocation();
      SwingUtilities.convertPointFromScreen(var1, this);
      Component var2 = SwingUtilities.getDeepestComponentAt(this, var1.x, var1.y);
      if (var2 != null) {
         this.content.setCursor(var2.getCursor());
      }

   }

   public void overrideNativeWindowHandle(long var1, Runnable var3) {
      ComponentPeer var4 = AWTAccessor.getComponentAccessor().getPeer(this);
      if (var4 instanceof OverrideNativeWindowHandle) {
         ((OverrideNativeWindowHandle)var4).overrideWindowHandle(var1);
      }

      if (var3 != null) {
         var3.run();
      }

   }

   public <T extends DragGestureRecognizer> T createDragGestureRecognizer(Class<T> var1, DragSource var2, Component var3, int var4, DragGestureListener var5) {
      return this.content == null ? null : this.content.createDragGestureRecognizer(var1, var2, var3, var4, var5);
   }

   public DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent var1) throws InvalidDnDOperationException {
      return this.content == null ? null : this.content.createDragSourceContextPeer(var1);
   }

   public void addDropTarget(DropTarget var1) {
      if (this.content != null) {
         this.content.addDropTarget(var1);
      }
   }

   public void removeDropTarget(DropTarget var1) {
      if (this.content != null) {
         this.content.removeDropTarget(var1);
      }
   }

   static {
      SwingAccessor.setJLightweightFrameAccessor(new SwingAccessor.JLightweightFrameAccessor() {
         public void updateCursor(JLightweightFrame var1) {
            var1.updateClientCursor();
         }
      });
      copyBufferEnabled = "true".equals(AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("swing.jlf.copyBufferEnabled", "true"))));
   }
}
