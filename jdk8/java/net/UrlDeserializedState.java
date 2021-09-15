package java.net;

final class UrlDeserializedState {
   private final String protocol;
   private final String host;
   private final int port;
   private final String authority;
   private final String file;
   private final String ref;
   private final int hashCode;

   public UrlDeserializedState(String var1, String var2, int var3, String var4, String var5, String var6, int var7) {
      this.protocol = var1;
      this.host = var2;
      this.port = var3;
      this.authority = var4;
      this.file = var5;
      this.ref = var6;
      this.hashCode = var7;
   }

   String getProtocol() {
      return this.protocol;
   }

   String getHost() {
      return this.host;
   }

   String getAuthority() {
      return this.authority;
   }

   int getPort() {
      return this.port;
   }

   String getFile() {
      return this.file;
   }

   String getRef() {
      return this.ref;
   }

   int getHashCode() {
      return this.hashCode;
   }

   String reconstituteUrlString() {
      int var1 = this.protocol.length() + 1;
      if (this.authority != null && this.authority.length() > 0) {
         var1 += 2 + this.authority.length();
      }

      if (this.file != null) {
         var1 += this.file.length();
      }

      if (this.ref != null) {
         var1 += 1 + this.ref.length();
      }

      StringBuilder var2 = new StringBuilder(var1);
      var2.append(this.protocol);
      var2.append(":");
      if (this.authority != null && this.authority.length() > 0) {
         var2.append("//");
         var2.append(this.authority);
      }

      if (this.file != null) {
         var2.append(this.file);
      }

      if (this.ref != null) {
         var2.append("#");
         var2.append(this.ref);
      }

      return var2.toString();
   }
}
