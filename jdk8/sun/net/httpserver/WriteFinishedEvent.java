package sun.net.httpserver;

class WriteFinishedEvent extends Event {
   WriteFinishedEvent(ExchangeImpl var1) {
      super(var1);

      assert !var1.writefinished;

      var1.writefinished = true;
   }
}
