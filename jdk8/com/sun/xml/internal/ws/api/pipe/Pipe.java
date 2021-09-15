package com.sun.xml.internal.ws.api.pipe;

import com.sun.xml.internal.ws.api.message.Packet;

/** @deprecated */
public interface Pipe {
   Packet process(Packet var1);

   void preDestroy();

   Pipe copy(PipeCloner var1);
}
