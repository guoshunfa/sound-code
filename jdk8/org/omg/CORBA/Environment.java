package org.omg.CORBA;

public abstract class Environment {
   public abstract Exception exception();

   public abstract void exception(Exception var1);

   public abstract void clear();
}
