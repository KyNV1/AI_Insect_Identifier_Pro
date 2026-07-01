# Kế hoạch triển khai: Onboarding Flow (4 màn hình)

Tài liệu này vạch ra kế hoạch triển khai giao diện và logic cho luồng Onboarding (4 bước giới thiệu) của ứng dụng **AI Insect Identifier Pro** dựa trên các bản thiết kế đã tạo trên Google Stitch.

---

## 🔍 Khảo sát hiện trạng & Kiến trúc đề xuất

### 1. Luồng hoạt động (User Flow)
1. **Lần đầu mở ứng dụng:** Ứng dụng hiển thị màn hình Onboarding. Người dùng có thể vuốt qua lại giữa 4 trang hoặc nhấn nút **"CONTINUE"** để chuyển tiếp.
2. **Tại trang cuối (Trang 4 - Đánh giá):** Khi nhấn **"CONTINUE"**, trạng thái `onboarding_completed = true` sẽ được lưu lại, và ứng dụng điều hướng sang màn hình chính (Scan).
3. **Các lần mở ứng dụng tiếp theo:** Ứng dụng tự động bỏ qua Onboarding và vào thẳng màn hình Scan.

### 2. Thiết kế giao diện (UI Specs)
- Cả 4 màn hình sử dụng chung cấu trúc khung nền tối màu thiên nhiên (Dark Forest background), nút điều hướng chính dạng viên thuốc bo tròn màu xanh lá cây (`#7CB342`).
- Sử dụng `HorizontalPager` từ Jetpack Compose để tạo hiệu ứng chuyển trang vuốt ngang mượt mà.
- Hiển thị thanh chỉ báo trang (Pager Indicator) dạng các dấu chấm ở phía dưới để người dùng biết mình đang ở bước nào.

---

## ❓ Câu hỏi làm rõ (Socratic Gate)

> [!NOTE]
> Lựa chọn đã thống nhất với người dùng:
> 1. **Lưu trữ trạng thái:** Sử dụng **SharedPreferences** truyền thống.
> 2. **Tài nguyên hình ảnh:** Đặt hình ảnh trong thư mục **`res/drawable`** (lưu trữ local).

---

## 🛠️ Các bước thực hiện chi tiết (Task Breakdown)

### Bước 1: Cấu hình lưu trữ trạng thái Onboarding [HOÀN THÀNH]
- [x] Tạo lớp quản lý cài đặt `OnboardingStore` (Sử dụng SharedPreferences) để đọc/ghi trạng thái hoàn thành onboarding: `onboarding_completed`.
- [x] Đăng ký `OnboardingStore` trong Hilt `DatabaseModule` để có thể dễ dàng tiêm (inject) vào ViewModels hoặc MainActivity.

### Bước 2: Tạo màn hình Onboarding Screen [HOÀN THÀNH]
- [x] Tải 5 tài nguyên ảnh từ Stitch về thư mục `res/drawable`.
- [x] Tạo file [OnboardingScreen.kt](file:///d:/chplay/AIInsectIdentifierPro/app/src/main/java/com/kynv1/aiinsectidentifierpro/ui/screens/onboarding/OnboardingScreen.kt) và [OnboardingViewModel.kt](file:///d:/chplay/AIInsectIdentifierPro/app/src/main/java/com/kynv1/aiinsectidentifierpro/ui/screens/onboarding/OnboardingViewModel.kt).
- [x] Xây dựng giao diện với `HorizontalPager`, hiển thị ảnh nền, hiệu ứng quét động, cột sóng âm dao động và form vote sao tiện ích.

### Bước 3: Điều chỉnh luồng điều hướng (Navigation Host) [HOÀN THÀNH]
- [x] Thêm route `Screen.Onboarding` vào [Screen.kt](file:///d:/chplay/AIInsectIdentifierPro/app/src/main/java/com/kynv1/aiinsectidentifierpro/ui/navigation/Screen.kt).
- [x] Trong `MainActivity`, kiểm tra trạng thái onboarding trước khi hiển thị `startDestination` để chuyển tiếp thông minh.

---

## 🚦 Kế hoạch Xác minh (Verification Plan) [ĐÃ XÁC MINH]

### Kiểm thử thủ công
1. Xóa dữ liệu ứng dụng (Clear Storage), mở app lên để kiểm tra xem Onboarding có hiển thị chính xác 4 trang hay không.
2. Vuốt qua lại các trang và click nút "CONTINUE" ở mỗi trang để xem chuyển động.
3. Ở trang 4, click "CONTINUE", kiểm tra xem app có chuyển sang màn hình Scan không.
4. Tắt ứng dụng đi và mở lại, kiểm tra xem app có đi thẳng vào màn hình Scan (bỏ qua Onboarding) không.
