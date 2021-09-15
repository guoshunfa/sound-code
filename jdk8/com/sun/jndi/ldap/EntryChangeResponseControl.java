package com.sun.jndi.ldap;

import java.io.IOException;

public final class EntryChangeResponseControl extends BasicControl {
   public static final String OID = "2.16.840.1.113730.3.4.7";
   public static final int ADD = 1;
   public static final int DELETE = 2;
   public static final int MODIFY = 4;
   public static final int RENAME = 8;
   private int changeType;
   private String previousDN = null;
   private long changeNumber = -1L;
   private static final long serialVersionUID = -2087354136750180511L;

   public EntryChangeResponseControl(String var1, boolean var2, byte[] var3) throws IOException {
      super(var1, var2, var3);
      if (var3 != null && var3.length > 0) {
         BerDecoder var4 = new BerDecoder(var3, 0, var3.length);
         var4.parseSeq((int[])null);
         this.changeType = var4.parseEnumeration();
         if (var4.bytesLeft() > 0 && var4.peekByte() == 4) {
            this.previousDN = var4.parseString(true);
         }

         if (var4.bytesLeft() > 0 && var4.peekByte() == 2) {
            this.changeNumber = (long)var4.parseInt();
         }
      }

   }

   public int getChangeType() {
      return this.changeType;
   }

   public String getPreviousDN() {
      return this.previousDN;
   }

   public long getChangeNumber() {
      return this.changeNumber;
   }
}
