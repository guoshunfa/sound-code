package java.awt;

import java.awt.event.AWTEventListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.awt.dnd.SunDropTargetEvent;
import sun.util.logging.PlatformLogger;

class LightweightDispatcher implements Serializable, AWTEventListener {
   private static final long serialVersionUID = 5184291520170872969L;
   private static final int LWD_MOUSE_DRAGGED_OVER = 1500;
   private static final PlatformLogger eventLog = PlatformLogger.getLogger("java.awt.event.LightweightDispatcher");
   private static final int BUTTONS_DOWN_MASK;
   private Container nativeContainer;
   private Component focus;
   private transient WeakReference<Component> mouseEventTarget;
   private transient WeakReference<Component> targetLastEntered;
   private transient WeakReference<Component> targetLastEnteredDT;
   private transient boolean isMouseInNativeContainer = false;
   private transient boolean isMouseDTInNativeContainer = false;
   private Cursor nativeCursor;
   private long eventMask;
   private static final long PROXY_EVENT_MASK = 131132L;
   private static final long MOUSE_MASK = 131120L;

   LightweightDispatcher(Container var1) {
      this.nativeContainer = var1;
      this.mouseEventTarget = new WeakReference((Object)null);
      this.targetLastEntered = new WeakReference((Object)null);
      this.targetLastEnteredDT = new WeakReference((Object)null);
      this.eventMask = 0L;
   }

   void dispose() {
      this.stopListeningForOtherDrags();
      this.mouseEventTarget.clear();
      this.targetLastEntered.clear();
      this.targetLastEnteredDT.clear();
   }

   void enableEvents(long var1) {
      this.eventMask |= var1;
   }

   boolean dispatchEvent(AWTEvent var1) {
      boolean var2 = false;
      if (var1 instanceof SunDropTargetEvent) {
         SunDropTargetEvent var3 = (SunDropTargetEvent)var1;
         var2 = this.processDropTargetEvent(var3);
      } else {
         if (var1 instanceof MouseEvent && (this.eventMask & 131120L) != 0L) {
            MouseEvent var4 = (MouseEvent)var1;
            var2 = this.processMouseEvent(var4);
         }

         if (var1.getID() == 503) {
            this.nativeContainer.updateCursorImmediately();
         }
      }

      return var2;
   }

   private boolean isMouseGrab(MouseEvent var1) {
      int var2 = var1.getModifiersEx();
      if (var1.getID() == 501 || var1.getID() == 502) {
         var2 ^= InputEvent.getMaskForButton(var1.getButton());
      }

      return (var2 & BUTTONS_DOWN_MASK) != 0;
   }

   private boolean processMouseEvent(MouseEvent var1) {
      int var2 = var1.getID();
      Component var3 = this.nativeContainer.getMouseEventTarget(var1.getX(), var1.getY(), true);
      this.trackMouseEnterExit(var3, var1);
      Component var4 = (Component)this.mouseEventTarget.get();
      if (!this.isMouseGrab(var1) && var2 != 500) {
         var4 = var3 != this.nativeContainer ? var3 : null;
         this.mouseEventTarget = new WeakReference(var4);
      }

      if (var4 != null) {
         switch(var2) {
         case 500:
            if (var3 == var4) {
               this.retargetMouseEvent(var3, var2, var1);
            }
            break;
         case 501:
            this.retargetMouseEvent(var4, var2, var1);
            break;
         case 502:
            this.retargetMouseEvent(var4, var2, var1);
            break;
         case 503:
            this.retargetMouseEvent(var4, var2, var1);
         case 504:
         case 505:
         default:
            break;
         case 506:
            if (this.isMouseGrab(var1)) {
               this.retargetMouseEvent(var4, var2, var1);
            }
            break;
         case 507:
            if (eventLog.isLoggable(PlatformLogger.Level.FINEST) && var3 != null) {
               eventLog.finest("retargeting mouse wheel to " + var3.getName() + ", " + var3.getClass());
            }

            this.retargetMouseEvent(var3, var2, var1);
         }

         if (var2 != 507) {
            var1.consume();
         }
      }

      return var1.isConsumed();
   }

   private boolean processDropTargetEvent(SunDropTargetEvent var1) {
      int var2 = var1.getID();
      int var3 = var1.getX();
      int var4 = var1.getY();
      if (!this.nativeContainer.contains(var3, var4)) {
         Dimension var5 = this.nativeContainer.getSize();
         if (var5.width <= var3) {
            var3 = var5.width - 1;
         } else if (var3 < 0) {
            var3 = 0;
         }

         if (var5.height <= var4) {
            var4 = var5.height - 1;
         } else if (var4 < 0) {
            var4 = 0;
         }
      }

      Component var6 = this.nativeContainer.getDropTargetEventTarget(var3, var4, true);
      this.trackMouseEnterExit(var6, var1);
      if (var6 != this.nativeContainer && var6 != null) {
         switch(var2) {
         case 504:
         case 505:
            break;
         default:
            this.retargetMouseEvent(var6, var2, var1);
            var1.consume();
         }
      }

      return var1.isConsumed();
   }

   private void trackDropTargetEnterExit(Component var1, MouseEvent var2) {
      int var3 = var2.getID();
      if (var3 == 504 && this.isMouseDTInNativeContainer) {
         this.targetLastEnteredDT.clear();
      } else if (var3 == 504) {
         this.isMouseDTInNativeContainer = true;
      } else if (var3 == 505) {
         this.isMouseDTInNativeContainer = false;
      }

      Component var4 = this.retargetMouseEnterExit(var1, var2, (Component)this.targetLastEnteredDT.get(), this.isMouseDTInNativeContainer);
      this.targetLastEnteredDT = new WeakReference(var4);
   }

   private void trackMouseEnterExit(Component var1, MouseEvent var2) {
      if (var2 instanceof SunDropTargetEvent) {
         this.trackDropTargetEnterExit(var1, var2);
      } else {
         int var3 = var2.getID();
         if (var3 != 505 && var3 != 506 && var3 != 1500 && !this.isMouseInNativeContainer) {
            this.isMouseInNativeContainer = true;
            this.startListeningForOtherDrags();
         } else if (var3 == 505) {
            this.isMouseInNativeContainer = false;
            this.stopListeningForOtherDrags();
         }

         Component var4 = this.retargetMouseEnterExit(var1, var2, (Component)this.targetLastEntered.get(), this.isMouseInNativeContainer);
         this.targetLastEntered = new WeakReference(var4);
      }
   }

   private Component retargetMouseEnterExit(Component var1, MouseEvent var2, Component var3, boolean var4) {
      int var5 = var2.getID();
      Component var6 = var4 ? var1 : null;
      if (var3 != var6) {
         if (var3 != null) {
            this.retargetMouseEvent(var3, 505, var2);
         }

         if (var5 == 505) {
            var2.consume();
         }

         if (var6 != null) {
            this.retargetMouseEvent(var6, 504, var2);
         }

         if (var5 == 504) {
            var2.consume();
         }
      }

      return var6;
   }

   private void startListeningForOtherDrags() {
      AccessController.doPrivileged(new PrivilegedAction<Object>() {
         public Object run() {
            LightweightDispatcher.this.nativeContainer.getToolkit().addAWTEventListener(LightweightDispatcher.this, 48L);
            return null;
         }
      });
   }

   private void stopListeningForOtherDrags() {
      AccessController.doPrivileged(new PrivilegedAction<Object>() {
         public Object run() {
            LightweightDispatcher.this.nativeContainer.getToolkit().removeAWTEventListener(LightweightDispatcher.this);
            return null;
         }
      });
   }

   public void eventDispatched(AWTEvent var1) {
      boolean var2 = var1 instanceof MouseEvent && !(var1 instanceof SunDropTargetEvent) && var1.id == 506 && var1.getSource() != this.nativeContainer;
      if (var2) {
         MouseEvent var3 = (MouseEvent)var1;
         final MouseEvent var4;
         synchronized(this.nativeContainer.getTreeLock()) {
            Component var6 = var3.getComponent();
            if (!var6.isShowing()) {
               return;
            }

            Container var7;
            for(var7 = this.nativeContainer; var7 != null && !(var7 instanceof Window); var7 = var7.getParent_NoClientCode()) {
            }

            if (var7 == null || ((Window)var7).isModalBlocked()) {
               return;
            }

            var4 = new MouseEvent(this.nativeContainer, 1500, var3.getWhen(), var3.getModifiersEx() | var3.getModifiers(), var3.getX(), var3.getY(), var3.getXOnScreen(), var3.getYOnScreen(), var3.getClickCount(), var3.isPopupTrigger(), var3.getButton());
            AWTAccessor.MouseEventAccessor var8 = AWTAccessor.getMouseEventAccessor();
            var8.setCausedByTouchEvent(var4, var8.isCausedByTouchEvent(var3));
            var3.copyPrivateDataInto(var4);
            final Point var9 = var6.getLocationOnScreen();
            if (AppContext.getAppContext() != this.nativeContainer.appContext) {
               Runnable var11 = new Runnable() {
                  public void run() {
                     if (LightweightDispatcher.this.nativeContainer.isShowing()) {
                        Point var1 = LightweightDispatcher.this.nativeContainer.getLocationOnScreen();
                        var4.translatePoint(var9.x - var1.x, var9.y - var1.y);
                        Component var2 = LightweightDispatcher.this.nativeContainer.getMouseEventTarget(var4.getX(), var4.getY(), true);
                        LightweightDispatcher.this.trackMouseEnterExit(var2, var4);
                     }
                  }
               };
               SunToolkit.executeOnEventHandlerThread(this.nativeContainer, var11);
               return;
            }

            if (!this.nativeContainer.isShowing()) {
               return;
            }

            Point var10 = this.nativeContainer.getLocationOnScreen();
            var4.translatePoint(var9.x - var10.x, var9.y - var10.y);
         }

         Component var5 = this.nativeContainer.getMouseEventTarget(var4.getX(), var4.getY(), true);
         this.trackMouseEnterExit(var5, var4);
      }
   }

   void retargetMouseEvent(Component var1, int var2, MouseEvent var3) {
      if (var1 != null) {
         int var4 = var3.getX();
         int var5 = var3.getY();

         Object var6;
         for(var6 = var1; var6 != null && var6 != this.nativeContainer; var6 = ((Component)var6).getParent()) {
            var4 -= ((Component)var6).x;
            var5 -= ((Component)var6).y;
         }

         if (var6 != null) {
            Object var7;
            if (var3 instanceof SunDropTargetEvent) {
               var7 = new SunDropTargetEvent(var1, var2, var4, var5, ((SunDropTargetEvent)var3).getDispatcher());
            } else if (var2 == 507) {
               var7 = new MouseWheelEvent(var1, var2, var3.getWhen(), var3.getModifiersEx() | var3.getModifiers(), var4, var5, var3.getXOnScreen(), var3.getYOnScreen(), var3.getClickCount(), var3.isPopupTrigger(), ((MouseWheelEvent)var3).getScrollType(), ((MouseWheelEvent)var3).getScrollAmount(), ((MouseWheelEvent)var3).getWheelRotation(), ((MouseWheelEvent)var3).getPreciseWheelRotation());
            } else {
               var7 = new MouseEvent(var1, var2, var3.getWhen(), var3.getModifiersEx() | var3.getModifiers(), var4, var5, var3.getXOnScreen(), var3.getYOnScreen(), var3.getClickCount(), var3.isPopupTrigger(), var3.getButton());
               AWTAccessor.MouseEventAccessor var8 = AWTAccessor.getMouseEventAccessor();
               var8.setCausedByTouchEvent((MouseEvent)var7, var8.isCausedByTouchEvent(var3));
            }

            var3.copyPrivateDataInto((AWTEvent)var7);
            if (var1 == this.nativeContainer) {
               ((Container)var1).dispatchEventToSelf((AWTEvent)var7);
            } else {
               assert AppContext.getAppContext() == var1.appContext;

               if (this.nativeContainer.modalComp != null) {
                  if (((Container)this.nativeContainer.modalComp).isAncestorOf(var1)) {
                     var1.dispatchEvent((AWTEvent)var7);
                  } else {
                     var3.consume();
                  }
               } else {
                  var1.dispatchEvent((AWTEvent)var7);
               }
            }

            if (var2 == 507 && ((MouseEvent)var7).isConsumed()) {
               var3.consume();
            }
         }

      }
   }

   static {
      int[] var0 = AWTAccessor.getInputEventAccessor().getButtonDownMasks();
      int var1 = 0;
      int[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = var2[var4];
         var1 |= var5;
      }

      BUTTONS_DOWN_MASK = var1;
   }
}
