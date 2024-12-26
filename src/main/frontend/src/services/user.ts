import http from './api.ts';
import type { ApiResponse } from '@/types/apiResponse.ts';
import type { UserInfo } from '@/types/userInfo.ts';

async function loadUserInfo() {
	return await http.get<ApiResponse<UserInfo>>('/user/');
}

export default {
	loadUserInfo
};
