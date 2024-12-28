import http from './api.ts';
import type { ApiResponse } from '@/types/apiResponse.ts';
import type { AuthRequest } from '@/types/authRequest.ts';
import type { AuthResponse } from '@/types/authResponse.ts';

async function login(authentication: AuthRequest) {
	return await http.post<ApiResponse<AuthResponse>>('/api/auth/login', authentication);
}

export default {
	login
};
