package java.security;

import java.nio.ByteBuffer;
import java.security.spec.AlgorithmParameterSpec;
import sun.security.jca.JCAUtil;

public abstract class SignatureSpi {
   protected SecureRandom appRandom = null;

   protected abstract void engineInitVerify(PublicKey var1) throws InvalidKeyException;

   protected abstract void engineInitSign(PrivateKey var1) throws InvalidKeyException;

   protected void engineInitSign(PrivateKey var1, SecureRandom var2) throws InvalidKeyException {
      this.appRandom = var2;
      this.engineInitSign(var1);
   }

   protected abstract void engineUpdate(byte var1) throws SignatureException;

   protected abstract void engineUpdate(byte[] var1, int var2, int var3) throws SignatureException;

   protected void engineUpdate(ByteBuffer var1) {
      if (var1.hasRemaining()) {
         try {
            int var4;
            if (var1.hasArray()) {
               byte[] var2 = var1.array();
               int var3 = var1.arrayOffset();
               var4 = var1.position();
               int var5 = var1.limit();
               this.engineUpdate(var2, var3 + var4, var5 - var4);
               var1.position(var5);
            } else {
               int var7 = var1.remaining();

               for(byte[] var8 = new byte[JCAUtil.getTempArraySize(var7)]; var7 > 0; var7 -= var4) {
                  var4 = Math.min(var7, var8.length);
                  var1.get(var8, 0, var4);
                  this.engineUpdate(var8, 0, var4);
               }
            }

         } catch (SignatureException var6) {
            throw new ProviderException("update() failed", var6);
         }
      }
   }

   protected abstract byte[] engineSign() throws SignatureException;

   protected int engineSign(byte[] var1, int var2, int var3) throws SignatureException {
      byte[] var4 = this.engineSign();
      if (var3 < var4.length) {
         throw new SignatureException("partial signatures not returned");
      } else if (var1.length - var2 < var4.length) {
         throw new SignatureException("insufficient space in the output buffer to store the signature");
      } else {
         System.arraycopy(var4, 0, var1, var2, var4.length);
         return var4.length;
      }
   }

   protected abstract boolean engineVerify(byte[] var1) throws SignatureException;

   protected boolean engineVerify(byte[] var1, int var2, int var3) throws SignatureException {
      byte[] var4 = new byte[var3];
      System.arraycopy(var1, var2, var4, 0, var3);
      return this.engineVerify(var4);
   }

   /** @deprecated */
   @Deprecated
   protected abstract void engineSetParameter(String var1, Object var2) throws InvalidParameterException;

   protected void engineSetParameter(AlgorithmParameterSpec var1) throws InvalidAlgorithmParameterException {
      throw new UnsupportedOperationException();
   }

   protected AlgorithmParameters engineGetParameters() {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   protected abstract Object engineGetParameter(String var1) throws InvalidParameterException;

   public Object clone() throws CloneNotSupportedException {
      if (this instanceof Cloneable) {
         return super.clone();
      } else {
         throw new CloneNotSupportedException();
      }
   }
}
