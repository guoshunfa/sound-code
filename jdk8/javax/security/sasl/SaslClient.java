package javax.security.sasl;

public interface SaslClient {
   String getMechanismName();

   boolean hasInitialResponse();

   byte[] evaluateChallenge(byte[] var1) throws SaslException;

   boolean isComplete();

   byte[] unwrap(byte[] var1, int var2, int var3) throws SaslException;

   byte[] wrap(byte[] var1, int var2, int var3) throws SaslException;

   Object getNegotiatedProperty(String var1);

   void dispose() throws SaslException;
}
