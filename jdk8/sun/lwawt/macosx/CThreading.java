package sun.lwawt.macosx;

import java.awt.EventQueue;

public class CThreading {
   static String APPKIT_THREAD_NAME = "AppKit Thread";

   static boolean isEventQueue() {
      return EventQueue.isDispatchThread();
   }

   static boolean isAppKit() {
      return APPKIT_THREAD_NAME.equals(Thread.currentThread().getName());
   }

   static boolean assertEventQueue() {
      boolean var0 = isEventQueue();

      assert var0 : "Threading violation: not EventQueue thread";

      return var0;
   }

   static boolean assertNotEventQueue() {
      boolean var0 = isEventQueue();

      assert var0 : "Threading violation: EventQueue thread";

      return var0;
   }

   static boolean assertAppKit() {
      boolean var0 = isAppKit();

      assert var0 : "Threading violation: not AppKit thread";

      return var0;
   }

   static boolean assertNotAppKit() {
      boolean var0 = !isAppKit();

      assert var0 : "Threading violation: AppKit thread";

      return var0;
   }
}
