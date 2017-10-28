package handles;

//creates the object that holds the handle information
public class Account {
	private String name;
	private String username;
	private int category;

	public Account(String name, String username, int category) {
		super();
		this.name = name;
		this.username = username;
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	
	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return "Account [name=" + name + ", username=" + username + ", category=" + category + "]";
	}

}
