package java.rmi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.ObjectInputFilter;
import sun.rmi.server.MarshalInputStream;
import sun.rmi.server.MarshalOutputStream;

public final class MarshalledObject<T> implements Serializable {
   private byte[] objBytes = null;
   private byte[] locBytes = null;
   private int hash;
   private transient ObjectInputFilter objectInputFilter = null;
   private static final long serialVersionUID = 8988374069173025854L;

   public MarshalledObject(T var1) throws IOException {
      if (var1 == null) {
         this.hash = 13;
      } else {
         ByteArrayOutputStream var2 = new ByteArrayOutputStream();
         ByteArrayOutputStream var3 = new ByteArrayOutputStream();
         MarshalledObject.MarshalledObjectOutputStream var4 = new MarshalledObject.MarshalledObjectOutputStream(var2, var3);
         var4.writeObject(var1);
         var4.flush();
         this.objBytes = var2.toByteArray();
         this.locBytes = var4.hadAnnotations() ? var3.toByteArray() : null;
         int var5 = 0;

         for(int var6 = 0; var6 < this.objBytes.length; ++var6) {
            var5 = 31 * var5 + this.objBytes[var6];
         }

         this.hash = var5;
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.objectInputFilter = ObjectInputFilter.Config.getObjectInputFilter(var1);
   }

   public T get() throws IOException, ClassNotFoundException {
      if (this.objBytes == null) {
         return null;
      } else {
         ByteArrayInputStream var1 = new ByteArrayInputStream(this.objBytes);
         ByteArrayInputStream var2 = this.locBytes == null ? null : new ByteArrayInputStream(this.locBytes);
         MarshalledObject.MarshalledObjectInputStream var3 = new MarshalledObject.MarshalledObjectInputStream(var1, var2, this.objectInputFilter);
         Object var4 = var3.readObject();
         var3.close();
         return var4;
      }
   }

   public int hashCode() {
      return this.hash;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 != null && var1 instanceof MarshalledObject) {
         MarshalledObject var2 = (MarshalledObject)var1;
         if (this.objBytes != null && var2.objBytes != null) {
            if (this.objBytes.length != var2.objBytes.length) {
               return false;
            } else {
               for(int var3 = 0; var3 < this.objBytes.length; ++var3) {
                  if (this.objBytes[var3] != var2.objBytes[var3]) {
                     return false;
                  }
               }

               return true;
            }
         } else {
            return this.objBytes == var2.objBytes;
         }
      } else {
         return false;
      }
   }

   private static class MarshalledObjectInputStream extends MarshalInputStream {
      private ObjectInputStream locIn;

      MarshalledObjectInputStream(InputStream var1, InputStream var2, final ObjectInputFilter var3) throws IOException {
         super(var1);
         this.locIn = var2 == null ? null : new ObjectInputStream(var2);
         if (var3 != null) {
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
               public Void run() {
                  ObjectInputFilter.Config.setObjectInputFilter(MarshalledObjectInputStream.this, var3);
                  if (MarshalledObjectInputStream.this.locIn != null) {
                     ObjectInputFilter.Config.setObjectInputFilter(MarshalledObjectInputStream.this.locIn, var3);
                  }

                  return null;
               }
            });
         }

      }

      protected Object readLocation() throws IOException, ClassNotFoundException {
         return this.locIn == null ? null : this.locIn.readObject();
      }
   }

   private static class MarshalledObjectOutputStream extends MarshalOutputStream {
      private ObjectOutputStream locOut;
      private boolean hadAnnotations;

      MarshalledObjectOutputStream(OutputStream var1, OutputStream var2) throws IOException {
         super(var1);
         this.useProtocolVersion(2);
         this.locOut = new ObjectOutputStream(var2);
         this.hadAnnotations = false;
      }

      boolean hadAnnotations() {
         return this.hadAnnotations;
      }

      protected void writeLocation(String var1) throws IOException {
         this.hadAnnotations |= var1 != null;
         this.locOut.writeObject(var1);
      }

      public void flush() throws IOException {
         super.flush();
         this.locOut.flush();
      }
   }
}
