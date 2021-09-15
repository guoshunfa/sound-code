package sun.lwawt.macosx;

import sun.awt.Mutex;
import sun.awt.datatransfer.ToolkitThreadBlockedHandler;

final class CToolkitThreadBlockedHandler extends Mutex implements ToolkitThreadBlockedHandler {
   private long awtRunLoopMediator = 0L;
   private final boolean processEvents = true;

   public void enter() {
      if (!this.isOwned()) {
         throw new IllegalMonitorStateException();
      } else {
         this.awtRunLoopMediator = LWCToolkit.createAWTRunLoopMediator();
         this.unlock();
         LWCToolkit.doAWTRunLoop(this.awtRunLoopMediator, this.processEvents);
         this.lock();
      }
   }

   public void exit() {
      if (!this.isOwned()) {
         throw new IllegalMonitorStateException();
      } else {
         LWCToolkit.stopAWTRunLoop(this.awtRunLoopMediator);
         this.awtRunLoopMediator = 0L;
      }
   }
}
