import React, { useState, useEffect } from 'react';
import axios from '../utils/axiosConfig';
import Navbar from '../components/Navbar';
import { Divida } from '../types';
import './CrudPages.css';

function Dividas() {
  const [dividas, setDividas] = useState<Divida[]>([]);
  const [showModal, setShowModal] = useState<boolean>(false);
  const [editing, setEditing] = useState<number | null>(null);
  const [formData, setFormData] = useState<Partial<Divida>>({
    descricao: '',
    valorTotal: undefined,
    jurosMensal: 0,
    dtVencimento: '',
    tpDivida: ''
  });

  useEffect(() => {
    fetchDividas();
  }, []);

  const fetchDividas = async () => {
    try {
      const response = await axios.get('/api/dividas');
      console.log('Dívidas recebidas:', response.data);
      setDividas(response.data || []);
    } catch (error: any) {
      console.error('Erro ao buscar dívidas:', error.response?.data || error.message);
      // Se der erro, deixa lista vazia
      setDividas([]);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const payload = {
        descricao: formData.descricao!,
        valorTotal: formData.valorTotal!,
        jurosMensal: formData.jurosMensal || 0,
        dtVencimento: formData.dtVencimento!,
        tpDivida: formData.tpDivida!,
        quitada: 'N'
      };

      console.log('Enviando dívida:', payload);

      if (editing) {
        await axios.put(`/api/dividas/${editing}`, payload);
      } else {
        const response = await axios.post('/api/dividas', payload);
        console.log('Dívida criada com sucesso:', response.data);
      }
      
      // Recarrega a lista de dívidas
      await fetchDividas();
      setShowModal(false);
      setFormData({ descricao: '', valorTotal: undefined, jurosMensal: 0, dtVencimento: '', tpDivida: '' });
      setEditing(null);
    } catch (error: any) {
      console.error('Erro detalhado ao salvar dívida:', error.response?.data || error.message);
      const errorMessage = error.response?.data?.message || error.response?.data || error.message;
      alert(`Erro ao salvar dívida: ${errorMessage}`);
    }
  };

  const handleEdit = (divida: Divida) => {
    if (!divida.idDivida) {
      console.error('ID da dívida não encontrado:', divida);
      alert('Erro: ID da dívida não encontrado');
      return;
    }

    console.log('Editando dívida:', divida);
    
    // Formatar data de vencimento para o input date
    let dtVencimentoFormatada = '';
    if (divida.dtVencimento) {
      if (typeof divida.dtVencimento === 'string') {
        dtVencimentoFormatada = divida.dtVencimento.split('T')[0];
      } else {
        dtVencimentoFormatada = new Date(divida.dtVencimento).toISOString().split('T')[0];
      }
    }

    setEditing(divida.idDivida);
    setFormData({
      descricao: divida.descricao || '',
      valorTotal: divida.valorTotal,
      jurosMensal: divida.jurosMensal || 0,
      dtVencimento: dtVencimentoFormatada,
      tpDivida: divida.tpDivida || ''
    });
    setShowModal(true);
  };

  const handleDelete = async (id: number) => {
    if (!id) {
      console.error('ID da dívida não fornecido');
      alert('Erro: ID da dívida não encontrado');
      return;
    }

    if (window.confirm('Tem certeza que deseja excluir esta dívida?')) {
      try {
        console.log('Excluindo dívida ID:', id);
        await axios.delete(`/api/dividas/${id}`);
        console.log('Dívida excluída com sucesso');
        await fetchDividas();
      } catch (error: any) {
        console.error('Erro detalhado ao excluir dívida:', error.response?.data || error.message);
        const errorMessage = error.response?.data?.message || error.response?.data || error.message;
        alert(`Erro ao excluir dívida: ${errorMessage}`);
      }
    }
  };

  const handleQuitar = async (id: number) => {
    if (!id) {
      console.error('ID da dívida não fornecido');
      alert('Erro: ID da dívida não encontrado');
      return;
    }

    if (window.confirm('Tem certeza que deseja quitar esta dívida?')) {
      try {
        console.log('Quitando dívida ID:', id);
        await axios.put(`/api/dividas/${id}/quitar`);
        console.log('Dívida quitada com sucesso');
        await fetchDividas();
      } catch (error: any) {
        console.error('Erro detalhado ao quitar dívida:', error.response?.data || error.message);
        const errorMessage = error.response?.data?.message || error.response?.data || error.message;
        alert(`Erro ao quitar dívida: ${errorMessage}`);
      }
    }
  };

  return (
    <div className="app">
      <Navbar />
      <div className="main-content">
        <div className="page-header">
          <h1>Minhas Dívidas</h1>
          <button className="btn-primary" onClick={() => { setShowModal(true); setEditing(null); setFormData({ descricao: '', valorTotal: undefined, jurosMensal: 0, dtVencimento: '', tpDivida: '' }); }}>
            <i className="fas fa-plus"></i> Nova Dívida
          </button>
        </div>

        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Conta ID</th>
                <th>Descrição</th>
                <th>Valor Total</th>
                <th>Vencimento</th>
                <th>Tipo</th>
                <th>Status</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              {dividas.length === 0 ? (
                <tr>
                  <td colSpan={8} style={{ textAlign: 'center', padding: '20px' }}>
                    Nenhuma dívida cadastrada. Clique em "Nova Dívida" para adicionar.
                  </td>
                </tr>
              ) : (
                dividas.map((divida) => (
                  <tr key={divida.idDivida}>
                    <td>{divida.idDivida}</td>
                    <td>{divida.contaIdConta}</td>
                    <td>{divida.descricao}</td>
                    <td>R$ {divida.valorTotal?.toFixed(2)}</td>
                    <td>{new Date(divida.dtVencimento).toLocaleDateString('pt-BR')}</td>
                    <td>{divida.tpDivida}</td>
                    <td>
                      <span className={`status-badge ${divida.quitada === 'S' ? 'quitada' : 'nao-quitada'}`}>
                        {divida.quitada === 'S' ? 'Quitada' : 'Não Quitada'}
                      </span>
                    </td>
                    <td>
                      <button 
                        className="btn-edit" 
                        onClick={() => divida.idDivida && handleEdit(divida)}
                        disabled={!divida.idDivida}
                      >
                        <i className="fas fa-edit"></i> Editar
                      </button>
                      <button 
                        className="btn-quitar" 
                        onClick={() => divida.idDivida && handleQuitar(divida.idDivida)} 
                        disabled={divida.quitada === 'S' || !divida.idDivida}
                      >
                        <i className="fas fa-check"></i> Quitar
                      </button>
                      <button 
                        className="btn-delete" 
                        onClick={() => divida.idDivida && handleDelete(divida.idDivida)}
                        disabled={!divida.idDivida}
                      >
                        <i className="fas fa-trash"></i> Excluir
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {showModal && (
          <div className="modal-overlay" onClick={() => { setShowModal(false); setEditing(null); }}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
              <h2>{editing ? 'Editar' : 'Nova'} Dívida</h2>
              <form onSubmit={handleSubmit}>
                <div className="form-group">
                  <label>Descrição</label>
                  <input
                    type="text"
                    value={formData.descricao || ''}
                    onChange={(e) => setFormData({ ...formData, descricao: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Valor Total</label>
                  <input
                    type="number"
                    step="0.01"
                    value={formData.valorTotal || ''}
                    onChange={(e) => setFormData({ ...formData, valorTotal: parseFloat(e.target.value) })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Juros Mensal (%)</label>
                  <input
                    type="number"
                    step="0.01"
                    value={formData.jurosMensal || 0}
                    onChange={(e) => setFormData({ ...formData, jurosMensal: parseFloat(e.target.value) })}
                  />
                </div>
                <div className="form-group">
                  <label>Data de Vencimento</label>
                  <input
                    type="date"
                    value={typeof formData.dtVencimento === 'string' ? formData.dtVencimento : ''}
                    onChange={(e) => setFormData({ ...formData, dtVencimento: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Tipo de Dívida</label>
                  <input
                    type="text"
                    value={formData.tpDivida || ''}
                    onChange={(e) => setFormData({ ...formData, tpDivida: e.target.value })}
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

export default Dividas;

