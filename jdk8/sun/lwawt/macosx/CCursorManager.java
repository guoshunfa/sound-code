package sun.lwawt.macosx;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.geom.Point2D;
import sun.lwawt.LWCursorManager;

final class CCursorManager extends LWCursorManager {
   private static final int NAMED_CURSOR = -1;
   private static final CCursorManager theInstance = new CCursorManager();
   private volatile Cursor currentCursor;

   private static native Point2D nativeGetCursorPosition();

   private static native void nativeSetBuiltInCursor(int var0, String var1);

   private static native void nativeSetCustomCursor(long var0, double var2, double var4);

   public static native void nativeSetAllowsCursorSetInBackground(boolean var0);

   public static CCursorManager getInstance() {
      return theInstance;
   }

   private CCursorManager() {
   }

   protected Point getCursorPosition() {
      Point2D var1 = nativeGetCursorPosition();
      return new Point((int)var1.getX(), (int)var1.getY());
   }

   protected void setCursor(Cursor var1) {
      if (var1 != this.currentCursor) {
         this.currentCursor = var1;
         if (var1 == null) {
            nativeSetBuiltInCursor(0, (String)null);
         } else if (var1 instanceof CCustomCursor) {
            CCustomCursor var6 = (CCustomCursor)var1;
            long var7 = var6.getImageData();
            if (var7 != 0L) {
               Point var5 = var6.getHotSpot();
               nativeSetCustomCursor(var7, (double)var5.x, (double)var5.y);
            }

         } else {
            int var2 = var1.getType();
            if (var2 != -1) {
               nativeSetBuiltInCursor(var2, (String)null);
            } else {
               String var3 = var1.getName();
               if (var3 != null) {
                  nativeSetBuiltInCursor(-1, var3);
               } else {
                  throw new RuntimeException("Unimplemented");
               }
            }
         }
      }
   }
}
