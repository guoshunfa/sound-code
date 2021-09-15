package com.sun.corba.se.spi.ior;

import com.sun.corba.se.impl.ior.EncapsulationUtility;
import org.omg.CORBA_2_3.portable.InputStream;

public abstract class EncapsulationFactoryBase implements IdentifiableFactory {
   private int id;

   public int getId() {
      return this.id;
   }

   public EncapsulationFactoryBase(int var1) {
      this.id = var1;
   }

   public final Identifiable create(InputStream var1) {
      InputStream var2 = EncapsulationUtility.getEncapsulationStream(var1);
      return this.readContents(var2);
   }

   protected abstract Identifiable readContents(InputStream var1);
}
