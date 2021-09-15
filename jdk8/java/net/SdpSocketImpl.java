package java.net;

import java.io.IOException;
import sun.net.sdp.SdpSupport;

class SdpSocketImpl extends PlainSocketImpl {
   protected void create(boolean var1) throws IOException {
      if (!var1) {
         throw new UnsupportedOperationException("Must be a stream socket");
      } else {
         this.fd = SdpSupport.createSocket();
         if (this.socket != null) {
            this.socket.setCreated();
         }

         if (this.serverSocket != null) {
            this.serverSocket.setCreated();
         }

      }
   }
}
