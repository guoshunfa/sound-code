package com.sun.corba.se.impl.corba;

import com.sun.corba.se.spi.orb.ORB;
import java.util.Vector;
import org.omg.CORBA.Any;
import org.omg.CORBA.Bounds;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;

public class NVListImpl extends NVList {
   private final int INITIAL_CAPACITY = 4;
   private final int CAPACITY_INCREMENT = 2;
   private Vector _namedValues;
   private ORB orb;

   public NVListImpl(ORB var1) {
      this.orb = var1;
      this._namedValues = new Vector(4, 2);
   }

   public NVListImpl(ORB var1, int var2) {
      this.orb = var1;
      this._namedValues = new Vector(var2);
   }

   public int count() {
      return this._namedValues.size();
   }

   public NamedValue add(int var1) {
      NamedValueImpl var2 = new NamedValueImpl(this.orb, "", new AnyImpl(this.orb), var1);
      this._namedValues.addElement(var2);
      return var2;
   }

   public NamedValue add_item(String var1, int var2) {
      NamedValueImpl var3 = new NamedValueImpl(this.orb, var1, new AnyImpl(this.orb), var2);
      this._namedValues.addElement(var3);
      return var3;
   }

   public NamedValue add_value(String var1, Any var2, int var3) {
      NamedValueImpl var4 = new NamedValueImpl(this.orb, var1, var2, var3);
      this._namedValues.addElement(var4);
      return var4;
   }

   public NamedValue item(int var1) throws Bounds {
      try {
         return (NamedValue)this._namedValues.elementAt(var1);
      } catch (ArrayIndexOutOfBoundsException var3) {
         throw new Bounds();
      }
   }

   public void remove(int var1) throws Bounds {
      try {
         this._namedValues.removeElementAt(var1);
      } catch (ArrayIndexOutOfBoundsException var3) {
         throw new Bounds();
      }
   }
}
