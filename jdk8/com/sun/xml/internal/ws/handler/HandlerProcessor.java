package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.WSBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;

abstract class HandlerProcessor<C extends MessageUpdatableContext> {
   boolean isClient;
   static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.handler");
   private List<? extends Handler> handlers;
   WSBinding binding;
   private int index = -1;
   private HandlerTube owner;

   protected HandlerProcessor(HandlerTube owner, WSBinding binding, List<? extends Handler> chain) {
      this.owner = owner;
      if (chain == null) {
         chain = new ArrayList();
      }

      this.handlers = (List)chain;
      this.binding = binding;
   }

   int getIndex() {
      return this.index;
   }

   void setIndex(int i) {
      this.index = i;
   }

   public boolean callHandlersRequest(HandlerProcessor.Direction direction, C context, boolean responseExpected) {
      this.setDirection(direction, context);

      boolean result;
      try {
         if (direction == HandlerProcessor.Direction.OUTBOUND) {
            result = this.callHandleMessage(context, 0, this.handlers.size() - 1);
         } else {
            result = this.callHandleMessage(context, this.handlers.size() - 1, 0);
         }
      } catch (ProtocolException var6) {
         logger.log(Level.FINER, (String)"exception in handler chain", (Throwable)var6);
         if (responseExpected) {
            this.insertFaultMessage(context, var6);
            this.reverseDirection(direction, context);
            this.setHandleFaultProperty();
            if (direction == HandlerProcessor.Direction.OUTBOUND) {
               this.callHandleFault(context, this.getIndex() - 1, 0);
            } else {
               this.callHandleFault(context, this.getIndex() + 1, this.handlers.size() - 1);
            }

            return false;
         }

         throw var6;
      } catch (RuntimeException var7) {
         logger.log(Level.FINER, (String)"exception in handler chain", (Throwable)var7);
         throw var7;
      }

      if (!result) {
         if (responseExpected) {
            this.reverseDirection(direction, context);
            if (direction == HandlerProcessor.Direction.OUTBOUND) {
               this.callHandleMessageReverse(context, this.getIndex() - 1, 0);
            } else {
               this.callHandleMessageReverse(context, this.getIndex() + 1, this.handlers.size() - 1);
            }
         } else {
            this.setHandleFalseProperty();
         }

         return false;
      } else {
         return result;
      }
   }

   public void callHandlersResponse(HandlerProcessor.Direction direction, C context, boolean isFault) {
      this.setDirection(direction, context);

      try {
         if (isFault) {
            if (direction == HandlerProcessor.Direction.OUTBOUND) {
               this.callHandleFault(context, 0, this.handlers.size() - 1);
            } else {
               this.callHandleFault(context, this.handlers.size() - 1, 0);
            }
         } else if (direction == HandlerProcessor.Direction.OUTBOUND) {
            this.callHandleMessageReverse(context, 0, this.handlers.size() - 1);
         } else {
            this.callHandleMessageReverse(context, this.handlers.size() - 1, 0);
         }

      } catch (RuntimeException var5) {
         logger.log(Level.FINER, (String)"exception in handler chain", (Throwable)var5);
         throw var5;
      }
   }

   private void reverseDirection(HandlerProcessor.Direction origDirection, C context) {
      if (origDirection == HandlerProcessor.Direction.OUTBOUND) {
         context.put((String)"javax.xml.ws.handler.message.outbound", false);
      } else {
         context.put((String)"javax.xml.ws.handler.message.outbound", true);
      }

   }

   private void setDirection(HandlerProcessor.Direction direction, C context) {
      if (direction == HandlerProcessor.Direction.OUTBOUND) {
         context.put((String)"javax.xml.ws.handler.message.outbound", true);
      } else {
         context.put((String)"javax.xml.ws.handler.message.outbound", false);
      }

   }

   private void setHandleFaultProperty() {
      this.owner.setHandleFault();
   }

   private void setHandleFalseProperty() {
      this.owner.setHandleFalse();
   }

   abstract void insertFaultMessage(C var1, ProtocolException var2);

   private boolean callHandleMessage(C context, int start, int end) {
      int i = start;

      try {
         if (start > end) {
            while(i >= end) {
               if (!((Handler)this.handlers.get(i)).handleMessage(context)) {
                  this.setIndex(i);
                  return false;
               }

               --i;
            }
         } else {
            while(i <= end) {
               if (!((Handler)this.handlers.get(i)).handleMessage(context)) {
                  this.setIndex(i);
                  return false;
               }

               ++i;
            }
         }

         return true;
      } catch (RuntimeException var6) {
         this.setIndex(start);
         throw var6;
      }
   }

   private boolean callHandleMessageReverse(C context, int start, int end) {
      if (!this.handlers.isEmpty() && start != -1 && start != this.handlers.size()) {
         int i = start;
         if (start > end) {
            while(i >= end) {
               if (!((Handler)this.handlers.get(i)).handleMessage(context)) {
                  this.setHandleFalseProperty();
                  return false;
               }

               --i;
            }
         } else {
            while(i <= end) {
               if (!((Handler)this.handlers.get(i)).handleMessage(context)) {
                  this.setHandleFalseProperty();
                  return false;
               }

               ++i;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean callHandleFault(C context, int start, int end) {
      if (!this.handlers.isEmpty() && start != -1 && start != this.handlers.size()) {
         int i = start;
         if (start > end) {
            try {
               while(i >= end) {
                  if (!((Handler)this.handlers.get(i)).handleFault(context)) {
                     return false;
                  }

                  --i;
               }
            } catch (RuntimeException var6) {
               logger.log(Level.FINER, (String)"exception in handler chain", (Throwable)var6);
               throw var6;
            }
         } else {
            try {
               while(i <= end) {
                  if (!((Handler)this.handlers.get(i)).handleFault(context)) {
                     return false;
                  }

                  ++i;
               }
            } catch (RuntimeException var7) {
               logger.log(Level.FINER, (String)"exception in handler chain", (Throwable)var7);
               throw var7;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   void closeHandlers(MessageContext context, int start, int end) {
      if (!this.handlers.isEmpty() && start != -1) {
         int i;
         if (start > end) {
            for(i = start; i >= end; --i) {
               try {
                  ((Handler)this.handlers.get(i)).close(context);
               } catch (RuntimeException var7) {
                  logger.log(Level.INFO, (String)"Exception ignored during close", (Throwable)var7);
               }
            }
         } else {
            for(i = start; i <= end; ++i) {
               try {
                  ((Handler)this.handlers.get(i)).close(context);
               } catch (RuntimeException var6) {
                  logger.log(Level.INFO, (String)"Exception ignored during close", (Throwable)var6);
               }
            }
         }

      }
   }

   public static enum Direction {
      OUTBOUND,
      INBOUND;
   }

   public static enum RequestOrResponse {
      REQUEST,
      RESPONSE;
   }
}
