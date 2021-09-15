package com.sun.rowset;

import java.sql.SQLException;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.FilteredRowSet;
import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.JoinRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.WebRowSet;

public final class RowSetFactoryImpl implements RowSetFactory {
   public CachedRowSet createCachedRowSet() throws SQLException {
      return new CachedRowSetImpl();
   }

   public FilteredRowSet createFilteredRowSet() throws SQLException {
      return new FilteredRowSetImpl();
   }

   public JdbcRowSet createJdbcRowSet() throws SQLException {
      return new JdbcRowSetImpl();
   }

   public JoinRowSet createJoinRowSet() throws SQLException {
      return new JoinRowSetImpl();
   }

   public WebRowSet createWebRowSet() throws SQLException {
      return new WebRowSetImpl();
   }
}
