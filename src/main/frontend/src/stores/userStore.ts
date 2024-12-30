import { Api } from '@/services';
import { defineStore } from 'pinia';
import type { AuthRequest } from '@/types/authRequest.ts';
import type { ApiResponse } from '@/types/apiResponse.ts';
import type { AxiosError } from 'axios';
import type { UserInfo } from '@/types/userInfo.ts';

export const useUserStore = defineStore('userStore', () => {
	let user: UserInfo | null = null;

	function currentUser() {
		return user;
	}

	async function dispatchSignUp(user: UserInfo): Promise<ApiResponse<null>> {
		try {
			const { status, data } = await Api.user.createUser(user);

			if (status === 200 && data && data.success) {
				return dispatchLogin({
					email: user.email,
					password: user.plainTextPassword ?? '',
					persistLogin: false
				});
			}

			return {
				success: false,
				message: status !== 200 ? `Received a ${status} status from the server` : data?.message,
				payload: null
			};
		} catch (error) {
			return {
				success: false,
				message: (error as AxiosError<string>).response?.statusText,
				payload: null
			};
		}
	}

	async function dispatchLogin(authentication: AuthRequest): Promise<ApiResponse<null>> {
		try {
			const { status } = await Api.authentication.login(authentication);

			if (status === 200) {
				return {
					success: true,
					message: 'User logged in',
					payload: null
				};
			}

			return {
				success: false,
				message: `Received a ${status} status from the server`,
				payload: null
			};
		} catch (error) {
			console.error('failed to dispatchLogin');
			return {
				success: false,
				message: (error as AxiosError<string>).response?.statusText,
				payload: null
			};
		}
	}

	async function dispatchLogout(): Promise<ApiResponse<null>> {
		try {
			const { status } = await Api.authentication.logout();

			if (status === 200) {
				return {
					success: true,
					message: 'User logged out',
					payload: null
				};
			}

			return {
				success: false,
				message: `Received a ${status} status from the server`,
				payload: null
			};
		} catch (error) {
			console.error('failed to dispatchLogin');
			return {
				success: false,
				message: (error as AxiosError<string>).response?.statusText,
				payload: null
			};
		}
	}

	async function dispatchLoadUserInfo(): Promise<ApiResponse<UserInfo | null>> {
		try {
			const { status, data } = await Api.user.loadUserInfo();

			if (status === 200 && data && data.success) {
				user = data.payload ?? null;
				return data;
			}

			return {
				success: false,
				message: status !== 200 ? `Received a ${status} status from the server` : data?.message,
				payload: null
			};
		} catch (error) {
			return {
				success: false,
				message: (error as AxiosError<string>).response?.statusText,
				payload: null
			};
		}
	}

	return {
		currentUser,
		dispatchLogin,
		dispatchSignUp,
		dispatchLogout,
		dispatchLoadUserInfo
	};
});
