package com.sun.corba.se.spi.servicecontext;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.SystemException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class MaxStreamFormatVersionServiceContext extends ServiceContext {
   private byte maxStreamFormatVersion;
   public static final MaxStreamFormatVersionServiceContext singleton = new MaxStreamFormatVersionServiceContext();
   public static final int SERVICE_CONTEXT_ID = 17;

   public MaxStreamFormatVersionServiceContext() {
      this.maxStreamFormatVersion = ORBUtility.getMaxStreamFormatVersion();
   }

   public MaxStreamFormatVersionServiceContext(byte var1) {
      this.maxStreamFormatVersion = var1;
   }

   public MaxStreamFormatVersionServiceContext(InputStream var1, GIOPVersion var2) {
      super(var1, var2);
      this.maxStreamFormatVersion = var1.read_octet();
   }

   public int getId() {
      return 17;
   }

   public void writeData(OutputStream var1) throws SystemException {
      var1.write_octet(this.maxStreamFormatVersion);
   }

   public byte getMaximumStreamFormatVersion() {
      return this.maxStreamFormatVersion;
   }

   public String toString() {
      return "MaxStreamFormatVersionServiceContext[" + this.maxStreamFormatVersion + "]";
   }
}
