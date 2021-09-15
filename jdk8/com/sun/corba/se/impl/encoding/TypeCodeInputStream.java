package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.corba.TypeCodeImpl;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.omg.CORBA.ORB;
import org.omg.CORBA_2_3.portable.InputStream;
import sun.corba.EncapsInputStreamFactory;

public class TypeCodeInputStream extends EncapsInputStream implements TypeCodeReader {
   private Map typeMap = null;
   private InputStream enclosure = null;
   private boolean isEncapsulation = false;

   public TypeCodeInputStream(ORB var1, byte[] var2, int var3) {
      super(var1, var2, var3);
   }

   public TypeCodeInputStream(ORB var1, byte[] var2, int var3, boolean var4, GIOPVersion var5) {
      super(var1, var2, var3, var4, var5);
   }

   public TypeCodeInputStream(ORB var1, ByteBuffer var2, int var3, boolean var4, GIOPVersion var5) {
      super(var1, var2, var3, var4, var5);
   }

   public void addTypeCodeAtPosition(TypeCodeImpl var1, int var2) {
      if (this.typeMap == null) {
         this.typeMap = new HashMap(16);
      }

      this.typeMap.put(new Integer(var2), var1);
   }

   public TypeCodeImpl getTypeCodeAtPosition(int var1) {
      return this.typeMap == null ? null : (TypeCodeImpl)this.typeMap.get(new Integer(var1));
   }

   public void setEnclosingInputStream(InputStream var1) {
      this.enclosure = var1;
   }

   public TypeCodeReader getTopLevelStream() {
      if (this.enclosure == null) {
         return this;
      } else {
         return (TypeCodeReader)(this.enclosure instanceof TypeCodeReader ? ((TypeCodeReader)this.enclosure).getTopLevelStream() : this);
      }
   }

   public int getTopLevelPosition() {
      if (this.enclosure != null && this.enclosure instanceof TypeCodeReader) {
         int var1 = ((TypeCodeReader)this.enclosure).getTopLevelPosition();
         int var2 = var1 - this.getBufferLength() + this.getPosition();
         return var2;
      } else {
         return this.getPosition();
      }
   }

   public static TypeCodeInputStream readEncapsulation(InputStream var0, ORB var1) {
      int var3 = var0.read_long();
      byte[] var4 = new byte[var3];
      var0.read_octet_array(var4, 0, var4.length);
      TypeCodeInputStream var2;
      if (var0 instanceof CDRInputStream) {
         var2 = EncapsInputStreamFactory.newTypeCodeInputStream((com.sun.corba.se.spi.orb.ORB)var1, (byte[])var4, var4.length, ((CDRInputStream)var0).isLittleEndian(), ((CDRInputStream)var0).getGIOPVersion());
      } else {
         var2 = EncapsInputStreamFactory.newTypeCodeInputStream((com.sun.corba.se.spi.orb.ORB)var1, var4, var4.length);
      }

      var2.setEnclosingInputStream(var0);
      var2.makeEncapsulation();
      return var2;
   }

   protected void makeEncapsulation() {
      this.consumeEndian();
      this.isEncapsulation = true;
   }

   public void printTypeMap() {
      System.out.println("typeMap = {");
      Iterator var1 = this.typeMap.keySet().iterator();

      while(var1.hasNext()) {
         Integer var2 = (Integer)var1.next();
         TypeCodeImpl var3 = (TypeCodeImpl)this.typeMap.get(var2);
         System.out.println("  key = " + var2 + ", value = " + var3.description());
      }

      System.out.println("}");
   }
}
