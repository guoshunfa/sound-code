package javax.swing.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class DefaultStyledDocument extends AbstractDocument implements StyledDocument {
   public static final int BUFFER_SIZE_DEFAULT = 4096;
   protected DefaultStyledDocument.ElementBuffer buffer;
   private transient Vector<Style> listeningStyles;
   private transient ChangeListener styleChangeListener;
   private transient ChangeListener styleContextChangeListener;
   private transient DefaultStyledDocument.ChangeUpdateRunnable updateRunnable;

   public DefaultStyledDocument(AbstractDocument.Content var1, StyleContext var2) {
      super(var1, var2);
      this.listeningStyles = new Vector();
      this.buffer = new DefaultStyledDocument.ElementBuffer(this.createDefaultRoot());
      Style var3 = var2.getStyle("default");
      this.setLogicalStyle(0, var3);
   }

   public DefaultStyledDocument(StyleContext var1) {
      this(new GapContent(4096), var1);
   }

   public DefaultStyledDocument() {
      this(new GapContent(4096), new StyleContext());
   }

   public Element getDefaultRootElement() {
      return this.buffer.getRootElement();
   }

   protected void create(DefaultStyledDocument.ElementSpec[] var1) {
      try {
         if (this.getLength() != 0) {
            this.remove(0, this.getLength());
         }

         this.writeLock();
         AbstractDocument.Content var2 = this.getContent();
         int var3 = var1.length;
         StringBuilder var4 = new StringBuilder();

         for(int var5 = 0; var5 < var3; ++var5) {
            DefaultStyledDocument.ElementSpec var6 = var1[var5];
            if (var6.getLength() > 0) {
               var4.append(var6.getArray(), var6.getOffset(), var6.getLength());
            }
         }

         UndoableEdit var13 = var2.insertString(0, var4.toString());
         int var14 = var4.length();
         AbstractDocument.DefaultDocumentEvent var7 = new AbstractDocument.DefaultDocumentEvent(0, var14, DocumentEvent.EventType.INSERT);
         var7.addEdit(var13);
         this.buffer.create(var14, var1, var7);
         super.insertUpdate(var7, (AttributeSet)null);
         var7.end();
         this.fireInsertUpdate(var7);
         this.fireUndoableEditUpdate(new UndoableEditEvent(this, var7));
      } catch (BadLocationException var11) {
         throw new StateInvariantError("problem initializing");
      } finally {
         this.writeUnlock();
      }
   }

   protected void insert(int var1, DefaultStyledDocument.ElementSpec[] var2) throws BadLocationException {
      if (var2 != null && var2.length != 0) {
         try {
            this.writeLock();
            AbstractDocument.Content var3 = this.getContent();
            int var4 = var2.length;
            StringBuilder var5 = new StringBuilder();

            for(int var6 = 0; var6 < var4; ++var6) {
               DefaultStyledDocument.ElementSpec var7 = var2[var6];
               if (var7.getLength() > 0) {
                  var5.append(var7.getArray(), var7.getOffset(), var7.getLength());
               }
            }

            if (var5.length() == 0) {
               return;
            }

            UndoableEdit var12 = var3.insertString(var1, var5.toString());
            int var13 = var5.length();
            AbstractDocument.DefaultDocumentEvent var8 = new AbstractDocument.DefaultDocumentEvent(var1, var13, DocumentEvent.EventType.INSERT);
            var8.addEdit(var12);
            this.buffer.insert(var1, var13, var2, var8);
            super.insertUpdate(var8, (AttributeSet)null);
            var8.end();
            this.fireInsertUpdate(var8);
            this.fireUndoableEditUpdate(new UndoableEditEvent(this, var8));
         } finally {
            this.writeUnlock();
         }

      }
   }

   public void removeElement(Element var1) {
      try {
         this.writeLock();
         this.removeElementImpl(var1);
      } finally {
         this.writeUnlock();
      }

   }

   private void removeElementImpl(Element var1) {
      if (((Element)var1).getDocument() != this) {
         throw new IllegalArgumentException("element doesn't belong to document");
      } else {
         AbstractDocument.BranchElement var2 = (AbstractDocument.BranchElement)((Element)var1).getParentElement();
         if (var2 == null) {
            throw new IllegalArgumentException("can't remove the root element");
         } else {
            int var3 = ((Element)var1).getStartOffset();
            int var4 = var3;
            int var5 = ((Element)var1).getEndOffset();
            int var6 = var5;
            int var7 = this.getLength() + 1;
            AbstractDocument.Content var8 = this.getContent();
            boolean var9 = false;
            boolean var10 = Utilities.isComposedTextElement((Element)var1);
            if (var5 >= var7) {
               if (var3 <= 0) {
                  throw new IllegalArgumentException("can't remove the whole content");
               }

               var6 = var7 - 1;

               try {
                  if (var8.getString(var3 - 1, 1).charAt(0) == '\n') {
                     --var4;
                  }
               } catch (BadLocationException var25) {
                  throw new IllegalStateException(var25);
               }

               var9 = true;
            }

            int var11 = var6 - var4;
            AbstractDocument.DefaultDocumentEvent var12 = new AbstractDocument.DefaultDocumentEvent(var4, var11, DocumentEvent.EventType.REMOVE);
            UndoableEdit var13 = null;

            while(var2.getElementCount() == 1) {
               var1 = var2;
               var2 = (AbstractDocument.BranchElement)var2.getParentElement();
               if (var2 == null) {
                  throw new IllegalStateException("invalid element structure");
               }
            }

            Element[] var14 = new Element[]{(Element)var1};
            Element[] var15 = new Element[0];
            int var16 = var2.getElementIndex(var3);
            var2.replace(var16, 1, var15);
            var12.addEdit(new AbstractDocument.ElementEdit(var2, var16, var14, var15));
            if (var11 > 0) {
               try {
                  var13 = var8.remove(var4, var11);
                  if (var13 != null) {
                     var12.addEdit(var13);
                  }
               } catch (BadLocationException var24) {
                  throw new IllegalStateException(var24);
               }

               var7 -= var11;
            }

            if (var9) {
               Element var17;
               for(var17 = var2.getElement(var2.getElementCount() - 1); var17 != null && !var17.isLeaf(); var17 = var17.getElement(var17.getElementCount() - 1)) {
               }

               if (var17 == null) {
                  throw new IllegalStateException("invalid element structure");
               }

               int var18 = var17.getStartOffset();
               AbstractDocument.BranchElement var19 = (AbstractDocument.BranchElement)var17.getParentElement();
               int var20 = var19.getElementIndex(var18);
               Element var21 = this.createLeafElement(var19, var17.getAttributes(), var18, var7);
               Element[] var22 = new Element[]{var17};
               Element[] var23 = new Element[]{var21};
               var19.replace(var20, 1, var23);
               var12.addEdit(new AbstractDocument.ElementEdit(var19, var20, var22, var23));
            }

            this.postRemoveUpdate(var12);
            var12.end();
            this.fireRemoveUpdate(var12);
            if (!var10 || var13 == null) {
               this.fireUndoableEditUpdate(new UndoableEditEvent(this, var12));
            }

         }
      }
   }

   public Style addStyle(String var1, Style var2) {
      StyleContext var3 = (StyleContext)this.getAttributeContext();
      return var3.addStyle(var1, var2);
   }

   public void removeStyle(String var1) {
      StyleContext var2 = (StyleContext)this.getAttributeContext();
      var2.removeStyle(var1);
   }

   public Style getStyle(String var1) {
      StyleContext var2 = (StyleContext)this.getAttributeContext();
      return var2.getStyle(var1);
   }

   public Enumeration<?> getStyleNames() {
      return ((StyleContext)this.getAttributeContext()).getStyleNames();
   }

   public void setLogicalStyle(int var1, Style var2) {
      Element var3 = this.getParagraphElement(var1);
      if (var3 != null && var3 instanceof AbstractDocument.AbstractElement) {
         try {
            this.writeLock();
            DefaultStyledDocument.StyleChangeUndoableEdit var4 = new DefaultStyledDocument.StyleChangeUndoableEdit((AbstractDocument.AbstractElement)var3, var2);
            ((AbstractDocument.AbstractElement)var3).setResolveParent(var2);
            int var5 = var3.getStartOffset();
            int var6 = var3.getEndOffset();
            AbstractDocument.DefaultDocumentEvent var7 = new AbstractDocument.DefaultDocumentEvent(var5, var6 - var5, DocumentEvent.EventType.CHANGE);
            var7.addEdit(var4);
            var7.end();
            this.fireChangedUpdate(var7);
            this.fireUndoableEditUpdate(new UndoableEditEvent(this, var7));
         } finally {
            this.writeUnlock();
         }
      }

   }

   public Style getLogicalStyle(int var1) {
      Style var2 = null;
      Element var3 = this.getParagraphElement(var1);
      if (var3 != null) {
         AttributeSet var4 = var3.getAttributes();
         AttributeSet var5 = var4.getResolveParent();
         if (var5 instanceof Style) {
            var2 = (Style)var5;
         }
      }

      return var2;
   }

   public void setCharacterAttributes(int var1, int var2, AttributeSet var3, boolean var4) {
      if (var2 != 0) {
         try {
            this.writeLock();
            AbstractDocument.DefaultDocumentEvent var5 = new AbstractDocument.DefaultDocumentEvent(var1, var2, DocumentEvent.EventType.CHANGE);
            this.buffer.change(var1, var2, var5);
            AttributeSet var6 = var3.copyAttributes();
            int var8 = var1;

            while(true) {
               if (var8 < var1 + var2) {
                  Element var9 = this.getCharacterElement(var8);
                  int var7 = var9.getEndOffset();
                  if (var8 != var7) {
                     MutableAttributeSet var10 = (MutableAttributeSet)var9.getAttributes();
                     var5.addEdit(new DefaultStyledDocument.AttributeUndoableEdit(var9, var6, var4));
                     if (var4) {
                        var10.removeAttributes((AttributeSet)var10);
                     }

                     var10.addAttributes(var3);
                     var8 = var7;
                     continue;
                  }
               }

               var5.end();
               this.fireChangedUpdate(var5);
               this.fireUndoableEditUpdate(new UndoableEditEvent(this, var5));
               return;
            }
         } finally {
            this.writeUnlock();
         }
      }
   }

   public void setParagraphAttributes(int var1, int var2, AttributeSet var3, boolean var4) {
      try {
         this.writeLock();
         AbstractDocument.DefaultDocumentEvent var5 = new AbstractDocument.DefaultDocumentEvent(var1, var2, DocumentEvent.EventType.CHANGE);
         AttributeSet var6 = var3.copyAttributes();
         Element var7 = this.getDefaultRootElement();
         int var8 = var7.getElementIndex(var1);
         int var9 = var7.getElementIndex(var1 + (var2 > 0 ? var2 - 1 : 0));
         boolean var10 = Boolean.TRUE.equals(this.getProperty("i18n"));
         boolean var11 = false;

         for(int var12 = var8; var12 <= var9; ++var12) {
            Element var13 = var7.getElement(var12);
            MutableAttributeSet var14 = (MutableAttributeSet)var13.getAttributes();
            var5.addEdit(new DefaultStyledDocument.AttributeUndoableEdit(var13, var6, var4));
            if (var4) {
               var14.removeAttributes((AttributeSet)var14);
            }

            var14.addAttributes(var3);
            if (var10 && !var11) {
               var11 = var14.getAttribute(TextAttribute.RUN_DIRECTION) != null;
            }
         }

         if (var11) {
            this.updateBidi(var5);
         }

         var5.end();
         this.fireChangedUpdate(var5);
         this.fireUndoableEditUpdate(new UndoableEditEvent(this, var5));
      } finally {
         this.writeUnlock();
      }

   }

   public Element getParagraphElement(int var1) {
      Element var2;
      int var3;
      for(var2 = this.getDefaultRootElement(); !var2.isLeaf(); var2 = var2.getElement(var3)) {
         var3 = var2.getElementIndex(var1);
      }

      return var2 != null ? var2.getParentElement() : var2;
   }

   public Element getCharacterElement(int var1) {
      Element var2;
      int var3;
      for(var2 = this.getDefaultRootElement(); !var2.isLeaf(); var2 = var2.getElement(var3)) {
         var3 = var2.getElementIndex(var1);
      }

      return var2;
   }

   protected void insertUpdate(AbstractDocument.DefaultDocumentEvent var1, AttributeSet var2) {
      int var3 = var1.getOffset();
      int var4 = var1.getLength();
      if (var2 == null) {
         var2 = SimpleAttributeSet.EMPTY;
      }

      Element var5 = this.getParagraphElement(var3 + var4);
      AttributeSet var6 = var5.getAttributes();
      Element var7 = this.getParagraphElement(var3);
      Element var8 = var7.getElement(var7.getElementIndex(var3));
      int var9 = var3 + var4;
      boolean var10 = var8.getEndOffset() == var9;
      AttributeSet var11 = var8.getAttributes();

      try {
         Segment var12 = new Segment();
         Vector var13 = new Vector();
         DefaultStyledDocument.ElementSpec var14 = null;
         boolean var15 = false;
         short var16 = 6;
         if (var3 > 0) {
            this.getText(var3 - 1, 1, var12);
            if (var12.array[var12.offset] == '\n') {
               var15 = true;
               var16 = this.createSpecsForInsertAfterNewline(var5, var7, var6, var13, var3, var9);

               for(int var17 = var13.size() - 1; var17 >= 0; --var17) {
                  DefaultStyledDocument.ElementSpec var18 = (DefaultStyledDocument.ElementSpec)var13.elementAt(var17);
                  if (var18.getType() == 1) {
                     var14 = var18;
                     break;
                  }
               }
            }
         }

         if (!var15) {
            var6 = var7.getAttributes();
         }

         this.getText(var3, var4, var12);
         char[] var25 = var12.array;
         int var26 = var12.offset + var12.count;
         int var19 = var12.offset;

         int var21;
         for(int var20 = var12.offset; var20 < var26; ++var20) {
            if (var25[var20] == '\n') {
               var21 = var20 + 1;
               var13.addElement(new DefaultStyledDocument.ElementSpec(var2, (short)3, var21 - var19));
               var13.addElement(new DefaultStyledDocument.ElementSpec((AttributeSet)null, (short)2));
               var14 = new DefaultStyledDocument.ElementSpec(var6, (short)1);
               var13.addElement(var14);
               var19 = var21;
            }
         }

         if (var19 < var26) {
            var13.addElement(new DefaultStyledDocument.ElementSpec(var2, (short)3, var26 - var19));
         }

         DefaultStyledDocument.ElementSpec var27 = (DefaultStyledDocument.ElementSpec)var13.firstElement();
         var21 = this.getLength();
         if (var27.getType() == 3 && var11.isEqual(var2)) {
            var27.setDirection((short)4);
         }

         if (var14 != null) {
            if (var15) {
               var14.setDirection(var16);
            } else if (var7.getEndOffset() != var9) {
               var14.setDirection((short)7);
            } else {
               Element var22 = var7.getParentElement();
               int var23 = var22.getElementIndex(var3);
               if (var23 + 1 < var22.getElementCount() && !var22.getElement(var23 + 1).isLeaf()) {
                  var14.setDirection((short)5);
               }
            }
         }

         DefaultStyledDocument.ElementSpec var28;
         if (var10 && var9 < var21) {
            var28 = (DefaultStyledDocument.ElementSpec)var13.lastElement();
            if (var28.getType() == 3 && var28.getDirection() != 4 && (var14 == null && (var5 == var7 || var15) || var14 != null && var14.getDirection() != 6)) {
               Element var30 = var5.getElement(var5.getElementIndex(var9));
               if (var30.isLeaf() && var2.isEqual(var30.getAttributes())) {
                  var28.setDirection((short)5);
               }
            }
         } else if (!var10 && var14 != null && var14.getDirection() == 7) {
            var28 = (DefaultStyledDocument.ElementSpec)var13.lastElement();
            if (var28.getType() == 3 && var28.getDirection() != 4 && var2.isEqual(var11)) {
               var28.setDirection((short)5);
            }
         }

         if (Utilities.isComposedTextAttributeDefined(var2)) {
            MutableAttributeSet var29 = (MutableAttributeSet)var2;
            var29.addAttributes(var11);
            var29.addAttribute("$ename", "content");
            var29.addAttribute(StyleConstants.NameAttribute, "content");
            if (var29.isDefined("CR")) {
               var29.removeAttribute("CR");
            }
         }

         DefaultStyledDocument.ElementSpec[] var31 = new DefaultStyledDocument.ElementSpec[var13.size()];
         var13.copyInto(var31);
         this.buffer.insert(var3, var4, var31, var1);
      } catch (BadLocationException var24) {
      }

      super.insertUpdate(var1, var2);
   }

   short createSpecsForInsertAfterNewline(Element var1, Element var2, AttributeSet var3, Vector<DefaultStyledDocument.ElementSpec> var4, int var5, int var6) {
      if (var1.getParentElement() == var2.getParentElement()) {
         DefaultStyledDocument.ElementSpec var7 = new DefaultStyledDocument.ElementSpec(var3, (short)2);
         var4.addElement(var7);
         var7 = new DefaultStyledDocument.ElementSpec(var3, (short)1);
         var4.addElement(var7);
         if (var2.getEndOffset() != var6) {
            return 7;
         }

         Element var8 = var2.getParentElement();
         if (var8.getElementIndex(var5) + 1 < var8.getElementCount()) {
            return 5;
         }
      } else {
         Vector var13 = new Vector();
         Vector var14 = new Vector();

         Element var9;
         for(var9 = var2; var9 != null; var9 = var9.getParentElement()) {
            var13.addElement(var9);
         }

         var9 = var1;

         int var10;
         for(var10 = -1; var9 != null && (var10 = var13.indexOf(var9)) == -1; var9 = var9.getParentElement()) {
            var14.addElement(var9);
         }

         if (var9 != null) {
            for(int var11 = 0; var11 < var10; ++var11) {
               var4.addElement(new DefaultStyledDocument.ElementSpec((AttributeSet)null, (short)2));
            }

            for(int var12 = var14.size() - 1; var12 >= 0; --var12) {
               DefaultStyledDocument.ElementSpec var15 = new DefaultStyledDocument.ElementSpec(((Element)var14.elementAt(var12)).getAttributes(), (short)1);
               if (var12 > 0) {
                  var15.setDirection((short)5);
               }

               var4.addElement(var15);
            }

            if (var14.size() > 0) {
               return 5;
            }

            return 7;
         }
      }

      return 6;
   }

   protected void removeUpdate(AbstractDocument.DefaultDocumentEvent var1) {
      super.removeUpdate(var1);
      this.buffer.remove(var1.getOffset(), var1.getLength(), var1);
   }

   protected AbstractDocument.AbstractElement createDefaultRoot() {
      this.writeLock();
      DefaultStyledDocument.SectionElement var1 = new DefaultStyledDocument.SectionElement();
      AbstractDocument.BranchElement var2 = new AbstractDocument.BranchElement(var1, (AttributeSet)null);
      AbstractDocument.LeafElement var3 = new AbstractDocument.LeafElement(var2, (AttributeSet)null, 0, 1);
      Element[] var4 = new Element[]{var3};
      var2.replace(0, 0, var4);
      var4[0] = var2;
      var1.replace(0, 0, var4);
      this.writeUnlock();
      return var1;
   }

   public Color getForeground(AttributeSet var1) {
      StyleContext var2 = (StyleContext)this.getAttributeContext();
      return var2.getForeground(var1);
   }

   public Color getBackground(AttributeSet var1) {
      StyleContext var2 = (StyleContext)this.getAttributeContext();
      return var2.getBackground(var1);
   }

   public Font getFont(AttributeSet var1) {
      StyleContext var2 = (StyleContext)this.getAttributeContext();
      return var2.getFont(var1);
   }

   protected void styleChanged(Style var1) {
      if (this.getLength() != 0) {
         if (this.updateRunnable == null) {
            this.updateRunnable = new DefaultStyledDocument.ChangeUpdateRunnable();
         }

         synchronized(this.updateRunnable) {
            if (!this.updateRunnable.isPending) {
               SwingUtilities.invokeLater(this.updateRunnable);
               this.updateRunnable.isPending = true;
            }
         }
      }

   }

   public void addDocumentListener(DocumentListener var1) {
      synchronized(this.listeningStyles) {
         int var3 = this.listenerList.getListenerCount(DocumentListener.class);
         super.addDocumentListener(var1);
         if (var3 == 0) {
            if (this.styleContextChangeListener == null) {
               this.styleContextChangeListener = this.createStyleContextChangeListener();
            }

            if (this.styleContextChangeListener != null) {
               StyleContext var4 = (StyleContext)this.getAttributeContext();
               List var5 = DefaultStyledDocument.AbstractChangeHandler.getStaleListeners(this.styleContextChangeListener);
               Iterator var6 = var5.iterator();

               while(var6.hasNext()) {
                  ChangeListener var7 = (ChangeListener)var6.next();
                  var4.removeChangeListener(var7);
               }

               var4.addChangeListener(this.styleContextChangeListener);
            }

            this.updateStylesListeningTo();
         }

      }
   }

   public void removeDocumentListener(DocumentListener var1) {
      synchronized(this.listeningStyles) {
         super.removeDocumentListener(var1);
         if (this.listenerList.getListenerCount(DocumentListener.class) == 0) {
            for(int var3 = this.listeningStyles.size() - 1; var3 >= 0; --var3) {
               ((Style)this.listeningStyles.elementAt(var3)).removeChangeListener(this.styleChangeListener);
            }

            this.listeningStyles.removeAllElements();
            if (this.styleContextChangeListener != null) {
               StyleContext var6 = (StyleContext)this.getAttributeContext();
               var6.removeChangeListener(this.styleContextChangeListener);
            }
         }

      }
   }

   ChangeListener createStyleChangeListener() {
      return new DefaultStyledDocument.StyleChangeHandler(this);
   }

   ChangeListener createStyleContextChangeListener() {
      return new DefaultStyledDocument.StyleContextChangeHandler(this);
   }

   void updateStylesListeningTo() {
      synchronized(this.listeningStyles) {
         StyleContext var2 = (StyleContext)this.getAttributeContext();
         if (this.styleChangeListener == null) {
            this.styleChangeListener = this.createStyleChangeListener();
         }

         if (this.styleChangeListener != null && var2 != null) {
            Enumeration var3 = var2.getStyleNames();
            Vector var4 = (Vector)this.listeningStyles.clone();
            this.listeningStyles.removeAllElements();
            List var5 = DefaultStyledDocument.AbstractChangeHandler.getStaleListeners(this.styleChangeListener);

            while(true) {
               Style var7;
               while(var3.hasMoreElements()) {
                  String var6 = (String)var3.nextElement();
                  var7 = var2.getStyle(var6);
                  int var8 = var4.indexOf(var7);
                  this.listeningStyles.addElement(var7);
                  if (var8 == -1) {
                     Iterator var9 = var5.iterator();

                     while(var9.hasNext()) {
                        ChangeListener var10 = (ChangeListener)var9.next();
                        var7.removeChangeListener(var10);
                     }

                     var7.addChangeListener(this.styleChangeListener);
                  } else {
                     var4.removeElementAt(var8);
                  }
               }

               for(int var13 = var4.size() - 1; var13 >= 0; --var13) {
                  var7 = (Style)var4.elementAt(var13);
                  var7.removeChangeListener(this.styleChangeListener);
               }

               if (this.listeningStyles.size() == 0) {
                  this.styleChangeListener = null;
               }
               break;
            }
         }

      }
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      this.listeningStyles = new Vector();
      var1.defaultReadObject();
      if (this.styleContextChangeListener == null && this.listenerList.getListenerCount(DocumentListener.class) > 0) {
         this.styleContextChangeListener = this.createStyleContextChangeListener();
         if (this.styleContextChangeListener != null) {
            StyleContext var2 = (StyleContext)this.getAttributeContext();
            var2.addChangeListener(this.styleContextChangeListener);
         }

         this.updateStylesListeningTo();
      }

   }

   class ChangeUpdateRunnable implements Runnable {
      boolean isPending = false;

      public void run() {
         synchronized(this) {
            this.isPending = false;
         }

         try {
            DefaultStyledDocument.this.writeLock();
            AbstractDocument.DefaultDocumentEvent var1 = DefaultStyledDocument.this.new DefaultDocumentEvent(0, DefaultStyledDocument.this.getLength(), DocumentEvent.EventType.CHANGE);
            var1.end();
            DefaultStyledDocument.this.fireChangedUpdate(var1);
         } finally {
            DefaultStyledDocument.this.writeUnlock();
         }

      }
   }

   static class StyleContextChangeHandler extends DefaultStyledDocument.AbstractChangeHandler {
      StyleContextChangeHandler(DefaultStyledDocument var1) {
         super(var1);
      }

      void fireStateChanged(DefaultStyledDocument var1, ChangeEvent var2) {
         var1.updateStylesListeningTo();
      }
   }

   static class StyleChangeHandler extends DefaultStyledDocument.AbstractChangeHandler {
      StyleChangeHandler(DefaultStyledDocument var1) {
         super(var1);
      }

      void fireStateChanged(DefaultStyledDocument var1, ChangeEvent var2) {
         Object var3 = var2.getSource();
         if (var3 instanceof Style) {
            var1.styleChanged((Style)var3);
         } else {
            var1.styleChanged((Style)null);
         }

      }
   }

   abstract static class AbstractChangeHandler implements ChangeListener {
      private static final Map<Class, ReferenceQueue<DefaultStyledDocument>> queueMap = new HashMap();
      private DefaultStyledDocument.AbstractChangeHandler.DocReference doc;

      AbstractChangeHandler(DefaultStyledDocument var1) {
         Class var2 = this.getClass();
         ReferenceQueue var3;
         synchronized(queueMap) {
            var3 = (ReferenceQueue)queueMap.get(var2);
            if (var3 == null) {
               var3 = new ReferenceQueue();
               queueMap.put(var2, var3);
            }
         }

         this.doc = new DefaultStyledDocument.AbstractChangeHandler.DocReference(var1, var3);
      }

      static List<ChangeListener> getStaleListeners(ChangeListener var0) {
         ArrayList var1 = new ArrayList();
         ReferenceQueue var2 = (ReferenceQueue)queueMap.get(var0.getClass());
         if (var2 != null) {
            DefaultStyledDocument.AbstractChangeHandler.DocReference var3;
            synchronized(var2) {
               while((var3 = (DefaultStyledDocument.AbstractChangeHandler.DocReference)var2.poll()) != null) {
                  var1.add(var3.getListener());
               }
            }
         }

         return var1;
      }

      public void stateChanged(ChangeEvent var1) {
         DefaultStyledDocument var2 = (DefaultStyledDocument)this.doc.get();
         if (var2 != null) {
            this.fireStateChanged(var2, var1);
         }

      }

      abstract void fireStateChanged(DefaultStyledDocument var1, ChangeEvent var2);

      private class DocReference extends WeakReference<DefaultStyledDocument> {
         DocReference(DefaultStyledDocument var2, ReferenceQueue<DefaultStyledDocument> var3) {
            super(var2, var3);
         }

         ChangeListener getListener() {
            return AbstractChangeHandler.this;
         }
      }
   }

   static class StyleChangeUndoableEdit extends AbstractUndoableEdit {
      protected AbstractDocument.AbstractElement element;
      protected Style newStyle;
      protected AttributeSet oldStyle;

      public StyleChangeUndoableEdit(AbstractDocument.AbstractElement var1, Style var2) {
         this.element = var1;
         this.newStyle = var2;
         this.oldStyle = var1.getResolveParent();
      }

      public void redo() throws CannotRedoException {
         super.redo();
         this.element.setResolveParent(this.newStyle);
      }

      public void undo() throws CannotUndoException {
         super.undo();
         this.element.setResolveParent(this.oldStyle);
      }
   }

   public static class AttributeUndoableEdit extends AbstractUndoableEdit {
      protected AttributeSet newAttributes;
      protected AttributeSet copy;
      protected boolean isReplacing;
      protected Element element;

      public AttributeUndoableEdit(Element var1, AttributeSet var2, boolean var3) {
         this.element = var1;
         this.newAttributes = var2;
         this.isReplacing = var3;
         this.copy = var1.getAttributes().copyAttributes();
      }

      public void redo() throws CannotRedoException {
         super.redo();
         MutableAttributeSet var1 = (MutableAttributeSet)this.element.getAttributes();
         if (this.isReplacing) {
            var1.removeAttributes((AttributeSet)var1);
         }

         var1.addAttributes(this.newAttributes);
      }

      public void undo() throws CannotUndoException {
         super.undo();
         MutableAttributeSet var1 = (MutableAttributeSet)this.element.getAttributes();
         var1.removeAttributes((AttributeSet)var1);
         var1.addAttributes(this.copy);
      }
   }

   public class ElementBuffer implements Serializable {
      Element root;
      transient int pos;
      transient int offset;
      transient int length;
      transient int endOffset;
      transient Vector<DefaultStyledDocument.ElementBuffer.ElemChanges> changes;
      transient Stack<DefaultStyledDocument.ElementBuffer.ElemChanges> path;
      transient boolean insertOp;
      transient boolean recreateLeafs;
      transient DefaultStyledDocument.ElementBuffer.ElemChanges[] insertPath;
      transient boolean createdFracture;
      transient Element fracturedParent;
      transient Element fracturedChild;
      transient boolean offsetLastIndex;
      transient boolean offsetLastIndexOnReplace;

      public ElementBuffer(Element var2) {
         this.root = var2;
         this.changes = new Vector();
         this.path = new Stack();
      }

      public Element getRootElement() {
         return this.root;
      }

      public void insert(int var1, int var2, DefaultStyledDocument.ElementSpec[] var3, AbstractDocument.DefaultDocumentEvent var4) {
         if (var2 != 0) {
            this.insertOp = true;
            this.beginEdits(var1, var2);
            this.insertUpdate(var3);
            this.endEdits(var4);
            this.insertOp = false;
         }
      }

      void create(int var1, DefaultStyledDocument.ElementSpec[] var2, AbstractDocument.DefaultDocumentEvent var3) {
         this.insertOp = true;
         this.beginEdits(this.offset, var1);
         Element var4 = this.root;

         Element var6;
         for(int var5 = var4.getElementIndex(0); !var4.isLeaf(); var5 = var6.getElementIndex(0)) {
            var6 = var4.getElement(var5);
            this.push(var4, var5);
            var4 = var6;
         }

         DefaultStyledDocument.ElementBuffer.ElemChanges var12 = (DefaultStyledDocument.ElementBuffer.ElemChanges)this.path.peek();
         Element var7 = var12.parent.getElement(var12.index);
         var12.added.addElement(DefaultStyledDocument.this.createLeafElement(var12.parent, var7.getAttributes(), DefaultStyledDocument.this.getLength(), var7.getEndOffset()));
         var12.removed.addElement(var7);

         while(this.path.size() > 1) {
            this.pop();
         }

         int var8 = var2.length;
         AttributeSet var9 = null;
         if (var8 > 0 && var2[0].getType() == 1) {
            var9 = var2[0].getAttributes();
         }

         if (var9 == null) {
            var9 = SimpleAttributeSet.EMPTY;
         }

         MutableAttributeSet var10 = (MutableAttributeSet)this.root.getAttributes();
         var3.addEdit(new DefaultStyledDocument.AttributeUndoableEdit(this.root, var9, true));
         var10.removeAttributes((AttributeSet)var10);
         var10.addAttributes(var9);

         for(int var11 = 1; var11 < var8; ++var11) {
            this.insertElement(var2[var11]);
         }

         while(this.path.size() != 0) {
            this.pop();
         }

         this.endEdits(var3);
         this.insertOp = false;
      }

      public void remove(int var1, int var2, AbstractDocument.DefaultDocumentEvent var3) {
         this.beginEdits(var1, var2);
         this.removeUpdate();
         this.endEdits(var3);
      }

      public void change(int var1, int var2, AbstractDocument.DefaultDocumentEvent var3) {
         this.beginEdits(var1, var2);
         this.changeUpdate();
         this.endEdits(var3);
      }

      protected void insertUpdate(DefaultStyledDocument.ElementSpec[] var1) {
         Element var2 = this.root;

         Element var4;
         for(int var3 = var2.getElementIndex(this.offset); !var2.isLeaf(); var3 = var4.getElementIndex(this.offset)) {
            var4 = var2.getElement(var3);
            this.push(var2, var4.isLeaf() ? var3 : var3 + 1);
            var2 = var4;
         }

         this.insertPath = new DefaultStyledDocument.ElementBuffer.ElemChanges[this.path.size()];
         this.path.copyInto(this.insertPath);
         this.createdFracture = false;
         this.recreateLeafs = false;
         int var8;
         if (var1[0].getType() == 3) {
            this.insertFirstContent(var1);
            this.pos += var1[0].getLength();
            var8 = 1;
         } else {
            this.fractureDeepestLeaf(var1);
            var8 = 0;
         }

         for(int var5 = var1.length; var8 < var5; ++var8) {
            this.insertElement(var1[var8]);
         }

         if (!this.createdFracture) {
            this.fracture(-1);
         }

         while(this.path.size() != 0) {
            this.pop();
         }

         if (this.offsetLastIndex && this.offsetLastIndexOnReplace) {
            ++this.insertPath[this.insertPath.length - 1].index;
         }

         int var6;
         DefaultStyledDocument.ElementBuffer.ElemChanges var7;
         for(var6 = this.insertPath.length - 1; var6 >= 0; --var6) {
            var7 = this.insertPath[var6];
            if (var7.parent == this.fracturedParent) {
               var7.added.addElement(this.fracturedChild);
            }

            if ((var7.added.size() > 0 || var7.removed.size() > 0) && !this.changes.contains(var7)) {
               this.changes.addElement(var7);
            }
         }

         if (this.offset == 0 && this.fracturedParent != null && var1[0].getType() == 2) {
            for(var6 = 0; var6 < var1.length && var1[var6].getType() == 2; ++var6) {
            }

            var7 = this.insertPath[this.insertPath.length - var6 - 1];
            var7.removed.insertElementAt(var7.parent.getElement(--var7.index), 0);
         }

      }

      protected void removeUpdate() {
         this.removeElements(this.root, this.offset, this.offset + this.length);
      }

      protected void changeUpdate() {
         boolean var1 = this.split(this.offset, this.length);
         if (!var1) {
            while(true) {
               if (this.path.size() == 0) {
                  this.split(this.offset + this.length, 0);
                  break;
               }

               this.pop();
            }
         }

         while(this.path.size() != 0) {
            this.pop();
         }

      }

      boolean split(int var1, int var2) {
         boolean var3 = false;
         Element var4 = this.root;

         for(int var5 = var4.getElementIndex(var1); !var4.isLeaf(); var5 = var4.getElementIndex(var1)) {
            this.push(var4, var5);
            var4 = var4.getElement(var5);
         }

         DefaultStyledDocument.ElementBuffer.ElemChanges var6 = (DefaultStyledDocument.ElementBuffer.ElemChanges)this.path.peek();
         Element var7 = var6.parent.getElement(var6.index);
         if (var7.getStartOffset() < var1 && var1 < var7.getEndOffset()) {
            int var8 = var6.index;
            int var9 = var8;
            if (var1 + var2 < var6.parent.getEndOffset() && var2 != 0) {
               var9 = var6.parent.getElementIndex(var1 + var2);
               if (var9 == var8) {
                  var6.removed.addElement(var7);
                  var4 = DefaultStyledDocument.this.createLeafElement(var6.parent, var7.getAttributes(), var7.getStartOffset(), var1);
                  var6.added.addElement(var4);
                  var4 = DefaultStyledDocument.this.createLeafElement(var6.parent, var7.getAttributes(), var1, var1 + var2);
                  var6.added.addElement(var4);
                  var4 = DefaultStyledDocument.this.createLeafElement(var6.parent, var7.getAttributes(), var1 + var2, var7.getEndOffset());
                  var6.added.addElement(var4);
                  return true;
               }

               var7 = var6.parent.getElement(var9);
               if (var1 + var2 == var7.getStartOffset()) {
                  var9 = var8;
               }

               var3 = true;
            }

            this.pos = var1;
            var7 = var6.parent.getElement(var8);
            var6.removed.addElement(var7);
            var4 = DefaultStyledDocument.this.createLeafElement(var6.parent, var7.getAttributes(), var7.getStartOffset(), this.pos);
            var6.added.addElement(var4);
            var4 = DefaultStyledDocument.this.createLeafElement(var6.parent, var7.getAttributes(), this.pos, var7.getEndOffset());
            var6.added.addElement(var4);

            for(int var10 = var8 + 1; var10 < var9; ++var10) {
               var7 = var6.parent.getElement(var10);
               var6.removed.addElement(var7);
               var6.added.addElement(var7);
            }

            if (var9 != var8) {
               var7 = var6.parent.getElement(var9);
               this.pos = var1 + var2;
               var6.removed.addElement(var7);
               var4 = DefaultStyledDocument.this.createLeafElement(var6.parent, var7.getAttributes(), var7.getStartOffset(), this.pos);
               var6.added.addElement(var4);
               var4 = DefaultStyledDocument.this.createLeafElement(var6.parent, var7.getAttributes(), this.pos, var7.getEndOffset());
               var6.added.addElement(var4);
            }
         }

         return var3;
      }

      void endEdits(AbstractDocument.DefaultDocumentEvent var1) {
         int var2 = this.changes.size();

         for(int var3 = 0; var3 < var2; ++var3) {
            DefaultStyledDocument.ElementBuffer.ElemChanges var4 = (DefaultStyledDocument.ElementBuffer.ElemChanges)this.changes.elementAt(var3);
            Element[] var5 = new Element[var4.removed.size()];
            var4.removed.copyInto(var5);
            Element[] var6 = new Element[var4.added.size()];
            var4.added.copyInto(var6);
            int var7 = var4.index;
            ((AbstractDocument.BranchElement)var4.parent).replace(var7, var5.length, var6);
            AbstractDocument.ElementEdit var8 = new AbstractDocument.ElementEdit(var4.parent, var7, var5, var6);
            var1.addEdit(var8);
         }

         this.changes.removeAllElements();
         this.path.removeAllElements();
      }

      void beginEdits(int var1, int var2) {
         this.offset = var1;
         this.length = var2;
         this.endOffset = var1 + var2;
         this.pos = var1;
         if (this.changes == null) {
            this.changes = new Vector();
         } else {
            this.changes.removeAllElements();
         }

         if (this.path == null) {
            this.path = new Stack();
         } else {
            this.path.removeAllElements();
         }

         this.fracturedParent = null;
         this.fracturedChild = null;
         this.offsetLastIndex = this.offsetLastIndexOnReplace = false;
      }

      void push(Element var1, int var2, boolean var3) {
         DefaultStyledDocument.ElementBuffer.ElemChanges var4 = new DefaultStyledDocument.ElementBuffer.ElemChanges(var1, var2, var3);
         this.path.push(var4);
      }

      void push(Element var1, int var2) {
         this.push(var1, var2, false);
      }

      void pop() {
         DefaultStyledDocument.ElementBuffer.ElemChanges var1 = (DefaultStyledDocument.ElementBuffer.ElemChanges)this.path.peek();
         this.path.pop();
         if (var1.added.size() <= 0 && var1.removed.size() <= 0) {
            if (!this.path.isEmpty()) {
               Element var2 = var1.parent;
               if (var2.getElementCount() == 0) {
                  var1 = (DefaultStyledDocument.ElementBuffer.ElemChanges)this.path.peek();
                  var1.added.removeElement(var2);
               }
            }
         } else {
            this.changes.addElement(var1);
         }

      }

      void advance(int var1) {
         this.pos += var1;
      }

      void insertElement(DefaultStyledDocument.ElementSpec var1) {
         DefaultStyledDocument.ElementBuffer.ElemChanges var2 = (DefaultStyledDocument.ElementBuffer.ElemChanges)this.path.peek();
         Element var4;
         switch(var1.getType()) {
         case 1:
            switch(var1.getDirection()) {
            case 5:
               Element var6 = var2.parent.getElement(var2.index);
               if (var6.isLeaf()) {
                  if (var2.index + 1 >= var2.parent.getElementCount()) {
                     throw new StateInvariantError("Join next to leaf");
                  }

                  var6 = var2.parent.getElement(var2.index + 1);
               }

               this.push(var6, 0, true);
               return;
            case 7:
               if (!this.createdFracture) {
                  this.fracture(this.path.size() - 1);
               }

               if (!var2.isFracture) {
                  this.push(this.fracturedChild, 0, true);
               } else {
                  this.push(var2.parent.getElement(0), 0, true);
               }

               return;
            default:
               var4 = DefaultStyledDocument.this.createBranchElement(var2.parent, var1.getAttributes());
               var2.added.addElement(var4);
               this.push(var4, 0);
               return;
            }
         case 2:
            this.pop();
            break;
         case 3:
            int var3 = var1.getLength();
            if (var1.getDirection() != 5) {
               var4 = DefaultStyledDocument.this.createLeafElement(var2.parent, var1.getAttributes(), this.pos, this.pos + var3);
               var2.added.addElement(var4);
            } else {
               Element var7;
               if (var2.isFracture) {
                  var4 = var2.parent.getElement(0);
                  var7 = DefaultStyledDocument.this.createLeafElement(var2.parent, var4.getAttributes(), this.pos, var4.getEndOffset());
                  var2.added.addElement(var7);
                  var2.removed.addElement(var4);
               } else {
                  var4 = null;
                  if (this.insertPath != null) {
                     for(int var5 = this.insertPath.length - 1; var5 >= 0; --var5) {
                        if (this.insertPath[var5] == var2) {
                           if (var5 != this.insertPath.length - 1) {
                              var4 = var2.parent.getElement(var2.index);
                           }
                           break;
                        }
                     }
                  }

                  if (var4 == null) {
                     var4 = var2.parent.getElement(var2.index + 1);
                  }

                  var7 = DefaultStyledDocument.this.createLeafElement(var2.parent, var4.getAttributes(), this.pos, var4.getEndOffset());
                  var2.added.addElement(var7);
                  var2.removed.addElement(var4);
               }
            }

            this.pos += var3;
         }

      }

      boolean removeElements(Element var1, int var2, int var3) {
         if (!var1.isLeaf()) {
            int var4 = var1.getElementIndex(var2);
            int var5 = var1.getElementIndex(var3);
            this.push(var1, var4);
            DefaultStyledDocument.ElementBuffer.ElemChanges var6 = (DefaultStyledDocument.ElementBuffer.ElemChanges)this.path.peek();
            Element var7;
            if (var4 == var5) {
               var7 = var1.getElement(var4);
               if (var2 <= var7.getStartOffset() && var3 >= var7.getEndOffset()) {
                  var6.removed.addElement(var7);
               } else if (this.removeElements(var7, var2, var3)) {
                  var6.removed.addElement(var7);
               }
            } else {
               var7 = var1.getElement(var4);
               Element var8 = var1.getElement(var5);
               boolean var9 = var3 < var1.getEndOffset();
               int var10;
               if (var9 && this.canJoin(var7, var8)) {
                  for(var10 = var4; var10 <= var5; ++var10) {
                     var6.removed.addElement(var1.getElement(var10));
                  }

                  Element var13 = this.join(var1, var7, var8, var2, var3);
                  var6.added.addElement(var13);
               } else {
                  var10 = var4 + 1;
                  int var11 = var5 - 1;
                  if (var7.getStartOffset() == var2 || var4 == 0 && var7.getStartOffset() > var2 && var7.getEndOffset() <= var3) {
                     var7 = null;
                     var10 = var4;
                  }

                  if (!var9) {
                     var8 = null;
                     ++var11;
                  } else if (var8.getStartOffset() == var3) {
                     var8 = null;
                  }

                  if (var10 <= var11) {
                     var6.index = var10;
                  }

                  for(int var12 = var10; var12 <= var11; ++var12) {
                     var6.removed.addElement(var1.getElement(var12));
                  }

                  if (var7 != null && this.removeElements(var7, var2, var3)) {
                     var6.removed.insertElementAt(var7, 0);
                     var6.index = var4;
                  }

                  if (var8 != null && this.removeElements(var8, var2, var3)) {
                     var6.removed.addElement(var8);
                  }
               }
            }

            this.pop();
            if (var1.getElementCount() == var6.removed.size() - var6.added.size()) {
               return true;
            }
         }

         return false;
      }

      boolean canJoin(Element var1, Element var2) {
         if (var1 != null && var2 != null) {
            boolean var3 = var1.isLeaf();
            boolean var4 = var2.isLeaf();
            if (var3 != var4) {
               return false;
            } else if (var3) {
               return var1.getAttributes().isEqual(var2.getAttributes());
            } else {
               String var5 = var1.getName();
               String var6 = var2.getName();
               if (var5 != null) {
                  return var5.equals(var6);
               } else {
                  return var6 != null ? var6.equals(var5) : true;
               }
            }
         } else {
            return false;
         }
      }

      Element join(Element var1, Element var2, Element var3, int var4, int var5) {
         if (var2.isLeaf() && var3.isLeaf()) {
            return DefaultStyledDocument.this.createLeafElement(var1, var2.getAttributes(), var2.getStartOffset(), var3.getEndOffset());
         } else if (!var2.isLeaf() && !var3.isLeaf()) {
            Element var6 = DefaultStyledDocument.this.createBranchElement(var1, var2.getAttributes());
            int var7 = var2.getElementIndex(var4);
            int var8 = var3.getElementIndex(var5);
            Element var9 = var2.getElement(var7);
            if (var9.getStartOffset() >= var4) {
               var9 = null;
            }

            Element var10 = var3.getElement(var8);
            if (var10.getStartOffset() == var5) {
               var10 = null;
            }

            Vector var11 = new Vector();

            int var12;
            for(var12 = 0; var12 < var7; ++var12) {
               var11.addElement(this.clone(var6, var2.getElement(var12)));
            }

            if (this.canJoin(var9, var10)) {
               Element var14 = this.join(var6, var9, var10, var4, var5);
               var11.addElement(var14);
            } else {
               if (var9 != null) {
                  var11.addElement(this.cloneAsNecessary(var6, var9, var4, var5));
               }

               if (var10 != null) {
                  var11.addElement(this.cloneAsNecessary(var6, var10, var4, var5));
               }
            }

            var12 = var3.getElementCount();

            for(int var13 = var10 == null ? var8 : var8 + 1; var13 < var12; ++var13) {
               var11.addElement(this.clone(var6, var3.getElement(var13)));
            }

            Element[] var15 = new Element[var11.size()];
            var11.copyInto(var15);
            ((AbstractDocument.BranchElement)var6).replace(0, 0, var15);
            return var6;
         } else {
            throw new StateInvariantError("No support to join leaf element with non-leaf element");
         }
      }

      public Element clone(Element var1, Element var2) {
         if (var2.isLeaf()) {
            return DefaultStyledDocument.this.createLeafElement(var1, var2.getAttributes(), var2.getStartOffset(), var2.getEndOffset());
         } else {
            Element var3 = DefaultStyledDocument.this.createBranchElement(var1, var2.getAttributes());
            int var4 = var2.getElementCount();
            Element[] var5 = new Element[var4];

            for(int var6 = 0; var6 < var4; ++var6) {
               var5[var6] = this.clone(var3, var2.getElement(var6));
            }

            ((AbstractDocument.BranchElement)var3).replace(0, 0, var5);
            return var3;
         }
      }

      Element cloneAsNecessary(Element var1, Element var2, int var3, int var4) {
         if (var2.isLeaf()) {
            return DefaultStyledDocument.this.createLeafElement(var1, var2.getAttributes(), var2.getStartOffset(), var2.getEndOffset());
         } else {
            Element var5 = DefaultStyledDocument.this.createBranchElement(var1, var2.getAttributes());
            int var6 = var2.getElementCount();
            ArrayList var7 = new ArrayList(var6);

            for(int var8 = 0; var8 < var6; ++var8) {
               Element var9 = var2.getElement(var8);
               if (var9.getStartOffset() < var3 || var9.getEndOffset() > var4) {
                  var7.add(this.cloneAsNecessary(var5, var9, var3, var4));
               }
            }

            Element[] var10 = new Element[var7.size()];
            var10 = (Element[])var7.toArray(var10);
            ((AbstractDocument.BranchElement)var5).replace(0, 0, var10);
            return var5;
         }
      }

      void fracture(int var1) {
         int var2 = this.insertPath.length;
         int var3 = -1;
         boolean var4 = this.recreateLeafs;
         DefaultStyledDocument.ElementBuffer.ElemChanges var5 = this.insertPath[var2 - 1];
         boolean var6 = var5.index + 1 < var5.parent.getElementCount();
         int var7 = var4 ? var2 : -1;
         int var8 = var2 - 1;
         this.createdFracture = true;

         for(int var9 = var2 - 2; var9 >= 0; --var9) {
            DefaultStyledDocument.ElementBuffer.ElemChanges var10 = this.insertPath[var9];
            if (var10.added.size() > 0 || var9 == var1) {
               var3 = var9;
               if (!var4 && var6) {
                  var4 = true;
                  if (var7 == -1) {
                     var7 = var8 + 1;
                  }
               }
            }

            if (!var6 && var10.index < var10.parent.getElementCount()) {
               var6 = true;
               var8 = var9;
            }
         }

         if (var4) {
            if (var3 == -1) {
               var3 = var2 - 1;
            }

            this.fractureFrom(this.insertPath, var3, var7);
         }

      }

      void fractureFrom(DefaultStyledDocument.ElementBuffer.ElemChanges[] var1, int var2, int var3) {
         DefaultStyledDocument.ElementBuffer.ElemChanges var4 = var1[var2];
         int var7 = var1.length;
         Element var5;
         if (var2 + 1 == var7) {
            var5 = var4.parent.getElement(var4.index);
         } else {
            var5 = var4.parent.getElement(var4.index - 1);
         }

         Element var6;
         if (var5.isLeaf()) {
            var6 = DefaultStyledDocument.this.createLeafElement(var4.parent, var5.getAttributes(), Math.max(this.endOffset, var5.getStartOffset()), var5.getEndOffset());
         } else {
            var6 = DefaultStyledDocument.this.createBranchElement(var4.parent, var5.getAttributes());
         }

         this.fracturedParent = var4.parent;
         this.fracturedChild = var6;
         Element var8 = var6;

         while(true) {
            ++var2;
            if (var2 >= var3) {
               return;
            }

            boolean var9 = var2 + 1 == var3;
            boolean var10 = var2 + 1 == var7;
            var4 = var1[var2];
            if (var9) {
               if (!this.offsetLastIndex && var10) {
                  var5 = var4.parent.getElement(var4.index);
               } else {
                  var5 = null;
               }
            } else {
               var5 = var4.parent.getElement(var4.index - 1);
            }

            if (var5 != null) {
               if (var5.isLeaf()) {
                  var6 = DefaultStyledDocument.this.createLeafElement(var8, var5.getAttributes(), Math.max(this.endOffset, var5.getStartOffset()), var5.getEndOffset());
               } else {
                  var6 = DefaultStyledDocument.this.createBranchElement(var8, var5.getAttributes());
               }
            } else {
               var6 = null;
            }

            int var11 = var4.parent.getElementCount() - var4.index;
            byte var14 = 1;
            Element[] var12;
            int var13;
            if (var6 == null) {
               if (var10) {
                  --var11;
                  var13 = var4.index + 1;
               } else {
                  var13 = var4.index;
               }

               var14 = 0;
               var12 = new Element[var11];
            } else {
               if (!var9) {
                  ++var11;
                  var13 = var4.index;
               } else {
                  var13 = var4.index + 1;
               }

               var12 = new Element[var11];
               var12[0] = var6;
            }

            for(int var15 = var14; var15 < var11; ++var15) {
               Element var16 = var4.parent.getElement(var13++);
               var12[var15] = this.recreateFracturedElement(var8, var16);
               var4.removed.addElement(var16);
            }

            ((AbstractDocument.BranchElement)var8).replace(0, 0, var12);
            var8 = var6;
         }
      }

      Element recreateFracturedElement(Element var1, Element var2) {
         if (var2.isLeaf()) {
            return DefaultStyledDocument.this.createLeafElement(var1, var2.getAttributes(), Math.max(var2.getStartOffset(), this.endOffset), var2.getEndOffset());
         } else {
            Element var3 = DefaultStyledDocument.this.createBranchElement(var1, var2.getAttributes());
            int var4 = var2.getElementCount();
            Element[] var5 = new Element[var4];

            for(int var6 = 0; var6 < var4; ++var6) {
               var5[var6] = this.recreateFracturedElement(var3, var2.getElement(var6));
            }

            ((AbstractDocument.BranchElement)var3).replace(0, 0, var5);
            return var3;
         }
      }

      void fractureDeepestLeaf(DefaultStyledDocument.ElementSpec[] var1) {
         DefaultStyledDocument.ElementBuffer.ElemChanges var2 = (DefaultStyledDocument.ElementBuffer.ElemChanges)this.path.peek();
         Element var3 = var2.parent.getElement(var2.index);
         if (this.offset != 0) {
            Element var4 = DefaultStyledDocument.this.createLeafElement(var2.parent, var3.getAttributes(), var3.getStartOffset(), this.offset);
            var2.added.addElement(var4);
         }

         var2.removed.addElement(var3);
         if (var3.getEndOffset() != this.endOffset) {
            this.recreateLeafs = true;
         } else {
            this.offsetLastIndex = true;
         }

      }

      void insertFirstContent(DefaultStyledDocument.ElementSpec[] var1) {
         DefaultStyledDocument.ElementSpec var2 = var1[0];
         DefaultStyledDocument.ElementBuffer.ElemChanges var3 = (DefaultStyledDocument.ElementBuffer.ElemChanges)this.path.peek();
         Element var4 = var3.parent.getElement(var3.index);
         int var5 = this.offset + var2.getLength();
         boolean var6 = var1.length == 1;
         Element var7;
         switch(var2.getDirection()) {
         case 4:
            if (var4.getEndOffset() != var5 && !var6) {
               var7 = DefaultStyledDocument.this.createLeafElement(var3.parent, var4.getAttributes(), var4.getStartOffset(), var5);
               var3.added.addElement(var7);
               var3.removed.addElement(var4);
               if (var4.getEndOffset() != this.endOffset) {
                  this.recreateLeafs = true;
               } else {
                  this.offsetLastIndex = true;
               }
            } else {
               this.offsetLastIndex = true;
               this.offsetLastIndexOnReplace = true;
            }
            break;
         case 5:
            if (this.offset != 0) {
               var7 = DefaultStyledDocument.this.createLeafElement(var3.parent, var4.getAttributes(), var4.getStartOffset(), this.offset);
               var3.added.addElement(var7);
               Element var8 = var3.parent.getElement(var3.index + 1);
               if (var6) {
                  var7 = DefaultStyledDocument.this.createLeafElement(var3.parent, var8.getAttributes(), this.offset, var8.getEndOffset());
               } else {
                  var7 = DefaultStyledDocument.this.createLeafElement(var3.parent, var8.getAttributes(), this.offset, var5);
               }

               var3.added.addElement(var7);
               var3.removed.addElement(var4);
               var3.removed.addElement(var8);
            }
            break;
         default:
            if (var4.getStartOffset() != this.offset) {
               var7 = DefaultStyledDocument.this.createLeafElement(var3.parent, var4.getAttributes(), var4.getStartOffset(), this.offset);
               var3.added.addElement(var7);
            }

            var3.removed.addElement(var4);
            var7 = DefaultStyledDocument.this.createLeafElement(var3.parent, var2.getAttributes(), this.offset, var5);
            var3.added.addElement(var7);
            if (var4.getEndOffset() != this.endOffset) {
               this.recreateLeafs = true;
            } else {
               this.offsetLastIndex = true;
            }
         }

      }

      class ElemChanges {
         Element parent;
         int index;
         Vector<Element> added;
         Vector<Element> removed;
         boolean isFracture;

         ElemChanges(Element var2, int var3, boolean var4) {
            this.parent = var2;
            this.index = var3;
            this.isFracture = var4;
            this.added = new Vector();
            this.removed = new Vector();
         }

         public String toString() {
            return "added: " + this.added + "\nremoved: " + this.removed + "\n";
         }
      }
   }

   public static class ElementSpec {
      public static final short StartTagType = 1;
      public static final short EndTagType = 2;
      public static final short ContentType = 3;
      public static final short JoinPreviousDirection = 4;
      public static final short JoinNextDirection = 5;
      public static final short OriginateDirection = 6;
      public static final short JoinFractureDirection = 7;
      private AttributeSet attr;
      private int len;
      private short type;
      private short direction;
      private int offs;
      private char[] data;

      public ElementSpec(AttributeSet var1, short var2) {
         this(var1, var2, (char[])null, 0, 0);
      }

      public ElementSpec(AttributeSet var1, short var2, int var3) {
         this(var1, var2, (char[])null, 0, var3);
      }

      public ElementSpec(AttributeSet var1, short var2, char[] var3, int var4, int var5) {
         this.attr = var1;
         this.type = var2;
         this.data = var3;
         this.offs = var4;
         this.len = var5;
         this.direction = 6;
      }

      public void setType(short var1) {
         this.type = var1;
      }

      public short getType() {
         return this.type;
      }

      public void setDirection(short var1) {
         this.direction = var1;
      }

      public short getDirection() {
         return this.direction;
      }

      public AttributeSet getAttributes() {
         return this.attr;
      }

      public char[] getArray() {
         return this.data;
      }

      public int getOffset() {
         return this.offs;
      }

      public int getLength() {
         return this.len;
      }

      public String toString() {
         String var1 = "??";
         String var2 = "??";
         switch(this.type) {
         case 1:
            var1 = "StartTag";
            break;
         case 2:
            var1 = "EndTag";
            break;
         case 3:
            var1 = "Content";
         }

         switch(this.direction) {
         case 4:
            var2 = "JoinPrevious";
            break;
         case 5:
            var2 = "JoinNext";
            break;
         case 6:
            var2 = "Originate";
            break;
         case 7:
            var2 = "Fracture";
         }

         return var1 + ":" + var2 + ":" + this.getLength();
      }
   }

   protected class SectionElement extends AbstractDocument.BranchElement {
      public SectionElement() {
         super((Element)null, (AttributeSet)null);
      }

      public String getName() {
         return "section";
      }
   }
}
