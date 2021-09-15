package javax.swing.text.html.parser;

import java.io.IOException;
import java.io.Reader;
import javax.swing.text.ChangedCharSetException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;

public class DocumentParser extends Parser {
   private int inbody;
   private int intitle;
   private int inhead;
   private int instyle;
   private int inscript;
   private boolean seentitle;
   private HTMLEditorKit.ParserCallback callback = null;
   private boolean ignoreCharSet = false;
   private static final boolean debugFlag = false;

   public DocumentParser(DTD var1) {
      super(var1);
   }

   public void parse(Reader var1, HTMLEditorKit.ParserCallback var2, boolean var3) throws IOException {
      this.ignoreCharSet = var3;
      this.callback = var2;
      this.parse(var1);
      var2.handleEndOfLineString(this.getEndOfLineString());
   }

   protected void handleStartTag(TagElement var1) {
      Element var2 = var1.getElement();
      if (var2 == this.dtd.body) {
         ++this.inbody;
      } else if (var2 != this.dtd.html) {
         if (var2 == this.dtd.head) {
            ++this.inhead;
         } else if (var2 == this.dtd.title) {
            ++this.intitle;
         } else if (var2 == this.dtd.style) {
            ++this.instyle;
         } else if (var2 == this.dtd.script) {
            ++this.inscript;
         }
      }

      if (var1.fictional()) {
         SimpleAttributeSet var3 = new SimpleAttributeSet();
         var3.addAttribute(HTMLEditorKit.ParserCallback.IMPLIED, Boolean.TRUE);
         this.callback.handleStartTag(var1.getHTMLTag(), var3, this.getBlockStartPosition());
      } else {
         this.callback.handleStartTag(var1.getHTMLTag(), this.getAttributes(), this.getBlockStartPosition());
         this.flushAttributes();
      }

   }

   protected void handleComment(char[] var1) {
      this.callback.handleComment(var1, this.getBlockStartPosition());
   }

   protected void handleEmptyTag(TagElement var1) throws ChangedCharSetException {
      Element var2 = var1.getElement();
      SimpleAttributeSet var3;
      if (var2 == this.dtd.meta && !this.ignoreCharSet) {
         var3 = this.getAttributes();
         if (var3 != null) {
            String var4 = (String)var3.getAttribute(HTML.Attribute.CONTENT);
            if (var4 != null) {
               if ("content-type".equalsIgnoreCase((String)var3.getAttribute(HTML.Attribute.HTTPEQUIV))) {
                  if (!var4.equalsIgnoreCase("text/html") && !var4.equalsIgnoreCase("text/plain")) {
                     throw new ChangedCharSetException(var4, false);
                  }
               } else if ("charset".equalsIgnoreCase((String)var3.getAttribute(HTML.Attribute.HTTPEQUIV))) {
                  throw new ChangedCharSetException(var4, true);
               }
            }
         }
      }

      if (this.inbody != 0 || var2 == this.dtd.meta || var2 == this.dtd.base || var2 == this.dtd.isindex || var2 == this.dtd.style || var2 == this.dtd.link) {
         if (var1.fictional()) {
            var3 = new SimpleAttributeSet();
            var3.addAttribute(HTMLEditorKit.ParserCallback.IMPLIED, Boolean.TRUE);
            this.callback.handleSimpleTag(var1.getHTMLTag(), var3, this.getBlockStartPosition());
         } else {
            this.callback.handleSimpleTag(var1.getHTMLTag(), this.getAttributes(), this.getBlockStartPosition());
            this.flushAttributes();
         }
      }

   }

   protected void handleEndTag(TagElement var1) {
      Element var2 = var1.getElement();
      if (var2 == this.dtd.body) {
         --this.inbody;
      } else if (var2 == this.dtd.title) {
         --this.intitle;
         this.seentitle = true;
      } else if (var2 == this.dtd.head) {
         --this.inhead;
      } else if (var2 == this.dtd.style) {
         --this.instyle;
      } else if (var2 == this.dtd.script) {
         --this.inscript;
      }

      this.callback.handleEndTag(var1.getHTMLTag(), this.getBlockStartPosition());
   }

   protected void handleText(char[] var1) {
      if (var1 != null) {
         if (this.inscript != 0) {
            this.callback.handleComment(var1, this.getBlockStartPosition());
            return;
         }

         if (this.inbody != 0 || this.instyle != 0 || this.intitle != 0 && !this.seentitle) {
            this.callback.handleText(var1, this.getBlockStartPosition());
         }
      }

   }

   protected void handleError(int var1, String var2) {
      this.callback.handleError(var2, this.getCurrentPos());
   }

   private void debug(String var1) {
      System.out.println(var1);
   }
}
