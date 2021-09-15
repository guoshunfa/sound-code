package sun.management.counter;

public interface ByteArrayCounter extends Counter {
   byte[] byteArrayValue();

   byte byteAt(int var1);
}
