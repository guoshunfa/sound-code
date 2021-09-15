package com.sun.corba.se.impl.corba;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import org.omg.CORBA.Any;
import org.omg.CORBA.Context;
import org.omg.CORBA.NVList;
import org.omg.CORBA.ORB;

public final class ContextImpl extends Context {
   private ORB _orb;
   private ORBUtilSystemException wrapper;

   public ContextImpl(ORB var1) {
      this._orb = var1;
      this.wrapper = ORBUtilSystemException.get((com.sun.corba.se.spi.orb.ORB)var1, "rpc.presentation");
   }

   public ContextImpl(Context var1) {
      throw this.wrapper.contextNotImplemented();
   }

   public String context_name() {
      throw this.wrapper.contextNotImplemented();
   }

   public Context parent() {
      throw this.wrapper.contextNotImplemented();
   }

   public Context create_child(String var1) {
      throw this.wrapper.contextNotImplemented();
   }

   public void set_one_value(String var1, Any var2) {
      throw this.wrapper.contextNotImplemented();
   }

   public void set_values(NVList var1) {
      throw this.wrapper.contextNotImplemented();
   }

   public void delete_values(String var1) {
      throw this.wrapper.contextNotImplemented();
   }

   public NVList get_values(String var1, int var2, String var3) {
      throw this.wrapper.contextNotImplemented();
   }
}
