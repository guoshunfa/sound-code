package sun.awt;

import java.awt.AWTEvent;
import java.awt.EventQueue;

public class EventQueueDelegate {
   private static final Object EVENT_QUEUE_DELEGATE_KEY = new StringBuilder("EventQueueDelegate.Delegate");

   public static void setDelegate(EventQueueDelegate.Delegate var0) {
      AppContext.getAppContext().put(EVENT_QUEUE_DELEGATE_KEY, var0);
   }

   public static EventQueueDelegate.Delegate getDelegate() {
      return (EventQueueDelegate.Delegate)AppContext.getAppContext().get(EVENT_QUEUE_DELEGATE_KEY);
   }

   public interface Delegate {
      AWTEvent getNextEvent(EventQueue var1) throws InterruptedException;

      Object beforeDispatch(AWTEvent var1) throws InterruptedException;

      void afterDispatch(AWTEvent var1, Object var2) throws InterruptedException;
   }
}
