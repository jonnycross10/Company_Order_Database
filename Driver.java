import java.sql.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
class Driver{  
   public static void main(String[] args)throws Exception{
      DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
     
      Connection con = DriverManager.getConnection("DBHostName", "csus", "student");
      
      Statement st=con.createStatement();
      
      try {
         st.execute("drop table company_order");
      } catch (SQLException s){ 
         System.out.println("unable to drop company_order");
      }
      
      
      try {
         st.execute("drop table company_item");
      } catch (SQLException s){ 
         System.out.println("unable to drop company_item");
      }
      
      
      
      //create tables
      
      String createItem = """
         create table company_item(
           item_id int primary key,
           item_name varchar(30) unique
           )""";
      st.executeQuery(createItem);
      
      
      String createOrder = """
         create table company_order(
           order_id  int primary key,
           order_date date,
           processed number(1),
           item_id int,
           quantity int not null,
           foreign key(item_id) references company_item(item_id)
         )""";
      
      st.executeQuery(createOrder);
      
      
      
      boolean running = true;
      
      //main loop
      while(running){
         System.out.print("Menu:\n1) Insert\n2) Delete\n3) Update\n4) View\n5) Quit\nEnter #: ");
         
         BufferedReader reader = new BufferedReader(
               new InputStreamReader(System.in));
    
         // Reading data using readLine
         String name = reader.readLine();
         int option = Integer.parseInt(name);
         
         switch(option){
            case(1):
               //insert
               System.out.println("option 1 chosen");
               
               //check if inserting order or item
               System.out.print("1) insert order\n2) insert item\nEnter #: ");
               String insertResponse = reader.readLine();
               int insertOption = Integer.parseInt(insertResponse);
               //orders
               if(insertOption == 1){
                  System.out.println("Inserting order");
                  System.out.print("Enter order_id: ");
                  String idResponse = reader.readLine();
                  System.out.print("Enter 0 for processed, 1 for unprocessed: ");
                  String processedResponse = reader.readLine();
                  System.out.print("Enter item_id: ");
                  String itemIdResponse = reader.readLine();
                  System.out.print("Enter quantity: ");
                  String quantityResponse = reader.readLine();
                  
                  String insertString= "insert into company_order values(" + idResponse + ",sysdate,"+ processedResponse +
                     ", " + itemIdResponse + ", " + quantityResponse + ")";
                  st.executeUpdate(insertString);
               }  
                         
               //items
               else if(insertOption == 2){
                  System.out.println("Inserting item");
                  System.out.print("Enter item_id: ");
                  String itemIdResponse = reader.readLine();
                  System.out.print("Enter item name: ");
                  String itemNameResponse = reader.readLine();               
                  String insertString= "insert into company_item values(" + itemIdResponse +", '" + itemNameResponse + "')";
                  st.executeUpdate(insertString);
                  
               }            
               break;
            case(2):
               //delete            
               System.out.println("option 2 chosen");
               System.out.print("1) Delete from Orders\n2) Delete from items\nEnter #: ");
               String optionResponse = reader.readLine();
               int deleteOption = Integer.parseInt(optionResponse);
               if(deleteOption ==1){
                  System.out.println("Deleting from Orders");
                  System.out.print("Delete by order id: ");
                  String orderIdResponse = reader.readLine();
                  
                  //get the item ID in case row we delete is the last FK reference
                  String orderFkReferences = "select item_id from company_order where order_id = " + orderIdResponse;
                  ResultSet rs = st.executeQuery(orderFkReferences);
                  String orderId = null;
                  while(rs.next()){
                     orderId = rs.getString(1);
                  }
                  System.out.println("Order id is " + orderId);
                  
                  //delete order
                  String queryString = "delete from company_order where order_id = " + orderIdResponse; 
                  st.executeUpdate(queryString);
                  
                   //if there are no more references for this foreign key, delete from parent table
                  String itemFkReferences = "select order_id from company_order where item_id = " + orderId;
                  ResultSet fkQuery = st.executeQuery(itemFkReferences);
                  int responseSize = 0;
                  while(fkQuery.next()){
                     responseSize++;
                  }
                  if(responseSize ==0){
                     String delParentRow = "delete from company_item where item_id = " + orderId;
                     st.executeUpdate(delParentRow);
                     System.out.println("Parent row deleted");
                  }
               }
               else if(deleteOption ==2){
                  System.out.println("Deleting from Items");
                  System.out.print("Delete by item id: ");
                  String itemIdResponse = reader.readLine();
                  
                  
                  //delete the item
                  String queryString = "delete from company_item where item_id = " + itemIdResponse;
                  st.executeUpdate(queryString);
                  
                 
               }
               break;
            case(3):
               //Update              
               System.out.println("option 3 chosen");
               System.out.print("Enter old item name: ");
               String oldNameResponse = reader.readLine();               
               System.out.print("Enter new item name: ");
               String newNameResponse = reader.readLine();
               
               String updateString = "update company_item set item_name = '" + newNameResponse + "' where item_name = '" + oldNameResponse + "'";
               st.executeUpdate(updateString);
               break;
            case(4):
               //View
               System.out.println("option 4 chosen");
               System.out.println("Here are results from the tables: ");
               String selectString = """
               select item.item_name, o.order_date, o.processed ,o.quantity
               from company_order o join company_item item on o.item_id= item.item_id""";
               
               ResultSet rs = st.executeQuery(selectString);
               System.out.println("item_name, order_date, processed, quantity");
               while (rs.next())             
                  System.out.println(rs.getString(1)+", " +rs.getString(2) + ", "+ rs.getString(3) + ", " + rs.getString(4));
               break;
            case(5):
               //Quit
               System.out.println("option 5 chosen");
               running = false;
               break;   
            default:
               System.out.println("Invalid option");
         }
          
        /*
         ResultSet rs=st.executeQuery("select * from test ");
         while (rs.next())
            System.out.print(rs.getString(1)+" " +rs.getString(2));
         */
      }
   }
}
