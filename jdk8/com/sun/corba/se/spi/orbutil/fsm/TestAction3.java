package com.sun.corba.se.spi.orbutil.fsm;

class TestAction3 implements Action {
   private State oldState;
   private Input label;

   public void doIt(FSM var1, Input var2) {
      System.out.println("TestAction1:");
      System.out.println("\tlabel    = " + this.label);
      System.out.println("\toldState = " + this.oldState);
      if (this.label != var2) {
         throw new Error("Unexcepted Input " + var2);
      }
   }

   public TestAction3(State var1, Input var2) {
      this.oldState = var1;
      this.label = var2;
   }
}
