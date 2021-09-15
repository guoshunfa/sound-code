package javax.management.relation;

import java.io.Serializable;
import java.util.List;

public interface RelationType extends Serializable {
   String getRelationTypeName();

   List<RoleInfo> getRoleInfos();

   RoleInfo getRoleInfo(String var1) throws IllegalArgumentException, RoleInfoNotFoundException;
}
