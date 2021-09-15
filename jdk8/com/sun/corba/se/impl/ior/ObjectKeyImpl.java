package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import org.omg.CORBA.ORB;
import org.omg.CORBA_2_3.portable.OutputStream;
import sun.corba.OutputStreamFactory;

public class ObjectKeyImpl implements ObjectKey {
   private ObjectKeyTemplate oktemp;
   private ObjectId id;

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (!(var1 instanceof ObjectKeyImpl)) {
         return false;
      } else {
         ObjectKeyImpl var2 = (ObjectKeyImpl)var1;
         return this.oktemp.equals(var2.oktemp) && this.id.equals(var2.id);
      }
   }

   public int hashCode() {
      return this.oktemp.hashCode() ^ this.id.hashCode();
   }

   public ObjectKeyTemplate getTemplate() {
      return this.oktemp;
   }

   public ObjectId getId() {
      return this.id;
   }

   public ObjectKeyImpl(ObjectKeyTemplate var1, ObjectId var2) {
      this.oktemp = var1;
      this.id = var2;
   }

   public void write(OutputStream var1) {
      this.oktemp.write(this.id, var1);
   }

   public byte[] getBytes(ORB var1) {
      EncapsOutputStream var2 = OutputStreamFactory.newEncapsOutputStream((com.sun.corba.se.spi.orb.ORB)var1);
      this.write(var2);
      return var2.toByteArray();
   }

   public CorbaServerRequestDispatcher getServerRequestDispatcher(com.sun.corba.se.spi.orb.ORB var1) {
      return this.oktemp.getServerRequestDispatcher(var1, this.id);
   }
}
