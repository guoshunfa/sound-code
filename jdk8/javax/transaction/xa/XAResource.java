package javax.transaction.xa;

public interface XAResource {
   int TMENDRSCAN = 8388608;
   int TMFAIL = 536870912;
   int TMJOIN = 2097152;
   int TMNOFLAGS = 0;
   int TMONEPHASE = 1073741824;
   int TMRESUME = 134217728;
   int TMSTARTRSCAN = 16777216;
   int TMSUCCESS = 67108864;
   int TMSUSPEND = 33554432;
   int XA_RDONLY = 3;
   int XA_OK = 0;

   void commit(Xid var1, boolean var2) throws XAException;

   void end(Xid var1, int var2) throws XAException;

   void forget(Xid var1) throws XAException;

   int getTransactionTimeout() throws XAException;

   boolean isSameRM(XAResource var1) throws XAException;

   int prepare(Xid var1) throws XAException;

   Xid[] recover(int var1) throws XAException;

   void rollback(Xid var1) throws XAException;

   boolean setTransactionTimeout(int var1) throws XAException;

   void start(Xid var1, int var2) throws XAException;
}
