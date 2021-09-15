package org.omg.CORBA;

import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ServantObject;

public class LocalObject implements Object {
   private static String reason = "This is a locally constrained object.";

   public boolean _is_equivalent(Object var1) {
      return this.equals(var1);
   }

   public boolean _non_existent() {
      return false;
   }

   public int _hash(int var1) {
      return this.hashCode();
   }

   public boolean _is_a(String var1) {
      throw new NO_IMPLEMENT(reason);
   }

   public Object _duplicate() {
      throw new NO_IMPLEMENT(reason);
   }

   public void _release() {
      throw new NO_IMPLEMENT(reason);
   }

   public Request _request(String var1) {
      throw new NO_IMPLEMENT(reason);
   }

   public Request _create_request(Context var1, String var2, NVList var3, NamedValue var4) {
      throw new NO_IMPLEMENT(reason);
   }

   public Request _create_request(Context var1, String var2, NVList var3, NamedValue var4, ExceptionList var5, ContextList var6) {
      throw new NO_IMPLEMENT(reason);
   }

   public Object _get_interface() {
      throw new NO_IMPLEMENT(reason);
   }

   public Object _get_interface_def() {
      throw new NO_IMPLEMENT(reason);
   }

   public ORB _orb() {
      throw new NO_IMPLEMENT(reason);
   }

   public Policy _get_policy(int var1) {
      throw new NO_IMPLEMENT(reason);
   }

   public DomainManager[] _get_domain_managers() {
      throw new NO_IMPLEMENT(reason);
   }

   public Object _set_policy_override(Policy[] var1, SetOverrideType var2) {
      throw new NO_IMPLEMENT(reason);
   }

   public boolean _is_local() {
      throw new NO_IMPLEMENT(reason);
   }

   public ServantObject _servant_preinvoke(String var1, Class var2) {
      throw new NO_IMPLEMENT(reason);
   }

   public void _servant_postinvoke(ServantObject var1) {
      throw new NO_IMPLEMENT(reason);
   }

   public OutputStream _request(String var1, boolean var2) {
      throw new NO_IMPLEMENT(reason);
   }

   public InputStream _invoke(OutputStream var1) throws ApplicationException, RemarshalException {
      throw new NO_IMPLEMENT(reason);
   }

   public void _releaseReply(InputStream var1) {
      throw new NO_IMPLEMENT(reason);
   }

   public boolean validate_connection() {
      throw new NO_IMPLEMENT(reason);
   }
}
