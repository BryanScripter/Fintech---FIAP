import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axios from '../utils/axiosConfig';
import './Login.css';

function Login() {
  const [email, setEmail] = useState<string>('');
  const [senha, setSenha] = useState<string>('');
  const [erro, setErro] = useState<string>('');
  const navigate = useNavigate();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setErro('');

    try {
      const response = await axios.post('/api/usuarios/login', {
        email,
        senha
      });

      if (response.data) {
        localStorage.setItem('isAuthenticated', 'true');
        localStorage.setItem('user', JSON.stringify(response.data));
        navigate('/');
      }
    } catch (error) {
      setErro('Email ou senha inválidos');
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <div className="login-header">
          <i className="fas fa-chart-line"></i>
          <h1>Fintech</h1>
          <p>Sistema de Gestão Financeira</p>
        </div>

        <form onSubmit={handleLogin} className="login-form">
          <div className="form-group">
            <label>
              <i className="fas fa-envelope"></i>
              Email
            </label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              placeholder="Digite seu email"
            />
          </div>

          <div className="form-group">
            <label>
              <i className="fas fa-lock"></i>
              Senha
            </label>
            <input
              type="password"
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
              required
              placeholder="Digite sua senha"
            />
          </div>

          {erro && <div className="error-message">{erro}</div>}

          <button type="submit" className="login-button">
            Entrar
          </button>

          <div className="registro-link">
            <p>Não tem uma conta? <Link to="/registro">Registre-se</Link></p>
          </div>
        </form>
      </div>
    </div>
  );
}

export default Login;

