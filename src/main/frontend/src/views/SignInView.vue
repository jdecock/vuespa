<script setup lang="ts">
	import router from '@/router';
	import { useAlertStore } from '@/stores/alertStore.ts';
	import { useAuthStore } from '@/stores/authStore.ts';
	import type { AuthRequest } from '@/types/authRequest.ts';
	import { RouterLink } from 'vue-router';
	import { ref } from 'vue';

	const alertStore = useAlertStore();
	const authStore = useAuthStore();

	const email = ref('');
	const password = ref('');
	const rememberMe = ref(false);

	function login() {
		const authRequest: AuthRequest = {
			email: email.value,
			password: password.value,
			persistLogin: rememberMe.value
		};

		authStore.login(authRequest).then(x => {
			if (x.success) {
				const params = new URLSearchParams(document.location.search);
				let returnUrl = params.get('returnUrl');
				returnUrl = returnUrl ? decodeURIComponent(returnUrl) : '/';
				router.push(returnUrl);
			} else {
				alertStore.error(x.message);
			}
		});
	}
</script>

<template>
	<main>
		<h1>Sign In</h1>

		<!-- TODO: Google sign in -->
		<a href="#">Sign in with Google</a>

		<div>or sign in with email</div>

		<form @submit.prevent="login">
			<div>
				<label for="inputEmail">Email</label><br />
				<input type="email" id="inputEmail" v-model.trim="email" required />
			</div>

			<div>
				<label for="inputPassword">Password</label><br />
				<input type="password" id="inputPassword" v-model.trim="password" required />
			</div>

			<div>
				<input type="checkbox" id="checkboxRememberMe" v-model="rememberMe" />
				<label for="checkboxRememberMe">Remember me</label>
			</div>

			<div>
				<RouterLink to="/forgot-password">Forgot Password</RouterLink>
			</div>

			<div>
				<button type="submit">Sign In</button>
			</div>
		</form>

		Don't have an account? <RouterLink to="/sign-up">Sign Up</RouterLink>
	</main>
</template>
