package com.sun.corba.se.impl.protocol.giopmsgheaders;

public interface FragmentMessage extends Message {
   int getRequestId();

   int getHeaderLength();
}
