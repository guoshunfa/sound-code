package com.sun.beans.editors;

import java.beans.PropertyEditorSupport;

public class StringEditor extends PropertyEditorSupport {
   public String getJavaInitializationString() {
      Object var1 = this.getValue();
      if (var1 == null) {
         return "null";
      } else {
         String var2 = var1.toString();
         int var3 = var2.length();
         StringBuilder var4 = new StringBuilder(var3 + 2);
         var4.append('"');

         for(int var5 = 0; var5 < var3; ++var5) {
            char var6 = var2.charAt(var5);
            String var7;
            int var8;
            switch(var6) {
            case '\b':
               var4.append("\\b");
               continue;
            case '\t':
               var4.append("\\t");
               continue;
            case '\n':
               var4.append("\\n");
               continue;
            case '\f':
               var4.append("\\f");
               continue;
            case '\r':
               var4.append("\\r");
               continue;
            case '"':
               var4.append("\\\"");
               continue;
            case '\\':
               var4.append("\\\\");
               continue;
            default:
               if (var6 >= ' ' && var6 <= '~') {
                  var4.append(var6);
                  continue;
               }

               var4.append("\\u");
               var7 = Integer.toHexString(var6);
               var8 = var7.length();
            }

            while(var8 < 4) {
               var4.append('0');
               ++var8;
            }

            var4.append(var7);
         }

         var4.append('"');
         return var4.toString();
      }
   }

   public void setAsText(String var1) {
      this.setValue(var1);
   }
}
