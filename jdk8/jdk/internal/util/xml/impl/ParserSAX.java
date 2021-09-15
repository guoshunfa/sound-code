package jdk.internal.util.xml.impl;

import java.io.IOException;
import java.io.InputStream;
import jdk.internal.org.xml.sax.ContentHandler;
import jdk.internal.org.xml.sax.DTDHandler;
import jdk.internal.org.xml.sax.EntityResolver;
import jdk.internal.org.xml.sax.ErrorHandler;
import jdk.internal.org.xml.sax.InputSource;
import jdk.internal.org.xml.sax.Locator;
import jdk.internal.org.xml.sax.SAXException;
import jdk.internal.org.xml.sax.SAXParseException;
import jdk.internal.org.xml.sax.XMLReader;
import jdk.internal.org.xml.sax.helpers.DefaultHandler;

final class ParserSAX extends Parser implements XMLReader, Locator {
   public static final String FEATURE_NS = "http://xml.org/sax/features/namespaces";
   public static final String FEATURE_PREF = "http://xml.org/sax/features/namespace-prefixes";
   private boolean mFNamespaces = true;
   private boolean mFPrefixes = false;
   private DefaultHandler mHand = new DefaultHandler();
   private ContentHandler mHandCont;
   private DTDHandler mHandDtd;
   private ErrorHandler mHandErr;
   private EntityResolver mHandEnt;

   public ParserSAX() {
      this.mHandCont = this.mHand;
      this.mHandDtd = this.mHand;
      this.mHandErr = this.mHand;
      this.mHandEnt = this.mHand;
   }

   public ContentHandler getContentHandler() {
      return this.mHandCont != this.mHand ? this.mHandCont : null;
   }

   public void setContentHandler(ContentHandler var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.mHandCont = var1;
      }
   }

   public DTDHandler getDTDHandler() {
      return this.mHandDtd != this.mHand ? this.mHandDtd : null;
   }

   public void setDTDHandler(DTDHandler var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.mHandDtd = var1;
      }
   }

   public ErrorHandler getErrorHandler() {
      return this.mHandErr != this.mHand ? this.mHandErr : null;
   }

   public void setErrorHandler(ErrorHandler var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.mHandErr = var1;
      }
   }

   public EntityResolver getEntityResolver() {
      return this.mHandEnt != this.mHand ? this.mHandEnt : null;
   }

   public void setEntityResolver(EntityResolver var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.mHandEnt = var1;
      }
   }

   public String getPublicId() {
      return this.mInp != null ? this.mInp.pubid : null;
   }

   public String getSystemId() {
      return this.mInp != null ? this.mInp.sysid : null;
   }

   public int getLineNumber() {
      return -1;
   }

   public int getColumnNumber() {
      return -1;
   }

   public void parse(String var1) throws IOException, SAXException {
      this.parse(new InputSource(var1));
   }

   public void parse(InputSource var1) throws IOException, SAXException {
      if (var1 == null) {
         throw new IllegalArgumentException("");
      } else {
         this.mInp = new Input(512);
         this.mPh = -1;

         try {
            this.setinp(var1);
         } catch (SAXException var3) {
            throw var3;
         } catch (IOException var4) {
            throw var4;
         } catch (RuntimeException var5) {
            throw var5;
         } catch (Exception var6) {
            this.panic(var6.toString());
         }

         this.parse();
      }
   }

   public void parse(InputStream var1, DefaultHandler var2) throws SAXException, IOException {
      if (var1 != null && var2 != null) {
         this.parse(new InputSource(var1), var2);
      } else {
         throw new IllegalArgumentException("");
      }
   }

   public void parse(InputSource var1, DefaultHandler var2) throws SAXException, IOException {
      if (var1 != null && var2 != null) {
         this.mHandCont = var2;
         this.mHandDtd = var2;
         this.mHandErr = var2;
         this.mHandEnt = var2;
         this.mInp = new Input(512);
         this.mPh = -1;

         try {
            this.setinp(var1);
         } catch (IOException | RuntimeException | SAXException var4) {
            throw var4;
         } catch (Exception var5) {
            this.panic(var5.toString());
         }

         this.parse();
      } else {
         throw new IllegalArgumentException("");
      }
   }

   private void parse() throws SAXException, IOException {
      this.init();

      try {
         this.mHandCont.setDocumentLocator(this);
         this.mHandCont.startDocument();
         if (this.mPh != 1) {
            this.mPh = 1;
         }

         boolean var1 = false;

         int var13;
         do {
            this.wsskip();
            switch(var13 = this.step()) {
            case 1:
            case 2:
               this.mPh = 4;
               break;
            case 3:
            case 4:
            case 5:
            case 7:
            default:
               this.panic("");
            case 6:
            case 8:
               break;
            case 9:
               if (this.mPh >= 3) {
                  this.panic("");
               }

               this.mPh = 3;
            }
         } while(this.mPh < 4);

         do {
            switch(var13) {
            case 1:
            case 2:
               if (this.mIsNSAware) {
                  this.mHandCont.startElement(this.mElm.value, this.mElm.name, "", this.mAttrs);
               } else {
                  this.mHandCont.startElement("", "", this.mElm.name, this.mAttrs);
               }

               if (var13 == 2) {
                  var13 = this.step();
                  continue;
               }
            case 3:
               if (this.mIsNSAware) {
                  this.mHandCont.endElement(this.mElm.value, this.mElm.name, "");
               } else {
                  this.mHandCont.endElement("", "", this.mElm.name);
               }
               break;
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 10:
               var13 = this.step();
               continue;
            case 9:
            default:
               this.panic("");
               continue;
            }

            while(this.mPref.list == this.mElm) {
               this.mHandCont.endPrefixMapping(this.mPref.name);
               this.mPref = this.del(this.mPref);
            }

            this.mElm = this.del(this.mElm);
            if (this.mElm == null) {
               this.mPh = 5;
            } else {
               var13 = this.step();
            }
         } while(this.mPh == 4);

         while(this.wsskip() != '\uffff') {
            switch(this.step()) {
            case 6:
            case 8:
               break;
            default:
               this.panic("");
            }

            if (this.mPh != 5) {
               break;
            }
         }

         this.mPh = 6;
      } catch (SAXException var8) {
         throw var8;
      } catch (IOException var9) {
         throw var9;
      } catch (RuntimeException var10) {
         throw var10;
      } catch (Exception var11) {
         this.panic(var11.toString());
      } finally {
         this.mHandCont.endDocument();
         this.cleanup();
      }

   }

   protected void docType(String var1, String var2, String var3) throws SAXException {
      this.mHandDtd.notationDecl(var1, var2, var3);
   }

   protected void comm(char[] var1, int var2) {
   }

   protected void pi(String var1, String var2) throws SAXException {
      this.mHandCont.processingInstruction(var1, var2);
   }

   protected void newPrefix() throws SAXException {
      this.mHandCont.startPrefixMapping(this.mPref.name, this.mPref.value);
   }

   protected void skippedEnt(String var1) throws SAXException {
      this.mHandCont.skippedEntity(var1);
   }

   protected InputSource resolveEnt(String var1, String var2, String var3) throws SAXException, IOException {
      return this.mHandEnt.resolveEntity(var2, var3);
   }

   protected void notDecl(String var1, String var2, String var3) throws SAXException {
      this.mHandDtd.notationDecl(var1, var2, var3);
   }

   protected void unparsedEntDecl(String var1, String var2, String var3, String var4) throws SAXException {
      this.mHandDtd.unparsedEntityDecl(var1, var2, var3, var4);
   }

   protected void panic(String var1) throws SAXException {
      SAXParseException var2 = new SAXParseException(var1, this);
      this.mHandErr.fatalError(var2);
      throw var2;
   }

   protected void bflash() throws SAXException {
      if (this.mBuffIdx >= 0) {
         this.mHandCont.characters(this.mBuff, 0, this.mBuffIdx + 1);
         this.mBuffIdx = -1;
      }

   }

   protected void bflash_ws() throws SAXException {
      if (this.mBuffIdx >= 0) {
         this.mHandCont.characters(this.mBuff, 0, this.mBuffIdx + 1);
         this.mBuffIdx = -1;
      }

   }

   public boolean getFeature(String var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public void setFeature(String var1, boolean var2) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public Object getProperty(String var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public void setProperty(String var1, Object var2) {
      throw new UnsupportedOperationException("Not supported yet.");
   }
}
