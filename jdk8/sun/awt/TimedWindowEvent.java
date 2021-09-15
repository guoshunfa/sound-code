package sun.awt;

import java.awt.Window;
import java.awt.event.WindowEvent;

public class TimedWindowEvent extends WindowEvent {
   private long time;

   public long getWhen() {
      return this.time;
   }

   public TimedWindowEvent(Window var1, int var2, Window var3, long var4) {
      super(var1, var2, var3);
      this.time = var4;
   }

   public TimedWindowEvent(Window var1, int var2, Window var3, int var4, int var5, long var6) {
      super(var1, var2, var3, var4, var5);
      this.time = var6;
   }
}
