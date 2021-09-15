package com.sun.corba.se.impl.oa.poa;

import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.ThreadPolicy;
import org.omg.PortableServer.ThreadPolicyValue;

final class ThreadPolicyImpl extends LocalObject implements ThreadPolicy {
   private ThreadPolicyValue value;

   public ThreadPolicyImpl(ThreadPolicyValue var1) {
      this.value = var1;
   }

   public ThreadPolicyValue value() {
      return this.value;
   }

   public int policy_type() {
      return 16;
   }

   public Policy copy() {
      return new ThreadPolicyImpl(this.value);
   }

   public void destroy() {
      this.value = null;
   }

   public String toString() {
      return "ThreadPolicy[" + (this.value.value() == 1 ? "SINGLE_THREAD_MODEL" : "ORB_CTRL_MODEL]");
   }
}
