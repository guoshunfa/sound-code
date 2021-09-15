package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.spi.ior.TaggedComponentBase;
import org.omg.CORBA_2_3.portable.OutputStream;

public class JavaSerializationComponent extends TaggedComponentBase {
   private byte version;
   private static JavaSerializationComponent singleton;

   public static JavaSerializationComponent singleton() {
      if (singleton == null) {
         Class var0 = JavaSerializationComponent.class;
         synchronized(JavaSerializationComponent.class) {
            singleton = new JavaSerializationComponent((byte)1);
         }
      }

      return singleton;
   }

   public JavaSerializationComponent(byte var1) {
      this.version = var1;
   }

   public byte javaSerializationVersion() {
      return this.version;
   }

   public void writeContents(OutputStream var1) {
      var1.write_octet(this.version);
   }

   public int getId() {
      return 1398099458;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof JavaSerializationComponent)) {
         return false;
      } else {
         JavaSerializationComponent var2 = (JavaSerializationComponent)var1;
         return this.version == var2.version;
      }
   }

   public int hashCode() {
      return this.version;
   }
}
