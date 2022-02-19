import { useEffect, useContext } from "react"
import { SetUserContext } from "../../App"
import { useNavigate } from "react-router-dom"
import axios from "axios"

const Logout = () => {
    const navigate = useNavigate()
    const setUser = useContext(SetUserContext)

    useEffect(() => {
        async function logout() {
            try {
                axios.post("/api/logout")
                setUser(null)
                navigate("/login")
            } catch (e) {
                console.log(e)
            }
        }
        logout()
    }, [])
    return <></>
}

export default Logout