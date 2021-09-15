package com.sun.corba.se.impl.corba;

import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.impl.encoding.CDROutputStream;
import com.sun.corba.se.impl.encoding.TypeCodeInputStream;
import com.sun.corba.se.impl.encoding.TypeCodeOutputStream;
import com.sun.corba.se.impl.encoding.TypeCodeReader;
import com.sun.corba.se.impl.encoding.WrapperInputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import org.omg.CORBA.Any;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.UnionMember;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import sun.corba.OutputStreamFactory;

public final class TypeCodeImpl extends TypeCode {
   protected static final int tk_indirect = -1;
   private static final int EMPTY = 0;
   private static final int SIMPLE = 1;
   private static final int COMPLEX = 2;
   private static final int[] typeTable = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 1, 2, 2, 2, 2, 0, 0, 0, 0, 1, 1, 2, 2, 2, 2};
   static final String[] kindNames = new String[]{"null", "void", "short", "long", "ushort", "ulong", "float", "double", "boolean", "char", "octet", "any", "typecode", "principal", "objref", "struct", "union", "enum", "string", "sequence", "array", "alias", "exception", "longlong", "ulonglong", "longdouble", "wchar", "wstring", "fixed", "value", "valueBox", "native", "abstractInterface"};
   private int _kind;
   private String _id;
   private String _name;
   private int _memberCount;
   private String[] _memberNames;
   private TypeCodeImpl[] _memberTypes;
   private AnyImpl[] _unionLabels;
   private TypeCodeImpl _discriminator;
   private int _defaultIndex;
   private int _length;
   private TypeCodeImpl _contentType;
   private short _digits;
   private short _scale;
   private short _type_modifier;
   private TypeCodeImpl _concrete_base;
   private short[] _memberAccess;
   private TypeCodeImpl _parent;
   private int _parentOffset;
   private TypeCodeImpl _indirectType;
   private byte[] outBuffer;
   private boolean cachingEnabled;
   private ORB _orb;
   private ORBUtilSystemException wrapper;

   public TypeCodeImpl(ORB var1) {
      this._kind = 0;
      this._id = "";
      this._name = "";
      this._memberCount = 0;
      this._memberNames = null;
      this._memberTypes = null;
      this._unionLabels = null;
      this._discriminator = null;
      this._defaultIndex = -1;
      this._length = 0;
      this._contentType = null;
      this._digits = 0;
      this._scale = 0;
      this._type_modifier = -1;
      this._concrete_base = null;
      this._memberAccess = null;
      this._parent = null;
      this._parentOffset = 0;
      this._indirectType = null;
      this.outBuffer = null;
      this.cachingEnabled = false;
      this._orb = var1;
      this.wrapper = ORBUtilSystemException.get(var1, "rpc.presentation");
   }

   public TypeCodeImpl(ORB var1, TypeCode var2) {
      this(var1);
      if (var2 instanceof TypeCodeImpl) {
         TypeCodeImpl var3 = (TypeCodeImpl)var2;
         if (var3._kind == -1) {
            throw this.wrapper.badRemoteTypecode();
         }

         if (var3._kind == 19 && var3._contentType == null) {
            throw this.wrapper.badRemoteTypecode();
         }
      }

      this._kind = var2.kind().value();

      try {
         label87: {
            label96: {
               int var4;
               switch(this._kind) {
               case 14:
               case 21:
               case 30:
               case 31:
               case 32:
                  break label96;
               case 17:
                  break;
               case 18:
               case 19:
               case 20:
               case 23:
               case 24:
               case 25:
               case 26:
               case 27:
               case 28:
               default:
                  break label87;
               case 29:
                  this._type_modifier = var2.type_modifier();
                  TypeCode var7 = var2.concrete_base_type();
                  if (var7 != null) {
                     this._concrete_base = convertToNative(this._orb, var7);
                  } else {
                     this._concrete_base = null;
                  }

                  this._memberAccess = new short[var2.member_count()];

                  for(var4 = 0; var4 < var2.member_count(); ++var4) {
                     this._memberAccess[var4] = var2.member_visibility(var4);
                  }
               case 15:
               case 16:
               case 22:
                  this._memberTypes = new TypeCodeImpl[var2.member_count()];

                  for(var4 = 0; var4 < var2.member_count(); ++var4) {
                     this._memberTypes[var4] = convertToNative(this._orb, var2.member_type(var4));
                     this._memberTypes[var4].setParent(this);
                  }
               }

               this._memberNames = new String[var2.member_count()];

               for(var4 = 0; var4 < var2.member_count(); ++var4) {
                  this._memberNames[var4] = var2.member_name(var4);
               }

               this._memberCount = var2.member_count();
            }

            this.setId(var2.id());
            this._name = var2.name();
         }

         switch(this._kind) {
         case 16:
            this._discriminator = convertToNative(this._orb, var2.discriminator_type());
            this._defaultIndex = var2.default_index();
            this._unionLabels = new AnyImpl[this._memberCount];

            for(int var8 = 0; var8 < this._memberCount; ++var8) {
               this._unionLabels[var8] = new AnyImpl(this._orb, var2.member_label(var8));
            }
         }

         switch(this._kind) {
         case 18:
         case 19:
         case 20:
         case 27:
            this._length = var2.length();
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         default:
            switch(this._kind) {
            case 19:
            case 20:
            case 21:
            case 30:
               this._contentType = convertToNative(this._orb, var2.content_type());
            }
         }
      } catch (Bounds var5) {
      } catch (BadKind var6) {
      }

   }

   public TypeCodeImpl(ORB var1, int var2) {
      this(var1);
      this._kind = var2;
      switch(this._kind) {
      case 14:
         this.setId("IDL:omg.org/CORBA/Object:1.0");
         this._name = "Object";
         break;
      case 18:
      case 27:
         this._length = 0;
         break;
      case 29:
         this._concrete_base = null;
      }

   }

   public TypeCodeImpl(ORB var1, int var2, String var3, String var4, StructMember[] var5) {
      this(var1);
      if (var2 == 15 || var2 == 22) {
         this._kind = var2;
         this.setId(var3);
         this._name = var4;
         this._memberCount = var5.length;
         this._memberNames = new String[this._memberCount];
         this._memberTypes = new TypeCodeImpl[this._memberCount];

         for(int var6 = 0; var6 < this._memberCount; ++var6) {
            this._memberNames[var6] = var5[var6].name;
            this._memberTypes[var6] = convertToNative(this._orb, var5[var6].type);
            this._memberTypes[var6].setParent(this);
         }
      }

   }

   public TypeCodeImpl(ORB var1, int var2, String var3, String var4, TypeCode var5, UnionMember[] var6) {
      this(var1);
      if (var2 == 16) {
         this._kind = var2;
         this.setId(var3);
         this._name = var4;
         this._memberCount = var6.length;
         this._discriminator = convertToNative(this._orb, var5);
         this._memberNames = new String[this._memberCount];
         this._memberTypes = new TypeCodeImpl[this._memberCount];
         this._unionLabels = new AnyImpl[this._memberCount];

         for(int var7 = 0; var7 < this._memberCount; ++var7) {
            this._memberNames[var7] = var6[var7].name;
            this._memberTypes[var7] = convertToNative(this._orb, var6[var7].type);
            this._memberTypes[var7].setParent(this);
            this._unionLabels[var7] = new AnyImpl(this._orb, var6[var7].label);
            if (this._unionLabels[var7].type().kind() == TCKind.tk_octet && this._unionLabels[var7].extract_octet() == 0) {
               this._defaultIndex = var7;
            }
         }
      }

   }

   public TypeCodeImpl(ORB var1, int var2, String var3, String var4, short var5, TypeCode var6, ValueMember[] var7) {
      this(var1);
      if (var2 == 29) {
         this._kind = var2;
         this.setId(var3);
         this._name = var4;
         this._type_modifier = var5;
         if (var6 != null) {
            this._concrete_base = convertToNative(this._orb, var6);
         }

         this._memberCount = var7.length;
         this._memberNames = new String[this._memberCount];
         this._memberTypes = new TypeCodeImpl[this._memberCount];
         this._memberAccess = new short[this._memberCount];

         for(int var8 = 0; var8 < this._memberCount; ++var8) {
            this._memberNames[var8] = var7[var8].name;
            this._memberTypes[var8] = convertToNative(this._orb, var7[var8].type);
            this._memberTypes[var8].setParent(this);
            this._memberAccess[var8] = var7[var8].access;
         }
      }

   }

   public TypeCodeImpl(ORB var1, int var2, String var3, String var4, String[] var5) {
      this(var1);
      if (var2 == 17) {
         this._kind = var2;
         this.setId(var3);
         this._name = var4;
         this._memberCount = var5.length;
         this._memberNames = new String[this._memberCount];

         for(int var6 = 0; var6 < this._memberCount; ++var6) {
            this._memberNames[var6] = var5[var6];
         }
      }

   }

   public TypeCodeImpl(ORB var1, int var2, String var3, String var4, TypeCode var5) {
      this(var1);
      if (var2 == 21 || var2 == 30) {
         this._kind = var2;
         this.setId(var3);
         this._name = var4;
         this._contentType = convertToNative(this._orb, var5);
      }

   }

   public TypeCodeImpl(ORB var1, int var2, String var3, String var4) {
      this(var1);
      if (var2 == 14 || var2 == 31 || var2 == 32) {
         this._kind = var2;
         this.setId(var3);
         this._name = var4;
      }

   }

   public TypeCodeImpl(ORB var1, int var2, int var3) {
      this(var1);
      if (var3 < 0) {
         throw this.wrapper.negativeBounds();
      } else {
         if (var2 == 18 || var2 == 27) {
            this._kind = var2;
            this._length = var3;
         }

      }
   }

   public TypeCodeImpl(ORB var1, int var2, int var3, TypeCode var4) {
      this(var1);
      if (var2 == 19 || var2 == 20) {
         this._kind = var2;
         this._length = var3;
         this._contentType = convertToNative(this._orb, var4);
      }

   }

   public TypeCodeImpl(ORB var1, int var2, int var3, int var4) {
      this(var1);
      if (var2 == 19) {
         this._kind = var2;
         this._length = var3;
         this._parentOffset = var4;
      }

   }

   public TypeCodeImpl(ORB var1, String var2) {
      this(var1);
      this._kind = -1;
      this._id = var2;
      this.tryIndirectType();
   }

   public TypeCodeImpl(ORB var1, int var2, short var3, short var4) {
      this(var1);
      if (var2 == 28) {
         this._kind = var2;
         this._digits = var3;
         this._scale = var4;
      }

   }

   protected static TypeCodeImpl convertToNative(ORB var0, TypeCode var1) {
      return var1 instanceof TypeCodeImpl ? (TypeCodeImpl)var1 : new TypeCodeImpl(var0, var1);
   }

   public static CDROutputStream newOutputStream(ORB var0) {
      TypeCodeOutputStream var1 = OutputStreamFactory.newTypeCodeOutputStream(var0);
      return var1;
   }

   private TypeCodeImpl indirectType() {
      this._indirectType = this.tryIndirectType();
      if (this._indirectType == null) {
         throw this.wrapper.unresolvedRecursiveTypecode();
      } else {
         return this._indirectType;
      }
   }

   private TypeCodeImpl tryIndirectType() {
      if (this._indirectType != null) {
         return this._indirectType;
      } else {
         this.setIndirectType(this._orb.getTypeCode(this._id));
         return this._indirectType;
      }
   }

   private void setIndirectType(TypeCodeImpl var1) {
      this._indirectType = var1;
      if (this._indirectType != null) {
         try {
            this._id = this._indirectType.id();
         } catch (BadKind var3) {
            throw this.wrapper.badkindCannotOccur();
         }
      }

   }

   private void setId(String var1) {
      this._id = var1;
      if (this._orb instanceof TypeCodeFactory) {
         this._orb.setTypeCode(this._id, this);
      }

   }

   private void setParent(TypeCodeImpl var1) {
      this._parent = var1;
   }

   private TypeCodeImpl getParentAtLevel(int var1) {
      if (var1 == 0) {
         return this;
      } else if (this._parent == null) {
         throw this.wrapper.unresolvedRecursiveTypecode();
      } else {
         return this._parent.getParentAtLevel(var1 - 1);
      }
   }

   private TypeCodeImpl lazy_content_type() {
      if (this._contentType == null && this._kind == 19 && this._parentOffset > 0 && this._parent != null) {
         TypeCodeImpl var1 = this.getParentAtLevel(this._parentOffset);
         if (var1 != null && var1._id != null) {
            this._contentType = new TypeCodeImpl(this._orb, var1._id);
         }
      }

      return this._contentType;
   }

   private TypeCode realType(TypeCode var1) {
      TypeCode var2 = var1;

      try {
         while(var2.kind().value() == 21) {
            var2 = var2.content_type();
         }

         return var2;
      } catch (BadKind var4) {
         throw this.wrapper.badkindCannotOccur();
      }
   }

   public final boolean equal(TypeCode var1) {
      if (var1 == this) {
         return true;
      } else {
         try {
            if (this._kind == -1) {
               if (this._id != null && var1.id() != null) {
                  return this._id.equals(var1.id());
               }

               return this._id == null && var1.id() == null;
            }

            if (this._kind != var1.kind().value()) {
               return false;
            }

            switch(typeTable[this._kind]) {
            case 0:
               return true;
            case 1:
               switch(this._kind) {
               case 18:
               case 27:
                  return this._length == var1.length();
               case 28:
                  return this._digits == var1.fixed_digits() && this._scale == var1.fixed_scale();
               default:
                  return false;
               }
            case 2:
               int var2;
               switch(this._kind) {
               case 14:
                  if (this._id.compareTo(var1.id()) == 0) {
                     return true;
                  }

                  if (this._id.compareTo(this._orb.get_primitive_tc(this._kind).id()) == 0) {
                     return true;
                  }

                  if (var1.id().compareTo(this._orb.get_primitive_tc(this._kind).id()) == 0) {
                     return true;
                  }

                  return false;
               case 15:
               case 22:
                  if (this._memberCount != var1.member_count()) {
                     return false;
                  }

                  if (this._id.compareTo(var1.id()) != 0) {
                     return false;
                  }

                  for(var2 = 0; var2 < this._memberCount; ++var2) {
                     if (!this._memberTypes[var2].equal(var1.member_type(var2))) {
                        return false;
                     }
                  }

                  return true;
               case 16:
                  if (this._memberCount != var1.member_count()) {
                     return false;
                  }

                  if (this._id.compareTo(var1.id()) != 0) {
                     return false;
                  }

                  if (this._defaultIndex != var1.default_index()) {
                     return false;
                  }

                  if (!this._discriminator.equal(var1.discriminator_type())) {
                     return false;
                  }

                  for(var2 = 0; var2 < this._memberCount; ++var2) {
                     if (!this._unionLabels[var2].equal(var1.member_label(var2))) {
                        return false;
                     }
                  }

                  for(var2 = 0; var2 < this._memberCount; ++var2) {
                     if (!this._memberTypes[var2].equal(var1.member_type(var2))) {
                        return false;
                     }
                  }

                  return true;
               case 17:
                  if (this._id.compareTo(var1.id()) != 0) {
                     return false;
                  }

                  if (this._memberCount != var1.member_count()) {
                     return false;
                  }

                  return true;
               case 18:
               case 23:
               case 24:
               case 25:
               case 26:
               case 27:
               case 28:
               default:
                  break;
               case 19:
               case 20:
                  if (this._length != var1.length()) {
                     return false;
                  }

                  if (!this.lazy_content_type().equal(var1.content_type())) {
                     return false;
                  }

                  return true;
               case 21:
               case 30:
                  if (this._id.compareTo(var1.id()) != 0) {
                     return false;
                  }

                  return this._contentType.equal(var1.content_type());
               case 29:
                  if (this._memberCount != var1.member_count()) {
                     return false;
                  }

                  if (this._id.compareTo(var1.id()) != 0) {
                     return false;
                  }

                  for(var2 = 0; var2 < this._memberCount; ++var2) {
                     if (this._memberAccess[var2] != var1.member_visibility(var2) || !this._memberTypes[var2].equal(var1.member_type(var2))) {
                        return false;
                     }
                  }

                  if (this._type_modifier == var1.type_modifier()) {
                     return false;
                  }

                  TypeCode var5 = var1.concrete_base_type();
                  if ((this._concrete_base != null || var5 == null) && (this._concrete_base == null || var5 != null) && this._concrete_base.equal(var5)) {
                     return true;
                  }

                  return false;
               case 31:
               case 32:
                  if (this._id.compareTo(var1.id()) != 0) {
                     return false;
                  }

                  return true;
               }
            }
         } catch (Bounds var3) {
         } catch (BadKind var4) {
         }

         return false;
      }
   }

   public boolean equivalent(TypeCode var1) {
      if (var1 == this) {
         return true;
      } else {
         TypeCodeImpl var2 = this._kind == -1 ? this.indirectType() : this;
         TypeCode var11 = this.realType(var2);
         TypeCode var3 = this.realType(var1);
         if (var11.kind().value() != var3.kind().value()) {
            return false;
         } else {
            String var4 = null;
            String var5 = null;

            try {
               var4 = this.id();
               var5 = var1.id();
               if (var4 != null && var5 != null) {
                  return var4.equals(var5);
               }
            } catch (BadKind var8) {
            }

            int var6 = var11.kind().value();

            try {
               if ((var6 == 15 || var6 == 16 || var6 == 17 || var6 == 22 || var6 == 29) && var11.member_count() != var3.member_count()) {
                  return false;
               } else if (var6 == 16 && var11.default_index() != var3.default_index()) {
                  return false;
               } else if ((var6 == 18 || var6 == 27 || var6 == 19 || var6 == 20) && var11.length() != var3.length()) {
                  return false;
               } else if (var6 != 28 || var11.fixed_digits() == var3.fixed_digits() && var11.fixed_scale() == var3.fixed_scale()) {
                  int var7;
                  if (var6 == 16) {
                     for(var7 = 0; var7 < var11.member_count(); ++var7) {
                        if (var11.member_label(var7) != var3.member_label(var7)) {
                           return false;
                        }
                     }

                     if (!var11.discriminator_type().equivalent(var3.discriminator_type())) {
                        return false;
                     }
                  }

                  if ((var6 == 21 || var6 == 30 || var6 == 19 || var6 == 20) && !var11.content_type().equivalent(var3.content_type())) {
                     return false;
                  } else {
                     if (var6 == 15 || var6 == 16 || var6 == 22 || var6 == 29) {
                        for(var7 = 0; var7 < var11.member_count(); ++var7) {
                           if (!var11.member_type(var7).equivalent(var3.member_type(var7))) {
                              return false;
                           }
                        }
                     }

                     return true;
                  }
               } else {
                  return false;
               }
            } catch (BadKind var9) {
               throw this.wrapper.badkindCannotOccur();
            } catch (Bounds var10) {
               throw this.wrapper.boundsCannotOccur();
            }
         }
      }
   }

   public TypeCode get_compact_typecode() {
      return this;
   }

   public TCKind kind() {
      return this._kind == -1 ? this.indirectType().kind() : TCKind.from_int(this._kind);
   }

   public boolean is_recursive() {
      return this._kind == -1;
   }

   public String id() throws BadKind {
      switch(this._kind) {
      case -1:
      case 14:
      case 15:
      case 16:
      case 17:
      case 21:
      case 22:
      case 29:
      case 30:
      case 31:
      case 32:
         return this._id;
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      case 10:
      case 11:
      case 12:
      case 13:
      case 18:
      case 19:
      case 20:
      case 23:
      case 24:
      case 25:
      case 26:
      case 27:
      case 28:
      default:
         throw new BadKind();
      }
   }

   public String name() throws BadKind {
      switch(this._kind) {
      case -1:
         return this.indirectType().name();
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      case 10:
      case 11:
      case 12:
      case 13:
      case 18:
      case 19:
      case 20:
      case 23:
      case 24:
      case 25:
      case 26:
      case 27:
      case 28:
      default:
         throw new BadKind();
      case 14:
      case 15:
      case 16:
      case 17:
      case 21:
      case 22:
      case 29:
      case 30:
      case 31:
      case 32:
         return this._name;
      }
   }

   public int member_count() throws BadKind {
      switch(this._kind) {
      case -1:
         return this.indirectType().member_count();
      case 15:
      case 16:
      case 17:
      case 22:
      case 29:
         return this._memberCount;
      default:
         throw new BadKind();
      }
   }

   public String member_name(int var1) throws BadKind, Bounds {
      switch(this._kind) {
      case -1:
         return this.indirectType().member_name(var1);
      case 15:
      case 16:
      case 17:
      case 22:
      case 29:
         try {
            return this._memberNames[var1];
         } catch (ArrayIndexOutOfBoundsException var3) {
            throw new Bounds();
         }
      default:
         throw new BadKind();
      }
   }

   public TypeCode member_type(int var1) throws BadKind, Bounds {
      switch(this._kind) {
      case -1:
         return this.indirectType().member_type(var1);
      case 15:
      case 16:
      case 22:
      case 29:
         try {
            return this._memberTypes[var1];
         } catch (ArrayIndexOutOfBoundsException var3) {
            throw new Bounds();
         }
      default:
         throw new BadKind();
      }
   }

   public Any member_label(int var1) throws BadKind, Bounds {
      switch(this._kind) {
      case -1:
         return this.indirectType().member_label(var1);
      case 16:
         try {
            return new AnyImpl(this._orb, this._unionLabels[var1]);
         } catch (ArrayIndexOutOfBoundsException var3) {
            throw new Bounds();
         }
      default:
         throw new BadKind();
      }
   }

   public TypeCode discriminator_type() throws BadKind {
      switch(this._kind) {
      case -1:
         return this.indirectType().discriminator_type();
      case 16:
         return this._discriminator;
      default:
         throw new BadKind();
      }
   }

   public int default_index() throws BadKind {
      switch(this._kind) {
      case -1:
         return this.indirectType().default_index();
      case 16:
         return this._defaultIndex;
      default:
         throw new BadKind();
      }
   }

   public int length() throws BadKind {
      switch(this._kind) {
      case -1:
         return this.indirectType().length();
      case 18:
      case 19:
      case 20:
      case 27:
         return this._length;
      default:
         throw new BadKind();
      }
   }

   public TypeCode content_type() throws BadKind {
      switch(this._kind) {
      case -1:
         return this.indirectType().content_type();
      case 19:
         return this.lazy_content_type();
      case 20:
      case 21:
      case 30:
         return this._contentType;
      default:
         throw new BadKind();
      }
   }

   public short fixed_digits() throws BadKind {
      switch(this._kind) {
      case 28:
         return this._digits;
      default:
         throw new BadKind();
      }
   }

   public short fixed_scale() throws BadKind {
      switch(this._kind) {
      case 28:
         return this._scale;
      default:
         throw new BadKind();
      }
   }

   public short member_visibility(int var1) throws BadKind, Bounds {
      switch(this._kind) {
      case -1:
         return this.indirectType().member_visibility(var1);
      case 29:
         try {
            return this._memberAccess[var1];
         } catch (ArrayIndexOutOfBoundsException var3) {
            throw new Bounds();
         }
      default:
         throw new BadKind();
      }
   }

   public short type_modifier() throws BadKind {
      switch(this._kind) {
      case -1:
         return this.indirectType().type_modifier();
      case 29:
         return this._type_modifier;
      default:
         throw new BadKind();
      }
   }

   public TypeCode concrete_base_type() throws BadKind {
      switch(this._kind) {
      case -1:
         return this.indirectType().concrete_base_type();
      case 29:
         return this._concrete_base;
      default:
         throw new BadKind();
      }
   }

   public void read_value(InputStream var1) {
      if (var1 instanceof TypeCodeReader) {
         if (this.read_value_kind((TypeCodeReader)var1)) {
            this.read_value_body(var1);
         }
      } else if (var1 instanceof CDRInputStream) {
         WrapperInputStream var2 = new WrapperInputStream((CDRInputStream)var1);
         if (this.read_value_kind((TypeCodeReader)var2)) {
            this.read_value_body(var2);
         }
      } else {
         this.read_value_kind(var1);
         this.read_value_body(var1);
      }

   }

   private void read_value_recursive(TypeCodeInputStream var1) {
      if (var1 instanceof TypeCodeReader) {
         if (this.read_value_kind((TypeCodeReader)var1)) {
            this.read_value_body(var1);
         }
      } else {
         this.read_value_kind((InputStream)var1);
         this.read_value_body(var1);
      }

   }

   boolean read_value_kind(TypeCodeReader var1) {
      this._kind = var1.read_long();
      int var2 = var1.getTopLevelPosition() - 4;
      if ((this._kind < 0 || this._kind > typeTable.length) && this._kind != -1) {
         throw this.wrapper.cannotMarshalBadTckind();
      } else if (this._kind == 31) {
         throw this.wrapper.cannotMarshalNative();
      } else {
         TypeCodeReader var3 = var1.getTopLevelStream();
         if (this._kind == -1) {
            int var4 = var1.read_long();
            if (var4 > -4) {
               throw this.wrapper.invalidIndirection(new Integer(var4));
            } else {
               int var5 = var1.getTopLevelPosition();
               int var6 = var5 - 4 + var4;
               TypeCodeImpl var7 = var3.getTypeCodeAtPosition(var6);
               if (var7 == null) {
                  throw this.wrapper.indirectionNotFound(new Integer(var6));
               } else {
                  this.setIndirectType(var7);
                  return false;
               }
            }
         } else {
            var3.addTypeCodeAtPosition(this, var2);
            return true;
         }
      }
   }

   void read_value_kind(InputStream var1) {
      this._kind = var1.read_long();
      if ((this._kind < 0 || this._kind > typeTable.length) && this._kind != -1) {
         throw this.wrapper.cannotMarshalBadTckind();
      } else if (this._kind == 31) {
         throw this.wrapper.cannotMarshalNative();
      } else if (this._kind == -1) {
         throw this.wrapper.recursiveTypecodeError();
      }
   }

   void read_value_body(InputStream var1) {
      switch(typeTable[this._kind]) {
      case 0:
      default:
         break;
      case 1:
         switch(this._kind) {
         case 18:
         case 27:
            this._length = var1.read_long();
            return;
         case 28:
            this._digits = var1.read_ushort();
            this._scale = var1.read_short();
            return;
         default:
            throw this.wrapper.invalidSimpleTypecode();
         }
      case 2:
         TypeCodeInputStream var2 = TypeCodeInputStream.readEncapsulation(var1, var1.orb());
         int var3;
         switch(this._kind) {
         case 14:
         case 32:
            this.setId(var2.read_string());
            this._name = var2.read_string();
            break;
         case 15:
         case 22:
            this.setId(var2.read_string());
            this._name = var2.read_string();
            this._memberCount = var2.read_long();
            this._memberNames = new String[this._memberCount];
            this._memberTypes = new TypeCodeImpl[this._memberCount];

            for(var3 = 0; var3 < this._memberCount; ++var3) {
               this._memberNames[var3] = var2.read_string();
               this._memberTypes[var3] = new TypeCodeImpl((ORB)var1.orb());
               this._memberTypes[var3].read_value_recursive(var2);
               this._memberTypes[var3].setParent(this);
            }

            return;
         case 16:
            this.setId(var2.read_string());
            this._name = var2.read_string();
            this._discriminator = new TypeCodeImpl((ORB)var1.orb());
            this._discriminator.read_value_recursive(var2);
            this._defaultIndex = var2.read_long();
            this._memberCount = var2.read_long();
            this._unionLabels = new AnyImpl[this._memberCount];
            this._memberNames = new String[this._memberCount];
            this._memberTypes = new TypeCodeImpl[this._memberCount];

            for(var3 = 0; var3 < this._memberCount; ++var3) {
               this._unionLabels[var3] = new AnyImpl((ORB)var1.orb());
               if (var3 == this._defaultIndex) {
                  this._unionLabels[var3].insert_octet(var2.read_octet());
               } else {
                  switch(this.realType(this._discriminator).kind().value()) {
                  case 2:
                     this._unionLabels[var3].insert_short(var2.read_short());
                     break;
                  case 3:
                     this._unionLabels[var3].insert_long(var2.read_long());
                     break;
                  case 4:
                     this._unionLabels[var3].insert_ushort(var2.read_short());
                     break;
                  case 5:
                     this._unionLabels[var3].insert_ulong(var2.read_long());
                     break;
                  case 6:
                     this._unionLabels[var3].insert_float(var2.read_float());
                     break;
                  case 7:
                     this._unionLabels[var3].insert_double(var2.read_double());
                     break;
                  case 8:
                     this._unionLabels[var3].insert_boolean(var2.read_boolean());
                     break;
                  case 9:
                     this._unionLabels[var3].insert_char(var2.read_char());
                     break;
                  case 10:
                  case 11:
                  case 12:
                  case 13:
                  case 14:
                  case 15:
                  case 16:
                  case 18:
                  case 19:
                  case 20:
                  case 21:
                  case 22:
                  case 25:
                  default:
                     throw this.wrapper.invalidComplexTypecode();
                  case 17:
                     this._unionLabels[var3].type(this._discriminator);
                     this._unionLabels[var3].insert_long(var2.read_long());
                     break;
                  case 23:
                     this._unionLabels[var3].insert_longlong(var2.read_longlong());
                     break;
                  case 24:
                     this._unionLabels[var3].insert_ulonglong(var2.read_longlong());
                     break;
                  case 26:
                     this._unionLabels[var3].insert_wchar(var2.read_wchar());
                  }
               }

               this._memberNames[var3] = var2.read_string();
               this._memberTypes[var3] = new TypeCodeImpl((ORB)var1.orb());
               this._memberTypes[var3].read_value_recursive(var2);
               this._memberTypes[var3].setParent(this);
            }

            return;
         case 17:
            this.setId(var2.read_string());
            this._name = var2.read_string();
            this._memberCount = var2.read_long();
            this._memberNames = new String[this._memberCount];

            for(var3 = 0; var3 < this._memberCount; ++var3) {
               this._memberNames[var3] = var2.read_string();
            }

            return;
         case 18:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 31:
         default:
            throw this.wrapper.invalidTypecodeKindMarshal();
         case 19:
            this._contentType = new TypeCodeImpl((ORB)var1.orb());
            this._contentType.read_value_recursive(var2);
            this._length = var2.read_long();
            break;
         case 20:
            this._contentType = new TypeCodeImpl((ORB)var1.orb());
            this._contentType.read_value_recursive(var2);
            this._length = var2.read_long();
            break;
         case 21:
         case 30:
            this.setId(var2.read_string());
            this._name = var2.read_string();
            this._contentType = new TypeCodeImpl((ORB)var1.orb());
            this._contentType.read_value_recursive(var2);
            break;
         case 29:
            this.setId(var2.read_string());
            this._name = var2.read_string();
            this._type_modifier = var2.read_short();
            this._concrete_base = new TypeCodeImpl((ORB)var1.orb());
            this._concrete_base.read_value_recursive(var2);
            if (this._concrete_base.kind().value() == 0) {
               this._concrete_base = null;
            }

            this._memberCount = var2.read_long();
            this._memberNames = new String[this._memberCount];
            this._memberTypes = new TypeCodeImpl[this._memberCount];
            this._memberAccess = new short[this._memberCount];

            for(var3 = 0; var3 < this._memberCount; ++var3) {
               this._memberNames[var3] = var2.read_string();
               this._memberTypes[var3] = new TypeCodeImpl((ORB)var1.orb());
               this._memberTypes[var3].read_value_recursive(var2);
               this._memberTypes[var3].setParent(this);
               this._memberAccess[var3] = var2.read_short();
            }
         }
      }

   }

   public void write_value(OutputStream var1) {
      if (var1 instanceof TypeCodeOutputStream) {
         this.write_value((TypeCodeOutputStream)var1);
      } else {
         TypeCodeOutputStream var2 = null;
         if (this.outBuffer == null) {
            var2 = TypeCodeOutputStream.wrapOutputStream(var1);
            this.write_value(var2);
            if (this.cachingEnabled) {
               this.outBuffer = var2.getTypeCodeBuffer();
            }
         }

         if (this.cachingEnabled && this.outBuffer != null) {
            var1.write_long(this._kind);
            var1.write_octet_array(this.outBuffer, 0, this.outBuffer.length);
         } else {
            var2.writeRawBuffer(var1, this._kind);
         }
      }

   }

   public void write_value(TypeCodeOutputStream var1) {
      if (this._kind == 31) {
         throw this.wrapper.cannotMarshalNative();
      } else {
         TypeCodeOutputStream var2 = var1.getTopLevelStream();
         int var4;
         if (this._kind == -1) {
            int var5 = var2.getPositionForID(this._id);
            var4 = var1.getTopLevelPosition();
            var1.writeIndirection(-1, var5);
         } else {
            var1.write_long(this._kind);
            var2.addIDAtPosition(this._id, var1.getTopLevelPosition() - 4);
            switch(typeTable[this._kind]) {
            case 0:
            default:
               return;
            case 1:
               switch(this._kind) {
               case 18:
               case 27:
                  var1.write_long(this._length);
                  return;
               case 28:
                  var1.write_ushort(this._digits);
                  var1.write_short(this._scale);
                  return;
               default:
                  throw this.wrapper.invalidSimpleTypecode();
               }
            case 2:
               TypeCodeOutputStream var3;
               var3 = var1.createEncapsulation(var1.orb());
               label93:
               switch(this._kind) {
               case 14:
               case 32:
                  var3.write_string(this._id);
                  var3.write_string(this._name);
                  break;
               case 15:
               case 22:
                  var3.write_string(this._id);
                  var3.write_string(this._name);
                  var3.write_long(this._memberCount);
                  var4 = 0;

                  while(true) {
                     if (var4 >= this._memberCount) {
                        break label93;
                     }

                     var3.write_string(this._memberNames[var4]);
                     this._memberTypes[var4].write_value(var3);
                     ++var4;
                  }
               case 16:
                  var3.write_string(this._id);
                  var3.write_string(this._name);
                  this._discriminator.write_value(var3);
                  var3.write_long(this._defaultIndex);
                  var3.write_long(this._memberCount);
                  var4 = 0;

                  while(true) {
                     if (var4 >= this._memberCount) {
                        break label93;
                     }

                     if (var4 == this._defaultIndex) {
                        var3.write_octet(this._unionLabels[var4].extract_octet());
                     } else {
                        switch(this.realType(this._discriminator).kind().value()) {
                        case 2:
                           var3.write_short(this._unionLabels[var4].extract_short());
                           break;
                        case 3:
                           var3.write_long(this._unionLabels[var4].extract_long());
                           break;
                        case 4:
                           var3.write_short(this._unionLabels[var4].extract_ushort());
                           break;
                        case 5:
                           var3.write_long(this._unionLabels[var4].extract_ulong());
                           break;
                        case 6:
                           var3.write_float(this._unionLabels[var4].extract_float());
                           break;
                        case 7:
                           var3.write_double(this._unionLabels[var4].extract_double());
                           break;
                        case 8:
                           var3.write_boolean(this._unionLabels[var4].extract_boolean());
                           break;
                        case 9:
                           var3.write_char(this._unionLabels[var4].extract_char());
                           break;
                        case 10:
                        case 11:
                        case 12:
                        case 13:
                        case 14:
                        case 15:
                        case 16:
                        case 18:
                        case 19:
                        case 20:
                        case 21:
                        case 22:
                        case 25:
                        default:
                           throw this.wrapper.invalidComplexTypecode();
                        case 17:
                           var3.write_long(this._unionLabels[var4].extract_long());
                           break;
                        case 23:
                           var3.write_longlong(this._unionLabels[var4].extract_longlong());
                           break;
                        case 24:
                           var3.write_longlong(this._unionLabels[var4].extract_ulonglong());
                           break;
                        case 26:
                           var3.write_wchar(this._unionLabels[var4].extract_wchar());
                        }
                     }

                     var3.write_string(this._memberNames[var4]);
                     this._memberTypes[var4].write_value(var3);
                     ++var4;
                  }
               case 17:
                  var3.write_string(this._id);
                  var3.write_string(this._name);
                  var3.write_long(this._memberCount);
                  var4 = 0;

                  while(true) {
                     if (var4 >= this._memberCount) {
                        break label93;
                     }

                     var3.write_string(this._memberNames[var4]);
                     ++var4;
                  }
               case 18:
               case 23:
               case 24:
               case 25:
               case 26:
               case 27:
               case 28:
               case 31:
               default:
                  throw this.wrapper.invalidTypecodeKindMarshal();
               case 19:
                  this.lazy_content_type().write_value(var3);
                  var3.write_long(this._length);
                  break;
               case 20:
                  this._contentType.write_value(var3);
                  var3.write_long(this._length);
                  break;
               case 21:
               case 30:
                  var3.write_string(this._id);
                  var3.write_string(this._name);
                  this._contentType.write_value(var3);
                  break;
               case 29:
                  var3.write_string(this._id);
                  var3.write_string(this._name);
                  var3.write_short(this._type_modifier);
                  if (this._concrete_base == null) {
                     this._orb.get_primitive_tc(0).write_value(var3);
                  } else {
                     this._concrete_base.write_value(var3);
                  }

                  var3.write_long(this._memberCount);

                  for(var4 = 0; var4 < this._memberCount; ++var4) {
                     var3.write_string(this._memberNames[var4]);
                     this._memberTypes[var4].write_value(var3);
                     var3.write_short(this._memberAccess[var4]);
                  }
               }

               var3.writeOctetSequenceTo(var1);
            }
         }
      }
   }

   protected void copy(org.omg.CORBA.portable.InputStream var1, org.omg.CORBA.portable.OutputStream var2) {
      String var3;
      int var4;
      int var6;
      switch(this._kind) {
      case -1:
         this.indirectType().copy(var1, var2);
      case 0:
      case 1:
      case 31:
      case 32:
         break;
      case 2:
      case 4:
         var2.write_short(var1.read_short());
         break;
      case 3:
      case 5:
         var2.write_long(var1.read_long());
         break;
      case 6:
         var2.write_float(var1.read_float());
         break;
      case 7:
         var2.write_double(var1.read_double());
         break;
      case 8:
         var2.write_boolean(var1.read_boolean());
         break;
      case 9:
         var2.write_char(var1.read_char());
         break;
      case 10:
         var2.write_octet(var1.read_octet());
         break;
      case 11:
         Any var8 = ((CDRInputStream)var1).orb().create_any();
         TypeCodeImpl var15 = new TypeCodeImpl((ORB)var2.orb());
         var15.read_value((InputStream)var1);
         var15.write_value((OutputStream)var2);
         var8.read_value(var1, var15);
         var8.write_value(var2);
         break;
      case 12:
         var2.write_TypeCode(var1.read_TypeCode());
         break;
      case 13:
         var2.write_Principal(var1.read_Principal());
         break;
      case 14:
         var2.write_Object(var1.read_Object());
         break;
      case 16:
         AnyImpl var7 = new AnyImpl((ORB)var1.orb());
         char var9;
         long var10;
         short var14;
         switch(this.realType(this._discriminator).kind().value()) {
         case 2:
            var14 = var1.read_short();
            var7.insert_short(var14);
            var2.write_short(var14);
            break;
         case 3:
            var4 = var1.read_long();
            var7.insert_long(var4);
            var2.write_long(var4);
            break;
         case 4:
            var14 = var1.read_short();
            var7.insert_ushort(var14);
            var2.write_short(var14);
            break;
         case 5:
            var4 = var1.read_long();
            var7.insert_ulong(var4);
            var2.write_long(var4);
            break;
         case 6:
            float var13 = var1.read_float();
            var7.insert_float(var13);
            var2.write_float(var13);
            break;
         case 7:
            double var12 = var1.read_double();
            var7.insert_double(var12);
            var2.write_double(var12);
            break;
         case 8:
            boolean var11 = var1.read_boolean();
            var7.insert_boolean(var11);
            var2.write_boolean(var11);
            break;
         case 9:
            var9 = var1.read_char();
            var7.insert_char(var9);
            var2.write_char(var9);
            break;
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 16:
         case 18:
         case 19:
         case 20:
         case 21:
         case 22:
         case 25:
         default:
            throw this.wrapper.illegalUnionDiscriminatorType();
         case 17:
            var4 = var1.read_long();
            var7.type(this._discriminator);
            var7.insert_long(var4);
            var2.write_long(var4);
            break;
         case 23:
            var10 = var1.read_longlong();
            var7.insert_longlong(var10);
            var2.write_longlong(var10);
            break;
         case 24:
            var10 = var1.read_longlong();
            var7.insert_ulonglong(var10);
            var2.write_longlong(var10);
            break;
         case 26:
            var9 = var1.read_wchar();
            var7.insert_wchar(var9);
            var2.write_wchar(var9);
         }

         for(var4 = 0; var4 < this._unionLabels.length; ++var4) {
            if (var7.equal(this._unionLabels[var4])) {
               this._memberTypes[var4].copy(var1, var2);
               break;
            }
         }

         if (var4 == this._unionLabels.length && this._defaultIndex != -1) {
            this._memberTypes[this._defaultIndex].copy(var1, var2);
         }
         break;
      case 17:
         var2.write_long(var1.read_long());
         break;
      case 18:
         var3 = var1.read_string();
         if (this._length != 0 && var3.length() > this._length) {
            throw this.wrapper.badStringBounds(new Integer(var3.length()), new Integer(this._length));
         }

         var2.write_string(var3);
         break;
      case 19:
         var6 = var1.read_long();
         if (this._length != 0 && var6 > this._length) {
            throw this.wrapper.badSequenceBounds(new Integer(var6), new Integer(this._length));
         }

         var2.write_long(var6);
         this.lazy_content_type();

         for(var4 = 0; var4 < var6; ++var4) {
            this._contentType.copy(var1, var2);
         }

         return;
      case 20:
         for(var4 = 0; var4 < this._length; ++var4) {
            this._contentType.copy(var1, var2);
         }

         return;
      case 21:
      case 30:
         this._contentType.copy(var1, var2);
         break;
      case 22:
         var2.write_string(var1.read_string());
      case 15:
      case 29:
         for(var6 = 0; var6 < this._memberTypes.length; ++var6) {
            this._memberTypes[var6].copy(var1, var2);
         }

         return;
      case 23:
      case 24:
         var2.write_longlong(var1.read_longlong());
         break;
      case 25:
         throw this.wrapper.tkLongDoubleNotSupported();
      case 26:
         var2.write_wchar(var1.read_wchar());
         break;
      case 27:
         var3 = var1.read_wstring();
         if (this._length != 0 && var3.length() > this._length) {
            throw this.wrapper.badStringBounds(new Integer(var3.length()), new Integer(this._length));
         }

         var2.write_wstring(var3);
         break;
      case 28:
         var2.write_ushort(var1.read_ushort());
         var2.write_short(var1.read_short());
         break;
      default:
         throw this.wrapper.invalidTypecodeKindMarshal();
      }

   }

   protected static short digits(BigDecimal var0) {
      if (var0 == null) {
         return 0;
      } else {
         short var1 = (short)var0.unscaledValue().toString().length();
         if (var0.signum() == -1) {
            --var1;
         }

         return var1;
      }
   }

   protected static short scale(BigDecimal var0) {
      return var0 == null ? 0 : (short)var0.scale();
   }

   int currentUnionMemberIndex(Any var1) throws BadKind {
      if (this._kind != 16) {
         throw new BadKind();
      } else {
         try {
            for(int var2 = 0; var2 < this.member_count(); ++var2) {
               if (this.member_label(var2).equal(var1)) {
                  return var2;
               }
            }

            if (this._defaultIndex != -1) {
               return this._defaultIndex;
            }
         } catch (BadKind var3) {
         } catch (Bounds var4) {
         }

         return -1;
      }
   }

   public String description() {
      return "TypeCodeImpl with kind " + this._kind + " and id " + this._id;
   }

   public String toString() {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream(1024);
      PrintStream var2 = new PrintStream(var1, true);
      this.printStream(var2);
      return super.toString() + " =\n" + var1.toString();
   }

   public void printStream(PrintStream var1) {
      this.printStream(var1, 0);
   }

   private void printStream(PrintStream var1, int var2) {
      if (this._kind == -1) {
         var1.print("indirect " + this._id);
      } else {
         switch(this._kind) {
         case 0:
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 23:
         case 24:
         case 25:
         case 26:
         case 31:
            var1.print(kindNames[this._kind] + " " + this._name);
            break;
         case 15:
         case 22:
         case 29:
            var1.println(kindNames[this._kind] + " " + this._name + " = {");

            for(int var3 = 0; var3 < this._memberCount; ++var3) {
               var1.print(this.indent(var2 + 1));
               if (this._memberTypes[var3] != null) {
                  this._memberTypes[var3].printStream(var1, var2 + 1);
               } else {
                  var1.print("<unknown type>");
               }

               var1.println(" " + this._memberNames[var3] + ";");
            }

            var1.print(this.indent(var2) + "}");
            break;
         case 16:
            var1.print("union " + this._name + "...");
            break;
         case 17:
            var1.print("enum " + this._name + "...");
            break;
         case 18:
            if (this._length == 0) {
               var1.print("unbounded string " + this._name);
            } else {
               var1.print("bounded string(" + this._length + ") " + this._name);
            }
            break;
         case 19:
         case 20:
            var1.println(kindNames[this._kind] + "[" + this._length + "] " + this._name + " = {");
            var1.print(this.indent(var2 + 1));
            if (this.lazy_content_type() != null) {
               this.lazy_content_type().printStream(var1, var2 + 1);
            }

            var1.println(this.indent(var2) + "}");
            break;
         case 21:
            var1.print("alias " + this._name + " = " + (this._contentType != null ? this._contentType._name : "<unresolved>"));
            break;
         case 27:
            var1.print("wstring[" + this._length + "] " + this._name);
            break;
         case 28:
            var1.print("fixed(" + this._digits + ", " + this._scale + ") " + this._name);
            break;
         case 30:
            var1.print("valueBox " + this._name + "...");
            break;
         case 32:
            var1.print("abstractInterface " + this._name + "...");
            break;
         default:
            var1.print("<unknown type>");
         }

      }
   }

   private String indent(int var1) {
      String var2 = "";

      for(int var3 = 0; var3 < var1; ++var3) {
         var2 = var2 + "  ";
      }

      return var2;
   }

   protected void setCaching(boolean var1) {
      this.cachingEnabled = var1;
      if (!var1) {
         this.outBuffer = null;
      }

   }
}
