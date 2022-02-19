import styles from "./LoginForm.module.css"

import { TextField, Button, Select, MenuItem } from "@mui/material"
import SendIcon from '@mui/icons-material/Send';

const LoginForm = ({ state, setState, send, isLogin, error }) => {

    return (
        <div className={styles.formContainer}>
            <h2 className={styles.formHeader}>{isLogin ? "Login" : "Sign-up"}</h2>
            <div className={styles.formRow}>
                <TextField className={styles.textfield} fullWidth type="text" value={state.username} error={error}
                    id="username" name="username" onChange={(e) => setState(state => { return { ...state, username: e.target.value } })}
                    placeholder="Username" required />

            </div>
            <div className={styles.formRow}>
                <TextField fullWidth error={error}
                    type="password" id="password" name="password" value={state.password}
                    onChange={(e) => setState(state => { return { ...state, password: e.target.value } })}
                    placeholder="Password" required />
            </div>
            {
                isLogin ? null : (
                    <div className={styles.formRow}>
                        <Select fullWidth className={styles.textfield} value={state.authority}
                            type="text" id="authority" name="authority"
                            onChange={(e) => setState(state => { return { ...state, authority: e.target.value } })}
                            placeholder="Authority" required>
                                <MenuItem value={"BUYER"}>Buyer</MenuItem>
                                <MenuItem value={"SELLER"}>Seller</MenuItem>
                            </Select>
                    </div>
                )
            }
            <div className={styles.formRow}>
                <Button variant="contained" className={styles.button} endIcon={<SendIcon />}
                    onClick={send} type="submit">{isLogin ? "Sign In" : "Sign Up"}</Button>
            </div>
        </div>
    )
}

export default LoginForm