package com.sun.jmx.snmp;

public class SnmpSecurityException extends Exception {
   private static final long serialVersionUID = 5574448147432833480L;
   public SnmpVarBind[] list = null;
   public int status = 242;
   public SnmpSecurityParameters params = null;
   public byte[] contextEngineId = null;
   public byte[] contextName = null;
   public byte flags = 0;

   public SnmpSecurityException(String var1) {
      super(var1);
   }
}
