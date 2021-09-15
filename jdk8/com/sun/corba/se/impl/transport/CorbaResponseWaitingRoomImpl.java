package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.encoding.BufferManagerReadStream;
import com.sun.corba.se.impl.encoding.CDRInputObject;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyOrReplyMessage;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.transport.CorbaResponseWaitingRoom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;

public class CorbaResponseWaitingRoomImpl implements CorbaResponseWaitingRoom {
   private ORB orb;
   private ORBUtilSystemException wrapper;
   private CorbaConnection connection;
   private final Map<Integer, CorbaResponseWaitingRoomImpl.OutCallDesc> out_calls;

   public CorbaResponseWaitingRoomImpl(ORB var1, CorbaConnection var2) {
      this.orb = var1;
      this.wrapper = ORBUtilSystemException.get(var1, "rpc.transport");
      this.connection = var2;
      this.out_calls = Collections.synchronizedMap(new HashMap());
   }

   public void registerWaiter(MessageMediator var1) {
      CorbaMessageMediator var2 = (CorbaMessageMediator)var1;
      if (this.orb.transportDebugFlag) {
         this.dprint(".registerWaiter: " + this.opAndId(var2));
      }

      Integer var3 = var2.getRequestIdInteger();
      CorbaResponseWaitingRoomImpl.OutCallDesc var4 = new CorbaResponseWaitingRoomImpl.OutCallDesc();
      var4.thread = Thread.currentThread();
      var4.messageMediator = var2;
      this.out_calls.put(var3, var4);
   }

   public void unregisterWaiter(MessageMediator var1) {
      CorbaMessageMediator var2 = (CorbaMessageMediator)var1;
      if (this.orb.transportDebugFlag) {
         this.dprint(".unregisterWaiter: " + this.opAndId(var2));
      }

      Integer var3 = var2.getRequestIdInteger();
      this.out_calls.remove(var3);
   }

   public InputObject waitForResponse(MessageMediator var1) {
      CorbaMessageMediator var2 = (CorbaMessageMediator)var1;

      CorbaResponseWaitingRoomImpl.OutCallDesc var5;
      try {
         InputObject var3 = null;
         if (this.orb.transportDebugFlag) {
            this.dprint(".waitForResponse->: " + this.opAndId(var2));
         }

         Integer var4 = var2.getRequestIdInteger();
         if (!var2.isOneWay()) {
            var5 = (CorbaResponseWaitingRoomImpl.OutCallDesc)this.out_calls.get(var4);
            if (var5 == null) {
               throw this.wrapper.nullOutCall(CompletionStatus.COMPLETED_MAYBE);
            }

            synchronized(var5.done) {
               while(var5.inputObject == null && var5.exception == null) {
                  try {
                     if (this.orb.transportDebugFlag) {
                        this.dprint(".waitForResponse: waiting: " + this.opAndId(var2));
                     }

                     var5.done.wait();
                  } catch (InterruptedException var13) {
                  }
               }

               if (var5.exception != null) {
                  if (this.orb.transportDebugFlag) {
                     this.dprint(".waitForResponse: exception: " + this.opAndId(var2));
                  }

                  throw var5.exception;
               }

               var3 = var5.inputObject;
            }

            if (var3 != null) {
               ((CDRInputObject)var3).unmarshalHeader();
            }

            InputObject var6 = var3;
            return var6;
         }

         if (this.orb.transportDebugFlag) {
            this.dprint(".waitForResponse: one way - not waiting: " + this.opAndId(var2));
         }

         var5 = null;
      } finally {
         if (this.orb.transportDebugFlag) {
            this.dprint(".waitForResponse<-: " + this.opAndId(var2));
         }

      }

      return var5;
   }

   public void responseReceived(InputObject var1) {
      CDRInputObject var2 = (CDRInputObject)var1;
      LocateReplyOrReplyMessage var3 = (LocateReplyOrReplyMessage)var2.getMessageHeader();
      Integer var4 = new Integer(var3.getRequestId());
      CorbaResponseWaitingRoomImpl.OutCallDesc var5 = (CorbaResponseWaitingRoomImpl.OutCallDesc)this.out_calls.get(var4);
      if (this.orb.transportDebugFlag) {
         this.dprint(".responseReceived: id/" + var4 + ": " + var3);
      }

      if (var5 == null) {
         if (this.orb.transportDebugFlag) {
            this.dprint(".responseReceived: id/" + var4 + ": no waiter: " + var3);
         }

      } else {
         synchronized(var5.done) {
            CorbaMessageMediator var7 = (CorbaMessageMediator)var5.messageMediator;
            if (this.orb.transportDebugFlag) {
               this.dprint(".responseReceived: " + this.opAndId(var7) + ": notifying waiters");
            }

            var7.setReplyHeader(var3);
            var7.setInputObject(var1);
            var2.setMessageMediator(var7);
            var5.inputObject = var1;
            var5.done.notify();
         }
      }
   }

   public int numberRegistered() {
      return this.out_calls.size();
   }

   public void signalExceptionToAllWaiters(SystemException var1) {
      if (this.orb.transportDebugFlag) {
         this.dprint(".signalExceptionToAllWaiters: " + var1);
      }

      synchronized(this.out_calls) {
         if (this.orb.transportDebugFlag) {
            this.dprint(".signalExceptionToAllWaiters: out_calls size :" + this.out_calls.size());
         }

         Iterator var3 = this.out_calls.values().iterator();

         while(var3.hasNext()) {
            CorbaResponseWaitingRoomImpl.OutCallDesc var4 = (CorbaResponseWaitingRoomImpl.OutCallDesc)var3.next();
            if (this.orb.transportDebugFlag) {
               this.dprint(".signalExceptionToAllWaiters: signaling " + var4);
            }

            synchronized(var4.done) {
               try {
                  CorbaMessageMediator var6 = (CorbaMessageMediator)var4.messageMediator;
                  CDRInputObject var7 = (CDRInputObject)var6.getInputObject();
                  if (var7 != null) {
                     BufferManagerReadStream var8 = (BufferManagerReadStream)var7.getBufferManager();
                     int var9 = var6.getRequestId();
                     var8.cancelProcessing(var9);
                  }
               } catch (Exception var17) {
               } finally {
                  var4.inputObject = null;
                  var4.exception = var1;
                  var4.done.notifyAll();
               }
            }
         }

      }
   }

   public MessageMediator getMessageMediator(int var1) {
      Integer var2 = new Integer(var1);
      CorbaResponseWaitingRoomImpl.OutCallDesc var3 = (CorbaResponseWaitingRoomImpl.OutCallDesc)this.out_calls.get(var2);
      return var3 == null ? null : var3.messageMediator;
   }

   protected void dprint(String var1) {
      ORBUtility.dprint("CorbaResponseWaitingRoomImpl", var1);
   }

   protected String opAndId(CorbaMessageMediator var1) {
      return ORBUtility.operationNameAndRequestId(var1);
   }

   static final class OutCallDesc {
      Object done = new Object();
      Thread thread;
      MessageMediator messageMediator;
      SystemException exception;
      InputObject inputObject;
   }
}
