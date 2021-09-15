package com.sun.security.sasl.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.sasl.SaslException;
import sun.misc.HexDumpEncoder;

public abstract class AbstractSaslImpl {
   protected boolean completed = false;
   protected boolean privacy = false;
   protected boolean integrity = false;
   protected byte[] qop;
   protected byte allQop;
   protected byte[] strength;
   protected int sendMaxBufSize = 0;
   protected int recvMaxBufSize = 65536;
   protected int rawSendSize;
   protected String myClassName;
   private static final String SASL_LOGGER_NAME = "javax.security.sasl";
   protected static final String MAX_SEND_BUF = "javax.security.sasl.sendmaxbuffer";
   protected static final Logger logger = Logger.getLogger("javax.security.sasl");
   protected static final byte NO_PROTECTION = 1;
   protected static final byte INTEGRITY_ONLY_PROTECTION = 2;
   protected static final byte PRIVACY_PROTECTION = 4;
   protected static final byte LOW_STRENGTH = 1;
   protected static final byte MEDIUM_STRENGTH = 2;
   protected static final byte HIGH_STRENGTH = 4;
   private static final byte[] DEFAULT_QOP = new byte[]{1};
   private static final String[] QOP_TOKENS = new String[]{"auth-conf", "auth-int", "auth"};
   private static final byte[] QOP_MASKS = new byte[]{4, 2, 1};
   private static final byte[] DEFAULT_STRENGTH = new byte[]{4, 2, 1};
   private static final String[] STRENGTH_TOKENS = new String[]{"low", "medium", "high"};
   private static final byte[] STRENGTH_MASKS = new byte[]{1, 2, 4};

   protected AbstractSaslImpl(Map<String, ?> var1, String var2) throws SaslException {
      this.myClassName = var2;
      if (var1 != null) {
         String var3;
         this.qop = parseQop(var3 = (String)var1.get("javax.security.sasl.qop"));
         logger.logp(Level.FINE, this.myClassName, "constructor", (String)"SASLIMPL01:Preferred qop property: {0}", (Object)var3);
         this.allQop = combineMasks(this.qop);
         StringBuffer var4;
         int var5;
         if (logger.isLoggable(Level.FINE)) {
            logger.logp(Level.FINE, this.myClassName, "constructor", (String)"SASLIMPL02:Preferred qop mask: {0}", (Object)(new Byte(this.allQop)));
            if (this.qop.length > 0) {
               var4 = new StringBuffer();

               for(var5 = 0; var5 < this.qop.length; ++var5) {
                  var4.append(Byte.toString(this.qop[var5]));
                  var4.append(' ');
               }

               logger.logp(Level.FINE, this.myClassName, "constructor", (String)"SASLIMPL03:Preferred qops : {0}", (Object)var4.toString());
            }
         }

         this.strength = parseStrength(var3 = (String)var1.get("javax.security.sasl.strength"));
         logger.logp(Level.FINE, this.myClassName, "constructor", (String)"SASLIMPL04:Preferred strength property: {0}", (Object)var3);
         if (logger.isLoggable(Level.FINE) && this.strength.length > 0) {
            var4 = new StringBuffer();

            for(var5 = 0; var5 < this.strength.length; ++var5) {
               var4.append(Byte.toString(this.strength[var5]));
               var4.append(' ');
            }

            logger.logp(Level.FINE, this.myClassName, "constructor", (String)"SASLIMPL05:Cipher strengths: {0}", (Object)var4.toString());
         }

         var3 = (String)var1.get("javax.security.sasl.maxbuffer");
         if (var3 != null) {
            try {
               logger.logp(Level.FINE, this.myClassName, "constructor", (String)"SASLIMPL06:Max receive buffer size: {0}", (Object)var3);
               this.recvMaxBufSize = Integer.parseInt(var3);
            } catch (NumberFormatException var7) {
               throw new SaslException("Property must be string representation of integer: javax.security.sasl.maxbuffer");
            }
         }

         var3 = (String)var1.get("javax.security.sasl.sendmaxbuffer");
         if (var3 != null) {
            try {
               logger.logp(Level.FINE, this.myClassName, "constructor", (String)"SASLIMPL07:Max send buffer size: {0}", (Object)var3);
               this.sendMaxBufSize = Integer.parseInt(var3);
            } catch (NumberFormatException var6) {
               throw new SaslException("Property must be string representation of integer: javax.security.sasl.sendmaxbuffer");
            }
         }
      } else {
         this.qop = DEFAULT_QOP;
         this.allQop = 1;
         this.strength = STRENGTH_MASKS;
      }

   }

   public boolean isComplete() {
      return this.completed;
   }

   public Object getNegotiatedProperty(String var1) {
      if (!this.completed) {
         throw new IllegalStateException("SASL authentication not completed");
      } else {
         byte var3 = -1;
         switch(var1.hashCode()) {
         case -2079432448:
            if (var1.equals("javax.security.sasl.rawsendsize")) {
               var3 = 2;
            }
            break;
         case -1673898581:
            if (var1.equals("javax.security.sasl.sendmaxbuffer")) {
               var3 = 3;
            }
            break;
         case -1548608927:
            if (var1.equals("javax.security.sasl.qop")) {
               var3 = 0;
            }
            break;
         case 1495157683:
            if (var1.equals("javax.security.sasl.maxbuffer")) {
               var3 = 1;
            }
         }

         switch(var3) {
         case 0:
            if (this.privacy) {
               return "auth-conf";
            } else {
               if (this.integrity) {
                  return "auth-int";
               }

               return "auth";
            }
         case 1:
            return Integer.toString(this.recvMaxBufSize);
         case 2:
            return Integer.toString(this.rawSendSize);
         case 3:
            return Integer.toString(this.sendMaxBufSize);
         default:
            return null;
         }
      }
   }

   protected static final byte combineMasks(byte[] var0) {
      byte var1 = 0;

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1 |= var0[var2];
      }

      return var1;
   }

   protected static final byte findPreferredMask(byte var0, byte[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         if ((var1[var2] & var0) != 0) {
            return var1[var2];
         }
      }

      return 0;
   }

   private static final byte[] parseQop(String var0) throws SaslException {
      return parseQop(var0, (String[])null, false);
   }

   protected static final byte[] parseQop(String var0, String[] var1, boolean var2) throws SaslException {
      return var0 == null ? DEFAULT_QOP : parseProp("javax.security.sasl.qop", var0, QOP_TOKENS, QOP_MASKS, var1, var2);
   }

   private static final byte[] parseStrength(String var0) throws SaslException {
      return var0 == null ? DEFAULT_STRENGTH : parseProp("javax.security.sasl.strength", var0, STRENGTH_TOKENS, STRENGTH_MASKS, (String[])null, false);
   }

   private static final byte[] parseProp(String var0, String var1, String[] var2, byte[] var3, String[] var4, boolean var5) throws SaslException {
      StringTokenizer var6 = new StringTokenizer(var1, ", \t\n");
      byte[] var8 = new byte[var2.length];
      int var9 = 0;

      int var11;
      while(var6.hasMoreTokens() && var9 < var8.length) {
         String var7 = var6.nextToken();
         boolean var10 = false;

         for(var11 = 0; !var10 && var11 < var2.length; ++var11) {
            if (var7.equalsIgnoreCase(var2[var11])) {
               var10 = true;
               var8[var9++] = var3[var11];
               if (var4 != null) {
                  var4[var11] = var7;
               }
            }
         }

         if (!var10 && !var5) {
            throw new SaslException("Invalid token in " + var0 + ": " + var1);
         }
      }

      for(var11 = var9; var11 < var8.length; ++var11) {
         var8[var11] = 0;
      }

      return var8;
   }

   protected static final void traceOutput(String var0, String var1, String var2, byte[] var3) {
      traceOutput(var0, var1, var2, var3, 0, var3 == null ? 0 : var3.length);
   }

   protected static final void traceOutput(String var0, String var1, String var2, byte[] var3, int var4, int var5) {
      try {
         Level var7;
         if (!logger.isLoggable(Level.FINEST)) {
            var5 = Math.min(16, var5);
            var7 = Level.FINER;
         } else {
            var7 = Level.FINEST;
         }

         String var8;
         if (var3 != null) {
            ByteArrayOutputStream var9 = new ByteArrayOutputStream(var5);
            (new HexDumpEncoder()).encodeBuffer(new ByteArrayInputStream(var3, var4, var5), var9);
            var8 = var9.toString();
         } else {
            var8 = "NULL";
         }

         logger.logp(var7, var0, var1, "{0} ( {1} ): {2}", new Object[]{var2, new Integer(var5), var8});
      } catch (Exception var10) {
         logger.logp(Level.WARNING, var0, var1, (String)"SASLIMPL09:Error generating trace output: {0}", (Throwable)var10);
      }

   }

   protected static final int networkByteOrderToInt(byte[] var0, int var1, int var2) {
      if (var2 > 4) {
         throw new IllegalArgumentException("Cannot handle more than 4 bytes");
      } else {
         int var3 = 0;

         for(int var4 = 0; var4 < var2; ++var4) {
            var3 <<= 8;
            var3 |= var0[var1 + var4] & 255;
         }

         return var3;
      }
   }

   protected static final void intToNetworkByteOrder(int var0, byte[] var1, int var2, int var3) {
      if (var3 > 4) {
         throw new IllegalArgumentException("Cannot handle more than 4 bytes");
      } else {
         for(int var4 = var3 - 1; var4 >= 0; --var4) {
            var1[var2 + var4] = (byte)(var0 & 255);
            var0 >>>= 8;
         }

      }
   }
}
