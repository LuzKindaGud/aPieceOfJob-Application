import './style/alterjobs.css';
function AlterJobsCard({id, title, img, onSelect}) {
    return (
        <div className="alter-jobs-header">
            <h1>{title}</h1>
            <img src={img} alt="Jobs Banner" className="alter-jobs-banner" />
            <button
                type="button"
                data-id={id}
                onClick={() => onSelect?.(id)}
                aria-label={`Learn more about ${title}`}
            >
                Learn More
            </button>
        </div>
    );
}

export default AlterJobsCard;