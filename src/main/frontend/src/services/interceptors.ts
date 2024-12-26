import http from './api.ts';
import { useUserStore } from '@/stores/userStore.ts';

function setup() {
	const userStore = useUserStore();

	http.interceptors.request.use(config => {
		const token = userStore.jwt;
		if (token.length) config.headers['Authorization'] = 'Bearer ' + token;
		return config;
	});
}

export default setup;
