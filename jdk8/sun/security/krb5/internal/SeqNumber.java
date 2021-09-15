package sun.security.krb5.internal;

public interface SeqNumber {
   void randInit();

   void init(int var1);

   int current();

   int next();

   int step();
}
