package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.Channel;

public interface SelChImpl extends Channel {
   FileDescriptor getFD();

   int getFDVal();

   boolean translateAndUpdateReadyOps(int var1, SelectionKeyImpl var2);

   boolean translateAndSetReadyOps(int var1, SelectionKeyImpl var2);

   void translateAndSetInterestOps(int var1, SelectionKeyImpl var2);

   int validOps();

   void kill() throws IOException;
}
