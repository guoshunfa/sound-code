package java.net;

import java.io.IOException;
import jdk.net.ExtendedSocketOptions;
import jdk.net.SocketFlow;
import sun.net.ExtendedOptionsImpl;

class PlainDatagramSocketImpl extends AbstractPlainDatagramSocketImpl {
   protected <T> void setOption(SocketOption<T> var1, T var2) throws IOException {
      if (!var1.equals(ExtendedSocketOptions.SO_FLOW_SLA)) {
         super.setOption(var1, var2);
      } else {
         if (this.isClosed()) {
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
      } else if (this.isClosed()) {
         throw new SocketException("Socket closed");
      } else {
         ExtendedOptionsImpl.checkGetOptionPermission(var1);
         SocketFlow var2 = SocketFlow.create();
         ExtendedOptionsImpl.getFlowOption(this.getFileDescriptor(), var2);
         return var2;
      }
   }

   protected void socketSetOption(int var1, Object var2) throws SocketException {
      try {
         this.socketSetOption0(var1, var2);
      } catch (SocketException var4) {
         if (!this.connected) {
            throw var4;
         }
      }

   }

   protected synchronized native void bind0(int var1, InetAddress var2) throws SocketException;

   protected native void send(DatagramPacket var1) throws IOException;

   protected synchronized native int peek(InetAddress var1) throws IOException;

   protected synchronized native int peekData(DatagramPacket var1) throws IOException;

   protected synchronized native void receive0(DatagramPacket var1) throws IOException;

   protected native void setTimeToLive(int var1) throws IOException;

   protected native int getTimeToLive() throws IOException;

   /** @deprecated */
   @Deprecated
   protected native void setTTL(byte var1) throws IOException;

   /** @deprecated */
   @Deprecated
   protected native byte getTTL() throws IOException;

   protected native void join(InetAddress var1, NetworkInterface var2) throws IOException;

   protected native void leave(InetAddress var1, NetworkInterface var2) throws IOException;

   protected native void datagramSocketCreate() throws SocketException;

   protected native void datagramSocketClose();

   protected native void socketSetOption0(int var1, Object var2) throws SocketException;

   protected native Object socketGetOption(int var1) throws SocketException;

   protected native void connect0(InetAddress var1, int var2) throws SocketException;

   protected native void disconnect0(int var1);

   native int dataAvailable();

   private static native void init();

   static {
      init();
   }
}
