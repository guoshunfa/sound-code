package com.sun.corba.se.impl.orbutil.fsm;

import com.sun.corba.se.spi.orbutil.fsm.Action;
import com.sun.corba.se.spi.orbutil.fsm.Guard;
import com.sun.corba.se.spi.orbutil.fsm.Input;
import com.sun.corba.se.spi.orbutil.fsm.State;
import java.util.StringTokenizer;

public class NameBase {
   private String name;
   private String toStringName;

   private String getClassName() {
      String var1 = this.getClass().getName();
      StringTokenizer var2 = new StringTokenizer(var1, ".");

      String var3;
      for(var3 = var2.nextToken(); var2.hasMoreTokens(); var3 = var2.nextToken()) {
      }

      return var3;
   }

   private String getPreferredClassName() {
      if (this instanceof Action) {
         return "Action";
      } else if (this instanceof State) {
         return "State";
      } else if (this instanceof Guard) {
         return "Guard";
      } else {
         return this instanceof Input ? "Input" : this.getClassName();
      }
   }

   public NameBase(String var1) {
      this.name = var1;
      this.toStringName = this.getPreferredClassName() + "[" + var1 + "]";
   }

   public String getName() {
      return this.name;
   }

   public String toString() {
      return this.toStringName;
   }
}
