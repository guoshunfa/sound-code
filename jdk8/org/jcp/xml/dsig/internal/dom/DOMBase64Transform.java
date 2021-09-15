package org.jcp.xml.dsig.internal.dom;

import java.security.InvalidAlgorithmParameterException;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

public final class DOMBase64Transform extends ApacheTransform {
   public void init(TransformParameterSpec var1) throws InvalidAlgorithmParameterException {
      if (var1 != null) {
         throw new InvalidAlgorithmParameterException("params must be null");
      }
   }
}
