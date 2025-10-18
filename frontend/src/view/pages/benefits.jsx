import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faStar, faRocket, faCheckCircle, faMobileAlt, faHandshake, faLightbulb } from '@fortawesome/free-solid-svg-icons';
import { Link } from 'react-router-dom';

import benefitsData from './data/benefit-data.json';
import './pages-style/benefits.css';

function Benefits({ style }) {
  
  // Cái này giống như từ điển tra cứu icon. Nếu JSON có "icon": "faRocket" → thì nó sẽ lấy đúng faRocket từ đây để hiển thị.
  const iconMap = {
    faStar, faRocket, faCheckCircle, faMobileAlt, faHandshake, faLightbulb
  };


  /*   
  + benefitsData.map(...): duyệt qua từng mục trong file JSON.
  + ...b: sao chép toàn bộ dữ liệu của từng mục.
  + icon: iconMap[b.icon] || faStar:
  Nếu icon có trong iconMap → dùng icon đó,
  nếu không có → mặc định dùng ngôi sao (faStar).
  => Kết quả là mảng benefits đã sẵn sàng để render lên giao diện. */
  const benefits = benefitsData.map(b => ({ ...b, icon: iconMap[b.icon] || faStar}));

  return (
    <div className="benefits-container" style={style}>
      <div className="benefits-header">
        <h1>Make it a reality with aPieceOfJob</h1>
        <p>Make your dreams come true.</p>
      </div>
      <div className="benefits-grid">
        {benefits.map((benefit, index) => ( //Benefits duyệt hết tất cả các phần tử trong mảng benefits, benefit là từng phần tử trong JSON, index là chỉ số của phần tử đó trong mảng.
          <div key={index} className="benefit-card">
            <FontAwesomeIcon icon={benefit.icon} className="benefit-icon" />
            <h3>{benefit.title}</h3>
            <p>{benefit.description}</p>
          </div>
        ))}
      </div>
      <div className="benefits-footer">
        <p>Ready to get started? Join thousands of businesses and freelancers making things happen on aPieceOfJob.</p>
        <Link to="/register" className="cta-button-link">
          <button className="cta-button">Get Started</button>
        </Link>
      </div>
    </div>
  );
}

export default Benefits;