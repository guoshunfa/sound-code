package com.sun.xml.internal.ws.org.objectweb.asm;

import java.io.IOException;
import java.io.InputStream;

public class ClassReader {
   static final boolean SIGNATURES = true;
   static final boolean ANNOTATIONS = true;
   static final boolean FRAMES = true;
   static final boolean WRITER = true;
   static final boolean RESIZE = true;
   public static final int SKIP_CODE = 1;
   public static final int SKIP_DEBUG = 2;
   public static final int SKIP_FRAMES = 4;
   public static final int EXPAND_FRAMES = 8;
   public final byte[] b;
   private final int[] items;
   private final String[] strings;
   private final int maxStringLength;
   public final int header;

   public ClassReader(byte[] b) {
      this(b, 0, b.length);
   }

   public ClassReader(byte[] b, int off, int len) {
      this.b = b;
      this.items = new int[this.readUnsignedShort(off + 8)];
      int n = this.items.length;
      this.strings = new String[n];
      int max = 0;
      int index = off + 10;

      for(int i = 1; i < n; ++i) {
         this.items[i] = index + 1;
         int size;
         switch(b[index]) {
         case 1:
            size = 3 + this.readUnsignedShort(index + 1);
            if (size > max) {
               max = size;
            }
            break;
         case 2:
         case 7:
         case 8:
         default:
            size = 3;
            break;
         case 3:
         case 4:
         case 9:
         case 10:
         case 11:
         case 12:
            size = 5;
            break;
         case 5:
         case 6:
            size = 9;
            ++i;
         }

         index += size;
      }

      this.maxStringLength = max;
      this.header = index;
   }

   public int getAccess() {
      return this.readUnsignedShort(this.header);
   }

   public String getClassName() {
      return this.readClass(this.header + 2, new char[this.maxStringLength]);
   }

   public String getSuperName() {
      int n = this.items[this.readUnsignedShort(this.header + 4)];
      return n == 0 ? null : this.readUTF8(n, new char[this.maxStringLength]);
   }

   public String[] getInterfaces() {
      int index = this.header + 6;
      int n = this.readUnsignedShort(index);
      String[] interfaces = new String[n];
      if (n > 0) {
         char[] buf = new char[this.maxStringLength];

         for(int i = 0; i < n; ++i) {
            index += 2;
            interfaces[i] = this.readClass(index, buf);
         }
      }

      return interfaces;
   }

   void copyPool(ClassWriter classWriter) {
      char[] buf = new char[this.maxStringLength];
      int ll = this.items.length;
      Item[] items2 = new Item[ll];

      int i;
      for(i = 1; i < ll; ++i) {
         int index = this.items[i];
         int tag = this.b[index - 1];
         Item item = new Item(i);
         switch(tag) {
         case 1:
            String s = this.strings[i];
            if (s == null) {
               index = this.items[i];
               s = this.strings[i] = this.readUTF(index + 2, this.readUnsignedShort(index), buf);
            }

            item.set(tag, s, (String)null, (String)null);
            break;
         case 2:
         case 7:
         case 8:
         default:
            item.set(tag, this.readUTF8(index, buf), (String)null, (String)null);
            break;
         case 3:
            item.set(this.readInt(index));
            break;
         case 4:
            item.set(Float.intBitsToFloat(this.readInt(index)));
            break;
         case 5:
            item.set(this.readLong(index));
            ++i;
            break;
         case 6:
            item.set(Double.longBitsToDouble(this.readLong(index)));
            ++i;
            break;
         case 9:
         case 10:
         case 11:
            int nameType = this.items[this.readUnsignedShort(index + 2)];
            item.set(tag, this.readClass(index, buf), this.readUTF8(nameType, buf), this.readUTF8(nameType + 2, buf));
            break;
         case 12:
            item.set(tag, this.readUTF8(index, buf), this.readUTF8(index + 2, buf), (String)null);
         }

         int index2 = item.hashCode % items2.length;
         item.next = items2[index2];
         items2[index2] = item;
      }

      i = this.items[1] - 1;
      classWriter.pool.putByteArray(this.b, i, this.header - i);
      classWriter.items = items2;
      classWriter.threshold = (int)(0.75D * (double)ll);
      classWriter.index = ll;
   }

   public ClassReader(InputStream is) throws IOException {
      this(readClass(is));
   }

   public ClassReader(String name) throws IOException {
      this(ClassLoader.getSystemResourceAsStream(name.replace('.', '/') + ".class"));
   }

   private static byte[] readClass(InputStream is) throws IOException {
      if (is == null) {
         throw new IOException("Class not found");
      } else {
         byte[] b = new byte[is.available()];
         int len = 0;

         while(true) {
            int n = is.read(b, len, b.length - len);
            byte[] c;
            if (n == -1) {
               if (len < b.length) {
                  c = new byte[len];
                  System.arraycopy(b, 0, c, 0, len);
                  b = c;
               }

               return b;
            }

            len += n;
            if (len == b.length) {
               c = new byte[b.length + 1000];
               System.arraycopy(b, 0, c, 0, len);
               b = c;
            }
         }
      }
   }

   public void accept(ClassVisitor classVisitor, int flags) {
      this.accept(classVisitor, new Attribute[0], flags);
   }

   public void accept(ClassVisitor classVisitor, Attribute[] attrs, int flags) {
      byte[] b = this.b;
      char[] c = new char[this.maxStringLength];
      int anns = 0;
      int ianns = 0;
      Attribute cattrs = null;
      int u = this.header;
      int access = this.readUnsignedShort(u);
      String name = this.readClass(u + 2, c);
      int v = this.items[this.readUnsignedShort(u + 4)];
      String superClassName = v == 0 ? null : this.readUTF8(v, c);
      String[] implementedItfs = new String[this.readUnsignedShort(u + 6)];
      int w = 0;
      u += 8;

      int i;
      for(i = 0; i < implementedItfs.length; ++i) {
         implementedItfs[i] = this.readClass(u, c);
         u += 2;
      }

      boolean skipCode = (flags & 1) != 0;
      boolean skipDebug = (flags & 2) != 0;
      boolean unzip = (flags & 8) != 0;
      i = this.readUnsignedShort(u);

      int j;
      for(v = u + 2; i > 0; --i) {
         j = this.readUnsignedShort(v + 6);

         for(v += 8; j > 0; --j) {
            v += 6 + this.readInt(v + 2);
         }
      }

      i = this.readUnsignedShort(v);

      for(v += 2; i > 0; --i) {
         j = this.readUnsignedShort(v + 6);

         for(v += 8; j > 0; --j) {
            v += 6 + this.readInt(v + 2);
         }
      }

      String signature = null;
      String sourceFile = null;
      String sourceDebug = null;
      String enclosingOwner = null;
      String enclosingName = null;
      String enclosingDesc = null;
      i = this.readUnsignedShort(v);

      Attribute attr;
      String attrName;
      int u0;
      for(v += 2; i > 0; --i) {
         attrName = this.readUTF8(v, c);
         if ("SourceFile".equals(attrName)) {
            sourceFile = this.readUTF8(v + 6, c);
         } else if ("InnerClasses".equals(attrName)) {
            w = v + 6;
         } else if ("EnclosingMethod".equals(attrName)) {
            enclosingOwner = this.readClass(v + 6, c);
            u0 = this.readUnsignedShort(v + 8);
            if (u0 != 0) {
               enclosingName = this.readUTF8(this.items[u0], c);
               enclosingDesc = this.readUTF8(this.items[u0] + 2, c);
            }
         } else if ("Signature".equals(attrName)) {
            signature = this.readUTF8(v + 6, c);
         } else if ("RuntimeVisibleAnnotations".equals(attrName)) {
            anns = v + 6;
         } else if ("Deprecated".equals(attrName)) {
            access |= 131072;
         } else if ("Synthetic".equals(attrName)) {
            access |= 4096;
         } else if ("SourceDebugExtension".equals(attrName)) {
            u0 = this.readInt(v + 2);
            sourceDebug = this.readUTF(v + 6, u0, new char[u0]);
         } else if ("RuntimeInvisibleAnnotations".equals(attrName)) {
            ianns = v + 6;
         } else {
            attr = this.readAttribute(attrs, attrName, v + 6, this.readInt(v + 2), c, -1, (Label[])null);
            if (attr != null) {
               attr.next = cattrs;
               cattrs = attr;
            }
         }

         v += 6 + this.readInt(v + 2);
      }

      classVisitor.visit(this.readInt(4), access, name, signature, superClassName, implementedItfs);
      if (!skipDebug && (sourceFile != null || sourceDebug != null)) {
         classVisitor.visitSource(sourceFile, sourceDebug);
      }

      if (enclosingOwner != null) {
         classVisitor.visitOuterClass(enclosingOwner, enclosingName, enclosingDesc);
      }

      for(i = 1; i >= 0; --i) {
         v = i == 0 ? ianns : anns;
         if (v != 0) {
            j = this.readUnsignedShort(v);

            for(v += 2; j > 0; --j) {
               v = this.readAnnotationValues(v + 2, c, true, classVisitor.visitAnnotation(this.readUTF8(v, c), i != 0));
            }
         }
      }

      while(cattrs != null) {
         attr = cattrs.next;
         cattrs.next = null;
         classVisitor.visitAttribute(cattrs);
         cattrs = attr;
      }

      if (w != 0) {
         i = this.readUnsignedShort(w);

         for(w += 2; i > 0; --i) {
            classVisitor.visitInnerClass(this.readUnsignedShort(w) == 0 ? null : this.readClass(w, c), this.readUnsignedShort(w + 2) == 0 ? null : this.readClass(w + 2, c), this.readUnsignedShort(w + 4) == 0 ? null : this.readUTF8(w + 4, c), this.readUnsignedShort(w + 6));
            w += 8;
         }
      }

      i = this.readUnsignedShort(u);

      int k;
      String desc;
      for(u += 2; i > 0; --i) {
         access = this.readUnsignedShort(u);
         name = this.readUTF8(u + 2, c);
         desc = this.readUTF8(u + 4, c);
         u0 = 0;
         signature = null;
         anns = 0;
         ianns = 0;
         cattrs = null;
         j = this.readUnsignedShort(u + 6);

         for(u += 8; j > 0; --j) {
            attrName = this.readUTF8(u, c);
            if ("ConstantValue".equals(attrName)) {
               u0 = this.readUnsignedShort(u + 6);
            } else if ("Signature".equals(attrName)) {
               signature = this.readUTF8(u + 6, c);
            } else if ("Deprecated".equals(attrName)) {
               access |= 131072;
            } else if ("Synthetic".equals(attrName)) {
               access |= 4096;
            } else if ("RuntimeVisibleAnnotations".equals(attrName)) {
               anns = u + 6;
            } else if ("RuntimeInvisibleAnnotations".equals(attrName)) {
               ianns = u + 6;
            } else {
               attr = this.readAttribute(attrs, attrName, u + 6, this.readInt(u + 2), c, -1, (Label[])null);
               if (attr != null) {
                  attr.next = cattrs;
                  cattrs = attr;
               }
            }

            u += 6 + this.readInt(u + 2);
         }

         FieldVisitor fv = classVisitor.visitField(access, name, desc, signature, u0 == 0 ? null : this.readConst(u0, c));
         if (fv != null) {
            for(j = 1; j >= 0; --j) {
               v = j == 0 ? ianns : anns;
               if (v != 0) {
                  k = this.readUnsignedShort(v);

                  for(v += 2; k > 0; --k) {
                     v = this.readAnnotationValues(v + 2, c, true, fv.visitAnnotation(this.readUTF8(v, c), j != 0));
                  }
               }
            }

            while(cattrs != null) {
               attr = cattrs.next;
               cattrs.next = null;
               fv.visitAttribute(cattrs);
               cattrs = attr;
            }

            fv.visitEnd();
         }
      }

      i = this.readUnsignedShort(u);

      for(u += 2; i > 0; --i) {
         u0 = u + 6;
         access = this.readUnsignedShort(u);
         name = this.readUTF8(u + 2, c);
         desc = this.readUTF8(u + 4, c);
         signature = null;
         anns = 0;
         ianns = 0;
         int dann = 0;
         int mpanns = 0;
         int impanns = 0;
         cattrs = null;
         v = 0;
         w = 0;
         j = this.readUnsignedShort(u + 6);

         for(u += 8; j > 0; --j) {
            attrName = this.readUTF8(u, c);
            int attrSize = this.readInt(u + 2);
            u += 6;
            if ("Code".equals(attrName)) {
               if (!skipCode) {
                  v = u;
               }
            } else if ("Exceptions".equals(attrName)) {
               w = u;
            } else if ("Signature".equals(attrName)) {
               signature = this.readUTF8(u, c);
            } else if ("Deprecated".equals(attrName)) {
               access |= 131072;
            } else if ("RuntimeVisibleAnnotations".equals(attrName)) {
               anns = u;
            } else if ("AnnotationDefault".equals(attrName)) {
               dann = u;
            } else if ("Synthetic".equals(attrName)) {
               access |= 4096;
            } else if ("RuntimeInvisibleAnnotations".equals(attrName)) {
               ianns = u;
            } else if ("RuntimeVisibleParameterAnnotations".equals(attrName)) {
               mpanns = u;
            } else if ("RuntimeInvisibleParameterAnnotations".equals(attrName)) {
               impanns = u;
            } else {
               attr = this.readAttribute(attrs, attrName, u, attrSize, c, -1, (Label[])null);
               if (attr != null) {
                  attr.next = cattrs;
                  cattrs = attr;
               }
            }

            u += attrSize;
         }

         String[] exceptions;
         if (w == 0) {
            exceptions = null;
         } else {
            exceptions = new String[this.readUnsignedShort(w)];
            w += 2;

            for(j = 0; j < exceptions.length; ++j) {
               exceptions[j] = this.readClass(w, c);
               w += 2;
            }
         }

         MethodVisitor mv = classVisitor.visitMethod(access, name, desc, signature, exceptions);
         if (mv != null) {
            if (mv instanceof MethodWriter) {
               MethodWriter mw = (MethodWriter)mv;
               if (mw.cw.cr == this && signature == mw.signature) {
                  boolean sameExceptions = false;
                  if (exceptions == null) {
                     sameExceptions = mw.exceptionCount == 0;
                  } else if (exceptions.length == mw.exceptionCount) {
                     sameExceptions = true;

                     for(j = exceptions.length - 1; j >= 0; --j) {
                        w -= 2;
                        if (mw.exceptions[j] != this.readUnsignedShort(w)) {
                           sameExceptions = false;
                           break;
                        }
                     }
                  }

                  if (sameExceptions) {
                     mw.classReaderOffset = u0;
                     mw.classReaderLength = u - u0;
                     continue;
                  }
               }
            }

            if (dann != 0) {
               AnnotationVisitor dv = mv.visitAnnotationDefault();
               this.readAnnotationValue(dann, c, (String)null, dv);
               if (dv != null) {
                  dv.visitEnd();
               }
            }

            for(j = 1; j >= 0; --j) {
               w = j == 0 ? ianns : anns;
               if (w != 0) {
                  k = this.readUnsignedShort(w);

                  for(w += 2; k > 0; --k) {
                     w = this.readAnnotationValues(w + 2, c, true, mv.visitAnnotation(this.readUTF8(w, c), j != 0));
                  }
               }
            }

            if (mpanns != 0) {
               this.readParameterAnnotations(mpanns, desc, c, true, mv);
            }

            if (impanns != 0) {
               this.readParameterAnnotations(impanns, desc, c, false, mv);
            }

            while(cattrs != null) {
               attr = cattrs.next;
               cattrs.next = null;
               mv.visitAttribute(cattrs);
               cattrs = attr;
            }
         }

         if (mv != null && v != 0) {
            int maxStack = this.readUnsignedShort(v);
            int maxLocals = this.readUnsignedShort(v + 2);
            int codeLength = this.readInt(v + 4);
            v += 8;
            int codeStart = v;
            int codeEnd = v + codeLength;
            mv.visitCode();
            Label[] labels = new Label[codeLength + 2];
            this.readLabel(codeLength + 1, labels);

            label713:
            while(true) {
               int varTable;
               label711:
               while(v < codeEnd) {
                  w = v - codeStart;
                  varTable = b[v] & 255;
                  switch(ClassWriter.TYPE[varTable]) {
                  case 0:
                  case 4:
                     ++v;
                     break;
                  case 1:
                  case 3:
                  case 10:
                     v += 2;
                     break;
                  case 2:
                  case 5:
                  case 6:
                  case 11:
                  case 12:
                     v += 3;
                     break;
                  case 7:
                     v += 5;
                     break;
                  case 8:
                     this.readLabel(w + this.readShort(v + 1), labels);
                     v += 3;
                     break;
                  case 9:
                     this.readLabel(w + this.readInt(v + 1), labels);
                     v += 5;
                     break;
                  case 13:
                     v = v + 4 - (w & 3);
                     this.readLabel(w + this.readInt(v), labels);
                     j = this.readInt(v + 8) - this.readInt(v + 4) + 1;
                     v += 12;

                     while(true) {
                        if (j <= 0) {
                           continue label711;
                        }

                        this.readLabel(w + this.readInt(v), labels);
                        v += 4;
                        --j;
                     }
                  case 14:
                     v = v + 4 - (w & 3);
                     this.readLabel(w + this.readInt(v), labels);
                     j = this.readInt(v + 4);
                     v += 8;

                     while(true) {
                        if (j <= 0) {
                           continue label711;
                        }

                        this.readLabel(w + this.readInt(v + 4), labels);
                        v += 8;
                        --j;
                     }
                  case 15:
                  default:
                     v += 4;
                     break;
                  case 16:
                     varTable = b[v + 1] & 255;
                     if (varTable == 132) {
                        v += 6;
                     } else {
                        v += 4;
                     }
                  }
               }

               j = this.readUnsignedShort(v);

               int frameCount;
               for(v += 2; j > 0; --j) {
                  Label start = this.readLabel(this.readUnsignedShort(v), labels);
                  Label end = this.readLabel(this.readUnsignedShort(v + 2), labels);
                  Label handler = this.readLabel(this.readUnsignedShort(v + 4), labels);
                  frameCount = this.readUnsignedShort(v + 6);
                  if (frameCount == 0) {
                     mv.visitTryCatchBlock(start, end, handler, (String)null);
                  } else {
                     mv.visitTryCatchBlock(start, end, handler, this.readUTF8(this.items[frameCount], c));
                  }

                  v += 8;
               }

               varTable = 0;
               int varTypeTable = 0;
               int stackMap = 0;
               frameCount = 0;
               int frameMode = 0;
               int frameOffset = 0;
               int frameLocalCount = 0;
               int frameLocalDiff = 0;
               int frameStackCount = 0;
               Object[] frameLocal = null;
               Object[] frameStack = null;
               boolean zip = true;
               cattrs = null;
               j = this.readUnsignedShort(v);

               int label;
               for(v += 2; j > 0; --j) {
                  attrName = this.readUTF8(v, c);
                  Label var10000;
                  if ("LocalVariableTable".equals(attrName)) {
                     if (!skipDebug) {
                        varTable = v + 6;
                        k = this.readUnsignedShort(v + 6);

                        for(w = v + 8; k > 0; --k) {
                           label = this.readUnsignedShort(w);
                           if (labels[label] == null) {
                              var10000 = this.readLabel(label, labels);
                              var10000.status |= 1;
                           }

                           label += this.readUnsignedShort(w + 2);
                           if (labels[label] == null) {
                              var10000 = this.readLabel(label, labels);
                              var10000.status |= 1;
                           }

                           w += 10;
                        }
                     }
                  } else if ("LocalVariableTypeTable".equals(attrName)) {
                     varTypeTable = v + 6;
                  } else if ("LineNumberTable".equals(attrName)) {
                     if (!skipDebug) {
                        k = this.readUnsignedShort(v + 6);

                        for(w = v + 8; k > 0; --k) {
                           label = this.readUnsignedShort(w);
                           if (labels[label] == null) {
                              var10000 = this.readLabel(label, labels);
                              var10000.status |= 1;
                           }

                           labels[label].line = this.readUnsignedShort(w + 2);
                           w += 4;
                        }
                     }
                  } else if ("StackMapTable".equals(attrName)) {
                     if ((flags & 4) == 0) {
                        stackMap = v + 8;
                        frameCount = this.readUnsignedShort(v + 6);
                     }
                  } else if ("StackMap".equals(attrName)) {
                     if ((flags & 4) == 0) {
                        stackMap = v + 8;
                        frameCount = this.readUnsignedShort(v + 6);
                        zip = false;
                     }
                  } else {
                     for(k = 0; k < attrs.length; ++k) {
                        if (attrs[k].type.equals(attrName)) {
                           attr = attrs[k].read(this, v + 6, this.readInt(v + 2), c, codeStart - 8, labels);
                           if (attr != null) {
                              attr.next = cattrs;
                              cattrs = attr;
                           }
                        }
                     }
                  }

                  v += 6 + this.readInt(v + 2);
               }

               if (stackMap != 0) {
                  frameLocal = new Object[maxLocals];
                  frameStack = new Object[maxStack];
                  if (unzip) {
                     int local = 0;
                     if ((access & 8) == 0) {
                        if ("<init>".equals(name)) {
                           frameLocal[local++] = Opcodes.UNINITIALIZED_THIS;
                        } else {
                           frameLocal[local++] = this.readClass(this.header + 2, c);
                        }
                     }

                     j = 1;

                     label649:
                     while(true) {
                        while(true) {
                           k = j;
                           switch(desc.charAt(j++)) {
                           case 'B':
                           case 'C':
                           case 'I':
                           case 'S':
                           case 'Z':
                              frameLocal[local++] = Opcodes.INTEGER;
                              break;
                           case 'D':
                              frameLocal[local++] = Opcodes.DOUBLE;
                              break;
                           case 'E':
                           case 'G':
                           case 'H':
                           case 'K':
                           case 'M':
                           case 'N':
                           case 'O':
                           case 'P':
                           case 'Q':
                           case 'R':
                           case 'T':
                           case 'U':
                           case 'V':
                           case 'W':
                           case 'X':
                           case 'Y':
                           default:
                              frameLocalCount = local;
                              break label649;
                           case 'F':
                              frameLocal[local++] = Opcodes.FLOAT;
                              break;
                           case 'J':
                              frameLocal[local++] = Opcodes.LONG;
                              break;
                           case 'L':
                              while(desc.charAt(j) != ';') {
                                 ++j;
                              }

                              frameLocal[local++] = desc.substring(k + 1, j++);
                              break;
                           case '[':
                              while(desc.charAt(j) == '[') {
                                 ++j;
                              }

                              if (desc.charAt(j) == 'L') {
                                 ++j;

                                 while(desc.charAt(j) != ';') {
                                    ++j;
                                 }
                              }

                              int var10001 = local++;
                              ++j;
                              frameLocal[var10001] = desc.substring(k, j);
                           }
                        }
                     }
                  }

                  frameOffset = -1;
               }

               v = codeStart;

               while(true) {
                  int delta;
                  int n;
                  Label l;
                  label612:
                  while(v < codeEnd) {
                     w = v - codeStart;
                     l = labels[w];
                     if (l != null) {
                        mv.visitLabel(l);
                        if (!skipDebug && l.line > 0) {
                           mv.visitLineNumber(l.line, l);
                        }
                     }

                     while(true) {
                        int tag;
                        while(frameLocal != null && (frameOffset == w || frameOffset == -1)) {
                           if (zip && !unzip) {
                              if (frameOffset != -1) {
                                 mv.visitFrame(frameMode, frameLocalDiff, frameLocal, frameStackCount, frameStack);
                              }
                           } else {
                              mv.visitFrame(-1, frameLocalCount, frameLocal, frameStackCount, frameStack);
                           }

                           if (frameCount > 0) {
                              if (zip) {
                                 tag = b[stackMap++] & 255;
                              } else {
                                 tag = 255;
                                 frameOffset = -1;
                              }

                              frameLocalDiff = 0;
                              if (tag < 64) {
                                 delta = tag;
                                 frameMode = 3;
                                 frameStackCount = 0;
                              } else if (tag < 128) {
                                 delta = tag - 64;
                                 stackMap = this.readFrameType(frameStack, 0, stackMap, c, labels);
                                 frameMode = 4;
                                 frameStackCount = 1;
                              } else {
                                 delta = this.readUnsignedShort(stackMap);
                                 stackMap += 2;
                                 if (tag == 247) {
                                    stackMap = this.readFrameType(frameStack, 0, stackMap, c, labels);
                                    frameMode = 4;
                                    frameStackCount = 1;
                                 } else if (tag >= 248 && tag < 251) {
                                    frameMode = 2;
                                    frameLocalDiff = 251 - tag;
                                    frameLocalCount -= frameLocalDiff;
                                    frameStackCount = 0;
                                 } else if (tag == 251) {
                                    frameMode = 3;
                                    frameStackCount = 0;
                                 } else if (tag < 255) {
                                    j = unzip ? frameLocalCount : 0;

                                    for(k = tag - 251; k > 0; --k) {
                                       stackMap = this.readFrameType(frameLocal, j++, stackMap, c, labels);
                                    }

                                    frameMode = 1;
                                    frameLocalDiff = tag - 251;
                                    frameLocalCount += frameLocalDiff;
                                    frameStackCount = 0;
                                 } else {
                                    frameMode = 0;
                                    n = frameLocalDiff = frameLocalCount = this.readUnsignedShort(stackMap);
                                    stackMap += 2;

                                    for(j = 0; n > 0; --n) {
                                       stackMap = this.readFrameType(frameLocal, j++, stackMap, c, labels);
                                    }

                                    n = frameStackCount = this.readUnsignedShort(stackMap);
                                    stackMap += 2;

                                    for(j = 0; n > 0; --n) {
                                       stackMap = this.readFrameType(frameStack, j++, stackMap, c, labels);
                                    }
                                 }
                              }

                              frameOffset += delta + 1;
                              this.readLabel(frameOffset, labels);
                              --frameCount;
                           } else {
                              frameLocal = null;
                           }
                        }

                        tag = b[v] & 255;
                        switch(ClassWriter.TYPE[tag]) {
                        case 0:
                           mv.visitInsn(tag);
                           ++v;
                           continue label612;
                        case 1:
                           mv.visitIntInsn(tag, b[v + 1]);
                           v += 2;
                           continue label612;
                        case 2:
                           mv.visitIntInsn(tag, this.readShort(v + 1));
                           v += 3;
                           continue label612;
                        case 3:
                           mv.visitVarInsn(tag, b[v + 1] & 255);
                           v += 2;
                           continue label612;
                        case 4:
                           if (tag > 54) {
                              tag -= 59;
                              mv.visitVarInsn(54 + (tag >> 2), tag & 3);
                           } else {
                              tag -= 26;
                              mv.visitVarInsn(21 + (tag >> 2), tag & 3);
                           }

                           ++v;
                           continue label612;
                        case 5:
                           mv.visitTypeInsn(tag, this.readClass(v + 1, c));
                           v += 3;
                           continue label612;
                        case 6:
                        case 7:
                           int cpIndex = this.items[this.readUnsignedShort(v + 1)];
                           String iowner = this.readClass(cpIndex, c);
                           cpIndex = this.items[this.readUnsignedShort(cpIndex + 2)];
                           String iname = this.readUTF8(cpIndex, c);
                           String idesc = this.readUTF8(cpIndex + 2, c);
                           if (tag < 182) {
                              mv.visitFieldInsn(tag, iowner, iname, idesc);
                           } else {
                              mv.visitMethodInsn(tag, iowner, iname, idesc);
                           }

                           if (tag == 185) {
                              v += 5;
                           } else {
                              v += 3;
                           }
                           continue label612;
                        case 8:
                           mv.visitJumpInsn(tag, labels[w + this.readShort(v + 1)]);
                           v += 3;
                           continue label612;
                        case 9:
                           mv.visitJumpInsn(tag - 33, labels[w + this.readInt(v + 1)]);
                           v += 5;
                           continue label612;
                        case 10:
                           mv.visitLdcInsn(this.readConst(b[v + 1] & 255, c));
                           v += 2;
                           continue label612;
                        case 11:
                           mv.visitLdcInsn(this.readConst(this.readUnsignedShort(v + 1), c));
                           v += 3;
                           continue label612;
                        case 12:
                           mv.visitIincInsn(b[v + 1] & 255, b[v + 2]);
                           v += 3;
                           continue label612;
                        case 13:
                           v = v + 4 - (w & 3);
                           label = w + this.readInt(v);
                           delta = this.readInt(v + 4);
                           n = this.readInt(v + 8);
                           v += 12;
                           Label[] table = new Label[n - delta + 1];

                           for(j = 0; j < table.length; ++j) {
                              table[j] = labels[w + this.readInt(v)];
                              v += 4;
                           }

                           mv.visitTableSwitchInsn(delta, n, labels[label], table);
                           continue label612;
                        case 14:
                           v = v + 4 - (w & 3);
                           label = w + this.readInt(v);
                           j = this.readInt(v + 4);
                           v += 8;
                           int[] keys = new int[j];
                           Label[] values = new Label[j];

                           for(j = 0; j < keys.length; ++j) {
                              keys[j] = this.readInt(v);
                              values[j] = labels[w + this.readInt(v + 4)];
                              v += 8;
                           }

                           mv.visitLookupSwitchInsn(labels[label], keys, values);
                           continue label612;
                        case 15:
                        default:
                           mv.visitMultiANewArrayInsn(this.readClass(v + 1, c), b[v + 3] & 255);
                           v += 4;
                           continue label612;
                        case 16:
                           tag = b[v + 1] & 255;
                           if (tag == 132) {
                              mv.visitIincInsn(this.readUnsignedShort(v + 2), this.readShort(v + 4));
                              v += 6;
                           } else {
                              mv.visitVarInsn(tag, this.readUnsignedShort(v + 2));
                              v += 4;
                           }
                           continue label612;
                        }
                     }
                  }

                  l = labels[codeEnd - codeStart];
                  if (l != null) {
                     mv.visitLabel(l);
                  }

                  if (!skipDebug && varTable != 0) {
                     int[] typeTable = null;
                     if (varTypeTable != 0) {
                        k = this.readUnsignedShort(varTypeTable) * 3;
                        w = varTypeTable + 2;

                        for(typeTable = new int[k]; k > 0; w += 10) {
                           --k;
                           typeTable[k] = w + 6;
                           --k;
                           typeTable[k] = this.readUnsignedShort(w + 8);
                           --k;
                           typeTable[k] = this.readUnsignedShort(w);
                        }
                     }

                     k = this.readUnsignedShort(varTable);

                     for(w = varTable + 2; k > 0; --k) {
                        delta = this.readUnsignedShort(w);
                        n = this.readUnsignedShort(w + 2);
                        int index = this.readUnsignedShort(w + 8);
                        String vsignature = null;
                        if (typeTable != null) {
                           for(int a = 0; a < typeTable.length; a += 3) {
                              if (typeTable[a] == delta && typeTable[a + 1] == index) {
                                 vsignature = this.readUTF8(typeTable[a + 2], c);
                                 break;
                              }
                           }
                        }

                        mv.visitLocalVariable(this.readUTF8(w + 4, c), this.readUTF8(w + 6, c), vsignature, labels[delta], labels[delta + n], index);
                        w += 10;
                     }
                  }

                  while(cattrs != null) {
                     attr = cattrs.next;
                     cattrs.next = null;
                     mv.visitAttribute(cattrs);
                     cattrs = attr;
                  }

                  mv.visitMaxs(maxStack, maxLocals);
                  break label713;
               }
            }
         }

         if (mv != null) {
            mv.visitEnd();
         }
      }

      classVisitor.visitEnd();
   }

   private void readParameterAnnotations(int v, String desc, char[] buf, boolean visible, MethodVisitor mv) {
      int n = this.b[v++] & 255;
      int synthetics = Type.getArgumentTypes(desc).length - n;

      int i;
      AnnotationVisitor av;
      for(i = 0; i < synthetics; ++i) {
         av = mv.visitParameterAnnotation(i, "Ljava/lang/Synthetic;", false);
         if (av != null) {
            av.visitEnd();
         }
      }

      while(i < n + synthetics) {
         int j = this.readUnsignedShort(v);

         for(v += 2; j > 0; --j) {
            av = mv.visitParameterAnnotation(i, this.readUTF8(v, buf), visible);
            v = this.readAnnotationValues(v + 2, buf, true, av);
         }

         ++i;
      }

   }

   private int readAnnotationValues(int v, char[] buf, boolean named, AnnotationVisitor av) {
      int i = this.readUnsignedShort(v);
      v += 2;
      if (named) {
         while(i > 0) {
            v = this.readAnnotationValue(v + 2, buf, this.readUTF8(v, buf), av);
            --i;
         }
      } else {
         while(i > 0) {
            v = this.readAnnotationValue(v, buf, (String)null, av);
            --i;
         }
      }

      if (av != null) {
         av.visitEnd();
      }

      return v;
   }

   private int readAnnotationValue(int v, char[] buf, String name, AnnotationVisitor av) {
      if (av == null) {
         switch(this.b[v] & 255) {
         case 64:
            return this.readAnnotationValues(v + 3, buf, true, (AnnotationVisitor)null);
         case 91:
            return this.readAnnotationValues(v + 1, buf, false, (AnnotationVisitor)null);
         case 101:
            return v + 5;
         default:
            return v + 3;
         }
      } else {
         switch(this.b[v++] & 255) {
         case 64:
            v = this.readAnnotationValues(v + 2, buf, true, av.visitAnnotation(name, this.readUTF8(v, buf)));
         case 65:
         case 69:
         case 71:
         case 72:
         case 75:
         case 76:
         case 77:
         case 78:
         case 79:
         case 80:
         case 81:
         case 82:
         case 84:
         case 85:
         case 86:
         case 87:
         case 88:
         case 89:
         case 92:
         case 93:
         case 94:
         case 95:
         case 96:
         case 97:
         case 98:
         case 100:
         case 102:
         case 103:
         case 104:
         case 105:
         case 106:
         case 107:
         case 108:
         case 109:
         case 110:
         case 111:
         case 112:
         case 113:
         case 114:
         default:
            break;
         case 66:
            av.visit(name, new Byte((byte)this.readInt(this.items[this.readUnsignedShort(v)])));
            v += 2;
            break;
         case 67:
            av.visit(name, new Character((char)this.readInt(this.items[this.readUnsignedShort(v)])));
            v += 2;
            break;
         case 68:
         case 70:
         case 73:
         case 74:
            av.visit(name, this.readConst(this.readUnsignedShort(v), buf));
            v += 2;
            break;
         case 83:
            av.visit(name, new Short((short)this.readInt(this.items[this.readUnsignedShort(v)])));
            v += 2;
            break;
         case 90:
            av.visit(name, this.readInt(this.items[this.readUnsignedShort(v)]) == 0 ? Boolean.FALSE : Boolean.TRUE);
            v += 2;
            break;
         case 91:
            int size = this.readUnsignedShort(v);
            v += 2;
            if (size == 0) {
               return this.readAnnotationValues(v - 2, buf, false, av.visitArray(name));
            }

            int i;
            switch(this.b[v++] & 255) {
            case 66:
               byte[] bv = new byte[size];

               for(i = 0; i < size; ++i) {
                  bv[i] = (byte)this.readInt(this.items[this.readUnsignedShort(v)]);
                  v += 3;
               }

               av.visit(name, bv);
               --v;
               return v;
            case 67:
               char[] cv = new char[size];

               for(i = 0; i < size; ++i) {
                  cv[i] = (char)this.readInt(this.items[this.readUnsignedShort(v)]);
                  v += 3;
               }

               av.visit(name, cv);
               --v;
               return v;
            case 68:
               double[] dv = new double[size];

               for(i = 0; i < size; ++i) {
                  dv[i] = Double.longBitsToDouble(this.readLong(this.items[this.readUnsignedShort(v)]));
                  v += 3;
               }

               av.visit(name, dv);
               --v;
               return v;
            case 69:
            case 71:
            case 72:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            case 82:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            default:
               v = this.readAnnotationValues(v - 3, buf, false, av.visitArray(name));
               return v;
            case 70:
               float[] fv = new float[size];

               for(i = 0; i < size; ++i) {
                  fv[i] = Float.intBitsToFloat(this.readInt(this.items[this.readUnsignedShort(v)]));
                  v += 3;
               }

               av.visit(name, fv);
               --v;
               return v;
            case 73:
               int[] iv = new int[size];

               for(i = 0; i < size; ++i) {
                  iv[i] = this.readInt(this.items[this.readUnsignedShort(v)]);
                  v += 3;
               }

               av.visit(name, iv);
               --v;
               return v;
            case 74:
               long[] lv = new long[size];

               for(i = 0; i < size; ++i) {
                  lv[i] = this.readLong(this.items[this.readUnsignedShort(v)]);
                  v += 3;
               }

               av.visit(name, lv);
               --v;
               return v;
            case 83:
               short[] sv = new short[size];

               for(i = 0; i < size; ++i) {
                  sv[i] = (short)this.readInt(this.items[this.readUnsignedShort(v)]);
                  v += 3;
               }

               av.visit(name, sv);
               --v;
               return v;
            case 90:
               boolean[] zv = new boolean[size];

               for(i = 0; i < size; ++i) {
                  zv[i] = this.readInt(this.items[this.readUnsignedShort(v)]) != 0;
                  v += 3;
               }

               av.visit(name, zv);
               --v;
               return v;
            }
         case 99:
            av.visit(name, Type.getType(this.readUTF8(v, buf)));
            v += 2;
            break;
         case 101:
            av.visitEnum(name, this.readUTF8(v, buf), this.readUTF8(v + 2, buf));
            v += 4;
            break;
         case 115:
            av.visit(name, this.readUTF8(v, buf));
            v += 2;
         }

         return v;
      }
   }

   private int readFrameType(Object[] frame, int index, int v, char[] buf, Label[] labels) {
      int type = this.b[v++] & 255;
      switch(type) {
      case 0:
         frame[index] = Opcodes.TOP;
         break;
      case 1:
         frame[index] = Opcodes.INTEGER;
         break;
      case 2:
         frame[index] = Opcodes.FLOAT;
         break;
      case 3:
         frame[index] = Opcodes.DOUBLE;
         break;
      case 4:
         frame[index] = Opcodes.LONG;
         break;
      case 5:
         frame[index] = Opcodes.NULL;
         break;
      case 6:
         frame[index] = Opcodes.UNINITIALIZED_THIS;
         break;
      case 7:
         frame[index] = this.readClass(v, buf);
         v += 2;
         break;
      default:
         frame[index] = this.readLabel(this.readUnsignedShort(v), labels);
         v += 2;
      }

      return v;
   }

   protected Label readLabel(int offset, Label[] labels) {
      if (labels[offset] == null) {
         labels[offset] = new Label();
      }

      return labels[offset];
   }

   private Attribute readAttribute(Attribute[] attrs, String type, int off, int len, char[] buf, int codeOff, Label[] labels) {
      for(int i = 0; i < attrs.length; ++i) {
         if (attrs[i].type.equals(type)) {
            return attrs[i].read(this, off, len, buf, codeOff, labels);
         }
      }

      return (new Attribute(type)).read(this, off, len, (char[])null, -1, (Label[])null);
   }

   public int getItem(int item) {
      return this.items[item];
   }

   public int readByte(int index) {
      return this.b[index] & 255;
   }

   public int readUnsignedShort(int index) {
      byte[] b = this.b;
      return (b[index] & 255) << 8 | b[index + 1] & 255;
   }

   public short readShort(int index) {
      byte[] b = this.b;
      return (short)((b[index] & 255) << 8 | b[index + 1] & 255);
   }

   public int readInt(int index) {
      byte[] b = this.b;
      return (b[index] & 255) << 24 | (b[index + 1] & 255) << 16 | (b[index + 2] & 255) << 8 | b[index + 3] & 255;
   }

   public long readLong(int index) {
      long l1 = (long)this.readInt(index);
      long l0 = (long)this.readInt(index + 4) & 4294967295L;
      return l1 << 32 | l0;
   }

   public String readUTF8(int index, char[] buf) {
      int item = this.readUnsignedShort(index);
      String s = this.strings[item];
      if (s != null) {
         return s;
      } else {
         index = this.items[item];
         return this.strings[item] = this.readUTF(index + 2, this.readUnsignedShort(index), buf);
      }
   }

   private String readUTF(int index, int utfLen, char[] buf) {
      int endIndex = index + utfLen;
      byte[] b = this.b;
      int strLen = 0;

      while(index < endIndex) {
         int c = b[index++] & 255;
         byte d;
         switch(c >> 4) {
         case 0:
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
            buf[strLen++] = (char)c;
            break;
         case 8:
         case 9:
         case 10:
         case 11:
         default:
            d = b[index++];
            int e = b[index++];
            buf[strLen++] = (char)((c & 15) << 12 | (d & 63) << 6 | e & 63);
            break;
         case 12:
         case 13:
            d = b[index++];
            buf[strLen++] = (char)((c & 31) << 6 | d & 63);
         }
      }

      return new String(buf, 0, strLen);
   }

   public String readClass(int index, char[] buf) {
      return this.readUTF8(this.items[this.readUnsignedShort(index)], buf);
   }

   public Object readConst(int item, char[] buf) {
      int index = this.items[item];
      switch(this.b[index - 1]) {
      case 3:
         return new Integer(this.readInt(index));
      case 4:
         return new Float(Float.intBitsToFloat(this.readInt(index)));
      case 5:
         return new Long(this.readLong(index));
      case 6:
         return new Double(Double.longBitsToDouble(this.readLong(index)));
      case 7:
         return Type.getObjectType(this.readUTF8(index, buf));
      default:
         return this.readUTF8(index, buf);
      }
   }
}
