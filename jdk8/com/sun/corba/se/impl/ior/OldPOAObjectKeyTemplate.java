package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.OctetSeqHolder;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public final class OldPOAObjectKeyTemplate extends OldObjectKeyTemplateBase {
   public OldPOAObjectKeyTemplate(ORB var1, int var2, int var3, InputStream var4) {
      this(var1, var2, var3, var4.read_long(), var4.read_long(), var4.read_long());
   }

   public OldPOAObjectKeyTemplate(ORB var1, int var2, int var3, InputStream var4, OctetSeqHolder var5) {
      this(var1, var2, var3, var4);
      var5.value = this.readObjectKey(var4);
   }

   public OldPOAObjectKeyTemplate(ORB var1, int var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, Integer.toString(var5), new ObjectAdapterIdNumber(var6));
   }

   public void writeTemplate(OutputStream var1) {
      var1.write_long(this.getMagic());
      var1.write_long(this.getSubcontractId());
      var1.write_long(this.getServerId());
      int var2 = Integer.parseInt(this.getORBId());
      var1.write_long(var2);
      ObjectAdapterIdNumber var3 = (ObjectAdapterIdNumber)((ObjectAdapterIdNumber)this.getObjectAdapterId());
      int var4 = var3.getOldPOAId();
      var1.write_long(var4);
   }

   public ORBVersion getORBVersion() {
      if (this.getMagic() == -1347695874) {
         return ORBVersionFactory.getOLD();
      } else if (this.getMagic() == -1347695873) {
         return ORBVersionFactory.getNEW();
      } else {
         throw new INTERNAL();
      }
   }
}
