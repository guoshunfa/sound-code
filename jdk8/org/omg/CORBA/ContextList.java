package org.omg.CORBA;

public abstract class ContextList {
   public abstract int count();

   public abstract void add(String var1);

   public abstract String item(int var1) throws Bounds;

   public abstract void remove(int var1) throws Bounds;
}
