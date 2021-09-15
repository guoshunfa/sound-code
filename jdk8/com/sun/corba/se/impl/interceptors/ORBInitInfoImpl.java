package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.logging.InterceptorsSystemException;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.legacy.interceptor.ORBInitInfoExt;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.CORBA.PolicyError;
import org.omg.IOP.CodecFactory;
import org.omg.PortableInterceptor.ClientRequestInterceptor;
import org.omg.PortableInterceptor.IORInterceptor;
import org.omg.PortableInterceptor.ORBInitInfo;
import org.omg.PortableInterceptor.PolicyFactory;
import org.omg.PortableInterceptor.ServerRequestInterceptor;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import org.omg.PortableInterceptor.ORBInitInfoPackage.InvalidName;

public final class ORBInitInfoImpl extends LocalObject implements ORBInitInfo, ORBInitInfoExt {
   private ORB orb;
   private InterceptorsSystemException wrapper;
   private ORBUtilSystemException orbutilWrapper;
   private OMGSystemException omgWrapper;
   private String[] args;
   private String orbId;
   private CodecFactory codecFactory;
   private int stage = 0;
   public static final int STAGE_PRE_INIT = 0;
   public static final int STAGE_POST_INIT = 1;
   public static final int STAGE_CLOSED = 2;
   private static final String MESSAGE_ORBINITINFO_INVALID = "ORBInitInfo object is only valid during ORB_init";

   ORBInitInfoImpl(ORB var1, String[] var2, String var3, CodecFactory var4) {
      this.orb = var1;
      this.wrapper = InterceptorsSystemException.get(var1, "rpc.protocol");
      this.orbutilWrapper = ORBUtilSystemException.get(var1, "rpc.protocol");
      this.omgWrapper = OMGSystemException.get(var1, "rpc.protocol");
      this.args = var2;
      this.orbId = var3;
      this.codecFactory = var4;
   }

   public ORB getORB() {
      return this.orb;
   }

   void setStage(int var1) {
      this.stage = var1;
   }

   private void checkStage() {
      if (this.stage == 2) {
         throw this.wrapper.orbinitinfoInvalid();
      }
   }

   public String[] arguments() {
      this.checkStage();
      return this.args;
   }

   public String orb_id() {
      this.checkStage();
      return this.orbId;
   }

   public CodecFactory codec_factory() {
      this.checkStage();
      return this.codecFactory;
   }

   public void register_initial_reference(String var1, Object var2) throws InvalidName {
      this.checkStage();
      if (var1 == null) {
         this.nullParam();
      }

      if (var2 == null) {
         throw this.omgWrapper.rirWithNullObject();
      } else {
         try {
            this.orb.register_initial_reference(var1, var2);
         } catch (org.omg.CORBA.ORBPackage.InvalidName var5) {
            InvalidName var4 = new InvalidName(var5.getMessage());
            var4.initCause(var5);
            throw var4;
         }
      }
   }

   public Object resolve_initial_references(String var1) throws InvalidName {
      this.checkStage();
      if (var1 == null) {
         this.nullParam();
      }

      if (this.stage == 0) {
         throw this.wrapper.rirInvalidPreInit();
      } else {
         Object var2 = null;

         try {
            var2 = this.orb.resolve_initial_references(var1);
            return var2;
         } catch (org.omg.CORBA.ORBPackage.InvalidName var4) {
            throw new InvalidName();
         }
      }
   }

   public void add_client_request_interceptor_with_policy(ClientRequestInterceptor var1, Policy[] var2) throws DuplicateName {
      this.add_client_request_interceptor(var1);
   }

   public void add_client_request_interceptor(ClientRequestInterceptor var1) throws DuplicateName {
      this.checkStage();
      if (var1 == null) {
         this.nullParam();
      }

      this.orb.getPIHandler().register_interceptor(var1, 0);
   }

   public void add_server_request_interceptor_with_policy(ServerRequestInterceptor var1, Policy[] var2) throws DuplicateName, PolicyError {
      this.add_server_request_interceptor(var1);
   }

   public void add_server_request_interceptor(ServerRequestInterceptor var1) throws DuplicateName {
      this.checkStage();
      if (var1 == null) {
         this.nullParam();
      }

      this.orb.getPIHandler().register_interceptor(var1, 1);
   }

   public void add_ior_interceptor_with_policy(IORInterceptor var1, Policy[] var2) throws DuplicateName, PolicyError {
      this.add_ior_interceptor(var1);
   }

   public void add_ior_interceptor(IORInterceptor var1) throws DuplicateName {
      this.checkStage();
      if (var1 == null) {
         this.nullParam();
      }

      this.orb.getPIHandler().register_interceptor(var1, 2);
   }

   public int allocate_slot_id() {
      this.checkStage();
      return ((PICurrent)this.orb.getPIHandler().getPICurrent()).allocateSlotId();
   }

   public void register_policy_factory(int var1, PolicyFactory var2) {
      this.checkStage();
      if (var2 == null) {
         this.nullParam();
      }

      this.orb.getPIHandler().registerPolicyFactory(var1, var2);
   }

   private void nullParam() throws BAD_PARAM {
      throw this.orbutilWrapper.nullParam();
   }
}
