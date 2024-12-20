import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '../views/HomeView.vue';

const router = createRouter({
	history: createWebHistory(import.meta.env.BASE_URL),
	routes: [
		{
			path: '/',
			name: 'home',
			component: HomeView
		},
		{
			path: '/sign-in',
			name: 'signIn',
			component: () => import('../views/SignInView.vue')
		},
		{
			path: '/sign-up',
			name: 'signUp',
			component: () => import('../views/SignUpView.vue')
		},
		{
			path: '/forgot-password',
			name: 'forgotPassword',
			component: () => import('../views/ForgotPasswordView.vue')
		},
		{
			path: '/about',
			name: 'about',
			component: () => import('../views/AboutView.vue')
		}
	]
});

export default router;
