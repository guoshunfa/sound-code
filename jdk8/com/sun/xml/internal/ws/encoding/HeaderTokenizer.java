package com.sun.xml.internal.ws.encoding;

import javax.xml.ws.WebServiceException;

class HeaderTokenizer {
   private String string;
   private boolean skipComments;
   private String delimiters;
   private int currentPos;
   private int maxPos;
   private int nextPos;
   private int peekPos;
   private static final String RFC822 = "()<>@,;:\\\"\t .[]";
   static final String MIME = "()<>@,;:\\\"\t []/?=";
   private static final HeaderTokenizer.Token EOFToken = new HeaderTokenizer.Token(-4, (String)null);

   HeaderTokenizer(String header, String delimiters, boolean skipComments) {
      this.string = header == null ? "" : header;
      this.skipComments = skipComments;
      this.delimiters = delimiters;
      this.currentPos = this.nextPos = this.peekPos = 0;
      this.maxPos = this.string.length();
   }

   HeaderTokenizer(String header, String delimiters) {
      this(header, delimiters, true);
   }

   HeaderTokenizer(String header) {
      this(header, "()<>@,;:\\\"\t .[]");
   }

   HeaderTokenizer.Token next() throws WebServiceException {
      this.currentPos = this.nextPos;
      HeaderTokenizer.Token tk = this.getNext();
      this.nextPos = this.peekPos = this.currentPos;
      return tk;
   }

   HeaderTokenizer.Token peek() throws WebServiceException {
      this.currentPos = this.peekPos;
      HeaderTokenizer.Token tk = this.getNext();
      this.peekPos = this.currentPos;
      return tk;
   }

   String getRemainder() {
      return this.string.substring(this.nextPos);
   }

   private HeaderTokenizer.Token getNext() throws WebServiceException {
      if (this.currentPos >= this.maxPos) {
         return EOFToken;
      } else if (this.skipWhiteSpace() == -4) {
         return EOFToken;
      } else {
         boolean filter = false;

         char c;
         int start;
         for(c = this.string.charAt(this.currentPos); c == '('; c = this.string.charAt(this.currentPos)) {
            start = ++this.currentPos;

            int nesting;
            for(nesting = 1; nesting > 0 && this.currentPos < this.maxPos; ++this.currentPos) {
               c = this.string.charAt(this.currentPos);
               if (c == '\\') {
                  ++this.currentPos;
                  filter = true;
               } else if (c == '\r') {
                  filter = true;
               } else if (c == '(') {
                  ++nesting;
               } else if (c == ')') {
                  --nesting;
               }
            }

            if (nesting != 0) {
               throw new WebServiceException("Unbalanced comments");
            }

            if (!this.skipComments) {
               String s;
               if (filter) {
                  s = filterToken(this.string, start, this.currentPos - 1);
               } else {
                  s = this.string.substring(start, this.currentPos - 1);
               }

               return new HeaderTokenizer.Token(-3, s);
            }

            if (this.skipWhiteSpace() == -4) {
               return EOFToken;
            }
         }

         if (c == '"') {
            for(start = ++this.currentPos; this.currentPos < this.maxPos; ++this.currentPos) {
               c = this.string.charAt(this.currentPos);
               if (c == '\\') {
                  ++this.currentPos;
                  filter = true;
               } else if (c == '\r') {
                  filter = true;
               } else if (c == '"') {
                  ++this.currentPos;
                  String s;
                  if (filter) {
                     s = filterToken(this.string, start, this.currentPos - 1);
                  } else {
                     s = this.string.substring(start, this.currentPos - 1);
                  }

                  return new HeaderTokenizer.Token(-2, s);
               }
            }

            throw new WebServiceException("Unbalanced quoted string");
         } else if (c >= ' ' && c < 127 && this.delimiters.indexOf(c) < 0) {
            for(start = this.currentPos; this.currentPos < this.maxPos; ++this.currentPos) {
               c = this.string.charAt(this.currentPos);
               if (c < ' ' || c >= 127 || c == '(' || c == ' ' || c == '"' || this.delimiters.indexOf(c) >= 0) {
                  break;
               }
            }

            return new HeaderTokenizer.Token(-1, this.string.substring(start, this.currentPos));
         } else {
            ++this.currentPos;
            char[] ch = new char[]{c};
            return new HeaderTokenizer.Token(c, new String(ch));
         }
      }
   }

   private int skipWhiteSpace() {
      while(this.currentPos < this.maxPos) {
         char c;
         if ((c = this.string.charAt(this.currentPos)) != ' ' && c != '\t' && c != '\r' && c != '\n') {
            return this.currentPos;
         }

         ++this.currentPos;
      }

      return -4;
   }

   private static String filterToken(String s, int start, int end) {
      StringBuffer sb = new StringBuffer();
      boolean gotEscape = false;
      boolean gotCR = false;

      for(int i = start; i < end; ++i) {
         char c = s.charAt(i);
         if (c == '\n' && gotCR) {
            gotCR = false;
         } else {
            gotCR = false;
            if (!gotEscape) {
               if (c == '\\') {
                  gotEscape = true;
               } else if (c == '\r') {
                  gotCR = true;
               } else {
                  sb.append(c);
               }
            } else {
               sb.append(c);
               gotEscape = false;
            }
         }
      }

      return sb.toString();
   }

   static class Token {
      private int type;
      private String value;
      public static final int ATOM = -1;
      public static final int QUOTEDSTRING = -2;
      public static final int COMMENT = -3;
      public static final int EOF = -4;

      public Token(int type, String value) {
         this.type = type;
         this.value = value;
      }

      public int getType() {
         return this.type;
      }

      public String getValue() {
         return this.value;
      }
   }
}
