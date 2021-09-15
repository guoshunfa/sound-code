package javax.lang.model.element;

public enum ElementKind {
   PACKAGE,
   ENUM,
   CLASS,
   ANNOTATION_TYPE,
   INTERFACE,
   ENUM_CONSTANT,
   FIELD,
   PARAMETER,
   LOCAL_VARIABLE,
   EXCEPTION_PARAMETER,
   METHOD,
   CONSTRUCTOR,
   STATIC_INIT,
   INSTANCE_INIT,
   TYPE_PARAMETER,
   OTHER,
   RESOURCE_VARIABLE;

   public boolean isClass() {
      return this == CLASS || this == ENUM;
   }

   public boolean isInterface() {
      return this == INTERFACE || this == ANNOTATION_TYPE;
   }

   public boolean isField() {
      return this == FIELD || this == ENUM_CONSTANT;
   }
}
