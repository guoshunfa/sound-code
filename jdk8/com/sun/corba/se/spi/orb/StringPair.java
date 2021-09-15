package com.sun.corba.se.spi.orb;

public class StringPair {
   private String first;
   private String second;

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof StringPair)) {
         return false;
      } else {
         StringPair var2 = (StringPair)var1;
         return this.first.equals(var2.first) && this.second.equals(var2.second);
      }
   }

   public int hashCode() {
      return this.first.hashCode() ^ this.second.hashCode();
   }

   public StringPair(String var1, String var2) {
      this.first = var1;
      this.second = var2;
   }

   public String getFirst() {
      return this.first;
   }

   public String getSecond() {
      return this.second;
   }
}
