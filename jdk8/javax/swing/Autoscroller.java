package javax.swing;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import sun.awt.AWTAccessor;

class Autoscroller implements ActionListener {
   private static Autoscroller sharedInstance = new Autoscroller();
   private static MouseEvent event;
   private static Timer timer;
   private static JComponent component;

   public static void stop(JComponent var0) {
      sharedInstance._stop(var0);
   }

   public static boolean isRunning(JComponent var0) {
      return sharedInstance._isRunning(var0);
   }

   public static void processMouseDragged(MouseEvent var0) {
      sharedInstance._processMouseDragged(var0);
   }

   private void start(JComponent var1, MouseEvent var2) {
      Point var3 = var1.getLocationOnScreen();
      if (component != var1) {
         this._stop(component);
      }

      component = var1;
      event = new MouseEvent(component, var2.getID(), var2.getWhen(), var2.getModifiers(), var2.getX() + var3.x, var2.getY() + var3.y, var2.getXOnScreen(), var2.getYOnScreen(), var2.getClickCount(), var2.isPopupTrigger(), 0);
      AWTAccessor.MouseEventAccessor var4 = AWTAccessor.getMouseEventAccessor();
      var4.setCausedByTouchEvent(event, var4.isCausedByTouchEvent(var2));
      if (timer == null) {
         timer = new Timer(100, this);
      }

      if (!timer.isRunning()) {
         timer.start();
      }

   }

   private void _stop(JComponent var1) {
      if (component == var1) {
         if (timer != null) {
            timer.stop();
         }

         timer = null;
         event = null;
         component = null;
      }

   }

   private boolean _isRunning(JComponent var1) {
      return var1 == component && timer != null && timer.isRunning();
   }

   private void _processMouseDragged(MouseEvent var1) {
      JComponent var2 = (JComponent)var1.getComponent();
      boolean var3 = true;
      if (var2.isShowing()) {
         Rectangle var4 = var2.getVisibleRect();
         var3 = var4.contains(var1.getX(), var1.getY());
      }

      if (var3) {
         this._stop(var2);
      } else {
         this.start(var2, var1);
      }

   }

   public void actionPerformed(ActionEvent var1) {
      JComponent var2 = component;
      if (var2 != null && var2.isShowing() && event != null) {
         Point var3 = var2.getLocationOnScreen();
         MouseEvent var4 = new MouseEvent(var2, event.getID(), event.getWhen(), event.getModifiers(), event.getX() - var3.x, event.getY() - var3.y, event.getXOnScreen(), event.getYOnScreen(), event.getClickCount(), event.isPopupTrigger(), 0);
         AWTAccessor.MouseEventAccessor var5 = AWTAccessor.getMouseEventAccessor();
         var5.setCausedByTouchEvent(var4, var5.isCausedByTouchEvent(event));
         var2.superProcessMouseMotionEvent(var4);
      } else {
         this._stop(var2);
      }
   }
}
