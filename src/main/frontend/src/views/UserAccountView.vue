<script setup lang="ts">
	import { useAlertStore } from '@/stores/alertStore.ts';
	import { useAuthStore } from '@/stores/authStore.ts';
	import type { UserInfo } from '@/types/userInfo.ts';
	import { computed, ref } from 'vue';

	const alertStore = useAlertStore();
	const authStore = useAuthStore();

	const user = ref<UserInfo | null>(null);
	const curPassword = ref('');
	const newPassword = ref('');
	const confirmPassword = ref('');

	const passwordsMatch = computed(() => newPassword.value === confirmPassword.value);

	function saveUser() {
		if (!user.value) return;

		authStore.updateUser(user.value).then(x => {
			if (x.success) {
				alertStore.success(x.message);
			} else {
				alertStore.error(x.message);
			}
		});
	}

	function changePassword() {
		const changePassword = {
			curPassword: curPassword.value,
			newPassword: newPassword.value
		};

		authStore.changePassword(changePassword).then(x => {
			if (x.success) {
				alertStore.success(x.message);
			} else {
				alertStore.error(x.message);
			}
		});
	}

	function signOutEverywhere() {
		// TODO:
	}

	// Force a refresh of the user data
	authStore.refreshUserInfo().then(() => (user.value = authStore.user));
</script>

<template>
	<main>
		<h1>My Account</h1>

		<div v-if="user">
			<form @submit.prevent="saveUser">
				<fieldset>
					<legend>Profile</legend>

					<div>
						<label for="inputName">Name</label><br />
						<input type="text" id="inputName" v-model.trim="user.name" required />
					</div>

					<div>
						<label for="inputEmail">Email</label><br />
						<input type="email" id="inputEmail" v-model.trim="user.email" required />
					</div>

					<div>
						<button type="submit">Save</button>
					</div>
				</fieldset>
			</form>

			<form @submit.prevent="changePassword">
				<fieldset>
					<legend>Change Password</legend>

					<div>
						<label for="inputPassword">Current Password</label><br />
						<input type="password" id="inputPassword" v-model.trim="curPassword" required />
					</div>

					<div>
						<label for="inputPassword">New Password</label><br />
						<input type="password" id="inputPassword" v-model.trim="newPassword" required />
					</div>

					<div>
						<label for="inputPassword">Confirm Password</label><br />
						<input type="password" id="inputPassword" v-model.trim="confirmPassword" required />
					</div>

					<div>
						<button type="submit" :disabled="!passwordsMatch">Save</button>
					</div>
				</fieldset>
			</form>

			<section>
				<h2>Your Devices</h2>

				<p>Your devices linked to this account.</p>

				<ul>
					<li>TODO:</li>
				</ul>

				<button @click="signOutEverywhere">Sign Out From All Devices</button>
			</section>
		</div>
	</main>
</template>
