package handles;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dbconnection.DBConnect;

public class CategoryService {

	//returns a list of filled Category objects
	private List<Category> fillCategory() {
		AccountService accountService = new AccountService();
		List<Category> resultlist = new ArrayList<Category>();
		try {
			List<String[]> categories = sortCategoriesByAlphabet(DBConnect.selectFromCategory());
			List<Account> accounts = accountService.retrieveAccounts();
			//loop runs through the list of categories
			for (String[] categoryElement : categories) {
				//creates an Account ArrayList
				List<Account> relevantAccounts = new ArrayList<Account>();
				//runs through the entire list of accounts
				for (Account account : accounts) {
					//categoryElement[0] is the categoryID, checks if the ID's are equal in both objects
					if (Integer.parseInt(categoryElement[0]) == account.getCategory()) {
						//adds all the accounts that are in that category to the relevantAccounts list
						relevantAccounts.add(account);
					}
				}
				//if there are no accounts in that category
				if (relevantAccounts.isEmpty()) {
					relevantAccounts.add(new Account("No accounts in this category yet", "", 0));
				}
				//turns the relativeAccounts ArrayList to a Accounts array called accountsToAdd
				Account[] accountsToAdd = relevantAccounts.toArray(new Account[relevantAccounts.size()]);
				//adds Category object w/ the category name and the array of accounts
				resultlist.add(new Category(categoryElement[1], accountsToAdd));
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException
				| IOException e) {
			//what's error handling?
			e.printStackTrace();
		}
		return resultlist;
	}

	//works in the same way as the sortHandlesByAlphabet function in the AccountService class
	private List<String[]> sortCategoriesByAlphabet(List<String[]> categories) {
		List<String> categoriesToSort = new ArrayList<String>();
		List<String[]> finalCategories = new ArrayList<String[]>();
		for (String[] category : categories) {
			String categoryName = category[1];
			categoriesToSort.add(categoryName);
		}
		Collections.sort(categoriesToSort);
		for (String sortedCategory : categoriesToSort) {
			for (String[] category : categories) {
				if (sortedCategory == category[1]) {
					finalCategories.add(category);
					break;
				}
			}
		}
		return finalCategories;
	}

	public List<Category> retrieveCategories() {
		return fillCategory();
	}

}
