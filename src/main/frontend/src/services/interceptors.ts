import http from './api.ts';
import { useUserStore } from '@/stores/userStore.ts';
import axios from 'axios';
import type { AuthResponse } from '@/types/authResponse.ts';
import type { ApiResponse } from '@/types/apiResponse.ts';

function setup() {
	const userStore = useUserStore();

	http.interceptors.request.use(
		config => {
			const accessToken = userStore.getAccessToken();
			if (accessToken && accessToken.length) {
				config.headers['Authorization'] = 'Bearer ' + accessToken;
			}
			return config;
		},
		error => {
			return Promise.reject(error);
		}
	);

	http.interceptors.response.use(
		response => response, // No error - just pass through the response.
		async error => {
			const originalRequest = error.config;

			// TODO: This should probably be a 401 but the service is returning 403 instead.
			if (error.response.status === 403 && !originalRequest._retry) {
				originalRequest._retry = true; // Mark the request as retried to avoid infinite loops.
				try {
					const refreshToken = userStore.getRefreshToken();

					// Request a new auth token using the refresh token
					const { status, data } = await axios.post<ApiResponse<AuthResponse>>('/api/auth/refresh-token', {
						refreshToken: refreshToken
					});

					if (status === 200 && data && data.success) {
						// Store the new access and refresh tokens
						userStore.setAccessToken(data.payload?.accessToken);
						userStore.setRefreshToken(data.payload?.refreshToken);

						// Update the authorization header with the new access token and retry the original request.
						http.defaults.headers.common['Authorization'] = `Bearer ${data.payload?.accessToken}`;
						return http(originalRequest);
					} else {
						// Clear the stored tokens and redirect to the login page.
						console.error('Token refresh failed: ', data.message);
						userStore.setRefreshToken('');
						userStore.setAccessToken('');
						// window.location.href = '/login';
						return Promise.reject(data.message);
					}
				} catch (refreshError) {
					// Clear the stored tokens and redirect to the login page.
					console.error('Token refresh failed: ', refreshError);
					userStore.setRefreshToken('');
					userStore.setAccessToken('');
					// window.location.href = '/login';
					return Promise.reject(refreshError);
				}
			}

			// For any other errors, return the error as is.
			return Promise.reject(error);
		}
	);
}

export default setup;
