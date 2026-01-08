PURPOSE OF THE APPLICATION:

This application is an e-commerce web app developed in React that simulates an online store selling handmade bags, created by the examinee.
The names of the bag models are inspired by the important women who surround the creator's life.
The project's goal is to provide an example of an online shopping experience, distinguishing between user and admin roles, and applying fundamental front-end concepts,
state management, routing, and data management.
This project has mainly an educational purpose but can be a good starting point for a real e-commerce site.

MAIN FEATURES:

- AUTHENTICATION AND ROLES
    Role-based route protection:
    - User -> can fill the cart and place an order
    - Admin -> can manage product stock and orders

- PRODUCT CATALOG
View bag list and product details with:
image
description
price
availability
Filters by category / search

- CART
Adding and removing products
Increasing/decreasing quantities
Automatic total calculation
Resetting the cart after completing the order

- ORDERS
  Send order with date and summary
  View orders:
    - User -> can only view their own orders
    - Admin -> can view all orders

- ADMIN AREA
Restricted access
Product stock management
View orders for all users
Access to the Orders and Restock pages
Access to the Cart and My Orders pages is not required

- CONTACT US
  One-way internal communication between users --> admins
     - User -> can send a message that can be viewed on the dedicated page by admin users
     - Admin -> can read all user messages (designed for manual responses via an email service)


TO TEST THE FEATURES:

1. Create a .env file in the root directory with the following structure:

VITE_USERS_JSON=
[
{"id":1,
"email":"",
"password":"",
"name":"",
"role":"user"},

{"id":2,
"email":"",
"password":"",
"name":"",
"role":"admin"}
]

Please note: The code must be written entirely on one line!

2. TO START THE "BACK-END" (JSON SERVER)
  - cd "bag e-commerce\server"
  - npm install
  - npm run dev

3. TO START THE FRONT END
  - cd "bag e-commerce"
  - npm install
  - npm run dev

- TECHNOLOGIES USED:
  - React – UI and components
  - Redux – Global state management
  - React Router – Route navigation and protection
  - Vite – Build tool and developer server
  - JavaScript
  - CSS
  - JSON Server
  - Git & GitHub

- FUTURE IMPLEMENTATIONS:
  - Deleting products from the catalog
  - Deleting orders
  - Dashboard for tracking orders
  - Stock removal
  - Visual preview of the bag while filling out the new product creation page
  - In-app messaging tool
  - Implementation of a backend that integrates
    - DB management
    - Online payments
    - Automatic email sending
