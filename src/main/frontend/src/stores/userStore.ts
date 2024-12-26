import { defineStore } from 'pinia';
import type { AuthRequest } from '@/types/authRequest.ts';
import type { ApiResponse } from '@/types/apiResponse.ts';
import type { AxiosError } from 'axios';
import { Api } from '@/services';
import type { UserInfo } from '@/types/userInfo.ts';
import { ref } from 'vue';

export const useUserStore = defineStore('userStore', () => {
	const jwt = ref('');

	async function dispatchLogin(authentication: AuthRequest): Promise<ApiResponse<null>> {
		try {
			const { status, data } = await Api.authentication.login(authentication);

			if (status === 200) {
				jwt.value = data.message ?? '';
				return {
					success: true,
					message: 'User logged in',
					content: null
				};
			}
		} catch (error) {
			console.error('Error:', error);
			return {
				success: false,
				message: (error as AxiosError<string>).response?.statusText,
				content: null
			};
		}

		return {
			success: false,
			message: 'There was no response from the server',
			content: null
		};
	}

	async function dispatchUserInfo(): Promise<ApiResponse<UserInfo | null>> {
		try {
			const { status, data } = await Api.user.loadUserInfo();

			if (status === 200) {
				return {
					success: true,
					message: 'User info loaded',
					content: null
				};
			}
		} catch (error) {
			return {
				success: false,
				message: (error as AxiosError<string>).response?.statusText,
				content: null
			};
		}

		return {
			success: false,
			message: 'There was no response from the server',
			content: null
		};
	}

	return {
		dispatchLogin,
		dispatchUserInfo,
		jwt
	};
});
