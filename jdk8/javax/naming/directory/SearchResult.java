package javax.naming.directory;

import javax.naming.Binding;

public class SearchResult extends Binding {
   private Attributes attrs;
   private static final long serialVersionUID = -9158063327699723172L;

   public SearchResult(String var1, Object var2, Attributes var3) {
      super(var1, var2);
      this.attrs = var3;
   }

   public SearchResult(String var1, Object var2, Attributes var3, boolean var4) {
      super(var1, var2, var4);
      this.attrs = var3;
   }

   public SearchResult(String var1, String var2, Object var3, Attributes var4) {
      super(var1, var2, var3);
      this.attrs = var4;
   }

   public SearchResult(String var1, String var2, Object var3, Attributes var4, boolean var5) {
      super(var1, var2, var3, var5);
      this.attrs = var4;
   }

   public Attributes getAttributes() {
      return this.attrs;
   }

   public void setAttributes(Attributes var1) {
      this.attrs = var1;
   }

   public String toString() {
      return super.toString() + ":" + this.getAttributes();
   }
}
