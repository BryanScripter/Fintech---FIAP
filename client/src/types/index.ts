export interface Usuario {
  idUsuario?: number;
  nome: string;
  email: string;
  telefone: string;
  senha: string;
  dtCadastro?: Date;
}

export interface Transacao {
  idTransacao?: number;
  contaIdConta: number;
  cdTpTransacaoIdTipoTransacao: number;
  valor: number;
  dtTransacao?: Date;
  descricao: string;
}

export interface Divida {
  idDivida?: number;
  contaIdConta: number;
  descricao: string;
  valorTotal: number;
  jurosMensal?: number;
  dtInicio?: Date;
  dtVencimento: Date | string;
  quitada?: string;
  tpDivida: string;
}

