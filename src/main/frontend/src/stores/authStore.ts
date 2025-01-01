import { Api } from '@/services';
import type { ApiResponse } from '@/types/apiResponse.ts';
import type { AuthRequest } from '@/types/authRequest.ts';
import type { UserInfo } from '@/types/userInfo.ts';
import type { AxiosError } from 'axios';
import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import type { UserRole } from '@/types/userRole.ts';

export const useAuthStore = defineStore('authStore', () => {
	const user = ref<UserInfo | null>(null);

	const isAuthenticated = computed(() => !!user.value);

	function initialize() {
		const savedUser = localStorage.getItem('currentUser');
		if (savedUser && savedUser.length) {
			user.value = JSON.parse(savedUser) as UserInfo | null;
		}
	}

	function setUser(newUser: UserInfo | null) {
		if (newUser) {
			localStorage.setItem('currentUser', JSON.stringify(newUser));
		} else {
			localStorage.removeItem('currentUser');
		}

		user.value = newUser;
	}

	async function login(authentication: AuthRequest): Promise<ApiResponse<null>> {
		try {
			const { status, data } = await Api.authentication.login(authentication);

			if (status === 200 && data && data.success) {
				setUser(data.payload ?? null);

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
			console.error('Login attempt failed:', error);
			return {
				success: false,
				message: (error as AxiosError<string>).response?.statusText,
				payload: null
			};
		}
	}

	async function logout(): Promise<ApiResponse<null>> {
		try {
			const { status } = await Api.authentication.logout();

			if (status === 200) {
				setUser(null);

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
			console.error('Failed to logout:', error);
			return {
				success: false,
				message: (error as AxiosError<string>).response?.statusText,
				payload: null
			};
		}
	}

	async function signUp(newUser: UserInfo): Promise<ApiResponse<null>> {
		try {
			const { status, data } = await Api.user.createUser(newUser);

			if (status === 200 && data && data.success) {
				setUser(data.payload ?? null);

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

	function userHasRole(role: UserRole) {
		const roles = user?.value?.roles;
		return roles && roles.includes(role);
	}

	function userHasAnyRole(allowedRoles: UserRole[]) {
		return isAuthenticated.value && allowedRoles && allowedRoles.some(x => userHasRole(x));
	}

	initialize();

	return {
		user,
		isAuthenticated,
		login,
		logout,
		signUp,
		userHasRole,
		userHasAnyRole
	};
});
