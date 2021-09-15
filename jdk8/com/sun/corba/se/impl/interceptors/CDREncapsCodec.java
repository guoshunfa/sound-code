package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.corba.AnyImpl;
import com.sun.corba.se.impl.encoding.EncapsInputStream;
import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.Any;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.IOP.Codec;
import org.omg.IOP.CodecPackage.FormatMismatch;
import org.omg.IOP.CodecPackage.InvalidTypeForEncoding;
import org.omg.IOP.CodecPackage.TypeMismatch;
import sun.corba.EncapsInputStreamFactory;
import sun.corba.OutputStreamFactory;

public final class CDREncapsCodec extends LocalObject implements Codec {
   private ORB orb;
   ORBUtilSystemException wrapper;
   private GIOPVersion giopVersion;

   public CDREncapsCodec(ORB var1, int var2, int var3) {
      this.orb = var1;
      this.wrapper = ORBUtilSystemException.get((com.sun.corba.se.spi.orb.ORB)var1, "rpc.protocol");
      this.giopVersion = GIOPVersion.getInstance((byte)var2, (byte)var3);
   }

   public byte[] encode(Any var1) throws InvalidTypeForEncoding {
      if (var1 == null) {
         throw this.wrapper.nullParam();
      } else {
         return this.encodeImpl(var1, true);
      }
   }

   public Any decode(byte[] var1) throws FormatMismatch {
      if (var1 == null) {
         throw this.wrapper.nullParam();
      } else {
         return this.decodeImpl(var1, (TypeCode)null);
      }
   }

   public byte[] encode_value(Any var1) throws InvalidTypeForEncoding {
      if (var1 == null) {
         throw this.wrapper.nullParam();
      } else {
         return this.encodeImpl(var1, false);
      }
   }

   public Any decode_value(byte[] var1, TypeCode var2) throws FormatMismatch, TypeMismatch {
      if (var1 == null) {
         throw this.wrapper.nullParam();
      } else if (var2 == null) {
         throw this.wrapper.nullParam();
      } else {
         return this.decodeImpl(var1, var2);
      }
   }

   private byte[] encodeImpl(Any var1, boolean var2) throws InvalidTypeForEncoding {
      if (var1 == null) {
         throw this.wrapper.nullParam();
      } else {
         EncapsOutputStream var3 = OutputStreamFactory.newEncapsOutputStream((com.sun.corba.se.spi.orb.ORB)this.orb, this.giopVersion);
         var3.putEndian();
         if (var2) {
            var3.write_TypeCode(var1.type());
         }

         var1.write_value(var3);
         return var3.toByteArray();
      }
   }

   private Any decodeImpl(byte[] var1, TypeCode var2) throws FormatMismatch {
      if (var1 == null) {
         throw this.wrapper.nullParam();
      } else {
         AnyImpl var3 = null;

         try {
            EncapsInputStream var4 = EncapsInputStreamFactory.newEncapsInputStream(this.orb, var1, var1.length, this.giopVersion);
            var4.consumeEndian();
            if (var2 == null) {
               var2 = var4.read_TypeCode();
            }

            var3 = new AnyImpl((com.sun.corba.se.spi.orb.ORB)this.orb);
            var3.read_value(var4, var2);
            return var3;
         } catch (RuntimeException var5) {
            throw new FormatMismatch();
         }
      }
   }
}
