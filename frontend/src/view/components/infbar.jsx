import React from 'react';
import './style/infbar.css';

const logos = [
  { name: 'Google', url: 'https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/Google_2015_logo.svg/160px-Google_2015_logo.svg.png' },
  { name: 'Microsoft', url: 'https://blogs.microsoft.com/wp-content/uploads/2012/08/8867.Microsoft_5F00_Logo_2D00_for_2D00_screen.jpg' },
  { name: 'Netflix', url: 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/08/Netflix_2015_logo.svg/1200px-Netflix_2015_logo.svg.png' },
  { name: 'Spotify', url: 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/84/Spotify_icon.svg/1982px-Spotify_icon.svg.png' },
  { name: 'Adobe', url: 'https://www.adobe.com/homepage/assets/product-icons/jpg/default.jpg?width=1200&format=pjpg&optimize=medium' },
  { name: 'Meta', url: 'https://a.storyblok.com/f/182663/2000x1125/b7517efede/meta-platforms_from-dorm-room-to-global-giant.png/m/3840x0/filters:quality(85)' }
];

function Infobar() {
  // Nhân đôi logo để hiệu ứng cuộn không đứt đoạn
  const extendedLogos = [...logos, ...logos];

  return (
    <div className="infobar-container">
      <p className="infobar-title">TRUSTED BY LEADING COMPANIES</p>
      <div className="infobar-scroller">
        <div className="infobar-track">
          {extendedLogos.map((logo, index) => (
            <div className="logo-item" key={index}>
              <img src={logo.url} alt={`${logo.name} logo`} />
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default Infobar;
