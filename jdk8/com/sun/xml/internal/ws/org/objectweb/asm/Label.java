package com.sun.xml.internal.ws.org.objectweb.asm;

public class Label {
   static final int DEBUG = 1;
   static final int RESOLVED = 2;
   static final int RESIZED = 4;
   static final int PUSHED = 8;
   static final int TARGET = 16;
   static final int STORE = 32;
   static final int REACHABLE = 64;
   static final int JSR = 128;
   static final int RET = 256;
   static final int SUBROUTINE = 512;
   static final int VISITED = 1024;
   public Object info;
   int status;
   int line;
   int position;
   private int referenceCount;
   private int[] srcAndRefPositions;
   int inputStackTop;
   int outputStackMax;
   Frame frame;
   Label successor;
   Edge successors;
   Label next;

   public int getOffset() {
      if ((this.status & 2) == 0) {
         throw new IllegalStateException("Label offset position has not been resolved yet");
      } else {
         return this.position;
      }
   }

   void put(MethodWriter owner, ByteVector out, int source, boolean wideOffset) {
      if ((this.status & 2) == 0) {
         if (wideOffset) {
            this.addReference(-1 - source, out.length);
            out.putInt(-1);
         } else {
            this.addReference(source, out.length);
            out.putShort(-1);
         }
      } else if (wideOffset) {
         out.putInt(this.position - source);
      } else {
         out.putShort(this.position - source);
      }

   }

   private void addReference(int sourcePosition, int referencePosition) {
      if (this.srcAndRefPositions == null) {
         this.srcAndRefPositions = new int[6];
      }

      if (this.referenceCount >= this.srcAndRefPositions.length) {
         int[] a = new int[this.srcAndRefPositions.length + 6];
         System.arraycopy(this.srcAndRefPositions, 0, a, 0, this.srcAndRefPositions.length);
         this.srcAndRefPositions = a;
      }

      this.srcAndRefPositions[this.referenceCount++] = sourcePosition;
      this.srcAndRefPositions[this.referenceCount++] = referencePosition;
   }

   boolean resolve(MethodWriter owner, int position, byte[] data) {
      boolean needUpdate = false;
      this.status |= 2;
      this.position = position;
      int i = 0;

      while(true) {
         while(i < this.referenceCount) {
            int source = this.srcAndRefPositions[i++];
            int reference = this.srcAndRefPositions[i++];
            int offset;
            if (source >= 0) {
               offset = position - source;
               if (offset < -32768 || offset > 32767) {
                  int opcode = data[reference - 1] & 255;
                  if (opcode <= 168) {
                     data[reference - 1] = (byte)(opcode + 49);
                  } else {
                     data[reference - 1] = (byte)(opcode + 20);
                  }

                  needUpdate = true;
               }

               data[reference++] = (byte)(offset >>> 8);
               data[reference] = (byte)offset;
            } else {
               offset = position + source + 1;
               data[reference++] = (byte)(offset >>> 24);
               data[reference++] = (byte)(offset >>> 16);
               data[reference++] = (byte)(offset >>> 8);
               data[reference] = (byte)offset;
            }
         }

         return needUpdate;
      }
   }

   Label getFirst() {
      return this.frame == null ? this : this.frame.owner;
   }

   boolean inSubroutine(long id) {
      if ((this.status & 1024) != 0) {
         return (this.srcAndRefPositions[(int)(id >>> 32)] & (int)id) != 0;
      } else {
         return false;
      }
   }

   boolean inSameSubroutine(Label block) {
      for(int i = 0; i < this.srcAndRefPositions.length; ++i) {
         if ((this.srcAndRefPositions[i] & block.srcAndRefPositions[i]) != 0) {
            return true;
         }
      }

      return false;
   }

   void addToSubroutine(long id, int nbSubroutines) {
      if ((this.status & 1024) == 0) {
         this.status |= 1024;
         this.srcAndRefPositions = new int[(nbSubroutines - 1) / 32 + 1];
      }

      int[] var10000 = this.srcAndRefPositions;
      var10000[(int)(id >>> 32)] |= (int)id;
   }

   void visitSubroutine(Label JSR, long id, int nbSubroutines) {
      Edge e;
      if (JSR != null) {
         if ((this.status & 1024) != 0) {
            return;
         }

         this.status |= 1024;
         if ((this.status & 256) != 0 && !this.inSameSubroutine(JSR)) {
            e = new Edge();
            e.info = this.inputStackTop;
            e.successor = JSR.successors.successor;
            e.next = this.successors;
            this.successors = e;
         }
      } else {
         if (this.inSubroutine(id)) {
            return;
         }

         this.addToSubroutine(id, nbSubroutines);
      }

      for(e = this.successors; e != null; e = e.next) {
         if ((this.status & 128) == 0 || e != this.successors.next) {
            e.successor.visitSubroutine(JSR, id, nbSubroutines);
         }
      }

   }

   public String toString() {
      return "L" + System.identityHashCode(this);
   }
}
