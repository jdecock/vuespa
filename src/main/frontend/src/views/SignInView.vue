<script setup lang="ts">
	import { ref } from 'vue';
	import axios from "axios";

	const email = ref('');
	const password = ref('');
	const rememberMe = ref(false);

	function login() {
		axios.post('/auth/generate-token', {
			email: email.value,
			password: password.value
		})
		.then(result => {
			console.log(result.data.success);
			console.log(result.data.message);
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
