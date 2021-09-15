package javax.swing.text.html.parser;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.text.AttributeSet;
import javax.swing.text.ChangedCharSetException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML;

public class Parser implements DTDConstants {
   private char[] text = new char[1024];
   private int textpos = 0;
   private TagElement last;
   private boolean space;
   private char[] str = new char[128];
   private int strpos = 0;
   protected DTD dtd = null;
   private int ch;
   private int ln;
   private Reader in;
   private Element recent;
   private TagStack stack;
   private boolean skipTag = false;
   private TagElement lastFormSent = null;
   private SimpleAttributeSet attributes = new SimpleAttributeSet();
   private boolean seenHtml = false;
   private boolean seenHead = false;
   private boolean seenBody = false;
   private boolean ignoreSpace;
   protected boolean strict = false;
   private int crlfCount;
   private int crCount;
   private int lfCount;
   private int currentBlockStartPos;
   private int lastBlockStartPos;
   private static final char[] cp1252Map = new char[]{'‚', 'ƒ', '„', '…', '†', '‡', 'ˆ', '‰', 'Š', '‹', 'Œ', '\u008d', '\u008e', '\u008f', '\u0090', '‘', '’', '“', '”', '•', '–', '—', '˜', '™', 'š', '›', 'œ', '\u009d', '\u009e', 'Ÿ'};
   private static final String START_COMMENT = "<!--";
   private static final String END_COMMENT = "-->";
   private static final char[] SCRIPT_END_TAG = "</script>".toCharArray();
   private static final char[] SCRIPT_END_TAG_UPPER_CASE = "</SCRIPT>".toCharArray();
   private char[] buf = new char[1];
   private int pos;
   private int len;
   private int currentPosition;

   public Parser(DTD var1) {
      this.dtd = var1;
   }

   protected int getCurrentLine() {
      return this.ln;
   }

   int getBlockStartPosition() {
      return Math.max(0, this.lastBlockStartPos - 1);
   }

   protected TagElement makeTag(Element var1, boolean var2) {
      return new TagElement(var1, var2);
   }

   protected TagElement makeTag(Element var1) {
      return this.makeTag(var1, false);
   }

   protected SimpleAttributeSet getAttributes() {
      return this.attributes;
   }

   protected void flushAttributes() {
      this.attributes.removeAttributes((AttributeSet)this.attributes);
   }

   protected void handleText(char[] var1) {
   }

   protected void handleTitle(char[] var1) {
      this.handleText(var1);
   }

   protected void handleComment(char[] var1) {
   }

   protected void handleEOFInComment() {
      int var1 = this.strIndexOf('\n');
      if (var1 >= 0) {
         this.handleComment(this.getChars(0, var1));

         try {
            this.in.close();
            this.in = new CharArrayReader(this.getChars(var1 + 1));
            this.ch = 62;
         } catch (IOException var3) {
            this.error("ioexception");
         }

         this.resetStrBuffer();
      } else {
         this.error("eof.comment");
      }

   }

   protected void handleEmptyTag(TagElement var1) throws ChangedCharSetException {
   }

   protected void handleStartTag(TagElement var1) {
   }

   protected void handleEndTag(TagElement var1) {
   }

   protected void handleError(int var1, String var2) {
   }

   void handleText(TagElement var1) {
      if (var1.breaksFlow()) {
         this.space = false;
         if (!this.strict) {
            this.ignoreSpace = true;
         }
      }

      if (this.textpos == 0 && (!this.space || this.stack == null || this.last.breaksFlow() || !this.stack.advance(this.dtd.pcdata))) {
         this.last = var1;
         this.space = false;
         this.lastBlockStartPos = this.currentBlockStartPos;
      } else {
         char[] var2;
         if (this.space) {
            if (!this.ignoreSpace) {
               if (this.textpos + 1 > this.text.length) {
                  var2 = new char[this.text.length + 200];
                  System.arraycopy(this.text, 0, var2, 0, this.text.length);
                  this.text = var2;
               }

               this.text[this.textpos++] = ' ';
               if (!this.strict && !var1.getElement().isEmpty()) {
                  this.ignoreSpace = true;
               }
            }

            this.space = false;
         }

         var2 = new char[this.textpos];
         System.arraycopy(this.text, 0, var2, 0, this.textpos);
         if (var1.getElement().getName().equals("title")) {
            this.handleTitle(var2);
         } else {
            this.handleText(var2);
         }

         this.lastBlockStartPos = this.currentBlockStartPos;
         this.textpos = 0;
         this.last = var1;
         this.space = false;
      }
   }

   protected void error(String var1, String var2, String var3, String var4) {
      this.handleError(this.ln, var1 + " " + var2 + " " + var3 + " " + var4);
   }

   protected void error(String var1, String var2, String var3) {
      this.error(var1, var2, var3, "?");
   }

   protected void error(String var1, String var2) {
      this.error(var1, var2, "?", "?");
   }

   protected void error(String var1) {
      this.error(var1, "?", "?", "?");
   }

   protected void startTag(TagElement var1) throws ChangedCharSetException {
      Element var2 = var1.getElement();
      if (var2.isEmpty() && (this.last == null || this.last.breaksFlow()) && this.textpos == 0) {
         this.last = var1;
         this.space = false;
      } else {
         this.handleText(var1);
      }

      this.lastBlockStartPos = this.currentBlockStartPos;

      for(AttributeList var3 = var2.atts; var3 != null; var3 = var3.next) {
         if (var3.modifier == 2 && (this.attributes.isEmpty() || !this.attributes.isDefined(var3.name) && !this.attributes.isDefined(HTML.getAttributeKey(var3.name)))) {
            this.error("req.att ", var3.getName(), var2.getName());
         }
      }

      if (var2.isEmpty()) {
         this.handleEmptyTag(var1);
      } else {
         this.recent = var2;
         this.stack = new TagStack(var1, this.stack);
         this.handleStartTag(var1);
      }

   }

   protected void endTag(boolean var1) {
      this.handleText(this.stack.tag);
      if (var1 && !this.stack.elem.omitEnd()) {
         this.error("end.missing", this.stack.elem.getName());
      } else if (!this.stack.terminate()) {
         this.error("end.unexpected", this.stack.elem.getName());
      }

      this.handleEndTag(this.stack.tag);
      this.stack = this.stack.next;
      this.recent = this.stack != null ? this.stack.elem : null;
   }

   boolean ignoreElement(Element var1) {
      String var2 = this.stack.elem.getName();
      String var3 = var1.getName();
      if (var3.equals("html") && this.seenHtml || var3.equals("head") && this.seenHead || var3.equals("body") && this.seenBody) {
         return true;
      } else {
         if (var3.equals("dt") || var3.equals("dd")) {
            TagStack var4;
            for(var4 = this.stack; var4 != null && !var4.elem.getName().equals("dl"); var4 = var4.next) {
            }

            if (var4 == null) {
               return true;
            }
         }

         return var2.equals("table") && !var3.equals("#pcdata") && !var3.equals("input") || var3.equals("font") && (var2.equals("ul") || var2.equals("ol")) || var3.equals("meta") && this.stack != null || var3.equals("style") && this.seenBody || var2.equals("table") && var3.equals("a");
      }
   }

   protected void markFirstTime(Element var1) {
      String var2 = var1.getName();
      if (var2.equals("html")) {
         this.seenHtml = true;
      } else if (var2.equals("head")) {
         this.seenHead = true;
      } else if (var2.equals("body")) {
         if (this.buf.length == 1) {
            char[] var3 = new char[256];
            var3[0] = this.buf[0];
            this.buf = var3;
         }

         this.seenBody = true;
      }

   }

   boolean legalElementContext(Element var1) throws ChangedCharSetException {
      if (this.stack == null) {
         if (var1 != this.dtd.html) {
            this.startTag(this.makeTag(this.dtd.html, true));
            return this.legalElementContext(var1);
         } else {
            return true;
         }
      } else if (this.stack.advance(var1)) {
         this.markFirstTime(var1);
         return true;
      } else {
         boolean var2 = false;
         String var3 = this.stack.elem.getName();
         String var4 = var1.getName();
         if (!this.strict && (var3.equals("table") && var4.equals("td") || var3.equals("table") && var4.equals("th") || var3.equals("tr") && !var4.equals("tr"))) {
            var2 = true;
         }

         if (!this.strict && !var2 && (this.stack.elem.getName() != var1.getName() || var1.getName().equals("body")) && (this.skipTag = this.ignoreElement(var1))) {
            this.error("tag.ignore", var1.getName());
            return this.skipTag;
         } else {
            Element var13;
            TagElement var14;
            if (!this.strict && var3.equals("table") && !var4.equals("tr") && !var4.equals("td") && !var4.equals("th") && !var4.equals("caption")) {
               var13 = this.dtd.getElement("tr");
               var14 = this.makeTag(var13, true);
               this.legalTagContext(var14);
               this.startTag(var14);
               this.error("start.missing", var1.getName());
               return this.legalElementContext(var1);
            } else {
               if (!var2 && this.stack.terminate() && (!this.strict || this.stack.elem.omitEnd())) {
                  for(TagStack var5 = this.stack.next; var5 != null; var5 = var5.next) {
                     if (var5.advance(var1)) {
                        while(this.stack != var5) {
                           this.endTag(true);
                        }

                        return true;
                     }

                     if (!var5.terminate() || this.strict && !var5.elem.omitEnd()) {
                        break;
                     }
                  }
               }

               var13 = this.stack.first();
               if (var13 != null && (!this.strict || var13.omitStart()) && (var13 != this.dtd.head || var1 != this.dtd.pcdata)) {
                  var14 = this.makeTag(var13, true);
                  this.legalTagContext(var14);
                  this.startTag(var14);
                  if (!var13.omitStart()) {
                     this.error("start.missing", var1.getName());
                  }

                  return this.legalElementContext(var1);
               } else {
                  if (!this.strict) {
                     ContentModel var6 = this.stack.contentModel();
                     Vector var7 = new Vector();
                     if (var6 != null) {
                        var6.getElements(var7);
                        Iterator var8 = var7.iterator();

                        label126:
                        while(true) {
                           Element var9;
                           do {
                              if (!var8.hasNext()) {
                                 break label126;
                              }

                              var9 = (Element)var8.next();
                           } while(this.stack.excluded(var9.getIndex()));

                           boolean var10 = false;

                           for(AttributeList var11 = var9.getAttributes(); var11 != null; var11 = var11.next) {
                              if (var11.modifier == 2) {
                                 var10 = true;
                                 break;
                              }
                           }

                           if (!var10) {
                              ContentModel var15 = var9.getContent();
                              if (var15 != null && var15.first(var1)) {
                                 TagElement var12 = this.makeTag(var9, true);
                                 this.legalTagContext(var12);
                                 this.startTag(var12);
                                 this.error("start.missing", var9.getName());
                                 return this.legalElementContext(var1);
                              }
                           }
                        }
                     }
                  }

                  if (!this.stack.terminate() || this.stack.elem == this.dtd.body || this.strict && !this.stack.elem.omitEnd()) {
                     return false;
                  } else {
                     if (!this.stack.elem.omitEnd()) {
                        this.error("end.missing", var1.getName());
                     }

                     this.endTag(true);
                     return this.legalElementContext(var1);
                  }
               }
            }
         }
      }
   }

   void legalTagContext(TagElement var1) throws ChangedCharSetException {
      if (this.legalElementContext(var1.getElement())) {
         this.markFirstTime(var1.getElement());
      } else if (var1.breaksFlow() && this.stack != null && !this.stack.tag.breaksFlow()) {
         this.endTag(true);
         this.legalTagContext(var1);
      } else {
         for(TagStack var2 = this.stack; var2 != null; var2 = var2.next) {
            if (var2.tag.getElement() == this.dtd.head) {
               while(this.stack != var2) {
                  this.endTag(true);
               }

               this.endTag(true);
               this.legalTagContext(var1);
               return;
            }
         }

         this.error("tag.unexpected", var1.getElement().getName());
      }
   }

   void errorContext() throws ChangedCharSetException {
      while(this.stack != null && this.stack.tag.getElement() != this.dtd.body) {
         this.handleEndTag(this.stack.tag);
         this.stack = this.stack.next;
      }

      if (this.stack == null) {
         this.legalElementContext(this.dtd.body);
         this.startTag(this.makeTag(this.dtd.body, true));
      }

   }

   void addString(int var1) {
      if (this.strpos == this.str.length) {
         char[] var2 = new char[this.str.length + 128];
         System.arraycopy(this.str, 0, var2, 0, this.str.length);
         this.str = var2;
      }

      this.str[this.strpos++] = (char)var1;
   }

   String getString(int var1) {
      char[] var2 = new char[this.strpos - var1];
      System.arraycopy(this.str, var1, var2, 0, this.strpos - var1);
      this.strpos = var1;
      return new String(var2);
   }

   char[] getChars(int var1) {
      char[] var2 = new char[this.strpos - var1];
      System.arraycopy(this.str, var1, var2, 0, this.strpos - var1);
      this.strpos = var1;
      return var2;
   }

   char[] getChars(int var1, int var2) {
      char[] var3 = new char[var2 - var1];
      System.arraycopy(this.str, var1, var3, 0, var2 - var1);
      return var3;
   }

   void resetStrBuffer() {
      this.strpos = 0;
   }

   int strIndexOf(char var1) {
      for(int var2 = 0; var2 < this.strpos; ++var2) {
         if (this.str[var2] == var1) {
            return var2;
         }
      }

      return -1;
   }

   void skipSpace() throws IOException {
      while(true) {
         switch(this.ch) {
         case 9:
         case 32:
            this.ch = this.readCh();
            break;
         case 10:
            ++this.ln;
            this.ch = this.readCh();
            ++this.lfCount;
            break;
         case 13:
            ++this.ln;
            if ((this.ch = this.readCh()) == 10) {
               this.ch = this.readCh();
               ++this.crlfCount;
               break;
            }

            ++this.crCount;
            break;
         default:
            return;
         }
      }
   }

   boolean parseIdentifier(boolean var1) throws IOException {
      switch(this.ch) {
      case 65:
      case 66:
      case 67:
      case 68:
      case 69:
      case 70:
      case 71:
      case 72:
      case 73:
      case 74:
      case 75:
      case 76:
      case 77:
      case 78:
      case 79:
      case 80:
      case 81:
      case 82:
      case 83:
      case 84:
      case 85:
      case 86:
      case 87:
      case 88:
      case 89:
      case 90:
         if (var1) {
            this.ch = 97 + (this.ch - 65);
         }
         break;
      case 91:
      case 92:
      case 93:
      case 94:
      case 95:
      case 96:
      default:
         return false;
      case 97:
      case 98:
      case 99:
      case 100:
      case 101:
      case 102:
      case 103:
      case 104:
      case 105:
      case 106:
      case 107:
      case 108:
      case 109:
      case 110:
      case 111:
      case 112:
      case 113:
      case 114:
      case 115:
      case 116:
      case 117:
      case 118:
      case 119:
      case 120:
      case 121:
      case 122:
      }

      while(true) {
         this.addString(this.ch);
         switch(this.ch = this.readCh()) {
         case 45:
         case 46:
         case 48:
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55:
         case 56:
         case 57:
         case 95:
         case 97:
         case 98:
         case 99:
         case 100:
         case 101:
         case 102:
         case 103:
         case 104:
         case 105:
         case 106:
         case 107:
         case 108:
         case 109:
         case 110:
         case 111:
         case 112:
         case 113:
         case 114:
         case 115:
         case 116:
         case 117:
         case 118:
         case 119:
         case 120:
         case 121:
         case 122:
            break;
         case 47:
         case 58:
         case 59:
         case 60:
         case 61:
         case 62:
         case 63:
         case 64:
         case 91:
         case 92:
         case 93:
         case 94:
         case 96:
         default:
            return true;
         case 65:
         case 66:
         case 67:
         case 68:
         case 69:
         case 70:
         case 71:
         case 72:
         case 73:
         case 74:
         case 75:
         case 76:
         case 77:
         case 78:
         case 79:
         case 80:
         case 81:
         case 82:
         case 83:
         case 84:
         case 85:
         case 86:
         case 87:
         case 88:
         case 89:
         case 90:
            if (var1) {
               this.ch = 97 + (this.ch - 65);
            }
         }
      }
   }

   private char[] parseEntityReference() throws IOException {
      int var1 = this.strpos;
      if ((this.ch = this.readCh()) == 35) {
         int var2 = 0;
         this.ch = this.readCh();
         char[] var3;
         if (this.ch >= 48 && this.ch <= 57 || this.ch == 120 || this.ch == 88) {
            if (this.ch >= 48 && this.ch <= 57) {
               while(this.ch >= 48 && this.ch <= 57) {
                  var2 = var2 * 10 + this.ch - 48;
                  this.ch = this.readCh();
               }
            } else {
               this.ch = this.readCh();

               for(char var8 = (char)Character.toLowerCase(this.ch); var8 >= '0' && var8 <= '9' || var8 >= 'a' && var8 <= 'f'; var8 = (char)Character.toLowerCase(this.ch)) {
                  if (var8 >= '0' && var8 <= '9') {
                     var2 = var2 * 16 + var8 - 48;
                  } else {
                     var2 = var2 * 16 + var8 - 97 + 10;
                  }

                  this.ch = this.readCh();
               }
            }

            switch(this.ch) {
            case 10:
               ++this.ln;
               this.ch = this.readCh();
               ++this.lfCount;
               break;
            case 13:
               ++this.ln;
               if ((this.ch = this.readCh()) == 10) {
                  this.ch = this.readCh();
                  ++this.crlfCount;
               } else {
                  ++this.crCount;
               }
               break;
            case 59:
               this.ch = this.readCh();
            }

            var3 = this.mapNumericReference(var2);
            return var3;
         }

         this.addString(35);
         if (!this.parseIdentifier(false)) {
            this.error("ident.expected");
            this.strpos = var1;
            var3 = new char[]{'&', '#'};
            return var3;
         }
      } else if (!this.parseIdentifier(false)) {
         char[] var9 = new char[]{'&'};
         return var9;
      }

      boolean var7 = false;
      switch(this.ch) {
      case 10:
         ++this.ln;
         this.ch = this.readCh();
         ++this.lfCount;
         break;
      case 13:
         ++this.ln;
         if ((this.ch = this.readCh()) == 10) {
            this.ch = this.readCh();
            ++this.crlfCount;
         } else {
            ++this.crCount;
         }
         break;
      case 59:
         var7 = true;
         this.ch = this.readCh();
      }

      String var10 = this.getString(var1);
      Entity var4 = this.dtd.getEntity(var10);
      if (!this.strict && var4 == null) {
         var4 = this.dtd.getEntity(var10.toLowerCase());
      }

      if (var4 != null && var4.isGeneral()) {
         return var4.getData();
      } else if (var10.length() == 0) {
         this.error("invalid.entref", var10);
         return new char[0];
      } else {
         String var5 = "&" + var10 + (var7 ? ";" : "");
         char[] var6 = new char[var5.length()];
         var5.getChars(0, var6.length, var6, 0);
         return var6;
      }
   }

   private char[] mapNumericReference(int var1) {
      char[] var2;
      if (var1 >= 65535) {
         try {
            var2 = Character.toChars(var1);
         } catch (IllegalArgumentException var4) {
            var2 = new char[0];
         }
      } else {
         var2 = new char[]{var1 >= 130 && var1 <= 159 ? cp1252Map[var1 - 130] : (char)var1};
      }

      return var2;
   }

   void parseComment() throws IOException {
      while(true) {
         int var1;
         label57:
         while(true) {
            var1 = this.ch;
            switch(var1) {
            case -1:
               this.handleEOFInComment();
               return;
            case 10:
               ++this.ln;
               this.ch = this.readCh();
               ++this.lfCount;
               break label57;
            case 13:
               ++this.ln;
               if ((this.ch = this.readCh()) == 10) {
                  this.ch = this.readCh();
                  ++this.crlfCount;
               } else {
                  ++this.crCount;
               }

               var1 = 10;
               break label57;
            case 45:
               if (!this.strict && this.strpos != 0 && this.str[this.strpos - 1] == '-') {
                  if ((this.ch = this.readCh()) == 62) {
                     return;
                  }

                  if (this.ch == 33) {
                     if ((this.ch = this.readCh()) == 62) {
                        return;
                     }

                     this.addString(45);
                     this.addString(33);
                     break;
                  }
                  break label57;
               } else {
                  if ((this.ch = this.readCh()) != 45) {
                     break label57;
                  }

                  this.ch = this.readCh();
                  if (!this.strict && this.ch != 62) {
                     if (this.ch == 33) {
                        if ((this.ch = this.readCh()) == 62) {
                           return;
                        }

                        this.addString(45);
                        this.addString(33);
                        break;
                     }

                     this.addString(45);
                     break label57;
                  }

                  return;
               }
            case 62:
               this.ch = this.readCh();
               break label57;
            default:
               this.ch = this.readCh();
               break label57;
            }
         }

         this.addString(var1);
      }
   }

   void parseLiteral(boolean var1) throws IOException {
      while(true) {
         int var2 = this.ch;
         switch(var2) {
         case -1:
            this.error("eof.literal", this.stack.elem.getName());
            this.endTag(true);
            return;
         case 10:
            ++this.ln;
            this.ch = this.readCh();
            ++this.lfCount;
            break;
         case 13:
            ++this.ln;
            if ((this.ch = this.readCh()) == 10) {
               this.ch = this.readCh();
               ++this.crlfCount;
            } else {
               ++this.crCount;
            }

            var2 = 10;
            break;
         case 38:
            char[] var5 = this.parseEntityReference();
            if (this.textpos + var5.length > this.text.length) {
               char[] var6 = new char[Math.max(this.textpos + var5.length + 128, this.text.length * 2)];
               System.arraycopy(this.text, 0, var6, 0, this.text.length);
               this.text = var6;
            }

            System.arraycopy(var5, 0, this.text, this.textpos, var5.length);
            this.textpos += var5.length;
            continue;
         case 62:
            this.ch = this.readCh();
            int var3 = this.textpos - (this.stack.elem.name.length() + 2);
            int var4 = 0;
            if (var3 >= 0 && this.text[var3++] == '<' && this.text[var3] == '/') {
               do {
                  ++var3;
               } while(var3 < this.textpos && Character.toLowerCase(this.text[var3]) == this.stack.elem.name.charAt(var4++));

               if (var3 == this.textpos) {
                  this.textpos -= this.stack.elem.name.length() + 2;
                  if (this.textpos > 0 && this.text[this.textpos - 1] == '\n') {
                     --this.textpos;
                  }

                  this.endTag(false);
                  return;
               }
            }
            break;
         default:
            this.ch = this.readCh();
         }

         if (this.textpos == this.text.length) {
            char[] var7 = new char[this.text.length + 128];
            System.arraycopy(this.text, 0, var7, 0, this.text.length);
            this.text = var7;
         }

         this.text[this.textpos++] = (char)var2;
      }
   }

   String parseAttributeValue(boolean var1) throws IOException {
      int var2 = -1;
      switch(this.ch) {
      case 34:
      case 39:
         var2 = this.ch;
         this.ch = this.readCh();
      }

      while(true) {
         while(true) {
            int var3 = this.ch;
            switch(var3) {
            case -1:
               return this.getString(0);
            case 9:
               if (var2 < 0) {
                  var3 = 32;
               }
            case 32:
               this.ch = this.readCh();
               if (var2 < 0) {
                  return this.getString(0);
               }
               break;
            case 10:
               ++this.ln;
               this.ch = this.readCh();
               ++this.lfCount;
               if (var2 < 0) {
                  return this.getString(0);
               }
               break;
            case 13:
               ++this.ln;
               if ((this.ch = this.readCh()) == 10) {
                  this.ch = this.readCh();
                  ++this.crlfCount;
               } else {
                  ++this.crCount;
               }

               if (var2 < 0) {
                  return this.getString(0);
               }
               break;
            case 34:
            case 39:
               this.ch = this.readCh();
               if (var3 == var2) {
                  return this.getString(0);
               }

               if (var2 == -1) {
                  this.error("attvalerr");
                  if (!this.strict && this.ch != 32) {
                     continue;
                  }

                  return this.getString(0);
               }
               break;
            case 38:
               if (!this.strict || var2 >= 0) {
                  char[] var4 = this.parseEntityReference();

                  for(int var5 = 0; var5 < var4.length; ++var5) {
                     char var6 = var4[var5];
                     this.addString(var1 && var6 >= 'A' && var6 <= 'Z' ? 97 + var6 - 65 : var6);
                  }
                  continue;
               }

               this.ch = this.readCh();
               break;
            case 60:
            case 62:
               if (var2 < 0) {
                  return this.getString(0);
               }

               this.ch = this.readCh();
               break;
            case 61:
               if (var2 < 0) {
                  this.error("attvalerr");
                  if (this.strict) {
                     return this.getString(0);
                  }
               }

               this.ch = this.readCh();
               break;
            default:
               if (var1 && var3 >= 65 && var3 <= 90) {
                  var3 = 97 + var3 - 65;
               }

               this.ch = this.readCh();
            }

            this.addString(var3);
         }
      }
   }

   void parseAttributeSpecificationList(Element var1) throws IOException {
      while(true) {
         this.skipSpace();
         switch(this.ch) {
         case -1:
         case 47:
         case 60:
         case 62:
            return;
         case 45:
            if ((this.ch = this.readCh()) == 45) {
               this.ch = this.readCh();
               this.parseComment();
               this.strpos = 0;
               continue;
            }

            this.error("invalid.tagchar", "-", var1.getName());
            this.ch = this.readCh();
            continue;
         }

         AttributeList var2;
         String var3;
         String var4;
         if (this.parseIdentifier(true)) {
            var3 = this.getString(0);
            this.skipSpace();
            if (this.ch == 61) {
               this.ch = this.readCh();
               this.skipSpace();
               var2 = var1.getAttribute(var3);
               var4 = this.parseAttributeValue(var2 != null && var2.type != 1 && var2.type != 11 && var2.type != 7);
            } else {
               var4 = var3;
               var2 = var1.getAttributeByValue(var3);
               if (var2 == null) {
                  var2 = var1.getAttribute(var3);
                  if (var2 != null) {
                     var4 = var2.getValue();
                  } else {
                     var4 = null;
                  }
               }
            }
         } else {
            if (!this.strict && this.ch == 44) {
               this.ch = this.readCh();
               continue;
            }

            char[] var5;
            if (!this.strict && this.ch == 34) {
               this.ch = this.readCh();
               this.skipSpace();
               if (!this.parseIdentifier(true)) {
                  var5 = new char[]{(char)this.ch};
                  this.error("invalid.tagchar", new String(var5), var1.getName());
                  this.ch = this.readCh();
                  continue;
               }

               var3 = this.getString(0);
               if (this.ch == 34) {
                  this.ch = this.readCh();
               }

               this.skipSpace();
               if (this.ch == 61) {
                  this.ch = this.readCh();
                  this.skipSpace();
                  var2 = var1.getAttribute(var3);
                  var4 = this.parseAttributeValue(var2 != null && var2.type != 1 && var2.type != 11);
               } else {
                  var4 = var3;
                  var2 = var1.getAttributeByValue(var3);
                  if (var2 == null) {
                     var2 = var1.getAttribute(var3);
                     if (var2 != null) {
                        var4 = var2.getValue();
                     }
                  }
               }
            } else {
               if (this.strict || !this.attributes.isEmpty() || this.ch != 61) {
                  if (!this.strict && this.ch == 61) {
                     this.ch = this.readCh();
                     this.skipSpace();
                     var4 = this.parseAttributeValue(true);
                     this.error("attvalerr");
                     return;
                  }

                  var5 = new char[]{(char)this.ch};
                  this.error("invalid.tagchar", new String(var5), var1.getName());
                  if (!this.strict) {
                     this.ch = this.readCh();
                     continue;
                  }

                  return;
               }

               this.ch = this.readCh();
               this.skipSpace();
               var3 = var1.getName();
               var2 = var1.getAttribute(var3);
               var4 = this.parseAttributeValue(var2 != null && var2.type != 1 && var2.type != 11);
            }
         }

         if (var2 != null) {
            var3 = var2.getName();
         } else {
            this.error("invalid.tagatt", var3, var1.getName());
         }

         if (this.attributes.isDefined(var3)) {
            this.error("multi.tagatt", var3, var1.getName());
         }

         if (var4 == null) {
            var4 = var2 != null && var2.value != null ? var2.value : "#DEFAULT";
         } else if (var2 != null && var2.values != null && !var2.values.contains(var4)) {
            this.error("invalid.tagattval", var3, var1.getName());
         }

         HTML.Attribute var6 = HTML.getAttributeKey(var3);
         if (var6 == null) {
            this.attributes.addAttribute(var3, var4);
         } else {
            this.attributes.addAttribute(var6, var4);
         }
      }
   }

   public String parseDTDMarkup() throws IOException {
      StringBuilder var1 = new StringBuilder();
      this.ch = this.readCh();

      while(true) {
         switch(this.ch) {
         case -1:
            this.error("invalid.markup");
            return var1.toString();
         case 10:
            ++this.ln;
            this.ch = this.readCh();
            ++this.lfCount;
            break;
         case 13:
            ++this.ln;
            if ((this.ch = this.readCh()) == 10) {
               this.ch = this.readCh();
               ++this.crlfCount;
            } else {
               ++this.crCount;
            }
            break;
         case 34:
            this.ch = this.readCh();
            break;
         case 62:
            this.ch = this.readCh();
            return var1.toString();
         default:
            var1.append((char)(this.ch & 255));
            this.ch = this.readCh();
         }
      }
   }

   protected boolean parseMarkupDeclarations(StringBuffer var1) throws IOException {
      if (var1.length() == "DOCTYPE".length() && var1.toString().toUpperCase().equals("DOCTYPE")) {
         this.parseDTDMarkup();
         return true;
      } else {
         return false;
      }
   }

   void parseInvalidTag() throws IOException {
      while(true) {
         this.skipSpace();
         switch(this.ch) {
         case -1:
         case 62:
            this.ch = this.readCh();
            return;
         case 60:
            return;
         default:
            this.ch = this.readCh();
         }
      }
   }

   void parseTag() throws IOException {
      boolean var2 = false;
      boolean var3 = false;
      boolean var4 = false;
      Element var1;
      String var5;
      TagElement var8;
      switch(this.ch = this.readCh()) {
      case -1:
         this.error("eof");
         return;
      case 33:
         switch(this.ch = this.readCh()) {
         case 45:
            while(true) {
               while(true) {
                  if (this.ch == 45) {
                     if (!this.strict || (this.ch = this.readCh()) == 45) {
                        this.ch = this.readCh();
                        if (!this.strict && this.ch == 45) {
                           this.ch = this.readCh();
                        }

                        if (this.textpos != 0) {
                           char[] var9 = new char[this.textpos];
                           System.arraycopy(this.text, 0, var9, 0, this.textpos);
                           this.handleText(var9);
                           this.lastBlockStartPos = this.currentBlockStartPos;
                           this.textpos = 0;
                        }

                        this.parseComment();
                        this.last = this.makeTag(this.dtd.getElement("comment"), true);
                        this.handleComment(this.getChars(0));
                        continue;
                     }

                     if (!var3) {
                        var3 = true;
                        this.error("invalid.commentchar", "-");
                     }
                  }

                  this.skipSpace();
                  switch(this.ch) {
                  case 45:
                     break;
                  case 62:
                     this.ch = this.readCh();
                  case -1:
                     return;
                  default:
                     this.ch = this.readCh();
                     if (!var3) {
                        var3 = true;
                        this.error("invalid.commentchar", String.valueOf((char)this.ch));
                     }
                  }
               }
            }
         default:
            StringBuffer var10 = new StringBuffer();

            while(true) {
               var10.append((char)this.ch);
               if (this.parseMarkupDeclarations(var10)) {
                  return;
               }

               switch(this.ch) {
               case 10:
                  ++this.ln;
                  this.ch = this.readCh();
                  ++this.lfCount;
                  break;
               case 13:
                  ++this.ln;
                  if ((this.ch = this.readCh()) == 10) {
                     this.ch = this.readCh();
                     ++this.crlfCount;
                  } else {
                     ++this.crCount;
                  }
                  break;
               case 62:
                  this.ch = this.readCh();
               case -1:
                  this.error("invalid.markup");
                  return;
               default:
                  this.ch = this.readCh();
               }
            }
         }
      case 47:
         switch(this.ch = this.readCh()) {
         case 62:
            this.ch = this.readCh();
         case 60:
            if (this.recent == null) {
               this.error("invalid.shortend");
               return;
            }

            var1 = this.recent;
            break;
         default:
            if (!this.parseIdentifier(true)) {
               this.error("expected.endtagname");
               return;
            }

            label288: {
               this.skipSpace();
               switch(this.ch) {
               case 60:
                  break label288;
               case 62:
                  this.ch = this.readCh();
                  break label288;
               default:
                  this.error("expected", "'>'");
               }

               while(this.ch != -1 && this.ch != 10 && this.ch != 62) {
                  this.ch = this.readCh();
               }

               if (this.ch == 62) {
                  this.ch = this.readCh();
               }
            }

            var5 = this.getString(0);
            if (!this.dtd.elementExists(var5)) {
               this.error("end.unrecognized", var5);
               if (this.textpos > 0 && this.text[this.textpos - 1] == '\n') {
                  --this.textpos;
               }

               var1 = this.dtd.getElement("unknown");
               var1.name = var5;
               var4 = true;
            } else {
               var1 = this.dtd.getElement(var5);
            }
         }

         if (this.stack == null) {
            this.error("end.extra.tag", var1.getName());
            return;
         } else {
            if (this.textpos > 0 && this.text[this.textpos - 1] == '\n') {
               if (this.stack.pre) {
                  if (this.textpos > 1 && this.text[this.textpos - 2] != '\n') {
                     --this.textpos;
                  }
               } else {
                  --this.textpos;
               }
            }

            if (var4) {
               var8 = this.makeTag(var1);
               this.handleText(var8);
               this.attributes.addAttribute(HTML.Attribute.ENDTAG, "true");
               this.handleEmptyTag(this.makeTag(var1));
               var4 = false;
               return;
            } else {
               if (!this.strict) {
                  var5 = this.stack.elem.getName();
                  if (var5.equals("table") && !var1.getName().equals(var5)) {
                     this.error("tag.ignore", var1.getName());
                     return;
                  }

                  if ((var5.equals("tr") || var5.equals("td")) && !var1.getName().equals("table") && !var1.getName().equals(var5)) {
                     this.error("tag.ignore", var1.getName());
                     return;
                  }
               }

               TagStack var7;
               for(var7 = this.stack; var7 != null && var1 != var7.elem; var7 = var7.next) {
               }

               if (var7 == null) {
                  this.error("unmatched.endtag", var1.getName());
                  return;
               } else {
                  String var6 = var1.getName();
                  if (this.stack == var7 || !var6.equals("font") && !var6.equals("center")) {
                     while(this.stack != var7) {
                        this.endTag(true);
                     }

                     this.endTag(false);
                     return;
                  }

                  if (var6.equals("center")) {
                     while(this.stack.elem.omitEnd() && this.stack != var7) {
                        this.endTag(true);
                     }

                     if (this.stack.elem == var1) {
                        this.endTag(false);
                     }
                  }

                  return;
               }
            }
         }
      default:
         if (!this.parseIdentifier(true)) {
            var1 = this.recent;
            if (this.ch != 62 || var1 == null) {
               this.error("expected.tagname");
               return;
            }
         } else {
            var5 = this.getString(0);
            if (var5.equals("image")) {
               var5 = "img";
            }

            if (!this.dtd.elementExists(var5)) {
               this.error("tag.unrecognized ", var5);
               var1 = this.dtd.getElement("unknown");
               var1.name = var5;
               var4 = true;
            } else {
               var1 = this.dtd.getElement(var5);
            }
         }

         this.parseAttributeSpecificationList(var1);
         switch(this.ch) {
         case 47:
            var2 = true;
         case 62:
            this.ch = this.readCh();
            if (this.ch == 62 && var2) {
               this.ch = this.readCh();
            }
         case 60:
            break;
         default:
            this.error("expected", "'>'");
         }

         if (!this.strict && var1.getName().equals("script")) {
            this.error("javascript.unsupported");
         }

         if (!var1.isEmpty()) {
            if (this.ch == 10) {
               ++this.ln;
               ++this.lfCount;
               this.ch = this.readCh();
            } else if (this.ch == 13) {
               ++this.ln;
               if ((this.ch = this.readCh()) == 10) {
                  this.ch = this.readCh();
                  ++this.crlfCount;
               } else {
                  ++this.crCount;
               }
            }
         }

         var8 = this.makeTag(var1, false);
         if (!var4) {
            this.legalTagContext(var8);
            if (!this.strict && this.skipTag) {
               this.skipTag = false;
               return;
            }
         }

         this.startTag(var8);
         if (!var1.isEmpty()) {
            switch(var1.getType()) {
            case 1:
               this.parseLiteral(false);
               break;
            case 16:
               this.parseLiteral(true);
               break;
            default:
               if (this.stack != null) {
                  this.stack.net = var2;
               }
            }
         }

      }
   }

   void parseScript() throws IOException {
      char[] var1 = new char[SCRIPT_END_TAG.length];
      boolean var2 = false;

      while(true) {
         while(true) {
            int var3;
            for(var3 = 0; !var2 && var3 < SCRIPT_END_TAG.length && (SCRIPT_END_TAG[var3] == this.ch || SCRIPT_END_TAG_UPPER_CASE[var3] == this.ch); ++var3) {
               var1[var3] = (char)this.ch;
               this.ch = this.readCh();
            }

            if (var3 == SCRIPT_END_TAG.length) {
               return;
            }

            if (!var2 && var3 == 1 && var1[0] == "<!--".charAt(0)) {
               while(var3 < "<!--".length() && "<!--".charAt(var3) == this.ch) {
                  var1[var3] = (char)this.ch;
                  this.ch = this.readCh();
                  ++var3;
               }

               if (var3 == "<!--".length()) {
                  var2 = true;
               }
            }

            if (var2) {
               while(var3 < "-->".length() && "-->".charAt(var3) == this.ch) {
                  var1[var3] = (char)this.ch;
                  this.ch = this.readCh();
                  ++var3;
               }

               if (var3 == "-->".length()) {
                  var2 = false;
               }
            }

            if (var3 > 0) {
               for(int var4 = 0; var4 < var3; ++var4) {
                  this.addString(var1[var4]);
               }
            } else {
               switch(this.ch) {
               case -1:
                  this.error("eof.script");
                  return;
               case 10:
                  ++this.ln;
                  this.ch = this.readCh();
                  ++this.lfCount;
                  this.addString(10);
                  break;
               case 13:
                  ++this.ln;
                  if ((this.ch = this.readCh()) == 10) {
                     this.ch = this.readCh();
                     ++this.crlfCount;
                  } else {
                     ++this.crCount;
                  }

                  this.addString(10);
                  break;
               default:
                  this.addString(this.ch);
                  this.ch = this.readCh();
               }
            }
         }
      }
   }

   void parseContent() throws IOException {
      Thread var1 = Thread.currentThread();

      while(true) {
         while(!var1.isInterrupted()) {
            int var2 = this.ch;
            this.currentBlockStartPos = this.currentPosition;
            if (this.recent != this.dtd.script) {
               char[] var5;
               switch(var2) {
               case -1:
                  return;
               case 9:
               case 32:
                  this.ch = this.readCh();
                  if (this.stack == null || !this.stack.pre) {
                     if (this.textpos == 0) {
                        this.lastBlockStartPos = this.currentPosition;
                     }

                     if (!this.ignoreSpace) {
                        this.space = true;
                     }
                     continue;
                  }
                  break;
               case 10:
                  ++this.ln;
                  ++this.lfCount;
                  this.ch = this.readCh();
                  if (this.stack == null || !this.stack.pre) {
                     if (this.textpos == 0) {
                        this.lastBlockStartPos = this.currentPosition;
                     }

                     if (!this.ignoreSpace) {
                        this.space = true;
                     }
                     continue;
                  }
                  break;
               case 13:
                  ++this.ln;
                  var2 = 10;
                  if ((this.ch = this.readCh()) == 10) {
                     this.ch = this.readCh();
                     ++this.crlfCount;
                  } else {
                     ++this.crCount;
                  }

                  if (this.stack == null || !this.stack.pre) {
                     if (this.textpos == 0) {
                        this.lastBlockStartPos = this.currentPosition;
                     }

                     if (!this.ignoreSpace) {
                        this.space = true;
                     }
                     continue;
                  }
                  break;
               case 38:
                  if (this.textpos == 0) {
                     if (!this.legalElementContext(this.dtd.pcdata)) {
                        this.error("unexpected.pcdata");
                     }

                     if (this.last.breaksFlow()) {
                        this.space = false;
                     }
                  }

                  var5 = this.parseEntityReference();
                  if (this.textpos + var5.length + 1 > this.text.length) {
                     char[] var6 = new char[Math.max(this.textpos + var5.length + 128, this.text.length * 2)];
                     System.arraycopy(this.text, 0, var6, 0, this.text.length);
                     this.text = var6;
                  }

                  if (this.space) {
                     this.space = false;
                     this.text[this.textpos++] = ' ';
                  }

                  System.arraycopy(var5, 0, this.text, this.textpos, var5.length);
                  this.textpos += var5.length;
                  this.ignoreSpace = false;
                  continue;
               case 47:
                  this.ch = this.readCh();
                  if (this.stack != null && this.stack.net) {
                     this.endTag(false);
                     continue;
                  }

                  if (this.textpos == 0) {
                     if (!this.legalElementContext(this.dtd.pcdata)) {
                        this.error("unexpected.pcdata");
                     }

                     if (this.last.breaksFlow()) {
                        this.space = false;
                     }
                  }
                  break;
               case 60:
                  this.parseTag();
                  this.lastBlockStartPos = this.currentPosition;
                  continue;
               default:
                  if (this.textpos == 0) {
                     if (!this.legalElementContext(this.dtd.pcdata)) {
                        this.error("unexpected.pcdata");
                     }

                     if (this.last.breaksFlow()) {
                        this.space = false;
                     }
                  }

                  this.ch = this.readCh();
               }

               if (this.textpos + 2 > this.text.length) {
                  var5 = new char[this.text.length + 128];
                  System.arraycopy(this.text, 0, var5, 0, this.text.length);
                  this.text = var5;
               }

               if (this.space) {
                  if (this.textpos == 0) {
                     --this.lastBlockStartPos;
                  }

                  this.text[this.textpos++] = ' ';
                  this.space = false;
               }

               this.text[this.textpos++] = (char)var2;
               this.ignoreSpace = false;
            } else {
               this.parseScript();
               this.last = this.makeTag(this.dtd.getElement("comment"), true);
               String var3 = (new String(this.getChars(0))).trim();
               int var4 = "<!--".length() + "-->".length();
               if (var3.startsWith("<!--") && var3.endsWith("-->") && var3.length() >= var4) {
                  var3 = var3.substring("<!--".length(), var3.length() - "-->".length());
               }

               this.handleComment(var3.toCharArray());
               this.endTag(false);
               this.lastBlockStartPos = this.currentPosition;
            }
         }

         var1.interrupt();
         return;
      }
   }

   String getEndOfLineString() {
      if (this.crlfCount >= this.crCount) {
         return this.lfCount >= this.crlfCount ? "\n" : "\r\n";
      } else {
         return this.crCount > this.lfCount ? "\r" : "\n";
      }
   }

   public synchronized void parse(Reader var1) throws IOException {
      this.in = var1;
      this.ln = 1;
      this.seenHtml = false;
      this.seenHead = false;
      this.seenBody = false;
      this.crCount = this.lfCount = this.crlfCount = 0;

      try {
         this.ch = this.readCh();
         this.text = new char[1024];
         this.str = new char[128];
         this.parseContent();

         while(this.stack != null) {
            this.endTag(true);
         }

         var1.close();
      } catch (IOException var8) {
         this.errorContext();
         this.error("ioexception");
         throw var8;
      } catch (Exception var9) {
         this.errorContext();
         this.error("exception", var9.getClass().getName(), var9.getMessage());
         var9.printStackTrace();
      } catch (ThreadDeath var10) {
         this.errorContext();
         this.error("terminated");
         var10.printStackTrace();
         throw var10;
      } finally {
         while(this.stack != null) {
            this.handleEndTag(this.stack.tag);
            this.stack = this.stack.next;
         }

         this.text = null;
         this.str = null;
      }

   }

   private final int readCh() throws IOException {
      if (this.pos >= this.len) {
         try {
            this.len = this.in.read(this.buf);
         } catch (InterruptedIOException var2) {
            throw var2;
         }

         if (this.len <= 0) {
            return -1;
         }

         this.pos = 0;
      }

      ++this.currentPosition;
      return this.buf[this.pos++];
   }

   protected int getCurrentPos() {
      return this.currentPosition;
   }
}
