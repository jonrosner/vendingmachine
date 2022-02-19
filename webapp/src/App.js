import {
  Routes,
  Route,
  Link,
  useNavigate
} from "react-router-dom";
import Login from "./pages/login/Login.js"
import Logout from "./pages/logout/Logout.js";
import Page from "./pages/Page.js"
import Main from "./pages/main/Main.js"
import MultipleSessionsDialog from "./pages/multipleSessions/MultipleSessions.js";

import { useState, useEffect, createContext } from "react"
import axios from "axios";

const UserContext = createContext(null);
const SetUserContext = createContext(null);

function App() {
  const navigate = useNavigate()
  const [user, setUser] = useState(null)

  useEffect(() => {
    async function getUser() {
      try {
        const res = await axios.get("/api/user");
        console.log("user", res)
        setUser(res.data)
      } catch (e) {
        navigate("/login")
      }
    }
    getUser()
  }, [])

  return (
    <div className="App">
      <UserContext.Provider value={user}>
        <SetUserContext.Provider value={setUser}>
          <Routes>
            <Route path="/" element={<Page><Main /></Page>} />
            <Route path="/login" element={<Page><Login /></Page>} />
            <Route path="/logout" element={<Page><Logout /></Page>} />
            <Route path="/multipleSessions" element={<Page><MultipleSessionsDialog /></Page>} />
          </Routes>
        </SetUserContext.Provider>
      </UserContext.Provider>
    </div>
  );
}

export {
  UserContext,
  SetUserContext
}

export default App;
