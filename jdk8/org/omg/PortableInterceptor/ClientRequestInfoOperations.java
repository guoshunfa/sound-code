package org.omg.PortableInterceptor;

import org.omg.CORBA.Any;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.IOP.ServiceContext;
import org.omg.IOP.TaggedComponent;
import org.omg.IOP.TaggedProfile;

public interface ClientRequestInfoOperations extends RequestInfoOperations {
   Object target();

   Object effective_target();

   TaggedProfile effective_profile();

   Any received_exception();

   String received_exception_id();

   TaggedComponent get_effective_component(int var1);

   TaggedComponent[] get_effective_components(int var1);

   Policy get_request_policy(int var1);

   void add_request_service_context(ServiceContext var1, boolean var2);
}
