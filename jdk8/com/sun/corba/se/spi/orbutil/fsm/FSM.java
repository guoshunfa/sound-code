package com.sun.corba.se.spi.orbutil.fsm;

public interface FSM {
   State getState();

   void doIt(Input var1);
}
