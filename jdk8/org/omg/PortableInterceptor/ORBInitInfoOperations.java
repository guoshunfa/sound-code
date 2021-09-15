package org.omg.PortableInterceptor;

import org.omg.CORBA.Object;
import org.omg.IOP.CodecFactory;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import org.omg.PortableInterceptor.ORBInitInfoPackage.InvalidName;

public interface ORBInitInfoOperations {
   String[] arguments();

   String orb_id();

   CodecFactory codec_factory();

   void register_initial_reference(String var1, Object var2) throws InvalidName;

   Object resolve_initial_references(String var1) throws InvalidName;

   void add_client_request_interceptor(ClientRequestInterceptor var1) throws DuplicateName;

   void add_server_request_interceptor(ServerRequestInterceptor var1) throws DuplicateName;

   void add_ior_interceptor(IORInterceptor var1) throws DuplicateName;

   int allocate_slot_id();

   void register_policy_factory(int var1, PolicyFactory var2);
}
