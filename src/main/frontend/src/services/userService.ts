import type { ApiResponse } from '@/types/apiResponse.ts';
import type { ChangePasswordRequest } from '@/types/changePasswordRequest.ts';
import type { UserInfo } from '@/types/userInfo.ts';
import http from './api.ts';

async function changePassword(password: ChangePasswordRequest) {
	return await http.post<ApiResponse<null>>('/api/user/change-password', password);
}

async function loadUserInfo() {
	return await http.get<ApiResponse<UserInfo>>('/api/user');
}

async function updateUser(user: UserInfo) {
	return await http.post<ApiResponse<UserInfo>>('/api/user/update', user);
}

async function search(searchTerm: string) {
	return await http.get<ApiResponse<UserInfo[]>>('/api/user/search', { params: { query: searchTerm } });
}

export default {
	changePassword,
	loadUserInfo,
	search,
	updateUser
};
