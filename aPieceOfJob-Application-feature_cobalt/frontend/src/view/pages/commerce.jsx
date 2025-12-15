import './pages-style/commerce.css';
import Card from '../components/Card.jsx';
import cardData from './data/data.json';
import { useNavigate } from 'react-router-dom';

function Commerce({ style }) {
  const navigate = useNavigate();

  // xử lý khi chọn card
  const handleSelect = (id) => {
    console.log("Card selected:", id);
    // Điều hướng đến trang jobs
    navigate('/jobs');
  };

  return (
    <div className="commerce-container" style={style}>
      <h2 className="commerce-title">Explore Our Commerce Solutions</h2>
      <div className="commerce-content">
        {cardData.map((item) => (
          <Card
            key={item.id}
            id={item.id}
            card={item.card}
            title={item.title}
            description={item.description}
            onSelect={handleSelect}
          />
        ))}
      </div>
    </div>
  );
}

export default Commerce;
