package javax.naming;

public class StringRefAddr extends RefAddr {
   private String contents;
   private static final long serialVersionUID = -8913762495138505527L;

   public StringRefAddr(String var1, String var2) {
      super(var1);
      this.contents = var2;
   }

   public Object getContent() {
      return this.contents;
   }
}
