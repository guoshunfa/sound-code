package sun.lwawt.macosx;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.peer.RobotPeer;
import sun.awt.CGraphicsDevice;

class CRobot implements RobotPeer {
   private static final int MOUSE_LOCATION_UNKNOWN = -1;
   private final CGraphicsDevice fDevice;
   private int mouseLastX = -1;
   private int mouseLastY = -1;
   private int mouseButtonsState = 0;

   public CRobot(Robot var1, CGraphicsDevice var2) {
      this.fDevice = var2;
      this.initRobot();
   }

   public void dispose() {
   }

   public void mouseMove(int var1, int var2) {
      this.mouseLastX = var1;
      this.mouseLastY = var2;
      this.mouseEvent(this.fDevice.getCGDisplayID(), this.mouseLastX, this.mouseLastY, this.mouseButtonsState, true, true);
   }

   public void mousePress(int var1) {
      this.mouseButtonsState |= var1;
      this.checkMousePos();
      this.mouseEvent(this.fDevice.getCGDisplayID(), this.mouseLastX, this.mouseLastY, var1, true, false);
   }

   public void mouseRelease(int var1) {
      this.mouseButtonsState &= ~var1;
      this.checkMousePos();
      this.mouseEvent(this.fDevice.getCGDisplayID(), this.mouseLastX, this.mouseLastY, var1, false, false);
   }

   private void checkMousePos() {
      if (this.mouseLastX == -1 || this.mouseLastY == -1) {
         Rectangle var1 = this.fDevice.getDefaultConfiguration().getBounds();
         Point var2 = CCursorManager.getInstance().getCursorPosition();
         if (var2.x < var1.x) {
            var2.x = var1.x;
         } else if (var2.x > var1.x + var1.width) {
            var2.x = var1.x + var1.width;
         }

         if (var2.y < var1.y) {
            var2.y = var1.y;
         } else if (var2.y > var1.y + var1.height) {
            var2.y = var1.y + var1.height;
         }

         this.mouseLastX = var2.x;
         this.mouseLastY = var2.y;
      }

   }

   public native void mouseWheel(int var1);

   public void keyPress(int var1) {
      this.keyEvent(var1, true);
   }

   public void keyRelease(int var1) {
      this.keyEvent(var1, false);
   }

   public int getRGBPixel(int var1, int var2) {
      int[] var3 = new int[1];
      this.getScreenPixels(new Rectangle(var1, var2, 1, 1), var3);
      return var3[0];
   }

   public int[] getRGBPixels(Rectangle var1) {
      int[] var2 = new int[var1.width * var1.height];
      this.getScreenPixels(var1, var2);
      return var2;
   }

   private native void initRobot();

   private native void mouseEvent(int var1, int var2, int var3, int var4, boolean var5, boolean var6);

   private native void keyEvent(int var1, boolean var2);

   private void getScreenPixels(Rectangle var1, int[] var2) {
      this.nativeGetScreenPixels(var1.x, var1.y, var1.width, var1.height, var2);
   }

   private native void nativeGetScreenPixels(int var1, int var2, int var3, int var4, int[] var5);
}
