package handles;

import java.util.Arrays;

//defines a Category object
public class Category {
	private String categoryName;
	private Account[] accounts;
	
	public Category(String categoryName, Account[] accounts) {
		super();
		this.categoryName = categoryName;
		this.accounts = accounts;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Account[] getAccounts() {
		return accounts;
	}

	public void setAccounts(Account[] accounts) {
		this.accounts = accounts;
	}

	@Override
	public String toString() {
		return "Category [categoryName=" + categoryName + ", accounts=" + Arrays.toString(accounts) + "]";
	}
	
}
