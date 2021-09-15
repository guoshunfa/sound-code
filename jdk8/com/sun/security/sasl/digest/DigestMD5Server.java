package com.sun.security.sasl.digest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.AuthorizeCallback;
import javax.security.sasl.RealmCallback;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;

final class DigestMD5Server extends DigestMD5Base implements SaslServer {
   private static final String MY_CLASS_NAME = DigestMD5Server.class.getName();
   private static final String UTF8_DIRECTIVE = "charset=utf-8,";
   private static final String ALGORITHM_DIRECTIVE = "algorithm=md5-sess";
   private static final int NONCE_COUNT_VALUE = 1;
   private static final String UTF8_PROPERTY = "com.sun.security.sasl.digest.utf8";
   private static final String REALM_PROPERTY = "com.sun.security.sasl.digest.realm";
   private static final String[] DIRECTIVE_KEY = new String[]{"username", "realm", "nonce", "cnonce", "nonce-count", "qop", "digest-uri", "response", "maxbuf", "charset", "cipher", "authzid", "auth-param"};
   private static final int USERNAME = 0;
   private static final int REALM = 1;
   private static final int NONCE = 2;
   private static final int CNONCE = 3;
   private static final int NONCE_COUNT = 4;
   private static final int QOP = 5;
   private static final int DIGEST_URI = 6;
   private static final int RESPONSE = 7;
   private static final int MAXBUF = 8;
   private static final int CHARSET = 9;
   private static final int CIPHER = 10;
   private static final int AUTHZID = 11;
   private static final int AUTH_PARAM = 12;
   private String specifiedQops;
   private byte[] myCiphers;
   private List<String> serverRealms = new ArrayList();

   DigestMD5Server(String var1, String var2, Map<String, ?> var3, CallbackHandler var4) throws SaslException {
      super(var3, MY_CLASS_NAME, 1, var1 + "/" + (var2 == null ? "*" : var2), var4);
      this.useUTF8 = true;
      if (var3 != null) {
         this.specifiedQops = (String)var3.get("javax.security.sasl.qop");
         if ("false".equals((String)var3.get("com.sun.security.sasl.digest.utf8"))) {
            this.useUTF8 = false;
            logger.log(Level.FINE, "DIGEST80:Server supports ISO-Latin-1");
         }

         String var5 = (String)var3.get("com.sun.security.sasl.digest.realm");
         if (var5 != null) {
            StringTokenizer var6 = new StringTokenizer(var5, ", \t\n");
            int var7 = var6.countTokens();
            String var8 = null;

            for(int var9 = 0; var9 < var7; ++var9) {
               var8 = var6.nextToken();
               logger.log(Level.FINE, (String)"DIGEST81:Server supports realm {0}", (Object)var8);
               this.serverRealms.add(var8);
            }
         }
      }

      this.encoding = this.useUTF8 ? "UTF8" : "8859_1";
      if (this.serverRealms.isEmpty()) {
         if (var2 == null) {
            throw new SaslException("A realm must be provided in props or serverName");
         }

         this.serverRealms.add(var2);
      }

   }

   public byte[] evaluateResponse(byte[] var1) throws SaslException {
      if (var1.length > 4096) {
         throw new SaslException("DIGEST-MD5: Invalid digest response length. Got:  " + var1.length + " Expected < " + 4096);
      } else {
         byte[] var2;
         switch(this.step) {
         case 1:
            if (var1.length != 0) {
               throw new SaslException("DIGEST-MD5 must not have an initial response");
            } else {
               String var3 = null;
               if ((this.allQop & 4) != 0) {
                  this.myCiphers = getPlatformCiphers();
                  StringBuffer var17 = new StringBuffer();

                  for(int var5 = 0; var5 < CIPHER_TOKENS.length; ++var5) {
                     if (this.myCiphers[var5] != 0) {
                        if (var17.length() > 0) {
                           var17.append(',');
                        }

                        var17.append(CIPHER_TOKENS[var5]);
                     }
                  }

                  var3 = var17.toString();
               }

               try {
                  var2 = this.generateChallenge(this.serverRealms, this.specifiedQops, var3);
                  this.step = 3;
                  return var2;
               } catch (UnsupportedEncodingException var15) {
                  throw new SaslException("DIGEST-MD5: Error encoding challenge", var15);
               } catch (IOException var16) {
                  throw new SaslException("DIGEST-MD5: Error generating challenge", var16);
               }
            }
         case 3:
            try {
               byte[][] var4 = parseDirectives(var1, DIRECTIVE_KEY, (List)null, 1);
               var2 = this.validateClientResponse(var4);
            } catch (SaslException var12) {
               throw var12;
            } catch (UnsupportedEncodingException var13) {
               throw new SaslException("DIGEST-MD5: Error validating client response", var13);
            } finally {
               this.step = 0;
            }

            this.completed = true;
            if (this.integrity && this.privacy) {
               this.secCtx = new DigestMD5Base.DigestPrivacy(false);
            } else if (this.integrity) {
               this.secCtx = new DigestMD5Base.DigestIntegrity(false);
            }

            return var2;
         default:
            throw new SaslException("DIGEST-MD5: Server at illegal state");
         }
      }
   }

   private byte[] generateChallenge(List<String> var1, String var2, String var3) throws UnsupportedEncodingException, IOException {
      ByteArrayOutputStream var4 = new ByteArrayOutputStream();

      for(int var5 = 0; var1 != null && var5 < var1.size(); ++var5) {
         var4.write("realm=\"".getBytes(this.encoding));
         writeQuotedStringValue(var4, ((String)var1.get(var5)).getBytes(this.encoding));
         var4.write(34);
         var4.write(44);
      }

      var4.write("nonce=\"".getBytes(this.encoding));
      this.nonce = generateNonce();
      writeQuotedStringValue(var4, this.nonce);
      var4.write(34);
      var4.write(44);
      if (var2 != null) {
         var4.write("qop=\"".getBytes(this.encoding));
         writeQuotedStringValue(var4, var2.getBytes(this.encoding));
         var4.write(34);
         var4.write(44);
      }

      if (this.recvMaxBufSize != 65536) {
         var4.write(("maxbuf=\"" + this.recvMaxBufSize + "\",").getBytes(this.encoding));
      }

      if (this.useUTF8) {
         var4.write("charset=utf-8,".getBytes(this.encoding));
      }

      if (var3 != null) {
         var4.write("cipher=\"".getBytes(this.encoding));
         writeQuotedStringValue(var4, var3.getBytes(this.encoding));
         var4.write(34);
         var4.write(44);
      }

      var4.write("algorithm=md5-sess".getBytes(this.encoding));
      return var4.toByteArray();
   }

   private byte[] validateClientResponse(byte[][] var1) throws SaslException, UnsupportedEncodingException {
      if (var1[9] == null || this.useUTF8 && "utf-8".equals(new String(var1[9], this.encoding))) {
         int var2 = var1[8] == null ? 65536 : Integer.parseInt(new String(var1[8], this.encoding));
         this.sendMaxBufSize = this.sendMaxBufSize == 0 ? var2 : Math.min(this.sendMaxBufSize, var2);
         if (var1[0] != null) {
            String var3 = new String(var1[0], this.encoding);
            logger.log(Level.FINE, (String)"DIGEST82:Username: {0}", (Object)var3);
            this.negotiatedRealm = var1[1] != null ? new String(var1[1], this.encoding) : "";
            logger.log(Level.FINE, (String)"DIGEST83:Client negotiated realm: {0}", (Object)this.negotiatedRealm);
            if (!this.serverRealms.contains(this.negotiatedRealm)) {
               throw new SaslException("DIGEST-MD5: digest response format violation. Nonexistent realm: " + this.negotiatedRealm);
            } else if (var1[2] == null) {
               throw new SaslException("DIGEST-MD5: digest response format violation. Missing nonce.");
            } else {
               byte[] var4 = var1[2];
               if (!Arrays.equals(var4, this.nonce)) {
                  throw new SaslException("DIGEST-MD5: digest response format violation. Mismatched nonce.");
               } else if (var1[3] == null) {
                  throw new SaslException("DIGEST-MD5: digest response format violation. Missing cnonce.");
               } else {
                  byte[] var5 = var1[3];
                  if (var1[4] != null && 1 != Integer.parseInt(new String(var1[4], this.encoding), 16)) {
                     throw new SaslException("DIGEST-MD5: digest response format violation. Nonce count does not match: " + new String(var1[4]));
                  } else {
                     this.negotiatedQop = var1[5] != null ? new String(var1[5], this.encoding) : "auth";
                     logger.log(Level.FINE, (String)"DIGEST84:Client negotiated qop: {0}", (Object)this.negotiatedQop);
                     String var7 = this.negotiatedQop;
                     byte var8 = -1;
                     switch(var7.hashCode()) {
                     case 3005864:
                        if (var7.equals("auth")) {
                           var8 = 0;
                        }
                        break;
                     case 1414216745:
                        if (var7.equals("auth-conf")) {
                           var8 = 2;
                        }
                        break;
                     case 1431098954:
                        if (var7.equals("auth-int")) {
                           var8 = 1;
                        }
                     }

                     byte var6;
                     switch(var8) {
                     case 0:
                        var6 = 1;
                        break;
                     case 1:
                        var6 = 2;
                        this.integrity = true;
                        this.rawSendSize = this.sendMaxBufSize - 16;
                        break;
                     case 2:
                        var6 = 4;
                        this.integrity = this.privacy = true;
                        this.rawSendSize = this.sendMaxBufSize - 26;
                        break;
                     default:
                        throw new SaslException("DIGEST-MD5: digest response format violation. Invalid QOP: " + this.negotiatedQop);
                     }

                     if ((var6 & this.allQop) == 0) {
                        throw new SaslException("DIGEST-MD5: server does not support  qop: " + this.negotiatedQop);
                     } else {
                        if (this.privacy) {
                           this.negotiatedCipher = var1[10] != null ? new String(var1[10], this.encoding) : null;
                           if (this.negotiatedCipher == null) {
                              throw new SaslException("DIGEST-MD5: digest response format violation. No cipher specified.");
                           }

                           int var34 = -1;
                           logger.log(Level.FINE, (String)"DIGEST85:Client negotiated cipher: {0}", (Object)this.negotiatedCipher);

                           for(int var35 = 0; var35 < CIPHER_TOKENS.length; ++var35) {
                              if (this.negotiatedCipher.equals(CIPHER_TOKENS[var35]) && this.myCiphers[var35] != 0) {
                                 var34 = var35;
                                 break;
                              }
                           }

                           if (var34 == -1) {
                              throw new SaslException("DIGEST-MD5: server does not support cipher: " + this.negotiatedCipher);
                           }

                           if ((CIPHER_MASKS[var34] & 4) != 0) {
                              this.negotiatedStrength = "high";
                           } else if ((CIPHER_MASKS[var34] & 2) != 0) {
                              this.negotiatedStrength = "medium";
                           } else {
                              this.negotiatedStrength = "low";
                           }

                           logger.log(Level.FINE, (String)"DIGEST86:Negotiated strength: {0}", (Object)this.negotiatedStrength);
                        }

                        var7 = var1[6] != null ? new String(var1[6], this.encoding) : null;
                        if (var7 != null) {
                           logger.log(Level.FINE, (String)"DIGEST87:digest URI: {0}", (Object)var7);
                        }

                        if (!uriMatches(this.digestUri, var7)) {
                           throw new SaslException("DIGEST-MD5: digest response format violation. Mismatched URI: " + var7 + "; expecting: " + this.digestUri);
                        } else {
                           this.digestUri = var7;
                           byte[] var36 = var1[7];
                           if (var36 == null) {
                              throw new SaslException("DIGEST-MD5: digest response format  violation. Missing response.");
                           } else {
                              byte[] var9;
                              String var10 = (var9 = var1[11]) != null ? new String(var9, this.encoding) : var3;
                              if (var9 != null) {
                                 logger.log(Level.FINE, (String)"DIGEST88:Authzid: {0}", (Object)(new String(var9)));
                              }

                              char[] var11;
                              try {
                                 RealmCallback var12 = new RealmCallback("DIGEST-MD5 realm: ", this.negotiatedRealm);
                                 NameCallback var13 = new NameCallback("DIGEST-MD5 authentication ID: ", var3);
                                 PasswordCallback var14 = new PasswordCallback("DIGEST-MD5 password: ", false);
                                 this.cbh.handle(new Callback[]{var12, var13, var14});
                                 var11 = var14.getPassword();
                                 var14.clearPassword();
                              } catch (UnsupportedCallbackException var28) {
                                 throw new SaslException("DIGEST-MD5: Cannot perform callback to acquire password", var28);
                              } catch (IOException var29) {
                                 throw new SaslException("DIGEST-MD5: IO error acquiring password", var29);
                              }

                              if (var11 == null) {
                                 throw new SaslException("DIGEST-MD5: cannot acquire password for " + var3 + " in realm : " + this.negotiatedRealm);
                              } else {
                                 boolean var25 = false;

                                 byte[] var39;
                                 try {
                                    byte[] var37;
                                    try {
                                       var25 = true;
                                       var37 = this.generateResponseValue("AUTHENTICATE", this.digestUri, this.negotiatedQop, var3, this.negotiatedRealm, var11, this.nonce, var5, 1, var9);
                                    } catch (NoSuchAlgorithmException var26) {
                                       throw new SaslException("DIGEST-MD5: problem duplicating client response", var26);
                                    } catch (IOException var27) {
                                       throw new SaslException("DIGEST-MD5: problem duplicating client response", var27);
                                    }

                                    if (!Arrays.equals(var36, var37)) {
                                       throw new SaslException("DIGEST-MD5: digest response format violation. Mismatched response.");
                                    }

                                    try {
                                       AuthorizeCallback var38 = new AuthorizeCallback(var3, var10);
                                       this.cbh.handle(new Callback[]{var38});
                                       if (!var38.isAuthorized()) {
                                          throw new SaslException("DIGEST-MD5: " + var3 + " is not authorized to act as " + var10);
                                       }

                                       this.authzid = var38.getAuthorizedID();
                                    } catch (SaslException var30) {
                                       throw var30;
                                    } catch (UnsupportedCallbackException var31) {
                                       throw new SaslException("DIGEST-MD5: Cannot perform callback to check authzid", var31);
                                    } catch (IOException var32) {
                                       throw new SaslException("DIGEST-MD5: IO error checking authzid", var32);
                                    }

                                    var39 = this.generateResponseAuth(var3, var11, var5, 1, var9);
                                    var25 = false;
                                 } finally {
                                    if (var25) {
                                       for(int var16 = 0; var16 < var11.length; ++var16) {
                                          var11[var16] = 0;
                                       }

                                    }
                                 }

                                 for(int var40 = 0; var40 < var11.length; ++var40) {
                                    var11[var40] = 0;
                                 }

                                 return var39;
                              }
                           }
                        }
                     }
                  }
               }
            }
         } else {
            throw new SaslException("DIGEST-MD5: digest response format violation. Missing username.");
         }
      } else {
         throw new SaslException("DIGEST-MD5: digest response format violation. Incompatible charset value: " + new String(var1[9]));
      }
   }

   private static boolean uriMatches(String var0, String var1) {
      if (var0.equalsIgnoreCase(var1)) {
         return true;
      } else if (var0.endsWith("/*")) {
         int var2 = var0.length() - 1;
         String var3 = var0.substring(0, var2);
         String var4 = var1.substring(0, var2);
         return var3.equalsIgnoreCase(var4);
      } else {
         return false;
      }
   }

   private byte[] generateResponseAuth(String var1, char[] var2, byte[] var3, int var4, byte[] var5) throws SaslException {
      try {
         byte[] var6 = this.generateResponseValue("", this.digestUri, this.negotiatedQop, var1, this.negotiatedRealm, var2, this.nonce, var3, var4, var5);
         byte[] var7 = new byte[var6.length + 8];
         System.arraycopy("rspauth=".getBytes(this.encoding), 0, var7, 0, 8);
         System.arraycopy(var6, 0, var7, 8, var6.length);
         return var7;
      } catch (NoSuchAlgorithmException var8) {
         throw new SaslException("DIGEST-MD5: problem generating response", var8);
      } catch (IOException var9) {
         throw new SaslException("DIGEST-MD5: problem generating response", var9);
      }
   }

   public String getAuthorizationID() {
      if (this.completed) {
         return this.authzid;
      } else {
         throw new IllegalStateException("DIGEST-MD5 server negotiation not complete");
      }
   }
}
