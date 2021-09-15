package com.sun.jndi.ldap;

import java.io.IOException;

public final class PersistentSearchControl extends BasicControl {
   public static final String OID = "2.16.840.1.113730.3.4.3";
   public static final int ADD = 1;
   public static final int DELETE = 2;
   public static final int MODIFY = 4;
   public static final int RENAME = 8;
   public static final int ANY = 15;
   private int changeTypes = 15;
   private boolean changesOnly = false;
   private boolean returnControls = true;
   private static final long serialVersionUID = 6335140491154854116L;

   public PersistentSearchControl() throws IOException {
      super("2.16.840.1.113730.3.4.3");
      super.value = this.setEncodedValue();
   }

   public PersistentSearchControl(int var1, boolean var2, boolean var3, boolean var4) throws IOException {
      super("2.16.840.1.113730.3.4.3", var4, (byte[])null);
      this.changeTypes = var1;
      this.changesOnly = var2;
      this.returnControls = var3;
      super.value = this.setEncodedValue();
   }

   private byte[] setEncodedValue() throws IOException {
      BerEncoder var1 = new BerEncoder(32);
      var1.beginSeq(48);
      var1.encodeInt(this.changeTypes);
      var1.encodeBoolean(this.changesOnly);
      var1.encodeBoolean(this.returnControls);
      var1.endSeq();
      return var1.getTrimmedBuf();
   }
}
