package javax.swing.text;

import java.awt.ComponentOrientation;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import javax.swing.Action;
import javax.swing.UIManager;
import sun.awt.SunToolkit;

public class DefaultEditorKit extends EditorKit {
   public static final String EndOfLineStringProperty = "__EndOfLine__";
   public static final String insertContentAction = "insert-content";
   public static final String insertBreakAction = "insert-break";
   public static final String insertTabAction = "insert-tab";
   public static final String deletePrevCharAction = "delete-previous";
   public static final String deleteNextCharAction = "delete-next";
   public static final String deleteNextWordAction = "delete-next-word";
   public static final String deletePrevWordAction = "delete-previous-word";
   public static final String readOnlyAction = "set-read-only";
   public static final String writableAction = "set-writable";
   public static final String cutAction = "cut-to-clipboard";
   public static final String copyAction = "copy-to-clipboard";
   public static final String pasteAction = "paste-from-clipboard";
   public static final String beepAction = "beep";
   public static final String pageUpAction = "page-up";
   public static final String pageDownAction = "page-down";
   static final String selectionPageUpAction = "selection-page-up";
   static final String selectionPageDownAction = "selection-page-down";
   static final String selectionPageLeftAction = "selection-page-left";
   static final String selectionPageRightAction = "selection-page-right";
   public static final String forwardAction = "caret-forward";
   public static final String backwardAction = "caret-backward";
   public static final String selectionForwardAction = "selection-forward";
   public static final String selectionBackwardAction = "selection-backward";
   public static final String upAction = "caret-up";
   public static final String downAction = "caret-down";
   public static final String selectionUpAction = "selection-up";
   public static final String selectionDownAction = "selection-down";
   public static final String beginWordAction = "caret-begin-word";
   public static final String endWordAction = "caret-end-word";
   public static final String selectionBeginWordAction = "selection-begin-word";
   public static final String selectionEndWordAction = "selection-end-word";
   public static final String previousWordAction = "caret-previous-word";
   public static final String nextWordAction = "caret-next-word";
   public static final String selectionPreviousWordAction = "selection-previous-word";
   public static final String selectionNextWordAction = "selection-next-word";
   public static final String beginLineAction = "caret-begin-line";
   public static final String endLineAction = "caret-end-line";
   public static final String selectionBeginLineAction = "selection-begin-line";
   public static final String selectionEndLineAction = "selection-end-line";
   public static final String beginParagraphAction = "caret-begin-paragraph";
   public static final String endParagraphAction = "caret-end-paragraph";
   public static final String selectionBeginParagraphAction = "selection-begin-paragraph";
   public static final String selectionEndParagraphAction = "selection-end-paragraph";
   public static final String beginAction = "caret-begin";
   public static final String endAction = "caret-end";
   public static final String selectionBeginAction = "selection-begin";
   public static final String selectionEndAction = "selection-end";
   public static final String selectWordAction = "select-word";
   public static final String selectLineAction = "select-line";
   public static final String selectParagraphAction = "select-paragraph";
   public static final String selectAllAction = "select-all";
   static final String unselectAction = "unselect";
   static final String toggleComponentOrientationAction = "toggle-componentOrientation";
   public static final String defaultKeyTypedAction = "default-typed";
   private static final Action[] defaultActions = new Action[]{new DefaultEditorKit.InsertContentAction(), new DefaultEditorKit.DeletePrevCharAction(), new DefaultEditorKit.DeleteNextCharAction(), new DefaultEditorKit.ReadOnlyAction(), new DefaultEditorKit.DeleteWordAction("delete-previous-word"), new DefaultEditorKit.DeleteWordAction("delete-next-word"), new DefaultEditorKit.WritableAction(), new DefaultEditorKit.CutAction(), new DefaultEditorKit.CopyAction(), new DefaultEditorKit.PasteAction(), new DefaultEditorKit.VerticalPageAction("page-up", -1, false), new DefaultEditorKit.VerticalPageAction("page-down", 1, false), new DefaultEditorKit.VerticalPageAction("selection-page-up", -1, true), new DefaultEditorKit.VerticalPageAction("selection-page-down", 1, true), new DefaultEditorKit.PageAction("selection-page-left", true, true), new DefaultEditorKit.PageAction("selection-page-right", false, true), new DefaultEditorKit.InsertBreakAction(), new DefaultEditorKit.BeepAction(), new DefaultEditorKit.NextVisualPositionAction("caret-forward", false, 3), new DefaultEditorKit.NextVisualPositionAction("caret-backward", false, 7), new DefaultEditorKit.NextVisualPositionAction("selection-forward", true, 3), new DefaultEditorKit.NextVisualPositionAction("selection-backward", true, 7), new DefaultEditorKit.NextVisualPositionAction("caret-up", false, 1), new DefaultEditorKit.NextVisualPositionAction("caret-down", false, 5), new DefaultEditorKit.NextVisualPositionAction("selection-up", true, 1), new DefaultEditorKit.NextVisualPositionAction("selection-down", true, 5), new DefaultEditorKit.BeginWordAction("caret-begin-word", false), new DefaultEditorKit.EndWordAction("caret-end-word", false), new DefaultEditorKit.BeginWordAction("selection-begin-word", true), new DefaultEditorKit.EndWordAction("selection-end-word", true), new DefaultEditorKit.PreviousWordAction("caret-previous-word", false), new DefaultEditorKit.NextWordAction("caret-next-word", false), new DefaultEditorKit.PreviousWordAction("selection-previous-word", true), new DefaultEditorKit.NextWordAction("selection-next-word", true), new DefaultEditorKit.BeginLineAction("caret-begin-line", false), new DefaultEditorKit.EndLineAction("caret-end-line", false), new DefaultEditorKit.BeginLineAction("selection-begin-line", true), new DefaultEditorKit.EndLineAction("selection-end-line", true), new DefaultEditorKit.BeginParagraphAction("caret-begin-paragraph", false), new DefaultEditorKit.EndParagraphAction("caret-end-paragraph", false), new DefaultEditorKit.BeginParagraphAction("selection-begin-paragraph", true), new DefaultEditorKit.EndParagraphAction("selection-end-paragraph", true), new DefaultEditorKit.BeginAction("caret-begin", false), new DefaultEditorKit.EndAction("caret-end", false), new DefaultEditorKit.BeginAction("selection-begin", true), new DefaultEditorKit.EndAction("selection-end", true), new DefaultEditorKit.DefaultKeyTypedAction(), new DefaultEditorKit.InsertTabAction(), new DefaultEditorKit.SelectWordAction(), new DefaultEditorKit.SelectLineAction(), new DefaultEditorKit.SelectParagraphAction(), new DefaultEditorKit.SelectAllAction(), new DefaultEditorKit.UnselectAction(), new DefaultEditorKit.ToggleComponentOrientationAction(), new DefaultEditorKit.DumpModelAction()};

   public String getContentType() {
      return "text/plain";
   }

   public ViewFactory getViewFactory() {
      return null;
   }

   public Action[] getActions() {
      return (Action[])defaultActions.clone();
   }

   public Caret createCaret() {
      return null;
   }

   public Document createDefaultDocument() {
      return new PlainDocument();
   }

   public void read(InputStream var1, Document var2, int var3) throws IOException, BadLocationException {
      this.read((Reader)(new InputStreamReader(var1)), var2, var3);
   }

   public void write(OutputStream var1, Document var2, int var3, int var4) throws IOException, BadLocationException {
      OutputStreamWriter var5 = new OutputStreamWriter(var1);
      this.write((Writer)var5, var2, var3, var4);
      var5.flush();
   }

   MutableAttributeSet getInputAttributes() {
      return null;
   }

   public void read(Reader var1, Document var2, int var3) throws IOException, BadLocationException {
      char[] var4 = new char[4096];
      boolean var6 = false;
      boolean var7 = false;
      boolean var8 = false;
      boolean var10 = var2.getLength() == 0;
      MutableAttributeSet var11 = this.getInputAttributes();

      int var5;
      while((var5 = var1.read(var4, 0, var4.length)) != -1) {
         int var9 = 0;

         for(int var12 = 0; var12 < var5; ++var12) {
            switch(var4[var12]) {
            case '\n':
               if (var6) {
                  if (var12 > var9 + 1) {
                     var2.insertString(var3, new String(var4, var9, var12 - var9 - 1), var11);
                     var3 += var12 - var9 - 1;
                  }

                  var6 = false;
                  var9 = var12;
                  var7 = true;
               }
               break;
            case '\r':
               if (var6) {
                  var8 = true;
                  if (var12 == 0) {
                     var2.insertString(var3, "\n", var11);
                     ++var3;
                  } else {
                     var4[var12 - 1] = '\n';
                  }
               } else {
                  var6 = true;
               }
               break;
            default:
               if (var6) {
                  var8 = true;
                  if (var12 == 0) {
                     var2.insertString(var3, "\n", var11);
                     ++var3;
                  } else {
                     var4[var12 - 1] = '\n';
                  }

                  var6 = false;
               }
            }
         }

         if (var9 < var5) {
            if (var6) {
               if (var9 < var5 - 1) {
                  var2.insertString(var3, new String(var4, var9, var5 - var9 - 1), var11);
                  var3 += var5 - var9 - 1;
               }
            } else {
               var2.insertString(var3, new String(var4, var9, var5 - var9), var11);
               var3 += var5 - var9;
            }
         }
      }

      if (var6) {
         var2.insertString(var3, "\n", var11);
         var8 = true;
      }

      if (var10) {
         if (var7) {
            var2.putProperty("__EndOfLine__", "\r\n");
         } else if (var8) {
            var2.putProperty("__EndOfLine__", "\r");
         } else {
            var2.putProperty("__EndOfLine__", "\n");
         }
      }

   }

   public void write(Writer var1, Document var2, int var3, int var4) throws IOException, BadLocationException {
      if (var3 >= 0 && var3 + var4 <= var2.getLength()) {
         Segment var5 = new Segment();
         int var6 = var4;
         int var7 = var3;
         Object var8 = var2.getProperty("__EndOfLine__");
         if (var8 == null) {
            try {
               var8 = System.getProperty("line.separator");
            } catch (SecurityException var15) {
            }
         }

         String var9;
         if (var8 instanceof String) {
            var9 = (String)var8;
         } else {
            var9 = null;
         }

         int var10;
         if (var8 != null && !var9.equals("\n")) {
            while(var6 > 0) {
               var10 = Math.min(var6, 4096);
               var2.getText(var7, var10, var5);
               int var11 = var5.offset;
               char[] var12 = var5.array;
               int var13 = var11 + var5.count;

               for(int var14 = var11; var14 < var13; ++var14) {
                  if (var12[var14] == '\n') {
                     if (var14 > var11) {
                        var1.write(var12, var11, var14 - var11);
                     }

                     var1.write(var9);
                     var11 = var14 + 1;
                  }
               }

               if (var13 > var11) {
                  var1.write(var12, var11, var13 - var11);
               }

               var7 += var10;
               var6 -= var10;
            }
         } else {
            while(var6 > 0) {
               var10 = Math.min(var6, 4096);
               var2.getText(var7, var10, var5);
               var1.write(var5.array, var5.offset, var5.count);
               var7 += var10;
               var6 -= var10;
            }
         }

         var1.flush();
      } else {
         throw new BadLocationException("DefaultEditorKit.write", var3);
      }
   }

   static class ToggleComponentOrientationAction extends TextAction {
      ToggleComponentOrientationAction() {
         super("toggle-componentOrientation");
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         if (var2 != null) {
            ComponentOrientation var3 = var2.getComponentOrientation();
            ComponentOrientation var4;
            if (var3 == ComponentOrientation.RIGHT_TO_LEFT) {
               var4 = ComponentOrientation.LEFT_TO_RIGHT;
            } else {
               var4 = ComponentOrientation.RIGHT_TO_LEFT;
            }

            var2.setComponentOrientation(var4);
            var2.repaint();
         }

      }
   }

   static class UnselectAction extends TextAction {
      UnselectAction() {
         super("unselect");
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         if (var2 != null) {
            var2.setCaretPosition(var2.getCaretPosition());
         }

      }
   }

   static class SelectAllAction extends TextAction {
      SelectAllAction() {
         super("select-all");
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         if (var2 != null) {
            Document var3 = var2.getDocument();
            var2.setCaretPosition(0);
            var2.moveCaretPosition(var3.getLength());
         }

      }
   }

   static class SelectParagraphAction extends TextAction {
      private Action start = new DefaultEditorKit.BeginParagraphAction("pigdog", false);
      private Action end = new DefaultEditorKit.EndParagraphAction("pigdog", true);

      SelectParagraphAction() {
         super("select-paragraph");
      }

      public void actionPerformed(ActionEvent var1) {
         this.start.actionPerformed(var1);
         this.end.actionPerformed(var1);
      }
   }

   static class SelectLineAction extends TextAction {
      private Action start = new DefaultEditorKit.BeginLineAction("pigdog", false);
      private Action end = new DefaultEditorKit.EndLineAction("pigdog", true);

      SelectLineAction() {
         super("select-line");
      }

      public void actionPerformed(ActionEvent var1) {
         this.start.actionPerformed(var1);
         this.end.actionPerformed(var1);
      }
   }

   static class SelectWordAction extends TextAction {
      private Action start = new DefaultEditorKit.BeginWordAction("pigdog", false);
      private Action end = new DefaultEditorKit.EndWordAction("pigdog", true);

      SelectWordAction() {
         super("select-word");
      }

      public void actionPerformed(ActionEvent var1) {
         this.start.actionPerformed(var1);
         this.end.actionPerformed(var1);
      }
   }

   static class EndAction extends TextAction {
      private boolean select;

      EndAction(String var1, boolean var2) {
         super(var1);
         this.select = var2;
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         if (var2 != null) {
            Document var3 = var2.getDocument();
            int var4 = var3.getLength();
            if (this.select) {
               var2.moveCaretPosition(var4);
            } else {
               var2.setCaretPosition(var4);
            }
         }

      }
   }

   static class BeginAction extends TextAction {
      private boolean select;

      BeginAction(String var1, boolean var2) {
         super(var1);
         this.select = var2;
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         if (var2 != null) {
            if (this.select) {
               var2.moveCaretPosition(0);
            } else {
               var2.setCaretPosition(0);
            }
         }

      }
   }

   static class EndParagraphAction extends TextAction {
      private boolean select;

      EndParagraphAction(String var1, boolean var2) {
         super(var1);
         this.select = var2;
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         if (var2 != null) {
            int var3 = var2.getCaretPosition();
            Element var4 = Utilities.getParagraphElement(var2, var3);
            var3 = Math.min(var2.getDocument().getLength(), var4.getEndOffset());
            if (this.select) {
               var2.moveCaretPosition(var3);
            } else {
               var2.setCaretPosition(var3);
            }
         }

      }
   }

   static class BeginParagraphAction extends TextAction {
      private boolean select;

      BeginParagraphAction(String var1, boolean var2) {
         super(var1);
         this.select = var2;
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         if (var2 != null) {
            int var3 = var2.getCaretPosition();
            Element var4 = Utilities.getParagraphElement(var2, var3);
            var3 = var4.getStartOffset();
            if (this.select) {
               var2.moveCaretPosition(var3);
            } else {
               var2.setCaretPosition(var3);
            }
         }

      }
   }

   static class EndLineAction extends TextAction {
      private boolean select;

      EndLineAction(String var1, boolean var2) {
         super(var1);
         this.select = var2;
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         if (var2 != null) {
            try {
               int var3 = var2.getCaretPosition();
               int var4 = Utilities.getRowEnd(var2, var3);
               if (this.select) {
                  var2.moveCaretPosition(var4);
               } else {
                  var2.setCaretPosition(var4);
               }
            } catch (BadLocationException var5) {
               UIManager.getLookAndFeel().provideErrorFeedback(var2);
            }
         }

      }
   }

   static class BeginLineAction extends TextAction {
      private boolean select;

      BeginLineAction(String var1, boolean var2) {
         super(var1);
         this.select = var2;
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         if (var2 != null) {
            try {
               int var3 = var2.getCaretPosition();
               int var4 = Utilities.getRowStart(var2, var3);
               if (this.select) {
                  var2.moveCaretPosition(var4);
               } else {
                  var2.setCaretPosition(var4);
               }
            } catch (BadLocationException var5) {
               UIManager.getLookAndFeel().provideErrorFeedback(var2);
            }
         }

      }
   }

   static class NextWordAction extends TextAction {
      private boolean select;

      NextWordAction(String var1, boolean var2) {
         super(var1);
         this.select = var2;
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         if (var2 != null) {
            int var3 = var2.getCaretPosition();
            boolean var4 = false;
            int var5 = var3;
            Element var6 = Utilities.getParagraphElement(var2, var3);

            try {
               var3 = Utilities.getNextWord(var2, var3);
               if (var3 >= var6.getEndOffset() && var5 != var6.getEndOffset() - 1) {
                  var3 = var6.getEndOffset() - 1;
               }
            } catch (BadLocationException var9) {
               int var8 = var2.getDocument().getLength();
               if (var3 != var8) {
                  if (var3 != var6.getEndOffset() - 1) {
                     var3 = var6.getEndOffset() - 1;
                  } else {
                     var3 = var8;
                  }
               } else {
                  var4 = true;
               }
            }

            if (!var4) {
               if (this.select) {
                  var2.moveCaretPosition(var3);
               } else {
                  var2.setCaretPosition(var3);
               }
            } else {
               UIManager.getLookAndFeel().provideErrorFeedback(var2);
            }
         }

      }
   }

   static class PreviousWordAction extends TextAction {
      private boolean select;

      PreviousWordAction(String var1, boolean var2) {
         super(var1);
         this.select = var2;
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         if (var2 != null) {
            int var3 = var2.getCaretPosition();
            boolean var4 = false;

            try {
               Element var5 = Utilities.getParagraphElement(var2, var3);
               var3 = Utilities.getPreviousWord(var2, var3);
               if (var3 < var5.getStartOffset()) {
                  var3 = Utilities.getParagraphElement(var2, var3).getEndOffset() - 1;
               }
            } catch (BadLocationException var6) {
               if (var3 != 0) {
                  var3 = 0;
               } else {
                  var4 = true;
               }
            }

            if (!var4) {
               if (this.select) {
                  var2.moveCaretPosition(var3);
               } else {
                  var2.setCaretPosition(var3);
               }
            } else {
               UIManager.getLookAndFeel().provideErrorFeedback(var2);
            }
         }

      }
   }

   static class EndWordAction extends TextAction {
      private boolean select;

      EndWordAction(String var1, boolean var2) {
         super(var1);
         this.select = var2;
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         if (var2 != null) {
            try {
               int var3 = var2.getCaretPosition();
               int var4 = Utilities.getWordEnd(var2, var3);
               if (this.select) {
                  var2.moveCaretPosition(var4);
               } else {
                  var2.setCaretPosition(var4);
               }
            } catch (BadLocationException var5) {
               UIManager.getLookAndFeel().provideErrorFeedback(var2);
            }
         }

      }
   }

   static class BeginWordAction extends TextAction {
      private boolean select;

      BeginWordAction(String var1, boolean var2) {
         super(var1);
         this.select = var2;
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         if (var2 != null) {
            try {
               int var3 = var2.getCaretPosition();
               int var4 = Utilities.getWordStart(var2, var3);
               if (this.select) {
                  var2.moveCaretPosition(var4);
               } else {
                  var2.setCaretPosition(var4);
               }
            } catch (BadLocationException var5) {
               UIManager.getLookAndFeel().provideErrorFeedback(var2);
            }
         }

      }
   }

   static class NextVisualPositionAction extends TextAction {
      private boolean select;
      private int direction;

      NextVisualPositionAction(String var1, boolean var2, int var3) {
         super(var1);
         this.select = var2;
         this.direction = var3;
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         if (var2 != null) {
            Caret var3 = var2.getCaret();
            DefaultCaret var4 = var3 instanceof DefaultCaret ? (DefaultCaret)var3 : null;
            int var5 = var3.getDot();
            Position.Bias[] var6 = new Position.Bias[1];
            Point var7 = var3.getMagicCaretPosition();

            try {
               if (var7 == null && (this.direction == 1 || this.direction == 5)) {
                  Rectangle var8 = var4 != null ? var2.getUI().modelToView(var2, var5, var4.getDotBias()) : var2.modelToView(var5);
                  var7 = new Point(var8.x, var8.y);
               }

               NavigationFilter var10 = var2.getNavigationFilter();
               if (var10 != null) {
                  var5 = var10.getNextVisualPositionFrom(var2, var5, var4 != null ? var4.getDotBias() : Position.Bias.Forward, this.direction, var6);
               } else {
                  var5 = var2.getUI().getNextVisualPositionFrom(var2, var5, var4 != null ? var4.getDotBias() : Position.Bias.Forward, this.direction, var6);
               }

               if (var6[0] == null) {
                  var6[0] = Position.Bias.Forward;
               }

               if (var4 != null) {
                  if (this.select) {
                     var4.moveDot(var5, var6[0]);
                  } else {
                     var4.setDot(var5, var6[0]);
                  }
               } else if (this.select) {
                  var3.moveDot(var5);
               } else {
                  var3.setDot(var5);
               }

               if (var7 != null && (this.direction == 1 || this.direction == 5)) {
                  var2.getCaret().setMagicCaretPosition(var7);
               }
            } catch (BadLocationException var9) {
            }
         }

      }
   }

   static class DumpModelAction extends TextAction {
      DumpModelAction() {
         super("dump-model");
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         if (var2 != null) {
            Document var3 = var2.getDocument();
            if (var3 instanceof AbstractDocument) {
               ((AbstractDocument)var3).dump(System.err);
            }
         }

      }
   }

   static class PageAction extends TextAction {
      private boolean select;
      private boolean left;

      public PageAction(String var1, boolean var2, boolean var3) {
         super(var1);
         this.select = var3;
         this.left = var2;
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         if (var2 != null) {
            Rectangle var4 = new Rectangle();
            var2.computeVisibleRect(var4);
            if (this.left) {
               var4.x = Math.max(0, var4.x - var4.width);
            } else {
               var4.x += var4.width;
            }

            int var3 = var2.getCaretPosition();
            if (var3 != -1) {
               if (this.left) {
                  var3 = var2.viewToModel(new Point(var4.x, var4.y));
               } else {
                  var3 = var2.viewToModel(new Point(var4.x + var4.width - 1, var4.y + var4.height - 1));
               }

               Document var5 = var2.getDocument();
               if (var3 != 0 && var3 > var5.getLength() - 1) {
                  var3 = var5.getLength() - 1;
               } else if (var3 < 0) {
                  var3 = 0;
               }

               if (this.select) {
                  var2.moveCaretPosition(var3);
               } else {
                  var2.setCaretPosition(var3);
               }
            }
         }

      }
   }

   static class VerticalPageAction extends TextAction {
      private boolean select;
      private int direction;

      public VerticalPageAction(String var1, int var2, boolean var3) {
         super(var1);
         this.select = var3;
         this.direction = var2;
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         if (var2 != null) {
            Rectangle var3 = var2.getVisibleRect();
            Rectangle var4 = new Rectangle(var3);
            int var5 = var2.getCaretPosition();
            int var6 = this.direction * var2.getScrollableBlockIncrement(var3, 1, this.direction);
            int var7 = var3.y;
            Caret var8 = var2.getCaret();
            Point var9 = var8.getMagicCaretPosition();
            if (var5 != -1) {
               try {
                  Rectangle var10 = var2.modelToView(var5);
                  int var11 = var9 != null ? var9.x : var10.x;
                  int var12 = var10.height;
                  if (var12 > 0) {
                     var6 = var6 / var12 * var12;
                  }

                  var4.y = this.constrainY(var2, var7 + var6, var3.height);
                  int var13;
                  if (var3.contains(var10.x, var10.y)) {
                     var13 = var2.viewToModel(new Point(var11, this.constrainY(var2, var10.y + var6, 0)));
                  } else if (this.direction == -1) {
                     var13 = var2.viewToModel(new Point(var11, var4.y));
                  } else {
                     var13 = var2.viewToModel(new Point(var11, var4.y + var3.height));
                  }

                  var13 = this.constrainOffset(var2, var13);
                  if (var13 != var5) {
                     int var14 = this.getAdjustedY(var2, var4, var13);
                     if (this.direction == -1 && var14 <= var7 || this.direction == 1 && var14 >= var7) {
                        var4.y = var14;
                        if (this.select) {
                           var2.moveCaretPosition(var13);
                        } else {
                           var2.setCaretPosition(var13);
                        }
                     }
                  }
               } catch (BadLocationException var15) {
               }
            } else {
               var4.y = this.constrainY(var2, var7 + var6, var3.height);
            }

            if (var9 != null) {
               var8.setMagicCaretPosition(var9);
            }

            var2.scrollRectToVisible(var4);
         }

      }

      private int constrainY(JTextComponent var1, int var2, int var3) {
         if (var2 < 0) {
            var2 = 0;
         } else if (var2 + var3 > var1.getHeight()) {
            var2 = Math.max(0, var1.getHeight() - var3);
         }

         return var2;
      }

      private int constrainOffset(JTextComponent var1, int var2) {
         Document var3 = var1.getDocument();
         if (var2 != 0 && var2 > var3.getLength()) {
            var2 = var3.getLength();
         }

         if (var2 < 0) {
            var2 = 0;
         }

         return var2;
      }

      private int getAdjustedY(JTextComponent var1, Rectangle var2, int var3) {
         int var4 = var2.y;

         try {
            Rectangle var5 = var1.modelToView(var3);
            if (var5.y < var2.y) {
               var4 = var5.y;
            } else if (var5.y > var2.y + var2.height || var5.y + var5.height > var2.y + var2.height) {
               var4 = var5.y + var5.height - var2.height;
            }
         } catch (BadLocationException var6) {
         }

         return var4;
      }
   }

   public static class BeepAction extends TextAction {
      public BeepAction() {
         super("beep");
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         UIManager.getLookAndFeel().provideErrorFeedback(var2);
      }
   }

   public static class PasteAction extends TextAction {
      public PasteAction() {
         super("paste-from-clipboard");
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         if (var2 != null) {
            var2.paste();
         }

      }
   }

   public static class CopyAction extends TextAction {
      public CopyAction() {
         super("copy-to-clipboard");
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         if (var2 != null) {
            var2.copy();
         }

      }
   }

   public static class CutAction extends TextAction {
      public CutAction() {
         super("cut-to-clipboard");
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         if (var2 != null) {
            var2.cut();
         }

      }
   }

   static class WritableAction extends TextAction {
      WritableAction() {
         super("set-writable");
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         if (var2 != null) {
            var2.setEditable(true);
         }

      }
   }

   static class ReadOnlyAction extends TextAction {
      ReadOnlyAction() {
         super("set-read-only");
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         if (var2 != null) {
            var2.setEditable(false);
         }

      }
   }

   static class DeleteWordAction extends TextAction {
      DeleteWordAction(String var1) {
         super(var1);

         assert var1 == "delete-previous-word" || var1 == "delete-next-word";

      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         if (var2 != null && var1 != null) {
            if (!var2.isEditable() || !var2.isEnabled()) {
               UIManager.getLookAndFeel().provideErrorFeedback(var2);
               return;
            }

            boolean var3 = true;

            try {
               int var4 = var2.getSelectionStart();
               Element var5 = Utilities.getParagraphElement(var2, var4);
               int var6;
               int var7;
               if ("delete-next-word" == this.getValue("Name")) {
                  var6 = Utilities.getNextWordInParagraph(var2, var5, var4, false);
                  if (var6 == -1) {
                     var7 = var5.getEndOffset();
                     if (var4 == var7 - 1) {
                        var6 = var7;
                     } else {
                        var6 = var7 - 1;
                     }
                  }
               } else {
                  var6 = Utilities.getPrevWordInParagraph(var2, var5, var4);
                  if (var6 == -1) {
                     var7 = var5.getStartOffset();
                     if (var4 == var7) {
                        var6 = var7 - 1;
                     } else {
                        var6 = var7;
                     }
                  }
               }

               var7 = Math.min(var4, var6);
               int var8 = Math.abs(var6 - var4);
               if (var7 >= 0) {
                  var2.getDocument().remove(var7, var8);
                  var3 = false;
               }
            } catch (BadLocationException var9) {
            }

            if (var3) {
               UIManager.getLookAndFeel().provideErrorFeedback(var2);
            }
         }

      }
   }

   static class DeleteNextCharAction extends TextAction {
      DeleteNextCharAction() {
         super("delete-next");
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         boolean var3 = true;
         if (var2 != null && var2.isEditable()) {
            try {
               Document var4 = var2.getDocument();
               Caret var5 = var2.getCaret();
               int var6 = var5.getDot();
               int var7 = var5.getMark();
               if (var6 != var7) {
                  var4.remove(Math.min(var6, var7), Math.abs(var6 - var7));
                  var3 = false;
               } else if (var6 < var4.getLength()) {
                  byte var8 = 1;
                  if (var6 < var4.getLength() - 1) {
                     String var9 = var4.getText(var6, 2);
                     char var10 = var9.charAt(0);
                     char var11 = var9.charAt(1);
                     if (var10 >= '\ud800' && var10 <= '\udbff' && var11 >= '\udc00' && var11 <= '\udfff') {
                        var8 = 2;
                     }
                  }

                  var4.remove(var6, var8);
                  var3 = false;
               }
            } catch (BadLocationException var12) {
            }
         }

         if (var3) {
            UIManager.getLookAndFeel().provideErrorFeedback(var2);
         }

      }
   }

   static class DeletePrevCharAction extends TextAction {
      DeletePrevCharAction() {
         super("delete-previous");
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         boolean var3 = true;
         if (var2 != null && var2.isEditable()) {
            try {
               Document var4 = var2.getDocument();
               Caret var5 = var2.getCaret();
               int var6 = var5.getDot();
               int var7 = var5.getMark();
               if (var6 != var7) {
                  var4.remove(Math.min(var6, var7), Math.abs(var6 - var7));
                  var3 = false;
               } else if (var6 > 0) {
                  byte var8 = 1;
                  if (var6 > 1) {
                     String var9 = var4.getText(var6 - 2, 2);
                     char var10 = var9.charAt(0);
                     char var11 = var9.charAt(1);
                     if (var10 >= '\ud800' && var10 <= '\udbff' && var11 >= '\udc00' && var11 <= '\udfff') {
                        var8 = 2;
                     }
                  }

                  var4.remove(var6 - var8, var8);
                  var3 = false;
               }
            } catch (BadLocationException var12) {
            }
         }

         if (var3) {
            UIManager.getLookAndFeel().provideErrorFeedback(var2);
         }

      }
   }

   public static class InsertTabAction extends TextAction {
      public InsertTabAction() {
         super("insert-tab");
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         if (var2 != null) {
            if (!var2.isEditable() || !var2.isEnabled()) {
               UIManager.getLookAndFeel().provideErrorFeedback(var2);
               return;
            }

            var2.replaceSelection("\t");
         }

      }
   }

   public static class InsertBreakAction extends TextAction {
      public InsertBreakAction() {
         super("insert-break");
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         if (var2 != null) {
            if (!var2.isEditable() || !var2.isEnabled()) {
               UIManager.getLookAndFeel().provideErrorFeedback(var2);
               return;
            }

            var2.replaceSelection("\n");
         }

      }
   }

   public static class InsertContentAction extends TextAction {
      public InsertContentAction() {
         super("insert-content");
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         if (var2 != null && var1 != null) {
            if (!var2.isEditable() || !var2.isEnabled()) {
               UIManager.getLookAndFeel().provideErrorFeedback(var2);
               return;
            }

            String var3 = var1.getActionCommand();
            if (var3 != null) {
               var2.replaceSelection(var3);
            } else {
               UIManager.getLookAndFeel().provideErrorFeedback(var2);
            }
         }

      }
   }

   public static class DefaultKeyTypedAction extends TextAction {
      public DefaultKeyTypedAction() {
         super("default-typed");
      }

      public void actionPerformed(ActionEvent var1) {
         JTextComponent var2 = this.getTextComponent(var1);
         if (var2 != null && var1 != null) {
            if (!var2.isEditable() || !var2.isEnabled()) {
               return;
            }

            String var3 = var1.getActionCommand();
            int var4 = var1.getModifiers();
            if (var3 != null && var3.length() > 0) {
               boolean var5 = true;
               Toolkit var6 = Toolkit.getDefaultToolkit();
               if (var6 instanceof SunToolkit) {
                  var5 = ((SunToolkit)var6).isPrintableCharacterModifiersMask(var4);
               }

               if (var5) {
                  char var7 = var3.charAt(0);
                  if (var7 >= ' ' && var7 != 127) {
                     var2.replaceSelection(var3);
                  }
               }
            }
         }

      }
   }
}
