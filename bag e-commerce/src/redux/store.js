import {configureStore} from "@reduxjs/toolkit";
import cartReducer from "./cart/cartReducer";
import usersReducer from "./users/usersReducer";
import orderReducer from "./orders/orderReducer";

const store = configureStore({
    reducer: {
        cart: cartReducer,
        user: usersReducer,
        orders: orderReducer
    },
})

export default store