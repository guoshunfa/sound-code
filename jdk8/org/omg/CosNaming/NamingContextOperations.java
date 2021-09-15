package org.omg.CosNaming;

import org.omg.CORBA.Object;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import org.omg.CosNaming.NamingContextPackage.NotFound;

public interface NamingContextOperations {
   void bind(NameComponent[] var1, Object var2) throws NotFound, CannotProceed, InvalidName, AlreadyBound;

   void bind_context(NameComponent[] var1, NamingContext var2) throws NotFound, CannotProceed, InvalidName, AlreadyBound;

   void rebind(NameComponent[] var1, Object var2) throws NotFound, CannotProceed, InvalidName;

   void rebind_context(NameComponent[] var1, NamingContext var2) throws NotFound, CannotProceed, InvalidName;

   Object resolve(NameComponent[] var1) throws NotFound, CannotProceed, InvalidName;

   void unbind(NameComponent[] var1) throws NotFound, CannotProceed, InvalidName;

   void list(int var1, BindingListHolder var2, BindingIteratorHolder var3);

   NamingContext new_context();

   NamingContext bind_new_context(NameComponent[] var1) throws NotFound, AlreadyBound, CannotProceed, InvalidName;

   void destroy() throws NotEmpty;
}
