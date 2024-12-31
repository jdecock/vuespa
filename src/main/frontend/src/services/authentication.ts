import type { AuthRequest } from '@/types/authRequest.ts';
import http from './api.ts';

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
