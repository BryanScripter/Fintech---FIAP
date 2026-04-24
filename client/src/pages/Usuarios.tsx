import React, { useState, useEffect } from 'react';
import axios from '../utils/axiosConfig';
import Navbar from '../components/Navbar';
import { Usuario } from '../types';
import { useNavigate } from 'react-router-dom';
import './CrudPages.css';

function Usuarios() {
  const navigate = useNavigate();
  const [usuarios, setUsuarios] = useState<Usuario[]>([]);
  const [showModal, setShowModal] = useState<boolean>(false);
  const [editing, setEditing] = useState<number | null>(null);
  const [isAdmin, setIsAdmin] = useState<boolean>(false);
  const [formData, setFormData] = useState<Partial<Usuario>>({
    nome: '',
    email: '',
    telefone: '',
    senha: ''
  });
  
  useEffect(() => {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      try {
        const user = JSON.parse(userStr);
        const isAdminUser = user.email?.toLowerCase() === 'admin@gmail.com';
        setIsAdmin(isAdminUser);
        if (!isAdminUser) {
          // Redirecionar se não for admin
          navigate('/');
          alert('Acesso negado. Apenas administradores podem acessar esta página.');
        }
      } catch (error) {
        console.error('Erro ao parsear usuário:', error);
        navigate('/');
      }
    } else {
      navigate('/login');
    }
  }, [navigate]);

  useEffect(() => {
    if (isAdmin) {
      fetchUsuarios();
    }
  }, [isAdmin]);

  const fetchUsuarios = async () => {
    try {
      const response = await axios.get('/api/usuarios');
      setUsuarios(response.data);
    } catch (error: any) {
      console.error('Erro ao buscar usuários:', error);
      if (error.response?.status === 403) {
        alert('Acesso negado. Apenas administradores podem acessar esta página.');
        navigate('/');
      }
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editing) {
        await axios.put(`/api/usuarios/${editing}`, formData);
      } else {
        await axios.post('/api/usuarios', formData);
      }
      fetchUsuarios();
      setShowModal(false);
      setFormData({ nome: '', email: '', telefone: '', senha: '' });
      setEditing(null);
    } catch (error) {
      alert('Erro ao salvar usuário');
    }
  };

  const handleEdit = (usuario: Usuario) => {
    setEditing(usuario.idUsuario!);
    setFormData({
      nome: usuario.nome,
      email: usuario.email,
      telefone: usuario.telefone,
      senha: usuario.senha
    });
    setShowModal(true);
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Tem certeza que deseja excluir este usuário?')) {
      try {
        await axios.delete(`/api/usuarios/${id}`);
        fetchUsuarios();
      } catch (error) {
        alert('Erro ao excluir usuário');
      }
    }
  };

  if (!isAdmin) {
    return null; // Não renderiza nada se não for admin
  }

  return (
    <div className="app">
      <Navbar />
      <div className="main-content">
        <div className="page-header">
          <h1>Gerenciar Usuários</h1>
          <button className="btn-primary" onClick={() => { setShowModal(true); setEditing(null); setFormData({ nome: '', email: '', telefone: '', senha: '' }); }}>
            <i className="fas fa-plus"></i> Novo Usuário
          </button>
        </div>

        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Nome</th>
                <th>Email</th>
                <th>Telefone</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              {usuarios.map((usuario) => (
                <tr key={usuario.idUsuario}>
                  <td>{usuario.idUsuario}</td>
                  <td>{usuario.nome}</td>
                  <td>{usuario.email}</td>
                  <td>{usuario.telefone}</td>
                  <td>
                    <button className="btn-edit" onClick={() => handleEdit(usuario)}>
                      <i className="fas fa-edit"></i> Editar
                    </button>
                    <button className="btn-delete" onClick={() => handleDelete(usuario.idUsuario!)}>
                      <i className="fas fa-trash"></i> Excluir
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {showModal && (
          <div className="modal-overlay" onClick={() => { setShowModal(false); setEditing(null); }}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
              <h2>{editing ? 'Editar' : 'Novo'} Usuário</h2>
              <form onSubmit={handleSubmit}>
                <div className="form-group">
                  <label>Nome</label>
                  <input
                    type="text"
                    value={formData.nome || ''}
                    onChange={(e) => setFormData({ ...formData, nome: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Email</label>
                  <input
                    type="email"
                    value={formData.email || ''}
                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Telefone</label>
                  <input
                    type="text"
                    value={formData.telefone || ''}
                    onChange={(e) => setFormData({ ...formData, telefone: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Senha</label>
                  <input
                    type="password"
                    value={formData.senha || ''}
                    onChange={(e) => setFormData({ ...formData, senha: e.target.value })}
                    required
                  />
                </div>
                <div className="modal-actions">
                  <button type="submit" className="btn-primary">Salvar</button>
                  <button type="button" onClick={() => { setShowModal(false); setEditing(null); }} className="btn-secondary">Cancelar</button>
                </div>
              </form>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default Usuarios;

