package handles;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dbconnection.DBConnect;

//retrieves all the accounts and returns them in alphabetical order
public class AccountService {

	//returns the accounts in an ArrayList of Account objects
	private List<Account> getAccountsFromDb() throws IOException {
		List<Account> accounts = new ArrayList<Account>();
		try {
			List<String[]> rawAccounts = sortHandlesByAlphabet(DBConnect.selectAllFromHandles());
			//runs through the rawAccounts list, creates an Account object then adds that object to the list
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

	//returns an ArrayList of sorted handles, takes in an ArrayList of String arrays that contain the handle info
	private List<String[]> sortHandlesByAlphabet(List<String[]> handles) {
		List<String> handlesToSort = new ArrayList<String>();
		List<String[]> finalHandles = new ArrayList<String[]>();
		//fills the handlesToSort with the handle names only
		for (String[] handle : handles) {
			//handle[0] is the handle name 
			String handleName = handle[0];
			//adds the name to an ArrayList
			handlesToSort.add(handleName);
		}
		//sorts the ArrayList in alphabetical order
		Collections.sort(handlesToSort);
		//this for loop runs through the now sorted handlesToSort ArrayList
		for (String sortedHandles : handlesToSort) {
			//runs through the handle ArrayList that was inputted into the function
			for (String[] handle : handles) {
				//if the name of the handle and the name on the handlesToSort list match, add it to the finalHandles list
				if (sortedHandles == handle[0]) {
					finalHandles.add(handle);
					break;
				}
			}
		}
		return finalHandles;
	}
	
	public List<Account> retrieveAccounts() throws IOException {
		return getAccountsFromDb();
	}
}
