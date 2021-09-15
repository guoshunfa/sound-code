package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpEngine;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpVarBind;
import java.util.Enumeration;
import java.util.Vector;

final class SnmpMibRequestImpl implements SnmpMibRequest {
   private Vector<SnmpVarBind> varbinds;
   private int version;
   private Object data;
   private SnmpPdu reqPdu = null;
   private SnmpRequestTree tree = null;
   private SnmpEngine engine = null;
   private String principal = null;
   private int securityLevel = -1;
   private int securityModel = -1;
   private byte[] contextName = null;
   private byte[] accessContextName = null;

   public SnmpMibRequestImpl(SnmpEngine var1, SnmpPdu var2, Vector<SnmpVarBind> var3, int var4, Object var5, String var6, int var7, int var8, byte[] var9, byte[] var10) {
      this.varbinds = var3;
      this.version = var4;
      this.data = var5;
      this.reqPdu = var2;
      this.engine = var1;
      this.principal = var6;
      this.securityLevel = var7;
      this.securityModel = var8;
      this.contextName = var9;
      this.accessContextName = var10;
   }

   public SnmpEngine getEngine() {
      return this.engine;
   }

   public String getPrincipal() {
      return this.principal;
   }

   public int getSecurityLevel() {
      return this.securityLevel;
   }

   public int getSecurityModel() {
      return this.securityModel;
   }

   public byte[] getContextName() {
      return this.contextName;
   }

   public byte[] getAccessContextName() {
      return this.accessContextName;
   }

   public final SnmpPdu getPdu() {
      return this.reqPdu;
   }

   public final Enumeration<SnmpVarBind> getElements() {
      return this.varbinds.elements();
   }

   public final Vector<SnmpVarBind> getSubList() {
      return this.varbinds;
   }

   public final int getSize() {
      return this.varbinds == null ? 0 : this.varbinds.size();
   }

   public final int getVersion() {
      return this.version;
   }

   public final int getRequestPduVersion() {
      return this.reqPdu.version;
   }

   public final Object getUserData() {
      return this.data;
   }

   public final int getVarIndex(SnmpVarBind var1) {
      return this.varbinds.indexOf(var1);
   }

   public void addVarBind(SnmpVarBind var1) {
      this.varbinds.addElement(var1);
   }

   final void setRequestTree(SnmpRequestTree var1) {
      this.tree = var1;
   }

   final SnmpRequestTree getRequestTree() {
      return this.tree;
   }

   final Vector<SnmpVarBind> getVarbinds() {
      return this.varbinds;
   }
}
