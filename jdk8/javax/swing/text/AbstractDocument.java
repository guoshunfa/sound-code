package javax.swing.text;

import java.awt.font.TextAttribute;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.Bidi;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.tree.TreeNode;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import sun.font.BidiUtils;
import sun.swing.SwingUtilities2;

public abstract class AbstractDocument implements Document, Serializable {
   private transient int numReaders;
   private transient Thread currWriter;
   private transient int numWriters;
   private transient boolean notifyingListeners;
   private static Boolean defaultI18NProperty;
   private Dictionary<Object, Object> documentProperties;
   protected EventListenerList listenerList;
   private AbstractDocument.Content data;
   private AbstractDocument.AttributeContext context;
   private transient AbstractDocument.BranchElement bidiRoot;
   private DocumentFilter documentFilter;
   private transient DocumentFilter.FilterBypass filterBypass;
   private static final String BAD_LOCK_STATE = "document lock failure";
   protected static final String BAD_LOCATION = "document location failure";
   public static final String ParagraphElementName = "paragraph";
   public static final String ContentElementName = "content";
   public static final String SectionElementName = "section";
   public static final String BidiElementName = "bidi level";
   public static final String ElementNameAttribute = "$ename";
   static final String I18NProperty = "i18n";
   static final Object MultiByteProperty = "multiByte";
   static final String AsyncLoadPriority = "load priority";

   protected AbstractDocument(AbstractDocument.Content var1) {
      this(var1, StyleContext.getDefaultStyleContext());
   }

   protected AbstractDocument(AbstractDocument.Content var1, AbstractDocument.AttributeContext var2) {
      this.documentProperties = null;
      this.listenerList = new EventListenerList();
      this.data = var1;
      this.context = var2;
      this.bidiRoot = new AbstractDocument.BidiRootElement();
      if (defaultI18NProperty == null) {
         String var3 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
               return System.getProperty("i18n");
            }
         });
         if (var3 != null) {
            defaultI18NProperty = Boolean.valueOf(var3);
         } else {
            defaultI18NProperty = Boolean.FALSE;
         }
      }

      this.putProperty("i18n", defaultI18NProperty);
      this.writeLock();

      try {
         Element[] var7 = new Element[]{new AbstractDocument.BidiElement(this.bidiRoot, 0, 1, 0)};
         this.bidiRoot.replace(0, 0, var7);
      } finally {
         this.writeUnlock();
      }

   }

   public Dictionary<Object, Object> getDocumentProperties() {
      if (this.documentProperties == null) {
         this.documentProperties = new Hashtable(2);
      }

      return this.documentProperties;
   }

   public void setDocumentProperties(Dictionary<Object, Object> var1) {
      this.documentProperties = var1;
   }

   protected void fireInsertUpdate(DocumentEvent var1) {
      this.notifyingListeners = true;

      try {
         Object[] var2 = this.listenerList.getListenerList();

         for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
            if (var2[var3] == DocumentListener.class) {
               ((DocumentListener)var2[var3 + 1]).insertUpdate(var1);
            }
         }
      } finally {
         this.notifyingListeners = false;
      }

   }

   protected void fireChangedUpdate(DocumentEvent var1) {
      this.notifyingListeners = true;

      try {
         Object[] var2 = this.listenerList.getListenerList();

         for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
            if (var2[var3] == DocumentListener.class) {
               ((DocumentListener)var2[var3 + 1]).changedUpdate(var1);
            }
         }
      } finally {
         this.notifyingListeners = false;
      }

   }

   protected void fireRemoveUpdate(DocumentEvent var1) {
      this.notifyingListeners = true;

      try {
         Object[] var2 = this.listenerList.getListenerList();

         for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
            if (var2[var3] == DocumentListener.class) {
               ((DocumentListener)var2[var3 + 1]).removeUpdate(var1);
            }
         }
      } finally {
         this.notifyingListeners = false;
      }

   }

   protected void fireUndoableEditUpdate(UndoableEditEvent var1) {
      Object[] var2 = this.listenerList.getListenerList();

      for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
         if (var2[var3] == UndoableEditListener.class) {
            ((UndoableEditListener)var2[var3 + 1]).undoableEditHappened(var1);
         }
      }

   }

   public <T extends EventListener> T[] getListeners(Class<T> var1) {
      return this.listenerList.getListeners(var1);
   }

   public int getAsynchronousLoadPriority() {
      Integer var1 = (Integer)this.getProperty("load priority");
      return var1 != null ? var1 : -1;
   }

   public void setAsynchronousLoadPriority(int var1) {
      Integer var2 = var1 >= 0 ? var1 : null;
      this.putProperty("load priority", var2);
   }

   public void setDocumentFilter(DocumentFilter var1) {
      this.documentFilter = var1;
   }

   public DocumentFilter getDocumentFilter() {
      return this.documentFilter;
   }

   public void render(Runnable var1) {
      this.readLock();

      try {
         var1.run();
      } finally {
         this.readUnlock();
      }

   }

   public int getLength() {
      return this.data.length() - 1;
   }

   public void addDocumentListener(DocumentListener var1) {
      this.listenerList.add(DocumentListener.class, var1);
   }

   public void removeDocumentListener(DocumentListener var1) {
      this.listenerList.remove(DocumentListener.class, var1);
   }

   public DocumentListener[] getDocumentListeners() {
      return (DocumentListener[])this.listenerList.getListeners(DocumentListener.class);
   }

   public void addUndoableEditListener(UndoableEditListener var1) {
      this.listenerList.add(UndoableEditListener.class, var1);
   }

   public void removeUndoableEditListener(UndoableEditListener var1) {
      this.listenerList.remove(UndoableEditListener.class, var1);
   }

   public UndoableEditListener[] getUndoableEditListeners() {
      return (UndoableEditListener[])this.listenerList.getListeners(UndoableEditListener.class);
   }

   public final Object getProperty(Object var1) {
      return this.getDocumentProperties().get(var1);
   }

   public final void putProperty(Object var1, Object var2) {
      if (var2 != null) {
         this.getDocumentProperties().put(var1, var2);
      } else {
         this.getDocumentProperties().remove(var1);
      }

      if (var1 == TextAttribute.RUN_DIRECTION && Boolean.TRUE.equals(this.getProperty("i18n"))) {
         this.writeLock();

         try {
            AbstractDocument.DefaultDocumentEvent var3 = new AbstractDocument.DefaultDocumentEvent(0, this.getLength(), DocumentEvent.EventType.INSERT);
            this.updateBidi(var3);
         } finally {
            this.writeUnlock();
         }
      }

   }

   public void remove(int var1, int var2) throws BadLocationException {
      DocumentFilter var3 = this.getDocumentFilter();
      this.writeLock();

      try {
         if (var3 != null) {
            var3.remove(this.getFilterBypass(), var1, var2);
         } else {
            this.handleRemove(var1, var2);
         }
      } finally {
         this.writeUnlock();
      }

   }

   void handleRemove(int var1, int var2) throws BadLocationException {
      if (var2 > 0) {
         if (var1 < 0 || var1 + var2 > this.getLength()) {
            throw new BadLocationException("Invalid remove", this.getLength() + 1);
         }

         AbstractDocument.DefaultDocumentEvent var3 = new AbstractDocument.DefaultDocumentEvent(var1, var2, DocumentEvent.EventType.REMOVE);
         boolean var4 = Utilities.isComposedTextElement(this, var1);
         this.removeUpdate(var3);
         UndoableEdit var5 = this.data.remove(var1, var2);
         if (var5 != null) {
            var3.addEdit(var5);
         }

         this.postRemoveUpdate(var3);
         var3.end();
         this.fireRemoveUpdate(var3);
         if (var5 != null && !var4) {
            this.fireUndoableEditUpdate(new UndoableEditEvent(this, var3));
         }
      }

   }

   public void replace(int var1, int var2, String var3, AttributeSet var4) throws BadLocationException {
      if (var2 != 0 || var3 != null && var3.length() != 0) {
         DocumentFilter var5 = this.getDocumentFilter();
         this.writeLock();

         try {
            if (var5 != null) {
               var5.replace(this.getFilterBypass(), var1, var2, var3, var4);
            } else {
               if (var2 > 0) {
                  this.remove(var1, var2);
               }

               if (var3 != null && var3.length() > 0) {
                  this.insertString(var1, var3, var4);
               }
            }
         } finally {
            this.writeUnlock();
         }

      }
   }

   public void insertString(int var1, String var2, AttributeSet var3) throws BadLocationException {
      if (var2 != null && var2.length() != 0) {
         DocumentFilter var4 = this.getDocumentFilter();
         this.writeLock();

         try {
            if (var4 != null) {
               var4.insertString(this.getFilterBypass(), var1, var2, var3);
            } else {
               this.handleInsertString(var1, var2, var3);
            }
         } finally {
            this.writeUnlock();
         }

      }
   }

   private void handleInsertString(int var1, String var2, AttributeSet var3) throws BadLocationException {
      if (var2 != null && var2.length() != 0) {
         UndoableEdit var4 = this.data.insertString(var1, var2);
         AbstractDocument.DefaultDocumentEvent var5 = new AbstractDocument.DefaultDocumentEvent(var1, var2.length(), DocumentEvent.EventType.INSERT);
         if (var4 != null) {
            var5.addEdit(var4);
         }

         if (this.getProperty("i18n").equals(Boolean.FALSE)) {
            Object var6 = this.getProperty(TextAttribute.RUN_DIRECTION);
            if (var6 != null && var6.equals(TextAttribute.RUN_DIRECTION_RTL)) {
               this.putProperty("i18n", Boolean.TRUE);
            } else {
               char[] var7 = var2.toCharArray();
               if (SwingUtilities2.isComplexLayout(var7, 0, var7.length)) {
                  this.putProperty("i18n", Boolean.TRUE);
               }
            }
         }

         this.insertUpdate(var5, var3);
         var5.end();
         this.fireInsertUpdate(var5);
         if (var4 != null && (var3 == null || !var3.isDefined(StyleConstants.ComposedTextAttribute))) {
            this.fireUndoableEditUpdate(new UndoableEditEvent(this, var5));
         }

      }
   }

   public String getText(int var1, int var2) throws BadLocationException {
      if (var2 < 0) {
         throw new BadLocationException("Length must be positive", var2);
      } else {
         String var3 = this.data.getString(var1, var2);
         return var3;
      }
   }

   public void getText(int var1, int var2, Segment var3) throws BadLocationException {
      if (var2 < 0) {
         throw new BadLocationException("Length must be positive", var2);
      } else {
         this.data.getChars(var1, var2, var3);
      }
   }

   public synchronized Position createPosition(int var1) throws BadLocationException {
      return this.data.createPosition(var1);
   }

   public final Position getStartPosition() {
      Position var1;
      try {
         var1 = this.createPosition(0);
      } catch (BadLocationException var3) {
         var1 = null;
      }

      return var1;
   }

   public final Position getEndPosition() {
      Position var1;
      try {
         var1 = this.createPosition(this.data.length());
      } catch (BadLocationException var3) {
         var1 = null;
      }

      return var1;
   }

   public Element[] getRootElements() {
      Element[] var1 = new Element[]{this.getDefaultRootElement(), this.getBidiRootElement()};
      return var1;
   }

   public abstract Element getDefaultRootElement();

   private DocumentFilter.FilterBypass getFilterBypass() {
      if (this.filterBypass == null) {
         this.filterBypass = new AbstractDocument.DefaultFilterBypass();
      }

      return this.filterBypass;
   }

   public Element getBidiRootElement() {
      return this.bidiRoot;
   }

   static boolean isLeftToRight(Document var0, int var1, int var2) {
      if (Boolean.TRUE.equals(var0.getProperty("i18n")) && var0 instanceof AbstractDocument) {
         AbstractDocument var3 = (AbstractDocument)var0;
         Element var4 = var3.getBidiRootElement();
         int var5 = var4.getElementIndex(var1);
         Element var6 = var4.getElement(var5);
         if (var6.getEndOffset() >= var2) {
            AttributeSet var7 = var6.getAttributes();
            return StyleConstants.getBidiLevel(var7) % 2 == 0;
         }
      }

      return true;
   }

   public abstract Element getParagraphElement(int var1);

   protected final AbstractDocument.AttributeContext getAttributeContext() {
      return this.context;
   }

   protected void insertUpdate(AbstractDocument.DefaultDocumentEvent var1, AttributeSet var2) {
      if (this.getProperty("i18n").equals(Boolean.TRUE)) {
         this.updateBidi(var1);
      }

      if (var1.type == DocumentEvent.EventType.INSERT && var1.getLength() > 0 && !Boolean.TRUE.equals(this.getProperty(MultiByteProperty))) {
         Segment var3 = SegmentCache.getSharedSegment();

         try {
            this.getText(var1.getOffset(), var1.getLength(), var3);
            var3.first();

            do {
               if (var3.current() > 255) {
                  this.putProperty(MultiByteProperty, Boolean.TRUE);
                  break;
               }
            } while(var3.next() != '\uffff');
         } catch (BadLocationException var5) {
         }

         SegmentCache.releaseSharedSegment(var3);
      }

   }

   protected void removeUpdate(AbstractDocument.DefaultDocumentEvent var1) {
   }

   protected void postRemoveUpdate(AbstractDocument.DefaultDocumentEvent var1) {
      if (this.getProperty("i18n").equals(Boolean.TRUE)) {
         this.updateBidi(var1);
      }

   }

   void updateBidi(AbstractDocument.DefaultDocumentEvent var1) {
      int var2;
      int var3;
      if (var1.type != DocumentEvent.EventType.INSERT && var1.type != DocumentEvent.EventType.CHANGE) {
         if (var1.type != DocumentEvent.EventType.REMOVE) {
            throw new Error("Internal error: unknown event type.");
         }

         Element var17 = this.getParagraphElement(var1.getOffset());
         var2 = var17.getStartOffset();
         var3 = var17.getEndOffset();
      } else {
         int var4 = var1.getOffset();
         int var5 = var4 + var1.getLength();
         var2 = this.getParagraphElement(var4).getStartOffset();
         var3 = this.getParagraphElement(var5).getEndOffset();
      }

      byte[] var18 = this.calculateBidiLevels(var2, var3);
      Vector var19 = new Vector();
      int var6 = var2;
      int var7 = 0;
      int var8;
      if (var2 > 0) {
         var8 = this.bidiRoot.getElementIndex(var2 - 1);
         var7 = var8;
         Element var9 = this.bidiRoot.getElement(var8);
         int var10 = StyleConstants.getBidiLevel(var9.getAttributes());
         if (var10 == var18[0]) {
            var6 = var9.getStartOffset();
         } else if (var9.getEndOffset() > var2) {
            var19.addElement(new AbstractDocument.BidiElement(this.bidiRoot, var9.getStartOffset(), var2, var10));
         } else {
            var7 = var8 + 1;
         }
      }

      for(var8 = 0; var8 < var18.length && var18[var8] == var18[0]; ++var8) {
      }

      int var20 = var3;
      AbstractDocument.BidiElement var21 = null;
      int var11 = this.bidiRoot.getElementCount() - 1;
      int var12;
      int var14;
      if (var3 <= this.getLength()) {
         var12 = this.bidiRoot.getElementIndex(var3);
         var11 = var12;
         Element var13 = this.bidiRoot.getElement(var12);
         var14 = StyleConstants.getBidiLevel(var13.getAttributes());
         if (var14 == var18[var18.length - 1]) {
            var20 = var13.getEndOffset();
         } else if (var13.getStartOffset() < var3) {
            var21 = new AbstractDocument.BidiElement(this.bidiRoot, var3, var13.getEndOffset(), var14);
         } else {
            var11 = var12 - 1;
         }
      }

      for(var12 = var18.length; var12 > var8 && var18[var12 - 1] == var18[var18.length - 1]; --var12) {
      }

      int var22;
      if (var8 == var12 && var18[0] == var18[var18.length - 1]) {
         var19.addElement(new AbstractDocument.BidiElement(this.bidiRoot, var6, var20, var18[0]));
      } else {
         var19.addElement(new AbstractDocument.BidiElement(this.bidiRoot, var6, var8 + var2, var18[0]));

         for(var22 = var8; var22 < var12; var22 = var14) {
            for(var14 = var22; var14 < var18.length && var18[var14] == var18[var22]; ++var14) {
            }

            var19.addElement(new AbstractDocument.BidiElement(this.bidiRoot, var2 + var22, var2 + var14, var18[var22]));
         }

         var19.addElement(new AbstractDocument.BidiElement(this.bidiRoot, var12 + var2, var20, var18[var18.length - 1]));
      }

      if (var21 != null) {
         var19.addElement(var21);
      }

      var22 = 0;
      if (this.bidiRoot.getElementCount() > 0) {
         var22 = var11 - var7 + 1;
      }

      Element[] var23 = new Element[var22];

      for(int var15 = 0; var15 < var22; ++var15) {
         var23[var15] = this.bidiRoot.getElement(var7 + var15);
      }

      Element[] var24 = new Element[var19.size()];
      var19.copyInto(var24);
      AbstractDocument.ElementEdit var16 = new AbstractDocument.ElementEdit(this.bidiRoot, var7, var23, var24);
      var1.addEdit(var16);
      this.bidiRoot.replace(var7, var23.length, var24);
   }

   private byte[] calculateBidiLevels(int var1, int var2) {
      byte[] var3 = new byte[var2 - var1];
      int var4 = 0;
      Boolean var5 = null;
      Object var6 = this.getProperty(TextAttribute.RUN_DIRECTION);
      if (var6 instanceof Boolean) {
         var5 = (Boolean)var6;
      }

      int var7 = var1;

      while(var7 < var2) {
         Element var8 = this.getParagraphElement(var7);
         int var9 = var8.getStartOffset();
         int var10 = var8.getEndOffset();
         Boolean var11 = var5;
         var6 = var8.getAttributes().getAttribute(TextAttribute.RUN_DIRECTION);
         if (var6 instanceof Boolean) {
            var11 = (Boolean)var6;
         }

         Segment var12 = SegmentCache.getSharedSegment();

         try {
            this.getText(var9, var10 - var9, var12);
         } catch (BadLocationException var15) {
            throw new Error("Internal error: " + var15.toString());
         }

         byte var14 = -2;
         if (var11 != null) {
            if (TextAttribute.RUN_DIRECTION_LTR.equals(var11)) {
               var14 = 0;
            } else {
               var14 = 1;
            }
         }

         Bidi var13 = new Bidi(var12.array, var12.offset, (byte[])null, 0, var12.count, var14);
         BidiUtils.getLevels(var13, var3, var4);
         var4 += var13.getLength();
         var7 = var8.getEndOffset();
         SegmentCache.releaseSharedSegment(var12);
      }

      if (var4 != var3.length) {
         throw new Error("levelsEnd assertion failed.");
      } else {
         return var3;
      }
   }

   public void dump(PrintStream var1) {
      Element var2 = this.getDefaultRootElement();
      if (var2 instanceof AbstractDocument.AbstractElement) {
         ((AbstractDocument.AbstractElement)var2).dump(var1, 0);
      }

      this.bidiRoot.dump(var1, 0);
   }

   protected final AbstractDocument.Content getContent() {
      return this.data;
   }

   protected Element createLeafElement(Element var1, AttributeSet var2, int var3, int var4) {
      return new AbstractDocument.LeafElement(var1, var2, var3, var4);
   }

   protected Element createBranchElement(Element var1, AttributeSet var2) {
      return new AbstractDocument.BranchElement(var1, var2);
   }

   protected final synchronized Thread getCurrentWriter() {
      return this.currWriter;
   }

   protected final synchronized void writeLock() {
      try {
         while(this.numReaders > 0 || this.currWriter != null) {
            if (Thread.currentThread() == this.currWriter) {
               if (this.notifyingListeners) {
                  throw new IllegalStateException("Attempt to mutate in notification");
               }

               ++this.numWriters;
               return;
            }

            this.wait();
         }

         this.currWriter = Thread.currentThread();
         this.numWriters = 1;
      } catch (InterruptedException var2) {
         throw new Error("Interrupted attempt to acquire write lock");
      }
   }

   protected final synchronized void writeUnlock() {
      if (--this.numWriters <= 0) {
         this.numWriters = 0;
         this.currWriter = null;
         this.notifyAll();
      }

   }

   public final synchronized void readLock() {
      try {
         while(this.currWriter != null) {
            if (this.currWriter == Thread.currentThread()) {
               return;
            }

            this.wait();
         }

         ++this.numReaders;
      } catch (InterruptedException var2) {
         throw new Error("Interrupted attempt to acquire read lock");
      }
   }

   public final synchronized void readUnlock() {
      if (this.currWriter != Thread.currentThread()) {
         if (this.numReaders <= 0) {
            throw new StateInvariantError("document lock failure");
         } else {
            --this.numReaders;
            this.notify();
         }
      }
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      var1.defaultReadObject();
      this.listenerList = new EventListenerList();
      this.bidiRoot = new AbstractDocument.BidiRootElement();

      try {
         this.writeLock();
         Element[] var2 = new Element[]{new AbstractDocument.BidiElement(this.bidiRoot, 0, 1, 0)};
         this.bidiRoot.replace(0, 0, var2);
      } finally {
         this.writeUnlock();
      }

      var1.registerValidation(new ObjectInputValidation() {
         public void validateObject() {
            try {
               AbstractDocument.this.writeLock();
               AbstractDocument.DefaultDocumentEvent var1 = AbstractDocument.this.new DefaultDocumentEvent(0, AbstractDocument.this.getLength(), DocumentEvent.EventType.INSERT);
               AbstractDocument.this.updateBidi(var1);
            } finally {
               AbstractDocument.this.writeUnlock();
            }

         }
      }, 0);
   }

   private class DefaultFilterBypass extends DocumentFilter.FilterBypass {
      private DefaultFilterBypass() {
      }

      public Document getDocument() {
         return AbstractDocument.this;
      }

      public void remove(int var1, int var2) throws BadLocationException {
         AbstractDocument.this.handleRemove(var1, var2);
      }

      public void insertString(int var1, String var2, AttributeSet var3) throws BadLocationException {
         AbstractDocument.this.handleInsertString(var1, var2, var3);
      }

      public void replace(int var1, int var2, String var3, AttributeSet var4) throws BadLocationException {
         AbstractDocument.this.handleRemove(var1, var2);
         AbstractDocument.this.handleInsertString(var1, var3, var4);
      }

      // $FF: synthetic method
      DefaultFilterBypass(Object var2) {
         this();
      }
   }

   public static class ElementEdit extends AbstractUndoableEdit implements DocumentEvent.ElementChange {
      private Element e;
      private int index;
      private Element[] removed;
      private Element[] added;

      public ElementEdit(Element var1, int var2, Element[] var3, Element[] var4) {
         this.e = var1;
         this.index = var2;
         this.removed = var3;
         this.added = var4;
      }

      public Element getElement() {
         return this.e;
      }

      public int getIndex() {
         return this.index;
      }

      public Element[] getChildrenRemoved() {
         return this.removed;
      }

      public Element[] getChildrenAdded() {
         return this.added;
      }

      public void redo() throws CannotRedoException {
         super.redo();
         Element[] var1 = this.removed;
         this.removed = this.added;
         this.added = var1;
         ((AbstractDocument.BranchElement)this.e).replace(this.index, this.removed.length, this.added);
      }

      public void undo() throws CannotUndoException {
         super.undo();
         ((AbstractDocument.BranchElement)this.e).replace(this.index, this.added.length, this.removed);
         Element[] var1 = this.removed;
         this.removed = this.added;
         this.added = var1;
      }
   }

   class UndoRedoDocumentEvent implements DocumentEvent {
      private AbstractDocument.DefaultDocumentEvent src = null;
      private DocumentEvent.EventType type = null;

      public UndoRedoDocumentEvent(AbstractDocument.DefaultDocumentEvent var2, boolean var3) {
         this.src = var2;
         if (var3) {
            if (var2.getType().equals(DocumentEvent.EventType.INSERT)) {
               this.type = DocumentEvent.EventType.REMOVE;
            } else if (var2.getType().equals(DocumentEvent.EventType.REMOVE)) {
               this.type = DocumentEvent.EventType.INSERT;
            } else {
               this.type = var2.getType();
            }
         } else {
            this.type = var2.getType();
         }

      }

      public AbstractDocument.DefaultDocumentEvent getSource() {
         return this.src;
      }

      public int getOffset() {
         return this.src.getOffset();
      }

      public int getLength() {
         return this.src.getLength();
      }

      public Document getDocument() {
         return this.src.getDocument();
      }

      public DocumentEvent.EventType getType() {
         return this.type;
      }

      public DocumentEvent.ElementChange getChange(Element var1) {
         return this.src.getChange(var1);
      }
   }

   public class DefaultDocumentEvent extends CompoundEdit implements DocumentEvent {
      private int offset;
      private int length;
      private Hashtable<Element, DocumentEvent.ElementChange> changeLookup;
      private DocumentEvent.EventType type;

      public DefaultDocumentEvent(int var2, int var3, DocumentEvent.EventType var4) {
         this.offset = var2;
         this.length = var3;
         this.type = var4;
      }

      public String toString() {
         return this.edits.toString();
      }

      public boolean addEdit(UndoableEdit var1) {
         if (this.changeLookup == null && this.edits.size() > 10) {
            this.changeLookup = new Hashtable();
            int var2 = this.edits.size();

            for(int var3 = 0; var3 < var2; ++var3) {
               Object var4 = this.edits.elementAt(var3);
               if (var4 instanceof DocumentEvent.ElementChange) {
                  DocumentEvent.ElementChange var5 = (DocumentEvent.ElementChange)var4;
                  this.changeLookup.put(var5.getElement(), var5);
               }
            }
         }

         if (this.changeLookup != null && var1 instanceof DocumentEvent.ElementChange) {
            DocumentEvent.ElementChange var6 = (DocumentEvent.ElementChange)var1;
            this.changeLookup.put(var6.getElement(), var6);
         }

         return super.addEdit(var1);
      }

      public void redo() throws CannotRedoException {
         AbstractDocument.this.writeLock();

         try {
            super.redo();
            AbstractDocument.UndoRedoDocumentEvent var1 = AbstractDocument.this.new UndoRedoDocumentEvent(this, false);
            if (this.type == DocumentEvent.EventType.INSERT) {
               AbstractDocument.this.fireInsertUpdate(var1);
            } else if (this.type == DocumentEvent.EventType.REMOVE) {
               AbstractDocument.this.fireRemoveUpdate(var1);
            } else {
               AbstractDocument.this.fireChangedUpdate(var1);
            }
         } finally {
            AbstractDocument.this.writeUnlock();
         }

      }

      public void undo() throws CannotUndoException {
         AbstractDocument.this.writeLock();

         try {
            super.undo();
            AbstractDocument.UndoRedoDocumentEvent var1 = AbstractDocument.this.new UndoRedoDocumentEvent(this, true);
            if (this.type == DocumentEvent.EventType.REMOVE) {
               AbstractDocument.this.fireInsertUpdate(var1);
            } else if (this.type == DocumentEvent.EventType.INSERT) {
               AbstractDocument.this.fireRemoveUpdate(var1);
            } else {
               AbstractDocument.this.fireChangedUpdate(var1);
            }
         } finally {
            AbstractDocument.this.writeUnlock();
         }

      }

      public boolean isSignificant() {
         return true;
      }

      public String getPresentationName() {
         DocumentEvent.EventType var1 = this.getType();
         if (var1 == DocumentEvent.EventType.INSERT) {
            return UIManager.getString("AbstractDocument.additionText");
         } else {
            return var1 == DocumentEvent.EventType.REMOVE ? UIManager.getString("AbstractDocument.deletionText") : UIManager.getString("AbstractDocument.styleChangeText");
         }
      }

      public String getUndoPresentationName() {
         return UIManager.getString("AbstractDocument.undoText") + " " + this.getPresentationName();
      }

      public String getRedoPresentationName() {
         return UIManager.getString("AbstractDocument.redoText") + " " + this.getPresentationName();
      }

      public DocumentEvent.EventType getType() {
         return this.type;
      }

      public int getOffset() {
         return this.offset;
      }

      public int getLength() {
         return this.length;
      }

      public Document getDocument() {
         return AbstractDocument.this;
      }

      public DocumentEvent.ElementChange getChange(Element var1) {
         if (this.changeLookup != null) {
            return (DocumentEvent.ElementChange)this.changeLookup.get(var1);
         } else {
            int var2 = this.edits.size();

            for(int var3 = 0; var3 < var2; ++var3) {
               Object var4 = this.edits.elementAt(var3);
               if (var4 instanceof DocumentEvent.ElementChange) {
                  DocumentEvent.ElementChange var5 = (DocumentEvent.ElementChange)var4;
                  if (var1.equals(var5.getElement())) {
                     return var5;
                  }
               }
            }

            return null;
         }
      }
   }

   class BidiElement extends AbstractDocument.LeafElement {
      BidiElement(Element var2, int var3, int var4, int var5) {
         super(var2, new SimpleAttributeSet(), var3, var4);
         this.addAttribute(StyleConstants.BidiLevel, var5);
      }

      public String getName() {
         return "bidi level";
      }

      int getLevel() {
         Integer var1 = (Integer)this.getAttribute(StyleConstants.BidiLevel);
         return var1 != null ? var1 : 0;
      }

      boolean isLeftToRight() {
         return this.getLevel() % 2 == 0;
      }
   }

   class BidiRootElement extends AbstractDocument.BranchElement {
      BidiRootElement() {
         super((Element)null, (AttributeSet)null);
      }

      public String getName() {
         return "bidi root";
      }
   }

   public class LeafElement extends AbstractDocument.AbstractElement {
      private transient Position p0;
      private transient Position p1;

      public LeafElement(Element var2, AttributeSet var3, int var4, int var5) {
         super(var2, var3);

         try {
            this.p0 = AbstractDocument.this.createPosition(var4);
            this.p1 = AbstractDocument.this.createPosition(var5);
         } catch (BadLocationException var7) {
            this.p0 = null;
            this.p1 = null;
            throw new StateInvariantError("Can't create Position references");
         }
      }

      public String toString() {
         return "LeafElement(" + this.getName() + ") " + this.p0 + "," + this.p1 + "\n";
      }

      public int getStartOffset() {
         return this.p0.getOffset();
      }

      public int getEndOffset() {
         return this.p1.getOffset();
      }

      public String getName() {
         String var1 = super.getName();
         if (var1 == null) {
            var1 = "content";
         }

         return var1;
      }

      public int getElementIndex(int var1) {
         return -1;
      }

      public Element getElement(int var1) {
         return null;
      }

      public int getElementCount() {
         return 0;
      }

      public boolean isLeaf() {
         return true;
      }

      public boolean getAllowsChildren() {
         return false;
      }

      public Enumeration children() {
         return null;
      }

      private void writeObject(ObjectOutputStream var1) throws IOException {
         var1.defaultWriteObject();
         var1.writeInt(this.p0.getOffset());
         var1.writeInt(this.p1.getOffset());
      }

      private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
         var1.defaultReadObject();
         int var2 = var1.readInt();
         int var3 = var1.readInt();

         try {
            this.p0 = AbstractDocument.this.createPosition(var2);
            this.p1 = AbstractDocument.this.createPosition(var3);
         } catch (BadLocationException var5) {
            this.p0 = null;
            this.p1 = null;
            throw new IOException("Can't restore Position references");
         }
      }
   }

   public class BranchElement extends AbstractDocument.AbstractElement {
      private AbstractDocument.AbstractElement[] children = new AbstractDocument.AbstractElement[1];
      private int nchildren = 0;
      private int lastIndex = -1;

      public BranchElement(Element var2, AttributeSet var3) {
         super(var2, var3);
      }

      public Element positionToElement(int var1) {
         int var2 = this.getElementIndex(var1);
         AbstractDocument.AbstractElement var3 = this.children[var2];
         int var4 = var3.getStartOffset();
         int var5 = var3.getEndOffset();
         return var1 >= var4 && var1 < var5 ? var3 : null;
      }

      public void replace(int var1, int var2, Element[] var3) {
         int var4 = var3.length - var2;
         int var5 = var1 + var2;
         int var6 = this.nchildren - var5;
         int var7 = var5 + var4;
         if (this.nchildren + var4 >= this.children.length) {
            int var8 = Math.max(2 * this.children.length, this.nchildren + var4);
            AbstractDocument.AbstractElement[] var9 = new AbstractDocument.AbstractElement[var8];
            System.arraycopy(this.children, 0, var9, 0, var1);
            System.arraycopy(var3, 0, var9, var1, var3.length);
            System.arraycopy(this.children, var5, var9, var7, var6);
            this.children = var9;
         } else {
            System.arraycopy(this.children, var5, this.children, var7, var6);
            System.arraycopy(var3, 0, this.children, var1, var3.length);
         }

         this.nchildren += var4;
      }

      public String toString() {
         return "BranchElement(" + this.getName() + ") " + this.getStartOffset() + "," + this.getEndOffset() + "\n";
      }

      public String getName() {
         String var1 = super.getName();
         if (var1 == null) {
            var1 = "paragraph";
         }

         return var1;
      }

      public int getStartOffset() {
         return this.children[0].getStartOffset();
      }

      public int getEndOffset() {
         AbstractDocument.AbstractElement var1 = this.nchildren > 0 ? this.children[this.nchildren - 1] : this.children[0];
         return var1.getEndOffset();
      }

      public Element getElement(int var1) {
         return var1 < this.nchildren ? this.children[var1] : null;
      }

      public int getElementCount() {
         return this.nchildren;
      }

      public int getElementIndex(int var1) {
         int var3 = 0;
         int var4 = this.nchildren - 1;
         int var5 = 0;
         int var6 = this.getStartOffset();
         if (this.nchildren == 0) {
            return 0;
         } else if (var1 >= this.getEndOffset()) {
            return this.nchildren - 1;
         } else {
            int var7;
            AbstractDocument.AbstractElement var8;
            if (this.lastIndex >= var3 && this.lastIndex <= var4) {
               var8 = this.children[this.lastIndex];
               var6 = var8.getStartOffset();
               var7 = var8.getEndOffset();
               if (var1 >= var6 && var1 < var7) {
                  return this.lastIndex;
               }

               if (var1 < var6) {
                  var4 = this.lastIndex;
               } else {
                  var3 = this.lastIndex;
               }
            }

            while(var3 <= var4) {
               var5 = var3 + (var4 - var3) / 2;
               var8 = this.children[var5];
               var6 = var8.getStartOffset();
               var7 = var8.getEndOffset();
               if (var1 >= var6 && var1 < var7) {
                  this.lastIndex = var5;
                  return var5;
               }

               if (var1 < var6) {
                  var4 = var5 - 1;
               } else {
                  var3 = var5 + 1;
               }
            }

            int var2;
            if (var1 < var6) {
               var2 = var5;
            } else {
               var2 = var5 + 1;
            }

            this.lastIndex = var2;
            return var2;
         }
      }

      public boolean isLeaf() {
         return false;
      }

      public boolean getAllowsChildren() {
         return true;
      }

      public Enumeration children() {
         if (this.nchildren == 0) {
            return null;
         } else {
            Vector var1 = new Vector(this.nchildren);

            for(int var2 = 0; var2 < this.nchildren; ++var2) {
               var1.addElement(this.children[var2]);
            }

            return var1.elements();
         }
      }
   }

   public abstract class AbstractElement implements Element, MutableAttributeSet, Serializable, TreeNode {
      private Element parent;
      private transient AttributeSet attributes;

      public AbstractElement(Element var2, AttributeSet var3) {
         this.parent = var2;
         this.attributes = AbstractDocument.this.getAttributeContext().getEmptySet();
         if (var3 != null) {
            this.addAttributes(var3);
         }

      }

      private final void indent(PrintWriter var1, int var2) {
         for(int var3 = 0; var3 < var2; ++var3) {
            var1.print("  ");
         }

      }

      public void dump(PrintStream var1, int var2) {
         PrintWriter var3;
         try {
            var3 = new PrintWriter(new OutputStreamWriter(var1, "JavaEsc"), true);
         } catch (UnsupportedEncodingException var8) {
            var3 = new PrintWriter(var1, true);
         }

         this.indent(var3, var2);
         if (this.getName() == null) {
            var3.print("<??");
         } else {
            var3.print("<" + this.getName());
         }

         if (this.getAttributeCount() > 0) {
            var3.println("");
            Enumeration var4 = this.attributes.getAttributeNames();

            while(var4.hasMoreElements()) {
               Object var5 = var4.nextElement();
               this.indent(var3, var2 + 1);
               var3.println(var5 + "=" + this.getAttribute(var5));
            }

            this.indent(var3, var2);
         }

         var3.println(">");
         if (this.isLeaf()) {
            this.indent(var3, var2 + 1);
            var3.print("[" + this.getStartOffset() + "," + this.getEndOffset() + "]");
            AbstractDocument.Content var9 = AbstractDocument.this.getContent();

            try {
               String var11 = var9.getString(this.getStartOffset(), this.getEndOffset() - this.getStartOffset());
               if (var11.length() > 40) {
                  var11 = var11.substring(0, 40) + "...";
               }

               var3.println("[" + var11 + "]");
            } catch (BadLocationException var7) {
            }
         } else {
            int var10 = this.getElementCount();

            for(int var12 = 0; var12 < var10; ++var12) {
               AbstractDocument.AbstractElement var6 = (AbstractDocument.AbstractElement)this.getElement(var12);
               var6.dump(var1, var2 + 1);
            }
         }

      }

      public int getAttributeCount() {
         return this.attributes.getAttributeCount();
      }

      public boolean isDefined(Object var1) {
         return this.attributes.isDefined(var1);
      }

      public boolean isEqual(AttributeSet var1) {
         return this.attributes.isEqual(var1);
      }

      public AttributeSet copyAttributes() {
         return this.attributes.copyAttributes();
      }

      public Object getAttribute(Object var1) {
         Object var2 = this.attributes.getAttribute(var1);
         if (var2 == null) {
            AttributeSet var3 = this.parent != null ? this.parent.getAttributes() : null;
            if (var3 != null) {
               var2 = var3.getAttribute(var1);
            }
         }

         return var2;
      }

      public Enumeration<?> getAttributeNames() {
         return this.attributes.getAttributeNames();
      }

      public boolean containsAttribute(Object var1, Object var2) {
         return this.attributes.containsAttribute(var1, var2);
      }

      public boolean containsAttributes(AttributeSet var1) {
         return this.attributes.containsAttributes(var1);
      }

      public AttributeSet getResolveParent() {
         AttributeSet var1 = this.attributes.getResolveParent();
         if (var1 == null && this.parent != null) {
            var1 = this.parent.getAttributes();
         }

         return var1;
      }

      public void addAttribute(Object var1, Object var2) {
         this.checkForIllegalCast();
         AbstractDocument.AttributeContext var3 = AbstractDocument.this.getAttributeContext();
         this.attributes = var3.addAttribute(this.attributes, var1, var2);
      }

      public void addAttributes(AttributeSet var1) {
         this.checkForIllegalCast();
         AbstractDocument.AttributeContext var2 = AbstractDocument.this.getAttributeContext();
         this.attributes = var2.addAttributes(this.attributes, var1);
      }

      public void removeAttribute(Object var1) {
         this.checkForIllegalCast();
         AbstractDocument.AttributeContext var2 = AbstractDocument.this.getAttributeContext();
         this.attributes = var2.removeAttribute(this.attributes, var1);
      }

      public void removeAttributes(Enumeration<?> var1) {
         this.checkForIllegalCast();
         AbstractDocument.AttributeContext var2 = AbstractDocument.this.getAttributeContext();
         this.attributes = var2.removeAttributes(this.attributes, var1);
      }

      public void removeAttributes(AttributeSet var1) {
         this.checkForIllegalCast();
         AbstractDocument.AttributeContext var2 = AbstractDocument.this.getAttributeContext();
         if (var1 == this) {
            this.attributes = var2.getEmptySet();
         } else {
            this.attributes = var2.removeAttributes(this.attributes, var1);
         }

      }

      public void setResolveParent(AttributeSet var1) {
         this.checkForIllegalCast();
         AbstractDocument.AttributeContext var2 = AbstractDocument.this.getAttributeContext();
         if (var1 != null) {
            this.attributes = var2.addAttribute(this.attributes, StyleConstants.ResolveAttribute, var1);
         } else {
            this.attributes = var2.removeAttribute(this.attributes, StyleConstants.ResolveAttribute);
         }

      }

      private final void checkForIllegalCast() {
         Thread var1 = AbstractDocument.this.getCurrentWriter();
         if (var1 == null || var1 != Thread.currentThread()) {
            throw new StateInvariantError("Illegal cast to MutableAttributeSet");
         }
      }

      public Document getDocument() {
         return AbstractDocument.this;
      }

      public Element getParentElement() {
         return this.parent;
      }

      public AttributeSet getAttributes() {
         return this;
      }

      public String getName() {
         return this.attributes.isDefined("$ename") ? (String)this.attributes.getAttribute("$ename") : null;
      }

      public abstract int getStartOffset();

      public abstract int getEndOffset();

      public abstract Element getElement(int var1);

      public abstract int getElementCount();

      public abstract int getElementIndex(int var1);

      public abstract boolean isLeaf();

      public TreeNode getChildAt(int var1) {
         return (TreeNode)this.getElement(var1);
      }

      public int getChildCount() {
         return this.getElementCount();
      }

      public TreeNode getParent() {
         return (TreeNode)this.getParentElement();
      }

      public int getIndex(TreeNode var1) {
         for(int var2 = this.getChildCount() - 1; var2 >= 0; --var2) {
            if (this.getChildAt(var2) == var1) {
               return var2;
            }
         }

         return -1;
      }

      public abstract boolean getAllowsChildren();

      public abstract Enumeration children();

      private void writeObject(ObjectOutputStream var1) throws IOException {
         var1.defaultWriteObject();
         StyleContext.writeAttributeSet(var1, this.attributes);
      }

      private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
         var1.defaultReadObject();
         SimpleAttributeSet var2 = new SimpleAttributeSet();
         StyleContext.readAttributeSet(var1, var2);
         AbstractDocument.AttributeContext var3 = AbstractDocument.this.getAttributeContext();
         this.attributes = var3.addAttributes(SimpleAttributeSet.EMPTY, var2);
      }
   }

   public interface AttributeContext {
      AttributeSet addAttribute(AttributeSet var1, Object var2, Object var3);

      AttributeSet addAttributes(AttributeSet var1, AttributeSet var2);

      AttributeSet removeAttribute(AttributeSet var1, Object var2);

      AttributeSet removeAttributes(AttributeSet var1, Enumeration<?> var2);

      AttributeSet removeAttributes(AttributeSet var1, AttributeSet var2);

      AttributeSet getEmptySet();

      void reclaim(AttributeSet var1);
   }

   public interface Content {
      Position createPosition(int var1) throws BadLocationException;

      int length();

      UndoableEdit insertString(int var1, String var2) throws BadLocationException;

      UndoableEdit remove(int var1, int var2) throws BadLocationException;

      String getString(int var1, int var2) throws BadLocationException;

      void getChars(int var1, int var2, Segment var3) throws BadLocationException;
   }
}
