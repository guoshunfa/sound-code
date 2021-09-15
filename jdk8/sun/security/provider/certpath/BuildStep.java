package sun.security.provider.certpath;

import java.security.cert.X509Certificate;

public class BuildStep {
   private Vertex vertex;
   private X509Certificate cert;
   private Throwable throwable;
   private int result;
   public static final int POSSIBLE = 1;
   public static final int BACK = 2;
   public static final int FOLLOW = 3;
   public static final int FAIL = 4;
   public static final int SUCCEED = 5;

   public BuildStep(Vertex var1, int var2) {
      this.vertex = var1;
      if (this.vertex != null) {
         this.cert = this.vertex.getCertificate();
         this.throwable = this.vertex.getThrowable();
      }

      this.result = var2;
   }

   public Vertex getVertex() {
      return this.vertex;
   }

   public X509Certificate getCertificate() {
      return this.cert;
   }

   public String getIssuerName() {
      return this.getIssuerName((String)null);
   }

   public String getIssuerName(String var1) {
      return this.cert == null ? var1 : this.cert.getIssuerX500Principal().toString();
   }

   public String getSubjectName() {
      return this.getSubjectName((String)null);
   }

   public String getSubjectName(String var1) {
      return this.cert == null ? var1 : this.cert.getSubjectX500Principal().toString();
   }

   public Throwable getThrowable() {
      return this.throwable;
   }

   public int getResult() {
      return this.result;
   }

   public String resultToString(int var1) {
      String var2 = "";
      switch(var1) {
      case 1:
         var2 = "Certificate to be tried.\n";
         break;
      case 2:
         var2 = "Certificate backed out since path does not satisfy build requirements.\n";
         break;
      case 3:
         var2 = "Certificate satisfies conditions.\n";
         break;
      case 4:
         var2 = "Certificate backed out since path does not satisfy conditions.\n";
         break;
      case 5:
         var2 = "Certificate satisfies conditions.\n";
         break;
      default:
         var2 = "Internal error: Invalid step result value.\n";
      }

      return var2;
   }

   public String toString() {
      String var1 = "Internal Error\n";
      switch(this.result) {
      case 1:
      case 3:
      case 5:
         var1 = this.resultToString(this.result);
         break;
      case 2:
      case 4:
         var1 = this.resultToString(this.result);
         var1 = var1 + this.vertex.throwableToString();
         break;
      default:
         var1 = "Internal Error: Invalid step result\n";
      }

      return var1;
   }

   public String verboseToString() {
      String var1 = this.resultToString(this.getResult());
      switch(this.result) {
      case 1:
      default:
         break;
      case 2:
      case 4:
         var1 = var1 + this.vertex.throwableToString();
         break;
      case 3:
      case 5:
         var1 = var1 + this.vertex.moreToString();
      }

      var1 = var1 + "Certificate contains:\n" + this.vertex.certToString();
      return var1;
   }

   public String fullToString() {
      return this.resultToString(this.getResult()) + this.vertex.toString();
   }
}
