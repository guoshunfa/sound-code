package sun.misc;

import java.io.IOException;
import java.io.ObjectInputStream;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SealedObject;

public interface JavaxCryptoSealedObjectAccess {
   ObjectInputStream getExtObjectInputStream(SealedObject var1, Cipher var2) throws BadPaddingException, IllegalBlockSizeException, IOException;
}
