import http from './api.ts';
import axios from 'axios';

function setup() {
	http.interceptors.response.use(
		response => response, // No error - just pass through the response.
		async error => {
			const originalRequest = error.config;

			// TODO: This should probably be a 401 but the service is returning 403 instead.
			console.log('STATUS CODE:', error.response.status);
			if (error.response.status === 403 && !originalRequest._retry) {
				originalRequest._retry = true; // Mark the request as retried to avoid infinite loops.
				try {
					// Attempt to refresh the auth token
					const { status } = await axios.post('/api/auth/refresh-token');

					if (status === 200) {
						// Retry the original request.
						return http(originalRequest);
					} else {
						// Clear the stored tokens and redirect to the login page.
						console.error('Token refresh failed: ', status);
						// window.location.href = '/login';
						return Promise.reject(status);
					}
				} catch (refreshError) {
					// Clear the stored tokens and redirect to the login page.
					console.error('Token refresh failed: ', refreshError);
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
