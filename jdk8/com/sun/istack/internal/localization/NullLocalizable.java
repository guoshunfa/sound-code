package com.sun.istack.internal.localization;

public final class NullLocalizable implements Localizable {
   private final String msg;

   public NullLocalizable(String msg) {
      if (msg == null) {
         throw new IllegalArgumentException();
      } else {
         this.msg = msg;
      }
   }

   public String getKey() {
      return "\u0000";
   }

   public Object[] getArguments() {
      return new Object[]{this.msg};
   }

   public String getResourceBundleName() {
      return "";
   }
}
