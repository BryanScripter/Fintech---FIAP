import React, { useState, useEffect } from 'react';
import axios from '../utils/axiosConfig';
import Navbar from '../components/Navbar';
import { Transacao, Divida } from '../types';
import './Dashboard.css';

function Dashboard() {
  const [transacoes, setTransacoes] = useState<Transacao[]>([]);
  const [dividas, setDividas] = useState<Divida[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchData();
    // Atualiza a cada 5 segundos para refletir mudanças
    const interval = setInterval(fetchData, 5000);
    return () => clearInterval(interval);
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [transacoesRes, dividasRes] = await Promise.all([
        axios.get('/api/transacoes'),
        axios.get('/api/dividas')
      ]);
      setTransacoes(transacoesRes.data || []);
      setDividas(dividasRes.data || []);
    } catch (error) {
      console.error('Erro ao buscar dados:', error);
    } finally {
      setLoading(false);
    }
  };

  // Formatar valor em reais
  const formatCurrency = (value: number): string => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  };

  // Calcular receitas (tipo 1 = Receita)
  const receitas = transacoes
    .filter(t => t.cdTpTransacaoIdTipoTransacao === 1 || 
                 (t as any).tipoTransacao?.idTipoTransacao === 1 ||
                 (t as any).tipoTransacao?.descricao?.toLowerCase().includes('receita'))
    .reduce((sum, t) => sum + (t.valor || 0), 0);

  // Calcular despesas (tipo 2 = Despesa ou qualquer outro tipo que não seja receita)
  const despesas = transacoes
    .filter(t => {
      const tipoId = t.cdTpTransacaoIdTipoTransacao || (t as any).tipoTransacao?.idTipoTransacao;
      const tipoDesc = (t as any).tipoTransacao?.descricao?.toLowerCase() || '';
      return tipoId !== 1 && !tipoDesc.includes('receita');
    })
    .reduce((sum, t) => sum + (t.valor || 0), 0);

  // Calcular dívidas não quitadas
  const dividasAtivas = dividas
    .filter(d => d.quitada !== 'S' && d.quitada !== 's')
    .reduce((sum, d) => sum + (d.valorTotal || 0), 0);

  // Calcular saldo total (receitas - despesas - dívidas ativas)
  const saldoTotal = receitas - despesas - dividasAtivas;

  return (
    <div className="app">
      <Navbar />
      <div className="main-content">
        <div className="page-header">
          <h1>Dashboard</h1>
          <p>Visão geral do seu sistema financeiro</p>
        </div>

        <div className="dashboard-grid">
          <div className="stat-card">
            <div className="stat-icon income">
              <i className="fas fa-arrow-trend-up"></i>
            </div>
            <div className="stat-content">
              <h3>Receitas</h3>
              <p className="stat-value">
                {loading ? 'Carregando...' : formatCurrency(receitas)}
              </p>
            </div>
          </div>

          <div className="stat-card">
            <div className="stat-icon expense">
              <i className="fas fa-arrow-trend-down"></i>
            </div>
            <div className="stat-content">
              <h3>Despesas</h3>
              <p className="stat-value">
                {loading ? 'Carregando...' : formatCurrency(despesas)}
              </p>
            </div>
          </div>

          <div className="stat-card">
            <div className="stat-icon debt">
              <i className="fas fa-file-invoice-dollar"></i>
            </div>
            <div className="stat-content">
              <h3>Dívidas</h3>
              <p className="stat-value">
                {loading ? 'Carregando...' : formatCurrency(dividasAtivas)}
              </p>
            </div>
          </div>

          <div className="stat-card">
            <div className="stat-icon balance">
              <i className="fas fa-wallet"></i>
            </div>
            <div className="stat-content">
              <h3>Saldo Total</h3>
              <p className={`stat-value ${saldoTotal < 0 ? 'negative' : ''}`}>
                {loading ? 'Carregando...' : formatCurrency(saldoTotal)}
              </p>
            </div>
          </div>
        </div>

        <div className="info-section">
          <h2>Bem-vindo ao Sistema Fintech</h2>
          <p>Este é um sistema completo de gestão financeira desenvolvido com:</p>
          <ul>
            <li><strong>Backend:</strong> Spring Boot com JPA/Hibernate</li>
            <li><strong>Frontend:</strong> React.js com Vite e TypeScript</li>
            <li><strong>Banco de Dados:</strong> Oracle Database (FIAP)</li>
          </ul>
          <p>Use o menu lateral para navegar entre as funcionalidades.</p>
          <p style={{ marginTop: '15px', fontSize: '14px', color: '#999' }}>
            Os dados são atualizados automaticamente a cada 5 segundos.
          </p>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;

