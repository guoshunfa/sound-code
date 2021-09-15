package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.org.omg.SendingContext.CodeBase;
import java.nio.ByteBuffer;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.ORB;
import sun.corba.EncapsInputStreamFactory;

public class EncapsInputStream extends CDRInputStream {
   private ORBUtilSystemException wrapper;
   private CodeBase codeBase;

   public EncapsInputStream(ORB var1, byte[] var2, int var3, boolean var4, GIOPVersion var5) {
      super(var1, ByteBuffer.wrap(var2), var3, var4, var5, (byte)0, BufferManagerFactory.newBufferManagerRead(0, (byte)0, (com.sun.corba.se.spi.orb.ORB)var1));
      this.wrapper = ORBUtilSystemException.get((com.sun.corba.se.spi.orb.ORB)var1, "rpc.encoding");
      this.performORBVersionSpecificInit();
   }

   public EncapsInputStream(ORB var1, ByteBuffer var2, int var3, boolean var4, GIOPVersion var5) {
      super(var1, var2, var3, var4, var5, (byte)0, BufferManagerFactory.newBufferManagerRead(0, (byte)0, (com.sun.corba.se.spi.orb.ORB)var1));
      this.performORBVersionSpecificInit();
   }

   public EncapsInputStream(ORB var1, byte[] var2, int var3) {
      this(var1, var2, var3, GIOPVersion.V1_2);
   }

   public EncapsInputStream(EncapsInputStream var1) {
      super(var1);
      this.wrapper = ORBUtilSystemException.get((com.sun.corba.se.spi.orb.ORB)((com.sun.corba.se.spi.orb.ORB)var1.orb()), "rpc.encoding");
      this.performORBVersionSpecificInit();
   }

   public EncapsInputStream(ORB var1, byte[] var2, int var3, GIOPVersion var4) {
      this(var1, var2, var3, false, var4);
   }

   public EncapsInputStream(ORB var1, byte[] var2, int var3, GIOPVersion var4, CodeBase var5) {
      super(var1, ByteBuffer.wrap(var2), var3, false, var4, (byte)0, BufferManagerFactory.newBufferManagerRead(0, (byte)0, (com.sun.corba.se.spi.orb.ORB)var1));
      this.codeBase = var5;
      this.performORBVersionSpecificInit();
   }

   public CDRInputStream dup() {
      return EncapsInputStreamFactory.newEncapsInputStream(this);
   }

   protected CodeSetConversion.BTCConverter createCharBTCConverter() {
      return CodeSetConversion.impl().getBTCConverter(OSFCodeSetRegistry.ISO_8859_1);
   }

   protected CodeSetConversion.BTCConverter createWCharBTCConverter() {
      if (this.getGIOPVersion().equals(GIOPVersion.V1_0)) {
         throw this.wrapper.wcharDataInGiop10(CompletionStatus.COMPLETED_MAYBE);
      } else {
         return this.getGIOPVersion().equals(GIOPVersion.V1_1) ? CodeSetConversion.impl().getBTCConverter(OSFCodeSetRegistry.UTF_16, this.isLittleEndian()) : CodeSetConversion.impl().getBTCConverter(OSFCodeSetRegistry.UTF_16, false);
      }
   }

   public CodeBase getCodeBase() {
      return this.codeBase;
   }
}
