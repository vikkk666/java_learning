import java.util.*;
import java.sql.*;

class Banking
{
	private static final String url="jdbc:mysql:///banking_system";
	private static final String username="root";
	private static final String password="root";
	
	public static void main(String ar[])
	{
		System.out.println("*-----Welcome to banking system-----*");
		try
		{
			Scanner sc=new Scanner(System.in);
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con=DriverManager.getConnection(url,username,password);
			
			User user=new User(con,sc);
			Accounts acc=new Accounts(con,sc);
			Account_manager am=new Account_manager(con,sc);
			
			String email;
			long account_number;
			
			while(true)
			{
				System.out.println();
				System.out.println("     *     MENU     *");
				System.out.println();
				System.out.println("     1.LOGIN     ");
				System.out.println("     2.REGISTER");
				System.out.println("     3.EXIT");
				System.out.println("        Please choose a option...");
				int choise=sc.nextInt();
				switch(choise)
				{
					case 1:
					email=user.login();
					if(email!=null)
					{
					System.out.println();
					System.out.println("Loged IN");
						if(!acc.account_exist(email))
						{
							System.out.println();
							System.out.println("1.OPEN ACCOUNT");
							System.out.println("2.EXIT");
							if(sc.nextInt()==1)
							{
								account_number=acc.open_account(email);
								System.out.println("Account opened succesfully");
								System.out.println("Account no is = "+account_number);
							}
							else{
								break;
							}
						}
						account_number=acc.getAccountNo(email);
						int choise_2=0;
						while(choise_2!=5){
							System.out.println("");
							System.out.println("1.Debit Money");
							System.out.println("2.Cretid Money");
							System.out.println("3.Transfer Money");
							System.out.println("4.Check Balance");
							System.out.println("5.Log out");
							System.out.println("Enter your choise : ");
							choise_2=sc.nextInt();
							switch(choise_2){
								case 1:
								am.debit_money(account_number);
								break;
								case 2:
								am.credit_money(account_number);
								break;
								case 3:
								am.transfer_money(account_number);
								break;
								case 4:
								am.get_balance(account_number);
								break;
								case 5:
								break;
								default:
								System.out.println("Please enter valid option");
								break;
							}
						}
						
					}
					else{
						System.out.println("Incorrect email & password ");
					}
					break;
					case 2:
					user.register();
					System.out.println("You'registered succesfully");
					return;
					case 3:
					System.out.println("e");
					return;
					default:
					System.out.println("Please select valid option");
					break;
					
				}
			
			return;
			
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
			
		}
		
	}
}





class User
{
	Connection con;
	Scanner sc;
	User(Connection con,Scanner sc)
	{
		this.con=con;
		this.sc=sc;
	}   
	public String login()
	{
		sc.nextLine();
		System.out.print("Enter E-mail : ");
		String email= sc.nextLine();
		System.out.print("Enter Password : ");
		String password= sc.nextLine();	
		try{
			Class.forName("com.mysql.cj.jdbc.Driver");
			String login_query="SELECT * FROM USER WHERE email='"+email+"' AND password='"+password+"' " ;
			Statement st=con.createStatement();
			ResultSet rs=st.executeQuery(login_query);
			if(rs.next()){
				return email;
			}
			else{
				return null;
			}
			//con.close();
		}
		catch(Exception e)
		{
		System.out.println(e);
		}
		return null;
	}
	public void register(){
		sc.nextLine();
		System.out.println("Enter Full Name :");
		String full_name=sc.nextLine();	
		//String full_name=sc.nextLine();
		
		System.out.println("Enter E-mail :"); 
		//String password=sc.nextLine();	
		String email=sc.nextLine();
		
		System.out.println("Enter Password :");
		String password=sc.nextLine();	
		if(user_exist(email)){
			System.out.println("USER ALREADY EXISTS");
			return;
		}
		try{
			Class.forName("com.mysql.cj.jdbc.Driver");
			String register_query="INSERT INTO USER VALUES('"+full_name+"','"+email+"','"+password+"')";
			Statement st=con.createStatement();
			st.executeUpdate(register_query);
			//con.close();
		}
		catch(Exception e)
		{
		System.out.println(e);
		}
	}
	public boolean user_exist(String email){
		try{
			Class.forName("com.mysql.cj.jdbc.Driver");
			String query="SELECT * FROM USER WHERE email='"+email+"'" ;
			Statement st=con.createStatement();
			ResultSet rs=st.executeQuery(query);
			if(rs.next()){
				return true;
			}
			else{
				return false;
			}
			//con.close();
		}
		catch(Exception e)
		{
		System.out.println(e);
		}
				return false;
	}
}

class Accounts
{
	Connection con;
	Scanner sc;
	Accounts(Connection con,Scanner sc)
	{
		this.con=con;
		this.sc=sc;
	}
	public long open_account(String email){
		if(!account_exist(email)){
			sc.nextLine();
			System.out.println("Enter Full Name :");
			String full_name=sc.nextLine();	
			System.out.println("Initial_amount :"); 
			double initial_amount=sc.nextDouble();
			System.out.println("Create Pin :");
			int pin=sc.nextInt();
			long account_number=genrateAccountNo();
			try{
			Class.forName("com.mysql.cj.jdbc.Driver");
			String open_account="INSERT INTO ACCOUNTS VALUES('"+account_number+"','"+full_name+"','"+email+"','"+initial_amount+"','"+pin+"')";
			Statement st=con.createStatement();
			st.executeUpdate(open_account);
			return account_number;
			}
			catch(Exception e){
				System.out.println(e);
			}
		}
		else{
			System.out.println("Account creation failed");
		}
		throw new RuntimeException("Account already exist with this email");
		}
	public boolean account_exist(String email){
		try{
			String account_exist="SELECT * FROM ACCOUNTS WHERE email='"+email+"' ";
			Statement st=con.createStatement();
			ResultSet rs=st.executeQuery(account_exist);
			if(rs.next()){
				return true;
			}
			else{
				return false;
			}
			
		}
		catch(Exception e){
			System.out.println(e);
		}
		return false;
	}
	public long genrateAccountNo(){
		try{
			String genrate_account_no="SELECT account_number from ACCOUNTS ORDER BY account_number DESC LIMIT 1";
			Statement st=con.createStatement();
			ResultSet rs=st.executeQuery(genrate_account_no);
			if(rs.next()){
				long last_account_number=rs.getLong("account_number");
				return last_account_number+1;
			}
			else{
				return 10000100;
			}
		}
		catch(Exception e){
			System.out.println(e);
		}
		return 10000100;
	}
	public long getAccountNo(String email){
		try{
			String getAccountNo="SELECT * FROM ACCOUNTS WHERE email='"+email+"'";
			Statement st=con.createStatement();
			ResultSet rs=st.executeQuery(getAccountNo);
			if(rs.next()){
				return rs.getLong("account_number");
			}
		}
		catch(Exception e){
			System.out.println(e);
		}
		throw new RuntimeException("Account Number does'nt Found");
	}
}

class Account_manager
{
	Connection con;
	Scanner sc;
	Account_manager(Connection con,Scanner sc)
	{
		this.con=con;
		this.sc=sc;
	}
	public void debit_money(long account_number){
		sc.nextLine();
		System.out.print("ENTER AMOUNT : ");
		double amount=sc.nextInt();
		System.out.print("ENTER PIN : ");
		int pin=sc.nextInt();
		String fetch_query="SELECT * FROM ACCOUNTS WHERE account_number='"+account_number+"' AND pin='"+pin+"'";
		try{
			con.setAutoCommit(false);
			if(account_number!=0){
			Statement st=con.createStatement();
			ResultSet rs=st.executeQuery(fetch_query);
			if(rs.next()){
				double currunt_balance=rs.getDouble("balance");
				if(amount<=currunt_balance){
					currunt_balance-=amount;
					String debit_money="UPDATE ACCOUNTS SET balance='"+currunt_balance+"' WHERE account_number='"+account_number+"'";
					//Statement st=con.createStatement();
					int rowsaffected=st.executeUpdate(debit_money);
					if(rowsaffected>0){
						System.out.println();
						System.out.println("Rs."+amount+" debited succesfully");
						con.commit();
						con.setAutoCommit(true);	
					}
					else{
						System.out.println("Transaction failed");
						con.rollback();
						con.setAutoCommit(true);
						
					}
				}
				else{
						System.out.println("Insufficient Balance");
				}
			}
			else{
				System.out.println("Incorrect PIN");
			}
		}
		}
		catch(Exception e){
			
		}
	}
	public void credit_money(long account_number){
		sc.nextLine();
		System.out.print("ENTER AMOUNT : ");
		double amount=sc.nextInt();
		System.out.print("ENTER PIN : ");
		int pin=sc.nextInt();
		String fetch_query="SELECT * FROM ACCOUNTS WHERE account_number='"+account_number+"' AND pin='"+pin+"'";
		try{
			con.setAutoCommit(false);
			if(account_number!=0){
			Statement st=con.createStatement();
			ResultSet rs=st.executeQuery(fetch_query);
			if(rs.next()){
				double currunt_balance=rs.getDouble("balance");
				
					currunt_balance+=amount;
					String debit_money="UPDATE ACCOUNTS SET balance='"+currunt_balance+"' WHERE account_number='"+account_number+"'";
					//Statement st=con.createStatement();
					int rowsaffected=st.executeUpdate(debit_money);
					if(rowsaffected>0){
						System.out.println();
						System.out.println("Rs."+amount+" Credited succesfully");
						con.commit();
						con.setAutoCommit(true);	
					}
					else{
						System.out.println("Transaction failed");
						con.rollback();
						con.setAutoCommit(true);
						
					}
			}
			else{
						System.out.println("Incorrect PIN");
				}
		}
		}
		catch(Exception e){
			
		}
	}
	public void transfer_money(long sender_account_number)throws SQLException{
		sc.nextLine();
		System.out.println("To Account Number : ");
		long receiver_account_number=sc.nextLong();
		System.out.println("Amount : ");
		double amount=sc.nextDouble();
		System.out.println("Enter PIN : ");
		int pin=sc.nextInt();
		String sender_query="SELECT * FROM ACCOUNTS WHERE account_number='"+sender_account_number+"' AND pin='"+pin+"'";
		String receiver_query="SELECT * FROM ACCOUNTS WHERE account_number='"+receiver_account_number+"'";
		try{
			con.setAutoCommit(false);
			if(sender_account_number!=0&&receiver_account_number!=0){
			//Statement st1=con.createStatement();
			//Statement st2=con.createStatement();
			//ResultSet rs1=st1.executeQuery(sender_query);
			PreparedStatement ps=con.prepareStatement(sender_query);
			ResultSet rs=ps.executeQuery();
			if(rs.next()){
				double sender_currunt_balance=rs.getDouble("balance");
				//double receiver_currunt_balance=rs2.getDouble("balance");
				if(amount<=sender_currunt_balance){
					//sender_currunt_balance-=amount;
					//receiver_currunt_balance+=amount;
					String debit_query="UPDATE ACCOUNTS SET balance = balance - ? WHERE account_number='"+sender_account_number+"'";
					String credit_query="UPDATE ACCOUNTS SET balance = balance + ? WHERE account_number='"+receiver_account_number+"'";
					PreparedStatement dps=con.prepareStatement(debit_query);
					PreparedStatement cps=con.prepareStatement(credit_query);
					dps.setDouble(1,amount);
					cps.setDouble(1,amount);
					int rowsaffected1=dps.executeUpdate();
					int rowsaffected2=cps.executeUpdate();
					if(rowsaffected1>0&&rowsaffected2>0){
						System.out.println("Trasaction Succesfull !");
						System.out.println("Rs. "+amount+" Transfer Succesfull !");
						//System.out.println("Balance : "+sender_currunt_balance);
						con.commit();
						con.setAutoCommit(true);
						return;
					}
					else{
						System.out.println("Trasaction Failed  !!");
						con.rollback();
						con.setAutoCommit(true);
					}
				}
				else{
						System.out.println("Insufficient Balance !!");
				}
			}
			else{
						System.out.println("Invalid PIN!!");
			}
			}
			else{
						System.out.println("Invalid Account No.!!");
			}		
		}
		catch(Exception e){
			System.out.println(e);
		}
		con.setAutoCommit(true);
	}
	public void get_balance(long account_number){
		sc.nextLine();
		System.out.println("Enter PIN : ");
		int pin=sc.nextInt();
		String get_balance="SELECT * FROM ACCOUNTS WHERE account_number='"+account_number+"' AND pin='"+pin+"'";
		try{
			Statement st=con.createStatement();
			ResultSet rs=st.executeQuery(get_balance);
			if(rs.next()){
			double balance=rs.getDouble("balance");
			System.out.println("Balance = "+balance);
			}
			else{
			System.out.println("Incorrect PIN");
			}
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
}