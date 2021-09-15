package com.sun.corba.se.impl.naming.pcosnaming;

import java.io.Serializable;
import org.omg.CORBA.Object;
import org.omg.CosNaming.BindingType;

public class InternalBindingValue implements Serializable {
   public BindingType theBindingType;
   public String strObjectRef;
   private transient Object theObjectRef;

   public InternalBindingValue() {
   }

   public InternalBindingValue(BindingType var1, String var2) {
      this.theBindingType = var1;
      this.strObjectRef = var2;
   }

   public Object getObjectRef() {
      return this.theObjectRef;
   }

   public void setObjectRef(Object var1) {
      this.theObjectRef = var1;
   }
}
