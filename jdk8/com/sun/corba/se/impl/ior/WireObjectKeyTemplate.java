package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.impl.logging.IORSystemException;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import org.omg.CORBA.OctetSeqHolder;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class WireObjectKeyTemplate implements ObjectKeyTemplate {
   private ORB orb;
   private IORSystemException wrapper;

   public boolean equals(Object var1) {
      return var1 == null ? false : var1 instanceof WireObjectKeyTemplate;
   }

   public int hashCode() {
      return 53;
   }

   private byte[] getId(InputStream var1) {
      CDRInputStream var2 = (CDRInputStream)var1;
      int var3 = var2.getBufferLength();
      byte[] var4 = new byte[var3];
      var2.read_octet_array((byte[])var4, 0, var3);
      return var4;
   }

   public WireObjectKeyTemplate(ORB var1) {
      this.initORB(var1);
   }

   public WireObjectKeyTemplate(InputStream var1, OctetSeqHolder var2) {
      var2.value = this.getId(var1);
      this.initORB((ORB)((ORB)var1.orb()));
   }

   private void initORB(ORB var1) {
      this.orb = var1;
      this.wrapper = IORSystemException.get(var1, "oa.ior");
   }

   public void write(ObjectId var1, OutputStream var2) {
      byte[] var3 = var1.getId();
      var2.write_octet_array(var3, 0, var3.length);
   }

   public void write(OutputStream var1) {
   }

   public int getSubcontractId() {
      return 2;
   }

   public int getServerId() {
      return -1;
   }

   public String getORBId() {
      throw this.wrapper.orbIdNotAvailable();
   }

   public ObjectAdapterId getObjectAdapterId() {
      throw this.wrapper.objectAdapterIdNotAvailable();
   }

   public byte[] getAdapterId() {
      throw this.wrapper.adapterIdNotAvailable();
   }

   public ORBVersion getORBVersion() {
      return ORBVersionFactory.getFOREIGN();
   }

   public CorbaServerRequestDispatcher getServerRequestDispatcher(ORB var1, ObjectId var2) {
      byte[] var3 = var2.getId();
      String var4 = new String(var3);
      return var1.getRequestDispatcherRegistry().getServerRequestDispatcher(var4);
   }
}
