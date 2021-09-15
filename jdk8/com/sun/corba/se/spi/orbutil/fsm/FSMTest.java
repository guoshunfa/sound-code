package com.sun.corba.se.spi.orbutil.fsm;

public class FSMTest {
   public static final State STATE1 = new StateImpl("1");
   public static final State STATE2 = new StateImpl("2");
   public static final State STATE3 = new StateImpl("3");
   public static final State STATE4 = new StateImpl("4");
   public static final Input INPUT1 = new InputImpl("1");
   public static final Input INPUT2 = new InputImpl("2");
   public static final Input INPUT3 = new InputImpl("3");
   public static final Input INPUT4 = new InputImpl("4");
   private Guard counterGuard = new Guard() {
      public Guard.Result evaluate(FSM var1, Input var2) {
         MyFSM var3 = (MyFSM)var1;
         return Guard.Result.convert(var3.counter < 3);
      }
   };

   private static void add1(StateEngine var0, State var1, Input var2, State var3) {
      var0.add(var1, var2, new TestAction1(var1, var2, var3), var3);
   }

   private static void add2(StateEngine var0, State var1, State var2) {
      var0.setDefault(var1, new TestAction2(var1, var2), var2);
   }

   public static void main(String[] var0) {
      TestAction3 var1 = new TestAction3(STATE3, INPUT1);
      StateEngine var2 = StateEngineFactory.create();
      add1(var2, STATE1, INPUT1, STATE1);
      add2(var2, STATE1, STATE2);
      add1(var2, STATE2, INPUT1, STATE2);
      add1(var2, STATE2, INPUT2, STATE2);
      add1(var2, STATE2, INPUT3, STATE1);
      add1(var2, STATE2, INPUT4, STATE3);
      var2.add(STATE3, INPUT1, var1, STATE3);
      var2.add(STATE3, INPUT1, var1, STATE4);
      add1(var2, STATE3, INPUT2, STATE1);
      add1(var2, STATE3, INPUT3, STATE2);
      add1(var2, STATE3, INPUT4, STATE2);
      MyFSM var3 = new MyFSM(var2);
      TestInput var4 = new TestInput(INPUT1, "1.1");
      TestInput var5 = new TestInput(INPUT1, "1.2");
      new TestInput(INPUT2, "2.1");
      TestInput var7 = new TestInput(INPUT2, "2.2");
      TestInput var8 = new TestInput(INPUT3, "3.1");
      TestInput var9 = new TestInput(INPUT3, "3.2");
      TestInput var10 = new TestInput(INPUT3, "3.3");
      TestInput var11 = new TestInput(INPUT4, "4.1");
      var3.doIt(var4.getInput());
      var3.doIt(var5.getInput());
      var3.doIt(var11.getInput());
      var3.doIt(var4.getInput());
      var3.doIt(var7.getInput());
      var3.doIt(var8.getInput());
      var3.doIt(var10.getInput());
      var3.doIt(var11.getInput());
      var3.doIt(var11.getInput());
      var3.doIt(var11.getInput());
      var3.doIt(var7.getInput());
      var3.doIt(var9.getInput());
      var3.doIt(var11.getInput());
      var3.doIt(var4.getInput());
      var3.doIt(var5.getInput());
      var3.doIt(var4.getInput());
      var3.doIt(var4.getInput());
      var3.doIt(var4.getInput());
      var3.doIt(var4.getInput());
      var3.doIt(var4.getInput());
   }
}
