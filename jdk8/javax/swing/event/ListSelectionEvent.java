package javax.swing.event;

import java.util.EventObject;

public class ListSelectionEvent extends EventObject {
   private int firstIndex;
   private int lastIndex;
   private boolean isAdjusting;

   public ListSelectionEvent(Object var1, int var2, int var3, boolean var4) {
      super(var1);
      this.firstIndex = var2;
      this.lastIndex = var3;
      this.isAdjusting = var4;
   }

   public int getFirstIndex() {
      return this.firstIndex;
   }

   public int getLastIndex() {
      return this.lastIndex;
   }

   public boolean getValueIsAdjusting() {
      return this.isAdjusting;
   }

   public String toString() {
      String var1 = " source=" + this.getSource() + " firstIndex= " + this.firstIndex + " lastIndex= " + this.lastIndex + " isAdjusting= " + this.isAdjusting + " ";
      return this.getClass().getName() + "[" + var1 + "]";
   }
}
