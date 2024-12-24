import { defineStore } from 'pinia';
import type { AuthRequest } from '@/types/authRequest.ts';
import type { ApiResponse } from '@/types/apiResponse.ts';
import type { AxiosError } from 'axios';
import { Api } from '@/services';

export const useUserStore = defineStore('userStore', () => {
	async function dispatchLogin(authentication: AuthRequest): Promise<ApiResponse<null>> {
		console.log('dispatchLogin', authentication);
		try {
			const { status, data } = await Api.authentication.login(authentication);
			console.log('status', status);
			console.log('data', data);

			if (status === 200) {
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

	return {
		dispatchLogin
	};
});
