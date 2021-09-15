package com.apple.laf;

import apple.laf.JRSUIConstants;
import apple.laf.JRSUIState;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class AquaInternalFrameUI extends BasicInternalFrameUI implements SwingConstants {
   protected static final String IS_PALETTE_PROPERTY = "JInternalFrame.isPalette";
   private static final String FRAME_TYPE = "JInternalFrame.frameType";
   private static final String NORMAL_FRAME = "normal";
   private static final String PALETTE_FRAME = "palette";
   private static final String OPTION_DIALOG = "optionDialog";
   PropertyChangeListener fPropertyListener;
   protected Color fSelectedTextColor;
   protected Color fNotSelectedTextColor;
   AquaInternalFrameBorder fAquaBorder;
   boolean fMouseOverPressedButton;
   int fWhichButtonPressed = -1;
   boolean fRollover = false;
   boolean fDocumentEdited = false;
   boolean fIsPallet;
   static final AquaUtils.RecyclableSingleton<Icon> closeIcon = new AquaUtils.RecyclableSingleton<Icon>() {
      protected Icon getInstance() {
         return new AquaInternalFrameUI.AquaInternalFrameButtonIcon(JRSUIConstants.Widget.TITLE_BAR_CLOSE_BOX);
      }
   };
   static final AquaUtils.RecyclableSingleton<Icon> minimizeIcon = new AquaUtils.RecyclableSingleton<Icon>() {
      protected Icon getInstance() {
         return new AquaInternalFrameUI.AquaInternalFrameButtonIcon(JRSUIConstants.Widget.TITLE_BAR_COLLAPSE_BOX);
      }
   };
   static final AquaUtils.RecyclableSingleton<Icon> zoomIcon = new AquaUtils.RecyclableSingleton<Icon>() {
      protected Icon getInstance() {
         return new AquaInternalFrameUI.AquaInternalFrameButtonIcon(JRSUIConstants.Widget.TITLE_BAR_ZOOM_BOX);
      }
   };
   protected AquaInternalFrameUI.ResizeBox resizeBox;
   static final AquaInternalFrameUI.InternalFrameShadow documentWindowShadow = new AquaInternalFrameUI.InternalFrameShadow() {
      Border getForegroundShadowBorder() {
         return new AquaUtils.SlicedShadowBorder(new AquaUtils.Painter() {
            public void paint(Graphics var1, int var2, int var3, int var4, int var5) {
               var1.setColor(new Color(0, 0, 0, 196));
               var1.fillRoundRect(var2, var3, var4, var5, 16, 16);
               var1.fillRect(var2, var3 + var5 - 16, var4, 16);
            }
         }, new AquaUtils.Painter() {
            public void paint(Graphics var1, int var2, int var3, int var4, int var5) {
               var1.setColor(new Color(0, 0, 0, 64));
               var1.drawLine(var2 + 2, var3 - 8, var2 + var4 - 2, var3 - 8);
            }
         }, 0, 7, 1.1F, 1.0F, 24, 51, 51, 25, 25, 25, 25);
      }

      Border getBackgroundShadowBorder() {
         return new AquaUtils.SlicedShadowBorder(new AquaUtils.Painter() {
            public void paint(Graphics var1, int var2, int var3, int var4, int var5) {
               var1.setColor(new Color(0, 0, 0, 128));
               var1.fillRoundRect(var2 - 3, var3 - 8, var4 + 6, var5, 16, 16);
               var1.fillRect(var2 - 3, var3 + var5 - 20, var4 + 6, 19);
            }
         }, new AquaUtils.Painter() {
            public void paint(Graphics var1, int var2, int var3, int var4, int var5) {
               var1.setColor(new Color(0, 0, 0, 32));
               var1.drawLine(var2, var3 - 11, var2 + var4 - 1, var3 - 11);
            }
         }, 0, 0, 3.0F, 1.0F, 10, 51, 51, 25, 25, 25, 25);
      }
   };
   static final AquaInternalFrameUI.InternalFrameShadow paletteWindowShadow = new AquaInternalFrameUI.InternalFrameShadow() {
      Border getForegroundShadowBorder() {
         return new AquaUtils.SlicedShadowBorder(new AquaUtils.Painter() {
            public void paint(Graphics var1, int var2, int var3, int var4, int var5) {
               var1.setColor(new Color(0, 0, 0, 128));
               var1.fillRect(var2, var3 + 3, var4, var5 - 3);
            }
         }, (AquaUtils.Painter)null, 0, 3, 1.0F, 1.0F, 10, 25, 25, 12, 12, 12, 12);
      }

      Border getBackgroundShadowBorder() {
         return this.getForegroundShadowBorder();
      }
   };
   static final AquaUtils.RecyclableSingleton<Icon> RESIZE_ICON = new AquaUtils.RecyclableSingleton<Icon>() {
      protected Icon getInstance() {
         return new AquaIcon.ScalingJRSUIIcon(11, 11) {
            public void initIconPainter(AquaPainter<JRSUIState> var1) {
               var1.state.set(JRSUIConstants.Widget.GROW_BOX_TEXTURED);
               var1.state.set(JRSUIConstants.WindowType.UTILITY);
            }
         };
      }
   };

   public int getWhichButtonPressed() {
      return this.fWhichButtonPressed;
   }

   public boolean getMouseOverPressedButton() {
      return this.fMouseOverPressedButton;
   }

   public boolean getRollover() {
      return this.fRollover;
   }

   public static ComponentUI createUI(JComponent var0) {
      return new AquaInternalFrameUI((JInternalFrame)var0);
   }

   public AquaInternalFrameUI(JInternalFrame var1) {
      super(var1);
   }

   public void installUI(JComponent var1) {
      this.frame = (JInternalFrame)var1;
      this.frame.add(this.frame.getRootPane(), "Center");
      this.installDefaults();
      this.installListeners();
      this.installComponents();
      this.installKeyboardActions();
      Object var2 = var1.getClientProperty("JInternalFrame.isPalette");
      if (var2 != null) {
         this.setPalette((Boolean)var2);
      } else {
         var2 = var1.getClientProperty("JInternalFrame.frameType");
         if (var2 != null) {
            this.setFrameType((String)var2);
         } else {
            this.setFrameType("normal");
         }
      }

      this.frame.setMinimumSize(new Dimension(this.fIsPallet ? 120 : 150, this.fIsPallet ? 39 : 65));
      this.frame.setOpaque(false);
      var1.setBorder(new AquaInternalFrameUI.CompoundUIBorder(this.fIsPallet ? (Border)paletteWindowShadow.get() : (Border)documentWindowShadow.get(), var1.getBorder()));
   }

   protected void installDefaults() {
      super.installDefaults();
      this.fSelectedTextColor = UIManager.getColor("InternalFrame.activeTitleForeground");
      this.fNotSelectedTextColor = UIManager.getColor("InternalFrame.inactiveTitleForeground");
   }

   public void setSouthPane(JComponent var1) {
      if (this.southPane != null) {
         this.frame.remove(this.southPane);
         this.deinstallMouseHandlers(this.southPane);
      }

      if (var1 != null) {
         this.frame.add(var1);
         this.installMouseHandlers(var1);
      }

      this.southPane = var1;
   }

   public static Icon exportCloseIcon() {
      return (Icon)closeIcon.get();
   }

   public static Icon exportMinimizeIcon() {
      return (Icon)minimizeIcon.get();
   }

   public static Icon exportZoomIcon() {
      return (Icon)zoomIcon.get();
   }

   protected void installKeyboardActions() {
   }

   protected void installComponents() {
      JLayeredPane var1 = this.frame.getLayeredPane();
      if (this.resizeBox != null) {
         this.resizeBox.removeListeners();
         var1.removeComponentListener(this.resizeBox);
         var1.remove(this.resizeBox);
         this.resizeBox = null;
      }

      this.resizeBox = new AquaInternalFrameUI.ResizeBox(var1);
      this.resizeBox.repositionResizeBox();
      var1.add(this.resizeBox);
      var1.setLayer(this.resizeBox, JLayeredPane.DRAG_LAYER);
      var1.addComponentListener(this.resizeBox);
      this.resizeBox.addListeners();
      this.resizeBox.setVisible(this.frame.isResizable());
   }

   protected void installListeners() {
      this.fPropertyListener = new AquaInternalFrameUI.PropertyListener();
      this.frame.addPropertyChangeListener(this.fPropertyListener);
      super.installListeners();
   }

   protected void uninstallListeners() {
      super.uninstallListeners();
      this.frame.removePropertyChangeListener(this.fPropertyListener);
   }

   protected void uninstallKeyboardActions() {
   }

   protected void installMouseHandlers(JComponent var1) {
      var1.addMouseListener(this.borderListener);
      var1.addMouseMotionListener(this.borderListener);
   }

   protected void deinstallMouseHandlers(JComponent var1) {
      var1.removeMouseListener(this.borderListener);
      var1.removeMouseMotionListener(this.borderListener);
   }

   ActionMap createActionMap() {
      ActionMapUIResource var1 = new ActionMapUIResource();
      AquaLookAndFeel var2 = (AquaLookAndFeel)UIManager.getLookAndFeel();
      ActionMap var3 = var2.getAudioActionMap();
      var1.setParent(var3);
      return var1;
   }

   public Dimension getPreferredSize(JComponent var1) {
      Dimension var2 = super.getPreferredSize(var1);
      Dimension var3 = this.frame.getMinimumSize();
      if (var2.width < var3.width) {
         var2.width = var3.width;
      }

      if (var2.height < var3.height) {
         var2.height = var3.height;
      }

      return var2;
   }

   public void setNorthPane(JComponent var1) {
      this.replacePane(this.northPane, var1);
      this.northPane = var1;
   }

   protected void replacePane(JComponent var1, JComponent var2) {
      if (var1 != null) {
         this.deinstallMouseHandlers(var1);
         this.frame.remove(var1);
      }

      if (var2 != null) {
         this.frame.add(var2);
         this.installMouseHandlers(var2);
      }

   }

   protected MouseInputAdapter createBorderListener(JInternalFrame var1) {
      return new AquaInternalFrameUI.AquaBorderListener();
   }

   void setFrameType(String var1) {
      Color var2 = this.frame.getBackground();
      boolean var3 = var2 == null || var2 instanceof UIResource;
      Font var4 = this.frame.getFont();
      boolean var5 = var4 == null || var4 instanceof UIResource;
      boolean var6 = false;
      if (var1.equals("optionDialog")) {
         this.fAquaBorder = AquaInternalFrameBorder.dialog();
         if (var3) {
            this.frame.setBackground(UIManager.getColor("InternalFrame.optionDialogBackground"));
         }

         if (var5) {
            this.frame.setFont(UIManager.getFont("InternalFrame.optionDialogTitleFont"));
         }
      } else if (var1.equals("palette")) {
         this.fAquaBorder = AquaInternalFrameBorder.utility();
         if (var3) {
            this.frame.setBackground(UIManager.getColor("InternalFrame.paletteBackground"));
         }

         if (var5) {
            this.frame.setFont(UIManager.getFont("InternalFrame.paletteTitleFont"));
         }

         var6 = true;
      } else {
         this.fAquaBorder = AquaInternalFrameBorder.window();
         if (var3) {
            this.frame.setBackground(UIManager.getColor("InternalFrame.background"));
         }

         if (var5) {
            this.frame.setFont(UIManager.getFont("InternalFrame.titleFont"));
         }
      }

      this.fAquaBorder.setColors(this.fSelectedTextColor, this.fNotSelectedTextColor);
      this.frame.setBorder(this.fAquaBorder);
      this.fIsPallet = var6;
   }

   public void setPalette(boolean var1) {
      this.setFrameType(var1 ? "palette" : "normal");
   }

   public boolean isDocumentEdited() {
      return this.fDocumentEdited;
   }

   public void setDocumentEdited(boolean var1) {
      this.fDocumentEdited = var1;
   }

   static void updateComponentTreeUIActivation(Component var0, Object var1) {
      if (var0 instanceof JComponent) {
         ((JComponent)var0).putClientProperty("Frame.active", var1);
      }

      Component[] var2 = null;
      if (var0 instanceof JMenu) {
         var2 = ((JMenu)var0).getMenuComponents();
      } else if (var0 instanceof Container) {
         var2 = ((Container)var0).getComponents();
      }

      if (var2 != null) {
         Component[] var3 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Component var6 = var3[var5];
            updateComponentTreeUIActivation(var6, var1);
         }
      }

   }

   class ResizeBox extends JLabel implements MouseListener, MouseMotionListener, MouseWheelListener, ComponentListener, PropertyChangeListener, UIResource {
      final JLayeredPane layeredPane;
      Dimension originalSize;
      Point originalLocation;

      public ResizeBox(JLayeredPane var2) {
         super((Icon)AquaInternalFrameUI.RESIZE_ICON.get());
         this.setSize(11, 11);
         this.layeredPane = var2;
         this.addMouseListener(this);
         this.addMouseMotionListener(this);
         this.addMouseWheelListener(this);
      }

      void addListeners() {
         AquaInternalFrameUI.this.frame.addPropertyChangeListener("resizable", this);
      }

      void removeListeners() {
         AquaInternalFrameUI.this.frame.removePropertyChangeListener("resizable", this);
      }

      void repositionResizeBox() {
         if (AquaInternalFrameUI.this.frame == null) {
            this.setSize(0, 0);
         } else {
            this.setSize(11, 11);
         }

         this.setLocation(this.layeredPane.getWidth() - 12, this.layeredPane.getHeight() - 12);
      }

      void resizeInternalFrame(Point var1) {
         if (this.originalLocation != null && AquaInternalFrameUI.this.frame != null) {
            Container var2 = AquaInternalFrameUI.this.frame.getParent();
            if (var2 instanceof JDesktopPane) {
               Point var3 = SwingUtilities.convertPoint(this, var1, AquaInternalFrameUI.this.frame);
               int var4 = this.originalLocation.x - var3.x;
               int var5 = this.originalLocation.y - var3.y;
               Dimension var6 = AquaInternalFrameUI.this.frame.getMinimumSize();
               Dimension var7 = AquaInternalFrameUI.this.frame.getMaximumSize();
               int var8 = AquaInternalFrameUI.this.frame.getX();
               int var9 = AquaInternalFrameUI.this.frame.getY();
               int var10 = AquaInternalFrameUI.this.frame.getWidth();
               int var11 = AquaInternalFrameUI.this.frame.getHeight();
               Rectangle var12 = var2.getBounds();
               if (this.originalSize.width - var4 < var6.width) {
                  var4 = this.originalSize.width - var6.width;
               } else if (this.originalSize.width - var4 > var7.width) {
                  var4 = -(var7.width - this.originalSize.width);
               }

               if (var8 + this.originalSize.width - var4 > var12.width) {
                  var4 = var8 + this.originalSize.width - var12.width;
               }

               if (this.originalSize.height - var5 < var6.height) {
                  var5 = this.originalSize.height - var6.height;
               } else if (this.originalSize.height - var5 > var7.height) {
                  var5 = -(var7.height - this.originalSize.height);
               }

               if (var9 + this.originalSize.height - var5 > var12.height) {
                  var5 = var9 + this.originalSize.height - var12.height;
               }

               var10 = this.originalSize.width - var4;
               var11 = this.originalSize.height - var5;
               AquaInternalFrameUI.this.getDesktopManager().resizeFrame(AquaInternalFrameUI.this.frame, var8, var9, var10, var11);
            }
         }
      }

      boolean testGrowboxPoint(int var1, int var2, int var3, int var4) {
         return var3 - var1 + (var4 - var2) < 12;
      }

      void forwardEventToFrame(MouseEvent var1) {
         Point var2 = new Point();
         Component var3 = this.getComponentToForwardTo(var1, var2);
         if (var3 != null) {
            var3.dispatchEvent(new MouseEvent(var3, var1.getID(), var1.getWhen(), var1.getModifiers(), var2.x, var2.y, var1.getClickCount(), var1.isPopupTrigger(), var1.getButton()));
         }
      }

      Component getComponentToForwardTo(MouseEvent var1, Point var2) {
         if (AquaInternalFrameUI.this.frame == null) {
            return null;
         } else {
            Container var3 = AquaInternalFrameUI.this.frame.getContentPane();
            if (var3 == null) {
               return null;
            } else {
               Point var4 = SwingUtilities.convertPoint(this, var1.getPoint(), var3);
               Component var5 = SwingUtilities.getDeepestComponentAt(var3, var4.x, var4.y);
               if (var5 == null) {
                  return null;
               } else {
                  var4 = SwingUtilities.convertPoint(var3, var4, var5);
                  if (var2 != null) {
                     var2.setLocation(var4);
                  }

                  return var5;
               }
            }
         }
      }

      public void mouseClicked(MouseEvent var1) {
         this.forwardEventToFrame(var1);
      }

      public void mouseEntered(MouseEvent var1) {
      }

      public void mouseExited(MouseEvent var1) {
      }

      public void mousePressed(MouseEvent var1) {
         if (AquaInternalFrameUI.this.frame != null) {
            if (AquaInternalFrameUI.this.frame.isResizable() && !AquaInternalFrameUI.this.frame.isMaximum() && this.testGrowboxPoint(var1.getX(), var1.getY(), this.getWidth(), this.getHeight())) {
               this.originalLocation = SwingUtilities.convertPoint(this, var1.getPoint(), AquaInternalFrameUI.this.frame);
               this.originalSize = AquaInternalFrameUI.this.frame.getSize();
               AquaInternalFrameUI.this.getDesktopManager().beginResizingFrame(AquaInternalFrameUI.this.frame, 4);
            } else {
               this.forwardEventToFrame(var1);
            }
         }
      }

      public void mouseReleased(MouseEvent var1) {
         if (this.originalLocation != null) {
            this.resizeInternalFrame(var1.getPoint());
            this.originalLocation = null;
            AquaInternalFrameUI.this.getDesktopManager().endResizingFrame(AquaInternalFrameUI.this.frame);
         } else {
            this.forwardEventToFrame(var1);
         }
      }

      public void mouseDragged(MouseEvent var1) {
         this.resizeInternalFrame(var1.getPoint());
         this.repositionResizeBox();
      }

      public void mouseMoved(MouseEvent var1) {
      }

      public void mouseWheelMoved(MouseWheelEvent var1) {
         Point var2 = new Point();
         Component var3 = this.getComponentToForwardTo(var1, var2);
         if (var3 != null) {
            var3.dispatchEvent(new MouseWheelEvent(var3, var1.getID(), var1.getWhen(), var1.getModifiers(), var2.x, var2.y, var1.getClickCount(), var1.isPopupTrigger(), var1.getScrollType(), var1.getScrollAmount(), var1.getWheelRotation()));
         }
      }

      public void componentResized(ComponentEvent var1) {
         this.repositionResizeBox();
      }

      public void componentShown(ComponentEvent var1) {
         this.repositionResizeBox();
      }

      public void componentMoved(ComponentEvent var1) {
         this.repositionResizeBox();
      }

      public void componentHidden(ComponentEvent var1) {
      }

      public void propertyChange(PropertyChangeEvent var1) {
         if ("resizable".equals(var1.getPropertyName())) {
            this.setVisible(Boolean.TRUE.equals(var1.getNewValue()));
         }
      }
   }

   abstract static class InternalFrameShadow extends AquaUtils.RecyclableSingleton<Border> {
      abstract Border getForegroundShadowBorder();

      abstract Border getBackgroundShadowBorder();

      protected Border getInstance() {
         final Border var1 = this.getForegroundShadowBorder();
         final Border var2 = this.getBackgroundShadowBorder();
         return new Border() {
            public Insets getBorderInsets(Component var1x) {
               return var1.getBorderInsets(var1x);
            }

            public boolean isBorderOpaque() {
               return false;
            }

            public void paintBorder(Component var1x, Graphics var2x, int var3, int var4, int var5, int var6) {
               if (((JInternalFrame)var1x).isSelected()) {
                  var1.paintBorder(var1x, var2x, var3, var4, var5, var6);
               } else {
                  var2.paintBorder(var1x, var2x, var3, var4, var5, var6);
               }

            }
         };
      }
   }

   static class CompoundUIBorder extends CompoundBorder implements UIResource {
      public CompoundUIBorder(Border var1, Border var2) {
         super(var1, var2);
      }
   }

   class PropertyListener implements PropertyChangeListener {
      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if ("JInternalFrame.frameType".equals(var2)) {
            if (var1.getNewValue() instanceof String) {
               AquaInternalFrameUI.this.setFrameType((String)var1.getNewValue());
            }
         } else if ("JInternalFrame.isPalette".equals(var2)) {
            if (var1.getNewValue() != null) {
               AquaInternalFrameUI.this.setPalette((Boolean)var1.getNewValue());
            } else {
               AquaInternalFrameUI.this.setPalette(false);
            }
         } else if (!"windowModified".equals(var2) && !"Window.documentModified".equals(var2)) {
            if (!"resizable".equals(var2) && !"state".equals(var2) && !"iconable".equals(var2) && !"maximizable".equals(var2) && !"closable".equals(var2)) {
               if ("title".equals(var2)) {
                  AquaInternalFrameUI.this.frame.repaint();
               } else if ("componentOrientation".equals(var2)) {
                  AquaInternalFrameUI.this.frame.revalidate();
                  AquaInternalFrameUI.this.frame.repaint();
               } else if ("selected".equals(var2)) {
                  Component var3 = (Component)((Component)var1.getSource());
                  AquaInternalFrameUI.updateComponentTreeUIActivation(var3, AquaInternalFrameUI.this.frame.isSelected() ? Boolean.TRUE : Boolean.FALSE);
               }
            } else {
               if ("resizable".equals(var2)) {
                  AquaInternalFrameUI.this.frame.revalidate();
               }

               AquaInternalFrameUI.this.frame.repaint();
            }
         } else {
            AquaInternalFrameUI.this.setDocumentEdited((Boolean)var1.getNewValue());
            AquaInternalFrameUI.this.frame.repaint(0, 0, AquaInternalFrameUI.this.frame.getWidth(), AquaInternalFrameUI.this.frame.getBorder().getBorderInsets(AquaInternalFrameUI.this.frame).top);
         }

      }
   }

   protected class AquaBorderListener extends MouseInputAdapter {
      int _x;
      int _y;
      int __x;
      int __y;
      Rectangle startingBounds;
      boolean fDraggingFrame;
      int resizeDir;
      protected final int RESIZE_NONE = 0;
      private boolean discardRelease = false;
      boolean isTryingToForwardEvent = false;

      public void mouseClicked(MouseEvent var1) {
         if (!this.didForwardEvent(var1)) {
            if (var1.getClickCount() > 1 && var1.getSource() == AquaInternalFrameUI.this.getNorthPane()) {
               if (AquaInternalFrameUI.this.frame.isIconifiable() && AquaInternalFrameUI.this.frame.isIcon()) {
                  try {
                     AquaInternalFrameUI.this.frame.setIcon(false);
                  } catch (PropertyVetoException var5) {
                  }
               } else if (AquaInternalFrameUI.this.frame.isMaximizable()) {
                  if (!AquaInternalFrameUI.this.frame.isMaximum()) {
                     try {
                        AquaInternalFrameUI.this.frame.setMaximum(true);
                     } catch (PropertyVetoException var4) {
                     }
                  } else {
                     try {
                        AquaInternalFrameUI.this.frame.setMaximum(false);
                     } catch (PropertyVetoException var3) {
                     }
                  }
               }

            }
         }
      }

      public void updateRollover(MouseEvent var1) {
         boolean var2 = AquaInternalFrameUI.this.fRollover;
         Insets var3 = AquaInternalFrameUI.this.frame.getInsets();
         AquaInternalFrameUI.this.fRollover = this.isTitleBarDraggableArea(var1) && AquaInternalFrameUI.this.fAquaBorder.getWithinRolloverArea(var3, var1.getX(), var1.getY());
         if (AquaInternalFrameUI.this.fRollover != var2) {
            this.repaintButtons();
         }

      }

      public void repaintButtons() {
         AquaInternalFrameUI.this.fAquaBorder.repaintButtonArea(AquaInternalFrameUI.this.frame);
      }

      public void mouseReleased(MouseEvent var1) {
         if (!this.didForwardEvent(var1)) {
            this.fDraggingFrame = false;
            if (AquaInternalFrameUI.this.fWhichButtonPressed != -1) {
               int var4 = AquaInternalFrameUI.this.fAquaBorder.getWhichButtonHit(AquaInternalFrameUI.this.frame, var1.getX(), var1.getY());
               int var3 = AquaInternalFrameUI.this.fWhichButtonPressed;
               AquaInternalFrameUI.this.fWhichButtonPressed = -1;
               AquaInternalFrameUI.this.fMouseOverPressedButton = false;
               if (var3 == var4) {
                  AquaInternalFrameUI.this.fMouseOverPressedButton = false;
                  AquaInternalFrameUI.this.fRollover = false;
                  AquaInternalFrameUI.this.fAquaBorder.doButtonAction(AquaInternalFrameUI.this.frame, var3);
               }

               this.updateRollover(var1);
               this.repaintButtons();
            } else if (this.discardRelease) {
               this.discardRelease = false;
            } else {
               if (this.resizeDir == 0) {
                  AquaInternalFrameUI.this.getDesktopManager().endDraggingFrame(AquaInternalFrameUI.this.frame);
               } else {
                  Container var2 = AquaInternalFrameUI.this.frame.getTopLevelAncestor();
                  if (var2 instanceof JFrame) {
                     ((JFrame)AquaInternalFrameUI.this.frame.getTopLevelAncestor()).getGlassPane().setCursor(Cursor.getPredefinedCursor(0));
                     ((JFrame)AquaInternalFrameUI.this.frame.getTopLevelAncestor()).getGlassPane().setVisible(false);
                  } else if (var2 instanceof JApplet) {
                     ((JApplet)var2).getGlassPane().setCursor(Cursor.getPredefinedCursor(0));
                     ((JApplet)var2).getGlassPane().setVisible(false);
                  } else if (var2 instanceof JWindow) {
                     ((JWindow)var2).getGlassPane().setCursor(Cursor.getPredefinedCursor(0));
                     ((JWindow)var2).getGlassPane().setVisible(false);
                  } else if (var2 instanceof JDialog) {
                     ((JDialog)var2).getGlassPane().setCursor(Cursor.getPredefinedCursor(0));
                     ((JDialog)var2).getGlassPane().setVisible(false);
                  }

                  AquaInternalFrameUI.this.getDesktopManager().endResizingFrame(AquaInternalFrameUI.this.frame);
               }

               this._x = 0;
               this._y = 0;
               this.__x = 0;
               this.__y = 0;
               this.startingBounds = null;
               this.resizeDir = 0;
            }
         }
      }

      public void mousePressed(MouseEvent var1) {
         if (!this.didForwardEvent(var1)) {
            Point var2 = SwingUtilities.convertPoint((Component)var1.getSource(), var1.getX(), var1.getY(), (Component)null);
            this.__x = var1.getX();
            this.__y = var1.getY();
            this._x = var2.x;
            this._y = var2.y;
            this.startingBounds = AquaInternalFrameUI.this.frame.getBounds();
            this.resizeDir = 0;
            if (!this.updatePressed(var1)) {
               if (!AquaInternalFrameUI.this.frame.isSelected()) {
                  try {
                     AquaInternalFrameUI.this.frame.setSelected(true);
                  } catch (PropertyVetoException var4) {
                  }
               }

               if (this.isTitleBarDraggableArea(var1)) {
                  AquaInternalFrameUI.this.getDesktopManager().beginDraggingFrame(AquaInternalFrameUI.this.frame);
                  this.fDraggingFrame = true;
               } else if (var1.getSource() == AquaInternalFrameUI.this.getNorthPane()) {
                  AquaInternalFrameUI.this.getDesktopManager().beginDraggingFrame(AquaInternalFrameUI.this.frame);
               } else if (AquaInternalFrameUI.this.frame.isResizable()) {
                  if (var1.getSource() == AquaInternalFrameUI.this.frame) {
                     this.discardRelease = true;
                  }
               }
            }
         }
      }

      public boolean updatePressed(MouseEvent var1) {
         AquaInternalFrameUI.this.fWhichButtonPressed = this.getButtonHit(var1);
         AquaInternalFrameUI.this.fMouseOverPressedButton = true;
         this.repaintButtons();
         return AquaInternalFrameUI.this.fWhichButtonPressed >= 0;
      }

      public int getButtonHit(MouseEvent var1) {
         return AquaInternalFrameUI.this.fAquaBorder.getWhichButtonHit(AquaInternalFrameUI.this.frame, var1.getX(), var1.getY());
      }

      public boolean isTitleBarDraggableArea(MouseEvent var1) {
         if (var1.getSource() != AquaInternalFrameUI.this.frame) {
            return false;
         } else {
            Point var2 = var1.getPoint();
            Insets var3 = AquaInternalFrameUI.this.frame.getInsets();
            if (var2.y < var3.top - AquaInternalFrameUI.this.fAquaBorder.getTitleHeight()) {
               return false;
            } else if (var2.y > var3.top) {
               return false;
            } else if (var2.x < var3.left) {
               return false;
            } else {
               return var2.x <= AquaInternalFrameUI.this.frame.getWidth() - var3.left - var3.right;
            }
         }
      }

      public void mouseDragged(MouseEvent var1) {
         if (this.startingBounds != null) {
            if (AquaInternalFrameUI.this.fWhichButtonPressed != -1) {
               int var11 = this.getButtonHit(var1);
               AquaInternalFrameUI.this.fMouseOverPressedButton = AquaInternalFrameUI.this.fWhichButtonPressed == var11;
               this.repaintButtons();
            } else {
               Point var2 = SwingUtilities.convertPoint((Component)var1.getSource(), var1.getX(), var1.getY(), (Component)null);
               int var3 = this._x - var2.x;
               int var4 = this._y - var2.y;
               if (this.fDraggingFrame || var1.getSource() == AquaInternalFrameUI.this.getNorthPane()) {
                  if (!AquaInternalFrameUI.this.frame.isMaximum() && (var1.getModifiers() & 16) == 16) {
                     Dimension var7 = AquaInternalFrameUI.this.frame.getParent().getSize();
                     int var8 = var7.width;
                     int var9 = var7.height;
                     Insets var10 = AquaInternalFrameUI.this.frame.getInsets();
                     int var5 = this.startingBounds.x - var3;
                     int var6 = this.startingBounds.y - var4;
                     if (var5 + var10.left <= -this.__x) {
                        var5 = -this.__x - var10.left;
                     }

                     if (var6 + var10.top <= -this.__y) {
                        var6 = -this.__y - var10.top;
                     }

                     if (var5 + this.__x + var10.right > var8) {
                        var5 = var8 - this.__x - var10.right;
                     }

                     if (var6 + this.__y + var10.bottom > var9) {
                        var6 = var9 - this.__y - var10.bottom;
                     }

                     AquaInternalFrameUI.this.getDesktopManager().dragFrame(AquaInternalFrameUI.this.frame, var5, var6);
                  }
               }
            }
         }
      }

      public void mouseMoved(MouseEvent var1) {
         if (!this.didForwardEvent(var1)) {
            this.updateRollover(var1);
         }
      }

      boolean didForwardEvent(MouseEvent var1) {
         if (this.isTryingToForwardEvent) {
            return true;
         } else {
            this.isTryingToForwardEvent = true;
            boolean var2 = this.didForwardEventInternal(var1);
            this.isTryingToForwardEvent = false;
            return var2;
         }
      }

      boolean didForwardEventInternal(MouseEvent var1) {
         if (this.fDraggingFrame) {
            return false;
         } else {
            Point var2 = var1.getPoint();
            if (!this.isEventInWindowShadow(var2)) {
               return false;
            } else {
               Container var3 = AquaInternalFrameUI.this.frame.getParent();
               if (!(var3 instanceof JDesktopPane)) {
                  return false;
               } else {
                  JDesktopPane var4 = (JDesktopPane)var3;
                  Point var5 = SwingUtilities.convertPoint(AquaInternalFrameUI.this.frame, var2, var3);
                  Component var6 = this.findComponentToHitBehindMe(var4, var5);
                  if (var6 != null && var6 != AquaInternalFrameUI.this.frame) {
                     Point var7 = SwingUtilities.convertPoint(var4, var5, var6);
                     var6.dispatchEvent(new MouseEvent(var6, var1.getID(), var1.getWhen(), var1.getModifiers(), var7.x, var7.y, var1.getClickCount(), var1.isPopupTrigger(), var1.getButton()));
                     return true;
                  } else {
                     return false;
                  }
               }
            }
         }
      }

      Component findComponentToHitBehindMe(JDesktopPane var1, Point var2) {
         JInternalFrame[] var3 = var1.getAllFrames();
         boolean var4 = false;
         JInternalFrame[] var5 = var3;
         int var6 = var3.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            JInternalFrame var8 = var5[var7];
            if (var8 == AquaInternalFrameUI.this.frame) {
               var4 = true;
            } else if (var4) {
               Rectangle var9 = var8.getBounds();
               if (var9.contains(var2)) {
                  return var8;
               }
            }
         }

         return var1;
      }

      boolean isEventInWindowShadow(Point var1) {
         Rectangle var2 = AquaInternalFrameUI.this.frame.getBounds();
         Insets var3 = AquaInternalFrameUI.this.frame.getInsets();
         var3.top -= AquaInternalFrameUI.this.fAquaBorder.getTitleHeight();
         if (var1.x < var3.left) {
            return true;
         } else if (var1.x > var2.width - var3.right) {
            return true;
         } else if (var1.y < var3.top) {
            return true;
         } else {
            return var1.y > var2.height - var3.bottom;
         }
      }
   }

   static class AquaInternalFrameButtonIcon extends AquaIcon.JRSUIIcon {
      public AquaInternalFrameButtonIcon(JRSUIConstants.Widget var1) {
         this.painter.state.set(var1);
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         this.painter.state.set(this.getStateFor(var1));
         super.paintIcon(var1, var2, var3, var4);
      }

      JRSUIConstants.State getStateFor(Component var1) {
         return JRSUIConstants.State.ROLLOVER;
      }

      public int getIconWidth() {
         return 19;
      }

      public int getIconHeight() {
         return 19;
      }
   }
}
