package com.sun.corba.se.spi.transport;

public interface SocketInfo {
   String IIOP_CLEAR_TEXT = "IIOP_CLEAR_TEXT";

   String getType();

   String getHost();

   int getPort();
}
