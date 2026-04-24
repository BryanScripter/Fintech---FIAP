import axios from 'axios';

// Configurar interceptor para adicionar o header X-User-Id em todas as requisições
axios.interceptors.request.use(
  (config) => {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      try {
        const user = JSON.parse(userStr);
        if (user.idUsuario) {
          config.headers['X-User-Id'] = user.idUsuario.toString();
        }
      } catch (error) {
        console.error('Erro ao parsear usuário do localStorage:', error);
      }
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Configurar base URL se necessário
axios.defaults.baseURL = 'http://localhost:8080';

export default axios;

