package com.sun.org.apache.xpath.internal.compiler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Keywords {
   private static final Map<String, Integer> m_keywords;
   private static final Map<String, Integer> m_axisnames;
   private static final Map<String, Integer> m_nodetests;
   private static final Map<String, Integer> m_nodetypes;
   private static final String FROM_ANCESTORS_STRING = "ancestor";
   private static final String FROM_ANCESTORS_OR_SELF_STRING = "ancestor-or-self";
   private static final String FROM_ATTRIBUTES_STRING = "attribute";
   private static final String FROM_CHILDREN_STRING = "child";
   private static final String FROM_DESCENDANTS_STRING = "descendant";
   private static final String FROM_DESCENDANTS_OR_SELF_STRING = "descendant-or-self";
   private static final String FROM_FOLLOWING_STRING = "following";
   private static final String FROM_FOLLOWING_SIBLINGS_STRING = "following-sibling";
   private static final String FROM_PARENT_STRING = "parent";
   private static final String FROM_PRECEDING_STRING = "preceding";
   private static final String FROM_PRECEDING_SIBLINGS_STRING = "preceding-sibling";
   private static final String FROM_SELF_STRING = "self";
   private static final String FROM_NAMESPACE_STRING = "namespace";
   private static final String FROM_SELF_ABBREVIATED_STRING = ".";
   private static final String NODETYPE_COMMENT_STRING = "comment";
   private static final String NODETYPE_TEXT_STRING = "text";
   private static final String NODETYPE_PI_STRING = "processing-instruction";
   private static final String NODETYPE_NODE_STRING = "node";
   private static final String NODETYPE_ANYELEMENT_STRING = "*";
   public static final String FUNC_CURRENT_STRING = "current";
   public static final String FUNC_LAST_STRING = "last";
   public static final String FUNC_POSITION_STRING = "position";
   public static final String FUNC_COUNT_STRING = "count";
   static final String FUNC_ID_STRING = "id";
   public static final String FUNC_KEY_STRING = "key";
   public static final String FUNC_LOCAL_PART_STRING = "local-name";
   public static final String FUNC_NAMESPACE_STRING = "namespace-uri";
   public static final String FUNC_NAME_STRING = "name";
   public static final String FUNC_GENERATE_ID_STRING = "generate-id";
   public static final String FUNC_NOT_STRING = "not";
   public static final String FUNC_TRUE_STRING = "true";
   public static final String FUNC_FALSE_STRING = "false";
   public static final String FUNC_BOOLEAN_STRING = "boolean";
   public static final String FUNC_LANG_STRING = "lang";
   public static final String FUNC_NUMBER_STRING = "number";
   public static final String FUNC_FLOOR_STRING = "floor";
   public static final String FUNC_CEILING_STRING = "ceiling";
   public static final String FUNC_ROUND_STRING = "round";
   public static final String FUNC_SUM_STRING = "sum";
   public static final String FUNC_STRING_STRING = "string";
   public static final String FUNC_STARTS_WITH_STRING = "starts-with";
   public static final String FUNC_CONTAINS_STRING = "contains";
   public static final String FUNC_SUBSTRING_BEFORE_STRING = "substring-before";
   public static final String FUNC_SUBSTRING_AFTER_STRING = "substring-after";
   public static final String FUNC_NORMALIZE_SPACE_STRING = "normalize-space";
   public static final String FUNC_TRANSLATE_STRING = "translate";
   public static final String FUNC_CONCAT_STRING = "concat";
   public static final String FUNC_SYSTEM_PROPERTY_STRING = "system-property";
   public static final String FUNC_EXT_FUNCTION_AVAILABLE_STRING = "function-available";
   public static final String FUNC_EXT_ELEM_AVAILABLE_STRING = "element-available";
   public static final String FUNC_SUBSTRING_STRING = "substring";
   public static final String FUNC_STRING_LENGTH_STRING = "string-length";
   public static final String FUNC_UNPARSED_ENTITY_URI_STRING = "unparsed-entity-uri";
   public static final String FUNC_DOCLOCATION_STRING = "document-location";

   static Integer getAxisName(String key) {
      return (Integer)m_axisnames.get(key);
   }

   static Integer lookupNodeTest(String key) {
      return (Integer)m_nodetests.get(key);
   }

   static Integer getKeyWord(String key) {
      return (Integer)m_keywords.get(key);
   }

   static Integer getNodeType(String key) {
      return (Integer)m_nodetypes.get(key);
   }

   static {
      Map<String, Integer> keywords = new HashMap();
      Map<String, Integer> axisnames = new HashMap();
      Map<String, Integer> nodetests = new HashMap();
      Map<String, Integer> nodetypes = new HashMap();
      axisnames.put("ancestor", 37);
      axisnames.put("ancestor-or-self", 38);
      axisnames.put("attribute", 39);
      axisnames.put("child", 40);
      axisnames.put("descendant", 41);
      axisnames.put("descendant-or-self", 42);
      axisnames.put("following", 43);
      axisnames.put("following-sibling", 44);
      axisnames.put("parent", 45);
      axisnames.put("preceding", 46);
      axisnames.put("preceding-sibling", 47);
      axisnames.put("self", 48);
      axisnames.put("namespace", 49);
      m_axisnames = Collections.unmodifiableMap(axisnames);
      nodetypes.put("comment", 1030);
      nodetypes.put("text", 1031);
      nodetypes.put("processing-instruction", 1032);
      nodetypes.put("node", 1033);
      nodetypes.put("*", 36);
      m_nodetypes = Collections.unmodifiableMap(nodetypes);
      keywords.put(".", 48);
      keywords.put("id", 4);
      keywords.put("key", 5);
      m_keywords = Collections.unmodifiableMap(keywords);
      nodetests.put("comment", 1030);
      nodetests.put("text", 1031);
      nodetests.put("processing-instruction", 1032);
      nodetests.put("node", 1033);
      m_nodetests = Collections.unmodifiableMap(nodetests);
   }
}
