package javax.security.auth.callback;

import java.io.IOException;

public interface CallbackHandler {
   void handle(Callback[] var1) throws IOException, UnsupportedCallbackException;
}
