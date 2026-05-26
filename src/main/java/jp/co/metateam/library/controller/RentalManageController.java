package jp.co.metateam.library.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import jp.co.metateam.library.model.Stock;
import jp.co.metateam.library.model.RentalManageDto;
import jp.co.metateam.library.model.RentalManage;
import jp.co.metateam.library.service.AccountService;
import jp.co.metateam.library.model.Account;
import jp.co.metateam.library.service.RentalManageService;
import jp.co.metateam.library.service.StockService;
import jp.co.metateam.library.values.RentalStatus;
import lombok.extern.log4j.Log4j2;

/**
 * 貸出管理関連クラス
 */
@Log4j2
@Controller
public class RentalManageController {

    private final StockService stockService;
    private final RentalManageService rentalManageService;
    private final AccountService accountService;

    @Autowired
    public RentalManageController(
        StockService stockService,
        RentalManageService rentalManageService,
        AccountService accountService ) {

        this.stockService = stockService;
        this.rentalManageService = rentalManageService;
        this.accountService = accountService;
    }

    /**初期：貸出記録の一覧の表示 */
    @GetMapping("/rental/index")
    public String index(Model model) {
        List<RentalManage> rentalManageList = this.rentalManageService.findAll();
        model.addAttribute("rentalManage", rentalManageList);
        return "/rental/index";
    }

    /**貸出登録画面 */
    @GetMapping("/rental/add")
    public String add(Model model){
        List<Stock> stockList = this.stockService.findAll();
        List<Account> accounts = this.accountService.findAll();
        model.addAttribute("stockList", stockList);
        model.addAttribute("rentalManageStatus", RentalStatus.values());
        model.addAttribute("accounts", accounts);
      
        if(!model.containsAttribute("rentalManageDto")) {
            model.addAttribute("rentalManageDto", new RentalManageDto());
        }

        return "/rental/add";
    }

    /**貸出登録の処理フロー */
    @PostMapping("/rental/add")
    public String save(@Valid @ModelAttribute 
        RentalManageDto rentalManageDto, 
        BindingResult result,
        RedirectAttributes ra,
        Model model) {
        
        try {
            //返却予定日が貸出予定日よりも後かチェック
            if (!rentalManageDto.isValidReturnDate()) {
                result.rejectValue(
                    "expectedReturnOn",
                    "date.invalid",
                    "返却予定日の値が貸出予定日に対して不正です\r\n" + 
                    "貸出予定日より後の日付を選択してください");
            } 
            
            //ステータスチェック（整合性）
            String statusError = rentalManageDto.validateStatusByRentalDate();
            if (statusError != null) {
                result.rejectValue(
                    "status", 
                    "status.invalid",
                    statusError);
            }

            // 在庫のストックステータスチェック
            if (rentalManageDto.getStockId() != null && !rentalManageDto.getStockId().isEmpty()) {
                // stockServiceを使って、DBから現在の在庫情報を取得
                Stock stock = this.stockService.findById(rentalManageDto.getStockId());
                
                // 在庫が存在し、かつステータスが1（利用不可）の場合はエラーにする
                if (stock != null && stock.getStatus() == 1) {
                    result.rejectValue(
                        "stockId",                   
                        "stockStatus.invalid",       
                        "この書籍は貸出不可能です"   
                    );
                }
            }

            // 入力チェックや在庫ステータスチェックでエラーがある場合は、即座に画面へ戻す
            if (result.hasErrors()) {
                List<Stock> stockList = this.stockService.findAll();
                List<Account> accounts = this.accountService.findAll();
                model.addAttribute("stockList", stockList);
                model.addAttribute("rentalManageStatus", RentalStatus.values());
                model.addAttribute("accounts", accounts);
                return "/rental/add"; 
            }

            // エラーが一切ない場合のみ、サービスの保存（重複チェック含む）を実行する
            try {
                this.rentalManageService.save(rentalManageDto);
            } catch (IllegalStateException e) {
                result.rejectValue("expectedReturnOn", "overlapping.error", e.getMessage());
                
                List<Stock> stockList = this.stockService.findAll();
                List<Account> accounts = this.accountService.findAll();
                model.addAttribute("stockList", stockList);
                model.addAttribute("rentalManageStatus", RentalStatus.values());
                model.addAttribute("accounts", accounts);
                return "/rental/add";
            }

            // すべて正常に完了したら一覧画面へリダイレクト
            return "redirect:/rental/index";
            
        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("rentalManageDto", rentalManageDto);
            return "redirect:/rental/add";
        }
    }
}
