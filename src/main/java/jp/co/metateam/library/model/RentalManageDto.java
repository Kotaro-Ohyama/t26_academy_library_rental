package jp.co.metateam.library.model;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

/**
 * 貸出管理DTO
 */
@Getter
@Setter
public class RentalManageDto {

    @NotEmpty(message = "社員番号の選択は必須です")
    private String employeeId;

    @NotNull(message = "貸出予定日は必須です")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expectedRentalOn;

    @NotNull(message = "返却予定日は必須です")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expectedReturnOn;

    @NotNull(message = "貸出ステータスの選択は必須です")
    private Integer status;

    @NotEmpty(message = "在庫管理番号の選択は必須です")
    private String stockId;
/**
 * 返却予定日が貸出予定日より後かの整合性チェック
 */
public boolean isValidReturnDate() {
    //null時はほかの@Notnullに任せる
    if (expectedRentalOn == null || expectedReturnOn == null) {
        return true;
    }
    //返却予定日＞貸出予定日
    return expectedReturnOn.isAfter(expectedRentalOn);
  }
/**
 * 貸出予定日とステータスの整合性をチェック
 */
public String validateStatusByRentalDate() {
    // null時は@NotNullに任せる
    if (expectedRentalOn == null || status == null) {
        return null;
    }
    
    if (status == 2 || status == 3) {
        return "貸出ステータスは貸出待ち、貸出中を選択してください";
    }

    LocalDate today = LocalDate.now();
    // 過去日
    if (expectedRentalOn.isBefore(today)) {
        // 貸出中以外ならエラー
        if (status != 1) {
            return "過去日付では「貸出中」を選択してください";
        }
    }
    // 未来日
    if (expectedRentalOn.isAfter(today)) {
        // 貸出待ち以外ならエラー
        if (status != 0) {
            return "未来日付では「貸出待ち」を選択してください";
        }
    }
    return null;
}
}


