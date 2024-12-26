<script setup lang="ts">
	import { ref } from 'vue';
	import { useUserStore } from '@/stores/userStore.ts';
	import type { AuthRequest } from '@/types/authRequest.ts';

	const email = ref('');
	const password = ref('');
	const rememberMe = ref(false);

	const userStore = useUserStore();

	function login() {
		const authentication: AuthRequest = {
			email: email.value,
			password: password.value
		};

		userStore.dispatchLogin(authentication).then(x => {
			console.log(x);
			userStore.dispatchUserInfo().then(y => console.log(y));
		});
	}
</script>

<template>
	<main>
		<h1>Sign In</h1>

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
