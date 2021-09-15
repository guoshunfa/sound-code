package java.net;

public final class PasswordAuthentication {
   private String userName;
   private char[] password;

   public PasswordAuthentication(String var1, char[] var2) {
      this.userName = var1;
      this.password = (char[])var2.clone();
   }

   public String getUserName() {
      return this.userName;
   }

   public char[] getPassword() {
      return this.password;
   }
}
