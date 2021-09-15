package com.sun.security.sasl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.AuthorizeCallback;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;

final class CramMD5Server extends CramMD5Base implements SaslServer {
   private String fqdn;
   private byte[] challengeData = null;
   private String authzid;
   private CallbackHandler cbh;

   CramMD5Server(String var1, String var2, Map<String, ?> var3, CallbackHandler var4) throws SaslException {
      if (var2 == null) {
         throw new SaslException("CRAM-MD5: fully qualified server name must be specified");
      } else {
         this.fqdn = var2;
         this.cbh = var4;
      }
   }

   public byte[] evaluateResponse(byte[] var1) throws SaslException {
      if (this.completed) {
         throw new IllegalStateException("CRAM-MD5 authentication already completed");
      } else if (this.aborted) {
         throw new IllegalStateException("CRAM-MD5 authentication previously aborted due to error");
      } else {
         try {
            String var23;
            if (this.challengeData == null) {
               if (var1.length != 0) {
                  this.aborted = true;
                  throw new SaslException("CRAM-MD5 does not expect any initial response");
               } else {
                  Random var18 = new Random();
                  long var20 = var18.nextLong();
                  long var21 = System.currentTimeMillis();
                  StringBuffer var22 = new StringBuffer();
                  var22.append('<');
                  var22.append(var20);
                  var22.append('.');
                  var22.append(var21);
                  var22.append('@');
                  var22.append(this.fqdn);
                  var22.append('>');
                  var23 = var22.toString();
                  logger.log(Level.FINE, (String)"CRAMSRV01:Generated challenge: {0}", (Object)var23);
                  this.challengeData = var23.getBytes("UTF8");
                  return (byte[])this.challengeData.clone();
               }
            } else {
               if (logger.isLoggable(Level.FINE)) {
                  logger.log(Level.FINE, (String)"CRAMSRV02:Received response: {0}", (Object)(new String(var1, "UTF8")));
               }

               int var2 = 0;

               for(int var3 = 0; var3 < var1.length; ++var3) {
                  if (var1[var3] == 32) {
                     var2 = var3;
                     break;
                  }
               }

               if (var2 == 0) {
                  this.aborted = true;
                  throw new SaslException("CRAM-MD5: Invalid response; space missing");
               } else {
                  String var19 = new String(var1, 0, var2, "UTF8");
                  logger.log(Level.FINE, (String)"CRAMSRV03:Extracted username: {0}", (Object)var19);
                  NameCallback var4 = new NameCallback("CRAM-MD5 authentication ID: ", var19);
                  PasswordCallback var5 = new PasswordCallback("CRAM-MD5 password: ", false);
                  this.cbh.handle(new Callback[]{var4, var5});
                  char[] var6 = var5.getPassword();
                  if (var6 != null && var6.length != 0) {
                     var5.clearPassword();
                     String var7 = new String(var6);

                     for(int var8 = 0; var8 < var6.length; ++var8) {
                        var6[var8] = 0;
                     }

                     this.pw = var7.getBytes("UTF8");
                     var23 = HMAC_MD5(this.pw, this.challengeData);
                     logger.log(Level.FINE, (String)"CRAMSRV04:Expecting digest: {0}", (Object)var23);
                     this.clearPassword();
                     byte[] var9 = var23.getBytes("UTF8");
                     int var10 = var1.length - var2 - 1;
                     if (var9.length != var10) {
                        this.aborted = true;
                        throw new SaslException("Invalid response");
                     } else {
                        int var11 = 0;

                        for(int var12 = var2 + 1; var12 < var1.length; ++var12) {
                           if (var9[var11++] != var1[var12]) {
                              this.aborted = true;
                              throw new SaslException("Invalid response");
                           }
                        }

                        AuthorizeCallback var24 = new AuthorizeCallback(var19, var19);
                        this.cbh.handle(new Callback[]{var24});
                        if (var24.isAuthorized()) {
                           this.authzid = var24.getAuthorizedID();
                           logger.log(Level.FINE, (String)"CRAMSRV05:Authorization id: {0}", (Object)this.authzid);
                           this.completed = true;
                           return null;
                        } else {
                           this.aborted = true;
                           throw new SaslException("CRAM-MD5: user not authorized: " + var19);
                        }
                     }
                  } else {
                     this.aborted = true;
                     throw new SaslException("CRAM-MD5: username not found: " + var19);
                  }
               }
            }
         } catch (UnsupportedEncodingException var13) {
            this.aborted = true;
            throw new SaslException("UTF8 not available on platform", var13);
         } catch (NoSuchAlgorithmException var14) {
            this.aborted = true;
            throw new SaslException("MD5 algorithm not available on platform", var14);
         } catch (UnsupportedCallbackException var15) {
            this.aborted = true;
            throw new SaslException("CRAM-MD5 authentication failed", var15);
         } catch (SaslException var16) {
            throw var16;
         } catch (IOException var17) {
            this.aborted = true;
            throw new SaslException("CRAM-MD5 authentication failed", var17);
         }
      }
   }

   public String getAuthorizationID() {
      if (this.completed) {
         return this.authzid;
      } else {
         throw new IllegalStateException("CRAM-MD5 authentication not completed");
      }
   }
}
