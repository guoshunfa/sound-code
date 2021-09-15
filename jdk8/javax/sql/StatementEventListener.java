package javax.sql;

import java.util.EventListener;

public interface StatementEventListener extends EventListener {
   void statementClosed(StatementEvent var1);

   void statementErrorOccurred(StatementEvent var1);
}
