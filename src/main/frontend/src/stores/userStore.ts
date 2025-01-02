import { Api } from '@/services';
import type { ApiResponse } from '@/types/apiResponse.ts';
import type { UserInfo } from '@/types/userInfo.ts';
import type { AxiosError } from 'axios';
import { defineStore } from 'pinia';

export const useUserStore = defineStore('userStore', () => {
	async function search(searchTerm: string): Promise<ApiResponse<UserInfo[] | null>> {
		try {
			const { status, data } = await Api.user.search(searchTerm);

			return {
				success: data && data.success,
				message: status === 200 ? data.message : `Received a ${status} status from the server`,
				payload: data?.payload
			};
		} catch (error) {
			console.error('User search failed:', error);

			return {
				success: false,
				message: (error as AxiosError<string>).response?.statusText,
				payload: null
			};
		}
	}

	async function getUser(userId: number): Promise<ApiResponse<UserInfo | null>> {
		try {
			const { status, data } = await Api.user.loadUserInfo(userId);

			return {
				success: data && data.success,
				message: status === 200 ? data.message : `Received a ${status} status from the server`,
				payload: data.payload
			};
		} catch (error) {
			return {
				success: false,
				message: (error as AxiosError<string>).response?.statusText,
				payload: null
			};
		}
	}

	async function saveUser(user: UserInfo): Promise<ApiResponse<UserInfo | null>> {
		try {
			const { status, data } = await Api.user.saveUser(user);

			return {
				success: data && data.success,
				message: status === 200 ? data.message : `Received a ${status} status from the server`,
				payload: data.payload
			};
		} catch (error) {
			return {
				success: false,
				message: (error as AxiosError<string>).response?.statusText,
				payload: null
			};
		}
	}

	async function updateProfile(user: UserInfo): Promise<ApiResponse<UserInfo | null>> {
		try {
			const { status, data } = await Api.user.updateProfile(user);

			return {
				success: data && data.success,
				message: status === 200 ? data.message : `Received a ${status} status from the server`,
				payload: data.payload
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
		getUser,
		saveUser,
		search,
		updateProfile
	};
});
