package org.omg.CosNaming;

public interface BindingIteratorOperations {
   boolean next_one(BindingHolder var1);

   boolean next_n(int var1, BindingListHolder var2);

   void destroy();
}
