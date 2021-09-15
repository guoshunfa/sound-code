package sun.management.jdp;

import java.io.IOException;

public interface JdpPacket {
   byte[] getPacketData() throws IOException;
}
