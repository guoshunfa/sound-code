package javax.swing.event;

import java.util.EventObject;
import javax.swing.table.TableModel;

public class TableModelEvent extends EventObject {
   public static final int INSERT = 1;
   public static final int UPDATE = 0;
   public static final int DELETE = -1;
   public static final int HEADER_ROW = -1;
   public static final int ALL_COLUMNS = -1;
   protected int type;
   protected int firstRow;
   protected int lastRow;
   protected int column;

   public TableModelEvent(TableModel var1) {
      this(var1, 0, Integer.MAX_VALUE, -1, 0);
   }

   public TableModelEvent(TableModel var1, int var2) {
      this(var1, var2, var2, -1, 0);
   }

   public TableModelEvent(TableModel var1, int var2, int var3) {
      this(var1, var2, var3, -1, 0);
   }

   public TableModelEvent(TableModel var1, int var2, int var3, int var4) {
      this(var1, var2, var3, var4, 0);
   }

   public TableModelEvent(TableModel var1, int var2, int var3, int var4, int var5) {
      super(var1);
      this.firstRow = var2;
      this.lastRow = var3;
      this.column = var4;
      this.type = var5;
   }

   public int getFirstRow() {
      return this.firstRow;
   }

   public int getLastRow() {
      return this.lastRow;
   }

   public int getColumn() {
      return this.column;
   }

   public int getType() {
      return this.type;
   }
}
