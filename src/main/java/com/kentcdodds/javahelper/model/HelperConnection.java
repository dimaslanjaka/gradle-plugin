package com.kentcdodds.javahelper.model;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper object for the SQLHelper. Has several helpful methods of its own.
 *
 * @author kentcdodds
 */
public class HelperConnection {

  private String jdbcURL = null;
  private Properties connectionProperties = null;
  private Connection connection = null;
  private java.util.List<HelperQuery> queuedQueries = new java.util.ArrayList<HelperQuery>();
  private java.util.List<HelperQuery> errorQueries = new java.util.ArrayList<HelperQuery>();
  private java.util.List<HelperQuery> executedQueries = new java.util.ArrayList<HelperQuery>();

  /**
   * Empty constructor, be sure to set jdbcURL and properties map
   */
  public HelperConnection() {
  }

  /**
   * Full constructor
   *
   * @param jdbcURL
   * @param properties
   */
  public HelperConnection(String jdbcURL, Map<String, String> properties) {
    this.jdbcURL = jdbcURL;
    for (Map.Entry<String, String> entry : properties.entrySet()) {
      this.connectionProperties.put(entry.getKey(), entry.getValue());
    }
  }

  /**
   * Full constructor
   *
   * @param jdbcURL
   * @param properties
   */
  public HelperConnection(String jdbcURL, Properties properties) {
    this.jdbcURL = jdbcURL;
    this.connectionProperties = properties;
  }

  /**
   * Creates a connection on the url with the given props
   *
   * @return java.sql.Connection
   * @throws SQLException
   */
  public Connection getOrCreateConnection() throws SQLException {
    if (connection == null
            || connection.isClosed()) {
      connection = DriverManager.getConnection(jdbcURL, getConnectionProperties());
      connection.setAutoCommit(false);
    }
    return connection;
  }

  /**
   * Executes the given HelperQuery on this connection based on the HelperQuery's query and the first of the
   * QueryParameters in the HelperQuery's list
   *
   * @param helperQuery
   * @return
   * @throws SQLException
   */
  public ResultSet executeQuery(HelperQuery helperQuery) throws SQLException {
    return executeQuery(helperQuery, 0);
  }

  /**
   * Executes the given HelperQuery on this connection based on the HelperQuery's query and the given QueryParameters
   *
   * @param helperQuery
   * @return
   * @throws SQLException
   */
  public ResultSet executeQuery(HelperQuery helperQuery, QueryParameter... parameters) throws SQLException {
    PreparedStatement pstmt = prepareStatement(helperQuery.getQuery());
    fillStatement(pstmt, parameters);
    return executeQueryHelper(helperQuery, pstmt);
  }

  /**
   * Executes the given HelperQuery on this connection based on the HelperQuery's query and the pos (1st, 2nd, 3rd,
   * etc.) QueryParameters of the HelperQuery's QueryParameter list
   *
   * @param helperQuery
   * @return
   * @throws SQLException
   */
  public ResultSet executeQuery(HelperQuery helperQuery, int pos) throws SQLException {
    PreparedStatement pstmt = prepareStatement(helperQuery.getQuery());
    fillStatement(pstmt, helperQuery.getParametersList().get(pos));
    return executeQueryHelper(helperQuery, pstmt);
  }

  /**
   * Used to help the executeQuery methods (because it performs the same operations for both)
   *
   * @param helperQuery
   * @param pstmt
   * @return
   * @throws SQLException
   */
  private ResultSet executeQueryHelper(HelperQuery helperQuery, PreparedStatement pstmt) throws SQLException {
    ResultSet rs = null;
    try { //This try catch is so the query will get added to the appropriate list depending on whether the query is properly executed.
      rs = executeStatement(pstmt);
      helperQuery.setResultSet(rs);
      queuedQueries.remove(helperQuery);
      executedQueries.add(helperQuery);
    } catch (SQLException ex) {
      errorQueries.add(helperQuery);
      throw ex;
    }
    return rs;
  }

  /**
   * Executes the given statement and returns the result set
   *
   * @param pstmt
   * @return the result set of the execution
   * @throws SQLException when executing the statement
   */
  public ResultSet executeStatement(PreparedStatement pstmt) throws SQLException {
    ResultSet rs = pstmt.executeQuery();
    connection.commit();
    return rs;
  }

  /**
   * Creates a PreparedStatement from the given helperQuery's query and fills it with batches of the HelperQuery's
   * QueryParameters then executes the PreparedStatement in batch and returns the returned int[]
   *
   * @param helperQuery
   * @return the return from PreparedStatement.executeBatch();
   * @throws SQLException
   */
  public int[] executeBatchQuery(HelperQuery helperQuery) throws SQLException {
    PreparedStatement pstmt = prepareStatement(helperQuery.getQuery());
    List<QueryParameter[]> parametersList = helperQuery.getParametersList();
    for (QueryParameter[] queryParameters : parametersList) {
      fillStatement(pstmt, queryParameters);
      pstmt.addBatch();
    }
    return executeBatchStatement(pstmt);
  }

  /**
   * Executes the given statement in batch and returns the int[]
   *
   * @param pstmt
   * @return the result set of the execution
   * @throws SQLException when executing the statement
   */
  public int[] executeBatchStatement(PreparedStatement pstmt) throws SQLException {
    int[] results = pstmt.executeBatch();
    connection.commit();
    return results;
  }

  /**
   * Applies the parameters to the statement
   *
   * @param pstmt  the PreparedStatement to fill
   * @param params the parameters to fill the pstmt with
   * @throws SQLException when applying the parameters
   */
  public void fillStatement(PreparedStatement pstmt, QueryParameter... params) throws SQLException {
    for (int i = 1; i <= params.length; i++) {
      QueryParameter param = params[i - 1];
      Object value = param.getValue();
      switch (param.getType()) {
        case QueryParameter.ARRAY:
          pstmt.setArray(i, (Array) value);
          break;
        case QueryParameter.ASCIISTREAM:
          pstmt.setAsciiStream(i, (java.io.InputStream) value);
          break;
        case QueryParameter.BIGDECIMAL:
          pstmt.setBigDecimal(i, (java.math.BigDecimal) value);
          break;
        case QueryParameter.BINARYSTREAM:
          pstmt.setBinaryStream(i, (java.io.InputStream) value);
          break;
        case QueryParameter.BLOB:
          if (value instanceof Blob) {
            pstmt.setBlob(i, (Blob) value);
          } else if (value instanceof java.io.InputStream) {
            pstmt.setBlob(i, (java.io.InputStream) value);
          } else {
            throw new ClassCastException("The value of the QueryParameter of type BLOB was not a Blob or an InputStream. "
                    + "Unable to set the blob in the prepared statement.");
          }
          break;
        case QueryParameter.BOOLEAN:
          pstmt.setBoolean(i, (Boolean) value);
          break;
        case QueryParameter.BYTE:
          pstmt.setByte(i, (Byte) value);
          break;
        case QueryParameter.BYTES:
          pstmt.setBytes(i, (byte[]) value);
          break;
        case QueryParameter.CHARACTERSTREAM:
          pstmt.setCharacterStream(i, (java.io.Reader) value);
          break;
        case QueryParameter.CLOB:
          if (value instanceof Clob) {
            pstmt.setClob(i, (Clob) value);
          } else if (value instanceof java.io.Reader) {
            pstmt.setClob(i, (java.io.Reader) value);
          } else {
            throw new ClassCastException("The value of the QueryParameter of type CLOB was not a Clob or an Reader. "
                    + "Unable to set the clob in the prepared statement.");
          }
          break;
        case QueryParameter.DATE:
          pstmt.setDate(i, (java.sql.Date) value);
          break;
        case QueryParameter.DOUBLE:
          pstmt.setDouble(i, (Double) value);
          break;
        case QueryParameter.FLOAT:
          pstmt.setFloat(i, (Float) value);
          break;
        case QueryParameter.INT:
          pstmt.setInt(i, (Integer) value);
          break;
        case QueryParameter.LONG:
          pstmt.setLong(i, (Long) value);
          break;
        case QueryParameter.NCHARACTERSTREAM:
          pstmt.setNCharacterStream(i, (java.io.Reader) value);
          break;
        case QueryParameter.NCLOB:
          if (value instanceof NClob) {
            pstmt.setClob(i, (NClob) value);
          } else if (value instanceof java.io.Reader) {
            pstmt.setClob(i, (java.io.Reader) value);
          } else {
            throw new ClassCastException("The value of the QueryParameter of type NCLOB was not a NClob or an Reader. "
                    + "Unable to set the nclob in the prepared statement.");
          }
          break;
        case QueryParameter.NSTRING:
          pstmt.setNString(i, (String) value);
          break;
        case QueryParameter.OBJECT:
          pstmt.setObject(i, value);
          break;
        case QueryParameter.REF:
          pstmt.setRef(i, (Ref) value);
          break;
        case QueryParameter.ROWID:
          pstmt.setRowId(i, (java.sql.RowId) value);
          break;
        case QueryParameter.SHORT:
          pstmt.setShort(i, (Short) value);
          break;
        case QueryParameter.SQLXML:
          pstmt.setSQLXML(i, (java.sql.SQLXML) value);
          break;
        case QueryParameter.STRING:
          pstmt.setString(i, (String) value);
          break;
        case QueryParameter.TIME:
          pstmt.setTime(i, (Time) value);
          break;
        case QueryParameter.TIMESTAMP:
          pstmt.setTimestamp(i, (Timestamp) value);
          break;
      }
    }
  }

  /**
   * Gets or creates a connection and prepares a statement on it with the given query.
   *
   * @param query
   * @return
   * @throws SQLException
   */
  public PreparedStatement prepareStatement(String query) throws SQLException {
    return getOrCreateConnection().prepareStatement(query);
  }

  /**
   * Executes the queries in the queue in sequence and returns the successfully executed queries (the errored queries
   * can be found in the error list). Catches its own errors.
   *
   * @return
   */
  public java.util.List<HelperQuery> executeQueue() {
    java.util.List<HelperQuery> returnList = new java.util.ArrayList<HelperQuery>(queuedQueries.size());
    int i = 0;
    while (i < queuedQueries.size()) {
      HelperQuery helperQuery = queuedQueries.get(i);
      try {
        executeQuery(helperQuery);
        returnList.add(helperQuery);
      } catch (SQLException ex) {
        i++; //If there's a failure move forward in the queue. If not, the query that ran will no longer be in the list.
        Logger.getLogger(HelperConnection.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    return returnList;
  }

  /**
   * Adds the helperQuery to the queue
   *
   * @param helperQuery
   */
  public void addQueryToQueue(HelperQuery helperQuery) {
    queuedQueries.add(helperQuery);
  }

  /**
   * @return the jdbcURL
   */
  public String getJdbcURL() {
    return jdbcURL;
  }

  /**
   * @param jdbcURL the jdbcURL to set
   */
  public void setJdbcURL(String jdbcURL) {
    this.jdbcURL = jdbcURL;
  }

  /**
   * @return the connection
   */
  public Connection getConnection() {
    return connection;
  }

  /**
   * @param connection the connection to set
   */
  public void setConnection(Connection connection) {
    this.connection = connection;
  }

  /**
   * @return the queuedQueries
   */
  public java.util.List<HelperQuery> getQueuedQueries() {
    return queuedQueries;
  }

  /**
   * @param queuedQueries the queuedQueries to set
   */
  public void setQueuedQueries(java.util.List<HelperQuery> queuedQueries) {
    this.queuedQueries = queuedQueries;
  }

  /**
   * @return the executedQueries
   */
  public java.util.List<HelperQuery> getExecutedQueries() {
    return executedQueries;
  }

  /**
   * @param executedQueries the executedQueries to set
   */
  public void setExecutedQueries(java.util.List<HelperQuery> executedQueries) {
    this.executedQueries = executedQueries;
  }

  /**
   * @return the errorQueries
   */
  public java.util.List<HelperQuery> getErrorQueries() {
    return errorQueries;
  }

  /**
   * @param errorQueries the errorQueries to set
   */
  public void setErrorQueries(java.util.List<HelperQuery> errorQueries) {
    this.errorQueries = errorQueries;
  }

  /**
   * @return the connectionProperties
   */
  public Properties getConnectionProperties() {
    return connectionProperties;
  }

  /**
   * @param connectionProperties the connectionProperties to set
   */
  public void setConnectionProperties(Properties connectionProperties) {
    this.connectionProperties = connectionProperties;
  }
}
