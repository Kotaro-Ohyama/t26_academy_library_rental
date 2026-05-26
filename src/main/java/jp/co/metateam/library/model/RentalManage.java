package jp.co.metateam.library.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.time.LocalDate;

/**
 * 貸出登録
 */

@Entity
@Table(name = "rental_manage")
public class RentalManage {
    
    /**貸出管理番号 */
    @Id
    //オートインクリメントの指示
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**貸出予定日 */
    @Column(name = "expected_rental_on", nullable = false)
    private LocalDate expectedRentalOn; 

    /**返却予定日 */
    @Column(name = "expected_return_on", nullable = false)
    private LocalDate expectedReturnOn; 

    /**貸出ステータス */
    @Column(name = "status", nullable = false)
    private Integer status;

    /**貸出開始日時（貸出されるまで未入力） */
    @Column(name = "rentaled_at")
    private Timestamp rentaled_at;

    /**返却日時（返却されるまで未入力） */
    @Column(name = "returned_at")
    private Timestamp returned_at;

    /**キャンセル日時（キャンセルされるまで未入力） */
    @Column(name = "canceled_at")
    private Timestamp canceled_at;

    /**社員番号（外部キー） */
    @ManyToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "employee_id", nullable = false)
    private Account account;

    /**在庫管理番号（外部キー） */
    @ManyToOne
    @JoinColumn(name = "stock_id", referencedColumnName = "id" , nullable = false)
    private Stock stock;


    /**Getters */
    public Long getId() {return id;}

    public LocalDate getExpectedRentalOn() { 
        return expectedRentalOn;
    }

    public LocalDate getExpectedReturnOn() { 
        return expectedReturnOn;
    }

    public Integer getStatus() {
        return status;
    }

    public Timestamp getRentaledAt() {
        return rentaled_at;
    }

    public Timestamp getRetuenedAt() {
        return returned_at;
    }

    public Timestamp getCanceledAt() {
        return canceled_at;
    }

    public Account getAccount() {
        return account;
    }

    public Stock getStock() {
        return stock;
    }

    
    /**Setters */
    public void setId(long id) {this.id = id;}

    public void setExpectedRentalOn(LocalDate expectedRentalOn) { 
        this.expectedRentalOn = expectedRentalOn;
    }

    public void setExpectedReturnOn(LocalDate expectedReturnOn) { 
        this.expectedReturnOn = expectedReturnOn;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setRentaledAt(Timestamp rentaledAt) {
        this.rentaled_at = rentaledAt;
    }

    public void setReturnedAt(Timestamp returnedAt) {
        this.returned_at = returnedAt;
    }

    public void setCanceledAt(Timestamp canceledAt) {
        this.canceled_at = canceledAt;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }
}