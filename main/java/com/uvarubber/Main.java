package com.uvarubber;

import com.uvarubber.model.Supplier;
import com.uvarubber.service.RubberService;
import com.uvarubber.dao.SupplierDAO;
import com.uvarubber.dao.CollectionDAO;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        
        SupplierDAO supplierDao = new SupplierDAO();
        CollectionDAO collectionDao = new CollectionDAO();
        RubberService rubberService = new RubberService();

        // 2. Get the list of farmers from MySQL
        List<Supplier> allFarmers = supplierDao.getAllSuppliers();

        if (allFarmers.isEmpty()) {
            System.out.println("No farmers found! Please add one to the database first.");
            return;
        }

        // 3. Simulate collecting rubber for the first farmer in your list
        Supplier selectedFarmer = allFarmers.get(0);
        double litersCollected = 45.5;
        int metrolacReading = 120;

        // 4. Use the "Brain" to calculate the values
        double drc = rubberService.calculateDRC(metrolacReading);
        double dryKg = rubberService.calculateDryWeight(litersCollected, metrolacReading);

        System.out.println("Processing for: " + selectedFarmer.getName());
        System.out.println("Calculated Dry Weight: " + dryKg + " kg");

        // 5. Save the record to the database
        // (Using today's date)
        collectionDao.saveCollection(
                selectedFarmer.getId(),
                "2026-04-04",
                litersCollected,
                metrolacReading,
                drc,
                dryKg
        );
        // 6. Launch the Graphical Interface
        // Wrap this in 'invokeLater' to ensure the UI runs smoothly on its own thread
        javax.swing.SwingUtilities.invokeLater(() -> {
            new com.uvarubber.view.LoginFrame().setVisible(true);
        });
    }
}
