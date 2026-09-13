---
version: alpha
name: AI Insect Identifier Pro
description: Nature-forward Android app for identifying insects with AI. Photo-first surfaces, green as the single interaction driver, generous touch targets.
colors:
  primary: "#2E7D32"
  primary-dark: "#1B5E20"
  primary-disabled: "#1E3528"
  secondary: "#7CB342"
  secondary-dark: "#558B2F"
  accent-lime: "#D4E157"
  promo: "#FFB300"
  promo-soft: "#FFCA28"
  star: "#FFC107"
  error: "#E53935"
  warning: "#FF9800"
  surface: "#FFFFFF"
  surface-muted: "#F7FAF7"
  surface-app: "#F9FBF9"
  border: "#E5EBE6"
  border-strong: "#CCCCCC"
  on-surface: "#333333"
  on-surface-muted: "#757575"
  surface-dark: "#0A0F0D"
  surface-dark-raised: "#14241C"
  border-dark: "#2E4C3E"
  on-surface-dark: "#FFFFFF"
  on-surface-dark-muted: "#B9C4BE"
  scrim: "#00000073"
typography:
  headline-lg: { fontFamily: Roboto, fontSize: 22px, fontWeight: 800, lineHeight: 1.2, letterSpacing: 0.5px }
  headline-md: { fontFamily: Roboto, fontSize: 20px, fontWeight: 700, lineHeight: 1.2 }
  title-md: { fontFamily: Roboto, fontSize: 16px, fontWeight: 700, lineHeight: 1.3 }
  body-lg: { fontFamily: Roboto, fontSize: 16px, fontWeight: 400, lineHeight: 1.5 }
  body-md: { fontFamily: Roboto, fontSize: 14px, fontWeight: 400, lineHeight: 1.5 }
  body-sm: { fontFamily: Roboto, fontSize: 13px, fontWeight: 500, lineHeight: 1.4 }
  label-md: { fontFamily: Roboto, fontSize: 12px, fontWeight: 600, lineHeight: 1.3 }
spacing:
  xs: 4px
  sm: 8px
  md: 12px
  lg: 16px
  xl: 24px
  xxl: 32px
rounded:
  sm: 8px
  md: 12px
  lg: 16px
  xl: 24px
  full: 999px
components:
  button-primary:
    backgroundColor: "{colors.primary}"
    textColor: "{colors.on-surface-dark}"
    typography: "{typography.title-md}"
    rounded: "{rounded.full}"
    height: 56px
  plan-card:
    backgroundColor: "{colors.surface}"
    textColor: "{colors.on-surface}"
    rounded: "{rounded.md}"
    height: 56px
    padding: 16px
  plan-card-selected:
    backgroundColor: "{colors.surface-muted}"
    textColor: "{colors.on-surface}"
    rounded: "{rounded.md}"
    height: 56px
    padding: 16px
  badge-promo:
    backgroundColor: "{colors.promo}"
    textColor: "{colors.on-surface}"
    typography: "{typography.label-md}"
    rounded: "{rounded.sm}"
    padding: 4px
  trust-badge:
    backgroundColor: "{colors.surface-muted}"
    textColor: "{colors.on-surface}"
    typography: "{typography.body-sm}"
    rounded: "{rounded.lg}"
    padding: 14px
  icon-button-circular:
    backgroundColor: "{colors.scrim}"
    textColor: "{colors.on-surface-dark}"
    rounded: "{rounded.full}"
    size: 48px
---

# AI Insect Identifier Pro

## Overview

Ứng dụng nhận diện côn trùng bằng AI. Người dùng là người đi dạo trong vườn, phụ huynh có con tò mò, người làm vườn — không phải nhà côn trùng học. Họ mở app **ngoài trời, một tay, dưới nắng**.

Ba hệ quả thiết kế từ bối cảnh đó:

1. **Ảnh là nội dung chính** — mọi surface đều nhường chỗ cho ảnh côn trùng. Chữ và điều khiển đứng ngoài rìa hoặc trên nền phủ tối, không đè lên chủ thể.
2. **Xanh lá là màu tương tác duy nhất** — app nói về thiên nhiên; xanh vừa là thương hiệu vừa là tín hiệu "bấm được". Mọi màu khác chỉ để truyền tin, không để mời bấm.
3. **Một tay, nắng gắt** — vùng chạm rộng, tương phản cao, chữ không nhỏ hơn 12sp.

Tông cảm xúc: **hiếu kỳ và tin cậy**, không phải "sang trọng" hay "công nghệ cao". Tránh mọi thứ tạo cảm giác cường điệu — người dùng cần tin vào kết quả nhận diện.

---

## Colors

### Màu tương tác

| Token | Hex | Dùng cho |
|---|---|---|
| `primary` | `#2E7D32` | Nút chính, trạng thái được chọn, tiêu đề nhấn mạnh |
| `primary-dark` | `#1B5E20` | Đầu gradient nút, trạng thái nhấn |
| `primary-disabled` | `#1E3528` | Nút vô hiệu trên nền tối |
| `secondary` | `#7CB342` | Xanh lá sáng — icon, khung ngắm, cuối gradient |

> `primary` và `secondary` **không thay thế nhau**. `primary` cho hành động; `secondary` cho trang trí và icon.

### Màu truyền tin (không bao giờ để mời bấm)

| Token | Hex | Dùng cho | 🚫 Không dùng cho |
|---|---|---|---|
| `promo` | `#FFB300` | Badge tiết kiệm, nhãn khuyến mãi | Nút, viền trạng thái chọn |
| `star` | `#FFC107` | Icon sao đánh giá | Bất kỳ thứ gì khác |
| `error` | `#E53935` | **Chỉ lỗi thật** — thất bại, cảnh báo phá hủy | Badge khuyến mãi, nhãn "hot" |
| `warning` | `#FF9800` | Cảnh báo mức độ nguy hiểm của côn trùng | Khuyến mãi |

> 🔴 **Quy tắc một màu nhấn:** trong một vùng nội dung, ngoài `primary` chỉ được thêm **tối đa một** màu truyền tin. Ba màu nhấn cạnh nhau là dấu hiệu thiết kế đã hỏng.

### Surface sáng (paywall, danh sách, cài đặt)

| Token | Hex | Dùng cho |
|---|---|---|
| `surface` | `#FFFFFF` | Nền thẻ, nền sheet |
| `surface-muted` | `#F7FAF7` | Thẻ chìm, vùng nhóm nội dung |
| `surface-app` | `#F9FBF9` | Nền màn hình |
| `border` | `#E5EBE6` | Viền mặc định |
| `on-surface` | `#333333` | Chữ chính |
| `on-surface-muted` | `#757575` | Chữ phụ, disclaimer |

### Surface tối (màn scan, màn có ảnh nền)

| Token | Hex | Dùng cho |
|---|---|---|
| `surface-dark` | `#0A0F0D` | Nền màn |
| `surface-dark-raised` | `#14241C` | Thẻ nổi trên nền tối |
| `border-dark` | `#2E4C3E` | Viền trên nền tối |
| `on-surface-dark` | `#FFFFFF` | Chữ chính trên nền tối |
| `on-surface-dark-muted` | `#B9C4BE` | Chữ phụ trên nền tối |
| `scrim` | `#000000` @ 45% | Lớp phủ trên ảnh để chữ đọc được |

> 🔴 **Không dùng token của nền sáng cho nền tối.** `on-surface-muted` (#757575) đặt trên `surface-dark-raised` chỉ đạt **3.5:1**, dưới ngưỡng AA 4.5:1. Đó là lý do có `on-surface-dark-muted` riêng (9:1).

> Chữ trắng đặt trên ảnh **bắt buộc** có `scrim` phía dưới. Không có ngoại lệ — ảnh do người dùng chụp nên không dự đoán được độ sáng.

### ⛔ Purple Ban

`Color.kt` hiện còn `Purple80`, `PurpleGrey80`, `Pink80`, `Purple40`, `PurpleGrey40`, `Pink40` — tàn dư của template Android Studio, và `Theme.kt` **vẫn đang nạp chúng vào `MaterialTheme.colorScheme`**.

**Hệ quả thực tế:** bất kỳ component Material3 nào dùng màu mặc định (`Button`, `Switch`, `TextField`, hiệu ứng ripple) sẽ **hiện ra màu tím**, không phải xanh lá.

Cho tới khi `Theme.kt` được sửa:

- Mọi component Material3 phải **truyền màu tường minh**, không dựa vào `colorScheme`.
- Không thêm mới bất kỳ màu tím/violet nào.

> 📌 Nợ kỹ thuật đã ghi nhận: sửa `Theme.kt` để `colorScheme` trỏ về `primary`/`secondary` ở trên. Nằm ngoài phạm vi [paywall-redesign.md](paywall-redesign.md).

---

## Typography

Font hệ thống (`FontFamily.Default` → Roboto). Không có font tùy chỉnh, và **cố ý giữ vậy** — font hệ thống tải tức thì, hiển thị đúng ở mọi ngôn ngữ, và tôn trọng cỡ chữ người dùng đặt trong cài đặt Android.

| Token | Cỡ | Đậm | Dùng cho |
|---|---|---|---|
| `headline-lg` | 22sp | 800 | Tiêu đề màn (UNLOCK ALL ACCESS) |
| `headline-md` | 20sp | 700 | Tiêu đề mục |
| `title-md` | 16sp | 700 | Nhãn nút, giá tiền |
| `body-lg` | 16sp | 400 | Nội dung chính |
| `body-md` | 14sp | 400 | Tên gói, mô tả |
| `body-sm` | 13sp | 500 | Dòng lợi ích |
| `label-md` | 12sp | 600 | Badge, chú thích, disclaimer |

### 🔴 Sàn 12sp

**Không có text nào nhỏ hơn 12sp.** `Dimens.sp_8`, `sp_10`, `sp_11` tồn tại trong `Dimens.kt` nhưng **không được dùng cho text hiển thị** — chúng là di sản, sẽ gỡ dần.

Lý do không phải thẩm mỹ: người dùng ngoài trời, ánh nắng làm giảm tương phản biểu kiến; và nhóm người dùng lớn tuổi (làm vườn) chiếm tỷ trọng đáng kể. Chữ 8sp là không đọc được, không phải "nhỏ tinh tế".

Khi thấy mình cần chữ nhỏ hơn 12sp, đó là tín hiệu **bố cục sai**, không phải cần thêm token nhỏ hơn. Hãy đổi bố cục.

---

## Layout

Thang khoảng cách dùng bội số 4: `xs 4` · `sm 8` · `md 12` · `lg 16` · `xl 24` · `xxl 32`.

| Ngữ cảnh | Giá trị |
|---|---|
| Lề ngang màn hình | `lg` (16dp) |
| Khoảng cách giữa các thẻ trong danh sách | `sm` (8dp) |
| Padding trong thẻ | `lg` (16dp) |
| Khoảng cách giữa các nhóm nội dung | `xl` (24dp) |

### 🔴 Sàn vùng chạm 48dp

Mọi phần tử bấm được có **vùng chạm tối thiểu 48×48dp**, kể cả khi phần nhìn thấy nhỏ hơn — dùng padding để mở rộng vùng chạm.

Áp dụng cho cả những thứ dễ quên: link Terms/Privacy, nút đóng, icon trong toolbar.

### Bố cục màn hình có CTA

Màn hình có hành động chính (paywall, xác nhận) dùng cấu trúc:

```
┌──────────────────────┐
│  Nội dung cuộn được  │  ← weight(1f) + verticalScroll
├──────────────────────┤
│  CTA cố định         │  ← navigationBarsPadding()
└──────────────────────┘
```

CTA **không bao giờ** nằm trong vùng cuộn. Người dùng phải thấy hành động chính mà không cần cuộn.

---

## Elevation & Depth

**Nội dung nằm trong luồng thì phẳng** — không đổ bóng. Dùng ba cơ chế thay thế:

| Cơ chế | Dùng khi |
|---|---|
| **Viền** (`border` 1dp) | Phân tách thẻ trên nền sáng |
| **Lớp tông** (`surface-muted` trên `surface`) | Nhóm nội dung liên quan |
| **Lớp phủ** (`scrim`) | Tách chữ khỏi ảnh nền |

Lý do: nội dung chính là ảnh chụp thiên nhiên vốn đã nhiều chi tiết; thêm bóng làm nhiễu và tốn hiệu năng khi cuộn.

### Phần tử nổi thì phải có bóng

Thứ **trôi bên trên** nội dung — FAB, snackbar, bottom sheet, dialog — **bắt buộc** có elevation. Không có bóng thì người dùng đọc nó thành một phần của trang, không biết nó đang che nội dung bên dưới và sẽ trôi đi.

| Phần tử | Elevation |
|---|---|
| FAB / nút nổi (vd. "Ask AI") | 6dp |
| Snackbar | mặc định Material3 |
| Bottom sheet / dialog | mặc định Material3 |

Ranh giới: **trong luồng → phẳng; trôi bên trên → có bóng.** Nếu phải hỏi "cái này có nổi không", câu trả lời gần như luôn là không.

Ngoại lệ riêng: sheet trắng bo góc trên đè lên ảnh hero được phép dịch lên bằng `offset` để tạo cảm giác chồng lớp — dùng vị trí thay cho bóng.

---

## Shapes

| Token | Bán kính | Dùng cho |
|---|---|---|
| `sm` | 8dp | Badge, chip nhỏ |
| `md` | 12dp | Thẻ trong danh sách |
| `lg` | 16dp | Thẻ nhóm nội dung |
| `xl` | 24dp | Sheet, khung ảnh lớn |
| `full` | tròn hoàn toàn | Nút chính, nút icon tròn |

Ngôn ngữ hình khối: **bo tròn nhất quán, không góc nhọn**. Nút chính bo tròn hoàn toàn để tách bạch khỏi thẻ (bo vừa) — hình dạng tự nó đã báo hiệu "đây là hành động".

---

## Components

### Nút chính (`button-primary`)

- Cao 56dp, bo tròn hoàn toàn, rộng hết chiều ngang khả dụng
- Nền gradient ngang `primary-dark` → `primary`
- Chữ `title-md` màu trắng, viết hoa
- **Dùng `Button` của Material3**, không dùng `Box` + `clickable` — cần ripple và semantics `role=Button` cho TalkBack
- Vì `colorScheme` còn tím, phải truyền `ButtonDefaults.buttonColors(containerColor = Color.Transparent)` rồi vẽ gradient bằng `Modifier.background`

Mỗi màn hình chỉ có **một** nút chính.

### Thẻ chọn gói (`plan-card`)

- Bố cục **một hàng ngang**: `[radio] [tên gói + giá theo tuần] ... [giá] [badge]`
- Cao tối thiểu 56dp
- Chưa chọn: nền `surface`, viền `border` 1dp
- Đã chọn: nền `surface-muted`, viền `primary` 2dp, radio đầy

> Xếp dọc, không xếp ngang. Ba thẻ xếp ngang trên màn điện thoại buộc chữ phải nhỏ hơn sàn 12sp — bố cục sai, không phải chữ sai.

### Badge khuyến mãi (`badge-promo`)

- Nền `promo`, chữ `label-md` màu `on-surface`
- **Mỗi nhóm lựa chọn chỉ một badge.** Hai badge trên cùng một thẻ luôn là dấu hiệu thừa

### Badge tin cậy (`trust-badge`)

- Nền `surface-muted`, bo `lg`
- Chỉ chứa **tuyên bố kiểm chứng được**: công nghệ đang dùng, năng lực đo được
- 🚫 Không chứa lời chứng thực bịa, đánh giá giả, số liệu không kiểm chứng được

### Dialog

**Tông dialog theo tông của màn nền**, không cố định một màu:

| Màn nền | Dialog |
|---|---|
| Sáng (Collection, Settings) | `surface` trắng, chữ `on-surface` |
| Tối / phủ ảnh (Scan) | `surface-dark-raised`, chữ `on-surface-dark` + `on-surface-dark-muted` |

Lý do: dialog trắng bật lên giữa màn ảnh tối gây chói mắt, còn dialog tối trên nền trắng thì nặng nề. Tông phải khớp với thứ nó đang che.

**Dùng `Dialog` thật, không dùng `Box` phủ toàn màn.** `Box` + `background()` chỉ vẽ màu chứ **không nuốt sự kiện chạm** — người dùng vẫn bấm xuyên xuống nội dung bên dưới, nút back không bị chặn, và TalkBack không coi đó là modal. `Dialog` xử lý cả ba.

Tác vụ chờ lâu (gọi AI, tải mạng) **phải có đường thoát** — nút Cancel, hoặc cho đóng bằng back. Không bao giờ khoá người dùng trong màn chờ vô hạn.

### Nút icon tròn (`icon-button-circular`)

- 48dp, nền `scrim`, icon trắng
- Dùng cho nút đóng/quay lại đặt trên ảnh

---

## Do's and Don'ts

### Do

- Dùng `primary` là màu duy nhất mời bấm
- Đặt `scrim` dưới mọi chữ trắng nằm trên ảnh
- Mở rộng vùng chạm tới 48dp bằng padding khi phần nhìn thấy nhỏ hơn
- Truyền màu tường minh cho component Material3 (vì `colorScheme` còn tím)
- Giữ CTA ngoài vùng cuộn
- Chỉ nêu tuyên bố kiểm chứng được

### Don't

- ❌ Dùng `error` đỏ cho badge khuyến mãi — làm loãng tín hiệu lỗi thật
- ❌ Đặt ba màu nhấn cạnh nhau trong một vùng
- ❌ Dùng text nhỏ hơn 12sp
- ❌ Thêm màu tím/violet mới
- ❌ Đưa biểu tượng riêng của một màn sang màn khác chỉ để trang trí (khung ngắm là của màn scan)
- ❌ Tự chuyển động nội dung mà không cho dừng khi người dùng chạm
- ❌ Hardcode mã màu trong composable — mọi màu phải có tên trong `Color.kt` và tra được ở đây

---

## Đối chiếu token ↔ code

| Token DESIGN.md | Biến trong `Color.kt` |
|---|---|
| `primary` | `ActiveGreen` / `ButtonGreen` (cùng `#2E7D32`) |
| `primary-dark` | `DarkButtonGreen` |
| `secondary` | `NatureGreen` / `NatureLightGreen` (cùng `#7CB342`) |
| `promo` | `GoldAmber` |
| `promo-soft` | `GoldYellow` |
| `star` | `StarGold` |
| `error` | `AlertRed` |
| `surface-muted` | `PremiumBgLight` |
| `border` | `PremiumBorderLight` |
| `on-surface` | `TextCharcoal` |
| `on-surface-muted` | `TextMediumGrey` |
| `on-surface-dark-muted` | `TextMutedOnDark` |
| `surface-dark-raised` | `CardBackground` |

> ⚠️ Có các cặp trùng hex nhưng khác tên (`ActiveGreen`/`ButtonGreen`, `NatureGreen`/`NatureLightGreen`). Nên hợp nhất dần để tránh trôi màu về sau.
