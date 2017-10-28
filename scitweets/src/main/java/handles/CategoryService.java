package handles;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dbconnection.DBConnect;

public class CategoryService {

	private List<Category> fillCategory() {
		AccountService accountService = new AccountService();
		List<Category> resultlist = new ArrayList<Category>();
		try {
			List<String[]> categories = sortCategoriesByAlphabet(DBConnect.selectFromCategory());
			List<Account> accounts = accountService.retrieveAccounts();
			for (String[] categoryElement : categories) {
				List<Account> relevantAccounts = new ArrayList<Account>();
				for (Account account : accounts) {
					if (Integer.parseInt(categoryElement[0]) == account.getCategory()) {
						relevantAccounts.add(account);
					}
				}
				if (relevantAccounts.isEmpty()) {
					relevantAccounts.add(new Account("No accounts in this category yet", "", 0));
				}
				Account[] accountsToAdd = relevantAccounts.toArray(new Account[relevantAccounts.size()]);
				resultlist.add(new Category(categoryElement[1], accountsToAdd));
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException
				| IOException e) {
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
