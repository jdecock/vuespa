import http from './api.ts';
import type { ApiResponse } from '@/types/apiResponse.ts';
import type { UserInfo } from '@/types/userInfo.ts';

async function createUser(user: UserInfo) {
	return await http.post<ApiResponse<UserInfo>>('/api/auth/sign-up', user);
}

async function loadUserInfo() {
	return await http.get<ApiResponse<UserInfo>>('/api/user');
}

export default {
	createUser,
	loadUserInfo
};
