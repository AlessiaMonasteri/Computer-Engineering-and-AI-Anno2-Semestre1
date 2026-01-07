export const addToCart = (product) => ({
    type: "cart/addToCart",
    payload: product
})

export const removeFromCart = (productId) => ({
    type: "cart/removeFromCart",
    payload: productId
})

export const increaseQuantity = (productId) => ({
    type: "cart/increaseQuantity",
    payload: productId
})

export const decreaseQuantity = (productId) => ({
    type: "cart/decreaseQuantity",
    payload: productId
})

export const clearCart = () => ({
    type: "cart/clearCart"
})