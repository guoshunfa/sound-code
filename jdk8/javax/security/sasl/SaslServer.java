package javax.security.sasl;

public interface SaslServer {
   String getMechanismName();

   byte[] evaluateResponse(byte[] var1) throws SaslException;

   boolean isComplete();

   String getAuthorizationID();

   byte[] unwrap(byte[] var1, int var2, int var3) throws SaslException;

   byte[] wrap(byte[] var1, int var2, int var3) throws SaslException;

   Object getNegotiatedProperty(String var1);

   void dispose() throws SaslException;
}
