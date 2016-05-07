# Za
Pizza delivery franchise management system.

How Za is Organized:
There are two packages, "Za" (backend database functionality) and "ui", our core frontend. To run Za, run from ProgramLauncher.java. Additionally, we've included
a TablePopulator class which can be ran to wipe the database and repopulate with sample data found within the table_data directory. Those text files should be left
as is. 

Sample Use Case -- Placing an Order from an Unregistered Account
1) From the ProgramLaunch/LoginView, click "Sign up"
2) Enter your information. At the minimum, you'll need to provide a username and password. 
3) Click "Submit"
4) On the LoginView, enter the Username/PW you provided and click "Login". 
5) Click "Place Order" in the top left
6) Click on an item in the left panel
7) Click "Add" 
8) In the bottom left, click "Place Order"
9) Enter credit card information, or click "Pay by cash"\
10) Click "Submit"

Known Issues
1) "Forgot password" doesn't work (LoginView)
2) "Edit Profile" doesn't work (CustomerView)
3) Employees can't create customer profiles
4) In the Employee's "Create Order" view, order column titles can be dragged into different positions.
5) Multiselecting (click + drag, ctrl + click, shift + click) order items selects them, but clicking "Add"/"Remove" doesn't add/remove all the selected items.
6) In Past Orders view (customer perspective), "Quantity" column indicates payment type, not quantity.  
7) Customer order view: "Shopping car" --> "Shopping cart"
8) Employee view behaves strangely; switch between columns and click around to see. 