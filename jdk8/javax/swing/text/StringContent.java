package javax.swing.text;

import java.io.Serializable;
import java.util.Vector;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public final class StringContent implements AbstractDocument.Content, Serializable {
   private static final char[] empty = new char[0];
   private char[] data;
   private int count;
   transient Vector<StringContent.PosRec> marks;

   public StringContent() {
      this(10);
   }

   public StringContent(int var1) {
      if (var1 < 1) {
         var1 = 1;
      }

      this.data = new char[var1];
      this.data[0] = '\n';
      this.count = 1;
   }

   public int length() {
      return this.count;
   }

   public UndoableEdit insertString(int var1, String var2) throws BadLocationException {
      if (var1 < this.count && var1 >= 0) {
         char[] var3 = var2.toCharArray();
         this.replace(var1, 0, var3, 0, var3.length);
         if (this.marks != null) {
            this.updateMarksForInsert(var1, var2.length());
         }

         return new StringContent.InsertUndo(var1, var2.length());
      } else {
         throw new BadLocationException("Invalid location", this.count);
      }
   }

   public UndoableEdit remove(int var1, int var2) throws BadLocationException {
      if (var1 + var2 >= this.count) {
         throw new BadLocationException("Invalid range", this.count);
      } else {
         String var3 = this.getString(var1, var2);
         StringContent.RemoveUndo var4 = new StringContent.RemoveUndo(var1, var3);
         this.replace(var1, var2, empty, 0, 0);
         if (this.marks != null) {
            this.updateMarksForRemove(var1, var2);
         }

         return var4;
      }
   }

   public String getString(int var1, int var2) throws BadLocationException {
      if (var1 + var2 > this.count) {
         throw new BadLocationException("Invalid range", this.count);
      } else {
         return new String(this.data, var1, var2);
      }
   }

   public void getChars(int var1, int var2, Segment var3) throws BadLocationException {
      if (var1 + var2 > this.count) {
         throw new BadLocationException("Invalid location", this.count);
      } else {
         var3.array = this.data;
         var3.offset = var1;
         var3.count = var2;
      }
   }

   public Position createPosition(int var1) throws BadLocationException {
      if (this.marks == null) {
         this.marks = new Vector();
      }

      return new StringContent.StickyPosition(var1);
   }

   void replace(int var1, int var2, char[] var3, int var4, int var5) {
      int var6 = var5 - var2;
      int var7 = var1 + var2;
      int var8 = this.count - var7;
      int var9 = var7 + var6;
      if (this.count + var6 >= this.data.length) {
         int var10 = Math.max(2 * this.data.length, this.count + var6);
         char[] var11 = new char[var10];
         System.arraycopy(this.data, 0, var11, 0, var1);
         System.arraycopy(var3, var4, var11, var1, var5);
         System.arraycopy(this.data, var7, var11, var9, var8);
         this.data = var11;
      } else {
         System.arraycopy(this.data, var7, this.data, var9, var8);
         System.arraycopy(var3, var4, this.data, var1, var5);
      }

      this.count += var6;
   }

   void resize(int var1) {
      char[] var2 = new char[var1];
      System.arraycopy(this.data, 0, var2, 0, Math.min(var1, this.count));
      this.data = var2;
   }

   synchronized void updateMarksForInsert(int var1, int var2) {
      if (var1 == 0) {
         var1 = 1;
      }

      int var3 = this.marks.size();

      for(int var4 = 0; var4 < var3; ++var4) {
         StringContent.PosRec var5 = (StringContent.PosRec)this.marks.elementAt(var4);
         if (var5.unused) {
            this.marks.removeElementAt(var4);
            --var4;
            --var3;
         } else if (var5.offset >= var1) {
            var5.offset += var2;
         }
      }

   }

   synchronized void updateMarksForRemove(int var1, int var2) {
      int var3 = this.marks.size();

      for(int var4 = 0; var4 < var3; ++var4) {
         StringContent.PosRec var5 = (StringContent.PosRec)this.marks.elementAt(var4);
         if (var5.unused) {
            this.marks.removeElementAt(var4);
            --var4;
            --var3;
         } else if (var5.offset >= var1 + var2) {
            var5.offset -= var2;
         } else if (var5.offset >= var1) {
            var5.offset = var1;
         }
      }

   }

   protected Vector getPositionsInRange(Vector var1, int var2, int var3) {
      int var4 = this.marks.size();
      int var5 = var2 + var3;
      Vector var6 = var1 == null ? new Vector() : var1;

      for(int var7 = 0; var7 < var4; ++var7) {
         StringContent.PosRec var8 = (StringContent.PosRec)this.marks.elementAt(var7);
         if (var8.unused) {
            this.marks.removeElementAt(var7);
            --var7;
            --var4;
         } else if (var8.offset >= var2 && var8.offset <= var5) {
            var6.addElement(new StringContent.UndoPosRef(var8));
         }
      }

      return var6;
   }

   protected void updateUndoPositions(Vector var1) {
      for(int var2 = var1.size() - 1; var2 >= 0; --var2) {
         StringContent.UndoPosRef var3 = (StringContent.UndoPosRef)var1.elementAt(var2);
         if (var3.rec.unused) {
            var1.removeElementAt(var2);
         } else {
            var3.resetLocation();
         }
      }

   }

   class RemoveUndo extends AbstractUndoableEdit {
      protected int offset;
      protected int length;
      protected String string;
      protected Vector posRefs;

      protected RemoveUndo(int var2, String var3) {
         this.offset = var2;
         this.string = var3;
         this.length = var3.length();
         if (StringContent.this.marks != null) {
            this.posRefs = StringContent.this.getPositionsInRange((Vector)null, var2, this.length);
         }

      }

      public void undo() throws CannotUndoException {
         super.undo();

         try {
            synchronized(StringContent.this) {
               StringContent.this.insertString(this.offset, this.string);
               if (this.posRefs != null) {
                  StringContent.this.updateUndoPositions(this.posRefs);
                  this.posRefs = null;
               }

               this.string = null;
            }
         } catch (BadLocationException var4) {
            throw new CannotUndoException();
         }
      }

      public void redo() throws CannotRedoException {
         super.redo();

         try {
            synchronized(StringContent.this) {
               this.string = StringContent.this.getString(this.offset, this.length);
               if (StringContent.this.marks != null) {
                  this.posRefs = StringContent.this.getPositionsInRange((Vector)null, this.offset, this.length);
               }

               StringContent.this.remove(this.offset, this.length);
            }
         } catch (BadLocationException var4) {
            throw new CannotRedoException();
         }
      }
   }

   class InsertUndo extends AbstractUndoableEdit {
      protected int offset;
      protected int length;
      protected String string;
      protected Vector posRefs;

      protected InsertUndo(int var2, int var3) {
         this.offset = var2;
         this.length = var3;
      }

      public void undo() throws CannotUndoException {
         super.undo();

         try {
            synchronized(StringContent.this) {
               if (StringContent.this.marks != null) {
                  this.posRefs = StringContent.this.getPositionsInRange((Vector)null, this.offset, this.length);
               }

               this.string = StringContent.this.getString(this.offset, this.length);
               StringContent.this.remove(this.offset, this.length);
            }
         } catch (BadLocationException var4) {
            throw new CannotUndoException();
         }
      }

      public void redo() throws CannotRedoException {
         super.redo();

         try {
            synchronized(StringContent.this) {
               StringContent.this.insertString(this.offset, this.string);
               this.string = null;
               if (this.posRefs != null) {
                  StringContent.this.updateUndoPositions(this.posRefs);
                  this.posRefs = null;
               }

            }
         } catch (BadLocationException var4) {
            throw new CannotRedoException();
         }
      }
   }

   final class UndoPosRef {
      protected int undoLocation;
      protected StringContent.PosRec rec;

      UndoPosRef(StringContent.PosRec var2) {
         this.rec = var2;
         this.undoLocation = var2.offset;
      }

      protected void resetLocation() {
         this.rec.offset = this.undoLocation;
      }
   }

   final class StickyPosition implements Position {
      StringContent.PosRec rec;

      StickyPosition(int var2) {
         this.rec = StringContent.this.new PosRec(var2);
         StringContent.this.marks.addElement(this.rec);
      }

      public int getOffset() {
         return this.rec.offset;
      }

      protected void finalize() throws Throwable {
         this.rec.unused = true;
      }

      public String toString() {
         return Integer.toString(this.getOffset());
      }
   }

   final class PosRec {
      int offset;
      boolean unused;

      PosRec(int var2) {
         this.offset = var2;
      }
   }
}
