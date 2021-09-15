package com.sun.security.sasl.gsskerb;

import com.sun.security.sasl.util.AbstractSaslImpl;
import java.util.Map;
import java.util.logging.Level;
import javax.security.sasl.SaslException;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.MessageProp;
import org.ietf.jgss.Oid;

abstract class GssKrb5Base extends AbstractSaslImpl {
   private static final String KRB5_OID_STR = "1.2.840.113554.1.2.2";
   protected static Oid KRB5_OID;
   protected static final byte[] EMPTY = new byte[0];
   protected GSSContext secCtx = null;
   protected static final int JGSS_QOP = 0;

   protected GssKrb5Base(Map<String, ?> var1, String var2) throws SaslException {
      super(var1, var2);
   }

   public String getMechanismName() {
      return "GSSAPI";
   }

   public byte[] unwrap(byte[] var1, int var2, int var3) throws SaslException {
      if (!this.completed) {
         throw new IllegalStateException("GSSAPI authentication not completed");
      } else if (!this.integrity) {
         throw new IllegalStateException("No security layer negotiated");
      } else {
         try {
            MessageProp var4 = new MessageProp(0, this.privacy);
            byte[] var5 = this.secCtx.unwrap(var1, var2, var3, var4);
            if (logger.isLoggable(Level.FINEST)) {
               traceOutput(this.myClassName, "KRB501:Unwrap", "incoming: ", var1, var2, var3);
               traceOutput(this.myClassName, "KRB502:Unwrap", "unwrapped: ", var5, 0, var5.length);
            }

            return var5;
         } catch (GSSException var6) {
            throw new SaslException("Problems unwrapping SASL buffer", var6);
         }
      }
   }

   public byte[] wrap(byte[] var1, int var2, int var3) throws SaslException {
      if (!this.completed) {
         throw new IllegalStateException("GSSAPI authentication not completed");
      } else if (!this.integrity) {
         throw new IllegalStateException("No security layer negotiated");
      } else {
         try {
            MessageProp var4 = new MessageProp(0, this.privacy);
            byte[] var5 = this.secCtx.wrap(var1, var2, var3, var4);
            if (logger.isLoggable(Level.FINEST)) {
               traceOutput(this.myClassName, "KRB503:Wrap", "outgoing: ", var1, var2, var3);
               traceOutput(this.myClassName, "KRB504:Wrap", "wrapped: ", var5, 0, var5.length);
            }

            return var5;
         } catch (GSSException var6) {
            throw new SaslException("Problem performing GSS wrap", var6);
         }
      }
   }

   public void dispose() throws SaslException {
      if (this.secCtx != null) {
         try {
            this.secCtx.dispose();
         } catch (GSSException var2) {
            throw new SaslException("Problem disposing GSS context", var2);
         }

         this.secCtx = null;
      }

   }

   protected void finalize() throws Throwable {
      this.dispose();
   }

   static {
      try {
         KRB5_OID = new Oid("1.2.840.113554.1.2.2");
      } catch (GSSException var1) {
      }

   }
}
