package com.sun.corba.se.impl.naming.cosnaming;

import org.omg.CORBA.Object;
import org.omg.CORBA.SystemException;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.BindingTypeHolder;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.PortableServer.POA;

public interface NamingContextDataStore {
   void Bind(NameComponent var1, Object var2, BindingType var3) throws SystemException;

   Object Resolve(NameComponent var1, BindingTypeHolder var2) throws SystemException;

   Object Unbind(NameComponent var1) throws SystemException;

   void List(int var1, BindingListHolder var2, BindingIteratorHolder var3) throws SystemException;

   NamingContext NewContext() throws SystemException;

   void Destroy() throws SystemException;

   boolean IsEmpty();

   POA getNSPOA();
}
