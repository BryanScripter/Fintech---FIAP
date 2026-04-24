import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Registro from './pages/Registro';
import Dashboard from './pages/Dashboard';
import Usuarios from './pages/Usuarios';
import Transacoes from './pages/Transacoes';
import Dividas from './pages/Dividas';
import NotFound from './pages/NotFound';
import './App.css';

function App() {
  const isAuthenticated = localStorage.getItem('isAuthenticated') === 'true';
  
  // Verificar se é admin
  const userStr = localStorage.getItem('user');
  const user = userStr ? JSON.parse(userStr) : {};
  const isAdmin = user.email?.toLowerCase() === 'admin@gmail.com';

  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/registro" element={<Registro />} />
        <Route 
          path="/" 
          element={isAuthenticated ? <Dashboard /> : <Navigate to="/login" />} 
        />
        <Route 
          path="/usuarios" 
          element={isAuthenticated && isAdmin ? <Usuarios /> : <Navigate to="/" />} 
        />
        <Route 
          path="/transacoes" 
          element={isAuthenticated ? <Transacoes /> : <Navigate to="/login" />} 
        />
        <Route 
          path="/dividas" 
          element={isAuthenticated ? <Dividas /> : <Navigate to="/login" />} 
        />
        <Route path="*" element={<NotFound />} />
      </Routes>
    </Router>
  );
}

export default App;

