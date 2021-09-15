package com.sun.corba.se.spi.orbutil.fsm;

public interface StateEngine {
   StateEngine add(State var1, Input var2, Guard var3, Action var4, State var5) throws IllegalStateException;

   StateEngine add(State var1, Input var2, Action var3, State var4) throws IllegalStateException;

   StateEngine setDefault(State var1, Action var2, State var3) throws IllegalStateException;

   StateEngine setDefault(State var1, State var2) throws IllegalStateException;

   StateEngine setDefault(State var1) throws IllegalStateException;

   void setDefaultAction(Action var1) throws IllegalStateException;

   void done() throws IllegalStateException;

   FSM makeFSM(State var1) throws IllegalStateException;
}
