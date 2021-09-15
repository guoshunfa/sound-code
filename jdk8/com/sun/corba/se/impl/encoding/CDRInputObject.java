package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.org.omg.SendingContext.CodeBase;
import java.nio.ByteBuffer;

public class CDRInputObject extends CDRInputStream implements InputObject {
   private CorbaConnection corbaConnection;
   private Message header;
   private boolean unmarshaledHeader;
   private ORB orb;
   private ORBUtilSystemException wrapper;
   private OMGSystemException omgWrapper;

   public CDRInputObject(ORB var1, CorbaConnection var2, ByteBuffer var3, Message var4) {
      super(var1, var3, var4.getSize(), var4.isLittleEndian(), var4.getGIOPVersion(), var4.getEncodingVersion(), BufferManagerFactory.newBufferManagerRead(var4.getGIOPVersion(), var4.getEncodingVersion(), var1));
      this.corbaConnection = var2;
      this.orb = var1;
      this.wrapper = ORBUtilSystemException.get(var1, "rpc.encoding");
      this.omgWrapper = OMGSystemException.get(var1, "rpc.encoding");
      if (var1.transportDebugFlag) {
         this.dprint(".CDRInputObject constructor:");
      }

      this.getBufferManager().init(var4);
      this.header = var4;
      this.unmarshaledHeader = false;
      this.setIndex(12);
      this.setBufferLength(var4.getSize());
   }

   public final CorbaConnection getConnection() {
      return this.corbaConnection;
   }

   public Message getMessageHeader() {
      return this.header;
   }

   public void unmarshalHeader() {
      if (!this.unmarshaledHeader) {
         try {
            if (((ORB)this.orb()).transportDebugFlag) {
               this.dprint(".unmarshalHeader->: " + this.getMessageHeader());
            }

            this.getMessageHeader().read(this);
            this.unmarshaledHeader = true;
         } catch (RuntimeException var5) {
            if (((ORB)this.orb()).transportDebugFlag) {
               this.dprint(".unmarshalHeader: !!ERROR!!: " + this.getMessageHeader() + ": " + var5);
            }

            throw var5;
         } finally {
            if (((ORB)this.orb()).transportDebugFlag) {
               this.dprint(".unmarshalHeader<-: " + this.getMessageHeader());
            }

         }
      }

   }

   public final boolean unmarshaledHeader() {
      return this.unmarshaledHeader;
   }

   protected CodeSetConversion.BTCConverter createCharBTCConverter() {
      CodeSetComponentInfo.CodeSetContext var1 = this.getCodeSets();
      if (var1 == null) {
         return super.createCharBTCConverter();
      } else {
         OSFCodeSetRegistry.Entry var2 = OSFCodeSetRegistry.lookupEntry(var1.getCharCodeSet());
         if (var2 == null) {
            throw this.wrapper.unknownCodeset(var2);
         } else {
            return CodeSetConversion.impl().getBTCConverter(var2, this.isLittleEndian());
         }
      }
   }

   protected CodeSetConversion.BTCConverter createWCharBTCConverter() {
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
            return var2 == OSFCodeSetRegistry.UTF_16 && this.getGIOPVersion().equals(GIOPVersion.V1_2) ? CodeSetConversion.impl().getBTCConverter(var2, false) : CodeSetConversion.impl().getBTCConverter(var2, this.isLittleEndian());
         }
      }
   }

   private CodeSetComponentInfo.CodeSetContext getCodeSets() {
      return this.getConnection() == null ? CodeSetComponentInfo.LOCAL_CODE_SETS : this.getConnection().getCodeSetContext();
   }

   public final CodeBase getCodeBase() {
      return this.getConnection() == null ? null : this.getConnection().getCodeBase();
   }

   public CDRInputStream dup() {
      return null;
   }

   protected void dprint(String var1) {
      ORBUtility.dprint("CDRInputObject", var1);
   }
}
