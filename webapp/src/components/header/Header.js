import { useContext } from 'react';
import { UserContext } from "../../App";
import AppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import { Link } from "react-router-dom";

const Header = () => {
    const user = useContext(UserContext)

    return (
        <AppBar position="static">
            <Toolbar>
                <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                    Vending Machine{user ? ` - Hello, ${user?.username}` : ""}
                </Typography>
                {
                    user === null ? (
                        <Link style={{color: "white"}} to="/login">LOGIN</Link>
                    ) : <Link style={{color: "white"}} to="/logout">LOGOUT</Link>
                }
            </Toolbar>
        </AppBar>
    )
}

export default Header