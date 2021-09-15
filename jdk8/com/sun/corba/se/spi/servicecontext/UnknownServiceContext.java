package com.sun.corba.se.spi.servicecontext;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.SystemException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class UnknownServiceContext extends ServiceContext {
   private int id = -1;
   private byte[] data = null;

   public UnknownServiceContext(int var1, byte[] var2) {
      this.id = var1;
      this.data = var2;
   }

   public UnknownServiceContext(int var1, InputStream var2) {
      this.id = var1;
      int var3 = var2.read_long();
      this.data = new byte[var3];
      var2.read_octet_array(this.data, 0, var3);
   }

   public int getId() {
      return this.id;
   }

   public void writeData(OutputStream var1) throws SystemException {
   }

   public void write(OutputStream var1, GIOPVersion var2) throws SystemException {
      var1.write_long(this.id);
      var1.write_long(this.data.length);
      var1.write_octet_array(this.data, 0, this.data.length);
   }

   public byte[] getData() {
      return this.data;
   }
}
