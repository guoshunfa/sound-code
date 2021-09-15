package org.omg.CORBA;

public abstract class Context {
   public abstract String context_name();

   public abstract Context parent();

   public abstract Context create_child(String var1);

   public abstract void set_one_value(String var1, Any var2);

   public abstract void set_values(NVList var1);

   public abstract void delete_values(String var1);

   public abstract NVList get_values(String var1, int var2, String var3);
}
