package java.nio.channels;

import java.io.IOException;
import java.nio.channels.spi.AbstractInterruptibleChannel;
import java.nio.channels.spi.SelectorProvider;

public abstract class SelectableChannel extends AbstractInterruptibleChannel implements Channel {
   protected SelectableChannel() {
   }

   public abstract SelectorProvider provider();

   public abstract int validOps();

   public abstract boolean isRegistered();

   public abstract SelectionKey keyFor(Selector var1);

   public abstract SelectionKey register(Selector var1, int var2, Object var3) throws ClosedChannelException;

   public final SelectionKey register(Selector var1, int var2) throws ClosedChannelException {
      return this.register(var1, var2, (Object)null);
   }

   public abstract SelectableChannel configureBlocking(boolean var1) throws IOException;

   public abstract boolean isBlocking();

   public abstract Object blockingLock();
}
