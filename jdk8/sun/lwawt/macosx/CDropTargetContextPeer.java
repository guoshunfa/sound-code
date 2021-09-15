package sun.lwawt.macosx;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DropTarget;
import javax.swing.SwingUtilities;
import sun.awt.dnd.SunDropTargetContextPeer;
import sun.awt.dnd.SunDropTargetEvent;

final class CDropTargetContextPeer extends SunDropTargetContextPeer {
   private long fNativeDropTransfer = 0L;
   private long fNativeDataAvailable = 0L;
   private Object fNativeData = null;
   private DropTarget insideTarget = null;
   Object awtLockAccess = new Object();

   static CDropTargetContextPeer getDropTargetContextPeer() {
      return new CDropTargetContextPeer();
   }

   private CDropTargetContextPeer() {
   }

   private void flushEvents(Component var1) {
      try {
         LWCToolkit.invokeAndWait(new Runnable() {
            public synchronized void run() {
            }
         }, var1);
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   protected Object getNativeData(long var1) {
      long var3 = this.getNativeDragContext();
      synchronized(this.awtLockAccess) {
         this.fNativeDataAvailable = 0L;
         if (this.fNativeDropTransfer == 0L) {
            this.fNativeDropTransfer = this.startTransfer(var3, var1);
         } else {
            this.addTransfer(var3, this.fNativeDropTransfer, var1);
         }

         while(var1 != this.fNativeDataAvailable) {
            try {
               this.awtLockAccess.wait();
            } catch (Throwable var8) {
               var8.printStackTrace();
            }
         }

         return this.fNativeData;
      }
   }

   protected void processMotionMessage(SunDropTargetEvent var1, boolean var2) {
      boolean var3 = this.isEventInsideTarget(var1);
      if (var1.getComponent().getDropTarget() == this.insideTarget) {
         if (!var3) {
            this.processExitMessage(var1);
            return;
         }
      } else {
         if (!var3) {
            return;
         }

         this.processEnterMessage(var1);
      }

      super.processMotionMessage(var1, var2);
   }

   protected void processEnterMessage(SunDropTargetEvent var1) {
      Component var2 = var1.getComponent();
      DropTarget var3 = var1.getComponent().getDropTarget();
      if (this.isEventInsideTarget(var1) && var3 != this.insideTarget && var2.isShowing() && var3 != null && var3.isActive()) {
         this.insideTarget = var3;
         super.processEnterMessage(var1);
      }

   }

   protected void processExitMessage(SunDropTargetEvent var1) {
      if (var1.getComponent().getDropTarget() == this.insideTarget) {
         this.insideTarget = null;
         super.processExitMessage(var1);
      }

   }

   protected void processDropMessage(SunDropTargetEvent var1) {
      if (this.isEventInsideTarget(var1)) {
         super.processDropMessage(var1);
         this.insideTarget = null;
      }

   }

   private boolean isEventInsideTarget(SunDropTargetEvent var1) {
      Component var2 = var1.getComponent();
      Point var3 = var1.getPoint();
      SwingUtilities.convertPointToScreen(var3, var2);
      Point var4 = var2.getLocationOnScreen();
      Rectangle var5 = new Rectangle(var4.x, var4.y, var2.getWidth(), var2.getHeight());
      return var5.contains(var3);
   }

   protected int postDropTargetEvent(Component var1, int var2, int var3, int var4, int var5, long[] var6, long var7, int var9, boolean var10) {
      return super.postDropTargetEvent(var1, var2, var3, var4, var5, var6, var7, var9, true);
   }

   protected void doDropDone(boolean var1, int var2, boolean var3) {
      long var4 = this.getNativeDragContext();
      this.dropDone(var4, this.fNativeDropTransfer, var3, var1, var2);
   }

   private void newData(long var1, byte[] var3) {
      this.fNativeDataAvailable = var1;
      this.fNativeData = var3;
      this.awtLockAccess.notifyAll();
   }

   private void transferFailed(long var1) {
      this.fNativeDataAvailable = var1;
      this.fNativeData = null;
      this.awtLockAccess.notifyAll();
   }

   private native long startTransfer(long var1, long var3);

   private native void addTransfer(long var1, long var3, long var5);

   private native void dropDone(long var1, long var3, boolean var5, boolean var6, int var7);
}
