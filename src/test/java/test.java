import java.time.Instant;

import bank.BankAccount;
import bank.Transactions;

//    BankAccount a1 = new BankAccount("babi",1,10.5,"babideeran695@gmail.com","12345678");
      //  a1.createAccount();
public class test {

    public static void main(String[] args) {




        BankAccount a1 = BankAccount.login("babideeran695@gmail.com", "12345678");
        BankAccount a2 = BankAccount.login("babideeran694@gmail.com", "12345678");
        System.out.println(a2.checkBalance());
              
    }
}
