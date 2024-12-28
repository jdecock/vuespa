import { Api } from '@/services';
import { defineStore } from 'pinia';
import type { AuthRequest } from '@/types/authRequest.ts';
import type { ApiResponse } from '@/types/apiResponse.ts';
import type { AxiosError } from 'axios';
import type { UserInfo } from '@/types/userInfo.ts';

export const useUserStore = defineStore('userStore', () => {
	const accessTokenStorageKey = 'accessToken';
	const refreshTokenStorageKey = 'refreshToken';

	let user: UserInfo | null = null;

	// TODO: This is not secure. Don't store access token in the local storage.
	function getAccessToken() {
		return localStorage.getItem(accessTokenStorageKey);
	}

	function setAccessToken(token?: string) {
		if (!token || !token.length) {
			localStorage.removeItem(accessTokenStorageKey);
		} else {
			localStorage.setItem(accessTokenStorageKey, token);
		}
	}

	function getRefreshToken() {
		return localStorage.getItem(refreshTokenStorageKey);
	}

	function setRefreshToken(token?: string) {
		if (!token || !token.length) {
			localStorage.removeItem(refreshTokenStorageKey);
		} else {
			localStorage.setItem(refreshTokenStorageKey, token);
		}
	}

	function currentUser() {
		return user;
	}

	async function dispatchLogin(authentication: AuthRequest): Promise<ApiResponse<null>> {
		try {
			const { status, data } = await Api.authentication.login(authentication);

			if (status === 200 && data && data.success) {
				setAccessToken(data.payload?.accessToken);
				setRefreshToken(data.payload?.refreshToken);

				return {
					success: true,
					message: 'User logged in',
					payload: null
				};
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

	async function dispatchSignUp(user: UserInfo): Promise<ApiResponse<null>> {
		try {
			const { status, data } = await Api.user.createUser(user);

			if (status === 200 && data && data.success) {
				return dispatchLogin({
					email: user.email,
					password: user.plainTextPassword ?? ''
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
		getAccessToken,
		setAccessToken,
		getRefreshToken,
		setRefreshToken,
		dispatchLogin,
		dispatchSignUp,
		dispatchLoadUserInfo
	};
});