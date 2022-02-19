import { Paper } from '@mui/material';
import styles from "./MultipleSessions.module.css"
import { Link } from "react-router-dom"

const MultipleSessionsDialog = () => {

    return (
        <div className={styles.root}>
            <Paper>
                <div className={styles.content}>
                    <p>There are multiple sessions accessing this account right now.</p>
                    <h3>How do you want to continue?</h3>
                    <div className={styles.buttonBar}>
                        <Link style={{marginRight: "16px"}} variant="contained" to="/logout">Logout</Link>
                        <Link variant="contained" to="/">Continue</Link>
                    </div>
                </div>
            </Paper>
        </div>
    )
}

export default MultipleSessionsDialog