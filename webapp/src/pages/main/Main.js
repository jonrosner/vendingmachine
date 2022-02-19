import { useContext, useEffect, useState } from "react"
import { UserContext, SetUserContext } from "../../App"
import styles from "./Main.module.css"
import axios from "axios"

import {
    Button,
    Divider,
    TextField,
    DialogTitle,
    Dialog,
    Card,
    CardContent,
    IconButton
} from "@mui/material"

import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';


const inputElements = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "R", "0", ">"]
const defaultDigits = [null, null, null, null, null]
const coins = {
    "FIVE": 5,
    "TEN": 10,
    "TWENTY": 20,
    "FIFTY": 50,
    "HUNDRED": 100
}

const MainPage = () => {
    const [user, setUser] = [useContext(UserContext), useContext(SetUserContext)]
    const [state, setState] = useState({
        digits: defaultDigits,
        digitPos: 0,
        id: "",
        amount: 1,
        products: [],
        didLoadProducts: false,
        showDepositDialog: false,
        showBuyDialog: false,
        showErrorDialog: false,
        showEditProductDialog: false,
        change: [],
        productsPurchased: [],
        totalSpent: 0,
        productName: "",
        productAmount: "",
        productCost: "",
        error: "",
        productToEdit: {}
    })

    async function getProducts() {
        try {
            const res = await axios.get("/api/product")
            setState(state => ({ ...state, products: res.data, showEditProductDialog: false }))
        } catch (e) {
            console.log(e)
            setState(state => ({ ...state, error: e?.response?.data?.message, showErrorDialog: true }))
        }
    }

    useEffect(() => {
        getProducts()
    }, [])

    const handleInputClick = (el) => {
        if (el === "R") {
            setState({ ...state, digits: defaultDigits, digitPos: 0, id: "" })
        } else if (el === ">") {
            handleBuyClick()
        } else {
            handleDigitClick(el)
        }
    }

    const handleDigitClick = (el) => {
        if (state.digitPos === 0 && el === "0") {
            return
        }
        const newDigits = [...state.digits]
        newDigits[state.digitPos] = el
        const newDigitPos = state.digitPos === defaultDigits.length - 1 ? state.digitPos : state.digitPos + 1
        const newId = state.id + el
        setState({
            ...state,
            digits: newDigits,
            digitPos: newDigitPos,
            id: newId
        })
    }

    const handleBuyClick = async () => {
        try {
            const res = await axios.post("/api/buy", {
                productId: state.id,
                amount: state.amount
            })
            const buyResponse = res.data
            setState({
                ...state,
                ...buyResponse,
                showBuyDialog: true,
                digits: defaultDigits,
                digitPos: 0,
                id: ""
            })
            setUser({ ...user, deposit: 0 })
            getProducts()
        } catch (e) {
            console.log(e)
            setState({ ...state, error: e?.response?.data?.message, showErrorDialog: true })
        }
    }

    const handleDepositClick = async (coin) => {
        try {
            const res = await axios.post(`/api/deposit/${coin}`)
            const newUser = res.data
            setUser(newUser)
        } catch (e) {
            console.log(e)
            setState({ ...state, error: e?.response?.data?.message, showErrorDialog: true })
        }
        setState({ ...state, showDepositDialog: false })
    }

    const handleResetClick = async () => {
        try {
            const res = await axios.post("/api/reset")
            const change = res.data
            setUser({ ...user, deposit: 0 })
        } catch (e) {
            console.log(e)
            setState({ ...state, error: e?.response?.data?.message, showErrorDialog: true })
        }
    }

    const handleCloseBuyDialog = () => {
        setState({
            ...state,
            change: [],
            productsPurchased: [],
            totalSpent: 0,
            showBuyDialog: false
        })
    }

    const handleCreateProduct = async () => {
        try {
            const res = await axios.post("/api/product", {
                productName: state.productName,
                amountAvailable: state.productAmount,
                cost: state.productCost
            })
            const newProducts = [...state.products, res.data]
            setState({
                ...state,
                productName: "",
                productAmount: "",
                productCost: "",
                products: newProducts
            })
        } catch (e) {
            console.log(e)
            setState({ ...state, error: e?.response?.data?.message, showErrorDialog: true })
        }
    }

    const deleteProduct = async (product) => {
        try {
            await axios.delete(`/api/product/${product.id}`)
            await getProducts()
        } catch (e) {
            console.log(e)
            setState({ ...state, error: e?.response?.data?.message, showErrorDialog: true })
        }
    }

    const openEditProductDialog = (product) => {
        setState({
            ...state,
            showEditProductDialog: true,
            productToEdit: product
        })
    }

    const handleEditProductClick = async () => {
        try {
            await axios.put(`/api/product/${state.productToEdit.id}`, {
                productName: state.productToEdit.productName,
                amountAvailable: state.productToEdit.amountAvailable,
                cost: state.productToEdit.cost,
            })
            await getProducts()
        } catch (e) {
            console.log(e)
            setState({ ...state, error: e?.response?.data?.message, showErrorDialog: true })
        }
    }

    return (
        <div className={styles.root}>
            <div className={styles.machine}>
                {[...state.products, ...Array(12 - state.products.length).fill(null)].map((product, i) => (
                    <div key={`machineproduct_${i}`} className={styles.product}>
                        {product !== null ? (
                            <>
                                <p className={styles.productName}>{product.productName} ({product.amountAvailable})</p>
                                <div className={styles.productBox}>
                                    {
                                        user?.id === product.sellerId ? (
                                            <>
                                                <IconButton sx={{ color: "white" }} onClick={() => openEditProductDialog(product)}>
                                                    <EditIcon />
                                                </IconButton>
                                                <IconButton sx={{ color: "white" }} onClick={() => deleteProduct(product)}>
                                                    <DeleteIcon />
                                                </IconButton>
                                            </>
                                        ) : null
                                    }
                                </div>
                                <div className={styles.productIdContainer}>
                                    <p className={styles.productId}><div>{product.id}</div></p>
                                    <p className={styles.productCost}>{`$ ${product.cost}`}</p>
                                </div>
                            </>
                        ) : null}
                    </div>
                ))}
            </div>
            {
                user?.authorities[0] === "ROLE_BUYER" ? (
                    <div className={styles.panel}>
                        <div className={styles.buttonBar}>
                            <Button style={{ marginRight: "16px" }} variant="contained" onClick={() => setState({ ...state, showDepositDialog: true })}>DEPOSIT</Button>
                            <Button variant="contained" onClick={handleResetClick}>RESET</Button>
                        </div>

                        <Divider />

                        <p>Balance</p>
                        <h2>{user?.deposit}</h2>

                        <Divider />

                        <div className={styles.buyPanel}>
                            <div className={styles.display}>
                                <div className={styles.idDisplay}>
                                    {state.digits.map((digit, i) => (
                                        <div key={`digit_${i}`} className={styles.idDigit}>
                                            <div>{digit}</div>
                                        </div>
                                    ))}
                                </div>
                                <TextField className={styles.amount} onChange={e => {
                                    if (e.target.value >= 1) {
                                        setState({ ...state, amount: e.target.value })
                                    }
                                }}
                                    type="number" variant="outlined" id="amount" name="amount" placeholder="Amount" value={state.amount} />
                            </div>
                            <div className={styles.inputPanel}>
                                {inputElements.map(el => (
                                    <button key={`input_${el}`} className={styles.inputElement} onClick={() => handleInputClick(el)}>
                                        <div>{el}</div>
                                    </button>
                                ))}
                            </div>
                        </div>
                    </div>
                ) : (
                    <div className={styles.panel}>
                        <h2>Add a product</h2>

                        <Divider />

                        <div style={{ maxWidth: "200px" }}>
                            <TextField sx={{ marginBottom: "16px" }} fullWidth type="text" value={state.productName}
                                onChange={e => setState({ ...state, productName: e.target.value })}
                                id="productname" name="productname" placeholder="Product Name" required />

                            <TextField sx={{ marginBottom: "16px" }} fullWidth type="number" value={state.productAmount}
                                onChange={e => setState({ ...state, productAmount: e.target.value })}
                                id="productamount" name="productamount" placeholder="# Available" required />

                            <TextField sx={{ marginBottom: "16px" }} fullWidth type="number" value={state.productCost}
                                onChange={e => setState({ ...state, productCost: e.target.value })}
                                id="productcost" name="productcost" placeholder="Cost" required />
                        </div>
                        <Button variant="contained" onClick={handleCreateProduct}>CREATE</Button>
                    </div>
                )
            }

            <Dialog open={state.showDepositDialog} onClose={() => setState({ ...state, showDepositDialog: false })}>
                <div className={styles.depositDialog} >
                    <DialogTitle>Deposit</DialogTitle>
                    <p>Click a coin to deposit it.</p>
                    <div className={styles.inputPanel}>
                        {Object.entries(coins).map(([key, value]) => (
                            <button key={`coin_${key}`} className={styles.inputElement} onClick={() => handleDepositClick(key)}>
                                <div>{value}</div>
                            </button>
                        ))}
                    </div>
                </div>
            </Dialog>

            <Dialog open={state.showBuyDialog} onClose={handleCloseBuyDialog}>
                <div className={styles.buyDialog} >
                    <DialogTitle>{`Total Cost: ${state.totalSpent}`}</DialogTitle>
                    <div className={styles.purchasedProductContainer}>
                        {state.productsPurchased.map((product, i) => (
                            <div key={`product_${i}`} className={styles.purchasedProduct}>
                                <Card>
                                    <CardContent>
                                        <p>{product.productName}</p>
                                    </CardContent>
                                </Card>
                            </div>
                        ))}
                    </div>
                    <p>Your change</p>
                    <div className={styles.changeContainer}>
                        {state.change.map((coin, i) => (
                            <div key={`coin_${i}`} className={styles.inputElement}>
                                <div>{coins[coin]}</div>
                            </div>
                        ))}
                    </div>
                </div>
            </Dialog>

            <Dialog open={state.showErrorDialog} onClose={() => setState({ ...state, showErrorDialog: false })}>
                <div className={styles.buyDialog} >
                    <DialogTitle>An error occured</DialogTitle>
                    <p>{state.error}</p>
                </div>
            </Dialog>

            <Dialog open={state.showEditProductDialog} onClose={() => setState({ ...state, showEditProductDialog: false, productToEdit: {} })}>
                <div className={styles.panel}>
                    <h2>{`Edit Product ${state.productToEdit?.id}`}</h2>

                    <Divider />

                    <div style={{ maxWidth: "200px" }}>
                        <TextField sx={{ marginBottom: "16px" }} fullWidth type="text" value={state.productToEdit.productName}
                            onChange={e => setState({ ...state, productToEdit: {...state.productToEdit, productName: e.target.value} })}
                            id="productname" name="productname" placeholder="Product Name" required />

                        <TextField sx={{ marginBottom: "16px" }} fullWidth type="number" value={state.productToEdit.amountAvailable}
                            onChange={e => setState({ ...state, productToEdit: {...state.productToEdit, amountAvailable: e.target.value} })}
                            id="productamount" name="productamount" placeholder="# Available" required />

                        <TextField sx={{ marginBottom: "16px" }} fullWidth type="number" value={state.productToEdit.cost}
                            onChange={e => setState({ ...state, productToEdit: {...state.productToEdit, cost: e.target.value} })}
                            id="productcost" name="productcost" placeholder="Cost" required />
                    </div>
                    <Button variant="contained" onClick={handleEditProductClick}>EDIT</Button>
                </div>
            </Dialog>
        </div>
    )
}

export default MainPage