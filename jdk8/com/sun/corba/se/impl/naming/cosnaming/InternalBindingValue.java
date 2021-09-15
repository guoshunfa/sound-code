package com.sun.corba.se.impl.naming.cosnaming;

import org.omg.CORBA.Object;
import org.omg.CosNaming.Binding;

public class InternalBindingValue {
   public Binding theBinding;
   public String strObjectRef;
   public Object theObjectRef;

   public InternalBindingValue() {
   }

   public InternalBindingValue(Binding var1, String var2) {
      this.theBinding = var1;
      this.strObjectRef = var2;
   }
}
