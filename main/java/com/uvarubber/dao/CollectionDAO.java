package com.uvarubber.dao;

import com.uvarubber.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CollectionDAO {

    // Saves a new collection record
    public void saveCollection(int supplierId, String date, double liters, int metrolac, double drc, double dryKg) {
        String sql = "INSERT INTO daily_collections (supplier_id, collection_date, liters, metrolac_reading, drc_percentage, dry_kg) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, supplierId);
            pstmt.setString(2, date);
            pstmt.setDouble(3, liters);
            pstmt.setInt(4, metrolac);
            pstmt.setDouble(5, drc);
            pstmt.setDouble(6, dryKg);

            pstmt.executeUpdate();
            System.out.println("Data saved successfully!");

        } catch (SQLException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    // Retrieves all records for the history table
    public List<Object[]> getAllCollections() {
        List<Object[]> data = new ArrayList<>();
        String sql = "SELECT c.collection_id, c.collection_date, s.supplier_name, c.liters, c.metrolac_reading, c.dry_kg " +
                "FROM daily_collections c JOIN suppliers s ON c.supplier_id = s.supplier_id " +
                "ORDER BY c.collection_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                data.add(new Object[]{
                        rs.getInt("collection_id"), // Added ID to the data array for deletion logic
                        rs.getString("collection_date"),
                        rs.getString("supplier_name"),
                        rs.getDouble("liters"),
                        rs.getInt("metrolac_reading"),
                        rs.getDouble("dry_kg")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    // NEW: Deletes a specific record by its Primary Key
    public void deleteCollection(int id) {
        String sql = "DELETE FROM daily_collections WHERE collection_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Record ID " + id + " deleted successfully.");

        } catch (SQLException e) {
            System.out.println("Error deleting record: " + e.getMessage());
        }
    }

    public List<Object[]> getPaymentSummary(String startDate, String endDate) {
        List<Object[]> reportData = new ArrayList<>();
        String sql = "SELECT s.supplier_name, s.bank_name, s.account_no, SUM(c.dry_kg) as total_dry_kg " +
                "FROM daily_collections c " +
                "JOIN suppliers s ON c.supplier_id = s.supplier_id " +
                "WHERE c.collection_date BETWEEN ? AND ? " +
                "GROUP BY s.supplier_id, s.supplier_name, s.bank_name, s.account_no";

        try (Connection conn = com.uvarubber.util.DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                reportData.add(new Object[]{
                        rs.getString("supplier_name"),
                        rs.getString("bank_name"),
                        rs.getString("account_no"),
                        rs.getDouble("total_dry_kg")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reportData;
    }
}