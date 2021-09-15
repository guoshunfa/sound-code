package com.sun.org.apache.xml.internal.serialize;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class IndentPrinter extends Printer {
   private StringBuffer _line = new StringBuffer(80);
   private StringBuffer _text = new StringBuffer(20);
   private int _spaces = 0;
   private int _thisIndent;
   private int _nextIndent;

   public IndentPrinter(Writer writer, OutputFormat format) {
      super(writer, format);
      this._thisIndent = this._nextIndent = 0;
   }

   public void enterDTD() {
      if (this._dtdWriter == null) {
         this._line.append(this._text);
         this._text = new StringBuffer(20);
         this.flushLine(false);
         this._dtdWriter = new StringWriter();
         this._docWriter = this._writer;
         this._writer = this._dtdWriter;
      }

   }

   public String leaveDTD() {
      if (this._writer == this._dtdWriter) {
         this._line.append(this._text);
         this._text = new StringBuffer(20);
         this.flushLine(false);
         this._writer = this._docWriter;
         return this._dtdWriter.toString();
      } else {
         return null;
      }
   }

   public void printText(String text) {
      this._text.append(text);
   }

   public void printText(StringBuffer text) {
      this._text.append(text.toString());
   }

   public void printText(char ch) {
      this._text.append(ch);
   }

   public void printText(char[] chars, int start, int length) {
      this._text.append(chars, start, length);
   }

   public void printSpace() {
      if (this._text.length() > 0) {
         if (this._format.getLineWidth() > 0 && this._thisIndent + this._line.length() + this._spaces + this._text.length() > this._format.getLineWidth()) {
            this.flushLine(false);

            try {
               this._writer.write(this._format.getLineSeparator());
            } catch (IOException var2) {
               if (this._exception == null) {
                  this._exception = var2;
               }
            }
         }

         while(this._spaces > 0) {
            this._line.append(' ');
            --this._spaces;
         }

         this._line.append(this._text);
         this._text = new StringBuffer(20);
      }

      ++this._spaces;
   }

   public void breakLine() {
      this.breakLine(false);
   }

   public void breakLine(boolean preserveSpace) {
      if (this._text.length() > 0) {
         while(true) {
            if (this._spaces <= 0) {
               this._line.append(this._text);
               this._text = new StringBuffer(20);
               break;
            }

            this._line.append(' ');
            --this._spaces;
         }
      }

      this.flushLine(preserveSpace);

      try {
         this._writer.write(this._format.getLineSeparator());
      } catch (IOException var3) {
         if (this._exception == null) {
            this._exception = var3;
         }
      }

   }

   public void flushLine(boolean preserveSpace) {
      if (this._line.length() > 0) {
         try {
            if (this._format.getIndenting() && !preserveSpace) {
               int indent = this._thisIndent;
               if (2 * indent > this._format.getLineWidth() && this._format.getLineWidth() > 0) {
                  indent = this._format.getLineWidth() / 2;
               }

               while(indent > 0) {
                  this._writer.write(32);
                  --indent;
               }
            }

            this._thisIndent = this._nextIndent;
            this._spaces = 0;
            this._writer.write(this._line.toString());
            this._line = new StringBuffer(40);
         } catch (IOException var4) {
            if (this._exception == null) {
               this._exception = var4;
            }
         }
      }

   }

   public void flush() {
      if (this._line.length() > 0 || this._text.length() > 0) {
         this.breakLine();
      }

      try {
         this._writer.flush();
      } catch (IOException var2) {
         if (this._exception == null) {
            this._exception = var2;
         }
      }

   }

   public void indent() {
      this._nextIndent += this._format.getIndent();
   }

   public void unindent() {
      this._nextIndent -= this._format.getIndent();
      if (this._nextIndent < 0) {
         this._nextIndent = 0;
      }

      if (this._line.length() + this._spaces + this._text.length() == 0) {
         this._thisIndent = this._nextIndent;
      }

   }

   public int getNextIndent() {
      return this._nextIndent;
   }

   public void setNextIndent(int indent) {
      this._nextIndent = indent;
   }

   public void setThisIndent(int indent) {
      this._thisIndent = indent;
   }
}
