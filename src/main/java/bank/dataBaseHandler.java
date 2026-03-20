package bank;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;

public class dataBaseHandler {

    public static MongoClient databaseClient = null;
    private static MongoDatabase database = null;
    public static MongoCollection<BankAccount> accountTable = null;
    public static MongoCollection<Transactions> transactionTable = null;
    static {

         String uri = "mongodb+srv://babideeran:babideeran@bank.l131pog.mongodb.net/?appName=Bank";

        CodecRegistry pojoCodecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

       
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .codecRegistry(pojoCodecRegistry)
                .build();
       
        try {
            databaseClient = MongoClients.create(settings);
            database = databaseClient.getDatabase("Bank");
            accountTable = database.getCollection("bankAccount", BankAccount.class);
            transactionTable = database.getCollection("transaction", Transactions.class);
        } catch (Exception e) {
            System.err.println("Failed to initialize MongoDB: " + e.getMessage());
        }
    }

}