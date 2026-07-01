# Kế hoạch phát triển: Insect ID - Bug Identifier Clone

Bản kế hoạch này hướng dẫn chi tiết cách thức xây dựng ứng dụng nhận diện côn trùng (côn trùng, nhện, sâu bướm,...) dựa trên ứng dụng gốc "Bug Identifier - Bug Finder" (`com.insectidentifier.insectid`) bằng Android Jetpack Compose, Room Database và Gemini 1.5 Flash API.

---

## Tổng quan dự án (Overview)

Ứng dụng cho phép người dùng chụp ảnh côn trùng từ camera hoặc chọn ảnh từ thư viện, sau đó sử dụng sức mạnh AI của mô hình **Gemini 1.5 Flash** để nhận diện loài côn trùng đó ngay lập tức. Kết quả nhận diện bao gồm các thông tin chi tiết (tên khoa học, đặc điểm, môi trường sống) và được lưu lại vào lịch sử để người dùng có thể xem lại bất cứ lúc nào.

- **Loại dự án (Project Type):** MOBILE (Android - Native Kotlin with Jetpack Compose)
- **Tác nhân chính (Primary Agent):** `mobile-developer`
- **Kỹ năng áp dụng (Skills):** `mobile-design`, `clean-code`, `plan-writing`

---

## Tiêu chí thành công (Success Criteria)

- [x] **Nhận diện chính xác:** Gửi ảnh thành công lên Gemini API và nhận lại kết quả dạng JSON được định dạng chuẩn (Tên tiếng Việt, Tên tiếng Anh, Tên khoa học, Độ chính xác, Mô tả ngắn, Đặc điểm, Môi trường sống, Mức nguy hiểm).
- [x] **Lưu trữ Lịch sử:** Room Database hoạt động ổn định, lưu lại thông tin mỗi lần quét thành công (bao gồm đường dẫn ảnh local, tên loài, thời gian quét).
- [x] **Trải nghiệm UI/UX mượt mà:** Sử dụng Jetpack Compose với phong cách thiết kế hiện đại (sleek & clean), hỗ trợ chụp ảnh nhanh, xem chi tiết và duyệt danh sách lịch sử một cách trực quan, tối ưu cho thiết bị di động.
- [x] **Xử lý lỗi tốt:** Có màn hình/trạng thái xử lý khi không có mạng, ảnh không nhận diện được côn trùng, hoặc API quá hạn mức.

---

## Công nghệ sử dụng (Tech Stack)

| Thành phần | Công nghệ lựa chọn | Lý do lựa chọn |
|---|---|---|
| **Language** | Kotlin | Ngôn ngữ phát triển Android hiện đại và an toàn. |
| **UI Framework** | Jetpack Compose | Tạo giao diện động, phản hồi nhanh và dễ tùy biến. |
| **Local Storage** | Room Database | Thư viện chuẩn Jetpack giúp quản lý cơ sở dữ liệu SQLite có cấu trúc tốt, dễ truy vấn lịch sử. |
| **Image Loading** | Coil | Thư viện tải ảnh tối ưu nhất dành cho Jetpack Compose. |
| **AI Backend** | Gemini 1.5 Flash via Google AI SDK | Nhận diện ảnh nhanh chóng, độ chính xác cao và hỗ trợ tạo schema JSON đầu ra dễ dàng mà không mất phí dịch vụ ban đầu. |
| **Camera integration** | CameraX / Android ActivityResultContracts | Hỗ trợ chụp ảnh ổn định trên nhiều thiết bị Android khác nhau. |

---

## Cấu trúc thư mục dự kiến (File Structure)

Chúng ta sẽ tổ chức mã nguồn theo mô hình Clean Architecture thu gọn (MVVM) trong package `com.kynv1.aiinsectidentifierpro`:

```text
app/src/main/java/com/kynv1/aiinsectidentifierpro/
├── data/
│   ├── local/
│   │   ├── InsectDao.kt            # Room DAO để CRUD dữ liệu lịch sử
│   │   ├── InsectDatabase.kt       # Cấu hình Room Database
│   │   └── entity/
│   │       └── InsectEntity.kt     # Thực thể bảng lưu lịch sử quét
│   ├── model/
│   │   └── InsectInfo.kt           # Data class ánh xạ kết quả từ Gemini API (JSON)
│   ├── repository/
│   │   └── InsectRepository.kt     # Quản lý luồng dữ liệu (Room & API)
│   └── remote/
│       └── GeminiServiceClient.kt  # Helper giao tiếp với Google AI Client SDK
├── ui/
│   ├── theme/
│   │   └── Theme.kt                # Custom Design Tokens (Color, Type)
│   ├── navigation/
│   │   └── Screen.kt               # Các màn hình và cấu hình điều hướng (Compose Nav)
│   ├── screens/
│   │   ├── scan/
│   │   │   ├── ScanScreen.kt       # Màn hình chính chụp/chọn ảnh & gửi scan
│   │   │   └── ScanViewModel.kt    # Quản lý trạng thái camera, call API nhận diện
│   │   ├── detail/
│   │   │   └── DetailScreen.kt     # Màn hình hiển thị chi tiết côn trùng
│   │   └── history/
│   │       ├── HistoryScreen.kt    # Màn hình danh sách lịch sử đã quét
│   │       └── HistoryViewModel.kt # ViewModel lấy dữ liệu lịch sử từ Room DB
│   └── MainActivity.kt             # Entry point thiết lập Navigation Host
```

---

## Phân rã công việc (Task Breakdown)

### 📌 Giai đoạn 1: Foundation & Setup

#### Task 1: Cấu hình Dependencies & API Client
- **Mô tả:** Thêm các thư viện cần thiết (Google AI SDK, Room, Compose Navigation, Coil) vào `build.gradle.kts` và cấu hình Gemini Client.
- **Tác nhân:** `mobile-developer`
- **Kỹ năng:** `clean-code`
- **Độ ưu tiên:** P0
- **Phụ thuộc:** Không
- **INPUT:** File `build.gradle.kts` hiện tại.
- **OUTPUT:**
  - `build.gradle.kts` được cập nhật các thư viện Room, Navigation Compose, Coil, và Google AI SDK.
  - File [GeminiServiceClient.kt](file:///d:/chplay/AIInsectIdentifierPro/app/src/main/java/com/kynv1/aiinsectidentifierpro/data/remote/GeminiServiceClient.kt) để giao tiếp với mô hình `gemini-1.5-flash` sử dụng System Instructions và Structured JSON Output.
- **VERIFY:** Build Gradle thành công không lỗi.

#### Task 2: Thiết lập Room Database cho Lịch sử
- **Mô tả:** Tạo Entity, DAO và Database class để lưu lại lịch sử các côn trùng đã nhận diện.
- **Tác nhân:** `mobile-developer`
- **Kỹ năng:** `clean-code`
- **Độ ưu tiên:** P0
- **Phụ thuộc:** Task 1
- **INPUT:** Package `com.kynv1.aiinsectidentifierpro`.
- **OUTPUT:**
  - File [InsectEntity.kt](file:///d:/chplay/AIInsectIdentifierPro/app/src/main/java/com/kynv1/aiinsectidentifierpro/data/local/entity/InsectEntity.kt) chứa các trường dữ liệu lịch sử (id, imageUri, commonName, scientificName, description, timestamp, v.v.).
  - File [InsectDao.kt](file:///d:/chplay/AIInsectIdentifierPro/app/src/main/java/com/kynv1/aiinsectidentifierpro/data/local/InsectDao.kt) chứa các câu lệnh truy vấn Room.
  - File [InsectDatabase.kt](file:///d:/chplay/AIInsectIdentifierPro/app/src/main/java/com/kynv1/aiinsectidentifierpro/data/local/InsectDatabase.kt).
  - File [InsectRepository.kt](file:///d:/chplay/AIInsectIdentifierPro/app/src/main/java/com/kynv1/aiinsectidentifierpro/data/repository/InsectRepository.kt).
- **VERIFY:** Compile code thành công, Room database build bình thường.

---

### 📌 Giai đoạn 2: UI & Features

#### Task 3: Tích hợp Camera & Thư viện Ảnh (Gallery)
- **Mô tả:** Xây dựng luồng chọn hình ảnh: cho phép mở camera chụp ảnh mới hoặc mở thư viện chọn ảnh có sẵn, sau đó lưu tạm đường dẫn URI.
- **Tác nhân:** `mobile-developer`
- **Kỹ năng:** `mobile-design`
- **Độ ưu tiên:** P1
- **Phụ thuộc:** Task 2
- **INPUT:** AndroidManifest.xml và UI flow.
- **OUTPUT:**
  - Cập nhật [AndroidManifest.xml](file:///d:/chplay/AIInsectIdentifierPro/app/src/main/AndroidManifest.xml) để xin quyền Camera (nếu dùng CameraX) hoặc cấu hình ActivityResultContracts để gọi System Camera/Gallery.
  - Trình quản lý URI ảnh đã chụp để hiển thị lên UI.
- **VERIFY:** Chạy app trên máy thử nghiệm, nhấn nút Camera/Gallery mở được giao diện hệ thống và trả về URI hình ảnh.

#### Task 4: Giao diện Màn hình chính Scan (ScanScreen)
- **Mô tả:** Thiết kế màn hình chính cho phép chụp ảnh, hiển thị ảnh đã chọn, nút kích hoạt "Nhận diện" (Scan) và trạng thái Loading sinh động khi chờ kết quả API.
- **Tác nhân:** `mobile-developer`
- **Kỹ năng:** `mobile-design`
- **Độ ưu tiên:** P1
- **Phụ thuộc:** Task 3
- **INPUT:** UI theme hiện tại.
- **OUTPUT:**
  - File [ScanScreen.kt](file:///d:/chplay/AIInsectIdentifierPro/app/src/main/java/com/kynv1/aiinsectidentifierpro/ui/screens/scan/ScanScreen.kt) và [ScanViewModel.kt](file:///d:/chplay/AIInsectIdentifierPro/app/src/main/java/com/kynv1/aiinsectidentifierpro/ui/screens/scan/ScanViewModel.kt).
- **VERIFY:** UI hiển thị đẹp mắt, nút chụp/chọn ảnh hoạt động, hiển thị ảnh preview đúng vị trí.

#### Task 5: Tích hợp AI Gemini & Lưu trữ kết quả
- **Mô tả:** Gửi ảnh từ URI lên Gemini API kèm theo prompt chi tiết để nhận diện côn trùng. Trả về kết quả JSON, parse thành object, lưu vào Room database và điều hướng sang màn hình Chi tiết.
- **Tác nhân:** `mobile-developer`
- **Kỹ năng:** `clean-code`
- **Độ ưu tiên:** P1
- **Phụ thuộc:** Task 4
- **INPUT:** Client API từ Task 1 và DB từ Task 2.
- **OUTPUT:**
  - [GeminiServiceClient.kt](file:///d:/chplay/AIInsectIdentifierPro/app/src/main/java/com/kynv1/aiinsectidentifierpro/data/remote/GeminiServiceClient.kt) được hoàn thiện hàm nhận diện qua hình ảnh đầu vào.
  - [ScanViewModel.kt](file:///d:/chplay/AIInsectIdentifierPro/app/src/main/java/com/kynv1/aiinsectidentifierpro/ui/screens/scan/ScanViewModel.kt) gọi API, lưu dữ liệu quét thành công vào Room qua Repository.
- **VERIFY:** Gửi thử ảnh côn trùng mẫu, kiểm tra log nhận được JSON phản hồi chuẩn xác từ Gemini và lưu được vào Database.

#### Task 6: Giao diện Chi tiết Côn trùng (DetailScreen)
- **Mô tả:** Thiết kế giao diện chi tiết đầy đủ thông tin: Ảnh đã quét, Tên khoa học, Tên thường gọi, Độ tin cậy (%), Mô tả chi tiết, Đặc điểm nổi bật, Môi trường sống và Cảnh báo an toàn (độ nguy hiểm).
- **Tác nhân:** `mobile-developer`
- **Kỹ năng:** `mobile-design`
- **Độ ưu tiên:** P1
- **Phụ thuộc:** Task 5
- **INPUT:** Dữ liệu Entity côn trùng.
- **OUTPUT:**
  - File [DetailScreen.kt](file:///d:/chplay/AIInsectIdentifierPro/app/src/main/java/com/kynv1/aiinsectidentifierpro/ui/screens/detail/DetailScreen.kt).
- **VERIFY:** Giao diện hiển thị đúng các thông tin từ database một cách trực quan, có định dạng đẹp mắt.

#### Task 7: Màn hình Lịch sử quét (HistoryScreen)
- **Mô tả:** Màn hình liệt kê toàn bộ các lần quét trước đây theo dạng lưới (Grid) hoặc danh sách (List), sắp xếp từ mới nhất đến cũ nhất. Cho phép click vào để xem lại chi tiết hoặc vuốt/nhấn nút để xóa bản ghi.
- **Tác nhân:** `mobile-developer`
- **Kỹ năng:** `mobile-design`
- **Độ ưu tiên:** P2
- **Phụ thuộc:** Task 2, Task 6
- **INPUT:** Room Database.
- **OUTPUT:**
  - File [HistoryScreen.kt](file:///d:/chplay/AIInsectIdentifierPro/app/src/main/java/com/kynv1/aiinsectidentifierpro/ui/screens/history/HistoryScreen.kt) và [HistoryViewModel.kt](file:///d:/chplay/AIInsectIdentifierPro/app/src/main/java/com/kynv1/aiinsectidentifierpro/ui/screens/history/HistoryViewModel.kt).
- **VERIFY:** Thêm dữ liệu giả lập hoặc quét thử nhiều lần, màn hình lịch sử cập nhật chính xác danh sách, click vào chuyển hướng sang màn hình chi tiết đúng bản ghi, xóa bản ghi hoạt động tốt.

#### Task 8: Điều hướng & Kết nối MainActivity (Navigation Host)
- **Mô tả:** Thiết lập Bottom Navigation hoặc Drawer để chuyển đổi qua lại giữa màn hình Scan chính và màn hình Lịch sử. Tích hợp Jetpack Compose Navigation.
- **Tác nhân:** `mobile-developer`
- **Kỹ năng:** `clean-code`
- **Độ ưu tiên:** P1
- **Phụ thuộc:** Task 4, Task 6, Task 7
- **INPUT:** MainActivity.kt
- **OUTPUT:**
  - Cập nhật [MainActivity.kt](file:///d:/chplay/AIInsectIdentifierPro/app/src/main/java/com/kynv1/aiinsectidentifierpro/MainActivity.kt) để chứa NavHost, cấu hình route cho Scan, Detail và History.
  - Setup BottomNavigationBar hoặc Tab bar thân thiện trên di động.
- **VERIFY:** Có thể click chuyển đổi mượt mà giữa các màn hình, nút Back trên thiết bị hoạt động tự nhiên.

---

## Giai đoạn X: Kiểm thử & Xác minh (Verification Phase)

### Tự động hóa & Kiểm tra Quy chuẩn (Automated & Compliance Checks)
Sau khi hoàn tất cài đặt code, ta thực hiện các bước sau để đảm bảo chất lượng:
- **Lint & Build:** 
  - Chạy `./gradlew build` hoặc `./gradlew assembleDebug` để đảm bảo code build thành công, không có lỗi cú pháp hay cảnh báo nghiêm trọng từ compiler.
- **Quy chuẩn UX/UI di động (Manual Check):**
  - Không sử dụng màu tím mặc định kém thẩm mỹ (tuân thủ quy tắc không dùng violet/purple slop của hệ thống). Thiết kế gam màu xanh tự nhiên (nature green) tươi sáng phối hợp Sleek Dark mode làm chủ đạo.
  - Kiểm tra độ lớn của Touch Targets (nút bấm tối thiểu 48x48dp) để dễ thao tác trên màn hình cảm ứng di động.

### 📝 Kế hoạch Xác minh thủ công
1. **Kiểm thử Luồng Nhận diện thực tế:**
   - Mở ứng dụng -> Chọn chức năng Camera/Gallery -> Chọn một hình ảnh côn trùng rõ nét (ví dụ: con bướm Monarch, kiến lửa, hoặc ong mật).
   - Nhấn "Scan" -> Đợi Loading hiển thị -> Xác minh xem kết quả hiển thị có chính xác loài đó và chứa đầy đủ mô tả chi tiết không.
2. **Kiểm thử Lịch sử:**
   - Quay lại màn hình chính -> Vào tab Lịch sử (History) -> Xác nhận bản ghi của con côn trùng vừa quét đã xuất hiện đầu danh sách với hình ảnh thu nhỏ và thời gian chính xác.
   - Click vào bản ghi đó -> Ứng dụng phải mở lại chính xác màn hình chi tiết của loài đó.
   - Nhấn xóa bản ghi -> Xác minh bản ghi biến mất khỏi lịch sử local.
3. **Kiểm thử xử lý lỗi offline:**
   - Tắt kết nối mạng của thiết bị -> Nhấn Scan một hình ảnh -> Ứng dụng phải hiển thị thông báo lỗi mạng thân thiện thay vì bị crash.

---

## ✅ PHASE X COMPLETE
- Lint: ✅ Pass (Kotlin compiled without errors)
- Security: ✅ No critical issues
- Build: ✅ Success (assembleDebug generated successfully)
- Date: 2026-06-30
