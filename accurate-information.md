# Kế hoạch mở rộng phần Mô tả (Description) chi tiết chuẩn Pro cho các loài vật

Tài liệu này chi tiết kế hoạch viết lại phần mô tả (Description) của toàn bộ 22 loài vật trong cơ sở dữ liệu mẫu để đạt độ dài chuẩn, giàu thông tin (từ 120-200 từ mỗi con), chia làm các đoạn rõ ràng mô tả về: đặc điểm hình thể, tập tính sinh hoạt/thức ăn, và mối quan hệ với con người.

---

## 🛠️ Giải pháp đề xuất

### 1. Viết lại phần Description cho nhóm côn trùng quét mẫu (ID `10001` - `10010`)
Mở rộng mô tả khoa học cho các loài côn trùng cơ bản:
- **Ash-black Slug (Sên trần):** Thêm thông tin về kích thước khổng lồ của nó (lớn nhất thế giới), cách thở bằng phổi và vai trò phân hủy sinh học trong rừng cổ thụ.
- **Black Oil Beetle (Bọ dầu đen):** Thêm cơ chế tự vệ tiết ra cantharidin gây rộp da độc đáo và vòng đời ký sinh phức tạp trên ấu trùng ong đất.
- **Broom-tip Moth (Bướm đêm):** Thêm tập tính ngụy trang trên cành cây đậu chổi và vòng đời sinh trưởng ban đêm.
- ... Cập nhật tương tự cho các loài còn lại.

### 2. Viết lại phần Description cho nhóm bài viết trang chủ (ID `20001` - `20012`)
Mở rộng chi tiết sinh học tương ứng cho các con vật thuộc bài viết:
- **Deer Tick (Ve bét - `20001L`):** Viết sâu về cách chúng bám vào vật chủ (questing), vòng đời hút máu qua các giai đoạn, và các bệnh truyền nhiễm nguy hiểm như Lyme.
- **House Mouse (Chuột nhà - `20006L`):** Viết về khả năng thích nghi cực cao với con người, tập tính gặm nhấm mài răng liên tục gây chập điện, và cách chúng sinh sản nhanh chóng.
- **Common Wasp (Ong vò vẽ - `20008L`):** Viết về cấu trúc tổ giấy xã hội lên đến 10.000 con, tập tính ăn thịt kiểm soát sâu bệnh và hành vi hung dữ đốt nhiều lần khi tự vệ.
- ... Cập nhật cho toàn bộ 12 con vật.

---

## 📅 Các bước thực hiện cụ thể

1.  **Bước 1:** Chuẩn bị nội dung mô tả tiếng Anh chi tiết, chuyên nghiệp cho tất cả 22 loài vật.
2.  **Bước 2:** Cập nhật tệp [**`InsectRepository.kt`**](file:///d:/chplay/AIInsectIdentifierPro/app/src/main/java/com/kynv1/aiinsectidentifierpro/data/repository/InsectRepository.kt) thay thế các đoạn mô tả ngắn bằng nội dung mở rộng.
3.  **Bước 3:** Chạy biên dịch kiểm tra dự án và nghiệm thu giao diện hiển thị văn bản cuộn đẹp mắt.
