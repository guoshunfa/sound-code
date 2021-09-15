package com.sun.corba.se.spi.servicecontext;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import org.omg.CORBA.SystemException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class ORBVersionServiceContext extends ServiceContext {
   public static final int SERVICE_CONTEXT_ID = 1313165056;
   private ORBVersion version = ORBVersionFactory.getORBVersion();

   public ORBVersionServiceContext() {
      this.version = ORBVersionFactory.getORBVersion();
   }

   public ORBVersionServiceContext(ORBVersion var1) {
      this.version = var1;
   }

   public ORBVersionServiceContext(InputStream var1, GIOPVersion var2) {
      super(var1, var2);
      this.version = ORBVersionFactory.create(this.in);
   }

   public int getId() {
      return 1313165056;
   }

   public void writeData(OutputStream var1) throws SystemException {
      this.version.write(var1);
   }

   public ORBVersion getVersion() {
      return this.version;
   }

   public String toString() {
      return "ORBVersionServiceContext[ version=" + this.version + " ]";
   }
}
