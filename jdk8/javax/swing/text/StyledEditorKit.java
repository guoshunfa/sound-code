package javax.swing.text;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class StyledEditorKit extends DefaultEditorKit {
   private static final ViewFactory defaultFactory = new StyledEditorKit.StyledViewFactory();
   Element currentRun;
   Element currentParagraph;
   MutableAttributeSet inputAttributes;
   private StyledEditorKit.AttributeTracker inputAttributeUpdater;
   private static final Action[] defaultActions = new Action[]{new StyledEditorKit.FontFamilyAction("font-family-SansSerif", "SansSerif"), new StyledEditorKit.FontFamilyAction("font-family-Monospaced", "Monospaced"), new StyledEditorKit.FontFamilyAction("font-family-Serif", "Serif"), new StyledEditorKit.FontSizeAction("font-size-8", 8), new StyledEditorKit.FontSizeAction("font-size-10", 10), new StyledEditorKit.FontSizeAction("font-size-12", 12), new StyledEditorKit.FontSizeAction("font-size-14", 14), new StyledEditorKit.FontSizeAction("font-size-16", 16), new StyledEditorKit.FontSizeAction("font-size-18", 18), new StyledEditorKit.FontSizeAction("font-size-24", 24), new StyledEditorKit.FontSizeAction("font-size-36", 36), new StyledEditorKit.FontSizeAction("font-size-48", 48), new StyledEditorKit.AlignmentAction("left-justify", 0), new StyledEditorKit.AlignmentAction("center-justify", 1), new StyledEditorKit.AlignmentAction("right-justify", 2), new StyledEditorKit.BoldAction(), new StyledEditorKit.ItalicAction(), new StyledEditorKit.StyledInsertBreakAction(), new StyledEditorKit.UnderlineAction()};

   public StyledEditorKit() {
      this.createInputAttributeUpdated();
      this.createInputAttributes();
   }

   public MutableAttributeSet getInputAttributes() {
      return this.inputAttributes;
   }

   public Element getCharacterAttributeRun() {
      return this.currentRun;
   }

   public Action[] getActions() {
      return TextAction.augmentList(super.getActions(), defaultActions);
   }

   public Document createDefaultDocument() {
      return new DefaultStyledDocument();
   }

   public void install(JEditorPane var1) {
      var1.addCaretListener(this.inputAttributeUpdater);
      var1.addPropertyChangeListener(this.inputAttributeUpdater);
      Caret var2 = var1.getCaret();
      if (var2 != null) {
         this.inputAttributeUpdater.updateInputAttributes(var2.getDot(), var2.getMark(), var1);
      }

   }

   public void deinstall(JEditorPane var1) {
      var1.removeCaretListener(this.inputAttributeUpdater);
      var1.removePropertyChangeListener(this.inputAttributeUpdater);
      this.currentRun = null;
      this.currentParagraph = null;
   }

   public ViewFactory getViewFactory() {
      return defaultFactory;
   }

   public Object clone() {
      StyledEditorKit var1 = (StyledEditorKit)super.clone();
      var1.currentRun = var1.currentParagraph = null;
      var1.createInputAttributeUpdated();
      var1.createInputAttributes();
      return var1;
   }

   private void createInputAttributes() {
      this.inputAttributes = new SimpleAttributeSet() {
         public AttributeSet getResolveParent() {
            return StyledEditorKit.this.currentParagraph != null ? StyledEditorKit.this.currentParagraph.getAttributes() : null;
         }

         public Object clone() {
            return new SimpleAttributeSet(this);
         }
      };
   }

   private void createInputAttributeUpdated() {
      this.inputAttributeUpdater = new StyledEditorKit.AttributeTracker();
   }

   protected void createInputAttributes(Element var1, MutableAttributeSet var2) {
      if (var1.getAttributes().getAttributeCount() > 0 || var1.getEndOffset() - var1.getStartOffset() > 1 || var1.getEndOffset() < var1.getDocument().getLength()) {
         var2.removeAttributes((AttributeSet)var2);
         var2.addAttributes(var1.getAttributes());
         var2.removeAttribute(StyleConstants.ComponentAttribute);
         var2.removeAttribute(StyleConstants.IconAttribute);
         var2.removeAttribute("$ename");
         var2.removeAttribute(StyleConstants.ComposedTextAttribute);
      }

   }

   static class StyledInsertBreakAction extends StyledEditorKit.StyledTextAction {
      private SimpleAttributeSet tempSet;

      StyledInsertBreakAction() {
         super("insert-break");
      }

      public void actionPerformed(ActionEvent var1) {
         JEditorPane var2 = this.getEditor(var1);
         if (var2 != null) {
            if (!var2.isEditable() || !var2.isEnabled()) {
               UIManager.getLookAndFeel().provideErrorFeedback(var2);
               return;
            }

            StyledEditorKit var3 = this.getStyledEditorKit(var2);
            if (this.tempSet != null) {
               this.tempSet.removeAttributes((AttributeSet)this.tempSet);
            } else {
               this.tempSet = new SimpleAttributeSet();
            }

            this.tempSet.addAttributes(var3.getInputAttributes());
            var2.replaceSelection("\n");
            MutableAttributeSet var4 = var3.getInputAttributes();
            var4.removeAttributes((AttributeSet)var4);
            var4.addAttributes(this.tempSet);
            this.tempSet.removeAttributes((AttributeSet)this.tempSet);
         } else {
            JTextComponent var5 = this.getTextComponent(var1);
            if (var5 != null) {
               if (!var5.isEditable() || !var5.isEnabled()) {
                  UIManager.getLookAndFeel().provideErrorFeedback(var2);
                  return;
               }

               var5.replaceSelection("\n");
            }
         }

      }
   }

   public static class UnderlineAction extends StyledEditorKit.StyledTextAction {
      public UnderlineAction() {
         super("font-underline");
      }

      public void actionPerformed(ActionEvent var1) {
         JEditorPane var2 = this.getEditor(var1);
         if (var2 != null) {
            StyledEditorKit var3 = this.getStyledEditorKit(var2);
            MutableAttributeSet var4 = var3.getInputAttributes();
            boolean var5 = !StyleConstants.isUnderline(var4);
            SimpleAttributeSet var6 = new SimpleAttributeSet();
            StyleConstants.setUnderline(var6, var5);
            this.setCharacterAttributes(var2, var6, false);
         }

      }
   }

   public static class ItalicAction extends StyledEditorKit.StyledTextAction {
      public ItalicAction() {
         super("font-italic");
      }

      public void actionPerformed(ActionEvent var1) {
         JEditorPane var2 = this.getEditor(var1);
         if (var2 != null) {
            StyledEditorKit var3 = this.getStyledEditorKit(var2);
            MutableAttributeSet var4 = var3.getInputAttributes();
            boolean var5 = !StyleConstants.isItalic(var4);
            SimpleAttributeSet var6 = new SimpleAttributeSet();
            StyleConstants.setItalic(var6, var5);
            this.setCharacterAttributes(var2, var6, false);
         }

      }
   }

   public static class BoldAction extends StyledEditorKit.StyledTextAction {
      public BoldAction() {
         super("font-bold");
      }

      public void actionPerformed(ActionEvent var1) {
         JEditorPane var2 = this.getEditor(var1);
         if (var2 != null) {
            StyledEditorKit var3 = this.getStyledEditorKit(var2);
            MutableAttributeSet var4 = var3.getInputAttributes();
            boolean var5 = !StyleConstants.isBold(var4);
            SimpleAttributeSet var6 = new SimpleAttributeSet();
            StyleConstants.setBold(var6, var5);
            this.setCharacterAttributes(var2, var6, false);
         }

      }
   }

   public static class AlignmentAction extends StyledEditorKit.StyledTextAction {
      private int a;

      public AlignmentAction(String var1, int var2) {
         super(var1);
         this.a = var2;
      }

      public void actionPerformed(ActionEvent var1) {
         JEditorPane var2 = this.getEditor(var1);
         if (var2 != null) {
            int var3 = this.a;
            if (var1 != null && var1.getSource() == var2) {
               String var4 = var1.getActionCommand();

               try {
                  var3 = Integer.parseInt(var4, 10);
               } catch (NumberFormatException var6) {
               }
            }

            SimpleAttributeSet var7 = new SimpleAttributeSet();
            StyleConstants.setAlignment(var7, var3);
            this.setParagraphAttributes(var2, var7, false);
         }

      }
   }

   public static class ForegroundAction extends StyledEditorKit.StyledTextAction {
      private Color fg;

      public ForegroundAction(String var1, Color var2) {
         super(var1);
         this.fg = var2;
      }

      public void actionPerformed(ActionEvent var1) {
         JEditorPane var2 = this.getEditor(var1);
         if (var2 != null) {
            Color var3 = this.fg;
            if (var1 != null && var1.getSource() == var2) {
               String var4 = var1.getActionCommand();

               try {
                  var3 = Color.decode(var4);
               } catch (NumberFormatException var6) {
               }
            }

            if (var3 != null) {
               SimpleAttributeSet var7 = new SimpleAttributeSet();
               StyleConstants.setForeground(var7, var3);
               this.setCharacterAttributes(var2, var7, false);
            } else {
               UIManager.getLookAndFeel().provideErrorFeedback(var2);
            }
         }

      }
   }

   public static class FontSizeAction extends StyledEditorKit.StyledTextAction {
      private int size;

      public FontSizeAction(String var1, int var2) {
         super(var1);
         this.size = var2;
      }

      public void actionPerformed(ActionEvent var1) {
         JEditorPane var2 = this.getEditor(var1);
         if (var2 != null) {
            int var3 = this.size;
            if (var1 != null && var1.getSource() == var2) {
               String var4 = var1.getActionCommand();

               try {
                  var3 = Integer.parseInt(var4, 10);
               } catch (NumberFormatException var6) {
               }
            }

            if (var3 != 0) {
               SimpleAttributeSet var7 = new SimpleAttributeSet();
               StyleConstants.setFontSize(var7, var3);
               this.setCharacterAttributes(var2, var7, false);
            } else {
               UIManager.getLookAndFeel().provideErrorFeedback(var2);
            }
         }

      }
   }

   public static class FontFamilyAction extends StyledEditorKit.StyledTextAction {
      private String family;

      public FontFamilyAction(String var1, String var2) {
         super(var1);
         this.family = var2;
      }

      public void actionPerformed(ActionEvent var1) {
         JEditorPane var2 = this.getEditor(var1);
         if (var2 != null) {
            String var3 = this.family;
            if (var1 != null && var1.getSource() == var2) {
               String var4 = var1.getActionCommand();
               if (var4 != null) {
                  var3 = var4;
               }
            }

            if (var3 != null) {
               SimpleAttributeSet var5 = new SimpleAttributeSet();
               StyleConstants.setFontFamily(var5, var3);
               this.setCharacterAttributes(var2, var5, false);
            } else {
               UIManager.getLookAndFeel().provideErrorFeedback(var2);
            }
         }

      }
   }

   public abstract static class StyledTextAction extends TextAction {
      public StyledTextAction(String var1) {
         super(var1);
      }

      protected final JEditorPane getEditor(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         return var2 instanceof JEditorPane ? (JEditorPane)var2 : null;
      }

      protected final StyledDocument getStyledDocument(JEditorPane var1) {
         Document var2 = var1.getDocument();
         if (var2 instanceof StyledDocument) {
            return (StyledDocument)var2;
         } else {
            throw new IllegalArgumentException("document must be StyledDocument");
         }
      }

      protected final StyledEditorKit getStyledEditorKit(JEditorPane var1) {
         EditorKit var2 = var1.getEditorKit();
         if (var2 instanceof StyledEditorKit) {
            return (StyledEditorKit)var2;
         } else {
            throw new IllegalArgumentException("EditorKit must be StyledEditorKit");
         }
      }

      protected final void setCharacterAttributes(JEditorPane var1, AttributeSet var2, boolean var3) {
         int var4 = var1.getSelectionStart();
         int var5 = var1.getSelectionEnd();
         if (var4 != var5) {
            StyledDocument var6 = this.getStyledDocument(var1);
            var6.setCharacterAttributes(var4, var5 - var4, var2, var3);
         }

         StyledEditorKit var8 = this.getStyledEditorKit(var1);
         MutableAttributeSet var7 = var8.getInputAttributes();
         if (var3) {
            var7.removeAttributes((AttributeSet)var7);
         }

         var7.addAttributes(var2);
      }

      protected final void setParagraphAttributes(JEditorPane var1, AttributeSet var2, boolean var3) {
         int var4 = var1.getSelectionStart();
         int var5 = var1.getSelectionEnd();
         StyledDocument var6 = this.getStyledDocument(var1);
         var6.setParagraphAttributes(var4, var5 - var4, var2, var3);
      }
   }

   static class StyledViewFactory implements ViewFactory {
      public View create(Element var1) {
         String var2 = var1.getName();
         if (var2 != null) {
            if (var2.equals("content")) {
               return new LabelView(var1);
            }

            if (var2.equals("paragraph")) {
               return new ParagraphView(var1);
            }

            if (var2.equals("section")) {
               return new BoxView(var1, 1);
            }

            if (var2.equals("component")) {
               return new ComponentView(var1);
            }

            if (var2.equals("icon")) {
               return new IconView(var1);
            }
         }

         return new LabelView(var1);
      }
   }

   class AttributeTracker implements CaretListener, PropertyChangeListener, Serializable {
      void updateInputAttributes(int var1, int var2, JTextComponent var3) {
         Document var4 = var3.getDocument();
         if (var4 instanceof StyledDocument) {
            int var5 = Math.min(var1, var2);
            StyledDocument var6 = (StyledDocument)var4;
            StyledEditorKit.this.currentParagraph = var6.getParagraphElement(var5);
            Element var7;
            if (StyledEditorKit.this.currentParagraph.getStartOffset() != var5 && var1 == var2) {
               var7 = var6.getCharacterElement(Math.max(var5 - 1, 0));
            } else {
               var7 = var6.getCharacterElement(var5);
            }

            if (var7 != StyledEditorKit.this.currentRun) {
               StyledEditorKit.this.currentRun = var7;
               StyledEditorKit.this.createInputAttributes(StyledEditorKit.this.currentRun, StyledEditorKit.this.getInputAttributes());
            }

         }
      }

      public void propertyChange(PropertyChangeEvent var1) {
         Object var2 = var1.getNewValue();
         Object var3 = var1.getSource();
         if (var3 instanceof JTextComponent && var2 instanceof Document) {
            this.updateInputAttributes(0, 0, (JTextComponent)var3);
         }

      }

      public void caretUpdate(CaretEvent var1) {
         this.updateInputAttributes(var1.getDot(), var1.getMark(), (JTextComponent)var1.getSource());
      }
   }
}
