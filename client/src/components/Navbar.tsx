import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './Navbar.css';

function Navbar() {
  const navigate = useNavigate();
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState<boolean>(false);

  const handleLogout = () => {
    localStorage.removeItem('isAuthenticated');
    localStorage.removeItem('user');
    navigate('/login');
  };

  const toggleMobileMenu = () => {
    setIsMobileMenuOpen(!isMobileMenuOpen);
  };

  const userStr = localStorage.getItem('user');
  const user = userStr ? JSON.parse(userStr) : {};
  const isAdmin = user.email?.toLowerCase() === 'admin@gmail.com';

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <i className="fas fa-chart-line"></i>
        <span>Fintech</span>
      </div>
      
      <div className={`navbar-menu ${isMobileMenuOpen ? 'active' : ''}`}>
        <a onClick={() => navigate('/')}>Dashboard</a>
        {isAdmin && (
          <a onClick={() => navigate('/usuarios')}>Usuários</a>
        )}
        <a onClick={() => navigate('/transacoes')}>Transações</a>
        <a onClick={() => navigate('/dividas')}>Dívidas</a>
        <a onClick={handleLogout} className="logout-btn">Sair</a>
      </div>

      <div className="navbar-user">
        <span>{user.nome || 'Usuário'}</span>
        <button className="mobile-toggle" onClick={toggleMobileMenu}>
          <i className="fas fa-bars"></i>
        </button>
      </div>
    </nav>
  );
}

export default Navbar;

