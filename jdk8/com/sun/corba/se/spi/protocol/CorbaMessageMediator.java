package com.sun.corba.se.spi.protocol;

import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyOrReplyMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import java.nio.ByteBuffer;
import org.omg.CORBA.Request;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA_2_3.portable.InputStream;

public interface CorbaMessageMediator extends MessageMediator, ResponseHandler {
   void setReplyHeader(LocateReplyOrReplyMessage var1);

   LocateReplyMessage getLocateReplyHeader();

   ReplyMessage getReplyHeader();

   void setReplyExceptionDetailMessage(String var1);

   RequestMessage getRequestHeader();

   GIOPVersion getGIOPVersion();

   byte getEncodingVersion();

   int getRequestId();

   Integer getRequestIdInteger();

   boolean isOneWay();

   short getAddrDisposition();

   String getOperationName();

   ServiceContexts getRequestServiceContexts();

   ServiceContexts getReplyServiceContexts();

   Message getDispatchHeader();

   void setDispatchHeader(Message var1);

   ByteBuffer getDispatchBuffer();

   void setDispatchBuffer(ByteBuffer var1);

   int getThreadPoolToUse();

   byte getStreamFormatVersion();

   byte getStreamFormatVersionForReply();

   void sendCancelRequestIfFinalFragmentNotSent();

   void setDIIInfo(Request var1);

   boolean isDIIRequest();

   Exception unmarshalDIIUserException(String var1, InputStream var2);

   void setDIIException(Exception var1);

   void handleDIIReply(InputStream var1);

   boolean isSystemExceptionReply();

   boolean isUserExceptionReply();

   boolean isLocationForwardReply();

   boolean isDifferentAddrDispositionRequestedReply();

   short getAddrDispositionReply();

   IOR getForwardedIOR();

   SystemException getSystemExceptionReply();

   ObjectKey getObjectKey();

   void setProtocolHandler(CorbaProtocolHandler var1);

   CorbaProtocolHandler getProtocolHandler();

   OutputStream createReply();

   OutputStream createExceptionReply();

   boolean executeReturnServantInResponseConstructor();

   void setExecuteReturnServantInResponseConstructor(boolean var1);

   boolean executeRemoveThreadInfoInResponseConstructor();

   void setExecuteRemoveThreadInfoInResponseConstructor(boolean var1);

   boolean executePIInResponseConstructor();

   void setExecutePIInResponseConstructor(boolean var1);
}
