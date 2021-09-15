package sun.reflect;

public class SignatureIterator {
   private final String sig;
   private int idx;

   public SignatureIterator(String var1) {
      this.sig = var1;
      this.reset();
   }

   public void reset() {
      this.idx = 1;
   }

   public boolean atEnd() {
      return this.sig.charAt(this.idx) == ')';
   }

   public String next() {
      if (this.atEnd()) {
         return null;
      } else {
         char var1 = this.sig.charAt(this.idx);
         if (var1 != '[' && var1 != 'L') {
            ++this.idx;
            return new String(new char[]{var1});
         } else {
            int var2 = this.idx;
            if (var1 == '[') {
               while((var1 = this.sig.charAt(var2)) == '[') {
                  ++var2;
               }
            }

            if (var1 == 'L') {
               while(this.sig.charAt(var2) != ';') {
                  ++var2;
               }
            }

            int var3 = this.idx;
            this.idx = var2 + 1;
            return this.sig.substring(var3, this.idx);
         }
      }
   }

   public String returnType() {
      if (!this.atEnd()) {
         throw new InternalError("Illegal use of SignatureIterator");
      } else {
         return this.sig.substring(this.idx + 1, this.sig.length());
      }
   }
}
