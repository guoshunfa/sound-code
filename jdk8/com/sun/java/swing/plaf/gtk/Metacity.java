package com.sun.java.swing.plaf.gtk;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageObserver;
import java.awt.image.RGBImageFilter;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.synth.ColorType;
import javax.swing.plaf.synth.SynthConstants;
import javax.swing.plaf.synth.SynthContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sun.swing.SwingUtilities2;

class Metacity implements SynthConstants {
   static Metacity INSTANCE;
   private static final String[] themeNames = new String[]{getUserTheme(), "blueprint", "Bluecurve", "Crux", "SwingFallbackTheme"};
   private static boolean errorLogged;
   private static DocumentBuilder documentBuilder;
   private static Document xmlDoc;
   private static String userHome;
   private Node frame_style_set;
   private Map<String, Object> frameGeometry;
   private Map<String, Map<String, Object>> frameGeometries;
   private LayoutManager titlePaneLayout = new Metacity.TitlePaneLayout();
   private Metacity.ColorizeImageFilter imageFilter = new Metacity.ColorizeImageFilter();
   private URL themeDir = null;
   private SynthContext context;
   private String themeName;
   private Metacity.ArithmeticExpressionEvaluator aee = new Metacity.ArithmeticExpressionEvaluator();
   private Map<String, Integer> variables;
   private Metacity.RoundRectClipShape roundedClipShape;
   private HashMap<String, Image> images = new HashMap();

   protected Metacity(String var1) throws IOException, ParserConfigurationException, SAXException {
      this.themeName = var1;
      this.themeDir = getThemeDir(var1);
      if (this.themeDir == null) {
         throw new FileNotFoundException(var1);
      } else {
         URL var2 = new URL(this.themeDir, "metacity-theme-1.xml");
         xmlDoc = getXMLDoc(var2);
         if (xmlDoc == null) {
            throw new IOException(var2.toString());
         } else {
            this.variables = new HashMap();
            NodeList var16 = xmlDoc.getElementsByTagName("constant");
            int var3 = var16.getLength();

            int var4;
            Node var5;
            String var6;
            for(var4 = 0; var4 < var3; ++var4) {
               var5 = var16.item(var4);
               var6 = this.getStringAttr(var5, "name");
               if (var6 != null) {
                  String var7 = this.getStringAttr(var5, "value");
                  if (var7 != null) {
                     try {
                        this.variables.put(var6, Integer.parseInt(var7));
                     } catch (NumberFormatException var15) {
                        logError(var1, (Exception)var15);
                     }
                  }
               }
            }

            this.frameGeometries = new HashMap();
            var16 = xmlDoc.getElementsByTagName("frame_geometry");
            var3 = var16.getLength();

            for(var4 = 0; var4 < var3; ++var4) {
               var5 = var16.item(var4);
               var6 = this.getStringAttr(var5, "name");
               if (var6 != null) {
                  HashMap var17 = new HashMap();
                  this.frameGeometries.put(var6, var17);
                  String var8 = this.getStringAttr(var5, "parent");
                  if (var8 != null) {
                     var17.putAll((Map)this.frameGeometries.get(var8));
                  }

                  var17.put("has_title", this.getBooleanAttr(var5, "has_title", true));
                  var17.put("rounded_top_left", this.getBooleanAttr(var5, "rounded_top_left", false));
                  var17.put("rounded_top_right", this.getBooleanAttr(var5, "rounded_top_right", false));
                  var17.put("rounded_bottom_left", this.getBooleanAttr(var5, "rounded_bottom_left", false));
                  var17.put("rounded_bottom_right", this.getBooleanAttr(var5, "rounded_bottom_right", false));
                  NodeList var9 = var5.getChildNodes();
                  int var10 = var9.getLength();

                  for(int var11 = 0; var11 < var10; ++var11) {
                     Node var12 = var9.item(var11);
                     if (var12.getNodeType() == 1) {
                        var6 = var12.getNodeName();
                        Object var13 = null;
                        if ("distance".equals(var6)) {
                           var13 = this.getIntAttr(var12, "value", 0);
                        } else if ("border".equals(var6)) {
                           var13 = new Insets(this.getIntAttr(var12, "top", 0), this.getIntAttr(var12, "left", 0), this.getIntAttr(var12, "bottom", 0), this.getIntAttr(var12, "right", 0));
                        } else if ("aspect_ratio".equals(var6)) {
                           var13 = new Float(this.getFloatAttr(var12, "value", 1.0F));
                        } else {
                           logError(var1, "Unknown Metacity frame geometry value type: " + var6);
                        }

                        String var14 = this.getStringAttr(var12, "name");
                        if (var14 != null && var13 != null) {
                           var17.put(var14, var13);
                        }
                     }
                  }
               }
            }

            this.frameGeometry = (Map)this.frameGeometries.get("normal");
         }
      }
   }

   public static LayoutManager getTitlePaneLayout() {
      return INSTANCE.titlePaneLayout;
   }

   private Shape getRoundedClipShape(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      if (this.roundedClipShape == null) {
         this.roundedClipShape = new Metacity.RoundRectClipShape();
      }

      this.roundedClipShape.setRoundedRect(var1, var2, var3, var4, var5, var6, var7);
      return this.roundedClipShape;
   }

   void paintButtonBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.updateFrameGeometry(var1);
      this.context = var1;
      JButton var7 = (JButton)var1.getComponent();
      String var8 = var7.getName();
      int var9 = var1.getComponentState();
      JComponent var10 = (JComponent)var7.getParent();
      Container var11 = var10.getParent();
      JInternalFrame var12;
      if (var11 instanceof JInternalFrame) {
         var12 = (JInternalFrame)var11;
      } else {
         if (!(var11 instanceof JInternalFrame.JDesktopIcon)) {
            return;
         }

         var12 = ((JInternalFrame.JDesktopIcon)var11).getInternalFrame();
      }

      boolean var13 = var12.isSelected();
      var7.setOpaque(false);
      String var14 = "normal";
      if ((var9 & 4) != 0) {
         var14 = "pressed";
      } else if ((var9 & 2) != 0) {
         var14 = "prelight";
      }

      String var15 = null;
      String var16 = null;
      boolean var17 = false;
      boolean var18 = false;
      if (var8 == "InternalFrameTitlePane.menuButton") {
         var15 = "menu";
         var16 = "left_left";
         var17 = true;
      } else if (var8 == "InternalFrameTitlePane.iconifyButton") {
         var15 = "minimize";
         int var19 = (var12.isIconifiable() ? 1 : 0) + (var12.isMaximizable() ? 1 : 0) + (var12.isClosable() ? 1 : 0);
         var18 = var19 == 1;
         switch(var19) {
         case 1:
            var16 = "right_right";
            break;
         case 2:
            var16 = "right_middle";
            break;
         case 3:
            var16 = "right_left";
         }
      } else if (var8 == "InternalFrameTitlePane.maximizeButton") {
         var15 = "maximize";
         var18 = !var12.isClosable();
         var16 = var12.isClosable() ? "right_middle" : "right_right";
      } else if (var8 == "InternalFrameTitlePane.closeButton") {
         var15 = "close";
         var18 = true;
         var16 = "right_right";
      }

      Node var24 = this.getNode(this.frame_style_set, "frame", new String[]{"focus", var13 ? "yes" : "no", "state", var12.isMaximum() ? "maximized" : "normal"});
      if (var15 != null && var24 != null) {
         Node var20 = this.getNode("frame_style", new String[]{"name", this.getStringAttr(var24, "style")});
         if (var20 != null) {
            Shape var21 = var2.getClip();
            if (var18 && this.getBoolean("rounded_top_right", false) || var17 && this.getBoolean("rounded_top_left", false)) {
               Point var22 = var7.getLocation();
               if (var18) {
                  var2.setClip(this.getRoundedClipShape(0, 0, var5, var6, 12, 12, 2));
               } else {
                  var2.setClip(this.getRoundedClipShape(0, 0, var5, var6, 11, 11, 1));
               }

               Rectangle var23 = var21.getBounds();
               var2.clipRect(var23.x, var23.y, var23.width, var23.height);
            }

            this.drawButton(var20, var16 + "_background", var14, var2, var5, var6, var12);
            this.drawButton(var20, var15, var14, var2, var5, var6, var12);
            var2.setClip(var21);
         }
      }

   }

   protected void drawButton(Node var1, String var2, String var3, Graphics var4, int var5, int var6, JInternalFrame var7) {
      Node var8 = this.getNode(var1, "button", new String[]{"function", var2, "state", var3});
      if (var8 == null && !var3.equals("normal")) {
         var8 = this.getNode(var1, "button", new String[]{"function", var2, "state", "normal"});
      }

      if (var8 != null) {
         String var10 = this.getStringAttr(var8, "draw_ops");
         Node var9;
         if (var10 != null) {
            var9 = this.getNode("draw_ops", new String[]{"name", var10});
         } else {
            var9 = this.getNode((Node)var8, "draw_ops", (String[])null);
         }

         this.variables.put("width", var5);
         this.variables.put("height", var6);
         this.draw(var9, var4, var7);
      }

   }

   void paintFrameBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      this.updateFrameGeometry(var1);
      this.context = var1;
      JComponent var7 = var1.getComponent();
      JComponent var8 = findChild(var7, "InternalFrame.northPane");
      if (var8 != null) {
         JInternalFrame var9 = null;
         if (var7 instanceof JInternalFrame) {
            var9 = (JInternalFrame)var7;
         } else {
            if (!(var7 instanceof JInternalFrame.JDesktopIcon)) {
               assert false : "component is not JInternalFrame or JInternalFrame.JDesktopIcon";

               return;
            }

            var9 = ((JInternalFrame.JDesktopIcon)var7).getInternalFrame();
         }

         boolean var10 = var9.isSelected();
         Font var11 = var2.getFont();
         var2.setFont(var8.getFont());
         var2.translate(var3, var4);
         Rectangle var12 = this.calculateTitleArea(var9);
         JComponent var13 = findChild(var8, "InternalFrameTitlePane.menuButton");
         Icon var14 = var9.getFrameIcon();
         this.variables.put("mini_icon_width", var14 != null ? var14.getIconWidth() : 0);
         this.variables.put("mini_icon_height", var14 != null ? var14.getIconHeight() : 0);
         this.variables.put("title_width", this.calculateTitleTextWidth(var2, var9));
         FontMetrics var15 = SwingUtilities2.getFontMetrics(var9, (Graphics)var2);
         this.variables.put("title_height", var15.getAscent() + var15.getDescent());
         this.variables.put("icon_width", 32);
         this.variables.put("icon_height", 32);
         if (this.frame_style_set != null) {
            Node var16 = this.getNode(this.frame_style_set, "frame", new String[]{"focus", var10 ? "yes" : "no", "state", var9.isMaximum() ? "maximized" : "normal"});
            if (var16 != null) {
               Node var17 = this.getNode("frame_style", new String[]{"name", this.getStringAttr(var16, "style")});
               if (var17 != null) {
                  Shape var18 = var2.getClip();
                  boolean var19 = this.getBoolean("rounded_top_left", false);
                  boolean var20 = this.getBoolean("rounded_top_right", false);
                  boolean var21 = this.getBoolean("rounded_bottom_left", false);
                  boolean var22 = this.getBoolean("rounded_bottom_right", false);
                  if (var19 || var20 || var21 || var22) {
                     var9.setOpaque(false);
                     var2.setClip(this.getRoundedClipShape(0, 0, var5, var6, 12, 12, (var19 ? 1 : 0) | (var20 ? 2 : 0) | (var21 ? 4 : 0) | (var22 ? 8 : 0)));
                  }

                  Rectangle var23 = var18.getBounds();
                  var2.clipRect(var23.x, var23.y, var23.width, var23.height);
                  int var24 = var8.getHeight();
                  boolean var25 = var9.isIcon();
                  Insets var26 = this.getBorderInsets(var1, (Insets)null);
                  int var27 = this.getInt("left_titlebar_edge");
                  int var28 = this.getInt("right_titlebar_edge");
                  int var29 = this.getInt("top_titlebar_edge");
                  int var30 = this.getInt("bottom_titlebar_edge");
                  if (!var25) {
                     this.drawPiece(var17, var2, "entire_background", 0, 0, var5, var6, var9);
                  }

                  this.drawPiece(var17, var2, "titlebar", 0, 0, var5, var24, var9);
                  this.drawPiece(var17, var2, "titlebar_middle", var27, var29, var5 - var27 - var28, var24 - var29 - var30, var9);
                  this.drawPiece(var17, var2, "left_titlebar_edge", 0, 0, var27, var24, var9);
                  this.drawPiece(var17, var2, "right_titlebar_edge", var5 - var28, 0, var28, var24, var9);
                  this.drawPiece(var17, var2, "top_titlebar_edge", 0, 0, var5, var29, var9);
                  this.drawPiece(var17, var2, "bottom_titlebar_edge", 0, var24 - var30, var5, var30, var9);
                  this.drawPiece(var17, var2, "title", var12.x, var12.y, var12.width, var12.height, var9);
                  if (!var25) {
                     this.drawPiece(var17, var2, "left_edge", 0, var24, var26.left, var6 - var24, var9);
                     this.drawPiece(var17, var2, "right_edge", var5 - var26.right, var24, var26.right, var6 - var24, var9);
                     this.drawPiece(var17, var2, "bottom_edge", 0, var6 - var26.bottom, var5, var26.bottom, var9);
                     this.drawPiece(var17, var2, "overlay", 0, 0, var5, var6, var9);
                  }

                  var2.setClip(var18);
               }
            }
         }

         var2.translate(-var3, -var4);
         var2.setFont(var11);
      }
   }

   private static URL getThemeDir(String var0) {
      return (URL)(new Metacity.Privileged()).doPrivileged(Metacity.Privileged.GET_THEME_DIR, var0);
   }

   private static String getUserTheme() {
      return (String)(new Metacity.Privileged()).doPrivileged(Metacity.Privileged.GET_USER_THEME, (Object)null);
   }

   protected void tileImage(Graphics var1, Image var2, int var3, int var4, int var5, int var6, float[] var7) {
      Graphics2D var8 = (Graphics2D)var1;
      Composite var9 = var8.getComposite();
      int var10 = var2.getWidth((ImageObserver)null);
      int var11 = var2.getHeight((ImageObserver)null);

      for(int var12 = var4; var12 < var4 + var6; var12 += var11) {
         var11 = Math.min(var11, var4 + var6 - var12);

         int var17;
         for(int var13 = var3; var13 < var3 + var5; var13 += var17) {
            float var14 = ((float)var7.length - 1.0F) * (float)var13 / (float)(var3 + var5);
            int var15 = (int)var14;
            var14 -= (float)((int)var14);
            float var16 = (1.0F - var14) * var7[var15];
            if (var15 + 1 < var7.length) {
               var16 += var14 * var7[var15 + 1];
            }

            var8.setComposite(AlphaComposite.getInstance(3, var16));
            var17 = Math.min(var10, var3 + var5 - var13);
            var1.drawImage(var2, var13, var12, var13 + var17, var12 + var11, 0, 0, var17, var11, (ImageObserver)null);
         }
      }

      var8.setComposite(var9);
   }

   protected Image getImage(String var1, Color var2) {
      Image var3 = (Image)this.images.get(var1 + "-" + var2.getRGB());
      if (var3 == null) {
         var3 = this.imageFilter.colorize(this.getImage(var1), var2);
         if (var3 != null) {
            this.images.put(var1 + "-" + var2.getRGB(), var3);
         }
      }

      return var3;
   }

   protected Image getImage(String var1) {
      Image var2 = (Image)this.images.get(var1);
      if (var2 == null) {
         if (this.themeDir != null) {
            try {
               URL var3 = new URL(this.themeDir, var1);
               var2 = (Image)(new Metacity.Privileged()).doPrivileged(Metacity.Privileged.GET_IMAGE, var3);
            } catch (MalformedURLException var4) {
            }
         }

         if (var2 != null) {
            this.images.put(var1, var2);
         }
      }

      return var2;
   }

   protected static JComponent findChild(JComponent var0, String var1) {
      int var2 = var0.getComponentCount();

      for(int var3 = 0; var3 < var2; ++var3) {
         JComponent var4 = (JComponent)var0.getComponent(var3);
         if (var1.equals(var4.getName())) {
            return var4;
         }
      }

      return null;
   }

   protected Map getFrameGeometry() {
      return this.frameGeometry;
   }

   protected void setFrameGeometry(JComponent var1, Map var2) {
      this.frameGeometry = var2;
      if (this.getInt("top_height") == 0 && var1 != null) {
         var2.put("top_height", var1.getHeight());
      }

   }

   protected int getInt(String var1) {
      Integer var2 = (Integer)this.frameGeometry.get(var1);
      if (var2 == null) {
         var2 = (Integer)this.variables.get(var1);
      }

      return var2 != null ? var2 : 0;
   }

   protected boolean getBoolean(String var1, boolean var2) {
      Boolean var3 = (Boolean)this.frameGeometry.get(var1);
      return var3 != null ? var3 : var2;
   }

   protected void drawArc(Node var1, Graphics var2) {
      NamedNodeMap var3 = var1.getAttributes();
      Color var4 = this.parseColor(this.getStringAttr(var3, "color"));
      int var5 = this.aee.evaluate(this.getStringAttr(var3, "x"));
      int var6 = this.aee.evaluate(this.getStringAttr(var3, "y"));
      int var7 = this.aee.evaluate(this.getStringAttr(var3, "width"));
      int var8 = this.aee.evaluate(this.getStringAttr(var3, "height"));
      int var9 = this.aee.evaluate(this.getStringAttr(var3, "start_angle"));
      int var10 = this.aee.evaluate(this.getStringAttr(var3, "extent_angle"));
      boolean var11 = this.getBooleanAttr(var1, "filled", false);
      if (this.getInt("width") == -1) {
         var5 -= var7;
      }

      if (this.getInt("height") == -1) {
         var6 -= var8;
      }

      var2.setColor(var4);
      if (var11) {
         var2.fillArc(var5, var6, var7, var8, var9, var10);
      } else {
         var2.drawArc(var5, var6, var7, var8, var9, var10);
      }

   }

   protected void drawLine(Node var1, Graphics var2) {
      NamedNodeMap var3 = var1.getAttributes();
      Color var4 = this.parseColor(this.getStringAttr(var3, "color"));
      int var5 = this.aee.evaluate(this.getStringAttr(var3, "x1"));
      int var6 = this.aee.evaluate(this.getStringAttr(var3, "y1"));
      int var7 = this.aee.evaluate(this.getStringAttr(var3, "x2"));
      int var8 = this.aee.evaluate(this.getStringAttr(var3, "y2"));
      int var9 = this.aee.evaluate(this.getStringAttr(var3, "width"), 1);
      var2.setColor(var4);
      if (var9 != 1) {
         Graphics2D var10 = (Graphics2D)var2;
         Stroke var11 = var10.getStroke();
         var10.setStroke(new BasicStroke((float)var9));
         var10.drawLine(var5, var6, var7, var8);
         var10.setStroke(var11);
      } else {
         var2.drawLine(var5, var6, var7, var8);
      }

   }

   protected void drawRectangle(Node var1, Graphics var2) {
      NamedNodeMap var3 = var1.getAttributes();
      Color var4 = this.parseColor(this.getStringAttr(var3, "color"));
      boolean var5 = this.getBooleanAttr(var1, "filled", false);
      int var6 = this.aee.evaluate(this.getStringAttr(var3, "x"));
      int var7 = this.aee.evaluate(this.getStringAttr(var3, "y"));
      int var8 = this.aee.evaluate(this.getStringAttr(var3, "width"));
      int var9 = this.aee.evaluate(this.getStringAttr(var3, "height"));
      var2.setColor(var4);
      if (this.getInt("width") == -1) {
         var6 -= var8;
      }

      if (this.getInt("height") == -1) {
         var7 -= var9;
      }

      if (var5) {
         var2.fillRect(var6, var7, var8, var9);
      } else {
         var2.drawRect(var6, var7, var8, var9);
      }

   }

   protected void drawTile(Node var1, Graphics var2, JInternalFrame var3) {
      NamedNodeMap var4 = var1.getAttributes();
      int var5 = this.aee.evaluate(this.getStringAttr(var4, "x"));
      int var6 = this.aee.evaluate(this.getStringAttr(var4, "y"));
      int var7 = this.aee.evaluate(this.getStringAttr(var4, "width"));
      int var8 = this.aee.evaluate(this.getStringAttr(var4, "height"));
      int var9 = this.aee.evaluate(this.getStringAttr(var4, "tile_width"));
      int var10 = this.aee.evaluate(this.getStringAttr(var4, "tile_height"));
      int var11 = this.getInt("width");
      int var12 = this.getInt("height");
      if (var11 == -1) {
         var5 -= var7;
      }

      if (var12 == -1) {
         var6 -= var8;
      }

      Shape var13 = var2.getClip();
      if (var2 instanceof Graphics2D) {
         ((Graphics2D)var2).clip(new Rectangle(var5, var6, var7, var8));
      }

      this.variables.put("width", var9);
      this.variables.put("height", var10);
      Node var14 = this.getNode("draw_ops", new String[]{"name", this.getStringAttr(var1, "name")});

      for(int var15 = var6; var15 < var6 + var8; var15 += var10) {
         for(int var16 = var5; var16 < var5 + var7; var16 += var9) {
            var2.translate(var16, var15);
            this.draw(var14, var2, var3);
            var2.translate(-var16, -var15);
         }
      }

      this.variables.put("width", var11);
      this.variables.put("height", var12);
      var2.setClip(var13);
   }

   protected void drawTint(Node var1, Graphics var2) {
      NamedNodeMap var3 = var1.getAttributes();
      Color var4 = this.parseColor(this.getStringAttr(var3, "color"));
      float var5 = Float.parseFloat(this.getStringAttr(var3, "alpha"));
      int var6 = this.aee.evaluate(this.getStringAttr(var3, "x"));
      int var7 = this.aee.evaluate(this.getStringAttr(var3, "y"));
      int var8 = this.aee.evaluate(this.getStringAttr(var3, "width"));
      int var9 = this.aee.evaluate(this.getStringAttr(var3, "height"));
      if (this.getInt("width") == -1) {
         var6 -= var8;
      }

      if (this.getInt("height") == -1) {
         var7 -= var9;
      }

      if (var2 instanceof Graphics2D) {
         Graphics2D var10 = (Graphics2D)var2;
         Composite var11 = var10.getComposite();
         AlphaComposite var12 = AlphaComposite.getInstance(3, var5);
         var10.setComposite(var12);
         var10.setColor(var4);
         var10.fillRect(var6, var7, var8, var9);
         var10.setComposite(var11);
      }

   }

   protected void drawTitle(Node var1, Graphics var2, JInternalFrame var3) {
      NamedNodeMap var4 = var1.getAttributes();
      String var5 = this.getStringAttr(var4, "color");
      int var6 = var5.indexOf("gtk:fg[");
      if (var6 > 0) {
         var5 = var5.substring(0, var6) + "gtk:text[" + var5.substring(var6 + 7);
      }

      Color var7 = this.parseColor(var5);
      int var8 = this.aee.evaluate(this.getStringAttr(var4, "x"));
      int var9 = this.aee.evaluate(this.getStringAttr(var4, "y"));
      String var10 = var3.getTitle();
      if (var10 != null) {
         FontMetrics var11 = SwingUtilities2.getFontMetrics(var3, (Graphics)var2);
         var10 = SwingUtilities2.clipStringIfNecessary(var3, var11, var10, this.calculateTitleArea(var3).width);
         var2.setColor(var7);
         SwingUtilities2.drawString(var3, var2, (String)var10, var8, var9 + var11.getAscent());
      }

   }

   protected Dimension calculateButtonSize(JComponent var1) {
      int var2 = this.getInt("button_height");
      if (var2 == 0) {
         var2 = var1.getHeight();
         if (var2 == 0) {
            var2 = 13;
         } else {
            Insets var3 = (Insets)this.frameGeometry.get("button_border");
            if (var3 != null) {
               var2 -= var3.top + var3.bottom;
            }
         }
      }

      int var5 = this.getInt("button_width");
      if (var5 == 0) {
         var5 = var2;
         Float var4 = (Float)this.frameGeometry.get("aspect_ratio");
         if (var4 != null) {
            var5 = (int)((float)var2 / var4);
         }
      }

      return new Dimension(var5, var2);
   }

   protected Rectangle calculateTitleArea(JInternalFrame var1) {
      JComponent var2 = findChild(var1, "InternalFrame.northPane");
      Dimension var3 = this.calculateButtonSize(var2);
      Insets var4 = (Insets)this.frameGeometry.get("title_border");
      Insets var5 = (Insets)this.getFrameGeometry().get("button_border");
      Rectangle var6 = new Rectangle();
      var6.x = this.getInt("left_titlebar_edge");
      var6.y = 0;
      var6.height = var2.getHeight();
      if (var4 != null) {
         var6.x += var4.left;
         var6.y += var4.top;
         var6.height -= var4.top + var4.bottom;
      }

      if (var2.getParent().getComponentOrientation().isLeftToRight()) {
         var6.x += var3.width;
         if (var5 != null) {
            var6.x += var5.left;
         }

         var6.width = var2.getWidth() - var6.x - this.getInt("right_titlebar_edge");
         if (var1.isClosable()) {
            var6.width -= var3.width;
         }

         if (var1.isMaximizable()) {
            var6.width -= var3.width;
         }

         if (var1.isIconifiable()) {
            var6.width -= var3.width;
         }
      } else {
         if (var1.isClosable()) {
            var6.x += var3.width;
         }

         if (var1.isMaximizable()) {
            var6.x += var3.width;
         }

         if (var1.isIconifiable()) {
            var6.x += var3.width;
         }

         var6.width = var2.getWidth() - var6.x - this.getInt("right_titlebar_edge") - var3.width;
         if (var5 != null) {
            var6.x -= var5.right;
         }
      }

      if (var4 != null) {
         var6.width -= var4.right;
      }

      return var6;
   }

   protected int calculateTitleTextWidth(Graphics var1, JInternalFrame var2) {
      String var3 = var2.getTitle();
      if (var3 != null) {
         Rectangle var4 = this.calculateTitleArea(var2);
         return Math.min(SwingUtilities2.stringWidth(var2, SwingUtilities2.getFontMetrics(var2, (Graphics)var1), var3), var4.width);
      } else {
         return 0;
      }
   }

   protected void setClip(Node var1, Graphics var2) {
      NamedNodeMap var3 = var1.getAttributes();
      int var4 = this.aee.evaluate(this.getStringAttr(var3, "x"));
      int var5 = this.aee.evaluate(this.getStringAttr(var3, "y"));
      int var6 = this.aee.evaluate(this.getStringAttr(var3, "width"));
      int var7 = this.aee.evaluate(this.getStringAttr(var3, "height"));
      if (this.getInt("width") == -1) {
         var4 -= var6;
      }

      if (this.getInt("height") == -1) {
         var5 -= var7;
      }

      if (var2 instanceof Graphics2D) {
         ((Graphics2D)var2).clip(new Rectangle(var4, var5, var6, var7));
      }

   }

   protected void drawGTKArrow(Node var1, Graphics var2) {
      NamedNodeMap var3 = var1.getAttributes();
      String var4 = this.getStringAttr(var3, "arrow");
      String var5 = this.getStringAttr(var3, "shadow");
      String var6 = this.getStringAttr(var3, "state").toUpperCase();
      int var7 = this.aee.evaluate(this.getStringAttr(var3, "x"));
      int var8 = this.aee.evaluate(this.getStringAttr(var3, "y"));
      int var9 = this.aee.evaluate(this.getStringAttr(var3, "width"));
      int var10 = this.aee.evaluate(this.getStringAttr(var3, "height"));
      short var11 = -1;
      if ("NORMAL".equals(var6)) {
         var11 = 1;
      } else if ("SELECTED".equals(var6)) {
         var11 = 512;
      } else if ("INSENSITIVE".equals(var6)) {
         var11 = 8;
      } else if ("PRELIGHT".equals(var6)) {
         var11 = 2;
      }

      GTKConstants.ShadowType var12 = null;
      if ("in".equals(var5)) {
         var12 = GTKConstants.ShadowType.IN;
      } else if ("out".equals(var5)) {
         var12 = GTKConstants.ShadowType.OUT;
      } else if ("etched_in".equals(var5)) {
         var12 = GTKConstants.ShadowType.ETCHED_IN;
      } else if ("etched_out".equals(var5)) {
         var12 = GTKConstants.ShadowType.ETCHED_OUT;
      } else if ("none".equals(var5)) {
         var12 = GTKConstants.ShadowType.NONE;
      }

      GTKConstants.ArrowType var13 = null;
      if ("up".equals(var4)) {
         var13 = GTKConstants.ArrowType.UP;
      } else if ("down".equals(var4)) {
         var13 = GTKConstants.ArrowType.DOWN;
      } else if ("left".equals(var4)) {
         var13 = GTKConstants.ArrowType.LEFT;
      } else if ("right".equals(var4)) {
         var13 = GTKConstants.ArrowType.RIGHT;
      }

      GTKPainter.INSTANCE.paintMetacityElement(this.context, var2, var11, "metacity-arrow", var7, var8, var9, var10, var12, var13);
   }

   protected void drawGTKBox(Node var1, Graphics var2) {
      NamedNodeMap var3 = var1.getAttributes();
      String var4 = this.getStringAttr(var3, "shadow");
      String var5 = this.getStringAttr(var3, "state").toUpperCase();
      int var6 = this.aee.evaluate(this.getStringAttr(var3, "x"));
      int var7 = this.aee.evaluate(this.getStringAttr(var3, "y"));
      int var8 = this.aee.evaluate(this.getStringAttr(var3, "width"));
      int var9 = this.aee.evaluate(this.getStringAttr(var3, "height"));
      short var10 = -1;
      if ("NORMAL".equals(var5)) {
         var10 = 1;
      } else if ("SELECTED".equals(var5)) {
         var10 = 512;
      } else if ("INSENSITIVE".equals(var5)) {
         var10 = 8;
      } else if ("PRELIGHT".equals(var5)) {
         var10 = 2;
      }

      GTKConstants.ShadowType var11 = null;
      if ("in".equals(var4)) {
         var11 = GTKConstants.ShadowType.IN;
      } else if ("out".equals(var4)) {
         var11 = GTKConstants.ShadowType.OUT;
      } else if ("etched_in".equals(var4)) {
         var11 = GTKConstants.ShadowType.ETCHED_IN;
      } else if ("etched_out".equals(var4)) {
         var11 = GTKConstants.ShadowType.ETCHED_OUT;
      } else if ("none".equals(var4)) {
         var11 = GTKConstants.ShadowType.NONE;
      }

      GTKPainter.INSTANCE.paintMetacityElement(this.context, var2, var10, "metacity-box", var6, var7, var8, var9, var11, (GTKConstants.ArrowType)null);
   }

   protected void drawGTKVLine(Node var1, Graphics var2) {
      NamedNodeMap var3 = var1.getAttributes();
      String var4 = this.getStringAttr(var3, "state").toUpperCase();
      int var5 = this.aee.evaluate(this.getStringAttr(var3, "x"));
      int var6 = this.aee.evaluate(this.getStringAttr(var3, "y1"));
      int var7 = this.aee.evaluate(this.getStringAttr(var3, "y2"));
      short var8 = -1;
      if ("NORMAL".equals(var4)) {
         var8 = 1;
      } else if ("SELECTED".equals(var4)) {
         var8 = 512;
      } else if ("INSENSITIVE".equals(var4)) {
         var8 = 8;
      } else if ("PRELIGHT".equals(var4)) {
         var8 = 2;
      }

      GTKPainter.INSTANCE.paintMetacityElement(this.context, var2, var8, "metacity-vline", var5, var6, 1, var7 - var6, (GTKConstants.ShadowType)null, (GTKConstants.ArrowType)null);
   }

   protected void drawGradient(Node var1, Graphics var2) {
      NamedNodeMap var3 = var1.getAttributes();
      String var4 = this.getStringAttr(var3, "type");
      float var5 = this.getFloatAttr(var1, "alpha", -1.0F);
      int var6 = this.aee.evaluate(this.getStringAttr(var3, "x"));
      int var7 = this.aee.evaluate(this.getStringAttr(var3, "y"));
      int var8 = this.aee.evaluate(this.getStringAttr(var3, "width"));
      int var9 = this.aee.evaluate(this.getStringAttr(var3, "height"));
      if (this.getInt("width") == -1) {
         var6 -= var8;
      }

      if (this.getInt("height") == -1) {
         var7 -= var9;
      }

      Node[] var10 = this.getNodesByName(var1, "color");
      Color[] var11 = new Color[var10.length];

      for(int var12 = 0; var12 < var10.length; ++var12) {
         var11[var12] = this.parseColor(this.getStringAttr(var10[var12], "value"));
      }

      boolean var18 = "diagonal".equals(var4) || "horizontal".equals(var4);
      boolean var13 = "diagonal".equals(var4) || "vertical".equals(var4);
      if (var2 instanceof Graphics2D) {
         Graphics2D var14 = (Graphics2D)var2;
         Composite var15 = var14.getComposite();
         if (var5 >= 0.0F) {
            var14.setComposite(AlphaComposite.getInstance(3, var5));
         }

         int var16 = var11.length - 1;

         for(int var17 = 0; var17 < var16; ++var17) {
            var14.setPaint(new GradientPaint((float)(var6 + (var18 ? var17 * var8 / var16 : 0)), (float)(var7 + (var13 ? var17 * var9 / var16 : 0)), var11[var17], (float)(var6 + (var18 ? (var17 + 1) * var8 / var16 : 0)), (float)(var7 + (var13 ? (var17 + 1) * var9 / var16 : 0)), var11[var17 + 1]));
            var14.fillRect(var6 + (var18 ? var17 * var8 / var16 : 0), var7 + (var13 ? var17 * var9 / var16 : 0), var18 ? var8 / var16 : var8, var13 ? var9 / var16 : var9);
         }

         var14.setComposite(var15);
      }

   }

   protected void drawImage(Node var1, Graphics var2) {
      NamedNodeMap var3 = var1.getAttributes();
      String var4 = this.getStringAttr(var3, "filename");
      String var5 = this.getStringAttr(var3, "colorize");
      Color var6 = var5 != null ? this.parseColor(var5) : null;
      String var7 = this.getStringAttr(var3, "alpha");
      Image var8 = var6 != null ? this.getImage(var4, var6) : this.getImage(var4);
      this.variables.put("object_width", var8.getWidth((ImageObserver)null));
      this.variables.put("object_height", var8.getHeight((ImageObserver)null));
      String var9 = this.getStringAttr(var3, "fill_type");
      int var10 = this.aee.evaluate(this.getStringAttr(var3, "x"));
      int var11 = this.aee.evaluate(this.getStringAttr(var3, "y"));
      int var12 = this.aee.evaluate(this.getStringAttr(var3, "width"));
      int var13 = this.aee.evaluate(this.getStringAttr(var3, "height"));
      if (this.getInt("width") == -1) {
         var10 -= var12;
      }

      if (this.getInt("height") == -1) {
         var11 -= var13;
      }

      if (var7 != null) {
         if ("tile".equals(var9)) {
            StringTokenizer var14 = new StringTokenizer(var7, ":");
            float[] var15 = new float[var14.countTokens()];

            for(int var16 = 0; var16 < var15.length; ++var16) {
               var15[var16] = Float.parseFloat(var14.nextToken());
            }

            this.tileImage(var2, var8, var10, var11, var12, var13, var15);
         } else {
            float var17 = Float.parseFloat(var7);
            if (var2 instanceof Graphics2D) {
               Graphics2D var18 = (Graphics2D)var2;
               Composite var19 = var18.getComposite();
               var18.setComposite(AlphaComposite.getInstance(3, var17));
               var18.drawImage(var8, var10, var11, var12, var13, (ImageObserver)null);
               var18.setComposite(var19);
            }
         }
      } else {
         var2.drawImage(var8, var10, var11, var12, var13, (ImageObserver)null);
      }

   }

   protected void drawIcon(Node var1, Graphics var2, JInternalFrame var3) {
      Icon var4 = var3.getFrameIcon();
      if (var4 != null) {
         NamedNodeMap var5 = var1.getAttributes();
         String var6 = this.getStringAttr(var5, "alpha");
         int var7 = this.aee.evaluate(this.getStringAttr(var5, "x"));
         int var8 = this.aee.evaluate(this.getStringAttr(var5, "y"));
         int var9 = this.aee.evaluate(this.getStringAttr(var5, "width"));
         int var10 = this.aee.evaluate(this.getStringAttr(var5, "height"));
         if (this.getInt("width") == -1) {
            var7 -= var9;
         }

         if (this.getInt("height") == -1) {
            var8 -= var10;
         }

         if (var6 != null) {
            float var11 = Float.parseFloat(var6);
            if (var2 instanceof Graphics2D) {
               Graphics2D var12 = (Graphics2D)var2;
               Composite var13 = var12.getComposite();
               var12.setComposite(AlphaComposite.getInstance(3, var11));
               var4.paintIcon(var3, var2, var7, var8);
               var12.setComposite(var13);
            }
         } else {
            var4.paintIcon(var3, var2, var7, var8);
         }

      }
   }

   protected void drawInclude(Node var1, Graphics var2, JInternalFrame var3) {
      int var4 = this.getInt("width");
      int var5 = this.getInt("height");
      NamedNodeMap var6 = var1.getAttributes();
      int var7 = this.aee.evaluate(this.getStringAttr(var6, "x"), 0);
      int var8 = this.aee.evaluate(this.getStringAttr(var6, "y"), 0);
      int var9 = this.aee.evaluate(this.getStringAttr(var6, "width"), -1);
      int var10 = this.aee.evaluate(this.getStringAttr(var6, "height"), -1);
      if (var9 != -1) {
         this.variables.put("width", var9);
      }

      if (var10 != -1) {
         this.variables.put("height", var10);
      }

      Node var11 = this.getNode("draw_ops", new String[]{"name", this.getStringAttr(var1, "name")});
      var2.translate(var7, var8);
      this.draw(var11, var2, var3);
      var2.translate(-var7, -var8);
      if (var9 != -1) {
         this.variables.put("width", var4);
      }

      if (var10 != -1) {
         this.variables.put("height", var5);
      }

   }

   protected void draw(Node var1, Graphics var2, JInternalFrame var3) {
      if (var1 != null) {
         NodeList var4 = var1.getChildNodes();
         if (var4 != null) {
            Shape var5 = var2.getClip();

            for(int var6 = 0; var6 < var4.getLength(); ++var6) {
               Node var7 = var4.item(var6);
               if (var7.getNodeType() == 1) {
                  try {
                     String var8 = var7.getNodeName();
                     if ("include".equals(var8)) {
                        this.drawInclude(var7, var2, var3);
                     } else if ("arc".equals(var8)) {
                        this.drawArc(var7, var2);
                     } else if ("clip".equals(var8)) {
                        this.setClip(var7, var2);
                     } else if ("gradient".equals(var8)) {
                        this.drawGradient(var7, var2);
                     } else if ("gtk_arrow".equals(var8)) {
                        this.drawGTKArrow(var7, var2);
                     } else if ("gtk_box".equals(var8)) {
                        this.drawGTKBox(var7, var2);
                     } else if ("gtk_vline".equals(var8)) {
                        this.drawGTKVLine(var7, var2);
                     } else if ("image".equals(var8)) {
                        this.drawImage(var7, var2);
                     } else if ("icon".equals(var8)) {
                        this.drawIcon(var7, var2, var3);
                     } else if ("line".equals(var8)) {
                        this.drawLine(var7, var2);
                     } else if ("rectangle".equals(var8)) {
                        this.drawRectangle(var7, var2);
                     } else if ("tint".equals(var8)) {
                        this.drawTint(var7, var2);
                     } else if ("tile".equals(var8)) {
                        this.drawTile(var7, var2, var3);
                     } else if ("title".equals(var8)) {
                        this.drawTitle(var7, var2, var3);
                     } else {
                        System.err.println("Unknown Metacity drawing op: " + var7);
                     }
                  } catch (NumberFormatException var9) {
                     logError(this.themeName, (Exception)var9);
                  }
               }
            }

            var2.setClip(var5);
         }
      }

   }

   protected void drawPiece(Node var1, Graphics var2, String var3, int var4, int var5, int var6, int var7, JInternalFrame var8) {
      Node var9 = this.getNode(var1, "piece", new String[]{"position", var3});
      if (var9 != null) {
         String var11 = this.getStringAttr(var9, "draw_ops");
         Node var10;
         if (var11 != null) {
            var10 = this.getNode("draw_ops", new String[]{"name", var11});
         } else {
            var10 = this.getNode((Node)var9, "draw_ops", (String[])null);
         }

         this.variables.put("width", var6);
         this.variables.put("height", var7);
         var2.translate(var4, var5);
         this.draw(var10, var2, var8);
         var2.translate(-var4, -var5);
      }

   }

   Insets getBorderInsets(SynthContext var1, Insets var2) {
      this.updateFrameGeometry(var1);
      if (var2 == null) {
         var2 = new Insets(0, 0, 0, 0);
      }

      var2.top = ((Insets)this.frameGeometry.get("title_border")).top;
      var2.bottom = this.getInt("bottom_height");
      var2.left = this.getInt("left_width");
      var2.right = this.getInt("right_width");
      return var2;
   }

   private void updateFrameGeometry(SynthContext var1) {
      this.context = var1;
      JComponent var2 = var1.getComponent();
      JComponent var3 = findChild(var2, "InternalFrame.northPane");
      JInternalFrame var4 = null;
      if (var2 instanceof JInternalFrame) {
         var4 = (JInternalFrame)var2;
      } else {
         if (!(var2 instanceof JInternalFrame.JDesktopIcon)) {
            assert false : "component is not JInternalFrame or JInternalFrame.JDesktopIcon";

            return;
         }

         var4 = ((JInternalFrame.JDesktopIcon)var2).getInternalFrame();
      }

      Node var5;
      if (this.frame_style_set == null) {
         var5 = this.getNode("window", new String[]{"type", "normal"});
         if (var5 != null) {
            this.frame_style_set = this.getNode("frame_style_set", new String[]{"name", this.getStringAttr(var5, "style_set")});
         }

         if (this.frame_style_set == null) {
            this.frame_style_set = this.getNode("frame_style_set", new String[]{"name", "normal"});
         }
      }

      if (this.frame_style_set != null) {
         var5 = this.getNode(this.frame_style_set, "frame", new String[]{"focus", var4.isSelected() ? "yes" : "no", "state", var4.isMaximum() ? "maximized" : "normal"});
         if (var5 != null) {
            Node var6 = this.getNode("frame_style", new String[]{"name", this.getStringAttr(var5, "style")});
            if (var6 != null) {
               Map var7 = (Map)this.frameGeometries.get(this.getStringAttr(var6, "geometry"));
               this.setFrameGeometry(var3, var7);
            }
         }
      }

   }

   protected static void logError(String var0, Exception var1) {
      logError(var0, var1.toString());
   }

   protected static void logError(String var0, String var1) {
      if (!errorLogged) {
         System.err.println("Exception in Metacity for theme \"" + var0 + "\": " + var1);
         errorLogged = true;
      }

   }

   protected static Document getXMLDoc(final URL var0) throws IOException, ParserConfigurationException, SAXException {
      if (documentBuilder == null) {
         documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      }

      InputStream var1 = (InputStream)AccessController.doPrivileged(new PrivilegedAction<InputStream>() {
         public InputStream run() {
            try {
               return new BufferedInputStream(var0.openStream());
            } catch (IOException var2) {
               return null;
            }
         }
      });
      Document var2 = null;
      if (var1 != null) {
         var2 = documentBuilder.parse(var1);
      }

      return var2;
   }

   protected Node[] getNodesByName(Node var1, String var2) {
      NodeList var3 = var1.getChildNodes();
      int var4 = var3.getLength();
      ArrayList var5 = new ArrayList();

      for(int var6 = 0; var6 < var4; ++var6) {
         Node var7 = var3.item(var6);
         if (var2.equals(var7.getNodeName())) {
            var5.add(var7);
         }
      }

      return (Node[])var5.toArray(new Node[var5.size()]);
   }

   protected Node getNode(String var1, String[] var2) {
      NodeList var3 = xmlDoc.getElementsByTagName(var1);
      return var3 != null ? this.getNode(var3, var1, var2) : null;
   }

   protected Node getNode(Node var1, String var2, String[] var3) {
      Node var4 = null;
      NodeList var5 = var1.getChildNodes();
      if (var5 != null) {
         var4 = this.getNode(var5, var2, var3);
      }

      if (var4 == null) {
         String var6 = this.getStringAttr(var1, "parent");
         if (var6 != null) {
            Node var7 = this.getNode(var1.getParentNode(), var1.getNodeName(), new String[]{"name", var6});
            if (var7 != null) {
               var4 = this.getNode(var7, var2, var3);
            }
         }
      }

      return var4;
   }

   protected Node getNode(NodeList var1, String var2, String[] var3) {
      int var4 = var1.getLength();

      for(int var5 = 0; var5 < var4; ++var5) {
         Node var6 = var1.item(var5);
         if (var2.equals(var6.getNodeName())) {
            if (var3 == null) {
               return var6;
            }

            NamedNodeMap var7 = var6.getAttributes();
            if (var7 != null) {
               boolean var8 = true;
               int var9 = var3.length / 2;

               for(int var10 = 0; var10 < var9; ++var10) {
                  String var11 = var3[var10 * 2];
                  String var12 = var3[var10 * 2 + 1];
                  Node var13 = var7.getNamedItem(var11);
                  if (var13 == null || var12 != null && !var12.equals(var13.getNodeValue())) {
                     var8 = false;
                     break;
                  }
               }

               if (var8) {
                  return var6;
               }
            }
         }
      }

      return null;
   }

   protected String getStringAttr(Node var1, String var2) {
      String var3 = null;
      NamedNodeMap var4 = var1.getAttributes();
      if (var4 != null) {
         var3 = this.getStringAttr(var4, var2);
         if (var3 == null) {
            String var5 = this.getStringAttr(var4, "parent");
            if (var5 != null) {
               Node var6 = this.getNode(var1.getParentNode(), var1.getNodeName(), new String[]{"name", var5});
               if (var6 != null) {
                  var3 = this.getStringAttr(var6, var2);
               }
            }
         }
      }

      return var3;
   }

   protected String getStringAttr(NamedNodeMap var1, String var2) {
      Node var3 = var1.getNamedItem(var2);
      return var3 != null ? var3.getNodeValue() : null;
   }

   protected boolean getBooleanAttr(Node var1, String var2, boolean var3) {
      String var4 = this.getStringAttr(var1, var2);
      return var4 != null ? Boolean.valueOf(var4) : var3;
   }

   protected int getIntAttr(Node var1, String var2, int var3) {
      String var4 = this.getStringAttr(var1, var2);
      int var5 = var3;
      if (var4 != null) {
         try {
            var5 = Integer.parseInt(var4);
         } catch (NumberFormatException var7) {
            logError(this.themeName, (Exception)var7);
         }
      }

      return var5;
   }

   protected float getFloatAttr(Node var1, String var2, float var3) {
      String var4 = this.getStringAttr(var1, var2);
      float var5 = var3;
      if (var4 != null) {
         try {
            var5 = Float.parseFloat(var4);
         } catch (NumberFormatException var7) {
            logError(this.themeName, (Exception)var7);
         }
      }

      return var5;
   }

   protected Color parseColor(String var1) {
      StringTokenizer var2 = new StringTokenizer(var1, "/");
      int var3 = var2.countTokens();
      if (var3 > 1) {
         String var4 = var2.nextToken();
         Color var5;
         if ("shade".equals(var4)) {
            assert var3 == 3;

            var5 = this.parseColor2(var2.nextToken());
            float var8 = Float.parseFloat(var2.nextToken());
            return GTKColorType.adjustColor(var5, 1.0F, var8, var8);
         } else if ("blend".equals(var4)) {
            assert var3 == 4;

            var5 = this.parseColor2(var2.nextToken());
            Color var6 = this.parseColor2(var2.nextToken());
            float var7 = Float.parseFloat(var2.nextToken());
            if (var7 > 1.0F) {
               var7 = 1.0F / var7;
            }

            return new Color((int)((float)var5.getRed() + (float)(var6.getRed() - var5.getRed()) * var7), (int)((float)var5.getRed() + (float)(var6.getRed() - var5.getRed()) * var7), (int)((float)var5.getRed() + (float)(var6.getRed() - var5.getRed()) * var7));
         } else {
            System.err.println("Unknown Metacity color function=" + var1);
            return null;
         }
      } else {
         return this.parseColor2(var1);
      }
   }

   protected Color parseColor2(String var1) {
      Color var2 = null;
      if (var1.startsWith("gtk:")) {
         int var3 = var1.indexOf(91);
         if (var3 > 3) {
            String var4 = var1.substring(4, var3).toLowerCase();
            int var5 = var1.indexOf(93);
            if (var5 > var3 + 1) {
               String var6 = var1.substring(var3 + 1, var5).toUpperCase();
               short var7 = -1;
               if ("ACTIVE".equals(var6)) {
                  var7 = 4;
               } else if ("INSENSITIVE".equals(var6)) {
                  var7 = 8;
               } else if ("NORMAL".equals(var6)) {
                  var7 = 1;
               } else if ("PRELIGHT".equals(var6)) {
                  var7 = 2;
               } else if ("SELECTED".equals(var6)) {
                  var7 = 512;
               }

               ColorType var8 = null;
               if ("fg".equals(var4)) {
                  var8 = GTKColorType.FOREGROUND;
               } else if ("bg".equals(var4)) {
                  var8 = GTKColorType.BACKGROUND;
               } else if ("base".equals(var4)) {
                  var8 = GTKColorType.TEXT_BACKGROUND;
               } else if ("text".equals(var4)) {
                  var8 = GTKColorType.TEXT_FOREGROUND;
               } else if ("dark".equals(var4)) {
                  var8 = GTKColorType.DARK;
               } else if ("light".equals(var4)) {
                  var8 = GTKColorType.LIGHT;
               }

               if (var7 >= 0 && var8 != null) {
                  var2 = ((GTKStyle)this.context.getStyle()).getGTKColor(this.context, var7, var8);
               }
            }
         }
      }

      if (var2 == null) {
         var2 = parseColorString(var1);
      }

      return var2;
   }

   private static Color parseColorString(String var0) {
      if (var0.charAt(0) == '#') {
         var0 = var0.substring(1);
         int var1 = var0.length();
         if (var1 >= 3 && var1 <= 12 && var1 % 3 == 0) {
            var1 /= 3;

            int var2;
            int var3;
            int var4;
            try {
               var2 = Integer.parseInt(var0.substring(0, var1), 16);
               var3 = Integer.parseInt(var0.substring(var1, var1 * 2), 16);
               var4 = Integer.parseInt(var0.substring(var1 * 2, var1 * 3), 16);
            } catch (NumberFormatException var6) {
               return null;
            }

            if (var1 == 4) {
               return new ColorUIResource((float)var2 / 65535.0F, (float)var3 / 65535.0F, (float)var4 / 65535.0F);
            } else if (var1 == 1) {
               return new ColorUIResource((float)var2 / 15.0F, (float)var3 / 15.0F, (float)var4 / 15.0F);
            } else {
               return var1 == 2 ? new ColorUIResource(var2, var3, var4) : new ColorUIResource((float)var2 / 4095.0F, (float)var3 / 4095.0F, (float)var4 / 4095.0F);
            }
         } else {
            return null;
         }
      } else {
         return XColors.lookupColor(var0);
      }
   }

   static {
      String[] var0 = themeNames;
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         String var3 = var0[var2];
         if (var3 != null) {
            try {
               INSTANCE = new Metacity(var3);
            } catch (FileNotFoundException var5) {
            } catch (IOException var6) {
               logError(var3, (Exception)var6);
            } catch (ParserConfigurationException var7) {
               logError(var3, (Exception)var7);
            } catch (SAXException var8) {
               logError(var3, (Exception)var8);
            }
         }

         if (INSTANCE != null) {
            break;
         }
      }

      if (INSTANCE == null) {
         throw new Error("Could not find any installed metacity theme, and fallback failed");
      } else {
         errorLogged = false;
      }
   }

   static class RoundRectClipShape extends RectangularShape {
      static final int TOP_LEFT = 1;
      static final int TOP_RIGHT = 2;
      static final int BOTTOM_LEFT = 4;
      static final int BOTTOM_RIGHT = 8;
      int x;
      int y;
      int width;
      int height;
      int arcwidth;
      int archeight;
      int corners;

      public RoundRectClipShape() {
      }

      public RoundRectClipShape(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
         this.setRoundedRect(var1, var2, var3, var4, var5, var6, var7);
      }

      public void setRoundedRect(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
         this.corners = var7;
         this.x = var1;
         this.y = var2;
         this.width = var3;
         this.height = var4;
         this.arcwidth = var5;
         this.archeight = var6;
      }

      public double getX() {
         return (double)this.x;
      }

      public double getY() {
         return (double)this.y;
      }

      public double getWidth() {
         return (double)this.width;
      }

      public double getHeight() {
         return (double)this.height;
      }

      public double getArcWidth() {
         return (double)this.arcwidth;
      }

      public double getArcHeight() {
         return (double)this.archeight;
      }

      public boolean isEmpty() {
         return false;
      }

      public Rectangle2D getBounds2D() {
         return null;
      }

      public int getCornerFlags() {
         return this.corners;
      }

      public void setFrame(double var1, double var3, double var5, double var7) {
      }

      public boolean contains(double var1, double var3) {
         return false;
      }

      private int classify(double var1, double var3, double var5, double var7) {
         return 0;
      }

      public boolean intersects(double var1, double var3, double var5, double var7) {
         return false;
      }

      public boolean contains(double var1, double var3, double var5, double var7) {
         return false;
      }

      public PathIterator getPathIterator(AffineTransform var1) {
         return new Metacity.RoundRectClipShape.RoundishRectIterator(this, var1);
      }

      static class RoundishRectIterator implements PathIterator {
         double x;
         double y;
         double w;
         double h;
         double aw;
         double ah;
         AffineTransform affine;
         int index;
         double[][] ctrlpts;
         int[] types;
         private static final double angle = 0.7853981633974483D;
         private static final double a = 1.0D - Math.cos(0.7853981633974483D);
         private static final double b = Math.tan(0.7853981633974483D);
         private static final double c;
         private static final double cv;
         private static final double acv;
         private static final double[][] CtrlPtTemplate;
         private static final int[] CornerFlags;

         RoundishRectIterator(Metacity.RoundRectClipShape var1, AffineTransform var2) {
            this.x = var1.getX();
            this.y = var1.getY();
            this.w = var1.getWidth();
            this.h = var1.getHeight();
            this.aw = Math.min(this.w, Math.abs(var1.getArcWidth()));
            this.ah = Math.min(this.h, Math.abs(var1.getArcHeight()));
            this.affine = var2;
            if (this.w >= 0.0D && this.h >= 0.0D) {
               int var3 = var1.getCornerFlags();
               int var4 = 5;

               int var5;
               for(var5 = 1; var5 < 16; var5 <<= 1) {
                  if ((var3 & var5) != 0) {
                     ++var4;
                  }
               }

               this.ctrlpts = new double[var4][];
               this.types = new int[var4];
               var5 = 0;

               for(int var6 = 0; var6 < 4; ++var6) {
                  this.types[var5] = 1;
                  if ((var3 & CornerFlags[var6]) == 0) {
                     this.ctrlpts[var5++] = CtrlPtTemplate[var6 * 3 + 0];
                  } else {
                     this.ctrlpts[var5++] = CtrlPtTemplate[var6 * 3 + 1];
                     this.types[var5] = 3;
                     this.ctrlpts[var5++] = CtrlPtTemplate[var6 * 3 + 2];
                  }
               }

               this.types[var5] = 4;
               this.ctrlpts[var5++] = CtrlPtTemplate[12];
               this.types[0] = 0;
            } else {
               this.ctrlpts = new double[0][];
               this.types = new int[0];
            }

         }

         public int getWindingRule() {
            return 1;
         }

         public boolean isDone() {
            return this.index >= this.ctrlpts.length;
         }

         public void next() {
            ++this.index;
         }

         public int currentSegment(float[] var1) {
            if (this.isDone()) {
               throw new NoSuchElementException("roundrect iterator out of bounds");
            } else {
               double[] var2 = this.ctrlpts[this.index];
               int var3 = 0;

               for(int var4 = 0; var4 < var2.length; var4 += 4) {
                  var1[var3++] = (float)(this.x + var2[var4 + 0] * this.w + var2[var4 + 1] * this.aw);
                  var1[var3++] = (float)(this.y + var2[var4 + 2] * this.h + var2[var4 + 3] * this.ah);
               }

               if (this.affine != null) {
                  this.affine.transform((float[])var1, 0, (float[])var1, 0, var3 / 2);
               }

               return this.types[this.index];
            }
         }

         public int currentSegment(double[] var1) {
            if (this.isDone()) {
               throw new NoSuchElementException("roundrect iterator out of bounds");
            } else {
               double[] var2 = this.ctrlpts[this.index];
               int var3 = 0;

               for(int var4 = 0; var4 < var2.length; var4 += 4) {
                  var1[var3++] = this.x + var2[var4 + 0] * this.w + var2[var4 + 1] * this.aw;
                  var1[var3++] = this.y + var2[var4 + 2] * this.h + var2[var4 + 3] * this.ah;
               }

               if (this.affine != null) {
                  this.affine.transform((double[])var1, 0, (double[])var1, 0, var3 / 2);
               }

               return this.types[this.index];
            }
         }

         static {
            c = Math.sqrt(1.0D + b * b) - 1.0D + a;
            cv = 1.3333333333333333D * a * b / c;
            acv = (1.0D - cv) / 2.0D;
            CtrlPtTemplate = new double[][]{{0.0D, 0.0D, 1.0D, 0.0D}, {0.0D, 0.0D, 1.0D, -0.5D}, {0.0D, 0.0D, 1.0D, -acv, 0.0D, acv, 1.0D, 0.0D, 0.0D, 0.5D, 1.0D, 0.0D}, {1.0D, 0.0D, 1.0D, 0.0D}, {1.0D, -0.5D, 1.0D, 0.0D}, {1.0D, -acv, 1.0D, 0.0D, 1.0D, 0.0D, 1.0D, -acv, 1.0D, 0.0D, 1.0D, -0.5D}, {1.0D, 0.0D, 0.0D, 0.0D}, {1.0D, 0.0D, 0.0D, 0.5D}, {1.0D, 0.0D, 0.0D, acv, 1.0D, -acv, 0.0D, 0.0D, 1.0D, -0.5D, 0.0D, 0.0D}, {0.0D, 0.0D, 0.0D, 0.0D}, {0.0D, 0.5D, 0.0D, 0.0D}, {0.0D, acv, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, acv, 0.0D, 0.0D, 0.0D, 0.5D}, new double[0]};
            CornerFlags = new int[]{4, 8, 2, 1};
         }
      }
   }

   static class PeekableStringTokenizer extends StringTokenizer {
      String token = null;

      public PeekableStringTokenizer(String var1, String var2, boolean var3) {
         super(var1, var2, var3);
         this.peek();
      }

      public String peek() {
         if (this.token == null) {
            this.token = this.nextToken();
         }

         return this.token;
      }

      public boolean hasMoreTokens() {
         return this.token != null || super.hasMoreTokens();
      }

      public String nextToken() {
         String var1;
         if (this.token != null) {
            var1 = this.token;
            this.token = null;
            if (this.hasMoreTokens()) {
               this.peek();
            }

            return var1;
         } else {
            for(var1 = super.nextToken(); (var1.equals(" ") || var1.equals("\t")) && this.hasMoreTokens(); var1 = super.nextToken()) {
            }

            return var1;
         }
      }
   }

   class ArithmeticExpressionEvaluator {
      private Metacity.PeekableStringTokenizer tokenizer;

      int evaluate(String var1) {
         this.tokenizer = new Metacity.PeekableStringTokenizer(var1, " \t+-*/%()", true);
         return Math.round(this.expression());
      }

      int evaluate(String var1, int var2) {
         return var1 != null ? this.evaluate(var1) : var2;
      }

      public float expression() {
         float var1 = this.getTermValue();
         boolean var2 = false;

         while(!var2 && this.tokenizer.hasMoreTokens()) {
            String var3 = this.tokenizer.peek();
            if (!"+".equals(var3) && !"-".equals(var3) && !"`max`".equals(var3) && !"`min`".equals(var3)) {
               var2 = true;
            } else {
               this.tokenizer.nextToken();
               float var4 = this.getTermValue();
               if ("+".equals(var3)) {
                  var1 += var4;
               } else if ("-".equals(var3)) {
                  var1 -= var4;
               } else if ("`max`".equals(var3)) {
                  var1 = Math.max(var1, var4);
               } else if ("`min`".equals(var3)) {
                  var1 = Math.min(var1, var4);
               }
            }
         }

         return var1;
      }

      public float getTermValue() {
         float var1 = this.getFactorValue();
         boolean var2 = false;

         while(!var2 && this.tokenizer.hasMoreTokens()) {
            String var3 = this.tokenizer.peek();
            if (!"*".equals(var3) && !"/".equals(var3) && !"%".equals(var3)) {
               var2 = true;
            } else {
               this.tokenizer.nextToken();
               float var4 = this.getFactorValue();
               if ("*".equals(var3)) {
                  var1 *= var4;
               } else if ("/".equals(var3)) {
                  var1 /= var4;
               } else {
                  var1 %= var4;
               }
            }
         }

         return var1;
      }

      public float getFactorValue() {
         float var1;
         if ("(".equals(this.tokenizer.peek())) {
            this.tokenizer.nextToken();
            var1 = this.expression();
            this.tokenizer.nextToken();
         } else {
            String var2 = this.tokenizer.nextToken();
            if (Character.isDigit(var2.charAt(0))) {
               var1 = Float.parseFloat(var2);
            } else {
               Integer var3 = (Integer)Metacity.this.variables.get(var2);
               if (var3 == null) {
                  var3 = (Integer)Metacity.this.getFrameGeometry().get(var2);
               }

               if (var3 == null) {
                  Metacity.logError(Metacity.this.themeName, "Variable \"" + var2 + "\" not defined");
                  return 0.0F;
               }

               var1 = var3 != null ? (float)var3 : 0.0F;
            }
         }

         return var1;
      }
   }

   protected class TitlePaneLayout implements LayoutManager {
      public void addLayoutComponent(String var1, Component var2) {
      }

      public void removeLayoutComponent(Component var1) {
      }

      public Dimension preferredLayoutSize(Container var1) {
         return this.minimumLayoutSize(var1);
      }

      public Dimension minimumLayoutSize(Container var1) {
         JComponent var2 = (JComponent)var1;
         Container var3 = var2.getParent();
         JInternalFrame var4;
         if (var3 instanceof JInternalFrame) {
            var4 = (JInternalFrame)var3;
         } else {
            if (!(var3 instanceof JInternalFrame.JDesktopIcon)) {
               return null;
            }

            var4 = ((JInternalFrame.JDesktopIcon)var3).getInternalFrame();
         }

         Dimension var5 = Metacity.this.calculateButtonSize(var2);
         Insets var6 = (Insets)Metacity.this.getFrameGeometry().get("title_border");
         Insets var7 = (Insets)Metacity.this.getFrameGeometry().get("button_border");
         int var8 = Metacity.this.getInt("left_titlebar_edge") + var5.width + Metacity.this.getInt("right_titlebar_edge");
         if (var6 != null) {
            var8 += var6.left + var6.right;
         }

         if (var4.isClosable()) {
            var8 += var5.width;
         }

         if (var4.isMaximizable()) {
            var8 += var5.width;
         }

         if (var4.isIconifiable()) {
            var8 += var5.width;
         }

         FontMetrics var9 = var4.getFontMetrics(var2.getFont());
         String var10 = var4.getTitle();
         int var11 = var10 != null ? SwingUtilities2.stringWidth(var4, var9, var10) : 0;
         int var12 = var10 != null ? var10.length() : 0;
         int var13;
         if (var12 > 3) {
            var13 = SwingUtilities2.stringWidth(var4, var9, var10.substring(0, 3) + "...");
            var8 += var11 < var13 ? var11 : var13;
         } else {
            var8 += var11;
         }

         var13 = var9.getHeight() + Metacity.this.getInt("title_vertical_pad");
         if (var6 != null) {
            var13 += var6.top + var6.bottom;
         }

         int var14 = var5.height;
         if (var7 != null) {
            var14 += var7.top + var7.bottom;
         }

         int var15 = Math.max(var14, var13);
         return new Dimension(var8, var15);
      }

      public void layoutContainer(Container var1) {
         JComponent var2 = (JComponent)var1;
         Container var3 = var2.getParent();
         JInternalFrame var4;
         if (var3 instanceof JInternalFrame) {
            var4 = (JInternalFrame)var3;
         } else {
            if (!(var3 instanceof JInternalFrame.JDesktopIcon)) {
               return;
            }

            var4 = ((JInternalFrame.JDesktopIcon)var3).getInternalFrame();
         }

         Map var5 = Metacity.this.getFrameGeometry();
         int var6 = var2.getWidth();
         int var7 = var2.getHeight();
         JComponent var8 = Metacity.findChild(var2, "InternalFrameTitlePane.menuButton");
         JComponent var9 = Metacity.findChild(var2, "InternalFrameTitlePane.iconifyButton");
         JComponent var10 = Metacity.findChild(var2, "InternalFrameTitlePane.maximizeButton");
         JComponent var11 = Metacity.findChild(var2, "InternalFrameTitlePane.closeButton");
         Insets var12 = (Insets)var5.get("button_border");
         Dimension var13 = Metacity.this.calculateButtonSize(var2);
         int var14 = var12 != null ? var12.top : 0;
         int var15;
         if (var3.getComponentOrientation().isLeftToRight()) {
            var15 = Metacity.this.getInt("left_titlebar_edge");
            var8.setBounds(var15, var14, var13.width, var13.height);
            var15 = var6 - var13.width - Metacity.this.getInt("right_titlebar_edge");
            if (var12 != null) {
               var15 -= var12.right;
            }

            if (var4.isClosable()) {
               var11.setBounds(var15, var14, var13.width, var13.height);
               var15 -= var13.width;
            }

            if (var4.isMaximizable()) {
               var10.setBounds(var15, var14, var13.width, var13.height);
               var15 -= var13.width;
            }

            if (var4.isIconifiable()) {
               var9.setBounds(var15, var14, var13.width, var13.height);
            }
         } else {
            var15 = var6 - var13.width - Metacity.this.getInt("right_titlebar_edge");
            var8.setBounds(var15, var14, var13.width, var13.height);
            var15 = Metacity.this.getInt("left_titlebar_edge");
            if (var12 != null) {
               var15 += var12.left;
            }

            if (var4.isClosable()) {
               var11.setBounds(var15, var14, var13.width, var13.height);
               var15 += var13.width;
            }

            if (var4.isMaximizable()) {
               var10.setBounds(var15, var14, var13.width, var13.height);
               var15 += var13.width;
            }

            if (var4.isIconifiable()) {
               var9.setBounds(var15, var14, var13.width, var13.height);
            }
         }

      }
   }

   private class ColorizeImageFilter extends RGBImageFilter {
      double cr;
      double cg;
      double cb;

      public ColorizeImageFilter() {
         this.canFilterIndexColorModel = true;
      }

      public void setColor(Color var1) {
         this.cr = (double)var1.getRed() / 255.0D;
         this.cg = (double)var1.getGreen() / 255.0D;
         this.cb = (double)var1.getBlue() / 255.0D;
      }

      public Image colorize(Image var1, Color var2) {
         this.setColor(var2);
         FilteredImageSource var3 = new FilteredImageSource(var1.getSource(), this);
         return (new ImageIcon(Metacity.this.context.getComponent().createImage(var3))).getImage();
      }

      public int filterRGB(int var1, int var2, int var3) {
         double var4 = (double)(2 * (var3 & 255)) / 255.0D;
         double var6;
         double var8;
         double var10;
         if (var4 <= 1.0D) {
            var6 = this.cr * var4;
            var8 = this.cg * var4;
            var10 = this.cb * var4;
         } else {
            --var4;
            var6 = this.cr + (1.0D - this.cr) * var4;
            var8 = this.cg + (1.0D - this.cg) * var4;
            var10 = this.cb + (1.0D - this.cb) * var4;
         }

         return (var3 & -16777216) + ((int)(var6 * 255.0D) << 16) + ((int)(var8 * 255.0D) << 8) + (int)(var10 * 255.0D);
      }
   }

   private static class Privileged implements PrivilegedAction<Object> {
      private static int GET_THEME_DIR = 0;
      private static int GET_USER_THEME = 1;
      private static int GET_IMAGE = 2;
      private int type;
      private Object arg;

      private Privileged() {
      }

      public Object doPrivileged(int var1, Object var2) {
         this.type = var1;
         this.arg = var2;
         return AccessController.doPrivileged((PrivilegedAction)this);
      }

      public Object run() {
         String var1;
         if (this.type == GET_THEME_DIR) {
            var1 = File.separator;
            String[] var15 = new String[]{Metacity.userHome + var1 + ".themes", System.getProperty("swing.metacitythemedir"), "/usr/X11R6/share/themes", "/usr/X11R6/share/gnome/themes", "/usr/local/share/themes", "/usr/local/share/gnome/themes", "/usr/share/themes", "/usr/gnome/share/themes", "/opt/gnome2/share/themes"};
            URL var16 = null;

            for(int var17 = 0; var17 < var15.length; ++var17) {
               if (var15[var17] != null) {
                  File var19 = new File(var15[var17] + var1 + this.arg + var1 + "metacity-1");
                  if ((new File(var19, "metacity-theme-1.xml")).canRead()) {
                     try {
                        var16 = var19.toURI().toURL();
                     } catch (MalformedURLException var12) {
                        var16 = null;
                     }
                     break;
                  }
               }
            }

            if (var16 == null) {
               String var18 = "resources/metacity/" + this.arg + "/metacity-1/metacity-theme-1.xml";
               URL var20 = this.getClass().getResource(var18);
               if (var20 != null) {
                  String var21 = var20.toString();

                  try {
                     var16 = new URL(var21.substring(0, var21.lastIndexOf(47)) + "/");
                  } catch (MalformedURLException var11) {
                     var16 = null;
                  }
               }
            }

            return var16;
         } else if (this.type == GET_USER_THEME) {
            try {
               Metacity.userHome = System.getProperty("user.home");
               var1 = System.getProperty("swing.metacitythemename");
               if (var1 != null) {
                  return var1;
               }

               URL var2 = new URL((new File(Metacity.userHome)).toURI().toURL(), ".gconf/apps/metacity/general/%25gconf.xml");
               InputStreamReader var3 = new InputStreamReader(var2.openStream(), "ISO-8859-1");
               char[] var4 = new char[1024];
               StringBuffer var5 = new StringBuffer();

               int var6;
               while((var6 = var3.read(var4)) >= 0) {
                  var5.append((char[])var4, 0, var6);
               }

               var3.close();
               String var7 = var5.toString();
               if (var7 != null) {
                  String var8 = var7.toLowerCase();
                  int var9 = var8.indexOf("<entry name=\"theme\"");
                  if (var9 >= 0) {
                     var9 = var8.indexOf("<stringvalue>", var9);
                     if (var9 > 0) {
                        var9 += "<stringvalue>".length();
                        int var10 = var7.indexOf("<", var9);
                        return var7.substring(var9, var10);
                     }
                  }
               }
            } catch (MalformedURLException var13) {
            } catch (IOException var14) {
            }

            return null;
         } else {
            return this.type == GET_IMAGE ? (new ImageIcon((URL)this.arg)).getImage() : null;
         }
      }

      // $FF: synthetic method
      Privileged(Object var1) {
         this();
      }
   }
}
