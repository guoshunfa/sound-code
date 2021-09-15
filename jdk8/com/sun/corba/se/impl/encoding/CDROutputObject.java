package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.encoding.CorbaOutputObject;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.transport.CorbaConnection;
import java.io.IOException;
import org.omg.CORBA.portable.InputStream;

public class CDROutputObject extends CorbaOutputObject {
   private Message header;
   private ORB orb;
   private ORBUtilSystemException wrapper;
   private OMGSystemException omgWrapper;
   private CorbaConnection connection;

   private CDROutputObject(ORB var1, GIOPVersion var2, Message var3, BufferManagerWrite var4, byte var5, CorbaMessageMediator var6) {
      super(var1, var2, var3.getEncodingVersion(), false, var4, var5, var6 != null && var6.getConnection() != null ? ((CorbaConnection)var6.getConnection()).shouldUseDirectByteBuffers() : false);
      this.header = var3;
      this.orb = var1;
      this.wrapper = ORBUtilSystemException.get(var1, "rpc.encoding");
      this.omgWrapper = OMGSystemException.get(var1, "rpc.encoding");
      this.getBufferManager().setOutputObject(this);
      this.corbaMessageMediator = var6;
   }

   public CDROutputObject(ORB var1, MessageMediator var2, Message var3, byte var4) {
      this(var1, ((CorbaMessageMediator)var2).getGIOPVersion(), var3, BufferManagerFactory.newBufferManagerWrite(((CorbaMessageMediator)var2).getGIOPVersion(), var3.getEncodingVersion(), var1), var4, (CorbaMessageMediator)var2);
   }

   public CDROutputObject(ORB var1, MessageMediator var2, Message var3, byte var4, int var5) {
      this(var1, ((CorbaMessageMediator)var2).getGIOPVersion(), var3, BufferManagerFactory.newBufferManagerWrite(var5, var3.getEncodingVersion(), var1), var4, (CorbaMessageMediator)var2);
   }

   public CDROutputObject(ORB var1, CorbaMessageMediator var2, GIOPVersion var3, CorbaConnection var4, Message var5, byte var6) {
      this(var1, var3, var5, BufferManagerFactory.newBufferManagerWrite(var3, var5.getEncodingVersion(), var1), var6, var2);
      this.connection = var4;
   }

   public Message getMessageHeader() {
      return this.header;
   }

   public final void finishSendingMessage() {
      this.getBufferManager().sendMessage();
   }

   public void writeTo(CorbaConnection var1) throws IOException {
      ByteBufferWithInfo var2 = this.getByteBufferWithInfo();
      this.getMessageHeader().setSize(var2.byteBuffer, var2.getSize());
      if (this.orb() != null) {
         if (((ORB)this.orb()).transportDebugFlag) {
            this.dprint(".writeTo: " + var1);
         }

         if (((ORB)this.orb()).giopDebugFlag) {
            CDROutputStream_1_0.printBuffer(var2);
         }
      }

      var2.byteBuffer.position(0).limit(var2.getSize());
      var1.write(var2.byteBuffer);
   }

   public InputStream create_input_stream() {
      return null;
   }

   public CorbaConnection getConnection() {
      return this.connection != null ? this.connection : (CorbaConnection)this.corbaMessageMediator.getConnection();
   }

   public final ByteBufferWithInfo getByteBufferWithInfo() {
      return super.getByteBufferWithInfo();
   }

   public final void setByteBufferWithInfo(ByteBufferWithInfo var1) {
      super.setByteBufferWithInfo(var1);
   }

   protected CodeSetConversion.CTBConverter createCharCTBConverter() {
      CodeSetComponentInfo.CodeSetContext var1 = this.getCodeSets();
      if (var1 == null) {
         return super.createCharCTBConverter();
      } else {
         OSFCodeSetRegistry.Entry var2 = OSFCodeSetRegistry.lookupEntry(var1.getCharCodeSet());
         if (var2 == null) {
            throw this.wrapper.unknownCodeset(var2);
         } else {
            return CodeSetConversion.impl().getCTBConverter(var2, this.isLittleEndian(), false);
         }
      }
   }

   protected CodeSetConversion.CTBConverter createWCharCTBConverter() {
      CodeSetComponentInfo.CodeSetContext var1 = this.getCodeSets();
      if (var1 == null) {
         if (this.getConnection().isServer()) {
            throw this.omgWrapper.noClientWcharCodesetCtx();
         } else {
            throw this.omgWrapper.noServerWcharCodesetCmp();
         }
      } else {
         OSFCodeSetRegistry.Entry var2 = OSFCodeSetRegistry.lookupEntry(var1.getWCharCodeSet());
         if (var2 == null) {
            throw this.wrapper.unknownCodeset(var2);
         } else {
            boolean var3 = ((ORB)this.orb()).getORBData().useByteOrderMarkers();
            if (var2 == OSFCodeSetRegistry.UTF_16) {
               if (this.getGIOPVersion().equals(GIOPVersion.V1_2)) {
                  return CodeSetConversion.impl().getCTBConverter(var2, false, var3);
               }

               if (this.getGIOPVersion().equals(GIOPVersion.V1_1)) {
                  return CodeSetConversion.impl().getCTBConverter(var2, this.isLittleEndian(), false);
               }
            }

            return CodeSetConversion.impl().getCTBConverter(var2, this.isLittleEndian(), var3);
         }
      }
   }

   private CodeSetComponentInfo.CodeSetContext getCodeSets() {
      return this.getConnection() == null ? CodeSetComponentInfo.LOCAL_CODE_SETS : this.getConnection().getCodeSetContext();
   }

   protected void dprint(String var1) {
      ORBUtility.dprint("CDROutputObject", var1);
   }
}
