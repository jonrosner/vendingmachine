import { useEffect, useState, useContext } from "react"
import { UserContext, SetUserContext } from "../../App";
import { useNavigate } from "react-router-dom"
import axios from "axios"
import Paper from '@mui/material/Paper';

import LoginForm from "../../components/loginForm/LoginForm"

import styles from "./Login.module.css"

const LoginPage = () => {
    const navigate = useNavigate()
    const [user, setUser] = [useContext(UserContext), useContext(SetUserContext)]
    const defaultState = {
        username: "",
        password: "",
        authority: "BUYER",
        error: false,
        isLogin: true
    }
    const [state, setState] = useState({ ...defaultState })

    async function doLogin() {
        try {
            const loginFormData = new FormData();
            loginFormData.append("username", state.username)
            loginFormData.append("password", state.password)
            const res = await axios({
                method: "post",
                url: "/api/login",
                data: loginFormData,
                headers: { "Content-Type": "multipart/form-data" },
            });
            setUser(res.data)
            if (res.headers["x-multiple-sessions"]) {
                navigate("/multipleSessions")
            } else {
                navigate("/")
            }
        } catch (e) {
            console.log(e)
            setState({ ...state, error: true })
        }
    }

    async function registerUser() {
        try {
            await axios.post("http://localhost:9000/api/user", {
                username: state.username,
                password: state.password,
                authority: state.authority
            })
            setState({...defaultState, isLogin: true})
        } catch (e) {
            console.log(e)
            setState({...defaultState, error: true})
        }
    }

    useEffect(() => {
        if (user !== null) {
            navigate("/")
        }
    }, [])

    return (
        <div className={styles.root}>
            <Paper className={styles.container}>
                <LoginForm state={state} setState={setState} send={state.isLogin ? doLogin : registerUser} error={state.error} isLogin={state.isLogin} />

                <div className={styles.helperContainer}>
                    {
                        state.isLogin ? (
                            <p>
                                No account yet? Register
                                <button className={styles.changeForm} onClick={() => setState({ ...defaultState, isLogin: false })}>here</button>
                            </p>
                        ) : (
                            <p>
                                Already got an account? Login
                                <button className={styles.changeForm} onClick={() => setState({ ...defaultState, isLogin: true })}>here</button>
                            </p>
                        )
                    }
                </div>
            </Paper>
        </div>
    )
}

export default LoginPage