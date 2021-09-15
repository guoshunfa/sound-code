package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.InputStream;
import sun.corba.EncapsInputStreamFactory;

public class EncapsOutputStream extends CDROutputStream {
   static final boolean usePooledByteBuffers = false;

   public EncapsOutputStream(ORB var1) {
      this(var1, GIOPVersion.V1_2);
   }

   public EncapsOutputStream(ORB var1, GIOPVersion var2) {
      this(var1, var2, false);
   }

   public EncapsOutputStream(ORB var1, boolean var2) {
      this(var1, GIOPVersion.V1_2, var2);
   }

   public EncapsOutputStream(ORB var1, GIOPVersion var2, boolean var3) {
      super(var1, var2, (byte)0, var3, BufferManagerFactory.newBufferManagerWrite(0, (byte)0, var1), (byte)1, false);
   }

   public InputStream create_input_stream() {
      this.freeInternalCaches();
      return EncapsInputStreamFactory.newEncapsInputStream(this.orb(), this.getByteBuffer(), this.getSize(), this.isLittleEndian(), this.getGIOPVersion());
   }

   protected CodeSetConversion.CTBConverter createCharCTBConverter() {
      return CodeSetConversion.impl().getCTBConverter(OSFCodeSetRegistry.ISO_8859_1);
   }

   protected CodeSetConversion.CTBConverter createWCharCTBConverter() {
      if (this.getGIOPVersion().equals(GIOPVersion.V1_0)) {
         throw this.wrapper.wcharDataInGiop10(CompletionStatus.COMPLETED_MAYBE);
      } else if (this.getGIOPVersion().equals(GIOPVersion.V1_1)) {
         return CodeSetConversion.impl().getCTBConverter(OSFCodeSetRegistry.UTF_16, this.isLittleEndian(), false);
      } else {
         boolean var1 = ((ORB)this.orb()).getORBData().useByteOrderMarkersInEncapsulations();
         return CodeSetConversion.impl().getCTBConverter(OSFCodeSetRegistry.UTF_16, false, var1);
      }
   }
}
