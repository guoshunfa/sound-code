package javax.swing;

import com.sun.java.swing.SwingUtilities3;
import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.ImageCapabilities;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import sun.awt.SubRegionShowable;
import sun.awt.SunToolkit;
import sun.java2d.SunGraphics2D;
import sun.java2d.pipe.hw.ExtendedBufferCapabilities;
import sun.util.logging.PlatformLogger;

class BufferStrategyPaintManager extends RepaintManager.PaintManager {
   private static Method COMPONENT_CREATE_BUFFER_STRATEGY_METHOD;
   private static Method COMPONENT_GET_BUFFER_STRATEGY_METHOD;
   private static final PlatformLogger LOGGER = PlatformLogger.getLogger("javax.swing.BufferStrategyPaintManager");
   private ArrayList<BufferStrategyPaintManager.BufferInfo> bufferInfos = new ArrayList(1);
   private boolean painting;
   private boolean showing;
   private int accumulatedX;
   private int accumulatedY;
   private int accumulatedMaxX;
   private int accumulatedMaxY;
   private JComponent rootJ;
   private int xOffset;
   private int yOffset;
   private Graphics bsg;
   private BufferStrategy bufferStrategy;
   private BufferStrategyPaintManager.BufferInfo bufferInfo;
   private boolean disposeBufferOnEnd;

   private static Method getGetBufferStrategyMethod() {
      if (COMPONENT_GET_BUFFER_STRATEGY_METHOD == null) {
         getMethods();
      }

      return COMPONENT_GET_BUFFER_STRATEGY_METHOD;
   }

   private static Method getCreateBufferStrategyMethod() {
      if (COMPONENT_CREATE_BUFFER_STRATEGY_METHOD == null) {
         getMethods();
      }

      return COMPONENT_CREATE_BUFFER_STRATEGY_METHOD;
   }

   private static void getMethods() {
      AccessController.doPrivileged(new PrivilegedAction<Object>() {
         public Object run() {
            try {
               BufferStrategyPaintManager.COMPONENT_CREATE_BUFFER_STRATEGY_METHOD = Component.class.getDeclaredMethod("createBufferStrategy", Integer.TYPE, BufferCapabilities.class);
               BufferStrategyPaintManager.COMPONENT_CREATE_BUFFER_STRATEGY_METHOD.setAccessible(true);
               BufferStrategyPaintManager.COMPONENT_GET_BUFFER_STRATEGY_METHOD = Component.class.getDeclaredMethod("getBufferStrategy");
               BufferStrategyPaintManager.COMPONENT_GET_BUFFER_STRATEGY_METHOD.setAccessible(true);
            } catch (SecurityException var2) {
               assert false;
            } catch (NoSuchMethodException var3) {
               assert false;
            }

            return null;
         }
      });
   }

   protected void dispose() {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            ArrayList var1;
            synchronized(BufferStrategyPaintManager.this) {
               while(BufferStrategyPaintManager.this.showing) {
                  try {
                     BufferStrategyPaintManager.this.wait();
                  } catch (InterruptedException var5) {
                  }
               }

               var1 = BufferStrategyPaintManager.this.bufferInfos;
               BufferStrategyPaintManager.this.bufferInfos = null;
            }

            BufferStrategyPaintManager.this.dispose(var1);
         }
      });
   }

   private void dispose(List<BufferStrategyPaintManager.BufferInfo> var1) {
      if (LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
         LOGGER.finer("BufferStrategyPaintManager disposed", (Throwable)(new RuntimeException()));
      }

      if (var1 != null) {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            BufferStrategyPaintManager.BufferInfo var3 = (BufferStrategyPaintManager.BufferInfo)var2.next();
            var3.dispose();
         }
      }

   }

   public boolean show(Container var1, int var2, int var3, int var4, int var5) {
      synchronized(this) {
         if (this.painting) {
            return false;
         }

         this.showing = true;
      }

      boolean var22 = false;

      boolean var10;
      label141: {
         try {
            var22 = true;
            BufferStrategyPaintManager.BufferInfo var6 = this.getBufferInfo(var1);
            if (var6 != null) {
               if (var6.isInSync()) {
                  BufferStrategy var7;
                  if ((var7 = var6.getBufferStrategy(false)) != null) {
                     SubRegionShowable var8 = (SubRegionShowable)var7;
                     boolean var9 = var6.getPaintAllOnExpose();
                     var6.setPaintAllOnExpose(false);
                     if (var8.showIfNotLost(var2, var3, var2 + var4, var3 + var5)) {
                        var10 = !var9;
                        var22 = false;
                        break label141;
                     }

                     this.bufferInfo.setContentsLostDuringExpose(true);
                     var22 = false;
                  } else {
                     var22 = false;
                  }
               } else {
                  var22 = false;
               }
            } else {
               var22 = false;
            }
         } finally {
            if (var22) {
               synchronized(this) {
                  this.showing = false;
                  this.notifyAll();
               }
            }
         }

         synchronized(this) {
            this.showing = false;
            this.notifyAll();
            return false;
         }
      }

      synchronized(this) {
         this.showing = false;
         this.notifyAll();
         return var10;
      }
   }

   public boolean paint(JComponent var1, JComponent var2, Graphics var3, int var4, int var5, int var6, int var7) {
      Container var8 = this.fetchRoot(var1);
      if (this.prepare(var1, var8, true, var4, var5, var6, var7)) {
         if (var3 instanceof SunGraphics2D && ((SunGraphics2D)var3).getDestination() == var8) {
            int var9 = ((SunGraphics2D)this.bsg).constrainX;
            int var10 = ((SunGraphics2D)this.bsg).constrainY;
            if (var9 != 0 || var10 != 0) {
               this.bsg.translate(-var9, -var10);
            }

            ((SunGraphics2D)this.bsg).constrain(this.xOffset + var9, this.yOffset + var10, var4 + var6, var5 + var7);
            this.bsg.setClip(var4, var5, var6, var7);
            var1.paintToOffscreen(this.bsg, var4, var5, var6, var7, var4 + var6, var5 + var7);
            this.accumulate(this.xOffset + var4, this.yOffset + var5, var6, var7);
            return true;
         }

         this.bufferInfo.setInSync(false);
      }

      if (LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
         LOGGER.finer("prepare failed");
      }

      return super.paint(var1, var2, var3, var4, var5, var6, var7);
   }

   public void copyArea(JComponent var1, Graphics var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9) {
      Container var10 = this.fetchRoot(var1);
      if (this.prepare(var1, var10, false, 0, 0, 0, 0) && this.bufferInfo.isInSync()) {
         if (var9) {
            Rectangle var11 = var1.getVisibleRect();
            int var12 = this.xOffset + var3;
            int var13 = this.yOffset + var4;
            this.bsg.clipRect(this.xOffset + var11.x, this.yOffset + var11.y, var11.width, var11.height);
            this.bsg.copyArea(var12, var13, var5, var6, var7, var8);
         } else {
            this.bsg.copyArea(this.xOffset + var3, this.yOffset + var4, var5, var6, var7, var8);
         }

         this.accumulate(var3 + this.xOffset + var7, var4 + this.yOffset + var8, var5, var6);
      } else {
         if (LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
            LOGGER.finer("copyArea: prepare failed or not in sync");
         }

         if (!this.flushAccumulatedRegion()) {
            this.rootJ.repaint();
         } else {
            super.copyArea(var1, var2, var3, var4, var5, var6, var7, var8, var9);
         }
      }

   }

   public void beginPaint() {
      synchronized(this) {
         this.painting = true;

         while(true) {
            if (!this.showing) {
               break;
            }

            try {
               this.wait();
            } catch (InterruptedException var4) {
            }
         }
      }

      if (LOGGER.isLoggable(PlatformLogger.Level.FINEST)) {
         LOGGER.finest("beginPaint");
      }

      this.resetAccumulated();
   }

   public void endPaint() {
      if (LOGGER.isLoggable(PlatformLogger.Level.FINEST)) {
         LOGGER.finest("endPaint: region " + this.accumulatedX + " " + this.accumulatedY + " " + this.accumulatedMaxX + " " + this.accumulatedMaxY);
      }

      if (this.painting && !this.flushAccumulatedRegion()) {
         if (!this.isRepaintingRoot()) {
            this.repaintRoot(this.rootJ);
         } else {
            this.resetDoubleBufferPerWindow();
            this.rootJ.repaint();
         }
      }

      BufferStrategyPaintManager.BufferInfo var1 = null;
      synchronized(this) {
         this.painting = false;
         if (this.disposeBufferOnEnd) {
            this.disposeBufferOnEnd = false;
            var1 = this.bufferInfo;
            this.bufferInfos.remove(var1);
         }
      }

      if (var1 != null) {
         var1.dispose();
      }

   }

   private boolean flushAccumulatedRegion() {
      boolean var1 = true;
      if (this.accumulatedX != Integer.MAX_VALUE) {
         SubRegionShowable var2 = (SubRegionShowable)this.bufferStrategy;
         boolean var3 = this.bufferStrategy.contentsLost();
         if (!var3) {
            var2.show(this.accumulatedX, this.accumulatedY, this.accumulatedMaxX, this.accumulatedMaxY);
            var3 = this.bufferStrategy.contentsLost();
         }

         if (var3) {
            if (LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
               LOGGER.finer("endPaint: contents lost");
            }

            this.bufferInfo.setInSync(false);
            var1 = false;
         }
      }

      this.resetAccumulated();
      return var1;
   }

   private void resetAccumulated() {
      this.accumulatedX = Integer.MAX_VALUE;
      this.accumulatedY = Integer.MAX_VALUE;
      this.accumulatedMaxX = 0;
      this.accumulatedMaxY = 0;
   }

   public void doubleBufferingChanged(final JRootPane var1) {
      if ((!var1.isDoubleBuffered() || !var1.getUseTrueDoubleBuffering()) && var1.getParent() != null) {
         if (!SwingUtilities.isEventDispatchThread()) {
            Runnable var2 = new Runnable() {
               public void run() {
                  BufferStrategyPaintManager.this.doubleBufferingChanged0(var1);
               }
            };
            SwingUtilities.invokeLater(var2);
         } else {
            this.doubleBufferingChanged0(var1);
         }
      }

   }

   private void doubleBufferingChanged0(JRootPane var1) {
      BufferStrategyPaintManager.BufferInfo var2;
      synchronized(this) {
         while(this.showing) {
            try {
               this.wait();
            } catch (InterruptedException var6) {
            }
         }

         var2 = this.getBufferInfo(var1.getParent());
         if (this.painting && this.bufferInfo == var2) {
            this.disposeBufferOnEnd = true;
            var2 = null;
         } else if (var2 != null) {
            this.bufferInfos.remove(var2);
         }
      }

      if (var2 != null) {
         var2.dispose();
      }

   }

   private boolean prepare(JComponent var1, Container var2, boolean var3, int var4, int var5, int var6, int var7) {
      if (this.bsg != null) {
         this.bsg.dispose();
         this.bsg = null;
      }

      this.bufferStrategy = null;
      if (var2 != null) {
         boolean var8 = false;
         BufferStrategyPaintManager.BufferInfo var9 = this.getBufferInfo(var2);
         if (var9 == null) {
            var8 = true;
            var9 = new BufferStrategyPaintManager.BufferInfo(var2);
            this.bufferInfos.add(var9);
            if (LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
               LOGGER.finer("prepare: new BufferInfo: " + var2);
            }
         }

         this.bufferInfo = var9;
         if (!var9.hasBufferStrategyChanged()) {
            this.bufferStrategy = var9.getBufferStrategy(true);
            if (this.bufferStrategy == null) {
               return false;
            }

            this.bsg = this.bufferStrategy.getDrawGraphics();
            if (this.bufferStrategy.contentsRestored()) {
               var8 = true;
               if (LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
                  LOGGER.finer("prepare: contents restored in prepare");
               }
            }

            if (var9.getContentsLostDuringExpose()) {
               var8 = true;
               var9.setContentsLostDuringExpose(false);
               if (LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
                  LOGGER.finer("prepare: contents lost on expose");
               }
            }

            if (var3 && var1 == this.rootJ && var4 == 0 && var5 == 0 && var1.getWidth() == var6 && var1.getHeight() == var7) {
               var9.setInSync(true);
            } else if (var8) {
               var9.setInSync(false);
               if (!this.isRepaintingRoot()) {
                  this.repaintRoot(this.rootJ);
               } else {
                  this.resetDoubleBufferPerWindow();
               }
            }

            return this.bufferInfos != null;
         }
      }

      return false;
   }

   private Container fetchRoot(JComponent var1) {
      boolean var2 = false;
      this.rootJ = var1;
      Object var3 = var1;
      this.xOffset = this.yOffset = 0;

      while(var3 != null && !(var3 instanceof Window) && !SunToolkit.isInstanceOf(var3, "java.applet.Applet")) {
         this.xOffset += ((Container)var3).getX();
         this.yOffset += ((Container)var3).getY();
         var3 = ((Container)var3).getParent();
         if (var3 != null) {
            if (var3 instanceof JComponent) {
               this.rootJ = (JComponent)var3;
            } else if (!((Container)var3).isLightweight()) {
               if (var2) {
                  return null;
               }

               var2 = true;
            }
         }
      }

      return (Container)(var3 instanceof RootPaneContainer && this.rootJ instanceof JRootPane && this.rootJ.isDoubleBuffered() && ((JRootPane)this.rootJ).getUseTrueDoubleBuffering() ? var3 : null);
   }

   private void resetDoubleBufferPerWindow() {
      if (this.bufferInfos != null) {
         this.dispose(this.bufferInfos);
         this.bufferInfos = null;
         this.repaintManager.setPaintManager((RepaintManager.PaintManager)null);
      }

   }

   private BufferStrategyPaintManager.BufferInfo getBufferInfo(Container var1) {
      for(int var2 = this.bufferInfos.size() - 1; var2 >= 0; --var2) {
         BufferStrategyPaintManager.BufferInfo var3 = (BufferStrategyPaintManager.BufferInfo)this.bufferInfos.get(var2);
         Container var4 = var3.getRoot();
         if (var4 == null) {
            this.bufferInfos.remove(var2);
            if (LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
               LOGGER.finer("BufferInfo pruned, root null");
            }
         } else if (var4 == var1) {
            return var3;
         }
      }

      return null;
   }

   private void accumulate(int var1, int var2, int var3, int var4) {
      this.accumulatedX = Math.min(var1, this.accumulatedX);
      this.accumulatedY = Math.min(var2, this.accumulatedY);
      this.accumulatedMaxX = Math.max(this.accumulatedMaxX, var1 + var3);
      this.accumulatedMaxY = Math.max(this.accumulatedMaxY, var2 + var4);
   }

   private class BufferInfo extends ComponentAdapter implements WindowListener {
      private WeakReference<BufferStrategy> weakBS;
      private WeakReference<Container> root;
      private boolean inSync;
      private boolean contentsLostDuringExpose;
      private boolean paintAllOnExpose;

      public BufferInfo(Container var2) {
         this.root = new WeakReference(var2);
         var2.addComponentListener(this);
         if (var2 instanceof Window) {
            ((Window)var2).addWindowListener(this);
         }

      }

      public void setPaintAllOnExpose(boolean var1) {
         this.paintAllOnExpose = var1;
      }

      public boolean getPaintAllOnExpose() {
         return this.paintAllOnExpose;
      }

      public void setContentsLostDuringExpose(boolean var1) {
         this.contentsLostDuringExpose = var1;
      }

      public boolean getContentsLostDuringExpose() {
         return this.contentsLostDuringExpose;
      }

      public void setInSync(boolean var1) {
         this.inSync = var1;
      }

      public boolean isInSync() {
         return this.inSync;
      }

      public Container getRoot() {
         return this.root == null ? null : (Container)this.root.get();
      }

      public BufferStrategy getBufferStrategy(boolean var1) {
         BufferStrategy var2 = this.weakBS == null ? null : (BufferStrategy)this.weakBS.get();
         if (var2 == null && var1) {
            var2 = this.createBufferStrategy();
            if (var2 != null) {
               this.weakBS = new WeakReference(var2);
            }

            if (BufferStrategyPaintManager.LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
               BufferStrategyPaintManager.LOGGER.finer("getBufferStrategy: created bs: " + var2);
            }
         }

         return var2;
      }

      public boolean hasBufferStrategyChanged() {
         Container var1 = this.getRoot();
         if (var1 != null) {
            BufferStrategy var2 = null;
            BufferStrategy var3 = null;
            var2 = this.getBufferStrategy(false);
            if (var1 instanceof Window) {
               var3 = ((Window)var1).getBufferStrategy();
            } else {
               try {
                  var3 = (BufferStrategy)BufferStrategyPaintManager.getGetBufferStrategyMethod().invoke(var1);
               } catch (InvocationTargetException var5) {
                  assert false;
               } catch (IllegalArgumentException var6) {
                  assert false;
               } catch (IllegalAccessException var7) {
                  assert false;
               }
            }

            if (var3 != var2) {
               if (var2 != null) {
                  var2.dispose();
               }

               this.weakBS = null;
               return true;
            }
         }

         return false;
      }

      private BufferStrategy createBufferStrategy() {
         Container var1 = this.getRoot();
         if (var1 == null) {
            return null;
         } else {
            BufferStrategy var2 = null;
            if (SwingUtilities3.isVsyncRequested(var1)) {
               var2 = this.createBufferStrategy(var1, true);
               if (BufferStrategyPaintManager.LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
                  BufferStrategyPaintManager.LOGGER.finer("createBufferStrategy: using vsynced strategy");
               }
            }

            if (var2 == null) {
               var2 = this.createBufferStrategy(var1, false);
            }

            if (!(var2 instanceof SubRegionShowable)) {
               var2 = null;
            }

            return var2;
         }
      }

      private BufferStrategy createBufferStrategy(Container var1, boolean var2) {
         Object var3;
         if (var2) {
            var3 = new ExtendedBufferCapabilities(new ImageCapabilities(true), new ImageCapabilities(true), BufferCapabilities.FlipContents.COPIED, ExtendedBufferCapabilities.VSyncType.VSYNC_ON);
         } else {
            var3 = new BufferCapabilities(new ImageCapabilities(true), new ImageCapabilities(true), (BufferCapabilities.FlipContents)null);
         }

         BufferStrategy var4 = null;
         if (SunToolkit.isInstanceOf((Object)var1, "java.applet.Applet")) {
            try {
               BufferStrategyPaintManager.getCreateBufferStrategyMethod().invoke(var1, 2, var3);
               var4 = (BufferStrategy)BufferStrategyPaintManager.getGetBufferStrategyMethod().invoke(var1);
            } catch (InvocationTargetException var7) {
               if (BufferStrategyPaintManager.LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
                  BufferStrategyPaintManager.LOGGER.finer("createBufferStratety failed", (Throwable)var7);
               }
            } catch (IllegalArgumentException var8) {
               assert false;
            } catch (IllegalAccessException var9) {
               assert false;
            }
         } else {
            try {
               ((Window)var1).createBufferStrategy(2, (BufferCapabilities)var3);
               var4 = ((Window)var1).getBufferStrategy();
            } catch (AWTException var6) {
               if (BufferStrategyPaintManager.LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
                  BufferStrategyPaintManager.LOGGER.finer("createBufferStratety failed", (Throwable)var6);
               }
            }
         }

         return var4;
      }

      public void dispose() {
         Container var1 = this.getRoot();
         if (BufferStrategyPaintManager.LOGGER.isLoggable(PlatformLogger.Level.FINER)) {
            BufferStrategyPaintManager.LOGGER.finer("disposed BufferInfo for: " + var1);
         }

         if (var1 != null) {
            var1.removeComponentListener(this);
            if (var1 instanceof Window) {
               ((Window)var1).removeWindowListener(this);
            }

            BufferStrategy var2 = this.getBufferStrategy(false);
            if (var2 != null) {
               var2.dispose();
            }
         }

         this.root = null;
         this.weakBS = null;
      }

      public void componentHidden(ComponentEvent var1) {
         Container var2 = this.getRoot();
         if (var2 != null && var2.isVisible()) {
            var2.repaint();
         } else {
            this.setPaintAllOnExpose(true);
         }

      }

      public void windowIconified(WindowEvent var1) {
         this.setPaintAllOnExpose(true);
      }

      public void windowClosed(WindowEvent var1) {
         synchronized(BufferStrategyPaintManager.this) {
            while(BufferStrategyPaintManager.this.showing) {
               try {
                  BufferStrategyPaintManager.this.wait();
               } catch (InterruptedException var5) {
               }
            }

            BufferStrategyPaintManager.this.bufferInfos.remove(this);
         }

         this.dispose();
      }

      public void windowOpened(WindowEvent var1) {
      }

      public void windowClosing(WindowEvent var1) {
      }

      public void windowDeiconified(WindowEvent var1) {
      }

      public void windowActivated(WindowEvent var1) {
      }

      public void windowDeactivated(WindowEvent var1) {
      }
   }
}
