package karup002;

/**
 * Created by karup002 on 10/8/2016.
 *
 * A transaction is essentially a group of Items (aka. ItemSet)
 * each transaction is identified by a unique transactionId
 */
public class Transaction {
    private final int transactionId;
    private ItemSet items;

    public Transaction(int id) {
        transactionId = id;
        items = new ItemSet();
    }

    public int addItemToTransaction(int entry) {
        return items.addItem(entry);
    }

    public ItemSet getItems() {
        return items;
    }

    @Override
    public String toString() {
        return this.items.toString();
    }
}
