package java.security.cert;

public interface CRLSelector extends Cloneable {
   boolean match(CRL var1);

   Object clone();
}
