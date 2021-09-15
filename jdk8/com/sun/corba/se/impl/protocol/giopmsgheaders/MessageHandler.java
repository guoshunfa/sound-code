package com.sun.corba.se.impl.protocol.giopmsgheaders;

import java.io.IOException;

public interface MessageHandler {
   void handleInput(Message var1) throws IOException;

   void handleInput(RequestMessage_1_0 var1) throws IOException;

   void handleInput(RequestMessage_1_1 var1) throws IOException;

   void handleInput(RequestMessage_1_2 var1) throws IOException;

   void handleInput(ReplyMessage_1_0 var1) throws IOException;

   void handleInput(ReplyMessage_1_1 var1) throws IOException;

   void handleInput(ReplyMessage_1_2 var1) throws IOException;

   void handleInput(LocateRequestMessage_1_0 var1) throws IOException;

   void handleInput(LocateRequestMessage_1_1 var1) throws IOException;

   void handleInput(LocateRequestMessage_1_2 var1) throws IOException;

   void handleInput(LocateReplyMessage_1_0 var1) throws IOException;

   void handleInput(LocateReplyMessage_1_1 var1) throws IOException;

   void handleInput(LocateReplyMessage_1_2 var1) throws IOException;

   void handleInput(FragmentMessage_1_1 var1) throws IOException;

   void handleInput(FragmentMessage_1_2 var1) throws IOException;

   void handleInput(CancelRequestMessage var1) throws IOException;
}
