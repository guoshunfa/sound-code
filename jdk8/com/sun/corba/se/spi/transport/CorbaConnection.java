package com.sun.corba.se.spi.transport;

import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ResponseWaitingRoom;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.org.omg.SendingContext.CodeBase;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import org.omg.CORBA.SystemException;

public interface CorbaConnection extends Connection, com.sun.corba.se.spi.legacy.connection.Connection {
   int OPENING = 1;
   int ESTABLISHED = 2;
   int CLOSE_SENT = 3;
   int CLOSE_RECVD = 4;
   int ABORT = 5;

   boolean shouldUseDirectByteBuffers();

   boolean shouldReadGiopHeaderOnly();

   ByteBuffer read(int var1, int var2, int var3, long var4) throws IOException;

   ByteBuffer read(ByteBuffer var1, int var2, int var3, long var4) throws IOException;

   void write(ByteBuffer var1) throws IOException;

   void dprint(String var1);

   int getNextRequestId();

   ORB getBroker();

   CodeSetComponentInfo.CodeSetContext getCodeSetContext();

   void setCodeSetContext(CodeSetComponentInfo.CodeSetContext var1);

   MessageMediator clientRequestMapGet(int var1);

   void clientReply_1_1_Put(MessageMediator var1);

   MessageMediator clientReply_1_1_Get();

   void clientReply_1_1_Remove();

   void serverRequest_1_1_Put(MessageMediator var1);

   MessageMediator serverRequest_1_1_Get();

   void serverRequest_1_1_Remove();

   boolean isPostInitialContexts();

   void setPostInitialContexts();

   void purgeCalls(SystemException var1, boolean var2, boolean var3);

   void setCodeBaseIOR(IOR var1);

   IOR getCodeBaseIOR();

   CodeBase getCodeBase();

   void sendCloseConnection(GIOPVersion var1) throws IOException;

   void sendMessageError(GIOPVersion var1) throws IOException;

   void sendCancelRequest(GIOPVersion var1, int var2) throws IOException;

   void sendCancelRequestWithLock(GIOPVersion var1, int var2) throws IOException;

   ResponseWaitingRoom getResponseWaitingRoom();

   void serverRequestMapPut(int var1, CorbaMessageMediator var2);

   CorbaMessageMediator serverRequestMapGet(int var1);

   void serverRequestMapRemove(int var1);

   SocketChannel getSocketChannel();

   void serverRequestProcessingBegins();

   void serverRequestProcessingEnds();

   void closeConnectionResources();
}
