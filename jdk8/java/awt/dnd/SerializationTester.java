package java.awt.dnd;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

final class SerializationTester {
   private static ObjectOutputStream stream;

   static boolean test(Object var0) {
      if (!(var0 instanceof Serializable)) {
         return false;
      } else {
         boolean var2;
         try {
            stream.writeObject(var0);
            return true;
         } catch (IOException var12) {
            var2 = false;
         } finally {
            try {
               stream.reset();
            } catch (IOException var11) {
            }

         }

         return var2;
      }
   }

   private SerializationTester() {
   }

   static {
      try {
         stream = new ObjectOutputStream(new OutputStream() {
            public void write(int var1) {
            }
         });
      } catch (IOException var1) {
      }

   }
}
