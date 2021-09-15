package javax.swing.event;

import java.util.EventObject;
import javax.swing.table.TableColumnModel;

public class TableColumnModelEvent extends EventObject {
   protected int fromIndex;
   protected int toIndex;

   public TableColumnModelEvent(TableColumnModel var1, int var2, int var3) {
      super(var1);
      this.fromIndex = var2;
      this.toIndex = var3;
   }

   public int getFromIndex() {
      return this.fromIndex;
   }

   public int getToIndex() {
      return this.toIndex;
   }
}
