package javax.swing.text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Vector;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class GapContent extends GapVector implements AbstractDocument.Content, Serializable {
   private static final char[] empty = new char[0];
   private transient GapContent.MarkVector marks;
   private transient GapContent.MarkData search;
   private transient int unusedMarks;
   private transient ReferenceQueue<GapContent.StickyPosition> queue;
   static final int GROWTH_SIZE = 524288;

   public GapContent() {
      this(10);
   }

   public GapContent(int var1) {
      super(Math.max(var1, 2));
      this.unusedMarks = 0;
      char[] var2 = new char[]{'\n'};
      this.replace(0, 0, var2, var2.length);
      this.marks = new GapContent.MarkVector();
      this.search = new GapContent.MarkData(0);
      this.queue = new ReferenceQueue();
   }

   protected Object allocateArray(int var1) {
      return new char[var1];
   }

   protected int getArrayLength() {
      char[] var1 = (char[])((char[])this.getArray());
      return var1.length;
   }

   public int length() {
      int var1 = this.getArrayLength() - (this.getGapEnd() - this.getGapStart());
      return var1;
   }

   public UndoableEdit insertString(int var1, String var2) throws BadLocationException {
      if (var1 <= this.length() && var1 >= 0) {
         char[] var3 = var2.toCharArray();
         this.replace(var1, 0, var3, var3.length);
         return new GapContent.InsertUndo(var1, var2.length());
      } else {
         throw new BadLocationException("Invalid insert", this.length());
      }
   }

   public UndoableEdit remove(int var1, int var2) throws BadLocationException {
      if (var1 + var2 >= this.length()) {
         throw new BadLocationException("Invalid remove", this.length() + 1);
      } else {
         String var3 = this.getString(var1, var2);
         GapContent.RemoveUndo var4 = new GapContent.RemoveUndo(var1, var3);
         this.replace(var1, var2, empty, 0);
         return var4;
      }
   }

   public String getString(int var1, int var2) throws BadLocationException {
      Segment var3 = new Segment();
      this.getChars(var1, var2, var3);
      return new String(var3.array, var3.offset, var3.count);
   }

   public void getChars(int var1, int var2, Segment var3) throws BadLocationException {
      int var4 = var1 + var2;
      if (var1 >= 0 && var4 >= 0) {
         if (var4 <= this.length() && var1 <= this.length()) {
            int var5 = this.getGapStart();
            int var6 = this.getGapEnd();
            char[] var7 = (char[])((char[])this.getArray());
            if (var1 + var2 <= var5) {
               var3.array = var7;
               var3.offset = var1;
            } else if (var1 >= var5) {
               var3.array = var7;
               var3.offset = var6 + var1 - var5;
            } else {
               int var8 = var5 - var1;
               if (var3.isPartialReturn()) {
                  var3.array = var7;
                  var3.offset = var1;
                  var3.count = var8;
                  return;
               }

               var3.array = new char[var2];
               var3.offset = 0;
               System.arraycopy(var7, var1, var3.array, 0, var8);
               System.arraycopy(var7, var6, var3.array, var8, var2 - var8);
            }

            var3.count = var2;
         } else {
            throw new BadLocationException("Invalid location", this.length() + 1);
         }
      } else {
         throw new BadLocationException("Invalid location", -1);
      }
   }

   public Position createPosition(int var1) throws BadLocationException {
      while(this.queue.poll() != null) {
         ++this.unusedMarks;
      }

      if (this.unusedMarks > Math.max(5, this.marks.size() / 10)) {
         this.removeUnusedMarks();
      }

      int var2 = this.getGapStart();
      int var3 = this.getGapEnd();
      int var4 = var1 < var2 ? var1 : var1 + (var3 - var2);
      this.search.index = var4;
      int var5 = this.findSortIndex(this.search);
      GapContent.MarkData var6;
      GapContent.StickyPosition var7;
      if (var5 >= this.marks.size() || (var6 = this.marks.elementAt(var5)).index != var4 || (var7 = var6.getPosition()) == null) {
         var7 = new GapContent.StickyPosition();
         var6 = new GapContent.MarkData(var4, var7, this.queue);
         var7.setMark(var6);
         this.marks.insertElementAt(var6, var5);
      }

      return var7;
   }

   protected void shiftEnd(int var1) {
      int var2 = this.getGapEnd();
      super.shiftEnd(var1);
      int var3 = this.getGapEnd() - var2;
      int var4 = this.findMarkAdjustIndex(var2);
      int var5 = this.marks.size();

      for(int var6 = var4; var6 < var5; ++var6) {
         GapContent.MarkData var7 = this.marks.elementAt(var6);
         var7.index += var3;
      }

   }

   int getNewArraySize(int var1) {
      return var1 < 524288 ? super.getNewArraySize(var1) : var1 + 524288;
   }

   protected void shiftGap(int var1) {
      int var2 = this.getGapStart();
      int var3 = var1 - var2;
      int var4 = this.getGapEnd();
      int var5 = var4 + var3;
      int var6 = var4 - var2;
      super.shiftGap(var1);
      int var7;
      int var8;
      int var9;
      GapContent.MarkData var10;
      if (var3 > 0) {
         var7 = this.findMarkAdjustIndex(var2);
         var8 = this.marks.size();

         for(var9 = var7; var9 < var8; ++var9) {
            var10 = this.marks.elementAt(var9);
            if (var10.index >= var5) {
               break;
            }

            var10.index -= var6;
         }
      } else if (var3 < 0) {
         var7 = this.findMarkAdjustIndex(var1);
         var8 = this.marks.size();

         for(var9 = var7; var9 < var8; ++var9) {
            var10 = this.marks.elementAt(var9);
            if (var10.index >= var4) {
               break;
            }

            var10.index += var6;
         }
      }

      this.resetMarksAtZero();
   }

   protected void resetMarksAtZero() {
      if (this.marks != null && this.getGapStart() == 0) {
         int var1 = this.getGapEnd();
         int var2 = 0;

         for(int var3 = this.marks.size(); var2 < var3; ++var2) {
            GapContent.MarkData var4 = this.marks.elementAt(var2);
            if (var4.index > var1) {
               break;
            }

            var4.index = 0;
         }
      }

   }

   protected void shiftGapStartDown(int var1) {
      int var2 = this.findMarkAdjustIndex(var1);
      int var3 = this.marks.size();
      int var4 = this.getGapStart();
      int var5 = this.getGapEnd();

      for(int var6 = var2; var6 < var3; ++var6) {
         GapContent.MarkData var7 = this.marks.elementAt(var6);
         if (var7.index > var4) {
            break;
         }

         var7.index = var5;
      }

      super.shiftGapStartDown(var1);
      this.resetMarksAtZero();
   }

   protected void shiftGapEndUp(int var1) {
      int var2 = this.findMarkAdjustIndex(this.getGapEnd());
      int var3 = this.marks.size();

      for(int var4 = var2; var4 < var3; ++var4) {
         GapContent.MarkData var5 = this.marks.elementAt(var4);
         if (var5.index >= var1) {
            break;
         }

         var5.index = var1;
      }

      super.shiftGapEndUp(var1);
      this.resetMarksAtZero();
   }

   final int compare(GapContent.MarkData var1, GapContent.MarkData var2) {
      if (var1.index < var2.index) {
         return -1;
      } else {
         return var1.index > var2.index ? 1 : 0;
      }
   }

   final int findMarkAdjustIndex(int var1) {
      this.search.index = Math.max(var1, 1);
      int var2 = this.findSortIndex(this.search);

      for(int var3 = var2 - 1; var3 >= 0; --var3) {
         GapContent.MarkData var4 = this.marks.elementAt(var3);
         if (var4.index != this.search.index) {
            break;
         }

         --var2;
      }

      return var2;
   }

   final int findSortIndex(GapContent.MarkData var1) {
      int var2 = 0;
      int var3 = this.marks.size() - 1;
      int var4 = 0;
      if (var3 == -1) {
         return 0;
      } else {
         GapContent.MarkData var6 = this.marks.elementAt(var3);
         int var5 = this.compare(var1, var6);
         if (var5 > 0) {
            return var3 + 1;
         } else {
            while(var2 <= var3) {
               var4 = var2 + (var3 - var2) / 2;
               GapContent.MarkData var7 = this.marks.elementAt(var4);
               var5 = this.compare(var1, var7);
               if (var5 == 0) {
                  return var4;
               }

               if (var5 < 0) {
                  var3 = var4 - 1;
               } else {
                  var2 = var4 + 1;
               }
            }

            return var5 < 0 ? var4 : var4 + 1;
         }
      }
   }

   final void removeUnusedMarks() {
      int var1 = this.marks.size();
      GapContent.MarkVector var2 = new GapContent.MarkVector(var1);

      for(int var3 = 0; var3 < var1; ++var3) {
         GapContent.MarkData var4 = this.marks.elementAt(var3);
         if (var4.get() != null) {
            var2.addElement(var4);
         }
      }

      this.marks = var2;
      this.unusedMarks = 0;
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      var1.defaultReadObject();
      this.marks = new GapContent.MarkVector();
      this.search = new GapContent.MarkData(0);
      this.queue = new ReferenceQueue();
   }

   protected Vector getPositionsInRange(Vector var1, int var2, int var3) {
      int var4 = var2 + var3;
      int var7 = this.getGapStart();
      int var8 = this.getGapEnd();
      int var5;
      int var6;
      if (var2 < var7) {
         if (var2 == 0) {
            var5 = 0;
         } else {
            var5 = this.findMarkAdjustIndex(var2);
         }

         if (var4 >= var7) {
            var6 = this.findMarkAdjustIndex(var4 + (var8 - var7) + 1);
         } else {
            var6 = this.findMarkAdjustIndex(var4 + 1);
         }
      } else {
         var5 = this.findMarkAdjustIndex(var2 + (var8 - var7));
         var6 = this.findMarkAdjustIndex(var4 + (var8 - var7) + 1);
      }

      Vector var9 = var1 == null ? new Vector(Math.max(1, var6 - var5)) : var1;

      for(int var10 = var5; var10 < var6; ++var10) {
         var9.addElement(new GapContent.UndoPosRef(this.marks.elementAt(var10)));
      }

      return var9;
   }

   protected void updateUndoPositions(Vector var1, int var2, int var3) {
      int var4 = var2 + var3;
      int var5 = this.getGapEnd();
      int var7 = this.findMarkAdjustIndex(var5 + 1);
      int var6;
      if (var2 != 0) {
         var6 = this.findMarkAdjustIndex(var5);
      } else {
         var6 = 0;
      }

      for(int var8 = var1.size() - 1; var8 >= 0; --var8) {
         GapContent.UndoPosRef var9 = (GapContent.UndoPosRef)var1.elementAt(var8);
         var9.resetLocation(var4, var5);
      }

      if (var6 < var7) {
         Object[] var12 = new Object[var7 - var6];
         int var13 = 0;
         int var10;
         GapContent.MarkData var11;
         if (var2 == 0) {
            for(var10 = var6; var10 < var7; ++var10) {
               var11 = this.marks.elementAt(var10);
               if (var11.index == 0) {
                  var12[var13++] = var11;
               }
            }

            for(var10 = var6; var10 < var7; ++var10) {
               var11 = this.marks.elementAt(var10);
               if (var11.index != 0) {
                  var12[var13++] = var11;
               }
            }
         } else {
            for(var10 = var6; var10 < var7; ++var10) {
               var11 = this.marks.elementAt(var10);
               if (var11.index != var5) {
                  var12[var13++] = var11;
               }
            }

            for(var10 = var6; var10 < var7; ++var10) {
               var11 = this.marks.elementAt(var10);
               if (var11.index == var5) {
                  var12[var13++] = var11;
               }
            }
         }

         this.marks.replaceRange(var6, var7, var12);
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
         this.posRefs = GapContent.this.getPositionsInRange((Vector)null, var2, this.length);
      }

      public void undo() throws CannotUndoException {
         super.undo();

         try {
            GapContent.this.insertString(this.offset, this.string);
            if (this.posRefs != null) {
               GapContent.this.updateUndoPositions(this.posRefs, this.offset, this.length);
               this.posRefs = null;
            }

            this.string = null;
         } catch (BadLocationException var2) {
            throw new CannotUndoException();
         }
      }

      public void redo() throws CannotRedoException {
         super.redo();

         try {
            this.string = GapContent.this.getString(this.offset, this.length);
            this.posRefs = GapContent.this.getPositionsInRange((Vector)null, this.offset, this.length);
            GapContent.this.remove(this.offset, this.length);
         } catch (BadLocationException var2) {
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
            this.posRefs = GapContent.this.getPositionsInRange((Vector)null, this.offset, this.length);
            this.string = GapContent.this.getString(this.offset, this.length);
            GapContent.this.remove(this.offset, this.length);
         } catch (BadLocationException var2) {
            throw new CannotUndoException();
         }
      }

      public void redo() throws CannotRedoException {
         super.redo();

         try {
            GapContent.this.insertString(this.offset, this.string);
            this.string = null;
            if (this.posRefs != null) {
               GapContent.this.updateUndoPositions(this.posRefs, this.offset, this.length);
               this.posRefs = null;
            }

         } catch (BadLocationException var2) {
            throw new CannotRedoException();
         }
      }
   }

   final class UndoPosRef {
      protected int undoLocation;
      protected GapContent.MarkData rec;

      UndoPosRef(GapContent.MarkData var2) {
         this.rec = var2;
         this.undoLocation = var2.getOffset();
      }

      protected void resetLocation(int var1, int var2) {
         if (this.undoLocation != var1) {
            this.rec.index = this.undoLocation;
         } else {
            this.rec.index = var2;
         }

      }
   }

   static class MarkVector extends GapVector {
      GapContent.MarkData[] oneMark = new GapContent.MarkData[1];

      MarkVector() {
      }

      MarkVector(int var1) {
         super(var1);
      }

      protected Object allocateArray(int var1) {
         return new GapContent.MarkData[var1];
      }

      protected int getArrayLength() {
         GapContent.MarkData[] var1 = (GapContent.MarkData[])((GapContent.MarkData[])this.getArray());
         return var1.length;
      }

      public int size() {
         int var1 = this.getArrayLength() - (this.getGapEnd() - this.getGapStart());
         return var1;
      }

      public void insertElementAt(GapContent.MarkData var1, int var2) {
         this.oneMark[0] = var1;
         this.replace(var2, 0, this.oneMark, 1);
      }

      public void addElement(GapContent.MarkData var1) {
         this.insertElementAt(var1, this.size());
      }

      public GapContent.MarkData elementAt(int var1) {
         int var2 = this.getGapStart();
         int var3 = this.getGapEnd();
         GapContent.MarkData[] var4 = (GapContent.MarkData[])((GapContent.MarkData[])this.getArray());
         if (var1 < var2) {
            return var4[var1];
         } else {
            var1 += var3 - var2;
            return var4[var1];
         }
      }

      protected void replaceRange(int var1, int var2, Object[] var3) {
         int var4 = this.getGapStart();
         int var5 = this.getGapEnd();
         int var6 = var1;
         int var7 = 0;
         Object[] var8 = (Object[])((Object[])this.getArray());
         if (var1 >= var4) {
            var6 = var1 + (var5 - var4);
            var2 += var5 - var4;
         } else if (var2 >= var4) {
            for(var2 += var5 - var4; var6 < var4; var8[var6++] = var3[var7++]) {
            }

            var6 = var5;
         } else {
            while(var6 < var2) {
               var8[var6++] = var3[var7++];
            }
         }

         while(var6 < var2) {
            var8[var6++] = var3[var7++];
         }

      }
   }

   final class StickyPosition implements Position {
      GapContent.MarkData mark;

      void setMark(GapContent.MarkData var1) {
         this.mark = var1;
      }

      public final int getOffset() {
         return this.mark.getOffset();
      }

      public String toString() {
         return Integer.toString(this.getOffset());
      }
   }

   final class MarkData extends WeakReference<GapContent.StickyPosition> {
      int index;

      MarkData(int var2) {
         super((Object)null);
         this.index = var2;
      }

      MarkData(int var2, GapContent.StickyPosition var3, ReferenceQueue<? super GapContent.StickyPosition> var4) {
         super(var3, var4);
         this.index = var2;
      }

      public final int getOffset() {
         int var1 = GapContent.this.getGapStart();
         int var2 = GapContent.this.getGapEnd();
         int var3 = this.index < var1 ? this.index : this.index - (var2 - var1);
         return Math.max(var3, 0);
      }

      GapContent.StickyPosition getPosition() {
         return (GapContent.StickyPosition)this.get();
      }
   }
}
