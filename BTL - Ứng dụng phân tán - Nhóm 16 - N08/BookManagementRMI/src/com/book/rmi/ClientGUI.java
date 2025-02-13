package com.book.rmi;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class ClientGUI extends JFrame {
    private LibraryService libraryService;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JTextField isbnField, titleField, authorField, publisherField, yearField, quantityField;

    public ClientGUI() {
        setTitle("Quản lý sách");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left Panel (các nút và thông tin nhập liệu)
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

        // Input Panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Book Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        addLabelAndField(inputPanel, gbc, "ISBN:", isbnField = new JTextField(15), 0, 0);
        addLabelAndField(inputPanel, gbc, "Title:", titleField = new JTextField(15), 0, 1);
        addLabelAndField(inputPanel, gbc, "Author:", authorField = new JTextField(15), 0, 2);
        addLabelAndField(inputPanel, gbc, "Publisher:", publisherField = new JTextField(15), 0, 3);
        addLabelAndField(inputPanel, gbc, "Year:", yearField = new JTextField(15), 0, 4);
        addLabelAndField(inputPanel, gbc, "Quantity:", quantityField = new JTextField(15), 0, 5);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10)); // 3 hàng, 2 cột
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton addButton = createButton("Thêm sách", new AddBookListener());
        JButton searchButton = createButton("Tìm kiếm", new SearchBookListener());
        JButton updateButton = createButton("Cập nhật sách", new UpdateBookListener());
        JButton deleteButton = createButton("Xóa sách", new DeleteBookListener());
        JButton viewButton = createButton("Xem danh sách", new ViewBooksListener());

        buttonPanel.add(addButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewButton);

        leftPanel.add(inputPanel, BorderLayout.NORTH);
        leftPanel.add(buttonPanel, BorderLayout.CENTER);

        // Right Panel (các bảng danh sách sách)
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Book List"));

        // Table Panel
        tableModel = new DefaultTableModel();
        tableModel.addColumn("ISBN");
        tableModel.addColumn("Title");
        tableModel.addColumn("Author");
        tableModel.addColumn("Publisher");
        tableModel.addColumn("Year");
        tableModel.addColumn("Quantity");

        bookTable = new JTable(tableModel);
        bookTable.setFillsViewportHeight(true);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        bookTable.getTableHeader().setBackground(new Color(59, 89, 182)); // Chỉnh màu cột
        bookTable.getTableHeader().setForeground(Color.WHITE);
        bookTable.setFont(new Font("Arial", Font.PLAIN, 14));
        bookTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(bookTable);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        add(mainPanel, BorderLayout.CENTER);
        connectToServer();
        setVisible(true);
    }

    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, String labelText, JTextField textField, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = x + 1;
        panel.add(textField, gbc);
    }

    private JButton createButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(59, 89, 182)); // Chỉnh màu button
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25)); // Độ dài của button
        return button;
    }

    private void connectToServer() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            libraryService = (LibraryService) registry.lookup("LibraryService");
            JOptionPane.showMessageDialog(this, "Kết nối tới server thành công!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Kết nối tới server thất bại: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private class AddBookListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String isbn = isbnField.getText();
                String title = titleField.getText();
                String author = authorField.getText();
                String publisher = publisherField.getText();
                int year = Integer.parseInt(yearField.getText());
                int quantity = Integer.parseInt(quantityField.getText());

                // Kiểm tra ISBN đã tồn tại hay chưa?
                Book existingBook = libraryService.getBook(isbn);
                if (existingBook != null) {
                    JOptionPane.showMessageDialog(ClientGUI.this, "Đã tồn tại mã ISBN này!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Book book = new Book(isbn, title, author, publisher, year, quantity);
                libraryService.addBook(book);
                JOptionPane.showMessageDialog(ClientGUI.this, "Đã thêm sách thành công!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ClientGUI.this, "Lỗi không thể thêm sách: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private class SearchBookListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String isbn = isbnField.getText();
                String title = titleField.getText();
                String author = authorField.getText();
                String publisher = publisherField.getText();
                String year = yearField.getText();
                String quantity = quantityField.getText();

                List<Book> books = libraryService.searchBooks(isbn, title, author, publisher, year, quantity);
                tableModel.setRowCount(0); // Clear table
                for (Book book : books) {
                    tableModel.addRow(new Object[]{
                            book.getIsbn(),
                            book.getTitle(),
                            book.getAuthor(),
                            book.getPublisher(),
                            book.getYear(),
                            book.getQuantity()
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ClientGUI.this, "Lỗi không tìm ra sách: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private class UpdateBookListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(ClientGUI.this, "Hãy chọn sách để cập nhật!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String isbn = (String) tableModel.getValueAt(selectedRow, 0);
            String title = (String) tableModel.getValueAt(selectedRow, 1);
            String author = (String) tableModel.getValueAt(selectedRow, 2);
            String publisher = (String) tableModel.getValueAt(selectedRow, 3);
            int year = (int) tableModel.getValueAt(selectedRow, 4);
            int quantity = (int) tableModel.getValueAt(selectedRow, 5);

            // Tạo dialog khi bấm vào update
            JDialog updateDialog = new JDialog(ClientGUI.this, "Update Book", true);
            updateDialog.setLayout(new GridLayout(6, 2, 10, 10));
            updateDialog.setSize(400, 300);

            JTextField titleField = new JTextField(title);
            JTextField authorField = new JTextField(author);
            JTextField publisherField = new JTextField(publisher);
            JTextField yearField = new JTextField(String.valueOf(year));
            JTextField quantityField = new JTextField(String.valueOf(quantity));

            updateDialog.add(new JLabel("Title:"));
            updateDialog.add(titleField);
            updateDialog.add(new JLabel("Author:"));
            updateDialog.add(authorField);
            updateDialog.add(new JLabel("Publisher:"));
            updateDialog.add(publisherField);
            updateDialog.add(new JLabel("Year:"));
            updateDialog.add(yearField);
            updateDialog.add(new JLabel("Quantity:"));
            updateDialog.add(quantityField);

            JButton updateButton = new JButton("Update");
            updateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        String newTitle = titleField.getText();
                        String newAuthor = authorField.getText();
                        String newPublisher = publisherField.getText();
                        int newYear = Integer.parseInt(yearField.getText());
                        int newQuantity = Integer.parseInt(quantityField.getText());

                        libraryService.updateBook(isbn, newTitle, newAuthor, newPublisher, newYear, newQuantity);
                        JOptionPane.showMessageDialog(updateDialog, "Sách đã được thành công!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        updateDialog.dispose();
                        refreshTable();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(updateDialog, "Lỗi cập nhật sách: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            });

            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateDialog.dispose();
                }
            });

            updateDialog.add(updateButton);
            updateDialog.add(cancelButton);

            updateDialog.setLocationRelativeTo(ClientGUI.this);
            updateDialog.setVisible(true);
        }
    }

    private class DeleteBookListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String isbn = isbnField.getText().trim();

                // Nếu ISBN trống, kiểm tra hàng đã được chọn trong table chưa
                if (isbn.isEmpty()) {
                    int selectedRow = bookTable.getSelectedRow();
                    if (selectedRow == -1) {
                        JOptionPane.showMessageDialog(ClientGUI.this, "Hãy nhập mã ISBN hoặc chọn sách để xóa", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    } else {
                        // Lấy ISBN từ hàng được chọn trong table
                        isbn = (String) tableModel.getValueAt(selectedRow, 0);
                    }
                }

                // Xác nhận xóa
                int confirm = JOptionPane.showConfirmDialog(ClientGUI.this, "Bạn có muốn xóa sách với mã ISBN: " + isbn + "?", "OK", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    libraryService.removeBook(isbn);
                    JOptionPane.showMessageDialog(ClientGUI.this, "Sách đã được xóa thành công!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                    refreshTable();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ClientGUI.this, "Lỗi không thể xóa sách: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private class ViewBooksListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            refreshTable();
        }
    }

    private void refreshTable() {
        try {
            List<Book> books = libraryService.getAllBooks();
            tableModel.setRowCount(0); // Clear table
            for (Book book : books) {
                tableModel.addRow(new Object[]{
                        book.getIsbn(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getPublisher(),
                        book.getYear(),
                        book.getQuantity()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(ClientGUI.this, "Lỗi không thể cập nhật lại danh sách: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void clearFields() {
        isbnField.setText("");
        titleField.setText("");
        authorField.setText("");
        publisherField.setText("");
        yearField.setText("");
        quantityField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientGUI());
    }
}