import { useState } from 'react'
import './App.css'

import Navbar from './components/navbar.jsx'
import Intro from './components/Intro.jsx'
import {Route, Routes, BrowserRouter as Router} from "react-router-dom";

function App() {
  const [showIntro, setShowIntro] = useState(true)

  const handleIntroComplete = () => {
    setTimeout(() => {
      setShowIntro(false);
    }, 0);
  }

  return (
    <>
      {showIntro && <Intro onComplete={handleIntroComplete} />}
      <Navbar />
      <Routes>
        <Route path="/about" element={<div>About</div>} />
        <Route path="/services" element={<div>Services</div>} />
        <Route path="/contact" element={<div>Contact</div>} />
        <Route path="/profile" element={<div>Profile</div>} />
      </Routes>
    </>
  )
}

export default App
