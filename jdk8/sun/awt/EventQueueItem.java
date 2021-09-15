package sun.awt;

import java.awt.AWTEvent;

public class EventQueueItem {
   public AWTEvent event;
   public EventQueueItem next;

   public EventQueueItem(AWTEvent var1) {
      this.event = var1;
   }
}
