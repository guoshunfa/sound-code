package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.ORB;
import org.omg.IOP.Codec;
import org.omg.IOP.CodecFactory;
import org.omg.IOP.Encoding;
import org.omg.IOP.CodecFactoryPackage.UnknownEncoding;

public final class CodecFactoryImpl extends LocalObject implements CodecFactory {
   private ORB orb;
   private ORBUtilSystemException wrapper;
   private static final int MAX_MINOR_VERSION_SUPPORTED = 2;
   private Codec[] codecs = new Codec[3];

   public CodecFactoryImpl(ORB var1) {
      this.orb = var1;
      this.wrapper = ORBUtilSystemException.get((com.sun.corba.se.spi.orb.ORB)var1, "rpc.protocol");

      for(int var2 = 0; var2 <= 2; ++var2) {
         this.codecs[var2] = new CDREncapsCodec(var1, 1, var2);
      }

   }

   public Codec create_codec(Encoding var1) throws UnknownEncoding {
      if (var1 == null) {
         this.nullParam();
      }

      Codec var2 = null;
      if (var1.format == 0 && var1.major_version == 1 && var1.minor_version >= 0 && var1.minor_version <= 2) {
         var2 = this.codecs[var1.minor_version];
      }

      if (var2 == null) {
         throw new UnknownEncoding();
      } else {
         return var2;
      }
   }

   private void nullParam() {
      throw this.wrapper.nullParam();
   }
}
