package com.sun.corba.se.spi.orbutil.fsm;

class MyFSM extends FSMImpl {
   public int counter = 0;

   public MyFSM(StateEngine var1) {
      super(var1, FSMTest.STATE1);
   }
}
