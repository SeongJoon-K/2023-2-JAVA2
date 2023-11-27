package org.example;

import org.example.entity.Book;
import org.example.repository.BookRepository;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookSalesKiosk {
    private JFrame frame;
    private Map<String, Integer> bookPrices;

    private Map<String, Integer> cart;
    Map<String, JTextField> bookQuantityFields = new HashMap<>();

    private JTextArea orderSummary;

    private List<Book> allBooks; // 모든 책의 목록
    private JPanel bookPanel; // 책을 표시하는 JPanel


    public BookSalesKiosk() {
        // Initialize the frame
        frame = new JFrame("책 판매 키오스크");
        Font font = new Font("Arial", Font.BOLD, 14);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Initialize book prices and cart
        bookPrices = new HashMap<>();
        cart = new HashMap<>();
        bookPrices.put("Book1", 10000);
        bookPrices.put("Book2", 12000);
        bookPrices.put("Book3", 21000);
        bookPrices.put("Book4", 8000);
        bookPrices.put("Book5", 20000);
        bookPrices.put("Book6", 13400);
        bookPrices.put("Book7", 15500);
        bookPrices.put("Book8", 19800);
        bookPrices.put("Book9", 13300);
        bookPrices.put("Book10", 22200);
        bookPrices.put("Book11", 33300);
        bookPrices.put("Book12", 10000);


        // Create UI components
        createImageBar();
        createBookPanel();
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

    private void createBookPanel() {

        JPanel bookPanel = new JPanel(new GridLayout(0, 4, 10, 10)); // rows, cols, hgap, vgap
        bookPanel.setBorder(BorderFactory.createTitledBorder("책 선택"));
        Font labelFont = new Font("Serif", Font.BOLD, 16);
        bookPanel.setBackground(Color.WHITE); // 패널 영역 배경 색깔 선택

        for (Map.Entry<String, Integer> entry : bookPrices.entrySet()) {
            JPanel panel = new JPanel();
            panel.setBackground(Color.WHITE); // 이미지 주변 배경색상을 변경함
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            String bookTitle = entry.getKey();
            int price = entry.getValue();

//            JLabel bookLabel = new JLabel(bookTitle, JLabel.CENTER);
//            bookLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            String imageFileName = bookTitle.replace(" ", "");
            ImageIcon originalIcon = new ImageIcon("src/main/resources/" + imageFileName + ".jpeg");
            Image scaledImage = originalIcon.getImage().getScaledInstance(80, 120, Image.SCALE_SMOOTH);
            ImageIcon bookIcon = new ImageIcon(scaledImage);

            JLabel imageLabel = new JLabel(bookIcon);
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

//            JLabel titleLabel = new JLabel("ㅇㅇ", JLabel.CENTER);
//            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);


            JLabel priceLabel = new JLabel(price + "원", JLabel.CENTER);
            priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);


            JTextField quantityField = new JTextField("0", 3);
            quantityField.setHorizontalAlignment(JTextField.CENTER);
            quantityField.setMaximumSize(new Dimension(Integer.MAX_VALUE, quantityField.getPreferredSize().height));
            bookQuantityFields.put(bookTitle, quantityField);

            JButton minusButton = new JButton("-");
            minusButton.setPreferredSize(new Dimension(50, 30)); // 버튼 크기 설정

            minusButton.addActionListener(e -> updateQuantity(bookTitle, -1,bookQuantityFields.get(bookTitle)));

            JButton plusButton = new JButton("+");
            plusButton.setPreferredSize(new Dimension(50, 30)); // 버튼 크기 설정

            plusButton.addActionListener(e -> updateQuantity(bookTitle, 1,bookQuantityFields.get(bookTitle)));

            JPanel quantityPanel = new JPanel();
            quantityPanel.add(minusButton);
            quantityPanel.add(quantityField);
            quantityPanel.add(plusButton);
            quantityPanel.setBackground(Color.WHITE);

//            panel.add(bookLabel);
            panel.add(imageLabel);
            panel.add(priceLabel);
            panel.add(quantityPanel);

            bookPanel.add(panel);
        }

        JScrollPane scrollPane = new JScrollPane(bookPanel);
        frame.add(scrollPane, BorderLayout.CENTER);
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


    private void updateQuantity(String book, int delta, JTextField quantityField) {
        int quantity = Integer.parseInt(quantityField.getText()) + delta;
        quantity = Math.max(0, quantity); // Prevent negative quantities
        quantityField.setText(String.valueOf(quantity));
        cart.put(book, quantity);
        updateOrderSummary();
    }

    private void updateOrderSummary() {
        orderSummary.setText(""); // Clear previous summary
        int total = 0;
        for (String book : cart.keySet()) {
            int quantity = cart.get(book);
            if (quantity > 0) {
                int price = bookPrices.get(book);
                orderSummary.append(String.format("%s: %d권 %d원\n", book, quantity, quantity * price));
                total += quantity * price;
            }
        }
        orderSummary.append("\n총계: " + total + "원");
    }

    private void placeOrder() {
        JOptionPane.showMessageDialog(frame, orderSummary.getText() + "\n주문이 완료되었습니다.", "주문 확인", JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetOrder() {
        cart.clear();
        updateOrderSummary();
        for (Component comp : ((JPanel) frame.getContentPane().getComponent(0)).getComponents()) {
            if (comp instanceof JPanel) {
                for (Component innerComp : ((JPanel) comp).getComponents()) {
                    if (innerComp instanceof JTextField) {
                        ((JTextField) innerComp).setText("0");
                    }
                }
            }
        }
    }

    private void createImageBar() {
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
    }

    public static void main(String[] args) {

        BookRepository bookRepository = new BookRepository();
        SwingUtilities.invokeLater(BookSalesKiosk::new);
    }

}
