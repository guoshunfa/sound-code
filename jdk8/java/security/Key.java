package java.security;

import java.io.Serializable;

public interface Key extends Serializable {
   long serialVersionUID = 6603384152749567654L;

   String getAlgorithm();

   String getFormat();

   byte[] getEncoded();
}
