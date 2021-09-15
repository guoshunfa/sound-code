package sun.nio.ch;

import java.io.FileDescriptor;
import java.nio.channels.Pipe;
import java.nio.channels.spi.SelectorProvider;

class PipeImpl extends Pipe {
   private final Pipe.SourceChannel source;
   private final Pipe.SinkChannel sink;

   PipeImpl(SelectorProvider var1) {
      long var2 = IOUtil.makePipe(true);
      int var4 = (int)(var2 >>> 32);
      int var5 = (int)var2;
      FileDescriptor var6 = new FileDescriptor();
      IOUtil.setfdVal(var6, var4);
      this.source = new SourceChannelImpl(var1, var6);
      FileDescriptor var7 = new FileDescriptor();
      IOUtil.setfdVal(var7, var5);
      this.sink = new SinkChannelImpl(var1, var7);
   }

   public Pipe.SourceChannel source() {
      return this.source;
   }

   public Pipe.SinkChannel sink() {
      return this.sink;
   }
}
