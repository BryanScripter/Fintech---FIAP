import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axios from '../utils/axiosConfig';
import './Registro.css';

function Registro() {
  const [nome, setNome] = useState<string>('');
  const [email, setEmail] = useState<string>('');
  const [telefone, setTelefone] = useState<string>('');
  const [senha, setSenha] = useState<string>('');
  const [confirmarSenha, setConfirmarSenha] = useState<string>('');
  const [erro, setErro] = useState<string>('');
  const [sucesso, setSucesso] = useState<string>('');
  const navigate = useNavigate();

  const handleRegistro = async (e: React.FormEvent) => {
    e.preventDefault();
    setErro('');
    setSucesso('');

    // Validação de senhas
    if (senha !== confirmarSenha) {
      setErro('As senhas não coincidem');
      return;
    }

    if (senha.length < 6) {
      setErro('A senha deve ter pelo menos 6 caracteres');
      return;
    }

    try {
      const response = await axios.post('/api/usuarios/register', {
        nome,
        email,
        telefone,
        senha
      });

      if (response.status === 201) {
        setSucesso('Conta criada com sucesso! Redirecionando...');
        setTimeout(() => {
          navigate('/login');
        }, 2000);
      }
    } catch (error: any) {
      if (error.response?.data) {
        const errorData = error.response.data;
        if (typeof errorData === 'string') {
          setErro(errorData);
        } else if (errorData.message) {
          setErro(errorData.message);
        } else if (errorData.error) {
          setErro(errorData.error);
        } else {
          setErro('Erro ao criar conta. Verifique o backend.');
        }
      } else {
        setErro('Erro ao criar conta. Tente novamente.');
      }
    }
  };

  return (
    <div className="registro-container">
      <div className="registro-card">
        <div className="registro-header">
          <i className="fas fa-user-plus"></i>
          <h1>Criar Conta</h1>
          <p>Preencha os dados para se registrar</p>
        </div>

        <form onSubmit={handleRegistro} className="registro-form">
          <div className="form-group">
            <label>
              <i className="fas fa-user"></i>
              Nome Completo
            </label>
            <input
              type="text"
              value={nome}
              onChange={(e) => setNome(e.target.value)}
              required
              placeholder="Digite seu nome completo"
            />
          </div>

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
              <i className="fas fa-phone"></i>
              Telefone
            </label>
            <input
              type="tel"
              value={telefone}
              onChange={(e) => setTelefone(e.target.value)}
              required
              placeholder="(00) 00000-0000"
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
              placeholder="Mínimo 6 caracteres"
            />
          </div>

          <div className="form-group">
            <label>
              <i className="fas fa-lock"></i>
              Confirmar Senha
            </label>
            <input
              type="password"
              value={confirmarSenha}
              onChange={(e) => setConfirmarSenha(e.target.value)}
              required
              placeholder="Confirme sua senha"
            />
          </div>

          {erro && <div className="error-message">{erro}</div>}
          {sucesso && <div className="success-message">{sucesso}</div>}

          <button type="submit" className="registro-button">
            Criar Conta
          </button>

          <div className="login-link">
            <p>Já tem uma conta? <Link to="/login">Fazer login</Link></p>
          </div>
        </form>
      </div>
    </div>
  );
}

export default Registro;


