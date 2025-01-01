import { useAlertStore } from '@/stores/alertStore.ts';
import { useAuthStore } from '@/stores/authStore.ts';
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
		{
			path: '/sign-out',
			name: 'signOut',
			component: {
				beforeRouteEnter(to, from, next) {
					const authStore = useAuthStore();
					authStore.logout().then(() => {
						next({ name: 'signIn' });
					});
				}
			}
		},
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
		},
		{
			path: '/admin/users/:id',
			name: 'userEdit',
			component: () => import('../views/admin/UserEditView.vue'),
			meta: { requiresAuth: true, roles: [UserRole.Admin] }
		},
		{
			path: '/denied',
			name: 'insufficientRole',
			component: () => import('../views/errors/InsufficientRoleView.vue')
		}
	]
});

router.beforeEach((to, from, next) => {
	const requiresAuth = to.matched.some(x => x.meta.requiresAuth);
	const allowedRoles = to.meta.roles as UserRole[];

	const authStore = useAuthStore();
	const alertStore = useAlertStore();
	alertStore.clear();

	if (requiresAuth && !authStore.isAuthenticated) {
		// User is not authenticated.
		next({ name: 'signIn', query: { returnUrl: encodeURIComponent(to.fullPath) } });
	} else if (requiresAuth && !authStore.userHasAnyRole(allowedRoles)) {
		// Redirect the user to a not authorized page.
		next({ name: 'insufficientRole' });
	} else {
		// Proceed to the route
		next();
	}
});

export default router;
