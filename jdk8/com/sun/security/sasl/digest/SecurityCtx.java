package com.sun.security.sasl.digest;

import javax.security.sasl.SaslException;

interface SecurityCtx {
   byte[] wrap(byte[] var1, int var2, int var3) throws SaslException;

   byte[] unwrap(byte[] var1, int var2, int var3) throws SaslException;
}
