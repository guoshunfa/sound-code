package com.sun.corba.se.spi.orbutil.fsm;

class TestAction1 implements Action {
   private State oldState;
   private Input label;
   private State newState;

   public void doIt(FSM var1, Input var2) {
      System.out.println("TestAction1:");
      System.out.println("\tlabel    = " + this.label);
      System.out.println("\toldState = " + this.oldState);
      System.out.println("\tnewState = " + this.newState);
      if (this.label != var2) {
         throw new Error("Unexcepted Input " + var2);
      } else if (this.oldState != var1.getState()) {
         throw new Error("Unexpected old State " + var1.getState());
      }
   }

   public TestAction1(State var1, Input var2, State var3) {
      this.oldState = var1;
      this.newState = var3;
      this.label = var2;
   }
}
