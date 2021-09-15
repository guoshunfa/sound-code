package com.sun.corba.se.pept.encoding;

import com.sun.corba.se.pept.protocol.MessageMediator;
import java.io.IOException;

public interface InputObject {
   void setMessageMediator(MessageMediator var1);

   MessageMediator getMessageMediator();

   void close() throws IOException;
}
