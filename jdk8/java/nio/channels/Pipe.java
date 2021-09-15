package java.nio.channels;

import java.io.IOException;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.SelectorProvider;

public abstract class Pipe {
   protected Pipe() {
   }

   public abstract Pipe.SourceChannel source();

   public abstract Pipe.SinkChannel sink();

   public static Pipe open() throws IOException {
      return SelectorProvider.provider().openPipe();
   }

   public abstract static class SinkChannel extends AbstractSelectableChannel implements WritableByteChannel, GatheringByteChannel {
      protected SinkChannel(SelectorProvider var1) {
         super(var1);
      }

      public final int validOps() {
         return 4;
      }
   }

   public abstract static class SourceChannel extends AbstractSelectableChannel implements ReadableByteChannel, ScatteringByteChannel {
      protected SourceChannel(SelectorProvider var1) {
         super(var1);
      }

      public final int validOps() {
         return 1;
      }
   }
}
