package org.example;

import org.example.entity.Book;
import org.example.entity.Order;
import org.example.repository.BookRepository;
import org.example.repository.OrderRepository;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookSalesKiosk {
    private JFrame frame;
    private Map<String, Integer> bookPrices;

    private Map<Book, Integer> cart;
    Map<String, JTextField> bookQuantityFields = new HashMap<>();

    private JTextArea orderSummary;

    private List<Book> allBooks; // 모든 책의 목록
    private JPanel bookPanel; // 책을 표시하는 JPanel
    private BookRepository bookRepository;


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
        bookPanel = new JPanel(new GridLayout(0, 4, 10, 10)); // 새로운 JPanel을 생성합니다.
        bookPanel.setBorder(BorderFactory.createTitledBorder("책 선택"));
        bookPanel.setBackground(Color.WHITE); // 패널 배경색 설정

        if (allBooks != null) {
            for (Book book : allBooks) {
                // 각 책 정보를 기반으로 UI 컴포넌트 생성
                JPanel panel = createBookDisplayPanel(book);
                bookPanel.add(panel);
            }
        }

        // 기존에 추가된 컴포넌트를 제거하고 새로운 bookPanel을 추가합니다.
        JScrollPane scrollPane = new JScrollPane(bookPanel);
        frame.add(scrollPane, BorderLayout.CENTER);
        createOrderPanel(); // 주문 패널을 재생성합니다.
        createControlPanel(); // 컨트롤 패널을 재생성합니다.
        frame.validate(); // 프레임의 레이아웃을 갱신합니다.
        frame.repaint(); // 프레임을 다시 그립니다.
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
            JOptionPane.showMessageDialog(frame, "주문이 완료되었습니다.\n총 주문금액 : "
                            + order.getTotalPrice(),
                            "주문 확인",
                            JOptionPane.INFORMATION_MESSAGE);
            resetOrder(); // 주문 후 카트 초기화
        } else {
            JOptionPane.showMessageDialog(frame, "주문 처리 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
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

    private void loadBooksFromDB() {
        BookRepository bookRepository = new BookRepository();
        allBooks = bookRepository.findAllBooks(); // DB에서 모든 책을 가져옵니다.
        bookRepository.close();

        // UI를 구성하는 bookPanel을 재구성합니다.
        createBookPanel();
    }

    public static void main(String[] args) {

        BookRepository bookRepository = new BookRepository();
        SwingUtilities.invokeLater(BookSalesKiosk::new);
    }

}
