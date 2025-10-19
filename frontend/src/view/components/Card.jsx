import './style/Card.css';
import { Link } from 'react-router-dom';
function Card({ id, card, title, description, onSelect }) {

    return (
        <div className="card">
            <img src={card} alt="Card Image" />
            <div className="card-content">
                <h2>{title}</h2>
                <p>{description}</p>
                <Link to="/jobs">
                    <button
                        type="button"
                        data-id={id}
                        onClick={() => navigate('/jobs')}
                        aria-label={`Learn more about ${title}`}
                    >
                        Learn More
                    </button>
                </Link>
            </div>
        </div>
    );
}

export default Card;