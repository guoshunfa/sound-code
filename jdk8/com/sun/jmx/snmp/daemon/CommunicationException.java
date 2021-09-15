package com.sun.jmx.snmp.daemon;

import javax.management.JMRuntimeException;

public class CommunicationException extends JMRuntimeException {
   private static final long serialVersionUID = -2499186113233316177L;

   public CommunicationException(Throwable var1) {
      super(var1.getMessage());
      this.initCause(var1);
   }

   public CommunicationException(Throwable var1, String var2) {
      super(var2);
      this.initCause(var1);
   }

   public CommunicationException(String var1) {
      super(var1);
   }

   public Throwable getTargetException() {
      return this.getCause();
   }
}
