package javax.swing.plaf.synth;

import com.sun.beans.decoder.DocumentHandler;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.PatternSyntaxException;
import javax.swing.ImageIcon;
import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.UIResource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import sun.reflect.misc.ReflectUtil;
import sun.swing.plaf.synth.DefaultSynthStyle;

class SynthParser extends DefaultHandler {
   private static final String ELEMENT_SYNTH = "synth";
   private static final String ELEMENT_STYLE = "style";
   private static final String ELEMENT_STATE = "state";
   private static final String ELEMENT_FONT = "font";
   private static final String ELEMENT_COLOR = "color";
   private static final String ELEMENT_IMAGE_PAINTER = "imagePainter";
   private static final String ELEMENT_PAINTER = "painter";
   private static final String ELEMENT_PROPERTY = "property";
   private static final String ELEMENT_SYNTH_GRAPHICS = "graphicsUtils";
   private static final String ELEMENT_IMAGE_ICON = "imageIcon";
   private static final String ELEMENT_BIND = "bind";
   private static final String ELEMENT_BIND_KEY = "bindKey";
   private static final String ELEMENT_INSETS = "insets";
   private static final String ELEMENT_OPAQUE = "opaque";
   private static final String ELEMENT_DEFAULTS_PROPERTY = "defaultsProperty";
   private static final String ELEMENT_INPUT_MAP = "inputMap";
   private static final String ATTRIBUTE_ACTION = "action";
   private static final String ATTRIBUTE_ID = "id";
   private static final String ATTRIBUTE_IDREF = "idref";
   private static final String ATTRIBUTE_CLONE = "clone";
   private static final String ATTRIBUTE_VALUE = "value";
   private static final String ATTRIBUTE_NAME = "name";
   private static final String ATTRIBUTE_STYLE = "style";
   private static final String ATTRIBUTE_SIZE = "size";
   private static final String ATTRIBUTE_TYPE = "type";
   private static final String ATTRIBUTE_TOP = "top";
   private static final String ATTRIBUTE_LEFT = "left";
   private static final String ATTRIBUTE_BOTTOM = "bottom";
   private static final String ATTRIBUTE_RIGHT = "right";
   private static final String ATTRIBUTE_KEY = "key";
   private static final String ATTRIBUTE_SOURCE_INSETS = "sourceInsets";
   private static final String ATTRIBUTE_DEST_INSETS = "destinationInsets";
   private static final String ATTRIBUTE_PATH = "path";
   private static final String ATTRIBUTE_STRETCH = "stretch";
   private static final String ATTRIBUTE_PAINT_CENTER = "paintCenter";
   private static final String ATTRIBUTE_METHOD = "method";
   private static final String ATTRIBUTE_DIRECTION = "direction";
   private static final String ATTRIBUTE_CENTER = "center";
   private DocumentHandler _handler;
   private int _depth;
   private DefaultSynthStyleFactory _factory;
   private List<ParsedSynthStyle.StateInfo> _stateInfos = new ArrayList();
   private ParsedSynthStyle _style;
   private ParsedSynthStyle.StateInfo _stateInfo;
   private List<String> _inputMapBindings = new ArrayList();
   private String _inputMapID;
   private Map<String, Object> _mapping = new HashMap();
   private URL _urlResourceBase;
   private Class<?> _classResourceBase;
   private List<ColorType> _colorTypes = new ArrayList();
   private Map<String, Object> _defaultsMap;
   private List<ParsedSynthStyle.PainterInfo> _stylePainters = new ArrayList();
   private List<ParsedSynthStyle.PainterInfo> _statePainters = new ArrayList();

   public void parse(InputStream var1, DefaultSynthStyleFactory var2, URL var3, Class<?> var4, Map<String, Object> var5) throws ParseException, IllegalArgumentException {
      if (var1 != null && var2 != null && (var3 != null || var4 != null)) {
         assert var3 == null || var4 == null;

         this._factory = var2;
         this._classResourceBase = var4;
         this._urlResourceBase = var3;
         this._defaultsMap = var5;

         try {
            SAXParser var6 = SAXParserFactory.newInstance().newSAXParser();
            var6.parse((InputStream)(new BufferedInputStream(var1)), (DefaultHandler)this);
         } catch (ParserConfigurationException var12) {
            throw new ParseException("Error parsing: " + var12, 0);
         } catch (SAXException var13) {
            throw new ParseException("Error parsing: " + var13 + " " + var13.getException(), 0);
         } catch (IOException var14) {
            throw new ParseException("Error parsing: " + var14, 0);
         } finally {
            this.reset();
         }

      } else {
         throw new IllegalArgumentException("You must supply an InputStream, StyleFactory and Class or URL");
      }
   }

   private URL getResource(String var1) {
      if (this._classResourceBase != null) {
         return this._classResourceBase.getResource(var1);
      } else {
         try {
            return new URL(this._urlResourceBase, var1);
         } catch (MalformedURLException var3) {
            return null;
         }
      }
   }

   private void reset() {
      this._handler = null;
      this._depth = 0;
      this._mapping.clear();
      this._stateInfos.clear();
      this._colorTypes.clear();
      this._statePainters.clear();
      this._stylePainters.clear();
   }

   private boolean isForwarding() {
      return this._depth > 0;
   }

   private DocumentHandler getHandler() {
      if (this._handler == null) {
         this._handler = new DocumentHandler();
         if (this._urlResourceBase != null) {
            URL[] var1 = new URL[]{this.getResource(".")};
            ClassLoader var2 = Thread.currentThread().getContextClassLoader();
            URLClassLoader var3 = new URLClassLoader(var1, var2);
            this._handler.setClassLoader(var3);
         } else {
            this._handler.setClassLoader(this._classResourceBase.getClassLoader());
         }

         Iterator var4 = this._mapping.keySet().iterator();

         while(var4.hasNext()) {
            String var5 = (String)var4.next();
            this._handler.setVariable(var5, this._mapping.get(var5));
         }
      }

      return this._handler;
   }

   private Object checkCast(Object var1, Class var2) throws SAXException {
      if (!var2.isInstance(var1)) {
         throw new SAXException("Expected type " + var2 + " got " + var1.getClass());
      } else {
         return var1;
      }
   }

   private Object lookup(String var1, Class var2) throws SAXException {
      if (this._handler != null && this._handler.hasVariable(var1)) {
         return this.checkCast(this._handler.getVariable(var1), var2);
      } else {
         Object var3 = this._mapping.get(var1);
         if (var3 == null) {
            throw new SAXException("ID " + var1 + " has not been defined");
         } else {
            return this.checkCast(var3, var2);
         }
      }
   }

   private void register(String var1, Object var2) throws SAXException {
      if (var1 != null) {
         if (this._mapping.get(var1) != null || this._handler != null && this._handler.hasVariable(var1)) {
            throw new SAXException("ID " + var1 + " is already defined");
         }

         if (this._handler != null) {
            this._handler.setVariable(var1, var2);
         } else {
            this._mapping.put(var1, var2);
         }
      }

   }

   private int nextInt(StringTokenizer var1, String var2) throws SAXException {
      if (!var1.hasMoreTokens()) {
         throw new SAXException(var2);
      } else {
         try {
            return Integer.parseInt(var1.nextToken());
         } catch (NumberFormatException var4) {
            throw new SAXException(var2);
         }
      }
   }

   private Insets parseInsets(String var1, String var2) throws SAXException {
      StringTokenizer var3 = new StringTokenizer(var1);
      return new Insets(this.nextInt(var3, var2), this.nextInt(var3, var2), this.nextInt(var3, var2), this.nextInt(var3, var2));
   }

   private void startStyle(Attributes var1) throws SAXException {
      String var2 = null;
      this._style = null;

      for(int var3 = var1.getLength() - 1; var3 >= 0; --var3) {
         String var4 = var1.getQName(var3);
         if (var4.equals("clone")) {
            this._style = (ParsedSynthStyle)((ParsedSynthStyle)this.lookup(var1.getValue(var3), ParsedSynthStyle.class)).clone();
         } else if (var4.equals("id")) {
            var2 = var1.getValue(var3);
         }
      }

      if (this._style == null) {
         this._style = new ParsedSynthStyle();
      }

      this.register(var2, this._style);
   }

   private void endStyle() {
      int var1 = this._stylePainters.size();
      if (var1 > 0) {
         this._style.setPainters((ParsedSynthStyle.PainterInfo[])this._stylePainters.toArray(new ParsedSynthStyle.PainterInfo[var1]));
         this._stylePainters.clear();
      }

      var1 = this._stateInfos.size();
      if (var1 > 0) {
         this._style.setStateInfo((DefaultSynthStyle.StateInfo[])this._stateInfos.toArray(new ParsedSynthStyle.StateInfo[var1]));
         this._stateInfos.clear();
      }

      this._style = null;
   }

   private void startState(Attributes var1) throws SAXException {
      Object var2 = null;
      int var3 = 0;
      String var4 = null;
      this._stateInfo = null;

      for(int var5 = var1.getLength() - 1; var5 >= 0; --var5) {
         String var6 = var1.getQName(var5);
         if (var6.equals("id")) {
            var4 = var1.getValue(var5);
         } else if (var6.equals("idref")) {
            this._stateInfo = (ParsedSynthStyle.StateInfo)this.lookup(var1.getValue(var5), ParsedSynthStyle.StateInfo.class);
         } else if (var6.equals("clone")) {
            this._stateInfo = (ParsedSynthStyle.StateInfo)((ParsedSynthStyle.StateInfo)this.lookup(var1.getValue(var5), ParsedSynthStyle.StateInfo.class)).clone();
         } else if (var6.equals("value")) {
            StringTokenizer var7 = new StringTokenizer(var1.getValue(var5));

            while(var7.hasMoreTokens()) {
               String var8 = var7.nextToken().toUpperCase().intern();
               if (var8 == "ENABLED") {
                  var3 |= 1;
               } else if (var8 == "MOUSE_OVER") {
                  var3 |= 2;
               } else if (var8 == "PRESSED") {
                  var3 |= 4;
               } else if (var8 == "DISABLED") {
                  var3 |= 8;
               } else if (var8 == "FOCUSED") {
                  var3 |= 256;
               } else if (var8 == "SELECTED") {
                  var3 |= 512;
               } else if (var8 == "DEFAULT") {
                  var3 |= 1024;
               } else if (var8 != "AND") {
                  throw new SAXException("Unknown state: " + var3);
               }
            }
         }
      }

      if (this._stateInfo == null) {
         this._stateInfo = new ParsedSynthStyle.StateInfo();
      }

      this._stateInfo.setComponentState(var3);
      this.register(var4, this._stateInfo);
      this._stateInfos.add(this._stateInfo);
   }

   private void endState() {
      int var1 = this._statePainters.size();
      if (var1 > 0) {
         this._stateInfo.setPainters((ParsedSynthStyle.PainterInfo[])this._statePainters.toArray(new ParsedSynthStyle.PainterInfo[var1]));
         this._statePainters.clear();
      }

      this._stateInfo = null;
   }

   private void startFont(Attributes var1) throws SAXException {
      Object var2 = null;
      int var3 = 0;
      int var4 = 0;
      String var5 = null;
      String var6 = null;

      for(int var7 = var1.getLength() - 1; var7 >= 0; --var7) {
         String var8 = var1.getQName(var7);
         if (var8.equals("id")) {
            var5 = var1.getValue(var7);
         } else if (var8.equals("idref")) {
            var2 = (Font)this.lookup(var1.getValue(var7), Font.class);
         } else if (var8.equals("name")) {
            var6 = var1.getValue(var7);
         } else if (var8.equals("size")) {
            try {
               var4 = Integer.parseInt(var1.getValue(var7));
            } catch (NumberFormatException var11) {
               throw new SAXException("Invalid font size: " + var1.getValue(var7));
            }
         } else if (var8.equals("style")) {
            StringTokenizer var9 = new StringTokenizer(var1.getValue(var7));

            while(var9.hasMoreTokens()) {
               String var10 = var9.nextToken().intern();
               if (var10 == "BOLD") {
                  var3 = (var3 | 0) ^ 0 | 1;
               } else if (var10 == "ITALIC") {
                  var3 |= 2;
               }
            }
         }
      }

      if (var2 == null) {
         if (var6 == null) {
            throw new SAXException("You must define a name for the font");
         }

         if (var4 == 0) {
            throw new SAXException("You must define a size for the font");
         }

         var2 = new FontUIResource(var6, var3, var4);
      } else if (var6 != null || var4 != 0 || var3 != 0) {
         throw new SAXException("Name, size and style are not for use with idref");
      }

      this.register(var5, var2);
      if (this._stateInfo != null) {
         this._stateInfo.setFont((Font)var2);
      } else if (this._style != null) {
         this._style.setFont((Font)var2);
      }

   }

   private void startColor(Attributes var1) throws SAXException {
      Object var2 = null;
      String var3 = null;
      this._colorTypes.clear();

      for(int var4 = var1.getLength() - 1; var4 >= 0; --var4) {
         String var5 = var1.getQName(var4);
         if (var5.equals("id")) {
            var3 = var1.getValue(var4);
         } else if (var5.equals("idref")) {
            var2 = (Color)this.lookup(var1.getValue(var4), Color.class);
         } else if (!var5.equals("name")) {
            if (var5.equals("value")) {
               String var20 = var1.getValue(var4);
               if (var20.startsWith("#")) {
                  try {
                     int var25 = var20.length();
                     int var23;
                     boolean var24;
                     if (var25 < 8) {
                        var23 = Integer.decode(var20);
                        var24 = false;
                     } else if (var25 == 8) {
                        var23 = Integer.decode(var20);
                        var24 = true;
                     } else {
                        if (var25 != 9) {
                           throw new SAXException("Invalid Color value: " + var20);
                        }

                        int var10 = Integer.decode('#' + var20.substring(3, 9));
                        int var11 = Integer.decode(var20.substring(0, 3));
                        var23 = var11 << 24 | var10;
                        var24 = true;
                     }

                     var2 = new ColorUIResource(new Color(var23, var24));
                  } catch (NumberFormatException var17) {
                     throw new SAXException("Invalid Color value: " + var20);
                  }
               } else {
                  try {
                     var2 = new ColorUIResource((Color)Color.class.getField(var20.toUpperCase()).get(Color.class));
                  } catch (NoSuchFieldException var15) {
                     throw new SAXException("Invalid color name: " + var20);
                  } catch (IllegalAccessException var16) {
                     throw new SAXException("Invalid color name: " + var20);
                  }
               }
            } else if (var5.equals("type")) {
               StringTokenizer var6 = new StringTokenizer(var1.getValue(var4));

               while(var6.hasMoreTokens()) {
                  String var7 = var6.nextToken();
                  int var8 = var7.lastIndexOf(46);
                  Class var9;
                  if (var8 == -1) {
                     var9 = ColorType.class;
                     var8 = 0;
                  } else {
                     try {
                        var9 = ReflectUtil.forName(var7.substring(0, var8));
                     } catch (ClassNotFoundException var14) {
                        throw new SAXException("Unknown class: " + var7.substring(0, var8));
                     }

                     ++var8;
                  }

                  try {
                     this._colorTypes.add((ColorType)this.checkCast(var9.getField(var7.substring(var8)).get(var9), ColorType.class));
                  } catch (NoSuchFieldException var12) {
                     throw new SAXException("Unable to find color type: " + var7);
                  } catch (IllegalAccessException var13) {
                     throw new SAXException("Unable to find color type: " + var7);
                  }
               }
            }
         }
      }

      if (var2 == null) {
         throw new SAXException("color: you must specificy a value");
      } else {
         this.register(var3, var2);
         if (this._stateInfo != null && this._colorTypes.size() > 0) {
            Color[] var18 = this._stateInfo.getColors();
            int var19 = 0;

            int var21;
            for(var21 = this._colorTypes.size() - 1; var21 >= 0; --var21) {
               var19 = Math.max(var19, ((ColorType)this._colorTypes.get(var21)).getID());
            }

            if (var18 == null || var18.length <= var19) {
               Color[] var22 = new Color[var19 + 1];
               if (var18 != null) {
                  System.arraycopy(var18, 0, var22, 0, var18.length);
               }

               var18 = var22;
            }

            for(var21 = this._colorTypes.size() - 1; var21 >= 0; --var21) {
               var18[((ColorType)this._colorTypes.get(var21)).getID()] = (Color)var2;
            }

            this._stateInfo.setColors(var18);
         }

      }
   }

   private void startProperty(Attributes var1, Object var2) throws SAXException {
      Object var3 = null;
      String var4 = null;
      byte var5 = 0;
      String var6 = null;

      for(int var7 = var1.getLength() - 1; var7 >= 0; --var7) {
         String var8 = var1.getQName(var7);
         if (var8.equals("type")) {
            String var9 = var1.getValue(var7).toUpperCase();
            if (var9.equals("IDREF")) {
               var5 = 0;
            } else if (var9.equals("BOOLEAN")) {
               var5 = 1;
            } else if (var9.equals("DIMENSION")) {
               var5 = 2;
            } else if (var9.equals("INSETS")) {
               var5 = 3;
            } else if (var9.equals("INTEGER")) {
               var5 = 4;
            } else {
               if (!var9.equals("STRING")) {
                  throw new SAXException(var2 + " unknown type, useidref, boolean, dimension, insets or integer");
               }

               var5 = 5;
            }
         } else if (var8.equals("value")) {
            var6 = var1.getValue(var7);
         } else if (var8.equals("key")) {
            var4 = var1.getValue(var7);
         }
      }

      if (var6 != null) {
         switch(var5) {
         case 0:
            var3 = this.lookup(var6, Object.class);
            break;
         case 1:
            if (var6.toUpperCase().equals("TRUE")) {
               var3 = Boolean.TRUE;
            } else {
               var3 = Boolean.FALSE;
            }
            break;
         case 2:
            StringTokenizer var11 = new StringTokenizer(var6);
            var3 = new DimensionUIResource(this.nextInt(var11, "Invalid dimension"), this.nextInt(var11, "Invalid dimension"));
            break;
         case 3:
            var3 = this.parseInsets(var6, var2 + " invalid insets");
            break;
         case 4:
            try {
               var3 = new Integer(Integer.parseInt(var6));
               break;
            } catch (NumberFormatException var10) {
               throw new SAXException(var2 + " invalid value");
            }
         case 5:
            var3 = var6;
         }
      }

      if (var3 != null && var4 != null) {
         if (var2 == "defaultsProperty") {
            this._defaultsMap.put(var4, var3);
         } else if (this._stateInfo != null) {
            if (this._stateInfo.getData() == null) {
               this._stateInfo.setData(new HashMap());
            }

            this._stateInfo.getData().put(var4, var3);
         } else if (this._style != null) {
            if (this._style.getData() == null) {
               this._style.setData(new HashMap());
            }

            this._style.getData().put(var4, var3);
         }

      } else {
         throw new SAXException(var2 + ": you must supply a key and value");
      }
   }

   private void startGraphics(Attributes var1) throws SAXException {
      SynthGraphicsUtils var2 = null;

      for(int var3 = var1.getLength() - 1; var3 >= 0; --var3) {
         String var4 = var1.getQName(var3);
         if (var4.equals("idref")) {
            var2 = (SynthGraphicsUtils)this.lookup(var1.getValue(var3), SynthGraphicsUtils.class);
         }
      }

      if (var2 == null) {
         throw new SAXException("graphicsUtils: you must supply an idref");
      } else {
         if (this._style != null) {
            this._style.setGraphicsUtils(var2);
         }

      }
   }

   private void startInsets(Attributes var1) throws SAXException {
      int var2 = 0;
      int var3 = 0;
      int var4 = 0;
      int var5 = 0;
      Object var6 = null;
      String var7 = null;

      for(int var8 = var1.getLength() - 1; var8 >= 0; --var8) {
         String var9 = var1.getQName(var8);

         try {
            if (var9.equals("idref")) {
               var6 = (Insets)this.lookup(var1.getValue(var8), Insets.class);
            } else if (var9.equals("id")) {
               var7 = var1.getValue(var8);
            } else if (var9.equals("top")) {
               var2 = Integer.parseInt(var1.getValue(var8));
            } else if (var9.equals("left")) {
               var4 = Integer.parseInt(var1.getValue(var8));
            } else if (var9.equals("bottom")) {
               var3 = Integer.parseInt(var1.getValue(var8));
            } else if (var9.equals("right")) {
               var5 = Integer.parseInt(var1.getValue(var8));
            }
         } catch (NumberFormatException var11) {
            throw new SAXException("insets: bad integer value for " + var1.getValue(var8));
         }
      }

      if (var6 == null) {
         var6 = new InsetsUIResource(var2, var4, var3, var5);
      }

      this.register(var7, var6);
      if (this._style != null) {
         this._style.setInsets((Insets)var6);
      }

   }

   private void startBind(Attributes var1) throws SAXException {
      ParsedSynthStyle var2 = null;
      String var3 = null;
      byte var4 = -1;

      for(int var5 = var1.getLength() - 1; var5 >= 0; --var5) {
         String var6 = var1.getQName(var5);
         if (var6.equals("style")) {
            var2 = (ParsedSynthStyle)this.lookup(var1.getValue(var5), ParsedSynthStyle.class);
         } else if (var6.equals("type")) {
            String var7 = var1.getValue(var5).toUpperCase();
            if (var7.equals("NAME")) {
               var4 = 0;
            } else {
               if (!var7.equals("REGION")) {
                  throw new SAXException("bind: unknown type " + var7);
               }

               var4 = 1;
            }
         } else if (var6.equals("key")) {
            var3 = var1.getValue(var5);
         }
      }

      if (var2 != null && var3 != null && var4 != -1) {
         try {
            this._factory.addStyle(var2, var3, var4);
         } catch (PatternSyntaxException var8) {
            throw new SAXException("bind: " + var3 + " is not a valid regular expression");
         }
      } else {
         throw new SAXException("bind: you must specify a style, type and key");
      }
   }

   private void startPainter(Attributes var1, String var2) throws SAXException {
      Insets var3 = null;
      Insets var4 = null;
      String var5 = null;
      boolean var6 = true;
      boolean var7 = true;
      Object var8 = null;
      String var9 = null;
      String var10 = null;
      byte var11 = -1;
      boolean var12 = false;
      boolean var13 = false;
      boolean var14 = false;

      for(int var15 = var1.getLength() - 1; var15 >= 0; --var15) {
         String var16 = var1.getQName(var15);
         String var17 = var1.getValue(var15);
         if (var16.equals("id")) {
            var10 = var17;
         } else if (var16.equals("method")) {
            var9 = var17.toLowerCase(Locale.ENGLISH);
         } else if (var16.equals("idref")) {
            var8 = (SynthPainter)this.lookup(var17, SynthPainter.class);
         } else if (var16.equals("path")) {
            var5 = var17;
         } else if (var16.equals("sourceInsets")) {
            var3 = this.parseInsets(var17, var2 + ": sourceInsets must be top left bottom right");
         } else if (var16.equals("destinationInsets")) {
            var4 = this.parseInsets(var17, var2 + ": destinationInsets must be top left bottom right");
         } else if (var16.equals("paintCenter")) {
            var6 = var17.toLowerCase().equals("true");
            var14 = true;
         } else if (var16.equals("stretch")) {
            var7 = var17.toLowerCase().equals("true");
            var13 = true;
         } else if (var16.equals("direction")) {
            var17 = var17.toUpperCase().intern();
            if (var17 == "EAST") {
               var11 = 3;
            } else if (var17 == "NORTH") {
               var11 = 1;
            } else if (var17 == "SOUTH") {
               var11 = 5;
            } else if (var17 == "WEST") {
               var11 = 7;
            } else if (var17 == "TOP") {
               var11 = 1;
            } else if (var17 == "LEFT") {
               var11 = 2;
            } else if (var17 == "BOTTOM") {
               var11 = 3;
            } else if (var17 == "RIGHT") {
               var11 = 4;
            } else if (var17 == "HORIZONTAL") {
               var11 = 0;
            } else if (var17 == "VERTICAL") {
               var11 = 1;
            } else if (var17 == "HORIZONTAL_SPLIT") {
               var11 = 1;
            } else {
               if (var17 != "VERTICAL_SPLIT") {
                  throw new SAXException(var2 + ": unknown direction");
               }

               var11 = 0;
            }
         } else if (var16.equals("center")) {
            var12 = var17.toLowerCase().equals("true");
         }
      }

      if (var8 == null) {
         if (var2 == "painter") {
            throw new SAXException(var2 + ": you must specify an idref");
         }

         if (var3 == null && !var12) {
            throw new SAXException("property: you must specify sourceInsets");
         }

         if (var5 == null) {
            throw new SAXException("property: you must specify a path");
         }

         if (var12 && (var3 != null || var4 != null || var14 || var13)) {
            throw new SAXException("The attributes: sourceInsets, destinationInsets, paintCenter and stretch  are not legal when center is true");
         }

         var8 = new ImagePainter(!var7, var6, var3, var4, this.getResource(var5), var12);
      }

      this.register(var10, var8);
      if (this._stateInfo != null) {
         this.addPainterOrMerge(this._statePainters, var9, (SynthPainter)var8, var11);
      } else if (this._style != null) {
         this.addPainterOrMerge(this._stylePainters, var9, (SynthPainter)var8, var11);
      }

   }

   private void addPainterOrMerge(List<ParsedSynthStyle.PainterInfo> var1, String var2, SynthPainter var3, int var4) {
      ParsedSynthStyle.PainterInfo var5 = new ParsedSynthStyle.PainterInfo(var2, var3, var4);
      Iterator var6 = var1.iterator();

      ParsedSynthStyle.PainterInfo var8;
      do {
         if (!var6.hasNext()) {
            var1.add(var5);
            return;
         }

         Object var7 = var6.next();
         var8 = (ParsedSynthStyle.PainterInfo)var7;
      } while(!var5.equalsPainter(var8));

      var8.addPainter(var3);
   }

   private void startImageIcon(Attributes var1) throws SAXException {
      String var2 = null;
      String var3 = null;

      for(int var4 = var1.getLength() - 1; var4 >= 0; --var4) {
         String var5 = var1.getQName(var4);
         if (var5.equals("id")) {
            var3 = var1.getValue(var4);
         } else if (var5.equals("path")) {
            var2 = var1.getValue(var4);
         }
      }

      if (var2 == null) {
         throw new SAXException("imageIcon: you must specify a path");
      } else {
         this.register(var3, new SynthParser.LazyImageIcon(this.getResource(var2)));
      }
   }

   private void startOpaque(Attributes var1) {
      if (this._style != null) {
         this._style.setOpaque(true);

         for(int var2 = var1.getLength() - 1; var2 >= 0; --var2) {
            String var3 = var1.getQName(var2);
            if (var3.equals("value")) {
               this._style.setOpaque("true".equals(var1.getValue(var2).toLowerCase()));
            }
         }
      }

   }

   private void startInputMap(Attributes var1) throws SAXException {
      this._inputMapBindings.clear();
      this._inputMapID = null;
      if (this._style != null) {
         for(int var2 = var1.getLength() - 1; var2 >= 0; --var2) {
            String var3 = var1.getQName(var2);
            if (var3.equals("id")) {
               this._inputMapID = var1.getValue(var2);
            }
         }
      }

   }

   private void endInputMap() throws SAXException {
      if (this._inputMapID != null) {
         this.register(this._inputMapID, new UIDefaults.LazyInputMap(this._inputMapBindings.toArray(new Object[this._inputMapBindings.size()])));
      }

      this._inputMapBindings.clear();
      this._inputMapID = null;
   }

   private void startBindKey(Attributes var1) throws SAXException {
      if (this._inputMapID != null) {
         if (this._style != null) {
            String var2 = null;
            String var3 = null;
            int var4 = var1.getLength() - 1;

            while(true) {
               if (var4 < 0) {
                  if (var2 == null || var3 == null) {
                     throw new SAXException("bindKey: you must supply a key and action");
                  }

                  this._inputMapBindings.add(var2);
                  this._inputMapBindings.add(var3);
                  break;
               }

               String var5 = var1.getQName(var4);
               if (var5.equals("key")) {
                  var2 = var1.getValue(var4);
               } else if (var5.equals("action")) {
                  var3 = var1.getValue(var4);
               }

               --var4;
            }
         }

      }
   }

   public InputSource resolveEntity(String var1, String var2) throws IOException, SAXException {
      return this.isForwarding() ? this.getHandler().resolveEntity(var1, var2) : null;
   }

   public void notationDecl(String var1, String var2, String var3) throws SAXException {
      if (this.isForwarding()) {
         this.getHandler().notationDecl(var1, var2, var3);
      }

   }

   public void unparsedEntityDecl(String var1, String var2, String var3, String var4) throws SAXException {
      if (this.isForwarding()) {
         this.getHandler().unparsedEntityDecl(var1, var2, var3, var4);
      }

   }

   public void setDocumentLocator(Locator var1) {
      if (this.isForwarding()) {
         this.getHandler().setDocumentLocator(var1);
      }

   }

   public void startDocument() throws SAXException {
      if (this.isForwarding()) {
         this.getHandler().startDocument();
      }

   }

   public void endDocument() throws SAXException {
      if (this.isForwarding()) {
         this.getHandler().endDocument();
      }

   }

   public void startElement(String var1, String var2, String var3, Attributes var4) throws SAXException {
      var3 = var3.intern();
      if (var3 == "style") {
         this.startStyle(var4);
      } else if (var3 == "state") {
         this.startState(var4);
      } else if (var3 == "font") {
         this.startFont(var4);
      } else if (var3 == "color") {
         this.startColor(var4);
      } else if (var3 == "painter") {
         this.startPainter(var4, var3);
      } else if (var3 == "imagePainter") {
         this.startPainter(var4, var3);
      } else if (var3 == "property") {
         this.startProperty(var4, "property");
      } else if (var3 == "defaultsProperty") {
         this.startProperty(var4, "defaultsProperty");
      } else if (var3 == "graphicsUtils") {
         this.startGraphics(var4);
      } else if (var3 == "insets") {
         this.startInsets(var4);
      } else if (var3 == "bind") {
         this.startBind(var4);
      } else if (var3 == "bindKey") {
         this.startBindKey(var4);
      } else if (var3 == "imageIcon") {
         this.startImageIcon(var4);
      } else if (var3 == "opaque") {
         this.startOpaque(var4);
      } else if (var3 == "inputMap") {
         this.startInputMap(var4);
      } else if (var3 != "synth") {
         if (this._depth++ == 0) {
            this.getHandler().startDocument();
         }

         this.getHandler().startElement(var1, var2, var3, var4);
      }

   }

   public void endElement(String var1, String var2, String var3) throws SAXException {
      if (this.isForwarding()) {
         this.getHandler().endElement(var1, var2, var3);
         --this._depth;
         if (!this.isForwarding()) {
            this.getHandler().startDocument();
         }
      } else {
         var3 = var3.intern();
         if (var3 == "style") {
            this.endStyle();
         } else if (var3 == "state") {
            this.endState();
         } else if (var3 == "inputMap") {
            this.endInputMap();
         }
      }

   }

   public void characters(char[] var1, int var2, int var3) throws SAXException {
      if (this.isForwarding()) {
         this.getHandler().characters(var1, var2, var3);
      }

   }

   public void ignorableWhitespace(char[] var1, int var2, int var3) throws SAXException {
      if (this.isForwarding()) {
         this.getHandler().ignorableWhitespace(var1, var2, var3);
      }

   }

   public void processingInstruction(String var1, String var2) throws SAXException {
      if (this.isForwarding()) {
         this.getHandler().processingInstruction(var1, var2);
      }

   }

   public void warning(SAXParseException var1) throws SAXException {
      if (this.isForwarding()) {
         this.getHandler().warning(var1);
      }

   }

   public void error(SAXParseException var1) throws SAXException {
      if (this.isForwarding()) {
         this.getHandler().error(var1);
      }

   }

   public void fatalError(SAXParseException var1) throws SAXException {
      if (this.isForwarding()) {
         this.getHandler().fatalError(var1);
      }

      throw var1;
   }

   private static class LazyImageIcon extends ImageIcon implements UIResource {
      private URL location;

      public LazyImageIcon(URL var1) {
         this.location = var1;
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         if (this.getImage() != null) {
            super.paintIcon(var1, var2, var3, var4);
         }

      }

      public int getIconWidth() {
         return this.getImage() != null ? super.getIconWidth() : 0;
      }

      public int getIconHeight() {
         return this.getImage() != null ? super.getIconHeight() : 0;
      }

      public Image getImage() {
         if (this.location != null) {
            this.setImage(Toolkit.getDefaultToolkit().getImage(this.location));
            this.location = null;
         }

         return super.getImage();
      }
   }
}
