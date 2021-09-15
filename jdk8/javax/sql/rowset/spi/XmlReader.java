package javax.sql.rowset.spi;

import java.io.Reader;
import java.sql.SQLException;
import javax.sql.RowSetReader;
import javax.sql.rowset.WebRowSet;

public interface XmlReader extends RowSetReader {
   void readXML(WebRowSet var1, Reader var2) throws SQLException;
}
