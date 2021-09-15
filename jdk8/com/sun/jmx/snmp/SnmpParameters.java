package com.sun.jmx.snmp;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

public class SnmpParameters extends SnmpParams implements Cloneable, Serializable {
   private static final long serialVersionUID = -1822462497931733790L;
   static final String defaultRdCommunity = "public";
   private int _protocolVersion = 0;
   private String _readCommunity;
   private String _writeCommunity;
   private String _informCommunity;

   public SnmpParameters() {
      this._readCommunity = "public";
      this._informCommunity = "public";
   }

   public SnmpParameters(String var1, String var2) {
      this._readCommunity = var1;
      this._writeCommunity = var2;
      this._informCommunity = "public";
   }

   public SnmpParameters(String var1, String var2, String var3) {
      this._readCommunity = var1;
      this._writeCommunity = var2;
      this._informCommunity = var3;
   }

   public String getRdCommunity() {
      return this._readCommunity;
   }

   public synchronized void setRdCommunity(String var1) {
      if (var1 == null) {
         this._readCommunity = "public";
      } else {
         this._readCommunity = var1;
      }

   }

   public String getWrCommunity() {
      return this._writeCommunity;
   }

   public void setWrCommunity(String var1) {
      this._writeCommunity = var1;
   }

   public String getInformCommunity() {
      return this._informCommunity;
   }

   public void setInformCommunity(String var1) {
      if (var1 == null) {
         this._informCommunity = "public";
      } else {
         this._informCommunity = var1;
      }

   }

   public boolean allowSnmpSets() {
      return this._writeCommunity != null;
   }

   public synchronized boolean equals(Object var1) {
      if (!(var1 instanceof SnmpParameters)) {
         return false;
      } else if (this == var1) {
         return true;
      } else {
         SnmpParameters var2 = (SnmpParameters)var1;
         return this._protocolVersion == var2._protocolVersion && this._readCommunity.equals(var2._readCommunity);
      }
   }

   public synchronized int hashCode() {
      return this._protocolVersion * 31 ^ Objects.hashCode(this._readCommunity);
   }

   public synchronized Object clone() {
      SnmpParameters var1 = null;

      try {
         var1 = (SnmpParameters)super.clone();
         var1._readCommunity = this._readCommunity;
         var1._writeCommunity = this._writeCommunity;
         var1._informCommunity = this._informCommunity;
         return var1;
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }
   }

   public byte[] encodeAuthentication(int var1) throws SnmpStatusException {
      try {
         if (var1 == 163) {
            return this._writeCommunity.getBytes("8859_1");
         } else {
            return var1 == 166 ? this._informCommunity.getBytes("8859_1") : this._readCommunity.getBytes("8859_1");
         }
      } catch (UnsupportedEncodingException var3) {
         throw new SnmpStatusException(var3.getMessage());
      }
   }
}
