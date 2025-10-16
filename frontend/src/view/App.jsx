import { useState } from 'react'

import Navbar from './components/navbar.jsx'
import Intro from './components/Intro.jsx'
import Home from './pages/home.jsx'
import Commerce from './pages/commerce.jsx'

import {Route, Routes, BrowserRouter as Router} from "react-router-dom";

function App() {
  const [showIntro, setShowIntro] = useState(true)

  const handleIntroComplete = () => {
    // mark intro finished so page animations can start
    document.body.classList.add('intro-finished');
    // then hide the Intro component (keeps a tick for layout)
    setTimeout(() => {
      setShowIntro(false);
    }, 0);
  }

  return (
    <>
      {showIntro && <Intro onComplete={handleIntroComplete} />}
      <Navbar />
      <Routes>
        <Route 
          path="/" 
          element={
            <>
              <Home />
              <Commerce style={{zIndex: 10}}/>
            </> 
        }/>
      </Routes>
    </>
  )
}

export default App
