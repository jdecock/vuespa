import http from './api.ts';
import { useUserStore } from '@/stores/userStore.ts';
import axios from 'axios';
import type { AuthResponse } from '@/types/authResponse.ts';

function setup() {
	const userStore = useUserStore();

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
					const { status, data } = await axios.post<AuthResponse>('/api/auth/refresh-token', {
						refreshToken: refreshToken
					});

					if (status === 200 && data) {
						// Store the new refresh token
						userStore.setRefreshToken(data.refreshToken);

						// Retry the original request.
						return http(originalRequest);
					} else {
						// Clear the stored tokens and redirect to the login page.
						console.error('Token refresh failed: ', status);
						userStore.setRefreshToken('');
						// window.location.href = '/login';
						return Promise.reject(status);
					}
				} catch (refreshError) {
					// Clear the stored tokens and redirect to the login page.
					console.error('Token refresh failed: ', refreshError);
					userStore.setRefreshToken('');
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
