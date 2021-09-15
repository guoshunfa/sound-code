package org.omg.CORBA;

public abstract class ExceptionList {
   public abstract int count();

   public abstract void add(TypeCode var1);

   public abstract TypeCode item(int var1) throws Bounds;

   public abstract void remove(int var1) throws Bounds;
}
