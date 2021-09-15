package org.omg.CORBA;

public abstract class NVList {
   public abstract int count();

   public abstract NamedValue add(int var1);

   public abstract NamedValue add_item(String var1, int var2);

   public abstract NamedValue add_value(String var1, Any var2, int var3);

   public abstract NamedValue item(int var1) throws Bounds;

   public abstract void remove(int var1) throws Bounds;
}
