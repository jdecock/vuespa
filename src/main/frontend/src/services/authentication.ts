import http from './api.ts';
import type { ApiResponse } from '@/types/apiResponse.ts';
import type { AuthRequest } from '@/types/authRequest.ts';

async function login(authentication: AuthRequest) {
	return await http.post<ApiResponse<string>>('/auth/generate-token', authentication);
}

export default {
	login
};
