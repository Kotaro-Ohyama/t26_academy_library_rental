package jp.co.metateam.library.repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jp.co.metateam.library.model.RentalManage;

@Repository
public interface RentalManageRepository extends JpaRepository<RentalManage, Long> {

    List<RentalManage> findAll();

    Optional<RentalManage> findById(Long id);

    @Query("SELECT r FROM RentalManage r WHERE r.stock.id = :stockId AND r.status = :status")
    List<RentalManage> findByStockIdAndStatus(
        @Param("stockId") Object stockId,
        @Param("status") Integer status
    );

    // ★ stockId の型を String に修正しました
    @Query("SELECT r FROM RentalManage r " +
           "WHERE r.stock.id = :stockId " +
           "AND r.status IN (0, 1) " +
           "AND r.expectedRentalOn <= :newReturn " +
           "AND :newRental <= r.expectedReturnOn")
    List<RentalManage> findOverlappingRentals(
        @Param("stockId") String stockId,
        @Param("newRental") LocalDate newRental,
        @Param("newReturn") LocalDate newReturn
    );
}