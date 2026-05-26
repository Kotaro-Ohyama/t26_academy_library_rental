package jp.co.metateam.library.service;

import java.util.List;
import java.sql.Timestamp; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.metateam.library.model.Account;
import jp.co.metateam.library.model.Stock;
import jp.co.metateam.library.repository.RentalManageRepository;
import jp.co.metateam.library.repository.StockRepository;
import jp.co.metateam.library.model.RentalManage;
import jp.co.metateam.library.model.RentalManageDto;

@Service
public class RentalManageService {
    private final StockRepository stockRepository;
    private final RentalManageRepository rentalManageRepository;

    @Autowired
    public RentalManageService(StockRepository stockRepository, RentalManageRepository rentalManageRepository){
        this.stockRepository = stockRepository;
        this.rentalManageRepository = rentalManageRepository;
    }

    @Transactional
    public void save(RentalManageDto rentalManageDto) {
        String stockId = rentalManageDto.getStockId();

        // 期間の重複チェック
        List<RentalManage> duplicateRentals = rentalManageRepository.findOverlappingRentals(
            stockId,
            rentalManageDto.getExpectedRentalOn(),
            rentalManageDto.getExpectedReturnOn()
        );

        // 重複データが存在する場合、エラーを投げて処理を中断する
        if (!duplicateRentals.isEmpty()) {
            throw new IllegalStateException("指定した期間は既存の予約・貸出データと重複しています");
        }

        // 重複がなければ保存処理
        RentalManage rentalManage = new RentalManage();
        
        Account account = new Account();
        account.setEmployeeId(rentalManageDto.getEmployeeId());
        rentalManage.setAccount(account);
        
        rentalManage.setExpectedRentalOn(rentalManageDto.getExpectedRentalOn());
        rentalManage.setExpectedReturnOn(rentalManageDto.getExpectedReturnOn());
        
        rentalManage.setStatus(rentalManageDto.getStatus());
        
        if (rentalManageDto.getStatus() != null && rentalManageDto.getStatus() == 1) {
            if (rentalManageDto.getExpectedRentalOn() != null) {
                Timestamp rentaledAt = Timestamp.valueOf(rentalManageDto.getExpectedRentalOn().atStartOfDay());
                rentalManage.setRentaledAt(rentaledAt);
            }
        }
        
        Stock stock = new Stock();
        stock.setId(stockId);
        rentalManage.setStock(stock);

        rentalManageRepository.save(rentalManage);
    }

    public List<RentalManage> findAll() {
        return rentalManageRepository.findAll();
    }
}