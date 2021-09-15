package com.sun.corba.se.pept.transport;

import com.sun.corba.se.spi.orbutil.threadpool.Work;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

public interface EventHandler {
   void setUseSelectThreadToWait(boolean var1);

   boolean shouldUseSelectThreadToWait();

   SelectableChannel getChannel();

   int getInterestOps();

   void setSelectionKey(SelectionKey var1);

   SelectionKey getSelectionKey();

   void handleEvent();

   void setUseWorkerThreadForEvent(boolean var1);

   boolean shouldUseWorkerThreadForEvent();

   void setWork(Work var1);

   Work getWork();

   Acceptor getAcceptor();

   Connection getConnection();
}
