package com.gp16.event.view;

import com.gp16.event.dao.mongodb.EventMongoDao;
import com.gp16.event.dao.mongodb.AttendeeMongoDao;
import com.gp16.event.model.Event;
import com.gp16.event.model.Attendee;
import com.gp16.event.service.EventService;
import com.gp16.event.service.AttendeeService;
import com.gp16.event.util.DatabaseConnection;
import com.gp16.event.util.DateTimeUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Vector;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// for gui enhanced appearance
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.intellijthemes.FlatArcIJTheme;
//

public class ManagementApp {
    private static EventService eventService;
    private static AttendeeService attendeeService;
    private static DefaultTableModel eventTableModel;
    private static JTable eventTable;
    private static DefaultTableModel attendeeTableModel;
    private static JTable attendeeTable;
    // --- NEW: Assignment table model and table ---
    private static DefaultTableModel assignmentTableModel;
    private static JTable assignmentTable;
    private static JLabel timeLabel;

    public static void main(String[] args) {
        var db = DatabaseConnection.connect();

        eventService = new EventService(new EventMongoDao(db));
        attendeeService = new AttendeeService(new AttendeeMongoDao(db));

        SwingUtilities.invokeLater(ManagementApp::createAndShowGUI);
    }

    private static void createAndShowGUI() {

        try {
            //UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.setLookAndFeel(new FlatDarkLaf());
            //UIManager.setLookAndFeel(new FlatArcIJTheme());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }

        JFrame frame = new JFrame("Shift Scheduler");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create top panel for timestamp
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        timeLabel = new JLabel(DateTimeUtil.getCurrentDateTime());
        timeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        topPanel.add(timeLabel);
        
        // Create timer to update time every second
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLabel.setText(DateTimeUtil.getCurrentDateTime());
            }
        });
        timer.start();

        // Add this
        Timer refreshTimer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadStores();
                loadEmployees();
                loadAssignments();
            }
        });
        refreshTimer.start();

        JTabbedPane tabbedPane = new JTabbedPane();

        // Stores Tab
        Vector<String> storeCols = new Vector<>(List.of(
            "Store ID", "Store Name", "Store Location", "Date", "Capacity"
        ));
        eventTableModel = new DefaultTableModel(storeCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        eventTable = new JTable(eventTableModel);
        eventTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane storeScroll = new JScrollPane(eventTable);
        JPanel storePanel = new JPanel(new BorderLayout());
        storePanel.add(storeScroll, BorderLayout.CENTER);

        JPanel storeBtnPanel = new JPanel();
        JButton addStoreBtn = new JButton("Add Store");
        JButton deleteStoreBtn = new JButton("Delete Store");
        JButton editStoreBtn = new JButton("Edit Store");
        deleteStoreBtn.setEnabled(false);
        editStoreBtn.setEnabled(false);

        addStoreBtn.addActionListener(e -> showAddStoreDialog());
        deleteStoreBtn.addActionListener(e -> showDeleteStoreDialog());
        editStoreBtn.addActionListener(e -> showEditStoreDialog());

        storeBtnPanel.add(addStoreBtn);
        storeBtnPanel.add(deleteStoreBtn);
        storeBtnPanel.add(editStoreBtn);
        storePanel.add(storeBtnPanel, BorderLayout.SOUTH);

        eventTable.getSelectionModel().addListSelectionListener(e -> {
            boolean selected = eventTable.getSelectedRow() != -1;
            deleteStoreBtn.setEnabled(selected);
            editStoreBtn.setEnabled(selected);
        });

        tabbedPane.addTab("Stores", storePanel);

        // Employees Tab
        Vector<String> empCols = new Vector<>(List.of(
            "Employee ID", "Name", "Position", "Full-Time", "Status"
        ));
        attendeeTableModel = new DefaultTableModel(empCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        attendeeTable = new JTable(attendeeTableModel);
        attendeeTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane empScroll = new JScrollPane(attendeeTable);
        JPanel employeePanel = new JPanel(new BorderLayout());
        employeePanel.add(empScroll, BorderLayout.CENTER);

        JPanel empBtnPanel = new JPanel();
        JButton addEmployeeBtn = new JButton("Add Employee");
        JButton deleteEmployeeBtn = new JButton("Delete Employee");
        JButton editEmployeeBtn = new JButton("Edit Employee");
        deleteEmployeeBtn.setEnabled(false);
        editEmployeeBtn.setEnabled(false);

        addEmployeeBtn.addActionListener(e -> showAddEmployeeDialog());
        deleteEmployeeBtn.addActionListener(e -> showDeleteEmployeeDialog());
        editEmployeeBtn.addActionListener(e -> showEditEmployeeDialog());

        empBtnPanel.add(addEmployeeBtn);
        empBtnPanel.add(deleteEmployeeBtn);
        empBtnPanel.add(editEmployeeBtn);
        employeePanel.add(empBtnPanel, BorderLayout.SOUTH);

        attendeeTable.getSelectionModel().addListSelectionListener(e -> {
            boolean selected = attendeeTable.getSelectedRow() != -1;
            deleteEmployeeBtn.setEnabled(selected);
            editEmployeeBtn.setEnabled(selected);
        });

        tabbedPane.addTab("Employees", employeePanel);

        // Assignments Tab
        Vector<String> assignmentCols = new Vector<>(List.of(
            "Employee Name", "Assigned Store"
        ));
        assignmentTableModel = new DefaultTableModel(assignmentCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        assignmentTable = new JTable(assignmentTableModel);
        assignmentTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane assignmentScroll = new JScrollPane(assignmentTable);
        JPanel assignmentPanel = new JPanel(new BorderLayout());
        assignmentPanel.add(assignmentScroll, BorderLayout.CENTER);

        // --- NEW: Add Assign Employees button ---
        JButton assignEmployeesBtn = new JButton("Assign Employees to Store");
        assignEmployeesBtn.addActionListener(e -> showBulkAssignDialog());
        assignmentPanel.add(assignEmployeesBtn, BorderLayout.SOUTH);

        tabbedPane.addTab("Assignments", assignmentPanel);

        // Add all components to the main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        frame.add(mainPanel);

        loadStores();
        loadEmployees();
        loadAssignments(); // --- NEW: load assignments table ---
        frame.setVisible(true);
    }

    private static void loadStores() {
        eventTableModel.setRowCount(0);
        List<Event> stores = eventService.getAllEvents();
        for (Event s : stores) {
            String dateOnly = s.getDateTime() != null
                ? s.getDateTime().toLocalDate().toString()
                : "";
            eventTableModel.addRow(new Object[]{
                s.getId(), s.getName(), s.getLocation(), dateOnly, s.getCapacity()
            });
        }
    }

    private static void loadEmployees() {
        attendeeTableModel.setRowCount(0);
        List<Attendee> employees = attendeeService.getAllAttendees();
        for (Attendee a : employees) {
            attendeeTableModel.addRow(new Object[]{
                a.getId(), a.getName(), a.getPosition(), a.isFullTime(), a.getStatus()
            });
        }
    }

    // --- NEW: Load assignments into the assignments table ---
    private static void loadAssignments() {
        assignmentTableModel.setRowCount(0);
        List<Attendee> employees = attendeeService.getAllAttendees();
        for (Attendee a : employees) {
            if (!"Unassigned".equals(a.getStatus())) {
                assignmentTableModel.addRow(new Object[]{
                    a.getName(), a.getStatus()
                });
            }
        }
    }

    private static void showBulkAssignDialog() {
        List<Attendee> employees = attendeeService.getAllAttendees();
        List<Event> stores = eventService.getAllEvents();
    
        DefaultListModel<String> empListModel = new DefaultListModel<>();
        for (Attendee a : employees) {
            empListModel.addElement(a.getId() + " - " + a.getName());
        }
        JList<String> empList = new JList<>(empListModel);
        empList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane empScroll = new JScrollPane(empList);
    
        JButton selectAllBtn = new JButton("Select All");
        JButton clearSelectionBtn = new JButton("Clear Selection");
        JPanel selectionPanel = new JPanel();
        selectionPanel.add(selectAllBtn);
        selectionPanel.add(clearSelectionBtn);
    
        selectAllBtn.addActionListener(e -> {
            int start = 0;
            int end = empListModel.size() - 1;
            if (end >= 0) {
                empList.setSelectionInterval(start, end);
            }
        });
    
        clearSelectionBtn.addActionListener(e -> empList.clearSelection());
    
        JComboBox<String> storeDropdown = new JComboBox<>();
        storeDropdown.addItem("Unassigned");  // ✅ Add Unassigned option
        for (Event store : stores) {
            storeDropdown.addItem(store.getName());
        }
    
        JLabel tipLabel = new JLabel("Hold Ctrl (Cmd on Mac) to select multiple employees");
    
        Object[] message = {
            tipLabel,
            "Select Employees:", empScroll,
            selectionPanel,
            "Assign to Store:", storeDropdown
        };
    
        int option = JOptionPane.showConfirmDialog(null, message, "Bulk Assign Employees", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String selectedStore = (String) storeDropdown.getSelectedItem();
            List<String> selectedEmployees = empList.getSelectedValuesList();
        
            if (!"Unassigned".equals(selectedStore)) {
                // Capacity check only if not assigning to "Unassigned"
                Event store = eventService.getAllEvents().stream()
                    .filter(s -> s.getName().equals(selectedStore))
                    .findFirst()
                    .orElse(null);
        
                if (store != null) {
                    int storeCapacity = store.getCapacity();
                    long currentlyAssigned = attendeeService.getAllAttendees().stream()
                        .filter(a -> selectedStore.equals(a.getStatus()))
                        .count();
        
                    if (currentlyAssigned + selectedEmployees.size() > storeCapacity) {
                        JOptionPane.showMessageDialog(null,
                            "Cannot assign employees. Store capacity exceeded!\n" +
                            "Capacity: " + storeCapacity + ", Currently Assigned: " + currentlyAssigned + ", Trying to Assign: " + selectedEmployees.size(),
                            "Capacity Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                        return; // Stop assigning if over capacity
                    }
                }
            }
        
            // Proceed with assignment if capacity is okay
            for (String selectedValue : selectedEmployees) {
                String empId = selectedValue.split(" - ")[0];
                Attendee a = employees.stream().filter(emp -> emp.getId().equals(empId)).findFirst().orElse(null);
                if (a != null) {
                    a.setStatus(selectedStore != null ? selectedStore : "Unassigned");
                    attendeeService.updateAttendee(a);
                }
            }
            loadEmployees();
            loadAssignments();
        }        
    }
    

    private static void showAddStoreDialog() {
        JTextField nameField = new JTextField();
        JTextField locationField = new JTextField();
        JTextField dateField = new JTextField();
        JComboBox<Integer> capacityBox = new JComboBox<>();
        for (int i = 2; i <= 10; i++) {
            capacityBox.addItem(i);
        }

        Object[] message = {
            "Store Name:", nameField,
            "Store Location:", locationField,
            "Date (YYYY-MM-DD):", dateField,
            "Capacity (2-10):", capacityBox
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Add Store", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                LocalDate date = LocalDate.parse(dateField.getText());
                Event e = new Event();
                e.setName(nameField.getText());
                e.setLocation(locationField.getText());
                e.setDateTime(date.atStartOfDay());
                e.setCapacity((Integer) capacityBox.getSelectedItem());
                eventService.addEvent(e);
                loadStores();
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(null, "Invalid date format. Please use YYYY-MM-DD.");
            }
        }
    }

    private static void showDeleteStoreDialog() {
        int row = eventTable.getSelectedRow();
        if (row == -1) return;
    
        String id = (String) eventTableModel.getValueAt(row, 0);
        String storeName = (String) eventTableModel.getValueAt(row, 1);
    
        int choice = JOptionPane.showConfirmDialog(null,
            "Delete selected store?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
    
        if (choice == JOptionPane.YES_OPTION) {
            // 🔥 New: Check if employees are assigned to this store
            long assignedCount = attendeeService.getAllAttendees().stream()
                .filter(attendee -> storeName.equals(attendee.getStatus()))
                .count();
    
            if (assignedCount > 0) {
                int secondChoice = JOptionPane.showConfirmDialog(null,
                    "There are employees still assigned to this store.\n" +
                    "Continuing will unassign them.\n\n" +
                    "Do you want to continue?",
                    "Employees Assigned",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
    
                if (secondChoice != JOptionPane.YES_OPTION) {
                    return; // User chose NO → cancel deletion
                }
            }
    
            // Proceed: Unassign employees and delete store
            List<Attendee> employees = attendeeService.getAllAttendees();
            for (Attendee attendee : employees) {
                if (storeName.equals(attendee.getStatus())) {
                    attendee.setStatus("Unassigned");
                    attendeeService.updateAttendee(attendee);
                }
            }
    
            eventService.deleteEvent(id);
            loadStores();
            loadEmployees();
            loadAssignments();
        }
    }
    

    private static void showAddEmployeeDialog() {
        JTextField nameField = new JTextField();
        JTextField positionField = new JTextField();
        JCheckBox fullTimeBox = new JCheckBox("Full-Time");

        JComboBox<String> storeDropdown = new JComboBox<>();
        storeDropdown.addItem("Unassigned");
        for (Event store : eventService.getAllEvents()) {
            storeDropdown.addItem(store.getName());
        }

        Object[] message = {
            "Name:", nameField,
            "Position:", positionField,
            fullTimeBox,
            "Store Assignment:", storeDropdown
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Add Employee", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            Attendee a = new Attendee();
            a.setName(nameField.getText());
            a.setPosition(positionField.getText());
            a.setFullTime(fullTimeBox.isSelected());

            String selectedStore = (String) storeDropdown.getSelectedItem();
            a.setStatus(selectedStore != null ? selectedStore : "Unassigned");

            attendeeService.addAttendee(a);
            loadEmployees();
            loadAssignments();
        }
    }

    private static void showDeleteEmployeeDialog() {
        int row = attendeeTable.getSelectedRow();
        if (row == -1) return;

        String id = (String) attendeeTableModel.getValueAt(row, 0);

        int choice = JOptionPane.showConfirmDialog(null,
            "Delete selected employee?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            attendeeService.deleteAttendee(id);
            loadEmployees();
            loadAssignments();
        }
    }

    private static void showEditStoreDialog() {
        int row = eventTable.getSelectedRow();
        if (row == -1) return;

        String id = (String) eventTableModel.getValueAt(row, 0);
        String name = (String) eventTableModel.getValueAt(row, 1);
        String location = (String) eventTableModel.getValueAt(row, 2);
        String date = (String) eventTableModel.getValueAt(row, 3);
        Integer capacity = (Integer) eventTableModel.getValueAt(row, 4);

        JTextField nameField = new JTextField(name);
        JTextField locationField = new JTextField(location);
        JTextField dateField = new JTextField(date);
        JComboBox<Integer> capacityBox = new JComboBox<>();
        for (int i = 2; i <= 10; i++) {
            capacityBox.addItem(i);
        }
        capacityBox.setSelectedItem(capacity);

        Object[] message = {
            "Store Name:", nameField,
            "Location:", locationField,
            "Date (YYYY-MM-DD):", dateField,
            "Capacity:", capacityBox
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Edit Store", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            Event e = new Event();
            e.setId(id);
            e.setName(nameField.getText());
            e.setLocation(locationField.getText());
            e.setDateTime(LocalDate.parse(dateField.getText()).atStartOfDay());
            e.setCapacity((Integer) capacityBox.getSelectedItem());

            eventService.updateEvent(e);
            loadStores();
        }
    }

    private static void showEditEmployeeDialog() {
        int row = attendeeTable.getSelectedRow();
        if (row == -1) return;
    
        String id = (String) attendeeTableModel.getValueAt(row, 0);
        String name = (String) attendeeTableModel.getValueAt(row, 1);
        String position = (String) attendeeTableModel.getValueAt(row, 2);
        boolean fullTime = (Boolean) attendeeTableModel.getValueAt(row, 3);
        String status = (String) attendeeTableModel.getValueAt(row, 4);
    
        JTextField nameField = new JTextField(name);
        JTextField positionField = new JTextField(position);
        JCheckBox fullTimeBox = new JCheckBox("Full-Time", fullTime);
    
        JComboBox<String> storeDropdown = new JComboBox<>();
        storeDropdown.addItem("Unassigned");
        for (Event store : eventService.getAllEvents()) {
            storeDropdown.addItem(store.getName());
        }
        storeDropdown.setSelectedItem(status);
    
        Object[] message = {
            "Name:", nameField,
            "Position:", positionField,
            fullTimeBox,
            "Store Assignment:", storeDropdown
        };
    
        int option = JOptionPane.showConfirmDialog(null, message, "Edit Employee", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            Attendee a = new Attendee();
            a.setId(id);
            a.setName(nameField.getText());
            a.setPosition(positionField.getText());
            a.setFullTime(fullTimeBox.isSelected());
    
            String selectedStore = (String) storeDropdown.getSelectedItem();
            a.setStatus(selectedStore != null ? selectedStore : "Unassigned");
    
            attendeeService.updateAttendee(a);
            loadEmployees();
            loadAssignments();
        }
    }
    
    
}