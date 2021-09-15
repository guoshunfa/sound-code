package com.sun.org.apache.xerces.internal.impl.xpath.regex;

public class CaseInsensitiveMap {
   private static int CHUNK_SHIFT = 10;
   private static int CHUNK_SIZE;
   private static int CHUNK_MASK;
   private static int INITIAL_CHUNK_COUNT;
   private static int[][][] caseInsensitiveMap;
   private static Boolean mapBuilt;
   private static int LOWER_CASE_MATCH;
   private static int UPPER_CASE_MATCH;

   public static int[] get(int codePoint) {
      if (mapBuilt == Boolean.FALSE) {
         synchronized(mapBuilt) {
            if (mapBuilt == Boolean.FALSE) {
               buildCaseInsensitiveMap();
            }
         }
      }

      return codePoint < 65536 ? getMapping(codePoint) : null;
   }

   private static int[] getMapping(int codePoint) {
      int chunk = codePoint >>> CHUNK_SHIFT;
      int offset = codePoint & CHUNK_MASK;
      return caseInsensitiveMap[chunk][offset];
   }

   private static void buildCaseInsensitiveMap() {
      caseInsensitiveMap = new int[INITIAL_CHUNK_COUNT][][];

      int lc;
      for(lc = 0; lc < INITIAL_CHUNK_COUNT; ++lc) {
         caseInsensitiveMap[lc] = new int[CHUNK_SIZE][];
      }

      for(int i = 0; i < 65536; ++i) {
         lc = Character.toLowerCase(i);
         int uc = Character.toUpperCase(i);
         if (lc != uc || lc != i) {
            int[] map = new int[2];
            int index = 0;
            int[] ucMap;
            if (lc != i) {
               map[index++] = lc;
               map[index++] = LOWER_CASE_MATCH;
               ucMap = getMapping(lc);
               if (ucMap != null) {
                  map = updateMap(i, map, lc, ucMap, LOWER_CASE_MATCH);
               }
            }

            if (uc != i) {
               if (index == map.length) {
                  map = expandMap(map, 2);
               }

               map[index++] = uc;
               map[index++] = UPPER_CASE_MATCH;
               ucMap = getMapping(uc);
               if (ucMap != null) {
                  map = updateMap(i, map, uc, ucMap, UPPER_CASE_MATCH);
               }
            }

            set(i, map);
         }
      }

      mapBuilt = Boolean.TRUE;
   }

   private static int[] expandMap(int[] srcMap, int expandBy) {
      int oldLen = srcMap.length;
      int[] newMap = new int[oldLen + expandBy];
      System.arraycopy(srcMap, 0, newMap, 0, oldLen);
      return newMap;
   }

   private static void set(int codePoint, int[] map) {
      int chunk = codePoint >>> CHUNK_SHIFT;
      int offset = codePoint & CHUNK_MASK;
      caseInsensitiveMap[chunk][offset] = map;
   }

   private static int[] updateMap(int codePoint, int[] codePointMap, int ciCodePoint, int[] ciCodePointMap, int matchType) {
      for(int i = 0; i < ciCodePointMap.length; i += 2) {
         int c = ciCodePointMap[i];
         int[] cMap = getMapping(c);
         if (cMap != null && contains(cMap, ciCodePoint, matchType)) {
            if (!contains(cMap, codePoint)) {
               cMap = expandAndAdd(cMap, codePoint, matchType);
               set(c, cMap);
            }

            if (!contains(codePointMap, c)) {
               codePointMap = expandAndAdd(codePointMap, c, matchType);
            }
         }
      }

      if (!contains(ciCodePointMap, codePoint)) {
         ciCodePointMap = expandAndAdd(ciCodePointMap, codePoint, matchType);
         set(ciCodePoint, ciCodePointMap);
      }

      return codePointMap;
   }

   private static boolean contains(int[] map, int codePoint) {
      for(int i = 0; i < map.length; i += 2) {
         if (map[i] == codePoint) {
            return true;
         }
      }

      return false;
   }

   private static boolean contains(int[] map, int codePoint, int matchType) {
      for(int i = 0; i < map.length; i += 2) {
         if (map[i] == codePoint && map[i + 1] == matchType) {
            return true;
         }
      }

      return false;
   }

   private static int[] expandAndAdd(int[] srcMap, int codePoint, int matchType) {
      int oldLen = srcMap.length;
      int[] newMap = new int[oldLen + 2];
      System.arraycopy(srcMap, 0, newMap, 0, oldLen);
      newMap[oldLen] = codePoint;
      newMap[oldLen + 1] = matchType;
      return newMap;
   }

   static {
      CHUNK_SIZE = 1 << CHUNK_SHIFT;
      CHUNK_MASK = CHUNK_SIZE - 1;
      INITIAL_CHUNK_COUNT = 64;
      mapBuilt = Boolean.FALSE;
      LOWER_CASE_MATCH = 1;
      UPPER_CASE_MATCH = 2;
   }
}
