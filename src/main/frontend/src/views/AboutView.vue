<script setup lang="ts">
	import { ref } from 'vue';
	import { useCounterStore } from '@/stores/counter';

	const message = ref('Loading...');
	const store = useCounterStore();
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
