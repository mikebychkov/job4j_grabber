package com.grabber;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

public class StorePSQL implements Store, AutoCloseable {

    Connection conn;

    @Override
    public void close() throws Exception {
        if (conn != null) {
            conn.close();
        }
    }

    public StorePSQL(Properties pr) throws SQLException {
        conn = DriverManager.getConnection(
                pr.getProperty("grabber.connection.url"),
                pr.getProperty("grabber.connection.user"),
                pr.getProperty("grabber.connection.password")
        );
        clearStore();
    }

    private void clearStore() {
        String query = "DELETE FROM posts";
        try (Statement st = conn.createStatement()) {
            st.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Post post) {
        String query = "INSERT INTO posts(name, text, link, created) VALUES(?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            Timestamp timestamp = new Timestamp(post.getCreated().getTimeInMillis());
            ps.setString(1, post.getName());
            ps.setString(2, post.getText());
            ps.setString(3, post.getLink());
            ps.setTimestamp(4, timestamp);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> rsl = new ArrayList<>();
        String query = "SELECT id, name, text, link, created FROM posts";
        try (Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(rs.getTimestamp(5).getTime());
                Post post = new Post(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        calendar
                );
                rsl.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rsl;
    }
}
