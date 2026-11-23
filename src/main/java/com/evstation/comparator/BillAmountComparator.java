package com.evstation.comparator;

import com.evstation.model.Bill;
import java.util.Comparator;

/**
 * Comparator that sorts completed invoices by revenue descending.
 */
public class BillAmountComparator implements Comparator<Bill> {

    @Override
    public int compare(Bill b1, Bill b2) {
        if (b1 == null && b2 == null) return 0;
        if (b1 == null) return 1;
        if (b2 == null) return -1;

        return Double.compare(b2.getTotalPayable(), b1.getTotalPayable());
    }
}
