import { useState } from 'react';

// Components
import Navbar from './components/navbar.jsx';
import Intro from './components/Intro.jsx';
import Footer from './components/footer.jsx';
import Infbar from './components/infbar.jsx';

// Pages
import Home from './pages/home.jsx';
import Commerce from './pages/commerce.jsx'; 
import About from './pages/benefits.jsx';

import { Route, Routes } from "react-router-dom";

function App() {
  const [showIntro, setShowIntro] = useState(true);

  const handleIntroComplete = () => {
    document.body.classList.add('intro-finished');
    setTimeout(() => {
      setShowIntro(false);
    }, 0);
  };

  return (
    <div className="app-wrapper">
      {showIntro && <Intro onComplete={handleIntroComplete} />}
      <Navbar />
      <main className="main-content">
        <Routes>
          <Route
            path="/"
            element={
              <>
                <Home />
                <Infbar style={{ zIndex: 10 }} />
                <About style={{ zIndex: 10 }} />
                <Commerce style={{ zIndex: 10 }} />
              </>
            }
          />
        </Routes>
        <Footer style={{ zIndex: 0}}/>
      </main>
    </div>
  );
}

export default App;