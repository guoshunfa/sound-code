package javax.sql;

import java.util.EventListener;

public interface ConnectionEventListener extends EventListener {
   void connectionClosed(ConnectionEvent var1);

   void connectionErrorOccurred(ConnectionEvent var1);
}
