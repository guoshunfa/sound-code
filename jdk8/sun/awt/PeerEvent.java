package sun.awt;

import java.awt.event.InvocationEvent;

public class PeerEvent extends InvocationEvent {
   public static final long PRIORITY_EVENT = 1L;
   public static final long ULTIMATE_PRIORITY_EVENT = 2L;
   public static final long LOW_PRIORITY_EVENT = 4L;
   private long flags;

   public PeerEvent(Object var1, Runnable var2, long var3) {
      this(var1, var2, (Object)null, false, var3);
   }

   public PeerEvent(Object var1, Runnable var2, Object var3, boolean var4, long var5) {
      super(var1, var2, var3, var4);
      this.flags = var5;
   }

   public long getFlags() {
      return this.flags;
   }

   public PeerEvent coalesceEvents(PeerEvent var1) {
      return null;
   }
}
