import { Routes, Route, Navigate } from "react-router-dom";
import { useSelector } from "react-redux";
import NavBar from "./layout/NavBar";
import Login from "./pages/Login";
import Home from "./pages/Home";
import Cart from "./pages/Cart";
import ProductDetail from "./pages/ProductDetail";
import OrdersPage from "./pages/OrdersPage";
import AdminOrdersPage from "./pages/Admin/AdminOrdersPage";
import ContactUs from "./pages/ContactUs";
import AdminStockPage from "./pages/Admin/AdminStockPage";
import NotFound from "./pages/NotFound";
import AdminNewProduct from "./pages/Admin/AdminNewProduct";

function App() {
  const currentUser = useSelector((state) => state.user.currentUser);

  return (
    <div id="app-container">
      <NavBar />

      <div className="main-layout">
        <Routes>
          
          {/* LOGIN */}
          <Route
            path="/login"
            element={currentUser ? <Navigate to="/products" replace /> : <Login />}
          />

          {/* HOME REDIRECT */}
          <Route path="/" element={<Navigate to="/products" replace />} />

          {/* PRODUCTS (HOME) */}
          <Route
            path="/products"
            element={
              currentUser ? <Home /> : <Navigate to="/login" replace />
            }
          />

          {/* PRODUCT DETAIL */}
          <Route
            path="/products/:id"
            element={
              currentUser ? <ProductDetail /> : <Navigate to="/login" replace />
            }
          />

          {/* CONTACT US */}
          <Route
            path="/contact"
            element={
              currentUser ? <ContactUs /> : <Navigate to="/login" replace />
            }
          />

          {/* CART */}
          <Route
            path="/cart"
            element={
              !currentUser 
                ? <Navigate to="/login" replace /> 
                : currentUser.role === "admin"
                  ? <Navigate to="/admin/orders" replace />     // <-- blocco admin
                  : <Cart />
            }
          />

          {/* USER ORDERS */}
          <Route
            path="/orders"
            element={
              !currentUser
                ? <Navigate to="/login" replace />
                : currentUser.role === "admin"
                  ? <Navigate to="/admin/orders" replace />     // <-- blocco admin
                  : <OrdersPage />
            }
          />

          {/* ADMIN ORDERS */}
          <Route
            path="/admin/orders"
            element={
              currentUser?.role === "admin" 
                ? <AdminOrdersPage />
                : <Navigate to="/products" replace />
            }
          />

          {/* ADMIN STOCK */}
          <Route
            path="/admin/stock"
            element={
              currentUser?.role === "admin" 
                ? <AdminStockPage />
                : <Navigate to="/products" replace />
            }
          />

          {/* ADMIN NEW PRODUCT */}
          <Route
            path="/admin/newproduct"
            element={
              currentUser?.role === "admin"
                ? <AdminNewProduct />
                : <Navigate to="/products" replace />
            }
          />
          
          <Route path="*" element={<NotFound />} />

        </Routes>
      </div>
    </div>
  );
}

export default App;
