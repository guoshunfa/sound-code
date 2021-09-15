package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.encoding.EncapsInputStream;
import com.sun.corba.se.impl.logging.IORSystemException;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyFactory;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.orb.ORB;
import java.io.IOException;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.OctetSeqHolder;
import org.omg.CORBA_2_3.portable.InputStream;
import sun.corba.EncapsInputStreamFactory;

public class ObjectKeyFactoryImpl implements ObjectKeyFactory {
   public static final int MAGIC_BASE = -1347695874;
   public static final int JAVAMAGIC_OLD = -1347695874;
   public static final int JAVAMAGIC_NEW = -1347695873;
   public static final int JAVAMAGIC_NEWER = -1347695872;
   public static final int MAX_MAGIC = -1347695872;
   public static final byte JDK1_3_1_01_PATCH_LEVEL = 1;
   private final ORB orb;
   private IORSystemException wrapper;
   private Handler fullKey = new Handler() {
      public ObjectKeyTemplate handle(int var1, int var2, InputStream var3, OctetSeqHolder var4) {
         Object var5 = null;
         if (var2 >= 32 && var2 <= 63) {
            if (var1 >= -1347695872) {
               var5 = new POAObjectKeyTemplate(ObjectKeyFactoryImpl.this.orb, var1, var2, var3, var4);
            } else {
               var5 = new OldPOAObjectKeyTemplate(ObjectKeyFactoryImpl.this.orb, var1, var2, var3, var4);
            }
         } else if (var2 >= 0 && var2 < 32) {
            if (var1 >= -1347695872) {
               var5 = new JIDLObjectKeyTemplate(ObjectKeyFactoryImpl.this.orb, var1, var2, var3, var4);
            } else {
               var5 = new OldJIDLObjectKeyTemplate(ObjectKeyFactoryImpl.this.orb, var1, var2, var3, var4);
            }
         }

         return (ObjectKeyTemplate)var5;
      }
   };
   private Handler oktempOnly = new Handler() {
      public ObjectKeyTemplate handle(int var1, int var2, InputStream var3, OctetSeqHolder var4) {
         Object var5 = null;
         if (var2 >= 32 && var2 <= 63) {
            if (var1 >= -1347695872) {
               var5 = new POAObjectKeyTemplate(ObjectKeyFactoryImpl.this.orb, var1, var2, var3);
            } else {
               var5 = new OldPOAObjectKeyTemplate(ObjectKeyFactoryImpl.this.orb, var1, var2, var3);
            }
         } else if (var2 >= 0 && var2 < 32) {
            if (var1 >= -1347695872) {
               var5 = new JIDLObjectKeyTemplate(ObjectKeyFactoryImpl.this.orb, var1, var2, var3);
            } else {
               var5 = new OldJIDLObjectKeyTemplate(ObjectKeyFactoryImpl.this.orb, var1, var2, var3);
            }
         }

         return (ObjectKeyTemplate)var5;
      }
   };

   public ObjectKeyFactoryImpl(ORB var1) {
      this.orb = var1;
      this.wrapper = IORSystemException.get(var1, "oa.ior");
   }

   private boolean validMagic(int var1) {
      return var1 >= -1347695874 && var1 <= -1347695872;
   }

   private ObjectKeyTemplate create(InputStream var1, Handler var2, OctetSeqHolder var3) {
      ObjectKeyTemplate var4 = null;

      try {
         var1.mark(0);
         int var5 = var1.read_long();
         if (this.validMagic(var5)) {
            int var6 = var1.read_long();
            var4 = var2.handle(var5, var6, var1, var3);
         }
      } catch (MARSHAL var8) {
      }

      if (var4 == null) {
         try {
            var1.reset();
         } catch (IOException var7) {
         }
      }

      return var4;
   }

   public ObjectKey create(byte[] var1) {
      OctetSeqHolder var2 = new OctetSeqHolder();
      EncapsInputStream var3 = EncapsInputStreamFactory.newEncapsInputStream(this.orb, var1, var1.length);
      Object var4 = this.create(var3, this.fullKey, var2);
      if (var4 == null) {
         var4 = new WireObjectKeyTemplate(var3, var2);
      }

      ObjectIdImpl var5 = new ObjectIdImpl(var2.value);
      return new ObjectKeyImpl((ObjectKeyTemplate)var4, var5);
   }

   public ObjectKeyTemplate createTemplate(InputStream var1) {
      Object var2 = this.create(var1, this.oktempOnly, (OctetSeqHolder)null);
      if (var2 == null) {
         var2 = new WireObjectKeyTemplate(this.orb);
      }

      return (ObjectKeyTemplate)var2;
   }
}
