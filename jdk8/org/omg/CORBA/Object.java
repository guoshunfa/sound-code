package org.omg.CORBA;

public interface Object {
   boolean _is_a(String var1);

   boolean _is_equivalent(Object var1);

   boolean _non_existent();

   int _hash(int var1);

   Object _duplicate();

   void _release();

   Object _get_interface_def();

   Request _request(String var1);

   Request _create_request(Context var1, String var2, NVList var3, NamedValue var4);

   Request _create_request(Context var1, String var2, NVList var3, NamedValue var4, ExceptionList var5, ContextList var6);

   Policy _get_policy(int var1);

   DomainManager[] _get_domain_managers();

   Object _set_policy_override(Policy[] var1, SetOverrideType var2);
}
