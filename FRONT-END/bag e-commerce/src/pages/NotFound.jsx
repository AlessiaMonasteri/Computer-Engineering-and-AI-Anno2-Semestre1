import {Link} from 'react-router'

const NotFound = () => {
    return (
        <>
        <div className="page-not-found">
            <p>Page Not Found</p>
            <div><Link to = "/">Back to Home</Link></div>
        </div>
        </>
    )
}

export default NotFound