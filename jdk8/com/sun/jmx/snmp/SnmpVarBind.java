package com.sun.jmx.snmp;

import java.io.Serializable;

public class SnmpVarBind implements SnmpDataTypeEnums, Cloneable, Serializable {
   private static final long serialVersionUID = 491778383240759376L;
   private static final String[] statusLegend = new String[]{"Status Mapper", "Value not initialized", "Valid Value", "No such object", "No such Instance", "End of Mib View"};
   public static final int stValueUnspecified = 1;
   public static final int stValueOk = 2;
   public static final int stValueNoSuchObject = 3;
   public static final int stValueNoSuchInstance = 4;
   public static final int stValueEndOfMibView = 5;
   public static final SnmpNull noSuchObject = new SnmpNull(128);
   public static final SnmpNull noSuchInstance = new SnmpNull(129);
   public static final SnmpNull endOfMibView = new SnmpNull(130);
   public SnmpOid oid = null;
   public SnmpValue value = null;
   public int status = 1;

   public SnmpVarBind() {
   }

   public SnmpVarBind(SnmpOid var1) {
      this.oid = var1;
   }

   public SnmpVarBind(SnmpOid var1, SnmpValue var2) {
      this.oid = var1;
      this.setSnmpValue(var2);
   }

   public SnmpVarBind(String var1) throws SnmpStatusException {
      if (var1.startsWith(".")) {
         this.oid = new SnmpOid(var1);
      } else {
         try {
            int var3 = var1.indexOf(46);
            this.handleLong(var1, var3);
            this.oid = new SnmpOid(var1);
         } catch (NumberFormatException var5) {
            int var4 = var1.indexOf(46);
            SnmpOidRecord var2;
            if (var4 <= 0) {
               var2 = this.resolveVarName(var1);
               this.oid = new SnmpOid(var2.getName());
            } else {
               var2 = this.resolveVarName(var1.substring(0, var4));
               this.oid = new SnmpOid(var2.getName() + var1.substring(var4));
            }
         }
      }

   }

   public final SnmpOid getOid() {
      return this.oid;
   }

   public final void setOid(SnmpOid var1) {
      this.oid = var1;
      this.clearValue();
   }

   public final synchronized SnmpValue getSnmpValue() {
      return this.value;
   }

   public final void setSnmpValue(SnmpValue var1) {
      this.value = var1;
      this.setValueValid();
   }

   public final SnmpCounter64 getSnmpCounter64Value() throws ClassCastException {
      return (SnmpCounter64)this.value;
   }

   public final void setSnmpCounter64Value(long var1) throws IllegalArgumentException {
      this.clearValue();
      this.value = new SnmpCounter64(var1);
      this.setValueValid();
   }

   public final SnmpInt getSnmpIntValue() throws ClassCastException {
      return (SnmpInt)this.value;
   }

   public final void setSnmpIntValue(long var1) throws IllegalArgumentException {
      this.clearValue();
      this.value = new SnmpInt(var1);
      this.setValueValid();
   }

   public final SnmpCounter getSnmpCounterValue() throws ClassCastException {
      return (SnmpCounter)this.value;
   }

   public final void setSnmpCounterValue(long var1) throws IllegalArgumentException {
      this.clearValue();
      this.value = new SnmpCounter(var1);
      this.setValueValid();
   }

   public final SnmpGauge getSnmpGaugeValue() throws ClassCastException {
      return (SnmpGauge)this.value;
   }

   public final void setSnmpGaugeValue(long var1) throws IllegalArgumentException {
      this.clearValue();
      this.value = new SnmpGauge(var1);
      this.setValueValid();
   }

   public final SnmpTimeticks getSnmpTimeticksValue() throws ClassCastException {
      return (SnmpTimeticks)this.value;
   }

   public final void setSnmpTimeticksValue(long var1) throws IllegalArgumentException {
      this.clearValue();
      this.value = new SnmpTimeticks(var1);
      this.setValueValid();
   }

   public final SnmpOid getSnmpOidValue() throws ClassCastException {
      return (SnmpOid)this.value;
   }

   public final void setSnmpOidValue(String var1) throws IllegalArgumentException {
      this.clearValue();
      this.value = new SnmpOid(var1);
      this.setValueValid();
   }

   public final SnmpIpAddress getSnmpIpAddressValue() throws ClassCastException {
      return (SnmpIpAddress)this.value;
   }

   public final void setSnmpIpAddressValue(String var1) throws IllegalArgumentException {
      this.clearValue();
      this.value = new SnmpIpAddress(var1);
      this.setValueValid();
   }

   public final SnmpString getSnmpStringValue() throws ClassCastException {
      return (SnmpString)this.value;
   }

   public final void setSnmpStringValue(String var1) {
      this.clearValue();
      this.value = new SnmpString(var1);
      this.setValueValid();
   }

   public final SnmpOpaque getSnmpOpaqueValue() throws ClassCastException {
      return (SnmpOpaque)this.value;
   }

   public final void setSnmpOpaqueValue(byte[] var1) {
      this.clearValue();
      this.value = new SnmpOpaque(var1);
      this.setValueValid();
   }

   public final SnmpStringFixed getSnmpStringFixedValue() throws ClassCastException {
      return (SnmpStringFixed)this.value;
   }

   public final void setSnmpStringFixedValue(String var1) {
      this.clearValue();
      this.value = new SnmpStringFixed(var1);
      this.setValueValid();
   }

   public SnmpOidRecord resolveVarName(String var1) throws SnmpStatusException {
      SnmpOidTable var2 = SnmpOid.getSnmpOidTable();
      if (var2 == null) {
         throw new SnmpStatusException(2);
      } else {
         int var3 = var1.indexOf(46);
         return var3 < 0 ? var2.resolveVarName(var1) : var2.resolveVarOid(var1);
      }
   }

   public final int getValueStatus() {
      return this.status;
   }

   public final String getValueStatusLegend() {
      return statusLegend[this.status];
   }

   public final boolean isValidValue() {
      return this.status == 2;
   }

   public final boolean isUnspecifiedValue() {
      return this.status == 1;
   }

   public final void clearValue() {
      this.value = null;
      this.status = 1;
   }

   public final boolean isOidEqual(SnmpVarBind var1) {
      return this.oid.equals(var1.oid);
   }

   public final void addInstance(long var1) {
      this.oid.append(var1);
   }

   public final void addInstance(long[] var1) throws SnmpStatusException {
      this.oid.addToOid(var1);
   }

   public final void addInstance(String var1) throws SnmpStatusException {
      if (var1 != null) {
         this.oid.addToOid(var1);
      }

   }

   public void insertInOid(int var1) {
      this.oid.insert(var1);
   }

   public void appendInOid(SnmpOid var1) {
      this.oid.append(var1);
   }

   public final synchronized boolean hasVarBindException() {
      switch(this.status) {
      case 1:
      case 3:
      case 4:
      case 5:
         return true;
      case 2:
      default:
         return false;
      }
   }

   public void copyValueAndOid(SnmpVarBind var1) {
      this.setOid((SnmpOid)((SnmpOid)var1.oid.clone()));
      this.copyValue(var1);
   }

   public void copyValue(SnmpVarBind var1) {
      if (var1.isValidValue()) {
         this.value = var1.getSnmpValue().duplicate();
         this.setValueValid();
      } else {
         this.status = var1.getValueStatus();
         if (this.status == 5) {
            this.value = endOfMibView;
         } else if (this.status == 3) {
            this.value = noSuchObject;
         } else if (this.status == 4) {
            this.value = noSuchInstance;
         }
      }

   }

   public Object cloneWithoutValue() {
      SnmpOid var1 = (SnmpOid)this.oid.clone();
      return new SnmpVarBind(var1);
   }

   public SnmpVarBind clone() {
      SnmpVarBind var1 = new SnmpVarBind();
      var1.copyValueAndOid(this);
      return var1;
   }

   public final String getStringValue() {
      return this.value.toString();
   }

   public final void setNoSuchObject() {
      this.value = noSuchObject;
      this.status = 3;
   }

   public final void setNoSuchInstance() {
      this.value = noSuchInstance;
      this.status = 4;
   }

   public final void setEndOfMibView() {
      this.value = endOfMibView;
      this.status = 5;
   }

   public final String toString() {
      StringBuilder var1 = new StringBuilder(400);
      var1.append("Object ID : ").append(this.oid.toString());
      if (this.isValidValue()) {
         var1.append("  (Syntax : ").append(this.value.getTypeName()).append(")\n");
         var1.append("Value : ").append(this.value.toString());
      } else {
         var1.append("\nValue Exception : ").append(this.getValueStatusLegend());
      }

      return var1.toString();
   }

   private void setValueValid() {
      if (this.value == endOfMibView) {
         this.status = 5;
      } else if (this.value == noSuchObject) {
         this.status = 3;
      } else if (this.value == noSuchInstance) {
         this.status = 4;
      } else {
         this.status = 2;
      }

   }

   private void handleLong(String var1, int var2) throws NumberFormatException, SnmpStatusException {
      String var3;
      if (var2 > 0) {
         var3 = var1.substring(0, var2);
      } else {
         var3 = var1;
      }

      Long.parseLong(var3);
   }
}
