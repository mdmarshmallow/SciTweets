package handles;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dbconnection.DBConnect;

public class AccountService {
<<<<<<< HEAD
	private static List<Account> accounts = new ArrayList<Account>();
	static {
		/* in order to add a new account, just copy and paste the line below and
		 replace the name and username*/
		accounts.add(new Account("Stand Up To Cancer", "@SU2C"));
		accounts.add(new Account("Broad Institute", "@broadinstitute"));
		accounts.add(new Account("Node.js", "@nodejs"));
		accounts.add(new Account("Nature BIOTECH", "@NatureBiotech"));
		accounts.add(new Account("Nasa", "@Nasa"));
		accounts.add(new Account("Jetstrap", "@jetstrap"));
	}
=======
>>>>>>> ae706e2fec0a13209acf8101b663362c00b06e1d

	private List<Account> getAccountsFromDb() throws IOException {
		List<Account> accounts = new ArrayList<Account>();
		try {
			List<String[]> rawAccounts = DBConnect.selectAllFromHandles();
			for (int i = 0; i < rawAccounts.size(); i++) {
				String accountName = rawAccounts.get(i)[0];
				String accountUsername = rawAccounts.get(i)[1];
				int categoryID = Integer.parseInt(rawAccounts.get(i)[2]);
				accounts.add(new Account(accountName, accountUsername, categoryID));
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return accounts;
	}


	public List<Account> retrieveAccounts() throws IOException {
		return getAccountsFromDb();
	}
}