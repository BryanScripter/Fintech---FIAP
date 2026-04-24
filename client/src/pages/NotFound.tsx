import React from 'react';
import { useNavigate } from 'react-router-dom';
import './NotFound.css';

function NotFound() {
  const navigate = useNavigate();

  return (
    <div className="not-found-container">
      <div className="not-found-content">
        <h1>404</h1>
        <h2>Página não encontrada</h2>
        <p>A página que você está procurando não existe.</p>
        <button onClick={() => navigate('/')} className="btn-home">
          Voltar para o Dashboard
        </button>
      </div>
    </div>
  );
}

export default NotFound;

