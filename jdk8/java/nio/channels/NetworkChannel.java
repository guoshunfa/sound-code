package java.nio.channels;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.util.Set;

public interface NetworkChannel extends Channel {
   NetworkChannel bind(SocketAddress var1) throws IOException;

   SocketAddress getLocalAddress() throws IOException;

   <T> NetworkChannel setOption(SocketOption<T> var1, T var2) throws IOException;

   <T> T getOption(SocketOption<T> var1) throws IOException;

   Set<SocketOption<?>> supportedOptions();
}
