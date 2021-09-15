package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.spi.orb.ORB;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import sun.corba.EncapsInputStreamFactory;

public final class TypeCodeOutputStream extends EncapsOutputStream {
   private OutputStream enclosure = null;
   private Map typeMap = null;
   private boolean isEncapsulation = false;

   public TypeCodeOutputStream(ORB var1) {
      super(var1, false);
   }

   public TypeCodeOutputStream(ORB var1, boolean var2) {
      super(var1, var2);
   }

   public InputStream create_input_stream() {
      TypeCodeInputStream var1 = EncapsInputStreamFactory.newTypeCodeInputStream((ORB)this.orb(), (ByteBuffer)this.getByteBuffer(), this.getIndex(), this.isLittleEndian(), this.getGIOPVersion());
      return var1;
   }

   public void setEnclosingOutputStream(OutputStream var1) {
      this.enclosure = var1;
   }

   public TypeCodeOutputStream getTopLevelStream() {
      if (this.enclosure == null) {
         return this;
      } else {
         return this.enclosure instanceof TypeCodeOutputStream ? ((TypeCodeOutputStream)this.enclosure).getTopLevelStream() : this;
      }
   }

   public int getTopLevelPosition() {
      if (this.enclosure != null && this.enclosure instanceof TypeCodeOutputStream) {
         int var1 = ((TypeCodeOutputStream)this.enclosure).getTopLevelPosition() + this.getPosition();
         if (this.isEncapsulation) {
            var1 += 4;
         }

         return var1;
      } else {
         return this.getPosition();
      }
   }

   public void addIDAtPosition(String var1, int var2) {
      if (this.typeMap == null) {
         this.typeMap = new HashMap(16);
      }

      this.typeMap.put(var1, new Integer(var2));
   }

   public int getPositionForID(String var1) {
      if (this.typeMap == null) {
         throw this.wrapper.refTypeIndirType(CompletionStatus.COMPLETED_NO);
      } else {
         return (Integer)this.typeMap.get(var1);
      }
   }

   public void writeRawBuffer(org.omg.CORBA.portable.OutputStream var1, int var2) {
      var1.write_long(var2);
      ByteBuffer var3 = this.getByteBuffer();
      if (var3.hasArray()) {
         var1.write_octet_array(var3.array(), 4, this.getIndex() - 4);
      } else {
         byte[] var4 = new byte[var3.limit()];

         for(int var5 = 0; var5 < var4.length; ++var5) {
            var4[var5] = var3.get(var5);
         }

         var1.write_octet_array(var4, 4, this.getIndex() - 4);
      }

   }

   public TypeCodeOutputStream createEncapsulation(org.omg.CORBA.ORB var1) {
      TypeCodeOutputStream var2 = sun.corba.OutputStreamFactory.newTypeCodeOutputStream((ORB)var1, this.isLittleEndian());
      var2.setEnclosingOutputStream(this);
      var2.makeEncapsulation();
      return var2;
   }

   protected void makeEncapsulation() {
      this.putEndian();
      this.isEncapsulation = true;
   }

   public static TypeCodeOutputStream wrapOutputStream(OutputStream var0) {
      boolean var1 = var0 instanceof CDROutputStream ? ((CDROutputStream)var0).isLittleEndian() : false;
      TypeCodeOutputStream var2 = sun.corba.OutputStreamFactory.newTypeCodeOutputStream((ORB)var0.orb(), var1);
      var2.setEnclosingOutputStream(var0);
      return var2;
   }

   public int getPosition() {
      return this.getIndex();
   }

   public int getRealIndex(int var1) {
      int var2 = this.getTopLevelPosition();
      return var2;
   }

   public byte[] getTypeCodeBuffer() {
      ByteBuffer var1 = this.getByteBuffer();
      byte[] var2 = new byte[this.getIndex() - 4];

      for(int var3 = 0; var3 < var2.length; ++var3) {
         var2[var3] = var1.get(var3 + 4);
      }

      return var2;
   }

   public void printTypeMap() {
      System.out.println("typeMap = {");
      Iterator var1 = this.typeMap.keySet().iterator();

      while(var1.hasNext()) {
         String var2 = (String)var1.next();
         Integer var3 = (Integer)this.typeMap.get(var2);
         System.out.println("  key = " + var2 + ", value = " + var3);
      }

      System.out.println("}");
   }
}
