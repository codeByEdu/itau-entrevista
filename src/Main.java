import java.util.ArrayList;
import java.util.List;

enum Type{
    PAYMENT, REFUND
}

class Transaction{
    String id;
    Double value;
    Type type;

    Transaction(String id, Double value, Type type){
        this.id = id;
        this.value = value;
        this.type = type;
    }
}

class Database{
    private List<Transaction> transactions;
    private static Database instance;

    public static Database getInstance(){
        if(instance == null) instance = new Database();
        return instance;
    }
    private Database(){
        transactions = new ArrayList<>();
    }

    public List<Transaction> getTransactions(){
        return transactions;
    }

    public void insertTransaction(Transaction transaction){
        transactions.add(transaction);
    }

    public Transaction getTransaction(String idPix) {
        for(Transaction transaction : transactions){
            if(transaction.id.equals(idPix)){
                return transaction;
            }
        }
        return null;
    }

}

public class Main{
    private static Database database = Database.getInstance();

    public static void main(String args[]) throws Exception {



        executePayment("ID123", 10.0);
        executePayment("ID124", 20.0);
        executePayment("ID125", 30.0);
        executePayment("ID126", 40.0);



        executePayment("ID127", 0.00);


        /* Terceiro teste */
        executePayment("ID126", 40.0);


        /* Quarto teste */
        executeRefund("ID126", 10.0);
        executeRefund("ID123", 10.0);


        /* Quinto teste */
        executeRefund("ID126", 40.0);


        System.out.println("Total transacionado: " + getTotalPaymentValue());
        System.out.println("Total devolvido: " + getTotalRefundValue());
    }

    public static Double getTotalPaymentValue(){
        List<Transaction> transactions =  database.getTransactions();

        return getTotalTransactionsValueByType(transactions, Type.PAYMENT);
    }

    public static Double getTotalRefundValue(){
        //retorna a soma de todas devolucoes do tipo REFUND realizadas
        List<Transaction> transactions =  database.getTransactions();

        return getTotalTransactionsValueByType(transactions, Type.REFUND);;
    }

    private static Double getTotalTransactionsValueByType(List<Transaction> transactions, Type refund) {
        Double result = 0.0;
        for (Transaction transaction : transactions) {
            if (transaction.type.equals(refund)) {
                result += transaction.value;
            }
        }
        return result;
    }

    public static void executePayment(String idPix, Double value) throws Exception {
        //adiciona na lista de transacoes um payment para o idPix recebido

        if(database.getTransaction(idPix) == null && value > 0.0){
            Transaction transaction = new Transaction(idPix, value, Type.PAYMENT);
            database.insertTransaction(transaction);
        }
        else{
            System.out.println("Valor da transação é invalido.");
        }

    }

    public static void executeRefund(String idPix, Double value) throws Exception {
        //adiciona na lista de transacoes um refund para o idPix recebido(pode ter mais de 1 refund pro mesmo id)

        Double limitRefund = 0.0;
        Double totalRefund = 0.0;
        Transaction transactionPayment = database.getTransaction(idPix);
        List<Transaction> transactions = database.getTransactions();

        for (Transaction transaction: transactions) {
            if(transaction.type.equals(Type.REFUND)){
               if(transaction.id.equals(idPix)){
                   totalRefund += transaction.value;
                }
            }
        }
        limitRefund = transactionPayment.value - totalRefund;


        if(value <= limitRefund){
            Transaction transaction = new Transaction(idPix,value,Type.REFUND);
            database.insertTransaction(transaction);
        }
    }

}