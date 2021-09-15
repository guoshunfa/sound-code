package java.awt;

import java.awt.peer.LightweightPeer;
import sun.awt.SunGraphicsCallback;

abstract class GraphicsCallback extends SunGraphicsCallback {
   static final class PrintHeavyweightComponentsCallback extends GraphicsCallback {
      private static GraphicsCallback.PrintHeavyweightComponentsCallback instance = new GraphicsCallback.PrintHeavyweightComponentsCallback();

      private PrintHeavyweightComponentsCallback() {
      }

      public void run(Component var1, Graphics var2) {
         if (var1.peer instanceof LightweightPeer) {
            var1.printHeavyweightComponents(var2);
         } else {
            var1.printAll(var2);
         }

      }

      static GraphicsCallback.PrintHeavyweightComponentsCallback getInstance() {
         return instance;
      }
   }

   static final class PaintHeavyweightComponentsCallback extends GraphicsCallback {
      private static GraphicsCallback.PaintHeavyweightComponentsCallback instance = new GraphicsCallback.PaintHeavyweightComponentsCallback();

      private PaintHeavyweightComponentsCallback() {
      }

      public void run(Component var1, Graphics var2) {
         if (var1.peer instanceof LightweightPeer) {
            var1.paintHeavyweightComponents(var2);
         } else {
            var1.paintAll(var2);
         }

      }

      static GraphicsCallback.PaintHeavyweightComponentsCallback getInstance() {
         return instance;
      }
   }

   static final class PeerPrintCallback extends GraphicsCallback {
      private static GraphicsCallback.PeerPrintCallback instance = new GraphicsCallback.PeerPrintCallback();

      private PeerPrintCallback() {
      }

      public void run(Component var1, Graphics var2) {
         var1.validate();
         if (var1.peer instanceof LightweightPeer) {
            var1.lightweightPrint(var2);
         } else {
            var1.peer.print(var2);
         }

      }

      static GraphicsCallback.PeerPrintCallback getInstance() {
         return instance;
      }
   }

   static final class PeerPaintCallback extends GraphicsCallback {
      private static GraphicsCallback.PeerPaintCallback instance = new GraphicsCallback.PeerPaintCallback();

      private PeerPaintCallback() {
      }

      public void run(Component var1, Graphics var2) {
         var1.validate();
         if (var1.peer instanceof LightweightPeer) {
            var1.lightweightPaint(var2);
         } else {
            var1.peer.paint(var2);
         }

      }

      static GraphicsCallback.PeerPaintCallback getInstance() {
         return instance;
      }
   }

   static final class PrintAllCallback extends GraphicsCallback {
      private static GraphicsCallback.PrintAllCallback instance = new GraphicsCallback.PrintAllCallback();

      private PrintAllCallback() {
      }

      public void run(Component var1, Graphics var2) {
         var1.printAll(var2);
      }

      static GraphicsCallback.PrintAllCallback getInstance() {
         return instance;
      }
   }

   static final class PaintAllCallback extends GraphicsCallback {
      private static GraphicsCallback.PaintAllCallback instance = new GraphicsCallback.PaintAllCallback();

      private PaintAllCallback() {
      }

      public void run(Component var1, Graphics var2) {
         var1.paintAll(var2);
      }

      static GraphicsCallback.PaintAllCallback getInstance() {
         return instance;
      }
   }

   static final class PrintCallback extends GraphicsCallback {
      private static GraphicsCallback.PrintCallback instance = new GraphicsCallback.PrintCallback();

      private PrintCallback() {
      }

      public void run(Component var1, Graphics var2) {
         var1.print(var2);
      }

      static GraphicsCallback.PrintCallback getInstance() {
         return instance;
      }
   }

   static final class PaintCallback extends GraphicsCallback {
      private static GraphicsCallback.PaintCallback instance = new GraphicsCallback.PaintCallback();

      private PaintCallback() {
      }

      public void run(Component var1, Graphics var2) {
         var1.paint(var2);
      }

      static GraphicsCallback.PaintCallback getInstance() {
         return instance;
      }
   }
}
