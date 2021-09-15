package com.sun.corba.se.spi.servicecontext;

import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import sun.corba.OutputStreamFactory;

public abstract class ServiceContext {
   protected InputStream in = null;

   protected ServiceContext() {
   }

   private void dprint(String var1) {
      ORBUtility.dprint((Object)this, var1);
   }

   protected ServiceContext(InputStream var1, GIOPVersion var2) throws SystemException {
      this.in = var1;
   }

   public abstract int getId();

   public void write(OutputStream var1, GIOPVersion var2) throws SystemException {
      EncapsOutputStream var3 = OutputStreamFactory.newEncapsOutputStream((ORB)((ORB)var1.orb()), var2);
      var3.putEndian();
      this.writeData(var3);
      byte[] var4 = var3.toByteArray();
      var1.write_long(this.getId());
      var1.write_long(var4.length);
      var1.write_octet_array(var4, 0, var4.length);
   }

   protected abstract void writeData(OutputStream var1);

   public String toString() {
      return "ServiceContext[ id=" + this.getId() + " ]";
   }
}
