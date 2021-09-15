package com.sun.corba.se.impl.orb;

import com.sun.corba.se.spi.orb.ORBVersion;
import org.omg.CORBA.portable.OutputStream;

public class ORBVersionImpl implements ORBVersion {
   private byte orbType;
   public static final ORBVersion FOREIGN = new ORBVersionImpl((byte)0);
   public static final ORBVersion OLD = new ORBVersionImpl((byte)1);
   public static final ORBVersion NEW = new ORBVersionImpl((byte)2);
   public static final ORBVersion JDK1_3_1_01 = new ORBVersionImpl((byte)3);
   public static final ORBVersion NEWER = new ORBVersionImpl((byte)10);
   public static final ORBVersion PEORB = new ORBVersionImpl((byte)20);

   public ORBVersionImpl(byte var1) {
      this.orbType = var1;
   }

   public byte getORBType() {
      return this.orbType;
   }

   public void write(OutputStream var1) {
      var1.write_octet(this.orbType);
   }

   public String toString() {
      return "ORBVersionImpl[" + Byte.toString(this.orbType) + "]";
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof ORBVersion)) {
         return false;
      } else {
         ORBVersion var2 = (ORBVersion)var1;
         return var2.getORBType() == this.orbType;
      }
   }

   public int hashCode() {
      return this.orbType;
   }

   public boolean lessThan(ORBVersion var1) {
      return this.orbType < var1.getORBType();
   }

   public int compareTo(Object var1) {
      return this.getORBType() - ((ORBVersion)var1).getORBType();
   }
}
