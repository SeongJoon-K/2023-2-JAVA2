package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.entity.Book;
import org.example.entity.Order;
import org.example.repository.BookRepository;
import org.example.repository.OrderRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BookSalesKiosk {
    private JFrame frame;
    private Map<String, Integer> bookPrices;

    private Map<Book, Integer> cart;
    Map<String, JTextField> bookQuantityFields = new HashMap<>();

    private JTextArea orderSummary;

    private List<Book> allBooks; // 모든 책의 목록
    private JPanel bookPanel; // 책을 표시하는 JPanel
    private BookRepository bookRepository;
    private JScrollPane bookScrollPane;


    public BookSalesKiosk() {
        // Initialize the frame
        frame = new JFrame("책 판매 키오스크");
        Font font = new Font("Arial", Font.BOLD, 14);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        loadBooksFromDB();


        // Initialize book prices and cart
        cart = new HashMap<Book, Integer>();


        // Create UI components
        createTopBar();
        createBookPanel(allBooks);
        createOrderPanel();
        createControlPanel();


        // Finalize and show the frame
        frame.setFont(font);
        frame.setBackground(Color.GREEN);
        frame.setSize(new Dimension(800, 1000)); // 초기 창 크기 설정
        frame.pack(); // 컴포넌트에 맞춰서 최종적으로 크기 조정
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void createTopBar() {
        // 상단 바를 위한 패널 생성
        JPanel topBar = new JPanel();
        topBar.setLayout(new BorderLayout());

        // 이미지 바 생성 및 상단 바에 추가
        JToolBar imageBar = createImageBar();
        topBar.add(imageBar, BorderLayout.NORTH);

        // 장르 바 생성 및 상단 바에 추가
        JPanel genreBar = createGenreBar();
        topBar.add(genreBar, BorderLayout.SOUTH);

        // 상단 바를 프레임의 NORTH 영역에 추가
        frame.add(topBar, BorderLayout.NORTH);
    }



    private void createBookPanel(List<Book> booksToShow) {
        // 새로운 bookPanel을 생성하거나 기존 bookPanel의 내용을 제거합니다.
        if (bookPanel == null) {
            bookPanel = new JPanel(new GridLayout(0, 4, 10, 10));
            bookPanel.setBorder(BorderFactory.createTitledBorder("책 선택"));
            bookPanel.setBackground(Color.WHITE);

            // JScrollPane에 bookPanel을 추가하고, frame에 scrollPane을 추가합니다.
            bookScrollPane = new JScrollPane(bookPanel);
            frame.add(bookScrollPane, BorderLayout.CENTER);
        } else {
            bookPanel.removeAll();
        }

        // 인자로 받은 책 목록을 통해 UI 컴포넌트를 생성합니다.
        for (Book book : booksToShow) {
            JPanel panel = createBookDisplayPanel(book);
            bookPanel.add(panel);
        }

        // UI를 갱신합니다.
        bookPanel.revalidate();
        bookPanel.repaint();
    }

    private JPanel createBookDisplayPanel(Book book) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE); // 패널 배경색 설정

        // 책 이미지
        ImageIcon bookIcon = new ImageIcon(book.getBookImage());

        Image scaledImage = bookIcon.getImage().getScaledInstance(80, 120, Image.SCALE_SMOOTH);
        ImageIcon bookImage = new ImageIcon(scaledImage);

        JLabel imageLabel = new JLabel(bookImage);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 책 제목
        JLabel titleLabel = new JLabel(String.valueOf(book.getTitle()));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 책 가격
        JLabel priceLabel = new JLabel(book.getPrice() + "원");
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 수량 입력 필드
        JTextField quantityField = new JTextField("0", 3);
        quantityField.setHorizontalAlignment(JTextField.CENTER);
        quantityField.setMaximumSize(new Dimension(Integer.MAX_VALUE, quantityField.getPreferredSize().height));
        bookQuantityFields.put(String.valueOf(book.getTitle()), quantityField);

        // +/- 버튼
        JButton minusButton = new JButton("-");
        minusButton.setPreferredSize(new Dimension(50, 30));
        minusButton.addActionListener(e -> updateQuantity(book, -1, quantityField));

        JButton plusButton = new JButton("+");
        plusButton.setPreferredSize(new Dimension(50, 30));
        plusButton.addActionListener(e -> updateQuantity(book, 1, quantityField));

        // 수량 조절 패널
        JPanel quantityPanel = new JPanel();
        quantityPanel.add(minusButton);
        quantityPanel.add(quantityField);
        quantityPanel.add(plusButton);
        quantityPanel.setBackground(Color.WHITE);

        // 패널에 컴포넌트 추가
        panel.add(imageLabel);
        panel.add(titleLabel);
        panel.add(priceLabel);
        panel.add(quantityPanel);

        return panel;
    }


    private JPanel createGenreBar() {
        JPanel genreBar = new JPanel();
        genreBar.setLayout(new FlowLayout(FlowLayout.LEFT));
        String[] genres = {"All", "소설", "자기계발", "전공"}; // 장르 목록

        for (String genre : genres) {
            JButton genreButton = new JButton(genre);
            genreButton.addActionListener(e -> filterBooksByGenre(genre));
            genreBar.add(genreButton);
        }

        return genreBar;
    }

    private void filterBooksByGenre(String genre) {
        List<Book> filteredBooks;
        if ("All".equals(genre)) {
            filteredBooks = new ArrayList<>(allBooks); // "All"이 선택되면 모든 책을 표시
        } else {
            filteredBooks = allBooks.stream()
                    .filter(book -> book.getGenre().equalsIgnoreCase(genre))
                    .collect(Collectors.toList()); // 선택된 장르에 해당하는 책만 필터링
        }

        createBookPanel(filteredBooks); // 필터링된 책 목록으로 UI 갱신
    }


    private void createOrderPanel() {
        orderSummary = new JTextArea(8, 20);
        orderSummary.setEditable(false);
        JScrollPane orderScrollPane = new JScrollPane(orderSummary,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // 주문 요약 패널을 남쪽에 배치하지만, SOUTH에 직접 추가하는 대신 별도의 패널을 사용합니다.
        JPanel orderPanel = new JPanel(new BorderLayout());
        orderPanel.add(orderScrollPane, BorderLayout.CENTER);

        // 주문 요약과 컨트롤 버튼이 들어갈 남쪽 패널을 생성합니다.
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(orderPanel, BorderLayout.CENTER); // 주문 요약 패널을 중앙에 배치합니다.
        southPanel.add(createControlPanel(), BorderLayout.SOUTH); // 컨트롤 패널을 남쪽에 배치합니다.

        // 이제 남쪽 패널 전체를 프레임의 남쪽에 추가합니다.
        frame.add(southPanel, BorderLayout.SOUTH);
    }



    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        JButton orderButton = new JButton("주문");
        JButton resetButton = new JButton("초기화");
        JButton closeButton = new JButton("닫기");

        orderButton.addActionListener(e -> placeOrder());
        resetButton.addActionListener(e -> resetOrder());
        closeButton.addActionListener(e -> frame.dispose());

        controlPanel.add(orderButton);
        controlPanel.add(resetButton);
        controlPanel.add(closeButton);

        return controlPanel; // JPanel을 반환합니다.
    }


    private void updateQuantity(Book book, int delta, JTextField quantityField) {
        int quantity = Integer.parseInt(quantityField.getText()) + delta;
        quantity = Math.max(0, quantity); // Prevent negative quantities
        quantityField.setText(String.valueOf(quantity));
        cart.put(book, quantity);
        updateOrderSummary();
    }


    private void updateOrderSummary() {
        orderSummary.setText(""); // Clear previous summary
        double total = 0.0;
        for (Book book : allBooks) {
            Integer quantity = cart.get(book); // Book 객체를 키로 사용
            if (quantity != null && quantity > 0) {
                double price = book.getPrice();
                orderSummary.append(String.format("%s: %d권 %,.2f원\n", book.getTitle(), quantity, price * quantity));
                total += price * quantity;
            }
        }
        orderSummary.append("\n총계: " + String.format("%d원", total));
    }


    private void placeOrder() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "주문할 책을 선택하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Order order = new Order();
        int bookCounter = 0;
        int totalPrice = 0;

        for (Map.Entry<Book, Integer> entry : cart.entrySet()) {
            Book book = entry.getKey();
            Integer quantity = entry.getValue();

            if (quantity > 0) {
                switch (bookCounter) {
                    case 0:
                        order.setFirstBookId(book.getId());
                        order.setFirstBookQuantity(quantity);
                        break;
                    case 1:
                        order.setSecondBookId(book.getId());
                        order.setSecondBookQuantity(quantity);
                        break;
                    case 2:
                        order.setThirdBookId(book.getId());
                        order.setThirdBookQuantity(quantity);
                        break;
                    case 3:
                        order.setFourthBookId(book.getId());
                        order.setFourthBookQuantity(quantity);
                        break;
                    default:
                        JOptionPane.showMessageDialog(frame, "최대 4종류의 책만 주문 가능합니다.", "오류", JOptionPane.ERROR_MESSAGE);
                        return;
                }
                totalPrice += book.getPrice() * quantity;
                bookCounter++;
            }
        }

        order.setTotalPrice(totalPrice);

        OrderRepository orderRepository = new OrderRepository();
        order = orderRepository.saveOrder(order); // 주문을 데이터베이스에 저장

        if (order != null) {
            createExcelFile(order);
            JOptionPane.showMessageDialog(frame, "주문이 완료되었습니다.\n총 주문금액 : "
                            + order.getTotalPrice() + " 원",
                            "주문 확인",
                            JOptionPane.INFORMATION_MESSAGE);
            resetOrder(); // 주문 후 카트 초기화
        } else {
            JOptionPane.showMessageDialog(frame, "주문 처리 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createExcelFile(Order order) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Order Details");

        // 헤더 행 생성
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("책 제목");
        headerRow.createCell(1).setCellValue("수량");
        headerRow.createCell(2).setCellValue("금액");

        // 주문 데이터 작성
        int rowNum = 1;
        for (Map.Entry<Book, Integer> entry : cart.entrySet()) {
            Book book = entry.getKey();
            Integer quantity = entry.getValue();

            if (quantity > 0) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(book.getTitle());
                row.createCell(1).setCellValue(quantity);
                row.createCell(2).setCellValue(book.getPrice() * quantity);
            }
        }

        // 파일로 저장
        try {
            File file = new File("Order_Details.xlsx");
            FileOutputStream fileOut = new FileOutputStream(file);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();

            // 파일 저장 확인 메시지
            JOptionPane.showMessageDialog(frame, "엑셀 파일이 생성되었습니다: " + file.getAbsolutePath(), "파일 생성", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "엑셀 파일 생성 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetOrder() {
        cart.clear();
        updateOrderSummary();

        // 모든 책의 수량 필드를 찾아 0으로 설정
        for (JTextField quantityField : bookQuantityFields.values()) {
            quantityField.setText("0");
        }
    }

    private JToolBar createImageBar() {
        // 툴바 생성
        JToolBar imageBar = new JToolBar();
        imageBar.setFloatable(false); // 툴바를 고정시킵니다.

        // 이미지 아이콘 로드
        Image image = new ImageIcon("src/main/resources/BookLogo.png").getImage();

        // 이미지 크기를 조절합니다.
        Image resizedImage = image.getScaledInstance(700, 150, Image.SCALE_SMOOTH);

        // 조절된 Image 객체를 ImageIcon으로 변환합니다.
        ImageIcon resizedIcon = new ImageIcon(resizedImage);        // 추가로 더 로드할 이미지 아이콘들...

        // 이미지 아이콘을 포함하는 라벨 또는 버튼 생성
        JLabel imageLabel1 = new JLabel(resizedIcon);
        // 추가로 더 생성할 라벨 또는 버튼...

        // 툴바에 이미지 라벨 또는 버튼 추가
        imageBar.add(imageLabel1);
        // 추가로 더 추가할 라벨 또는 버튼...

        // 툴바를 프레임의 상단에 추가
        frame.add(imageBar, BorderLayout.NORTH);

        return imageBar;
    }

    private void loadBooksFromDB() {
        BookRepository bookRepository = new BookRepository();
        allBooks = bookRepository.findAllBooks(); // DB에서 모든 책을 가져옵니다.
        bookRepository.close();

        // UI를 구성하는 bookPanel을 재구성합니다.
        createBookPanel(allBooks);
    }

    public static void main(String[] args) {

        BookRepository bookRepository = new BookRepository();
        SwingUtilities.invokeLater(BookSalesKiosk::new);
    }

}
