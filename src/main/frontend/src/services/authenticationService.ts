import type { AuthRequest } from '@/types/authRequest.ts';
import http from './api.ts';
import type { ApiResponse } from '@/types/apiResponse.ts';
import type { UserInfo } from '@/types/userInfo.ts';

async function createUser(user: UserInfo) {
	return await http.post<ApiResponse<UserInfo>>('/api/auth/sign-up', user);
}

async function login(authentication: AuthRequest) {
	return await http.post<ApiResponse<UserInfo>>('/api/auth/login', authentication);
}

async function logout() {
	return await http.post('/api/auth/logout');
}

export default {
	createUser,
	login,
	logout
};
