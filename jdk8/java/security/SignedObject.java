package java.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class SignedObject implements Serializable {
   private static final long serialVersionUID = 720502720485447167L;
   private byte[] content;
   private byte[] signature;
   private String thealgorithm;

   public SignedObject(Serializable var1, PrivateKey var2, Signature var3) throws IOException, InvalidKeyException, SignatureException {
      ByteArrayOutputStream var4 = new ByteArrayOutputStream();
      ObjectOutputStream var5 = new ObjectOutputStream(var4);
      var5.writeObject(var1);
      var5.flush();
      var5.close();
      this.content = var4.toByteArray();
      var4.close();
      this.sign(var2, var3);
   }

   public Object getObject() throws IOException, ClassNotFoundException {
      ByteArrayInputStream var1 = new ByteArrayInputStream(this.content);
      ObjectInputStream var2 = new ObjectInputStream(var1);
      Object var3 = var2.readObject();
      var1.close();
      var2.close();
      return var3;
   }

   public byte[] getSignature() {
      return (byte[])this.signature.clone();
   }

   public String getAlgorithm() {
      return this.thealgorithm;
   }

   public boolean verify(PublicKey var1, Signature var2) throws InvalidKeyException, SignatureException {
      var2.initVerify(var1);
      var2.update((byte[])this.content.clone());
      return var2.verify((byte[])this.signature.clone());
   }

   private void sign(PrivateKey var1, Signature var2) throws InvalidKeyException, SignatureException {
      var2.initSign(var1);
      var2.update((byte[])this.content.clone());
      this.signature = (byte[])var2.sign().clone();
      this.thealgorithm = var2.getAlgorithm();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField var2 = var1.readFields();
      this.content = (byte[])((byte[])((byte[])var2.get("content", (Object)null))).clone();
      this.signature = (byte[])((byte[])((byte[])var2.get("signature", (Object)null))).clone();
      this.thealgorithm = (String)var2.get("thealgorithm", (Object)null);
   }
}
