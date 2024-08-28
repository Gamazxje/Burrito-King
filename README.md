## Program Class Design
## Program Package Structure:

Model Package (model)
-Contains classes representing the core data and business logic.
View Package (view)
-Contains classes related to the user interface.
Controller Package (controller)
-Contains classes that handle user input and application logic.

## Essential Classes
## Model Classes
User

Purpose: Represents a user of the application.

Attributes:
username: String
password: String
firstName: String
lastName: String
isVIP: Boolean
email: String (only for VIP users)
credits: Integer (only for VIP users)
Methods:
editProfile(firstName, lastName, password)
upgradeToVIP(email)
addCredits(amount)
redeemCredits(amount)

Order

Purpose: Represents an order made by a user.

Attributes:
orderNumber: String
username: String
orderTime: LocalDateTime
totalPrice: double
status: String (e.g., "placed", "collected", "cancelled")
collectionTime: LocalDateTime
Methods:
addItem(item)
removeItem(item)
updateItemQuantity(item, quantity)
calculateTotalPrice()
setStatus(status)
setCollectionTime(time)
OrderItem

Purpose: Represents a food item in the order.

Attributes:
name: String
price: double
quantity: Integer
Methods:
getTotalPrice()

## View Classes
CheckoutView

Purpose: Handles the checkout process, displaying order summary, allowing users to redeem credits, and enter payment information.
Attributes:
orderSummaryListView: ListView
totalPriceLabel: Label
creditsLabel: Label
redeemCreditsField: TextField
cardNumberField: TextField
expiryDateField: TextField
cvvField: TextField

DashboardView

Purpose: Displays the user dashboard, showing user information, active orders, and providing navigation options.
Attributes:
userLabel: Label
ordersListView: ListView
Edit Profile Button
View Orders Button
Order Management Button
Logout Button
Shopping Basket Button
Upgrade to VIP Button

EditProfileView

Purpose: Allows users to edit their profile information.
Attributes:
firstNameField: TextField
lastNameField: TextField
passwordField: PasswordField
Save Changes Button
Back Button
LoginView

Purpose: Manages user login and registration.
Attributes:
usernameField: TextField
passwordField: PasswordField
Login Button
Register Button

OrderManagementView

Purpose: Manages orders, allowing users to view, collect, cancel, and export orders.
Attributes:
ordersListView: ListView
Collect Order Button
Cancel Order Button
Export Orders Button
Back Button
OrderView

Purpose: Displays details of user's orders.
Attributes:
ordersListView: ListView
Back Button

RegisterView

Purpose: Manages user registration.
Attributes:
usernameField: TextField
passwordField: PasswordField
firstNameField: TextField
lastNameField: TextField
Register Button
Cancel Button

ShoppingBasketView

Purpose: Manages the shopping basket, allowing users to add, remove, and update items, and proceed to checkout.
Attributes:
basketListView: ListView
Add Burrito Button
Add Fries Button
Add Soda Button
Remove Selected Button
Update Quantity Button
Checkout Button
Place Order Button
Back Button

UserProfileView

Purpose: Displays and allows editing of user profile information, including upgrading to VIP.
Attributes:
firstNameField: TextField
lastNameField: TextField
passwordField: PasswordField
emailField: TextField
Save Button
Cancel Button
Upgrade to VIP Button

## Controller Classes
CheckoutController

Purpose: Handles the checkout process, including validating payment information, placing orders, and managing user credits.
Attributes:
orderSummaryListView: ListView<OrderItem>
totalPriceLabel: Label
creditsLabel: Label
redeemCreditsField: TextField
cardNumberField: TextField
expiryDateField: TextField
cvvField: TextField
orderItems: List<OrderItem>
user: User
Methods:
setUser(User user)
setOrderItems(List<OrderItem> orderItems)
updateTotalPrice()
handlePlaceOrder()
placeOrder(double finalPrice, LocalDateTime orderTime)
validatePaymentDetails(String cardNumber, String expiryDate, String cvv)
generateOrderNumber()
showAlert(String title, String message)
navigateToDashboard()
handleBack(ActionEvent event)

DashboardController

Purpose: Manages the user dashboard, allowing navigation to profile editing, order viewing, order management, shopping basket, and VIP upgrade functionalities.
Attributes:
user: User
userController: UserController
Methods:
setUser(User user)
handleEditProfile(ActionEvent event)
handleViewOrders(ActionEvent event)
handleShoppingBasket(ActionEvent event)
handleOrderManagement(ActionEvent event)
handleCheckout(ActionEvent event)
handleLogout(ActionEvent event)
handleUpgradeToVIP(ActionEvent event)
showAlert(String title, String message)

EditProfileController

Purpose: Handles the editing of user profiles, allowing users to update their personal information and save changes to the database.
Attributes:
firstNameField: TextField
lastNameField: TextField
passwordField: PasswordField
user: User
Methods:
setUser(User user)
handleSaveChanges()
handleBack()
showAlert(String title, String message)

LoginController

Purpose: Manages user login and registration, allowing users to log in or navigate to the registration form.
Attributes:
usernameField: TextField
passwordField: PasswordField
userController: UserController
Methods:
handleLogin(ActionEvent event)
showRegisterForm(ActionEvent event)
showAlert(Alert.AlertType alertType, String title, String message)

OrderManagementController

Purpose: Manages order-related actions such as viewing, collecting, canceling, and exporting orders.
Attributes:
ordersListView: ListView<Order>
user: User
Methods:
setUser(User user)
loadOrders()
getOrdersForUser(String username) 
handleCollectOrder()
handleCancelOrder()
updateOrderStatus(String orderNumber, String status, LocalDateTime collectionTime)
handleExportOrders()
exportOrdersToCSV(File file, List<Order> orders)
handleBack(ActionEvent event)
showAlert(String title, String message)

OrderController

Purpose: Displays order details and handles navigation back to the dashboard.
Attributes:
ordersListView: ListView<Order>
user: User
Methods:
setUser(User user)
handleBack()
showAlert(String title, String message)

RegisterController

Purpose: Manages user registration, allowing new users to create an account.
Attributes:
usernameField: TextField
passwordField: PasswordField
firstNameField: TextField
lastNameField: TextField
Methods:
handleRegister()
handleCancel()
showAlert(String title, String message)

ShoppingBasketController

Purpose: Manages the shopping basket, including adding, removing, updating items, and proceeding to checkout or placing an order.
Attributes:
basketListView: ListView<OrderItem>
user: User
Methods:
setUser(User user)
handleAddBurrito()
handleAddFries()
handleAddSoda()
handleRemoveSelected()
handleUpdateQuantity()
handleCheckout()
handleAddOrder()
handleBack(ActionEvent event)
addItemToBasket(String itemName)
getItemPrice(String itemName)
calculateTotalPrice(List<OrderItem> items)
navigateToCheckout(List<OrderItem> orderItems)
navigateToDashboard()
showAlert(String title, String message)

UserProfileController

Purpose: Displays and allows editing of user profile information, including upgrading to VIP status.
Attributes:
firstNameField: TextField
lastNameField: TextField
passwordField: PasswordField
emailField: TextField
user: User
Methods:
setUser(User user)
handleSave()
handleCancel()
handleUpgradeToVIP()
showAlert(String title, String message)

UserController

Purpose: Handles user-related actions and interactions, including user creation, login, profile editing, VIP upgrades, and credit management.
Attributes:
None (all methods are static or instance methods that work with provided parameters)
Methods:
createUser(String username, String password, String firstName, String lastName) 
login(String username, String password) 
editProfile(User user, String firstName, String lastName, String password) 
upgradeToVIP(User user, String email) 
updateCredits(User user, int credits) 

## Design Pattern Implemented

## Model-View-Controller (MVC) Design Pattern:
Model: Represents the data and business logic. Classes include User, Order, and OrderItem...etc
View: Manages the user interface and user input. Classes include UserProfileView, OrderView, DashboardView, and LoginView...etc
Controller: Handles the user input, updates the model, and refreshes the view. Classes include UserController, OrderController, and DashboardController...etc



## JUnit Tests Implemented
JUnit tests have been implemented to ensure the functionality and reliability of the core classes and methods. Below are the key tests:



Test creating a new user.
Test editing user profile.
Test upgrading user to VIP.
Test adding credits for VIP users.
Test redeeming credits for VIP users.

