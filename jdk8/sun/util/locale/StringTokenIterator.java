package sun.util.locale;

public class StringTokenIterator {
   private String text;
   private String dlms;
   private char delimiterChar;
   private String token;
   private int start;
   private int end;
   private boolean done;

   public StringTokenIterator(String var1, String var2) {
      this.text = var1;
      if (var2.length() == 1) {
         this.delimiterChar = var2.charAt(0);
      } else {
         this.dlms = var2;
      }

      this.setStart(0);
   }

   public String first() {
      this.setStart(0);
      return this.token;
   }

   public String current() {
      return this.token;
   }

   public int currentStart() {
      return this.start;
   }

   public int currentEnd() {
      return this.end;
   }

   public boolean isDone() {
      return this.done;
   }

   public String next() {
      if (this.hasNext()) {
         this.start = this.end + 1;
         this.end = this.nextDelimiter(this.start);
         this.token = this.text.substring(this.start, this.end);
      } else {
         this.start = this.end;
         this.token = null;
         this.done = true;
      }

      return this.token;
   }

   public boolean hasNext() {
      return this.end < this.text.length();
   }

   public StringTokenIterator setStart(int var1) {
      if (var1 > this.text.length()) {
         throw new IndexOutOfBoundsException();
      } else {
         this.start = var1;
         this.end = this.nextDelimiter(this.start);
         this.token = this.text.substring(this.start, this.end);
         this.done = false;
         return this;
      }
   }

   public StringTokenIterator setText(String var1) {
      this.text = var1;
      this.setStart(0);
      return this;
   }

   private int nextDelimiter(int var1) {
      int var2 = this.text.length();
      int var3;
      if (this.dlms == null) {
         for(var3 = var1; var3 < var2; ++var3) {
            if (this.text.charAt(var3) == this.delimiterChar) {
               return var3;
            }
         }
      } else {
         var3 = this.dlms.length();

         for(int var4 = var1; var4 < var2; ++var4) {
            char var5 = this.text.charAt(var4);

            for(int var6 = 0; var6 < var3; ++var6) {
               if (var5 == this.dlms.charAt(var6)) {
                  return var4;
               }
            }
         }
      }

      return var2;
   }
}
