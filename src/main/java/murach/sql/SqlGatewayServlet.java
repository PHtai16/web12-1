package murach.sql;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.sql.*;
import murach.data.ConnectionPool;   // dùng class quản lý pool
import jakarta.servlet.annotation.WebServlet;   // thêm import này

@WebServlet("/sqlGateway")    // ánh xạ URL cho servlet

public class SqlGatewayServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String sqlStatement = request.getParameter("sqlStatement");
        String sqlResult = "";

        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // lấy connection từ pool
            connection = pool.getConnection();
            statement = connection.createStatement();

            sqlStatement = sqlStatement.trim();
            if (sqlStatement.length() >= 6) {
                String sqlType = sqlStatement.substring(0, 6);

                if (sqlType.equalsIgnoreCase("select")) {
                    // SELECT → build table
                    resultSet = statement.executeQuery(sqlStatement);
                    sqlResult = SQLUtil.getHtmlTable(resultSet);
                } else {
                    int i = statement.executeUpdate(sqlStatement);
                    if (i == 0) { // DDL
                        sqlResult = "<p>The statement executed successfully.</p>";
                    } else { // INSERT, UPDATE, DELETE
                        sqlResult = "<p>The statement executed successfully.<br>"
                                + i + " row(s) affected.</p>";
                    }
                }
            }

        } catch (SQLException e) {
            sqlResult = "<p>Error executing the SQL statement:<br>"
                    + e.getMessage() + "</p>";
        } finally {
            // đảm bảo đóng resource
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException ignored) {}
            pool.freeConnection(connection);
        }

        // gán vào session
        HttpSession session = request.getSession();
        session.setAttribute("sqlResult", sqlResult);
        session.setAttribute("sqlStatement", sqlStatement);

        // forward về index.jsp
        String url = "/index.jsp";
        getServletContext()
                .getRequestDispatcher(url)
                .forward(request, response);
    }
}
