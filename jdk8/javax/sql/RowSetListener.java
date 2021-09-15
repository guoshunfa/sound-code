package javax.sql;

import java.util.EventListener;

public interface RowSetListener extends EventListener {
   void rowSetChanged(RowSetEvent var1);

   void rowChanged(RowSetEvent var1);

   void cursorMoved(RowSetEvent var1);
}
