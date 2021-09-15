package java.net;

import java.io.FileDescriptor;
import java.io.IOException;
import jdk.net.ExtendedSocketOptions;
import jdk.net.SocketFlow;
import sun.net.ExtendedOptionsImpl;

class PlainSocketImpl extends AbstractPlainSocketImpl {
   PlainSocketImpl() {
   }

   PlainSocketImpl(FileDescriptor var1) {
      this.fd = var1;
   }

   protected <T> void setOption(SocketOption<T> var1, T var2) throws IOException {
      if (!var1.equals(ExtendedSocketOptions.SO_FLOW_SLA)) {
         super.setOption(var1, var2);
      } else {
         if (this.isClosedOrPending()) {
            throw new SocketException("Socket closed");
         }

         ExtendedOptionsImpl.checkSetOptionPermission(var1);
         ExtendedOptionsImpl.checkValueType(var2, SocketFlow.class);
         ExtendedOptionsImpl.setFlowOption(this.getFileDescriptor(), (SocketFlow)var2);
      }

   }

   protected <T> T getOption(SocketOption<T> var1) throws IOException {
      if (!var1.equals(ExtendedSocketOptions.SO_FLOW_SLA)) {
         return super.getOption(var1);
      } else if (this.isClosedOrPending()) {
         throw new SocketException("Socket closed");
      } else {
         ExtendedOptionsImpl.checkGetOptionPermission(var1);
         SocketFlow var2 = SocketFlow.create();
         ExtendedOptionsImpl.getFlowOption(this.getFileDescriptor(), var2);
         return var2;
      }
   }

   protected void socketSetOption(int var1, boolean var2, Object var3) throws SocketException {
      try {
         this.socketSetOption0(var1, var2, var3);
      } catch (SocketException var5) {
         if (this.socket == null || !this.socket.isConnected()) {
            throw var5;
         }
      }

   }

   native void socketCreate(boolean var1) throws IOException;

   native void socketConnect(InetAddress var1, int var2, int var3) throws IOException;

   native void socketBind(InetAddress var1, int var2) throws IOException;

   native void socketListen(int var1) throws IOException;

   native void socketAccept(SocketImpl var1) throws IOException;

   native int socketAvailable() throws IOException;

   native void socketClose0(boolean var1) throws IOException;

   native void socketShutdown(int var1) throws IOException;

   static native void initProto();

   native void socketSetOption0(int var1, boolean var2, Object var3) throws SocketException;

   native int socketGetOption(int var1, Object var2) throws SocketException;

   native void socketSendUrgentData(int var1) throws IOException;

   static {
      initProto();
   }
}
