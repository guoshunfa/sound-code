package java.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** @deprecated */
@Deprecated
public interface Certificate {
   Principal getGuarantor();

   Principal getPrincipal();

   PublicKey getPublicKey();

   void encode(OutputStream var1) throws KeyException, IOException;

   void decode(InputStream var1) throws KeyException, IOException;

   String getFormat();

   String toString(boolean var1);
}
