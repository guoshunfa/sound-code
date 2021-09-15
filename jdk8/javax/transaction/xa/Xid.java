package javax.transaction.xa;

public interface Xid {
   int MAXGTRIDSIZE = 64;
   int MAXBQUALSIZE = 64;

   int getFormatId();

   byte[] getGlobalTransactionId();

   byte[] getBranchQualifier();
}
