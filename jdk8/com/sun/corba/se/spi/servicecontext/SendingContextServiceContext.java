package com.sun.corba.se.spi.servicecontext;

import com.sun.corba.se.impl.ior.IORImpl;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.SystemException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class SendingContextServiceContext extends ServiceContext {
   public static final int SERVICE_CONTEXT_ID = 6;
   private IOR ior = null;

   public SendingContextServiceContext(IOR var1) {
      this.ior = var1;
   }

   public SendingContextServiceContext(InputStream var1, GIOPVersion var2) {
      super(var1, var2);
      this.ior = new IORImpl(this.in);
   }

   public int getId() {
      return 6;
   }

   public void writeData(OutputStream var1) throws SystemException {
      this.ior.write(var1);
   }

   public IOR getIOR() {
      return this.ior;
   }

   public String toString() {
      return "SendingContexServiceContext[ ior=" + this.ior + " ]";
   }
}
