import './style/Card.css';
function Card({ id, card, title, description, onSelect }) {

    return (
        <div className="card">
            <img src={card} alt="Card Image" />
            <div className="card-content">
                <h2>{title}</h2>
                <p>{description}</p>
                <button
                    type="button"
                    data-id={id}
                    onClick={() => onSelect?.(id)}
                    aria-label={`Learn more about ${title}`}
                >
                    Learn More
                </button>
            </div>
        </div>
    );
}

export default Card;