package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.TaggedComponentBase;
import com.sun.corba.se.spi.ior.iiop.MaxStreamFormatVersionComponent;
import org.omg.CORBA_2_3.portable.OutputStream;

public class MaxStreamFormatVersionComponentImpl extends TaggedComponentBase implements MaxStreamFormatVersionComponent {
   private byte version;
   public static final MaxStreamFormatVersionComponentImpl singleton = new MaxStreamFormatVersionComponentImpl();

   public boolean equals(Object var1) {
      if (!(var1 instanceof MaxStreamFormatVersionComponentImpl)) {
         return false;
      } else {
         MaxStreamFormatVersionComponentImpl var2 = (MaxStreamFormatVersionComponentImpl)var1;
         return this.version == var2.version;
      }
   }

   public int hashCode() {
      return this.version;
   }

   public String toString() {
      return "MaxStreamFormatVersionComponentImpl[version=" + this.version + "]";
   }

   public MaxStreamFormatVersionComponentImpl() {
      this.version = ORBUtility.getMaxStreamFormatVersion();
   }

   public MaxStreamFormatVersionComponentImpl(byte var1) {
      this.version = var1;
   }

   public byte getMaxStreamFormatVersion() {
      return this.version;
   }

   public void writeContents(OutputStream var1) {
      var1.write_octet(this.version);
   }

   public int getId() {
      return 38;
   }
}
