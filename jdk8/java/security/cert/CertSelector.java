package java.security.cert;

public interface CertSelector extends Cloneable {
   boolean match(Certificate var1);

   Object clone();
}
