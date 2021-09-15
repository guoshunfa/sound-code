package com.sun.jmx.snmp;

public interface SnmpUsmKeyHandler {
   int DES_KEY_SIZE = 16;
   int DES_DELTA_SIZE = 16;

   byte[] password_to_key(String var1, String var2) throws IllegalArgumentException;

   byte[] localizeAuthKey(String var1, byte[] var2, SnmpEngineId var3) throws IllegalArgumentException;

   byte[] localizePrivKey(String var1, byte[] var2, SnmpEngineId var3, int var4) throws IllegalArgumentException;

   byte[] calculateAuthDelta(String var1, byte[] var2, byte[] var3, byte[] var4) throws IllegalArgumentException;

   byte[] calculatePrivDelta(String var1, byte[] var2, byte[] var3, byte[] var4, int var5) throws IllegalArgumentException;
}
