package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.ObjectAdapterId;
import java.util.Iterator;
import org.omg.CORBA_2_3.portable.OutputStream;

abstract class ObjectAdapterIdBase implements ObjectAdapterId {
   public boolean equals(Object var1) {
      if (!(var1 instanceof ObjectAdapterId)) {
         return false;
      } else {
         ObjectAdapterId var2 = (ObjectAdapterId)var1;
         Iterator var3 = this.iterator();
         Iterator var4 = var2.iterator();

         while(var3.hasNext() && var4.hasNext()) {
            String var5 = (String)((String)var3.next());
            String var6 = (String)((String)var4.next());
            if (!var5.equals(var6)) {
               return false;
            }
         }

         return var3.hasNext() == var4.hasNext();
      }
   }

   public int hashCode() {
      int var1 = 17;

      String var3;
      for(Iterator var2 = this.iterator(); var2.hasNext(); var1 = 37 * var1 + var3.hashCode()) {
         var3 = (String)((String)var2.next());
      }

      return var1;
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append("ObjectAdapterID[");
      Iterator var2 = this.iterator();

      String var4;
      for(boolean var3 = true; var2.hasNext(); var1.append(var4)) {
         var4 = (String)((String)var2.next());
         if (var3) {
            var3 = false;
         } else {
            var1.append("/");
         }
      }

      var1.append("]");
      return var1.toString();
   }

   public void write(OutputStream var1) {
      var1.write_long(this.getNumLevels());
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         String var3 = (String)((String)var2.next());
         var1.write_string(var3);
      }

   }
}
