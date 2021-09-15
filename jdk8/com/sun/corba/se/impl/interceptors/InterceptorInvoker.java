package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.SystemException;
import org.omg.PortableInterceptor.ClientRequestInterceptor;
import org.omg.PortableInterceptor.ForwardRequest;
import org.omg.PortableInterceptor.IORInterceptor;
import org.omg.PortableInterceptor.IORInterceptor_3_0;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;
import org.omg.PortableInterceptor.ServerRequestInterceptor;

public class InterceptorInvoker {
   private ORB orb;
   private InterceptorList interceptorList;
   private boolean enabled = false;
   private PICurrent current;

   InterceptorInvoker(ORB var1, InterceptorList var2, PICurrent var3) {
      this.orb = var1;
      this.interceptorList = var2;
      this.enabled = false;
      this.current = var3;
   }

   void setEnabled(boolean var1) {
      this.enabled = var1;
   }

   void objectAdapterCreated(ObjectAdapter var1) {
      if (this.enabled) {
         IORInfoImpl var2 = new IORInfoImpl(var1);
         IORInterceptor[] var3 = (IORInterceptor[])((IORInterceptor[])this.interceptorList.getInterceptors(2));
         int var4 = var3.length;

         int var5;
         IORInterceptor var6;
         for(var5 = var4 - 1; var5 >= 0; --var5) {
            var6 = var3[var5];

            try {
               var6.establish_components(var2);
            } catch (Exception var8) {
            }
         }

         var2.makeStateEstablished();

         for(var5 = var4 - 1; var5 >= 0; --var5) {
            var6 = var3[var5];
            if (var6 instanceof IORInterceptor_3_0) {
               IORInterceptor_3_0 var7 = (IORInterceptor_3_0)var6;
               var7.components_established(var2);
            }
         }

         var2.makeStateDone();
      }

   }

   void adapterManagerStateChanged(int var1, short var2) {
      if (this.enabled) {
         IORInterceptor[] var3 = (IORInterceptor[])((IORInterceptor[])this.interceptorList.getInterceptors(2));
         int var4 = var3.length;

         for(int var5 = var4 - 1; var5 >= 0; --var5) {
            try {
               IORInterceptor var6 = var3[var5];
               if (var6 instanceof IORInterceptor_3_0) {
                  IORInterceptor_3_0 var7 = (IORInterceptor_3_0)var6;
                  var7.adapter_manager_state_changed(var1, var2);
               }
            } catch (Exception var8) {
            }
         }
      }

   }

   void adapterStateChanged(ObjectReferenceTemplate[] var1, short var2) {
      if (this.enabled) {
         IORInterceptor[] var3 = (IORInterceptor[])((IORInterceptor[])this.interceptorList.getInterceptors(2));
         int var4 = var3.length;

         for(int var5 = var4 - 1; var5 >= 0; --var5) {
            try {
               IORInterceptor var6 = var3[var5];
               if (var6 instanceof IORInterceptor_3_0) {
                  IORInterceptor_3_0 var7 = (IORInterceptor_3_0)var6;
                  var7.adapter_state_changed(var1, var2);
               }
            } catch (Exception var8) {
            }
         }
      }

   }

   void invokeClientInterceptorStartingPoint(ClientRequestInfoImpl var1) {
      if (this.enabled) {
         try {
            this.current.pushSlotTable();
            var1.setPICurrentPushed(true);
            var1.setCurrentExecutionPoint(0);
            ClientRequestInterceptor[] var2 = (ClientRequestInterceptor[])((ClientRequestInterceptor[])this.interceptorList.getInterceptors(0));
            int var3 = var2.length;
            int var4 = var3;
            boolean var5 = true;

            for(int var6 = 0; var5 && var6 < var3; ++var6) {
               try {
                  var2[var6].send_request(var1);
               } catch (ForwardRequest var12) {
                  var4 = var6;
                  var1.setForwardRequest(var12);
                  var1.setEndingPointCall(2);
                  var1.setReplyStatus((short)3);
                  this.updateClientRequestDispatcherForward(var1);
                  var5 = false;
               } catch (SystemException var13) {
                  var4 = var6;
                  var1.setEndingPointCall(1);
                  var1.setReplyStatus((short)1);
                  var1.setException(var13);
                  var5 = false;
               }
            }

            var1.setFlowStackIndex(var4);
         } finally {
            this.current.resetSlotTable();
         }
      }

   }

   void invokeClientInterceptorEndingPoint(ClientRequestInfoImpl var1) {
      if (this.enabled) {
         try {
            var1.setCurrentExecutionPoint(2);
            ClientRequestInterceptor[] var2 = (ClientRequestInterceptor[])((ClientRequestInterceptor[])this.interceptorList.getInterceptors(0));
            int var3 = var1.getFlowStackIndex();
            int var4 = var1.getEndingPointCall();
            if (var4 == 0 && var1.getIsOneWay()) {
               var4 = 2;
               var1.setEndingPointCall(var4);
            }

            for(int var5 = var3 - 1; var5 >= 0; --var5) {
               try {
                  switch(var4) {
                  case 0:
                     var2[var5].receive_reply(var1);
                     break;
                  case 1:
                     var2[var5].receive_exception(var1);
                     break;
                  case 2:
                     var2[var5].receive_other(var1);
                  }
               } catch (ForwardRequest var11) {
                  var4 = 2;
                  var1.setEndingPointCall(var4);
                  var1.setReplyStatus((short)3);
                  var1.setForwardRequest(var11);
                  this.updateClientRequestDispatcherForward(var1);
               } catch (SystemException var12) {
                  var4 = 1;
                  var1.setEndingPointCall(var4);
                  var1.setReplyStatus((short)1);
                  var1.setException(var12);
               }
            }
         } finally {
            if (var1 != null && var1.isPICurrentPushed()) {
               this.current.popSlotTable();
            }

         }
      }

   }

   void invokeServerInterceptorStartingPoint(ServerRequestInfoImpl var1) {
      if (this.enabled) {
         try {
            this.current.pushSlotTable();
            var1.setSlotTable(this.current.getSlotTable());
            this.current.pushSlotTable();
            var1.setCurrentExecutionPoint(0);
            ServerRequestInterceptor[] var2 = (ServerRequestInterceptor[])((ServerRequestInterceptor[])this.interceptorList.getInterceptors(1));
            int var3 = var2.length;
            int var4 = var3;
            boolean var5 = true;

            for(int var6 = 0; var5 && var6 < var3; ++var6) {
               try {
                  var2[var6].receive_request_service_contexts(var1);
               } catch (ForwardRequest var12) {
                  var4 = var6;
                  var1.setForwardRequest(var12);
                  var1.setIntermediatePointCall(1);
                  var1.setEndingPointCall(2);
                  var1.setReplyStatus((short)3);
                  var5 = false;
               } catch (SystemException var13) {
                  var4 = var6;
                  var1.setException(var13);
                  var1.setIntermediatePointCall(1);
                  var1.setEndingPointCall(1);
                  var1.setReplyStatus((short)1);
                  var5 = false;
               }
            }

            var1.setFlowStackIndex(var4);
         } finally {
            this.current.popSlotTable();
         }
      }

   }

   void invokeServerInterceptorIntermediatePoint(ServerRequestInfoImpl var1) {
      int var2 = var1.getIntermediatePointCall();
      if (this.enabled && var2 != 1) {
         var1.setCurrentExecutionPoint(1);
         ServerRequestInterceptor[] var3 = (ServerRequestInterceptor[])((ServerRequestInterceptor[])this.interceptorList.getInterceptors(1));
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            try {
               var3[var5].receive_request(var1);
            } catch (ForwardRequest var7) {
               var1.setForwardRequest(var7);
               var1.setEndingPointCall(2);
               var1.setReplyStatus((short)3);
               break;
            } catch (SystemException var8) {
               var1.setException(var8);
               var1.setEndingPointCall(1);
               var1.setReplyStatus((short)1);
               break;
            }
         }
      }

   }

   void invokeServerInterceptorEndingPoint(ServerRequestInfoImpl var1) {
      if (this.enabled) {
         try {
            ServerRequestInterceptor[] var2 = (ServerRequestInterceptor[])((ServerRequestInterceptor[])this.interceptorList.getInterceptors(1));
            int var3 = var1.getFlowStackIndex();
            int var4 = var1.getEndingPointCall();

            for(int var5 = var3 - 1; var5 >= 0; --var5) {
               try {
                  switch(var4) {
                  case 0:
                     var2[var5].send_reply(var1);
                     break;
                  case 1:
                     var2[var5].send_exception(var1);
                     break;
                  case 2:
                     var2[var5].send_other(var1);
                  }
               } catch (ForwardRequest var11) {
                  var4 = 2;
                  var1.setEndingPointCall(var4);
                  var1.setForwardRequest(var11);
                  var1.setReplyStatus((short)3);
                  var1.setForwardRequestRaisedInEnding();
               } catch (SystemException var12) {
                  var4 = 1;
                  var1.setEndingPointCall(var4);
                  var1.setException(var12);
                  var1.setReplyStatus((short)1);
               }
            }

            var1.setAlreadyExecuted(true);
         } finally {
            this.current.popSlotTable();
         }
      }

   }

   private void updateClientRequestDispatcherForward(ClientRequestInfoImpl var1) {
      ForwardRequest var2 = var1.getForwardRequestException();
      if (var2 != null) {
         Object var3 = var2.forward;
         IOR var4 = ORBUtility.getIOR(var3);
         var1.setLocatedIOR(var4);
      }

   }
}
