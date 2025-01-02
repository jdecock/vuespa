<script setup lang="ts">
	import { useAlertStore } from '@/stores/alertStore.ts';
	import { useUserStore } from '@/stores/userStore.ts';
	import type { UserInfo } from '@/types/userInfo.ts';
	import { ref } from 'vue';

	const alertStore = useAlertStore();
	const userStore = useUserStore();

	const searchTerm = ref('');
	const searchResults = ref<UserInfo[] | null>(null);

	function search() {
		userStore.search(searchTerm.value).then(x => {
			if (x.success) {
				searchResults.value = x.payload ?? [];
			} else {
				alertStore.error(x.message);
			}
		});
	}
</script>

<template>
	<main>
		<h1>Search Users</h1>

		<form @submit.prevent="search">
			<div>
				<label for="inputSearch">Search for User</label><br />
				<input type="text" id="inputSearch" v-model.trim="searchTerm" required />
			</div>

			<div>
				<button type="submit">Search</button>
			</div>
		</form>

		<section v-if="searchResults">
			<h2>Search Results</h2>

			<ul v-if="searchResults.length">
				<li v-for="user in searchResults" :key="user.id">
					<a :href="`/admin/users/${user.id}`">name: {{ user.name }}</a
					><br />
					email: {{ user.email }}<br />
					roles: {{ user.roles?.map(x => x.toString()).join(', ') }}<br />
					disabled: {{ user.disabled }}<br />
					disabledNote: {{ user.disabledNote }}<br />
					creationDate: {{ user.creationDate }}<br />
					lastModified: {{ user.lastModifiedDate }}
				</li>
			</ul>

			<div v-else>No results found</div>
		</section>
	</main>
</template>
