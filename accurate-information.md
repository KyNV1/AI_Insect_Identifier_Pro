# Kế hoạch đồng bộ hóa hình ảnh chính xác 100% với tên loài vật

Tài liệu này chi tiết kế hoạch sửa đổi lỗi nghiêm trọng trong cơ sở dữ liệu mẫu: Hình ảnh hiển thị bị lệch hoàn toàn so với tên và mô tả loài vật (ví dụ: Tên là Sên trần nhưng ảnh lại hiển thị Bọ cánh cứng màu xanh).

---

## 🛠️ Giải pháp đề xuất

### 1. Tạo mới 9 hình ảnh chính xác cho nhóm côn trùng quét cơ bản
Sử dụng công cụ tạo hình ảnh để tạo các hình ảnh côn trùng thực tế chính xác và lưu vào `res/drawable`:
- `img_basic_ash_black_slug.png` (Hình ảnh con Sên trần màu xám đen thực tế)
- `img_basic_black_oil_beetle.png` (Hình ảnh con Bọ dầu màu đen bóng)
- `img_basic_broom_tip_moth.png` (Hình ảnh con Bướm đêm Broom-tip ngụy trang)
- `img_basic_buffish_mining_bee.png` (Hình ảnh con Ong đất lông màu cam vàng)
- `img_basic_common_wasp.png` (Hình ảnh con Ong vò vẽ sọc vàng đen)
- `img_basic_brown_lipped_snail.png` (Hình ảnh con Ốc sên grove sọc viền nâu)
- `img_basic_black_red_froghopper.png` (Hình ảnh con Froghopper sọc đen đỏ)
- `img_basic_sabre_wasp.png` (Hình ảnh con Ong Sabre với ống đẻ trứng dài)
- `img_basic_red_ladybug.png` (Hình ảnh con Bọ rùa màu đỏ chấm đen thực tế)

### 2. Cập nhật ánh xạ hình ảnh trong `InsectRepository.kt`
Thay đổi đường dẫn hình ảnh của các thực thể từ ID `10001L` đến `10010L` từ ảnh onboarding mẫu cũ sang các tệp ảnh chính xác mới được tạo này.

---

## 📅 Các bước thực hiện cụ thể

1.  **Bước 1:** Tạo 9 hình ảnh chuẩn xác bằng công cụ tạo ảnh.
2.  **Bước 2:** Copy và đổi tên các ảnh vào thư mục `app/src/main/res/drawable/`.
3.  **Bước 3:** Cập nhật ánh xạ tài nguyên hình ảnh trong [**`InsectRepository.kt`**](file:///d:/chplay/AIInsectIdentifierPro/app/src/main/java/com/kynv1/aiinsectidentifierpro/data/repository/InsectRepository.kt).
4.  **Bước 4:** Biên dịch kiểm tra dự án.
