# Kế hoạch triển khai màn hình chính (Home Screen)

Kế hoạch này phác thảo các bước cần thiết để thiết kế và lập trình màn hình chính (Home Screen) của ứng dụng **AI Insect Identifier Pro** dựa trên thiết kế gốc từ ứng dụng clone.

## Tổng quan màn hình chính (Home Screen)
Màn hình chính sẽ đóng vai trò là trung tâm điều hướng của ứng dụng sau khi hoàn thành Onboarding, bao gồm:
1.  **Header:** Tiêu đề "Insect / identification by photo" và nút Cài đặt (Settings).
2.  **Banner Premium:** "Try Premium" khuyến khích người dùng nâng cấp.
3.  **Hộp chức năng chính (Photo ID & Sound ID):** Hai nút lớn kế bên nhau để kích hoạt quét ảnh hoặc quét âm thanh.
4.  **Danh sách cuộn ngang côn trùng phổ biến (Most Common):** Danh mục các côn trùng thường gặp dưới dạng thẻ (Card) ngang.
5.  **Danh sách cuộn ngang côn trùng vườn (Garden Insect):** Danh mục côn trùng trong vườn dưới dạng thẻ ngang.
6.  **Thanh điều hướng dưới (Bottom Navigation):** Gồm nút Home, nút Camera nổi (Floating Action Button) chính giữa để Scan, và Collections (Lịch sử).

---

## Quyết định thiết kế đã thống nhất (Resolved Architecture Decisions)

> [!NOTE]
> Các quyết định thiết kế dưới đây đã được thống nhất làm cơ sở triển khai:

1.  **Dữ liệu danh sách côn trùng (Most Common & Garden Insect):**
    *   Sử dụng **dữ liệu tĩnh (static mock data)** lưu cục bộ thông qua `InsectRepository` để hiển thị danh sách gợi ý côn trùng trên HomeScreen. 
    *   Việc này giúp màn hình tải cực kỳ nhanh, không tiêu tốn tài nguyên API khi chỉ xem danh sách.
2.  **Kích hoạt API nhận diện (Gemini API):**
    *   Chỉ gọi API nhận diện đám mây (Gemini AI) khi người dùng chủ động tải ảnh lên hoặc chụp ảnh qua luồng quét của `ScanScreen`.
3.  **Điều hướng & Chức năng phụ:**
    *   Nút Camera FAB nổi ở giữa thanh điều hướng dưới và nút "Photo Identification" sẽ cùng kích hoạt luồng quét ảnh (`ScanScreen`).
    *   Nút "Sound ID" sẽ dẫn tới một màn hình giả lập quét âm thanh (Sound Scan Screen) chứa hiệu ứng sóng âm chuyển động để tạo trải nghiệm đầy đủ cho người dùng.
    *   Nút "Collections" sẽ điều hướng đến màn hình Lịch sử quét (`HistoryScreen`) hiện tại.

---

## Kiến thức kỹ thuật & Công nghệ sử dụng (Tech Stack & Concepts)
*   **Layout & UI:** `Scaffold`, `LazyColumn` (cuộn dọc toàn trang), `LazyRow` (cuộn ngang các thẻ côn trùng), `FloatingActionButton` (nút camera nổi bo tròn ở giữa thụt vào thanh điều hướng), `Card` (thẻ côn trùng bo góc có đổ bóng nhẹ).
*   **Navigation:** Cập nhật `Screen.kt` để đăng ký route `home`, cấu hình `NavHost` trong `MainActivity.kt` sử dụng `BottomNavigation` để chuyển đổi giữa các tab.
*   **State Management:** Tạo `HomeViewModel` quản lý danh sách côn trùng và trạng thái Premium.
*   **Image Loading:** Sử dụng thư viện `Coil` (`AsyncImage`) để load mượt mà hình ảnh côn trùng.

---

## Đề xuất cấu trúc thư mục mới
```text
app/src/main/java/com/kynv1/aiinsectidentifierpro/
│
├── data/
│   ├── model/
│   │   └── InsectShort.kt (Model mô tả thông tin rút gọn của côn trùng)
│   └── repository/
│       └── InsectRepository.kt (Cung cấp mock data côn trùng cho Home và Detail)
│
└── ui/
    ├── screens/
    │   └── home/
    │       ├── HomeScreen.kt (Giao diện màn hình Home)
    │       ├── HomeViewModel.kt (Quản lý trạng thái và dữ liệu của Home)
    │       └── components/
    │           ├── PremiumBanner.kt (Banner Try Premium)
    │           ├── QuickScanButtons.kt (Nút Photo ID và Sound ID)
    │           └── InsectCategoryRow.kt (Dòng cuộn ngang côn trùng)
    └── navigation/
        └── BottomNavigationBar.kt (Thanh điều hướng dưới tùy biến)
```

---

## Danh sách công việc chi tiết (Task Breakdown)

### Phần 1: Chuẩn bị tài nguyên & Cấu trúc dữ liệu
*   **Task 1.1:** Định nghĩa các chuỗi ngôn ngữ (Localization Strings) cho Home Screen trong `strings.xml`.
    *   *Agent:* `mobile-developer` | *Skill:* `clean-code`
    *   *INPUT:* Các văn bản thiết kế như "Photo Identification", "Sound ID", "Most common", "Garden Insect".
    *   *OUTPUT:* Các thẻ string tương ứng trong `strings.xml`.
    *   *VERIFY:* Biên dịch thành công và các id tài nguyên hoạt động.
*   **Task 1.2:** Chuẩn bị các hình ảnh côn trùng phổ biến vào thư mục `res/drawable`.
    *   *Agent:* `mobile-developer`
    *   *INPUT:* Các hình ảnh mẫu côn trùng chất lượng cao.
    *   *OUTPUT:* Ảnh lưu trữ trong `res/drawable`.
    *   *VERIFY:* Ảnh hiển thị chính xác khi gọi tài nguyên drawable.
*   **Task 1.3:** Tạo Model `InsectShort` và `InsectRepository` để quản lý dữ liệu danh sách côn trùng.
    *   *Agent:* `mobile-developer`
    *   *INPUT:* Dữ liệu côn trùng mẫu (Ash-black Slug, Black Oil Beetle, Snail, Honey Bee,...).
    *   *OUTPUT:* Lớp Repository cung cấp danh sách côn trùng.
    *   *VERIFY:* Viết unit test nhỏ hoặc gọi thử in log thấy danh sách trả về đúng.

### Phần 2: Cài đặt Navigation & Bottom Bar
*   **Task 2.1:** Cập nhật `Screen.kt` đăng ký route `home` và tab điều hướng.
    *   *Agent:* `mobile-developer`
    *   *INPUT:* Định nghĩa route.
    *   *OUTPUT:* Lớp `Screen.Home` mới.
    *   *VERIFY:* Route hoạt động trong luồng điều hướng.
*   **Task 2.2:** Thiết kế `BottomNavigationBar` tùy biến chứa nút Camera nổi chính giữa.
    *   *Agent:* `mobile-developer`
    *   *INPUT:* Thư viện Compose Material3.
    *   *OUTPUT:* Thanh điều hướng tùy biến bo góc nhẹ, nút FAB ở tâm nhô lên.
    *   *VERIFY:* Chạm vào các tab đổi màu active và thực hiện chuyển trang chính xác.

### Phần 3: Thiết kế Giao diện Home Screen
*   **Task 3.1:** Thiết kế Header và Banner Premium (`PremiumBanner`).
    *   *Agent:* `mobile-developer`
    *   *INPUT:* Thiết kế màu Nature Green và vương miện vàng.
    *   *OUTPUT:* Component Banner có hiệu ứng gradient màu xanh tự nhiên và nút bấm "Get It Now".
    *   *VERIFY:* Bố cục căn lề chính xác theo tỷ lệ màn hình.
*   **Task 3.2:** Lập trình cụm nút "Photo ID" & "Sound ID".
    *   *Agent:* `mobile-developer`
    *   *INPUT:* Layout Grid 2 cột hoặc Row cân đối.
    *   *OUTPUT:* Component chứa 2 thẻ chức năng có icon camera/mic màu tím.
    *   *VERIFY:* Click hoạt động, điều hướng chính xác sang ScanScreen.
*   **Task 3.3:** Xây dựng danh sách côn trùng cuộn ngang (`InsectCategoryRow`).
    *   *Agent:* `mobile-developer`
    *   *INPUT:* Sử dụng `LazyRow` và danh sách từ Repository.
    *   *OUTPUT:* Các card côn trùng hiển thị ảnh nền tràn viền và tên ở chân thẻ.
    *   *VERIFY:* Vuốt ngang mượt mà, bấm vào card mở đúng màn hình chi tiết côn trùng tương ứng.

---

## Kế hoạch kiểm thử & Xác minh (Verification Plan)
1.  **Kiểm tra giao diện:** Chạy ứng dụng trên thiết bị ảo để đối chiếu độ hoàn thiện layout so với ảnh mẫu thiết kế.
2.  **Kiểm tra điều hướng:**
    *   Chạm nút Camera nổi ở giữa -> Mở ScanScreen.
    *   Chạm Collections -> Mở HistoryScreen.
    *   Chạm Home -> Mở HomeScreen.
    *   Chạm một thẻ côn trùng cụ thể -> Mở DetailScreen hiển thị đúng thông tin côn trùng đó.
