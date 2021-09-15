package sun.corba;

import com.sun.corba.se.impl.encoding.EncapsInputStream;
import com.sun.corba.se.impl.encoding.TypeCodeInputStream;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.org.omg.SendingContext.CodeBase;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.omg.CORBA.ORB;

public class EncapsInputStreamFactory {
   public static EncapsInputStream newEncapsInputStream(final ORB var0, final byte[] var1, final int var2, final boolean var3, final GIOPVersion var4) {
      return (EncapsInputStream)AccessController.doPrivileged(new PrivilegedAction<EncapsInputStream>() {
         public EncapsInputStream run() {
            return new EncapsInputStream(var0, var1, var2, var3, var4);
         }
      });
   }

   public static EncapsInputStream newEncapsInputStream(final ORB var0, final ByteBuffer var1, final int var2, final boolean var3, final GIOPVersion var4) {
      return (EncapsInputStream)AccessController.doPrivileged(new PrivilegedAction<EncapsInputStream>() {
         public EncapsInputStream run() {
            return new EncapsInputStream(var0, var1, var2, var3, var4);
         }
      });
   }

   public static EncapsInputStream newEncapsInputStream(final ORB var0, final byte[] var1, final int var2) {
      return (EncapsInputStream)AccessController.doPrivileged(new PrivilegedAction<EncapsInputStream>() {
         public EncapsInputStream run() {
            return new EncapsInputStream(var0, var1, var2);
         }
      });
   }

   public static EncapsInputStream newEncapsInputStream(final EncapsInputStream var0) {
      return (EncapsInputStream)AccessController.doPrivileged(new PrivilegedAction<EncapsInputStream>() {
         public EncapsInputStream run() {
            return new EncapsInputStream(var0);
         }
      });
   }

   public static EncapsInputStream newEncapsInputStream(final ORB var0, final byte[] var1, final int var2, final GIOPVersion var3) {
      return (EncapsInputStream)AccessController.doPrivileged(new PrivilegedAction<EncapsInputStream>() {
         public EncapsInputStream run() {
            return new EncapsInputStream(var0, var1, var2, var3);
         }
      });
   }

   public static EncapsInputStream newEncapsInputStream(final ORB var0, final byte[] var1, final int var2, final GIOPVersion var3, final CodeBase var4) {
      return (EncapsInputStream)AccessController.doPrivileged(new PrivilegedAction<EncapsInputStream>() {
         public EncapsInputStream run() {
            return new EncapsInputStream(var0, var1, var2, var3, var4);
         }
      });
   }

   public static TypeCodeInputStream newTypeCodeInputStream(final ORB var0, final byte[] var1, final int var2, final boolean var3, final GIOPVersion var4) {
      return (TypeCodeInputStream)AccessController.doPrivileged(new PrivilegedAction<TypeCodeInputStream>() {
         public TypeCodeInputStream run() {
            return new TypeCodeInputStream(var0, var1, var2, var3, var4);
         }
      });
   }

   public static TypeCodeInputStream newTypeCodeInputStream(final ORB var0, final ByteBuffer var1, final int var2, final boolean var3, final GIOPVersion var4) {
      return (TypeCodeInputStream)AccessController.doPrivileged(new PrivilegedAction<TypeCodeInputStream>() {
         public TypeCodeInputStream run() {
            return new TypeCodeInputStream(var0, var1, var2, var3, var4);
         }
      });
   }

   public static TypeCodeInputStream newTypeCodeInputStream(final ORB var0, final byte[] var1, final int var2) {
      return (TypeCodeInputStream)AccessController.doPrivileged(new PrivilegedAction<TypeCodeInputStream>() {
         public TypeCodeInputStream run() {
            return new TypeCodeInputStream(var0, var1, var2);
         }
      });
   }
}
