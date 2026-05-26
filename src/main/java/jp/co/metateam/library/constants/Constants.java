package jp.co.metateam.library.constants;

public class Constants {
    
    /** 保管状態：利用可 */
    public static final int STOCK_AVAILABLE = 0;

    /** 保管状態：利用不可 */
    public static final int STOCK_UNAVAILABLE = 1;

    /**貸出ステータス：貸出待ち */
    public static final int RENT_WAIT = 0;

    /**貸出ステータス：貸出中 */
    public static final int RENTAlING = 1;

    /**貸出ステータス：返却済み */
    public static final int RETURNED = 2;

    /**貸出ステータス：キャンセル済み */
    public static final int CANCELED = 3;
}
