import styles from "./Footer.module.css"

const Footer = () => {
    return (
        <div className={styles.root}>
            <div className={styles.content}>Made by Jonathan Rösner - Photo by <a style={{ color: "white" }}
                href="https://unsplash.com/@valentinsteph?utm_source=unsplash&utm_medium=referral&utm_content=creditCopyText">Stéphan Valentin</a> on <a style={{ color: "white" }} href="https://unsplash.com/s/photos/vending-machine?utm_source=unsplash&utm_medium=referral&utm_content=creditCopyText">Unsplash</a></div>
        </div>
    )
}

export default Footer