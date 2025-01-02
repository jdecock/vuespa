<script setup lang="ts">
	import { useAlertStore } from '@/stores/alertStore.ts';
	import { useUserStore } from '@/stores/userStore.ts';
	import type { UserInfo } from '@/types/userInfo.ts';
	import { UserRole } from '@/types/userRole.ts';
	import { ref } from 'vue';
	import { useRoute } from 'vue-router';

	type UserRoleOption = {
		label: string;
		role: UserRole;
		selected: boolean;
	};

	const alertStore = useAlertStore();
	const userStore = useUserStore();

	const userId = Number(useRoute().params.id);
	const user = ref<UserInfo | null>(null);
	const userRoles = ref<UserRoleOption[]>([]);

	function loadUser() {
		userStore.getUser(userId).then(x => {
			if (x.success) {
				user.value = x.payload ?? null;

				for (const role in UserRole) {
					userRoles.value.push({
						label: role,
						role: UserRole[role as keyof typeof UserRole],
						selected: !!(user.value?.roles?.includes(UserRole[role as keyof typeof UserRole]))
					});
				}
				console.log(JSON.stringify(userRoles.value));
			} else {
				alertStore.error(x.message);
			}
		});
	}

	function saveUser() {
		if (user.value) {
			user.value.roles = userRoles.value.filter(x => x.selected).map(x => x.role);

			userStore.saveUser(user.value).then(x => {
				if (x.success) {
					alertStore.success(x.message);
				} else {
					alertStore.error(x.message);
				}
			});
		}
	}

	loadUser();
</script>

<template>
	<main>
		<h1>Edit User</h1>

		<form v-if="user" @submit.prevent="saveUser">
			<div>
				<label for="inputName">Name</label><br />
				<input type="text" id="inputName" v-model.trim="user.name" required />
			</div>

			<div>
				<label for="inputEmail">Email</label><br />
				<input type="email" id="inputEmail" v-model.trim="user.email" required />
			</div>

			<div v-for="(role, idx) in userRoles" :key="role.role">
				<input type="checkbox" :id="`checkboxRole${idx}`" v-model="role.selected" />
				<label :for="`checkboxRole${idx}`">{{ role.label }}</label>
			</div>

			<div>
				<input type="checkbox" id="checkboxDisableUser" v-model="user.disabled" />
				<label for="checkboxDisableUser">Disable this account</label>
			</div>

			<div v-if="user.disabled">
				<label for="textareaDisabledNote">Reason</label><br />
				<textarea id="textareaDisabledNote" required v-model.trim="user.disabledNote"></textarea>
			</div>

			<div>
				<button type="submit">Save</button>
			</div>
		</form>
	</main>
</template>
