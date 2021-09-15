package javax.swing;

import java.awt.Component;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.BreakIterator;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleExtendedComponent;
import javax.accessibility.AccessibleIcon;
import javax.accessibility.AccessibleKeyBinding;
import javax.accessibility.AccessibleRelation;
import javax.accessibility.AccessibleRelationSet;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleText;
import javax.swing.plaf.LabelUI;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;

public class JLabel extends JComponent implements SwingConstants, Accessible {
   private static final String uiClassID = "LabelUI";
   private int mnemonic;
   private int mnemonicIndex;
   private String text;
   private Icon defaultIcon;
   private Icon disabledIcon;
   private boolean disabledIconSet;
   private int verticalAlignment;
   private int horizontalAlignment;
   private int verticalTextPosition;
   private int horizontalTextPosition;
   private int iconTextGap;
   protected Component labelFor;
   static final String LABELED_BY_PROPERTY = "labeledBy";

   public JLabel(String var1, Icon var2, int var3) {
      this.mnemonic = 0;
      this.mnemonicIndex = -1;
      this.text = "";
      this.defaultIcon = null;
      this.disabledIcon = null;
      this.disabledIconSet = false;
      this.verticalAlignment = 0;
      this.horizontalAlignment = 10;
      this.verticalTextPosition = 0;
      this.horizontalTextPosition = 11;
      this.iconTextGap = 4;
      this.labelFor = null;
      this.setText(var1);
      this.setIcon(var2);
      this.setHorizontalAlignment(var3);
      this.updateUI();
      this.setAlignmentX(0.0F);
   }

   public JLabel(String var1, int var2) {
      this(var1, (Icon)null, var2);
   }

   public JLabel(String var1) {
      this(var1, (Icon)null, 10);
   }

   public JLabel(Icon var1, int var2) {
      this((String)null, var1, var2);
   }

   public JLabel(Icon var1) {
      this((String)null, var1, 0);
   }

   public JLabel() {
      this("", (Icon)null, 10);
   }

   public LabelUI getUI() {
      return (LabelUI)this.ui;
   }

   public void setUI(LabelUI var1) {
      super.setUI(var1);
      if (!this.disabledIconSet && this.disabledIcon != null) {
         this.setDisabledIcon((Icon)null);
      }

   }

   public void updateUI() {
      this.setUI((LabelUI)UIManager.getUI(this));
   }

   public String getUIClassID() {
      return "LabelUI";
   }

   public String getText() {
      return this.text;
   }

   public void setText(String var1) {
      String var2 = null;
      if (this.accessibleContext != null) {
         var2 = this.accessibleContext.getAccessibleName();
      }

      String var3 = this.text;
      this.text = var1;
      this.firePropertyChange("text", var3, var1);
      this.setDisplayedMnemonicIndex(SwingUtilities.findDisplayedMnemonicIndex(var1, this.getDisplayedMnemonic()));
      if (this.accessibleContext != null && this.accessibleContext.getAccessibleName() != var2) {
         this.accessibleContext.firePropertyChange("AccessibleVisibleData", var2, this.accessibleContext.getAccessibleName());
      }

      if (var1 == null || var3 == null || !var1.equals(var3)) {
         this.revalidate();
         this.repaint();
      }

   }

   public Icon getIcon() {
      return this.defaultIcon;
   }

   public void setIcon(Icon var1) {
      Icon var2 = this.defaultIcon;
      this.defaultIcon = var1;
      if (this.defaultIcon != var2 && !this.disabledIconSet) {
         this.disabledIcon = null;
      }

      this.firePropertyChange("icon", var2, this.defaultIcon);
      if (this.accessibleContext != null && var2 != this.defaultIcon) {
         this.accessibleContext.firePropertyChange("AccessibleVisibleData", var2, this.defaultIcon);
      }

      if (this.defaultIcon != var2) {
         if (this.defaultIcon == null || var2 == null || this.defaultIcon.getIconWidth() != var2.getIconWidth() || this.defaultIcon.getIconHeight() != var2.getIconHeight()) {
            this.revalidate();
         }

         this.repaint();
      }

   }

   @Transient
   public Icon getDisabledIcon() {
      if (!this.disabledIconSet && this.disabledIcon == null && this.defaultIcon != null) {
         this.disabledIcon = UIManager.getLookAndFeel().getDisabledIcon(this, this.defaultIcon);
         if (this.disabledIcon != null) {
            this.firePropertyChange("disabledIcon", (Object)null, this.disabledIcon);
         }
      }

      return this.disabledIcon;
   }

   public void setDisabledIcon(Icon var1) {
      Icon var2 = this.disabledIcon;
      this.disabledIcon = var1;
      this.disabledIconSet = var1 != null;
      this.firePropertyChange("disabledIcon", var2, var1);
      if (var1 != var2) {
         if (var1 == null || var2 == null || var1.getIconWidth() != var2.getIconWidth() || var1.getIconHeight() != var2.getIconHeight()) {
            this.revalidate();
         }

         if (!this.isEnabled()) {
            this.repaint();
         }
      }

   }

   public void setDisplayedMnemonic(int var1) {
      int var2 = this.mnemonic;
      this.mnemonic = var1;
      this.firePropertyChange("displayedMnemonic", var2, this.mnemonic);
      this.setDisplayedMnemonicIndex(SwingUtilities.findDisplayedMnemonicIndex(this.getText(), this.mnemonic));
      if (var1 != var2) {
         this.revalidate();
         this.repaint();
      }

   }

   public void setDisplayedMnemonic(char var1) {
      int var2 = KeyEvent.getExtendedKeyCodeForChar(var1);
      if (var2 != 0) {
         this.setDisplayedMnemonic(var2);
      }

   }

   public int getDisplayedMnemonic() {
      return this.mnemonic;
   }

   public void setDisplayedMnemonicIndex(int var1) throws IllegalArgumentException {
      int var2 = this.mnemonicIndex;
      if (var1 == -1) {
         this.mnemonicIndex = -1;
      } else {
         String var3 = this.getText();
         int var4 = var3 == null ? 0 : var3.length();
         if (var1 < -1 || var1 >= var4) {
            throw new IllegalArgumentException("index == " + var1);
         }
      }

      this.mnemonicIndex = var1;
      this.firePropertyChange("displayedMnemonicIndex", var2, var1);
      if (var1 != var2) {
         this.revalidate();
         this.repaint();
      }

   }

   public int getDisplayedMnemonicIndex() {
      return this.mnemonicIndex;
   }

   protected int checkHorizontalKey(int var1, String var2) {
      if (var1 != 2 && var1 != 0 && var1 != 4 && var1 != 10 && var1 != 11) {
         throw new IllegalArgumentException(var2);
      } else {
         return var1;
      }
   }

   protected int checkVerticalKey(int var1, String var2) {
      if (var1 != 1 && var1 != 0 && var1 != 3) {
         throw new IllegalArgumentException(var2);
      } else {
         return var1;
      }
   }

   public int getIconTextGap() {
      return this.iconTextGap;
   }

   public void setIconTextGap(int var1) {
      int var2 = this.iconTextGap;
      this.iconTextGap = var1;
      this.firePropertyChange("iconTextGap", var2, var1);
      if (var1 != var2) {
         this.revalidate();
         this.repaint();
      }

   }

   public int getVerticalAlignment() {
      return this.verticalAlignment;
   }

   public void setVerticalAlignment(int var1) {
      if (var1 != this.verticalAlignment) {
         int var2 = this.verticalAlignment;
         this.verticalAlignment = this.checkVerticalKey(var1, "verticalAlignment");
         this.firePropertyChange("verticalAlignment", var2, this.verticalAlignment);
         this.repaint();
      }
   }

   public int getHorizontalAlignment() {
      return this.horizontalAlignment;
   }

   public void setHorizontalAlignment(int var1) {
      if (var1 != this.horizontalAlignment) {
         int var2 = this.horizontalAlignment;
         this.horizontalAlignment = this.checkHorizontalKey(var1, "horizontalAlignment");
         this.firePropertyChange("horizontalAlignment", var2, this.horizontalAlignment);
         this.repaint();
      }
   }

   public int getVerticalTextPosition() {
      return this.verticalTextPosition;
   }

   public void setVerticalTextPosition(int var1) {
      if (var1 != this.verticalTextPosition) {
         int var2 = this.verticalTextPosition;
         this.verticalTextPosition = this.checkVerticalKey(var1, "verticalTextPosition");
         this.firePropertyChange("verticalTextPosition", var2, this.verticalTextPosition);
         this.revalidate();
         this.repaint();
      }
   }

   public int getHorizontalTextPosition() {
      return this.horizontalTextPosition;
   }

   public void setHorizontalTextPosition(int var1) {
      int var2 = this.horizontalTextPosition;
      this.horizontalTextPosition = this.checkHorizontalKey(var1, "horizontalTextPosition");
      this.firePropertyChange("horizontalTextPosition", var2, this.horizontalTextPosition);
      this.revalidate();
      this.repaint();
   }

   public boolean imageUpdate(Image var1, int var2, int var3, int var4, int var5, int var6) {
      return this.isShowing() && (SwingUtilities.doesIconReferenceImage(this.getIcon(), var1) || SwingUtilities.doesIconReferenceImage(this.disabledIcon, var1)) ? super.imageUpdate(var1, var2, var3, var4, var5, var6) : false;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("LabelUI")) {
         byte var2 = JComponent.getWriteObjCounter(this);
         --var2;
         JComponent.setWriteObjCounter(this, var2);
         if (var2 == 0 && this.ui != null) {
            this.ui.installUI(this);
         }
      }

   }

   protected String paramString() {
      String var1 = this.text != null ? this.text : "";
      String var2 = this.defaultIcon != null && this.defaultIcon != this ? this.defaultIcon.toString() : "";
      String var3 = this.disabledIcon != null && this.disabledIcon != this ? this.disabledIcon.toString() : "";
      String var4 = this.labelFor != null ? this.labelFor.toString() : "";
      String var5;
      if (this.verticalAlignment == 1) {
         var5 = "TOP";
      } else if (this.verticalAlignment == 0) {
         var5 = "CENTER";
      } else if (this.verticalAlignment == 3) {
         var5 = "BOTTOM";
      } else {
         var5 = "";
      }

      String var6;
      if (this.horizontalAlignment == 2) {
         var6 = "LEFT";
      } else if (this.horizontalAlignment == 0) {
         var6 = "CENTER";
      } else if (this.horizontalAlignment == 4) {
         var6 = "RIGHT";
      } else if (this.horizontalAlignment == 10) {
         var6 = "LEADING";
      } else if (this.horizontalAlignment == 11) {
         var6 = "TRAILING";
      } else {
         var6 = "";
      }

      String var7;
      if (this.verticalTextPosition == 1) {
         var7 = "TOP";
      } else if (this.verticalTextPosition == 0) {
         var7 = "CENTER";
      } else if (this.verticalTextPosition == 3) {
         var7 = "BOTTOM";
      } else {
         var7 = "";
      }

      String var8;
      if (this.horizontalTextPosition == 2) {
         var8 = "LEFT";
      } else if (this.horizontalTextPosition == 0) {
         var8 = "CENTER";
      } else if (this.horizontalTextPosition == 4) {
         var8 = "RIGHT";
      } else if (this.horizontalTextPosition == 10) {
         var8 = "LEADING";
      } else if (this.horizontalTextPosition == 11) {
         var8 = "TRAILING";
      } else {
         var8 = "";
      }

      return super.paramString() + ",defaultIcon=" + var2 + ",disabledIcon=" + var3 + ",horizontalAlignment=" + var6 + ",horizontalTextPosition=" + var8 + ",iconTextGap=" + this.iconTextGap + ",labelFor=" + var4 + ",text=" + var1 + ",verticalAlignment=" + var5 + ",verticalTextPosition=" + var7;
   }

   public Component getLabelFor() {
      return this.labelFor;
   }

   public void setLabelFor(Component var1) {
      Component var2 = this.labelFor;
      this.labelFor = var1;
      this.firePropertyChange("labelFor", var2, var1);
      if (var2 instanceof JComponent) {
         ((JComponent)var2).putClientProperty("labeledBy", (Object)null);
      }

      if (var1 instanceof JComponent) {
         ((JComponent)var1).putClientProperty("labeledBy", this);
      }

   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JLabel.AccessibleJLabel();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJLabel extends JComponent.AccessibleJComponent implements AccessibleText, AccessibleExtendedComponent {
      protected AccessibleJLabel() {
         super();
      }

      public String getAccessibleName() {
         String var1 = this.accessibleName;
         if (var1 == null) {
            var1 = (String)JLabel.this.getClientProperty("AccessibleName");
         }

         if (var1 == null) {
            var1 = JLabel.this.getText();
         }

         if (var1 == null) {
            var1 = super.getAccessibleName();
         }

         return var1;
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.LABEL;
      }

      public AccessibleIcon[] getAccessibleIcon() {
         Icon var1 = JLabel.this.getIcon();
         if (var1 instanceof Accessible) {
            AccessibleContext var2 = ((Accessible)var1).getAccessibleContext();
            if (var2 != null && var2 instanceof AccessibleIcon) {
               return new AccessibleIcon[]{(AccessibleIcon)var2};
            }
         }

         return null;
      }

      public AccessibleRelationSet getAccessibleRelationSet() {
         AccessibleRelationSet var1 = super.getAccessibleRelationSet();
         if (!var1.contains(AccessibleRelation.LABEL_FOR)) {
            Component var2 = JLabel.this.getLabelFor();
            if (var2 != null) {
               AccessibleRelation var3 = new AccessibleRelation(AccessibleRelation.LABEL_FOR);
               var3.setTarget((Object)var2);
               var1.add(var3);
            }
         }

         return var1;
      }

      public AccessibleText getAccessibleText() {
         View var1 = (View)JLabel.this.getClientProperty("html");
         return var1 != null ? this : null;
      }

      public int getIndexAtPoint(Point var1) {
         View var2 = (View)JLabel.this.getClientProperty("html");
         if (var2 != null) {
            Rectangle var3 = this.getTextRectangle();
            if (var3 == null) {
               return -1;
            } else {
               Rectangle2D.Float var4 = new Rectangle2D.Float((float)var3.x, (float)var3.y, (float)var3.width, (float)var3.height);
               Position.Bias[] var5 = new Position.Bias[1];
               return var2.viewToModel((float)var1.x, (float)var1.y, var4, var5);
            }
         } else {
            return -1;
         }
      }

      public Rectangle getCharacterBounds(int var1) {
         View var2 = (View)JLabel.this.getClientProperty("html");
         if (var2 != null) {
            Rectangle var3 = this.getTextRectangle();
            if (var3 == null) {
               return null;
            } else {
               Rectangle2D.Float var4 = new Rectangle2D.Float((float)var3.x, (float)var3.y, (float)var3.width, (float)var3.height);

               try {
                  Shape var5 = var2.modelToView(var1, var4, Position.Bias.Forward);
                  return var5.getBounds();
               } catch (BadLocationException var6) {
                  return null;
               }
            }
         } else {
            return null;
         }
      }

      public int getCharCount() {
         View var1 = (View)JLabel.this.getClientProperty("html");
         if (var1 != null) {
            Document var2 = var1.getDocument();
            if (var2 instanceof StyledDocument) {
               StyledDocument var3 = (StyledDocument)var2;
               return var3.getLength();
            }
         }

         return JLabel.this.accessibleContext.getAccessibleName().length();
      }

      public int getCaretPosition() {
         return -1;
      }

      public String getAtIndex(int var1, int var2) {
         if (var2 >= 0 && var2 < this.getCharCount()) {
            String var3;
            BreakIterator var4;
            int var5;
            switch(var1) {
            case 1:
               try {
                  return this.getText(var2, 1);
               } catch (BadLocationException var8) {
                  return null;
               }
            case 2:
               try {
                  var3 = this.getText(0, this.getCharCount());
                  var4 = BreakIterator.getWordInstance(this.getLocale());
                  var4.setText(var3);
                  var5 = var4.following(var2);
                  return var3.substring(var4.previous(), var5);
               } catch (BadLocationException var7) {
                  return null;
               }
            case 3:
               try {
                  var3 = this.getText(0, this.getCharCount());
                  var4 = BreakIterator.getSentenceInstance(this.getLocale());
                  var4.setText(var3);
                  var5 = var4.following(var2);
                  return var3.substring(var4.previous(), var5);
               } catch (BadLocationException var6) {
                  return null;
               }
            default:
               return null;
            }
         } else {
            return null;
         }
      }

      public String getAfterIndex(int var1, int var2) {
         if (var2 >= 0 && var2 < this.getCharCount()) {
            String var3;
            BreakIterator var4;
            int var5;
            int var6;
            switch(var1) {
            case 1:
               if (var2 + 1 >= this.getCharCount()) {
                  return null;
               } else {
                  try {
                     return this.getText(var2 + 1, 1);
                  } catch (BadLocationException var9) {
                     return null;
                  }
               }
            case 2:
               try {
                  var3 = this.getText(0, this.getCharCount());
                  var4 = BreakIterator.getWordInstance(this.getLocale());
                  var4.setText(var3);
                  var5 = var4.following(var2);
                  if (var5 != -1 && var5 < var3.length()) {
                     var6 = var4.following(var5);
                     if (var6 != -1 && var6 < var3.length()) {
                        return var3.substring(var5, var6);
                     }

                     return null;
                  }

                  return null;
               } catch (BadLocationException var8) {
                  return null;
               }
            case 3:
               try {
                  var3 = this.getText(0, this.getCharCount());
                  var4 = BreakIterator.getSentenceInstance(this.getLocale());
                  var4.setText(var3);
                  var5 = var4.following(var2);
                  if (var5 != -1 && var5 <= var3.length()) {
                     var6 = var4.following(var5);
                     if (var6 != -1 && var6 <= var3.length()) {
                        return var3.substring(var5, var6);
                     }

                     return null;
                  }

                  return null;
               } catch (BadLocationException var7) {
                  return null;
               }
            default:
               return null;
            }
         } else {
            return null;
         }
      }

      public String getBeforeIndex(int var1, int var2) {
         if (var2 >= 0 && var2 <= this.getCharCount() - 1) {
            String var3;
            BreakIterator var4;
            int var5;
            int var6;
            switch(var1) {
            case 1:
               if (var2 == 0) {
                  return null;
               } else {
                  try {
                     return this.getText(var2 - 1, 1);
                  } catch (BadLocationException var9) {
                     return null;
                  }
               }
            case 2:
               try {
                  var3 = this.getText(0, this.getCharCount());
                  var4 = BreakIterator.getWordInstance(this.getLocale());
                  var4.setText(var3);
                  var4.following(var2);
                  var5 = var4.previous();
                  var6 = var4.previous();
                  if (var6 == -1) {
                     return null;
                  }

                  return var3.substring(var6, var5);
               } catch (BadLocationException var8) {
                  return null;
               }
            case 3:
               try {
                  var3 = this.getText(0, this.getCharCount());
                  var4 = BreakIterator.getSentenceInstance(this.getLocale());
                  var4.setText(var3);
                  var4.following(var2);
                  var5 = var4.previous();
                  var6 = var4.previous();
                  if (var6 == -1) {
                     return null;
                  }

                  return var3.substring(var6, var5);
               } catch (BadLocationException var7) {
                  return null;
               }
            default:
               return null;
            }
         } else {
            return null;
         }
      }

      public AttributeSet getCharacterAttribute(int var1) {
         View var2 = (View)JLabel.this.getClientProperty("html");
         if (var2 != null) {
            Document var3 = var2.getDocument();
            if (var3 instanceof StyledDocument) {
               StyledDocument var4 = (StyledDocument)var3;
               Element var5 = var4.getCharacterElement(var1);
               if (var5 != null) {
                  return var5.getAttributes();
               }
            }
         }

         return null;
      }

      public int getSelectionStart() {
         return -1;
      }

      public int getSelectionEnd() {
         return -1;
      }

      public String getSelectedText() {
         return null;
      }

      private String getText(int var1, int var2) throws BadLocationException {
         View var3 = (View)JLabel.this.getClientProperty("html");
         if (var3 != null) {
            Document var4 = var3.getDocument();
            if (var4 instanceof StyledDocument) {
               StyledDocument var5 = (StyledDocument)var4;
               return var5.getText(var1, var2);
            }
         }

         return null;
      }

      private Rectangle getTextRectangle() {
         String var1 = JLabel.this.getText();
         Icon var2 = JLabel.this.isEnabled() ? JLabel.this.getIcon() : JLabel.this.getDisabledIcon();
         if (var2 == null && var1 == null) {
            return null;
         } else {
            Rectangle var3 = new Rectangle();
            Rectangle var4 = new Rectangle();
            Rectangle var5 = new Rectangle();
            Insets var6 = new Insets(0, 0, 0, 0);
            var6 = JLabel.this.getInsets(var6);
            var5.x = var6.left;
            var5.y = var6.top;
            var5.width = JLabel.this.getWidth() - (var6.left + var6.right);
            var5.height = JLabel.this.getHeight() - (var6.top + var6.bottom);
            SwingUtilities.layoutCompoundLabel(JLabel.this, this.getFontMetrics(this.getFont()), var1, var2, JLabel.this.getVerticalAlignment(), JLabel.this.getHorizontalAlignment(), JLabel.this.getVerticalTextPosition(), JLabel.this.getHorizontalTextPosition(), var5, var3, var4, JLabel.this.getIconTextGap());
            return var4;
         }
      }

      AccessibleExtendedComponent getAccessibleExtendedComponent() {
         return this;
      }

      public String getToolTipText() {
         return JLabel.this.getToolTipText();
      }

      public String getTitledBorderText() {
         return super.getTitledBorderText();
      }

      public AccessibleKeyBinding getAccessibleKeyBinding() {
         int var1 = JLabel.this.getDisplayedMnemonic();
         return var1 == 0 ? null : new JLabel.AccessibleJLabel.LabelKeyBinding(var1);
      }

      class LabelKeyBinding implements AccessibleKeyBinding {
         int mnemonic;

         LabelKeyBinding(int var2) {
            this.mnemonic = var2;
         }

         public int getAccessibleKeyBindingCount() {
            return 1;
         }

         public Object getAccessibleKeyBinding(int var1) {
            if (var1 != 0) {
               throw new IllegalArgumentException();
            } else {
               return KeyStroke.getKeyStroke(this.mnemonic, 0);
            }
         }
      }
   }
}
