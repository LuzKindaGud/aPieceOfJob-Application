import './pages-style/commerce.css';
import Card from '../components/Card.jsx';
import cardData from './data/data.json'; 

function Commerce({ style }) {
  // xử lý khi chọn card
  const handleSelect = (id) => {
    console.log("Card selected:", id);
    // có thể mở modal, điều hướng, v.v.
  };

  return (
    <div className="commerce-container" style={style}>
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
