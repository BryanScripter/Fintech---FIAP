import React, { useState, useEffect } from 'react';
import axios from '../utils/axiosConfig';
import Navbar from '../components/Navbar';
import { Transacao } from '../types';
import './CrudPages.css';

function Transacoes() {
  const [transacoes, setTransacoes] = useState<Transacao[]>([]);
  const [showModal, setShowModal] = useState<boolean>(false);
  const [editing, setEditing] = useState<number | null>(null);
  const [formData, setFormData] = useState<Partial<Transacao>>({
    cdTpTransacaoIdTipoTransacao: undefined,
    valor: undefined,
    descricao: ''
  });

  useEffect(() => {
    fetchTransacoes();
  }, []);

  const fetchTransacoes = async () => {
    try {
      const response = await axios.get('/api/transacoes');
      setTransacoes(response.data);
    } catch (error) {
      console.error('Erro ao buscar transações:', error);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      // Payload apenas com os campos necessários (backend associa automaticamente a conta)
      const payload = {
        cdTpTransacaoIdTipoTransacao: formData.cdTpTransacaoIdTipoTransacao!,
        valor: formData.valor!,
        descricao: formData.descricao!
      };

      console.log('Enviando dados:', payload);
      
      if (editing) {
        await axios.put(`/api/transacoes/${editing}`, payload);
      } else {
        const response = await axios.post('/api/transacoes', payload);
        console.log('Resposta da API:', response.data);
      }
      fetchTransacoes();
      setShowModal(false);
      setFormData({ cdTpTransacaoIdTipoTransacao: undefined, valor: undefined, descricao: '' });
      setEditing(null);
    } catch (error: any) {
      console.error('Erro detalhado:', error.response?.data || error.message);
      const errorMessage = error.response?.data?.message || error.response?.data || error.message;
      
      alert(`Erro ao salvar transação: ${errorMessage}`);
    }
  };


  const handleEdit = (transacao: Transacao) => {
    setEditing(transacao.idTransacao!);
    setFormData({
      cdTpTransacaoIdTipoTransacao: transacao.cdTpTransacaoIdTipoTransacao,
      valor: transacao.valor,
      descricao: transacao.descricao
    });
    setShowModal(true);
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Tem certeza que deseja excluir esta transação?')) {
      try {
        await axios.delete(`/api/transacoes/${id}`);
        fetchTransacoes();
      } catch (error) {
        alert('Erro ao excluir transação');
      }
    }
  };

  return (
    <div className="app">
      <Navbar />
      <div className="main-content">
        <div className="page-header">
          <h1>Gerenciar Transações</h1>
          <button className="btn-primary" onClick={() => { setShowModal(true); setEditing(null); setFormData({ cdTpTransacaoIdTipoTransacao: undefined, valor: undefined, descricao: '' }); }}>
            <i className="fas fa-plus"></i> Nova Transação
          </button>
        </div>


        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Conta ID</th>
                <th>Tipo</th>
                <th>Valor</th>
                <th>Descrição</th>
                <th>Data</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              {transacoes.map((transacao) => (
                <tr key={transacao.idTransacao}>
                  <td>{transacao.idTransacao}</td>
                  <td>{transacao.contaIdConta}</td>
                  <td>{transacao.cdTpTransacaoIdTipoTransacao === 1 ? 'Receita' : transacao.cdTpTransacaoIdTipoTransacao === 2 ? 'Despesa' : 'Transferência'}</td>
                  <td>R$ {transacao.valor?.toFixed(2)}</td>
                  <td>{transacao.descricao}</td>
                  <td>{new Date(transacao.dtTransacao!).toLocaleDateString('pt-BR')}</td>
                  <td>
                    <button className="btn-edit" onClick={() => handleEdit(transacao)}>
                      <i className="fas fa-edit"></i> Editar
                    </button>
                    <button className="btn-delete" onClick={() => handleDelete(transacao.idTransacao!)}>
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
              <h2>{editing ? 'Editar' : 'Nova'} Transação</h2>
              <form onSubmit={handleSubmit}>
                <div className="form-group">
                  <label>Tipo Transação (1=Receita, 2=Despesa, 3=Transferência)</label>
                  <input
                    type="number"
                    value={formData.cdTpTransacaoIdTipoTransacao || ''}
                    onChange={(e) => setFormData({ ...formData, cdTpTransacaoIdTipoTransacao: parseInt(e.target.value) })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Valor</label>
                  <input
                    type="number"
                    step="0.01"
                    value={formData.valor || ''}
                    onChange={(e) => setFormData({ ...formData, valor: parseFloat(e.target.value) })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Descrição</label>
                  <input
                    type="text"
                    value={formData.descricao || ''}
                    onChange={(e) => setFormData({ ...formData, descricao: e.target.value })}
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

export default Transacoes;

