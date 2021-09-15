package com.sun.corba.se.spi.orbutil.fsm;

class TestAction2 implements Action {
   private State oldState;
   private State newState;

   public void doIt(FSM var1, Input var2) {
      System.out.println("TestAction2:");
      System.out.println("\toldState = " + this.oldState);
      System.out.println("\tnewState = " + this.newState);
      System.out.println("\tinput    = " + var2);
      if (this.oldState != var1.getState()) {
         throw new Error("Unexpected old State " + var1.getState());
      }
   }

   public TestAction2(State var1, State var2) {
      this.oldState = var1;
      this.newState = var2;
   }
}
