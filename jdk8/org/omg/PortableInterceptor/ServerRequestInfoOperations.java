package org.omg.PortableInterceptor;

import org.omg.CORBA.Any;
import org.omg.CORBA.Policy;
import org.omg.IOP.ServiceContext;

public interface ServerRequestInfoOperations extends RequestInfoOperations {
   Any sending_exception();

   byte[] object_id();

   byte[] adapter_id();

   String server_id();

   String orb_id();

   String[] adapter_name();

   String target_most_derived_interface();

   Policy get_server_policy(int var1);

   void set_slot(int var1, Any var2) throws InvalidSlot;

   boolean target_is_a(String var1);

   void add_reply_service_context(ServiceContext var1, boolean var2);
}
