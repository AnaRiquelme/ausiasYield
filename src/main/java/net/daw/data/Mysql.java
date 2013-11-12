package net.daw.data;

/**
 * Clase Mysql para acceder a bases de datos Mysql. Nivel data.
 *
 * @author rafael aznar
 */
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.daw.connection.DataSourceConnection;
import net.daw.connection.DriverManagerConnection;
import net.daw.connection.GenericConnection;
import net.daw.helper.Enum;
import net.daw.helper.FilterBean;

public class Mysql implements GenericData {

    private Connection oConexionMySQL;

    @Override
    public void conexion(Enum.Connection tipo) throws Exception {
        try {
            GenericConnection oConexion;
            if (tipo == Enum.Connection.DataSource) {
                oConexion = new DataSourceConnection();

            } else {
                oConexion = new DriverManagerConnection();
            }
            oConexionMySQL = oConexion.crearConexion();
        } catch (Exception e) {
            throw new Exception("Mysql.conexion: Error al abrir la conexion:" + e.getMessage());
        }
    }

    @Override
    public void desconexion() throws Exception {
        try {
            if (oConexionMySQL != null) {
                oConexionMySQL.close();
            }
        } catch (SQLException e) {
            throw new Exception("MySQL.CerrarConexion: Error al cerrar la conexion" + e.getMessage());
        }
    }

    @Override
    public void initTrans() throws Exception {
        try {
            oConexionMySQL.setAutoCommit(false);
        } catch (SQLException e) {
            throw new Exception("Mysql.initTrans: Error al iniciar transacci�n: " + e.getMessage());
        }
    }

    @Override
    public void commitTrans() throws Exception {
        try {
            oConexionMySQL.commit();
        } catch (SQLException e) {
            throw new Exception("Mysql.commitTrans: Error en commit: " + e.getMessage());
        }
    }

    @Override
    public void rollbackTrans() throws Exception {
        try {
            oConexionMySQL.rollback();
        } catch (SQLException e) {
            throw new Exception("Mysql.rollbackTrans: Error en rollback: " + e.getMessage());
        }
    }

    @Override
    public void removeOne(int intId, String strTabla) throws Exception {
        Statement oStatement;
        try {
            oStatement = (Statement) oConexionMySQL.createStatement();
            String strSQL = "DELETE FROM " + strTabla + " WHERE id = " + intId;
            oStatement.executeUpdate(strSQL);
        } catch (SQLException e) {
            throw new Exception("mysql.deleteOne: Error al eliminar el registro: " + e.getMessage());
        }
    }

    @Override
    public int insertOne(String strTabla) throws Exception {
        ResultSet oResultSet;
        java.sql.PreparedStatement oPreparedStatement;
        int id = 0;
        try {
            String strSQL = "INSERT INTO " + strTabla + " (id) VALUES (null) ";
            oPreparedStatement = oConexionMySQL.prepareStatement(strSQL, Statement.RETURN_GENERATED_KEYS);
            int returnLastInsertId = oPreparedStatement.executeUpdate();
            if (returnLastInsertId != -1) {
                oResultSet = oPreparedStatement.getGeneratedKeys();
                oResultSet.next();
                id = oResultSet.getInt(1);
            }
            return id;
        } catch (SQLException e) {
            throw new Exception("mysql.insertOne: Error al insertar el registro: " + e.getMessage());
        }
    }

    @Override
    public void setNull(int intId, String strTabla, String strCampo) throws Exception {
        Statement oStatement;
        try {
            oStatement = (Statement) oConexionMySQL.createStatement();
            String strSQL = "UPDATE " + strTabla + " SET " + strCampo + " = null WHERE id = " + Integer.toString(intId);
            oStatement.executeUpdate(strSQL);
        } catch (SQLException e) {
            throw new Exception("mysql.setNull: Error al modificar el registro: " + e.getMessage());
        }
    }

    @Override
    public void updateOne(int intId, String strTabla, String strCampo, String strValor) throws Exception {
        Statement oStatement;
        try {
            oStatement = (Statement) oConexionMySQL.createStatement();
            String strSQL = "UPDATE " + strTabla + " SET " + strCampo + " = '" + strValor + "' WHERE id = " + Integer.toString(intId);
            oStatement.executeUpdate(strSQL);
        } catch (SQLException e) {
            throw new Exception("mysql.updateOne: Error al modificar el registro: " + e.getMessage());
        }
    }

    @Override
    public String getId(String strTabla, String strCampo, String strValor) throws Exception {
        Statement oStatement;
        ResultSet oResultSet;
        try {
            oStatement = (Statement) oConexionMySQL.createStatement();
            String strSQL = "SELECT id FROM " + strTabla + " WHERE " + strCampo + "='" + strValor + "'";
            oResultSet = oStatement.executeQuery(strSQL);
            if (oResultSet.next()) {
                return oResultSet.getString("id");
            } else {
                return null;
            }
        } catch (SQLException ex) {
            throw new Exception("mysql.getId: No se ha podido realizar la consulta: " + ex.getMessage());
        }
    }

    @Override
    public String getOne(String strTabla, String strCampo, int id) throws Exception {
        Statement oStatement;
        ResultSet oResultSet;
        try {
            oStatement = (Statement) oConexionMySQL.createStatement();
            String strSQL = "SELECT " + strCampo + " FROM " + strTabla + " WHERE id=" + Integer.toString(id);
            oResultSet = oStatement.executeQuery(strSQL);
            if (oResultSet.next()) {
                return oResultSet.getString(strCampo);
            } else {
                return null;
            }

        } catch (SQLException ex) {
            throw new Exception("mysql.getOne: No se ha podido realizar la consulta: " + ex.getMessage());
        }
    }

    @Override
    public Boolean existsOne(String strTabla, int id) throws Exception {
        int result = 0;
        Statement oStatement;
        try {
            oStatement = (Statement) oConexionMySQL.createStatement();
            String strSQL = "SELECT count(*) FROM " + strTabla + " WHERE 1=1";
            ResultSet rs = oStatement.executeQuery(strSQL);
            while (rs.next()) {
                result = rs.getInt("COUNT(*)");
            }
            return (result > 0);
        } catch (SQLException e) {
            throw new Exception("mysql.existsOne: Error en la consulta: " + e.getMessage());
        }
    }

    @Override
    public int getPages(String strTabla, int intRegsPerPage, ArrayList<FilterBean> alFilter, HashMap<String, String> hmOrder) throws Exception {
        int intResult = 0;
        Statement oStatement;
        try {
            oStatement = (Statement) oConexionMySQL.createStatement();
            String strSQL = "SELECT count(*) FROM " + strTabla + " WHERE 1=1";
            if (alFilter != null) {
                Iterator iterator = alFilter.iterator();
                while (iterator.hasNext()) {
                    FilterBean oFilterBean = (FilterBean) iterator.next();
                    switch (oFilterBean.getFilterOperator()) {
                        case "like":
                            strSQL += " AND " + oFilterBean.getFilter() + " LIKE '%" + oFilterBean.getFilterValue() + "%'";
                            break;
                        case "notlike":
                            strSQL += " AND " + oFilterBean.getFilter() + " NOT LIKE '%" + oFilterBean.getFilterValue() + "%'";
                            break;
                        case "equals":
                            strSQL += " AND " + oFilterBean.getFilter() + " = '" + oFilterBean.getFilterValue() + "'";
                            break;
                        case "notequalto":
                            strSQL += " AND " + oFilterBean.getFilter() + " <> '" + oFilterBean.getFilterValue() + "'";
                            break;
                        case "less":
                            strSQL += " AND " + oFilterBean.getFilter() + " < " + oFilterBean.getFilterValue() + "";
                            break;
                        case "lessorequal":
                            strSQL += " AND " + oFilterBean.getFilter() + " <= " + oFilterBean.getFilterValue() + "";
                            break;
                        case "greater":
                            strSQL += " AND " + oFilterBean.getFilter() + " > " + oFilterBean.getFilterValue() + "";
                            break;
                        case "greaterorequal":
                            strSQL += " AND " + oFilterBean.getFilter() + " >= " + oFilterBean.getFilterValue() + "";
                            break;
                    }

                }

            }

            if (hmOrder != null) {
                strSQL += " ORDER BY";
                for (Map.Entry oPar : hmOrder.entrySet()) {
                    strSQL += " " + oPar.getKey() + " " + oPar.getValue() + ",";
                }
                strSQL = strSQL.substring(0, strSQL.length() - 1);
            }
            ResultSet oResultSet = oStatement.executeQuery(strSQL);
            while (oResultSet.next()) {
                intResult = oResultSet.getInt("COUNT(*)") / intRegsPerPage;
                if ((oResultSet.getInt("COUNT(*)") % intRegsPerPage) > 0) {
                    intResult++;
                }
            }
            return intResult;
        } catch (SQLException e) {
            throw new Exception("mysql.getPages: Error en la consulta: " + e.getMessage());
        }
    }

    @Override
    public int getCount(String strTabla, ArrayList<FilterBean> alFilter) throws Exception {
        int intResult = 0;
        Statement oStatement;
        try {
            oStatement = (Statement) oConexionMySQL.createStatement();
            String strSQL = "SELECT count(*) FROM " + strTabla + " WHERE 1=1";
            if (alFilter != null) {
                Iterator iterator = alFilter.iterator();
                while (iterator.hasNext()) {
                    FilterBean oFilterBean = (FilterBean) iterator.next();
                    switch (oFilterBean.getFilterOperator()) {
                        case "like":
                            strSQL += " AND " + oFilterBean.getFilter() + " LIKE '%" + oFilterBean.getFilterValue() + "%'";
                            break;
                        case "notlike":
                            strSQL += " AND " + oFilterBean.getFilter() + " NOT LIKE '%" + oFilterBean.getFilterValue() + "%'";
                            break;
                        case "equals":
                            strSQL += " AND " + oFilterBean.getFilter() + " = '" + oFilterBean.getFilterValue() + "'";
                            break;
                        case "notequalto":
                            strSQL += " AND " + oFilterBean.getFilter() + " <> '" + oFilterBean.getFilterValue() + "'";
                            break;
                        case "less":
                            strSQL += " AND " + oFilterBean.getFilter() + " < " + oFilterBean.getFilterValue() + "";
                            break;
                        case "lessorequal":
                            strSQL += " AND " + oFilterBean.getFilter() + " <= " + oFilterBean.getFilterValue() + "";
                            break;
                        case "greater":
                            strSQL += " AND " + oFilterBean.getFilter() + " > " + oFilterBean.getFilterValue() + "";
                            break;
                        case "greaterorequal":
                            strSQL += " AND " + oFilterBean.getFilter() + " >= " + oFilterBean.getFilterValue() + "";
                            break;
                    }
                }
            }
            ResultSet oResultSet = oStatement.executeQuery(strSQL);
            while (oResultSet.next()) {
                intResult = oResultSet.getInt("COUNT(*)");
            }
            return intResult;
        } catch (SQLException e) {
            throw new Exception("mysql.getCount: Error en la consulta: " + e.getMessage());
        }
    }

    @Override
    public ArrayList<Integer> getPage(String strTabla, int intRegsPerPage, int intPagina, ArrayList<FilterBean> alFilter, HashMap<String, String> hmOrder) throws Exception {
        try {
            ArrayList<Integer> vector = new ArrayList<>();
            int intOffset;
            Statement oStatement;
            oStatement = (Statement) oConexionMySQL.createStatement();
            intOffset = Math.max(((intPagina - 1) * intRegsPerPage), 0);
            String strSQL = "SELECT id FROM " + strTabla + " WHERE 1=1 ";
            if (alFilter != null) {
                Iterator iterator = alFilter.iterator();
                while (iterator.hasNext()) {
                    FilterBean oFilterBean = (FilterBean) iterator.next();
                    switch (oFilterBean.getFilterOperator()) {
                        case "like":
                            strSQL += " AND " + oFilterBean.getFilter() + " LIKE '%" + oFilterBean.getFilterValue() + "%'";
                            break;
                        case "notlike":
                            strSQL += " AND " + oFilterBean.getFilter() + " NOT LIKE '%" + oFilterBean.getFilterValue() + "%'";
                            break;
                        case "equals":
                            strSQL += " AND " + oFilterBean.getFilter() + " = '" + oFilterBean.getFilterValue() + "'";
                            break;
                        case "notequalto":
                            strSQL += " AND " + oFilterBean.getFilter() + " <> '" + oFilterBean.getFilterValue() + "'";
                            break;
                        case "less":
                            strSQL += " AND " + oFilterBean.getFilter() + " < " + oFilterBean.getFilterValue() + "";
                            break;
                        case "lessorequal":
                            strSQL += " AND " + oFilterBean.getFilter() + " <= " + oFilterBean.getFilterValue() + "";
                            break;
                        case "greater":
                            strSQL += " AND " + oFilterBean.getFilter() + " > " + oFilterBean.getFilterValue() + "";
                            break;
                        case "greaterorequal":
                            strSQL += " AND " + oFilterBean.getFilter() + " >= " + oFilterBean.getFilterValue() + "";
                            break;
                    }

                }

            }
            if (hmOrder != null) {
                strSQL += " ORDER BY";
                for (Map.Entry oPar : hmOrder.entrySet()) {
                    strSQL += " " + oPar.getKey() + " " + oPar.getValue() + ",";
                }
                strSQL = strSQL.substring(0, strSQL.length() - 1);
            }
            strSQL += " LIMIT " + intOffset + " , " + intRegsPerPage;
            ResultSet oResultSet = oStatement.executeQuery(strSQL);
            while (oResultSet.next()) {
                vector.add(oResultSet.getInt("id"));
            }
            return vector;
        } catch (SQLException e) {
            throw new Exception("mysql.getPage: Error en la consulta: " + e.getMessage());
        }
    }

    @Override
    public ArrayList<String> getNeighborhood(String strLink, int intPageNumber, int intTotalPages, int intNeighborhood) throws Exception {
        ArrayList<String> vector = new ArrayList<>();
        if (intPageNumber > 0 && intTotalPages > 0) {
            if (intPageNumber > 1) {
                vector.add("<li>" + strLink + Integer.toString(intPageNumber - 1) + "\">prev</a></li>");
            }
            if (intPageNumber > intNeighborhood + 1) {
                vector.add("<li>" + strLink + "1\">1</a></li>");
            }
            if (intPageNumber > intNeighborhood + 2) {
                vector.add("<li>" + "<a href=\"#\">...</a>" + "</li>");
            }
            for (int i = intPageNumber - intNeighborhood; i <= intPageNumber + intNeighborhood; i++) {
                if (i >= 1 && i <= intTotalPages) {
                    if (intPageNumber == i) {
                        vector.add("<li class=\"active\">" + strLink + Integer.toString(i) + "\">" + i + "</a></li>");
                    } else {
                        vector.add("<li>" + strLink + Integer.toString(i) + "\">" + i + "</a></li>");
                    }
                }
            }
            if (intPageNumber < intTotalPages - (intNeighborhood + 1)) {
                vector.add("<li>" + "<a href=\"#\">...</a>" + "</li>");
            }
            if (intPageNumber < intTotalPages - (intNeighborhood)) {
                vector.add("<li>" + strLink + Integer.toString(intTotalPages) + "\">" + Integer.toString(intTotalPages) + "</a></li>");
            }
            if (intPageNumber < intTotalPages) {
                vector.add("<li>" + strLink + Integer.toString(intPageNumber + 1) + "\">next</a></li>");
            }

            String temp = vector.get(0);
            temp = "<div class=\"pagination\"><ul>" + temp;
            vector.remove(0);
            vector.add(0, temp);

            temp = vector.get(vector.size() - 1);
            temp += "</ul></div>";
            vector.remove(vector.size() - 1);
            vector.add(temp);
        }
        return vector;
    }
}
