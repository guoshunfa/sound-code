package javax.accessibility;

public class AccessibleRelation extends AccessibleBundle {
   private Object[] target = new Object[0];
   public static final String LABEL_FOR = new String("labelFor");
   public static final String LABELED_BY = new String("labeledBy");
   public static final String MEMBER_OF = new String("memberOf");
   public static final String CONTROLLER_FOR = new String("controllerFor");
   public static final String CONTROLLED_BY = new String("controlledBy");
   public static final String FLOWS_TO = "flowsTo";
   public static final String FLOWS_FROM = "flowsFrom";
   public static final String SUBWINDOW_OF = "subwindowOf";
   public static final String PARENT_WINDOW_OF = "parentWindowOf";
   public static final String EMBEDS = "embeds";
   public static final String EMBEDDED_BY = "embeddedBy";
   public static final String CHILD_NODE_OF = "childNodeOf";
   public static final String LABEL_FOR_PROPERTY = "labelForProperty";
   public static final String LABELED_BY_PROPERTY = "labeledByProperty";
   public static final String MEMBER_OF_PROPERTY = "memberOfProperty";
   public static final String CONTROLLER_FOR_PROPERTY = "controllerForProperty";
   public static final String CONTROLLED_BY_PROPERTY = "controlledByProperty";
   public static final String FLOWS_TO_PROPERTY = "flowsToProperty";
   public static final String FLOWS_FROM_PROPERTY = "flowsFromProperty";
   public static final String SUBWINDOW_OF_PROPERTY = "subwindowOfProperty";
   public static final String PARENT_WINDOW_OF_PROPERTY = "parentWindowOfProperty";
   public static final String EMBEDS_PROPERTY = "embedsProperty";
   public static final String EMBEDDED_BY_PROPERTY = "embeddedByProperty";
   public static final String CHILD_NODE_OF_PROPERTY = "childNodeOfProperty";

   public AccessibleRelation(String var1) {
      this.key = var1;
      this.target = null;
   }

   public AccessibleRelation(String var1, Object var2) {
      this.key = var1;
      this.target = new Object[1];
      this.target[0] = var2;
   }

   public AccessibleRelation(String var1, Object[] var2) {
      this.key = var1;
      this.target = var2;
   }

   public String getKey() {
      return this.key;
   }

   public Object[] getTarget() {
      if (this.target == null) {
         this.target = new Object[0];
      }

      Object[] var1 = new Object[this.target.length];

      for(int var2 = 0; var2 < this.target.length; ++var2) {
         var1[var2] = this.target[var2];
      }

      return var1;
   }

   public void setTarget(Object var1) {
      this.target = new Object[1];
      this.target[0] = var1;
   }

   public void setTarget(Object[] var1) {
      this.target = var1;
   }
}
