import { clearCart } from "../cart/cartActions"

export const submitOrder = orderData => {
    // Ritorna una funzione invece di un oggetto (redux-thunk)
    return async (dispatch) => {
        try{
            const response = await fetch("http://localhost:3001/orders",  {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(orderData)
            })
            const data = await response.json()
            console.log("Order submitted successfully! ", data)
            // Svuota il carrello solo dopo che l'ordine è stato salvato
            dispatch(clearCart())
        }
        catch (error) {
            console.log(error)
        }
    }
}