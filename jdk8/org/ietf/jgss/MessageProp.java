package org.ietf.jgss;

public class MessageProp {
   private boolean privacyState;
   private int qop;
   private boolean dupToken;
   private boolean oldToken;
   private boolean unseqToken;
   private boolean gapToken;
   private int minorStatus;
   private String minorString;

   public MessageProp(boolean var1) {
      this(0, var1);
   }

   public MessageProp(int var1, boolean var2) {
      this.qop = var1;
      this.privacyState = var2;
      this.resetStatusValues();
   }

   public int getQOP() {
      return this.qop;
   }

   public boolean getPrivacy() {
      return this.privacyState;
   }

   public void setQOP(int var1) {
      this.qop = var1;
   }

   public void setPrivacy(boolean var1) {
      this.privacyState = var1;
   }

   public boolean isDuplicateToken() {
      return this.dupToken;
   }

   public boolean isOldToken() {
      return this.oldToken;
   }

   public boolean isUnseqToken() {
      return this.unseqToken;
   }

   public boolean isGapToken() {
      return this.gapToken;
   }

   public int getMinorStatus() {
      return this.minorStatus;
   }

   public String getMinorString() {
      return this.minorString;
   }

   public void setSupplementaryStates(boolean var1, boolean var2, boolean var3, boolean var4, int var5, String var6) {
      this.dupToken = var1;
      this.oldToken = var2;
      this.unseqToken = var3;
      this.gapToken = var4;
      this.minorStatus = var5;
      this.minorString = var6;
   }

   private void resetStatusValues() {
      this.dupToken = false;
      this.oldToken = false;
      this.unseqToken = false;
      this.gapToken = false;
      this.minorStatus = 0;
      this.minorString = null;
   }
}
