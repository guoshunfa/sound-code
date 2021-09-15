package java.awt.peer;

import java.awt.Rectangle;

public interface RobotPeer {
   void mouseMove(int var1, int var2);

   void mousePress(int var1);

   void mouseRelease(int var1);

   void mouseWheel(int var1);

   void keyPress(int var1);

   void keyRelease(int var1);

   int getRGBPixel(int var1, int var2);

   int[] getRGBPixels(Rectangle var1);

   void dispose();
}
