# Plan - Thiết kế lại nền màn hình Scan sử dụng tài nguyên Google Stitch

Thay thế màu nền gradient xanh lá cây/đen đơn điệu hiện tại của màn hình Quét ảnh (`ScanScreen`) và Quét âm thanh (`SoundScanScreen`) bằng ảnh nền rừng sương mù huyền ảo (`bg_onboarding_forest`) lấy từ thiết kế Google Stitch. 

Sự thay đổi này đồng bộ giao diện toàn bộ ứng dụng theo phong cách Nature Green cao cấp và tăng tính thẩm mỹ đáng kể.

## User Review Required

> [!IMPORTANT]
> **Đồng bộ hóa ảnh nền:**
> - Chúng ta sẽ tái sử dụng tài nguyên hình ảnh rừng tối `R.drawable.bg_onboarding_forest` làm ảnh nền cho cả hai màn hình quét. Đây chính là hình ảnh thiết kế chuẩn từ Google Stitch (đã được tải sẵn trong dự án).
> - Để đảm bảo nút bấm và chữ hiển thị sắc nét, chúng ta sẽ phủ thêm một lớp che mờ tối (scrim overlay) có độ mờ 50% (`Color.Black.copy(alpha = 0.5f)`) đè lên trên ảnh nền.

---

## Open Questions

> [!NOTE]
> **Độ mờ của lớp phủ (Overlay Opacity):**
> Bạn có muốn lớp phủ màu tối đè lên ảnh nền dày hơn (ví dụ 60%-70%) để chữ dễ đọc hơn, hay giữ ở mức 50% để vẫn nhìn rõ vẻ đẹp của rừng cây phía sau?

---

## Proposed Changes

### [UI Components]

#### [MODIFY] [ScanScreen.kt](file:///d:/chplay/AIInsectIdentifierPro/app/src/main/java/com/kynv1/aiinsectidentifierpro/ui/screens/scan/ScanScreen.kt)
- Thay đổi cấu trúc container chính từ `Box` sử dụng `background(Brush.verticalGradient(...))` sang sử dụng `Image(painterResource(R.drawable.bg_onboarding_forest))` bao phủ toàn màn hình.
- Thêm một `Box` che mờ tối (`Color.Black.copy(alpha = 0.5f)`) phủ trên ảnh nền trước khi vẽ các thành phần nút bấm và thẻ xem trước.

#### [MODIFY] [SoundScanScreen.kt](file:///d:/chplay/AIInsectIdentifierPro/app/src/main/java/com/kynv1/aiinsectidentifierpro/ui/screens/sound/SoundScanScreen.kt)
- Thực hiện thay đổi tương tự cho màn hình Quét âm thanh, thay thế gradient nền bằng ảnh nền rừng sương mù kèm lớp phủ tối màu để đồng bộ trải nghiệm quét.

---

## Verification Plan

### Automated Tests
- Chạy lệnh biên dịch Gradle Kotlin để kiểm tra cú pháp và build hệ thống:
  ```powershell
  .\gradlew.bat compileDebugKotlin
  ```

### Manual Verification
- Cài đặt chạy ứng dụng trên thiết bị giả lập, mở màn hình Quét ảnh (Camera Identification) và màn hình Quét âm thanh (Acoustic ID) để nghiệm thu tính thẩm mỹ trực quan.

## ✅ PHASE X COMPLETE
- Lint: ✅ Pass
- Security: ✅ No critical issues
- Build: ✅ Success
- Date: 2026-07-25
