package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.encoding.CDROutputStream;
import com.sun.corba.se.impl.encoding.EncapsInputStream;
import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.spi.ior.Identifiable;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
import com.sun.corba.se.spi.ior.WriteContents;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Iterator;
import java.util.List;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import sun.corba.EncapsInputStreamFactory;
import sun.corba.OutputStreamFactory;

public class EncapsulationUtility {
   private EncapsulationUtility() {
   }

   public static void readIdentifiableSequence(List var0, IdentifiableFactoryFinder var1, InputStream var2) {
      int var3 = var2.read_long();

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = var2.read_long();
         Identifiable var6 = var1.create(var5, var2);
         var0.add(var6);
      }

   }

   public static void writeIdentifiableSequence(List var0, OutputStream var1) {
      var1.write_long(var0.size());
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         Identifiable var3 = (Identifiable)((Identifiable)var2.next());
         var1.write_long(var3.getId());
         var3.write(var1);
      }

   }

   public static void writeOutputStream(OutputStream var0, OutputStream var1) {
      byte[] var2 = ((CDROutputStream)var0).toByteArray();
      var1.write_long(var2.length);
      var1.write_octet_array(var2, 0, var2.length);
   }

   public static InputStream getEncapsulationStream(InputStream var0) {
      byte[] var1 = readOctets(var0);
      EncapsInputStream var2 = EncapsInputStreamFactory.newEncapsInputStream(var0.orb(), var1, var1.length);
      var2.consumeEndian();
      return var2;
   }

   public static byte[] readOctets(InputStream var0) {
      int var1 = var0.read_ulong();
      byte[] var2 = new byte[var1];
      var0.read_octet_array(var2, 0, var1);
      return var2;
   }

   public static void writeEncapsulation(WriteContents var0, OutputStream var1) {
      EncapsOutputStream var2 = OutputStreamFactory.newEncapsOutputStream((ORB)var1.orb());
      var2.putEndian();
      var0.writeContents(var2);
      writeOutputStream(var2, var1);
   }
}
