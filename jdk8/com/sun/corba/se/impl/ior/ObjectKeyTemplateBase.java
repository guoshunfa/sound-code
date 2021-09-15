package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.logging.IORSystemException;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import java.util.Iterator;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public abstract class ObjectKeyTemplateBase implements ObjectKeyTemplate {
   public static final String JIDL_ORB_ID = "";
   private static final String[] JIDL_OAID_STRINGS = new String[]{"TransientObjectAdapter"};
   public static final ObjectAdapterId JIDL_OAID;
   private ORB orb;
   protected IORSystemException wrapper;
   private ORBVersion version;
   private int magic;
   private int scid;
   private int serverid;
   private String orbid;
   private ObjectAdapterId oaid;
   private byte[] adapterId;

   public byte[] getAdapterId() {
      return (byte[])((byte[])this.adapterId.clone());
   }

   private byte[] computeAdapterId() {
      ByteBuffer var1 = new ByteBuffer();
      var1.append(this.getServerId());
      var1.append(this.orbid);
      var1.append(this.oaid.getNumLevels());
      Iterator var2 = this.oaid.iterator();

      while(var2.hasNext()) {
         String var3 = (String)((String)var2.next());
         var1.append(var3);
      }

      var1.trimToSize();
      return var1.toArray();
   }

   public ObjectKeyTemplateBase(ORB var1, int var2, int var3, int var4, String var5, ObjectAdapterId var6) {
      this.orb = var1;
      this.wrapper = IORSystemException.get(var1, "oa.ior");
      this.magic = var2;
      this.scid = var3;
      this.serverid = var4;
      this.orbid = var5;
      this.oaid = var6;
      this.adapterId = this.computeAdapterId();
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof ObjectKeyTemplateBase)) {
         return false;
      } else {
         ObjectKeyTemplateBase var2 = (ObjectKeyTemplateBase)var1;
         return this.magic == var2.magic && this.scid == var2.scid && this.serverid == var2.serverid && this.version.equals(var2.version) && this.orbid.equals(var2.orbid) && this.oaid.equals(var2.oaid);
      }
   }

   public int hashCode() {
      byte var1 = 17;
      int var2 = 37 * var1 + this.magic;
      var2 = 37 * var2 + this.scid;
      var2 = 37 * var2 + this.serverid;
      var2 = 37 * var2 + this.version.hashCode();
      var2 = 37 * var2 + this.orbid.hashCode();
      var2 = 37 * var2 + this.oaid.hashCode();
      return var2;
   }

   public int getSubcontractId() {
      return this.scid;
   }

   public int getServerId() {
      return this.serverid;
   }

   public String getORBId() {
      return this.orbid;
   }

   public ObjectAdapterId getObjectAdapterId() {
      return this.oaid;
   }

   public void write(ObjectId var1, OutputStream var2) {
      this.writeTemplate(var2);
      var1.write(var2);
   }

   public void write(OutputStream var1) {
      this.writeTemplate(var1);
   }

   protected abstract void writeTemplate(OutputStream var1);

   protected int getMagic() {
      return this.magic;
   }

   public void setORBVersion(ORBVersion var1) {
      this.version = var1;
   }

   public ORBVersion getORBVersion() {
      return this.version;
   }

   protected byte[] readObjectKey(InputStream var1) {
      int var2 = var1.read_long();
      byte[] var3 = new byte[var2];
      var1.read_octet_array(var3, 0, var2);
      return var3;
   }

   public CorbaServerRequestDispatcher getServerRequestDispatcher(ORB var1, ObjectId var2) {
      return var1.getRequestDispatcherRegistry().getServerRequestDispatcher(this.scid);
   }

   static {
      JIDL_OAID = new ObjectAdapterIdArray(JIDL_OAID_STRINGS);
   }
}
