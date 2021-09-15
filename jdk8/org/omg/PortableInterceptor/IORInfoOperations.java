package org.omg.PortableInterceptor;

import org.omg.CORBA.Policy;
import org.omg.IOP.TaggedComponent;

public interface IORInfoOperations {
   Policy get_effective_policy(int var1);

   void add_ior_component(TaggedComponent var1);

   void add_ior_component_to_profile(TaggedComponent var1, int var2);

   int manager_id();

   short state();

   ObjectReferenceTemplate adapter_template();

   ObjectReferenceFactory current_factory();

   void current_factory(ObjectReferenceFactory var1);
}
