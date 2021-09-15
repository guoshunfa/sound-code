package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.encoding.CDRInputObject;
import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.impl.encoding.CodeSetConversion;
import com.sun.corba.se.impl.encoding.EncapsInputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.transport.OutboundConnectionCache;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.CodeSetsComponent;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.servicecontext.CodeSetServiceContext;
import com.sun.corba.se.spi.servicecontext.MaxStreamFormatVersionServiceContext;
import com.sun.corba.se.spi.servicecontext.ORBVersionServiceContext;
import com.sun.corba.se.spi.servicecontext.SendingContextServiceContext;
import com.sun.corba.se.spi.servicecontext.ServiceContext;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.spi.servicecontext.UEInfoServiceContext;
import com.sun.corba.se.spi.servicecontext.UnknownServiceContext;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.spi.transport.CorbaContactInfoListIterator;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.UnknownException;
import org.omg.CORBA_2_3.portable.InputStream;
import sun.corba.EncapsInputStreamFactory;

public class CorbaClientRequestDispatcherImpl implements ClientRequestDispatcher {
   private ConcurrentMap<ContactInfo, Object> locks = new ConcurrentHashMap();

   public OutputObject beginRequest(Object var1, String var2, boolean var3, ContactInfo var4) {
      ORB var5 = null;

      try {
         CorbaContactInfo var6 = (CorbaContactInfo)var4;
         var5 = (ORB)var4.getBroker();
         if (var5.subcontractDebugFlag) {
            this.dprint(".beginRequest->: op/" + var2);
         }

         var5.getPIHandler().initiateClientPIRequest(false);
         CorbaConnection var7 = null;
         Object var8 = this.locks.get(var4);
         if (var8 == null) {
            Object var9 = new Object();
            var8 = this.locks.putIfAbsent(var4, var9);
            if (var8 == null) {
               var8 = var9;
            }
         }

         OutputObject var12;
         synchronized(var8) {
            if (var4.isConnectionBased()) {
               if (var4.shouldCacheConnection()) {
                  var7 = (CorbaConnection)var5.getTransportManager().getOutboundConnectionCache(var4).get(var4);
               }

               if (var7 != null) {
                  if (var5.subcontractDebugFlag) {
                     this.dprint(".beginRequest: op/" + var2 + ": Using cached connection: " + var7);
                  }
               } else {
                  try {
                     var7 = (CorbaConnection)var4.createConnection();
                     if (var5.subcontractDebugFlag) {
                        this.dprint(".beginRequest: op/" + var2 + ": Using created connection: " + var7);
                     }
                  } catch (RuntimeException var22) {
                     if (var5.subcontractDebugFlag) {
                        this.dprint(".beginRequest: op/" + var2 + ": failed to create connection: " + var22);
                     }

                     boolean var11 = this.getContactInfoListIterator(var5).reportException(var4, var22);
                     if (!var11) {
                        throw var22;
                     }

                     if (this.getContactInfoListIterator(var5).hasNext()) {
                        var4 = (ContactInfo)this.getContactInfoListIterator(var5).next();
                        this.unregisterWaiter(var5);
                        var12 = this.beginRequest(var1, var2, var3, var4);
                        return var12;
                     }

                     throw var22;
                  }

                  if (var7.shouldRegisterReadEvent()) {
                     var5.getTransportManager().getSelector(0).registerForEvent(var7.getEventHandler());
                     var7.setState("ESTABLISHED");
                  }

                  if (var4.shouldCacheConnection()) {
                     OutboundConnectionCache var10 = var5.getTransportManager().getOutboundConnectionCache(var4);
                     var10.stampTime(var7);
                     var10.put(var4, var7);
                  }
               }
            }
         }

         CorbaMessageMediator var26 = (CorbaMessageMediator)var4.createMessageMediator(var5, var4, var7, var2, var3);
         if (var5.subcontractDebugFlag) {
            this.dprint(".beginRequest: " + this.opAndId(var26) + ": created message mediator: " + var26);
         }

         var5.getInvocationInfo().setMessageMediator(var26);
         if (var7 != null && var7.getCodeSetContext() == null) {
            this.performCodeSetNegotiation(var26);
         }

         this.addServiceContexts(var26);
         OutputObject var27 = var4.createOutputObject(var26);
         if (var5.subcontractDebugFlag) {
            this.dprint(".beginRequest: " + this.opAndId(var26) + ": created output object: " + var27);
         }

         this.registerWaiter(var26);
         synchronized(var8) {
            if (var4.isConnectionBased() && var4.shouldCacheConnection()) {
               OutboundConnectionCache var29 = var5.getTransportManager().getOutboundConnectionCache(var4);
               var29.reclaim();
            }
         }

         var5.getPIHandler().setClientPIInfo(var26);

         try {
            var5.getPIHandler().invokeClientPIStartingPoint();
         } catch (RemarshalException var21) {
            if (var5.subcontractDebugFlag) {
               this.dprint(".beginRequest: " + this.opAndId(var26) + ": Remarshal");
            }

            if (!this.getContactInfoListIterator(var5).hasNext()) {
               if (var5.subcontractDebugFlag) {
                  this.dprint("RemarshalException: hasNext false");
               }

               ORBUtilSystemException var30 = ORBUtilSystemException.get(var5, "rpc.protocol");
               throw var30.remarshalWithNowhereToGo();
            }

            var4 = (ContactInfo)this.getContactInfoListIterator(var5).next();
            if (var5.subcontractDebugFlag) {
               this.dprint("RemarshalException: hasNext true\ncontact info " + var4);
            }

            var5.getPIHandler().makeCompletedClientRequest(3, (Exception)null);
            this.unregisterWaiter(var5);
            var5.getPIHandler().cleanupClientPIRequest();
            var12 = this.beginRequest(var1, var2, var3, var4);
            return var12;
         }

         var26.initializeMessage();
         if (var5.subcontractDebugFlag) {
            this.dprint(".beginRequest: " + this.opAndId(var26) + ": initialized message");
         }

         OutputObject var28 = var27;
         return var28;
      } finally {
         if (var5.subcontractDebugFlag) {
            this.dprint(".beginRequest<-: op/" + var2);
         }

      }
   }

   public InputObject marshalingComplete(Object var1, OutputObject var2) throws ApplicationException, RemarshalException {
      ORB var3 = null;
      CorbaMessageMediator var4 = null;

      InputObject var6;
      try {
         var4 = (CorbaMessageMediator)var2.getMessageMediator();
         var3 = (ORB)var4.getBroker();
         if (var3.subcontractDebugFlag) {
            this.dprint(".marshalingComplete->: " + this.opAndId(var4));
         }

         InputObject var5 = this.marshalingComplete1(var3, var4);
         var6 = this.processResponse(var3, var4, var5);
      } finally {
         if (var3.subcontractDebugFlag) {
            this.dprint(".marshalingComplete<-: " + this.opAndId(var4));
         }

      }

      return var6;
   }

   public InputObject marshalingComplete1(ORB var1, CorbaMessageMediator var2) throws ApplicationException, RemarshalException {
      try {
         var2.finishSendingRequest();
         if (var1.subcontractDebugFlag) {
            this.dprint(".marshalingComplete: " + this.opAndId(var2) + ": finished sending request");
         }

         return var2.waitForResponse();
      } catch (RuntimeException var6) {
         if (var1.subcontractDebugFlag) {
            this.dprint(".marshalingComplete: " + this.opAndId(var2) + ": exception: " + var6.toString());
         }

         boolean var4 = this.getContactInfoListIterator(var1).reportException(var2.getContactInfo(), var6);
         Exception var5 = var1.getPIHandler().invokeClientPIEndingPoint(2, var6);
         if (var4) {
            if (var5 == var6) {
               this.continueOrThrowSystemOrRemarshal(var2, new RemarshalException());
            } else {
               this.continueOrThrowSystemOrRemarshal(var2, var5);
            }

            return null;
         } else if (var5 instanceof RuntimeException) {
            throw (RuntimeException)var5;
         } else if (var5 instanceof RemarshalException) {
            throw (RemarshalException)var5;
         } else {
            throw var6;
         }
      }
   }

   protected InputObject processResponse(ORB var1, CorbaMessageMediator var2, InputObject var3) throws ApplicationException, RemarshalException {
      ORBUtilSystemException var4 = ORBUtilSystemException.get(var1, "rpc.protocol");
      if (var1.subcontractDebugFlag) {
         this.dprint(".processResponse: " + this.opAndId(var2) + ": response received");
      }

      if (var2.getConnection() != null) {
         ((CorbaConnection)var2.getConnection()).setPostInitialContexts();
      }

      Exception var5 = null;
      if (var2.isOneWay()) {
         this.getContactInfoListIterator(var1).reportSuccess(var2.getContactInfo());
         var5 = var1.getPIHandler().invokeClientPIEndingPoint(0, var5);
         this.continueOrThrowSystemOrRemarshal(var2, var5);
         return null;
      } else {
         this.consumeServiceContexts(var1, var2);
         ((CDRInputObject)var3).performORBVersionSpecificInit();
         if (var2.isSystemExceptionReply()) {
            SystemException var13 = var2.getSystemExceptionReply();
            if (var1.subcontractDebugFlag) {
               this.dprint(".processResponse: " + this.opAndId(var2) + ": received system exception: " + var13);
            }

            boolean var15 = this.getContactInfoListIterator(var1).reportException(var2.getContactInfo(), var13);
            if (var15) {
               var5 = var1.getPIHandler().invokeClientPIEndingPoint(2, var13);
               if (var13 == var5) {
                  var5 = null;
                  this.continueOrThrowSystemOrRemarshal(var2, new RemarshalException());
                  throw var4.statementNotReachable1();
               } else {
                  this.continueOrThrowSystemOrRemarshal(var2, var5);
                  throw var4.statementNotReachable2();
               }
            } else {
               ServiceContexts var16 = var2.getReplyServiceContexts();
               if (var16 != null) {
                  UEInfoServiceContext var9 = (UEInfoServiceContext)var16.get(9);
                  if (var9 != null) {
                     Throwable var10 = var9.getUE();
                     UnknownException var11 = new UnknownException(var10);
                     var5 = var1.getPIHandler().invokeClientPIEndingPoint(2, var11);
                     this.continueOrThrowSystemOrRemarshal(var2, var5);
                     throw var4.statementNotReachable3();
                  }
               }

               var5 = var1.getPIHandler().invokeClientPIEndingPoint(2, var13);
               this.continueOrThrowSystemOrRemarshal(var2, var5);
               throw var4.statementNotReachable4();
            }
         } else if (var2.isUserExceptionReply()) {
            if (var1.subcontractDebugFlag) {
               this.dprint(".processResponse: " + this.opAndId(var2) + ": received user exception");
            }

            this.getContactInfoListIterator(var1).reportSuccess(var2.getContactInfo());
            String var12 = this.peekUserExceptionId(var3);
            Exception var7 = null;
            Object var14;
            if (var2.isDIIRequest()) {
               var14 = var2.unmarshalDIIUserException(var12, (InputStream)var3);
               var7 = var1.getPIHandler().invokeClientPIEndingPoint(1, (Exception)var14);
               var2.setDIIException(var7);
            } else {
               ApplicationException var8 = new ApplicationException(var12, (org.omg.CORBA.portable.InputStream)var3);
               var14 = var8;
               var7 = var1.getPIHandler().invokeClientPIEndingPoint(1, var8);
            }

            if (var7 != var14) {
               this.continueOrThrowSystemOrRemarshal(var2, var7);
            }

            if (var7 instanceof ApplicationException) {
               throw (ApplicationException)var7;
            } else {
               return var3;
            }
         } else {
            Exception var6;
            if (var2.isLocationForwardReply()) {
               if (var1.subcontractDebugFlag) {
                  this.dprint(".processResponse: " + this.opAndId(var2) + ": received location forward");
               }

               this.getContactInfoListIterator(var1).reportRedirect((CorbaContactInfo)var2.getContactInfo(), var2.getForwardedIOR());
               var6 = var1.getPIHandler().invokeClientPIEndingPoint(3, (Exception)null);
               if (!(var6 instanceof RemarshalException)) {
                  var5 = var6;
               }

               if (var5 != null) {
                  this.continueOrThrowSystemOrRemarshal(var2, var5);
               }

               this.continueOrThrowSystemOrRemarshal(var2, new RemarshalException());
               throw var4.statementNotReachable5();
            } else if (var2.isDifferentAddrDispositionRequestedReply()) {
               if (var1.subcontractDebugFlag) {
                  this.dprint(".processResponse: " + this.opAndId(var2) + ": received different addressing dispostion request");
               }

               this.getContactInfoListIterator(var1).reportAddrDispositionRetry((CorbaContactInfo)var2.getContactInfo(), var2.getAddrDispositionReply());
               var6 = var1.getPIHandler().invokeClientPIEndingPoint(5, (Exception)null);
               if (!(var6 instanceof RemarshalException)) {
                  var5 = var6;
               }

               if (var5 != null) {
                  this.continueOrThrowSystemOrRemarshal(var2, var5);
               }

               this.continueOrThrowSystemOrRemarshal(var2, new RemarshalException());
               throw var4.statementNotReachable6();
            } else {
               if (var1.subcontractDebugFlag) {
                  this.dprint(".processResponse: " + this.opAndId(var2) + ": received normal response");
               }

               this.getContactInfoListIterator(var1).reportSuccess(var2.getContactInfo());
               var2.handleDIIReply((InputStream)var3);
               var5 = var1.getPIHandler().invokeClientPIEndingPoint(0, (Exception)null);
               this.continueOrThrowSystemOrRemarshal(var2, var5);
               return var3;
            }
         }
      }
   }

   protected void continueOrThrowSystemOrRemarshal(CorbaMessageMediator var1, Exception var2) throws SystemException, RemarshalException {
      ORB var3 = (ORB)var1.getBroker();
      if (var2 != null) {
         if (var2 instanceof RemarshalException) {
            var3.getInvocationInfo().setIsRetryInvocation(true);
            this.unregisterWaiter(var3);
            if (var3.subcontractDebugFlag) {
               this.dprint(".continueOrThrowSystemOrRemarshal: " + this.opAndId(var1) + ": throwing Remarshal");
            }

            throw (RemarshalException)var2;
         } else {
            if (var3.subcontractDebugFlag) {
               this.dprint(".continueOrThrowSystemOrRemarshal: " + this.opAndId(var1) + ": throwing sex:" + var2);
            }

            throw (SystemException)var2;
         }
      }
   }

   protected CorbaContactInfoListIterator getContactInfoListIterator(ORB var1) {
      return (CorbaContactInfoListIterator)((CorbaInvocationInfo)var1.getInvocationInfo()).getContactInfoListIterator();
   }

   protected void registerWaiter(CorbaMessageMediator var1) {
      if (var1.getConnection() != null) {
         var1.getConnection().registerWaiter(var1);
      }

   }

   protected void unregisterWaiter(ORB var1) {
      MessageMediator var2 = var1.getInvocationInfo().getMessageMediator();
      if (var2 != null && var2.getConnection() != null) {
         var2.getConnection().unregisterWaiter(var2);
      }

   }

   protected void addServiceContexts(CorbaMessageMediator var1) {
      ORB var2 = (ORB)var1.getBroker();
      CorbaConnection var3 = (CorbaConnection)var1.getConnection();
      GIOPVersion var4 = var1.getGIOPVersion();
      ServiceContexts var5 = var1.getRequestServiceContexts();
      this.addCodeSetServiceContext(var3, var5, var4);
      var5.put(MaxStreamFormatVersionServiceContext.singleton);
      ORBVersionServiceContext var6 = new ORBVersionServiceContext(ORBVersionFactory.getORBVersion());
      var5.put(var6);
      if (var3 != null && !var3.isPostInitialContexts()) {
         SendingContextServiceContext var7 = new SendingContextServiceContext(var2.getFVDCodeBaseIOR());
         var5.put(var7);
      }

   }

   protected void consumeServiceContexts(ORB var1, CorbaMessageMediator var2) {
      ServiceContexts var3 = var2.getReplyServiceContexts();
      ORBUtilSystemException var5 = ORBUtilSystemException.get(var1, "rpc.protocol");
      if (var3 != null) {
         ServiceContext var4 = var3.get(6);
         if (var4 != null) {
            SendingContextServiceContext var6 = (SendingContextServiceContext)var4;
            IOR var7 = var6.getIOR();

            try {
               if (var2.getConnection() != null) {
                  ((CorbaConnection)var2.getConnection()).setCodeBaseIOR(var7);
               }
            } catch (ThreadDeath var9) {
               throw var9;
            } catch (Throwable var10) {
               throw var5.badStringifiedIor(var10);
            }
         }

         var4 = var3.get(1313165056);
         if (var4 != null) {
            ORBVersionServiceContext var11 = (ORBVersionServiceContext)var4;
            ORBVersion var12 = var11.getVersion();
            var1.setORBVersion(var12);
         }

         this.getExceptionDetailMessage(var2, var5);
      }
   }

   protected void getExceptionDetailMessage(CorbaMessageMediator var1, ORBUtilSystemException var2) {
      ServiceContext var3 = var1.getReplyServiceContexts().get(14);
      if (var3 != null) {
         if (!(var3 instanceof UnknownServiceContext)) {
            throw var2.badExceptionDetailMessageServiceContextType();
         } else {
            byte[] var4 = ((UnknownServiceContext)var3).getData();
            EncapsInputStream var5 = EncapsInputStreamFactory.newEncapsInputStream((ORB)var1.getBroker(), var4, var4.length);
            var5.consumeEndian();
            String var6 = "----------BEGIN server-side stack trace----------\n" + var5.read_wstring() + "\n----------END server-side stack trace----------";
            var1.setReplyExceptionDetailMessage(var6);
         }
      }
   }

   public void endRequest(Broker var1, Object var2, InputObject var3) {
      ORB var4 = (ORB)var1;

      try {
         if (var4.subcontractDebugFlag) {
            this.dprint(".endRequest->");
         }

         MessageMediator var5 = var4.getInvocationInfo().getMessageMediator();
         if (var5 != null) {
            if (var5.getConnection() != null) {
               ((CorbaMessageMediator)var5).sendCancelRequestIfFinalFragmentNotSent();
            }

            InputObject var6 = var5.getInputObject();
            if (var6 != null) {
               var6.close();
            }

            OutputObject var7 = var5.getOutputObject();
            if (var7 != null) {
               var7.close();
            }
         }

         this.unregisterWaiter(var4);
         var4.getPIHandler().cleanupClientPIRequest();
      } catch (IOException var11) {
         if (var4.subcontractDebugFlag) {
            this.dprint(".endRequest: ignoring IOException - " + var11.toString());
         }
      } finally {
         if (var4.subcontractDebugFlag) {
            this.dprint(".endRequest<-");
         }

      }

   }

   protected void performCodeSetNegotiation(CorbaMessageMediator var1) {
      CorbaConnection var2 = (CorbaConnection)var1.getConnection();
      IOR var3 = ((CorbaContactInfo)var1.getContactInfo()).getEffectiveTargetIOR();
      GIOPVersion var4 = var1.getGIOPVersion();
      if (var2 != null && var2.getCodeSetContext() == null && !var4.equals(GIOPVersion.V1_0)) {
         synchronized(var2) {
            if (var2.getCodeSetContext() != null) {
               return;
            }

            IIOPProfileTemplate var6 = (IIOPProfileTemplate)var3.getProfile().getTaggedProfileTemplate();
            Iterator var7 = var6.iteratorById(1);
            if (!var7.hasNext()) {
               return;
            }

            CodeSetComponentInfo var8 = ((CodeSetsComponent)var7.next()).getCodeSetComponentInfo();
            CodeSetComponentInfo.CodeSetContext var9 = CodeSetConversion.impl().negotiate(var2.getBroker().getORBData().getCodeSetComponentInfo(), var8);
            var2.setCodeSetContext(var9);
         }
      }

   }

   protected void addCodeSetServiceContext(CorbaConnection var1, ServiceContexts var2, GIOPVersion var3) {
      if (!var3.equals(GIOPVersion.V1_0) && var1 != null) {
         CodeSetComponentInfo.CodeSetContext var4 = null;
         if (var1.getBroker().getORBData().alwaysSendCodeSetServiceContext() || !var1.isPostInitialContexts()) {
            var4 = var1.getCodeSetContext();
         }

         if (var4 != null) {
            CodeSetServiceContext var5 = new CodeSetServiceContext(var4);
            var2.put(var5);
         }
      }
   }

   protected String peekUserExceptionId(InputObject var1) {
      CDRInputObject var2 = (CDRInputObject)var1;
      var2.mark(Integer.MAX_VALUE);
      String var3 = var2.read_string();
      var2.reset();
      return var3;
   }

   protected void dprint(String var1) {
      ORBUtility.dprint("CorbaClientRequestDispatcherImpl", var1);
   }

   protected String opAndId(CorbaMessageMediator var1) {
      return ORBUtility.operationNameAndRequestId(var1);
   }
}
