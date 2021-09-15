package javax.swing;

import java.awt.Color;
import java.io.PrintStream;
import java.util.Hashtable;

class DebugGraphicsInfo {
   Color flashColor;
   int flashTime;
   int flashCount;
   Hashtable<JComponent, Integer> componentToDebug;
   JFrame debugFrame;
   PrintStream stream;

   DebugGraphicsInfo() {
      this.flashColor = Color.red;
      this.flashTime = 100;
      this.flashCount = 2;
      this.debugFrame = null;
      this.stream = System.out;
   }

   void setDebugOptions(JComponent var1, int var2) {
      if (var2 != 0) {
         if (this.componentToDebug == null) {
            this.componentToDebug = new Hashtable();
         }

         if (var2 > 0) {
            this.componentToDebug.put(var1, var2);
         } else {
            this.componentToDebug.remove(var1);
         }

      }
   }

   int getDebugOptions(JComponent var1) {
      if (this.componentToDebug == null) {
         return 0;
      } else {
         Integer var2 = (Integer)this.componentToDebug.get(var1);
         return var2 == null ? 0 : var2;
      }
   }

   void log(String var1) {
      this.stream.println(var1);
   }
}
