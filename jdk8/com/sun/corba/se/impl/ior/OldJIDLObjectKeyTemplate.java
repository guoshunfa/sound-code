package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import org.omg.CORBA.OctetSeqHolder;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public final class OldJIDLObjectKeyTemplate extends OldObjectKeyTemplateBase {
   public static final byte NULL_PATCH_VERSION = 0;
   byte patchVersion;

   public OldJIDLObjectKeyTemplate(ORB var1, int var2, int var3, InputStream var4, OctetSeqHolder var5) {
      this(var1, var2, var3, var4);
      var5.value = this.readObjectKey(var4);
      if (var2 == -1347695873 && var5.value.length > ((CDRInputStream)var4).getPosition()) {
         this.patchVersion = var4.read_octet();
         if (this.patchVersion == 1) {
            this.setORBVersion(ORBVersionFactory.getJDK1_3_1_01());
         } else {
            if (this.patchVersion <= 1) {
               throw this.wrapper.invalidJdk131PatchLevel(new Integer(this.patchVersion));
            }

            this.setORBVersion(ORBVersionFactory.getORBVersion());
         }
      }

   }

   public OldJIDLObjectKeyTemplate(ORB var1, int var2, int var3, int var4) {
      super(var1, var2, var3, var4, "", JIDL_OAID);
      this.patchVersion = 0;
   }

   public OldJIDLObjectKeyTemplate(ORB var1, int var2, int var3, InputStream var4) {
      this(var1, var2, var3, var4.read_long());
   }

   protected void writeTemplate(OutputStream var1) {
      var1.write_long(this.getMagic());
      var1.write_long(this.getSubcontractId());
      var1.write_long(this.getServerId());
   }

   public void write(ObjectId var1, OutputStream var2) {
      super.write(var1, var2);
      if (this.patchVersion != 0) {
         var2.write_octet(this.patchVersion);
      }

   }
}
