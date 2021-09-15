package jdk.internal.util.xml.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import jdk.internal.org.xml.sax.InputSource;
import jdk.internal.org.xml.sax.SAXException;

public abstract class Parser {
   public static final String FAULT = "";
   protected static final int BUFFSIZE_READER = 512;
   protected static final int BUFFSIZE_PARSER = 128;
   public static final char EOS = '\uffff';
   private Pair mNoNS;
   private Pair mXml;
   private Map<String, Input> mEnt;
   private Map<String, Input> mPEnt;
   protected boolean mIsSAlone;
   protected boolean mIsSAloneSet;
   protected boolean mIsNSAware;
   protected int mPh = -1;
   protected static final int PH_BEFORE_DOC = -1;
   protected static final int PH_DOC_START = 0;
   protected static final int PH_MISC_DTD = 1;
   protected static final int PH_DTD = 2;
   protected static final int PH_DTD_MISC = 3;
   protected static final int PH_DOCELM = 4;
   protected static final int PH_DOCELM_MISC = 5;
   protected static final int PH_AFTER_DOC = 6;
   protected int mEvt;
   protected static final int EV_NULL = 0;
   protected static final int EV_ELM = 1;
   protected static final int EV_ELMS = 2;
   protected static final int EV_ELME = 3;
   protected static final int EV_TEXT = 4;
   protected static final int EV_WSPC = 5;
   protected static final int EV_PI = 6;
   protected static final int EV_CDAT = 7;
   protected static final int EV_COMM = 8;
   protected static final int EV_DTD = 9;
   protected static final int EV_ENT = 10;
   private char mESt;
   protected char[] mBuff = new char[128];
   protected int mBuffIdx;
   protected Pair mPref;
   protected Pair mElm;
   protected Pair mAttL;
   protected Input mDoc;
   protected Input mInp;
   private char[] mChars;
   private int mChLen;
   private int mChIdx;
   protected Attrs mAttrs = new Attrs();
   private String[] mItems;
   private char mAttrIdx;
   private String mUnent;
   private Pair mDltd;
   private static final char[] NONS = new char[1];
   private static final char[] XML;
   private static final char[] XMLNS;
   private static final byte[] asctyp;
   private static final byte[] nmttyp;

   protected Parser() {
      this.mPref = this.pair(this.mPref);
      this.mPref.name = "";
      this.mPref.value = "";
      this.mPref.chars = NONS;
      this.mNoNS = this.mPref;
      this.mPref = this.pair(this.mPref);
      this.mPref.name = "xml";
      this.mPref.value = "http://www.w3.org/XML/1998/namespace";
      this.mPref.chars = XML;
      this.mXml = this.mPref;
   }

   protected void init() {
      this.mUnent = null;
      this.mElm = null;
      this.mPref = this.mXml;
      this.mAttL = null;
      this.mPEnt = new HashMap();
      this.mEnt = new HashMap();
      this.mDoc = this.mInp;
      this.mChars = this.mInp.chars;
      this.mPh = 0;
   }

   protected void cleanup() {
      while(this.mAttL != null) {
         for(; this.mAttL.list != null; this.mAttL.list = this.del(this.mAttL.list)) {
            if (this.mAttL.list.list != null) {
               this.del(this.mAttL.list.list);
            }
         }

         this.mAttL = this.del(this.mAttL);
      }

      while(this.mElm != null) {
         this.mElm = this.del(this.mElm);
      }

      while(this.mPref != this.mXml) {
         this.mPref = this.del(this.mPref);
      }

      while(this.mInp != null) {
         this.pop();
      }

      if (this.mDoc != null && this.mDoc.src != null) {
         try {
            this.mDoc.src.close();
         } catch (IOException var2) {
         }
      }

      this.mPEnt = null;
      this.mEnt = null;
      this.mDoc = null;
      this.mPh = 6;
   }

   protected int step() throws Exception {
      this.mEvt = 0;
      byte var1 = 0;

      while(true) {
         while(this.mEvt == 0) {
            char var2 = this.mChIdx < this.mChLen ? this.mChars[this.mChIdx++] : this.getch();
            switch(var1) {
            case 0:
               if (var2 != '<') {
                  this.bkch();
                  this.mBuffIdx = -1;
                  var1 = 1;
               } else {
                  switch(this.getch()) {
                  case '!':
                     var2 = this.getch();
                     this.bkch();
                     switch(var2) {
                     case '-':
                        this.mEvt = 8;
                        this.comm();
                        continue;
                     case '[':
                        this.mEvt = 7;
                        this.cdat();
                        continue;
                     default:
                        this.mEvt = 9;
                        this.dtd();
                        continue;
                     }
                  case '/':
                     this.mEvt = 3;
                     if (this.mElm == null) {
                        this.panic("");
                     }

                     this.mBuffIdx = -1;
                     this.bname(this.mIsNSAware);
                     char[] var3 = this.mElm.chars;
                     if (var3.length == this.mBuffIdx + 1) {
                        for(char var4 = 1; var4 <= this.mBuffIdx; ++var4) {
                           if (var3[var4] != this.mBuff[var4]) {
                              this.panic("");
                           }
                        }
                     } else {
                        this.panic("");
                     }

                     if (this.wsskip() != '>') {
                        this.panic("");
                     }

                     this.getch();
                     break;
                  case '?':
                     this.mEvt = 6;
                     this.pi();
                     break;
                  default:
                     this.bkch();
                     this.mElm = this.pair(this.mElm);
                     this.mElm.chars = this.qname(this.mIsNSAware);
                     this.mElm.name = this.mElm.local();
                     this.mElm.id = this.mElm.next != null ? this.mElm.next.id : 0;
                     this.mElm.num = 0;
                     Pair var6 = this.find(this.mAttL, this.mElm.chars);
                     this.mElm.list = var6 != null ? var6.list : null;
                     this.mAttrIdx = 0;
                     Pair var5 = this.pair((Pair)null);
                     var5.num = 0;
                     this.attr(var5);
                     this.del(var5);
                     this.mElm.value = this.mIsNSAware ? this.rslv(this.mElm.chars) : null;
                     switch(this.wsskip()) {
                     case '/':
                        this.getch();
                        if (this.getch() != '>') {
                           this.panic("");
                        }

                        this.mEvt = 1;
                        break;
                     case '>':
                        this.getch();
                        this.mEvt = 2;
                        break;
                     default:
                        this.panic("");
                     }
                  }
               }
               break;
            case 1:
               switch(var2) {
               case '\t':
               case '\n':
               case ' ':
                  this.bappend(var2);
                  continue;
               case '\r':
                  if (this.getch() != '\n') {
                     this.bkch();
                  }

                  this.bappend('\n');
                  continue;
               case '<':
                  this.mEvt = 5;
                  this.bkch();
                  this.bflash_ws();
                  continue;
               default:
                  this.bkch();
                  var1 = 2;
                  continue;
               }
            case 2:
               switch(var2) {
               case '\r':
                  if (this.getch() != '\n') {
                     this.bkch();
                  }

                  this.bappend('\n');
                  continue;
               case '&':
                  if (this.mUnent == null) {
                     if ((this.mUnent = this.ent('x')) != null) {
                        this.mEvt = 4;
                        this.bkch();
                        this.setch('&');
                        this.bflash();
                     }
                  } else {
                     this.mEvt = 10;
                     this.skippedEnt(this.mUnent);
                     this.mUnent = null;
                  }
                  continue;
               case '<':
                  this.mEvt = 4;
                  this.bkch();
                  this.bflash();
                  continue;
               case '\uffff':
                  this.panic("");
               default:
                  this.bappend(var2);
                  continue;
               }
            default:
               this.panic("");
            }
         }

         return this.mEvt;
      }
   }

   private void dtd() throws Exception {
      Object var2 = null;
      String var3 = null;
      Pair var4 = null;
      if (!"DOCTYPE".equals(this.name(false))) {
         this.panic("");
      }

      this.mPh = 2;
      byte var5 = 0;

      while(var5 >= 0) {
         char var1 = this.getch();
         switch(var5) {
         case 0:
            if (this.chtyp(var1) != ' ') {
               this.bkch();
               var3 = this.name(this.mIsNSAware);
               this.wsskip();
               var5 = 1;
            }
            break;
         case 1:
            switch(this.chtyp(var1)) {
            case '>':
               this.bkch();
               var5 = 3;
               this.docType(var3, (String)null, (String)null);
               continue;
            case 'A':
               this.bkch();
               var4 = this.pubsys(' ');
               var5 = 2;
               this.docType(var3, var4.name, var4.value);
               continue;
            case '[':
               this.bkch();
               var5 = 2;
               this.docType(var3, (String)null, (String)null);
               continue;
            default:
               this.panic("");
               continue;
            }
         case 2:
            switch(this.chtyp(var1)) {
            case ' ':
               continue;
            case '>':
               this.bkch();
               var5 = 3;
               continue;
            case '[':
               this.dtdsub();
               var5 = 3;
               continue;
            default:
               this.panic("");
               continue;
            }
         case 3:
            switch(this.chtyp(var1)) {
            case ' ':
               continue;
            case '>':
               if (var4 != null) {
                  InputSource var6 = this.resolveEnt(var3, var4.name, var4.value);
                  if (var6 != null) {
                     if (!this.mIsSAlone) {
                        this.bkch();
                        this.setch(']');
                        this.push(new Input(512));
                        this.setinp(var6);
                        this.mInp.pubid = var4.name;
                        this.mInp.sysid = var4.value;
                        this.dtdsub();
                     } else {
                        this.skippedEnt("[dtd]");
                        if (var6.getCharacterStream() != null) {
                           try {
                              var6.getCharacterStream().close();
                           } catch (IOException var9) {
                           }
                        }

                        if (var6.getByteStream() != null) {
                           try {
                              var6.getByteStream().close();
                           } catch (IOException var8) {
                           }
                        }
                     }
                  } else {
                     this.skippedEnt("[dtd]");
                  }

                  this.del(var4);
               }

               var5 = -1;
               continue;
            default:
               this.panic("");
               continue;
            }
         default:
            this.panic("");
         }
      }

   }

   private void dtdsub() throws Exception {
      byte var2 = 0;

      while(var2 >= 0) {
         char var1 = this.getch();
         switch(var2) {
         case 0:
            switch(this.chtyp(var1)) {
            case ' ':
               continue;
            case '%':
               this.pent(' ');
               continue;
            case '<':
               var1 = this.getch();
               switch(var1) {
               case '!':
                  var1 = this.getch();
                  this.bkch();
                  if (var1 == '-') {
                     this.comm();
                  } else {
                     this.bntok();
                     switch(this.bkeyword()) {
                     case 'a':
                        this.dtdattl();
                        break;
                     case 'e':
                        this.dtdelm();
                        break;
                     case 'n':
                        this.dtdent();
                        break;
                     case 'o':
                        this.dtdnot();
                        break;
                     default:
                        this.panic("");
                     }

                     var2 = 1;
                  }
                  continue;
               case '?':
                  this.pi();
                  continue;
               default:
                  this.panic("");
                  continue;
               }
            case 'Z':
               if (this.getch() != ']') {
                  this.panic("");
               }

               var2 = -1;
               continue;
            case ']':
               var2 = -1;
               continue;
            default:
               this.panic("");
               continue;
            }
         case 1:
            switch(var1) {
            case '\t':
            case '\n':
            case '\r':
            case ' ':
               continue;
            case '>':
               var2 = 0;
               continue;
            default:
               this.panic("");
               continue;
            }
         default:
            this.panic("");
         }
      }

   }

   private void dtdent() throws Exception {
      String var1 = null;
      Object var2 = null;
      Input var3 = null;
      Pair var4 = null;
      byte var6 = 0;

      while(true) {
         while(true) {
            while(var6 >= 0) {
               char var5 = this.getch();
               char[] var7;
               switch(var6) {
               case 0:
                  switch(this.chtyp(var5)) {
                  case ' ':
                     continue;
                  case '%':
                     var5 = this.getch();
                     this.bkch();
                     if (this.chtyp(var5) == ' ') {
                        this.wsskip();
                        var1 = this.name(false);
                        switch(this.chtyp(this.wsskip())) {
                        case '"':
                        case '\'':
                           this.bqstr('d');
                           var7 = new char[this.mBuffIdx + 1];
                           System.arraycopy(this.mBuff, 1, var7, 1, var7.length - 1);
                           var7[0] = ' ';
                           if (!this.mPEnt.containsKey(var1)) {
                              var3 = new Input(var7);
                              var3.pubid = this.mInp.pubid;
                              var3.sysid = this.mInp.sysid;
                              var3.xmlenc = this.mInp.xmlenc;
                              var3.xmlver = this.mInp.xmlver;
                              this.mPEnt.put(var1, var3);
                           }

                           var6 = -1;
                           continue;
                        case 'A':
                           var4 = this.pubsys(' ');
                           if (this.wsskip() == '>') {
                              if (!this.mPEnt.containsKey(var1)) {
                                 var3 = new Input();
                                 var3.pubid = var4.name;
                                 var3.sysid = var4.value;
                                 this.mPEnt.put(var1, var3);
                              }
                           } else {
                              this.panic("");
                           }

                           this.del(var4);
                           var6 = -1;
                           continue;
                        default:
                           this.panic("");
                        }
                     } else {
                        this.pent(' ');
                     }
                     continue;
                  default:
                     this.bkch();
                     var1 = this.name(false);
                     var6 = 1;
                     continue;
                  }
               case 1:
                  switch(this.chtyp(var5)) {
                  case ' ':
                     continue;
                  case '"':
                  case '\'':
                     this.bkch();
                     this.bqstr('d');
                     if (this.mEnt.get(var1) == null) {
                        var7 = new char[this.mBuffIdx];
                        System.arraycopy(this.mBuff, 1, var7, 0, var7.length);
                        if (!this.mEnt.containsKey(var1)) {
                           var3 = new Input(var7);
                           var3.pubid = this.mInp.pubid;
                           var3.sysid = this.mInp.sysid;
                           var3.xmlenc = this.mInp.xmlenc;
                           var3.xmlver = this.mInp.xmlver;
                           this.mEnt.put(var1, var3);
                        }
                     }

                     var6 = -1;
                     continue;
                  case 'A':
                     this.bkch();
                     var4 = this.pubsys(' ');
                     switch(this.wsskip()) {
                     case '>':
                        if (!this.mEnt.containsKey(var1)) {
                           var3 = new Input();
                           var3.pubid = var4.name;
                           var3.sysid = var4.value;
                           this.mEnt.put(var1, var3);
                        }
                        break;
                     case 'N':
                        if ("NDATA".equals(this.name(false))) {
                           this.wsskip();
                           this.unparsedEntDecl(var1, var4.name, var4.value, this.name(false));
                           break;
                        }
                     default:
                        this.panic("");
                     }

                     this.del(var4);
                     var6 = -1;
                     continue;
                  default:
                     this.panic("");
                     continue;
                  }
               default:
                  this.panic("");
               }
            }

            return;
         }
      }
   }

   private void dtdelm() throws Exception {
      this.wsskip();
      this.name(this.mIsNSAware);

      while(true) {
         char var1 = this.getch();
         switch(var1) {
         case '>':
            this.bkch();
            return;
         case '\uffff':
            this.panic("");
         }
      }
   }

   private void dtdattl() throws Exception {
      Object var1 = null;
      Pair var2 = null;
      byte var4 = 0;

      while(var4 >= 0) {
         char var3 = this.getch();
         switch(var4) {
         case 0:
            switch(this.chtyp(var3)) {
            case ' ':
               continue;
            case '%':
               this.pent(' ');
               continue;
            case ':':
            case 'A':
            case 'X':
            case '_':
            case 'a':
               this.bkch();
               char[] var5 = this.qname(this.mIsNSAware);
               var2 = this.find(this.mAttL, var5);
               if (var2 == null) {
                  var2 = this.pair(this.mAttL);
                  var2.chars = var5;
                  this.mAttL = var2;
               }

               var4 = 1;
               continue;
            default:
               this.panic("");
               continue;
            }
         case 1:
            switch(this.chtyp(var3)) {
            case ' ':
               continue;
            case '%':
               this.pent(' ');
               continue;
            case ':':
            case 'A':
            case 'X':
            case '_':
            case 'a':
               this.bkch();
               this.dtdatt(var2);
               if (this.wsskip() == '>') {
                  return;
               }
               continue;
            default:
               this.panic("");
               continue;
            }
         default:
            this.panic("");
         }
      }

   }

   private void dtdatt(Pair var1) throws Exception {
      Object var2 = null;
      Pair var3 = null;
      byte var5 = 0;

      while(var5 >= 0) {
         char var4 = this.getch();
         switch(var5) {
         case 0:
            switch(this.chtyp(var4)) {
            case ' ':
               continue;
            case '%':
               this.pent(' ');
               continue;
            case ':':
            case 'A':
            case 'X':
            case '_':
            case 'a':
               this.bkch();
               char[] var6 = this.qname(this.mIsNSAware);
               var3 = this.find(var1.list, var6);
               if (var3 == null) {
                  var3 = this.pair(var1.list);
                  var3.chars = var6;
                  var1.list = var3;
               } else {
                  var3 = this.pair((Pair)null);
                  var3.chars = var6;
                  var3.id = 99;
               }

               this.wsskip();
               var5 = 1;
               continue;
            default:
               this.panic("");
               continue;
            }
         case 1:
            switch(this.chtyp(var4)) {
            case ' ':
               continue;
            case '%':
               this.pent(' ');
               continue;
            case '(':
               var3.id = 117;
               var5 = 2;
               continue;
            default:
               this.bkch();
               this.bntok();
               var3.id = this.bkeyword();
               switch(var3.id) {
               case 78:
               case 82:
               case 84:
               case 99:
               case 105:
               case 110:
               case 114:
               case 116:
                  this.wsskip();
                  var5 = 4;
                  continue;
               case 111:
                  if (this.wsskip() != '(') {
                     this.panic("");
                  }

                  var4 = this.getch();
                  var5 = 2;
                  continue;
               default:
                  this.panic("");
                  continue;
               }
            }
         case 2:
            switch(this.chtyp(var4)) {
            case ' ':
               continue;
            case '%':
               this.pent(' ');
               continue;
            case '-':
            case '.':
            case ':':
            case 'A':
            case 'X':
            case '_':
            case 'a':
            case 'd':
               this.bkch();
               switch(var3.id) {
               case 111:
                  this.mBuffIdx = -1;
                  this.bname(false);
                  break;
               case 117:
                  this.bntok();
                  break;
               default:
                  this.panic("");
               }

               this.wsskip();
               var5 = 3;
               continue;
            default:
               this.panic("");
               continue;
            }
         case 3:
            switch(var4) {
            case '%':
               this.pent(' ');
               continue;
            case ')':
               this.wsskip();
               var5 = 4;
               continue;
            case '|':
               this.wsskip();
               switch(var3.id) {
               case 111:
                  this.mBuffIdx = -1;
                  this.bname(false);
                  break;
               case 117:
                  this.bntok();
                  break;
               default:
                  this.panic("");
               }

               this.wsskip();
               continue;
            default:
               this.panic("");
               continue;
            }
         case 4:
            switch(var4) {
            case '\t':
            case '\n':
            case '\r':
            case ' ':
               continue;
            case '"':
            case '\'':
               this.bkch();
               var5 = 5;
               continue;
            case '#':
               this.bntok();
               switch(this.bkeyword()) {
               case 'F':
                  switch(this.wsskip()) {
                  case '"':
                  case '\'':
                     var5 = 5;
                     continue;
                  case '\uffff':
                     this.panic("");
                  default:
                     var5 = -1;
                     continue;
                  }
               case 'I':
               case 'Q':
                  var5 = -1;
                  continue;
               default:
                  this.panic("");
                  continue;
               }
            case '%':
               this.pent(' ');
               continue;
            default:
               this.bkch();
               var5 = -1;
               continue;
            }
         case 5:
            switch(var4) {
            case '"':
            case '\'':
               this.bkch();
               this.bqstr('d');
               var3.list = this.pair((Pair)null);
               var3.list.chars = new char[var3.chars.length + this.mBuffIdx + 3];
               System.arraycopy(var3.chars, 1, var3.list.chars, 0, var3.chars.length - 1);
               var3.list.chars[var3.chars.length - 1] = '=';
               var3.list.chars[var3.chars.length] = var4;
               System.arraycopy(this.mBuff, 1, var3.list.chars, var3.chars.length + 1, this.mBuffIdx);
               var3.list.chars[var3.chars.length + this.mBuffIdx + 1] = var4;
               var3.list.chars[var3.chars.length + this.mBuffIdx + 2] = ' ';
               var5 = -1;
               continue;
            default:
               this.panic("");
               continue;
            }
         default:
            this.panic("");
         }
      }

   }

   private void dtdnot() throws Exception {
      this.wsskip();
      String var1 = this.name(false);
      this.wsskip();
      Pair var2 = this.pubsys('N');
      this.notDecl(var1, var2.name, var2.value);
      this.del(var2);
   }

   private void attr(Pair var1) throws Exception {
      Pair var4;
      switch(this.wsskip()) {
      case '/':
      case '>':
         if ((var1.num & 2) == 0) {
            var1.num |= 2;
            Input var2 = this.mInp;

            for(Pair var3 = this.mElm.list; var3 != null; var3 = var3.next) {
               if (var3.list != null) {
                  var4 = this.find(var1.next, var3.chars);
                  if (var4 == null) {
                     this.push(new Input(var3.list.chars));
                  }
               }
            }

            if (this.mInp != var2) {
               this.attr(var1);
               return;
            }
         }

         this.mAttrs.setLength(this.mAttrIdx);
         this.mItems = this.mAttrs.mItems;
         return;
      case '\uffff':
         this.panic("");
      default:
         var1.chars = this.qname(this.mIsNSAware);
         var1.name = var1.local();
         String var6 = this.atype(var1);
         this.wsskip();
         if (this.getch() != '=') {
            this.panic("");
         }

         this.bqstr((char)var1.id);
         String var7 = new String(this.mBuff, 1, this.mBuffIdx);
         var4 = this.pair(var1);
         var4.num = var1.num & -2;
         if (this.mIsNSAware && this.isdecl(var1, var7)) {
            this.newPrefix();
            this.attr(var4);
         } else {
            ++this.mAttrIdx;
            this.attr(var4);
            --this.mAttrIdx;
            char var5 = (char)(this.mAttrIdx << 3);
            this.mItems[var5 + 1] = var1.qname();
            this.mItems[var5 + 2] = this.mIsNSAware ? var1.name : "";
            this.mItems[var5 + 3] = var7;
            this.mItems[var5 + 4] = var6;
            switch(var1.num & 3) {
            case 0:
               this.mItems[var5 + 5] = null;
               break;
            case 1:
               this.mItems[var5 + 5] = "d";
               break;
            default:
               this.mItems[var5 + 5] = "D";
            }

            this.mItems[var5 + 0] = var1.chars[0] != 0 ? this.rslv(var1.chars) : "";
         }

         this.del(var4);
      }
   }

   private String atype(Pair var1) throws Exception {
      var1.id = 99;
      Pair var2;
      if (this.mElm.list != null && (var2 = this.find(this.mElm.list, var1.chars)) != null) {
         var1.num |= 1;
         var1.id = 105;
         switch(var2.id) {
         case 78:
            return "ENTITIES";
         case 79:
         case 80:
         case 81:
         case 83:
         case 85:
         case 86:
         case 87:
         case 88:
         case 89:
         case 90:
         case 91:
         case 92:
         case 93:
         case 94:
         case 95:
         case 96:
         case 97:
         case 98:
         case 100:
         case 101:
         case 102:
         case 103:
         case 104:
         case 106:
         case 107:
         case 108:
         case 109:
         case 112:
         case 113:
         case 115:
         default:
            this.panic("");
            return null;
         case 82:
            return "IDREFS";
         case 84:
            return "NMTOKENS";
         case 99:
            var1.id = 99;
            return "CDATA";
         case 105:
            return "ID";
         case 110:
            return "ENTITY";
         case 111:
            return "NOTATION";
         case 114:
            return "IDREF";
         case 116:
            return "NMTOKEN";
         case 117:
            return "NMTOKEN";
         }
      } else {
         return "CDATA";
      }
   }

   private void comm() throws Exception {
      if (this.mPh == 0) {
         this.mPh = 1;
      }

      this.mBuffIdx = -1;
      byte var2 = 0;

      while(true) {
         while(var2 >= 0) {
            char var1 = this.mChIdx < this.mChLen ? this.mChars[this.mChIdx++] : this.getch();
            if (var1 == '\uffff') {
               this.panic("");
            }

            switch(var2) {
            case 0:
               if (var1 == '-') {
                  var2 = 1;
               } else {
                  this.panic("");
               }
               break;
            case 1:
               if (var1 == '-') {
                  var2 = 2;
               } else {
                  this.panic("");
               }
               break;
            case 2:
               switch(var1) {
               case '-':
                  var2 = 3;
                  continue;
               default:
                  this.bappend(var1);
                  continue;
               }
            case 3:
               switch(var1) {
               case '-':
                  var2 = 4;
                  continue;
               default:
                  this.bappend('-');
                  this.bappend(var1);
                  var2 = 2;
                  continue;
               }
            case 4:
               if (var1 == '>') {
                  this.comm(this.mBuff, this.mBuffIdx + 1);
                  var2 = -1;
                  break;
               }
            default:
               this.panic("");
            }
         }

         return;
      }
   }

   private void pi() throws Exception {
      String var2 = null;
      this.mBuffIdx = -1;
      byte var3 = 0;

      while(true) {
         while(var3 >= 0) {
            char var1 = this.getch();
            if (var1 == '\uffff') {
               this.panic("");
            }

            switch(var3) {
            case 0:
               switch(this.chtyp(var1)) {
               case ':':
               case 'A':
               case 'X':
               case '_':
               case 'a':
                  this.bkch();
                  var2 = this.name(false);
                  if (var2.length() == 0 || this.mXml.name.equals(var2.toLowerCase())) {
                     this.panic("");
                  }

                  if (this.mPh == 0) {
                     this.mPh = 1;
                  }

                  this.wsskip();
                  var3 = 1;
                  this.mBuffIdx = -1;
                  continue;
               default:
                  this.panic("");
                  continue;
               }
            case 1:
               switch(var1) {
               case '?':
                  var3 = 2;
                  continue;
               default:
                  this.bappend(var1);
                  continue;
               }
            case 2:
               switch(var1) {
               case '>':
                  this.pi(var2, new String(this.mBuff, 0, this.mBuffIdx + 1));
                  var3 = -1;
                  continue;
               case '?':
                  this.bappend('?');
                  continue;
               default:
                  this.bappend('?');
                  this.bappend(var1);
                  var3 = 1;
                  continue;
               }
            default:
               this.panic("");
            }
         }

         return;
      }
   }

   private void cdat() throws Exception {
      this.mBuffIdx = -1;
      byte var2 = 0;

      while(var2 >= 0) {
         char var1 = this.getch();
         switch(var2) {
         case 0:
            if (var1 == '[') {
               var2 = 1;
            } else {
               this.panic("");
            }
            break;
         case 1:
            if (this.chtyp(var1) == 'A') {
               this.bappend(var1);
            } else {
               if (!"CDATA".equals(new String(this.mBuff, 0, this.mBuffIdx + 1))) {
                  this.panic("");
               }

               this.bkch();
               var2 = 2;
            }
            break;
         case 2:
            if (var1 != '[') {
               this.panic("");
            }

            this.mBuffIdx = -1;
            var2 = 3;
            break;
         case 3:
            if (var1 != ']') {
               this.bappend(var1);
            } else {
               var2 = 4;
            }
            break;
         case 4:
            if (var1 != ']') {
               this.bappend(']');
               this.bappend(var1);
               var2 = 3;
            } else {
               var2 = 5;
            }
            break;
         case 5:
            switch(var1) {
            case '>':
               this.bflash();
               var2 = -1;
               continue;
            case ']':
               this.bappend(']');
               continue;
            default:
               this.bappend(']');
               this.bappend(']');
               this.bappend(var1);
               var2 = 3;
               continue;
            }
         default:
            this.panic("");
         }
      }

   }

   protected String name(boolean var1) throws Exception {
      this.mBuffIdx = -1;
      this.bname(var1);
      return new String(this.mBuff, 1, this.mBuffIdx);
   }

   protected char[] qname(boolean var1) throws Exception {
      this.mBuffIdx = -1;
      this.bname(var1);
      char[] var2 = new char[this.mBuffIdx + 1];
      System.arraycopy(this.mBuff, 0, var2, 0, this.mBuffIdx + 1);
      return var2;
   }

   private void pubsys(Input var1) throws Exception {
      Pair var2 = this.pubsys(' ');
      var1.pubid = var2.name;
      var1.sysid = var2.value;
      this.del(var2);
   }

   private Pair pubsys(char var1) throws Exception {
      Pair var2 = this.pair((Pair)null);
      String var3 = this.name(false);
      if ("PUBLIC".equals(var3)) {
         this.bqstr('i');
         var2.name = new String(this.mBuff, 1, this.mBuffIdx);
         switch(this.wsskip()) {
         case '"':
         case '\'':
            this.bqstr(' ');
            var2.value = new String(this.mBuff, 1, this.mBuffIdx);
            break;
         case '\uffff':
            this.panic("");
         default:
            if (var1 != 'N') {
               this.panic("");
            }

            var2.value = null;
         }

         return var2;
      } else if ("SYSTEM".equals(var3)) {
         var2.name = null;
         this.bqstr(' ');
         var2.value = new String(this.mBuff, 1, this.mBuffIdx);
         return var2;
      } else {
         this.panic("");
         return null;
      }
   }

   protected String eqstr(char var1) throws Exception {
      if (var1 == '=') {
         this.wsskip();
         if (this.getch() != '=') {
            this.panic("");
         }
      }

      this.bqstr(var1 == '=' ? '-' : var1);
      return new String(this.mBuff, 1, this.mBuffIdx);
   }

   private String ent(char var1) throws Exception {
      int var3 = this.mBuffIdx + 1;
      Input var4 = null;
      String var5 = null;
      this.mESt = 256;
      this.bappend('&');
      byte var6 = 0;

      while(true) {
         while(true) {
            while(var6 >= 0) {
               char var2 = this.mChIdx < this.mChLen ? this.mChars[this.mChIdx++] : this.getch();
               int var7;
               switch(var6) {
               case 0:
               case 1:
                  switch(this.chtyp(var2)) {
                  case '#':
                     if (var6 != 0) {
                        this.panic("");
                     }

                     var6 = 2;
                     continue;
                  case '-':
                  case '.':
                  case 'd':
                     if (var6 != 1) {
                        this.panic("");
                     }
                  case 'A':
                  case 'X':
                  case '_':
                  case 'a':
                     this.bappend(var2);
                     this.eappend(var2);
                     var6 = 1;
                     continue;
                  case ':':
                     if (this.mIsNSAware) {
                        this.panic("");
                     }

                     this.bappend(var2);
                     this.eappend(var2);
                     var6 = 1;
                     continue;
                  case ';':
                     if (this.mESt < 256) {
                        this.mBuffIdx = var3 - 1;
                        this.bappend(this.mESt);
                        var6 = -1;
                     } else if (this.mPh == 2) {
                        this.bappend(';');
                        var6 = -1;
                     } else {
                        var5 = new String(this.mBuff, var3 + 1, this.mBuffIdx - var3);
                        var4 = (Input)this.mEnt.get(var5);
                        this.mBuffIdx = var3 - 1;
                        if (var4 != null) {
                           if (var4.chars == null) {
                              InputSource var10 = this.resolveEnt(var5, var4.pubid, var4.sysid);
                              if (var10 != null) {
                                 this.push(new Input(512));
                                 this.setinp(var10);
                                 this.mInp.pubid = var4.pubid;
                                 this.mInp.sysid = var4.sysid;
                                 var5 = null;
                              } else if (var1 != 'x') {
                                 this.panic("");
                              }
                           } else {
                              this.push(var4);
                              var5 = null;
                           }
                        } else if (var1 != 'x') {
                           this.panic("");
                        }

                        var6 = -1;
                     }
                     continue;
                  default:
                     this.panic("");
                     continue;
                  }
               case 2:
                  switch(this.chtyp(var2)) {
                  case ';':
                     try {
                        var7 = Integer.parseInt(new String(this.mBuff, var3 + 1, this.mBuffIdx - var3), 10);
                        if (var7 >= 65535) {
                           this.panic("");
                        }

                        var2 = (char)var7;
                     } catch (NumberFormatException var9) {
                        this.panic("");
                     }

                     this.mBuffIdx = var3 - 1;
                     if (var2 != ' ' && this.mInp.next == null) {
                        this.bappend(var2);
                     } else {
                        this.bappend(var2, var1);
                     }

                     var6 = -1;
                     continue;
                  case 'a':
                     if (this.mBuffIdx == var3 && var2 == 'x') {
                        var6 = 3;
                        continue;
                     }
                  default:
                     this.panic("");
                     continue;
                  case 'd':
                     this.bappend(var2);
                     continue;
                  }
               case 3:
                  switch(this.chtyp(var2)) {
                  case ';':
                     try {
                        var7 = Integer.parseInt(new String(this.mBuff, var3 + 1, this.mBuffIdx - var3), 16);
                        if (var7 >= 65535) {
                           this.panic("");
                        }

                        var2 = (char)var7;
                     } catch (NumberFormatException var8) {
                        this.panic("");
                     }

                     this.mBuffIdx = var3 - 1;
                     if (var2 != ' ' && this.mInp.next == null) {
                        this.bappend(var2);
                     } else {
                        this.bappend(var2, var1);
                     }

                     var6 = -1;
                     continue;
                  case 'A':
                  case 'a':
                  case 'd':
                     this.bappend(var2);
                     continue;
                  default:
                     this.panic("");
                     continue;
                  }
               default:
                  this.panic("");
               }
            }

            return var5;
         }
      }
   }

   private void pent(char var1) throws Exception {
      int var3 = this.mBuffIdx + 1;
      Input var4 = null;
      String var5 = null;
      this.bappend('%');
      if (this.mPh == 2) {
         this.bname(false);
         var5 = new String(this.mBuff, var3 + 2, this.mBuffIdx - var3 - 1);
         if (this.getch() != ';') {
            this.panic("");
         }

         var4 = (Input)this.mPEnt.get(var5);
         this.mBuffIdx = var3 - 1;
         if (var4 != null) {
            if (var4.chars == null) {
               InputSource var6 = this.resolveEnt(var5, var4.pubid, var4.sysid);
               if (var6 != null) {
                  if (var1 != '-') {
                     this.bappend(' ');
                  }

                  this.push(new Input(512));
                  this.setinp(var6);
                  this.mInp.pubid = var4.pubid;
                  this.mInp.sysid = var4.sysid;
               } else {
                  this.skippedEnt("%" + var5);
               }
            } else {
               if (var1 == '-') {
                  var4.chIdx = 1;
               } else {
                  this.bappend(' ');
                  var4.chIdx = 0;
               }

               this.push(var4);
            }
         } else {
            this.skippedEnt("%" + var5);
         }

      }
   }

   private boolean isdecl(Pair var1, String var2) {
      if (var1.chars[0] == 0) {
         if ("xmlns".equals(var1.name)) {
            this.mPref = this.pair(this.mPref);
            this.mPref.list = this.mElm;
            this.mPref.value = var2;
            this.mPref.name = "";
            this.mPref.chars = NONS;
            ++this.mElm.num;
            return true;
         }
      } else if (var1.eqpref(XMLNS)) {
         int var3 = var1.name.length();
         this.mPref = this.pair(this.mPref);
         this.mPref.list = this.mElm;
         this.mPref.value = var2;
         this.mPref.name = var1.name;
         this.mPref.chars = new char[var3 + 1];
         this.mPref.chars[0] = (char)(var3 + 1);
         var1.name.getChars(0, var3, this.mPref.chars, 1);
         ++this.mElm.num;
         return true;
      }

      return false;
   }

   private String rslv(char[] var1) throws Exception {
      Pair var2;
      for(var2 = this.mPref; var2 != null; var2 = var2.next) {
         if (var2.eqpref(var1)) {
            return var2.value;
         }
      }

      if (var1[0] == 1) {
         for(var2 = this.mPref; var2 != null; var2 = var2.next) {
            if (var2.chars[0] == 0) {
               return var2.value;
            }
         }
      }

      this.panic("");
      return null;
   }

   protected char wsskip() throws IOException {
      char var1;
      do {
         var1 = this.mChIdx < this.mChLen ? this.mChars[this.mChIdx++] : this.getch();
      } while(var1 < 128 && nmttyp[var1] == 3);

      --this.mChIdx;
      return var1;
   }

   protected abstract void docType(String var1, String var2, String var3) throws SAXException;

   protected abstract void comm(char[] var1, int var2);

   protected abstract void pi(String var1, String var2) throws Exception;

   protected abstract void newPrefix() throws Exception;

   protected abstract void skippedEnt(String var1) throws Exception;

   protected abstract InputSource resolveEnt(String var1, String var2, String var3) throws Exception;

   protected abstract void notDecl(String var1, String var2, String var3) throws Exception;

   protected abstract void unparsedEntDecl(String var1, String var2, String var3, String var4) throws Exception;

   protected abstract void panic(String var1) throws Exception;

   private void bname(boolean var1) throws Exception {
      ++this.mBuffIdx;
      int var4 = this.mBuffIdx;
      int var5 = var4;
      int var6 = var4 + 1;
      int var7 = var6;
      int var8 = this.mChIdx;
      short var9 = (short)(var1 ? 0 : 2);

      while(true) {
         if (this.mChIdx >= this.mChLen) {
            this.bcopy(var8, var7);
            this.getch();
            --this.mChIdx;
            var8 = this.mChIdx;
            var7 = var6;
         }

         char var2 = this.mChars[this.mChIdx++];
         char var3 = 0;
         if (var2 < 128) {
            var3 = (char)nmttyp[var2];
         } else if (var2 == '\uffff') {
            this.panic("");
         }

         switch(var9) {
         case 0:
         case 2:
            switch(var3) {
            case '\u0000':
               ++var6;
               ++var9;
               continue;
            case '\u0001':
               --this.mChIdx;
               ++var9;
               continue;
            default:
               this.panic("");
               continue;
            }
         case 1:
         case 3:
            switch(var3) {
            case '\u0000':
            case '\u0002':
               ++var6;
               continue;
            case '\u0001':
               ++var6;
               if (var1) {
                  if (var5 != var4) {
                     this.panic("");
                  }

                  var5 = var6 - 1;
                  if (var9 == 1) {
                     var9 = 2;
                  }
               }
               continue;
            default:
               --this.mChIdx;
               this.bcopy(var8, var7);
               this.mBuff[var4] = (char)(var5 - var4);
               return;
            }
         default:
            this.panic("");
         }
      }
   }

   private void bntok() throws Exception {
      this.mBuffIdx = -1;
      this.bappend('\u0000');

      label13:
      while(true) {
         char var1 = this.getch();
         switch(this.chtyp(var1)) {
         case '-':
         case '.':
         case ':':
         case 'A':
         case 'X':
         case '_':
         case 'a':
         case 'd':
            this.bappend(var1);
            break;
         case 'Z':
            this.panic("");
         default:
            break label13;
         }
      }

      this.bkch();
   }

   private char bkeyword() throws Exception {
      String var1 = new String(this.mBuff, 1, this.mBuffIdx);
      switch(var1.length()) {
      case 2:
         return (char)("ID".equals(var1) ? 'i' : '?');
      case 3:
      case 4:
      default:
         break;
      case 5:
         switch(this.mBuff[1]) {
         case 'C':
            return (char)("CDATA".equals(var1) ? 'c' : '?');
         case 'F':
            return (char)("FIXED".equals(var1) ? 'F' : '?');
         case 'I':
            return (char)("IDREF".equals(var1) ? 'r' : '?');
         default:
            return '?';
         }
      case 6:
         switch(this.mBuff[1]) {
         case 'E':
            return (char)("ENTITY".equals(var1) ? 'n' : '?');
         case 'I':
            return (char)("IDREFS".equals(var1) ? 'R' : '?');
         default:
            return '?';
         }
      case 7:
         switch(this.mBuff[1]) {
         case 'A':
            return (char)("ATTLIST".equals(var1) ? 'a' : '?');
         case 'E':
            return (char)("ELEMENT".equals(var1) ? 'e' : '?');
         case 'I':
            return (char)("IMPLIED".equals(var1) ? 'I' : '?');
         case 'N':
            return (char)("NMTOKEN".equals(var1) ? 't' : '?');
         default:
            return '?';
         }
      case 8:
         switch(this.mBuff[2]) {
         case 'E':
            return (char)("REQUIRED".equals(var1) ? 'Q' : '?');
         case 'M':
            return (char)("NMTOKENS".equals(var1) ? 'T' : '?');
         case 'N':
            return (char)("ENTITIES".equals(var1) ? 'N' : '?');
         case 'O':
            return (char)("NOTATION".equals(var1) ? 'o' : '?');
         }
      }

      return '?';
   }

   private void bqstr(char var1) throws Exception {
      Input var2 = this.mInp;
      this.mBuffIdx = -1;
      this.bappend('\u0000');
      byte var4 = 0;

      while(true) {
         while(true) {
            while(true) {
               while(var4 >= 0) {
                  char var3 = this.mChIdx < this.mChLen ? this.mChars[this.mChIdx++] : this.getch();
                  switch(var4) {
                  case 0:
                     switch(var3) {
                     case '\t':
                     case '\n':
                     case '\r':
                     case ' ':
                        continue;
                     case '"':
                        var4 = 3;
                        continue;
                     case '\'':
                        var4 = 2;
                        continue;
                     default:
                        this.panic("");
                        continue;
                     }
                  case 1:
                  default:
                     this.panic("");
                     break;
                  case 2:
                  case 3:
                     switch(var3) {
                     case '"':
                        if (var4 == 3 && this.mInp == var2) {
                           var4 = -1;
                           break;
                        }

                        this.bappend(var3);
                        break;
                     case '%':
                        if (var1 == 'd') {
                           this.pent('-');
                        } else {
                           this.bappend(var3);
                        }
                        break;
                     case '&':
                        if (var1 != 'd') {
                           this.ent(var1);
                        } else {
                           this.bappend(var3);
                        }
                        break;
                     case '\'':
                        if (var4 == 2 && this.mInp == var2) {
                           var4 = -1;
                           break;
                        }

                        this.bappend(var3);
                        break;
                     case '<':
                        if (var1 != '-' && var1 != 'd') {
                           this.panic("");
                           break;
                        }

                        this.bappend(var3);
                        break;
                     case '\uffff':
                        this.panic("");
                     case '\r':
                        if (var1 != ' ' && this.mInp.next == null) {
                           if (this.getch() != '\n') {
                              this.bkch();
                           }

                           var3 = '\n';
                        }
                     default:
                        this.bappend(var3, var1);
                     }
                  }
               }

               if (var1 == 'i' && this.mBuff[this.mBuffIdx] == ' ') {
                  --this.mBuffIdx;
               }

               return;
            }
         }
      }
   }

   protected abstract void bflash() throws Exception;

   protected abstract void bflash_ws() throws Exception;

   private void bappend(char var1, char var2) {
      label29:
      switch(var2) {
      case 'c':
         switch(var1) {
         case '\t':
         case '\n':
         case '\r':
            var1 = ' ';
         case '\u000b':
         case '\f':
         default:
            break label29;
         }
      case 'i':
         switch(var1) {
         case '\t':
         case '\n':
         case '\r':
         case ' ':
            if (this.mBuffIdx > 0 && this.mBuff[this.mBuffIdx] != ' ') {
               this.bappend(' ');
            }

            return;
         }
      }

      ++this.mBuffIdx;
      if (this.mBuffIdx < this.mBuff.length) {
         this.mBuff[this.mBuffIdx] = var1;
      } else {
         --this.mBuffIdx;
         this.bappend(var1);
      }

   }

   private void bappend(char var1) {
      try {
         this.mBuff[++this.mBuffIdx] = var1;
      } catch (Exception var4) {
         char[] var3 = new char[this.mBuff.length << 1];
         System.arraycopy(this.mBuff, 0, var3, 0, this.mBuff.length);
         this.mBuff = var3;
         this.mBuff[this.mBuffIdx] = var1;
      }

   }

   private void bcopy(int var1, int var2) {
      int var3 = this.mChIdx - var1;
      if (var2 + var3 + 1 >= this.mBuff.length) {
         char[] var4 = new char[this.mBuff.length + var3];
         System.arraycopy(this.mBuff, 0, var4, 0, this.mBuff.length);
         this.mBuff = var4;
      }

      System.arraycopy(this.mChars, var1, this.mBuff, var2, var3);
      this.mBuffIdx += var3;
   }

   private void eappend(char var1) {
      switch(this.mESt) {
      case '"':
      case '&':
      case '\'':
      case '<':
      case '>':
         this.mESt = 512;
         break;
      case 'Ā':
         switch(var1) {
         case 'a':
            this.mESt = 259;
            return;
         case 'g':
            this.mESt = 258;
            return;
         case 'l':
            this.mESt = 257;
            return;
         case 'q':
            this.mESt = 263;
            return;
         default:
            this.mESt = 512;
            return;
         }
      case 'ā':
         this.mESt = (char)(var1 == 't' ? 60 : 512);
         break;
      case 'Ă':
         this.mESt = (char)(var1 == 't' ? 62 : 512);
         break;
      case 'ă':
         switch(var1) {
         case 'm':
            this.mESt = 260;
            return;
         case 'p':
            this.mESt = 261;
            return;
         default:
            this.mESt = 512;
            return;
         }
      case 'Ą':
         this.mESt = (char)(var1 == 'p' ? 38 : 512);
         break;
      case 'ą':
         this.mESt = (char)(var1 == 'o' ? 262 : 512);
         break;
      case 'Ć':
         this.mESt = (char)(var1 == 's' ? 39 : 512);
         break;
      case 'ć':
         this.mESt = (char)(var1 == 'u' ? 264 : 512);
         break;
      case 'Ĉ':
         this.mESt = (char)(var1 == 'o' ? 265 : 512);
         break;
      case 'ĉ':
         this.mESt = (char)(var1 == 't' ? 34 : 512);
      }

   }

   protected void setinp(InputSource var1) throws Exception {
      Reader var2 = null;
      this.mChIdx = 0;
      this.mChLen = 0;
      this.mChars = this.mInp.chars;
      this.mInp.src = null;
      if (this.mPh < 0) {
         this.mIsSAlone = false;
      }

      this.mIsSAloneSet = false;
      if (var1.getCharacterStream() != null) {
         var2 = var1.getCharacterStream();
         this.xml(var2);
      } else if (var1.getByteStream() != null) {
         String var3;
         if (var1.getEncoding() != null) {
            var3 = var1.getEncoding().toUpperCase();
            if (var3.equals("UTF-16")) {
               var2 = this.bom(var1.getByteStream(), 'U');
            } else {
               var2 = this.enc(var3, var1.getByteStream());
            }

            this.xml(var2);
         } else {
            var2 = this.bom(var1.getByteStream(), ' ');
            if (var2 == null) {
               var2 = this.enc("UTF-8", var1.getByteStream());
               var3 = this.xml(var2);
               if (var3.startsWith("UTF-16")) {
                  this.panic("");
               }

               var2 = this.enc(var3, var1.getByteStream());
            } else {
               this.xml(var2);
            }
         }
      } else {
         this.panic("");
      }

      this.mInp.src = var2;
      this.mInp.pubid = var1.getPublicId();
      this.mInp.sysid = var1.getSystemId();
   }

   private Reader bom(InputStream var1, char var2) throws Exception {
      int var3 = var1.read();
      switch(var3) {
      case -1:
         this.mChars[this.mChIdx++] = '\uffff';
         return new ReaderUTF8(var1);
      case 239:
         if (var2 == 'U') {
            this.panic("");
         }

         if (var1.read() != 187) {
            this.panic("");
         }

         if (var1.read() != 191) {
            this.panic("");
         }

         return new ReaderUTF8(var1);
      case 254:
         if (var1.read() != 255) {
            this.panic("");
         }

         return new ReaderUTF16(var1, 'b');
      case 255:
         if (var1.read() != 254) {
            this.panic("");
         }

         return new ReaderUTF16(var1, 'l');
      default:
         if (var2 == 'U') {
            this.panic("");
         }

         switch(var3 & 240) {
         case 192:
         case 208:
            this.mChars[this.mChIdx++] = (char)((var3 & 31) << 6 | var1.read() & 63);
            break;
         case 224:
            this.mChars[this.mChIdx++] = (char)((var3 & 15) << 12 | (var1.read() & 63) << 6 | var1.read() & 63);
            break;
         case 240:
            throw new UnsupportedEncodingException();
         default:
            this.mChars[this.mChIdx++] = (char)var3;
         }

         return null;
      }
   }

   private String xml(Reader var1) throws Exception {
      String var2 = null;
      String var3 = "UTF-8";
      short var6;
      if (this.mChIdx != 0) {
         var6 = (short)(this.mChars[0] == '<' ? 1 : -1);
      } else {
         var6 = 0;
      }

      char var4;
      while(var6 >= 0 && this.mChIdx < this.mChars.length) {
         int var5;
         var4 = (var5 = var1.read()) >= 0 ? (char)var5 : '\uffff';
         this.mChars[this.mChIdx++] = var4;
         switch(var6) {
         case 0:
            switch(var4) {
            case '<':
               var6 = 1;
               continue;
            case '\ufeff':
               var4 = (var5 = var1.read()) >= 0 ? (char)var5 : '\uffff';
               this.mChars[this.mChIdx - 1] = var4;
               var6 = (short)(var4 == '<' ? 1 : -1);
               continue;
            default:
               var6 = -1;
               continue;
            }
         case 1:
            var6 = (short)(var4 == '?' ? 2 : -1);
            break;
         case 2:
            var6 = (short)(var4 == 'x' ? 3 : -1);
            break;
         case 3:
            var6 = (short)(var4 == 'm' ? 4 : -1);
            break;
         case 4:
            var6 = (short)(var4 == 'l' ? 5 : -1);
            break;
         case 5:
            switch(var4) {
            case '\t':
            case '\n':
            case '\r':
            case ' ':
               var6 = 6;
               continue;
            default:
               var6 = -1;
               continue;
            }
         case 6:
            switch(var4) {
            case '?':
               var6 = 7;
               continue;
            case '\uffff':
               var6 = -2;
            default:
               continue;
            }
         case 7:
            switch(var4) {
            case '>':
            case '\uffff':
               var6 = -2;
               continue;
            default:
               var6 = 6;
               continue;
            }
         default:
            this.panic("");
         }
      }

      this.mChLen = this.mChIdx;
      this.mChIdx = 0;
      if (var6 == -1) {
         return var3;
      } else {
         this.mChIdx = 5;
         byte var7 = 0;

         while(true) {
            while(var7 >= 0) {
               var4 = this.getch();
               switch(var7) {
               case 0:
                  if (this.chtyp(var4) != ' ') {
                     this.bkch();
                     var7 = 1;
                  }
                  break;
               case 1:
               case 2:
               case 3:
                  switch(this.chtyp(var4)) {
                  case ' ':
                     continue;
                  case '?':
                     if (var7 == 1) {
                        this.panic("");
                     }

                     this.bkch();
                     var7 = 4;
                     continue;
                  case 'A':
                  case '_':
                  case 'a':
                     this.bkch();
                     var2 = this.name(false).toLowerCase();
                     if ("version".equals(var2)) {
                        if (var7 != 1) {
                           this.panic("");
                        }

                        if (!"1.0".equals(this.eqstr('='))) {
                           this.panic("");
                        }

                        this.mInp.xmlver = 256;
                        var7 = 2;
                     } else if ("encoding".equals(var2)) {
                        if (var7 != 2) {
                           this.panic("");
                        }

                        this.mInp.xmlenc = this.eqstr('=').toUpperCase();
                        var3 = this.mInp.xmlenc;
                        var7 = 3;
                     } else {
                        if (!"standalone".equals(var2)) {
                           this.panic("");
                           continue;
                        }

                        if (var7 == 1 || this.mPh >= 0) {
                           this.panic("");
                        }

                        var2 = this.eqstr('=').toLowerCase();
                        if (var2.equals("yes")) {
                           this.mIsSAlone = true;
                        } else if (var2.equals("no")) {
                           this.mIsSAlone = false;
                        } else {
                           this.panic("");
                        }

                        this.mIsSAloneSet = true;
                        var7 = 4;
                     }
                     continue;
                  default:
                     this.panic("");
                     continue;
                  }
               case 4:
                  switch(this.chtyp(var4)) {
                  case ' ':
                     continue;
                  case '?':
                     if (this.getch() != '>') {
                        this.panic("");
                     }

                     if (this.mPh <= 0) {
                        this.mPh = 1;
                     }

                     var7 = -1;
                     continue;
                  default:
                     this.panic("");
                     continue;
                  }
               default:
                  this.panic("");
               }
            }

            return var3;
         }
      }
   }

   private Reader enc(String var1, InputStream var2) throws UnsupportedEncodingException {
      if (var1.equals("UTF-8")) {
         return new ReaderUTF8(var2);
      } else if (var1.equals("UTF-16LE")) {
         return new ReaderUTF16(var2, 'l');
      } else {
         return (Reader)(var1.equals("UTF-16BE") ? new ReaderUTF16(var2, 'b') : new InputStreamReader(var2, var1));
      }
   }

   protected void push(Input var1) {
      this.mInp.chLen = this.mChLen;
      this.mInp.chIdx = this.mChIdx;
      var1.next = this.mInp;
      this.mInp = var1;
      this.mChars = var1.chars;
      this.mChLen = var1.chLen;
      this.mChIdx = var1.chIdx;
   }

   protected void pop() {
      if (this.mInp.src != null) {
         try {
            this.mInp.src.close();
         } catch (IOException var2) {
         }

         this.mInp.src = null;
      }

      this.mInp = this.mInp.next;
      if (this.mInp != null) {
         this.mChars = this.mInp.chars;
         this.mChLen = this.mInp.chLen;
         this.mChIdx = this.mInp.chIdx;
      } else {
         this.mChars = null;
         this.mChLen = 0;
         this.mChIdx = 0;
      }

   }

   protected char chtyp(char var1) {
      if (var1 < 128) {
         return (char)asctyp[var1];
      } else {
         return (char)(var1 != '\uffff' ? 'X' : 'Z');
      }
   }

   protected char getch() throws IOException {
      if (this.mChIdx >= this.mChLen) {
         if (this.mInp.src == null) {
            this.pop();
            return this.getch();
         }

         int var1 = this.mInp.src.read(this.mChars, 0, this.mChars.length);
         if (var1 < 0) {
            if (this.mInp != this.mDoc) {
               this.pop();
               return this.getch();
            }

            this.mChars[0] = '\uffff';
            this.mChLen = 1;
         } else {
            this.mChLen = var1;
         }

         this.mChIdx = 0;
      }

      return this.mChars[this.mChIdx++];
   }

   protected void bkch() throws Exception {
      if (this.mChIdx <= 0) {
         this.panic("");
      }

      --this.mChIdx;
   }

   protected void setch(char var1) {
      this.mChars[this.mChIdx] = var1;
   }

   protected Pair find(Pair var1, char[] var2) {
      for(Pair var3 = var1; var3 != null; var3 = var3.next) {
         if (var3.eqname(var2)) {
            return var3;
         }
      }

      return null;
   }

   protected Pair pair(Pair var1) {
      Pair var2;
      if (this.mDltd != null) {
         var2 = this.mDltd;
         this.mDltd = var2.next;
      } else {
         var2 = new Pair();
      }

      var2.next = var1;
      return var2;
   }

   protected Pair del(Pair var1) {
      Pair var2 = var1.next;
      var1.name = null;
      var1.value = null;
      var1.chars = null;
      var1.list = null;
      var1.next = this.mDltd;
      this.mDltd = var1;
      return var2;
   }

   static {
      NONS[0] = 0;
      XML = new char[4];
      XML[0] = 4;
      XML[1] = 'x';
      XML[2] = 'm';
      XML[3] = 'l';
      XMLNS = new char[6];
      XMLNS[0] = 6;
      XMLNS[1] = 'x';
      XMLNS[2] = 'm';
      XMLNS[3] = 'l';
      XMLNS[4] = 'n';
      XMLNS[5] = 's';
      short var0 = 0;

      for(asctyp = new byte[128]; var0 < 32; asctyp[var0++] = 122) {
      }

      asctyp[9] = 32;
      asctyp[13] = 32;

      for(asctyp[10] = 32; var0 < 48; asctyp[var0] = (byte)(var0++)) {
      }

      while(var0 <= 57) {
         asctyp[var0++] = 100;
      }

      while(var0 < 65) {
         asctyp[var0] = (byte)(var0++);
      }

      while(var0 <= 90) {
         asctyp[var0++] = 65;
      }

      while(var0 < 97) {
         asctyp[var0] = (byte)(var0++);
      }

      while(var0 <= 122) {
         asctyp[var0++] = 97;
      }

      while(var0 < 128) {
         asctyp[var0] = (byte)(var0++);
      }

      nmttyp = new byte[128];

      for(var0 = 0; var0 < 48; ++var0) {
         nmttyp[var0] = -1;
      }

      while(var0 <= 57) {
         nmttyp[var0++] = 2;
      }

      while(var0 < 65) {
         nmttyp[var0++] = -1;
      }

      for(var0 = 91; var0 < 97; ++var0) {
         nmttyp[var0] = -1;
      }

      for(var0 = 123; var0 < 128; ++var0) {
         nmttyp[var0] = -1;
      }

      nmttyp[95] = 0;
      nmttyp[58] = 1;
      nmttyp[46] = 2;
      nmttyp[45] = 2;
      nmttyp[32] = 3;
      nmttyp[9] = 3;
      nmttyp[13] = 3;
      nmttyp[10] = 3;
   }
}
