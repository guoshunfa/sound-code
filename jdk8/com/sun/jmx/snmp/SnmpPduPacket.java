package com.sun.jmx.snmp;

import java.io.Serializable;

public abstract class SnmpPduPacket extends SnmpPdu implements Serializable {
   public byte[] community;
}
