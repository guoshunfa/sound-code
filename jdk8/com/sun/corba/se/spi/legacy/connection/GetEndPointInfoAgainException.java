package com.sun.corba.se.spi.legacy.connection;

import com.sun.corba.se.spi.transport.SocketInfo;

public class GetEndPointInfoAgainException extends Exception {
   private SocketInfo socketInfo;

   public GetEndPointInfoAgainException(SocketInfo var1) {
      this.socketInfo = var1;
   }

   public SocketInfo getEndPointInfo() {
      return this.socketInfo;
   }
}
