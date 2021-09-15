package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.activation.POANameHelper;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import org.omg.CORBA.OctetSeqHolder;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public final class POAObjectKeyTemplate extends NewObjectKeyTemplateBase {
   public POAObjectKeyTemplate(ORB var1, int var2, int var3, InputStream var4) {
      super(var1, var2, var3, var4.read_long(), var4.read_string(), new ObjectAdapterIdArray(POANameHelper.read(var4)));
      this.setORBVersion(var4);
   }

   public POAObjectKeyTemplate(ORB var1, int var2, int var3, InputStream var4, OctetSeqHolder var5) {
      super(var1, var2, var3, var4.read_long(), var4.read_string(), new ObjectAdapterIdArray(POANameHelper.read(var4)));
      var5.value = this.readObjectKey(var4);
      this.setORBVersion(var4);
   }

   public POAObjectKeyTemplate(ORB var1, int var2, int var3, String var4, ObjectAdapterId var5) {
      super(var1, -1347695872, var2, var3, var4, var5);
      this.setORBVersion(ORBVersionFactory.getORBVersion());
   }

   public void writeTemplate(OutputStream var1) {
      var1.write_long(this.getMagic());
      var1.write_long(this.getSubcontractId());
      var1.write_long(this.getServerId());
      var1.write_string(this.getORBId());
      this.getObjectAdapterId().write(var1);
   }
}
