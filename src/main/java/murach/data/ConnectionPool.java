package murach.data;

import java.sql.*;

public class ConnectionPool {
    private static ConnectionPool pool = null;

    private static final String URL = "jdbc:mysql://localhost:3306/murach?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";         // đổi theo user MySQL của bạn
    private static final String PASSWORD = "Phanvantai@123";   // đổi theo mật khẩu MySQL

    private ConnectionPool() {
        try {
            // load driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static synchronized ConnectionPool getInstance() {
        if (pool == null) {
            pool = new ConnectionPool();
        }
        return pool;
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void freeConnection(Connection c) {
        try {
            if (c != null) c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
