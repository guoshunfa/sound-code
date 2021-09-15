package com.sun.beans.decoder;

final class CharElementHandler extends StringElementHandler {
   public void addAttribute(String var1, String var2) {
      if (var1.equals("code")) {
         int var3 = Integer.decode(var2);
         char[] var4 = Character.toChars(var3);
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            char var7 = var4[var6];
            this.addCharacter(var7);
         }
      } else {
         super.addAttribute(var1, var2);
      }

   }

   public Object getValue(String var1) {
      if (var1.length() != 1) {
         throw new IllegalArgumentException("Wrong characters count");
      } else {
         return var1.charAt(0);
      }
   }
}
