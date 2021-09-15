package javax.swing.text;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.AttributedCharacterIterator;
import java.text.Format;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFormattedTextField;

public class InternationalFormatter extends DefaultFormatter {
   private static final Format.Field[] EMPTY_FIELD_ARRAY = new Format.Field[0];
   private Format format;
   private Comparable max;
   private Comparable min;
   private transient BitSet literalMask;
   private transient AttributedCharacterIterator iterator;
   private transient boolean validMask;
   private transient String string;
   private transient boolean ignoreDocumentMutate;

   public InternationalFormatter() {
      this.setOverwriteMode(false);
   }

   public InternationalFormatter(Format var1) {
      this();
      this.setFormat(var1);
   }

   public void setFormat(Format var1) {
      this.format = var1;
   }

   public Format getFormat() {
      return this.format;
   }

   public void setMinimum(Comparable var1) {
      if (this.getValueClass() == null && var1 != null) {
         this.setValueClass(var1.getClass());
      }

      this.min = var1;
   }

   public Comparable getMinimum() {
      return this.min;
   }

   public void setMaximum(Comparable var1) {
      if (this.getValueClass() == null && var1 != null) {
         this.setValueClass(var1.getClass());
      }

      this.max = var1;
   }

   public Comparable getMaximum() {
      return this.max;
   }

   public void install(JFormattedTextField var1) {
      super.install(var1);
      this.updateMaskIfNecessary();
      this.positionCursorAtInitialLocation();
   }

   public String valueToString(Object var1) throws ParseException {
      if (var1 == null) {
         return "";
      } else {
         Format var2 = this.getFormat();
         return var2 == null ? var1.toString() : var2.format(var1);
      }
   }

   public Object stringToValue(String var1) throws ParseException {
      Object var2 = this.stringToValue(var1, this.getFormat());
      if (var2 != null && this.getValueClass() != null && !this.getValueClass().isInstance(var2)) {
         var2 = super.stringToValue(var2.toString());
      }

      try {
         if (!this.isValidValue(var2, true)) {
            throw new ParseException("Value not within min/max range", 0);
         } else {
            return var2;
         }
      } catch (ClassCastException var4) {
         throw new ParseException("Class cast exception comparing values: " + var4, 0);
      }
   }

   public Format.Field[] getFields(int var1) {
      if (this.getAllowsInvalid()) {
         this.updateMask();
      }

      Map var2 = this.getAttributes(var1);
      if (var2 != null && var2.size() > 0) {
         ArrayList var3 = new ArrayList();
         var3.addAll(var2.keySet());
         return (Format.Field[])var3.toArray(EMPTY_FIELD_ARRAY);
      } else {
         return EMPTY_FIELD_ARRAY;
      }
   }

   public Object clone() throws CloneNotSupportedException {
      InternationalFormatter var1 = (InternationalFormatter)super.clone();
      var1.literalMask = null;
      var1.iterator = null;
      var1.validMask = false;
      var1.string = null;
      return var1;
   }

   protected Action[] getActions() {
      return this.getSupportsIncrement() ? new Action[]{new InternationalFormatter.IncrementAction("increment", 1), new InternationalFormatter.IncrementAction("decrement", -1)} : null;
   }

   Object stringToValue(String var1, Format var2) throws ParseException {
      return var2 == null ? var1 : var2.parseObject(var1);
   }

   boolean isValidValue(Object var1, boolean var2) {
      Comparable var3 = this.getMinimum();

      try {
         if (var3 != null && var3.compareTo(var1) > 0) {
            return false;
         }
      } catch (ClassCastException var7) {
         if (var2) {
            throw var7;
         }

         return false;
      }

      Comparable var4 = this.getMaximum();

      try {
         return var4 == null || var4.compareTo(var1) >= 0;
      } catch (ClassCastException var6) {
         if (var2) {
            throw var6;
         } else {
            return false;
         }
      }
   }

   Map<AttributedCharacterIterator.Attribute, Object> getAttributes(int var1) {
      if (this.isValidMask()) {
         AttributedCharacterIterator var2 = this.getIterator();
         if (var1 >= 0 && var1 <= var2.getEndIndex()) {
            var2.setIndex(var1);
            return var2.getAttributes();
         }
      }

      return null;
   }

   int getAttributeStart(AttributedCharacterIterator.Attribute var1) {
      if (this.isValidMask()) {
         AttributedCharacterIterator var2 = this.getIterator();
         var2.first();

         while(var2.current() != '\uffff') {
            if (var2.getAttribute(var1) != null) {
               return var2.getIndex();
            }

            var2.next();
         }
      }

      return -1;
   }

   AttributedCharacterIterator getIterator() {
      return this.iterator;
   }

   void updateMaskIfNecessary() {
      if (!this.getAllowsInvalid() && this.getFormat() != null) {
         if (!this.isValidMask()) {
            this.updateMask();
         } else {
            String var1 = this.getFormattedTextField().getText();
            if (!var1.equals(this.string)) {
               this.updateMask();
            }
         }
      }

   }

   void updateMask() {
      if (this.getFormat() != null) {
         Document var1 = this.getFormattedTextField().getDocument();
         this.validMask = false;
         if (var1 != null) {
            try {
               this.string = var1.getText(0, var1.getLength());
            } catch (BadLocationException var7) {
               this.string = null;
            }

            if (this.string != null) {
               try {
                  Object var2 = this.stringToValue(this.string);
                  AttributedCharacterIterator var3 = this.getFormat().formatToCharacterIterator(var2);
                  this.updateMask(var3);
               } catch (ParseException var4) {
               } catch (IllegalArgumentException var5) {
               } catch (NullPointerException var6) {
               }
            }
         }
      }

   }

   int getLiteralCountTo(int var1) {
      int var2 = 0;

      for(int var3 = 0; var3 < var1; ++var3) {
         if (this.isLiteral(var3)) {
            ++var2;
         }
      }

      return var2;
   }

   boolean isLiteral(int var1) {
      return this.isValidMask() && var1 < this.string.length() ? this.literalMask.get(var1) : false;
   }

   char getLiteral(int var1) {
      return this.isValidMask() && this.string != null && var1 < this.string.length() ? this.string.charAt(var1) : '\u0000';
   }

   boolean isNavigatable(int var1) {
      return !this.isLiteral(var1);
   }

   void updateValue(Object var1) {
      super.updateValue(var1);
      this.updateMaskIfNecessary();
   }

   void replace(DocumentFilter.FilterBypass var1, int var2, int var3, String var4, AttributeSet var5) throws BadLocationException {
      if (this.ignoreDocumentMutate) {
         var1.replace(var2, var3, var4, var5);
      } else {
         super.replace(var1, var2, var3, var4, var5);
      }
   }

   private int getNextNonliteralIndex(int var1, int var2) {
      int var3;
      for(var3 = this.getFormattedTextField().getDocument().getLength(); var1 >= 0 && var1 < var3; var1 += var2) {
         if (!this.isLiteral(var1)) {
            return var1;
         }
      }

      return var2 == -1 ? 0 : var3;
   }

   boolean canReplace(DefaultFormatter.ReplaceHolder var1) {
      if (!this.getAllowsInvalid()) {
         String var2 = var1.text;
         int var3 = var2 != null ? var2.length() : 0;
         JFormattedTextField var4 = this.getFormattedTextField();
         if (var3 == 0 && var1.length == 1 && var4.getSelectionStart() != var1.offset) {
            var1.offset = this.getNextNonliteralIndex(var1.offset, -1);
         } else if (!this.getOverwriteMode()) {
            if (var3 > 0) {
               var1.offset = this.getNextNonliteralIndex(var1.offset, 1);
            } else {
               var1.offset = this.getNextNonliteralIndex(var1.offset, -1);
            }
         } else {
            int var5 = var1.offset;
            int var6 = var5;
            boolean var7 = false;

            for(int var8 = 0; var8 < var1.length; ++var8) {
               while(this.isLiteral(var5)) {
                  ++var5;
               }

               if (var5 >= this.string.length()) {
                  var5 = var6;
                  var7 = true;
                  break;
               }

               ++var5;
               var6 = var5;
            }

            if (var7 || var4.getSelectedText() == null) {
               var1.length = var5 - var1.offset;
            }
         }

         ((InternationalFormatter.ExtendedReplaceHolder)var1).endOffset = var1.offset;
         ((InternationalFormatter.ExtendedReplaceHolder)var1).endTextLength = var1.text != null ? var1.text.length() : 0;
      } else {
         ((InternationalFormatter.ExtendedReplaceHolder)var1).endOffset = var1.offset;
         ((InternationalFormatter.ExtendedReplaceHolder)var1).endTextLength = var1.text != null ? var1.text.length() : 0;
      }

      boolean var9 = super.canReplace(var1);
      if (var9 && !this.getAllowsInvalid()) {
         ((InternationalFormatter.ExtendedReplaceHolder)var1).resetFromValue(this);
      }

      return var9;
   }

   boolean replace(DefaultFormatter.ReplaceHolder var1) throws BadLocationException {
      int var2 = -1;
      byte var3 = 1;
      int var4 = -1;
      if (var1.length > 0 && (var1.text == null || var1.text.length() == 0) && (this.getFormattedTextField().getSelectionStart() != var1.offset || var1.length > 1)) {
         var3 = -1;
      }

      if (!this.getAllowsInvalid()) {
         if ((var1.text == null || var1.text.length() == 0) && var1.length > 0) {
            var2 = this.getFormattedTextField().getSelectionStart();
         } else {
            var2 = var1.offset;
         }

         var4 = this.getLiteralCountTo(var2);
      }

      if (super.replace(var1)) {
         if (var2 != -1) {
            int var5 = ((InternationalFormatter.ExtendedReplaceHolder)var1).endOffset;
            var5 += ((InternationalFormatter.ExtendedReplaceHolder)var1).endTextLength;
            this.repositionCursor(var4, var5, var3);
         } else {
            var2 = ((InternationalFormatter.ExtendedReplaceHolder)var1).endOffset;
            if (var3 == 1) {
               var2 += ((InternationalFormatter.ExtendedReplaceHolder)var1).endTextLength;
            }

            this.repositionCursor(var2, var3);
         }

         return true;
      } else {
         return false;
      }
   }

   private void repositionCursor(int var1, int var2, int var3) {
      int var4 = this.getLiteralCountTo(var2);
      if (var4 != var2) {
         var2 -= var1;

         for(int var5 = 0; var5 < var2; ++var5) {
            if (this.isLiteral(var5)) {
               ++var2;
            }
         }
      }

      this.repositionCursor(var2, 1);
   }

   char getBufferedChar(int var1) {
      return this.isValidMask() && this.string != null && var1 < this.string.length() ? this.string.charAt(var1) : '\u0000';
   }

   boolean isValidMask() {
      return this.validMask;
   }

   boolean isLiteral(Map var1) {
      return var1 == null || var1.size() == 0;
   }

   private void updateMask(AttributedCharacterIterator var1) {
      if (var1 != null) {
         this.validMask = true;
         this.iterator = var1;
         if (this.literalMask == null) {
            this.literalMask = new BitSet();
         } else {
            for(int var2 = this.literalMask.length() - 1; var2 >= 0; --var2) {
               this.literalMask.clear(var2);
            }
         }

         var1.first();

         while(var1.current() != '\uffff') {
            Map var6 = var1.getAttributes();
            boolean var3 = this.isLiteral(var6);
            int var4 = var1.getIndex();

            for(int var5 = var1.getRunLimit(); var4 < var5; ++var4) {
               if (var3) {
                  this.literalMask.set(var4);
               } else {
                  this.literalMask.clear(var4);
               }
            }

            var1.setIndex(var4);
         }
      }

   }

   boolean canIncrement(Object var1, int var2) {
      return var1 != null;
   }

   void selectField(Object var1, int var2) {
      AttributedCharacterIterator var3 = this.getIterator();
      if (var3 != null && var1 instanceof AttributedCharacterIterator.Attribute) {
         AttributedCharacterIterator.Attribute var4 = (AttributedCharacterIterator.Attribute)var1;
         var3.first();

         while(var3.current() != '\uffff') {
            while(var3.getAttribute(var4) == null && var3.next() != '\uffff') {
            }

            if (var3.current() != '\uffff') {
               int var5 = var3.getRunLimit(var4);
               --var2;
               if (var2 <= 0) {
                  this.getFormattedTextField().select(var3.getIndex(), var5);
                  break;
               }

               var3.setIndex(var5);
               var3.next();
            }
         }
      }

   }

   Object getAdjustField(int var1, Map var2) {
      return null;
   }

   private int getFieldTypeCountTo(Object var1, int var2) {
      AttributedCharacterIterator var3 = this.getIterator();
      int var4 = 0;
      if (var3 != null && var1 instanceof AttributedCharacterIterator.Attribute) {
         AttributedCharacterIterator.Attribute var5 = (AttributedCharacterIterator.Attribute)var1;
         var3.first();

         while(var3.getIndex() < var2) {
            while(var3.getAttribute(var5) == null && var3.next() != '\uffff') {
            }

            if (var3.current() == '\uffff') {
               break;
            }

            var3.setIndex(var3.getRunLimit(var5));
            var3.next();
            ++var4;
         }
      }

      return var4;
   }

   Object adjustValue(Object var1, Map var2, Object var3, int var4) throws BadLocationException, ParseException {
      return null;
   }

   boolean getSupportsIncrement() {
      return false;
   }

   void resetValue(Object var1) throws BadLocationException, ParseException {
      Document var2 = this.getFormattedTextField().getDocument();
      String var3 = this.valueToString(var1);

      try {
         this.ignoreDocumentMutate = true;
         var2.remove(0, var2.getLength());
         var2.insertString(0, var3, (AttributeSet)null);
      } finally {
         this.ignoreDocumentMutate = false;
      }

      this.updateValue(var1);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.updateMaskIfNecessary();
   }

   DefaultFormatter.ReplaceHolder getReplaceHolder(DocumentFilter.FilterBypass var1, int var2, int var3, String var4, AttributeSet var5) {
      if (this.replaceHolder == null) {
         this.replaceHolder = new InternationalFormatter.ExtendedReplaceHolder();
      }

      return super.getReplaceHolder(var1, var2, var3, var4, var5);
   }

   private class IncrementAction extends AbstractAction {
      private int direction;

      IncrementAction(String var2, int var3) {
         super(var2);
         this.direction = var3;
      }

      public void actionPerformed(ActionEvent var1) {
         if (InternationalFormatter.this.getFormattedTextField().isEditable()) {
            if (InternationalFormatter.this.getAllowsInvalid()) {
               InternationalFormatter.this.updateMask();
            }

            boolean var2 = false;
            if (InternationalFormatter.this.isValidMask()) {
               int var3 = InternationalFormatter.this.getFormattedTextField().getSelectionStart();
               if (var3 != -1) {
                  AttributedCharacterIterator var4 = InternationalFormatter.this.getIterator();
                  var4.setIndex(var3);
                  Map var5 = var4.getAttributes();
                  Object var6 = InternationalFormatter.this.getAdjustField(var3, var5);
                  if (InternationalFormatter.this.canIncrement(var6, var3)) {
                     try {
                        Object var7 = InternationalFormatter.this.stringToValue(InternationalFormatter.this.getFormattedTextField().getText());
                        int var8 = InternationalFormatter.this.getFieldTypeCountTo(var6, var3);
                        var7 = InternationalFormatter.this.adjustValue(var7, var5, var6, this.direction);
                        if (var7 != null && InternationalFormatter.this.isValidValue(var7, false)) {
                           InternationalFormatter.this.resetValue(var7);
                           InternationalFormatter.this.updateMask();
                           if (InternationalFormatter.this.isValidMask()) {
                              InternationalFormatter.this.selectField(var6, var8);
                           }

                           var2 = true;
                        }
                     } catch (ParseException var9) {
                     } catch (BadLocationException var10) {
                     }
                  }
               }
            }

            if (!var2) {
               InternationalFormatter.this.invalidEdit();
            }
         }

      }
   }

   static class ExtendedReplaceHolder extends DefaultFormatter.ReplaceHolder {
      int endOffset;
      int endTextLength;

      void resetFromValue(InternationalFormatter var1) {
         this.offset = 0;

         try {
            this.text = var1.valueToString(this.value);
         } catch (ParseException var3) {
            this.text = "";
         }

         this.length = this.fb.getDocument().getLength();
      }
   }
}
