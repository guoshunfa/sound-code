package com.sun.corba.se.impl.corba;

import java.util.Vector;
import org.omg.CORBA.Bounds;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.ORB;

public class ContextListImpl extends ContextList {
   private final int INITIAL_CAPACITY = 2;
   private final int CAPACITY_INCREMENT = 2;
   private ORB _orb;
   private Vector _contexts;

   public ContextListImpl(ORB var1) {
      this._orb = var1;
      this._contexts = new Vector(2, 2);
   }

   public int count() {
      return this._contexts.size();
   }

   public void add(String var1) {
      this._contexts.addElement(var1);
   }

   public String item(int var1) throws Bounds {
      try {
         return (String)this._contexts.elementAt(var1);
      } catch (ArrayIndexOutOfBoundsException var3) {
         throw new Bounds();
      }
   }

   public void remove(int var1) throws Bounds {
      try {
         this._contexts.removeElementAt(var1);
      } catch (ArrayIndexOutOfBoundsException var3) {
         throw new Bounds();
      }
   }
}
