package com.sun.corba.se.impl.protocol.giopmsgheaders;

public interface CancelRequestMessage extends Message {
   int CANCEL_REQ_MSG_SIZE = 4;

   int getRequestId();
}
