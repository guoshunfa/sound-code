package sun.applet;

import java.util.EventObject;

public class AppletEvent extends EventObject {
   private Object arg;
   private int id;

   public AppletEvent(Object var1, int var2, Object var3) {
      super(var1);
      this.arg = var3;
      this.id = var2;
   }

   public int getID() {
      return this.id;
   }

   public Object getArgument() {
      return this.arg;
   }

   public String toString() {
      String var1 = this.getClass().getName() + "[source=" + this.source + " + id=" + this.id;
      if (this.arg != null) {
         var1 = var1 + " + arg=" + this.arg;
      }

      var1 = var1 + " ]";
      return var1;
   }
}
