package sun.rmi.transport;

class SequenceEntry {
   long sequenceNum;
   boolean keep;

   SequenceEntry(long var1) {
      this.sequenceNum = var1;
      this.keep = false;
   }

   void retain(long var1) {
      this.sequenceNum = var1;
      this.keep = true;
   }

   void update(long var1) {
      this.sequenceNum = var1;
   }
}
