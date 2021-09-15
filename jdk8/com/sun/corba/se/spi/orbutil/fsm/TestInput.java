package com.sun.corba.se.spi.orbutil.fsm;

class TestInput {
   Input value;
   String msg;

   TestInput(Input var1, String var2) {
      this.value = var1;
      this.msg = var2;
   }

   public String toString() {
      return "Input " + this.value + " : " + this.msg;
   }

   public Input getInput() {
      return this.value;
   }
}
