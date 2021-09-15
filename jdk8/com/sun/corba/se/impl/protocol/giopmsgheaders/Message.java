package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public interface Message {
   int defaultBufferSize = 1024;
   int GIOPBigEndian = 0;
   int GIOPLittleEndian = 1;
   int GIOPBigMagic = 1195986768;
   int GIOPLittleMagic = 1347373383;
   int GIOPMessageHeaderLength = 12;
   byte LITTLE_ENDIAN_BIT = 1;
   byte MORE_FRAGMENTS_BIT = 2;
   byte FLAG_NO_FRAG_BIG_ENDIAN = 0;
   byte TRAILING_TWO_BIT_BYTE_MASK = 3;
   byte THREAD_POOL_TO_USE_MASK = 63;
   byte CDR_ENC_VERSION = 0;
   byte JAVA_ENC_VERSION = 1;
   byte GIOPRequest = 0;
   byte GIOPReply = 1;
   byte GIOPCancelRequest = 2;
   byte GIOPLocateRequest = 3;
   byte GIOPLocateReply = 4;
   byte GIOPCloseConnection = 5;
   byte GIOPMessageError = 6;
   byte GIOPFragment = 7;

   GIOPVersion getGIOPVersion();

   byte getEncodingVersion();

   boolean isLittleEndian();

   boolean moreFragmentsToFollow();

   int getType();

   int getSize();

   ByteBuffer getByteBuffer();

   int getThreadPoolToUse();

   void read(InputStream var1);

   void write(OutputStream var1);

   void setSize(ByteBuffer var1, int var2);

   FragmentMessage createFragmentMessage();

   void callback(MessageHandler var1) throws IOException;

   void setByteBuffer(ByteBuffer var1);

   void setEncodingVersion(byte var1);
}
