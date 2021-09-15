package com.sun.security.jgss;

import jdk.Exported;
import sun.misc.HexDumpEncoder;

@Exported
public final class AuthorizationDataEntry {
   private final int type;
   private final byte[] data;

   public AuthorizationDataEntry(int var1, byte[] var2) {
      this.type = var1;
      this.data = (byte[])var2.clone();
   }

   public int getType() {
      return this.type;
   }

   public byte[] getData() {
      return (byte[])this.data.clone();
   }

   public String toString() {
      return "AuthorizationDataEntry: type=" + this.type + ", data=" + this.data.length + " bytes:\n" + (new HexDumpEncoder()).encodeBuffer(this.data);
   }
}
