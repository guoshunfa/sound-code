package javax.management.relation;

public interface RelationSupportMBean extends Relation {
   Boolean isInRelationService();

   void setRelationServiceManagementFlag(Boolean var1) throws IllegalArgumentException;
}
