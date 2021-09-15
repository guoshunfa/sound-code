package sun.lwawt.macosx;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Window;
import java.awt.geom.Point2D;
import java.awt.image.ImageObserver;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import sun.awt.AWTAccessor;
import sun.awt.AWTIcon32_security_icon_bw16_png;
import sun.awt.AWTIcon32_security_icon_bw24_png;
import sun.awt.AWTIcon32_security_icon_bw32_png;
import sun.awt.AWTIcon32_security_icon_bw48_png;
import sun.awt.AWTIcon32_security_icon_interim16_png;
import sun.awt.AWTIcon32_security_icon_interim24_png;
import sun.awt.AWTIcon32_security_icon_interim32_png;
import sun.awt.AWTIcon32_security_icon_interim48_png;
import sun.awt.AWTIcon32_security_icon_yellow16_png;
import sun.awt.AWTIcon32_security_icon_yellow24_png;
import sun.awt.AWTIcon32_security_icon_yellow32_png;
import sun.awt.AWTIcon32_security_icon_yellow48_png;
import sun.awt.IconInfo;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.opengl.CGLLayer;
import sun.lwawt.LWWindowPeer;
import sun.lwawt.PlatformEventNotifier;
import sun.lwawt.SecurityWarningWindow;

public final class CWarningWindow extends CPlatformWindow implements SecurityWarningWindow, PlatformEventNotifier {
   private final CWarningWindow.Lock lock = new CWarningWindow.Lock();
   private static final int SHOWING_DELAY = 300;
   private static final int HIDING_DELAY = 2000;
   private Rectangle bounds = new Rectangle();
   private final WeakReference<LWWindowPeer> ownerPeer;
   private final Window ownerWindow;
   private volatile int currentIcon = 0;
   private int currentSize = -1;
   private static IconInfo[][] icons;
   private final Runnable hidingTask = new Runnable() {
      public void run() {
         synchronized(CWarningWindow.this.lock) {
            CWarningWindow.this.setVisible(false);
         }

         synchronized(CWarningWindow.this.scheduler) {
            CWarningWindow.this.hidingTaskHandle = null;
         }
      }
   };
   private final Runnable showingTask = new Runnable() {
      public void run() {
         synchronized(CWarningWindow.this.lock) {
            if (!CWarningWindow.this.isVisible()) {
               CWarningWindow.this.setVisible(true);
            }

            CWarningWindow.this.repaint();
         }

         synchronized(CWarningWindow.this.scheduler) {
            if (CWarningWindow.this.currentIcon > 0) {
               CWarningWindow.this.currentIcon--;
               CWarningWindow.this.showingTaskHandle = CWarningWindow.this.scheduler.schedule(CWarningWindow.this.showingTask, 300L, TimeUnit.MILLISECONDS);
            } else {
               CWarningWindow.this.showingTaskHandle = null;
            }

         }
      }
   };
   private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
   private ScheduledFuture hidingTaskHandle;
   private ScheduledFuture showingTaskHandle;

   private static IconInfo getSecurityIconInfo(int var0, int var1) {
      Class var2 = CWarningWindow.class;
      synchronized(CWarningWindow.class) {
         if (icons == null) {
            icons = new IconInfo[4][3];
            icons[0][0] = new IconInfo(AWTIcon32_security_icon_bw16_png.security_icon_bw16_png);
            icons[0][1] = new IconInfo(AWTIcon32_security_icon_interim16_png.security_icon_interim16_png);
            icons[0][2] = new IconInfo(AWTIcon32_security_icon_yellow16_png.security_icon_yellow16_png);
            icons[1][0] = new IconInfo(AWTIcon32_security_icon_bw24_png.security_icon_bw24_png);
            icons[1][1] = new IconInfo(AWTIcon32_security_icon_interim24_png.security_icon_interim24_png);
            icons[1][2] = new IconInfo(AWTIcon32_security_icon_yellow24_png.security_icon_yellow24_png);
            icons[2][0] = new IconInfo(AWTIcon32_security_icon_bw32_png.security_icon_bw32_png);
            icons[2][1] = new IconInfo(AWTIcon32_security_icon_interim32_png.security_icon_interim32_png);
            icons[2][2] = new IconInfo(AWTIcon32_security_icon_yellow32_png.security_icon_yellow32_png);
            icons[3][0] = new IconInfo(AWTIcon32_security_icon_bw48_png.security_icon_bw48_png);
            icons[3][1] = new IconInfo(AWTIcon32_security_icon_interim48_png.security_icon_interim48_png);
            icons[3][2] = new IconInfo(AWTIcon32_security_icon_yellow48_png.security_icon_yellow48_png);
         }
      }

      int var5 = var0 % icons.length;
      return icons[var5][var1 % icons[var5].length];
   }

   public CWarningWindow(Window var1, LWWindowPeer var2) {
      this.ownerPeer = new WeakReference(var2);
      this.ownerWindow = var1;
      this.initialize((Window)null, (LWWindowPeer)null, var2.getPlatformWindow());
      this.setOpaque(false);
      String var3 = this.ownerWindow.getWarningString();
      if (var3 != null) {
         this.contentView.setToolTip(this.ownerWindow.getWarningString());
      }

      this.updateIconSize();
   }

   public void reposition(int var1, int var2, int var3, int var4) {
      Point2D var5 = AWTAccessor.getWindowAccessor().calculateSecurityWarningPosition(this.ownerWindow, (double)var1, (double)var2, (double)var3, (double)var4);
      this.setBounds((int)var5.getX(), (int)var5.getY(), this.getWidth(), this.getHeight());
   }

   public void setVisible(boolean var1, boolean var2) {
      synchronized(this.scheduler) {
         if (this.showingTaskHandle != null) {
            this.showingTaskHandle.cancel(false);
            this.showingTaskHandle = null;
         }

         if (this.hidingTaskHandle != null) {
            this.hidingTaskHandle.cancel(false);
            this.hidingTaskHandle = null;
         }

         if (var1) {
            if (this.isVisible()) {
               this.currentIcon = 0;
            } else {
               this.currentIcon = 2;
            }

            this.showingTaskHandle = this.scheduler.schedule(this.showingTask, 50L, TimeUnit.MILLISECONDS);
         } else {
            if (!this.isVisible()) {
               return;
            }

            if (var2) {
               this.hidingTaskHandle = this.scheduler.schedule(this.hidingTask, 2000L, TimeUnit.MILLISECONDS);
            } else {
               this.hidingTaskHandle = this.scheduler.schedule(this.hidingTask, 50L, TimeUnit.MILLISECONDS);
            }
         }

      }
   }

   public void notifyIconify(boolean var1) {
   }

   public void notifyZoom(boolean var1) {
   }

   public void notifyExpose(Rectangle var1) {
      this.repaint();
   }

   public void notifyReshape(int var1, int var2, int var3, int var4) {
   }

   public void notifyUpdateCursor() {
   }

   public void notifyActivation(boolean var1, LWWindowPeer var2) {
   }

   public void notifyNCMouseDown() {
   }

   public void notifyMouseEvent(int var1, long var2, int var4, int var5, int var6, int var7, int var8, int var9, int var10, boolean var11, byte[] var12) {
      LWWindowPeer var13 = (LWWindowPeer)this.ownerPeer.get();
      if (var1 == 505) {
         if (var13 != null) {
            var13.updateSecurityWarningVisibility();
         }
      } else if (var1 == 504 && var13 != null) {
         var13.updateSecurityWarningVisibility();
      }

   }

   public Rectangle getBounds() {
      synchronized(this.lock) {
         return this.bounds.getBounds();
      }
   }

   public boolean isVisible() {
      synchronized(this.lock) {
         return this.visible;
      }
   }

   public void setVisible(boolean var1) {
      synchronized(this.lock) {
         this.execute((var1x) -> {
            if (var1) {
               CWrapper.NSWindow.orderFront(var1x);
            } else {
               CWrapper.NSWindow.orderOut(var1x);
            }

         });
         this.visible = var1;
         if (var1 && this.owner != null && this.owner.isVisible()) {
            this.owner.execute((var1x) -> {
               this.execute((var2) -> {
                  CWrapper.NSWindow.orderWindow(var2, 1, var1x);
               });
            });
            this.applyWindowLevel(this.ownerWindow);
         }

      }
   }

   public void notifyMouseWheelEvent(long var1, int var3, int var4, int var5, int var6, int var7, int var8, double var9, byte[] var11) {
   }

   public void notifyKeyEvent(int var1, long var2, int var4, int var5, char var6, int var7) {
   }

   protected int getInitialStyleBits() {
      byte var1 = 0;
      CPlatformWindow.SET(var1, 16, true);
      return var1;
   }

   protected void deliverMoveResizeEvent(int var1, int var2, int var3, int var4, boolean var5) {
      boolean var6;
      synchronized(this.lock) {
         var6 = this.bounds.width != var3 || this.bounds.height != var4;
         this.bounds = new Rectangle(var1, var2, var3, var4);
      }

      if (var6) {
         this.replaceSurface();
      }

      super.deliverMoveResizeEvent(var1, var2, var3, var4, var5);
   }

   protected CPlatformResponder createPlatformResponder() {
      return new CPlatformResponder(this, false);
   }

   protected CPlatformView createContentView() {
      return new CPlatformView() {
         public GraphicsConfiguration getGraphicsConfiguration() {
            LWWindowPeer var1 = (LWWindowPeer)CWarningWindow.this.ownerPeer.get();
            return var1.getGraphicsConfiguration();
         }

         public Rectangle getBounds() {
            return CWarningWindow.this.getBounds();
         }

         public CGLLayer createCGLayer() {
            return new CGLLayer((LWWindowPeer)null) {
               public Rectangle getBounds() {
                  return CWarningWindow.this.getBounds();
               }

               public GraphicsConfiguration getGraphicsConfiguration() {
                  LWWindowPeer var1 = (LWWindowPeer)CWarningWindow.this.ownerPeer.get();
                  return var1.getGraphicsConfiguration();
               }

               public boolean isOpaque() {
                  return false;
               }
            };
         }
      };
   }

   private void updateIconSize() {
      byte var1 = -1;
      if (this.ownerWindow != null) {
         Insets var2 = this.ownerWindow.getInsets();
         int var3 = Math.max(var2.top, Math.max(var2.bottom, Math.max(var2.left, var2.right)));
         if (var3 < 24) {
            var1 = 0;
         } else if (var3 < 32) {
            var1 = 1;
         } else if (var3 < 48) {
            var1 = 2;
         } else {
            var1 = 3;
         }
      }

      if (var1 == -1) {
         var1 = 0;
      }

      synchronized(this.lock) {
         if (var1 != this.currentSize) {
            this.currentSize = var1;
            IconInfo var6 = getSecurityIconInfo(this.currentSize, 0);
            AWTAccessor.getWindowAccessor().setSecurityWarningSize(this.ownerWindow, var6.getWidth(), var6.getHeight());
         }

      }
   }

   private final Graphics getGraphics() {
      SurfaceData var1 = this.contentView.getSurfaceData();
      return this.ownerWindow != null && var1 != null ? this.transformGraphics(new SunGraphics2D(var1, SystemColor.windowText, SystemColor.window, this.ownerWindow.getFont())) : null;
   }

   private void repaint() {
      Graphics var1 = this.getGraphics();
      if (var1 != null) {
         try {
            ((Graphics2D)var1).setComposite(AlphaComposite.Src);
            var1.drawImage(this.getSecurityIconInfo().getImage(), 0, 0, (ImageObserver)null);
         } finally {
            var1.dispose();
         }
      }

   }

   private void replaceSurface() {
      SurfaceData var1 = this.contentView.getSurfaceData();
      this.replaceSurfaceData();
      if (var1 != null && var1 != this.contentView.getSurfaceData()) {
         var1.flush();
      }

   }

   private int getWidth() {
      return this.getSecurityIconInfo().getWidth();
   }

   private int getHeight() {
      return this.getSecurityIconInfo().getHeight();
   }

   private IconInfo getSecurityIconInfo() {
      return getSecurityIconInfo(this.currentSize, this.currentIcon);
   }

   private static class Lock {
      private Lock() {
      }

      // $FF: synthetic method
      Lock(Object var1) {
         this();
      }
   }
}
