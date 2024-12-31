import { useUserStore } from '@/stores/userStore.ts';
import { UserRole } from '@/types/userRole.ts';
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
		// {
		// 	// TODO: Figure out how to do a "virtual" page that just redirects the user
		// 	path: '/sign-out',
		// 	name: 'signOut',
		// 	component: () => import('../views/SignInView.vue')
		// },
		{
			path: '/account',
			name: 'userAccount',
			component: () => import('../views/UserAccountView.vue'),
			meta: { requiresAuth: true, roles: [UserRole.User, UserRole.Admin] }
		},
		{
			path: '/admin/users',
			name: 'userSearch',
			component: () => import('../views/admin/UserSearchView.vue'),
			meta: { requiresAuth: true, roles: [UserRole.Admin] }
		// },
		// {
		// 	// TODO: Figure out how to pass ':id' as an arg
		// 	path: '/admin/users/:id',
		// 	name: 'userEdit',
		// 	component: () => import('../views/admin/UserEditView.vue'),
		// 	meta: { requiresAuth: true, roles: [UserRole.Admin] }
		}
	]
});

router.beforeEach((to, from, next) => {
	const requiresAuth = to.matched.some(x => x.meta.requiresAuth);
	const allowedRoles = to.meta.roles as UserRole[];

	const userStore = useUserStore();
	const user = userStore?.currentUser();
	const userRoles = user?.roles;

	if (requiresAuth && !user) {
		// User is not authenticated.
		next({ name: 'signIn' });
	} else if (requiresAuth && !(allowedRoles && allowedRoles.some(x => userRoles && userRoles.includes(x)))) {
		// Redirect the user to a not authorized page.
		// TODO: Replace with an error page.
		next({ name: 'home' });
	} else {
		// Proceed to the route
		next();
	}
});

export default router;
