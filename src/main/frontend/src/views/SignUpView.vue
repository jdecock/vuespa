<script setup lang="ts">
	import { ref } from 'vue';
	import { useUserStore } from '@/stores/userStore.ts';
	import type { UserInfo } from '@/types/userInfo.ts';

	const name = ref('');
	const email = ref('');
	const password = ref('');

	const userStore = useUserStore();

	function createUser() {
		const user: UserInfo = {
			name: name.value,
			email: email.value,
			plainTextPassword: password.value
		};

		userStore.dispatchSignUp(user).then(x => {
			console.log('createUser', x);
		});
	}
</script>

<template>
	<main>
		<h1>Sign Up</h1>

		<a href="#">Sign up with Google</a>

		<div>or sign in with email</div>

		<form @submit.prevent="createUser">
			<div>
				<label for="inputName">Name</label><br />
				<input type="text" id="inputName" v-model.trim="name" required />
			</div>

			<div>
				<label for="inputEmail">Email</label><br />
				<input type="email" id="inputEmail" v-model.trim="email" required />
			</div>

			<div>
				<label for="inputPassword">Password</label><br />
				<input type="password" id="inputPassword" v-model.trim="password" required />
			</div>

			<div>
				<input type="checkbox" id="checkboxTerms" />
				<label for="checkboxTerms">I agree with the <a href="#">Terms of Service</a>.</label>
			</div>

			<div>
				<button type="submit">Create Account</button>
			</div>
		</form>

		Already have an account? <RouterLink to="/sign-in">Sign In</RouterLink>
	</main>
</template>
