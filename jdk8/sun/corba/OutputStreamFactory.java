package sun.corba;

import com.sun.corba.se.impl.encoding.CDROutputObject;
import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.impl.encoding.TypeCodeOutputStream;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.transport.CorbaConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;

public final class OutputStreamFactory {
   private OutputStreamFactory() {
   }

   public static TypeCodeOutputStream newTypeCodeOutputStream(final ORB var0) {
      return (TypeCodeOutputStream)AccessController.doPrivileged(new PrivilegedAction<TypeCodeOutputStream>() {
         public TypeCodeOutputStream run() {
            return new TypeCodeOutputStream(var0);
         }
      });
   }

   public static TypeCodeOutputStream newTypeCodeOutputStream(final ORB var0, final boolean var1) {
      return (TypeCodeOutputStream)AccessController.doPrivileged(new PrivilegedAction<TypeCodeOutputStream>() {
         public TypeCodeOutputStream run() {
            return new TypeCodeOutputStream(var0, var1);
         }
      });
   }

   public static EncapsOutputStream newEncapsOutputStream(final ORB var0) {
      return (EncapsOutputStream)AccessController.doPrivileged(new PrivilegedAction<EncapsOutputStream>() {
         public EncapsOutputStream run() {
            return new EncapsOutputStream(var0);
         }
      });
   }

   public static EncapsOutputStream newEncapsOutputStream(final ORB var0, final GIOPVersion var1) {
      return (EncapsOutputStream)AccessController.doPrivileged(new PrivilegedAction<EncapsOutputStream>() {
         public EncapsOutputStream run() {
            return new EncapsOutputStream(var0, var1);
         }
      });
   }

   public static EncapsOutputStream newEncapsOutputStream(final ORB var0, final boolean var1) {
      return (EncapsOutputStream)AccessController.doPrivileged(new PrivilegedAction<EncapsOutputStream>() {
         public EncapsOutputStream run() {
            return new EncapsOutputStream(var0, var1);
         }
      });
   }

   public static CDROutputObject newCDROutputObject(final ORB var0, final MessageMediator var1, final Message var2, final byte var3) {
      return (CDROutputObject)AccessController.doPrivileged(new PrivilegedAction<CDROutputObject>() {
         public CDROutputObject run() {
            return new CDROutputObject(var0, var1, var2, var3);
         }
      });
   }

   public static CDROutputObject newCDROutputObject(final ORB var0, final MessageMediator var1, final Message var2, final byte var3, final int var4) {
      return (CDROutputObject)AccessController.doPrivileged(new PrivilegedAction<CDROutputObject>() {
         public CDROutputObject run() {
            return new CDROutputObject(var0, var1, var2, var3, var4);
         }
      });
   }

   public static CDROutputObject newCDROutputObject(final ORB var0, final CorbaMessageMediator var1, final GIOPVersion var2, final CorbaConnection var3, final Message var4, final byte var5) {
      return (CDROutputObject)AccessController.doPrivileged(new PrivilegedAction<CDROutputObject>() {
         public CDROutputObject run() {
            return new CDROutputObject(var0, var1, var2, var3, var4, var5);
         }
      });
   }
}
