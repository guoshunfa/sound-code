package com.sun.corba.se.impl.corba;

import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.NamedValue;

public class NamedValueImpl extends NamedValue {
   private String _name;
   private Any _value;
   private int _flags;
   private ORB _orb;

   public NamedValueImpl(ORB var1) {
      this._orb = var1;
      this._value = new AnyImpl(this._orb);
   }

   public NamedValueImpl(ORB var1, String var2, Any var3, int var4) {
      this._orb = var1;
      this._name = var2;
      this._value = var3;
      this._flags = var4;
   }

   public String name() {
      return this._name;
   }

   public Any value() {
      return this._value;
   }

   public int flags() {
      return this._flags;
   }
}
