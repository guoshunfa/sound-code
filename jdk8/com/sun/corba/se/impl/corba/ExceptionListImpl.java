package com.sun.corba.se.impl.corba;

import java.util.Vector;
import org.omg.CORBA.Bounds;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.TypeCode;

public class ExceptionListImpl extends ExceptionList {
   private final int INITIAL_CAPACITY = 2;
   private final int CAPACITY_INCREMENT = 2;
   private Vector _exceptions = new Vector(2, 2);

   public int count() {
      return this._exceptions.size();
   }

   public void add(TypeCode var1) {
      this._exceptions.addElement(var1);
   }

   public TypeCode item(int var1) throws Bounds {
      try {
         return (TypeCode)this._exceptions.elementAt(var1);
      } catch (ArrayIndexOutOfBoundsException var3) {
         throw new Bounds();
      }
   }

   public void remove(int var1) throws Bounds {
      try {
         this._exceptions.removeElementAt(var1);
      } catch (ArrayIndexOutOfBoundsException var3) {
         throw new Bounds();
      }
   }
}
