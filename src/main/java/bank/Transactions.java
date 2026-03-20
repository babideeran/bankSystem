package bank;

import java.time.Instant;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.TransactionBody;
import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Transactions {
    @BsonProperty("id")
    String id;
    String currentHash;
    String previousHash;
    Integer sender;
    Integer receiver;
    Double amount;
    Instant date;
    String status;

    public static String transferFund(BankAccount sender, BankAccount recevier, Double Amount) {

        try (ClientSession session = dataBaseHandler.databaseClient.startSession()) {

            TransactionBody<String> txnBody = new TransactionBody<String>() {
                public String execute() {

                    if (sender.checkBalance() <= Amount) {
                        throw new RuntimeException("Insufficient funds");

                    }

                    dataBaseHandler.accountTable.updateOne(session, Filters.eq("id", sender.id),
                            Updates.inc("balance", -Amount));

                    dataBaseHandler.accountTable.updateOne(session, Filters.eq("id", recevier.id),
                            Updates.inc("balance", Amount));

                    return "Transfer Successful";
                }

            };

            return session.withTransaction(txnBody);
        }

        catch (Exception e) {
            return "Transfer Failed: " + e.getMessage();
        }
    }

    public String getLatestHash() {
        Transactions lastTx = dataBaseHandler.transactionTable.find().sort(Sorts.descending("date")).first();
        if (lastTx == null) {
            return "0000000000000000000000000000000000000000000000000000000000000000";
        }
        return lastTx.currentHash;
    }

    public Transactions(BankAccount sender, BankAccount receiver, Double amount, Instant date) {
        this.id = UUID.randomUUID().toString();
        this.date = date;
        this.sender = sender.getId();
        this.receiver = receiver.getId();
        this.amount = amount;
        this.date = date;
        this.status = "Ongoing";
        try {
            this.previousHash = getLatestHash();

        } catch (Exception e) {
            System.out.println(e);
        }
        try {

            String[] temp1 = { this.receiver.toString(), this.sender.toString(), this.amount.toString(),
                    this.previousHash,
                    this.date.toString() };
            String temp2 = String.join("|", temp1);
            this.currentHash = Hasher.toHexString(Hasher.getSHA(temp2));

        } catch (Exception e) {
            System.out.println(e);
        }

        dataBaseHandler.transactionTable.insertOne(this);

        String status = transferFund(sender, receiver, amount);

        Boolean success = (status == "Transfer Successful") ? true : false;

        if (success) {
            dataBaseHandler.accountTable.updateOne(Filters.eq("id", this.id),
                    Updates.set("status", "Success"));

            System.out.println("transfer was success");

        } else {
            dataBaseHandler.accountTable.updateOne(Filters.eq("id", this.id),
                    Updates.set("status", "Failed"));

            System.out.println("transfer was failed");
        }

    }
}
