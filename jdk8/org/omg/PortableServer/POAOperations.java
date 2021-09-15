package org.omg.PortableServer;

import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.POAPackage.AdapterAlreadyExists;
import org.omg.PortableServer.POAPackage.AdapterNonExistent;
import org.omg.PortableServer.POAPackage.InvalidPolicy;
import org.omg.PortableServer.POAPackage.NoServant;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongAdapter;
import org.omg.PortableServer.POAPackage.WrongPolicy;

public interface POAOperations {
   POA create_POA(String var1, POAManager var2, Policy[] var3) throws AdapterAlreadyExists, InvalidPolicy;

   POA find_POA(String var1, boolean var2) throws AdapterNonExistent;

   void destroy(boolean var1, boolean var2);

   ThreadPolicy create_thread_policy(ThreadPolicyValue var1);

   LifespanPolicy create_lifespan_policy(LifespanPolicyValue var1);

   IdUniquenessPolicy create_id_uniqueness_policy(IdUniquenessPolicyValue var1);

   IdAssignmentPolicy create_id_assignment_policy(IdAssignmentPolicyValue var1);

   ImplicitActivationPolicy create_implicit_activation_policy(ImplicitActivationPolicyValue var1);

   ServantRetentionPolicy create_servant_retention_policy(ServantRetentionPolicyValue var1);

   RequestProcessingPolicy create_request_processing_policy(RequestProcessingPolicyValue var1);

   String the_name();

   POA the_parent();

   POA[] the_children();

   POAManager the_POAManager();

   AdapterActivator the_activator();

   void the_activator(AdapterActivator var1);

   ServantManager get_servant_manager() throws WrongPolicy;

   void set_servant_manager(ServantManager var1) throws WrongPolicy;

   Servant get_servant() throws NoServant, WrongPolicy;

   void set_servant(Servant var1) throws WrongPolicy;

   byte[] activate_object(Servant var1) throws ServantAlreadyActive, WrongPolicy;

   void activate_object_with_id(byte[] var1, Servant var2) throws ServantAlreadyActive, ObjectAlreadyActive, WrongPolicy;

   void deactivate_object(byte[] var1) throws ObjectNotActive, WrongPolicy;

   Object create_reference(String var1) throws WrongPolicy;

   Object create_reference_with_id(byte[] var1, String var2);

   byte[] servant_to_id(Servant var1) throws ServantNotActive, WrongPolicy;

   Object servant_to_reference(Servant var1) throws ServantNotActive, WrongPolicy;

   Servant reference_to_servant(Object var1) throws ObjectNotActive, WrongPolicy, WrongAdapter;

   byte[] reference_to_id(Object var1) throws WrongAdapter, WrongPolicy;

   Servant id_to_servant(byte[] var1) throws ObjectNotActive, WrongPolicy;

   Object id_to_reference(byte[] var1) throws ObjectNotActive, WrongPolicy;

   byte[] id();
}
