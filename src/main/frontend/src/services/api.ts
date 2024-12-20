import axios from 'axios';

export const getApiTestMessage = () => axios.get('/api/v1/test');
