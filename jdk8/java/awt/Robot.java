package java.awt;

import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.peer.RobotPeer;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import sun.awt.ComponentFactory;
import sun.awt.SunToolkit;
import sun.awt.image.SunWritableRaster;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;
import sun.security.util.SecurityConstants;

public class Robot {
   private static final int MAX_DELAY = 60000;
   private RobotPeer peer;
   private boolean isAutoWaitForIdle = false;
   private int autoDelay = 0;
   private static int LEGAL_BUTTON_MASK = 0;
   private DirectColorModel screenCapCM = null;
   private transient Object anchor = new Object();
   private transient Robot.RobotDisposer disposer;

   public Robot() throws AWTException {
      if (GraphicsEnvironment.isHeadless()) {
         throw new AWTException("headless environment");
      } else {
         this.init(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice());
      }
   }

   public Robot(GraphicsDevice var1) throws AWTException {
      this.checkIsScreenDevice(var1);
      this.init(var1);
   }

   private void init(GraphicsDevice var1) throws AWTException {
      this.checkRobotAllowed();
      Toolkit var2 = Toolkit.getDefaultToolkit();
      if (var2 instanceof ComponentFactory) {
         this.peer = ((ComponentFactory)var2).createRobot(this, var1);
         this.disposer = new Robot.RobotDisposer(this.peer);
         Disposer.addRecord(this.anchor, this.disposer);
      }

      initLegalButtonMask();
   }

   private static synchronized void initLegalButtonMask() {
      if (LEGAL_BUTTON_MASK == 0) {
         int var0 = 0;
         if (Toolkit.getDefaultToolkit().areExtraMouseButtonsEnabled() && Toolkit.getDefaultToolkit() instanceof SunToolkit) {
            int var1 = ((SunToolkit)((SunToolkit)Toolkit.getDefaultToolkit())).getNumberOfButtons();

            for(int var2 = 0; var2 < var1; ++var2) {
               var0 |= InputEvent.getMaskForButton(var2 + 1);
            }
         }

         var0 |= 7196;
         LEGAL_BUTTON_MASK = var0;
      }
   }

   private void checkRobotAllowed() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(SecurityConstants.AWT.CREATE_ROBOT_PERMISSION);
      }

   }

   private void checkIsScreenDevice(GraphicsDevice var1) {
      if (var1 == null || var1.getType() != 0) {
         throw new IllegalArgumentException("not a valid screen device");
      }
   }

   public synchronized void mouseMove(int var1, int var2) {
      this.peer.mouseMove(var1, var2);
      this.afterEvent();
   }

   public synchronized void mousePress(int var1) {
      this.checkButtonsArgument(var1);
      this.peer.mousePress(var1);
      this.afterEvent();
   }

   public synchronized void mouseRelease(int var1) {
      this.checkButtonsArgument(var1);
      this.peer.mouseRelease(var1);
      this.afterEvent();
   }

   private void checkButtonsArgument(int var1) {
      if ((var1 | LEGAL_BUTTON_MASK) != LEGAL_BUTTON_MASK) {
         throw new IllegalArgumentException("Invalid combination of button flags");
      }
   }

   public synchronized void mouseWheel(int var1) {
      this.peer.mouseWheel(var1);
      this.afterEvent();
   }

   public synchronized void keyPress(int var1) {
      this.checkKeycodeArgument(var1);
      this.peer.keyPress(var1);
      this.afterEvent();
   }

   public synchronized void keyRelease(int var1) {
      this.checkKeycodeArgument(var1);
      this.peer.keyRelease(var1);
      this.afterEvent();
   }

   private void checkKeycodeArgument(int var1) {
      if (var1 == 0) {
         throw new IllegalArgumentException("Invalid key code");
      }
   }

   public synchronized Color getPixelColor(int var1, int var2) {
      Color var3 = new Color(this.peer.getRGBPixel(var1, var2));
      return var3;
   }

   public synchronized BufferedImage createScreenCapture(Rectangle var1) {
      checkScreenCaptureAllowed();
      checkValidRect(var1);
      if (this.screenCapCM == null) {
         this.screenCapCM = new DirectColorModel(24, 16711680, 65280, 255);
      }

      Toolkit.getDefaultToolkit().sync();
      int[] var6 = new int[3];
      int[] var5 = this.peer.getRGBPixels(var1);
      DataBufferInt var3 = new DataBufferInt(var5, var5.length);
      var6[0] = this.screenCapCM.getRedMask();
      var6[1] = this.screenCapCM.getGreenMask();
      var6[2] = this.screenCapCM.getBlueMask();
      WritableRaster var4 = Raster.createPackedRaster(var3, var1.width, var1.height, var1.width, var6, (Point)null);
      SunWritableRaster.makeTrackable(var3);
      BufferedImage var2 = new BufferedImage(this.screenCapCM, var4, false, (Hashtable)null);
      return var2;
   }

   private static void checkValidRect(Rectangle var0) {
      if (var0.width <= 0 || var0.height <= 0) {
         throw new IllegalArgumentException("Rectangle width and height must be > 0");
      }
   }

   private static void checkScreenCaptureAllowed() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(SecurityConstants.AWT.READ_DISPLAY_PIXELS_PERMISSION);
      }

   }

   private void afterEvent() {
      this.autoWaitForIdle();
      this.autoDelay();
   }

   public synchronized boolean isAutoWaitForIdle() {
      return this.isAutoWaitForIdle;
   }

   public synchronized void setAutoWaitForIdle(boolean var1) {
      this.isAutoWaitForIdle = var1;
   }

   private void autoWaitForIdle() {
      if (this.isAutoWaitForIdle) {
         this.waitForIdle();
      }

   }

   public synchronized int getAutoDelay() {
      return this.autoDelay;
   }

   public synchronized void setAutoDelay(int var1) {
      this.checkDelayArgument(var1);
      this.autoDelay = var1;
   }

   private void autoDelay() {
      this.delay(this.autoDelay);
   }

   public synchronized void delay(int var1) {
      this.checkDelayArgument(var1);

      try {
         Thread.sleep((long)var1);
      } catch (InterruptedException var3) {
         var3.printStackTrace();
      }

   }

   private void checkDelayArgument(int var1) {
      if (var1 < 0 || var1 > 60000) {
         throw new IllegalArgumentException("Delay must be to 0 to 60,000ms");
      }
   }

   public synchronized void waitForIdle() {
      this.checkNotDispatchThread();

      try {
         SunToolkit.flushPendingEvents();
         EventQueue.invokeAndWait(new Runnable() {
            public void run() {
            }
         });
      } catch (InterruptedException var2) {
         System.err.println("Robot.waitForIdle, non-fatal exception caught:");
         var2.printStackTrace();
      } catch (InvocationTargetException var3) {
         System.err.println("Robot.waitForIdle, non-fatal exception caught:");
         var3.printStackTrace();
      }

   }

   private void checkNotDispatchThread() {
      if (EventQueue.isDispatchThread()) {
         throw new IllegalThreadStateException("Cannot call method from the event dispatcher thread");
      }
   }

   public synchronized String toString() {
      String var1 = "autoDelay = " + this.getAutoDelay() + ", autoWaitForIdle = " + this.isAutoWaitForIdle();
      return this.getClass().getName() + "[ " + var1 + " ]";
   }

   static class RobotDisposer implements DisposerRecord {
      private final RobotPeer peer;

      public RobotDisposer(RobotPeer var1) {
         this.peer = var1;
      }

      public void dispose() {
         if (this.peer != null) {
            this.peer.dispose();
         }

      }
   }
}
