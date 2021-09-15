package com.sun.jmx.snmp;

import java.io.Serializable;

public class SnmpEngineParameters implements Serializable {
   private static final long serialVersionUID = 3720556613478400808L;
   private UserAcl uacl = null;
   private String securityFile = null;
   private boolean encrypt = false;
   private SnmpEngineId engineId = null;

   public void setSecurityFile(String var1) {
      this.securityFile = var1;
   }

   public String getSecurityFile() {
      return this.securityFile;
   }

   public void setUserAcl(UserAcl var1) {
      this.uacl = var1;
   }

   public UserAcl getUserAcl() {
      return this.uacl;
   }

   public void activateEncryption() {
      this.encrypt = true;
   }

   public void deactivateEncryption() {
      this.encrypt = false;
   }

   public boolean isEncryptionEnabled() {
      return this.encrypt;
   }

   public void setEngineId(SnmpEngineId var1) {
      this.engineId = var1;
   }

   public SnmpEngineId getEngineId() {
      return this.engineId;
   }
}
