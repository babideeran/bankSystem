package bank;

import java.time.Instant;

import org.bson.codecs.pojo.annotations.BsonProperty;

import com.mongodb.client.model.Filters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BankAccount {

    String name;
    @BsonProperty("id")
    Integer id;
    Double balance;
    String email;
    String password;

    public void createAccount() {
        dataBaseHandler.accountTable.insertOne(this);

        System.out.println("ACCOUNT CREATED SUCCESSFULLY");
    }

    public static BankAccount login(String email,String password){

        BankAccount account = dataBaseHandler.accountTable.find(
        Filters.and(
            Filters.eq("email",email ), 
            Filters.eq("password", password)
        )
    ).first();

    if (account != null) {
        System.out.println("Login Successful! Welcome back, " + account.getName());
        return account;
    } else {
        System.out.println(" Login Failed: Incorrect ID or Password.");
        return null;
    }

    }

    public void deleteAccount() {
        dataBaseHandler.accountTable.deleteOne(Filters.eq("id", this.id));
        
        System.out.println("ACCOUNT DELETED SUCCESSFULLY");
    }

    public Double checkBalance() {
        return dataBaseHandler.accountTable.find(Filters.eq("id", this.id)).first().getBalance();
    }

    public void sendMoney(BankAccount receiver, Double Amount) {

        if (dataBaseHandler.accountTable.find( Filters.eq("id", receiver.id)).first() == null){
                System.out.println(receiver.id.toString()+" DOESN'T EXIST");
        }

        if (this.checkBalance() <= Amount) {
            System.out.println("NO BALANCE IN YOUR ACCOUNT");
            return;
        }


            new Transactions( this, receiver, Amount,Instant.now());

           
        
    }
}
