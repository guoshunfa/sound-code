package com.apple.eawt.event;

import java.awt.Component;
import java.awt.Window;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import sun.awt.SunToolkit;

final class GestureHandler {
   private static final String CLIENT_PROPERTY = "com.apple.eawt.event.internalGestureHandler";
   static final int PHASE = 1;
   static final int ROTATE = 2;
   static final int MAGNIFY = 3;
   static final int SWIPE = 4;
   final List<GesturePhaseListener> phasers = new LinkedList();
   final List<RotationListener> rotaters = new LinkedList();
   final List<MagnificationListener> magnifiers = new LinkedList();
   final List<SwipeListener> swipers = new LinkedList();

   static void addGestureListenerTo(JComponent var0, GestureListener var1) {
      Object var2 = var0.getClientProperty("com.apple.eawt.event.internalGestureHandler");
      if (var2 instanceof GestureHandler) {
         ((GestureHandler)var2).addListener(var1);
      } else if (var2 == null) {
         GestureHandler var3 = new GestureHandler();
         var3.addListener(var1);
         var0.putClientProperty("com.apple.eawt.event.internalGestureHandler", var3);
      }
   }

   static void removeGestureListenerFrom(JComponent var0, GestureListener var1) {
      Object var2 = var0.getClientProperty("com.apple.eawt.event.internalGestureHandler");
      if (var2 instanceof GestureHandler) {
         ((GestureHandler)var2).removeListener(var1);
      }
   }

   static void handleGestureFromNative(final Window var0, final int var1, final double var2, final double var4, final double var6, final double var8) {
      if (var0 != null) {
         SunToolkit.executeOnEventHandlerThread(var0, new Runnable() {
            public void run() {
               Component var1x = SwingUtilities.getDeepestComponentAt(var0, (int)var2, (int)var4);
               GestureHandler.PerComponentNotifier var2x;
               if (var1x instanceof RootPaneContainer) {
                  var2x = GestureHandler.getNextNotifierForComponent(((RootPaneContainer)var1x).getRootPane());
               } else {
                  var2x = GestureHandler.getNextNotifierForComponent(var1x);
               }

               if (var2x != null) {
                  switch(var1) {
                  case 1:
                     var2x.recursivelyHandlePhaseChange(var6, new GesturePhaseEvent());
                     return;
                  case 2:
                     var2x.recursivelyHandleRotate(new RotationEvent(var6));
                     return;
                  case 3:
                     var2x.recursivelyHandleMagnify(new MagnificationEvent(var6));
                     return;
                  case 4:
                     var2x.recursivelyHandleSwipe(var6, var8, new SwipeEvent());
                     return;
                  default:
                  }
               }
            }
         });
      }
   }

   void addListener(GestureListener var1) {
      if (var1 instanceof GesturePhaseListener) {
         this.phasers.add((GesturePhaseListener)var1);
      }

      if (var1 instanceof RotationListener) {
         this.rotaters.add((RotationListener)var1);
      }

      if (var1 instanceof MagnificationListener) {
         this.magnifiers.add((MagnificationListener)var1);
      }

      if (var1 instanceof SwipeListener) {
         this.swipers.add((SwipeListener)var1);
      }

   }

   void removeListener(GestureListener var1) {
      this.phasers.remove(var1);
      this.rotaters.remove(var1);
      this.magnifiers.remove(var1);
      this.swipers.remove(var1);
   }

   static GestureHandler getHandlerForComponent(Component var0) {
      if (!(var0 instanceof JComponent)) {
         return null;
      } else {
         Object var1 = ((JComponent)var0).getClientProperty("com.apple.eawt.event.internalGestureHandler");
         return !(var1 instanceof GestureHandler) ? null : (GestureHandler)var1;
      }
   }

   static GestureHandler.PerComponentNotifier getNextNotifierForComponent(Component var0) {
      if (var0 == null) {
         return null;
      } else {
         GestureHandler var1 = getHandlerForComponent(var0);
         return var1 != null ? new GestureHandler.PerComponentNotifier(var0, var1) : getNextNotifierForComponent(var0.getParent());
      }
   }

   static class PerComponentNotifier {
      final Component component;
      final GestureHandler handler;

      public PerComponentNotifier(Component var1, GestureHandler var2) {
         this.component = var1;
         this.handler = var2;
      }

      void recursivelyHandlePhaseChange(double var1, GesturePhaseEvent var3) {
         Iterator var4 = this.handler.phasers.iterator();

         do {
            if (!var4.hasNext()) {
               GestureHandler.PerComponentNotifier var6 = GestureHandler.getNextNotifierForComponent(this.component.getParent());
               if (var6 != null) {
                  var6.recursivelyHandlePhaseChange(var1, var3);
               }

               return;
            }

            GesturePhaseListener var5 = (GesturePhaseListener)var4.next();
            if (var1 < 0.0D) {
               var5.gestureBegan(var3);
            } else {
               var5.gestureEnded(var3);
            }
         } while(!var3.isConsumed());

      }

      void recursivelyHandleRotate(RotationEvent var1) {
         Iterator var2 = this.handler.rotaters.iterator();

         do {
            if (!var2.hasNext()) {
               GestureHandler.PerComponentNotifier var4 = GestureHandler.getNextNotifierForComponent(this.component.getParent());
               if (var4 != null) {
                  var4.recursivelyHandleRotate(var1);
               }

               return;
            }

            RotationListener var3 = (RotationListener)var2.next();
            var3.rotate(var1);
         } while(!var1.isConsumed());

      }

      void recursivelyHandleMagnify(MagnificationEvent var1) {
         Iterator var2 = this.handler.magnifiers.iterator();

         do {
            if (!var2.hasNext()) {
               GestureHandler.PerComponentNotifier var4 = GestureHandler.getNextNotifierForComponent(this.component.getParent());
               if (var4 != null) {
                  var4.recursivelyHandleMagnify(var1);
               }

               return;
            }

            MagnificationListener var3 = (MagnificationListener)var2.next();
            var3.magnify(var1);
         } while(!var1.isConsumed());

      }

      void recursivelyHandleSwipe(double var1, double var3, SwipeEvent var5) {
         Iterator var6 = this.handler.swipers.iterator();

         do {
            if (!var6.hasNext()) {
               GestureHandler.PerComponentNotifier var8 = GestureHandler.getNextNotifierForComponent(this.component.getParent());
               if (var8 != null) {
                  var8.recursivelyHandleSwipe(var1, var3, var5);
               }

               return;
            }

            SwipeListener var7 = (SwipeListener)var6.next();
            if (var1 < 0.0D) {
               var7.swipedLeft(var5);
            }

            if (var1 > 0.0D) {
               var7.swipedRight(var5);
            }

            if (var3 < 0.0D) {
               var7.swipedDown(var5);
            }

            if (var3 > 0.0D) {
               var7.swipedUp(var5);
            }
         } while(!var5.isConsumed());

      }
   }
}
