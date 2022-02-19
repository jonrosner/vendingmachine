import styles from "./Page.module.css"

import Header from "../components/header/Header.js"
import Footer from "../components/footer/Footer.js"

const Page = ({ children }) => {
    return (
        <div className={styles.root}>
            <Header className={styles.header} />
            <main>{children}</main>
            <Footer className={styles.footer} />
        </div>
    )
}

export default Page