package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.corba.ServerRequestImpl;
import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.impl.encoding.MarshalInputStream;
import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.oa.NullServant;
import com.sun.corba.se.spi.oa.OADestroyed;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.spi.protocol.ForwardException;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import com.sun.corba.se.spi.servicecontext.CodeSetServiceContext;
import com.sun.corba.se.spi.servicecontext.ORBVersionServiceContext;
import com.sun.corba.se.spi.servicecontext.SendingContextServiceContext;
import com.sun.corba.se.spi.servicecontext.ServiceContext;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.spi.servicecontext.UEInfoServiceContext;
import com.sun.corba.se.spi.transport.CorbaConnection;
import org.omg.CORBA.Any;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.DynamicImplementation;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.UnknownException;

public class CorbaServerRequestDispatcherImpl implements CorbaServerRequestDispatcher {
   protected ORB orb;
   private ORBUtilSystemException wrapper;
   private POASystemException poaWrapper;

   public CorbaServerRequestDispatcherImpl(ORB var1) {
      this.orb = var1;
      this.wrapper = ORBUtilSystemException.get(var1, "rpc.protocol");
      this.poaWrapper = POASystemException.get(var1, "rpc.protocol");
   }

   public IOR locate(ObjectKey var1) {
      try {
         if (this.orb.subcontractDebugFlag) {
            this.dprint(".locate->");
         }

         ObjectKeyTemplate var2 = var1.getTemplate();

         try {
            this.checkServerId(var1);
         } catch (ForwardException var8) {
            IOR var4 = var8.getIOR();
            return var4;
         }

         this.findObjectAdapter(var2);
         Object var3 = null;
         return (IOR)var3;
      } finally {
         if (this.orb.subcontractDebugFlag) {
            this.dprint(".locate<-");
         }

      }
   }

   public void dispatch(MessageMediator var1) {
      CorbaMessageMediator var2 = (CorbaMessageMediator)var1;

      try {
         if (this.orb.subcontractDebugFlag) {
            this.dprint(".dispatch->: " + this.opAndId(var2));
         }

         this.consumeServiceContexts(var2);
         ((MarshalInputStream)var2.getInputObject()).performORBVersionSpecificInit();
         ObjectKey var3 = var2.getObjectKey();

         try {
            this.checkServerId(var3);
         } catch (ForwardException var23) {
            if (this.orb.subcontractDebugFlag) {
               this.dprint(".dispatch: " + this.opAndId(var2) + ": bad server id");
            }

            var2.getProtocolHandler().createLocationForward(var2, var23.getIOR(), (ServiceContexts)null);
            return;
         }

         String var4 = var2.getOperationName();
         ObjectAdapter var5 = null;

         try {
            byte[] var6 = var3.getId().getId();
            ObjectKeyTemplate var25 = var3.getTemplate();
            var5 = this.findObjectAdapter(var25);
            Object var26 = this.getServantWithPI(var2, var5, var6, var25, var4);
            this.dispatchToServant(var26, var2, var6, var5);
         } catch (ForwardException var18) {
            if (this.orb.subcontractDebugFlag) {
               this.dprint(".dispatch: " + this.opAndId(var2) + ": ForwardException caught");
            }

            var2.getProtocolHandler().createLocationForward(var2, var18.getIOR(), (ServiceContexts)null);
         } catch (OADestroyed var19) {
            if (this.orb.subcontractDebugFlag) {
               this.dprint(".dispatch: " + this.opAndId(var2) + ": OADestroyed exception caught");
            }

            this.dispatch(var2);
         } catch (RequestCanceledException var20) {
            if (this.orb.subcontractDebugFlag) {
               this.dprint(".dispatch: " + this.opAndId(var2) + ": RequestCanceledException caught");
            }

            throw var20;
         } catch (UnknownException var21) {
            if (this.orb.subcontractDebugFlag) {
               this.dprint(".dispatch: " + this.opAndId(var2) + ": UnknownException caught " + var21);
            }

            if (var21.originalEx instanceof RequestCanceledException) {
               throw (RequestCanceledException)var21.originalEx;
            } else {
               ServiceContexts var7 = new ServiceContexts(this.orb);
               UEInfoServiceContext var8 = new UEInfoServiceContext(var21.originalEx);
               var7.put(var8);
               UNKNOWN var9 = this.wrapper.unknownExceptionInDispatch(CompletionStatus.COMPLETED_MAYBE, var21);
               var2.getProtocolHandler().createSystemExceptionResponse(var2, var9, var7);
            }
         } catch (Throwable var22) {
            if (this.orb.subcontractDebugFlag) {
               this.dprint(".dispatch: " + this.opAndId(var2) + ": other exception " + var22);
            }

            var2.getProtocolHandler().handleThrowableDuringServerDispatch(var2, var22, CompletionStatus.COMPLETED_MAYBE);
         }
      } finally {
         if (this.orb.subcontractDebugFlag) {
            this.dprint(".dispatch<-: " + this.opAndId(var2));
         }

      }
   }

   private void releaseServant(ObjectAdapter var1) {
      try {
         if (this.orb.subcontractDebugFlag) {
            this.dprint(".releaseServant->");
         }

         if (var1 != null) {
            try {
               var1.returnServant();
               return;
            } finally {
               var1.exit();
               this.orb.popInvocationInfo();
            }
         }

         if (this.orb.subcontractDebugFlag) {
            this.dprint(".releaseServant: null object adapter");
         }
      } finally {
         if (this.orb.subcontractDebugFlag) {
            this.dprint(".releaseServant<-");
         }

      }

   }

   private Object getServant(ObjectAdapter var1, byte[] var2, String var3) throws OADestroyed {
      Object var5;
      try {
         if (this.orb.subcontractDebugFlag) {
            this.dprint(".getServant->");
         }

         OAInvocationInfo var4 = var1.makeInvocationInfo(var2);
         var4.setOperation(var3);
         this.orb.pushInvocationInfo(var4);
         var1.getInvocationServant(var4);
         var5 = var4.getServantContainer();
      } finally {
         if (this.orb.subcontractDebugFlag) {
            this.dprint(".getServant<-");
         }

      }

      return var5;
   }

   protected Object getServantWithPI(CorbaMessageMediator var1, ObjectAdapter var2, byte[] var3, ObjectKeyTemplate var4, String var5) throws OADestroyed {
      Object var8;
      try {
         if (this.orb.subcontractDebugFlag) {
            this.dprint(".getServantWithPI->");
         }

         this.orb.getPIHandler().initializeServerPIInfo(var1, var2, var3, var4);
         this.orb.getPIHandler().invokeServerPIStartingPoint();
         var2.enter();
         if (var1 != null) {
            var1.setExecuteReturnServantInResponseConstructor(true);
         }

         Object var6 = this.getServant(var2, var3, var5);
         String var7 = "unknown";
         if (var6 instanceof NullServant) {
            this.handleNullServant(var5, (NullServant)var6);
         } else {
            var7 = var2.getInterfaces(var6, var3)[0];
         }

         this.orb.getPIHandler().setServerPIInfo(var6, var7);
         if (var6 != null && !(var6 instanceof DynamicImplementation) && !(var6 instanceof org.omg.PortableServer.DynamicImplementation) || SpecialMethod.getSpecialMethod(var5) != null) {
            this.orb.getPIHandler().invokeServerPIIntermediatePoint();
         }

         var8 = var6;
      } finally {
         if (this.orb.subcontractDebugFlag) {
            this.dprint(".getServantWithPI<-");
         }

      }

      return var8;
   }

   protected void checkServerId(ObjectKey var1) {
      try {
         if (this.orb.subcontractDebugFlag) {
            this.dprint(".checkServerId->");
         }

         ObjectKeyTemplate var2 = var1.getTemplate();
         int var3 = var2.getServerId();
         int var4 = var2.getSubcontractId();
         if (!this.orb.isLocalServerId(var4, var3)) {
            if (this.orb.subcontractDebugFlag) {
               this.dprint(".checkServerId: bad server id");
            }

            this.orb.handleBadServerId(var1);
         }
      } finally {
         if (this.orb.subcontractDebugFlag) {
            this.dprint(".checkServerId<-");
         }

      }

   }

   private ObjectAdapter findObjectAdapter(ObjectKeyTemplate var1) {
      ObjectAdapter var7;
      try {
         if (this.orb.subcontractDebugFlag) {
            this.dprint(".findObjectAdapter->");
         }

         RequestDispatcherRegistry var2 = this.orb.getRequestDispatcherRegistry();
         int var3 = var1.getSubcontractId();
         ObjectAdapterFactory var4 = var2.getObjectAdapterFactory(var3);
         if (var4 == null) {
            if (this.orb.subcontractDebugFlag) {
               this.dprint(".findObjectAdapter: failed to find ObjectAdapterFactory");
            }

            throw this.wrapper.noObjectAdapterFactory();
         }

         ObjectAdapterId var5 = var1.getObjectAdapterId();
         ObjectAdapter var6 = var4.find(var5);
         if (var6 == null) {
            if (this.orb.subcontractDebugFlag) {
               this.dprint(".findObjectAdapter: failed to find ObjectAdaptor");
            }

            throw this.wrapper.badAdapterId();
         }

         var7 = var6;
      } finally {
         if (this.orb.subcontractDebugFlag) {
            this.dprint(".findObjectAdapter<-");
         }

      }

      return var7;
   }

   protected void handleNullServant(String var1, NullServant var2) {
      try {
         if (this.orb.subcontractDebugFlag) {
            this.dprint(".handleNullServant->: " + var1);
         }

         SpecialMethod var3 = SpecialMethod.getSpecialMethod(var1);
         if (var3 == null || !var3.isNonExistentMethod()) {
            if (this.orb.subcontractDebugFlag) {
               this.dprint(".handleNullServant: " + var1 + ": throwing OBJECT_NOT_EXIST");
            }

            throw var2.getException();
         }
      } finally {
         if (this.orb.subcontractDebugFlag) {
            this.dprint(".handleNullServant<-: " + var1);
         }

      }

   }

   protected void consumeServiceContexts(CorbaMessageMediator var1) {
      try {
         if (this.orb.subcontractDebugFlag) {
            this.dprint(".consumeServiceContexts->: " + this.opAndId(var1));
         }

         ServiceContexts var2 = var1.getRequestServiceContexts();
         GIOPVersion var4 = var1.getGIOPVersion();
         boolean var5 = this.processCodeSetContext(var1, var2);
         if (this.orb.subcontractDebugFlag) {
            this.dprint(".consumeServiceContexts: " + this.opAndId(var1) + ": GIOP version: " + var4);
            this.dprint(".consumeServiceContexts: " + this.opAndId(var1) + ": as code set context? " + var5);
         }

         ServiceContext var3 = var2.get(6);
         if (var3 != null) {
            SendingContextServiceContext var6 = (SendingContextServiceContext)var3;
            IOR var7 = var6.getIOR();

            try {
               ((CorbaConnection)var1.getConnection()).setCodeBaseIOR(var7);
            } catch (ThreadDeath var13) {
               throw var13;
            } catch (Throwable var14) {
               throw this.wrapper.badStringifiedIor(var14);
            }
         }

         boolean var16 = false;
         if (var4.equals(GIOPVersion.V1_0) && var5) {
            if (this.orb.subcontractDebugFlag) {
               this.dprint(".consumeServiceCOntexts: " + this.opAndId(var1) + ": Determined to be an old Sun ORB");
            }

            this.orb.setORBVersion(ORBVersionFactory.getOLD());
         } else {
            var16 = true;
         }

         var3 = var2.get(1313165056);
         if (var3 != null) {
            ORBVersionServiceContext var17 = (ORBVersionServiceContext)var3;
            ORBVersion var8 = var17.getVersion();
            this.orb.setORBVersion(var8);
            var16 = false;
         }

         if (var16) {
            if (this.orb.subcontractDebugFlag) {
               this.dprint(".consumeServiceContexts: " + this.opAndId(var1) + ": Determined to be a foreign ORB");
            }

            this.orb.setORBVersion(ORBVersionFactory.getFOREIGN());
         }
      } finally {
         if (this.orb.subcontractDebugFlag) {
            this.dprint(".consumeServiceContexts<-: " + this.opAndId(var1));
         }

      }

   }

   protected CorbaMessageMediator dispatchToServant(Object var1, CorbaMessageMediator var2, byte[] var3, ObjectAdapter var4) {
      CorbaMessageMediator var15;
      try {
         if (this.orb.subcontractDebugFlag) {
            this.dprint(".dispatchToServant->: " + this.opAndId(var2));
         }

         CorbaMessageMediator var5 = null;
         String var6 = var2.getOperationName();
         SpecialMethod var7 = SpecialMethod.getSpecialMethod(var6);
         if (var7 != null) {
            if (this.orb.subcontractDebugFlag) {
               this.dprint(".dispatchToServant: " + this.opAndId(var2) + ": Handling special method");
            }

            var5 = var7.invoke(var1, var2, var3, var4);
            var15 = var5;
            return var15;
         }

         ServerRequestImpl var9;
         if (var1 instanceof DynamicImplementation) {
            if (this.orb.subcontractDebugFlag) {
               this.dprint(".dispatchToServant: " + this.opAndId(var2) + ": Handling old style DSI type servant");
            }

            DynamicImplementation var8 = (DynamicImplementation)var1;
            var9 = new ServerRequestImpl(var2, this.orb);
            var8.invoke(var9);
            var5 = this.handleDynamicResult(var9, var2);
         } else if (var1 instanceof org.omg.PortableServer.DynamicImplementation) {
            if (this.orb.subcontractDebugFlag) {
               this.dprint(".dispatchToServant: " + this.opAndId(var2) + ": Handling POA DSI type servant");
            }

            org.omg.PortableServer.DynamicImplementation var13 = (org.omg.PortableServer.DynamicImplementation)var1;
            var9 = new ServerRequestImpl(var2, this.orb);
            var13.invoke(var9);
            var5 = this.handleDynamicResult(var9, var2);
         } else {
            if (this.orb.subcontractDebugFlag) {
               this.dprint(".dispatchToServant: " + this.opAndId(var2) + ": Handling invoke handler type servant");
            }

            InvokeHandler var14 = (InvokeHandler)var1;
            OutputStream var16 = var14._invoke(var6, (InputStream)var2.getInputObject(), var2);
            var5 = (CorbaMessageMediator)((OutputObject)var16).getMessageMediator();
         }

         var15 = var5;
      } finally {
         if (this.orb.subcontractDebugFlag) {
            this.dprint(".dispatchToServant<-: " + this.opAndId(var2));
         }

      }

      return var15;
   }

   protected CorbaMessageMediator handleDynamicResult(ServerRequestImpl var1, CorbaMessageMediator var2) {
      CorbaMessageMediator var9;
      try {
         if (this.orb.subcontractDebugFlag) {
            this.dprint(".handleDynamicResult->: " + this.opAndId(var2));
         }

         CorbaMessageMediator var3 = null;
         Any var4 = var1.checkResultCalled();
         if (var4 == null) {
            if (this.orb.subcontractDebugFlag) {
               this.dprint(".handleDynamicResult: " + this.opAndId(var2) + ": handling normal result");
            }

            var3 = this.sendingReply(var2);
            OutputStream var5 = (OutputStream)var3.getOutputObject();
            var1.marshalReplyParams(var5);
         } else {
            if (this.orb.subcontractDebugFlag) {
               this.dprint(".handleDynamicResult: " + this.opAndId(var2) + ": handling error");
            }

            var3 = this.sendingReply(var2, var4);
         }

         var9 = var3;
      } finally {
         if (this.orb.subcontractDebugFlag) {
            this.dprint(".handleDynamicResult<-: " + this.opAndId(var2));
         }

      }

      return var9;
   }

   protected CorbaMessageMediator sendingReply(CorbaMessageMediator var1) {
      CorbaMessageMediator var3;
      try {
         if (this.orb.subcontractDebugFlag) {
            this.dprint(".sendingReply->: " + this.opAndId(var1));
         }

         ServiceContexts var2 = new ServiceContexts(this.orb);
         var3 = var1.getProtocolHandler().createResponse(var1, var2);
      } finally {
         if (this.orb.subcontractDebugFlag) {
            this.dprint(".sendingReply<-: " + this.opAndId(var1));
         }

      }

      return var3;
   }

   protected CorbaMessageMediator sendingReply(CorbaMessageMediator var1, Any var2) {
      CorbaMessageMediator var14;
      try {
         if (this.orb.subcontractDebugFlag) {
            this.dprint(".sendingReply/Any->: " + this.opAndId(var1));
         }

         ServiceContexts var3 = new ServiceContexts(this.orb);
         String var5 = null;

         try {
            var5 = var2.type().id();
         } catch (BadKind var11) {
            throw this.wrapper.problemWithExceptionTypecode((Throwable)var11);
         }

         CorbaMessageMediator var4;
         if (ORBUtility.isSystemException(var5)) {
            if (this.orb.subcontractDebugFlag) {
               this.dprint(".sendingReply/Any: " + this.opAndId(var1) + ": handling system exception");
            }

            InputStream var6 = var2.create_input_stream();
            SystemException var7 = ORBUtility.readSystemException(var6);
            var4 = var1.getProtocolHandler().createSystemExceptionResponse(var1, var7, var3);
         } else {
            if (this.orb.subcontractDebugFlag) {
               this.dprint(".sendingReply/Any: " + this.opAndId(var1) + ": handling user exception");
            }

            var4 = var1.getProtocolHandler().createUserExceptionResponse(var1, var3);
            OutputStream var13 = (OutputStream)var4.getOutputObject();
            var2.write_value(var13);
         }

         var14 = var4;
      } finally {
         if (this.orb.subcontractDebugFlag) {
            this.dprint(".sendingReply/Any<-: " + this.opAndId(var1));
         }

      }

      return var14;
   }

   protected boolean processCodeSetContext(CorbaMessageMediator var1, ServiceContexts var2) {
      boolean var9;
      try {
         if (this.orb.subcontractDebugFlag) {
            this.dprint(".processCodeSetContext->: " + this.opAndId(var1));
         }

         ServiceContext var3 = var2.get(1);
         if (var3 != null) {
            if (var1.getConnection() == null) {
               var9 = true;
               return var9;
            }

            if (var1.getGIOPVersion().equals(GIOPVersion.V1_0)) {
               var9 = true;
               return var9;
            }

            CodeSetServiceContext var4 = (CodeSetServiceContext)var3;
            CodeSetComponentInfo.CodeSetContext var5 = var4.getCodeSetContext();
            if (((CorbaConnection)var1.getConnection()).getCodeSetContext() == null) {
               if (this.orb.subcontractDebugFlag) {
                  this.dprint(".processCodeSetContext: " + this.opAndId(var1) + ": Setting code sets to: " + var5);
               }

               ((CorbaConnection)var1.getConnection()).setCodeSetContext(var5);
               if (var5.getCharCodeSet() != OSFCodeSetRegistry.ISO_8859_1.getNumber()) {
                  ((MarshalInputStream)var1.getInputObject()).resetCodeSetConverters();
               }
            }
         }

         var9 = var3 != null;
      } finally {
         if (this.orb.subcontractDebugFlag) {
            this.dprint(".processCodeSetContext<-: " + this.opAndId(var1));
         }

      }

      return var9;
   }

   protected void dprint(String var1) {
      ORBUtility.dprint("CorbaServerRequestDispatcherImpl", var1);
   }

   protected String opAndId(CorbaMessageMediator var1) {
      return ORBUtility.operationNameAndRequestId(var1);
   }
}
