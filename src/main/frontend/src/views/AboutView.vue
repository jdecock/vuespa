<script setup lang="ts">
	import { ref, onMounted } from 'vue';
	import { getApiTestMessage } from '@/services/api';
	import { useCounterStore } from '@/stores/counter';

	const message = ref('Loading...');
	const store = useCounterStore();

	async function fetchTestMessage() {
		getApiTestMessage().then(response => {
			message.value = response.data.message;
		});
	}

	onMounted(async () => {
		await fetchTestMessage();
	});
</script>

<template>
	<div class="about">
		<h1>{{ message }}</h1>
		<button type="button" @click="store.increment">Clicked: {{ store.count }}</button>
	</div>
</template>

<style>
	@media (min-width: 1024px) {
		.about {
			min-height: 100vh;
			display: flex;
			align-items: center;
		}
	}
</style>
