package javax.swing.text.html;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class HTML {
   private static final Hashtable<String, HTML.Tag> tagHashtable = new Hashtable(73);
   private static final Hashtable<Object, HTML.Tag> scMapping = new Hashtable(8);
   public static final String NULL_ATTRIBUTE_VALUE = "#DEFAULT";
   private static final Hashtable<String, HTML.Attribute> attHashtable;

   public static HTML.Tag[] getAllTags() {
      HTML.Tag[] var0 = new HTML.Tag[HTML.Tag.allTags.length];
      System.arraycopy(HTML.Tag.allTags, 0, var0, 0, HTML.Tag.allTags.length);
      return var0;
   }

   public static HTML.Tag getTag(String var0) {
      HTML.Tag var1 = (HTML.Tag)tagHashtable.get(var0);
      return var1 == null ? null : var1;
   }

   static HTML.Tag getTagForStyleConstantsKey(StyleConstants var0) {
      return (HTML.Tag)scMapping.get(var0);
   }

   public static int getIntegerAttributeValue(AttributeSet var0, HTML.Attribute var1, int var2) {
      int var3 = var2;
      String var4 = (String)var0.getAttribute(var1);
      if (var4 != null) {
         try {
            var3 = Integer.valueOf(var4);
         } catch (NumberFormatException var6) {
            var3 = var2;
         }
      }

      return var3;
   }

   public static HTML.Attribute[] getAllAttributeKeys() {
      HTML.Attribute[] var0 = new HTML.Attribute[HTML.Attribute.allAttributes.length];
      System.arraycopy(HTML.Attribute.allAttributes, 0, var0, 0, HTML.Attribute.allAttributes.length);
      return var0;
   }

   public static HTML.Attribute getAttributeKey(String var0) {
      HTML.Attribute var1 = (HTML.Attribute)attHashtable.get(var0);
      return var1 == null ? null : var1;
   }

   static {
      int var0;
      for(var0 = 0; var0 < HTML.Tag.allTags.length; ++var0) {
         tagHashtable.put(HTML.Tag.allTags[var0].toString(), HTML.Tag.allTags[var0]);
         StyleContext.registerStaticAttributeKey(HTML.Tag.allTags[var0]);
      }

      StyleContext.registerStaticAttributeKey(HTML.Tag.IMPLIED);
      StyleContext.registerStaticAttributeKey(HTML.Tag.CONTENT);
      StyleContext.registerStaticAttributeKey(HTML.Tag.COMMENT);

      for(var0 = 0; var0 < HTML.Attribute.allAttributes.length; ++var0) {
         StyleContext.registerStaticAttributeKey(HTML.Attribute.allAttributes[var0]);
      }

      StyleContext.registerStaticAttributeKey("#DEFAULT");
      scMapping.put(StyleConstants.Bold, HTML.Tag.B);
      scMapping.put(StyleConstants.Italic, HTML.Tag.I);
      scMapping.put(StyleConstants.Underline, HTML.Tag.U);
      scMapping.put(StyleConstants.StrikeThrough, HTML.Tag.STRIKE);
      scMapping.put(StyleConstants.Superscript, HTML.Tag.SUP);
      scMapping.put(StyleConstants.Subscript, HTML.Tag.SUB);
      scMapping.put(StyleConstants.FontFamily, HTML.Tag.FONT);
      scMapping.put(StyleConstants.FontSize, HTML.Tag.FONT);
      attHashtable = new Hashtable(77);

      for(var0 = 0; var0 < HTML.Attribute.allAttributes.length; ++var0) {
         attHashtable.put(HTML.Attribute.allAttributes[var0].toString(), HTML.Attribute.allAttributes[var0]);
      }

   }

   public static final class Attribute {
      private String name;
      public static final HTML.Attribute SIZE = new HTML.Attribute("size");
      public static final HTML.Attribute COLOR = new HTML.Attribute("color");
      public static final HTML.Attribute CLEAR = new HTML.Attribute("clear");
      public static final HTML.Attribute BACKGROUND = new HTML.Attribute("background");
      public static final HTML.Attribute BGCOLOR = new HTML.Attribute("bgcolor");
      public static final HTML.Attribute TEXT = new HTML.Attribute("text");
      public static final HTML.Attribute LINK = new HTML.Attribute("link");
      public static final HTML.Attribute VLINK = new HTML.Attribute("vlink");
      public static final HTML.Attribute ALINK = new HTML.Attribute("alink");
      public static final HTML.Attribute WIDTH = new HTML.Attribute("width");
      public static final HTML.Attribute HEIGHT = new HTML.Attribute("height");
      public static final HTML.Attribute ALIGN = new HTML.Attribute("align");
      public static final HTML.Attribute NAME = new HTML.Attribute("name");
      public static final HTML.Attribute HREF = new HTML.Attribute("href");
      public static final HTML.Attribute REL = new HTML.Attribute("rel");
      public static final HTML.Attribute REV = new HTML.Attribute("rev");
      public static final HTML.Attribute TITLE = new HTML.Attribute("title");
      public static final HTML.Attribute TARGET = new HTML.Attribute("target");
      public static final HTML.Attribute SHAPE = new HTML.Attribute("shape");
      public static final HTML.Attribute COORDS = new HTML.Attribute("coords");
      public static final HTML.Attribute ISMAP = new HTML.Attribute("ismap");
      public static final HTML.Attribute NOHREF = new HTML.Attribute("nohref");
      public static final HTML.Attribute ALT = new HTML.Attribute("alt");
      public static final HTML.Attribute ID = new HTML.Attribute("id");
      public static final HTML.Attribute SRC = new HTML.Attribute("src");
      public static final HTML.Attribute HSPACE = new HTML.Attribute("hspace");
      public static final HTML.Attribute VSPACE = new HTML.Attribute("vspace");
      public static final HTML.Attribute USEMAP = new HTML.Attribute("usemap");
      public static final HTML.Attribute LOWSRC = new HTML.Attribute("lowsrc");
      public static final HTML.Attribute CODEBASE = new HTML.Attribute("codebase");
      public static final HTML.Attribute CODE = new HTML.Attribute("code");
      public static final HTML.Attribute ARCHIVE = new HTML.Attribute("archive");
      public static final HTML.Attribute VALUE = new HTML.Attribute("value");
      public static final HTML.Attribute VALUETYPE = new HTML.Attribute("valuetype");
      public static final HTML.Attribute TYPE = new HTML.Attribute("type");
      public static final HTML.Attribute CLASS = new HTML.Attribute("class");
      public static final HTML.Attribute STYLE = new HTML.Attribute("style");
      public static final HTML.Attribute LANG = new HTML.Attribute("lang");
      public static final HTML.Attribute FACE = new HTML.Attribute("face");
      public static final HTML.Attribute DIR = new HTML.Attribute("dir");
      public static final HTML.Attribute DECLARE = new HTML.Attribute("declare");
      public static final HTML.Attribute CLASSID = new HTML.Attribute("classid");
      public static final HTML.Attribute DATA = new HTML.Attribute("data");
      public static final HTML.Attribute CODETYPE = new HTML.Attribute("codetype");
      public static final HTML.Attribute STANDBY = new HTML.Attribute("standby");
      public static final HTML.Attribute BORDER = new HTML.Attribute("border");
      public static final HTML.Attribute SHAPES = new HTML.Attribute("shapes");
      public static final HTML.Attribute NOSHADE = new HTML.Attribute("noshade");
      public static final HTML.Attribute COMPACT = new HTML.Attribute("compact");
      public static final HTML.Attribute START = new HTML.Attribute("start");
      public static final HTML.Attribute ACTION = new HTML.Attribute("action");
      public static final HTML.Attribute METHOD = new HTML.Attribute("method");
      public static final HTML.Attribute ENCTYPE = new HTML.Attribute("enctype");
      public static final HTML.Attribute CHECKED = new HTML.Attribute("checked");
      public static final HTML.Attribute MAXLENGTH = new HTML.Attribute("maxlength");
      public static final HTML.Attribute MULTIPLE = new HTML.Attribute("multiple");
      public static final HTML.Attribute SELECTED = new HTML.Attribute("selected");
      public static final HTML.Attribute ROWS = new HTML.Attribute("rows");
      public static final HTML.Attribute COLS = new HTML.Attribute("cols");
      public static final HTML.Attribute DUMMY = new HTML.Attribute("dummy");
      public static final HTML.Attribute CELLSPACING = new HTML.Attribute("cellspacing");
      public static final HTML.Attribute CELLPADDING = new HTML.Attribute("cellpadding");
      public static final HTML.Attribute VALIGN = new HTML.Attribute("valign");
      public static final HTML.Attribute HALIGN = new HTML.Attribute("halign");
      public static final HTML.Attribute NOWRAP = new HTML.Attribute("nowrap");
      public static final HTML.Attribute ROWSPAN = new HTML.Attribute("rowspan");
      public static final HTML.Attribute COLSPAN = new HTML.Attribute("colspan");
      public static final HTML.Attribute PROMPT = new HTML.Attribute("prompt");
      public static final HTML.Attribute HTTPEQUIV = new HTML.Attribute("http-equiv");
      public static final HTML.Attribute CONTENT = new HTML.Attribute("content");
      public static final HTML.Attribute LANGUAGE = new HTML.Attribute("language");
      public static final HTML.Attribute VERSION = new HTML.Attribute("version");
      public static final HTML.Attribute N = new HTML.Attribute("n");
      public static final HTML.Attribute FRAMEBORDER = new HTML.Attribute("frameborder");
      public static final HTML.Attribute MARGINWIDTH = new HTML.Attribute("marginwidth");
      public static final HTML.Attribute MARGINHEIGHT = new HTML.Attribute("marginheight");
      public static final HTML.Attribute SCROLLING = new HTML.Attribute("scrolling");
      public static final HTML.Attribute NORESIZE = new HTML.Attribute("noresize");
      public static final HTML.Attribute ENDTAG = new HTML.Attribute("endtag");
      public static final HTML.Attribute COMMENT = new HTML.Attribute("comment");
      static final HTML.Attribute MEDIA = new HTML.Attribute("media");
      static final HTML.Attribute[] allAttributes;

      Attribute(String var1) {
         this.name = var1;
      }

      public String toString() {
         return this.name;
      }

      static {
         allAttributes = new HTML.Attribute[]{FACE, COMMENT, SIZE, COLOR, CLEAR, BACKGROUND, BGCOLOR, TEXT, LINK, VLINK, ALINK, WIDTH, HEIGHT, ALIGN, NAME, HREF, REL, REV, TITLE, TARGET, SHAPE, COORDS, ISMAP, NOHREF, ALT, ID, SRC, HSPACE, VSPACE, USEMAP, LOWSRC, CODEBASE, CODE, ARCHIVE, VALUE, VALUETYPE, TYPE, CLASS, STYLE, LANG, DIR, DECLARE, CLASSID, DATA, CODETYPE, STANDBY, BORDER, SHAPES, NOSHADE, COMPACT, START, ACTION, METHOD, ENCTYPE, CHECKED, MAXLENGTH, MULTIPLE, SELECTED, ROWS, COLS, DUMMY, CELLSPACING, CELLPADDING, VALIGN, HALIGN, NOWRAP, ROWSPAN, COLSPAN, PROMPT, HTTPEQUIV, CONTENT, LANGUAGE, VERSION, N, FRAMEBORDER, MARGINWIDTH, MARGINHEIGHT, SCROLLING, NORESIZE, MEDIA, ENDTAG};
      }
   }

   public static class UnknownTag extends HTML.Tag implements Serializable {
      public UnknownTag(String var1) {
         super(var1);
      }

      public int hashCode() {
         return this.toString().hashCode();
      }

      public boolean equals(Object var1) {
         return var1 instanceof HTML.UnknownTag ? this.toString().equals(var1.toString()) : false;
      }

      private void writeObject(ObjectOutputStream var1) throws IOException {
         var1.defaultWriteObject();
         var1.writeBoolean(this.blockTag);
         var1.writeBoolean(this.breakTag);
         var1.writeBoolean(this.unknown);
         var1.writeObject(this.name);
      }

      private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
         var1.defaultReadObject();
         this.blockTag = var1.readBoolean();
         this.breakTag = var1.readBoolean();
         this.unknown = var1.readBoolean();
         this.name = (String)var1.readObject();
      }
   }

   public static class Tag {
      boolean blockTag;
      boolean breakTag;
      String name;
      boolean unknown;
      public static final HTML.Tag A = new HTML.Tag("a");
      public static final HTML.Tag ADDRESS = new HTML.Tag("address");
      public static final HTML.Tag APPLET = new HTML.Tag("applet");
      public static final HTML.Tag AREA = new HTML.Tag("area");
      public static final HTML.Tag B = new HTML.Tag("b");
      public static final HTML.Tag BASE = new HTML.Tag("base");
      public static final HTML.Tag BASEFONT = new HTML.Tag("basefont");
      public static final HTML.Tag BIG = new HTML.Tag("big");
      public static final HTML.Tag BLOCKQUOTE = new HTML.Tag("blockquote", true, true);
      public static final HTML.Tag BODY = new HTML.Tag("body", true, true);
      public static final HTML.Tag BR = new HTML.Tag("br", true, false);
      public static final HTML.Tag CAPTION = new HTML.Tag("caption");
      public static final HTML.Tag CENTER = new HTML.Tag("center", true, false);
      public static final HTML.Tag CITE = new HTML.Tag("cite");
      public static final HTML.Tag CODE = new HTML.Tag("code");
      public static final HTML.Tag DD = new HTML.Tag("dd", true, true);
      public static final HTML.Tag DFN = new HTML.Tag("dfn");
      public static final HTML.Tag DIR = new HTML.Tag("dir", true, true);
      public static final HTML.Tag DIV = new HTML.Tag("div", true, true);
      public static final HTML.Tag DL = new HTML.Tag("dl", true, true);
      public static final HTML.Tag DT = new HTML.Tag("dt", true, true);
      public static final HTML.Tag EM = new HTML.Tag("em");
      public static final HTML.Tag FONT = new HTML.Tag("font");
      public static final HTML.Tag FORM = new HTML.Tag("form", true, false);
      public static final HTML.Tag FRAME = new HTML.Tag("frame");
      public static final HTML.Tag FRAMESET = new HTML.Tag("frameset");
      public static final HTML.Tag H1 = new HTML.Tag("h1", true, true);
      public static final HTML.Tag H2 = new HTML.Tag("h2", true, true);
      public static final HTML.Tag H3 = new HTML.Tag("h3", true, true);
      public static final HTML.Tag H4 = new HTML.Tag("h4", true, true);
      public static final HTML.Tag H5 = new HTML.Tag("h5", true, true);
      public static final HTML.Tag H6 = new HTML.Tag("h6", true, true);
      public static final HTML.Tag HEAD = new HTML.Tag("head", true, true);
      public static final HTML.Tag HR = new HTML.Tag("hr", true, false);
      public static final HTML.Tag HTML = new HTML.Tag("html", true, false);
      public static final HTML.Tag I = new HTML.Tag("i");
      public static final HTML.Tag IMG = new HTML.Tag("img");
      public static final HTML.Tag INPUT = new HTML.Tag("input");
      public static final HTML.Tag ISINDEX = new HTML.Tag("isindex", true, false);
      public static final HTML.Tag KBD = new HTML.Tag("kbd");
      public static final HTML.Tag LI = new HTML.Tag("li", true, true);
      public static final HTML.Tag LINK = new HTML.Tag("link");
      public static final HTML.Tag MAP = new HTML.Tag("map");
      public static final HTML.Tag MENU = new HTML.Tag("menu", true, true);
      public static final HTML.Tag META = new HTML.Tag("meta");
      static final HTML.Tag NOBR = new HTML.Tag("nobr");
      public static final HTML.Tag NOFRAMES = new HTML.Tag("noframes", true, true);
      public static final HTML.Tag OBJECT = new HTML.Tag("object");
      public static final HTML.Tag OL = new HTML.Tag("ol", true, true);
      public static final HTML.Tag OPTION = new HTML.Tag("option");
      public static final HTML.Tag P = new HTML.Tag("p", true, true);
      public static final HTML.Tag PARAM = new HTML.Tag("param");
      public static final HTML.Tag PRE = new HTML.Tag("pre", true, true);
      public static final HTML.Tag SAMP = new HTML.Tag("samp");
      public static final HTML.Tag SCRIPT = new HTML.Tag("script");
      public static final HTML.Tag SELECT = new HTML.Tag("select");
      public static final HTML.Tag SMALL = new HTML.Tag("small");
      public static final HTML.Tag SPAN = new HTML.Tag("span");
      public static final HTML.Tag STRIKE = new HTML.Tag("strike");
      public static final HTML.Tag S = new HTML.Tag("s");
      public static final HTML.Tag STRONG = new HTML.Tag("strong");
      public static final HTML.Tag STYLE = new HTML.Tag("style");
      public static final HTML.Tag SUB = new HTML.Tag("sub");
      public static final HTML.Tag SUP = new HTML.Tag("sup");
      public static final HTML.Tag TABLE = new HTML.Tag("table", false, true);
      public static final HTML.Tag TD = new HTML.Tag("td", true, true);
      public static final HTML.Tag TEXTAREA = new HTML.Tag("textarea");
      public static final HTML.Tag TH = new HTML.Tag("th", true, true);
      public static final HTML.Tag TITLE = new HTML.Tag("title", true, true);
      public static final HTML.Tag TR = new HTML.Tag("tr", false, true);
      public static final HTML.Tag TT = new HTML.Tag("tt");
      public static final HTML.Tag U = new HTML.Tag("u");
      public static final HTML.Tag UL = new HTML.Tag("ul", true, true);
      public static final HTML.Tag VAR = new HTML.Tag("var");
      public static final HTML.Tag IMPLIED = new HTML.Tag("p-implied");
      public static final HTML.Tag CONTENT = new HTML.Tag("content");
      public static final HTML.Tag COMMENT = new HTML.Tag("comment");
      static final HTML.Tag[] allTags;

      public Tag() {
      }

      protected Tag(String var1) {
         this(var1, false, false);
      }

      protected Tag(String var1, boolean var2, boolean var3) {
         this.name = var1;
         this.breakTag = var2;
         this.blockTag = var3;
      }

      public boolean isBlock() {
         return this.blockTag;
      }

      public boolean breaksFlow() {
         return this.breakTag;
      }

      public boolean isPreformatted() {
         return this == PRE || this == TEXTAREA;
      }

      public String toString() {
         return this.name;
      }

      boolean isParagraph() {
         return this == P || this == IMPLIED || this == DT || this == H1 || this == H2 || this == H3 || this == H4 || this == H5 || this == H6;
      }

      static {
         allTags = new HTML.Tag[]{A, ADDRESS, APPLET, AREA, B, BASE, BASEFONT, BIG, BLOCKQUOTE, BODY, BR, CAPTION, CENTER, CITE, CODE, DD, DFN, DIR, DIV, DL, DT, EM, FONT, FORM, FRAME, FRAMESET, H1, H2, H3, H4, H5, H6, HEAD, HR, HTML, I, IMG, INPUT, ISINDEX, KBD, LI, LINK, MAP, MENU, META, NOBR, NOFRAMES, OBJECT, OL, OPTION, P, PARAM, PRE, SAMP, SCRIPT, SELECT, SMALL, SPAN, STRIKE, S, STRONG, STYLE, SUB, SUP, TABLE, TD, TEXTAREA, TH, TITLE, TR, TT, U, UL, VAR};
         HTML.getTag("html");
      }
   }
}
