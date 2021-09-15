package java.beans;

import com.sun.beans.finder.ClassFinder;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;

class ObjectInputStreamWithLoader extends ObjectInputStream {
   private ClassLoader loader;

   public ObjectInputStreamWithLoader(InputStream var1, ClassLoader var2) throws IOException, StreamCorruptedException {
      super(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("Illegal null argument to ObjectInputStreamWithLoader");
      } else {
         this.loader = var2;
      }
   }

   protected Class resolveClass(ObjectStreamClass var1) throws IOException, ClassNotFoundException {
      String var2 = var1.getName();
      return ClassFinder.resolveClass(var2, this.loader);
   }
}
