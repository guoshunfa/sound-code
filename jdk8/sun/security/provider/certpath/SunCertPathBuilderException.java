package sun.security.provider.certpath;

import java.security.cert.CertPathBuilderException;

public class SunCertPathBuilderException extends CertPathBuilderException {
   private static final long serialVersionUID = -7814288414129264709L;
   private transient AdjacencyList adjList;

   public SunCertPathBuilderException() {
   }

   public SunCertPathBuilderException(String var1) {
      super(var1);
   }

   public SunCertPathBuilderException(Throwable var1) {
      super(var1);
   }

   public SunCertPathBuilderException(String var1, Throwable var2) {
      super(var1, var2);
   }

   SunCertPathBuilderException(String var1, AdjacencyList var2) {
      this(var1);
      this.adjList = var2;
   }

   SunCertPathBuilderException(String var1, Throwable var2, AdjacencyList var3) {
      this(var1, var2);
      this.adjList = var3;
   }

   public AdjacencyList getAdjacencyList() {
      return this.adjList;
   }
}
