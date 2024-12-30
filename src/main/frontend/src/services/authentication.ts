import http from './api.ts';
import type { AuthRequest } from '@/types/authRequest.ts';

async function login(authentication: AuthRequest) {
	return await http.post('/api/auth/login', authentication);
}

async function logout() {
	return await http.post('/api/auth/logout');
}

export default {
	login,
	logout
};
