package sun.net.www.protocol.http;

import java.io.IOException;
import java.lang.reflect.Constructor;
import sun.util.logging.PlatformLogger;

public abstract class Negotiator {
   static Negotiator getNegotiator(HttpCallerInfo var0) {
      Constructor var2;
      try {
         Class var1 = Class.forName("sun.net.www.protocol.http.spnego.NegotiatorImpl", true, (ClassLoader)null);
         var2 = var1.getConstructor(HttpCallerInfo.class);
      } catch (ClassNotFoundException var5) {
         finest(var5);
         return null;
      } catch (ReflectiveOperationException var6) {
         throw new AssertionError(var6);
      }

      try {
         return (Negotiator)((Negotiator)var2.newInstance(var0));
      } catch (ReflectiveOperationException var7) {
         finest(var7);
         Throwable var4 = var7.getCause();
         if (var4 != null && var4 instanceof Exception) {
            finest((Exception)var4);
         }

         return null;
      }
   }

   public abstract byte[] firstToken() throws IOException;

   public abstract byte[] nextToken(byte[] var1) throws IOException;

   private static void finest(Exception var0) {
      PlatformLogger var1 = HttpURLConnection.getHttpLogger();
      if (var1.isLoggable(PlatformLogger.Level.FINEST)) {
         var1.finest("NegotiateAuthentication: " + var0);
      }

   }
}
