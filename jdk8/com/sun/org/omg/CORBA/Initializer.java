package com.sun.org.omg.CORBA;

import org.omg.CORBA.StructMember;
import org.omg.CORBA.portable.IDLEntity;

public final class Initializer implements IDLEntity {
   public StructMember[] members = null;
   public String name = null;

   public Initializer() {
   }

   public Initializer(StructMember[] var1, String var2) {
      this.members = var1;
      this.name = var2;
   }
}
