package com.sun.xml.internal.ws.client;

import com.sun.xml.internal.ws.api.handler.MessageHandler;
import com.sun.xml.internal.ws.handler.HandlerException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.soap.SOAPHandler;

public class HandlerConfiguration {
   private final Set<String> roles;
   private final List<Handler> handlerChain;
   private final List<LogicalHandler> logicalHandlers;
   private final List<SOAPHandler> soapHandlers;
   private final List<MessageHandler> messageHandlers;
   private final Set<QName> handlerKnownHeaders;

   public HandlerConfiguration(Set<String> roles, List<Handler> handlerChain) {
      this.roles = roles;
      this.handlerChain = handlerChain;
      this.logicalHandlers = new ArrayList();
      this.soapHandlers = new ArrayList();
      this.messageHandlers = new ArrayList();
      Set<QName> modHandlerKnownHeaders = new HashSet();
      Iterator var4 = handlerChain.iterator();

      while(var4.hasNext()) {
         Handler handler = (Handler)var4.next();
         if (handler instanceof LogicalHandler) {
            this.logicalHandlers.add((LogicalHandler)handler);
         } else {
            Set headers;
            if (handler instanceof SOAPHandler) {
               this.soapHandlers.add((SOAPHandler)handler);
               headers = ((SOAPHandler)handler).getHeaders();
               if (headers != null) {
                  modHandlerKnownHeaders.addAll(headers);
               }
            } else {
               if (!(handler instanceof MessageHandler)) {
                  throw new HandlerException("handler.not.valid.type", new Object[]{handler.getClass()});
               }

               this.messageHandlers.add((MessageHandler)handler);
               headers = ((MessageHandler)handler).getHeaders();
               if (headers != null) {
                  modHandlerKnownHeaders.addAll(headers);
               }
            }
         }
      }

      this.handlerKnownHeaders = Collections.unmodifiableSet(modHandlerKnownHeaders);
   }

   public HandlerConfiguration(Set<String> roles, HandlerConfiguration oldConfig) {
      this.roles = roles;
      this.handlerChain = oldConfig.handlerChain;
      this.logicalHandlers = oldConfig.logicalHandlers;
      this.soapHandlers = oldConfig.soapHandlers;
      this.messageHandlers = oldConfig.messageHandlers;
      this.handlerKnownHeaders = oldConfig.handlerKnownHeaders;
   }

   public Set<String> getRoles() {
      return this.roles;
   }

   public List<Handler> getHandlerChain() {
      return (List)(this.handlerChain == null ? Collections.emptyList() : new ArrayList(this.handlerChain));
   }

   public List<LogicalHandler> getLogicalHandlers() {
      return this.logicalHandlers;
   }

   public List<SOAPHandler> getSoapHandlers() {
      return this.soapHandlers;
   }

   public List<MessageHandler> getMessageHandlers() {
      return this.messageHandlers;
   }

   public Set<QName> getHandlerKnownHeaders() {
      return this.handlerKnownHeaders;
   }
}
