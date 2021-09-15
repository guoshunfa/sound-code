package com.sun.xml.internal.org.jvnet.mimepull;

class Hdr implements Header {
   String name;
   String line;

   Hdr(String l) {
      int i = l.indexOf(58);
      if (i < 0) {
         this.name = l.trim();
      } else {
         this.name = l.substring(0, i).trim();
      }

      this.line = l;
   }

   Hdr(String n, String v) {
      this.name = n;
      this.line = n + ": " + v;
   }

   public String getName() {
      return this.name;
   }

   public String getValue() {
      int i = this.line.indexOf(58);
      if (i < 0) {
         return this.line;
      } else {
         int j;
         char c;
         if (this.name.equalsIgnoreCase("Content-Description")) {
            for(j = i + 1; j < this.line.length(); ++j) {
               c = this.line.charAt(j);
               if (c != '\t' && c != '\r' && c != '\n') {
                  break;
               }
            }
         } else {
            for(j = i + 1; j < this.line.length(); ++j) {
               c = this.line.charAt(j);
               if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                  break;
               }
            }
         }

         return this.line.substring(j);
      }
   }
}
