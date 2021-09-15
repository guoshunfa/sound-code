package java.awt;

import sun.security.util.SecurityConstants;

public class MouseInfo {
   private MouseInfo() {
   }

   public static PointerInfo getPointerInfo() throws HeadlessException {
      if (GraphicsEnvironment.isHeadless()) {
         throw new HeadlessException();
      } else {
         SecurityManager var0 = System.getSecurityManager();
         if (var0 != null) {
            var0.checkPermission(SecurityConstants.AWT.WATCH_MOUSE_PERMISSION);
         }

         Point var1 = new Point(0, 0);
         int var2 = Toolkit.getDefaultToolkit().getMouseInfoPeer().fillPointWithCoords(var1);
         GraphicsDevice[] var3 = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
         PointerInfo var4 = null;
         if (areScreenDevicesIndependent(var3)) {
            var4 = new PointerInfo(var3[var2], var1);
         } else {
            for(int var5 = 0; var5 < var3.length; ++var5) {
               GraphicsConfiguration var6 = var3[var5].getDefaultConfiguration();
               Rectangle var7 = var6.getBounds();
               if (var7.contains(var1)) {
                  var4 = new PointerInfo(var3[var5], var1);
               }
            }
         }

         return var4;
      }
   }

   private static boolean areScreenDevicesIndependent(GraphicsDevice[] var0) {
      for(int var1 = 0; var1 < var0.length; ++var1) {
         Rectangle var2 = var0[var1].getDefaultConfiguration().getBounds();
         if (var2.x != 0 || var2.y != 0) {
            return false;
         }
      }

      return true;
   }

   public static int getNumberOfButtons() throws HeadlessException {
      if (GraphicsEnvironment.isHeadless()) {
         throw new HeadlessException();
      } else {
         Object var0 = Toolkit.getDefaultToolkit().getDesktopProperty("awt.mouse.numButtons");
         if (var0 instanceof Integer) {
            return (Integer)var0;
         } else {
            assert false : "awt.mouse.numButtons is not an integer property";

            return 0;
         }
      }
   }
}
