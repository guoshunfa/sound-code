package com.sun.xml.internal.ws.wsdl;

import com.sun.xml.internal.ws.api.message.Message;

public final class DispatchException extends Exception {
   public final Message fault;

   public DispatchException(Message fault) {
      this.fault = fault;
   }
}
